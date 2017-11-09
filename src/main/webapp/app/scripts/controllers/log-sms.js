/*jslint node: true, eqeq: true*/
/*global angular, moment, $*/
'use strict';
var app = angular.module('portalNMC');
app.controller('LogSmsCtrl', ['$scope', '$timeout', 'mainSvc', 'objectSvc', '$http', 'notificationFactory', function ($scope, $timeout, mainSvc, objectSvc, $http, notificationFactory) {
    
    var LOG_VIEW_LENGTH = 20;
    var COL_NAME_SUFFIX = "view";
    
    $scope.messages = {};
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.smsLogUrl = "../api/subscr/smsLog";
    $scope.ctrlSettings.groupUrl = "../api/subscr/contGroup";
    $scope.ctrlSettings.showObjectsFlag = true;
    $scope.ctrlSettings.smsLogDaterange = {
        startDate: moment().startOf('day'),
        endDate: moment().endOf('day')
    };
    $scope.ctrlSettings.systemDateFormat = "YYYY-MM-DD";
    $scope.ctrlSettings.userDateFormat = "DD-MM-YYYY HH:mm:ss";
    $scope.ctrlSettings.daterangeOpts = mainSvc.getDateRangeOptions("ru");
    $scope.ctrlSettings.daterangeOpts.startDate = moment().startOf('day');
    $scope.ctrlSettings.daterangeOpts.endDate = moment().endOf('day');
    $scope.ctrlSettings.daterangeOpts.dateLimit = {"months": 1}; //set date range limit with 1 month    

    $scope.ctrlSettings.logColumns = [
        {
            name: "smsDateStr",
            caption: "Дата отправления",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 noPadding",
            type: "datetime"
        }, {
            name: "smsRecieverName",
            caption: "Кому",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 noPadding"
        }, {
            name: "smsRecieverTel",
            caption: "Номер",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 noPadding"
        },
        {
            name: "smsMessage",
            caption: "Текст",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 noPadding",
            type: "textarea"
        }, {
            name: "smsProviderUrl",
            caption: "Полный url адрес отправки",
            headerClass: "col-xs-1 col-md-1 paddingLeftRight5I",
            dataClass: "col-xs-1 col-md-1 noPadding",
            isTableView: false,
            type: "textarea"
        }, {
            name: "smsProviderResponseCode",
            caption: "Код ответа сервера",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 text-center noPadding"
        }, {
            name: "smsProviderResponseBody",
            caption: "Текст ответа сервера",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 noPadding",
            type: "textarea",
            viewInNewWindow: true
        }, {
            name: "smsStatus",
            caption: "Статус",
            headerClass: "col-xs-1 col-md-1",
            dataClass: "col-xs-1 col-md-1 noPadding"
        }
    ];
        //create columns for view in table
    var tmpArr = [],
        i;
    for (i = 0; i < $scope.ctrlSettings.logColumns.length; i += 1) {
        var elem = angular.copy($scope.ctrlSettings.logColumns[i]);
        if (mainSvc.checkUndefinedNull(elem.isTableView) || elem.isTableView != false) {
            elem.name += COL_NAME_SUFFIX;
            tmpArr.push(elem);
        }
    }
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
    
    function errorCallback(e) {
        $scope.ctrlSettings.loading = false;
        var errorObj = mainSvc.errorCallbackHandler(e);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    }
    
    function prepareLogForView(logs) {
        var column;
        logs.forEach(function (el) {
            el.smsDateStr = moment(el.smsDate).format($scope.ctrlSettings.userDateFormat);
            for (column = 0; column < $scope.ctrlSettings.logColumns.length; column += 1) {
                el[$scope.ctrlSettings.logColumns[column].name + COL_NAME_SUFFIX] = el[$scope.ctrlSettings.logColumns[column].name].toString().length >= LOG_VIEW_LENGTH ? el[$scope.ctrlSettings.logColumns[column].name].toString().substr(0, LOG_VIEW_LENGTH) + "..." : el[$scope.ctrlSettings.logColumns[column].name].toString();
            }
        });
    }
    
    /*******************************************/
    /* Test generation*/
    var LOG_ROW_COUNT = 125;
    
    var serverText = '"<!DOCTYPE html><html><head><title>Apache Tomcat/8.0.20 - Error report</title><style type="text/css">H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}A {color : black;}A.name {color : black;}.line {height: 1px; background-color: #525D76; border: none;}</style> </head><body><h1>HTTP Status 404 - /</h1><div class="line"></div><p><b>type</b> Status report</p><p><b>message</b> <u>/</u></p><p><b>description</b> <u>The requested resource is not available.</u></p><hr class="line"><h3>Apache Tomcat/8.0.20</h3></body></html>"';
    
    function textGenerator(wordCount) {
        
        var ABC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-.,!:;";
        var result = "";
        var wordLen = Math.round(Math.random() * 20);
        var j, i;
        for (j = 0; j < wordCount; j += 1) {
            for (i = 0; i < wordLen; i += 1) {
                result += ABC[Math.round(Math.random() * (ABC.length - 1))];
            }
            result += " ";
        }
        return result;
    }

    function loginGenerator() {
        var ABC = "abcdefghijklmnopqrstuvwxyz1234567890_-";
        var result = "";
        var loginLength = Math.round(Math.random() * 20);
        var i;
        for (i = 0; i < loginLength; i += 1) {
            result += ABC[Math.round(Math.random() * (ABC.length - 1))];
        }
        return result;
    }
    
    function pwdGenerator() {
        var ABC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-!@#$%^&*";
        var result = "";
        var loginLength = Math.round(Math.random() * 20);
        var i;
        for (i = 0; i < loginLength; i += 1) {
            result += ABC[Math.round(Math.random() * (ABC.length - 1))];
        }
        return result;
    }
    
    function phoneGenerator() {
        var ABC = "1234567890";
        var PHONE_LEN = 10;
        var result = "+7(";
        var i;
        for (i = 0; i < PHONE_LEN; i += 1) {
            result += ABC[Math.round(Math.random() * (ABC.length - 1))];
            if (i == 2) { result += ")"; }
            if (i == 5 || i == 7) { result += "-"; }
        }
        return result;
    }
    
    function generateLog() {
        var WORDS = ["Земля", "портфель", "идет", "лопатать", "потом", "дравина", "успел", "растопша", "трап", "мышь", "лететь"];
        var RECIEVERS = ["Земля", "Я", "Он", "Артамонов", "Самый главный", "ООО ЭБТ", "ООО ТТТ", "Большая компания"];
        var SERVER_ERRORS = ["Все ок", "Ошибка связи с сервером", "Отправка запрещена", "Ну и еще что-то стряслось"];
        
        var logs = [],
            i,
            j;
        for (i = 0; i < LOG_ROW_COUNT; i += 1) {
            var log = {};
            var rndNum = Math.random();
            var curMoment = moment();
            log.date = curMoment.format("DD-MM-YYYY HH:mm");
            log.recieverPhone = phoneGenerator();// "+" + curMoment.format("MM") +"("+ curMoment.format("ss") + curMoment.format("D")+")" + curMoment.format("mm")+curMoment.format("s") + "-" + curMoment.format("DD")+"-"+curMoment.format("MM");
            log.reciever = RECIEVERS[Math.round(Math.random() * (RECIEVERS.length - 1))];
            
            log.fullUrl = "http://testsmsservice/send.php?login=" + loginGenerator() + "&psw=" + pwdGenerator() + "&text=" + textGenerator(300);
            log.serverCode = Math.round(Math.random() * 505 + 100);
            log.serverText = serverText;//textGenerator(300);
            log.errorField = SERVER_ERRORS[Math.round(Math.random() * (SERVER_ERRORS.length - 1))];
            
            var msg = "";
            for (j = 0; j <= 20; j += 1) {
                msg += WORDS[Math.round(Math.random() * (WORDS.length - 1))] + " ";
            }
            log.text = msg;
            logs.push(log);
        }
        prepareLogForView(logs);
        $scope.data.log = logs;
    }

    function generate() {
        generateLog();
    }
//    generate();
    /* end Test generation*/
    /*************************************************************/
    
    function loadSmsLogData() {
        $scope.ctrlSettings.loading = true;
        var url = $scope.ctrlSettings.smsLogUrl;
        var params = {};
        if (!mainSvc.checkUndefinedNull($scope.ctrlSettings.smsLogDaterange.startDate)) {
            params.fromDate = moment($scope.ctrlSettings.smsLogDaterange.startDate).format($scope.ctrlSettings.systemDateFormat);
            url += "?fromDate=" + params.fromDate;
        }
        if (!mainSvc.checkUndefinedNull($scope.ctrlSettings.smsLogDaterange.endDate)) {
            params.toDate = moment($scope.ctrlSettings.smsLogDaterange.endDate).format($scope.ctrlSettings.systemDateFormat);
            url += "&toDate=" + params.toDate;
        }
        $http.get(url).then(function (resp) {
            var logs = angular.copy(resp.data);
            prepareLogForView(logs);
            $scope.data.log = logs;
            $scope.ctrlSettings.loading = false;
        }, errorCallback);
    }
    
    $scope.loadSmsLogData = function () {
        loadSmsLogData();
    };
    
    $scope.viewDetail = function (log, column) {
        if (!mainSvc.checkUndefinedNull(column.viewInNewWindow) && column.viewInNewWindow == true) {
            var newWin = window.open("about:blank", "Server respond");
            var dataColumnName = column.name.substr(0, column.name.indexOf(COL_NAME_SUFFIX));
            newWin.document.write(log[dataColumnName]);
            return true;
        }
        $scope.data.currentLog = angular.copy(log);
        $("#showLogModal").modal();
    };
    
    $scope.$watch('ctrlSettings.smsLogDaterange', function (newDates, oldDates) {
        if (newDates.startDate == oldDates.startDate && newDates.endDate == oldDates.endDate) {
            return;
        }
        loadSmsLogData();
    });
    
    function initCtrl() {
        loadSmsLogData();
    }
    initCtrl();
}]);