'use strict';
var app = angular.module('portalNMC');
app.controller('LogSmsCtrl', ['$scope', '$cookies', '$timeout', 'mainSvc', 'objectSvc', '$http', function($scope, $cookies, $timeout, mainSvc, objectSvc, $http){
    
    var LOG_VIEW_LENGTH = 20;
    var COL_NAME_SUFFIX = "view";
    
    $scope.messages = {};
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.groupUrl = "../api/subscr/contGroup";
    $scope.ctrlSettings.showObjectsFlag = true;
    $scope.ctrlSettings.daterangeOpts = mainSvc.getDateRangeOptions("ru");    
    $scope.ctrlSettings.daterangeOpts.dateLimit = {"months": 1}; //set date range limit with 1 month

    $scope.ctrlSettings.logColumns = [
        {
            name: "date",
            caption: "Дата отправления",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 noPadding",            
        },{
            name: "reciever",
            caption: "Кому",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 noPadding"
        },{
            name: "recieverPhone",
            caption: "Номер",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 noPadding"
        },
        {
            name: "text",
            caption: "Текст",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 noPadding",
            type: "textarea"
        },{
            name: "fullUrl",
            caption: "Полный url адрес отправки",
            headerClass: "col-xs-1 col-md-1 paddingLeftRight5I",
            dataClass: "col-xs-1 col-md-1 noPadding",
            isTableView: false,
            type: "textarea"
        },{
            name: "serverCode",
            caption: "Код ответа сервера",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 text-center noPadding"
        },{
            name: "serverText",
            caption: "Текст ответа сервера",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 noPadding",
            type: "textarea",
            viewInNewWindow: true
        },{
            name: "errorField",
            caption: "Поле ошибки",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 noPadding"
        }        
    ];
        //create columns for view in table
    var tmpArr = [];
    for (var i = 0; i < $scope.ctrlSettings.logColumns.length; i++){
        var elem = angular.copy($scope.ctrlSettings.logColumns[i]);
        if (mainSvc.checkUndefinedNull(elem.isTableView) || elem.isTableView != false){
            elem.name += COL_NAME_SUFFIX;
            tmpArr.push(elem);
        }
    };
    $scope.ctrlSettings.logColumnsForTable = tmpArr;
    
    $scope.data = {};
    $scope.data.log = [];
    $scope.data.logDaterange = {startDate: moment().subtract(6, 'days').startOf('day'), 
                                endDate: moment().endOf('day')};
    
    $scope.states = {};
    
    $scope.states.isSelectedAllObjects = true;
    
    $scope.states.isSelectedAllObjectsInWindow = true;
    
    $scope.messages.defaultFilterCaption = "Все";
    $scope.selectedObjects_list = {};//object for object caption params
    $scope.selectedObjects_list.caption = $scope.messages.defaultFilterCaption;
    
    function prepareLogForView(logs){
        logs.forEach(function(el){
            for (var column = 0; column < $scope.ctrlSettings.logColumns.length; column++){                
                el[$scope.ctrlSettings.logColumns[column].name + COL_NAME_SUFFIX] = el[$scope.ctrlSettings.logColumns[column].name].toString().length >= LOG_VIEW_LENGTH ? el[$scope.ctrlSettings.logColumns[column].name].toString().substr(0, LOG_VIEW_LENGTH) + "..." : el[$scope.ctrlSettings.logColumns[column].name].toString();
            }
        });
    }
    
    /*******************************************/
    /* Test generation*/
    var LOG_ROW_COUNT = 125;
    function generate(){
        generateLog();
    };
    
    var serverText = '"<!DOCTYPE html><html><head><title>Apache Tomcat/8.0.20 - Error report</title><style type="text/css">H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}A {color : black;}A.name {color : black;}.line {height: 1px; background-color: #525D76; border: none;}</style> </head><body><h1>HTTP Status 404 - /</h1><div class="line"></div><p><b>type</b> Status report</p><p><b>message</b> <u>/</u></p><p><b>description</b> <u>The requested resource is not available.</u></p><hr class="line"><h3>Apache Tomcat/8.0.20</h3></body></html>"';
    
    function textGenerator(wordCount){
        
        var ABC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-.,!:;";
        var result = "";
        var wordLen = Math.round(Math.random() * 20);
        for (var j = 0; j < wordCount; j++){
            for(var i = 0; i < wordLen; i++){
                result += ABC[Math.round(Math.random() * (ABC.length-1))];
            }
            result += " ";
        }
        return result;
    }

    function loginGenerator(){
        var ABC = "abcdefghijklmnopqrstuvwxyz1234567890_-";
        var result = "";
        var loginLength = Math.round(Math.random() * 20);
        for(var i = 0; i < loginLength; i++){
            result += ABC[Math.round(Math.random() * (ABC.length-1))];
        }
        return result;
    }
    
    function pwdGenerator(){
        var ABC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-!@#$%^&*";
        var result = "";
        var loginLength = Math.round(Math.random() * 20);
        for(var i = 0; i < loginLength; i++){
            result += ABC[Math.round(Math.random() * (ABC.length-1))];
        }
        return result;
    }
    
    function phoneGenerator(){
        var ABC = "1234567890";
        var PHONE_LEN = 10;
        var result = "+7(";        
        for(var i = 0; i < PHONE_LEN; i++){
            result += ABC[Math.round(Math.random() * (ABC.length-1))];
            if (i == 2) result += ")";
            if (i == 5 || i == 7) result += "-";
        }
        return result;
    }
    
    function generateLog(){
        var WORDS = ["Земля", "портфель", "идет", "лопатать", "потом", "дравина", "успел", "растопша", "трап", "мышь", "лететь"];
        var RECIEVERS = ["Земля", "Я", "Он", "Артамонов", "Самый главный", "ООО ЭБТ", "ООО ТТТ", "Большая компания"];
        var SERVER_ERRORS = ["Все ок", "Ошибка связи с сервером", "Отправка запрещена", "Ну и еще что-то стряслось"];
        
        var logs = []; 
        for (var i = 0; i < LOG_ROW_COUNT; i++){
            var log = {};
            var rndNum = Math.random();
            var curMoment = moment();
            log.date = curMoment.format("DD-MM-YYYY HH:mm");
            log.recieverPhone = phoneGenerator();// "+" + curMoment.format("MM") +"("+ curMoment.format("ss") + curMoment.format("D")+")" + curMoment.format("mm")+curMoment.format("s") + "-" + curMoment.format("DD")+"-"+curMoment.format("MM");
            log.reciever = RECIEVERS[Math.round(Math.random() * (RECIEVERS.length - 1))];
            
            log.fullUrl = "http://testsmsservice/send.php?login=" + loginGenerator() + "&psw=" + pwdGenerator() + "&text=" + textGenerator(300);
            log.serverCode = Math.round(Math.random() * 505 + 100);
            log.serverText = serverText;//textGenerator(300);
            log.errorField = SERVER_ERRORS[Math.round(Math.random()*(SERVER_ERRORS.length-1))];
            
            var msg = "";
            for (var j = 0; j <= 20; j++){
                msg += WORDS[Math.round(Math.random() * (WORDS.length - 1))] + " ";
            };
            log.text = msg;
            logs.push(log);
        };
        prepareLogForView(logs);
        $scope.data.log = logs;       
    };
    
    generate();
    /* end Test generation*/
    /*************************************************************/
    
    $scope.viewDetail = function(log, column){
        if (!mainSvc.checkUndefinedNull(column.viewInNewWindow) && column.viewInNewWindow == true){
            var newWin = window.open("about:blank", "Server respond");            
            var dataColumnName = column.name.substr(0, column.name.indexOf(COL_NAME_SUFFIX));            
            newWin.document.write(log[dataColumnName]);
            return true;
        };
        $scope.data.currentLog = angular.copy(log);
        $("#showLogModal").modal();
    }
}]);