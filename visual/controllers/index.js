var express = require('express');
var router = express.Router();
var Twig = require("twig");
var fs = require("fs");
var config = require("./../config/common");

// routing config
router.use('/config', require('./config'));
router.use('/data', require('./data'));

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

    config.getConfigData(function (configData) {
      config.getTopicsData(configData, function (topicData) {
        
        var topic = '-';
        
        for(var index = 0; index < topicData.length; index++){
          if (configData.data.defaultTopicId == topicData[index].id)
            topic = topicData[index].name;
        }
        res.render('index.html.twig', {"data": panels, "category": topic});
        return;
      }, function () {
      });
    }, function () {
    });
  });
});

router.get('/about', function (req, res) {

  config.getConfigData(function (configData) {
      config.getTopicsData(configData, function (topicData) {
        
        var topic = '-';
        
        for(var index = 0; index < topicData.length; index++){
          if (configData.data.defaultTopicId == topicData[index].id)
            topic = topicData[index].name;
        }
        res.render('about.html.twig', {"category": topic});
        return;
      }, function () {
        res.render('about.html.twig', {"category": '!! Cannot load Topic Data !!'});
      });
    }, function () {
      res.render('about.html.twig', {"category": '!! Cannot load Config Data !!'});
    });
});

module.exports = router;