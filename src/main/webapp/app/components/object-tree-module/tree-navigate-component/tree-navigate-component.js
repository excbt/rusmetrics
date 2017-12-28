/*global angular, console*/
(function () {
    'use strict';
    
    angular.module('objectTreeModule')
        .component('treeNavigateComponent', {
            templateUrl: "components/object-tree-module/tree-navigate-component/tree-navigate-component.html",
            controller: treeNavigateComponentController
        });
    
    treeNavigateComponentController.$inject = ['$scope', '$element', '$attrs', 'treeNavigateComponentService', '$cookies', '$timeout', '$rootScope', 'notificationFactory', '$http', '$state'];
    
    function treeNavigateComponentController($scope, $element, $attrs, treeNavigateComponentService, $cookies, $timeout, $rootScope, notificationFactory, $http, $state) {
        /*jshint validthis: true*/
        var ctrl = this;
        
        var IMG_PATH_MONITOR_TEMPLATE = "components/object-tree-module/tree-navigate-component/images/object-state-",
            IMG_EXT = ".png",
            PTREE_DEPTH_LEVEL = 0,
            VCOOKIE_URL = "../api/subscr/vcookie",
            USER_VCOOKIE_URL = "../api/subscr/vcookie/user",
            OBJECT_INDICATOR_PREFERENCES_VC_MODE = "OBJECT_INDICATOR_PREFERENCES",
            WIDGETS_URL = "../api/subscr/vcookie/widgets/list",
            TREE_NODE_INFO_STATE_NAME = 'objectsPTree.treeNodeInfo';
        
        var treeNavSvc = treeNavigateComponentService;
        
//        var mainSvc = {};
        var checkUndefinedNull = treeNavSvc.checkUndefinedNull;
        
        var checkEmptyObject = treeNavSvc.checkEmptyObject;
        
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
//console.log(resp);
//console.log(resp.data);

            ctrl.data.currentPTree = resp.data;
            setMonitorToPTree(ctrl.data.currentPTreeMonitor, ctrl.data.currentPTree);
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
//console.log(ctrl.data.currentPTree);
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
        
        function setMonitorToPTree(monitor, ptree) {
//console.log('setMonitorToPTree');            
//console.log(monitor);      
//console.log(ptree);            
            if (checkUndefinedNull(monitor) || checkUndefinedNull(ptree) || checkEmptyObject(monitor)) {
                return false;
            }
//console.log(ptree);
            ptree.monitorStatusPath = monitor[ptree.nodeType + ptree._id];
            if (ptree.hasOwnProperty('linkedNodeObjects') && angular.isArray(ptree.linkedNodeObjects)) {
//console.log(ptree.linkedNodeObjects);                
                ptree.linkedNodeObjects.forEach(function (lno) {
//console.log(lno);                    
                    lno.monitorStatusPath = monitor[lno.nodeType + lno.nodeObject.id];
                    if (lno.hasOwnProperty('childNodes') && angular.isArray(lno.childNodes)) {
                        lno.childNodes.forEach(function (cn) {
                            cn.monitorStatusPath = monitor[cn.nodeType + cn.nodeObject.id];
                        });
                    }
                });
            }
//console.log(ptree);            
            if (ptree.hasOwnProperty('childNodes') && angular.isArray(ptree.childNodes)) {
//console.log(ptree.childNodes);                                
                ptree.childNodes.forEach(function (child) {                    
                    setMonitorToPTree(monitor, child);
                });
            }
//console.log(ptree);            
        }

        function getPTree() {
//                    console.log("getPTree");
            ctrl.data.currentPTree = treeNavSvc.getPTree();
            setMonitorToPTree(ctrl.data.currentPTreeMonitor, ctrl.data.currentPTree);
            ctrl.data.currentPTreeWrapper = [ctrl.data.currentPTree];
            ctrl.messages.treeMenuHeader = ctrl.data.currentPTree.nodeName || ctrl.data.currentPTree._id;
            ctrl.loading = false;
            ctrl.treeLoading = false;
            setEventsForCurrentPTree(ctrl.data.currentPTree);
            console.log(ctrl.data.currentPTree);
        }

        $scope.$on(treeNavSvc.BROADCASTS.pTreeLoaded, function () {
            getPTree();
        });

        function getPTreeMonitor() {
            var monitorData = treeNavSvc.getPTreeMonitor();
            if (!angular.isArray(monitorData)) {
                return false;
            }
            var monitor = {};
            monitorData.forEach(function (md) {
                monitor[md.nodeType + md.monitorObjectId] = IMG_PATH_MONITOR_TEMPLATE + md.colorKey.toLowerCase() + IMG_EXT;
                setEventsForObject(md.monitorObjectId);
            });

            ctrl.data.currentPTreeMonitor = monitor;
            setMonitorToPTree(ctrl.data.currentPTreeMonitor, ctrl.data.currentPTree);

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
            
            //set lazyNode = false because node is loaded
            PTnode.lazyNode = false;
            //set monitor
            setMonitorToPTree(ctrl.data.currentPTreeMonitor, PTnode);
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

        ctrl.isElementNode = treeNavSvc.isElementNode; 
        ctrl.isContObjectNode = treeNavSvc.isContObjectNode;
        ctrl.isContZpointNode = treeNavSvc.isContZpointNode;
        ctrl.isDeviceNode = treeNavSvc.isDeviceNode;

        ctrl.isChevronRight = treeNavSvc.isChevronRight;

        ctrl.isChevronDown = treeNavSvc.isChevronDown;

        ctrl.isChevronDisabled = treeNavSvc.isChevronDisabled;

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
            
            if (ctrl.data.selectedPNode !== item) {
                $state.go(TREE_NODE_INFO_STATE_NAME, {node: item});
            }

            item.isSelected = true;
            ctrl.data.selectedPNode = item; //angular.copy(item);
            treeNavSvc.setPTreeSelectedNode(ctrl.data.selectedPNode);
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
        
        ctrl.$onInit = function () {
            ctrl.data.selectedPNode = treeNavSvc.getPTreeSelectedNode();
            if (ctrl.data.selectedPNode !== null) {
                $state.go(TREE_NODE_INFO_STATE_NAME, {node: ctrl.data.selectedPNode});
            }
            getWidgetList();
            checkTreeSettingsAndGetObjectsData();
        };
        
    }
    
}());