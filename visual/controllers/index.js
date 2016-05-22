var express = require('express');
var router = express.Router();
var Twig = require("twig");

// routing config
router.use('/config', require('./config'))
router.use('/data', require('./data'))

//default routes here
//these could go in a separate file if you want
router.get('/', function (req, res) {

  // TODO : Create a list of panels to draw

  var panels = {
    "panels": [
      {
        name: "Category Summary",
        size: "3",
        type: "grapths/topicCategorySummaryV.html.twig",
        dataUrl: "data/topicCategorySummary",
        specialClass: "panel-success"
      },
      {
        name: "Global Relational View",
        size: "3",
        type: "grapths/global.html.twig",
        dataUrl: "data/placesData"
      },
      {
        name: "Category Over Time (Combined)",
        size: "6",
        type: "grapths/topicCategoryCombinedHeatMap.html.twig",
        dataUrl: "data/categoryCountPerDay",
        specialClass: "panel-primary"
      },
      {
        name: "Category Over Time",
        size: "6",
        type: "grapths/topicCategoryHeatMap.html.twig",
        dataUrl: "data/categoryCountPerDay",
        specialClass: "panel-primary"
      },
      {
        name: "Category Summary",
        size: "3",
        type: "grapths/topicCategorySummaryH.html.twig",
        dataUrl: "data/topicCategorySummary",
        specialClass: "panel-success"
      },
      {
        name: "Criteria",
        size: "3",
        type: "criteria.html.twig",
        dataUrl: "filter/criteria",
        specialClass: "panel-primary"
      },
      {
        name: "Crossfilter",
        size: "4",
        type: "grapths/crossfilter.html.twig",
        dataUrl: "data/crossfiler",
        specialClass: "panel-warning"
      },
      {
        name: "Words",
        size: "8",
        type: "grapths/wordTree.html.twig",
        dataUrl: "data/wordtree",
        specialClass: "panel-info"
      }
    ]};

  res.render('index.html.twig', {"data": panels});
});

router.get('/about', function (req, res) {

  res.render('about.html.twig', {});
});

router.get('/testerror', function (req, res) {

  function error(msg) {
    this.message = msg;
    this.name = 'error';
    return this;
  }
  ;

  throw error('This is bad');
});

module.exports = router;