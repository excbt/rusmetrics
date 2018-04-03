/*global angular, console, Chart, $*/
/***
    created by Artamonov A.A. , Dec. 2017
*/
(function () {
    'use strict';
    
    function chartJsProviderConfig(ChartJsProvider) {
        // Configure all charts
        ChartJsProvider.setOptions({
            chartColors: ['#ef473a', '#FDB45C', '#6e7b90', '#46BFBD', '#803690', '#337ab7'],
            responsive: true
        });
    }    
    chartJsProviderConfig.$inject = ['ChartJsProvider'];
    
    angular.module('objectTreeModule')
        .config(chartJsProviderConfig)
        .component('contObjectMonitorComponent', {
            bindings: {
                node: '<'
            },
            templateUrl: "components/object-tree-module/cont-object-monitor-component/cont-object-monitor-component.html",
            controller: contObjectMonitorComponentController
        });
    
    contObjectMonitorComponentController.$inject = ['$scope', '$element', '$attrs', 'contObjectMonitorComponentService', '$stateParams', 'contObjectService', '$filter', '$timeout'];
    
    function contObjectMonitorComponentController($scope, $element, $attrs, contObjectMonitorComponentService, $stateParams, contObjectService, $filter, $timeout) {
        /*jshint validthis: true*/
        var ctrl = this;
        ctrl.nodeId = null;
        ctrl.contObjectStateShowFlag = false;
        ctrl.svc = contObjectMonitorComponentService;
        ctrl.$onInit = initCmpnt;
        ctrl.doughnuts = {};
        
        var labels = ["Критические", "Некритические", "Остальные"];
                
        
//        function getRandomColor () {
//            var color = [getRandomInt(0, 255), getRandomInt(0, 255), getRandomInt(0, 255)];
//            return getColor(color);
//        }
//        
//        function rgba (color, alpha) {
//          // rgba not supported by IE8
////            useExcanvas ? 'rgb(' + color.join(',') + ')' : 
//          return 'rgba(' + color.concat(alpha).join(',') + ')';
//        }
//        
//        function getColor (color) {
//            var alpha = color[3] || 1;
//            color = color.slice(0, 3);
//            return {
//                backgroundColor: rgba(color, 0.2),
//                pointBackgroundColor: rgba(color, alpha),
//                pointHoverBackgroundColor: rgba(color, 0.8),
//                borderColor: rgba(color, alpha),
//                pointBorderColor: '#fff',
//                pointHoverBorderColor: rgba(color, alpha)
//            };
//        }
        
        function generateLabels(chart) {
//                            console.log("generateLabels", chart);
            var data = chart.data;
//                            console.log(data);
            if (data.labels.length && data.datasets.length) {
                var result = data.labels.map(function(label, i) {
                    var meta = chart.getDatasetMeta(0);                                    
                    var ds = data.datasets[0];
                    var arc = meta.data[i];
                    var custom = arc && arc.custom || {};
                    var getValueAtIndexOrDefault = Chart.helpers.getValueAtIndexOrDefault;
                    var arcOpts = chart.options.elements.arc;
                    var fill = custom.backgroundColor ? custom.backgroundColor : getValueAtIndexOrDefault(ds.backgroundColor, i, arcOpts.backgroundColor);
                    var stroke = custom.borderColor ? custom.borderColor : getValueAtIndexOrDefault(ds.borderColor, i, arcOpts.borderColor);
                    var bw = custom.borderWidth ? custom.borderWidth : getValueAtIndexOrDefault(ds.borderWidth, i, arcOpts.borderWidth);

                    return {
                        text: data.datasets[0].data[i]/*label*/,
                        fillStyle: fill,
                        strokeStyle: stroke,
                        lineWidth: bw,
                        hidden: isNaN(ds.data[i]) || meta.data[i].hidden,

                        // Extra data used for toggling the correct item
                        index: i
                    };
                });
//                                console.log(result);
                return result;
            }
            return [];
        }

//        function legendCallback(chart) {
//            var text = [], i;
//            text.push('<ul class="' + chart.id + '-legend">');
//
//            var data = chart.data;
//            var datasets = chart.datasets;
//            var labels = data.labels;
//            if (datasets.length){
//                for (i = 0; i < datasets[0].length; i += 1) {
//                    text.push('<li><span style="height: 12px; width: 12px; border-radius: 5px; background-color:' + datasets[0].backgroundColor[i] + '"></span>');
//                    if (data[i]) {
//                        text.push(data[i]);
//                    }
//                    text.push('</li>');
//                }
//            }
//                text.push('</ul>');
//console.log(text.join(''));
//                return text.join('');
//        }
        
        function segmentClick(points, arg2, arg3, arg4) {
console.log(points);
            if (!angular.isArray(points) || points.length === 0) {
                return false;
            }
        if (angular.isArray(points) && points.length > 0) {            
console.log(points[0]._index);
console.log(labels[points[0]._index]);
        }
//console.log(arg2);
//console.log(arg3);
//console.log(arg3._index);
//console.log(arg4);
            ctrl.contObjectStateShowFlag = false;
            $timeout(function () {
                ctrl.contObjectStateShowFlag = true;
            }, 1000);
            console.log($('.nmc-cont-object-control-main-div'));
            var elm = $('.nmc-cont-object-control-main-div').get(0);
            console.log(elm);
            if (elm !== null) {
                elm.style.height = "43vh";//css("height", "45vh");
            }
        }
        
        var doughuntOpts = {
            responsive: false,
            cutoutPercentage: 80,
            legend: {
                display: true,
                position: "right",
                onClick: function (ev, label, arg3) {                    
                    console.log("Label click!");
                    console.log(ev);
                    console.log(label);
                    console.log(label.index);
                    console.log(arg3);
                },
                labels: {
                    fontSize: 10,
                    generateLabels: generateLabels
                },                    
            }/*,
            legendCallback: legendCallback
            */
            /*title: {
                display: true,
                postion: "top",
                text: "<img src = \"components\object-tree-module\cont-object-monitor-component\heat.png\"/> (123)"
            }*/
        };
        
        var doughuntColors = ["#ef473a", "#FDB45C", "#6e7b90"];
        ctrl.doughuntColors = doughuntColors;
        
        ctrl.doughnutTemplate = {};
        ctrl.doughnutTemplate.data = [];
        ctrl.doughnutTemplate.labels = labels;            
        ctrl.doughnutTemplate.colors = doughuntColors;
        ctrl.doughnutTemplate.options = doughuntOpts;
        
        function initCommonChart(nodeId) {
            ctrl.svc.loadCommonData(nodeId)
                .then(function (resp) {
//console.log(resp);
                if (resp === null || angular.isUndefined(resp.data) || resp.data === null) {
                    console.warn("Common data is empty", resp);
                    return false;
                }
                ctrl.data = [0, 0, 0];
                resp.data.forEach(function (colorObj) {
                    switch (colorObj.levelColor) {
                        case "RED": case "red":
                            ctrl.data[0] = colorObj.contObjectCount;
                            break;
                        case "YELLOW": case "yellow":
                            ctrl.data[1] = colorObj.contObjectCount;
                            break;
                        case "GREEN": case "green":
                            ctrl.data[2] = colorObj.contObjectCount;
                            break;
                    }
                });
//                ctrl.data = [resp.data.red, resp.data.yellow, resp.data.green];
//                console.log(ctrl.data);
            }, 
                      function (err) {
                console.log(err);
                
            });
        }
        
        function initResourceChart(nodeId, resource) {
            ctrl.svc.loadResourceData(nodeId, resource)
                .then(function (resp) {
console.log(resp);
                if (resp === null || angular.isUndefined(resp.data) || resp.data === null) {
                    console.warn("Resource data: " + resource + " is empty", resp);
                    return false;
                }
                ctrl.doughnuts[resource] = Object.assign({}, ctrl.doughnutTemplate); // angular.copy(ctrl.doughnutTemplate);
                ctrl.doughnuts[resource].data = [0, 0, 0];
                ctrl.doughnuts[resource].allCount = 0;
                resp.data.forEach(function (colorObj) {
                    ctrl.doughnuts[resource].allCount += colorObj.contObjectCount;
                    switch (colorObj.levelColor) {
                        case "RED": case "red":
                            ctrl.doughnuts[resource].data[0] = colorObj.contObjectCount;
                            break;
                        case "YELLOW": case "yellow":
                            ctrl.doughnuts[resource].data[1] = colorObj.contObjectCount;
                            break;
                        case "GREEN": case "green":
                            ctrl.doughnuts[resource].data[2] = colorObj.contObjectCount;
                            break;
                    }
                });                 
            }, 
                      function (err) {
                console.log(err);
                
            });
        }
        
        function initCmpnt() {
            console.log("contObjectMonitorComponentController Init!");
            console.log("$stateParams", $stateParams);
            console.log("node", ctrl.node);
            if (angular.isUndefined(ctrl.node) || ctrl.node === null) {
                if (angular.isDefined($stateParams.node) && $stateParams.node !== null) {
                    ctrl.node = $stateParams.node;
                }
            }
            if (angular.isUndefined(ctrl.node) || ctrl.node === null) {
                console.warn("Node is empty.", ctrl.node);
                return false;
            }
            if (ctrl.node.hasOwnProperty("_id") && ctrl.node._id !== null) {
                ctrl.nodeId = ctrl.node._id;
            } else if (ctrl.node.hasOwnProperty("nodeObject") && ctrl.node.nodeObject !== null) {
                ctrl.nodeId = ctrl.node.nodeObject.id;
            }
console.log(ctrl.nodeId);
            //polar graph
            ctrl.labels = labels;
            ctrl.data = [111, 178, 1024];
            ctrl.options = {
                responsive: true
            };
//            ctrl.colours = null;
//            ctrl.colours = [getColor([239, 71, 58]), {
//                    backgroundColor: "#FDB45C"
//                }, {
//                    backgroundColor: "#46BFBD"
//                }
//            ];
            
            //bublic 1 (doughnut 1)
//            ctrl.doughnut = {};
//            ctrl.doughnut.data = [33, 66, 100];
//            ctrl.doughnut.labels = ["Критические (" + ctrl.doughnut.data[0]+")", "Некритические (" + ctrl.doughnut.data[1]+")", "Остальные (" + ctrl.doughnut.data[2]+")"];            
//            ctrl.doughnut.colors = doughuntColors;
//            ctrl.doughnut.options = doughuntOpts;
            
             //bublic 2 (doughnut 1)
//            ctrl.doughnut2 = {};
//            ctrl.doughnut2.data = [11, 0, 1000];
//            ctrl.doughnut2.labels = ["Критические (" + ctrl.doughnut2.data[0]+")", "Некритические (" + ctrl.doughnut2.data[1]+")", "Остальные (" + ctrl.doughnut2.data[2]+")"];            
//            ctrl.doughnut2.colors = doughuntColors;
//            ctrl.doughnut2.options = doughuntOpts;
            
                         //bublic 3 (doughnut 3)
//            ctrl.doughnut3 = {};
//            ctrl.doughnut3.data = [1, 123, 0];
//            ctrl.doughnut3.labels = ["Критические (" + ctrl.doughnut3.data[0]+")", "Некритические (" + ctrl.doughnut3.data[1]+")", "Остальные (" + ctrl.doughnut3.data[2]+")"];            
//            ctrl.doughnut3.colors = doughuntColors;
//            ctrl.doughnut3.options = doughuntOpts;
            
            ctrl.doughnutClick = segmentClick;
            
            initCommonChart(ctrl.nodeId);
            var resources = ['heat', 'hw', 'cw', 'el'];
            for (var res in resources) {
                initResourceChart(ctrl.nodeId, resources[res]);
            }
        }
        
    }
    
}());