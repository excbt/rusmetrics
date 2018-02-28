/*global angular, console*/
/***
    created by Artamonov A.A. , Dec. 2017
*/
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
        ctrl.contObjectStateShowFlag = false;
        
        function getRandomColor () {
            var color = [getRandomInt(0, 255), getRandomInt(0, 255), getRandomInt(0, 255)];
            return getColor(color);
        }
        
        function rgba (color, alpha) {
          // rgba not supported by IE8
//            useExcanvas ? 'rgb(' + color.join(',') + ')' : 
          return 'rgba(' + color.concat(alpha).join(',') + ')';
        }
        
        function getColor (color) {
            var alpha = color[3] || 1;
            color = color.slice(0, 3);
            return {
                backgroundColor: rgba(color, 0.2),
                pointBackgroundColor: rgba(color, alpha),
                pointHoverBackgroundColor: rgba(color, 0.8),
                borderColor: rgba(color, alpha),
                pointBorderColor: '#fff',
                pointHoverBorderColor: rgba(color, alpha)
            };
        }
        
        ctrl.$onInit = function () {
            console.log("contObjectMonitorComponentController Init!");
            //polar graph
            ctrl.labels = ["Критические", "Некритические", "Зеленые", "Остальные"];
            ctrl.data = [111, 178, 48, 1024];
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
            
            function legendCallback(chart) {
                var text = [], i;
                text.push('<ul class="' + chart.id + '-legend">');

                var data = chart.data;
                var datasets = chart.datasets;
                var labels = data.labels;
                if (datasets.length){
                    for (i = 0; i < datasets[0].length; i += 1) {
                        text.push('<li><span style="height: 12px; width: 12px; border-radius: 5px; background-color:' + datasets[0].backgroundColor[i] + '"></span>');
                        if (data[i]) {
                            text.push(data[i]);
                        }
                        text.push('</li>');
                    }
                }
                    text.push('</ul>');
    console.log(text.join(''));
                    return text.join('');
                }
            
            //bublic 1 (doughnut 1)
            ctrl.doughnut = {};
            ctrl.doughnut.data = [33, 66, 100];
            ctrl.doughnut.labels = ["Критические (" + ctrl.doughnut.data[0]+")", "Некритические (" + ctrl.doughnut.data[1]+")", "Остальные (" + ctrl.doughnut.data[2]+")"];            
            ctrl.doughnut.colors = ["#ef473a", "#FDB45C", "#46BFBD"]; //green color
            ctrl.doughnut.options = {
                responsive: false,
                cutoutPercentage: 80,
                legend: {
                    display: true,
                    position: "right",
                    onClick: function () {
                        console.log("Label click!");
                    },
                    labels: {
                        fontSize: 10,
                        generateLabels: generateLabels
                    },                    
                },
                legendCallback: legendCallback
                /*title: {
                    display: true,
                    postion: "top",
                    text: "<img src = \"components\object-tree-module\cont-object-monitor-component\heat.png\"/> (123)"
                }*/
            };
            
             //bublic 1 (doughnut 1)
            ctrl.doughnut2 = {};
            ctrl.doughnut2.data = [11, 0, 1000];
            ctrl.doughnut2.labels = ["Критические (" + ctrl.doughnut2.data[0]+")", "Некритические (" + ctrl.doughnut2.data[1]+")", "Остальные (" + ctrl.doughnut2.data[2]+")"];            
            ctrl.doughnut2.colors = ["#ef473a", "#FDB45C", "#46BFBD"]; //green color
            ctrl.doughnut2.options = {
                responsive: false,
                cutoutPercentage: 80,
                legend: {
                    display: true,
                    position: "right",
                    onClick: function () {
                        console.log("Label click!");
                    },
                    labels: {
                        fontSize: 10,
                        generateLabels: generateLabels
                    }
                }
            };
            
                         //bublic 1 (doughnut 1)
            ctrl.doughnut3 = {};
            ctrl.doughnut3.data = [1, 123, 0];
            ctrl.doughnut3.labels = ["Критические (" + ctrl.doughnut3.data[0]+")", "Некритические (" + ctrl.doughnut3.data[1]+")", "Остальные (" + ctrl.doughnut3.data[2]+")"];            
            ctrl.doughnut3.colors = ["#ef473a", "#FDB45C", "#46BFBD"]; //green color
            ctrl.doughnut3.options = {
                responsive: false,
                cutoutPercentage: 80,
                maintainAspextRatio: true,
                legend: {
                    display: true,
                    position: "right",
                    onClick: function () {
                        console.log("Label click!");
                    },
                    labels: {
                        fontSize: 10,
                        generateLabels: generateLabels
                    }
                }
            };
            
            ctrl.doughnutClick = segmentClick;
            
            function segmentClick() {
                ctrl.contObjectStateShowFlag = false;
                $timeout(function () {
                    ctrl.contObjectStateShowFlag = true;
                }, 1000);
                console.log($('.nmc-cont-object-control-main-div'));
                var elm = $('.nmc-cont-object-control-main-div').get(0);
                console.log(elm);
                if (elm !== null) {
                    elm.style.height = "49vh";//css("height", "45vh");
                }
            }
        };
        
    }
    
}());