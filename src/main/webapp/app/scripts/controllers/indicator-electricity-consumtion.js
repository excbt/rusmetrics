angular.module('portalNMC')
.controller('ElectricityConsumptionCtrl', function($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope){
console.log("Run ConsumptionCtrl.");
    //TEMPPPPPPPPPPPPPPP
    $scope.data = [
        {
            "id": 0,
            "dataDate": "01.01.2016 00:00",
            "PPPt1": 11,
            "PPNt1": 11.11,
            "PPPt2": 111111
        },
        {
            "id": 1,
            "dataDate": "01.01.2016 01:00",
            "PPPt1": 12,
            "PPPt2": 12222.123
        },
        {
            "id": 2,
            "dataDate": "Итого",
            "PPPt1": 33,
            "PPNt1": 11.11,
            "PPPt2": Number(111111+12222.123).toFixed(3),
            "class": "nmc-el-totals-indicator-highlight nmc-view-digital-data",
            "onlyCons": true
        }
    ];
    /////////end TEMP region ////////////////
    $scope.indicatorsPerPage = 25; // this should match however many results your API puts on one page
    $scope.totalIndicators = $scope.data.length;
    $scope.pagination = {
        current: 1
    };
    
            // Настройки интервала дат для страницы с показаниями
    if (angular.isDefined($location.search().fromDate)&&($location.search().fromDate!=null)){
        $scope.indicatorDates = {
            startDate : $location.search().fromDate,
            endDate :  $location.search().toDate
        };
    }else{
        if (angular.isDefined($cookies.fromDate)&&($cookies.toDate!=null)){
            $scope.indicatorDates = {
                startDate : $cookies.fromDate,
                endDate :  $cookies.toDate
            };
        }else{
            $scope.indicatorDates = {
                startDate : indicatorSvc.getFromDate(),
                endDate :  indicatorSvc.getToDate()
            };
        };
    };
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.dataUrl = "";
    $scope.ctrlSettings.dateFormat = "DD.MM.YYYY"; //date format
    $scope.ctrlSettings.viewMode = "cons";
    
    $scope.ctrlSettings.ctxId = "electricity_consumption_page";
    
    $scope.tableDef = {
        tableClass : "crud-grid table table-lighter table-bordered table-condensed table-hover",
        hideHeader : false,
        headerClassTR : "nmc-main-table-header",
        columns : []
    };
    //create columns
    var elecType = [{"name":"P", "caption": "A"}, {"name":"Q", "caption":"R"}];
    var elecKind = [{"name":"P", "caption":"+"}, {"name":"N", "caption":"-"}];
    var tariffPlans = [1,2,3,4];
    var columns = [{
                header : "Дата",
                headerClass : "col-md-1 nmc-text-align-center",
                dataClass : "col-md-1",
                fieldName: "dataDate"
            }];
    for (var type = 0; type < elecType.length; type++){
        for (var tariff = 0; tariff < tariffPlans.length; tariff++){
            for (var kind = 0; kind < elecKind.length; kind++){
                var column = {};
                column.header = ""+elecType[type].caption+elecKind[kind].caption+"(T"+tariffPlans[tariff]+")";
                column.headerClass = "nmc-view-digital-data";
                column.dataClass = "nmc-view-digital-data";
                column.fieldName = "P"+elecType[type].name+elecKind[kind].name+"t"+tariffPlans[tariff]+"";
                columns.push(column);
            };
        };
        for (var kind = 0; kind < elecKind.length; kind++){
                var column = {};
                column.header = "\u03A3"+elecType[type].caption+elecKind[kind].caption;
                column.headerClass = "nmc-view-digital-data";
                column.dataClass = "nmc-el-totals-indicator-highlight nmc-view-digital-data";
                column.fieldName = "sum"+elecType[type].name+elecKind[kind].name;
                columns.push(column);
        };
    };
//console.log(columns);    
    $scope.tableDef.columns = columns;
    
    
    $scope.dateOptsParamsetRu ={
        locale : {
            daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
            firstDay : 1,
            monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                    'Октябрь', 'Ноябрь', 'Декабрь' ]
        },
        singleDatePicker: true,
        format: $scope.ctrlSettings.dateFormat
    };
    
                // Проверка пользователя - системный/ не системный
    $scope.isSystemuser = function(){
        var result = false;
        $scope.userInfo = $rootScope.userInfo;
        if (angular.isDefined($scope.userInfo)){
            result = $scope.userInfo._system;
        };
        return result;
    };
        //define init indicator params method
    var initIndicatorParams = function(){
        var pathParams = $location.search();       
        var tmpZpId = null;//indicatorSvc.getZpointId();    
        var tmpContObjectId = null;//indicatorSvc.getContObjectId();
        var tmpZpName = null;//indicatorSvc.getZpointName();    
        var tmpContObjectName = null;//indicatorSvc.getContObjectName();
        var tmpTimeDetailType = null;

        if (angular.isUndefined(tmpZpId)||(tmpZpId===null)){
            if (angular.isDefined(pathParams.zpointId)&&(pathParams.zpointId!=="null")){
                indicatorSvc.setZpointId(pathParams.zpointId);
            };
        };
        if (angular.isUndefined(tmpContObjectId)||(tmpContObjectId===null)){
            if (angular.isDefined(pathParams.objectId)&&(pathParams.objectId!=="null")){
                indicatorSvc.setContObjectId(pathParams.objectId);
            };
        };
        
        if (angular.isUndefined(tmpZpName)||(tmpZpName===null)){
            if (angular.isDefined(pathParams.zpointName)&&(pathParams.zpointName!=="null")){
                indicatorSvc.setZpointName(pathParams.zpointName);
            };
        };
        if (angular.isUndefined(tmpContObjectName)||(tmpContObjectName===null)){
            if (angular.isDefined(pathParams.objectName)&&(pathParams.objectName!=="null")){
                indicatorSvc.setContObjectName(pathParams.objectName);
            };
        };
        
        if (angular.isUndefined(tmpTimeDetailType)||(tmpTimeDetailType===null)){
            if (angular.isDefined(pathParams.timeDetailType)&&(pathParams.timeDetailType!=="null")){
                $scope.timeDetailType = pathParams.timeDetailType;
            }else{               
                if (angular.isDefined($cookies.timeDetailType)&&($cookies.timeDetailType!="undefined")&&($cookies.timeDetailType!="null")){
                    $scope.timeDetailType = $cookies.timeDetailType;
                }else{                   
                    $scope.timeDetailType = indicatorSvc.getTimeDetailType();
                };
            };
        };
        
        $scope.contZPoint = indicatorSvc.getZpointId();
        $scope.contZPointName = (indicatorSvc.getZpointName()!="undefined")?indicatorSvc.getZpointName() : "Без названия";
        $scope.contObject = indicatorSvc.getContObjectId();
        $scope.contObjectName = (indicatorSvc.getContObjectName()!="undefined")?indicatorSvc.getContObjectName() : "Без названия";     
        
        //if exists url params "fromDate" and "toDate" get date interval from url params, else get interval from indicator service.
        if (angular.isDefined(pathParams.fromDate)&&(pathParams.fromDate!=="null")){
            $rootScope.reportStart = pathParams.fromDate;
        }else if(angular.isDefined($cookies.fromDate)&&($cookies.fromDate!=="null")){
                $rootScope.reportStart = $cookies.fromDate;
            }else{
                $rootScope.reportStart = indicatorSvc.getFromDate();
        };
        if (angular.isDefined(pathParams.toDate)&&(pathParams.toDate!=="null")){
            $rootScope.reportEnd = pathParams.toDate;
        }else if (angular.isDefined($cookies.toDate)&&($cookies.toDate!=="null")){
                $rootScope.reportEnd = $cookies.toDate;
            }else{
                $rootScope.reportEnd = indicatorSvc.getToDate();
        };
        $scope.dateRangeOptsRu = mainSvc.getDateRangeOptions("indicator-ru");     
    };
        //run init method
    initIndicatorParams();
    
    $(document).ready(function() {

          $('#inputElConsDate').datepicker({
              dateFormat: "dd.mm.yy",
              firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
              dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
              monthNames: $scope.dateOptsParamsetRu.locale.monthNames
          });
    });
});