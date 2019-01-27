var request = require('sync-request');
const textID = "0ED7B052-FDB2-4EDF-9B4B-E732F69DDF7A";
const textToken = "3171FF33-83C5-4221-9BB0-051DC747AEB9";
const totalToken = textID + ":" + textToken;
const startURL = "https://www.assessmentcenter.net/ac_api/2014-01/"
//                                           api name^      ^api version
module.exports = {
    getFormID: function () {
		
		var res = request('GET', startURL + 'Forms/.json', {
			  headers: {
				'Authorization': 'Basic ' + Buffer.from(totalToken).toString('base64'),
			  },
			});
		return JSON.parse(res.getBody('utf8'));
		},
    
    getForm: function (formID) {
      var res = request('GET', startURL + 'Forms/' + formID + '.json', {
        headers: {
          'Authorization': 'Basic ' + Buffer.from(totalToken).toString('base64')
        },
      });

          //if (!error && response.statusCode == 200) {
            try {
              var info = JSON.parse(res.getBody('utf8'))
            }
            catch(err) {
              console.log("Error: Cannot retrieve Form Info.")
            }
            var items = info.Items
            // console.log(info); //JSON format
            var all_questions = []
            for (var i = 0; i < items.length; i++){
              var _div = ""
              //_div.push(items[i].ID + ":")
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
          //}
          /*else {
              console.log("Error: Cannot retrieve Form Info.")
          }*/
    },
    
    registerTest: function (formID) {
      var res = request('GET', startURL + 'Assessments/' + formID +'.json', {
        headers: {
          'Authorization': 'Basic ' + Buffer.from(totalToken).toString('base64')
        },
      });

          try {
            var info = JSON.parse(res.getBody('utf8'));
          }
          catch(err) {
            console.log("Error: Cannot get formID's")
          }
          return info;
    },
    
    /*mapHelper: function (element) {
      var maplist = ''
      var map = element[2].Map
      for (var n = 0; n < map.length; n++) {
        maplist += "(" + map[n].Value + "):"+  map[n].Description + "    " ;
      }
	  return maplist;
    },

    mapDict: function (element) {
      var mapdict = []
      var map = element[2].Map
      for (var n = 0; n < map.length; n++) {
        mapdict.push({'description':map[n].Description, 'number':map[n].Value, 'OID':map[n].ItemResponseOID})
      }
	  return mapdict;
    },*/
    
    administerTest: function (first, AssessmentToken, response) {
      
	  if (first) //if it is the first question
	  {
		 try {
		var res = request('GET', startURL + "Participants/" + AssessmentToken + ".json", {
        headers: {
          'Authorization': 'Basic ' + Buffer.from(totalToken).toString('base64')
		  },
		});

		var info = JSON.parse(res.getBody('utf8'))
    var question = info.Items[0].Elements[0].Description + ' ' + info.Items[0].Elements[1].Description
    
    var choices = ""
    var map = info.Items[0].Elements[2].Map
    for (var n = 0; n < map.length; n++) {
      choices += "(" + map[n].Value + "):"+  map[n].Description + " " ;
    }

    var mapdict = []
    for (var n = 0; n < map.length; n++) {
      mapdict.push({'description':map[n].Description, 'number':map[n].Value, 'OID':map[n].ItemResponseOID})
    }
    return [String(question + ' ' + choices), mapdict];
		 }
		 catch(err) {
            console.log("Error retrieving next question")
		 }
	  }
	  else
	  {
		try {
		var res = request('GET', startURL + "Participants/" + AssessmentToken + ".json" + '?ItemResponseOID=' + response.id + '&Response=' + response.value, {
        headers: {
          'Authorization': 'Basic ' + Buffer.from(totalToken).toString('base64')
		  },
		});
		var info = JSON.parse(res.getBody('utf8'))
		if (info.DateFinshed != '')//this means we are at the end
		{
			return "reached end"
		}
		else {
      var question = info.Items[0].Elements[0].Description + ' ' + info.Items[0].Elements[1].Description
    
    var choices = ''
    var map = info.Items[0].Elements[2].Map
    for (var n = 0; n < map.length; n++) {
      choices += "(" + map[n].Value + "):"+  map[n].Description + " " ;
    }

    var mapdict = []
    for (var n = 0; n < map.length; n++) {
      mapdict.push({'description':map[n].Description, 'number':map[n].Value, 'OID':map[n].ItemResponseOID})
    }
    return [String(question + ' ' + choices), mapdict];
		}
		}
		catch (err) {
            console.log("Error getting next question.")
		}
		}
	},
    
    testResults: function (AssessmentToken) {
      var res = request('GET', startURL + 'Results/' + AssessmentToken +'.json', {
        headers: {
          'Authorization': 'Basic ' + Buffer.from(totalToken).toString('base64')
        },
      });
         
          try {
            var info = JSON.parse(res.getBody('utf8'));
          }
          catch(err) {
              console.log("Error: Cannot get formID's")
          }
          return info;
    },
    };