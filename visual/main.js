/* global __dirname */
var express = require('express');
var bodyParser = require('body-parser');
var app = express();
var Twig = require("twig");
var url = require('url');
var nQuery = require('nodeQuery');
nQuery.use(app);

var config = require('./controllers/config');

// parse json/applciation
//app.use(express.bodyParser());
app.use(bodyParser.json());

// Handle Static content
app.use('/', express.static(__dirname + '/web'));
// load controllers. NOTE : Index.js is required to set routes.
app.use(require('./controllers'));

// Handle 404
app.use(function (req, res) {
  res.status(404);
  var pathname = url.parse(req.url).pathname;
  console.log('Error: ' + pathname + ' is not a vaild route!');
  if (req.accepts('html')) {
    res.render('error.html.twig', {"message": 'Hu-uh! No-way bro: ' + pathname + ' is not a vaild route!'});
    return;
  }
  if (req.accepts('json')) {
    res.send({error: 'Not found: ' + pathname});
    return;
  }
});

// Handle 500
app.use(function (error, req, res, next) {
  res.status(500);
  console.log('Error: ' + error.message);
  if (req.accepts('html')) {
    res.render('error.html.twig', {"message": 'Hu-uh! Some awry business: ' + error.message});
    return;
  }
  if (req.accepts('json')) {
    res.send({error: 'Not found'});
    return;
  }
});

var server = app.listen(8081, function () {
  var host = server.address().address;
  var port = server.address().port;
  console.log("Twit-Con-Pro started on  http://%s:%s", host, port);
});