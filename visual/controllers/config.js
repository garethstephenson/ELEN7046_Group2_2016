var express = require('express');
var router = express.Router();
var fs = require("fs");
var config = require("./../config/common");

router.get('/', function (req, res) {

  config.getConfigData(function (configData) {
    res.setHeader('Content-Type', 'application/json');
    res.send(configData);
  }, function () {
    res.setHeader('Content-Type', 'application/json');
    res.send(null);
  });
});

router.post('/', function (req, res) {

//console.log(req.body);
  //console.log(JSON.stringify(req.body.topicsPath));

//  var data = '{"topicsPathIsRelative": true,"topicsPath": "/../data/topics/","defaultTopicId": 1}';
//  config.setConfigData(data, function () {
//    res.setHeader('Content-Type', 'application/json');
//    req = null;
//    res.send({'success': true});
//  }, function () {
    res.setHeader('Content-Type', 'application/json');
    res.send({'success': false});
//  });
});

router.get('/topics', function (req, res) {

  config.getConfigData(function (configData) {

    config.getTopicsData(configData, function (topicData) {
      res.setHeader('Content-Type', 'application/json');
      res.send(topicData);
    }, function () {
      res.send(null);
    });
  }, function () {
    res.send(null);
  });
});

module.exports = router;