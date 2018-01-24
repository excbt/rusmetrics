/*jslint node: true, nomen: true, eqeq: true, es5: true*/
/*global angular, $*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .service('treeNavigateComponentService', Service);

    Service.$inject = ['$http', '$interval', '$rootScope', '$q', '$timeout', 'objectTreeService'];

    /* @ngInject */
    function Service($http, $interval, $rootScope, $q, $timeout, objectTreeService) {
        var service = {};
        var API_URL = "../api",
            P_TREE_NODE_URL = "../api/p-tree-node",
            P_TREE_NODE_MONITOR_URL = "../api/p-tree-node-monitor/all-linked-objects",            
            OBJECT_URL = "../api/subscr/contEvent/notifications/contObject",
            SUBSCR_TREES_URL = '../api/subscr/subscrObjectTree/contObjectTreeType1',
            REFRESH_PERIOD = 600000,
            TREE_ID_FIELD_NAME = "_id",
            NODE_ID_FIELD_NAME = "_id",
            SUBSCR_OBJECT_TREE_CONT_OBJECTS = "SUBSCR_OBJECT_TREE_CONT_OBJECTS";
        
        var DEFAULT_TREE_URL = '../api/subscr/subscrPrefValue?subscrPrefKeyname=' + SUBSCR_OBJECT_TREE_CONT_OBJECTS;
                 //Device types
        var HEAT_DISTRIBUTOR = "HEAT_DISTRIBUTOR";//

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
            pTreeMonitorLoadingFlag = false,
            pTreeSelectedNode = null;

        var interval = null,
            requestParams = {};
                
        function checkUndefinedNull(inpObj) {
            var result = false;
            if ((angular.isUndefined(inpObj)) || (inpObj === null)) {
                result = true;
            }
            return result;
        }
        
        var isSystemuser = objectTreeService.isSystemuser;

        ////////////////////////////request canceler 
        var requestCanceler = null;
        var httpOptions = null;

        function isCancelParamsIncorrect() {
            return checkUndefinedNull(requestCanceler) || checkUndefinedNull(httpOptions);
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
        
        function getPTreeSelectedNode() {
            return pTreeSelectedNode;
        }

        function setPTreeSelectedNode(ptreeNode) {
            pTreeSelectedNode = ptreeNode;
        }
        
        function addParamToURL(url, paramName, paramValue) {
            if (checkUndefinedNull(url) || checkUndefinedNull(paramName) || checkUndefinedNull(paramValue)) {
                return url;
            }
            url += ((url.indexOf('?') === -1) ? '?' : '&') + encodeURIComponent(paramName) + "=" + encodeURIComponent(paramValue);
            return url;

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
//            console.log("objectsTreeService.loadPTreeNode");
//            console.log("subscrObjectTreeId = " + subscrObjectTreeId);
//            console.log("requestParams: ", requestParams);
//            console.log("childLevel: ", childLevel);
            
            if (checkUndefinedNull(subscrObjectTreeId)) {
                console.warn("Incorrect input param: ", subscrObjectTreeId);
                return null;
            }
            var url = P_TREE_NODE_URL + "/" + subscrObjectTreeId; // "";
            url = addParamToURL(url, "childLevel", childLevel);

//            console.log("url: " + url);

            return $http.get(url, requestParams);
    //        return $http({
    //            method: 'GET',
    //            url: url,
    //            params: requestParams
    //        });
        }

        function loadingPTree(subscrObjectTreeId, childLevel) {
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
//console.log(resp);
            pTreeMonitorLoadingFlag = false;
            if (checkUndefinedNull(resp) || checkUndefinedNull(resp.data)) {
                $rootScope.$broadcast(BROADCASTS.pTreeMonitorLoaded);
                return false;
            }
            pTreeMonitor = resp.data;
            $rootScope.$broadcast(BROADCASTS.pTreeMonitorLoaded);
        }

        function loadPTreeMonitor(ptreeId) {
//console.log("objectsTreeSvc.loadPTreeMonitor");
            var url = P_TREE_NODE_MONITOR_URL;
            url = addParamToURL(url, "nodeId", ptreeId);
            pTreeMonitorLoadingFlag = true;
            $http.get(url).then(successPTreeMonotorLoadCallback, errorCallback);
        }

        $rootScope.$on(BROADCASTS.requestPTreeMonitorLoading, function (even, args) {
//console.log("Start ptree monitor refresher: ", args);
            if (checkUndefinedNull(args.subscrObjectTreeId) || Number(args.subscrObjectTreeId) === pTreeLoadedId) {
                return false;
            }
  
            if (interval !== null) {
                $interval.cancel(interval);
                interval = null;
            }

            loadPTreeMonitor(args.subscrObjectTreeId);
            interval = $interval(function () {
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
        
        function loadMonitorEventsForObject(objId) {
//console.log("loadMonitorEventsForObject: " + objId);        
            var url = OBJECT_URL + "/" + objId + "/monitorEventsV2";// + "?fromDate=" + $rootScope.monitorStart + "&toDate=" + $rootScope.monitorEnd;
//            if (isCancelParamsIncorrect() === true) {
//                var deffer = $q.defer();
//                deffer.reject('loadMonitorEventsForObject: isCancelParamsIncorrect');
//                return deffer.promise;
//            }
            return $http.get(url, httpOptions);
        }
        
        function prepareEventMessage(inputData) {
                        //temp array
            var tmpMessage = "";
    //                var tmpMessageEx = "";
            //make the new array of the types wich formatted to display
            inputData.forEach(function (element) {
    //console.log(element);
                var tmpEvent = "";
                var contEventTime = new Date(element.contEventTimeDT);
                var pstyle = "";
                if (element.contEventLevelColorKeyname === "RED") {
                    pstyle = "color: red;";
                }
                tmpEvent = "<p style='" + pstyle + "'>" + contEventTime.toLocaleString() + ", " + element.contEventType.name + "</p>";
                tmpMessage += tmpEvent;
            });
            return tmpMessage;
        }
        
        function setEventsForObject(objId) {
//console.log(objId);
            var imgObj = "#objState" + objId;
//console.log($(imgObj));
            $timeout(function () {
                $(imgObj).qtip({
                    content: {
                        text: function (event, api) {
                            loadMonitorEventsForObject(objId)
                                .then(function (resp) {
//                                        console.log(resp);
                                    var message = "";
                                    if (!checkUndefinedNull(resp) && !checkUndefinedNull(resp.data) && angular.isArray(resp.data)) {
                                        message = prepareEventMessage(resp.data);
                                    } else {
                                        if (!checkUndefinedNull(resp.data) && resp.data.length > 0) {
                                            message = "Ответ от сервера: " + resp.data;
                                        } else {
                                            message = "Нет событий!";
                                        }
                                        console.log(resp);
                                    }
                                    api.set('content.text', message);
                                },
                                     function (error) {
                                        api.set('content.text', error.status + ': ' + error.data);
                                    });
                            return "Загружаются сообытия...";
                        }
                    },

                    style: {
                        classes: 'qtip-bootstrap qtip-nmc-monitor-tooltip'
                    }
                });
            }, 1);
        }
        
        var loadSubscrTrees = function () {
            return $http.get(SUBSCR_TREES_URL);
        };
        
        var loadDefaultTreeSetting = function () {
            return $http.get(DEFAULT_TREE_URL);
        };
        
        function findItemBy(itemArray, fieldName, value) {
            var result = null;
            if (!angular.isArray(itemArray)) {
                console.log("Input value is no array.");
                return result;
            }
            if (checkUndefinedNull(value)) {
                console.log("value for search is undefined or null.");
                return result;
            }
            if (checkUndefinedNull(fieldName)) {
                itemArray.some(function (item) {
                    if (item == value) {
                        result = item;
                        return true;
                    }
                });
            } else {
                itemArray.some(function (item) {
                    if (item[fieldName] == value) {
                        result = item;
                        return true;
                    }
                });
            }
            return result;
        }
        
        
        function isChevronRight(collapsed, item) {
            if (isLazyNode(item)) {
                 return true;
            }
            if (checkUndefinedNull(item) || (checkUndefinedNull(item.childNodes) &&  checkUndefinedNull(item.linkedNodeObjects))) {
                return false;
            }
            return collapsed && ((!checkUndefinedNull(item.childNodes) && item.childNodes.length > 0) || (!checkUndefinedNull(item.linkedNodeObjects) && item.linkedNodeObjects.length > 0));
        }
        function isChevronDown(collapsed, item) {
            if (checkUndefinedNull(item) || ( checkUndefinedNull(item.childNodes) &&  checkUndefinedNull(item.linkedNodeObjects))) {
                return false;
            }
            return !collapsed && ((!checkUndefinedNull(item.childNodes) && item.childNodes.length > 0) || (!checkUndefinedNull(item.linkedNodeObjects) && item.linkedNodeObjects.length > 0));
        }

        function isChevronDisabled(collapsed, item) {
            if (isLazyNode(item)) {
                return false;
            }
            if ( checkUndefinedNull(item) || ( checkUndefinedNull(item.childNodes) &&  checkUndefinedNull(item.linkedNodeObjects))) {
                return true;
            }
            return !((!checkUndefinedNull(item.childNodes) && item.childNodes.length > 0) || (!checkUndefinedNull(item.linkedNodeObjects) && item.linkedNodeObjects.length > 0));
        }
        
        var isContObjectNode = objectTreeService.isContObjectNode;

        var isContZpointNode = objectTreeService.isContZpointNode;

        var isDeviceNode = objectTreeService.isDeviceNode;        

        var isElementNode = objectTreeService.isElementNode;
        var isLazyNode = objectTreeService.isLazyNode;        
        
            // Sort object array by some string field
        function sortItemsBy(itemArray, sortField) {
            if (!angular.isArray(itemArray)) {
                return "Input value is no array.";
            }
            if (checkUndefinedNull(sortField)) {
                return "Field for sort is undefined or null.";
            }            
            itemArray.sort(function (firstItem, secondItem) {
                if (checkUndefinedNull(firstItem[sortField]) && checkUndefinedNull(secondItem[sortField])) {
                    return 0;
                }
                if (checkUndefinedNull(firstItem[sortField])) {
                    return -1;
                }
                if (checkUndefinedNull(secondItem[sortField])) {
                    return 1;
                }
                if (firstItem[sortField].toUpperCase() > secondItem[sortField].toUpperCase()) {
                    return 1;
                }
                if (firstItem[sortField].toUpperCase() < secondItem[sortField].toUpperCase()) {
                    return -1;
                }
                return 0;
            });
        }
        
        function sortArrayWithNodeObjectBy(objectsArr, sortField) {
            if (!angular.isArray(objectsArr)) {
                return "Input value is no array.";
            }
            if (checkUndefinedNull(sortField)) {
                return "Field for sort is undefined or null.";
            }
            var nodeObject = 'nodeObject';
            objectsArr.sort(function (firstItem, secondItem) {
                if (checkUndefinedNull(firstItem[nodeObject]) && checkUndefinedNull(secondItem[nodeObject])) {
                    return 0;
                }
                if (checkUndefinedNull(firstItem[nodeObject])) {
                    return -1;
                }
                if (checkUndefinedNull(secondItem[nodeObject])) {
                    return 1;
                }
                
                if (checkUndefinedNull(firstItem.nodeObject[sortField]) && checkUndefinedNull(secondItem.nodeObject[sortField])) {
                    return 0;
                }
                if (checkUndefinedNull(firstItem.nodeObject[sortField])) {
                    return -1;
                }
                if (checkUndefinedNull(secondItem.nodeObject[sortField])) {
                    return 1;
                }
                if (firstItem.nodeObject[sortField].toUpperCase() > secondItem.nodeObject[sortField].toUpperCase()) {
                    return 1;
                }
                if (firstItem.nodeObject[sortField].toUpperCase() < secondItem.nodeObject[sortField].toUpperCase()) {
                    return -1;
                }
                return 0;
            });
        }
        
        function checkEmptyObject(obj) {
                return Object.keys(obj).length === 0 && obj.constructor === Object;
        }

        function initSvc() {            
            requestCanceler = $q.defer();
            requestParams.timeout = requestCanceler.promise;
    //        $interval(loadSessions, REFRESH_PERIOD);
        }

        initSvc();

        service.BROADCASTS = BROADCASTS;
        service.HEAT_DISTRIBUTOR = HEAT_DISTRIBUTOR;
        service.checkUndefinedNull = checkUndefinedNull;
        service.checkEmptyObject = checkEmptyObject;
        service.findItemBy = findItemBy;
        service.findNodeInPTree = findNodeInPTree;
        service.getPTree = getPTree;
        service.getPTreeMonitor = getPTreeMonitor;
        service.getPTreeSelectedNode = getPTreeSelectedNode;
        service.getRequestCanceler = getRequestCanceler;
        
        service.isChevronDisabled = isChevronDisabled;
        service.isChevronDown = isChevronDown;
        service.isChevronRight = isChevronRight;
        
        service.isContObjectNode = isContObjectNode;
        service.isContZpointNode = isContZpointNode;
        service.isDeviceNode = isDeviceNode;        
        service.isElementNode = isElementNode;
        service.isLazyNode = isLazyNode;
        
        service.isSystemuser = isSystemuser;
        
        service.loadDefaultTreeSetting = loadDefaultTreeSetting;
        service.loadPTreeNode = loadPTreeNode;
        service.loadSubscrTrees = loadSubscrTrees;
        service.setEventsForObject = setEventsForObject;        
        service.setPTree = setPTree;
        service.setPTreeSelectedNode = setPTreeSelectedNode;
        service.sortItemsBy = sortItemsBy;
        service.sortArrayWithNodeObjectBy = sortArrayWithNodeObjectBy;
        return service;
    }
})();
