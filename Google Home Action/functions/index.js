'use strict';

const {dialogflow,Permission,Suggestions} = require('actions-on-google');
const functions = require('firebase-functions');

const df = dialogflow({debug: true});

//importing file api.js as api
//use functions in api.js by doing api.<func1>()
var api = require('./api');
var formID = '037D7B69-FCB2-482E-A1CE-9A4D017D24AD';
//var Questions = api.getForm("037D7B69-FCB2-482E-A1CE-9A4D017D24AD")
df.intent('Patient Survey', (conv) => {
    var assessmentToken = api.registerTest(formID).OID;
    var firstQuestion = api.administerTest(true, assessmentToken, []);
    conv.ask(firstQuestion[0])
	conv.contexts.set('assessmentToken', 3, {token: assessmentToken}); 
	conv.contexts.set('choices', 3, JSON.stringify(firstQuestion[1]));
})

df.intent('Response', (conv, {num, phrase}) => {
    const at = conv.contexts.get('assessmentToken');
    const token = at.parameters.token;
    const choices = JSON.parse(conv.contexts.get('choices').parameters.choices);
    
    var OID = '';
    
    if(phrase != undefined){ //convert to number
        for(c in choices) {
            if (c.description == phrase){
                num = c.number;
                OID = c.OID;
            }
        }
    }
    else if(num != undefined){ //getItemResponseOID
        for(c in choices) {
            if (c.number == num){
                OID = c.OID;
            }
        }
        api.administerTest(at, OID, num)
        .then(function (data) { 
            data.Items.Elements[2].Map; //put this into the choices
        })
    }
    else{
        conv.ask("Sorry, we were unable to match your answer to the choices provided. Could you repeat that?");
    }
})

df.intent('Repeat', (conv) => {
    
})

exports.fulfillment = functions.https.onRequest(df);