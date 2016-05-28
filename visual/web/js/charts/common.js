/* 
 * This file holds common 'static' functions shared betwee charts
 */
/* global d3 */
var commonCharts = {
  // Array Helpers
  getIndexByValue: function (value, array) {
    for (var index = 0; index < array.length; index++) {
      if (array[index] === value) {
        return index;
      }
    }
    return -1;
  },
  // Date Helpers
  getDatePartValue: function (dateString, datePart) {
    //ref: https://github.com/d3/d3/wiki/Time-Formatting

    var dateFormat = d3.time.format("%Y-%m-%d");
    if (dateString.indexOf('T') > 0) {
      dateFormat = d3.time.format("%Y-%m-%dT%H:%MZ");
    }
    var printFormat = d3.time.format(datePart);
    var date = dateFormat.parse(dateString);
    return printFormat(date);
  },
  getHourValue: function (dateString) {
    return commonCharts.getDatePartValue(dateString, "%H");
  },
  getDayValue: function (dateString) {
    return commonCharts.getDatePartValue(dateString, "%d");
  },
  getMonthValue: function (dateString) {
    return commonCharts.getDatePartValue(dateString, "%m");
  },
  getDateValue: function (dateString) {
    return commonCharts.getDatePartValue(dateString, "%Y-%m-%d");
  },
  make2DigitNumberArray: function (start, end) {
    var array = [];
    for (var index = start; index <= end; index++) {
      array.push(("0" + (index)).slice(-2));
    }
    return array;
  },
  // Category Color Helprs 
  getCategoryColor: function (category, categoryColors) {
    for (var index = 0; index < categoryColors.length; index++) {
      var colorItem = categoryColors[index];
      if (category.Category === colorItem.Category) {
        return colorItem.Color;
      }
    }
    return "#000000";
  },
  getMaxCategoryItem: function (item) {
    var maxCategoryItem = null;
    for (var index = 0; index < item.Data.length; index++) {
      var categoryItem = item.Data[index];
      if (!maxCategoryItem || categoryItem.Count > maxCategoryItem.Count) {
        maxCategoryItem = categoryItem;
      }
    }
    return maxCategoryItem;
  },
};

