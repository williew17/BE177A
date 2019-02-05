'use strict';

const {dialogflow,Permission,Suggestions} = require('actions-on-google');
const functions = require('firebase-functions');
var AWS = require('aws-sdk');
AWS.config.loadFromPath('./config.json');
const s3 = new AWS.S3();
const df = dialogflow({debug: true});

//importing file api.js as api
//use functions in api.js by doing api.<func1>()
var api = require('./api');
var formID = '80C5D4A3-FC1F-4C1B-B07E-10B796CF8105'; // PROMIS Bank v2.0 - Physical Function

df.intent('Patient Survey', (conv) => {
    return api.registerTest(formID).then((token) => {
        return api.administerTest(token, {}).then((firstQuestion) => {
            conv.ask(firstQuestion[0]);
            conv.contexts.set('assessmenttoken', 3, {"token": token});
            conv.contexts.set('question', 3, {"question": firstQuestion[0]});
            conv.contexts.set('choices', 3, {"choices": firstQuestion[1]});
        })
    })
})

df.intent('Response', (conv, {num, phrase}) => {
    const token = conv.contexts.get('assessmenttoken').parameters.token;
    const choices = conv.contexts.get('choices').parameters.choices;
    
    var lowercasePhrase = '';
    var OID = '';
    var value = 0;
    
    if(phrase != ''){ //convert to number
    lowercasePhrase = phrase.toLowerCase();
        for(let c of choices) {
            if (c.description == lowercasePhrase){
                value = c.value;
                OID = c.OID;
            }
        }
    }
    else if(num != ''){ //getItemResponseOID
        for(let c of choices) {
            if (c.value == num){
                value = c.value;
                OID = c.OID;
            }
        }
    }
    else{
        conv.ask("Sorry, we were unable to match your answer to the choices provided. Could you repeat that?");
        return;
    }
    
    return api.administerTest(token, {"id": OID, "value": value}).then((output) => {
        if(output.length == 1) {
            conv.ask("You have finished the assessment.");
            return api.testResults(token).then((results) => {
                var file = new Buffer("test_name" + JSON.stringify(results), 'binary');
                var keystring = token + ":" + output[0];
                var opts = {Body: file, Bucket: "swellhomebucket", Key: keystring};
                var complete = new Promise( function(resolve, reject) {
                    s3.putObject( opts, function(){});
                    resolve();
                });
                complete.then(function () {conv.ask("Finished Upload.")});
                return complete; //call takes too long so we assume it works when we return a response
            });
            
        }
        conv.ask(output[0]);
        conv.contexts.set('assessmenttoken', 3, {"token": token});
        conv.contexts.set('question', 3, {"question": output[0]});
        conv.contexts.set('choices', 3, {"choices": output[1]});
    });
})

df.intent('Repeat', (conv) => {
    conv.ask(conv.contexts.get('question').parameters.question);
})

exports.fulfillment = functions.https.onRequest(df);