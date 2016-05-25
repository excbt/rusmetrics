'use strict';
var app = angular.module('portalNMC');
app.controller('LogViewCtrl', ['$scope', function($scope){
    $scope.ctrlSettings = {};
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
    };
    
    function generateSessionLog(){
        var WORDS = ["Земля", "портфель", "идет", "лопатать", "потом", "дравина", "успел", "растопша", "трап", "мышь", "лететь"];
        var logs = []; 
        for (var i = 0; i <= 24; i++){
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
    };
    
    generate();
    /* end Test generation*/
    /*************************************************************/
    
    $(document).ready(function(){
        $("#log-upper-part").resizable({
            handles: "s",
            minHeight: 63,
            maxHeight: 600,
            alsoResize: "#table-container"
        });
//        $("#table-container").resizable();
    });
}]);