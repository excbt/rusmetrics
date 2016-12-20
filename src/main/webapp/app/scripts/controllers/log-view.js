/*jslint node: true*/
/*global angular, moment, $*/
'use strict';
var app = angular.module('portalNMC');
app.controller('LogViewCtrl', ['$scope', '$cookies', '$timeout', 'mainSvc', 'objectSvc', '$http', 'notificationFactory', 'logSvc', '$rootScope', function ($scope, $cookies, $timeout, mainSvc, objectSvc, $http, notificationFactory, logSvc, $rootScope) {
    
    //dev
    var testStep = {
        "id": 1296685,
        "sessionId": 1296681,
        "stepUuid": "33167812-a64a-11e6-ba8e-db3d6c83c90a",
        "stepDate": 1478674829628,
        "stepModule": "ADAPTER",
        "stepType": "INFO",
        "stepMessage": "Тест Загрузка суточных интеграторов ,начиная с 2016-11-09 10:00:04. Прибор ID 128647060, модель Bts2 Загрузка суточных интеграторов ,начиная с 2016-11-09 10:00:04. Прибор ID 128647060, модель Bts2 Загрузка суточных интеграторов ,начиная с 2016-11-09 10:00:04. Прибор ID 128647060, модель Bts2 Загрузка суточных интеграторов ,начиная с 2016-11-09 10:00:04. Прибор ID 128647060, модель Bts2 "
            + "Мой дядя самых честных правил, "
            + "Когда в не шутку занемог, "
            + "Он уважать себя заставил"
            + "И лучше выдумать не мог.",
        "stepDescription": null,
        "isChecked": false,
        "sumRows": null,
        "isIncremental": null,
        "lastIncrementDate": null,
        "version": 0,
        "stepDateStr": "09-11-2016 10:00:29"
    };
    
    var testSession = {
        "id": 1296789,
        "sessionType": "SESSION_T1",
        "sessionUuid": "8b20dde4-a6bf-11e6-ba8e-33670b0e2b8e",
        "masterSessionUuid": "8b20b6d3-a6bf-11e6-ba8e-d350cd2d8b05",
        "sessionDate": 1478725228453,
        "sessionOwner": "SYSTEM",
        "sessionMessage": "Talend_Loader_Device",
        "sessionStatus": "FAILURE",
        "statusMessage": "Тест Сессия завершена успешно (все загружено)  Сессия завершена успешно (все загружено) Сессия завершена успешно (все загружено) Сессия завершена успешно (все загружено) Сессия завершена успешно (все загружено) Сессия завершена успешно (все загружено) Сессия завершена успешно (все загружено) Сессия завершена успешно (все загружено) Сессия завершена успешно (все загружено) Сессия завершена успешно (все загружено) Сессия завершена успешно (все загружено) Сессия завершена успешно (все загружено) Сессия завершена успешно (все загружено) Сессия завершена успешно (все загружено) Сессия завершена успешно (все загружено)",
        "statusDate": 1478725862787,
        "dataSourceId": 128647056,
        "deviceObjectId": 128656466,
        "version": 0,
        "sessionDateStr": "10-11-2016 00:00:28",
        "statusDateStr": "10-11-2016 00:11:02",
        "dataSourceInfo": {
            "id": 128647056,
            "dataSourceName": "1-ый корпус Ромашково"
        },
        "deviceObjectInfo": {
            "id": 128656466,
            "number": "52#2",
            "deviceModelId": 128647057,
            "deviceModelName": "БТС-2"
        },
        "authorInfo": {
            "authorId": null,
            "authorName": "Расписание"
        }
    };
    //end Dev
    
    var REFRESH_PERIOD = 300000;
    var ROW_PER_PAGE = 20;
    $scope.messages = {};
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.itemPerPage = ROW_PER_PAGE;
    $scope.ctrlSettings.pagination = {
        current: 1
    };
    $scope.ctrlSettings.sessionsUrl = "../api/rma/logSessions";
        
    $scope.ctrlSettings.groupUrl = "../api/subscr/contGroup";
    $scope.ctrlSettings.showObjectsFlag = true;
    
    $scope.ctrlSettings.daterangeOpts = mainSvc.getDateRangeOptions("ru");
    $scope.ctrlSettings.daterangeOpts.startDate = moment().startOf('day');
    $scope.ctrlSettings.daterangeOpts.endDate = moment().endOf('day');
    $scope.ctrlSettings.daterangeOpts.dateLimit = {"months": 1}; //set date range limit with 1 month
    $scope.ctrlSettings.sessionsLogDaterange = logSvc.getSessionsLogDaterange();
//        {
//        startDate: moment().startOf('day'),                        
//        endDate: moment().endOf('day')};
    var ctrlSettings = {};
    ctrlSettings.systemDateFormat = "YYYY-MM-DD";
    
    $scope.ctrlSettings.sessionColumns = [
        {
            name: "currentStatusColor",
            caption: "",
            type: 'color',
            headerClass: "col-xs-1 nmc-td-for-button noPadding"
        }, {
            name: "dataSource",
            caption: "Источник",
            headerClass: "col-xs-2 noPadding",
            type: 'id'
        }, {
            name: "deviceModel",
            caption: "Модель",
            headerClass: "col-xs-1"
        }, {
            name: "deviceNumber",
            caption: "Номер прибора",
            headerClass: "col-xs-1"
        }, {
            name: "startDate",
            caption: "Время начала",
            headerClass: "col-xs-1 noPadding"
        }, /*{
            name: "endDate",
            caption: "Время завершения",
            headerClass: "col-xs-1 noPadding"
        },*/ {
            name: "author",
            caption: "Инициатор",
            headerClass: "col-xs-1"
        }, {
            name: "currentStatus",
            caption: "Текущий статус",
            headerClass: "col-xs-1"
        }, {
            name: "statusMessage",
            caption: "Сообщение",
            headerClass: "col-xs-4"
        }
        
    ];
    $scope.ctrlSettings.logColumns = [
        {
            name: "stepDateStr",
            caption: "Дата-время",
            headerClass: "col-xs-2",
            type: "id"
        }, {
            name: "stepType",
            caption: "Тип",
            headerClass: "col-xs-1"
        }, {
            name: "stepMessage",
            caption: "Текст",
            headerClass: "col-xs-7"
        }, {
            name: "isIncremental",
            caption: "Счетчик",
            headerClass: "col-xs-1",
            type: "checkbox"
            
        }, {
            name: "sumRows",
            caption: "Значение",
            headerClass: "col-xs-1"
        }
        
    ];
    
    $scope.selectedObjects = [];
    $scope.data = {};
//    $scope.data.sessionsOnView = []; //sessions which view in table
    var data = {};
    data.sessions = []; //all sessions
    $scope.data.sessionLog = [];
    $scope.data.currentSession = {};
    
    $scope.states = {};
    
    $scope.states.isSelectedAllObjects = true;
    
    $scope.states.isSelectedAllObjectsInWindow = true;
    
    $scope.messages.defaultFilterCaption = "Все";
    $scope.selectedObjects_list = {};//object for object caption params
    $scope.selectedObjects_list.caption = $scope.messages.defaultFilterCaption;
    
    $scope.toggleChildSessionsView = function (session) {
        session.isChildView = !session.isChildView;
    };
    
    $scope.isSystemuser = function () {
        return mainSvc.isSystemuser();
    };
    
    /*******************************************/
    /* Test generation*/
    
    var SESSION_ROW_COUNT = 21;
    var LOG_ROW_COUNT = 125;
    
    function generateSessions() {
        var sessions = [],
            i;
        for (i = 0; i < SESSION_ROW_COUNT; i += 1) {
            var ses = {};
            //color status
            var statusColor = Math.random();
//            if (statusColor >= 0 && statusColor <= 0.3){
//                ses.colorStatus = "RED";
//            }else if (statusColor > 0.3 && statusColor <= 0.6){
//                ses.colorStatus = "YELLOW";
//            }else if (statusColor > 0.6 && statusColor <= 1){
//                ses.colorStatus = "GREEN";
//            };
            //data device: source, model, number
            var rndIntNumber = Math.round(statusColor * 100);
            ses.dataSource = "Источник " + rndIntNumber;
            ses.deviceModel = "Модель " + rndIntNumber;
            ses.deviceNumber = "№ 0000" + rndIntNumber;
            //time
            ses.startDate = moment().subtract(Math.round(statusColor * 10), 'days').format("DD-MM-YYYY HH:mm");
            ses.endDate = moment().subtract(Math.round(statusColor * 10 + 1), 'days').format("DD-MM-YYYY HH:mm");
            //other
            ses.author = statusColor > 0.5 ? "Артамонов А.А" : "Расписание";
            ses.currentStatus = statusColor < 0.2 ? "Закрыта" : "Открыта";
            ses.colorStatus = statusColor < 0.2 ? "RED" : "GREEN";
            ses.totalRow = Math.round(statusColor * 10);
            
            if (statusColor <= 0.3) {
                ses.type = "OID";
                ses.childs = [];
                var j;
                for (j = 0; j <= Math.round(statusColor * 10); j += 1) {
                    var child = angular.copy(ses);
                    child.type = "OP";
                    child.dataSource = "Прибор " + rndIntNumber + "-" + j;
                    ses.childs.push(child);
                }
            }
            sessions.push(ses);
//console.log(ses);            
        }
        $scope.data.sessionsOnView = sessions;
        //set session table height
        $timeout(function () {
            $("#log-upper-part > .rui-resizable-content").height(Number($cookies.heightLogUpperPart));
        });
    }
    
    function generateSessionLog() {
        var WORDS = ["Земля", "портфель", "идет", "лопатать", "потом", "дравина", "успел", "растопша", "трап", "мышь", "лететь"],
            logs = [],
            i,
            j;
        for (i = 0; i < LOG_ROW_COUNT; i += 1) {
            var log = {};
            var rndNum = Math.random();
            log.date = moment().format("DD-MM-YYYY HH:mm");
            if (rndNum <= 0.3) {
                log.type = "Аларм";
            } else if (rndNum > 0.3 && rndNum <= 0.6) {
                log.type = "Варнинг";
            } else {
                log.type = "Инфо";
            }
            var msg = "";
            for (j = 0; j <= 20; j += 1) {
                msg += WORDS[Math.round(Math.random() * WORDS.length)] + " ";
            }
            log.text = msg;
            logs.push(log);
        }
        $scope.data.sessionLog = logs;
        
                //set log table height
        $timeout(function () {
            $("#log-footer-part > .rui-resizable-content").height(Number($cookies.heightLogFooterPart));
        });
    }
    
    function generate() {
        generateSessions();
        generateSessionLog();
    }
    
//    generate();
    /* end Test generation*/
    /*************************************************************/
    
    function loadSessionsData() {
        $scope.ctrlSettings.sessionsLoading = true;
//        $scope.data.sessionsOnView = [];
        $scope.data.sessionLog = [];
        $scope.data.currentSession = {};
        var url = $scope.ctrlSettings.sessionsUrl;
            //define url params
        var params = {};
        if (!mainSvc.checkUndefinedNull($scope.ctrlSettings.sessionsLogDaterange.startDate)) {
            params.fromDate = moment($scope.ctrlSettings.sessionsLogDaterange.startDate).format(ctrlSettings.systemDateFormat);
        }
        if (!mainSvc.checkUndefinedNull($scope.ctrlSettings.sessionsLogDaterange.endDate)) {
            params.toDate = moment($scope.ctrlSettings.sessionsLogDaterange.endDate).format(ctrlSettings.systemDateFormat);
        }
        if (!mainSvc.checkUndefinedNull($scope.selectedObjects) && angular.isArray($scope.selectedObjects) && $scope.selectedObjects.length > 0) {
            params.contObjectIds = $scope.selectedObjects;
        }
//console.log('$broadcast logSvc:requestSessionsLoading');        
        $rootScope.$broadcast('logSvc:requestSessionsLoading', {params: params});
    }
    
    $scope.clearObjectFilter = function () {
        $scope.objects.forEach(function (el) {
            el.selected = false;
        });
        $scope.groups.forEach(function (el) {
            el.selected = false;
        });
        $scope.selectedObjects_list.caption = $scope.messages.defaultFilterCaption;
        $scope.selectedObjects = [];
        $scope.selectedGroups = [];
        $scope.states.isSelectedAllObjects = true;
        loadSessionsData();
    };
    
        // Открыть окно выбора объектов
    $scope.selectObjectsClick = function () {
        $scope.states.isSelectElement = false;
        $scope.states.isSelectedAllObjectsInWindow = angular.copy($scope.states.isSelectedAllObjects);
        if ($scope.ctrlSettings.showObjectsFlag == true) {
            $scope.objectsInWindow = angular.copy($scope.objects);
        } else {
            $scope.objectsInWindow = angular.copy($scope.groups);
        }
    };
    
            //perform objects from groups
    function getGroupObjects(group) {
        var selectedObjectUrl = $scope.ctrlSettings.groupUrl + "/" + group.id + "/contObject";
        $http.get(selectedObjectUrl).then(function (response) {
            group.objects = response.data;
        });
    }
    
    var successCallbackGetObjects = function (response) {
        $scope.objects = response.data;
        objectSvc.sortObjectsByFullName($scope.objects);
    };
    
    // ************* Object filter *****************************************
    
                 //get Objects
    $scope.getObjects = function () {
//        crudGridDataFactory($scope.objectsUrl).query(function(data){
        if (mainSvc.isRma()) {
            objectSvc.rmaPromise.then(successCallbackGetObjects);
        } else {
            objectSvc.promise.then(successCallbackGetObjects);
        }
    };
    
    $scope.getObjects();
    
    //get groups
    $scope.getGroups = function () {
        var url = $scope.ctrlSettings.groupUrl;
        $http.get(url).then(function (response) {
            var tmp = response.data;
            tmp.forEach(function (el) {
                getGroupObjects(el);
            });
            mainSvc.sortItemsBy(tmp, "contGroupName");
            $scope.groups = tmp;
//console.log($scope.groups);
        }, function (e) {
            console.log(e);
        });
    };
    $scope.getGroups();
    
    function performObjectsFilter() {
        $scope.selectedObjects_list.caption = $scope.messages.defaultFilterCaption;
        $scope.selectedObjects = [];
//        $('#selectObjectsModal').modal('hide');
        $scope.objects = angular.copy($scope.objectsInWindow);

        $scope.objects.map(function (el) {
            if (el.selected) {
//              $scope.selectedObjects_list+=el.fullName+"; ";
                $scope.selectedObjects.push(el.id);
            }
        });
        if ($scope.selectedObjects.length == 0) {
            $scope.selectedObjects_list.caption = $scope.messages.defaultFilterCaption;
            $scope.states.isSelectedAllObjects = true;
        } else {
            $scope.selectedObjects_list.caption = $scope.selectedObjects.length;
            $scope.states.isSelectedAllObjects = false;
        }
    }
    
    $scope.joinObjectsFromSelectedGroups = function (groups) {
        var result = [];
        groups.forEach(function (group) {
            if (group.selected) {
                Array.prototype.push.apply(result, group.objects);
//                    totalGroupObjects = group.objects;
            }
        });
        return result;
    };
    
    $scope.deleteDoublesObjects = function (targetArray) {
        var arrLength = targetArray.length;
        while (arrLength >= 2) {
            arrLength -= 1;
            if (targetArray[arrLength].fullName === targetArray[arrLength - 1].fullName) {
                targetArray.splice(arrLength, 1);
            }
        }
    };
    
    $scope.addUniqueObjectsFromGroupsToSelectedObjects = function (arrFrom, arrTo) {
        var j,
            i;
        for (j = 0; j < arrFrom.length; j += 1) {
            var uniqueFlag = true;
            for (i = 0; i < arrTo.length; i += 1) {
                if (arrFrom[j].fullName === arrTo[i].fullName) {
                    uniqueFlag = false;
                    break;
                }
            }
            if (uniqueFlag) {
                arrTo.push(arrFrom[j]);
            }
        }
        
    };
    
    function performGroupsFilter() {
        $scope.selectedObjects_list.caption = "";
        $scope.selectedGroups = [];
        $scope.selectedObjects = [];
//        $('#selectObjectsModal').modal('hide');
        $scope.groups = angular.copy($scope.objectsInWindow);
        
        var totalGroupObjects = $scope.joinObjectsFromSelectedGroups($scope.groups);
//console.log(totalGroupObjects);            
        objectSvc.sortObjectsByFullName(totalGroupObjects);
        //del doubles
        $scope.deleteDoublesObjects(totalGroupObjects);
        //add groupObjects to selected objects
            //add only unique objects
        $scope.addUniqueObjectsFromGroupsToSelectedObjects(totalGroupObjects, $scope.selectedObjects);
        var tmp = $scope.selectedObjects.map(function (el) {
            return el.id;
        });
        $scope.selectedObjects = tmp;
//console.log($scope.selectedObjects);        
        $scope.groups.map(function (el) {
            if (el.selected) {
            //              $scope.selectedObjects_list+=el.fullName+"; ";
                $scope.selectedGroups.push(el.id);
            }
        });
        if ($scope.selectedGroups.length == 0) {
            $scope.selectedObjects_list.caption = $scope.messages.defaultFilterCaption;
            $scope.states.isSelectedAllObjects = true;
        } else {
            $scope.selectedObjects_list.caption = $scope.selectedGroups.length;
            $scope.states.isSelectedAllObjects = false;
        }
    }
    
    var closeFilter = function () {
                //close filter list
        var btnGroup = document.getElementsByClassName("btn-group open");
        if (!mainSvc.checkUndefinedNull(btnGroup) && (btnGroup.length != 0)) {
            btnGroup[0].classList.remove("open");
        }
        $scope.states.isSelectElement = false;
    };
      
    $scope.selectObjects = function () {
        closeFilter();
        if ($scope.ctrlSettings.showObjectsFlag == true) {
            performObjectsFilter();
    //        $scope.states.applyObjects_flag = true;
        } else {
            performGroupsFilter();
        }
                    //Объекты были выбраны и их выбор был подтвержден нажатием кнопки "Применить"
        loadSessionsData();

    };
    
    $scope.pageChanged = function (newPage) {
//console.log("pageChanged");        
//        $scope.getResultsPage(newPage);
        $scope.ctrlSettings.pagination.current = newPage;
        $scope.data.sessionsOnView = data.sessions.slice(($scope.ctrlSettings.pagination.current - 1) * $scope.ctrlSettings.itemPerPage, ($scope.ctrlSettings.pagination.current) * $scope.ctrlSettings.itemPerPage);
    };
    
    $scope.isDisabledFilters = function () {
        return $scope.ctrlSettings.sessionsLoading || $scope.ctrlSettings.logLoading;
    };
    
    $scope.$on('$destroy', function () {
        //save session table height
        $cookies.heightLogUpperPart = $("#log-upper-part > .rui-resizable-content").height();
        $cookies.heightLogFooterPart = $("#log-footer-part > .rui-resizable-content").height();
        $rootScope.$broadcast('logSvc:cancelInterval');
    });
    
    $(document).ready(function () {
        
//        $("#log-upper-part").resizable({
//            handles: "s",
//            resize: function (event, ui){
//                console.log("#log-upper-part > .rui-resizable-handle click = " + $("#log-upper-part > .rui-resizable-content").height());
//            }
//        });
//        
//        $("#log-footer-part").resizable({
//            handles: "s"
//        });

        $("#log-upper-part > .rui-resizable-handle").on('click', function () {
//console.log("#log-upper-part > .rui-resizable-handle click = " + $("#log-upper-part > .rui-resizable-content").height());
            document.cookie = "heightLogUpperPart=" + $("#log-upper-part > .rui-resizable-content").height();
//            $cookies.heightLogUpperPart = $("#log-upper-part > .rui-resizable-content").height();
        });

        $("#log-footer-part > .rui-resizable-handle").on('click', function () {
//console.log("#log-footer-part > .rui-resizable-handle click = " + $("#log-footer-part > .rui-resizable-content").height());            
            document.cookie = "heightLogFooterPart=" + $("#log-footer-part > .rui-resizable-content").height();
//            $cookies.heightLogFooterPart = $("#log-footer-part > .rui-resizable-content").height();       
        });
        
        $('#object-toggle-view').bootstrapToggle();
        $('#object-toggle-view').change(function () {
            $scope.ctrlSettings.showObjectsFlag = Boolean($(this).prop('checked'));
            $scope.$apply();
            $scope.selectObjectsClick();
            //If no change for request - change only filter caption and filter object list
            if ($scope.selectedObjects_list.caption == $scope.messages.defaultFilterCaption) {
                return "Object / group filter. No changes";
            }
            $scope.clearObjectFilter();
        });
    });
    
    $scope.selectAllElements = function (elements) {
        $scope.states.isSelectElement = true;
        elements.forEach(function (elem) {
            elem.selected = false;
        });
    };
    
    $scope.selectElement = function (flagName) {
        $scope.states.isSelectElement = true;
        $scope.states[flagName] = false;
        return false;
    };
    
    $scope.isFilterApplyDisabled = function (checkElements, checkFlag) {
        if (mainSvc.checkUndefinedNull(checkElements) || !angular.isArray(checkElements)) {
            return false;
        }
        if (checkFlag == true) {
            return false;
        }
        return !checkElements.some(function (elem) {
            if (elem.selected == true) {
                return true;
            }
        });
    };
    
    // **** end of Object filter
    
    function errorCallback(e) {
        $scope.ctrlSettings.sessionsLoading = false;
        $scope.ctrlSettings.logLoading = false;
        console.log(e);
        var errorCode = "-1";
        if (mainSvc.checkUndefinedNull(e) || mainSvc.checkUndefinedNull(e.data)) {
            errorCode = "ERR_CONNECTION";
        }
        if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || (!mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode)))) {
            errorCode = e.resultCode || e.data.resultCode;
        }
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    }
    
    // *******************************************************************************
    // Sessions
    
        //for Refresh button
    $scope.loadSessionsData = function () {
        loadSessionsData();
    };
    
    function getSessionsData() {
        var i;
//console.log("Get session data");        
        data.sessions = logSvc.getSessions();
        //dev
//        data.sessions.push(testSession);
        //end dev
        $scope.ctrlSettings.sessionsLoading = false;
        if (mainSvc.checkUndefinedNull(data.sessions) || data.sessions.length == 0) {
            $scope.data.totalSessions = 0;
            $scope.data.sessionsOnView = [];
//console.log("data.sessions is null -> send request to load data");            
            $timeout(loadSessionsData, REFRESH_PERIOD);
            return "Session array is empty";
        }
        $scope.data.totalSessions = data.sessions.length;
        if (mainSvc.checkUndefinedNull($scope.ctrlSettings.pagination.current)) {
            $scope.ctrlSettings.pagination.current = 1;
        }
//        $scope.ctrlSettings.pagination.current = 1;
        //save current session before update table, that don't lost it
        var tmpCurSession = null;
        if (!mainSvc.checkUndefinedNull($scope.data.currentSession)) {
            tmpCurSession = angular.copy($scope.data.currentSession);
        }
//        $scope.data.sessionsOnView = data.sessions.slice(0, $scope.ctrlSettings.itemPerPage);
        var tmpSessions = null;
        if (mainSvc.checkUndefinedNull($scope.data.sessionsOnView) || $scope.data.sessionsOnView.length > 0) {
            tmpSessions = angular.copy($scope.data.sessionsOnView);
        }
        $scope.data.sessionsOnView = data.sessions.slice(($scope.ctrlSettings.pagination.current - 1) * $scope.ctrlSettings.itemPerPage, ($scope.ctrlSettings.pagination.current) * $scope.ctrlSettings.itemPerPage);
//console.log(tmpSessions);        
        if (!mainSvc.checkUndefinedNull(tmpSessions) && tmpSessions.length > 0) {
            tmpSessions.forEach(function (tmpSes) {
                for (i = 0; i < $scope.data.sessionsOnView.length; i += 1) {
                    if (tmpSes.id == $scope.data.sessionsOnView[i].id) {
                        $scope.data.sessionsOnView[i].isChildView = tmpSes.isChildView;
                    }
                }
            });
        }
        if (!mainSvc.checkUndefinedNull(tmpCurSession)) {
            $scope.data.sessionsOnView.some(function (elem) {
                if (elem.id == tmpCurSession.id) {
                    $scope.loadLogData(elem);
                    return true;
                } else {
                    if (mainSvc.checkUndefinedNull(elem.childs) || elem.childs.length == 0) {
                        return false;
                    }
                    var isCurSessionFound = false;
                    elem.childs.some(function (child) {
                        if (child.id == tmpCurSession.id) {
                            $scope.loadLogData(child);
                            isCurSessionFound = true;
                            return true;
                        }
                    });
                    return isCurSessionFound;
                }
            });
        }
        
    }
    function sessionsLoadedListener() {
//console.log("on logSvc:sessionsLoaded");        
        getSessionsData();
    }
    
    $scope.$on('logSvc:sessionsLoaded', sessionsLoadedListener);
    
 
    
    // **********************************************************************
    // session log
    
    $scope.loadLogData = function (session) {
        $scope.data.currentSession.selected = false;
        session.selected = true;
        $scope.ctrlSettings.logLoading = true;
        $scope.data.currentSession = session;
        var url = $scope.ctrlSettings.sessionsUrl + "/" + session.id + "/steps";
        $http.get(url).then(function (resp) {
            $scope.data.sessionLog = resp.data;
            //dev
//            $scope.data.sessionLog.push(testStep);
            //end dev
            $scope.ctrlSettings.logLoading = false;
        }, errorCallback);
    };
    //********************************
    
    $scope.$watch('ctrlSettings.sessionsLogDaterange', function (newDates, oldDates) {
        if (oldDates == newDates) {
            return;
        }
        loadSessionsData();
    });
    
    $scope.$watch('ctrlSettings.itemPerPage', function (newVal, oldVal) {
        if (newVal == oldVal) {
            return;
        }
        $scope.pageChanged(1);
    });
    
    $scope.checkEmptyObject = function (obj) {
        return mainSvc.checkEmptyObject(obj);
    };
    
    function initCtrl() {
//console.log("Init ctrl");        
//        getSessionsData();
        loadSessionsData();
        //set session and tables height
        $timeout(function () {
            $("#log-upper-part > .rui-resizable-content").height(Number($cookies.heightLogUpperPart));
            $("#log-footer-part > .rui-resizable-content").height(Number($cookies.heightLogFooterPart));
        });
    }
    
    initCtrl();

}]);