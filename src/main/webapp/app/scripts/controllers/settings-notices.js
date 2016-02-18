'use strict';
angular.module('portalNMC')
.controller('SettingsNoticesCtrl', function($rootScope, $scope, $http, notificationFactory, mainSvc){
//console.log("Run SettingsNoticeCtrl");
    $rootScope.ctxId = "settings_notices_page";
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.ctxId = "settings_notices";
    $scope.ctrlSettings.apiUrl = "../api/subscr/contEventType";
    $scope.ctrlSettings.noticeTypesUrl = $scope.ctrlSettings.apiUrl+"/actions/available";
    $scope.ctrlSettings.contactsUrl = "../api/subscr/subscrAction/users";
    //the path template of notice icon
    $scope.ctrlSettings.imgPathTmpl = "images/notice-state-";
    
    $scope.ctrlSettings.orderBy = {field: "name", asc: true};
    
    $scope.ctrlSettings.colorLevel = {
        green: {
            name: "green",
            min: 70000,
            max: 79999,
            title: "Уведомление"
        }, 
        yellow: {
            name: "yellow",
            min: 30000,
            max: 59999,
            title: "Уведомление"
        },
        orange: {
            name: "orange",
            min: 20000,
            max: 29999,
            title: "Некритическая ситуация"
        },
        red: {
            name: "red",
            min: 10000,
            max: 19999,
            title: "Критическая ситуация"
        }
    };
    
    $scope.currentNotice = {};//current notice
    
    $scope.columns = [
        {
            "name": "name",
            "header": "Тип",
            "headerClass": "col-xs-5 col-md-5 nmc-button-sort ",
            "dataClass": "col-xs-5 col-md-5"
        },
        {
            "name": "comment",
            "header": "Комментарий",
            "headerClass": "col-xs-5 col-md-5",
            "dataClass": "col-xs-5 col-md-5"
        }
    ];
    
    $scope.contactColumns = [
        {
            "name": "userName",
            "header": "Тип",
            "class": "col-xs-5 col-md-5 nmc-button-sort "
        },
        {
            "name": "comment",
            "header": "Комментарий",
            "class": "col-xs-5 col-md-5"
        }
    ];
    
    $scope.noticeTypes = [];
    $scope.contacts = [];
    
    //callbacks
    var errorCallback = function(e){
        console.log(e);              
//        notificationFactory.errorInfo(e.statusText, e.data.description || e.data || e);
//        console.log(e);
        var errorCode = "-1";
        if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
            errorCode = e.resultCode || e.data.resultCode;
        };
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };
    
    var successGetNoticeTypeSettingsCallback = function(data){
        //0)Перед этим методом я должен получить полный список контактов
        //1)Получить настройки для типа уведомлений
        var tmpTypeSettings = data;
        //2)сопоставить 2 эти списка и расставить соответственно флаги
        var tmpContacts = angular.copy($scope.contacts);
        tmpContacts.forEach(function(contact){
            var settingsLength = tmpTypeSettings.length;
            for (var iCount = 0; iCount < settingsLength; iCount++){
                if (contact.id == tmpTypeSettings[iCount].subscrActionUserId){
                    contact.isSms = tmpTypeSettings[iCount].isSms;
                    contact.isEmail = tmpTypeSettings[iCount].isEmail;
                };
            };
        });
        $scope.currentNotice.contacts = tmpContacts;
        //3)вывести на экран 
        $('#editNoticeModal').modal();
//console.log($scope.currentNotice.contacts);        
    };
    
    var putSuccessCallback = function(data){        
        $('#editNoticeModal').modal('hide');
        $scope.currentNotice = {};
    };
    
    //get all contacts
    $scope.getContacts = function(url){
        $http.get(url)
        .success(function(data){
            $scope.contacts = angular.copy(data);
//console.log($scope.contacts);            
        })
        .error(errorCallback);
    };
    $scope.getContacts($scope.ctrlSettings.contactsUrl);
    
    //get notice types
    $scope.getNoticeTypes = function(url){
       $http.get(url)
            .success(function(data){
                var tmp = angular.copy(data);
            //sort types by name
                tmp.sort(function(a, b){
                    if (a.name > b.name){
                        return 1;
                    };
                    if (a.name < b.name){
                        return -1;
                    };
                    return 0;
                });
           //add img with color for type
                tmp.forEach(function(type){
                    for (var colorCounter in $scope.ctrlSettings.colorLevel){
                        if ((type.contEventLevel >= $scope.ctrlSettings.colorLevel[colorCounter].min) && (type.contEventLevel <= $scope.ctrlSettings.colorLevel[colorCounter].max)){
                            type.color = $scope.ctrlSettings.colorLevel[colorCounter].name;
                            type.imgpath = $scope.ctrlSettings.imgPathTmpl + type.color.toLowerCase()+".png";
                            type.title = $scope.ctrlSettings.colorLevel[colorCounter].title;
                            return;
                        };
                    };
                });
                $scope.noticeTypes = tmp;
//console.log(data);           
            })
            .error(errorCallback);
    };
    $scope.getNoticeTypes($scope.ctrlSettings.noticeTypesUrl);
    
    $scope.getNoticeTypeSettings = function(noticeType){
        $scope.selectItem(noticeType);
        var url = $scope.ctrlSettings.apiUrl + "/" + noticeType.id + "/actions";
        $http.get(url).success(successGetNoticeTypeSettingsCallback).error(errorCallback);
    };
    
    $scope.putNoticeTypeSettings = function(noticeType){
        var url = $scope.ctrlSettings.apiUrl + "/" + noticeType.id + "/actions";
        var settings = [];
        noticeType.contacts.forEach(function(contact){
            if (!mainSvc.checkUndefinedNull(contact.isSms) || !mainSvc.checkUndefinedNull(contact.isEmail)){
                settings.push({subscrActionUserId: contact.id,
                   isSms: contact.isSms || false,
                   isEmail: contact.isEmail || false});
            };
            
        });
//console.log(settings);        
        $http.put(url, settings).success(putSuccessCallback).error(errorCallback);
    };
    
    $scope.setOrderBy = function(column){
        $scope.ctrlSettings.orderBy.field = column;
        $scope.ctrlSettings.orderBy.asc = !$scope.ctrlSettings.orderBy.asc;
    };
    
    $scope.selectItem = function(item){
        $scope.currentNotice = angular.copy(item);
    };    
});