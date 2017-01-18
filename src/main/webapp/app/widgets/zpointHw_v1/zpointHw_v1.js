/*jslint node: true, eqeq: true, nomen: true*/
/*global angular, moment*/
'use strict';

angular.module('zpointHw_v1Widget', ['angularWidget', 'chart.js'])
    .config(['ChartJsProvider', function (ChartJsProvider) {
        // Configure all charts
        ChartJsProvider.setOptions({
            chartColors: ['#ef473a', '#FDB45C', '#803690', '#337ab7'],
            responsive: true
        });
        // Configure all line charts
//        ChartJsProvider.setOptions('line', {
//            showLines: false
//        });
    }])
    .controller('zpointHw_v1WidgetCtrl', function ($scope, $http, $rootScope, widgetConfig) {
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
                node.t_in = Math.random()*10 + 50;                
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
        var DATA_URL = "../api/subscr/widgets/hw",/*//chart/HwTemp";*/
            ZPOINT_STATUS_TEMPLATE = "widgets/zpointHw_v1/zpoint-state-",
            SERVER_DATE_FORMAT = "DD-MM-YYYY HH:mm",
            USER_DATE_FORMAT = "DD.MM.YYYY HH:mm",
            T_MAX = 75,
            T_NORM = 60,
            T_COLD = 40;
        
        $scope.widgetOptions = widgetConfig.getOptions();
        //console.log($scope.widgetOptions);
//        var zpstatus = $scope.widgetOptions.zpointStatus;
        $scope.data = {};
        $scope.data.currentHwTemp = null;
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
                modeClass: "",
                timeDetailType: "24h",
                dateFormat: "DD, MMM",
                tooltipDateFormat: "DD.MM.YYYY"
            }, {
                keyname: "YESTERDAY",
                caption: "Вчера",
                modeClass: "",
                timeDetailType: "1h",
                dateFormat: "HH:mm",
                tooltipDateFormat: "DD.MM.YYYY HH:mm"
            }, {
                keyname: "TODAY",
                caption: "Сегодня",
                modeClass: "active",
                timeDetailType: "1h",
                dateFormat: "HH:mm",
                tooltipDateFormat: "DD.MM.YYYY HH:mm"
            }
        ];
        $scope.data.startModeIndex = 2;//default mode index; 2 - TODAY
        $scope.data.currentMode = $scope.data.MODES[$scope.data.startModeIndex];
    
        $scope.data.imgPath = "widgets/zpointHw_v1/tint.png";
        $scope.data.zpointStatus = ZPOINT_STATUS_TEMPLATE + "green.png";//"widgets/zpointHw/zpoint-state-" + zpstatus + ".png";
        $scope.data.zpointStatusTitle = $scope.widgetOptions.zpointStatusTitle;
        $scope.data.contZpointId = $scope.widgetOptions.contZpointId;
    
        $scope.presentDataFlag = false;
    
        var customTooltips_v1 = function (tooltip) {
			// Tooltip Element
			var tooltipEl = document.getElementById('chartjs-tooltip-hw');
            
			if (!tooltipEl) {
				tooltipEl = document.createElement('div');
				tooltipEl.id = 'chartjs-tooltip-hw';
				tooltipEl.innerHTML = "<table></table>";
				document.body.appendChild(tooltipEl);
			}

			// Hide if no tooltip
			if (tooltip.opacity === 0) {
				tooltipEl.style.opacity = 0;
				return;
			}

			// Set caret Position
			tooltipEl.classList.remove('above', 'below', 'no-transform');
			if (tooltip.yAlign) {
				tooltipEl.classList.add(tooltip.yAlign);
			} else {
				tooltipEl.classList.add('no-transform');
			}

			function getBody(bodyItem) {
				return bodyItem.lines;
			}

			// Set Text
			if (tooltip.body) {
//                console.log(tooltip);
				var titleLines = tooltip.title || [];
				var bodyLines = tooltip.body.map(getBody);
                var index = null;
                if (tooltip.dataPoints && tooltip.dataPoints.length > 0) {
                    index = tooltip.dataPoints[0].index;
                }

				var innerHtml = '<thead>';
                if (index !== null && $scope.lineChart.dataTitle.length > 0 && $scope.lineChart.dataTitle[index].dataDateString !== null) {
                    innerHtml += '<tr><th>' + moment($scope.lineChart.dataTitle[index].dataDateString, SERVER_DATE_FORMAT).format($scope.data.currentMode.tooltipDateFormat) + '</th></tr>';
                } else {
                    titleLines.forEach(function (title) {
                        innerHtml += '<tr><th>' + title + '</th></tr>';
                    });
                }
                if (index !== null && $scope.lineChart.dataTitle.length > 0 && $scope.lineChart.dataTitle[index].t_ambiance !== null) {
                    innerHtml += '<tr><th>' + 't <sub>окр.</sub> = ' + $scope.lineChart.dataTitle[index].t_ambiance + '&deg;C</th></tr>';
                }
                
				innerHtml += '</thead><tbody>';

				bodyLines.forEach(function (body, i) {
					var colors = tooltip.labelColors[i];
					var style = 'background:' + colors.backgroundColor;
					style += '; border-color:' + colors.borderColor;
					style += '; border-width: 2px';
					var span = '<span class="chartjs-tooltip-key-hw-v1" style="' + style + '"></span>';
					innerHtml += '<tr><td>' + span + body + '&deg;C</td></tr>';
				});
				innerHtml += '</tbody>';

				var tableRoot = tooltipEl.querySelector('table');
//                console.log(tooltipEl);
//				console.log(tableRoot);
				tableRoot.innerHTML = innerHtml;
			}

			var position = this._chart.canvas.getBoundingClientRect();
//            console.log(this);
//			console.log(this._chart);

			// Display, position, and set styles for font
			tooltipEl.style.opacity = 1;
			tooltipEl.style.left = position.left + tooltip.caretX + 'px';
			tooltipEl.style.top = position.top + tooltip.caretY + 'px';
			tooltipEl.style.fontFamily = tooltip._fontFamily;
			tooltipEl.style.fontSize = tooltip.fontSize;
			tooltipEl.style.fontStyle = tooltip._fontStyle;
			tooltipEl.style.padding = tooltip.yPadding + 'px ' + tooltip.xPadding + 'px';
		};
        $scope.lineChart = {};
        $scope.lineChart.labels = [];//["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"];
        $scope.lineChart.series = ['T макс, ' + '\u2103', 'T норм, ' + '\u2103', 'T факт, ' + '\u2103', 'T хвс, ' + '\u2103'];
        $scope.lineChart.data = [[], [], [], []];
        $scope.lineChart.dataTitle = [];
