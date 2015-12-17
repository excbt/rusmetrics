angular.module('portalNMC')
.controller('ElectricitySpecCtrl', function($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope){
console.log("Run ElectricitySpecCtrl.");
    $scope.electroKind = "Spec";
    $scope.dataUrl = "url";
    $scope.columns = [
        {
            header : "Дата",
            headerClass : "col-xs-2 col-md-2 nmc-text-align-center",
            dataClass : "col-xs-2 col-md-2",
            fieldName: "dataDate"
        }, 
        {
            header : "Ua, V",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "Ua"
        },
        {
            header : "Ub, V",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "Ub"
        },
        {
            header : "Uc, V",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "Uc"
        },
        {
            header : "Ia, A",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "Ia"
        },
        {
            header : "Ib, A",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "Ib"
        },
        {
            header : "Ic, A",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "Ic"
        },
        {
            header : "cos \u03C6a",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "cosXa"
        },
        {
            header : "cos \u03C6b",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "cosXb"
        },
        {
            header : "cos \u03C6c",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "cosXc"
        },
        {
            header : "f, Hz",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "frenc"
        },
        {
            header : "T, \u2103",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data",
            fieldName: "temp"
        },
    ];
    
    //TEMPPPPPPPPPPPPPPP
    $scope.data = [
        {
            "id": 0,
            "dataDate": "01.01.2016 00:00",
            "Ua": "30",
            "Ic": 11.11,
            "temp": 111111
        },
        {
            "id": 1,
            "dataDate": "01.01.2016 01:00",
            "Ib": "100",
            "cosXa": 12,
            "frenc": 12222.123
        }
    ];
    /////////end TEMP region ////////////////
});