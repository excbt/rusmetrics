angular.module('portalNMC')
.controller('ElectricityConsumptionCtrl', function($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope){
console.log("Run ConsumptionCtrl.");
    
    $scope.data = [];
    //TEMPPPPPPPPPPPPPPP
//    $scope.data = [
//        {
//            "id": 0,
//            "dataDate": "01.01.2016 00:00",
//            "PPPt1": 11,
//            "PPNt1": 11.11,
//            "PPPt2": 111111
//        },
//        {
//            "id": 1,
//            "dataDate": "01.01.2016 01:00",
//            "PPPt1": 12,
//            "PPPt2": 12222.123
//        },
//        {
//            "id": 2,
//            "dataDate": "Итого",
//            "PPPt1": 33,
//            "PPNt1": 11.11,
//            "PPPt2": Number(111111+12222.123).toFixed(3),
//            "class": "nmc-el-totals-indicator-highlight nmc-view-digital-data",
//            "onlyCons": true
//        }
//    ];
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
    $scope.ctrlSettings.userFormat = "DD.MM.YYYY"; //date format
    $scope.ctrlSettings.requestFormat = "YYYY-MM-DD"; //date format
    $scope.ctrlSettings.viewMode = "";
    $scope.ctrlSettings.dataDate = moment().endOf('day').format($scope.ctrlSettings.userFormat);
    
    $scope.ctrlSettings.ctxId = "electricity_consumption_page";
    
    $scope.tableDef = {
        tableClass : "crud-grid table table-lighter table-bordered table-condensed table-hover",
        hideHeader : false,
        headerClassTR : "nmc-main-table-header",
        columns : []
    };
    //create columns
    var elecType = [{"name":"p_A", "caption": "A"}, {"name":"q_R", "caption":"R"}];
    var elecKind = [{"name":"p", "caption":"+"}, {"name":"n", "caption":"-"}];
    var tariffPlans = [1,2,3,4];
    var columns = [{
                header : "Дата",
                headerClass : "col-md-1 nmc-text-align-center",
                dataClass : "col-md-1",
                fieldName: "dataDateString",
                type: "string",
                date: true
            }];
    for (var type = 0; type < elecType.length; type++){
        for (var tariff = 0; tariff < tariffPlans.length; tariff++){
            for (var kind = 0; kind < elecKind.length; kind++){
                var column = {};
                column.header = ""+elecType[type].caption+elecKind[kind].caption+"(T"+tariffPlans[tariff]+")";
                column.headerClass = "nmc-view-digital-data";
                column.dataClass = "nmc-view-digital-data";
                column.fieldName = ""+elecType[type].name+elecKind[kind].name+""+tariffPlans[tariff]+"";
                columns.push(column);
            };
        };
        for (var kind = 0; kind < elecKind.length; kind++){
                var column = {};
                column.header = "\u03A3"+elecType[type].caption+elecKind[kind].caption;
                column.headerClass = "nmc-view-digital-data";
                column.dataClass = "nmc-el-totals-indicator-highlight nmc-view-digital-data";
                column.fieldName = ""+elecType[type].name+elecKind[kind].name;
                columns.push(column);
        };
    };
//console.log(columns);    
    $scope.tableDef.columns = columns;
    $scope.columns = $scope.tableDef.columns;
    
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
    
    var getIndicators = function(table){       
        $http.get(table).then(function (response) {
                var tmp = angular.copy(response.data);          
                tmp.forEach(function(el){
                    for(var i in $scope.columns){
                        if ((el[$scope.columns[i].fieldName]!=null)&&($scope.columns[i].type !== "string")){
                            el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed(3);
                        };
                    };                    
                });
                $scope.data = tmp;             
        });
    };
    
    var getSummary = function(table){       
        $http.get(table).then(function (response) {
            var el = angular.copy(response.data.totals);           
                for(var i in $scope.columns){
                    if ((el[$scope.columns[i].fieldName]!=null)&&($scope.columns[i].type !== "string")){
                        el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed(3);
                    };
                };                    
            el.onlyCons = true;
            el.dataDateString = "Итого";
            el.class = "nmc-el-totals-indicator-highlight nmc-view-digital-data";
            $scope.data.push(el);
        });
    };
     
     $scope.getData = function () { 
         var paramString ="";
         var timeDetailType = $scope.timeDetailType || $cookies.timeDetailType;
         timeDetailType+=$scope.ctrlSettings.viewMode;
         $scope.zpointTable = "../api/subscr/" + $scope.contObject + "/serviceElCons/" + timeDetailType + "/" + $scope.contZPoint;// + "/?beginDate=" + $rootScope.reportStart + "&endDate=" + $rootScope.reportEnd;
         if ($scope.timeDetailType=="1h"){
             var requestDate = moment($scope.ctrlSettings.dataDate, $scope.ctrlSettings.userFormat).format($scope.ctrlSettings.requestFormat);
            paramString= "/?beginDate=" + requestDate + "&endDate=" + requestDate; 
         }else{
            paramString= "/?beginDate=" + $rootScope.reportStart + "&endDate=" + $rootScope.reportEnd;
         };
        var table =  $scope.zpointTable;
//console.log(table);
        getIndicators(table+paramString);         
         if ($scope.ctrlSettings.viewMode==""){
             getSummary(table+"/summary"+paramString);
         };
     };
    $scope.getData();
    
    $scope.changeViewMode = function(){
        $scope.getData();
    };
    
    $scope.changeTimeDetailType = function(){
        $scope.getData();
    };
    
    $scope.refreshData = function(){
        $scope.getData();
    };
    
    $(document).ready(function() {

          $('#inputElConsDate').datepicker({
              dateFormat: "dd.mm.yy",
              firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
              dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
              monthNames: $scope.dateOptsParamsetRu.locale.monthNames
          });
    });
    
    $scope.$watch('indicatorDates', function (newDates, oldDates) {      
        if ($location.path()!=="/objects/indicator-electricity"){
            return;
        };
        if(newDates===oldDates){
            return;
        };
        $cookies.fromDate = moment(newDates.startDate).format('YYYY-MM-DD');
        $cookies.toDate = moment(newDates.endDate).format('YYYY-MM-DD');
        indicatorSvc.setFromDate(moment(newDates.startDate).format('YYYY-MM-DD'));
        indicatorSvc.setToDate(moment(newDates.endDate).format('YYYY-MM-DD'));
        $rootScope.reportStart = moment(newDates.startDate).format('YYYY-MM-DD');
        $rootScope.reportEnd = moment(newDates.endDate).format('YYYY-MM-DD');       
        $scope.getData("");
    }, false);    
});