/*global angular, console*/
/***
    created by Artamonov A.A. , Dec. 2017
*/
(function () {
    'use strict';
    
    angular.module('objectTreeModule')
        .component('contObjectControlComponent', {
            bindings: {
                node: '<'
            },
            templateUrl: "components/object-tree-module/cont-object-control-component/cont-object-control-component.html",
            controller: contObjectControlComponentController
        });
    
    contObjectControlComponentController.$inject = ['$scope', '$element', '$attrs', 'contObjectControlComponentService', '$stateParams'];
    
    function contObjectControlComponentController($scope, $element, $attrs, contObjectControlComponentService, $stateParams) {
        /*jshint validthis: true*/
        var ctrl = this;
        ctrl.objects = [];
        
        var IMG_PATH_MONITOR_TEMPLATE = "components/object-tree-module/cont-object-control-component/object-state-",
            IMG_PATH_MODE_TEMPLATE = "images/object-mode-",
            IMG_EXT = ".png";
        
        var MONITOR_STATE_LVLS = {
            'green': {
                level: 0,
                color: 'green'
            },
            'yellow': {
                level: 1,
                color: 'yellow'
            },
            'red': {
                level: 2,
                color: 'red'
            }
        };
        
        var contObjectCtrlSvc = contObjectControlComponentService;
        
        ctrl.columns = [
            {
                name: "caption",
                caption: "Объект",
                headerClass: "col-xs-3"
            }, {
                name: "heat",
                caption: "Отопление",
                headerClass: "col-xs-1",
                type: "img"
            }, {
                name: "hw",
                caption: "ГВС",
                headerClass: "col-xs-1",
                type: "img"
            }, {
                name: "cw",
                caption: "ХВС",
                headerClass: "col-xs-1",
                type: "img"
            }, {
                name: "el",
                caption: "Электричество",
                headerClass: "col-xs-1",
                type: "img"

            }

        ];
        
        ctrl.zpointWidgetList = {};
        
        ctrl.checkUndefinedNull = contObjectCtrlSvc.checkUndefinedNull;
        ctrl.EVENTS = contObjectCtrlSvc.EVENTS;
        
        function errorCallback(e) {
            console.error(e);
        }
        
        function successLoadObjectMonitorStateCallback(resp) {
            if (angular.isUndefined(resp) || resp === null || angular.isUndefined(resp.data) || resp.data === null) {
                return false;
            }
            var inpData = resp.data, //angular.copy(resp.data);
                tmpObjInfo = {};
            if (!ctrl.checkUndefinedNull(inpData.contObjectShortInfo)) {
                tmpObjInfo.caption = inpData.contObjectShortInfo.contObjectName || inpData.contObjectShortInfo.contObjectFullName || inpData.contObjectShortInfo.contObjectId;
                tmpObjInfo.modeImgSrc = IMG_PATH_MODE_TEMPLATE + inpData.contObjectShortInfo.currentSettingMode.toLowerCase() + IMG_EXT;
            }
            if (angular.isArray(inpData.contZPointMonitorState)) {
                var zpointsStates = {};
                inpData.contZPointMonitorState.forEach(function (zpoint) {
                    if (zpointsStates.hasOwnProperty(zpoint.contServiceTypeKeyname)) {
                        if (zpointsStates[zpoint.contServiceTypeKeyname].level < MONITOR_STATE_LVLS[zpoint.stateColor.toLowerCase()].level) {

                            zpointsStates[zpoint.contServiceTypeKeyname] = angular.copy(MONITOR_STATE_LVLS[zpoint.stateColor.toLowerCase()]);
                        }
                    } else {
                        zpointsStates[zpoint.contServiceTypeKeyname] = angular.copy(MONITOR_STATE_LVLS[zpoint.stateColor.toLowerCase()]);
                    }
                });
                
                for (var kkey in zpointsStates) {
                    if (zpointsStates.hasOwnProperty(kkey)) {
                        tmpObjInfo[kkey] = IMG_PATH_MONITOR_TEMPLATE + zpointsStates[kkey].color + IMG_EXT;
                    }
                }
            }
            ctrl.objects.some(function (obj) {
                if (inpData.contObjectShortInfo.contObjectId === obj.id) {
                    for (var k in tmpObjInfo) {
                        obj[k] = tmpObjInfo[k];
                    }
                    obj.loading = false;
                    return true;
                }
            });
            
        }
        
        
        function successLoadObjectsCallback(resp) {
            var tmpBuf = angular.copy(resp.data);
            if (!angular.isArray(tmpBuf)) {
                return false;
            }
            tmpBuf.forEach(function (elm) {
                if (elm.nodeType !== "CONT_OBJECT") {
                    return false;
                }
                var obj = {
                    id: elm.monitorObjectId,
                    loading: true
                };
                ctrl.objects.push(obj);
                contObjectCtrlSvc.loadContObjectMonitorState(obj.id)
                    .then(successLoadObjectMonitorStateCallback, errorCallback);
            });            
        }
        
        //get objects with statuses
        ctrl.loadObjects = function (nodeId) {
            contObjectCtrlSvc.loadNodeObjects(nodeId).then(successLoadObjectsCallback, errorCallback);
        };
        
        function performZpointData(inputZpoints) {
            if (!angular.isArray(inputZpoints)) {
                return null;
            }
            var zpWidgetOptions;
            var preparedZpoints = angular.copy(inputZpoints);
            preparedZpoints.forEach(function (zp) {
                zpWidgetOptions = {};
                zpWidgetOptions.type = ctrl.zpointWidgetList[zp.contServiceTypeKeyname];
                zpWidgetOptions.zpointName = zp.customServiceName;
                zpWidgetOptions.contZpointId = zp.id;
                if (!ctrl.checkUndefinedNull(zp.deviceObject)) { 
                    zpWidgetOptions.contObjectFullName = zp.deviceObject.contObjectFullName;
                    zpWidgetOptions.isImpulse = zp.deviceObject.isImpulse;
                    zpWidgetOptions.zpointNumber = zp.deviceObject.number;
                    if (!ctrl.checkUndefinedNull(zp.deviceObject.deviceModel)) { 
                        zpWidgetOptions.zpointModel = zp.deviceObject.deviceModel.modelName;
                    } else {
                        zpWidgetOptions.zpointModel = "Не задано";
                    }
                }
                zpWidgetOptions.contObjectId = zp.contObjectId;
                zpWidgetOptions.isManualLoading = zp.isManualLoading;                
                
                zp.widgetOptions = zpWidgetOptions;
            });
            
            return preparedZpoints;
        }
        
        function successLoadZpointsCallback(resp) {
            console.log(resp);
            if (angular.isUndefined(resp) || resp === null || !angular.isArray(resp.data) || resp.data.length === 0) {
                return false;
            }
            //find object
            var contObjectId = resp.data[0].contObjectId;
            ctrl.objects.some(function (obj) {
                if (obj.id === contObjectId) {                    
                    obj.zpoints = performZpointData(resp.data);
                    return true;
                }
            });            
        }
        
        ctrl.loadZpointsByObjectId = function (objId) {
            contObjectCtrlSvc.loadZpointsByObjectId(objId).then(successLoadZpointsCallback, errorCallback);
        };
        
        function scrollTableElementToTop(index) {
            $scope.$broadcast(ctrl.EVENTS.OBJECT_CLICK, {index: index});
        }
        
        ctrl.showObjectWidget = function (obj, index) {            
            if (obj.hasOwnProperty('showWidgetFlag')) {
                obj.showWidgetFlag = !obj.showWidgetFlag;
            } else {
                obj.showWidgetFlag = true;
            }
            
            //load cont object zpoints
            if (obj.showWidgetFlag) {
                ctrl.loadZpointsByObjectId(obj.id);
                // close all objects besides current
                ctrl.objects.forEach(function (elmObj) {
                    if (elmObj.id != obj.id) {
                        elmObj.showWidgetFlag = false;
                    }
                });
                scrollTableElementToTop(index);
            }
        };
        
        function successLoadZpointWidgetListCallback(resp) {
            var widgetList = [], wkey, defaultWidgets = {};
            if (!angular.isArray(resp.data)) {
                return false;
            }
            resp.data.forEach(function (elm) {
                if (!angular.isArray(widgetList[elm.contServiceType])) {
                    widgetList[elm.contServiceType] = [];
                }
                widgetList[elm.contServiceType].push(elm);
            });

            for (wkey in widgetList) {
                if (widgetList[wkey].length > 0) {
                    defaultWidgets[wkey] = widgetList[wkey][0].widgetName;
                    for (var j = 0; j < widgetList[wkey].length; j++) {
                        var elm = widgetList[wkey][j];
                        if (elm.isDefault === true) {
                            defaultWidgets[wkey] = elm.widgetName;
                            break;
                        }
                    }
                }
            }
            ctrl.zpointWidgetList = defaultWidgets;
        }
        
        ctrl.loadZpointWidgetList = function () {
            contObjectCtrlSvc.loadZpointWidgetList()
                .then(successLoadZpointWidgetListCallback, errorCallback);
        };
        
        ctrl.$onInit = function () {
//            console.log($stateParams);
            var node = $stateParams.node;
            if (angular.isDefined(node) && node !== null) {
                ctrl.loadObjects(node.id || node._id);
            }
            ctrl.loadZpointWidgetList();
        };
    }
    
}());