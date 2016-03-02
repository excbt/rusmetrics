angular.module('portalNMC')
.controller('ElectricityPkeCtrl', function($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope, $filter, notificationFactory, $window, $timeout){
//console.log("Run ElectricityPkeCtrl.");
    $scope.electroKind = "Pke";
    
    $scope.pkeTypes = [];
    $scope.pkeData = [];
        
    $scope.defaultFilterCaption = "Все";
    $scope.selectedPkeTypes_list = {
        caption: $scope.defaultFilterCaption
    };
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.loading = true;
    $scope.ctrlSettings.systemDateFormat = "YYYY-MM-DD";
    
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
            header : "Значение предела",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data nmc-text-align-center",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data nmc-text-align-right",
            fieldName: "warnValue"
        },        
        {
            header : "Тип",
            headerClass : "col-xs-5 col-md-5 nmc-text-align-center",
            dataClass : "col-xs-5 col-md-5 nmc-text-align-right",
            fieldName: "pkeTypeCaption"
        }
    ];
    // sort settings
    $scope.ctrlSettings.orderBy = {"field": $scope.columns[0].fieldName, "asc": true};
    
    $scope.setOrderBy = function(field){
        var asc = $scope.ctrlSettings.orderBy.field === field ? !$scope.ctrlSettings.orderBy.asc : true;
        $scope.ctrlSettings.orderBy = { field: field, asc: asc };
    };
        
    var intervalSettings = {};
    $scope.indicatorPkeDates = {
        startDate :  moment().subtract(6, 'days').startOf('day'),
        endDate : moment().endOf('day')
    };
    intervalSettings.minDate = moment().subtract(29, 'days').startOf('day');    
    $scope.dateRangeOptsPkeRu = mainSvc.getDateRangeOptions("indicator-ru", intervalSettings);
//console.log($scope.dateRangeOptsPkeRu);    
    
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
    
    var getPke = function(pkeTypes){
        $scope.ctrlSettings.loading = true;
        var url = dataUrl;
        var params = {};
        //add time interval
        url += "/?beginDate=" + moment($scope.indicatorPkeDates.startDate).format($scope.ctrlSettings.systemDateFormat) + "&endDate=" + moment($scope.indicatorPkeDates.endDate).format($scope.ctrlSettings.systemDateFormat);
        //add pkeTypes
        if (!mainSvc.checkUndefinedNull(pkeTypes) && angular.isArray(pkeTypes)){
            var pkeTypeKeyNames = pkeTypes.map(function(elem){return elem.keyname});
            params = {pkeTypeKeynames: pkeTypeKeyNames}
        };
//        $http.get(url)
        $http({
            method: "GET",
            url: url,
            params: params
        })
        .then(function(resp){
            $scope.pkeData = angular.copy(resp.data);
            $scope.pkeData.forEach(function(elem){
                $scope.pkeTypes.some(function(type){
                    if (elem.deviceObjectPkeTypeKeyname == type.keyname){
                        elem.pkeTypeCaption = type.caption;
                        return true;
                    };
                });
            });
            $scope.ctrlSettings.loading = false; 
        },
             errorCallback);
    };
    
    getPkeTypes();
    
    $scope.getDataWithSelectedTypes = function(){
        var selectedTypes = [];
        $scope.pkeTypes.forEach(function(elem){
            if (elem.selected == true){
                selectedTypes.push(elem);
            };
        });
        if (selectedTypes.length == 0){
            $scope.selectedPkeTypes_list.caption = $scope.defaultFilterCaption;
        }else{
            $scope.selectedPkeTypes_list.caption = selectedTypes.length;
        };
        getPke(selectedTypes);
    };
    
    $scope.selectAllTypes = function(){
        var filteredTypes = $filter('filter')($scope.pkeTypes, $scope.pkeTypesFilter);       
        filteredTypes.forEach(function(elem){
            elem.selected = $scope.isSelectedAllTypes;
        });
        $scope.getDataWithSelectedTypes();
    };
    
    $scope.clearPkeTypeFilter = function(){
        $scope.pkeTypesFilter = "";
        $scope.isSelectedAllTypes = false;
        $scope.pkeTypes.forEach(function(elem){
            elem.selected = $scope.isSelectedAllTypes;
        });
        $scope.getDataWithSelectedTypes();
    };
    
    $scope.$watch('indicatorPkeDates', function(newDates, oldDates){
        $scope.getDataWithSelectedTypes();
    });
    
    $scope.setScoreStyles = function(){
        //ровняем таблицу, если появляются полосы прокрутки
        var tableHeader = document.getElementById("pkeTableHeader");
        var tableDiv = document.getElementById("divPkeTable");
        if (!mainSvc.checkUndefinedNull(tableDiv) && !mainSvc.checkUndefinedNull(tableHeader)){
            if (tableHeader.offsetWidth == 0){
                return "Pke. tableHeader.offsetWidth == 0";
            };
            if (tableDiv.offsetWidth > tableDiv.clientWidth){
                tableDiv.style.width = tableHeader.offsetWidth + 17 + 'px';
            }else{
                tableDiv.style.width = tableHeader.offsetWidth + 'px';                    
            };
        };
    };
        //listen window resize
    var wind = angular.element($window);
    var windowResize = function(){
        if (angular.isDefined($scope.setScoreStyles)){
            $scope.setScoreStyles();
        };
        $scope.$apply();
    };
    wind.bind('resize', windowResize); 
        
    $scope.$on('$destroy', function() {
        wind.unbind('resize', windowResize);
    }); 
    
    $scope.onTableLoad = function(){
        $scope.setScoreStyles();
    };
    
    $timeout(function(){
        $scope.setScoreStyles();
    }, 500);
});