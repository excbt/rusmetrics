/*global angular, console*/
(function () {
    'use strict';
    
    angular.module('objectTreeModule')
        .component('treeInfoComponent', {
            templateUrl: "components/object-tree-module/tree-info-component/tree-info-component.html",
            controller: treeInfoComponentController
        });
    
    treeInfoComponentController.$inject = ['$stateParams'];
    
    function treeInfoComponentController($stateParams) {
        /*jshint validthis: true*/
        var ctrl = this;
                
        ctrl.$onInit = function () {            
        };
    }
    
}());