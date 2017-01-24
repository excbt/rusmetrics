/*jslint node: true, eqeq: true, nomen: true*/
/*global angular, moment*/
'use strict';

angular.module('zpointCw_v1Widget', ['angularWidget', 'chart.js'])
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
    .controller('zpointCw_v1WidgetCtrl', function ($scope, $http, $rootScope, widgetConfig) {
    //data generator
        var timeDetailTypes = {            
            month: {
                timeDetailType: "24h",
                count: 30,
                dateFormatter: function(param) {                    
                    return (param >= 10 ? param : "0" + param) + "-" + moment().format("MM-YYYY");
                }
            },
            day: {
                timeDetailType: "1h",
                count: 24,
                dateFormatter: function(param) {                    
                    return moment().subtract(param, "hours").format("DD-MM-YYYY HH:ss");
                }
            },
            week: {
                timeDetailType: "24h",
                count: 7,
                dateFormatter: function(param) {                    
                    return moment().subtract(7 - param, "days").format("DD-MM-YYYY HH:ss");
                }
            },
            today: {
                timeDetailType: "1h",
                count: 24,
                dateFormatter: function(param) {                    
                    return moment().subtract(param, "hours").format("DD-MM-YYYY HH:ss");
                }
            },
            yesterday: {
                timeDetailType: "1h",
                count: 24,
                dateFormatter: function(param) {                    
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
                node.v_in = Math.random()*10 + 1;
                var v_delta = Math.random();
                node.v_out = node.v_in - v_delta;
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
        var DATA_URL = "../api/subscr/widgets/cw",/*//chart/HwTemp";*/
            ZPOINT_STATUS_TEMPLATE = "widgets/zpointCw_v1/zpoint-state-",
            SERVER_DATE_FORMAT = "DD-MM-YYYY HH:mm";
        
        $scope.widgetOptions = widgetConfig.getOptions();
        //console.log($scope.widgetOptions);
//        var zpstatus = $scope.widgetOptions.zpointStatus;
        $scope.data = {};
        $scope.data.currentCwTemp = null;
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
        $scope.data.startModeIndex = 3;//default mode index; 3 - WEEK
        $scope.data.currentMode = $scope.data.MODES[$scope.data.startModeIndex];
    
        $scope.data.imgPath = "widgets/zpointCw_v1/snowflake.png";
        $scope.data.zpointStatus = ZPOINT_STATUS_TEMPLATE + "green.png";//"widgets/zpointHw/zpoint-state-" + zpstatus + ".png";
        $scope.data.zpointStatusTitle = $scope.widgetOptions.zpointStatusTitle;
        $scope.data.contZpointId = $scope.widgetOptions.contZpointId;
    
        $scope.presentDataFlag = false;
    
        $scope.barChart = {};
        $scope.barChart.labels = [];//["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"];
        $scope.barChart.series = ['Потребление холодной воды, м' + '\u00B3'];
        $scope.barChart.data = [[]];
        $scope.barChart.dataTitle = [];

        $scope.onClick = function (points, evt) {
            console.log(points, evt);
        };
        $scope.barChart.datasetOverride = [];//[{ yAxisID: 'y-axis-1' }, { yAxisID: 'y-axis-2' }];
        $scope.barChart.options = {
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
            var tmpData = rsp.data;
            if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
                tmpData = generateTestData(timeDetailTypes[$scope.data.currentMode.keyname.toLowerCase()]);
            }
            if (!angular.isArray(tmpData) || tmpData.length === 0) {
                $scope.presentDataFlag = false;
                console.log("zpointCwWidget: response data is empty!");
                return false;
            }
            $scope.presentDataFlag = true;
            $scope.barChart.labels = [];
            $scope.barChart.data = [[]];
            $scope.barChart.dataTitle = [];
            var dataTitleElem = {};
            tmpData.forEach(function (elm) {
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
            if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
                return true;
            }
            if (angular.isUndefined(resp) || resp === null) {
                console.log("zpointCwWidget: status response is empty.");
                return false;
            }
            if (angular.isUndefined(resp.data) || resp.data === null) {
                console.log("zpointCwWidget: status response data is empty.");
                return false;
            }
            if (angular.isDefined(resp.data.color) && resp.data.color !== null && angular.isString(resp.data.color)) {
                $scope.data.zpointStatus = ZPOINT_STATUS_TEMPLATE + resp.data.color.toLowerCase() + ".png";
            } else {
                console.log("zpointCwWidget: zpoint status color is empty or not string.");
            }            
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
                console.log("zpointCwWidget: contZpoint or mode is null!");
                return false;
            }
            var url = DATA_URL + "/" + encodeURIComponent($scope.data.contZpointId) + "/chart/data/" + encodeURIComponent(mode.keyname);
            $http.get(url).then(getDataSuccessCallback, errorCallback);
        };
    
        function getZpointState() {
            if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
                return true;
            }
            var url = DATA_URL + "/" + encodeURIComponent($scope.data.contZpointId) + "/status";
            $http.get(url).then(getStatusSuccessCallback, errorCallback);
        }
                // Показания точек учета
        $scope.getIndicators = function () {
            if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
                return true;
            }
            widgetConfig.exportProperties({contObjectId: $scope.widgetOptions.contObjectId, contZpointId: $scope.widgetOptions.contZpointId, action: "openIndicators"});
            return true;
        };
    
        $scope.openNotices = function () {
            if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
                return true;
            }
            widgetConfig.exportProperties({contObjectId: $scope.widgetOptions.contObjectId, action: "openNotices"});
        };
    
        $scope.$on('chart-create', function (event, chart) {
            var chartLegend = document.getElementById("cw-chart-legend");
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