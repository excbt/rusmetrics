/*jslint node: true, eqeq: true*/
/*global angular, moment*/
'use strict';

angular.module('zpointHeatWidget', ['angularWidget', 'chart.js', 'ngCookies'])
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
    .controller('zpointHeatWidgetCtrl', function ($scope, $http, $cookies, $rootScope, widgetConfig) {
        var DATA_URL = "../api/subscr/widgets/chart/heatTemp";
        
        $scope.widgetOptions = widgetConfig.getOptions();
        var zpstatus = $scope.widgetOptions.zpointStatus;
        $scope.data = {};
        $scope.data.curTemp = null;
        $scope.data.MODES = [
            {
                keyname: "YESTERDAY",
                caption: "Вчера",
                modeClass: ""
            }, {
                keyname: "TODAY",
                caption: "Сегодня",
                modeClass: ""
            }, {
                keyname: "WEEK",
                caption: "Неделя",
                modeClass: "active"
            }
        ];
        $scope.data.startModeIndex = 2;//default mode index; 2 - WEEK
    
        $scope.data.imgPath = "widgets/zpointHeat/glyphicons-85-heat.png";
        $scope.data.zpointStatus = "widgets/zpointHeat/zpoint-state-" + zpstatus + ".png";
        $scope.data.zpointStatusTitle = $scope.widgetOptions.zpointStatusTitle;
        $scope.data.contZpointId = $scope.widgetOptions.contZpointId;
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
    
        function successCallback(rsp) {
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
                $scope.lineChart.labels.push(elm.dataDateString);
                $scope.lineChart.data[0].push(elm.chartT_in);
                $scope.lineChart.data[1].push(elm.t_in);
            });
        }
    
        function errorCallback(e) {
            console.log(e);
        }
    
        $scope.modeClick = function (mode) {
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
            var url = DATA_URL + "?contZpointId=" + encodeURIComponent($scope.data.contZpointId) + "&mode=" + encodeURIComponent(mode.keyname);
            $http.get(url).then(successCallback, errorCallback);
        };
    
    //Indicators
        $scope.setIndicatorsParams = function (objectId, zpointId) {
//            $scope.selectedZpoint(objectId, zpointId);
            $cookies.contZPoint = $scope.data.contZpointId;
            $cookies.contObject = $scope.data.contObjectId;
            $cookies.contZPointName = $scope.data.contZPointName;
            $cookies.contObjectName = $scope.data.objectFullName;

            $cookies.deviceModel = $scope.data.zpointModel;
            $cookies.deviceSN = $scope.data.zpointNumber;

            if (angular.isUndefined($cookies.timeDetailType) || ($cookies.timeDetailType == "undefined") || ($cookies.timeDetailType == "null")) {
                $cookies.timeDetailType = "24h";
            }

            $cookies.isManualLoading = ($scope.currentZpoint.isManualLoading === null ? false : $scope.currentZpoint.isManualLoading) || false;
//console.log($scope.currentZpoint);                    
            $rootScope.reportStart = moment().subtract(6, 'days').startOf('day').format('YYYY-MM-DD');
            $rootScope.reportEnd = moment().endOf('day').format('YYYY-MM-DD');

//                    window.location.assign("#/objects/indicators/");
        };
    
                // Показания точек учета
        $scope.getIndicators = function (objectId, zpointId) {
            $scope.setIndicatorsParams(objectId, zpointId);

            var url = "#/objects";
//                    url += "/impulse-indicators";
            if ($scope.data.isImpulse === true) {
                url += "/impulse-indicators";
            } else if ($scope.data.zpointType === 'el') {
                url += "/indicator-electricity";
            } else {
                url += "/indicators";
            }
            url += "/?objectId=" + encodeURIComponent(objectId) + "&zpointId=" + encodeURIComponent(zpointId) + "&objectName=" + encodeURIComponent($scope.data.objectFullName) + "&zpointName=" + encodeURIComponent($scope.data.zpointName);
            //add info about device
//console.log($scope.currentZpoint);                    

            url += "&deviceModel=" + encodeURIComponent($scope.data.zpointModel);
            url += "&deviceSN=" + encodeURIComponent($scope.data.zpointNumber);

            if (!angular.isDefined($scope.data.measureUnitCaption)) {
                url += "&mu=" + encodeURIComponent($scope.data.measureUnitCaption);
            }

            window.open(url, '_blank');
        };
        
        function initWidget() {
            $scope.modeClick($scope.data.MODES[$scope.data.startModeIndex]);
        }
        
        initWidget();
        
    });