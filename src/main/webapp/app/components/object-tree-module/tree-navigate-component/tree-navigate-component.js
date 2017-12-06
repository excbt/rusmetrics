/*global angular, console*/
(function () {
    'use strict';
    
    angular.module('objectTreeModule')
        .component('treeNavigateComponent', {
            templateUrl: "components/object-tree-module/tree-navigate-component/tree-navigate-component.html",
            controller: treeNavigateComponentController
        });
    
    treeNavigateComponentController.$inject = ['$scope', '$element', '$attrs', 'treeNavigateComponentService', '$cookies', '$timeout', '$rootScope', 'notificationFactory', '$http'];
    
    function treeNavigateComponentController($scope, $element, $attrs, treeNavigateComponentService, $cookies, $timeout, $rootScope, notificationFactory, $http) {
        /*jshint validthis: true*/
        var ctrl = this;
        
        var IMG_PATH_MONITOR_TEMPLATE = "images/object-state-",
            IMG_EXT = ".png",
            PTREE_DEPTH_LEVEL = 0,
            VCOOKIE_URL = "../api/subscr/vcookie",
            USER_VCOOKIE_URL = "../api/subscr/vcookie/user",
            OBJECT_INDICATOR_PREFERENCES_VC_MODE = "OBJECT_INDICATOR_PREFERENCES",
            WIDGETS_URL = "../api/subscr/vcookie/widgets/list";
        
        var treeNavSvc = treeNavigateComponentService;
        
//        var mainSvc = {};
        var checkUndefinedNull = treeNavSvc.checkUndefinedNull;        
        
        ctrl.data = {};
        ctrl.objectCtrlSettings = {};
        ctrl.messages = {
            treeMenuHeader: "Wazzzuuuup"
        };
        
        var thisdata = {};
        thisdata.deviceModels = [];
        thisdata.contServiceTypes = {};
        
        var errorCallback = function (e) {
            ctrl.treeLoading = false;
            ctrl.objectCtrlSettings.isPassportsLoading = false;
            ctrl.objectCtrlSettings.deviceModelsLoading = false;
            var errorObj = [];//mainSvc.errorCallbackHandler(e);
            errorObj.caption = e.status;
            errorObj.description = e.data;
            notificationFactory.errorInfo(errorObj.caption, errorObj.description);
        };
        
        ctrl.isChevronRight = treeNavSvc.isChevronRight;
        ctrl.isChevronDown = treeNavSvc.isChevronDown;
        ctrl.isChevronDisabled = treeNavSvc.isChevronDisabled;
        
        ctrl.isContObjectNode = treeNavSvc.isContObjectNode;
        ctrl.isContZpointNode = treeNavSvc.isContZpointNode;
        ctrl.isDeviceNode = treeNavSvc.isDeviceNode;
        ctrl.isElementNode = treeNavSvc.isElementNode;

 // ********************************************************************************************
    //  TREEVIEW
    //*********************************************************************************************
        ctrl.objectCtrlSettings.isTreeView = true;//(true && !mainSvc.isCabinet());
        ctrl.objectCtrlSettings.isFullObjectView = false;

        ctrl.data.currentTree = {};
        ctrl.data.newTree = {};
        ctrl.data.defaultTree = null;// default tree

        ctrl.data.trees = [];

        ctrl.loadTree = function (tree, objId) {
            ctrl.loading = true;
            ctrl.treeLoading = true;

            var tmpPTree = treeNavSvc.getPTree();
            if (checkUndefinedNull(tmpPTree) || tmpPTree._id !== tree.id) {
                loadPTree(tree.id, PTREE_DEPTH_LEVEL);
            } else {
                getPTree();
            }
            loadPTreeMonitorWithStartRefresh(tree.id);
        };

        var loadTrees = function (treeSetting) {
            ctrl.treeLoading = true;
            treeNavSvc.loadSubscrTrees().then(function (resp) {
                ctrl.treeLoading = false;
//                mainSvc.sortItemsBy(resp.data, "objectName");
                ctrl.data.trees = angular.copy(resp.data);
                if (angular.isDefined($cookies.loadedPTreeId)) {
                    ctrl.data.defaultTree = treeNavSvc.findItemBy(ctrl.data.trees, "id", Number($cookies.loadedPTreeId));
                }
                if (ctrl.data.defaultTree === null && !checkUndefinedNull(treeSetting) && (treeSetting.isActive === true)) {
                    ctrl.data.defaultTree = treeNavSvc.findItemBy(ctrl.data.trees, "id", Number(treeSetting.value));
                }
                if (!angular.isArray(ctrl.data.trees) || ctrl.data.trees.length <= 0 || checkUndefinedNull(ctrl.data.defaultTree)) {
                    ctrl.viewFullObjectList();
                    return "View full object list!";
                }
//                monitorSvc.setMonitorSettings({currentTree: ctrl.data.defaultTree, curTreeId: ctrl.data.defaultTree.id});
//                        $rootScope.$broadcast('monitor:updateObjectsRequest');
                ctrl.loadTree(ctrl.data.defaultTree);
            }, errorCallback);
        };

        var successLoadTreeSetting = function (resp) {
            ctrl.objectCtrlSettings.isTreeView = resp.data.isActive;
            loadTrees(resp.data);
        };

        function checkTreeSettingsAndGetObjectsData() {
//console.log("checkTreeSettingsAndGetObjectsData");
            //if tree is off
            if (ctrl.objectCtrlSettings.isTreeView === false) {
                //load data
//                getObjectsData();
                //monitor map data load start
//                        monitorSvc.setMonitorSettings({curTreeId: null, curTreeNodeId: null, isFullObjectView: true});
//                        monitorSvc.setMonitorSettings({currentTree: null, currentTreeNode: null});
//                        $rootScope.$broadcast('monitor:updateObjectsRequest');
            } else {
            //if tree is on
                ctrl.data.currentGroupId = null;
//                monitorSvc.setMonitorSettings({contGroupId: null});
                treeNavSvc.loadDefaultTreeSetting().then(successLoadTreeSetting, errorCallback);
            }
        }

        ctrl.toggleTreeView = function () {
            ctrl.objectCtrlSettings.isTreeView = !ctrl.objectCtrlSettings.isTreeView;
            checkTreeSettingsAndGetObjectsData();
        };

    // ********************************************************************************************
    //  END TREEVIEW
    //*********************************************************************************************
        
// *******************************************************************************************
//          Upgrade Tree Interface
//********************************************************************************************

        ctrl.data.selectedPNode = null; // текущий выбранный узел дерева, виджет которого отображается в инфо панели
        ctrl.data.currentPTreeMonitor = {}; // monitor statuses for current tree;
        ctrl.data.currentPTreeMonitorDefault = IMG_PATH_MONITOR_TEMPLATE + "green" + IMG_EXT;

        var selectedPNodes = []; // массив выбранных через ctrl/shift узлов дерева

        ctrl.data.chartModes = ['CRITICALS', 'CATEGORIES', 'TYPES'];
        ctrl.data.chartClass = 'col-xs-' + 12 / ctrl.data.chartModes.length + ' noPadding'; // 12 - col count in grid

        function isChildNodesEmpty(item) {
            return checkUndefinedNull(item) || checkUndefinedNull(item.childNodes) || !angular.isArray(item.childNodes) || item.childNodes.length === 0;
        }

        function isLinkedObjectNodesEmpty(item) {
            return checkUndefinedNull(item) || checkUndefinedNull(item.linkedNodeObjects) || !angular.isArray(item.linkedNodeObjects) || item.linkedNodeObjects.length === 0;
        }

        function successLoadPTreeCallback(resp) {
            console.log(resp);
            console.log(resp.data);

            ctrl.data.currentPTree = resp.data;
            ctrl.data.currentPTreeWrapper = [ctrl.data.currentPTree];
            treeNavSvc.setPTree(ctrl.data.currentPTree);
            $cookies.loadedPTreeId = ctrl.data.currentPTree._id;

            ctrl.loading = false;
            ctrl.treeLoading = false;

            ctrl.messages.treeMenuHeader = resp.data.nodeName || resp.data._id;

            ctrl.objects = [];
            ctrl.objectsOnPage = [];

//            monitorSvc.setMonitorSettings({isFullObjectView: false});
//            monitorSvc.setMonitorSettings({currentTreeNode: null, curTreeNodeId: null});


            ctrl.messages.noObjects = "";
            $timeout(function () {
                setEventsForCurrentPTree(ctrl.data.currentPTree);
            }, 1000);
        }

        function loadPTreeMonitorWithStartRefresh(treeId, depthLvl) {
            $rootScope.$broadcast(treeNavSvc.BROADCASTS.requestPTreeMonitorLoading, {subscrObjectTreeId: treeId, childLevel: depthLvl});
        }
        
        var setEventsForObject = treeNavSvc.setEventsForObject;

        function setEventsForPTreeNode(node, nodeFn) {
            if (ctrl.isElementNode(node)) {
                setEventsForObject(node._id);
            } else {
                setEventsForObject(node.nodeObject.id);
            }
            if (angular.isDefined(nodeFn)) {
                nodeFn(node);
            }
        }

        function setEventsForCurrentPTree(curPTree) {
            if (angular.isArray(curPTree.childNodes)) {
                curPTree.childNodes.forEach(function (cn) {
                    setEventsForPTreeNode(cn, setEventsForCurrentPTree);
                });
            }
            if (angular.isArray(curPTree.linkedNodeObjects)) {
                curPTree.linkedNodeObjects.forEach(function (ln) {
                    setEventsForPTreeNode(ln, setEventsForCurrentPTree);
                });
            }
            return true;
        }

        function getPTree() {
//                    console.log("getPTree");
            ctrl.data.currentPTree = treeNavSvc.getPTree();
            ctrl.data.currentPTreeWrapper = [ctrl.data.currentPTree];
            ctrl.messages.treeMenuHeader = ctrl.data.currentPTree.nodeName || ctrl.data.currentPTree._id;
            ctrl.loading = false;
            ctrl.treeLoading = false;
            setEventsForCurrentPTree(ctrl.data.currentPTree);
        }

        $scope.$on(treeNavSvc.BROADCASTS.pTreeLoaded, function () {
            getPTree();
        });

        function getPTreeMonitor() {
            var monitorData = treeNavSvc.getPTreeMonitor();
            if (checkUndefinedNull(monitorData)) {
                return false;
            }
            var monitor = {};
            monitorData.forEach(function (md) {
                monitor[md.nodeType + md.monitorObjectId] = IMG_PATH_MONITOR_TEMPLATE + md.colorKey.toLowerCase() + IMG_EXT;
                setEventsForObject(md.monitorObjectId);
            });

            ctrl.data.currentPTreeMonitor = monitor;

        }

        $scope.$on(treeNavSvc.BROADCASTS.pTreeMonitorLoaded, function () {
            getPTreeMonitor();
        });

        function loadPTree(treeId, depthLvl) {
            ctrl.loading = true;
            ctrl.treeLoading = true;
            treeNavSvc.loadPTreeNode(treeId, depthLvl)
                .then(successLoadPTreeCallback, errorCallback);
        }

        function successLoadPTreeNodeCallback(resp, PTnode) {
            if (checkUndefinedNull(resp) || checkUndefinedNull(resp.data)) {
                return false;
            }
            if (!checkUndefinedNull(resp.data.linkedNodeObjects)) {
                PTnode.linkedNodeObjects = resp.data.linkedNodeObjects;
            }
            if (!checkUndefinedNull(resp.data.childNodes)) {
                PTnode.childNodes = resp.data.childNodes;
            }
        }

        function loadPTreeNode(pTreeNode, depthLvl) {
            if (pTreeNode.loading === true) {
                return;
            }
            pTreeNode.loading = true;
            treeNavSvc.loadPTreeNode(pTreeNode.id || pTreeNode._id, depthLvl)
                .then(function (resp) {
                    pTreeNode.loading = false;
                    successLoadPTreeNodeCallback(resp, pTreeNode);
                }, function (err) {
                    pTreeNode.loading = false;
                    errorCallback(err);
                });
        }

        function isLazyNode(item) {
            return item.lazyNode;
        }

        ctrl.isElementNode = function (item) {
            if (checkUndefinedNull(item)) {
                return false;
            }
            return item.nodeType === 'ELEMENT';
        };
        ctrl.isContObjectNode = function (item) {
            if (checkUndefinedNull(item)) {
                return false;
            }
            return item.nodeType === 'CONT_OBJECT';
        };
        ctrl.isContZpointNode = function (item) {
            if (checkUndefinedNull(item)) {
                return false;
            }
            return item.nodeType === 'CONT_ZPOINT';
        };
        ctrl.isDeviceNode = function (item) {
            if (checkUndefinedNull(item)) {
                return false;
            }
            return item.nodeType === 'DEVICE_OBJECT';
        };

        ctrl.isChevronRight = function (collapsed, item) {
            if (checkUndefinedNull(item) || (checkUndefinedNull(item.childNodes) && checkUndefinedNull(item.linkedNodeObjects))) {
                return false;
            }
            return collapsed && ((!checkUndefinedNull(item.childNodes) && item.childNodes.length > 0) || (!checkUndefinedNull(item.linkedNodeObjects) && item.linkedNodeObjects.length > 0));
        };
        ctrl.isChevronDown = function (collapsed, item) {
            if (checkUndefinedNull(item) || (checkUndefinedNull(item.childNodes) && checkUndefinedNull(item.linkedNodeObjects))) {
                return false;
            }
            return !collapsed && ((!checkUndefinedNull(item.childNodes) && item.childNodes.length > 0) || (!checkUndefinedNull(item.linkedNodeObjects) && item.linkedNodeObjects.length > 0));
        };

        ctrl.isChevronDisabled = function (collapsed, item) {
            if (checkUndefinedNull(item) || (checkUndefinedNull(item.childNodes) && checkUndefinedNull(item.linkedNodeObjects))) {
                return true;
            }
            return !((!checkUndefinedNull(item.childNodes) && item.childNodes.length > 0) || (!checkUndefinedNull(item.linkedNodeObjects) && item.linkedNodeObjects.length > 0));
        };

        function findNodeInPTree(node, tree) {
            return treeNavSvc.findNodeInPTree(node, tree);
        }
        
            // ********************************************************************************************
                    //  Load widget settings - Need it here ???
    //*********************************************************************************************

        function loadModePrefs(indicatorModeKeyname, contObject) {
            ctrl.indicatorModes = [];
            ctrl.currentIndicatorMode = {};
            if (checkUndefinedNull(VCOOKIE_URL) || checkUndefinedNull(OBJECT_INDICATOR_PREFERENCES_VC_MODE)) {
                console.log("Request required params is null!");
                $scope.$broadcast("objectList:loadedModePrefs", {contObject: contObject});
                return false;
            }
    //        var url = VCOOKIE_URL + "?vcMode=" + OBJECT_INDICATOR_PREFERENCES_VC_MODE + "&vcKey=" + indicatorModeKeyname;
            var url = VCOOKIE_URL + "?vcMode=" + OBJECT_INDICATOR_PREFERENCES_VC_MODE;
            $http.get(url).then(function (resp) {
                var vcvalue;
                if (checkUndefinedNull(resp) || checkUndefinedNull(resp.data) || !angular.isArray(resp.data) || resp.data.length === 0) {
                    console.log("objectList: incorrect mode preferences!");
                    $scope.$broadcast("objectList:loadedModePrefs", {contObject: contObject});
                    return false;
                }

                var tmpRespData = angular.copy(resp.data);
                // prepared indicator mods
                tmpRespData.forEach(function (imode) {
                    if (imode.vcValue === null) {
                        return false;
                    }
                    vcvalue = JSON.parse(imode.vcValue);
                    imode.caption = vcvalue.caption;
                    imode.vv = vcvalue;
                    ctrl.indicatorModes.push(imode);

                });
                //find default indicator mode;
                ctrl.currentIndicatorMode = null;
                if (!checkUndefinedNull(indicatorModeKeyname)) {
                    ctrl.indicatorModes.some(function (imode) {
                        if (imode.vcKey === indicatorModeKeyname) {
                            ctrl.currentIndicatorMode = imode;
                            if (!checkUndefinedNull(imode.vv.widgets)) {
                                contObject.widgets = imode.vv.widgets;
                            }
                            return true;
                        }

                    });
                }

                if (checkUndefinedNull(ctrl.currentIndicatorMode)) {
                    console.log("Current view mode is undefined or null!");
                }

                $scope.$broadcast("objectList:loadedModePrefs", {contObject: contObject});

            }, errorCallback);
        }

        function loadViewMode(contObject) {
            //set default object view mode
            contObject.widgets = ctrl.objectCtrlSettings.widgetSettings;
            var objId = contObject.id;
            if (checkUndefinedNull(USER_VCOOKIE_URL) || checkUndefinedNull(OBJECT_INDICATOR_PREFERENCES_VC_MODE) || checkUndefinedNull(objId)) {
                console.log("Request required params is null!");
                $scope.$broadcast("objectList:loadedModePrefs", {contObject: contObject});
                return false;
            }
            var url = USER_VCOOKIE_URL + "?vcMode=" + OBJECT_INDICATOR_PREFERENCES_VC_MODE + "&vcKey=OIP_" + objId;
            $http.get(url).then(function (resp) {
                var objectIndicatorModeKeyname;
                if (!(checkUndefinedNull(resp) || checkUndefinedNull(resp.data) || resp.data.length === 0)) {
                    objectIndicatorModeKeyname = JSON.parse(resp.data[0].vcValue);
                }

                loadModePrefs(objectIndicatorModeKeyname, contObject);
            }, errorCallback);
        }
        
        function findAndSetDefaultWidgets(widgetList) {
            var wkey, defaultWidgets = {};
            for (wkey in widgetList) {
                if (widgetList[wkey].length > 0) {
                    defaultWidgets[wkey] = widgetList[wkey][0].widgetName;
                    for (var i = 0; i < widgetList[wkey].length; i++) {
                        var elm = widgetList[wkey][i];
                        if (elm.isDefault === true) {
                            defaultWidgets[wkey] = elm.widgetName;
                            break;
                        }
                    }
//                    widgetList[wkey].some(function (elm) {
//                        if (elm.isDefault === true) {
//                            defaultWidgets[wkey] = elm.widgetName;
//                            return true;
//                        }
//                    });
                }
            }
            return defaultWidgets;
        }
        
        function getWidgetList() {
//                            _testGetJson("/api/subscr/vcookie/widgets/list");
            var url = WIDGETS_URL;
            $http.get(url).then(function (resp) {
                //console.log(resp.data);
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
                
                defaultWidgets = findAndSetDefaultWidgets(widgetList);

//                for (wkey in widgetList) {
//                    if (widgetList[wkey].length > 0) {
//                        defaultWidgets[wkey] = widgetList[wkey][0].widgetName;
//                        widgetList[wkey].some(function (elm) {
//                            if (elm.isDefault === true) {
//                                defaultWidgets[wkey] = elm.widgetName;
//                                return true;
//                            }
//                        });
//                    }
//                }
                ctrl.objectCtrlSettings.widgetSettings = defaultWidgets;
            }, errorCallback);
        }

//        $scope.$on('objectList:loadedModePrefs', function (event, args) {
////console.log('objectList:loadedModePrefs');
////console.log(args);
//            if (!checkUndefinedNull(args.contObject)) {                
//                createContObjectWidgetForPTree(args.contObject);
//            }
//        });
    // ********************************************************************************************
                    //  end Load widget settings
    //*********************************************************************************************        

        ctrl.selectPNode = function (item, ev, collapsed) {
//console.log(collapsed);
            if (ctrl.isContObjectNode(item)) {
//                ctrl.selectedObjectBy(item.nodeObject);
                loadViewMode(item.nodeObject);
            }

            if (!checkUndefinedNull(ctrl.data.selectedPNode) && !ev.ctrlKey && !ev.shiftKey) {
                ctrl.data.selectedPNode.isSelected = false;
                if (ctrl.isContZpointNode(ctrl.data.selectedPNode)) {
                    ctrl.data.selectedPNode.isIndicatorsView = false;
                }
                ctrl.data.selectedPNode.isWidgetView = false;
            }

            if (!ev.ctrlKey && selectedPNodes.length > 0) {
                selectedPNodes.forEach(function (csn) {
                    csn.isSelected = false;
                });
                selectedPNodes = [];
            }
            if (!item.isSelected) {
                selectedPNodes.push(item);
            }

            if (isLazyNode(item) && (ctrl.data.selectedPNode !== item)) {
                loadPTreeNode(item, PTREE_DEPTH_LEVEL);
            }

            item.isSelected = true;
            ctrl.data.selectedPNode = item; //angular.copy(item);
// console.log(ctrl.data.selectedPNode);
            if (ctrl.isContZpointNode(item)) {
                // for refresh indicator directive
                item.isIndicatorsView = false;
                $timeout(function () {
                    item.isIndicatorsView = true;
                }, 0);
            }

            item.isWidgetView = false;
            $timeout(function () {
                item.isWidgetView = true;
            }, 0);


        };

//        function createContObjectWidgetForPTree(contObject) {
//            var zpointWidget = {};
//            zpointWidget.type = "";//"chart";
//            zpointWidget.zpointStatus = "yellow";
//            zpointWidget.zpointStatusTitle = "На точке учета были происшествия";
//
//            var searchingObjectNode = {
//                nodeType: "CONT_OBJECT",
//                nodeObject: contObject
//            };
//
//            var foundedObjectNode = angular.copy(findNodeInPTree(searchingObjectNode, ctrl.data.currentPTree));
//
//            if (ctrl.data.selectedPNode.hasOwnProperty('childNodes')) {
//
//                ctrl.data.selectedPNode.childNodes.forEach(function (zpNode) {
//                    if (thisdata.contServiceTypes.hasOwnProperty(zpNode.nodeObject.contServiceTypeKeyname)) {
//                        zpNode.zpointOrder = thisdata.contServiceTypes[zpNode.nodeObject.contServiceTypeKeyname].serviceOrder + zpNode.nodeObject.customServiceName;
//                    }
//                    var zpWidgetOpts = {};
//                    zpWidgetOpts.type = contObject.widgets[zpNode.nodeObject.contServiceTypeKeyname];
//                    zpWidgetOpts.zpointName = zpNode.nodeObject.customServiceName || zpNode.nodeObject.contServiceTypeKeyname;
//                    zpWidgetOpts.contZpointId = zpNode.nodeObject.id;
//                    if (angular.isArray(zpNode.childNodes) && zpNode.childNodes.length > 0) {
//                        zpWidgetOpts.zpointModel = zpNode.childNodes[0].deviceModelId;
//                        zpWidgetOpts.zpointNumber = zpNode.childNodes[0].number;
//                        zpWidgetOpts.isImpulse = zpNode.childNodes[0].nodeObject.isImpulse;
//                        zpWidgetOpts.isManualLoading = zpNode.childNodes[0].nodeObject.isManual;
//                    }
//                    zpWidgetOpts.zpointType = zpNode.nodeObject.contServiceTypeKeyname;
//                    //measureUnitCaption
//                    zpWidgetOpts.contObjectId = zpNode.nodeObject.contObjectId;
//                    zpWidgetOpts.contObjectFullName = contObject.fullName;
//
//                    zpNode.widgetOptions = zpWidgetOpts;
//                });
//
//                treeNavSvc.sortItemsBy(ctrl.data.selectedPNode.childNodes, 'zpointOrder');
////console.log(ctrl.data.selectedPNode.childNodes);
//            }
//
//        }

//        function createContZpointWidgetForPTree(zpointPTreeNode) {
//            var zpointWidget = {};
//            zpointWidget.type = "";//"chart";
//            zpointWidget.zpointStatus = "yellow";
//            zpointWidget.zpointStatusTitle = "На точке учета были происшествия";
//        }

//        function setPTreeIndicatorParams(url, zpId) {
////                    zpId;
//            var zpointNode = null,
//                zpModel = null;
//            ctrl.data.selectedPNode.childNodes.some(function (elm) {
//                if (elm.nodeObject.id === zpId) {
//                    zpointNode = elm;
//                    return true;
//                }
//            });
//
//            thisdata.deviceModels.some(function (dm) {
//                if (dm.id === zpointNode.childNodes[0].nodeObject.deviceModelId) {
//                    zpModel = dm;
//                    return true;
//                }
//            });
//            //                    url += "/impulse-indicators";
//            if (zpModel !== null && (zpModel.isImpulse === true || zpModel.deviceType === treeNavSvc.HEAT_DISTRIBUTOR)) {
//                url += "/impulse-indicators";
//            } else if (zpointNode.nodeObject.contServiceTypeKeyname === 'el') {
//                url += "/indicator-electricity";
//            } else {
//                url += "/indicators";
//            }
//            url += "/?objectId=" + encodeURIComponent(ctrl.data.selectedPNode.nodeObject.id) + "&zpointId=" + encodeURIComponent(zpId) + "&objectName=" + encodeURIComponent(ctrl.data.selectedPNode.nodeObject.fullName) + "&zpointName=" + encodeURIComponent(zpointNode.nodeObject.customServiceName);
//
//            if (!checkUndefinedNull(zpointNode.childNodes[0].nodeObject.isManual)) {
//                url += "&isManualLoading=" + encodeURIComponent(zpointNode.childNodes[0].nodeObject.isManual);
//            }
//            return url;
//        }

// *******************************************************************************************
//         End of  Upgrade Tree Interface
//********************************************************************************************
        
        ctrl.$onInit = function () {
            getWidgetList();
            checkTreeSettingsAndGetObjectsData();
        };
        
    }
    
}());