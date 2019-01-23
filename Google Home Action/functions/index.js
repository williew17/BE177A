'use strict';

const {dialogflow,Permission,Suggestions} = require('actions-on-google');
const functions = require('firebase-functions');

const df = dialogflow({debug: true});

//importing file api.js as api
//use functions in api.js by doing api.<func1>()
var api = require('./api');
var formID = "037D7B69-FCB2-482E-A1CE-9A4D017D24AD";

//var Questions = api.getForm("037D7B69-FCB2-482E-A1CE-9A4D017D24AD")

df.intent('Patient Survey', (conv) => {
    assessmentToken = api.registerTest(formID).OID;
	var firstQuestion = api.administerTest(assessmentToken);
	conv.ask( firstquestion[0].Description + firsquestion[1].Description + )

})

df.intent('Response', (conv, {num, phrase}) => {
    const at = conv.contexts.get('assessmentToken');
    const phrases = conv.contexts.get('phrases');
    const numbers = conv.contexts.get('numbers');
    
    if(phrase != undefined){ //convert to number
        for(p in phrases) {
            if (p.description == phrase){
                num = p.number;
            }
        }
    }
    if(num != undefined){
        response = ; // get ItemOID
        api.administerTest(at, response, num)
        .then(function (data) { 
            data.Items.Elements[2].Map; //put this into numbers and phrases
        })
    }
    else{
        conv.ask("Sorry, we were unable to match your answer to the choices provided. Could you repeat that?");
    }
})

df.intent('Repeat', (conv) => {
    
})

exports.fulfillment = functions.https.onRequest(df);