'use strict';

const {dialogflow,Permission,Suggestions} = require('actions-on-google');
const functions = require('firebase-functions');

const df = dialogflow({debug: true});

//importing file api.js as api
//use functions in api.js by doing api.<func1>()
var api = require('./api');

/*var Questions = [
'<speak>In general, would you say your health is: <break time="400ms"/>',
"In general, would you say your quality of life is: ",
"In general, how would you rate your physical health? ",
"In general, how would you rate you mental health, including your mood and your ability to think? ",
"In general, how would you rate your satisfaction with your social activities and relationships? ",
"In general, please rate how well you carry out your usual social activities and roles. (This includes activities at home, at work, and in your community, and responsibilities as a parent, child, spouse, employee, friend, etc.) ",
"To what extent are you able to carry out your everyday physical activities such as walking, climbing stairs, carrying groceries, or moving a chair? ",
"In the past seven days, how often have you been bothered by emotional problems such as feeling anxious, depressed or irritable? ",
"In the past seven days, how would you rate your fatigue on average? ",
"In the past seven days, how would you rate your pain on average? "
];


var Choices = [
'5,  <break time="200ms"/> excellent; <break time="200ms"/> 4, very good; <break time="200ms"/> 3, good; <break time="200ms"/> 2, fair; or 1, <break time="200ms"/> poor</speak>',
"5, completely; 4, mostly; 3, moderately; 2, a little; or 1, not at all",
"5, never; 4, rarely; 3, sometimes; 2, often; or 1, always",
"5, none; 4, mild; 3, moderate; 2, severe; or 1, very severe",
"0, no pain, up to 10, worst imaginable pain"
];

var QC_Pairs = [
0, 0, 0, 0, 0, 0, 1, 2, 3, 4
]*/

var Questions = api.getForm("037D7B69-FCB2-482E-A1CE-9A4D017D24AD")



df.intent('Patient Survey', (conv) => {
  conv.ask(Questions[0]);
  conv.contexts.set('survey', 3, {index: 0});
  conv.contexts.set('Answer0', 6);
});

df.intent('Answer0', (conv, {PROMs5}) => {
  //store data
  const s = conv.contexts.get('survey');
    if (s){
      const questionNum = s.parameters.index;
      conv.ask(Questions[questionNum + 1] + Choices[QC_Pairs[questionNum + 1]]);
      conv.contexts.set('survey', 3, {index: questionNum+1});
	  if (QC_Pairs[questionNum + 1] == 0) {
			conv.contexts.set('Answer0', 3);
	  } else if (QC_Pairs[questionNum + 1] == 1){
  			conv.contexts.set('Answer1', 3);	
	  } else if (QC_Pairs[questionNum + 1] == 2){
  			conv.contexts.set('Answer2', 3);	
  	  } else if (QC_Pairs[questionNum + 1] == 3){
			conv.contexts.set('Answer3', 3);	
  	  } else if (QC_Pairs[questionNum + 1] == 4){
			conv.contexts.set('Answer4', 3);	
  	  } 
    }
    else {
      conv.ask("error with context");
    }
});


df.intent('Answer1', (conv, {PROMsAMT}) => {
	const s = conv.contexts.get('survey');
    if (s) {
	  const questionNum = s.parameters.index;
      conv.ask(Questions[questionNum + 1] + Choices[QC_Pairs[questionNum + 1]]);
      conv.contexts.set('survey', 3, {index: questionNum+1});
	  if (QC_Pairs[questionNum + 1] == 0) {
			conv.contexts.set('Answer0', 3);
	  } else if (QC_Pairs[questionNum + 1] == 1){
  			conv.contexts.set('Answer1', 3);	
	  } else if (QC_Pairs[questionNum + 1] == 2){
  			conv.contexts.set('Answer2', 3);	
  	  } else if (QC_Pairs[questionNum + 1] == 3){
			conv.contexts.set('Answer3', 3);	
  	  } else if (QC_Pairs[questionNum + 1] == 4){
			conv.contexts.set('Answer4', 3);	
  	  } 
    }
    else {
      conv.ask("error with context");
    }
});

