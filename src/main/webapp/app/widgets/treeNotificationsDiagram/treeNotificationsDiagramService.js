
/*jslint node: true*/
/*global angular*/
(function () {
    'use strict';
    
    function treeNotificationsDiagramServiceFn($http, $rootScope) {
        var WIDGETS_URL = "../api/widgets",
            CONT_EVENT_MONITOR = WIDGETS_URL + "/cont-event-monitor";
        var API = {

            CONT_EVENT_CATEGORIES_URL: CONT_EVENT_MONITOR + "/cont-event-categories",
            CONT_EVENT_TYPES_URL: CONT_EVENT_MONITOR + "/cont-event-types",
            PTREE_NODE_STATS_URL: /*"resource/treeNotificationStats.json"*/ CONT_EVENT_MONITOR + "/p-tree-node/stats"
        };

        var EVENTS = {
            categoriesLoaded: "treeNotificationsDiagramService: categoriesLoaded",
            typesLoaded: "treeNotificationsDiagramService: typesLoaded"
        };

        var service = {},
            contEventCategories = null,
            contEventTypes = null;

        function getContEventCategories() {
            return contEventCategories;
        }

        function getContEventTypes() {
            return contEventTypes;
        }

        function checkEmptyObject(obj) {
            return Object.keys(obj).length === 0 && obj.constructor === Object;
        }

        function errorCallback(e) {
            console.log(e);
        }

        function successLoadContEventCategoriesCallback(resp) {
            console.log(resp);
            contEventCategories = resp.data || resp;
            $rootScope.$broadcast(EVENTS.categoriesLoaded);
        }

        function successLoadContEventTypesCallback(resp) {
            console.log(resp);
            contEventTypes = resp.data || resp;
            $rootScope.$broadcast(EVENTS.typesLoaded);
        }

        function loadContEventCategories() {
            var url = API.CONT_EVENT_CATEGORIES_URL;
            $http.get(url).then(successLoadContEventCategoriesCallback, errorCallback);
        }

        function loadContEventTypes() {
            var url = API.CONT_EVENT_TYPES_URL;
            $http.get(url).then(successLoadContEventTypesCallback, errorCallback);
        }

        function loadPTreeNodeStats(ptreeNodeId) {
            var url = API.PTREE_NODE_STATS_URL;
            if (angular.isDefined(ptreeNodeId) && ptreeNodeId !== null) {
                url += "?nodeId=" + ptreeNodeId;
            }
            return $http.get(url);
        }

        function initSvc() {
            loadContEventCategories();
            loadContEventTypes();                
        }

        initSvc();

        service.EVENTS = EVENTS;
        service.checkEmptyObject = checkEmptyObject;
        service.loadPTreeNodeStats = loadPTreeNodeStats;
        service.getContEventCategories = getContEventCategories;
        service.getContEventTypes = getContEventTypes;

        return service;
    }
    
    treeNotificationsDiagramServiceFn.$inject = ['$http', '$rootScope'];
    
    angular.module('treeNotificationsDiagramWidgetSvc', [])
        .service('treeNotificationsDiagramService', treeNotificationsDiagramServiceFn);
}());