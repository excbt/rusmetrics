/*global angular, console*/
(function () {
    'use strict';
    
    var ptreeComponentOpt = {
        bindings: {
            ptreeNode: '<',
            filterArray: '<'
        },
        templateUrl: 'components/object-tree-module/ptree-component/ptree-component.html',
        controller: ptreeComponentController
    };
    
    ptreeComponentController.$inject = ['$scope', '$element', '$attrs'];
    
    function ptreeComponentController($scope, $element, $attrs) {
        /*jshint validthis: true*/
        var ctrl = this;
        
        ctrl.isElementNode = function (item) {
            if (angular.isUndefined(item) || item === null) {
                return false;
            }
            return item.nodeType === 'ELEMENT';
        };
        ctrl.isContObjectNode = function (item) {
            if (angular.isUndefined(item) || item === null) {
                return false;
            }
            return item.nodeType === 'CONT_OBJECT';
        };
        ctrl.isContZpointNode = function (item) {
            if (angular.isUndefined(item) || item === null) {
                return false;
            }
            return item.nodeType === 'CONT_ZPOINT';
        };
        ctrl.isDeviceNode = function (item) {
            if (angular.isUndefined(item) || item === null) {
                return false;
            }
            return item.nodeType === 'DEVICE_OBJECT';
        };
        
        ctrl.filterTree = function (tree, filterArr) {
            var result = {}; //angular.copy(tree);
            result.linkedNodeObjects = [];
            result.childNodes = angular.copy(tree.childNodes);            
            tree.linkedNodeObjects.forEach(function (lno) {
                if (lno.nodeObject.id in filterArr) {
                }
            });
            return result;
        };
        
        ctrl.$onInit = function () {
//            console.log(ctrl.ptreeNode);
        };
    }
    
    angular.module('objectTreeModule')
        .component('ptreeComponent', ptreeComponentOpt);
    
}());