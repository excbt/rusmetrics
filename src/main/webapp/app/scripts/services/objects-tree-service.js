/*jslint node: true, nomen: true, eqeq: true, es5: true*/
/*global angular*/
'use strict';
var app = angular.module('portalNMC');
app.service('objectsTreeSvc', ['$http', 'mainSvc', function ($http, mainSvc) {
    var service = {};
    var API_URL = "../api",
        P_TREE_NODE_URL = API_URL + "/p-tree-node";
    
    function loadPTreeNode(subscrObjectTreeId, childLevel) {
        if (mainSvc.checkUndefinedNull(subscrObjectTreeId)) {
            console.warn("Incorrect input param: ", subscrObjectTreeId);
            return null;
        }
        var url = P_TREE_NODE_URL + "/" + subscrObjectTreeId; // "";
        mainSvc.addParamToURL(url, "childLevel", childLevel);
        return $http.get(url);
    }
    
    var findNodeInPTree = function (node, tree) {
        var result = null;
//        console.log("Serching tree: " + tree.nodeType);
        if (angular.isArray(tree.childNodes)) {
            tree.childNodes.some(function (curNode) {
//                console.log("curNode node: " + curNode.nodeType + "; " + curNode.nodeName);                
                if (node.nodeType == 'ELEMENT' && node.nodeType === curNode.nodeType && node._id == curNode._id && node.nodeName == curNode.nodeName) {
                    result = curNode;
                    return true;
                } else if (node.nodeType == 'CONT_ZPOINT' && node.nodeType === curNode.nodeType && node.nodeObject.id == curNode.nodeObject.id) {
                    result = curNode;
                    return true;
                } else if (node.nodeType == 'DEVICE_OBJECT' && node.nodeType === curNode.nodeType && node.nodeObject.id == curNode.nodeObject.id) {
                    result = curNode;
                    return true;
                } else {
                    result = findNodeInPTree(node, curNode);
                    return result != null;
                }
            });
        }
//        console.log("Serching tree - linkedNodeObjects: " + tree.hasOwnProperty('linkedNodeObjects'));
        if (angular.isArray(tree.linkedNodeObjects)) {
            tree.linkedNodeObjects.some(function (curLinkedNode) {
//                console.log("curLinked node: " + curLinkedNode.nodeType + "; " + curLinkedNode.nodeName);
                if (node.nodeType == 'CONT_OBJECT' && node.nodeType === curLinkedNode.nodeType && node.nodeObject.id == curLinkedNode.nodeObject.id) {
                    result = curLinkedNode;
                    return true;
                } else {
                    result = findNodeInPTree(node, curLinkedNode);
                    return result != null;
                }
            });
        }
        
        return result;
    };
    
    function findContObjectInPTree(contObject, tree) {
        return null;
    }
    
    function findContZpointInPTree(contZpoint, tree) {
        return null;
    }
    
    service.loadPTreeNode = loadPTreeNode;
    service.findNodeInPTree = findNodeInPTree;
    return service;
}]);
