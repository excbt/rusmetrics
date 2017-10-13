/*jslint node: true, eqeq: true */
/*global angular, $, moment*/

/*
    Директива отображает ПКЭ и Энергоприфили
*/
'use strict';
var app = angular.module('portalNMC');
app.directive('nmcViewElectricity', function () {
    return {
        restrict: 'EA',
        scope: {
            dataUrl: "=url",
            columns: "=columns",
            type: "=type",
            chartFlag: "=chart"
        },
        templateUrl: 'scripts/directives/templates/nmc-view-electricity.html',
        controller: ['$scope', '$element', '$attrs', '$http', 'notificationFactory', 'mainSvc', '$timeout', function ($scope, $element, $attrs, $http, notificationFactory, mainSvc, $timeout) {
            
            $scope.dirSettings = {};
            $scope.dirSettings.userFormat = "DD.MM.YYYY"; //date format
            $scope.dirSettings.requestFormat = "YYYY-MM-DD"; //date format
            $scope.dirSettings.dataDate = moment().endOf('day').format($scope.dirSettings.userFormat);
            
            $scope.dateOptsParamsetRu = {
                locale : {
                    daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
                    firstDay : 1,
                    monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                            'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                            'Октябрь', 'Ноябрь', 'Декабрь' ]
                },
                singleDatePicker: true,
                format: $scope.dirSettings.dateFormat
            };
            
            var successCallback = function (response) {
//console.log(response.data);                
                var tmp = angular.copy(response.data);
                tmp.forEach(function (el) {
                    var result  = {},
                        i;
                    for (i in $scope.columns) {
                        if ((el[$scope.columns[i].fieldName] != null) && ($scope.columns[i].type !== "string")) {
                            el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed(3);
                        }
                    }
                });
                $scope.data = tmp;
//                if (angular.isArray($scope.data) && ($scope.data.length > 0)){
                $scope.runChart();
//                };
            };
            
            var errorCallback = function (e) {
                var errorObj = mainSvc.errorCallbackHandler(e);
                notificationFactory.errorInfo(errorObj.caption, errorObj.description);
            };
            
            var checkUrl = function (url) {
                var undefinedIndex = url.indexOf('undefined');
                if (undefinedIndex >= 0) {
                    var error = {
                        statusText: "Некорректный запрос",
                        data: {
                            request: url,
                            description: "Запрос содержит неопределенные занчения.\n Обратитесь к администратору системы"
                        }
                    };
                    errorCallback(error);
                    return false;
                }
                return true;
            };
            
            var getData = function (url) {
                if (!checkUrl(url)) { return "request error"; }
                $http.get(url).then(successCallback, errorCallback);
            };
            
            $scope.refreshData = function () {
//console.log(moment($scope.dirSettings.dataDate, $scope.dirSettings.userFormat).format($scope.dirSettings.requestFormat));                
                var requestDate = moment($scope.dirSettings.dataDate, $scope.dirSettings.userFormat).format($scope.dirSettings.requestFormat);
                if (requestDate.localeCompare('Invalid date') == 0 || requestDate < '2000-01-01') {
                    return "requestDate is Invalid date.";
                }
                var url = $scope.dataUrl + "/?beginDate=" + requestDate + "&endDate=" + requestDate;
                getData(url);
            };
            
            $scope.refreshData();
            
            //set setting for input date and chart controls toggle
            $timeout(function () {
                $('#input' + $scope.type + 'Date').datepicker({
                    dateFormat: "dd.mm.yy",
                    firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
                    dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
                    monthNames: $scope.dateOptsParamsetRu.locale.monthNames,
                    beforeShow: function () {
                        setTimeout(function () {
                            $('.ui-datepicker-calendar').css("display", "table");
                        }, 1);
                    },
                    onChangeMonthYear: function () {
                        setTimeout(function () {
                            $('.ui-datepicker-calendar').css("display", "table");
                        }, 1);
                    }
                });
                
                $('#' + $scope.type + 'Chart-view').bootstrapToggle({
                    on: "да",
                    off: "нет"
                });
                $('#' + $scope.type + 'Chart-view').change(function () {
                    $scope.chartFlag = Boolean($(this).prop('checked'));
                    $scope.$apply();
                });
            }, 10);
            
            $scope.isSystemuser = function () {
                return mainSvc.isSystemuser();
            };
            
                    //chart
            $scope.runChart = function () {
                var graph = [];//["p_Ap", "p_An", "q_Rp", "q_Rn"];
                $scope.columns.forEach(function (column) {
                    column.graph && graph.push(column);
                });
                var data = [];//, series = Math.floor(Math.random() * 6) + 3;       
                var count = graph.length,//4;
                    i;
                for (i = 0; i < count; i += 1) {
                    var rowCount = $scope.data.length;
                    var seria = [],
                        ser;
                    for (ser = 0; ser < rowCount; ser += 1) {
                        seria[ser] = [$scope.data[ser].dataDate + 3 * 3600 * 1000, Number($scope.data[ser][graph[i].fieldName])];
                    }
                    var label = graph[i].header;
                    data.push({label: label, data: seria});
                }
//console.log(data);
                // выводим график
                var width = $(".nmc-el-view-main-div").width();
//console.log(width);                
//                $("#"+$scope.type+"Chart-area").width(1010);
//                $("#"+$scope.type+"Chart-area").height(150);

                $.plot('#' + $scope.type + 'Chart-area', data, {
                    series: {
                        lines: {show: true},
                        points: {show: true}
                    },
                    legend: {
                        show: true,
                        labelFormatter: labelFormatter,
                        position: "se",
                        margin: [-100, 0]
//                        container: $("#"+$scope.type+"Legend-area")
                    },
                    xaxis: {
                        mode: "time"
                    },
                    yaxis: {
                    }
                });

            };
            function labelFormatter(label, series) {
                return "<div style='font-size:8pt; text-align:center; padding:2px; color:black;'>" + label + "</div>";
            }
        }]
    };
});