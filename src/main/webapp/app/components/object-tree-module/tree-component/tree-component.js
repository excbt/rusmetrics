/*global angular*/
(function () {
    'use strict';
    
    angular.module('objectTreeModule')
        .component('treeComponent', {
            templateUrl: "components/object-tree-module/tree-component/tree-component.html",
            controller: treeComponentController
        });
    
    treeComponentController.$inject = ['$scope', '$element', '$attrs'];
    
    function treeComponentController($scope, $element, $attrs) {
        
    }
    
}());