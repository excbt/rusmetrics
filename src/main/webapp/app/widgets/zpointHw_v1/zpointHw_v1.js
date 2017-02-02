/*jslint node: true, eqeq: true, nomen: true*/
/*global angular, moment*/
'use strict';

angular.module('zpointHw_v1Widget', ['angularWidget', 'chart.js'])
    .config(['ChartJsProvider', function (ChartJsProvider) {
        // Configure all charts
        ChartJsProvider.setOptions({
            chartColors: ['#ef473a', '#FDB45C', '#FDB45C', '#803690', '#337ab7'],
            responsive: true
        });
    }])
    .controller('zpointHw_v1WidgetCtrl', function ($scope, $http, $rootScope, widgetConfig) {
    //data generator - use for widget preview
        var timeDetailTypes = {
            month: {
                timeDetailType: "24h",
                count: 30,
                dateFormatter: function (param) {
                    return (param >= 10 ? param : "0" + param) + "-" + moment().format("MM-YYYY");
                }
            },
            day: {
                timeDetailType: "1h",
                count: 24,
                dateFormatter: function (param) {
                    return moment().subtract(param, "hours").format("DD-MM-YYYY HH:ss");
                }
            },
            week: {
                timeDetailType: "24h",
                count: 7,
                dateFormatter: function (param) {
                    return moment().subtract(7 - param, "days").format("DD-MM-YYYY HH:ss");
                }
            },
            today: {
                timeDetailType: "1h",
                count: 24,
                dateFormatter: function (param) {
                    return moment().subtract(param, "hours").format("DD-MM-YYYY HH:ss");
                }
            },
            yesterday: {
                timeDetailType: "1h",
                count: 24,
                dateFormatter: function (param) {
                    return moment().subtract(param, "hours").format("DD-MM-YYYY HH:ss");
                }
            }
        };
        function generateTestData(timeDetailType) {
            var result = [],
                i,
                node;
            for (i = 1; i <= timeDetailType.count; i += 1) {
                node = {};
                node.timeDetailType = timeDetailType.timeDetailType;
                node.t_in = Math.random() * 10 + 50;
                node.dataDateString = timeDetailType.dateFormatter(i);
                result.push(node);
            }
            //console.log(result);
            return result;
        }
    //end data generator
        moment.locale('ru', {
            months : "январь_февраль_март_апрель_май_июнь_июль_август_сентябрь_октябрь_ноябрь_декабрь".split("_"),
            monthsShort : "янв._фев._март_апр._май_июнь_июль_авг._сен._окт._ноя._дек.".split("_")
        });
        $scope.widgetPath = "widgets/zpointHw_v1";
        var DATA_URL = "../api/subscr/widgets/hw",/*//chart/HwTemp";*/
            ZPOINT_STATUS_TEMPLATE = $scope.widgetPath + "/zpoint-state-",
            SERVER_DATE_FORMAT = "DD-MM-YYYY HH:mm",
            SERVER_DATE_FORMAT_SHORT = "DD-MM-YYYY",
            USER_DATE_FORMAT = "DD.MM.YYYY HH:mm",
            T_MAX = 75,
            T_NORM = 60,
            T_COLD = 40;
    
        var NIGHT_DEVIATION = {
                startPeriod: "00:00",
                endPeriod: "05:00",
                deviation: 5
            },
            DAY_DEVIATION = {
                startPeriod: "05:00",
                endPeriod: "00:00",
                deviation: 3
            };
        
        $scope.widgetOptions = widgetConfig.getOptions();
        $scope.data = {};
        $scope.data.currentHwTemp = null;

        $scope.data.MODES = [
            {
                keyname: "TODAY",
                caption: "Сегодня",
                modeClass: "",
                timeDetailType: "1h",
                dateFormat: "HH:mm",
                tooltipDateFormat: "DD.MM.YYYY HH:mm"
            }, {
                keyname: "DAY",
                caption: "Сутки",
                modeClass: "",
                timeDetailType: "1h",
                dateFormat: "HH:mm",
                tooltipDateFormat: "DD.MM.YYYY HH:mm"
            }, {
                keyname: "YESTERDAY",
                caption: "Вчера",
                modeClass: "",
                timeDetailType: "1h",
                dateFormat: "HH:mm",
                tooltipDateFormat: "DD.MM.YYYY HH:mm"
            }, {
                keyname: "WEEK",
                caption: "Неделя",
                modeClass: "active",
                timeDetailType: "24h",
                dateFormat: "DD, MMM",
                tooltipDateFormat: "DD.MM.YYYY"
            }, {
                keyname: "MONTH",
                caption: "Текущий месяц",
                modeClass: "",
                timeDetailType: "24h",
                dateFormat: "DD",
                tooltipDateFormat: "DD.MM.YYYY"
            }
        ];
        $scope.data.startModeIndex = 3;//default mode index; 2 - TODAY
        $scope.data.currentMode = $scope.data.MODES[$scope.data.startModeIndex];
    
        $scope.data.imgPath = $scope.widgetPath + "/tint.png";
        $scope.data.zpointStatus = ZPOINT_STATUS_TEMPLATE + "green.png";//"widgets/zpointHw/zpoint-state-" + zpstatus + ".png";
        $scope.data.zpointStatusTitle = $scope.widgetOptions.zpointStatusTitle;
        $scope.data.contZpointId = $scope.widgetOptions.contZpointId;
    
        $scope.presentDataFlag = false;

        $scope.lineChart = {};
        $scope.lineChart.labels = [];//["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"];
        $scope.lineChart.series = ['t макс, ' + '\u2103', 't норм верх, ' + '\u2103', 't норм низ, ' + '\u2103', 't факт, ' + '\u2103', 't хвс, ' + '\u2103'];
        $scope.lineChart.data = [[], [], [], [], []];
        $scope.lineChart.dataTitle = [];

        $scope.onClick = function (points, evt) {
            console.log(points, evt);
        };
        $scope.lineChart.datasetOverride = [];//[{ yAxisID: 'y-axis-1' }, { yAxisID: 'y-axis-2' }];
        $scope.lineChart.options = {
            elements: {
                line: {
                    fill: false,
                    tension: 0
                }
            },
            legendCallback: function (chart) {
				var text = [], i;
				text.push('<ul class="' + chart.id + '-legend">');
				for (i = 0; i < chart.data.datasets.length; i += 1) {
					text.push('<li><span style="background-color:' + chart.data.datasets[i].borderColor + '"></span>');
					if (chart.data.datasets[i].label) {
						text.push(chart.data.datasets[i].label);
					}
					text.push('</li>');
				}
				text.push('</ul>');

				return text.join('');
			},
            tooltips: {
                callbacks: {
                    beforeTitle: function (arr, data) {
                        var result = "";
                        if (angular.isArray(arr) && arr.length > 0 && $scope.lineChart.dataTitle.length > 0 && $scope.lineChart.dataTitle[arr[0].index].dataDateString !== null) {
                            result = moment($scope.lineChart.dataTitle[arr[0].index].dataDateString, SERVER_DATE_FORMAT).format($scope.data.currentMode.tooltipDateFormat);
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
            var tmpData = rsp.data;
            if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
                tmpData = generateTestData(timeDetailTypes[$scope.data.currentMode.keyname.toLowerCase()]);
            }
            if (!angular.isArray(tmpData) || tmpData.length === 0) {
                $scope.presentDataFlag = false;
                console.log("zpointHw_v1Widget: response data is empty!");
                return false;
            }
            $scope.presentDataFlag = true;
            $scope.lineChart.labels = [];
            $scope.lineChart.data = [[], [], [], [], []];
            $scope.lineChart.dataTitle = [];
            var dataTitleElem = {};
            tmpData.forEach(function (elm) {
                $scope.lineChart.labels.push(moment(elm.dataDateString, SERVER_DATE_FORMAT).format($scope.data.currentMode.dateFormat));
                var curDateFormatted = moment(elm.dataDateString, SERVER_DATE_FORMAT).format(SERVER_DATE_FORMAT_SHORT);
//console.log(curDateFormatted);
                
                if (moment(elm.dataDateString, SERVER_DATE_FORMAT) >= moment(curDateFormatted + " " + NIGHT_DEVIATION.startPeriod, SERVER_DATE_FORMAT) 
                    && moment(elm.dataDateString, SERVER_DATE_FORMAT) <= moment(curDateFormatted + " " + NIGHT_DEVIATION.endPeriod, SERVER_DATE_FORMAT)) {
                    
                    $scope.lineChart.data[1].push(T_NORM + NIGHT_DEVIATION.deviation);
                    $scope.lineChart.data[2].push(T_NORM - NIGHT_DEVIATION.deviation);
                } else {
                    $scope.lineChart.data[1].push(T_NORM + DAY_DEVIATION.deviation);
                    $scope.lineChart.data[2].push(T_NORM - DAY_DEVIATION.deviation);
                }
                $scope.lineChart.data[0].push(T_MAX);
//                $scope.lineChart.data[1].push(T_NORM);
                $scope.lineChart.data[3].push(elm.t_in);
                $scope.lineChart.data[4].push(T_COLD);
                dataTitleElem = {
                    dataDateString : elm.dataDateString
                };
                $scope.lineChart.dataTitle.push(dataTitleElem);
            });
        }
    
        function errorCallback(e) {
            console.log(e);
        }
    
        function getStatusSuccessCallback(resp) {
            if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
                return true;
            }
            if (angular.isUndefined(resp) || resp === null) {
                console.log("zpointHw_v1Widget: status response is empty.");
                return false;
            }
            if (angular.isUndefined(resp.data) || resp.data === null) {
                console.log("zpointHw_v1Widget: status response data is empty.");
                return false;
            }
            if (angular.isDefined(resp.data.color) && resp.data.color !== null && angular.isString(resp.data.color)) {
                $scope.data.zpointStatus = ZPOINT_STATUS_TEMPLATE + resp.data.color.toLowerCase() + ".png";
            } else {
                console.log("zpointHw_v1Widget: zpoint status color is empty or not string.");
            }
            if (angular.isDefined(resp.data.lastHwData) && resp.data.lastHwData !== null) {
                $scope.data.currentHwTemp = resp.data.lastHwData.t_in;
//                ['#ef473a', '#FDB45C', '#803690', '#00ADF9'],
                if ($scope.data.currentHwTemp <= T_COLD) {
                    $scope.data.currentHwTempColor = "#337ab7";
                } else if ($scope.data.currentHwTemp > T_MAX) {
                    $scope.data.currentHwTempColor = "#ef473a";
                } else {
                    $scope.data.currentHwTempColor = "#FDB45C";
                }
            } else {
                console.log("zpointHw_v1Widget: current hw temperature is empty.");
            }
            //$scope.data.forecastTemp = -88;//для теста
        }
    
        $scope.modeChange = function () {
            var mode = $scope.data.currentMode;
            //set class
            $scope.data.MODES.forEach(function (mod) {
                mod.modeClass = "";
            });
            mode.modeClass = "active";
            //get data
            if (angular.isUndefined($scope.data.contZpointId) || $scope.data.contZpointId === null || mode === null || mode.keyname === null) {
                console.log("zpointHw_v1Widget: contZpoint or mode is null!");
                return false;
            }
            var url = DATA_URL + "/" + encodeURIComponent($scope.data.contZpointId) + "/chart/data/" + encodeURIComponent(mode.keyname);
            $http.get(url).then(getDataSuccessCallback, errorCallback);
        };
    
        function getZpointState() {
            if (angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true) {
                return true;
            }
            var url = DATA_URL + "/" + encodeURIComponent($scope.data.contZpointId) + "/status";
            $http.get(url).then(getStatusSuccessCallback, errorCallback);
        }
                // Показания точек учета
        $scope.getIndicators = function () {
            if (angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true) {
                return true;
            }
            widgetConfig.exportProperties({contObjectId: $scope.widgetOptions.contObjectId, contZpointId: $scope.widgetOptions.contZpointId, action: "openIndicators"});
            return true;
        };
    
        $scope.openNotices = function () {
            if (angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true) {
                return true;
            }
            widgetConfig.exportProperties({contObjectId: $scope.widgetOptions.contObjectId, action: "openNotices"});
        };
    
        $scope.$on('chart-create', function (event, chart) {
            var chartLegend = document.getElementById("hw-chart-legend-" + $scope.data.contZpointId);
            if (angular.isDefined(chartLegend) && chartLegend !== null) {
                chartLegend.innerHTML = chart.generateLegend();
            }
        });
        
        function initWidget() {
            $scope.modeChange();
            getZpointState();
        }
        
        initWidget();
        
    });