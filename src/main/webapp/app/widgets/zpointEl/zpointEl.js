/*jslint node: true, eqeq: true, nomen: true*/
/*global angular, moment*/
'use strict';

angular.module('zpointElWidget', ['angularWidget', 'chart.js'])
    .config(['ChartJsProvider', function (ChartJsProvider) {
        // Configure all charts
        ChartJsProvider.setOptions({
            chartColors: ['#337ab7', '#ef473a', '#FDB45C', '#803690'],
            responsive: true
        });
        // Configure all line charts
//        ChartJsProvider.setOptions('line', {
//            showLines: false
//        });
    }])
    .controller('zpointElWidgetCtrl', function ($scope, $http, $rootScope, widgetConfig) {
        moment.locale('ru', {
            months : "январь_февраль_март_апрель_май_июнь_июль_август_сентябрь_октябрь_ноябрь_декабрь".split("_"),
            monthsShort : "янв._фев._март_апр._май_июнь_июль_авг._сен._окт._ноя._дек.".split("_")
        });
        var DATA_URL = "../api/subscr/widgets/el",/*//chart/HwTemp";*/
            ZPOINT_STATUS_TEMPLATE = "widgets/zpointEl/zpoint-state-",
            SERVER_DATE_FORMAT = "DD-MM-YYYY HH:mm";
        
        $scope.widgetOptions = widgetConfig.getOptions();
        //console.log($scope.widgetOptions);
//        var zpstatus = $scope.widgetOptions.zpointStatus;
        $scope.data = {};
        $scope.data.currentCwTemp = null;
//        $scope.data.MODES = [
//            {
//                keyname: "TODAY",
//                caption: "Сегодня",
//                modeClass: "",
//                timeDetailType: "1h"
//            }, {
//                keyname: "24",
//                caption: "Сутки",
//                modeClass: "",
//                timeDetailType: "1h"
//            }, {
//                keyname: "YESTERDAY",
//                caption: "Вчера",
//                modeClass: "",
//                timeDetailType: "1h"
//            }, {
//                keyname: "WEEK",
//                caption: "Неделя",
//                modeClass: "active",
//                timeDetailType: "24h"
//            }, {
//                keyname: "MONTH",
//                caption: "Текущий месяц",
//                modeClass: "",
//                timeDetailType: "24h"
//            }
//        ];
        $scope.data.MODES = [
            {
                keyname: "YEAR",
                caption: "Год",
                modeClass: "",
                timeDetailType: "1mon",
                dateFormat: "MMM",
                tooltipDateFormat: "MMM, YYYY"
            }, {
                keyname: "MONTH",
                caption: "Месяц",
                modeClass: "",
                timeDetailType: "24h",
                dateFormat: "DD",
                tooltipDateFormat: "DD.MM.YYYY"
            }, {
                keyname: "WEEK",
                caption: "Неделя",
                modeClass: "active",
                timeDetailType: "24h",
                dateFormat: "DD, MMM",
                tooltipDateFormat: "DD.MM.YYYY"
            }, {
                keyname: "DAY",
                caption: "Сутки",
                modeClass: "",
                timeDetailType: "1h",
                dateFormat: "HH:mm",
                tooltipDateFormat: "DD.MM.YYYY HH:mm"
            }
        ];
        $scope.data.startModeIndex = 2;//default mode index; 2 - TODAY
        $scope.data.currentMode = $scope.data.MODES[$scope.data.startModeIndex];
    
        $scope.data.imgPath = "widgets/zpointEl/flash.png";
        $scope.data.zpointStatus = "";//"widgets/zpointEl/zpoint-state-" + zpstatus + ".png";
        $scope.data.zpointStatusTitle = $scope.widgetOptions.zpointStatusTitle;
        $scope.data.contZpointId = $scope.widgetOptions.contZpointId;
    
        $scope.presentDataFlag = false;
        $scope.barChart = {};
        $scope.barChart.labels = [];//["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"];
        $scope.barChart.series = ['Потребление электрической энергии, КВт*ч'];
        $scope.barChart.data = [[]];
        $scope.barChart.dataTitle = [];
//        var tmpData = [
//            [65, 59, 80, 81, 56, 55, 40],
//            [28, 48, 40, 19, 86, 27, 90]
//        ];
        $scope.onClick = function (points, evt) {
            console.log(points, evt);
        };
        $scope.barChart.datasetOverride = [];//[{ yAxisID: 'y-axis-1' }, { yAxisID: 'y-axis-2' }];
        $scope.barChart.options = {            
            legend: {
                display: true
            },
            tooltips: {                
                callbacks: {
                    beforeTitle: function (arr, data) {
                        var result = "";
                        if (angular.isArray(arr) && arr.length > 0 && $scope.barChart.dataTitle.length > 0 && $scope.barChart.dataTitle[arr[0].index].dataDateString !== null) {
                            result = moment($scope.barChart.dataTitle[arr[0].index].dataDateString, SERVER_DATE_FORMAT).format($scope.data.currentMode.tooltipDateFormat);
                        }
                        return result;
                    },
                    title: function () {
                        return "";
                    }
                }
                
            }
        };
    
        function getDataSuccessCallback(rsp) {
//            console.log(rsp.data);
            if (!angular.isArray(rsp.data) || rsp.data.length === 0) {
                $scope.presentDataFlag = false;
                console.log("zpointCwWidget: response data is empty!");
                return false;
            }
            $scope.presentDataFlag = true;
            $scope.barChart.labels = [];
            $scope.barChart.data = [[]];
            $scope.barChart.dataTitle = [];
            //count el tariffs
            var elTariffCounter = 0;
//            rsp.data.some(function (elm) {
//                if () {
//                }
//            });
            var dataTitleElem = {};
            rsp.data.forEach(function (elm) {
                $scope.barChart.labels.push(moment(elm.dataDateString, SERVER_DATE_FORMAT).format($scope.data.currentMode.dateFormat));                
                $scope.barChart.data[0].push((elm.v_in - elm.v_out).toFixed(3));
                dataTitleElem = {
                    dataDateString : elm.dataDateString
                };
                $scope.barChart.dataTitle.push(dataTitleElem);
            });
        }
    
        function errorCallback(e) {
            console.log(e);
        }
    
        function getStatusSuccessCallback(resp) {
            if (angular.isUndefined(resp) || resp === null) {
                console.log("zpointElWidget: status response is empty.");
                return false;
            }
            if (angular.isUndefined(resp.data) || resp.data === null) {
                console.log("zpointElWidget: status response data is empty.");
                return false;
            }
            if (angular.isDefined(resp.data.color) && resp.data.color !== null && angular.isString(resp.data.color)) {
                $scope.data.zpointStatus = ZPOINT_STATUS_TEMPLATE + resp.data.color.toLowerCase() + ".png";
            } else {
                console.log("zpointElWidget: zpoint status color is empty or not string.");
            }            
        }
    
        $scope.modeClick = function (mode) {
            $scope.data.currentMode = mode;
            //set class
            $scope.data.MODES.forEach(function (mod) {
                mod.modeClass = "";
            });
            mode.modeClass = "active";
            //get data
            if (angular.isUndefined($scope.data.contZpointId) || $scope.data.contZpointId === null || mode === null || mode.keyname === null) {
                console.log("zpointElWidget: contZpoint or mode is null!");
                return false;
            }
            var url = DATA_URL + "/" + encodeURIComponent($scope.data.contZpointId) + "/chart/data/" + encodeURIComponent(mode.keyname);
            $http.get(url).then(getDataSuccessCallback, errorCallback);
        };
    
        function getZpointState() {
            var url = DATA_URL + "/" + encodeURIComponent($scope.data.contZpointId) + "/status";
            $http.get(url).then(getStatusSuccessCallback, errorCallback);
        }
                // Показания точек учета
        $scope.getIndicators = function () {
//            widgetConfig.requestToGetIndicators({contObjectId: $scope.widgetOptions.contObjectId, contZpointId: $scope.widgetOptions.contZpointId});
            widgetConfig.exportProperties({contObjectId: $scope.widgetOptions.contObjectId, contZpointId: $scope.widgetOptions.contZpointId, action: "openIndicators"});
//            $scope.$broadcast('requestToGetIndicators', {contObjectId: $scope.widgetOptions.contObjectId, contZpointId: $scope.widgetOptions.contZpointId});
            return true;
        };
    
        $scope.openNotices = function () {
            widgetConfig.exportProperties({contObjectId: $scope.widgetOptions.contObjectId, action: "openNotices"});
        };
        
        function initWidget() {
            $scope.modeClick($scope.data.currentMode);
            getZpointState();
        }
        
        initWidget();
        
    });