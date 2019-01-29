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
var formID = '037D7B69-FCB2-482E-A1CE-9A4D017D24AD';
//var Questions = api.getForm("037D7B69-FCB2-482E-A1CE-9A4D017D24AD")
df.intent('Patient Survey', (conv) => {
    var assessmentToken = api.registerTest(formID).OID;
    var firstQuestion = api.administerTest(true, assessmentToken, []);
    conv.ask(firstQuestion[0]);
	conv.contexts.set('assessmenttoken', 3, {"token": assessmentToken});
    conv.contexts.set('question', 3, {"question": firstQuestion[0]});
	conv.contexts.set('choices', 3, {"choices": firstQuestion[1]});
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
    var output = api.administerTest(false, token, {"id": OID, "value": value});
    if(output.length == 1){
        conv.ask("You have finished the assessment.");
    }
    conv.ask(output[0]);
    conv.contexts.set('assessmenttoken', 3, {"token": token});
    conv.contexts.set('question', 3, {"question": output[0]});
    conv.contexts.set('choices', 3, {"choices": output[1]});
})

df.intent('Repeat', (conv) => {
    conv.ask(conv.contexts.get('question').parameters.question);
})

exports.fulfillment = functions.https.onRequest(df);