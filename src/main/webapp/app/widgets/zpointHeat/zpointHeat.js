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
        moment.locale('ru', {
            months : "январь_февраль_март_апрель_май_июнь_июль_август_сентябрь_октябрь_ноябрь_декабрь".split("_"),
            monthsShort : "янв._фев._март_апр._май_июнь_июль_авг._сен._окт._ноя._дек.".split("_")
        });
        var DATA_URL = "../api/subscr/widgets/heat",/*//chart/heatTemp";*/
            ZPOINT_STATUS_TEMPLATE = "widgets/zpointHeat/zpoint-state-",
            SERVER_DATE_FORMAT = "DD-MM-YYYY HH:mm";
        
        $scope.widgetOptions = widgetConfig.getOptions();
        //console.log($scope.widgetOptions);
//        var zpstatus = $scope.widgetOptions.zpointStatus;
        $scope.data = {};
        $scope.data.forecastTemp = null;
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
        $scope.data.startModeIndex = 3;//default mode index; 0 - WEEK
        $scope.data.currentMode = $scope.data.MODES[$scope.data.startModeIndex];
    
        $scope.data.imgPath = "widgets/zpointHeat/glyphicons-85-heat.png";
        $scope.data.zpointStatus = "";//"widgets/zpointHeat/zpoint-state-" + zpstatus + ".png";
        $scope.data.zpointStatusTitle = $scope.widgetOptions.zpointStatusTitle;
        $scope.data.contZpointId = $scope.widgetOptions.contZpointId;
    
        $scope.presentDataFlag = false;
    
        var customTooltips = function (tooltip) {
			// Tooltip Element
			var tooltipEl = document.getElementById('chartjs-tooltip');
            
			if (!tooltipEl) {
				tooltipEl = document.createElement('div');
				tooltipEl.id = 'chartjs-tooltip';
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
					var span = '<span class="chartjs-tooltip-key" style="' + style + '"></span>';
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
        $scope.lineChart.series = ['T норм, ' + '\u2103', 'T факт, ' + '\u2103'];
        $scope.lineChart.data = [[], []];
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
            legend: {
                display: true
            },
            tooltips: {
                /*enabled: false,
                mode: 'index',
                position: 'nearest',
                custom: customTooltips
                */
                callbacks: {
                    beforeTitle: function (arr, data) {
//                        console.log(arr);
//                        console.log(data);
                        var result = "";
                        if (angular.isArray(arr) && arr.length > 0 && $scope.lineChart.dataTitle.length > 0 && $scope.lineChart.dataTitle[arr[0].index].dataDateString !== null) {
                            result = moment($scope.lineChart.dataTitle[arr[0].index].dataDateString, SERVER_DATE_FORMAT).format($scope.data.currentMode.tooltipDateFormat);
                        }
                        return result;
                    },
                    title: function (arr, data) {
//                        console.log(arr);
//                        console.log(data);
                        var result = "";
                        if (angular.isArray(arr) && arr.length > 0 && $scope.lineChart.dataTitle.length > 0 && $scope.lineChart.dataTitle[arr[0].index].t_ambiance !== null) {
                            result = "t окр., " + '\u2103' + " = " + $scope.lineChart.dataTitle[arr[0].index].t_ambiance;
                        }
                        return result;
//                        return "t окр. = " + arr[2].yLabel + " C";
                    }
                    
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
            $scope.lineChart.dataTitle = [];
            var dataTitleElem = {};
            rsp.data.forEach(function (elm) {
                $scope.lineChart.labels.push(moment(elm.dataDateString, SERVER_DATE_FORMAT).format($scope.data.currentMode.dateFormat));
                $scope.lineChart.data[0].push(elm.chartT_in);
                $scope.lineChart.data[1].push(elm.t_in);
                dataTitleElem = {
                    dataDateString : elm.dataDateString,
                    t_ambiance: elm.t_ambiance
                };
                $scope.lineChart.dataTitle.push(dataTitleElem);
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
        }
    
//        $scope.modeClick = function (mode) {
//            $scope.data.currentMode = mode;
//            //set class
//            $scope.data.MODES.forEach(function (mod) {
//                mod.modeClass = "";
//            });
//            mode.modeClass = "active";
//            //get data
//            if (angular.isUndefined($scope.data.contZpointId) || $scope.data.contZpointId === null || mode === null || mode.keyname === null) {
//                console.log("zpointHeatWidget: contZpoint or mode is null!");
//                return false;
//            }
//            var url = DATA_URL + "/" + encodeURIComponent($scope.data.contZpointId) + "/chart/data/" + encodeURIComponent(mode.keyname);
//            $http.get(url).then(getDataSuccessCallback, errorCallback);
//        };
    
        $scope.modeChange = function () {
            //set class
            $scope.data.MODES.forEach(function (mod) {
                mod.modeClass = "";
            });
            $scope.data.currentMode.modeClass = "active";
            //get data
            if (angular.isUndefined($scope.data.contZpointId) || $scope.data.contZpointId === null || $scope.data.currentMode === null || $scope.data.currentMode.keyname === null) {
                console.log("zpointHeatWidget: contZpoint or mode is null!");
                return false;
            }
            var url = DATA_URL + "/" + encodeURIComponent($scope.data.contZpointId) + "/chart/data/" + encodeURIComponent($scope.data.currentMode.keyname);
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
//            $scope.modeClick($scope.data.currentMode);
            $scope.modeChange();
            getZpointState();
        }
        
        initWidget();
        
    });