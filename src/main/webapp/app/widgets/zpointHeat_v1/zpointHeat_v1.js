/*jslint node: true, eqeq: true*/
/*global angular, moment*/
'use strict';

angular.module('zpointHeat_v1Widget', ['angularWidget', 'chart.js', 'ngCookies'])
    .config(['ChartJsProvider', function (ChartJsProvider) {
        // Configure all charts
        ChartJsProvider.setOptions({
            chartColors: ['#46BFBD', '#803690'],
            responsive: true
        });
        // Configure all line charts
//        ChartJsProvider.setOptions('line', {
//            showLines: false
//        });
    }])
    .controller('zpointHeat_v1WidgetCtrl', function ($scope, $http, $cookies, $rootScope, widgetConfig) {
        moment.locale('ru', {
            months : "январь_февраль_март_апрель_май_июнь_июль_август_сентябрь_октябрь_ноябрь_декабрь".split("_"),
            monthsShort : "янв._фев._март_апр._май_июнь_июль_авг._сен._окт._ноя._дек.".split("_")
        });
        var DATA_URL = "../api/subscr/widgets/heat",/*//chart/heatTemp";*/
            ZPOINT_STATUS_TEMPLATE = "widgets/zpointHeat/zpoint-state-";
        
        $scope.widgetOptions = widgetConfig.getOptions();
        //console.log($scope.widgetOptions);
//        var zpstatus = $scope.widgetOptions.zpointStatus;
        $scope.data = {};
        $scope.data.forecastTemp = null;
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
                keyname: "WEEK",
                caption: "Неделя",
                modeClass: "active",
                timeDetailType: "24h",
                dateFormat: "DD, MMM"
            }, {
                keyname: "YESTERDAY",
                caption: "Вчера",
                modeClass: "",
                timeDetailType: "1h",
                dateFormat: "HH:mm"
            }, {
                keyname: "TODAY",
                caption: "Сегодня",
                modeClass: "",
                timeDetailType: "1h",
                dateFormat: "HH:mm"
            }
        ];
        $scope.data.startModeIndex = 0;//default mode index; 0 - WEEK
        $scope.data.currentMode = $scope.data.MODES[$scope.data.startModeIndex];
    
        $scope.data.imgPath = "widgets/zpointHeat/glyphicons-85-heat.png";
        $scope.data.zpointStatus = "";//"widgets/zpointHeat/zpoint-state-" + zpstatus + ".png";
        $scope.data.zpointStatusTitle = $scope.widgetOptions.zpointStatusTitle;
        $scope.data.contZpointId = $scope.widgetOptions.contZpointId;
    
        $scope.presentDataFlag = false;
        $scope.lineChart = {};
        $scope.lineChart.labels = [];//["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"];
        $scope.lineChart.series = ['T норм', 'T факт'];
        $scope.lineChart.data = [[], []];
//        var tmpData = [
//            [65, 59, 80, 81, 56, 55, 40],
//            [28, 48, 40, 19, 86, 27, 90]
//        ];
        $scope.onClick = function (points, evt) {
            console.log(points, evt);
        };
        $scope.lineChart.datasetOverride = [];//[{ yAxisID: 'y-axis-1' }, { yAxisID: 'y-axis-2' }];
        $scope.lineChart.options = {
            legend: {
                display: true
            },
            tooltips: {
                beforeTitle: function (arr, data) {
                    return "tooltip";
                }
            }
        };
//        var tmpOpt = {
//            scales: {
//                yAxes: [
//                    {
//                        id: 'y-axis-1',
//                        type: 'linear',
//                        display: true,
//                        position: 'left'
//                    },
//                    {
//                        id: 'y-axis-2',
//                        type: 'linear',
//                        display: true,
//                        position: 'right'
//                    }
//                ]
//            },
//            legend: {
//                display: true
//            }
//        };
    
        function getDataSuccessCallback(rsp) {
//            console.log(rsp.data);
            if (!angular.isArray(rsp.data) || rsp.data.length === 0) {
                $scope.presentDataFlag = false;
                console.log("zpointHeatWidget: response data is empty!");
                return false;
            }
            $scope.presentDataFlag = true;
            $scope.lineChart.labels = [];
            $scope.lineChart.data = [[], []];
            rsp.data.forEach(function (elm) {
                $scope.lineChart.labels.push(moment(elm.dataDateString, "DD-MM-YYYY HH:mm").format($scope.data.currentMode.dateFormat));                
                $scope.lineChart.data[0].push(elm.chartT_in);
                $scope.lineChart.data[1].push(elm.t_in);
            });
        }
    
        function errorCallback(e) {
            console.log(e);
        }
    
        function getStatusSuccessCallback(resp) {
            if (angular.isUndefined(resp) || resp === null) {
                console.log("zpointHeatWidget: status response is empty.");
                return false;
            }
            if (angular.isUndefined(resp.data) || resp.data === null) {
                console.log("zpointHeatWidget: status response data is empty.");
                return false;
            }
            if (angular.isDefined(resp.data.color) && resp.data.color !== null && angular.isString(resp.data.color)) {
                $scope.data.zpointStatus = ZPOINT_STATUS_TEMPLATE + resp.data.color.toLowerCase() + ".png";
            } else {
                console.log("zpointHeatWidget: zpoint status color is empty or not string.");
            }
            if (angular.isDefined(resp.data.forecastTemp) && resp.data.forecastTemp !== null) {
                $scope.data.forecastTemp = resp.data.forecastTemp;
                if ($scope.data.forecastTemp <= 0) {
                    $scope.data.forecastTempColor = "#337ab7";
                } else {
                    $scope.data.forecastTempColor = "#ef473a";
                }
            } else {
                console.log("zpointHeatWidget: forecast temperature is empty.");
            }
            //$scope.data.forecastTemp = -88;//для теста
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
                console.log("zpointHeatWidget: contZpoint or mode is null!");
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