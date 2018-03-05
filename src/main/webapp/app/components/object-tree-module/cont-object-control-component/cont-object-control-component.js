/*global angular, console, $, document*/
/***
    created by Artamonov A.A. , Dec. 2017
*/
(function () {
    'use strict';
    
    angular.module('objectTreeModule')
        .component('contObjectControlComponent', {
            bindings: {
                node: '<',
                contObjectList: '<'
            },
            templateUrl: "components/object-tree-module/cont-object-control-component/cont-object-control-component.html",
            controller: contObjectControlComponentController
        });
    
    contObjectControlComponentController.$inject = ['$scope', '$element', '$attrs', 'contObjectControlComponentService', '$stateParams', 'contObjectService', '$filter', '$timeout', '$mdDialog'];
    
    function contObjectControlComponentController($scope, $element, $attrs, contObjectControlComponentService, $stateParams, contObjectService, $filter, $timeout, $mdDialog) {
        /*jshint validthis: true*/
        var ctrl = this;
        ctrl.objects = [];        
        ctrl.objectsOnPage = [];
        ctrl.filter = '';
        ctrl.searchFlag = false;
        ctrl.contZpointFilterValue = null;
        
        var IMG_PATH_MONITOR_TEMPLATE = "components/object-tree-module/cont-object-control-component/object-state-",
            IMG_PATH_BUILDING_TYPE = "components/object-tree-module/cont-object-control-component/buildings/",
            IMG_PATH_WIDGETS = "components/object-tree-module/cont-object-control-component/",
            IMG_PATH_MODE_TEMPLATE = "images/object-mode-",
            IMG_EXT = ".png",
            OBJECTS_PER_PAGE = 100;
        
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
        
        function setImgFilter(column) {
            
        }
        
        var filterValues = [
            IMG_PATH_MONITOR_TEMPLATE + "red" + IMG_EXT,
            IMG_PATH_MONITOR_TEMPLATE + "yellow" + IMG_EXT,
            IMG_PATH_MONITOR_TEMPLATE + "green" + IMG_EXT,
        ];
        
        var contObjectTypeFilterValues = [];        
        
        ctrl.columns = [
            {
                name: "contObjectType",
                caption: "",
                headerClass: "col-xs-1 nmc-ctrl-cmp-td-for-type",
                type: "contObjectType",
                filterValues: contObjectTypeFilterValues
            },
            {
                name: "caption",
                caption: "Объект",
                headerClass: "col-xs-3",
                type: "text"
            }, {
                name: "heat",
                caption: "Тепло",
                imgPath: IMG_PATH_WIDGETS + "heat22" + IMG_EXT,
                headerClass: "col-xs-1",
                type: "img",
                filterValues: filterValues
            }, {
                name: "hw",
                caption: "ГВС",
                headerClass: "col-xs-1",
                imgPath: IMG_PATH_WIDGETS + "hw22" + IMG_EXT,
                type: "img",
                filterValues: filterValues
            }, {
                name: "cw",
                caption: "ХВС",
                headerClass: "col-xs-1",
                imgPath: IMG_PATH_WIDGETS + "cw22" + IMG_EXT,
                type: "img",
                filterValues: filterValues
            }, {
                name: "el",
                caption: "Э/эн",
                headerClass: "col-xs-1",
                imgPath: IMG_PATH_WIDGETS + "el22" + IMG_EXT,
                type: "img",
                filterValues: filterValues

            }

        ];
        
        ctrl.zpointWidgetList = {};
        
        ctrl.checkUndefinedNull = contObjectCtrlSvc.checkUndefinedNull;
        ctrl.checkEmptyObject = contObjectCtrlSvc.checkEmptyObject;
        ctrl.EVENTS = contObjectCtrlSvc.EVENTS;
        
        ctrl.orderBy = {field: 'caption', asc: false};
//console.log('ctrl.orderBy: ', ctrl.orderBy);
        
        ctrl.setOrderBy = function (field) {
            var asc = ctrl.orderBy.field === field ? !ctrl.orderBy.asc : true;
            ctrl.orderBy = { field: field, asc: asc };
            ctrl.filterObjects();
//console.log('ctrl.orderBy: ', ctrl.orderBy);
//console.log(ctrl.objects);            
        };
        
//        function closeAllObjectsInArr(objArr) {
//            objArr.forEach(function (obj) {
//                obj.showWidgetFlag = false;
//            });
//        }
//        
//                // search objects
//        ctrl.searchObjects = function (searchString) {
//            if ((ctrl.objects.length <= 0)) {
//                return;
//            }
//
//                //close all opened objects zpoints
//            closeAllObjectsInArr(ctrl.objectsOnPage);
//
//            var tempArr = [];
//            if (angular.isUndefined(searchString) || (searchString === '')) {
//                //                        
//                ctrl.objectCtrlSettings.objectsOnPage = ctrl.objectCtrlSettings.beginObjectsOnPage;//objectsPerScroll
//                tempArr =  ctrl.objects.slice(0, ctrl.objectCtrlSettings.beginObjectsOnPage);//objectsPerScroll
//            } else {
//                ctrl.objects.forEach(function (elem) {
//                    if (elem.fullName.toUpperCase().indexOf(searchString.toUpperCase()) != -1) {
//                        tempArr.push(elem);
//                    }
//                });
//            }
//            $scope.objectsOnPage = tempArr;
//        };
        
        function errorCallback(e) {
            console.error(e);
        }
        
        ctrl.addMoreObjectsOnPage = function () {
            var filterFlag = false;
            ctrl.columns.some(function (clmn) {
                if (clmn.setFilterFlag) {
                    filterFlag = true;
                    return true;
                }
            });
            if (filterFlag) {
                return false;
            }
            if (ctrl.objectsOnPage.length < ctrl.objects.length) {
                var addedObjects = ctrl.objects.slice(ctrl.objectsOnPage.length, ctrl.objectsOnPage.length + OBJECTS_PER_PAGE);
                ctrl.objectsOnPage = ctrl.objectsOnPage.concat(addedObjects);
                addedObjects.forEach(function (elm) {
                    contObjectCtrlSvc.loadContObjectMonitorState(elm.id)
                        .then(successLoadObjectMonitorStateCallback, errorCallback);
                });
            }
            
//console.log(ctrl.objectsOnPage);            
        };
        
        function successLoadObjectMonitorStateCallback(resp) {
            if (angular.isUndefined(resp) || resp === null || angular.isUndefined(resp.data) || resp.data === null) {
                return false;
            }
            var inpData = resp.data, //angular.copy(resp.data);
                tmpObjInfo = {};
//            if (!ctrl.checkUndefinedNull(inpData.contObjectShortInfo)) {
//                tmpObjInfo.caption = inpData.contObjectShortInfo.contObjectName || inpData.contObjectShortInfo.contObjectFullName || inpData.contObjectShortInfo.contObjectId;
//                tmpObjInfo.modeImgSrc = IMG_PATH_MODE_TEMPLATE + inpData.contObjectShortInfo.currentSettingMode.toLowerCase() + IMG_EXT;
//            }
            if (angular.isArray(inpData.contZPointMonitorState)) {
                var zpointsStates = {};
                inpData.contZPointMonitorState.forEach(function (zpoint) {
//                    tmpObjInfo[zpoint.contServiceTypeKeyname] = IMG_PATH_MONITOR_TEMPLATE + zpoint.stateColor.toLowerCase() + IMG_EXT;
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
            var changedObject = contObjectCtrlSvc.findContObjectById(ctrl.objects, inpData.contObjectShortInfo.contObjectId);            
            if (changedObject !== null) {
                contObjectCtrlSvc.updateContObjectInfo(changedObject, tmpObjInfo);
            }           
        }
        
        
//        function successLoadObjectsCallback(resp) {
//            var tmpBuf = angular.copy(resp.data);
//            if (!angular.isArray(tmpBuf)) {
//                return false;
//            }
//            tmpBuf.forEach(function (elm) {
//                if (elm.nodeType !== "CONT_OBJECT") {
//                    return false;
//                }
//                var obj = {
//                    id: elm.monitorObjectId,
//                    loading: true
//                };
//                ctrl.objects.push(obj);
//                contObjectCtrlSvc.loadContObjectMonitorState(obj.id)
//                    .then(successLoadObjectMonitorStateCallback, errorCallback);
//            });
//            
//            //save contobjects at service
//            var node = $stateParams.node;
//            contObjectCtrlSvc.setNodeData(node.id | node._id, ctrl.objects);
//        }
        
        //get objects with statuses
        ctrl.loadObjects = function (nodeId) {
            contObjectCtrlSvc.loadNodeObjects(nodeId);
//            contObjectCtrlSvc.loadNodeObjects(nodeId).then(successLoadObjectsCallback, errorCallback);
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
//console.log(resp);
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
        
        ctrl.ngPopupConfig = {
            title: "Информация по событиям",
            width: 810,
            height: 350,
            template: "<cont-zpoint-monitor-component cont-zpoint-id = \"0\" cont-zpoint-name = \"null\" cont-zpoint-type = \"null\"></cont-zpoint-monitor-component>",
            resizable: false,
            draggable: true,
            position: {top: 100, left: 300},
            isShow: false
        };
        
        ctrl.showObjectWidgetAtDialog = function (obj, index, contZpointFilterVal, ev) {
            ctrl.ngPopupConfig.isShow = true;
            
            
//            var dialogSettings = {
//                controller: null,
//                templateUrl: "components/object-tree-module/cont-object-control-component/info.tmpl.html",
//                parent: angular.element(document.body),
//                targetEvent: ev,
//                clickOutsideToClose: true
//            };
//            $mdDialog
//                .show(dialogSettings)
//                .then(function (ans) {
//                console.log(ans);
//            }, function () {
//                console.log("Dialog is canceled");
//            });
        };
        
        ctrl.showObjectWidget = function (obj, index, contZpointFilterVal) {            
            if (obj.hasOwnProperty('showWidgetFlag')) {
                obj.showWidgetFlag = !obj.showWidgetFlag;
            } else {
                obj.showWidgetFlag = true;
            }
            
            //load cont object zpoints
            if (obj.showWidgetFlag) {
                ctrl.contZpointFilterValue = contZpointFilterVal;
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
            contObjectCtrlSvc.setWidgetList(defaultWidgets);
        }
        
        ctrl.loadZpointWidgetList = function () {
            contObjectCtrlSvc.loadZpointWidgetList()
                .then(successLoadZpointWidgetListCallback, errorCallback);
        };
        
        ctrl.filterObjects = filterObjects;
        function filterObjects() {
            var filteredObjects = $filter('filter')(ctrl.objects, ctrl.filter);
            filteredObjects = $filter('orderBy')(filteredObjects, ctrl.orderBy.field, ctrl.orderBy.asc);            
            ctrl.objectsOnPage = filteredObjects;
//console.log(ctrl.filter);
//console.log(ctrl.objectsOnPage);            
            filteredObjects.forEach(function (elm) {
                if (elm.loading === true) {
                    contObjectCtrlSvc.loadContObjectMonitorState(elm.id)
                        .then(successLoadObjectMonitorStateCallback, errorCallback);
                }
            });            
        }
        
        $scope.$on('mainSearch:filtering', function (ev, args) {
            if (ctrl.checkUndefinedNull(args) || ctrl.checkUndefinedNull(args.filter)) {
                return false;
            }
            ctrl.filter = args.filter;
            ctrl.filterObjects();
        });
        
        function getContObjects() {
//console.log(ctrl.node);            
//console.log('getNodeContObjects:', getNodeContObjects);
//            var nodeObjects = $filter('orderBy')(nodeObjects, ctrl.orderBy.field, ctrl.orderBy.asc);
            
//console.log(ctrl.contObjectList);            
            ctrl.objects = $filter('orderBy')(ctrl.contObjectList, ctrl.orderBy.field, ctrl.orderBy.asc);                    
            ctrl.addMoreObjectsOnPage();
//console.log(ctrl.objects);            
return;
            var node = $stateParams.node;
            if (angular.isDefined(node) && node !== null) {
                var nodeId = node.id || node._id || node.nodeObject.id;
                var nodeObjects = contObjectCtrlSvc.getNodeData(nodeId);
//console.log(nodeObjects);
                if (nodeObjects === null) {
                    ctrl.loadObjects(nodeId);
                } else {
                    nodeObjects.forEach(function (elm) {
                        elm.loading = true;
                    });
                    nodeObjects = $filter('orderBy')(nodeObjects, ctrl.orderBy.field, ctrl.orderBy.asc);
                    ctrl.objects = nodeObjects;                    
console.log(ctrl.objects);                    
                    ctrl.addMoreObjectsOnPage();
//                    ctrl.objects.forEach(function (elm) {
//                        contObjectCtrlSvc.loadContObjectMonitorState(elm.id)
//                            .then(successLoadObjectMonitorStateCallback, errorCallback);
//                    });
                }
            }
        }
        
        ctrl.$onInit = function () {
//console.log(ctrl);
            getContObjects();
            ctrl.zpointWidgetList = contObjectCtrlSvc.getWidgetList();
            if (ctrl.checkUndefinedNull(ctrl.zpointWidgetList) || ctrl.checkEmptyObject(ctrl.zpointWidgetList)) {
                ctrl.loadZpointWidgetList();
            }
        };
        
        $scope.$on(contObjectCtrlSvc.EVENTS.OBJECTS_LOADED, function () {
            getContObjects();
//            var node = $stateParams.node;
//            var nodeId = node.id || node._id;
//            var nodeObjects = contObjectCtrlSvc.getNodeData(nodeId);
//            nodeObjects.forEach(function (elm) {
//                elm.loading = true;
//            });
//            ctrl.objects = nodeObjects;
//            ctrl.objects.forEach(function (elm) {
//                contObjectCtrlSvc.loadContObjectMonitorState(elm.id)
//                    .then(successLoadObjectMonitorStateCallback, errorCallback);
//            });
        });
        
//        ctrl.setFilterBy = function (column) {
//            var result = {},
//                tmp = {};
//            ctrl.objects.forEach(function (obj) {
//                tmp[obj[column.name]] = 1;
//            });
//            column.filterValues = Object.keys(tmp);
//console.log(column);
//        };
        
        ctrl.setColorFilter = function (column, colorPath) {
            //need column: name, colorPath
            
            var filteredObjects = []; //$filter('filter')(ctrl.objects, ctrl.filter);
            ctrl.objects.forEach(function (obj) {
                if (obj[column.name] === colorPath) {
                    filteredObjects.push(obj);
                }
            });
            ctrl.objectsOnPage = filteredObjects;
            ctrl.columns.forEach(function (clmn) {
                clmn.setFilterFlag = false;
            });
            column.setFilterFlag = true;
        };
        
        ctrl.removeColorFilter = function(column) {
            column.setFilterFlag = false;
            ctrl.objectsOnPage = [];
            ctrl.addMoreObjectsOnPage();
        };
        
        ctrl.toggleSearch = function () {
            ctrl.searchFlag = !ctrl.searchFlag;
            if (ctrl.searchFlag) {
                $timeout(function () {
                    $('#nmc-ctrl-cmp-toolbarheader-id').css({"width": "99.2%", "margin-left": "10px"});
                    $('#nmc-ctrl-cmp-search-btn-id').focus();
                }, 1);
            } else {
                ctrl.filter = '';
                ctrl.filterObjects();
                $timeout(function () {
                    $('#nmc-ctrl-cmp-toolbarheader-id').css({"width": "96.4%", "margin-left": "50px"});
                }, 1);
            }
        };
        
        ctrl.getIcon = function (ind) {
//            if (ind % 2 == 0) {
//                return "home-g.ico";
//            } else if (ind % 3 == 0) {
//                return "home-r.ico";
//            } else {
//                return "home-y.ico";
//            }
            var cls = "building24.png";
            var rn = ind % 11;
            switch (rn) {
                case 0:
                    cls = "building24.png";
                    break;
                case 1:
                    cls = "childhome24.png";
                    break;
                case 2:
                    cls = "hospital24.png";
                    break;
                case 3:
                    cls = "hotel26.png";
                    break;
                case 4:
                    cls = "mkd26.png";
                    break;
                case 5:
                    cls = "school24.png";
                    break;
                case 6:
                    cls = "gos26.png";
                    break;
                case 7:
                    cls = "prod32.png";
                    break;
                case 8:
                    cls = "cot16.png";
                    break;
                case 9:
                    cls = "dom26.png";
                    break;                
                default:
                    cls = "stadium24.png";
                    break;
            }
            return cls;
        };
        //fill contObjectTypeFilterValues
        for (var i = 0; i < 11; i++) {
            contObjectTypeFilterValues.push(IMG_PATH_BUILDING_TYPE + ctrl.getIcon(i));
        }
        
//        ctrl.getTextShadow = function (ind) {
//            var clr = "rgb(95, 95, 95)";
//            if (ind % 2 === 0) {
//                clr = "green";
//            } else if (ind % 3 === 0) {
//                clr = "#ff0000";
//            } else {
//                clr = "#ffc000";
//            }
//            return "-1px 0 clr, 0 1px clr, 1px 0 clr, 0 -1px clr".replace(/clr/g, clr);
//        };
        
//        ctrl.getIconCssClass = function (ind) {
//            var cls = "btn btn-xs iconcls pull-right";
//            var rn = ind % 6;//Math.ceil(Math.random()*10);
////console.log(rn);
//            switch (rn) {
//                case 0:
//                    cls = cls.replace("iconcls", "far fa-building");
//                    break;
//                case 1:
//                    cls = cls.replace("iconcls", "fas fa-home");
//                    break;
//                case 2:
//                    cls = cls.replace("iconcls", "far fa-hospital");
//                    break;
//                case 3:
//                    cls = cls.replace("iconcls", "fas fa-university");
//                    break;
//                case 4:
//                    cls = cls.replace("iconcls", "fab fa-fort-awesome");
//                    break;
//                case 5:
//                    cls = cls.replace("iconcls", "fas fa-industry");
//                    break;
//                default:
//                    cls = cls.replace("iconcls", "glyphicon glyphicon-question-sign");
//                    break;
//            }
//            return cls;
//        };
                
        
    }
    
}());