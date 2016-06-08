var express = require('express');
var router = express.Router();
var Twig = require("twig");
var fs = require("fs");
// /config/*

router.get('/', function (req, res) {

  getConfigData(function (configData) {
    res.setHeader('Content-Type', 'application/json');
    res.send(configData);
  }, function () {
    res.setHeader('Content-Type', 'application/json');
    res.send(null);
  });
});
router.get('/topics', function (req, res) {

  getConfigData(function (configData) {

    getTopicsData(__dirname + configData.topicsPath, function (topicData) {
      res.setHeader('Content-Type', 'application/json');
      res.send(topicData);
    }, function () {
      res.setHeader('Content-Type', 'application/json');
      res.send(null);
    });
  }, function () {
    res.setHeader('Content-Type', 'application/json');
    res.send(null);
  });
});

function getConfigData(successCallBack, errorCallBack) {
  fs.readFile(__dirname + "/../config/" + "dataConfig.json", 'utf8', function (error, data) {
    if (error) {
      console.log("Error reading file: %s", error);
      errorCallBack(err);
    }
    var configData = JSON.parse(data);
    successCallBack(configData);
  });
}

function getTopicsData(topicsPath, successCallBack, errorCallBack) {
  fs.readFile(topicsPath + "topics.json", 'utf8', function (error, data) {
    if (error) {
      console.log("Error reading file: %s", error);
      errorCallBack(err);
    }
    var topicsData = JSON.parse(data);
    successCallBack(topicsData);
  });
}

module.exports = router;