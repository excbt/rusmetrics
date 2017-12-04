/*global angular, console*/
(function () {
    'use strict';
    
    angular.module('objectTreeModule')
        .component('treeNavigateComponent', {
            templateUrl: "components/object-tree-module/tree-navigate-component/tree-navigate-component.html",
            controller: treeNavigateComponentController
        });
    
    treeNavigateComponentController.$inject = ['$scope', '$element', '$attrs', 'treeNavigateComponentService'];
    
    function treeNavigateComponentController($scope, $element, $attrs, treeNavigateComponentService) {
        /*jshint validthis: true*/
        var ctrl = this;
                
    }
    
}());