/*jslint node: true, nomen: true, eqeq: true, es5: true*/
/*global angular*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .service('treeNavigateComponentService', Service);

    Service.$inject = ['$http', 'mainSvc', '$interval', '$rootScope', '$q'];

    /* @ngInject */
    function Service($http, mainSvc, $interval, $rootScope, $q) {
        var service = {};
        var API_URL = "../api",
            P_TREE_NODE_URL = API_URL + "/p-tree-node",
            P_TREE_NODE_MONITOR_URL = API_URL + "/p-tree-node-monitor/all-linked-objects",
            REFRESH_PERIOD = 600000,
            TREE_ID_FIELD_NAME = "_id",
            NODE_ID_FIELD_NAME = "_id";

        var BROADCASTS = {
            requestPTreeLoading: 'objectsTreeSvc:requestPTreeLoading',
            requestPTreeMonitorLoading: 'objectsTreeSvc:requestPTreeMonitorLoading',
            cancelInterval: 'objectsTreeSvc:cancelInterval',
            pTreeLoaded: 'objectsTreeSvc:pTreeLoaded',
            pTreeMonitorLoaded: 'objectsTreeSvc:pTreeMonitorLoaded'
        };

        var pTreeLoadedId = null,
            pTree = null,
            pTreeLoadingFlag = false,
            pTreeMonitor = null,
            pTreeMonitorLoadingFlag = false;

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

        function getPTreeMonitorLoadingFlag() {
            return pTreeMonitorLoadingFlag;
        }

        function getPTreeMonitor() {
            return pTreeMonitor;
        }

        function errorCallback(e) {
            pTreeLoadingFlag = false;
            pTreeMonitorLoadingFlag = false;
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
            console.log("childLevel: ", childLevel);
            if (mainSvc.checkUndefinedNull(subscrObjectTreeId)) {
                console.warn("Incorrect input param: ", subscrObjectTreeId);
                return null;
            }
            var url = P_TREE_NODE_URL + "/" + subscrObjectTreeId; // "";
            url = mainSvc.addParamToURL(url, "childLevel", childLevel);

            console.log("url: " + url);

            return $http.get(url, requestParams);
    //        return $http({
    //            method: 'GET',
    //            url: url,
    //            params: requestParams
    //        });
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
                        return result !== null;
                    }
                });
            }
    //        console.log("Serching tree - linkedNodeObjects: " + tree.hasOwnProperty('linkedNodeObjects'));
            if (angular.isArray(tree.linkedNodeObjects) && result === null) {
                tree.linkedNodeObjects.some(function (curLinkedNode) {
    //                console.log("curLinked node: " + curLinkedNode.nodeType + "; " + curLinkedNode.nodeName + "; " + curLinkedNode._id);
                    if (node.nodeType == 'CONT_OBJECT' && node.nodeType === curLinkedNode.nodeType && node.nodeObject.id == curLinkedNode.nodeObject.id) {
                        result = curLinkedNode;
                        return true;
                    } else {
                        result = findNodeInPTree(node, curLinkedNode);
                        return result !== null;
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

        function successPTreeMonotorLoadCallback(resp) {
            console.log(resp);
            pTreeMonitorLoadingFlag = false;
            if (mainSvc.checkUndefinedNull(resp) || mainSvc.checkUndefinedNull(resp.data)) {
                $rootScope.$broadcast(BROADCASTS.pTreeMonitorLoaded);
                return false;
            }
            pTreeMonitor = resp.data;
            $rootScope.$broadcast(BROADCASTS.pTreeMonitorLoaded);
        }

        function loadPTreeMonitor(ptreeId) {
            console.log("objectsTreeSvc.loadPTreeMonitor");
            var url = P_TREE_NODE_MONITOR_URL;
            url = mainSvc.addParamToURL(url, "nodeId", ptreeId);
            pTreeMonitorLoadingFlag = true;
            $http.get(url).then(successPTreeMonotorLoadCallback, errorCallback);
        }

        $rootScope.$on(BROADCASTS.requestPTreeMonitorLoading, function (even, args) {
            console.log("Start ptree monitor refresher: ", args);
            if (mainSvc.checkUndefinedNull(args.subscrObjectTreeId) || Number(args.subscrObjectTreeId) === pTreeLoadedId) {
                return false;
            }

    //console.log("logSvc:requestSessionsLoading");   
            if (interval !== null) {
                $interval.cancel(interval);
                interval = null;
            }
    //        requestParams = args.params;

    //console.log("Interval start");
    //        loadingPTreeNode(Number(args.subscrObjectTreeId), args.childLevel);
            loadPTreeMonitor(args.subscrObjectTreeId);
            interval = $interval(function () {
    //            loadingPTreeNode(Number(args.subscrObjectTreeId), args.childLevel);
                loadPTreeMonitor(args.subscrObjectTreeId);
            }, REFRESH_PERIOD);
        });

        $rootScope.$on(BROADCASTS.cancelInterval, function () {
    //console.log("Interval cancel");        
            if (interval !== null) {
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
        service.getPTreeMonitor = getPTreeMonitor;
        service.getRequestCanceler = getRequestCanceler;
        service.getPTree = getPTree;
        service.setPTree = setPTree;
        return service;
    }
})();
