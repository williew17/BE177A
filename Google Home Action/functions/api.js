var request = require('request-promise');
const textID = "0ED7B052-FDB2-4EDF-9B4B-E732F69DDF7A";
const textToken = "3171FF33-83C5-4221-9BB0-051DC747AEB9";
const totalToken = textID + ":" + textToken;
const startURL = "https://www.assessmentcenter.net/ac_api/2014-01/"
//                                           api name^      ^api version
module.exports = {
    getFormID: function () {
  		return request({
        'method': 'GET',
        'uri': startURL + 'Forms/.json',
        'json': true,
        'headers': {'Authorization': 'Basic ' + Buffer.from(totalToken).toString('base64'),}
      }).then(function(info){
        return info;
      });
  },
    
    getForm: function (formID) {
      return request({
        'method': 'GET',
        'uri': startURL + 'Forms/' + formID + '.json',
        'json': true,
        'headers': {'Authorization': 'Basic ' + Buffer.from(totalToken).toString('base64'),}
      }).then(function(info){
        var items = info.Items;
        var all_questions = [];
        for (var i = 0; i < items.length; i++) {
          var _div = ''
          for (var j = 0; j < items[i].Elements.length; j++) {
            if (typeof(items[i].Elements[j].Map) == "undefined") {
              _div += (items[i].Elements[j].Description) + " ";
            }
            else {
              var map = ""
              for (var n = 0; n < items[i].Elements[j].Map.length; n++) {
                map += "(" + items[i].Elements[j].Map[n].Value + "):"+  items[i].Elements[j].Map[n].Description + "    " ;
              }
              _div += "   " + map;
            }
          }
          all_questions.push(_div)
        }
        return all_questions
      });
    },
    
    registerTest: function (formID) {
      return request({
        'method': 'GET',
        'uri': startURL + 'Assessments/' + formID +'.json',
        'json': true,
        'headers': {'Authorization': 'Basic ' + Buffer.from(totalToken).toString('base64'),}
      }).then(function(info){
        return info.OID;
      });
    },
    
    administerTest: function (AssessmentToken, response) {
        var string = "";
        if(Object.keys(response).length != 0) //response is empty
            string = '?ItemResponseOID=' + response.id + '&Response=' + response.value;
        return request({
          'method': 'GET',
          'uri': startURL + "Participants/" + AssessmentToken + ".json" + string,
          'json': true,
          'headers': {'Authorization': 'Basic ' + Buffer.from(totalToken).toString('base64'),}
        }).then(function (info, response) {
          if (info.DateFinished != '') {
            return [info.DateFinished];
          }

          else {
            var elems = info.Items[0].Elements;

          var question = '<speak><prosody rate="90%"> ';
          for (var n = 0; n < (elems.length - 1); n++) {
            question += (elems[n].Description + '  ');
          }

          var choices = '';
          var map = info.Items[0].Elements[elems.length - 1].Map
          for (var n = 0; n < map.length; n++) {
            choices += '(' + map[n].Value + '): '+  map[n].Description + '<break time=".5s"/> ' ;
          }

          var choiceArray = [];
          for (var n = 0; n < map.length; n++) {
            choiceArray.push({'value': map[n].Value, 'description': map[n].Description.toLowerCase(), 'OID': map[n].ItemResponseOID});
          }
          return [question + " " + choices + '</prosody></speak>', choiceArray];
          }

        });
    },

    testResults: function (AssessmentToken) {
      return request({
        'method': 'POST',
        'uri': startURL + 'Results/' + AssessmentToken + '.json',
        'json': true,
        'headers': {'Authorization': 'Basic ' + Buffer.from(totalToken).toString('base64'),}
      }).then(function(info){
        return info;
      });
    },
};