angular.module('portalNMC')
.controller('ElectricityEnergyCtrl', function($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope, notificationFactory){
//console.log("Run ElectricityEnergyCtrl.");

    $scope.electroKind = "Energy";
    ///api/subscr/66948436/serviceElProfile/30min/159919982
    //{beginDate=[2015-12-01], endDate=[2015-12-31]}
    var apiSubscUrl = "../api/subscr/";
    var timeDetailType = "30min";
    var viewMode = "serviceElProfile";
    $scope.dataUrl = apiSubscUrl + $scope.contObject + "/" + viewMode + "/" + timeDetailType + "/" + $scope.contZPoint;
    $scope.columns = [
        {
            header : "Дата",
            headerClass : "col-xs-2 col-md-2 nmc-text-align-center",
            dataClass : "col-xs-2 col-md-2 nmc-text-align-right",
            fieldName: "dataDateString",
            type: "string",
            date: true
        }, 
        {
            header : "Интервал, мин",
            headerClass : "col-xs-1 col-md-1 nmc-text-align-center",
            dataClass : "col-xs-1 col-md-1 nmc-text-align-right",
            fieldName: "profileInterval",
            type: "string"
        },
        {
            header : "A+, кВт/ч",
            headerClass : "col-xs-1 col-md-1 nmc-text-align-center",
            dataClass : "col-xs-1 col-md-1 nmc-text-align-right",
            fieldName: "p_Ap"
        },
        {
            header : "A-, кВт/ч",
            headerClass : "col-xs-1 col-md-1 nmc-text-align-center",
            dataClass : "col-xs-1 col-md-1 nmc-text-align-right",
            fieldName: "p_An"
        },
        {
            header : "R+, кВАр/ч",
            headerClass : "col-xs-1 col-md-1 nmc-text-align-center",
            dataClass : "col-xs-1 col-md-1 nmc-text-align-right",
            fieldName: "q_Rp"
        },
        {
            header : "R-, кВАр/ч",
            headerClass : "col-xs-1 col-md-1 nmc-text-align-center",
            dataClass : "col-xs-1 col-md-1 nmc-text-align-right",
            fieldName: "q_Rn"
        },
    ];
    
});