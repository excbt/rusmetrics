/*global angular, console*/
(function () {
    'use strict';
    
    function ptreeComponentService($http, $q) {
        var API_URL = "../api",
            P_TREE_NODE_URL = API_URL + "/p-tree-node";
        var service = {},
            requestParams = {},
            requestCanceler = null;
        
        function checkUndefinedNull(numvalue) {
            var result = false;
            if ((angular.isUndefined(numvalue)) || (numvalue === null)) {
                result = true;
            }
            return result;
        }
        
        /**
        Work with URL 
    */
        function addParamToURL(url, paramName, paramValue) {
            if (checkUndefinedNull(url) || checkUndefinedNull(paramName) || checkUndefinedNull(paramValue)) {
                return url;
            }
            url += ((url.indexOf('?') === -1) ? '?' : '&') + encodeURIComponent(paramName) + "=" + encodeURIComponent(paramValue);
            return url;

        }
        
        function isChevronRight(collapsed, item) {
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
            // if (isLazyNode(item)) {
            //     return false;
            // }
            if ( checkUndefinedNull(item) || ( checkUndefinedNull(item.childNodes) &&  checkUndefinedNull(item.linkedNodeObjects))) {
                return true;
            }
            return !((!checkUndefinedNull(item.childNodes) && item.childNodes.length > 0) || (!checkUndefinedNull(item.linkedNodeObjects) && item.linkedNodeObjects.length > 0));
        }
        
        function isContObjectNode(item) {
            if (angular.isUndefined(item) || item === null) {
                return false;
            }
            return item.nodeType === 'CONT_OBJECT';
        }
        function isContZpointNode(item) {
            if (angular.isUndefined(item) || item === null) {
                return false;
            }
            return item.nodeType === 'CONT_ZPOINT';
        }
        function isDeviceNode(item) {
            if (angular.isUndefined(item) || item === null) {
                return false;
            }
            return item.nodeType === 'DEVICE_OBJECT';
        }
        function isElementNode(item) {
            if (angular.isUndefined(item) || item === null) {
                return false;
            }
            return item.nodeType === 'ELEMENT';
        }
        
        function loadPTreeNode(subscrObjectTreeId, childLevel) {
            console.log("objectsTreeService.loadPTreeNode");
            console.log("subscrObjectTreeId = " + subscrObjectTreeId);
            console.log("requestParams: ", requestParams);
            console.log("childLevel: ", childLevel);
            if (checkUndefinedNull(subscrObjectTreeId)) {
                console.warn("Incorrect input param: ", subscrObjectTreeId);
                return null;
            }
            var url = P_TREE_NODE_URL + "/" + subscrObjectTreeId; // "";
            url = addParamToURL(url, "childLevel", childLevel);

            console.log("url: " + url);

            return $http.get(url, requestParams);
    //        return $http({
    //            method: 'GET',
    //            url: url,
    //            params: requestParams
    //        });
        }
        
        function initSvc() {
            requestCanceler = $q.defer();
            requestParams.timeout = requestCanceler.promise;
    //        $interval(loadSessions, REFRESH_PERIOD);
        }

        initSvc();
            
        service.checkUndefinedNull = checkUndefinedNull;
        service.isChevronDisabled = isChevronDisabled;
        service.isChevronDown = isChevronDown;
        service.isChevronRight = isChevronRight;
        
        service.isContObjectNode = isContObjectNode;
        service.isContZpointNode = isContZpointNode;
        service.isDeviceNode = isDeviceNode;
        service.isElementNode = isElementNode;
        
        service.loadPTreeNode = loadPTreeNode;
        
        return service;
    }
    
    ptreeComponentService.$inject = ['$http', '$q'];
    
    angular.module('objectTreeModule')
        .service('ptreeComponentService', ptreeComponentService);
}());