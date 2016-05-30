'use strict';
var app = angular.module('portalNMC');
app.controller('LogViewCtrl', ['$scope', '$cookies', '$timeout', 'mainSvc', 'objectSvc', '$http', function($scope, $cookies, $timeout, mainSvc, objectSvc, $http){
    
    $scope.messages = {};
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.groupUrl = "../api/subscr/contGroup";
    $scope.ctrlSettings.showObjectsFlag = true;
    
    $scope.ctrlSettings.sessionColumns = [
        {
            name: "colorStatus",
            caption: "",
            type: 'color',
            headerClass: "col-xs-1 col-md-1 nmc-td-for-button noPadding"
        },{
            name: "dataSource",
            caption: "Источник",
            headerClass: "col-xs-2 col-md-2 noPadding"
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
            headerClass: "col-xs-1 col-md-1"
        },{
            name: "endDate",
            caption: "Время завершения",
            headerClass: "col-xs-1 col-md-1"
        },{
            name: "author",
            caption: "Инициатор",
            headerClass: "col-xs-2 col-md-2"
        },{
            name: "currentStatus",
            caption: "Текущий статус",
            headerClass: "col-xs-1 col-md-1"
        },{
            name: "totalRow",
            caption: "Число строк данных",
            headerClass: "col-xs-1 col-md-1"
        }
        
    ];
    $scope.ctrlSettings.logColumns = [
        {
            name: "date",
            caption: "Дата-время",
            headerClass: "col-xs-2 col-md-2"
        },{
            name: "type",
            caption: "Тип",
            headerClass: "col-xs-1 col-md-1"
        },{
            name: "text",
            caption: "Текст",
            headerClass: "col-xs-9 col-md-9"
        },
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
    
    /*******************************************/
    /* Test generation*/
    function generate(){
        generateSessions();
        generateSessionLog();
    };
    
    function generateSessions(){
        var sessions = [];
        for (var i = 0; i <= 20; i++){
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
                    child.dataSource = "Прибор " + rndIntNumber +"-"+ j;
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
        for (var i = 0; i <= 124; i++){
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
            for (var j = 0; j<= 20; j++){
                msg += WORDS[Math.round(Math.random()*WORDS.length)]+" ";
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
    
    generate();
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
        var selectedObjectUrl = $scope.ctrlSettings.groupUrl+"/"+group.id+"/contObject";
        $http.get(selectedObjectUrl).then(function(response){
            group.objects = response.data;
        });
    };
    
    var successCallbackGetObjects = function(response){        
        $scope.objects = response.data;
        objectSvc.sortObjectsByFullName($scope.objects);
    };
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
//            $scope.selectObjects();            
        });
//        $("#log-upper-part").resizable({
//            handles: "s",
//            minHeight: 63,
//            maxHeight: 600,
//            alsoResize: "#divWithSessionsTable"
//        });
//        $("#table-container").resizable();
    });

}]);