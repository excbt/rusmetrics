/*jslint node: true, white: true*/
/*global angular*/
'use strict';
angular.module("portalNMC")
    .controller('AboutProgramCtrl', ['mainSvc', '$scope', '$http', function (mainSvc, $scope, $http) {
        
        var MODULES_URL = "";
        
        $scope.data = {};
        $scope.data.columns = [
            {
                fieldName: "caption",
                header: "Наименование",
                headerClass: "col-xs-2 col-md-2 bg-info",
                dataClass: "col-xs-2 col-md-2"
            },{
                fieldName: "version",
                header: "Версия",
                headerClass: "col-xs-2 col-md-2 bg-info",
                dataClass: "col-xs-2 col-md-2"
            },{
                fieldName: "date",
                header: "Дата выхода",
                headerClass: "col-xs-2 col-md-2 bg-info",
                dataClass: "col-xs-2 col-md-2"
            }
        ]
        $scope.data.modules = [
            {
                id: 1,
                name: "module1",
                caption: "Модуль 1",
                version: "Версия 1",
                date: "15-03-2015"
            },{
                id: 2,
                name: "mainModule",
                caption: "Главный модуль",
                version: "60810",
                date: "10-08-2016"
            },{
                id: 3,
                name: "reportModule",
                caption: "Модуль отчетов",
                version: "60810-1",
                date: "10-08-2016"
            },{
                id: 4,
                name: "adminModule",
                caption: "Модуль управления",
                version: "60810-2",
                date: "10-08-2016"
            },
        ]
        
        function loadModules () {
            var url = "";
            $http.get(url).then(function (response) {
                $scope.data.modules = response.data;
            }, function (e) {
                console.log(e);
            });
            
        }
        
        $scope.isTestMode = function (){
            return mainSvc.isTestMode()
        }
        
        function initCtrl () {
        }
        
        initCtrl();
    }]);