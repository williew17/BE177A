var request = require('request');

const textID = "0ED7B052-FDB2-4EDF-9B4B-E732F69DDF7A";
const textToken = "3171FF33-83C5-4221-9BB0-051DC747AEB9";
const totalToken = textID + ":" + textToken;
const startURL = "https://www.assessmentcenter.net/ac_api/2014-01/"
//                                           api name^      ^api version
module.exports = {
    getFormID: function () {
        var options = {
          url: startURL + 'Forms/.json',
          headers: {
            'Authorization': 'Basic ' + Buffer.from(totalToken).toString('base64')
          }
        };
         
        function callback(error, response, body) {
          if (!error && response.statusCode == 200) {
            var info = JSON.parse(body);
          console.log(info); //JSON format where the {OID:_____, Name: ______}
          }
        }
         
        request(options, callback);
    },
    
    getForm: function (formID) {
      var options = {
          url: startURL + 'Forms/' + formID + '.json',
          headers: {
            'Authorization': 'Basic ' + Buffer.from(totalToken).toString('base64')
          }
        };
         
        function callback(error, response, body) {
          if (!error && response.statusCode == 200) {
            var info = JSON.parse(body);
          console.log(info); //JSON format where the {OID:_____, Name: ______}
          }
          else {
              console.log('failure')
          }
          
        }
         
        request(options, callback);
    },
    
    registerTest: function () {
      return "register";
    },
    
    administerTest: function () {
      return "administer" ;
    },
    
    testResults: function () {
      return "testResults";
    }
};