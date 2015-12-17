angular.module('portalNMC')
.controller('ElectricitySpecCtrl', function($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope){
console.log("Run ElectricitySpecCtrl.");
    $scope.electroKind = "Spec";
    ///api/subscr/66948436/serviceElProfile/30min/159919982
    //{beginDate=[2015-12-01], endDate=[2015-12-31]}
    var apiSubscUrl = "../api/subscr/";
    var timeDetailType = "abs";
    var viewMode = "serviceElTech";
    $scope.dataUrl = apiSubscUrl + $scope.contObject + "/" + viewMode + "/" + timeDetailType + "/" + $scope.contZPoint;
    $scope.columns = [
        {
            header : "Дата",
            headerClass : "col-xs-2 col-md-2 nmc-text-align-center",
            dataClass : "col-xs-2 col-md-2",
            fieldName: "dataDateString",
            type: "string",
            date: true
        }, 
        {
            header : "Ua, V",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "u1"
        },
        {
            header : "Ub, V",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "u2"
        },
        {
            header : "Uc, V",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "u3"
        },
        {
            header : "Ia, A",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "i1"
        },
        {
            header : "Ib, A",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "i2"
        },
        {
            header : "Ic, A",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "i3"
        },
        {
            header : "cos \u03C6a",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "phase1"
        },
        {
            header : "cos \u03C6b",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "phase2"
        },
        {
            header : "cos \u03C6c",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "phase3"
        },
        {
            header : "f, Hz",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "frec"
        },
        {
            header : "T, \u2103",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "temp"
        },
    ];
});