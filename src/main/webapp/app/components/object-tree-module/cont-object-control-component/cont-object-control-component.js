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
            IMG_PATH_MODE_TEMPLATE = "components/object-tree-module/cont-object-control-component/object-mode-winter",
            IMG_EXT = ".png";
        
        var contObjectCtrlSvc = contObjectControlComponentService;
        
        ctrl.columns = [
            {
                name: "caption",
                caption: "Объект",
                headerClass: "col-xs-3"
            }, {
                name: "heat",
                caption: "СО",
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
                caption: "Эл-во",
                headerClass: "col-xs-1",
                type: "img"

            }

        ];
        
        function errorCallback(e) {
            console.error(e);
        }
        
        function successLoadObjectMonitorStateCallback(resp) {
            if (angular.isUndefined(resp) || resp === null || angular.isUndefined(resp.data) || resp.data === null) {
                return false;
            }
            var inpData = resp.data, //angular.copy(resp.data);
                tmpObjInfo = {};
            tmpObjInfo.caption = inpData.contObjectShortInfo.contObjectName || inpData.contObjectShortInfo.contObjectFullName || inpData.contObjectShortInfo.contObjectId;
            if (angular.isArray(inpData.contZPointMonitorState)) {
                inpData.contZPointMonitorState.forEach(function (zpoint) {
                    tmpObjInfo[zpoint.contServiceTypeKeyname] = IMG_PATH_MONITOR_TEMPLATE + zpoint.stateColor.toLowerCase() + IMG_EXT;                    
                });
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
//            ctrl.objects.push(tmpObjInfo);
            
        }
        
        
        function successLoadObjectsCallback(resp) {
//            console.log(resp);
            var tmpBuf = angular.copy(resp.data);
            //test
//            var testElm = angular.copy(resp.data[0]);
//            testElm.colorKey = "YELLOW";
//            tmpBuf.push(testElm);
            //end test
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
//                elm.imgpath = IMG_PATH_MONITOR_TEMPLATE + elm.colorKey.toLowerCase() + IMG_EXT;
//                elm.modepath = IMG_PATH_MODE_TEMPLATE + IMG_EXT;
            });            
//            ctrl.objects = tmpBuf;
        }
        
        //get objects with statuses
        ctrl.loadObjects = function (nodeId) {
            contObjectCtrlSvc.loadNodeObjects(nodeId).then(successLoadObjectsCallback, errorCallback);
        };
        
        ctrl.$onInit = function () {
//            console.log($stateParams);
            var node = $stateParams.node;
            if (angular.isDefined(node) && node !== null) {
                ctrl.loadObjects(node.id || node._id);
            }
        };
    }
    
}());