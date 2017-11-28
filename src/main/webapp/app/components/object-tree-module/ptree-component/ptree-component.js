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
    
    ptreeComponentController.$inject = ['$scope', '$element', '$attrs', 'ptreeComponentService'];
    
    function ptreeComponentController($scope, $element, $attrs, ptreeComponentService) {
        var PTREE_DEPTH_LEVEL = 0;
        /*jshint validthis: true*/
        var ctrl = this;
        ctrl.filtredTree = {};
        ctrl.hashFilterTable = {};
        
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
            if (!angular.isArray(arr)) {
                return {};
            }
            var result = {};
            arr.forEach(function (elm) {
                result[elm.contObjectId] = angular.copy(elm);
            });
            return result;
        }
        
        function filterLinkedObjects(inputArray, hashFilter) {
            var result = [];
            if (!angular.isArray(inputArray)) {
                return result;
            }
            inputArray.forEach(function (elm) {
                if (angular.isDefined(hashFilter[elm.nodeObject.id]) && hashFilter[elm.nodeObject.id] !== null) {
                    result.push(angular.copy(elm));
                }
            });
            return result;
        }
        
        function filterTreeNode(treeNode, hashFilter) {
//console.log(treeNode);            
            var result = treeNode,
                treeCopy = angular.copy(treeNode);
            if (result.hasOwnProperty('linkedNodeObjects')) {
                delete result.linkedNodeObjects;
            }
            result.linkedNodeObjects = [];
//            result.childNodes = angular.copy(tree.childNodes);            
            if (treeCopy.hasOwnProperty('linkedNodeObjects') && angular.isArray(treeCopy.linkedNodeObjects)) {
                treeCopy.linkedNodeObjects.forEach(function (lno) {
                    if (angular.isDefined(hashFilter[lno.nodeObject.id]) && hashFilter[lno.nodeObject.id] !== null) {
                        result.linkedNodeObjects.push(angular.copy(lno));
                    }
                });
            }
            if (treeNode.hasOwnProperty('childNodes') && angular.isArray(treeNode.childNodes)) {
                treeNode.childNodes.forEach(function (lno, index) {
                    result.childNodes[index] = filterTreeNode(lno, hashFilter);
                });
            }
//console.log(result);            
            return result;
        }
        
        ctrl.filterTree = function (tree, filterArr) {                      
            //check for null && undefined
            if (!angular.isArray(filterArr)) {
                return tree; // angular.copy(tree);
            }
            var hashFilterArr = convertArrayToHash(filterArr);
//console.log(hashFilterArr);            
            //
            var result = filterTreeNode(tree, hashFilterArr);
            
//console.log(result);            
            return result;
        };
        
        function isLazyNode(treeNode) {
            return treeNode.lazyNode;
        }
        
        ctrl.isChevronRight = ptreeComponentService.isChevronRight;
        ctrl.isChevronDown = ptreeComponentService.isChevronDown;

        ctrl.isChevronDisabled = ptreeComponentService.isChevronDisabled;
        
        function errorCallback(e) {
            console.log(e);
        }
        
       function successLoadPTreeNodeCallback(resp, PTnode) {
            if (ptreeComponentService.checkUndefinedNull(resp) || ptreeComponentService.checkUndefinedNull(resp.data)) {
                return false;
            }
            // console.log(resp);
//                    PTnode = resp.data;
            if (!ptreeComponentService.checkUndefinedNull(resp.data.linkedNodeObjects)) {
                PTnode.linkedNodeObjects = filterLinkedObjects(resp.data.linkedNodeObjects, ctrl.hashFilterTable);
            }
            if (!ptreeComponentService.checkUndefinedNull(resp.data.childNodes)) {
                PTnode.childNodes = resp.data.childNodes;
            }
            // console.log(PTnode);
            // console.log($scope.data.selectedPNode);
        }
        
        function loadPTreeNode(ptNode) {
            ptNode.loading = true;
//            ctrl.filtredTree.loading = true;
            ptreeComponentService.loadPTreeNode(ptNode.id || ptNode._id, PTREE_DEPTH_LEVEL)
                .then(function (resp) {                    
                    successLoadPTreeNodeCallback(resp, ptNode);
//                    ptNode = ctrl.filterTree(ptNode, ctrl.filterArray);
                    ptNode.loading = false;
                    ptNode.lazyNode = false;
console.log(ptNode);                
console.log(ctrl.filtredTree);                
                }, function (err) {
                    ptNode.loading = false;
                    errorCallback(err);
                });            
        }
        
        ctrl.$onInit = function () {
//console.log(ctrl.ptreeNode);
            ctrl.hashFilterTable = convertArrayToHash(ctrl.filterArray);
            if (isLazyNode(ctrl.ptreeNode)) {
                ctrl.filtredTree._id = ctrl.ptreeNode._id; //angular.copy(ctrl.ptreeNode);
                loadPTreeNode(ctrl.filtredTree);
            }                       
        };
        
        ctrl.selectPNode = function (item, ev, collapsed) {
            if (isLazyNode(item)) {
                loadPTreeNode(item, PTREE_DEPTH_LEVEL);
            }
        };
    }
    
    angular.module('objectTreeModule')
        .component('ptreeComponent', ptreeComponentOpt);
    
}());