/*global angular, console*/
/***
    created by Artamonov A.A. , Dec. 2017
*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .service('contObjectControlComponentService', Service);

    Service.$inject = ['$http', 'objectTreeService', '$rootScope', 'contObjectService', 'contObjectBuildingService'];

    /* @ngInject */
    function Service($http, objectTreeService, $rootScope, contObjectService, contObjectBuildingService) {
        
        var P_TREE_NODE_URL = "../api/p-tree-node",
            P_TREE_NODE_MONITOR_URL = "../api/p-tree-node-monitor/all-linked-objects",            
            OBJECT_URL = "../api/subscr/contEvent/notifications/contObject",
            SUBSCR_TREES_URL = '../api/subscr/subscrObjectTree/contObjectTreeType1',
            CONT_OBJECT_MONITOR_STATE_URL = '../api/widgets/cont-event-monitor/cont-objects/{contObjectId}/monitor-state',
            SUBSCR_CONT_OBJECTS_URL = '../api/subscr/contObjects',
            SUBSCR_CONT_ZPOINTS_URL = '../api/subscr/contObjects/{contObjectId}/contZPoints/vo',
            WIDGETS_URL = "../api/subscr/vcookie/widgets/list",
            REFRESH_PERIOD = 600000,
            TREE_ID_FIELD_NAME = "_id",
            NODE_ID_FIELD_NAME = "_id",
            SUBSCR_OBJECT_TREE_CONT_OBJECTS = "SUBSCR_OBJECT_TREE_CONT_OBJECTS",        
            IMG_PATH_MODE_TEMPLATE = "images/object-mode-",
            IMG_EXT = ".png";
        
        var CONT_OBJECTS_PER_PAGE = 100;
        
        var EVENTS = {};
        EVENTS.OBJECT_CLICK = 'contObjectControlComponentService:objectClick';
        EVENTS.OBJECTS_LOADED = 'contObjectControlComponentService:objectsLoaded';
        //struct for keep nodes objects
//        nodes = {
//            "node1": [objects1],
//            "node2": [objects2],
//        }
        var nodes = {},
            widgetList = {},
            currentNodeId = null;
        
        var svc = this;
        svc.EVENTS = EVENTS;
        svc.CONT_OBJECTS_PER_PAGE = CONT_OBJECTS_PER_PAGE;
        svc.checkUndefinedNull = checkUndefinedNull;
        svc.checkEmptyObject = checkEmptyObject;
        svc.getNodeData = getNodeData;
        svc.getWidgetList = getWidgetList;
        svc.findContObjectById = findContObjectById;
        svc.loadContObjectMonitorState = loadContObjectMonitorState;
        svc.loadNodeObjects = loadNodeObjects;
        svc.loadZpointsByObjectId = loadZpointsByObjectId;
        svc.loadZpointWidgetList = loadZpointWidgetList;
        svc.setNodeData = setNodeData;
        svc.setWidgetList = setWidgetList;
        svc.updateContObject = updateContObject;
        svc.updateContObjectInfo = updateContObjectInfo;

        ////////////////
        
        function setCurrentNodeId(nId) {
            currentNodeId = nId;
        }
        
        function getCurrentNodeId() {
            return currentNodeId;
        }
        
        function checkUndefinedNull(inpObj) {
            var result = false;
            if ((angular.isUndefined(inpObj)) || (inpObj === null)) {
                result = true;
            }
            return result;
        }
        
        function checkEmptyObject(obj) {
                return Object.keys(obj).length === 0 && obj.constructor === Object;
        }
        
        function addParamToURL(url, paramName, paramValue) {
            if (checkUndefinedNull(url) || checkUndefinedNull(paramName) || checkUndefinedNull(paramValue)) {
                return url;
            }
            url += ((url.indexOf('?') === -1) ? '?' : '&') + encodeURIComponent(paramName) + "=" + encodeURIComponent(paramValue);
            return url;

        }
        
        function getNodeData(nodeId) {
            if (checkUndefinedNull(nodeId) || !nodes.hasOwnProperty(nodeId)) {
                return null;
            }
            return angular.copy(nodes[nodeId]);
        }
        
        function setNodeData(nodeId, nodeData) {
            if (checkUndefinedNull(nodeId) || checkUndefinedNull(nodeData)) {
                return false;
            }
            nodes[nodeId] = angular.copy(nodeData);
            return true;
        }
        
        $rootScope.$on(objectTreeService.EVENTS.TREE_STUB_LOADED, function () {
            loadNodeObjects(getCurrentNodeId());
        });

        function loadNodeObjects(nodeId) {
            var treeStub = objectTreeService.getCurrentTreeStub();
//console.log(treeStub);
            if (treeStub === null) {
                // request to load                
                return false;
            }
            var contObjects = null; // objectTreeService.findContObjectsByNodeId(nodeId);
            if (nodeId === treeStub._id) {
                contObjects = objectTreeService.findContObjectsByFullTree(nodeId);
            } else {
                contObjects = objectTreeService.findContObjectsByNodeId(nodeId);
            }            
            
            var tmpContObjects = angular.copy(contObjects),
                resultContObjects = [];
            tmpContObjects.forEach(function (tco) {
                var obj = {
                    id: tco.nodeObject.id,
                    loading: true
                };
                resultContObjects.push(obj);
            });
            if (contObjectService.getShortinfoLoadedFlag()) {
                resultContObjects.forEach(function (contObject) {
                    var objShortInfo = contObjectService.getContObjectShortInfoById(contObject.id);
                    if (angular.isUndefined(objShortInfo) || objShortInfo === null) {
                        return false;
                    }
                    contObject.caption = objShortInfo.contObjectName || objShortInfo.contObjectFullName || objShortInfo.id;
                    contObject.modeImgSrc = IMG_PATH_MODE_TEMPLATE + objShortInfo.currentSettingMode.toLowerCase() + IMG_EXT;
                    contObject.buildingTypeCategory = objShortInfo.buildingTypeCategory;
                    contObject.buildingTypeCategoryIconPath = contObjectBuildingService.getBuildingTypeCategoryIcon24(contObject.buildingTypeCategory);
                });
            }
            nodes[nodeId] = resultContObjects;
            $rootScope.$broadcast(EVENTS.OBJECTS_LOADED);
//console.log(resultContObjects);
            
            // first old version
//            var url = P_TREE_NODE_MONITOR_URL;
//            url = addParamToURL(url, "nodeId", nodeId);
//            return $http.get(url);
        }
        
        function loadContObjectMonitorState(contObjectId) {
            var url = CONT_OBJECT_MONITOR_STATE_URL.replace('{contObjectId}', contObjectId);
            return $http.get(url);
        }
        
        function loadZpointsByObjectId(contObjectId) {            
            var url = SUBSCR_CONT_ZPOINTS_URL.replace('{contObjectId}', contObjectId);
//            var url = SUBSCR_CONT_OBJECTS_URL + "/" + obj.id + "/contZPoints" + mode;//Ex";
//            if (isCancelParamsIncorrect() === true) {
//                return null;
//            }
//            return $http.get(table, httpOptions);
            return $http.get(url);
        }
        
        function loadZpointWidgetList() {
            var url = WIDGETS_URL;
            return $http.get(url);
        }
        
        function getWidgetList() {
            return widgetList;
        }
        
        function setWidgetList(wList) {
            widgetList = wList;
        }
        
        function updateContObject(contObjectsArr, objId, newObjInfo) {
            contObjectsArr.some(function (obj) {
                if (objId === obj.id) {
                    for (var k in newObjInfo) {
                        obj[k] = newObjInfo[k];
                    }
                    obj.loading = false;
                    return true;
                }
            });
        }
        
        function updateContObjectInfo(contObject, newObjInfo) {
            for (var k in newObjInfo) {
                contObject[k] = newObjInfo[k];
            }
            contObject.loading = false;
        }
        
        function findContObjectById(contObjectsArr, objId) {
            var result = null;
            contObjectsArr.some(function (obj) {
                if (objId === obj.id) {
                    result = obj;
                    return true;
                }
            });
            return result;
        }
    }
})();