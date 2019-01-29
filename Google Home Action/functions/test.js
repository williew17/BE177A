const http = require('http');
var request = require('request');

var api = require('./api');
const hostname = '127.0.0.1';
const port = 3000;

const server = http.createServer((req, res) => {
  res.statusCode = 200;
  res.setHeader('Content-Type', 'text/plain');
  res.end('Hello World\n');
});

server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
  var api = require('./api');
  var formID = '80C5D4A3-FC1F-4C1B-B07E-10B796CF8105';
  var form = api.getForm(formID)
  console.log(form.length)
  //var assessmentToken = api.registerTest(formID).OID;
  //var firstQuestion = api.administerTest(true, assessmentToken, []);
  //conv.ask(firstQuestion[0])
  //console.log(assessmentToken);
  //console.log(firstQuestion[1]);
  });

  
