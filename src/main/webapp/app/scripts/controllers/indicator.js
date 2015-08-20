
angular.module('portalNMC')
    .controller('IndicatorsCtrl', ['$scope','$rootScope', '$cookies', '$window', '$http', '$location', 'crudGridDataFactory', 'FileUploader', 'notificationFactory', 'indicatorSvc',function($scope, $rootScope, $cookies, $window, $http, $location, crudGridDataFactory, FileUploader, notificationFactory, indicatorSvc){

        //Определяем оформление для таблицы показаний прибора
        
        //Задаем пути к картинкам, которые будут показывать статус расхождения итого и итого по интеграторам
    var ALERT_IMG_PATH = "images/divergenceIndicatorAlert.png";
    var CRIT_IMG_PATH = "images/divergenceIndicatorCrit.png";
    var EMPTY_IMG_PATH = "images/plug.png";    
        //Определеяю названия колонок
    var listColumns = {
            "dataDate":{
                header : "Дата",
                headerClass : "col-md-2",
                dataClass : "col-md-2"
            }, 
            "workTime":{
                header : "Время наработки, час",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "h_delta":{
                header : "ГКал отопления",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
          "m_in":{
                header : "Масса подачи, т",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "m_out":{
                header : "Масса обратки, т",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "t_in":{
                header : "Температура подачи, град C",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "t_out":{
                header : "Температура обратки, град C",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            } , 
            "t_cold":{
                header : "Температура холодной воды, град C",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            } ,
            "t_outdoor":{
                header : "Температура окружающей среды, град C",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "m_delta":{
                header : "Разница масс воды, т",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "v_in":{
                header : "Объем подачи, м3",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "v_out":{
                header : "Объем обратки, м3",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "v_delta":{
                header : "Разница объемов (потребление), м3",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "h_in":{
                header : "Входящие ГКал",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "h_out":{
                header : "ГКал на выходе",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "p_in":{
                header : "Давление на подаче, Мпа",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "p_out":{
                header : "Давление на обратке, Мпа",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "p_delta":{
                header : "Разность давлений, Мпа",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }
        }
        ;
        
      $scope.intotalColumns = [
            {
                header : "Потребление тепла, ГКал",
//                header : "",
                class : "col-md-1",
                name: "h_delta",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            }, 
            {
                header : "Масса подачи, т",
//                header : "",
                class : "col-md-1",
                name: "m_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            }, 
            {
                header : "Масса обратки, т",
//                header : "",
                class : "col-md-1",
                name: "m_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            }, 
            {
//                header : "Температура подачи, град C",
                header : "",
                class : "col-md-1 nmc-th-invisible",
                name: "t_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            }, 
            {
//                header : "Температура обратки, град C",
                header : "",
                class : "col-md-1 nmc-th-invisible",
                name: "t_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            } , 
            {
//                header : "Температура холодной воды, град C",
                header : "",
                class : "col-md-1 nmc-th-invisible",
                name: "t_cold",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            } ,
            {
//                header : "Температура окружающей среды, град C",
                header : "",
                class : "col-md-1 nmc-th-invisible",
                name: "t_outdoor",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
//                header : "Разница масс воды, т",
                header : "",
                class : "col-md-1 nmc-th-invisible",
                name: "m_delta",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Объем подачи, м3",
//                header : "",
                class : "col-md-1",
                name: "v_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Объем обратки",
//                header : "",
                class : "col-md-1",
                name: "v_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
//                header : "Разница объемов, м3",
                header : "",
                class : "col-md-1 nmc-th-invisible",
                name: "v_delta",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
//                header : "Входящие ГКал",
                header : "",
                class : "col-md-1 nmc-th-invisible",
                name: "h_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
//                header : "ГКал на выходе",
                header : "",
                class : "col-md-1 nmc-th-invisible",
                name: "h_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
//                header : "Давление на подаче",
                header : "",
                class : "col-md-1 nmc-th-invisible",
                name: "p_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
//                header : "Давление на обратке",
                header : "",
                class : "col-md-1 nmc-th-invisible",
                name: "p_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
//                header : "Разность давлений, Мпа",
                header : "",
                class : "col-md-1 nmc-th-invisible",
                name: "p_delta",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            }];

          
//    $scope.intotalColumns = [
//        {"name": "m_in",
//         "header":"Масса подачи",
//         "class":"col-md-1",
//         "imgpath" : "",
//         "imgclass": ""
//         ,"title":""
//        },
//        {"name": "m_out",
//         "header":"Масса обратки",
//         "class":"col-md-1"
//         ,"imgpath" : ""
//         ,"imgclass": ""
//         ,"title":""
//        },
//        {"name": "v_in",
//         "header":"Объем подачи",
//         "class":"col-md-1"
//         ,"imgpath" : ""
//         ,"imgclass": ""
//         ,"title":""
//        },
//        {"name": "v_out",
//         "header":"Объем обратки",
//         "class":"col-md-1"
//         ,"imgpath" : ""
//         ,"imgclass": ""
//         ,"title":""
//        },
//        {"name": "h_delta",
//         "header":"ГКал отопления",
//         "class":"col-md-1"
//         ,"imgpath" : ""
//         ,"imgclass": ""
//         ,"title":""
//        }
//    ];    
    
    $scope.tableDef = {
        tableClass : "crud-grid table table-lighter table-bordered table-condensed table-hover",
        hideHeader : false,
        headerClassTR : "nmc-main-table-header",
        columns : [{
                header : "Дата",
                headerClass : "col-md-2",
                dataClass : "col-md-2",
                fieldName: "dataDate"
            }, 
            {
                header : "Время наработки, час",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "workTime"
            }, 
            {
                header : "Потребление тепла, ГКал",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "h_delta"
            }, 
            {
                header : "Масса подачи, т",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "m_in"
            }, 
            {
                header : "Масса обратки, т",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "m_out"
            },
            {
                header : "Разность масс, т",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "m_delta"
            },
            {
                header : "Темп. подачи",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "t_in"
                ,dataType: "temperature"                
            }, 
            {
                header : "Темп. обратки",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "t_out"
                ,dataType: "temperature"                
            } , 
            {
                header : "Темп. ХВС",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "t_cold"
                ,dataType: "temperature"                
            } ,
            {
                header : "Темп. окр. среды",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "t_outdoor"
                ,dataType: "temperature"                
            },

            {
                header : "Объем подачи, м3",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "v_in"
            },
            {
                header : "Объем обратки, м3",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "v_out"
            },
            {
                header : "Разность объемов, м3",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "v_delta"
            },
            {
                header : "ГКал на входе",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "h_in"
            },
            {
                header : "ГКал на выходе",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "h_out"
            },
            {
                header : "Давление на подаче, Мпа",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "p_in"
            },
            {
                header : "Давление на обратке, Мпа",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "p_out"
            },
            {
                header : "Разность давлений, Мпа",
                headerClass : "col-md-1",
                dataClass : "col-md-1",
                fieldName: "p_delta"
            }]
    };
    
    $scope.summary = {};        
    $scope.totalIndicators = 0;
    $scope.indicatorsPerPage = 25; // this should match however many results your API puts on one page    
    $scope.timeDetailType = "24h";    
    $scope.data = [];    
    $scope.pagination = {
        current: 1
    };
        //The flag for the link to the file with delete data
    $scope.showLinkToFileFlag = false;
        //flag for zpoint, which control manual loading data - true: on manual loading, false: off manual loading
    $scope.isManualLoading = $cookies.isManualLoading==="true"?true:false;
        
//console.log($cookies.isManualLoading);        
//console.log($scope.isManualLoading);        
        
    //file upload settings
    var initFileUploader =  function(){    
         var contZPoint = $cookies.contZPoint;
         var timeDetailType = "24h";
         var contObject = $cookies.contObject;
//         /contObjects/{contObjectId}/contZPoints/{contZPointId}/service/{timeDetailType}/csv
        $scope.uploader = new FileUploader({
            url: url = "../api/subscr/contObjects/"+contObject+"/contZPoints/"+contZPoint+"/service/"+timeDetailType+"/csv",
           
        });
        
        $scope.uploader.onErrorItem = function(fileItem, response, status, headers) {
            console.info('onErrorItem', status, response);
            notificationFactory.errorInfo(response.resultCode, response.description);
        };
        
        $scope.uploader.onSuccessItem = function(item, response, status, headers){
//console.log(item);            
        };
    };
    initFileUploader();
        
        //Functions for work with date
        //function for date converting
    var DateNMC = function(millisec){
//            var coeffecient = 0;//3600*3*1000;
//            var userOffset = (new Date()).getTimezoneOffset()*60000;
//console.log(millisec);
        var tempDate = new Date(millisec);
//console.log(tempDate.getTime());   
        console.log(tempDate); 
//            tempDate.getTi
        return tempDate;

    };
        //convert date to string
    var printDateNMC = function(dateNMC){
        function pad(num){
            num = num.toString();
            if (num.length == 1) return "0"+num;
            return num;
        }

        var dateToString = pad(dateNMC.getUTCDate())+"."+pad(dateNMC.getUTCMonth()+1)+"."+pad(dateNMC.getUTCFullYear())+" "+pad(dateNMC.getUTCHours())+":"+pad(dateNMC.getUTCMinutes());
        // +1 to month, because month start with index=0
        return dateToString;
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
console.log($location.search());
        var pathParams = $location.search();
        var tmpZpId = indicatorSvc.getZpointId();    
        var tmpContObjectId = indicatorSvc.getContObjectId();
        var tmpZpName = indicatorSvc.getZpointName();    
        var tmpContObjectName = indicatorSvc.getContObjectName();
//        if (angular.isUndefined(tmpZpId)||(tmpZpId===null)){
//            if (angular.isDefined($cookies.contZPoint)&&($cookies.contZPoint!=="null")){
//                indicatorSvc.setZpointId($cookies.contZPoint);
//            };
//        };
//        if (angular.isUndefined(tmpContObjectId)||(tmpContObjectId===null)){
//            if (angular.isDefined($cookies.contObject)&&($cookies.contObject!=="null")){
//                indicatorSvc.setContObjectId($cookies.contObject);
//            };
//        };
//        
//        if (angular.isUndefined(tmpZpName)||(tmpZpName===null)){
//            if (angular.isDefined($cookies.contZPointName)&&($cookies.contZPointName!=="null")){
//                indicatorSvc.setZpointName($cookies.contZPointName);
//            };
//        };
//        if (angular.isUndefined(tmpContObjectName)||(tmpContObjectName===null)){
//            if (angular.isDefined($cookies.contObjectName)&&($cookies.contObjectName!=="null")){
//                indicatorSvc.setContObjectName($cookies.contObjectName);
//            };
//        };
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
        
        $scope.contZPoint = indicatorSvc.getZpointId();
        $scope.contZPointName = indicatorSvc.getZpointName() || "Не задано";
        $scope.contObject = indicatorSvc.getContObjectId();
        $scope.contObjectName = indicatorSvc.getContObjectName() || "Не задано";
        
        //clear cookies
//console.log($cookies);        
//        $cookies.contZPoint = null;
//        $cookies.contObject = null;
//        $cookies.contZPointName = null;
//        $cookies.contObjectName = null;
    };
        //run init method
    initIndicatorParams();
        
      //Получаем показания
    $scope.columns = [];
    $scope.getData = function (pageNumber) {
//console.log("getData");        
        $scope.pagination.current = pageNumber;   
//         $scope.contZPoint = $cookies.contZPoint;
//         $scope.contZPointName = $cookies.contZPointName;
//         $scope.contObject = $cookies.contObject;
//         $scope.contObjectName = $cookies.contObjectName;
        
//console.log($scope.timeDetailType);
//console.log($cookies.timeDetailType);        
         var timeDetailType = $scope.timeDetailType || $cookies.timeDetailType;
         
         $scope.zpointTable = "../api/subscr/"+$scope.contObject+"/service/"+timeDetailType+"/"+$scope.contZPoint+"/paged?beginDate="+$rootScope.reportStart+"&endDate="+$rootScope.reportEnd+"&page="+(pageNumber-1)+"&size="+$scope.indicatorsPerPage;
        var table =  $scope.zpointTable;
//console.log(table);        
        crudGridDataFactory(table).get(function (data) {           
                $scope.totalIndicators = data.totalElements;
 
                $scope.columns = $scope.tableDef.columns;
                var tmp = data.objects.map(function(el){
                    var result  = {};
                    for(var i in $scope.columns){
                        if ($scope.columns[i].fieldName == "dataDate"){
//console.log("Indicator id = "+el.id);                            
//console.log("Indicator timestamp in millisec, which get from server = "+el.dataDate);
//console.log("Indicator timestamp +3 hours in sec = "+(Math.round(el.dataDate/1000.0)+3*3600));                            
//                          var datad = DateNMC(el.dataDate);
//console.log(datad.getTimezoneOffset());
//console.log(datad.toLocaleString());                            
                            el.dataDate=el.dataDateString;//printDateNMC(datad);
                            continue;
                        }
                        if ((el[$scope.columns[i].fieldName]!=null)&&($scope.columns[i].fieldName !== "dataDateString")){
                            el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed(3);
                        };
                        if ((el[$scope.columns[i].fieldName]==null)&&($scope.columns[i].fieldName === "m_delta")){
                            if((el.m_out!=null)&&(el.m_in!=null)){
                                el[$scope.columns[i].fieldName] = (el.m_out-el.m_in).toFixed(3);
                            };
                        };
                        if ((el[$scope.columns[i].fieldName]==null)&&($scope.columns[i].fieldName === "v_delta")){
                            if((el.v_out!=null)&&(el.v_in!=null)){
                                el[$scope.columns[i].fieldName] = (el.v_out-el.v_in).toFixed(3);
                            };
                        };
                        if ((el[$scope.columns[i].fieldName]==null)&&($scope.columns[i].fieldName === "p_delta")){
                            if((el.p_out!=null)&&(el.p_in!=null)){
                                el[$scope.columns[i].fieldName] = (el.p_out-el.p_in).toFixed(3);
                            };
                        };
                        
                    };                    
                });
                $scope.data = data.objects;
        });
         
        $scope.setScoreStyles = function(){
            //set styles for score/integrators
            var indicatorThDataDate = document.getElementById("indicators_th_dataDate");
            var indicatorThWorkTime = document.getElementById("indicators_th_workTime");
            var totalThHead = document.getElementById("totals_th_head"); 
            $scope.totals_th_head_style = indicatorThDataDate.clientWidth+indicatorThWorkTime.clientWidth+4;
//                totalThHead.clientWidth = indicatorThDataDate.clientWidth+indicatorThWorkTime.clientWidth;
            $scope.intotalColumns.forEach(function(element){
                var indicatorTh = document.getElementById("indicators_th_"+element.name);
                element.ngstyle =indicatorTh.clientWidth+1;

            });
        };
        
        // get summary (score)
        var table_summary = table.replace("paged", "summary");
        crudGridDataFactory(table_summary).get(function(data){        
                $scope.setScoreStyles();
                $scope.summary = data;            
                if ($scope.summary.hasOwnProperty('diffs')){
                    $scope.intotalColumns.forEach(function(element){
                        var columnName = element.name;
                        if ($scope.summary.diffs.hasOwnProperty(columnName) &&($scope.summary.diffs[columnName]!=null)){                
                            $scope.summary.diffs[columnName] = $scope.summary.diffs[columnName].toFixed(3);
                        }else{
                            $scope.summary.diffs[columnName] = "-";
                        };
                    });
                };
                if ($scope.summary.hasOwnProperty('totals')){                 
                    $scope.intotalColumns.forEach(function(element){                       
                        var columnName = element.name;
                        if ($scope.summary.totals.hasOwnProperty(columnName) &&($scope.summary.totals[columnName]!=null)){                
                            $scope.summary.totals[columnName] = $scope.summary.totals[columnName].toFixed(3);
                        }else{
                            $scope.summary.totals[columnName] = "-";
                        };
                    });
                };
                if (!$scope.summary.hasOwnProperty('diffs') || !$scope.summary.hasOwnProperty('totals')){
                    return;
                };
                        //work with fractional part
                //search the shortest fractional part
                $scope.intotalColumns.forEach(function(element, index, array){                
                    var columnName = element.name;                  
                    if (angular.isUndefined($scope.summary.firstData) || angular.isUndefined($scope.summary.lastData) || ($scope.summary.firstData===null) || ($scope.summary.lastData===null) || !$scope.summary.firstData.hasOwnProperty(columnName) || !$scope.summary.lastData.hasOwnProperty(columnName)){
                        return;
                    };
                    var textDetails = "Начальное значение = "+ $scope.summary.firstData[columnName]+" ";
//                    textDetails+="(Дата = "+ (new Date($scope.summary.firstData['dataDate'])).toLocaleString()+");<br><br>";
                    textDetails+="(Дата = "+ $scope.summary.firstData['dataDateString']+");<br><br>";
                    textDetails+= "Конечное значение = "+ $scope.summary.lastData[columnName]+" ";
//                    textDetails+="(Дата = "+ (new Date($scope.summary.lastData['dataDate'])).toLocaleString()+");";
                    textDetails+="(Дата = "+ $scope.summary.lastData['dataDateString']+");";
                    var titleDetails = "Детальная информация";
                    var elDOM = "#diffBtn"+columnName;
                    var targetDOM = "#total"+columnName;
                    $(elDOM).qtip({
                        suppress: false,
                        content:{
                            text: textDetails,
                            title: titleDetails,
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
                            target: $(targetDOM)
                        }
                    });
              
                    if ($scope.summary.diffs.hasOwnProperty(columnName) &&($scope.summary.diffs[columnName]!=null)){                
//console.log(Number($scope.summary.diffs[columnName]));                          
                        $scope.summary.diffs[columnName] = Number($scope.summary.diffs[columnName]).toFixed(3);
                    };
                    if ($scope.summary.totals.hasOwnProperty(columnName) && ($scope.summary.totals[columnName]!=null)){
                        $scope.summary.totals[columnName] = Number($scope.summary.totals[columnName]).toFixed(3);
                    };
                    if (!$scope.summary.diffs.hasOwnProperty(columnName) || !$scope.summary.totals.hasOwnProperty(columnName)){
                        return;
                    }
                    var lengthFractPart = 0;
                    var diff = $scope.summary.diffs[columnName];
                    var total = $scope.summary.totals[columnName];
//console.log(diff);                    
//console.log(total);                                        
                    if((diff==null) || (total==null)){
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
//                    $scope.summary.diffs[columnName] = diff.toFixed(lengthFractPart);
//                    $scope.summary.totals[columnName] = total.toFixed(lengthFractPart);                  

                    var precision = Number("0.00000000000000000000".substring(0, lengthFractPart+1)+"1");
//            console.log("diff = "+$scope.summary.diffs[columnName]);           
//            console.log("total = "+$scope.summary.totals[columnName]);           
//            console.log("precision = "+precision);        

                    var difference = Math.abs(($scope.summary.diffs[columnName]-$scope.summary.totals[columnName])).toFixed(lengthFractPart);
//            console.log("difference = "+difference);         
            //        var difference = Math.abs(total - diff);
                    if ((difference >precision)&&(difference <= 1))
                    {
//            console.log(ALERT_IMG_PATH);         
                       element.imgpath=  ALERT_IMG_PATH;
                       element.imgclass= "nmc-img-divergence-indicator";
                       element.title = "Итого и показания интеграторов расходятся НЕ более чем на 1";
                       return;

                    };
                    if ((difference >1))
                    {  
//            console.log(CRIT_IMG_PATH);            
                        element.imgpath = CRIT_IMG_PATH;
                        element.imgclass= "nmc-img-divergence-indicator";
                        element.title = "Итого и показания интеграторов расходятся БОЛЕЕ чем на 1";
                        return;
                    };
                    element.imgpath = EMPTY_IMG_PATH;
                    element.imgclass= "";
                    element.title = "";   
                });
//console.log(data);            
        });
    };

    $scope.pageChanged = function(newPage) {       
        $scope.getData(newPage);
    };  
        
    $scope.$watch('reportStart', function (newDates) {  
//console.log("change reportStart");        
        if( (typeof $rootScope.reportStart == 'undefined') || ($rootScope.reportStart==null) ){
            return;
        }
        $scope.getData(1);                              
    }, false);  
        
    //listen window resize
    var wind = angular.element($window);
    var windowResize = function(){
        $scope.setScoreStyles();
        $scope.$apply();
    };
    wind.bind('resize', windowResize); 
        
    $scope.$on('$destroy', function() {
        wind.unbind('resize', windowResize);
    });        
        
    $scope.saveIndicatorsToFile = function(exForUrl){ 
        var contZPoint = $cookies.contZPoint;
        var contObject = $cookies.contObject;
        var timeDetailType = $scope.timeDetailType || $cookies.timeDetailType;
        var url = "../api/subscr/"+contObject+"/service/"+timeDetailType+"/"+contZPoint+"/csv"+exForUrl+"?beginDate="+$rootScope.reportStart+"&endDate="+$rootScope.reportEnd;
        window.open(url);
    };
        //Upload file with the indicator data to the server
    $scope.uploadFile =function(){
        $scope.uploader.queue[$scope.uploader.queue.length-1].upload();
    };
    
        //delete indicator data for period
    $scope.deleteData = function(){
        var contObject = $scope.contObject || $cookies.contObject;
        var contZpoint = $scope.contZPoint || $cookies.contZpoint;      
        var timeDetailType = "24h";
        var fromDate = $rootScope.startDateToDel;
        var toDate = $rootScope.endDateToDel;
        var url = "../api/subscr/contObjects/"+contObject+"/contZPoints/"+contZpoint+"/service/"+timeDetailType+"/csv"+"?beginDate="+fromDate+"&endDate="+toDate;
        $http.delete(url)
            .success(function(data){       
                notificationFactory.success();
                $scope.linkToFileWithDeleteData = "../api/subscr/service/out/csv/"+ data.filename;
                $scope.fileWithDeleteData = data.filename;
                $scope.showLinkToFileFlag = true;
                $scope.getData(1);
            })
            .error(function(err){
                notificationFactory.errorInfo(err.title, err.description)
            });
    };
    
    //check indicators for data (проверка: есть данные для отображения или нет)
    $scope.isHaveData = function(){
        if (angular.isUndefined($scope.data)||($scope.data.length == 0)){
            return false;
        };
        return true;
    };
                
}]);