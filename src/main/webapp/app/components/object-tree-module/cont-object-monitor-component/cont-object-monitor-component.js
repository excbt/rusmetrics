/*global angular, console*/
/***
    created by Artamonov A.A. , Dec. 2017
*/
(function () {
    'use strict';
    
    angular.module('objectTreeModule')
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
        ctrl.$onInit = function () {
            console.log("contObjectMonitorComponentController Init!");
            ctrl.labels = ["label1", "label2", "label3"];
            ctrl.data = [172, 50, 0];
            ctrl.options = {
                responsive: true
            };
        };
        
    }
    
}());