
/*jslint node: true*/
/*global angular*/
(function () {
    'use strict';
    
    function treeElementNodeComponentServiceFn($http, $rootScope) {
        var API = {
            CONT_EVENT_CATEGORIES_URL: "../api/widgets/cont-event-monitor/cont-event-categories",
            CONT_EVENT_TYPES_URL: "../api/widgets/cont-event-monitor/cont-event-types",
            PTREE_NODE_STATS_URL: "../api/widgets/cont-event-monitor/p-tree-node/stats" /*"resource/treeNotificationStats.json"*/
        };

        var service = {};

        function loadPTreeNodeStats(ptreeNodeId) {
            var url = API.PTREE_NODE_STATS_URL;
            if (angular.isDefined(ptreeNodeId) && ptreeNodeId !== null) {
                url += "?nodeId=" + ptreeNodeId;
            }
            return $http.get(url);
        }
        
        function loadContEventTypes() {
            var url = API.CONT_EVENT_TYPES_URL;
            return $http.get(url); // .then(successLoadContEventTypesCallback, errorCallback);
        }
        
        function isCriticalEvent(event, contEventTypes) {
            var typeId = event.contEventTypeId;
            var eventType = null;
            if (contEventTypes.hasOwnProperty(typeId)) {
                eventType = contEventTypes[typeId];
                if (eventType.hasOwnProperty("levelColor") && eventType.levelColor === "RED") {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        service.isCriticalEvent = isCriticalEvent;
        service.loadContEventTypes = loadContEventTypes;
        service.loadPTreeNodeStats = loadPTreeNodeStats;        

        return service;
    }
    
    treeElementNodeComponentServiceFn.$inject = ['$http', '$rootScope'];
    
    angular.module('objectTreeModule')
        .service('treeNodeNotificationsComponentService', treeElementNodeComponentServiceFn);
}());