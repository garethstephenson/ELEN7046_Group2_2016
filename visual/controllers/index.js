var express = require('express');
var router = express.Router();
var Twig = require("twig");
var fs = require("fs");

// routing config
router.use('/config', require('./config'))
router.use('/data', require('./data'))

//default routes here
//these could go in a separate file if you want
router.get('/', function (req, res) {

  fs.readFile(__dirname + "/../config/" + "index.json", 'utf8', function (err, panelData) {
    if (err) {

      console.log("Error reading file: %s", err);
      res.render('error.html.twig', {"message": 'Whoops: ' + err});
      return;
    }
    var panels = JSON.parse(panelData);
    res.render('index.html.twig', {"data": panels});
  });
});

router.get('/about', function (req, res) {

  res.render('about.html.twig', {});
});

router.get('/error', function (req, res) {

  function error(msg) {
    this.message = msg;
    this.name = 'error';
    return this;
  }
  ;

  throw error('This is bad');
});

module.exports = router;