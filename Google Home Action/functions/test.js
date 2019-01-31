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
  
  return api.getFormID().then((forms) => {
    console.log(forms)
  })

  var formID = '80C5D4A3-FC1F-4C1B-B07E-10B796CF8105';
  
  return api.getForm(formID).then((form) => {
    console.log(form)
  })
  
  return api.registerTest(formID).then((token) => {
    return api.administerTest(token, true, []).then((firstQuestion) => {
      console.log(firstQuestion)
    })
  })

  });

  