//        var tmpData = [
//            [65, 59, 80, 81, 56, 55, 40],
//            [28, 48, 40, 19, 86, 27, 90]
//        ];
        $scope.onClick = function (points, evt) {
            console.log(points, evt);
        };
        $scope.lineChart.datasetOverride = [];//[{ yAxisID: 'y-axis-1' }, { yAxisID: 'y-axis-2' }];
        $scope.lineChart.options = {
            /*responsive: false,*/            
            elements: {
                line: {
                    fill: false,
                    tension: 0
                }
            },
            legend: {
                display: true,
                position: 'top'
            },
            tooltips: {
                /*enabled: false,
                mode: 'index',
                position: 'nearest',
                custom: customTooltips_v1
                */
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
            $scope.lineChart.data = [[], [], [], []];
            $scope.lineChart.dataTitle = [];
            var dataTitleElem = {};
            tmpData.forEach(function (elm) {
                $scope.lineChart.labels.push(moment(elm.dataDateString, SERVER_DATE_FORMAT).format($scope.data.currentMode.dateFormat));
                $scope.lineChart.data[0].push(T_MAX);
                $scope.lineChart.data[1].push(T_NORM);
                $scope.lineChart.data[2].push(elm.t_in);
                $scope.lineChart.data[3].push(T_COLD);
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
//            widgetConfig.requestToGetIndicators({contObjectId: $scope.widgetOptions.contObjectId, contZpointId: $scope.widgetOptions.contZpointId});
            widgetConfig.exportProperties({contObjectId: $scope.widgetOptions.contObjectId, contZpointId: $scope.widgetOptions.contZpointId, action: "openIndicators"});
//            $scope.$broadcast('requestToGetIndicators', {contObjectId: $scope.widgetOptions.contObjectId, contZpointId: $scope.widgetOptions.contZpointId});
            return true;
        };
    
        $scope.openNotices = function () {
            if (angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true) {
                return true;
            }
            widgetConfig.exportProperties({contObjectId: $scope.widgetOptions.contObjectId, action: "openNotices"});
        };
        
        function initWidget() {
            $scope.modeChange();
            getZpointState();
        }
        
        initWidget();
        
    });