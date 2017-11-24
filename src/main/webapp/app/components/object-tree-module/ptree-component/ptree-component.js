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
        ctrl.filtredTree = {};
        
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
        
        function convertArrayToHash(arr) {
            if (angular.isUndefined(arr) || arr === null || !angular.isArray(arr)) {
                return {};
            }
            var result = {};
            arr.forEach(function (elm) {
                result[elm.contObjectId] = angular.copy(elm);
            });
            return result;
        }
        
        ctrl.filterTree = function (tree, filterArr) {                      
            //check for null && undefined
            if (!angular.isArray(filterArr)) {
                return angular.copy(tree);
            }
            var hashFilterArr = convertArrayToHash(filterArr);
console.log(hashFilterArr);            
            //
            var result = angular.copy(tree);
            delete result.linkedNodeObjects;
            result.linkedNodeObjects = [];
//            result.childNodes = angular.copy(tree.childNodes);            
            tree.linkedNodeObjects.forEach(function (lno) {
                if (angular.isDefined(hashFilterArr[lno.nodeObject.id]) && hashFilterArr[lno.nodeObject.id] !== null) {
                    result.linkedNodeObjects.push(angular.copy(lno));
                }
            });
            return result;
        };
        
        ctrl.$onInit = function () {
console.log(ctrl.ptreeNode);
            ctrl.filtredTree = ctrl.filterTree(ctrl.ptreeNode, ctrl.filterArray);
console.log(ctrl.filtredTree);            
        };
    }
    
    angular.module('objectTreeModule')
        .component('ptreeComponent', ptreeComponentOpt);
    
}());