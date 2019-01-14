
/*jslint node: true*/
/*global angular, $*/
(function () {
    'use strict';
    
    function treeNotificationsDiagramServiceFn($http, $rootScope, $timeout) {
        var WIDGETS_URL = "../api/widgets",
            CONT_EVENT_MONITOR = WIDGETS_URL + "/cont-event-monitor";
        var API = {
            CONT_OBJECTS_URL: "../api/subscr/contObjects",
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
        
        function loadContObject(contObjectId) {
            if (angular.isUndefined(contObjectId) || contObjectId === null) {
                return false;
            }
            var url = API.CONT_OBJECTS_URL + "/" + contObjectId;
            return $http.get(url);
        }
        
        function checkUndefinedNull(numvalue) {
            var result = false;
            if ((angular.isUndefined(numvalue)) || (numvalue === null)) {
                result = true;
            }
            return result;
        }
        
        var setToolTip = function (title, text, elDom, targetDom, delay, width, my, at, qtipclass) {
            var tDelay = 1;
            if (!checkUndefinedNull(delay)) {
                tDelay = delay;
            }
            var tWidth = 1000;
            if (!checkUndefinedNull(width)) {
                tWidth = width;
            }
            var tMy = 'top right';
            if (!checkUndefinedNull(my)) {
                tMy = my;
            }
            var tAt = 'bottom right';
            if (!checkUndefinedNull(at)) {
                tAt = at;
            }
            var tQtipClass = 'qtip-nmc-indicator-tooltip';
            if (!checkUndefinedNull(qtipclass)) {
                tQtipClass = qtipclass;
            }
    //console.log(elDom);                
    //console.log(targetDom);    
    //console.log($(elDom));        
    //console.log($(targetDom));        
            $timeout(function () {
//    console.log($(elDom));            
                $(elDom).qtip({
                    suppress: false,
                    content: {
                        text: text,
                        title: title,
                        button : false
                    },
                    show: {
                        event: 'click'
                    },
                    style: {
                        classes: tQtipClass,
                        width: tWidth,
                        "min-width": tWidth
                    },
                    hide: {
                        event: 'unfocus click'
                    },
                    position: {
                        my: tMy,
                        at: tAt,
                        target: $(targetDom)
                    }
                });
            }, tDelay);
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
        
        function getObjectKeys(obj) {
            return Object.keys(obj);
        }

        function initSvc() {
            loadContEventCategories();
            loadContEventTypes();                
        }

        initSvc();

        service.EVENTS = EVENTS;
        service.checkEmptyObject = checkEmptyObject;
        service.convertArrayToHash = convertArrayToHash;
        service.getContEventCategories = getContEventCategories;
        service.getContEventTypes = getContEventTypes;
        service.getObjectKeys = getObjectKeys;
        service.loadContObject = loadContObject;
        service.loadPTreeNodeStats = loadPTreeNodeStats;
        service.setToolTip = setToolTip;

        return service;
    }
    
    treeNotificationsDiagramServiceFn.$inject = ['$http', '$rootScope', '$timeout'];
    
    angular.module('treeNotificationsDiagramWidgetSvc', [])
        .service('treeNotificationsDiagramService', treeNotificationsDiagramServiceFn);
}());