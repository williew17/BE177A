'use strict';

const {dialogflow,Permission,Suggestions} = require('actions-on-google');
const functions = require('firebase-functions');

const df = dialogflow({debug: true});

df.intent('Patient Survey', (conv) => {
  conv.ask('question');
});

df.intent('Answer', (conv) => {
    //store data
    conv.ask('question2');
});

df.intent('Repeat', (conv) => {
   conv.ask('previous question'); 
});

exports.dialogflowFirebaseFulfillment = functions.https.onRequest(df);
