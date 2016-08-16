/*jslint node: true, white: true*/
/*global angular*/
'use strict';
angular.module("portalNMC")
    .controller('AboutProgramCtrl', ['mainSvc', '$scope', '$http', function (mainSvc, $scope, $http) {
        
        var MODULES_URL = "../api/appStatus/appModulesVersions";
        
        $scope.data = {};
        $scope.data.columns = [
            {
                fieldName: "moduleName",
                header: "Наименование",
                headerClass: "col-xs-5 col-md-5 bg-info",
                dataClass: "col-xs-5 col-md-5"
            },{
                fieldName: "moduleVersion",
                header: "Версия",
                headerClass: "col-xs-2 col-md-2 bg-info",
                dataClass: "col-xs-2 col-md-2"
            },{
                fieldName: "moduleReleaseDate",
                header: "Дата выхода",
                headerClass: "col-xs-2 col-md-2 bg-info",
                dataClass: "col-xs-2 col-md-2"
            }
        ];
        $scope.data.modules = [];
        
        function loadModules () {
            var url = MODULES_URL;
            $http.get(url).then(function (response) {
                $scope.data.modules = response.data;
            }, function (e) {
                console.log(e);
            });
            
        }
        
        $scope.isTestMode = function (){
            return mainSvc.isTestMode();
        };
        
        function initCtrl () {
            loadModules();
        }
        
        initCtrl();
    }]);