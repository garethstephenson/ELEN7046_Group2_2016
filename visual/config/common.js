var fs = require("fs");

module.exports = {
  
  configFilePath: __dirname + "/" + "dataConfig.json",
  
  getConfigData: function (successCallBack, errorCallBack) {
    var self = this;

    fs.readFile(self.configFilePath, 'utf8', function (error, data) {
      if (error) {
        console.log("Error reading file: %s", error);
        errorCallBack(error);
        return;
      }
      var configData = JSON.parse(data);
      var path = configData.topicsPath;
      if (configData.topicsPathIsRelative) {
        path = __dirname + configData.topicsPath;
      }
      successCallBack({
        'topicPath': path,
        'data': configData
      });
    });
  },
  setConfigData: function (data,successCallBack, errorCallBack) {
    var self = this;
    
    fs.writeFile(self.configFilePath, data, function (error) {
      if (error) {
        console.log("Error writing config file: %s", error);
        errorCallBack(error);
        return;
      }
      console.log("config File saved.");
      successCallBack();
      return;
    });
  },
  getTopicsData: function (configData, successCallBack, errorCallBack) {

    fs.readFile(configData.topicPath + "topics.json", 'utf8', function (error, data) {
      if (error) {
        console.log("Error reading file: %s", error);
        errorCallBack(error);
        return;
      }
      var topicsData = JSON.parse(data);
      successCallBack(topicsData);
    });
  },
  getDefaultTopicData: function (successCallBack, errorCallBack) {
    var self = this;
    self.getConfigData(function (configData) {
      self.getTopicsData(configData, function (topicsData) {
        for (var index = 0; index < topicsData.length; index++) {
          var item = topicsData[index];
          if (item.id === configData.data.defaultTopicId) {

            var data = {
              'configData': configData,
              'topicData': item
            }

            successCallBack(data);
            return;
          }
        }
        errorCallBack('Topic Not found');
      },
        function (error) {
          errorCallBack(error);
        });
    }, function (error) {
      errorCallBack(error);
    });
  },
  getDefaultTopicDataFile: function (filename, successCallBack, errorCallBack) {
    var self = this;
    self.getDefaultTopicData(function (defaultConfigData) {
      var path = defaultConfigData.configData.topicPath + "/" + defaultConfigData.topicData.folder + "/" + filename;
      console.log('Reading Data:' + path);
      fs.readFile(path, 'utf8', function (error, data) {
        if (error) {
          console.log("File read error: %s", error);
          errorCallBack(error);
          return;
        }
        successCallBack(JSON.parse(data));
      });

    }, function (error) {
      errorCallBack(error);
    });
  }
};