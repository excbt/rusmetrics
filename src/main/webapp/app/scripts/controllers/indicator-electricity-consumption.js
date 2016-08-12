/*jslint node: true, white: true*/
/*global angular, moment*/
'use strict';
angular.module('portalNMC')
.controller('ElectricityConsumptionCtrl', function($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope, $window, $timeout, notificationFactory){
//console.log("Run ConsumptionCtrl.");
    
    var PRECISION_MASK = "0.00000000000000000000";
    var WARNING_BG_COLOR = "#FFFFA4"; 
    var ALARM_BG_COLOR = "#FF7171";
    
    var VCOOKIE_URL = "../api/subscr/vcookie";        
    var USER_VCOOKIE_URL = "../api/subscr/vcookie/user";
    var OBJECT_INDICATOR_PREFERENCES_VC_MODE = "OBJECT_INDICATOR_PREFERENCES";
    
    $scope.data = [];
    $scope.totals = [];
    $scope.indicatorsPerPage = $scope.data.length; //default = 25; // this should match however many results your API puts on one page
    $scope.totalIndicators = $scope.data.length;
    $scope.pagination = {
        current: 1
    };
    
            // Настройки интервала дат для страницы с показаниями
    if (angular.isDefined($location.search().fromDate) && ($location.search().fromDate != null)){
        $scope.indicatorDates = {
            startDate : $location.search().fromDate,
            endDate :  $location.search().toDate
        };
    }else{
        if (angular.isDefined($cookies.fromDate) && ($cookies.toDate != null)){
            $scope.indicatorDates = {
                startDate : $cookies.fromDate,
                endDate :  $cookies.toDate
            };
        }else{
            $scope.indicatorDates = {
                startDate : indicatorSvc.getFromDate(),
                endDate :  indicatorSvc.getToDate()
            };
        }
    }
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.dataUrl = "";
    $scope.ctrlSettings.userFormat = "DD.MM.YYYY"; //date format
    $scope.ctrlSettings.requestFormat = "YYYY-MM-DD"; //date format
    $scope.ctrlSettings.viewMode = "";
    $scope.ctrlSettings.dataDate = moment().endOf('day').format($scope.ctrlSettings.userFormat);
    $scope.ctrlSettings.loading = true;
    $scope.ctrlSettings.precision = 3; //precision for data view
    
    $scope.ctrlSettings.ctxId = "electricity_consumption_page";
    
    $scope.ctrlSettings.loadedElectricityColumnsPref = []; //columns prefs which loading from db
    
    $scope.tableDef = {
        tableClass : "crud-grid table table-lighter table-bordered table-condensed table-hover",
        hideHeader : false,
        headerClassTR : "nmc-main-table-header",
        columns : []
    };
    // ******************** create columns ****************************
//    var elecType = [{"name":"p_A", "caption": "A"}, /* active*/
//                    {"name":"q_R", "caption":"R"}]; /* reactive*/
//    var elecKind = [{"name":"p", "caption":"+"}, /*positive*/
//                    {"name":"n", "caption":"-"}];/*negative*/
//    var tariffPlans = [1, 2, 3, 4];//use 4 tariff plans
//    var columns = [{
//                header : "Дата",
//                headerClass : "col-md-2 nmc-text-align-center",
//                dataClass : "col-md-2",
//                fieldName: "dataDateString",
//                type: "string",
//                date: true
//            }];
//    //columns for active and reactive parts
//    for (var type = 0; type < elecType.length; type++){
//        //columns for tariff plans
//        for (var tariff = 0; tariff < tariffPlans.length; tariff++){
//            for (var kind = 0; kind < elecKind.length; kind++){
//                var column = {};
//                column.header = "" + elecType[type].caption + elecKind[kind].caption + " (T" + tariffPlans[tariff] + ")";
//                column.headerClass = "nmc-view-digital-data";
//                column.dataClass = "nmc-view-digital-data";
//                column.fieldName = "" + elecType[type].name + elecKind[kind].name + "" + tariffPlans[tariff] + "";
//                column.elKind = elecKind[kind].name;
//                column.elType = elecType[type].name;
//                columns.push(column);
//            };
//        };
//        //columns for sum
//        for (var kind = 0; kind < elecKind.length; kind++){
//                var column = {};
//                column.header = "\u03A3" + elecType[type].caption+elecKind[kind].caption;
//                column.headerClass = "nmc-view-digital-data";
//                column.dataClass = "nmc-el-totals-indicator-highlight nmc-view-digital-data";
//                column.fieldName = "" + elecType[type].name + elecKind[kind].name;
//                column.isSummary = true;
//                column.elKind = elecKind[kind].name;
//                column.elType = elecType[type].name;
//                columns.push(column);
//        };
//    };
//console.log(columns);    
    // ******************************* end Create columns **************************
    
    function errorCallback (e) {
        $scope.ctrlSettings.loading = false;
        console.log(e);
        var errorCode = "-1";
        if (mainSvc.checkUndefinedNull(e) || mainSvc.checkUndefinedNull(e.data)){
            errorCode = "ERR_CONNECTION";
        }
        if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
            errorCode = e.resultCode || e.data.resultCode;
        }
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
//        notificationFactory.errorInfo(e.statusText,e.data.description);       
    }
    
    function errorLoadingModePrefsCallback (e) {
        $scope.$broadcast("indicators:loadedModePrefs");
        errorCallback(e);
    }
    
    
    $scope.tableDef.columns = indicatorSvc.getElectricityColumns();//columns;
    $scope.columns = $scope.tableDef.columns;    
    var colLen = Math.floor($scope.columns.length / 2);
    $scope.ctrlSettings.activeEnergyColCount = colLen;
    $scope.ctrlSettings.reactiveEnergyColCount = colLen;
    
    
    function loadModePrefs(indicatorModeKeyname){
        if (mainSvc.checkUndefinedNull(VCOOKIE_URL) || mainSvc.checkUndefinedNull(OBJECT_INDICATOR_PREFERENCES_VC_MODE) || mainSvc.checkUndefinedNull(indicatorModeKeyname)){
            console.log("Request required params is null!");
            return false;
        }
        var url = VCOOKIE_URL + "?vcMode=" + OBJECT_INDICATOR_PREFERENCES_VC_MODE + "&vcKey=" + indicatorModeKeyname;                
        $http.get(url).then(function(resp){
            if (mainSvc.checkUndefinedNull(resp) || mainSvc.checkUndefinedNull(resp.data) || !angular.isArray(resp.data) || resp.data.length === 0){
                $scope.$broadcast("indicators:loadedModePrefs");
                console.log("Indicators: incorrect mode preferences!");
                return false;
            }
            
            var modePrefs = resp.data[0],
                vcvalue,
                electricityColumns;
            vcvalue = JSON.parse(modePrefs.vcValue);
            
            $scope.ctrlSettings.loadedElectricityColumnsPref = vcvalue.electricityColumns;                    
            
            $scope.ctrlSettings.viewMode = vcvalue.indicatorElMode;
            $scope.timeDetailType = vcvalue.indicatorElKind;
            
            $scope.$broadcast("indicators:loadedModePrefs");

        }, errorLoadingModePrefsCallback);
    }    
        
    function loadIndicatorMode (objId) {        
        if (mainSvc.checkUndefinedNull(USER_VCOOKIE_URL) || mainSvc.checkUndefinedNull(OBJECT_INDICATOR_PREFERENCES_VC_MODE) || mainSvc.checkUndefinedNull(objId)){
            console.log("Request required params is null!");
            $scope.$broadcast("indicators:loadedModePrefs");
            return false;
        }
        var url = USER_VCOOKIE_URL + "?vcMode=" + OBJECT_INDICATOR_PREFERENCES_VC_MODE + "&vcKey=" + "OIP_" + objId;
        $http.get(url).then(function(resp){
            if (mainSvc.checkUndefinedNull(resp) || mainSvc.checkUndefinedNull(resp.data) || resp.data.length === 0){
                $scope.$broadcast("indicators:loadedModePrefs");
                return false;
            } 
//console.log(resp.data);                        
            var objectIndicatorModeKeyname = JSON.parse(resp.data[0].vcValue);
            loadModePrefs(objectIndicatorModeKeyname);
        }, errorLoadingModePrefsCallback);
    }
    
    function setColumnPref(columnPrefs){        
        //if columnPrefs not set, that mean - view all columns
        if (mainSvc.checkUndefinedNull(columnPrefs) || columnPrefs.length === 0){
            //indicator columns
            $scope.ctrlSettings.activeEnergyColCount = colLen;
            $scope.ctrlSettings.reactiveEnergyColCount = colLen;
            for (var i = 1; i < $scope.columns.length; i++){                
                $scope.columns[i].isvisible = 'isvisible';
            }
            return;
        }
        var colCount = {};
        colCount["p_A"] = 0;
        colCount["q_R"] = 0;        
        //set preferences for indicator columns: date column is view always
        for (var i = 1; i < $scope.columns.length; i++){
            columnPrefs.forEach(function(pref){
                if (pref.fieldName === $scope.columns[i].fieldName && pref.isVisible === true){
                    $scope.columns[i].isvisible = 'isvisible';
                    colCount[$scope.columns[i].elType] += 1;
                }
            })
        }
        $scope.ctrlSettings.activeEnergyColCount = colCount["p_A"];
        $scope.ctrlSettings.reactiveEnergyColCount = colCount["q_R"];
    }
    
    
    function setColumnPrefOld(columnPrefs){        
        //if columnPrefs not set, that mean - view all columns
        if (mainSvc.checkUndefinedNull(columnPrefs) || columnPrefs.length == 0){
            //indicator columns
            $scope.ctrlSettings.activeEnergyColCount = colLen;
            $scope.ctrlSettings.reactiveEnergyColCount = colLen;
            for (var i = 1; i < $scope.columns.length; i++){                
                $scope.columns[i].isvisible = 'isvisible';
            }
            return;
        }
        var colCount = {};
        colCount["p_A"] = 0;
        colCount["q_R"] = 0;        
        //set preferences for indicator columns: date column is view always
        for (var i = 1; i < $scope.columns.length; i++){
            columnPrefs.forEach(function(pref){
                if (pref == $scope.columns[i].fieldName){
                    $scope.columns[i].isvisible = 'isvisible';
                    colCount[$scope.columns[i].elType] += 1;
                }
            })
        }
        $scope.ctrlSettings.activeEnergyColCount = colCount["p_A"];
        $scope.ctrlSettings.reactiveEnergyColCount = colCount["q_R"];
    }
        
    function readIndicatorColumnPref(contObjId){
        var columnPrefs = $cookies["indicator" + "el" + contObjId];
        if (!mainSvc.checkUndefinedNull(columnPrefs))
            columnPrefs = columnPrefs.split(',');
        setColumnPref(columnPrefs);
    } 
    
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
    
    /*  
    */
    function initializeElectroSums(dataArr){
        dataArr.forEach(function(el){
            for (var type = 0; type < elecType.length; type++){
                //columns for sum
                for (var kind = 0; kind < elecKind.length; kind++){
                    el["" + elecType[type].name + elecKind[kind].name + "_sum"] = -el["" + elecType[type].name + elecKind[kind].name];
                };
            };
        });
        return dataArr;
    };
    
    function setPrecisionAndGetSum(dataArr){
        dataArr.forEach(function(el){
            for(var i in $scope.columns){
                if ((el[$scope.columns[i].fieldName] != null) && ($scope.columns[i].type !== "string")){                            
                    el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed($scope.ctrlSettings.precision);
                    // get active/reactive, positive/negative sums
                    el["" + $scope.columns[i].elType + $scope.columns[i].elKind + "_sum"] += Number(el[$scope.columns[i].fieldName]);   
                };
            };                    
        });
        return dataArr;
    };
    
    function setSumPrecision(dataArr){
        dataArr.forEach(function(el){
            for (var type = 0; type < elecType.length; type++){
                //columns for sum
                for (var kind = 0; kind < elecKind.length; kind++){
                    el["" + elecType[type].name + elecKind[kind].name + "_sum"] = 
                        el["" + elecType[type].name + elecKind[kind].name + "_sum"].toFixed($scope.ctrlSettings.precision);
                };
            };
        });
    };
    
    function getSumDifference(dataArr){
        dataArr.forEach(function(el, index){
            for (var type = 0; type < elecType.length; type++){
                //columns for sum
                for (var kind = 0; kind < elecKind.length; kind++){
                    //set default style
                    el["" + elecType[type].name + elecKind[kind].name + "_style"] = {};
                    el["" + elecType[type].name + elecKind[kind].name + "_style"].bgcolor = "aquamarine";
                    el["" + elecType[type].name + elecKind[kind].name + "_style"].cursor = "default";
                    if (mainSvc.checkUndefinedNull(el["" + elecType[type].name + elecKind[kind].name + "_sum"]) || 
                        mainSvc.checkUndefinedNull(el["" + elecType[type].name + elecKind[kind].name])){ 
                        return "Sum is undefined or null."
                    };
                    var diff = Math.abs(el["" + elecType[type].name + elecKind[kind].name + "_sum"] - el["" + elecType[type].name + elecKind[kind].name]).toFixed($scope.ctrlSettings.precision);
                    var lengthFractPart = $scope.ctrlSettings.precision;
                    var difPrecision = Number(PRECISION_MASK.substring(0, lengthFractPart+1)+"1");                
                    if (diff > difPrecision && diff <= 1){
                        el["" + elecType[type].name + elecKind[kind].name + "_style"].bgcolor = WARNING_BG_COLOR;
                        el["" + elecType[type].name + elecKind[kind].name + "_style"].cursor = "pointer";
                    }else if (diff > 1){
                        el["" + elecType[type].name + elecKind[kind].name + "_style"].bgcolor = ALARM_BG_COLOR;
                        el["" + elecType[type].name + elecKind[kind].name + "_style"].cursor = "pointer";
                    };
                    var elDom = "#indicators_td_"+el.id+ ""+ elecType[type].name + elecKind[kind].name;
                    var detailInfo = "";
                    detailInfo += "Сумма полученная от сервера: " + el["" + elecType[type].name + elecKind[kind].name];
                    detailInfo += "<br><br>";
                    detailInfo += "Рассчитанная сумма: " + el["" + elecType[type].name + elecKind[kind].name + "_sum"];
                    setToolTip("Детальная информация", detailInfo, elDom, elDom);
//console.log(el["" + elecType[type].name + elecKind[kind].name + "_sum"] +" - "+el["" + elecType[type].name + elecKind[kind].name]+"="+ diff);                    
                };
            };
        });
    };
    
    
    var getIndicators = function(table, paramString){
//console.log("get Indicators.");        
        var url = table+paramString;
        $http.get(url).then(
            function (response) {                
                var tmp = angular.copy(response.data); 
                if (mainSvc.checkUndefinedNull(tmp)){ return "Electricity indicators undefined or null.";}            
        //console.log(response.data);
                $timeout(function(){
                    $scope.setScoreStyles();
                });
                    //set color highlight for manual and CRC error data
                if (mainSvc.getUseColorHighlightIndicatorData() === true){
                    indicatorSvc.setColorHighlightsForManualAndCrc(tmp);
                }
                            //set default active/reactive elec sum = 0
                initializeElectroSums(tmp);
                    //set precision
                setPrecisionAndGetSum(tmp);
                    //set precision for sum
                setSumPrecision(tmp);
                    //get difference between sums from server and our sums
                getSumDifference(tmp);
//console.log(tmp);        
                $scope.data = tmp;
                $scope.indicatorsPerPage = $scope.data.length;
                $scope.ctrlSettings.loading = false;
                if ($scope.ctrlSettings.viewMode=="" && $scope.data.length > 0){
                    getSummary(table+"/summary"+paramString);
                };
            }, 
            function(e){
                $scope.ctrlSettings.loading = false;
                console.log(e);
        });
    };
    
    var setToolTip = function(title, text, elDom, targetDom){
//console.log(elDom);                
//console.log(targetDom);    
//console.log($(elDom));        
//console.log($(targetDom));        
        $timeout(function(){
//console.log($(elDom));            
            $(elDom).qtip({
                suppress: false,
                content:{
                    text: text,
                    title: title,
                    button : true
                },
                show:{
                    event: 'click'
                },
                style:{
                    classes: 'qtip-nmc-indicator-tooltip',
                    width: 1000
                },
                hide: {
                    event: 'unfocus'
                },
                position:{
                    my: 'top right',
                    at: 'bottom right',
                    target: $(targetDom)
                }
            });
        }, 1);
    };
    
    var getSummary = function(table){
        $scope.totals = [];
        var respData = {};
        $http.get(table).then(function (response) {
            $timeout(function(){
                $scope.setScoreStyles();
            });
            
            respData = angular.copy(response.data);
            var usingProps = [
                {
                    name: "totals",
                    caption: "Итого:", 
                    type: "Cons"
                },
                {
                    name: "diffsAbs",
                    caption: "Итого по интеграторам:",
                    type: "Abs"
                }
            ];
            
            for (var propInd in usingProps){
                var el = angular.copy(respData[usingProps[propInd].name]);
                if (mainSvc.checkUndefinedNull(el)){ /*console.log(usingProps[propInd].name + " is undefined or null.");*/ continue;};
                for(var i in $scope.columns){
                    if ((el[$scope.columns[i].fieldName]!=null) && ($scope.columns[i].type !== "string")){
                        el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed($scope.ctrlSettings.precision);
                    };
                };                    
                el.onlyCons = true;
                el.type = usingProps[propInd].type;
                el.dataDateString = usingProps[propInd].caption;//"Итого:";
                el.class = "nmc-el-totals-indicator-highlight nmc-view-digital-data";
                $scope.totals.push(angular.copy(el));
                $scope.indicatorsPerPage += 1;
            };
            
            //mark diffs between cons and abs         
            if (!respData.hasOwnProperty('diffsAbs') || !respData.hasOwnProperty('totals')){
                return;
            };            
                    //work with fractional part
            //search the shortest fractional part
            $scope.tableDef.columns.forEach(function(element, index, array){
                //reset element style settings
                element.diffBgColor = "aquamarine";
                element.title = "";
                
                var columnName = element.fieldName;                
                if (angular.isUndefined(respData.firstDataAbs) 
                    || angular.isUndefined(respData.lastDataAbs) 
                    || (respData.firstDataAbs===null) 
                    || (respData.lastDataAbs===null) 
                    || !respData.firstDataAbs.hasOwnProperty(columnName) 
                    || !respData.lastDataAbs.hasOwnProperty(columnName)){
                    return;
                };                
                var textDetails = "Начальное значение = "+ respData.firstDataAbs[columnName]+" ";
//                    textDetails+="(Дата = "+ (new Date($scope.summary.firstData['dataDate'])).toLocaleString()+");<br><br>";
                var timeSuffix = "";
                if (respData.firstDataAbs['dataDateString'].length == 10) { timeSuffix = " 00:00"};
                textDetails += "(Дата = " + respData.firstDataAbs['dataDateString'] + timeSuffix + ");<br><br>";
                textDetails += "Конечное значение = " + respData.lastDataAbs[columnName] + " ";
//                    textDetails+="(Дата = "+ (new Date($scope.summary.lastData['dataDate'])).toLocaleString()+");";
                timeSuffix = "";
                if (respData.lastDataAbs['dataDateString'].length == 10) { timeSuffix = " 00:00"};
                textDetails += "(Дата = " + respData.lastDataAbs['dataDateString'] + timeSuffix + ");";
                var titleDetails = "Детальная информация";
                var elDOM = "#diffElBtn" + columnName;
                var targetDOM = "#totalAbs" + columnName;
                setToolTip(titleDetails, textDetails, elDOM, targetDOM);
                //calculate difference between totals and diffsAbs
                if (respData.diffsAbs.hasOwnProperty(columnName) && (!isNaN(respData.diffsAbs[columnName])) &&(respData.diffsAbs[columnName] != null)){                                         
                    respData.diffsAbs[columnName] = Number(respData.diffsAbs[columnName]).toFixed(3);
                };
                if (respData.totals.hasOwnProperty(columnName) && (!isNaN(respData.totals[columnName]))&&(respData.totals[columnName]!=null)){
                    respData.totals[columnName] = Number(respData.totals[columnName]).toFixed(3);
                };
                if (!respData.diffsAbs.hasOwnProperty(columnName) || !respData.totals.hasOwnProperty(columnName)){
                    return;
                }
                var lengthFractPart = 0;
                var diff = respData.diffsAbs[columnName];
                var total = respData.totals[columnName];                                     
                if((diff==null) || (total==null) || (diff=="-") || (total=="-")){
                    return;
                }
                var diffStr = diff.toString();
                var tempStrArr = diffStr.split(".");
                var diffFractPart = tempStrArr.length>1? tempStrArr[1].length : 0;
                var totalStr = total.toString();
                tempStrArr = totalStr.split(".");
                var totalFractPart = tempStrArr.length>1? tempStrArr[1].length : 0;
                //29.06.2015 - поступило требование - выводить 3 знака после запятой
                lengthFractPart = 3;//totalFractPart>diffFractPart ? diffFractPart : totalFractPart;
                var precision = Number(PRECISION_MASK.substring(0, lengthFractPart+1)+"1");
                var difference = Math.abs((respData.diffsAbs[columnName]-respData.totals[columnName])).toFixed(lengthFractPart);
                if ((difference >precision)&&(difference <= 1))
                {
                   element.diffBgColor = WARNING_BG_COLOR;
                   element.title = "Итого и показания интеграторов расходятся НЕ более чем на 1";
                   return;

                };
                if ((difference >1))
                {         
                    element.diffBgColor = ALARM_BG_COLOR;
                    element.title = "Итого и показания интеграторов расходятся БОЛЕЕ чем на 1";
                    return;
                };  
            });                        
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
         if (timeDetailType.indexOf("_abs") >= 0){
             timeDetailType = "24h";
             $scope.timeDetailType = "24h";
         };
         
         timeDetailType += $scope.ctrlSettings.viewMode;                
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
    
    $scope.$on('indicators:loadedModePrefs', function(){
//console.log('indicators:loadedModePrefs'); 
        setColumnPref($scope.ctrlSettings.loadedElectricityColumnsPref);
        $scope.getData();
    });
//    $scope.getData();
    
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
//console.log("watch indicatorDates");        
        if ($location.path() !== "/objects/indicator-electricity"){
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
    var wind = angular.element($window);
    var windowResize = function(){
//console.log("windowResize");        
        if (angular.isDefined($scope.setScoreStyles)){
            $scope.setScoreStyles();
        };
        $scope.$apply();
    };
    wind.bind('resize', windowResize); 
        
    $scope.$on('$destroy', function() {
        wind.unbind('resize', windowResize);
    });
    
    $scope.setScoreStyles = function(){
        //ровняем таблицу, если появляются полосы прокрутки
        var tableHeader = document.getElementById("indicatorConsTableHeader");
        var tableDiv = document.getElementById("divIndicatorConsTable");
        if (!mainSvc.checkUndefinedNull(tableDiv)){
            if (tableDiv.offsetWidth > tableDiv.clientWidth){
                tableDiv.style.width = tableHeader.offsetWidth + 17 + 'px';
            }else{
                tableDiv.style.width = tableHeader.offsetWidth + 'px';                    
            };
        };
    };
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
    
    function initCtrl() {
        if (!mainSvc.checkUndefinedNull($scope.contObject)){
            loadIndicatorMode($scope.contObject);
        }
    }
    
    initCtrl();
});