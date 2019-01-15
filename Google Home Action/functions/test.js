const http = require('http');
const api = require('./api')
const hostname = '127.0.0.1';
const port = 3000;

const server = http.createServer((req, res) => {
  res.statusCode = 200;
  res.setHeader('Content-Type', 'text/plain');
  res.end('Hello World\n');
});

server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
  //api.getFormID();
  console.log(api.registerTest('037D7B69-FCB2-482E-A1CE-9A4D017D24AD'));
  });
  
