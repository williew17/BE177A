'use strict';

const {dialogflow,Permission,Suggestions} = require('actions-on-google');
const functions = require('firebase-functions');

const df = dialogflow({debug: true});

var Questions = [
"In general, would you say your health is:",
"In general, would you say your quality of life is:",
"In general, how would you rate your physical health?",
"In general, how would you rate you mental health, including your mood and your ability to think?",
"In general, how would you rate your satisfaction with your social activities and relationships?",
"In general, please rate how well you carry out your usual social activities and roles. (This includes activities at home, at work, and in your community, and responsibilities as a parent, child, spouse, employee, friend, etc.)",
"To what extent are you able to carry out your everyday physical activities such as walking, climbing stairs, carrying groceries, or moving a chair?",
"In the past seven days, how often have you been bothered by emotional problems such as feeling anxious, depressed or irritable?",
"In the past seven days, how would you rate your fatigue on average?",
"In the past seven days, how would you rate your pain on average?"
];

var Choices = [
"5, excellent; 4, very good; 3, good; 2, fair; or 1, poor",
"5, completely; 4, mostly; 3, moderately; 2, a little; or 1, not at all",
"5, never; 4, rarely; 3, sometimes; 2, often; or 1, always",
"5, none; 4, mild; 3, moderate; 2, severe; or 1, very severe",
"0, no pain, up to 10, worst imaginable pain"
];

df.intent('Patient Survey', (conv) => {
  conv.ask(Questions[0] + Choices[0]);
});

df.intent('Answer', (conv) => {
    //store data
    conv.ask("need to get question number");
    //conv.ask(Questions[#questionNumber.number]);
});

df.intent('Repeat', (conv) => {
   conv.ask('previous question'); 
});

exports.dialogflowFirebaseFulfillment = functions.https.onRequest(df);