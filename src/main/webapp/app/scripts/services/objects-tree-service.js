/*jslint node: true, nomen: true, eqeq: true, es5: true*/
/*global angular*/
'use strict';
var app = angular.module('portalNMC');
app.service('objectsTreeSvc', ['$http', 'mainSvc', '$interval', '$rootScope', '$q', function ($http, mainSvc, $interval, $rootScope, $q) {
    var service = {};
    var API_URL = "../api",
        P_TREE_NODE_URL = API_URL + "/p-tree-node",
        REFRESH_PERIOD = 600000,
        TREE_ID_FIELD_NAME = "_id",
        NODE_ID_FIELD_NAME = "_id";
    
    var BROADCASTS = {
        requestPTreeLoading: 'objectsTreeSvc:requestPTreeLoading',
        cancelInterval: 'objectsTreeSvc:cancelInterval',
        pTreeLoaded: 'objectsTreeSvc:pTreeLoaded'
    };
    
    var pTreeLoadedId = null,
        pTree = null,
        pTreeLoadingFlag = false;
    
    var interval = null,
        requestParams = {};
    
    ////////////////////////////request canceler 
    var requestCanceler = null;
    var httpOptions = null;

    function isCancelParamsIncorrect() {
        return mainSvc.checkUndefinedNull(requestCanceler) || mainSvc.checkUndefinedNull(httpOptions);
    }

    function getRequestCanceler() {
        return requestCanceler;
    }
    //////////////////////////////////////////////////////////////////////////////
    
    function getPTreeLoadingFlag() {
        return pTreeLoadingFlag;
    }
    
    function getPTree() {
        return pTree;
    }
    
    function setPTree(inputPTree) {
        pTree = inputPTree;
    }
    
    function errorCallback(e) {
        pTreeLoadingFlag = false;
        console.error(e);
    }
    
    function successPTreeLoadingCallback(resp) {
        pTreeLoadingFlag = false;
        pTree = resp.data;
        pTreeLoadedId = pTree[TREE_ID_FIELD_NAME];
        $rootScope.$broadcast(BROADCASTS.pTreeLoaded);
    }
    
    function loadPTreeNode(subscrObjectTreeId, childLevel) {
console.log("objectsTreeService.loadPTreeNode");
console.log("subscrObjectTreeId = " + subscrObjectTreeId);
console.log("requestParams: ", requestParams);
        if (mainSvc.checkUndefinedNull(subscrObjectTreeId)) {
            console.warn("Incorrect input param: ", subscrObjectTreeId);
            return null;
        }
        var url = P_TREE_NODE_URL + "/" + subscrObjectTreeId; // "";
        mainSvc.addParamToURL(url, "childLevel", childLevel);
//        return $http.get(url);
        return $http({
            method: 'GET',
            url: url,
            params: requestParams
        });
    }
    
    function loadingPTreeNode(subscrObjectTreeId, childLevel) {
        pTreeLoadingFlag = true;
        loadPTreeNode(subscrObjectTreeId, childLevel)
            .then(successPTreeLoadingCallback, errorCallback);
    }
    
    
    var findNodeInPTree = function (node, tree) {
        var result = null;
//        console.log("Serching tree: " + tree.nodeType + "; " + tree._id);
//        console.log("Serching node: " + node.nodeType + "; " + node.nodeName + "; " + node._id);
        if (angular.isArray(tree.childNodes)) {
            tree.childNodes.some(function (curNode) {
//                console.log("curNode node: " + curNode.nodeType + "; " + curNode.nodeName + "; " + curNode._id);
                if (node.nodeType == 'ELEMENT' && node.nodeType === curNode.nodeType && node._id == curNode._id) {
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
        if (angular.isArray(tree.linkedNodeObjects) && result == null) {
            tree.linkedNodeObjects.some(function (curLinkedNode) {
//                console.log("curLinked node: " + curLinkedNode.nodeType + "; " + curLinkedNode.nodeName + "; " + curLinkedNode._id);
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
    
    $rootScope.$on(BROADCASTS.requestPTreeLoading, function (even, args) {
console.log("Start ptree refresher: ", args);
        if (mainSvc.checkUndefinedNull(args.subscrObjectTreeId) || Number(args.subscrObjectTreeId) === pTreeLoadedId) {
            return false;
        }
        
//console.log("logSvc:requestSessionsLoading");   
        if (interval != null) {
            $interval.cancel(interval);
            interval = null;
        }
//        requestParams = args.params;
        
//console.log("Interval start");
        loadingPTreeNode(Number(args.subscrObjectTreeId), args.childLevel);
        interval = $interval(function () {
            loadingPTreeNode(Number(args.subscrObjectTreeId), args.childLevel);
        }, REFRESH_PERIOD);
    });
    
    $rootScope.$on(BROADCASTS.cancelInterval, function () {
//console.log("Interval cancel");        
        if (interval != null) {
            $interval.cancel(interval);
            interval = null;
        }
    });

    function initSvc() {
        requestCanceler = $q.defer();
        requestParams.timeout = requestCanceler.promise;
//        $interval(loadSessions, REFRESH_PERIOD);
    }
    
    initSvc();
    
    service.BROADCASTS = BROADCASTS;
    service.loadPTreeNode = loadPTreeNode;
    service.findNodeInPTree = findNodeInPTree;
    service.getRequestCanceler = getRequestCanceler;
    service.getPTree = getPTree;
    service.setPTree = setPTree;
    return service;
}]);
