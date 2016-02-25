angular.module('portalNMC')
.controller('ElectricityPkeCtrl', function($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope){
console.log("Run ElectricityPkeCtrl.");
    $scope.electroKind = "Pke";
    
    $scope.pkeTypes = [
        {"name": "type1",
         "caption": "Тип 1"
        },
        {"name": "type2",
         "caption": "Тип 2"
        }
    ];
    var intervalSettings = {};
    $scope.indicatorPkeDates = {
        startDate :  moment().subtract(6, 'days').startOf('day'),
        endDate : moment().endOf('day')
    };
    intervalSettings.minDate = moment().subtract(30, 'days').startOf('day');    
    $scope.dateRangeOptsPkeRu = mainSvc.getDateRangeOptions("indicator-ru", intervalSettings);
console.log($scope.dateRangeOptsPkeRu);    
    
    ///api/subscr/66948436/serviceElProfile/30min/159919982
    //{beginDate=[2015-12-01], endDate=[2015-12-31]}
    var apiSubscUrl = "../api/subscr/";
    var timeDetailType = "abs";
    var viewMode = "serviceElPke";
    $scope.dataUrl = apiSubscUrl + $scope.contObject + "/" + viewMode + "/" + timeDetailType + "/" + $scope.contZPoint;
    var pkeTypesUrl = "";
    $scope.columns = [
        {
            header : "Дата начала",
            headerClass : "col-xs-2 col-md-2 nmc-text-align-center",
            dataClass : "col-xs-2 col-md-2 nmc-text-align-right",
            fieldName: "startDataDateString",
            type: "string",
            date: true
        }, 
        {
            header : "Дата окончания",
            headerClass : "col-xs-2 col-md-2 nmc-text-align-center",
            dataClass : "col-xs-2 col-md-2 nmc-text-align-right",
            fieldName: "endDataDateString",
            type: "string",
            date: true
        },
        {
            header : "Значение",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data nmc-text-align-center",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data nmc-text-align-right",
            fieldName: "warnEndDate"
        },        
        {
            header : "Тип",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data nmc-text-align-center",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data nmc-text-align-right",
            fieldName: "deviceObjectPkeType"
        }
    ];    
});