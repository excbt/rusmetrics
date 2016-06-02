'use strict';
var app = angular.module('portalNMC');
app.controller('LogViewCtrl', ['$scope', '$cookies', '$timeout', 'mainSvc', 'objectSvc', '$http', 'notificationFactory', function($scope, $cookies, $timeout, mainSvc, objectSvc, $http, notificationFactory){
    
    $scope.messages = {};
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.sessionsUrl = "../api/rma/logSessions";
        
    $scope.ctrlSettings.groupUrl = "../api/subscr/contGroup";
    $scope.ctrlSettings.showObjectsFlag = true;
    
    $scope.ctrlSettings.daterangeOpts = mainSvc.getDateRangeOptions("ru");    
    $scope.ctrlSettings.daterangeOpts.dateLimit = {"months": 1}; //set date range limit with 1 month
    $scope.ctrlSettings.sessionsLogDaterange = {
        startDate: moment().subtract(6, 'days').startOf('day'),                        
        endDate: moment().endOf('day')};
    $scope.ctrlSettings.systemDateFormat = "YYYY-MM-DD";
    
    $scope.ctrlSettings.sessionColumns = [
        {
            name: "colorStatus",
            caption: "",
            type: 'color',
            headerClass: "col-xs-1 col-md-1 nmc-td-for-button noPadding"
        },{
            name: "dataSource",
            caption: "Источник",
            headerClass: "col-xs-2 col-md-2 noPadding",
            type: 'id'
        },{
            name: "deviceModel",
            caption: "Модель",
            headerClass: "col-xs-1 col-md-1"
        },{
            name: "deviceNumber",
            caption: "Номер прибора",
            headerClass: "col-xs-1 col-md-1"
        },{
            name: "startDate",
            caption: "Время начала",
            headerClass: "col-xs-2 col-md-2 noPadding"
        },{
            name: "endDate",
            caption: "Время завершения",
            headerClass: "col-xs-2 col-md-2 noPadding"
        },{
            name: "author",
            caption: "Инициатор",
            headerClass: "col-xs-1 col-md-1"
        },{
            name: "currentStatus",
            caption: "Текущий статус",
            headerClass: "col-xs-1 col-md-1"
        },{
            name: "sessionMessage",
            caption: "Сообщение",
            headerClass: "col-xs-1 col-md-1"
        }
        
    ];
    $scope.ctrlSettings.logColumns = [
        {
            name: "stepDateStr",
            caption: "Дата-время",
            headerClass: "col-xs-2 col-md-2",
            type: "id"
        },{
            name: "stepType",
            caption: "Тип",
            headerClass: "col-xs-1 col-md-1"
        },{
            name: "stepMessage",
            caption: "Текст",
            headerClass: "col-xs-7 col-md-7"
        },{
            name: "isIncremental",
            caption: "Счетчик",
            headerClass: "col-xs-1 col-md-1",
            type: "checkbox"
            
        },{
            name: "sumRows",
            caption: "Значение",
            headerClass: "col-xs-1 col-md-1"
        }
        
    ];
    
    $scope.data = {};
    $scope.data.sessions = [];
    $scope.data.sessionLog = [];
    
    $scope.states = {};
    
    $scope.states.isSelectedAllObjects = true;
    
    $scope.states.isSelectedAllObjectsInWindow = true;
    
    $scope.messages.defaultFilterCaption = "Все";
    $scope.selectedObjects_list = {};//object for object caption params
    $scope.selectedObjects_list.caption = $scope.messages.defaultFilterCaption;
    
    $scope.toggleChildSessionsView = function(session){
        session.isChildView = !session.isChildView;
    };
    
    $scope.isSystemuser = function(){
        return mainSvc.isSystemuser();
    };
    
    /*******************************************/
    /* Test generation*/
    
    var SESSION_ROW_COUNT = 21;
    var LOG_ROW_COUNT = 125;
    function generate(){
        generateSessions();
        generateSessionLog();
    };
    
    function generateSessions(){
        var sessions = [];
        for (var i = 0; i < SESSION_ROW_COUNT; i++){
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
            ses.currentStatus = statusColor < .2 ? "Закрыта" : "Открыта";
            ses.colorStatus = statusColor < .2 ? "RED" : "GREEN";
            ses.totalRow = Math.round(statusColor * 10);
            
            if (statusColor <= 0.3){
                ses.type = "OID";
                ses.childs = [];
                for (var j = 0; j <= Math.round(statusColor * 10); j++){
                    var child = angular.copy(ses);
                    child.type = "OP";
                    child.dataSource = "Прибор " + rndIntNumber + "-" + j;
                    ses.childs.push(child);
                };
            };  
            sessions.push(ses);
//console.log(ses);            
        };
        $scope.data.sessions = sessions;
        //set session table height
        $timeout(function(){
            $("#log-upper-part > .rui-resizable-content").height(Number($cookies.heightLogUpperPart));    
        }); 
    };
    
    function generateSessionLog(){
        var WORDS = ["Земля", "портфель", "идет", "лопатать", "потом", "дравина", "успел", "растопша", "трап", "мышь", "лететь"];
        var logs = []; 
        for (var i = 0; i < LOG_ROW_COUNT; i++){
            var log = {};
            var rndNum = Math.random();
            log.date = moment().format("DD-MM-YYYY HH:mm");
            if (rndNum <= 0.3){
                log.type = "Аларм";
            }else if (rndNum > .3 && rndNum <= .6){
                log.type = "Варнинг";
            }else{
                log.type = "Инфо";
            };
            var msg = "";
            for (var j = 0; j <= 20; j++){
                msg += WORDS[Math.round(Math.random() * WORDS.length)] + " ";
            };
            log.text = msg;
            logs.push(log);
        };
        $scope.data.sessionLog = logs;
        
                //set log table height
        $timeout(function(){
            $("#log-footer-part > .rui-resizable-content").height(Number($cookies.heightLogFooterPart));    
        });  
    };
    
//    generate();
    /* end Test generation*/
    /*************************************************************/
    
    $scope.clearObjectFilter = function(){               
        $scope.objects.forEach(function(el){
            el.selected = false;
        });
        $scope.groups.forEach(function(el){
            el.selected = false;
        });
        $scope.selectedObjects_list.caption = $scope.messages.defaultFilterCaption;
        $scope.selectedObjects = [];
        $scope.selectedGroups = [];
        $scope.states.isSelectedAllObjects = true;
    };
    
        // Открыть окно выбора объектов
    $scope.selectObjectsClick = function(){
        $scope.states.isSelectElement = false;
        $scope.states.isSelectedAllObjectsInWindow = angular.copy($scope.states.isSelectedAllObjects);
        if ($scope.ctrlSettings.showObjectsFlag == true){
            $scope.objectsInWindow = angular.copy($scope.objects);
        }else{
            $scope.objectsInWindow = angular.copy($scope.groups);
        };        
    };
    
            //perform objects from groups
    function getGroupObjects(group){
        var selectedObjectUrl = $scope.ctrlSettings.groupUrl + "/" + group.id + "/contObject";
        $http.get(selectedObjectUrl).then(function(response){
            group.objects = response.data;
        });
    };
    
    var successCallbackGetObjects = function(response){        
        $scope.objects = response.data;
        objectSvc.sortObjectsByFullName($scope.objects);
    };
    
    // ************* Object filter *****************************************
    
                 //get Objects
    $scope.getObjects = function(){
//        crudGridDataFactory($scope.objectsUrl).query(function(data){
        if (mainSvc.isRma()){
            objectSvc.rmaPromise.then(successCallbackGetObjects);
        }else
            objectSvc.promise.then(successCallbackGetObjects);
    };
    
    $scope.getObjects();
    
    //get groups
    $scope.getGroups = function () {
        var url = $scope.ctrlSettings.groupUrl;
        $http.get(url).then(function (response) {
            var tmp = response.data;
            tmp.forEach(function(el){
                getGroupObjects(el);
            });
            $scope.groups = tmp;
//console.log($scope.groups);
        }, function(e){
            console.log(e);
        });
    };
    $scope.getGroups();
    
    function performObjectsFilter(){
        $scope.selectedObjects_list.caption = $scope.messages.defaultFilterCaption;
        $scope.selectedObjects = [];
//        $('#selectObjectsModal').modal('hide');
        $scope.objects = angular.copy($scope.objectsInWindow);

        $scope.objects.map(function(el){
          if(el.selected){
//              $scope.selectedObjects_list+=el.fullName+"; ";
              $scope.selectedObjects.push(el.id);
          }
        });
        if ($scope.selectedObjects.length == 0){
            $scope.selectedObjects_list.caption = $scope.messages.defaultFilterCaption;
            $scope.states.isSelectedAllObjects = true;
        }else{
            $scope.selectedObjects_list.caption = $scope.selectedObjects.length;
            $scope.states.isSelectedAllObjects = false;
        };
    };
    
    $scope.joinObjectsFromSelectedGroups = function(groups){
        var result = [];
        groups.forEach(function(group){
                if(group.selected){
                    Array.prototype.push.apply(result, group.objects);
//                    totalGroupObjects = group.objects;
                };
        });                 
        return result;
    };
    
    $scope.deleteDoublesObjects = function(targetArray){
        var arrLength = targetArray.length;
        while (arrLength>=2){
            arrLength--;                                               
            if (targetArray[arrLength].fullName===targetArray[arrLength-1].fullName){                   
                targetArray.splice(arrLength, 1);
            };
        }; 
    };
    
    $scope.addUniqueObjectsFromGroupsToSelectedObjects = function(arrFrom, arrTo){
        for (var j=0; j < arrFrom.length; j++){
            var uniqueFlag = true;
            for (var i = 0; i<arrTo.length; i++){
                if(arrFrom[j].fullName===arrTo[i].fullName){
                    uniqueFlag = false;
                    break;
                };
            };
            if (uniqueFlag){
                arrTo.push(arrFrom[j]);
            };
        }; 
        
    };
    
    function performGroupsFilter(){
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
        var tmp = $scope.selectedObjects.map(function(el){
            return el.id;
        });
        $scope.selectedObjects = tmp;
//console.log($scope.selectedObjects);        
        $scope.groups.map(function(el){
          if(el.selected){
//              $scope.selectedObjects_list+=el.fullName+"; ";
              $scope.selectedGroups.push(el.id);
          };
        });
        if ($scope.selectedGroups.length == 0){
            $scope.selectedObjects_list.caption = $scope.messages.defaultFilterCaption;
            $scope.states.isSelectedAllObjects = true;
        }else{
            $scope.selectedObjects_list.caption = $scope.selectedGroups.length;
            $scope.states.isSelectedAllObjects = false;
        };
    };
    
    var closeFilter = function(){
                //close filter list
        var btnGroup = document.getElementsByClassName("btn-group open");   
        if (!mainSvc.checkUndefinedNull(btnGroup) && (btnGroup.length != 0)){            
            btnGroup[0].classList.remove("open");
        };
        $scope.states.isSelectElement = false;
    };
      
    $scope.selectObjects = function(){  
        closeFilter();        
        if ($scope.ctrlSettings.showObjectsFlag == true){
             performObjectsFilter();
    //        $scope.states.applyObjects_flag = true;
        }else{
            performGroupsFilter();
        };
                    //Объекты были выбраны и их выбор был подтвержден нажатием кнопки "Применить"
        //$scope.getData();

    };
    
    $scope.$on('$destroy', function() {
        //save session table height
        $cookies.heightLogUpperPart = $("#log-upper-part > .rui-resizable-content").height();
        $cookies.heightLogFooterPart = $("#log-footer-part > .rui-resizable-content").height();
    });
    
    $(document).ready(function(){      
        $('#object-toggle-view').bootstrapToggle();
        $('#object-toggle-view').change(function(){
            $scope.ctrlSettings.showObjectsFlag = Boolean($(this).prop('checked'));
            $scope.$apply();                        
            $scope.selectObjectsClick();
            //If no change for request - change only filter caption and filter object list
            if ($scope.selectedObjects_list.caption == $scope.messages.defaultFilterCaption){
                return "Object / group filter. No changes";
            };
            $scope.clearObjectFilter();
            $scope.$broadcast('notices:selectObjects');          
        });
    });
    
    $scope.selectAllElements = function(elements){ 
        $scope.states.isSelectElement = true;
        elements.forEach(function(elem){
            elem.selected = false;
        });
    };
    
    
    $scope.selectElement = function(flagName){
        $scope.states.isSelectElement = true;
        $scope.states[flagName] = false;        
        return false;
    };
    
    $scope.isFilterApplyDisabled = function(checkElements, checkFlag){
        if (mainSvc.checkUndefinedNull(checkElements) || !angular.isArray(checkElements)){
            return false;
        }
        if (checkFlag == true){
            return false;
        };
        return !checkElements.some(function(elem){
            if (elem.selected == true){
                return true;
            };
        });
    };
    
    // **** end of Object filter
    
    function errorCallback(e){
        $scope.ctrlSettings.sessionsLoading = false;
        $scope.ctrlSettings.logLoading = false;
        console.log(e);
        var errorCode = "-1";
        if (mainSvc.checkUndefinedNull(e) || mainSvc.checkUndefinedNull(e.data)){
            errorCode = "ERR_CONNECTION";
        };
        if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
            errorCode = e.resultCode || e.data.resultCode;
        };
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);       
    };
    
    // *******************************************************************************
    // Sessions
    
    function loadSessionsData(){
        $scope.ctrlSettings.sessionsLoading = true;
        var url = $scope.ctrlSettings.sessionsUrl + "?fromDate=" + moment($scope.ctrlSettings.sessionsLogDaterange.startDate).format($scope.ctrlSettings.systemDateFormat) + "&toDate=" + moment($scope.ctrlSettings.sessionsLogDaterange.endDate).format($scope.ctrlSettings.systemDateFormat);
        $http.get(url).then(function(resp){
            $scope.data.sessions = serverDataParser(angular.copy(resp.data));
            //set session and tables height
            $timeout(function(){
                $("#log-upper-part > .rui-resizable-content").height(Number($cookies.heightLogUpperPart));    
                $("#log-footer-part > .rui-resizable-content").height(Number($cookies.heightLogFooterPart));    
            });                
        }, errorCallback);
    }
    
    function serverDataParser(data){        
        var result = data.map(function(dataRow){
            var tmpParsedRow = {};
            tmpParsedRow.id = dataRow.id;
            tmpParsedRow.dataSource = dataRow.dataSourceInfo.caption || dataRow.dataSourceInfo.dataSourceName;
            tmpParsedRow.deviceModel = dataRow.deviceObjectInfo.deviceModelName;
            tmpParsedRow.deviceNumber = dataRow.deviceObjectInfo.number;
            tmpParsedRow.startDate = dataRow.sessionDateStr;
            tmpParsedRow.endDate = dataRow.sessionEndDateStr;
            tmpParsedRow.author = dataRow.authorInfo.authorName;
            tmpParsedRow.currentStatus = dataRow.sessionStatus;
            tmpParsedRow.sessionMessage = dataRow.sessionMessage;
            return tmpParsedRow;
        });
        return result;        
    }
    
    function defineChildSessions(){
        var tmpData = angular.copy($scope.data.sessions);
        var newData = [];
        var childSessions = [];
            //find child sessions
        tmpData.forEach(function(session){
            if (!mainSvc.checkUndefinedNull(session.masterSessionUuid)){
                childSessions[session.masterSessionUuid].push(angular.copy(session));
            }else{
                newData.push(session);
            }
        });
            //add child sessions to their master sessions
        for (var uuid in childSessions){    
            newData.some(function(masterSession){
                if (uuid == masterSession.sessionUuid){
                    masterSession.childs = childSessions[uuid];
                    return true;
                }
            });
        }
        
        $scope.data.sessions = newData;
    }
    
    // **********************************************************************
    // session log
    
    $scope.loadLogData = function(session){
        $scope.data.currentSession = session;
        var url = $scope.ctrlSettings.sessionsUrl + "/" + session.id + "/steps";
        $http.get(url).then(function(resp){
            $scope.data.sessionLog = resp.data;                
        }, errorCallback)
    }
    
    function initCtrl(){
        loadSessionsData();
        defineChildSessions();
    }
    
    initCtrl();

}]);