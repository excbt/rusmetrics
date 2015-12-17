angular.module('portalNMC')
.controller('ElectricityEnergyCtrl', function($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope){
console.log("Run ElectricityEnergyCtrl.");
    $scope.electroKind = "Energy";
    $scope.dataUrl = "url";
    $scope.columns = [
        {
            header : "Дата",
            headerClass : "col-xs-2 col-md-2 nmc-text-align-center",
            dataClass : "col-xs-2 col-md-2",
            fieldName: "dataDate"
        }, 
        {
            header : "Интервал, мин",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "interval"
        },
        {
            header : "A+, кВт/ч",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "PPP"
        },
        {
            header : "A-, кВт/ч",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "PPN"
        },
        {
            header : "R+, кВАр/ч",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "PQP"
        },
        {
            header : "R-, кВАр/ч",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "PQN"
        },
    ];
    
    //TEMPPPPPPPPPPPPPPP
    $scope.data = [
        {
            "id": 0,
            "dataDate": "01.01.2016 00:00",
            "interval": "30",
            "PPP": 11.11,
            "PPN": 111111
        },
        {
            "id": 1,
            "dataDate": "01.01.2016 01:00",
            "interval": "100",
            "PPP": 12,
            "PPN": 12222.123
        }
    ];
    /////////end TEMP region ////////////////
});