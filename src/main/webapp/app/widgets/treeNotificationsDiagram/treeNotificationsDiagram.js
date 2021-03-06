/*jslint node: true, eqeq: true, nomen: true, es5: true*/
/*global angular, moment, $, document, Chart*/
(function () {
    'use strict';
    
    function chartJsProviderConfig(ChartJsProvider) {
        // Configure all charts
        ChartJsProvider.setOptions({
            chartColors: ['#ef473a', '#FDB45C', '#46BFBD', '#803690', '#337ab7'],
            responsive: true
        });
    }    
    chartJsProviderConfig.$inject = ['ChartJsProvider'];
    
    function treeNotificationsDiagramWidgetCtrl($scope, $http, $rootScope, widgetConfig, treeNotificationsDiagramService, $timeout) {
            
            /*jshint validthis: true*/
            var vm = this;
            vm.CONT_OBJECT_LIST_LENGTH = 2;
            vm.widgetPath = "widgets/zpointCw_v1";
            var DATA_URL = "../api/subscr/widgets/cw",/*//chart/HwTemp";*/
                ZPOINT_STATUS_TEMPLATE = vm.widgetPath + "/zpoint-state-",
                SERVER_DATE_FORMAT = "DD-MM-YYYY HH:mm",
                ZPOINT_EVENTS_URL = null;

            vm.widgetOptions = widgetConfig.getOptions();            
            var contObjectId = vm.widgetOptions.contObjectId;

            var thisdata = {};

            vm.data = {};
            vm.data.contZpointId = vm.widgetOptions.contZpointId;

            vm.presentDataFlag = false;

            vm.barChart = {};
            vm.barChart.labels = [];//["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"];
            vm.barChart.series = [];//['Потребление холодной воды, м' + '\u00B3'];
            vm.barChart.data = [];//[[]]
            vm.barChart.dataTitle = [];

            vm.onClick = function (points, evt) {
                console.log(points, evt);
            };
            vm.barChart.datasetOverride = [];//[{ yAxisID: 'y-axis-1' }, { yAxisID: 'y-axis-2' }];
            vm.barChart.options = {                
                responsive: false,
                cutoutPercentage: 80,
//                legend: {
//                    display: false,
//                    position: 'right',
//                    fullWidth: false,
//                    labels: {
//                        boxWidth: 10,
//                        generateLabels: function (chart) {
//                            var data = chart.data;
//                            if (data.labels.length && data.datasets.length) {
//                                return data.labels.map(function(label, i) {
//                                    var meta = chart.getDatasetMeta(0);
//                                    var ds = data.datasets[0];
//                                    var arc = meta.data[i];
//                                    var custom = arc && arc.custom || {};
//                                    var getValueAtIndexOrDefault = Chart.helpers.getValueAtIndexOrDefault;
//                                    var arcOpts = chart.options.elements.arc;
//                                    var fill = custom.backgroundColor ? custom.backgroundColor : getValueAtIndexOrDefault(ds.backgroundColor, i, arcOpts.backgroundColor);
//                                    var stroke = custom.borderColor ? custom.borderColor : getValueAtIndexOrDefault(ds.borderColor, i, arcOpts.borderColor);
//                                    var bw = custom.borderWidth ? custom.borderWidth : getValueAtIndexOrDefault(ds.borderWidth, i, arcOpts.borderWidth);
//
//                                    return {
//                                        text: label.substring(0, 10),
//                                        fillStyle: fill,
//                                        strokeStyle: stroke,
//                                        lineWidth: bw,
//                                        hidden: isNaN(ds.data[i]) || meta.data[i].hidden,
//
//                                        // Extra data used for toggling the correct item
//                                        index: i
//                                    };
//                                });
//                            }
//                            return [];
//                        }
                        
//                    }
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
                tooltips: {
                    position: 'nearest'
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
                },
                beforeDraw: function(chart) {
                    console.log("BeforeDraw", chart);
                }
            };

            function errorCallback(e) {
                console.log(e);
            }

            $scope.$on('chart-create', function (event, chart) {
//                console.log(event);
//                console.log(chart);
//  <div ng-attr-id = "{{'tree-chart-legend-' + widgetVm.data.ptreeNodeId + widgetVm.widgetOptions.chartMode}}" class = "tree-chart-legend"></div>
                var chartLegend = document.getElementById("tree-chart-legend-" + vm.data.ptreeNodeId + vm.widgetOptions.chartMode);
                if (angular.isDefined(chartLegend) && chartLegend !== null) {
                    chartLegend.innerHTML = chart.generateLegend();
                }
                
                //set tooltip menu
//                var chartLegendTooltip = document.getElementById();
                treeNotificationsDiagramService.setToolTip("Легенда", chartLegend, '#tree-chart-legend-btn-' + vm.data.ptreeNodeId + vm.widgetOptions.chartMode, '#tree-chart-legend-btn-' + vm.data.ptreeNodeId + vm.widgetOptions.chartMode, null, 700, null, null, 'qtip-bootstrap qtip-nmc-tooltip-widget');
            });

            vm.data.contObjectList = [];
            vm.data.contObjectHashTable = {};
//            vm.data.eventsCount = 0;

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
                            
//                            vm.data.eventsCount += resultCategories[contEventCategoriesIndex].count;
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
                chartObj.colors = ['#ef473a', '#FDB45C'];
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
                
//                vm.data.eventsCount = noCriticalCount + criticalCount;
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
                            
//                            vm.data.eventsCount += resultTypes[contEventTypeIndex].count;
                        }
                    }
                }
            }

            vm.data.CHART_MODES = {
                CRITICALS: {
                    caption: "Критические",
                    class: "btn btn-lg glyphicon glyphicon-alert text-primary",
                    prepareFunction: prepareChartDataWithCritical,
                    chartCaption: "Критичность?"
                },
                CATEGORIES: {
                    caption: "Категории",
                    class: "btn btn-lg glyphicon glyphicon-tags text-primary",
                    prepareFunction: prepareChartDataWithCategories,
                    chartCaption: "Категории"
                },
                TYPES: {
                    caption: "Типы",
                    class: "btn btn-lg glyphicon glyphicon-tag text-primary",
                    prepareFunction: prepareChartDataWithTypes,
                    chartCaption: "Типы"
                }
            };

            vm.data.currentChartMode = vm.data.CHART_MODES[vm.widgetOptions.chartMode];//$scope.data.CHART_MODES.TYPES;
            vm.data.currentChartMode.class += " active";

            function executeChartModeFunction(chartMode) {
                if (chartMode.hasOwnProperty("prepareFunction") && angular.isFunction(chartMode.prepareFunction)) {
                    chartMode.prepareFunction(vm.barChart, thisdata.contEventPTreeNodeData);
                } else {
                    console.warn("Chart can't be create! Transform function is not defined!", chartMode);
                }
            }

            vm.chartModeChange = function (chMode) {
                vm.data.currentChartMode.class = vm.data.currentChartMode.class.substring(0, vm.data.currentChartMode.class.indexOf(" active"));
    //console.log($scope.data.currentChartMode.class);            
                vm.data.currentChartMode = chMode;
                vm.data.currentChartMode.class += " active";
    //console.log($scope.data.currentChartMode.class);                        
                executeChartModeFunction(vm.data.currentChartMode);
            };

            function successLoadPTreeNodeStatsCallback(resp) {
                console.log(resp);
                var tmpData = resp.data;

                thisdata.contEventPTreeNodeData = tmpData;

                vm.presentDataFlag = true;
                vm.barChart.labels = ["Все ОК"];
                vm.barChart.data = [1];
                vm.barChart.colors = ["#46BFBD"]; //green color
//                tmpData = [];
                if (angular.isArray(tmpData) && tmpData.length > 0) {
    //            prepareChartDataWithCritical($scope.barChart, tmpData);
                    vm.barChart.colors = [];
                    executeChartModeFunction(vm.data.currentChartMode);
                }

                console.log(vm.barChart);
            }

            function checkRequiredData() {
                return treeNotificationsDiagramService.checkEmptyObject(thisdata.contEventTypes);
            }

            function getPTreeNodeState() {
                if (checkRequiredData()) {
                    return false;
                }

                var ptreeNode = vm.widgetOptions.ptreeNode,
                    ptreeNodeId = isPTreeNodeElement(ptreeNode) ? ptreeNode._id : ptreeNode.nodeObject.id;
                vm.data.ptreeNodeId = ptreeNodeId;
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
        
            function loadContObject(coe) {
                coe.isLoaded = false;
                treeNotificationsDiagramService.loadContObject(coe.contObjectId)
                    .then(function (resp) {                        
                    coe.fullName = resp.data.fullName;
                    coe.fullAddress = resp.data.fullAddress;
                    coe.isLoaded = true;
                }, function (err) {
                    coe.isLoaded = true;
                });
            }
        
            vm.getObjectKeys = treeNotificationsDiagramService.getObjectKeys;

            vm.chartClick = function (ev) {
                console.log(ev);
                vm.data.contObjectList = angular.copy(thisdata.contEventPTreeNodeData);
                // for test
                for (var i = 0; i < 3; i++) {
                    vm.data.contObjectList = vm.data.contObjectList.concat(vm.data.contObjectList);
                }                
                vm.data.contObjectList = vm.data.contObjectList.concat(angular.copy(thisdata.contEventPTreeNodeData));
                vm.data.contObjectList = vm.data.contObjectList.concat(angular.copy(thisdata.contEventPTreeNodeData));
                vm.data.contObjectList = vm.data.contObjectList.concat(angular.copy(thisdata.contEventPTreeNodeData));
                
                console.log(vm.data.contObjectList.length);
                //end 'for test'
                vm.data.contObjectHashTable = treeNotificationsDiagramService.convertArrayToHash(vm.data.contObjectList);

                for (var hkey in vm.data.contObjectHashTable) {
                    if (vm.data.contObjectHashTable.hasOwnProperty(hkey)) {
                        loadContObject(vm.data.contObjectHashTable[hkey]);
                    }
                }
                vm.data.contObjectList.forEach(function (coe) {
                    loadContObject(coe);
                });
                openObjectListModalWindow();
            };

            $scope.$on(treeNotificationsDiagramService.EVENTS.categoriesLoaded, function () {
                getContEventCategories();
//                getData();
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

    angular.module('treeNotificationsDiagramWidget', ['angularWidget', 'chart.js', 'treeNotificationsDiagramWidgetSvc', 'objectTreeModule'])
        .config(chartJsProviderConfig)
        .controller('treeNotificationsDiagramWidgetCtrl', treeNotificationsDiagramWidgetCtrl);
}());