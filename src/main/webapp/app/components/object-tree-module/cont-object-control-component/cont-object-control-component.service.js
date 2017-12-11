/*global angular*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .service('contObjectControlComponentService', Service);

    Service.$inject = ['$http'];

    /* @ngInject */
    function Service($http) {
        
        var API_URL = "../api",
            P_TREE_NODE_URL = API_URL + "/p-tree-node",
            P_TREE_NODE_MONITOR_URL = API_URL + "/p-tree-node-monitor/all-linked-objects",            
            OBJECT_URL = "../api/subscr/contEvent/notifications/contObject",
            SUBSCR_TREES_URL = '../api/subscr/subscrObjectTree/contObjectTreeType1',
            REFRESH_PERIOD = 600000,
            TREE_ID_FIELD_NAME = "_id",
            NODE_ID_FIELD_NAME = "_id",
            SUBSCR_OBJECT_TREE_CONT_OBJECTS = "SUBSCR_OBJECT_TREE_CONT_OBJECTS";
        
        this.loadNodeObjects = loadNodeObjects;

        ////////////////
        
        function checkUndefinedNull(inpObj) {
            var result = false;
            if ((angular.isUndefined(inpObj)) || (inpObj === null)) {
                result = true;
            }
            return result;
        }
        
        function addParamToURL(url, paramName, paramValue) {
            if (checkUndefinedNull(url) || checkUndefinedNull(paramName) || checkUndefinedNull(paramValue)) {
                return url;
            }
            url += ((url.indexOf('?') === -1) ? '?' : '&') + encodeURIComponent(paramName) + "=" + encodeURIComponent(paramValue);
            return url;

        }

        function loadNodeObjects(nodeId) {
            var url = P_TREE_NODE_MONITOR_URL;
            url = addParamToURL(url, "nodeId", nodeId);
            return $http.get(url);
        }
    }
})();