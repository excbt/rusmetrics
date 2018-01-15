/*global angular, console*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .service('objectTreeService', Service);

    Service.$inject = ['$http', '$rootScope'];

    /* @ngInject */
    function Service($http, $rootScope) {
        
        var TREE_STUB_URL = '../api/p-tree-node/{subscrObjectTreeId}/stub';
        var EVENTS = {
            TREE_STUB_LOADED: "objectTreeService:treeStubLoaded"
        };
        
        var currentTree = null,
            currentNode = null,
            currentTreeStub = null,
            treeList = null,
            treeStubLoadingFlag = false;
        var svc = this;
        svc.EVENTS = EVENTS;
        svc.findContObjectsByFullTree = findContObjectsByFullTree;
        svc.findContObjectsByNodeId = findContObjectsByNodeId;
        svc.getCurrentNode = getCurrentNode;
        svc.getCurrentTree = getCurrentTree;
        svc.getCurrentTreeStub = getCurrentTreeStub;
        svc.getTreeList = getTreeList;
        
        svc.loadTreeStub = loadTreeStub;
        svc.loadTreeStubWrap = loadTreeStubWrap;
        
        svc.setCurrentNode = setCurrentNode;
        svc.setCurrentTree = setCurrentTree;
        svc.setCurrentTreeStub = setCurrentTreeStub;
        svc.setTreeList = setTreeList;

        ////////////////
        
        function errorCallback(e) {
            console.log(e);
            treeStubLoadingFlag = false;
        }
        
        function findContObjects(node, resObj) {
            if (node.hasOwnProperty('linkedNodeObjects') && angular.isArray(node.linkedNodeObjects)) {
                var nodeContObjects = node.linkedNodeObjects;
                resObj.contObjects = resObj.contObjects.concat(angular.copy(nodeContObjects));
                node.linkedNodeObjects.forEach(function (lno) {                    
                    findContObjects(lno, resObj);
                });
            }
            if (node.hasOwnProperty('childNodes') && angular.isArray(node.childNodes)) {
                node.childNodes.forEach(function (cn) {
                    findContObjects(cn, resObj);
                });
            }
            
        }        
        
        function findContObjectsByFullTree() {
            var tree = getCurrentTreeStub();
            var resObj = {
                    contObjects: []
                };
            findContObjects(tree, resObj);            
            return resObj.contObjects;
        }
        
        function findContObjectsByNodeId(nodeId) {
            //TODO: check node id
            var tree = getCurrentTreeStub();
            var cObjects = [];
            var foundedNode = findNodeByIdAtTree(tree, nodeId);
console.log(foundedNode);            
            if (foundedNode.hasOwnProperty('linkedNodeObjects') && angular.isArray(foundedNode.linkedNodeObjects)) {
                cObjects = foundedNode.linkedNodeObjects;
            }
            return cObjects;
        }
        
        function findNodeByIdAtTree(node, nodeId) {
            var curNodeId = node._id || node.nodeObject.id;
            if (curNodeId === nodeId) {
                return node;
            }
            var fnode;
            if (node.hasOwnProperty('linkedNodeObjects') && angular.isArray(node.linkedNodeObjects)) {                
                node.linkedNodeObjects.some(function (lno) {
                    fnode = findNodeByIdAtTree(lno, nodeId);
                    if (angular.isDefined(fnode)) {
                        return true;
                    }
                });
                if (angular.isDefined(fnode)) {
                    return fnode;
                }
            }
            if (node.hasOwnProperty('childNodes') && angular.isArray(node.childNodes)) {
                
                node.childNodes.some(function (co) {
                    fnode = findNodeByIdAtTree(co, nodeId);
                    if (angular.isDefined(fnode)) {
                        return true;
                    }
                });
                if (angular.isDefined(fnode)) {
                    return fnode;
                }
            }            
//            node.childNodes.forEach();
            
        }

        function getCurrentNode() {
            return currentNode;
        }
        
        function getCurrentTree() {
            return currentTree;
        }
        
        function getCurrentTreeStub() {
            return currentTreeStub;
        }
        
        function getTreeList() {
            return treeList;
        }
        
        function loadTreeStub(treeId) {
            var url = TREE_STUB_URL.replace('{subscrObjectTreeId}', treeId);
            return $http.get(url);
        }
        
        function loadTreeStubWrap(treeId) {
            treeStubLoadingFlag = true;
            loadTreeStub(treeId).then(successLoadTreeStub, errorCallback);
        }
        
        function setCurrentNode(node) {
            currentNode = node;
        }
        
        function setCurrentTree(tree) {
            currentTree = tree;
        }
        function setCurrentTreeStub(treeStub) {
            currentTreeStub = treeStub;
        }
        
        function setTreeList(trees) {
            treeList = trees;
        }
        
        function successLoadTreeStub(resp) {
            console.log(resp);
            treeStubLoadingFlag = false;
            if (resp.data !== null) {
                setCurrentTreeStub(resp.data);
                $rootScope.$broadcast(svc.EVENTS.TREE_STUB_LOADED);
            } else {
                setCurrentTreeStub(null);
            }
        }
    }
})();