df.intent('Answer2', (conv, {PROMsTIME}) => {
	const s = conv.contexts.get('survey');
    if (s){
	  const questionNum = s.parameters.index;
      conv.ask(Questions[questionNum + 1] + Choices[QC_Pairs[questionNum + 1]]);
      conv.contexts.set('survey', 3, {index: questionNum+1});
	  if (QC_Pairs[questionNum + 1] == 0) {
			conv.contexts.set('Answer0', 3);
	  } else if (QC_Pairs[questionNum + 1] == 1){
  			conv.contexts.set('Answer1', 3);	
	  } else if (QC_Pairs[questionNum + 1] == 2){
  			conv.contexts.set('Answer2', 3);	
  	  } else if (QC_Pairs[questionNum + 1] == 3){
			conv.contexts.set('Answer3', 3);	
  	  } else if (QC_Pairs[questionNum + 1] == 4){
			conv.contexts.set('Answer4', 3);	
  	  } 
    }
    else {
      conv.ask("error with context");
    }
});

df.intent('Answer3', (conv, {PROMsSEV}) => {
	const s = conv.contexts.get('survey');
    if (s){
	  const questionNum = s.parameters.index;
      conv.ask(Questions[questionNum + 1] + Choices[QC_Pairs[questionNum + 1]]);
      conv.contexts.set('survey', 3, {index: questionNum+1});
	  if (QC_Pairs[questionNum + 1] == 0) {
			conv.contexts.set('Answer0', 3);
	  } else if (QC_Pairs[questionNum + 1] == 1){
  			conv.contexts.set('Answer1', 3);	
	  } else if (QC_Pairs[questionNum + 1] == 2){
  			conv.contexts.set('Answer2', 3);	
  	  } else if (QC_Pairs[questionNum + 1] == 3){
			conv.contexts.set('Answer3', 3);	
  	  } else if (QC_Pairs[questionNum + 1] == 4){
			conv.contexts.set('Answer4', 3);	
  	  } 
    }
    else {
      conv.ask("error with context");
    }
});

df.intent('Answer4', (conv, {PROMs10}) => {
	const s = conv.contexts.get('survey');
    if (s){
	  const questionNum = s.parameters.index;
      conv.ask(Questions[questionNum + 1] + Choices[QC_Pairs[questionNum + 1]]);
      conv.contexts.set('survey', 3, {index: questionNum+1});
	  if (QC_Pairs[questionNum + 1] == 0) {
			conv.contexts.set('Answer0', 3);
	  } else if (QC_Pairs[questionNum + 1] == 1){
  			conv.contexts.set('Answer1', 3);	
	  } else if (QC_Pairs[questionNum + 1] == 2){
  			conv.contexts.set('Answer2', 3);	
  	  } else if (QC_Pairs[questionNum + 1] == 3){
			conv.contexts.set('Answer3', 3);	
  	  } else if (QC_Pairs[questionNum + 1] == 4){
			conv.contexts.set('Answer4', 3);	
  	  } 
    }
    else {
      conv.ask("error with context");
    }
});


//not done at all
df.intent('Repeat', (conv) => {
   conv.ask(Questions[questionNum] + Choices[QC_Pairs[questionNum]]); 
   conv.contexts.set('survey', 3, {index: questionNum});
});

/*
df.intent('Response', (conv, {num, phrase}) => {
    const s = conv.contexts.get('survey');
    if(num in numbers){
    
    }
    else if(phrase in answers){
        
    }
    else{
        conv.ask("Sorry, we were unable to match your answer to the choices provided. Could you repeat that?");
    }
})
*/
exports.fulfillment = functions.https.onRequest(df);