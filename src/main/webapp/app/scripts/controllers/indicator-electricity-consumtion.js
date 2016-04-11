angular.module('portalNMC')
.controller('ElectricityConsumptionCtrl', function($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope, $window){
//console.log("Run ConsumptionCtrl.");
    
    $scope.data = [];
    $scope.totals = [];
    $scope.indicatorsPerPage = $scope.data.length; //default = 25; // this should match however many results your API puts on one page
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
    $scope.ctrlSettings.precision = 3; //precision for data view
    
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
                headerClass : "col-md-2 nmc-text-align-center",
                dataClass : "col-md-2",
                fieldName: "dataDateString",
                type: "string",
                date: true
            }];
    for (var type = 0; type < elecType.length; type++){
        for (var tariff = 0; tariff < tariffPlans.length; tariff++){
            for (var kind = 0; kind < elecKind.length; kind++){
                var column = {};
                column.header = "" + elecType[type].caption + elecKind[kind].caption + " (T" + tariffPlans[tariff] + ")";
                column.headerClass = "nmc-view-digital-data";
                column.dataClass = "nmc-view-digital-data";
                column.fieldName = "" + elecType[type].name + elecKind[kind].name + "" + tariffPlans[tariff] + "";
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
                        if ((el[$scope.columns[i].fieldName] != null)&&($scope.columns[i].type !== "string")){
                            
                            el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed($scope.ctrlSettings.precision);
                        };
                    };                    
                });
                $scope.data = tmp;
                $scope.indicatorsPerPage = $scope.data.length;
                $scope.ctrlSettings.loading = false;
                if ($scope.ctrlSettings.viewMode=="" && $scope.data.length > 0){
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
                if ((el[$scope.columns[i].fieldName]!=null) && ($scope.columns[i].type !== "string")){
                    el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed($scope.ctrlSettings.precision);
                };
            };                    
            el.onlyCons = true;
            el.dataDateString = "Итого:";
            el.class = "nmc-el-totals-indicator-highlight nmc-view-digital-data";
            $scope.totals[0] = angular.copy(el);
            var absTotal = {};
            absTotal.dataDateString = "Итого по интеграторам:";
            absTotal.onlyCons = true;
            $scope.totals[1] = angular.copy(absTotal);
            $scope.indicatorsPerPage += 2;
//console.log($scope.totals);            
        }, function(e){
            console.log(e);
        });
    };
     
     $scope.getData = function () {
         $scope.ctrlSettings.loading = true;
         //check view mode: if integrators -> precision = 2, else = 3
         ($scope.ctrlSettings.viewMode.indexOf("_abs") >= 0) ? $scope.ctrlSettings.precision = 2 : $scope.ctrlSettings.precision = 3;
         $scope.data = [];
         var paramString = "";
         var timeDetailType = $scope.timeDetailType || $cookies.timeDetailType;
         timeDetailType+=$scope.ctrlSettings.viewMode;
         $scope.zpointTable = "../api/subscr/" + $scope.contObject + "/serviceElCons/" + timeDetailType + "/" + $scope.contZPoint;// + "/?beginDate=" + $rootScope.reportStart + "&endDate=" + $rootScope.reportEnd;
         if ($scope.timeDetailType=="1h"){
             var requestDate = moment($scope.ctrlSettings.dataDate, $scope.ctrlSettings.userFormat).format($scope.ctrlSettings.requestFormat);
             if (requestDate.localeCompare('Invalid date') == 0 || requestDate < '2000-01-01'){
                    return "requestDate is Invalid date.";
                };
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
        $scope.getData();
    }, false);
    
    //listen window resize
//    var wind = angular.element($window);
//    var windowResize = function(){
//console.log("windowResize");        
//        if (angular.isDefined($scope.setScoreStyles)){
//            $scope.setScoreStyles();
//        };
//        $scope.$apply();
//    };
//    wind.bind('resize', windowResize); 
//        
//    $scope.$on('$destroy', function() {
//        wind.unbind('resize', windowResize);
//    });
//    
//    $scope.setScoreStyles = function(){
//        //ровняем таблицу, если появляются полосы прокрутки
//        var tableHeader = document.getElementById("indicatorConsTableHeader");
//        var tableDiv = document.getElementById("divIndicatorConsTable");
//        if (!mainSvc.checkUndefinedNull(tableDiv)){
//            if (tableDiv.offsetWidth > tableDiv.clientWidth){
//                tableDiv.style.width = tableHeader.offsetWidth + 17 + 'px';
//            }else{
//                tableDiv.style.width = tableHeader.offsetWidth + 'px';                    
//            };
//        };
//    };
//    $scope.onTableLoad = function(){
//console.log("OnTableLoad");        
//        $scope.setScoreStyles();
//    };
    
    $(document).ready(function() {
        $('#inputElConsDate').datepicker({
          dateFormat: "dd.mm.yy",
          firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
          dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
          monthNames: $scope.dateOptsParamsetRu.locale.monthNames,
            beforeShow: function(){
                setTimeout(function(){
                    $('.ui-datepicker-calendar').css("display", "table");
                }, 1);
            },
            onChangeMonthYear: function(){
                setTimeout(function(){
                    $('.ui-datepicker-calendar').css("display", "table");
                }, 1);
            }
        });        
    });
});