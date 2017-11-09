/*jslint node: true, eqeq: true, nomen: true*/
/*global angular, $, moment*/
'use strict';
var app = angular.module('portalNMC');
app.controller('MonitorCtrl', ['$rootScope', '$http', '$scope', '$compile', '$interval', '$cookies', '$location', 'objectSvc', 'monitorSvc', 'mainSvc', '$timeout', function ($rootScope, $http, $scope, $compile, $interval, $cookies, $location, objectSvc, monitorSvc, mainSvc, $timeout) {
         
//console.log("Monitor Controller.");     
      
    $rootScope.ctxId = "monitor_page";
    //object url
    var noticesUrl = "#/notices/list/";
    var notificationsUrl = "../api/subscr/contEvent/notifications";
    var objectUrl = notificationsUrl + "/contObject";//"resource/objects.json";  
    var monitorUrl = notificationsUrl + "/monitorColor";
      //user messages
    $scope.messages = {};
    $scope.messages.groupMenuHeader = "Полный список объектов";
    $scope.messages.treeMenuHeader = "Полный список объектов";
    $scope.messages.noObjects = "";
      
    //objects array
    $scope.objects = monitorSvc.getAllMonitorObjects();//[];           
    //default date interval settings
    $rootScope.monitorStart = $location.search().fromDate || monitorSvc.getMonitorSettings().fromDate || moment().subtract(6, 'days').startOf('day').format('YYYY-MM-DD');
    $rootScope.monitorEnd =  $location.search().toDate || monitorSvc.getMonitorSettings().toDate || moment().endOf('day').format('YYYY-MM-DD');
    
    //monitor settings
    $scope.monitorSettings = {};
    $scope.monitorSettings.refreshPeriod = monitorSvc.getMonitorSettings().refreshPeriod;//"180";
    $scope.monitorSettings.createRoundDiagram = false;
    $scope.monitorSettings.loadingFlag = monitorSvc.getMonitorSettings().loadingFlag;// true;//monitorSvc.monitorSvcSettings.loadingFlag;
//console.log($scope.monitorSettings.loadingFlag);      
    $scope.monitorSettings.treeLoadingFlag = true;
    //flag: false - get all objectcs, true - get only  red, orange and yellow objects.
    $scope.monitorSettings.noGreenObjectsFlag = monitorSvc.getMonitorSettings().noGreenObjectsFlag || false;
    
    $scope.monitorSettings.objectsPerScroll = 34;//the pie of the object array, which add to the page on window scrolling
    $scope.monitorSettings.objectsOnPage = $scope.monitorSettings.objectsPerScroll;//50;//current the count of objects, which view on the page
    $scope.monitorSettings.currentScrollYPos = window.pageYOffset || document.documentElement.scrollTop;
    $scope.monitorSettings.objectTopOnPage = 0;
    $scope.monitorSettings.objectBottomOnPage = 34;
      
    $scope.monitorSettings.isCtrlEnd = false;
      
    $scope.monitorSettings.ctxId = "monitor_page";
    //***************************** request canceler  
    var requestCanceler = monitorSvc.getRequestCanceler();
    var httpOptions = {
        timeout: requestCanceler.promise
    };
    //////////////////////////////////////////////////
      
    $scope.monitorSettings.dateRangeSettings = mainSvc.getDateRangeOptions("monitor-ru");
    $scope.monitorSettings.monitorDates = {
        startDate :  $rootScope.monitorStart,
        endDate :  $rootScope.monitorEnd
    };
    $scope.objectsOnPage = ($scope.objects.length === 0) ? [] : $scope.objects.slice(0, $scope.monitorSettings.objectsPerScroll);
    $scope.objectsOnPage.forEach(function (element) {
        monitorSvc.getMonitorEventsByObject(element);
    });
      
    var errorCallback = function (e) {
        $scope.monitorSettings.loadingFlag = false;
        $scope.monitorSettings.treeLoadingFlag = false;
        monitorSvc.setMonitorSettings({loadingFlag: false});
        console.log(e);
    };
    //monitor state
    $scope.monitorState = {};
    $scope.getMonitorState = function () {
        var url = monitorUrl + "?fromDate=" + $rootScope.monitorStart + "&toDate=" + $rootScope.monitorEnd;
        $http.get(url, httpOptions)
            .success(function (data) {
                $scope.monitorState = data;
                var monitorTab = document.getElementById('monitorTab');
                monitorTab.style.backgroundColor = $scope.monitorState.statusColor.toLowerCase();
                if ($scope.monitorState.statusColor === "RED" || $scope.monitorState.statusColor == "ORANGE") {
                    monitorTab.style.color = "#eee";
                }
                monitorTab.title = $scope.monitorState.colorDescription;
            })
            .error(function (e) {
                console.log(e);
            });
    };

    $scope.eventColumns = [
        {"name": "typeCategory", "header" : " ", "class" : ""},
        {"name": "typeEventCount", "header" : " ", "class" : ""},
        {"name": "typeName", "header" : "Типы уведомлений", "class" : "col-md-10"}
    ];
    
    function findObjectById(objId) {
        var obj = null;
        $scope.objects.some(function (element) {
            if (element.contObject.id === objId) {
                obj = element;
                return true;
            }
        });
        return obj;
    }
    
    //get event types by object
    $scope.getEventTypesByObject = function (objId, isChart) {
        var obj = findObjectById(objId);
        //if cur object = null => exit function
        if (obj == null) {
            return;
        }
      
        var url = objectUrl + "/" + obj.contObject.id + "/eventTypes/statusCollapse" + "?fromDate=" + $rootScope.monitorStart + "&toDate=" + $rootScope.monitorEnd;
//console.log(url);          
        $http.get(url, httpOptions)
            .success(function (data) {
//console.log(data);            
            //if data is not array - exit
                if (!data.hasOwnProperty('length') || (data.length == 0)) {
                    return;
                }
                //temp array
                var tmpTypes = [];
                //make the new array of the types wich formatted to display
                data.forEach(function (element) {
                    var tmpType = {};
                    tmpType.id = element.contEventType.id;
                    tmpType.isBaseEvent = element.contEventType.isBaseEvent;
                    tmpType.typeCategory = element.statusColor.toLowerCase();
                    var activeEventFlag = false;
                    if (angular.isArray(obj.monitorEventsForMap)) {
                        obj.monitorEventsForMap.some(function (el) {
                            if (el.contEventType.id == tmpType.id) {
                                activeEventFlag = true;
                                return true;
                            }
                        });
                    }
                    if (!activeEventFlag) {
                        tmpType.typeCategory += "-mono";
                    }
                    tmpType.typeEventCount = element.totalCount;
                    tmpType.typeName = element.contEventType.caption;
                    tmpTypes.push(tmpType);
                });
                tmpTypes.sort(function (a, b) {
                    if (a.typeEventCount > b.typeEventCount) {
                        return -1;
                    }
                    if (a.typeEventCount < b.typeEventCount) {
                        return 1;
                    }
                    return 0;
                });
                obj.eventTypes = tmpTypes;
                //If need diagram - don't create child table
                if (isChart) {
                    $scope.runChart(obj.contObject.id);
                } else {
                    makeEventTypesByObjectTable(obj);
                }
            })
            .error(function (e) {
                console.log(e);
            });
    };
    
    $scope.toggleShowGroupDetails = function (objId) {//switch option: current goup details   
        var curObject = null;
        $scope.objects.some(function (element) {
            if (element.contObject.id === objId) {
                curObject = element;
                return true;
            }
        });
        //if cur object = null => exit function
        if (curObject == null) {
            return;
        }
        //else
        
        var eventTable = document.getElementById("eventTable" + curObject.contObject.id);
        if ((curObject.showGroupDetails == true) && (eventTable == null)) {
            curObject.showGroupDetails = true;
        } else {
            curObject.showGroupDetails = !curObject.showGroupDetails;
        }
       
        var btnDetail = null;
        if (curObject.showGroupDetails === true) {
            $scope.getEventTypesByObject(curObject.contObject.id, false);
            
            btnDetail = document.getElementById("btnDetail" + curObject.contObject.id);
            btnDetail.classList.remove("glyphicon-chevron-right");
            btnDetail.classList.add("glyphicon-chevron-down");
        } else {
            //else if curObject.showGroupDetails = false => hide child zpoint table
            var trObj = document.getElementById("obj" + curObject.contObject.id);
            var trObjEvents = trObj.getElementsByClassName("nmc-tr-object-events")[0];//.getElementById("trObjZp");
//            var trObjEvents = document.getElementById("trObjEvents"+curObject.contObject.id);
            trObjEvents.innerHTML = "";
            btnDetail = document.getElementById("btnDetail" + curObject.contObject.id);
            btnDetail.classList.remove("glyphicon-chevron-down");
            btnDetail.classList.add("glyphicon-chevron-right");
        }
    };
    
      //<a href> right click performer
    $scope.getNoticesByObjectOnRightClick = function (objId) {
        $scope.setNoticeFilterByObject(objId);
        window.open(noticesUrl);
    };
      
    $scope.getNoticesByObjectAndRevisionOnRightClick = function (objId) {
        $scope.setNoticeFilterByObjectAndRevision(objId);
        window.open(noticesUrl);
    };
    
    //Формируем таблицу с событиями объекта
    function makeEventTypesByObjectTable(obj) {
        var trObj = document.getElementById("obj" + obj.contObject.id);
        if (angular.isUndefined(trObj) || (trObj == null)) {
            return;
        }
//console.log(obj);        
        var trObjEvents = trObj.getElementsByClassName("nmc-tr-object-events")[0];
        if (angular.isUndefined(trObjEvents) || (trObjEvents == null)) {
            return;
        }
        var trHTML = "";
        trHTML = "<td colspan='5' style=\"padding-top: 2px !important;\"><table id=\"eventTable" + obj.contObject.id + "\" class=\"crud-grid table table-lighter table-bordered table-condensed table-hover nmc-child-table\">";
        trHTML += "<thead>";
        trHTML += "<tr class=\"nmc-child-table-header\">";
        trHTML += "<th class=\"nmc-td-for-buttons\">";
        trHTML += "</th>";
        $scope.eventColumns.forEach(function (element) {
            trHTML += "<th>";
            trHTML += (element.header || element.name);
            trHTML += "</th>";
        });
        trHTML += "</tr>";
        trHTML += "</thead>";
        
        if (angular.isUndefined(obj) || !obj.hasOwnProperty('eventTypes') || (obj.eventTypes.length == 0)) {
            return;
        }
        obj.eventTypes.forEach(function (event) {
            trHTML += "<tr id=\"trEvent" + event.id + "\" >";
            trHTML += "<td class=\"nmc-td-for-buttons\">" +
                    "<a href=\"" + noticesUrl + "?objectMonitorId=" + obj.contObject.id + "&monitorFlag=true&fromDate=" + $rootScope.monitorStart + "&toDate=" + $rootScope.monitorEnd + "&typeId=" + event.id + "\" ng-mousedown=\"setNoticeFilterByObjectAndType(" + obj.contObject.id + "," + event.id + ")\"> <i class=\"btn btn-xs glyphicon glyphicon-list nmc-button-in-table\"" +
//                        "ng-click=\"getNoticesByObjectAndType("+obj.contObject.id+","+event.id+")\""+
                        "title=\"Посмотреть уведомления\">" +
                    "</i></a>" +
                "</td>";
            $scope.eventColumns.forEach(function (column) {
                switch (column.name) {
                case "typeName":
                    trHTML += "<td class=\"col-md-11\" ng-class=\"{'nmc-positive-notice':" + (!event.isBaseEvent) + "}\">" + event[column.name];
                    if ($scope.isSystemuser()) {
                        trHTML += "<span>(id = " + event.id + ")</span>";
                    }
                    trHTML += "</td>";
                    break;
                case "typeCategory":
                    var size = 16;
                    var title = "";
                    if (event[column.name] == "green") {
                        size = 1;
                    }
                    switch (event[column.name]) {
                    case "red":
                        title = "Критическая ситуация";
                        break;
                    case "orange":
                        title = "Некритическая ситуация";
                        break;
                    case "yellow":
                        title = "Уведомление";
                        break;
                    case "red-mono":
                        title = "Критическая ситуация";
                        break;
                    case "orange-mono":
                        title = "Некритическая ситуация";
                        break;
                    case "yellow-mono":
                        title = "Уведомление";
                        break;
                    }
                    trHTML += "<td><img title=\"" + title + "\" height=\"" + size + "\" width=\"" + size + "\" src=\"" + "images/object-state-" + event[column.name] + ".png" + "\"/></td>";
                    break;
                default:
                    trHTML += "<td ng-class=\"{'nmc-positive-notice':" + (!event.isBaseEvent) + "}\"> " + event[column.name] + "</td>";
                    break;
                }
            });
            trHTML += "</tr>";
        });
        trHTML += "</table></td>";
        trObjEvents.innerHTML = trHTML;
        $compile(trObjEvents)($scope);
    }
    
    //Set filters for notice window
    $scope.setNoticeFilterByObject = function (objId) {
        $rootScope.monitor = {};
        monitorSvc.setMonitorSettings({objectMonitorId: objId});
        $rootScope.reportStart = $rootScope.monitorStart;
        $rootScope.reportEnd = $rootScope.monitorEnd;
    };
    
    $scope.setNoticeFilterByObjectAndRevision = function (objId) {
        $scope.setNoticeFilterByObject(objId);
        $cookies.put('isNew', 'true');
    };
    
    $scope.setNoticeFilterByObjectAndType = function (objId, typeId) {
        $scope.setNoticeFilterByObject(objId);
        $cookies.put('typeIds', '[' + typeId + ']');
    };
    
    $scope.getNoticesByObjectAndType = function (objId, typeId) {
        $scope.setNoticeFilterByObjectAndType(objId, typeId);
        window.open(noticesUrl);
    };
      
    $scope.isSystemuser = function () {
        $scope.userInfo = $rootScope.userInfo;
        return $scope.userInfo._system;
    };

    $scope.refreshData = function () {
        $scope.monitorSettings.loadingFlag = true;
//console.log($scope.monitorSettings.loadingFlag);        
        $rootScope.$broadcast('monitor:updateObjectsRequest');
    };
      
    $scope.getMonitor = function (group) {
        if (monitorSvc.checkUndefinedNull(group)) {
            $scope.messages.groupMenuHeader = "Полный список объектов";
            $scope.monitorSettings.contGroupId = null;
            monitorSvc.setMonitorSettings({contGroupId: null});
        } else {
            $scope.monitorSettings.contGroupId = group.id;
            monitorSvc.setMonitorSettings({contGroupId: group.id});
            $scope.messages.groupMenuHeader = group.contGroupName;
        }
        $scope.refreshData();
    };
      
    $scope.getAllColorObjects = function () {
        $scope.monitorSettings.loadingFlag = true;
//console.log($scope.monitorSettings.loadingFlag);        
        $scope.monitorSettings.noGreenObjectsFlag = false;
        monitorSvc.setMonitorSettings({noGreenObjectsFlag: false});
        $rootScope.$broadcast('monitor:updateObjectsRequest');
    };
    
    $scope.getNoGreenObjects = function () {
        $scope.monitorSettings.loadingFlag = true;
//console.log($scope.monitorSettings.loadingFlag);        
        $scope.monitorSettings.noGreenObjectsFlag = true;
        monitorSvc.setMonitorSettings({noGreenObjectsFlag: true});
        $rootScope.$broadcast('monitor:updateObjectsRequest');
    };
    
    //Watching for the change period  
    $scope.$watch('monitorSettings.monitorDates', function (newDates, oldDates) {
        if (oldDates === newDates) {
            return;
        }
        $scope.monitorSettings.loadingFlag = true;
//console.log($scope.monitorSettings.loadingFlag);        
        $rootScope.monitorStart = moment(newDates.startDate).format('YYYY-MM-DD');
        $rootScope.monitorEnd = moment(newDates.endDate).format('YYYY-MM-DD');
        monitorSvc.setMonitorSettings({
            fromDate: $rootScope.monitorStart,
            toDate: $rootScope.monitorEnd
        });
        $scope.monitorSettings.dateRangeSettings.startDate = moment($rootScope.monitorStart).startOf('day');
        $scope.monitorSettings.dateRangeSettings.endDate = moment($rootScope.monitorEnd).endOf('day');
        
        $location.search("fromDate", $rootScope.monitorStart);
        $location.search("toDate", $rootScope.monitorEnd);
        $rootScope.$broadcast('monitor:updateObjectsRequest');
    }, false);
      
      
    $scope.$on('monitorObjects:getObjectEvents', function (event, args) {
        var obj = args.obj;
        var imgObj = "#imgObj" + obj.contObject.id;
        $(imgObj).qtip({
            content: {
                text: obj.monitorEvents
            },
            style: {
                classes: 'qtip-bootstrap qtip-nmc-monitor-tooltip'
            }
        });
    });
    
//The control of the period monitor refresh(Управление перодическим обновлением монитора)
//**************************************************************************  
    $scope.$on('monitorObjects:updated', function () {
        $scope.monitorSettings.isTreeView = monitorSvc.getMonitorSettings().isTreeView;
        $scope.objects = monitorSvc.getAllMonitorObjects();
//console.log($scope.objects);        
//console.log("Monitor ctrl. Objects was updated."); 
        //reset position
        window.scrollTo(0, 0);
        $scope.monitorSettings.currentScrollYPos = window.pageYOffset || document.documentElement.scrollTop;
        $scope.monitorSettings.objectTopOnPage = 0;
        $scope.monitorSettings.objectBottomOnPage = 34;
        var tempArr = $scope.objects.slice(0, $scope.monitorSettings.objectsPerScroll);
        $scope.monitorSettings.loadingFlag = monitorSvc.getMonitorSettings().loadingFlag;//false;
//console.log($scope.monitorSettings.loadingFlag);        
        $scope.monitorSettings.noGreenObjectsFlag = monitorSvc.getMonitorSettings().noGreenObjectsFlag;
        $scope.monitorSettings.objectsOnPage = $scope.monitorSettings.objectsPerScroll;
        $scope.objectsOnPage = tempArr;
        $scope.objectsOnPage.forEach(function (element) {
            monitorSvc.getMonitorEventsByObject(element);
        });
        
        //refresh qtip
        var qtips = document.getElementsByClassName("qtip"),
            index;
        for (index = 0; index < qtips.length; index += 1) {
            qtips[index].style.display = "none";
        }
    });

    $scope.$on('$destroy', function () {
        window.onkeydown = undefined;
    });

    window.onkeydown = function (e) {
        var elem = null;
        if (e.keyCode == 38) {
            elem = document.getElementById("divWithMonitorTable");
            elem.scrollTop = elem.scrollTop - 20;
            return;
        }
        if (e.keyCode == 40) {
            elem = document.getElementById("divWithMonitorTable");
            elem.scrollTop = elem.scrollTop + 20;
            return;
        }
        if (e.keyCode == 34) {
            elem = document.getElementById("divWithMonitorTable");
            elem.scrollTop = elem.scrollTop + $scope.monitorSettings.objectsPerScroll * 10;
            return;
        }
        if (e.keyCode == 33) {
            elem = document.getElementById("divWithMonitorTable");
            elem.scrollTop = elem.scrollTop - $scope.monitorSettings.objectsPerScroll * 10;
            return;
        }
        if (e.ctrlKey && e.keyCode == 36) {
            elem = document.getElementById("divWithMonitorTable");
            elem.scrollTop = 0;
            return;
        }
        if ((e.ctrlKey && e.keyCode == 35)) { /*&& ($scope.monitorSettings.objectsOnPage < $scope.objects.length)*/
            if ($scope.monitorSettings.objectsOnPage < $scope.objects.length) {
                $scope.monitorSettings.loadingFlag = true;
                $timeout(function () { $scope.monitorSettings.loadingFlag = false; }, $scope.objects.length);
            }
            var tempArr = $scope.objects.slice($scope.monitorSettings.objectsOnPage, $scope.objects.length);
            tempArr.forEach(function (element) {
                if ((element.statusColor === "RED") || (element.statusColor === "ORANGE")) {
                    monitorSvc.getMonitorEventsByObject(element);
                } else if (element.statusColor === "YELLOW") {
                    element.monitorEvents = "На объекте нет нештатных ситуаций";
                    $rootScope.$broadcast('monitorObjects:getObjectEvents', {"obj" : element});
                }
            });
            Array.prototype.push.apply($scope.objectsOnPage, tempArr);
            $scope.monitorSettings.objectsOnPage += $scope.objects.length;
            $scope.$apply();
            elem = document.getElementById("divWithMonitorTable");
            elem.scrollTop = elem.scrollHeight;
        }
    };

    //watch for the change of the refresh period
    $scope.$watch('monitorSettings.refreshPeriod', function (newPeriod, oldPeriod) {
        if (newPeriod === oldPeriod) {
            return;
        }
        monitorSvc.setMonitorSettings({refreshPeriod : $scope.monitorSettings.refreshPeriod});
        $rootScope.$broadcast('monitor:periodChanged');
    }, false);
      
          //check object array
    if ($scope.objects.length != 0) {
        var tempArr = $scope.objects.slice(0, $scope.monitorSettings.objectsPerScroll);
        $scope.monitorSettings.loadingFlag = monitorSvc.getMonitorSettings().loadingFlag;//false;
        if (angular.isDefined(monitorSvc.getMonitorSettings().objectMonitorId) && monitorSvc.getMonitorSettings().objectMonitorId !== "null") {
            $scope.getEventTypesByObject(Number(monitorSvc.getMonitorSettings().objectMonitorId), false);
            monitorSvc.setMonitorSettings({objectMonitorId : null});
        }
    } else if ($scope.monitorSettings.loadingFlag === false) {//else -> send request
        $rootScope.$broadcast('monitor:updateObjectsRequest');
    }
      
    $scope.$on('$destroy', function () {
//console.log("Monitor destroy");        
        window.onscroll = undefined;
    });
    
    $scope.setMonitorEventsForObject = function (obj) {
//console.log(obj);        
        if (angular.isUndefined(obj.monitorEvents)) {
            return;
        }
        var imgObj = "#imgObj" + obj.contObject.id;
        $(imgObj).ready(function () {
            $(imgObj).qtip({
                content: {
                    text: obj.monitorEvents
                },
                style: {
                    classes: 'qtip-bootstrap qtip-nmc-monitor-tooltip'
                }
            });
        });
    };
      
    $scope.addMoreObjectsForMonitor = function () {
//console.log("addMoreObjectsForMonitor. Run");
        if (($scope.objects.length <= 0)) {
            return;
        }
        var endIndex = $scope.monitorSettings.objectsOnPage + $scope.monitorSettings.objectsPerScroll;
        if (endIndex >= $scope.objects.length) {
            endIndex = $scope.objects.length;
        }
        var tempArr =  $scope.objects.slice($scope.monitorSettings.objectsOnPage, endIndex);
        Array.prototype.push.apply($scope.objectsOnPage, tempArr);
        tempArr.forEach(function (element) {
            monitorSvc.getMonitorEventsByObject(element);
        });
        if (endIndex >= ($scope.objects.length)) {
            $scope.monitorSettings.objectsOnPage = $scope.objects.length;
        } else {
            $scope.monitorSettings.objectsOnPage += $scope.monitorSettings.objectsPerScroll;
        }
    };
      
    $("#divWithMonitorTable").scroll(function () {
        $scope.addMoreObjectsForMonitor();
        $scope.$apply();
    });
         
    //control visibles
    var setVisibles = function (ctxId) {
        var ctxFlag = false;
        var tmp = mainSvc.getContextIds();
        tmp.forEach(function (element) {
            if (element.permissionTagId.localeCompare(ctxId) == 0) {
                ctxFlag = true;
            }
            var elDOM = document.getElementById(element.permissionTagId);//.style.display = "block";
            if (angular.isUndefined(elDOM) || (elDOM == null)) {
                return;
            }
            $('#' + element.permissionTagId).removeClass('nmc-hide');
        });
        if (ctxFlag == false) {
            window.location.assign('#/notices/list');
        }
    };
    setVisibles($scope.monitorSettings.ctxId);
    //listen change of service list
    $rootScope.$on('servicePermissions:loaded', function () {
        setVisibles($scope.monitorSettings.ctxId);
    });
      
    window.setTimeout(function () {
//console.log(3);        
        setVisibles($scope.monitorSettings.ctxId);
    }, 500);
    
        //chart
    $scope.runChart = function (objId) {
//        $scope.monitorSettings.createRoundDiagram = true;
        
        var curObjIndex = -1;
        $scope.objects.some(function (element, index) {
            if (element.contObject.id == objId) {
                curObjIndex = index;
                return true;
            }
        });
        if (curObjIndex == -1) {
            return;
        }
        var data = [];//, series = Math.floor(Math.random() * 6) + 3;
        var i;
        for (i = 0; i < $scope.objects[curObjIndex].eventTypes.length; i += 1) {
			data[i] = {
				label: $scope.objects[curObjIndex].eventTypes[i].typeName,
				data: $scope.objects[curObjIndex].eventTypes[i].typeEventCount
			};
		}
        $scope.monitorSettings.createRoundDiagram = false;
        
        // выводим график
        $("#noticeChart-area").width(300);
        $("#noticeChart-area").height(300);
        $("#chartModal.modal-dialog").width(700);
        $('#chartModal').modal();
        
        $.plot('#noticeChart-area', data, {
            series: {
                pie: {
                    show: true,
                    label : {
                        show : false,
                        formatter: labelFormatter
                    }
                }
            },
            legend: {
                show: true,
                labelFormatter: labelFormatter,
                position: "ne",
                margin: [-400, 0]
            }
        });
        
    };
    function labelFormatter(label, series) {
		return "<div style='font-size:8pt; text-align:center; padding:2px; color:black;'>" + label + " (" + Math.round(series.percent) + "%)</div>";
	}
      
    // ********************************************************************************************
//  TREEVIEW
//*********************************************************************************************
    $scope.monitorSettings.isTreeView = true;
    $scope.monitorSettings.isFullObjectView = false;

    $scope.data.currentTree = {};
    $scope.data.newTree = {};
    $scope.data.defaultTree = null;// default tree               

    var findNodeInTree = function (node, tree) {
        return mainSvc.findNodeInTree(node, tree);
    };
      
    $scope.viewFullObjectList = function () {
        $scope.monitorSettings.isFullObjectView = true;
        $scope.monitorSettings.loadingFlag = true;
        $scope.messages.treeMenuHeader = 'Полный список объектов';
        monitorSvc.setMonitorSettings({curTreeId: null, curTreeNodeId: null, isFullObjectView: true});
        monitorSvc.setMonitorSettings({currentTree: null, currentTreeNode: null});
        $rootScope.$broadcast('monitor:updateObjectsRequest');
    };

    $scope.selectNode = function (item) {
        var treeForSearch = $scope.data.currentTree;
        var selectedNode = $scope.data.selectedNode;
//console.log(item);        
//console.log(selectedNode);        
        if (!mainSvc.checkUndefinedNull(selectedNode)) {
            if (selectedNode.id == item.id || selectedNode.type == item.type == 'root') {
                return;
            }
            var preNode = findNodeInTree(selectedNode, treeForSearch);
            if (!mainSvc.checkUndefinedNull(preNode)) {
                preNode.isSelected = false;
            }
        }

        item.isSelected = true;
        $scope.data.selectedNode = angular.copy(item);
        $scope.monitorSettings.loadingFlag = true;
        $scope.messages.noObjects = "Объектов нет.";
        monitorSvc.setMonitorSettings({loadingFlag: true, curTreeId: $scope.data.currentTree.id, curTreeNodeId: item.id});
        monitorSvc.setMonitorSettings({currentTree: angular.copy($scope.data.currentTree), currentTreeNode: angular.copy(item)});
        $rootScope.$broadcast('monitor:updateObjectsRequest');
    };

    $scope.data.trees = [];

    $scope.loadTree = function (tree, selectedNode) {
        $scope.monitorSettings.treeLoadingFlag = true;
        objectSvc.loadSubscrTree(tree.id).then(function (resp) {
            $scope.monitorSettings.treeLoadingFlag = false;
            $scope.messages.treeMenuHeader = tree.objectName || tree.id;
            var respTree = angular.copy(resp.data);
            mainSvc.sortTreeNodesBy(respTree, "objectName");
            $scope.data.currentTree = respTree;
            monitorSvc.setMonitorSettings({currentTree: angular.copy($scope.data.currentTree), curTreeId: $scope.data.currentTree.id});
            if (!mainSvc.checkUndefinedNull(selectedNode)) {
                var originalCurrentTree = mainSvc.findItemBy($scope.data.trees, "id", respTree.id);
                var originalCurrentTreeNode = findNodeInTree(selectedNode, $scope.data.currentTree);
                $scope.selectNode(originalCurrentTreeNode);
                return "Current node is set.";
            }
            $scope.objects = [];
            $scope.objectsOnPage = [];
            $scope.monitorSettings.isFullObjectView = false;
            monitorSvc.setMonitorSettings({isFullObjectView: false});
            monitorSvc.setMonitorSettings({currentTreeNode: null, curTreeNodeId: null});
            $scope.messages.noObjects = "";
        }, errorCallback);
    };

    var loadTrees = function (treeSetting) {
        $scope.monitorSettings.treeLoadingFlag = true;
        objectSvc.loadSubscrTrees().then(function (resp) {
            $scope.monitorSettings.treeLoadingFlag = false;
            mainSvc.sortItemsBy(resp.data, "objectName");
            $scope.data.trees = angular.copy(resp.data);
            if (!mainSvc.checkUndefinedNull(treeSetting) && (treeSetting.isActive == true)) {
                $scope.data.defaultTree = mainSvc.findItemBy($scope.data.trees, "id", Number(treeSetting.value));
            }
            //считать текущее дерево и ноду, и если они заданы переключиться на них
            var curTree = monitorSvc.getMonitorSettings().currentTree;
            var curTreeNode = monitorSvc.getMonitorSettings().currentTreeNode;
            $scope.monitorSettings.isFullObjectView = monitorSvc.getMonitorSettings().isFullObjectView;
            if (!mainSvc.checkUndefinedNull(curTree) && !mainSvc.checkUndefinedNull(curTreeNode)) {
                $scope.loadTree(curTree, curTreeNode);
                return "Current tree and current tree node is defined.";
            }
            if (!angular.isArray($scope.data.trees) || $scope.data.trees.length <= 0 || mainSvc.checkUndefinedNull($scope.data.defaultTree) || $scope.monitorSettings.isFullObjectView == true) {
                $scope.viewFullObjectList();
                return "View full object list";
            }
            monitorSvc.setMonitorSettings({currentTree: $scope.data.defaultTree, curTreeId: $scope.data.defaultTree.id});
            $scope.loadTree($scope.data.defaultTree);
        }, errorCallback);
    };
          
// ********************************************************************************************
//  END TREEVIEW
//*********************************************************************************************
      
    var initCtrl = function () {
        $scope.data.selectedNode = null;
        monitorSvc.loadDefaultMonitorTreeSetting().then(function (resp) {
            loadTrees(resp.data);
        }, errorCallback);
    };
      
    initCtrl();
      
}]);