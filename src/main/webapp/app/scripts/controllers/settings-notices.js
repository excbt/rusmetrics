'use strict';
angular.module('portalNMC')
.controller('SettingsNoticesCtrl', function($scope, $http, notificationFactory){
console.log("Run SettingsNoticeCtrl");
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.ctxId = "settings_notices";
    $scope.ctrlSettings.noticeTypesUrl = "../api/contEvent/types";
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
    //get all contacts
    $scope.getContacts = function(url){
        $http.get(url)
        .success(function(data){
            $scope.contacts = angular.copy(data);
//console.log($scope.contacts);            
        })
        .error(function(e){
            notificationFactory.errorInfo(e.statusText, e.data.description || e.data);
            console.log(e);            
        });
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
            .error(function(e){
                notificationFactory.errorInfo(e.statusText, e.data.description || e.data);
                console.log(e);
            });
    };
    
    $scope.getNoticeTypes($scope.ctrlSettings.noticeTypesUrl);
    
    $scope.setOrderBy = function(column){
        $scope.ctrlSettings.orderBy.field = column;
        $scope.ctrlSettings.orderBy.asc = !$scope.ctrlSettings.orderBy.asc;
    };
    
    $scope.selectItem = function(item){
        $scope.currentNotice = angular.copy(item);
    };
});