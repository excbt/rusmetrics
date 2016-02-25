angular.module('portalNMC')
.controller('ElectricityPkeCtrl', function($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope){
console.log("Run ElectricityPkeCtrl.");
    $scope.electroKind = "Pke";
    
    $scope.pkeTypes = [];
    $scope.pkeData = [];
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.loading = true;
    $scope.ctrlSettings.systemDateFormat = "YYYY-MM-DD";
        
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
    var viewMode = "serviceElPke";//deviceObjects/pke/%d/warn
    var dataUrl = apiSubscUrl + "deviceObjects/pke/byContZPoint/" + $scope.contZPoint + "/warn";
    var pkeTypesUrl = apiSubscUrl+"deviceObjects/pke/types";
    
    
    var errorCallback = function(e){
        console.log(e);    
        var errorCode = "-1";
        if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
            errorCode = e.resultCode || e.data.resultCode;
        };
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };
    
    var getPkeTypes = function(){
        $http.get(pkeTypesUrl)
        .then(function(resp){
            $scope.pkeTypes = angular.copy(resp.data); 
            getPke();            
        },
             errorCallback);
    };
    
    var getPke = function(){
        var url = dataUrl;
        url += "/?beginDate=" + moment($scope.indicatorPkeDates.startDate).format($scope.ctrlSettings.systemDateFormat) + "&endDate=" + moment($scope.indicatorPkeDates.endDate).format($scope.ctrlSettings.systemDateFormat);
        $http.get(url)
        .then(function(resp){
            $scope.pkeData = angular.copy(resp.data);
            $scope.pkeData.forEach(function(elem){
                $scope.pkeTypes.some(function(type){
                    if (elem.deviceObjectPkeTypeKeyname == type.keyname){
                        elem.pkeTypeCaption = type.caption;
                    };
                });
            });
            $scope.ctrlSettings.loading = false; 
        },
             errorCallback);
    };
    
    getPkeTypes();
    
    $scope.columns = [
        {
            header : "Дата начала",
            headerClass : "col-xs-3 col-md-3 nmc-text-align-center",
            dataClass : "col-xs-3 col-md-3 nmc-text-align-right",
            fieldName: "warnStartDateStr",
            type: "string",
            date: true
        }, 
        {
            header : "Дата окончания",
            headerClass : "col-xs-3 col-md-3 nmc-text-align-center",
            dataClass : "col-xs-3 col-md-3 nmc-text-align-right",
            fieldName: "warnEndDateStr",
            type: "string",
            date: true
        },
        {
            header : "Значение",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data nmc-text-align-center",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data nmc-text-align-right",
            fieldName: "warnValue"
        },        
        {
            header : "Тип",
            headerClass : "col-xs-5 col-md-5 nmc-view-digital-data nmc-text-align-center",
            dataClass : "col-xs-5 col-md-5 nmc-view-digital-data nmc-text-align-right",
            fieldName: "pkeTypeCaption"
        }
    ];
    
    $('#typesMenu').on('shown.bs.dropdown', function () {
        console.log("hidden");
    });
});