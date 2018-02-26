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
            
            //bublic 1 (doughnut 1)
            ctrl.doughnut = {};
            ctrl.doughnut.labels = ["Критические", "Некритические", "Остальные"];
            ctrl.doughnut.data = [33, 66, 100];
            ctrl.doughnut.colors = ["#ef473a", "#FDB45C", "#46BFBD"]; //green color
            ctrl.doughnut.options = {
                responsive: true
            };
            
             //bublic 1 (doughnut 1)
            ctrl.doughnut2 = {};
            ctrl.doughnut2.labels = ["Критические", "Некритические", "Остальные"];
            ctrl.doughnut2.data = [11, 0, 1000];
            ctrl.doughnut2.colors = ["#ef473a", "#FDB45C", "#46BFBD"]; //green color
            ctrl.doughnut2.options = {
                responsive: true
            };
            
                         //bublic 1 (doughnut 1)
            ctrl.doughnut3 = {};
            ctrl.doughnut3.labels = ["Критические", "Некритические", "Остальные"];
            ctrl.doughnut3.data = [1, 123, 0];
            ctrl.doughnut3.colors = ["#ef473a", "#FDB45C", "#46BFBD"]; //green color
            ctrl.doughnut3.options = {
                responsive: true,
                maintainAspextRatio: false,
                legend: {
                    display: true,
                    position: "right",
                    onClick: function () {
                        console.log("Label click!");
                    }
                },
                title: {
                    display: true,
                    position: "top",
                    text: "График СД"
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
                    elm.style.height = "50vh";//css("height", "45vh");
                }
            }
        };
        
    }
    
}());