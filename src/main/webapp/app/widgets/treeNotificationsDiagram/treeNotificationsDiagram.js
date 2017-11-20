/*jslint node: true, eqeq: true, nomen: true, es5: true*/
/*global angular, moment, $, document*/
(function () {
    'use strict';
    
    function chartJsProviderConfig(ChartJsProvider) {
        // Configure all charts
        ChartJsProvider.setOptions({
            chartColors: ['#ef473a', '#FDB45C', '#46BFBD', '#803690', '#337ab7'],
            responsive: true
        });
        // Configure all line charts
//        ChartJsProvider.setOptions('line', {
//            showLines: false
//        });
    }    
    chartJsProviderConfig.$inject = ['ChartJsProvider'];
    
    function treeNotificationsDiagramWidgetCtrl($scope, $http, $rootScope, widgetConfig, treeNotificationsDiagramService, $timeout) {
        //data generator
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
                    node.v_in = Math.random() * 10 + 1;
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
            $scope.widgetPath = "widgets/zpointCw_v1";
            var DATA_URL = "../api/subscr/widgets/cw",/*//chart/HwTemp";*/
                ZPOINT_STATUS_TEMPLATE = $scope.widgetPath + "/zpoint-state-",
                SERVER_DATE_FORMAT = "DD-MM-YYYY HH:mm",
                ZPOINT_EVENTS_URL = null;

            $scope.widgetOptions = widgetConfig.getOptions();
            //console.log($scope.widgetOptions);
    //        var zpstatus = $scope.widgetOptions.zpointStatus;
            var contObjectId = $scope.widgetOptions.contObjectId;
            if (angular.isDefined(contObjectId) && contObjectId !== null && contObjectId !== 'null') {
                ZPOINT_EVENTS_URL = "../api/subscr/contEvent/notifications/contObject/" + contObjectId + "/monitorEventsV2/byContZPoint/" + $scope.widgetOptions.contZpointId; /*/notifications/contObject/{contObjectId}/monitorEventsV2/byContZPoint/{contZPointId}*/
            }

            var thisdata = {};

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

            $scope.data.imgPath = $scope.widgetPath + "/snowflake.png";
            $scope.data.zpointStatus = ZPOINT_STATUS_TEMPLATE + "green.png";//"widgets/zpointHw/zpoint-state-" + zpstatus + ".png";
            $scope.data.zpointStatusTitle = $scope.widgetOptions.zpointStatusTitle;
            $scope.data.contZpointId = $scope.widgetOptions.contZpointId;

            $scope.presentDataFlag = false;

            $scope.barChart = {};
            $scope.barChart.labels = [];//["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"];
            $scope.barChart.series = [];//['Потребление холодной воды, м' + '\u00B3'];
            $scope.barChart.data = [];//[[]]
            $scope.barChart.dataTitle = [];

            $scope.onClick = function (points, evt) {
                console.log(points, evt);
            };
            $scope.barChart.datasetOverride = [];//[{ yAxisID: 'y-axis-1' }, { yAxisID: 'y-axis-2' }];
            $scope.barChart.options = {                
                responsive: false,
//                legend: {
//                    display: true
//                },
                legendCallback: function (chart) {
//                    console.log(chart);
                    var text = [], i;
                    text.push('<ul class="' + chart.id + '-legend">');
                    var dataArr = chart.legend.legendItems;
                    for (i = 0; i < dataArr.length; i += 1) {
                        text.push('<li><span style="background-color:' + dataArr[i].fillStyle + '"></span>');                        
                        if (dataArr[i]) {
                            text.push(dataArr[i].text);
                            
                        }
                        text.push('</li>');
                    }
                    text.push('</ul>');

                    return text.join('');
                },
//                tooltips: {
//                    callbacks: {
//                        beforeTitle: function (arr, data) {
//                            var result = "";
//                            if (angular.isArray(arr) && arr.length > 0 && $scope.barChart.dataTitle.length > 0 && $scope.barChart.dataTitle[arr[0].index].dataDateString !== null) {
//                                result = moment($scope.barChart.dataTitle[arr[0].index].dataDateString, SERVER_DATE_FORMAT).format($scope.data.currentMode.tooltipDateFormat);
//                            }
//                            return result;
//                        },
//                        title: function () {
//                            return "";
//                        }
//                    }
//
//                },
                beforeDraw: function(chart) {
                    console.log("BeforeDraw", chart);
                }
            };
            function prepareEventMessage(inputData) {
                            //temp array
                var tmpMessage = "";
        //                var tmpMessageEx = "";
                //make the new array of the types wich formatted to display
                inputData.forEach(function (element) {
        //console.log(element);
                    var tmpEvent = "";
                    var contEventTime = new Date(element.contEventTimeDT);
                    var pstyle = "";
                    if (element.contEventLevelColorKeyname === "RED") {
                        pstyle = "color: red;";
                    }
                    tmpEvent = "<p style='" + pstyle + "'>" + contEventTime.toLocaleString() + ", " + element.contEventType.name + "</p>";
                    tmpMessage += tmpEvent;
                });
                return tmpMessage;
            }

            function getDataSuccessCallback(rsp) {
                console.log(rsp.data);
                var tmpData = rsp.data;
                if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
                    tmpData = generateTestData(timeDetailTypes[$scope.data.currentMode.keyname.toLowerCase()]);
                }
                if (!angular.isArray(tmpData) || tmpData.length === 0) {
                    $scope.presentDataFlag = false;
    //                console.log("zpointCwWidget: response data is empty!");
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

    //        function setEventsForZpoint(url, zpointId) {
    //            var imgObj = "#zpStatusImg" + zpointId;
    //            $(imgObj).qtip({
    //                content: {
    //                    text: function (event, api) {
    //                        $http.get(url)
    //                            .then(function (resp) {
    //                                var message = "";
    //                                if (angular.isDefined(resp) && angular.isDefined(resp.data) && angular.isArray(resp.data)) {
    //                                    message = prepareEventMessage(resp.data);
    //                                } else {
    //                                    message = "Непонятный ответ от сервера. Смотри консоль браузера.";
    //                                    console.log(resp);
    //                                }
    //                                api.set('content.text', message);
    //                            },
    //                                 function (error) {
    //                                    var message = "";
    //                                    switch (error.status) {
    //                                    case 404:
    //                                        message = "Для данной точки учета событий не найдено.";
    //                                        break;
    //                                    case 500:
    //                                        message = "Ошибка сервера. Закройте виджет и повторите попытку. Если ситуация не исправится, то обратитесь к разработчику.";
    //                                        break;
    //                                    default:
    //                                        message = "При загрузке событий произошла непредвиденная ситуация. Закройте виджет и повторите попытку. Если ситуация не исправится, то обратитесь к разработчику.";
    //                                        break;
    //                                    }
    //                                    api.set('content.text', error.status + ': ' + message);
    //                                });
    //                        return "Загружаются сообытия...";
    //                    }
    //                },
    //
    //                style: {
    //                    classes: 'qtip-bootstrap qtip-nmc-monitor-tooltip'
    //                }
    //            });
    //        }

            function getStatusSuccessCallback(resp) {
                if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
                    return true;
                }
                if (angular.isUndefined(resp) || resp === null) {
    //                console.log("zpointCwWidget: status response is empty.");
                    return false;
                }
                if (angular.isUndefined(resp.data) || resp.data === null) {
    //                console.log("zpointCwWidget: status response data is empty.");
                    return false;
                }
                if (angular.isDefined(resp.data.color) && resp.data.color !== null && angular.isString(resp.data.color)) {
                    $scope.data.zpointStatus = ZPOINT_STATUS_TEMPLATE + resp.data.color.toLowerCase() + ".png";
    //                if (resp.data.color.toLowerCase() !== 'green' && ZPOINT_EVENTS_URL !== null) {
                        //load zpoint events
    //                    setEventsForZpoint(ZPOINT_EVENTS_URL, $scope.data.contZpointId);
    //                }
                }/* else {
                    console.log("zpointCwWidget: zpoint status color is empty or not string.");
                }*/
            }

//            $scope.modeChange = function () {
//                var mode = $scope.data.currentMode;
//                //set class
//                $scope.data.MODES.forEach(function (mod) {
//                    mod.modeClass = "";
//                });
//                mode.modeClass = "active";
//                //get data
//                if (angular.isUndefined($scope.data.contZpointId) || $scope.data.contZpointId === null || mode === null || mode.keyname === null) {
//                    console.log("treeNotificationsDiagramWidget: contZpoint or mode is null!");
//                    console.log("data:");
//                    console.log($scope.data);
//                    console.log("mode:");
//                    console.log(mode);
//                    return false;
//                }
//                var url = DATA_URL + "/" + encodeURIComponent($scope.data.contZpointId) + "/chart/data/" + encodeURIComponent(mode.keyname);
//                $http.get(url).then(getDataSuccessCallback, errorCallback);
//            };

    //        function getZpointState() {
    //            if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
    //                return true;
    //            }
    //            var url = DATA_URL + "/" + encodeURIComponent($scope.data.contZpointId) + "/status";
    //            $http.get(url).then(getStatusSuccessCallback, errorCallback);
    //        }
                    // Показания точек учета
    //        $scope.getIndicators = function () {
    //            if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
    //                return true;
    //            }
    //            widgetConfig.exportProperties({contObjectId: $scope.widgetOptions.contObjectId, contZpointId: $scope.widgetOptions.contZpointId, action: "openIndicators"});
    //            return true;
    //        };
    //
    //        $scope.openNotices = function () {
    //            if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
    //                return true;
    //            }
    //            widgetConfig.exportProperties({contObjectId: $scope.widgetOptions.contObjectId, action: "openNotices"});
    //        };

            $scope.$on('chart-create', function (event, chart) {
//                console.log(event);
//                console.log(chart);
                var chartLegend = document.getElementById("cw-chart-legend-" + $scope.data.ptreeNodeId + $scope.widgetOptions.chartMode);
                if (angular.isDefined(chartLegend) && chartLegend !== null) {
                    chartLegend.innerHTML = chart.generateLegend();
                }
            });

            $scope.data.contObjectList = [];
            $scope.data.eventsCount = 0;

            thisdata.contEventCriticals = {
                YELLOW: {
                    caption: "Некритические",
                    color: "yellow"
                },
                RED: {
                    caption: "Критические",
                    color: "red"
                }
            };
            thisdata.contEventCategories = {};
            thisdata.contEventTypes = {};
            thisdata.contEventPTreeNodeData = [];

            function testDelegate(inpData) {
                console.log("Hello, " + inpData + "! I'm testDelegate");
            }

            function prepareContEventData(inputContEventData, field) {
                console.log(inputContEventData);
                if (!angular.isArray(inputContEventData)) {
                    return {};
                }
                var result = {};
                inputContEventData.forEach(function (icet) {
                    if (icet.hasOwnProperty(field)) {
                        result[icet[field]] = icet;
                    }
                });
                return result;
            }

            function getContEventCategories() {
                thisdata.contEventCategories = prepareContEventData(treeNotificationsDiagramService.getContEventCategories(), 'keyname');
            }

            function getContEventTypes() {
                thisdata.contEventTypes = prepareContEventData(treeNotificationsDiagramService.getContEventTypes(), 'id');
            }

            function isPTreeNodeElement(ptreeNode) {
                return ptreeNode.nodeType === "ELEMENT";
            }

            function isCriticalEvent(event) {
                var typeId = event.contEventTypeId;
                var eventType = null;
                if (thisdata.contEventTypes.hasOwnProperty(typeId)) {
                    eventType = thisdata.contEventTypes[typeId];
                    if (eventType.hasOwnProperty("levelColor") && eventType.levelColor === "RED") {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }

    //        function isCriticalEvent(event) {
    //            var typeId = event.contEventTypeId;
    //            var eventType = null;
    //            if (thisdata.contEventTypes.hasOwnProperty(typeId)) {
    //                eventType = thisdata.contEventTypes[typeId];
    //                if (eventType.hasOwnProperty("levelColor") && eventType.levelColor === "RED") {
    //                    return true;
    //                } else {
    //                    return false;
    //                }
    //            }
    //            return false;
    //        }

            function prepareChartDataWithCategories(chartObj, inputData) {
                if (angular.isUndefined(chartObj) || chartObj === null || !angular.isArray(inputData)) {
                    return false;
                }
                chartObj.labels = [];
                chartObj.data = [];
                var resultCategories = angular.copy(thisdata.contEventCategories),
                    contEventCategoriesIndex;

                for (contEventCategoriesIndex in resultCategories) {
                    if (resultCategories.hasOwnProperty(contEventCategoriesIndex)) {
                        resultCategories[contEventCategoriesIndex].count = 0;
                    }
                }
     console.log(resultCategories);
                inputData.forEach(function (event) {
                    try {
                        resultCategories[thisdata.contEventTypes[event.contEventTypeId].contEventCategoryKeyname].count += event.count;
                    } catch (err) {
                        console.error(err);
                        console.log(event);
                        console.log(thisdata.contEventTypes[event.contEventTypeId]);
                    }
                });

                for (contEventCategoriesIndex in resultCategories) {
                    if (resultCategories.hasOwnProperty(contEventCategoriesIndex)) {
                        if (resultCategories[contEventCategoriesIndex].count > 0) {
                            chartObj.labels.push(resultCategories[contEventCategoriesIndex].caption);
                            chartObj.data.push(resultCategories[contEventCategoriesIndex].count);
                            
                            $scope.data.eventsCount += resultCategories[contEventCategoriesIndex].count;
                        }
                    }
                }
            }

            function prepareChartDataWithCritical(chartObj, inputData) {
                if (angular.isUndefined(chartObj) || chartObj === null || !angular.isArray(inputData)) {
                    return false;
                }
                chartObj.labels = [];
                chartObj.data = [];
                var criticalCount = 0,
                    noCriticalCount = 0;
                inputData.forEach(function (event) {
                    if (isCriticalEvent(event)) {
                        criticalCount += event.count;
                    } else {
                        noCriticalCount += event.count;
                    }
                });

                chartObj.labels.push(thisdata.contEventCriticals.RED.caption);
                chartObj.data.push(criticalCount);

                chartObj.labels.push(thisdata.contEventCriticals.YELLOW.caption);
                chartObj.data.push(noCriticalCount);
                
                $scope.data.eventsCount = noCriticalCount + criticalCount;
            }

            function prepareChartDataWithTypes(chartObj, inputData) {
                if (angular.isUndefined(chartObj) || chartObj === null || !angular.isArray(inputData)) {
                    return false;
                }
                chartObj.labels = [];
                chartObj.data = [];
                var resultTypes = angular.copy(thisdata.contEventTypes),
                    contEventTypeIndex;

                for (contEventTypeIndex in resultTypes) {
                    if (resultTypes.hasOwnProperty(contEventTypeIndex)) {
                        resultTypes[contEventTypeIndex].count = 0;
                    }
                }

                inputData.forEach(function (event) {
                    try {
                        resultTypes[event.contEventTypeId].count += event.count;
                    } catch (err) {
                        console.error(err);
                        console.log(event);
                    }
                });

                for (contEventTypeIndex in resultTypes) {
                    if (resultTypes.hasOwnProperty(contEventTypeIndex)) {
                        if (resultTypes[contEventTypeIndex].count > 0) {
                            chartObj.labels.push(resultTypes[contEventTypeIndex].name);
                            chartObj.data.push(resultTypes[contEventTypeIndex].count);
                            
                            $scope.data.eventsCount += resultTypes[contEventTypeIndex].count;
                        }
                    }
                }
            }

            $scope.data.CHART_MODES = {
                CRITICALS: {
                    caption: "Критические",
                    class: "btn btn-lg glyphicon glyphicon-alert text-primary",
                    prepareFunction: prepareChartDataWithCritical,
                    chartCaption: "критичности"
                },
                CATEGORIES: {
                    caption: "Категории",
                    class: "btn btn-lg glyphicon glyphicon-tags text-primary",
                    prepareFunction: prepareChartDataWithCategories,
                    chartCaption: "категориям"
                },
                TYPES: {
                    caption: "Типы",
                    class: "btn btn-lg glyphicon glyphicon-tag text-primary",
                    prepareFunction: prepareChartDataWithTypes,
                    chartCaption: "типам"
                }
            };

            $scope.data.currentChartMode = $scope.data.CHART_MODES[$scope.widgetOptions.chartMode];//$scope.data.CHART_MODES.TYPES;
            $scope.data.currentChartMode.class += " active";

            function executeChartModeFunction(chartMode) {
                if (chartMode.hasOwnProperty("prepareFunction") && angular.isFunction(chartMode.prepareFunction)) {
                    chartMode.prepareFunction($scope.barChart, thisdata.contEventPTreeNodeData);
                } else {
                    console.warn("Chart can't be create! Transform function is not defined!", chartMode);
                }
            }

            $scope.chartModeChange = function (chMode) {
                $scope.data.currentChartMode.class = $scope.data.currentChartMode.class.substring(0, $scope.data.currentChartMode.class.indexOf(" active"));
    //console.log($scope.data.currentChartMode.class);            
                $scope.data.currentChartMode = chMode;
                $scope.data.currentChartMode.class += " active";
    //console.log($scope.data.currentChartMode.class);                        
                executeChartModeFunction($scope.data.currentChartMode);
            };

            function successLoadPTreeNodeStatsCallback(resp) {
                console.log(resp);
                var tmpData = resp.data;
                if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
                    tmpData = generateTestData(timeDetailTypes[$scope.data.currentMode.keyname.toLowerCase()]);
                }
                if (!angular.isArray(tmpData) || tmpData.length === 0) {
                    $scope.presentDataFlag = false;
    //                console.log("zpointCwWidget: response data is empty!");
                    return false;
                }

                thisdata.contEventPTreeNodeData = tmpData;

                $scope.presentDataFlag = true;
                $scope.barChart.labels = [];
                $scope.barChart.data = [];

    //            prepareChartDataWithCritical($scope.barChart, tmpData);
                executeChartModeFunction($scope.data.currentChartMode);
    //            if ($scope.data.currentChartMode.hasOwnProperty("prepareFunction") && angular.isFunction($scope.data.currentChartMode.prepareFunction)) {
    //                $scope.data.currentChartMode.prepareFunction($scope.barChart, tmpData);
    //            } else {
    //                console.warn("Chart can't be create! Transform function is not defined!", $scope.data.currentChartMode);
    //            }

                console.log($scope.barChart);
            }

            function checkRequiredData() {
                return treeNotificationsDiagramService.checkEmptyObject(thisdata.contEventTypes);
            }

            function getPTreeNodeState() {
                if (checkRequiredData()) {
                    return false;
                }

    //            if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
    //                return true;
    //            }

                // var url = DATA_URL + "/" + encodeURIComponent($scope.data.contZpointId) + "/status";
                // $http.get(url).then(getStatusSuccessCallback, errorCallback);
                var ptreeNode = $scope.widgetOptions.ptreeNode,
                    ptreeNodeId = isPTreeNodeElement(ptreeNode) ? ptreeNode._id : ptreeNode.nodeObject.id;
                $scope.data.ptreeNodeId = ptreeNodeId;
                console.log(ptreeNode);
                treeNotificationsDiagramService.loadPTreeNodeStats(ptreeNodeId).then(successLoadPTreeNodeStatsCallback, errorCallback);
            }

            function getData() {
                getContEventTypes();
                getPTreeNodeState();
            }

            function openObjectListModalWindow() {
                $timeout(function () {
                    $('#objectListModal').modal();
                });
            }

            $scope.chartClick = function (ev) {
                console.log(ev);
                $scope.data.contObjectList = angular.copy(thisdata.contEventPTreeNodeData);
                $scope.data.contObjectList = $scope.data.contObjectList.concat($scope.data.contObjectList);
                $scope.data.contObjectList = $scope.data.contObjectList.concat($scope.data.contObjectList);
    // console.log($scope.data.contObjectList);
                openObjectListModalWindow();
            };

            $scope.$on(treeNotificationsDiagramService.EVENTS.categoriesLoaded, function () {
                getContEventCategories();
            });

            $scope.$on(treeNotificationsDiagramService.EVENTS.typesLoaded, function () {
                getData();
            });

            function initWidget() {

                getContEventCategories();
                getData();
            }

            initWidget();
    }
    
    treeNotificationsDiagramWidgetCtrl.$inject = ['$scope', '$http', '$rootScope', 'widgetConfig', 'treeNotificationsDiagramService', '$timeout'];

    angular.module('treeNotificationsDiagramWidget', ['angularWidget', 'chart.js', 'treeNotificationsDiagramWidgetSvc'])
        .config(chartJsProviderConfig)
    //    .service('treeNotificationsDiagramService', [function () {
    //        console.log("Simple Hello.");
    //    }])
        .controller('treeNotificationsDiagramWidgetCtrl', treeNotificationsDiagramWidgetCtrl);
}());