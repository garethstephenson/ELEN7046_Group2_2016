var express = require('express');
var router = express.Router();
var Twig = require("twig");

// /config/*

router.get('/', function (req, res) {

  res.send('config');
  res.end();
  //res.render('index.html.twig', {});
});

module.exports = router;