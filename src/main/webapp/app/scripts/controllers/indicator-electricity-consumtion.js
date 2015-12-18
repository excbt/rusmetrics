angular.module('portalNMC')
.controller('ElectricityConsumptionCtrl', function($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope){
console.log("Run ConsumptionCtrl.");
    
    $scope.data = [];
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
    $scope.ctrlSettings.loading = true;
    
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
        //columns for sum
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
    
    var getIndicators = function(table, paramString){
        var url = table+paramString;
        $http.get(url).then(function (response) {
                var tmp = angular.copy(response.data); 
                if (mainSvc.checkUndefinedNull(tmp)){ return "Electricity indicators undefined or null."};            
//console.log(response.data);    
                tmp.forEach(function(el){
                    for(var i in $scope.columns){
                        if ((el[$scope.columns[i].fieldName]!=null)&&($scope.columns[i].type !== "string")){
                            el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed(3);
                        };
                    };                    
                });
                $scope.data = tmp;
                $scope.ctrlSettings.loading = false;
                if ($scope.ctrlSettings.viewMode==""&&$scope.data.length>0){
                    getSummary(table+"/summary"+paramString);
                };
        }, function(e){
            $scope.ctrlSettings.loading = false;
            console.log(e);
        });
    };
    
    var getSummary = function(table){       
        $http.get(table).then(function (response) {
            var el = angular.copy(response.data.totals);
            if (mainSvc.checkUndefinedNull(el)){ return "Summary undefined or null."};          
            for(var i in $scope.columns){
                if ((el[$scope.columns[i].fieldName]!=null)&&($scope.columns[i].type !== "string")){
                    el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed(3);
                };
            };                    
            el.onlyCons = true;
            el.dataDateString = "Итого";
            el.class = "nmc-el-totals-indicator-highlight nmc-view-digital-data";
            $scope.data.push(el);
        }, function(e){
            console.log(e);
        });
    };
     
     $scope.getData = function () {
         $scope.ctrlSettings.loading = true;
         $scope.data = [];
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
        getIndicators(table, paramString);                  
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
        $cookies.fromDate = moment(newDates.startDate).format($scope.ctrlSettings.requestFormat);
        $cookies.toDate = moment(newDates.endDate).format($scope.ctrlSettings.requestFormat);
        indicatorSvc.setFromDate(moment(newDates.startDate).format($scope.ctrlSettings.requestFormat));
        indicatorSvc.setToDate(moment(newDates.endDate).format($scope.ctrlSettings.requestFormat));
        $rootScope.reportStart = moment(newDates.startDate).format($scope.ctrlSettings.requestFormat);
        $rootScope.reportEnd = moment(newDates.endDate).format($scope.ctrlSettings.requestFormat);       
        $scope.getData("");
    }, false);    
});