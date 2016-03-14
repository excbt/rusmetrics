
angular.module('portalNMC')
    .controller('IndicatorsCtrl', ['$scope','$rootScope', '$cookies', '$window', '$http', '$location', 'crudGridDataFactory', 'FileUploader', 'notificationFactory', 'indicatorSvc', 'mainSvc',function($scope, $rootScope, $cookies, $window, $http, $location, crudGridDataFactory, FileUploader, notificationFactory, indicatorSvc, mainSvc){
        $rootScope.ctxId = "indicators_page";
        
//console.log($rootScope.reportStart);
//console.log($rootScope.reportEnd);        
        // Настройки интервала дат для страницы с показаниями
    if (angular.isDefined($location.search().fromDate)&&($location.search().fromDate!=null)){
        $scope.indicatorDates = {
            startDate : $location.search().fromDate,
            endDate :  $location.search().toDate
        };
    }else{
//        $scope.indicatorDates = {
//            startDate : moment().subtract(6, 'days').startOf('day'),
//            endDate :  moment().endOf('day')
//        };
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
//console.log($scope.indicatorDates.startDate);
//console.log($scope.indicatorDates.endDate); 
    
    $scope.ctrlSettings ={};
    $scope.ctrlSettings.ctxId = "zpoint_indicator_page";
        
//    sort settings
    $scope.ctrlSettings.orderBy = {};
    if (angular.isDefined($cookies.indicatorsortorder)&&($cookies.indicatorsortorder!=null)){  
        switch($cookies.indicatorsortorder){
            case "asc": $scope.ctrlSettings.orderBy = {field: "dataDate", asc: true, desc: false, order: "asc"}; break;
            case "desc": $scope.ctrlSettings.orderBy = {field: "dataDate", asc: false, desc: true, order: "desc"}; break;   
        };        
    }else{
        $scope.ctrlSettings.orderBy = {field: "dataDate", asc: false, desc: true, order: "desc"};
    };
//console.log($scope.ctrlSettings.orderBy.order);        

        //Определяем оформление для таблицы показаний прибора
        
        //Задаем пути к картинкам, которые будут показывать статус расхождения итого и итого по интеграторам
    var ALERT_IMG_PATH = "images/divergenceIndicatorAlert.png";
    var CRIT_IMG_PATH = "images/divergenceIndicatorCrit.png";
    var EMPTY_IMG_PATH = "images/plug.png";    
        //Определеяю названия колонок
        
      $scope.intotalColumns = [
            {
                header : "Потребление тепла, ГКал",
//                header : "",
                class : "col-xs-1 col-md-1",
                name: "h_delta",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            }, 
            {
                header : "Масса подачи, т",
//                header : "",
                class : "col-xs-1 col-md-1",
                name: "m_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            }, 
            {
                header : "Масса обратки, т",
//                header : "",
                class : "col-xs-1 col-md-1",
                name: "m_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            }, 
            {
                header : "Разница масс воды, т",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "m_delta",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Темп. подачи",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "t_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
                ,dataType: "temperature"
            }, 
            {
                header : "Темп. обратки",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "t_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
                ,dataType: "temperature"
            } , 
            {
                header : "Темп. ХВС",
//                header : "",
                class : "col-xs-1 col-md-1",    
//                class : "col-md-1 nmc-th-invisible",
                name: "t_cold",
                "imgpath" : "",
                "imgclass": "",
                "title":""
                ,dataType: "temperature"
            } ,
            {
                header : "Темп. окр. среды",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "t_outdoor",
                "imgpath" : "",
                "imgclass": "",
                "title":""
                ,dataType: "temperature"
            },
            {
                header : "Объем подачи, м3",
//                header : "",
                class : "col-xs-1 col-md-1",
                name: "v_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Объем обратки, м3",
//                header : "",
                class : "col-xs-1 col-md-1",
                name: "v_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Разница объемов, м3",
//                header : "",
                
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "v_delta",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "ГКал на входе",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "h_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "ГКал на выходе",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "h_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Давление на подаче, Мпа",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "p_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Давление на обратке, Мпа",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "p_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Разность давлений, Мпа",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "p_delta",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            }];

          
    $scope.integratorColumns = [
        {
            header : "Дата",
            headerClass : "col-xs-2 col-md-2",
            dataClass : "col-xs-2 col-md-2",
            fieldName: "dataDate"
        }, 
        {
            header : "Время наработки, час",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "workTime"
        },
        {
            header : "Потребление тепла, ГКал",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "h_delta"
        }, 
        {
            header : "Масса подачи, т",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "m_in"
        }, 
        {
            header : "Масса обратки, т",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "m_out"
        },
        {
            header : "Объем подачи, м3",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "v_in"
        },
        {
            header : "Объем обратки, м3",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "v_out"
        },
        {
            header : "ГКал на входе",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "h_in"
        },
        {
            header : "ГКал на выходе",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "h_out"
        }
    ];
    
    $scope.indicatorColumns = [{
                header : "Дата",
                headerClass : "col-xs-2 col-md-2",
                dataClass : "col-xs-2 col-md-2",
                fieldName: "dataDate",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            }, 
            {
                header : "Время наработки, час",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "workTime",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            }, 
            {
                header : "Потребление тепла, ГКал",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "h_delta",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            }, 
            {
                header : "Масса подачи, т",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "m_in",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            }, 
            {
                header : "Масса обратки, т",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "m_out",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            },
            {
                header : "Разность масс, т",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "m_delta",
                "1h": "1h",
                "24h" : "24h"
            },
            {
                header : "Темп. подачи",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "t_in"
                ,dataType: "temperature",
                "1h": "1h",
                "24h" : "24h"               
            }, 
            {
                header : "Темп. обратки",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "t_out"
                ,dataType: "temperature",
                "1h": "1h",
                "24h" : "24h"                
            } , 
            {
                header : "Темп. ХВС",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "t_cold"
                ,dataType: "temperature",
                "1h": "1h",
                "24h" : "24h"               
            } ,
            {
                header : "Темп. окр. среды",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "t_outdoor"
                ,dataType: "temperature",
                "1h": "1h",
                "24h" : "24h"                
            },

            {
                header : "Объем подачи, м3",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "v_in",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            },
            {
                header : "Объем обратки, м3",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "v_out",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            },
            {
                header : "Разность объемов, м3",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "v_delta",
                "1h": "1h",
                "24h" : "24h"
            },
            {
                header : "ГКал на входе",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "h_in",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            },
            {
                header : "ГКал на выходе",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "h_out",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            },
            {
                header : "Давление на подаче, Мпа",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "p_in",
                "1h": "1h",
                "24h" : "24h"
            },
            {
                header : "Давление на обратке, Мпа",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "p_out",
                "1h": "1h",
                "24h" : "24h"
            },
            {
                header : "Разность давлений, Мпа",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "p_delta",
                "1h": "1h",
                "24h" : "24h"
            }
    ];
    
    $scope.tableDef = {
        tableClass : "crud-grid table table-lighter table-bordered table-condensed table-hover",
        hideHeader : false,
        headerClassTR : "nmc-main-table-header-indicator",
        columns : $scope.indicatorColumns
    };
//    if ($scope.timeDetailType == "1h_abs")
//        $scope.tableDef.columns = $scope.inte;
    
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
            url: url = "../api/subscr/contObjects/" + contObject + "/contZPoints/" + contZPoint + "/service/" + timeDetailType + "/csv",
           
        });
        
        $scope.uploader.onErrorItem = function(fileItem, response, status, headers) {
            console.info('onErrorItem', status, response);
            errorCallback(response);
            //notificationFactory.errorInfo(response.resultCode, response.description);
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
//console.log($location.url());        
//console.log($location.search());
        var pathParams = $location.search();
//console.log($location);        
        var tmpZpId = null;//indicatorSvc.getZpointId();    
        var tmpContObjectId = null;//indicatorSvc.getContObjectId();
        var tmpZpName = null;//indicatorSvc.getZpointName();    
        var tmpContObjectName = null;//indicatorSvc.getContObjectName();
        var tmpTimeDetailType = null;
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
        
        if (angular.isUndefined(tmpTimeDetailType)||(tmpTimeDetailType===null)){
            if (angular.isDefined(pathParams.timeDetailType)&&(pathParams.timeDetailType!=="null")){
//                indicatorSvc.setTimeDetailType(pathParams.timeDetailType);
                $scope.timeDetailType = pathParams.timeDetailType;
            }else{
//console.log($cookies.timeDetailType);                
                if (angular.isDefined($cookies.timeDetailType)&&($cookies.timeDetailType!="undefined")&&($cookies.timeDetailType!="null")){
                    $scope.timeDetailType = $cookies.timeDetailType;
                }else{
//console.log("param TDT and COOKie TDT is undefined");                    
                    $scope.timeDetailType = indicatorSvc.getTimeDetailType();
                };
            };
        };
        
        $scope.contZPoint = indicatorSvc.getZpointId();
        $scope.contZPointName = (indicatorSvc.getZpointName()!="undefined")?indicatorSvc.getZpointName() : "Без названия";
        $scope.contObject = indicatorSvc.getContObjectId();
        $scope.contObjectName = (indicatorSvc.getContObjectName()!="undefined")?indicatorSvc.getContObjectName() : "Без названия";     
        
        //clear cookies
//console.log($cookies);        
//        $cookies.contZPoint = null;
//        $cookies.contObject = null;
//        $cookies.contZPointName = null;
//        $cookies.contObjectName = null;
        
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
//console.log($scope.timeDetailType);  
//console.log($rootScope.reportStart);        
//console.log($rootScope.reportEnd);        
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
         
         $scope.zpointTable = "../api/subscr/"+$scope.contObject+"/service/"+timeDetailType+"/"+$scope.contZPoint+"/paged?beginDate="+$rootScope.reportStart+"&endDate="+$rootScope.reportEnd+"&page="+(pageNumber-1)+"&size="+$scope.indicatorsPerPage+"&dataDateSort="+$scope.ctrlSettings.orderBy.order;
        var table =  $scope.zpointTable;
//console.log(table);        
        crudGridDataFactory(table).get(function (data) {
//console.log(data);            
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
                                el[$scope.columns[i].fieldName] = (el.m_in-el.m_out).toFixed(3);
                            };
                        };
                        if ((el[$scope.columns[i].fieldName]==null)&&($scope.columns[i].fieldName === "v_delta")){
                            if((el.v_out!=null)&&(el.v_in!=null)){
                                el[$scope.columns[i].fieldName] = (el.v_in-el.v_out).toFixed(3);
                            };
                        };
                        if ((el[$scope.columns[i].fieldName]==null)&&($scope.columns[i].fieldName === "p_delta")){
                            if((el.p_out!=null)&&(el.p_in!=null)){
                                el[$scope.columns[i].fieldName] = (el.p_in-el.p_out).toFixed(3);
                            };
                        };
                        
                    };                    
                });
                $scope.data = data.objects;            
        });
         
        $scope.setScoreStyles = function(){
            //ровняем таблицу, если появляются полосы прокрутки
            var tableHeader = document.getElementById("indicatorTableHeader");
            var tableDiv = document.getElementById("divIndicatorTable");
            if (!mainSvc.checkUndefinedNull(tableDiv) && !mainSvc.checkUndefinedNull(tableHeader)){
                if (tableHeader.offsetWidth == 0){
                    return "Indicators. tableHeader.offsetWidth == 0";
                };
                if (tableDiv.offsetWidth > tableDiv.clientWidth){
                    tableDiv.style.width = tableHeader.offsetWidth + 17 + 'px';
                }else{
                    tableDiv.style.width = tableHeader.offsetWidth + 'px';                    
                };
            };
//console.log("Run setScoreStyles");            
            //set styles for score/integrators
//            $scope.tableDef.columns.forEach(function(element){
            $scope.tableDef.columns.some(function(element){    
                var indicatorTd = document.getElementById("indicators_th_"+element.fieldName);
                var indicatorHead = document.getElementById("indicators_head_"+element.fieldName);
//console.log("indicators_td_"+element.fieldName);                
//console.log(indicatorHead);                    
//console.log(indicatorTd);                 
                if ((angular.isDefined(indicatorTd))&&(indicatorTd!=null)&&(angular.isDefined(indicatorHead))&&(indicatorHead!=null)){
//                    if (indicatorTd.offsetWidth>indicatorHead.offsetWidth){
                        var thWidth = Math.min(indicatorTd.offsetWidth, indicatorTd.clientWidth);
                        indicatorHead.style.width =thWidth;//indicatorTd.offsetWidth+"px";                   
//                    }else{
//                        indicatorTd.style.width =indicatorHead.offsetWidth+"px";                   
//                    };
//console.log(thWidth);                     
//console.log(indicatorTd.offsetWidth);                     
//console.log(indicatorTd.clientWidth);                     
//console.log(indicatorHead.style.width);                    
                };
//return true;
            });
            
            var indicatorThDataDate = document.getElementById("indicators_th_dataDate");
            var indicatorThWorkTime = document.getElementById("indicators_th_workTime");
            var totalThHead = document.getElementById("totals_th_head"); 
//console.log(indicatorThDataDate);            
//console.log(angular.isDefined(indicatorThDataDate));
//console.log(indicatorThDataDate.clientWidth);
//console.log(angular.isDefined(indicatorThWorkTime.clientWidth));
//console.log(indicatorThWorkTime.clientWidth);            
            if ((angular.isDefined(indicatorThDataDate))&&(indicatorThDataDate!=null)&&(angular.isDefined(indicatorThWorkTime))&&(indicatorThWorkTime!=null)){
                $scope.totals_th_head_style = indicatorThDataDate.offsetWidth+indicatorThWorkTime.offsetWidth+4;
                
            };
            
//                totalThHead.clientWidth = indicatorThDataDate.clientWidth+indicatorThWorkTime.clientWidth;
//            $scope.intotalColumns.forEach(function(element){
            $scope.intotalColumns.some(function(element){    
                var indicatorTh = document.getElementById("indicators_th_"+element.name);
                var indicatorHead = document.getElementById("indicators_head_"+element.name);
                if ((angular.isDefined(indicatorTh))&&(indicatorTh!=null)){
                    element.ngstyle =indicatorTh.offsetWidth;
//                    indicatorHead.width = indicatorTh.offsetWidth;
//console.log(indicatorHead.offsetWidth);                    
//console.log(indicatorTh.offsetWidth);                     
//console.log(element);                    
                };
//return true;
            });
            
            //Если есть боковая полоса прокрутки, то нужно увеличить область таблицы на 15px;
//            var tableEl = document.getElementsByClassName("container-fluid nmc-indicator-table-without-fixed-header");
//console.log(tableEl);            
        };
        
                //when document ready - set styles for score table.
//        $(document).ready(function() {
//            $scope.setScoreStyles();
//        });
        
        $scope.onTableLoad = function(){
            $scope.setScoreStyles();
        };
        
        
        //prepare summary data to the view - apply toFixed
        var prepareSummary = function(arr){
            $scope.intotalColumns.forEach(function(element){                       
                var columnName = element.name;
                if (arr.hasOwnProperty(columnName) &&(!isNaN(arr[columnName]))&&(arr[columnName]!=null)){                
                    arr[columnName] = arr[columnName].toFixed(3);
                }else{
                    if ((columnName=="m_delta")&&(arr.m_out!="-")&&(arr.m_in!="-")){                   
                            arr[columnName] = (arr.m_in-arr.m_out).toFixed(3);
                    }else if ((columnName=="v_delta")&&(arr.v_out!="-")&&(arr.v_in!="-")){                    
                            arr[columnName] = (arr.v_in-arr.v_out).toFixed(3);
                    }else if ((columnName=="p_delta")&&(arr.p_out!="-")&&(arr.p_in!="-")){                     
                            arr[columnName] = (arr.p_in-arr.p_out).toFixed(3);
                    }else{
                        arr[columnName] = "-";
                    };
                };
            });
        };
        
        // get summary (score)
        var table_summary = table.replace("paged", "summary");
//console.log(table_summary);        
        crudGridDataFactory(table_summary).get(function(data){        
                $scope.setScoreStyles();
                $scope.intotalColumns.forEach(function(element, index, array){
                    element.imgpath = EMPTY_IMG_PATH;
                    element.imgclass= "";
                    element.title = "";
                });

                $scope.summary = angular.copy(data);
//console.log(data);            
                if ($scope.summary.hasOwnProperty('diffs')){
                    prepareSummary($scope.summary.diffs);
//                    $scope.intotalColumns.forEach(function(element){
//                        var columnName = element.name;
//                        if ($scope.summary.diffs.hasOwnProperty(columnName) &&(!isNaN($scope.summary.diffs[columnName]))&&($scope.summary.diffs[columnName]!=null)){                                                     
//                            $scope.summary.diffs[columnName] = $scope.summary.diffs[columnName].toFixed(3);
//                        }else{
//                            $scope.summary.diffs[columnName] = "-";
//                        };
//                    });
                };
                if ($scope.summary.hasOwnProperty('totals')){ 
                    prepareSummary($scope.summary.totals);
//                    $scope.intotalColumns.forEach(function(element){                       
//                        var columnName = element.name;
//                        if ($scope.summary.totals.hasOwnProperty(columnName) &&(!isNaN($scope.summary.totals[columnName]))&&($scope.summary.totals[columnName]!=null)){                
//                            $scope.summary.totals[columnName] = $scope.summary.totals[columnName].toFixed(3);
//                        }else{
//                            $scope.summary.totals[columnName] = "-";
//                        };
//                    });
                };
                if ($scope.summary.hasOwnProperty('average')){ 
                    prepareSummary($scope.summary.average);
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
                    var timeSuffix = "";
                    if ($scope.summary.firstData['dataDateString'].length == 10) { timeSuffix = " 00:00"};
                    textDetails += "(Дата = " + $scope.summary.firstData['dataDateString'] + timeSuffix + ");<br><br>";
                    textDetails += "Конечное значение = " + $scope.summary.lastData[columnName] + " ";
//                    textDetails+="(Дата = "+ (new Date($scope.summary.lastData['dataDate'])).toLocaleString()+");";
                    timeSuffix = "";
                    if ($scope.summary.lastData['dataDateString'].length == 10) { timeSuffix = " 00:00"};
                    textDetails += "(Дата = " + $scope.summary.lastData['dataDateString'] + timeSuffix + ");";
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
              
                    if ($scope.summary.diffs.hasOwnProperty(columnName) &&(!isNaN($scope.summary.diffs[columnName]))&&($scope.summary.diffs[columnName]!=null)){                                         
                        $scope.summary.diffs[columnName] = Number($scope.summary.diffs[columnName]).toFixed(3);
                    };
                    if ($scope.summary.totals.hasOwnProperty(columnName) && (!isNaN($scope.summary.totals[columnName]))&&($scope.summary.totals[columnName]!=null)){
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
                    element.imgclass = "";
                    element.title = "";   
                });
//console.log(data);            
        });
    };
        
        //first load data
//console.log("first load data");        
    $scope.getData(1);

    $scope.pageChanged = function(newPage) {
//console.log("pageChanged getData");        
        $scope.getData(newPage);
    };
    $scope.changeTimeDetailType = function(){
//console.log("changeTimeDetailType getData");
        $cookies.timeDetailType = $scope.timeDetailType;
//console.log($cookies.timeDetailType);        
        $scope.getData(1);
    };
        
    $scope.$watch('indicatorDates', function (newDates, oldDates) {
//console.log("Date-range-settings indicatorDates");        
        if ($location.path() !== "/objects/indicators"){
            return;
        };
//console.log("Date-range-settings indicatorDates1");  
        if(newDates === oldDates){
            return;
        };
        $cookies.fromDate = moment(newDates.startDate).format('YYYY-MM-DD');
        $cookies.toDate = moment(newDates.endDate).format('YYYY-MM-DD');
        indicatorSvc.setFromDate(moment(newDates.startDate).format('YYYY-MM-DD'));
        indicatorSvc.setToDate(moment(newDates.endDate).format('YYYY-MM-DD'));
        $rootScope.reportStart = moment(newDates.startDate).format('YYYY-MM-DD');
        $rootScope.reportEnd = moment(newDates.endDate).format('YYYY-MM-DD');
//console.log("watch('indicatorDates') getData");        
        $scope.getData(1);
    }, false);    
        
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
        
    $scope.saveIndicatorsToFile = function(exForUrl){ 
        
        var contZPoint = $scope.contZPoint || $cookies.contZPoint;
        var contObject = $scope.contObject || $cookies.contObject;
        var timeDetailType = $scope.timeDetailType || $cookies.timeDetailType;
        var url = "../api/subscr/" + contObject + "/service/" + timeDetailType + "/" + contZPoint + "/csv" + exForUrl + "?beginDate=" + $rootScope.reportStart + "&endDate=" + $rootScope.reportEnd;
        window.open(url);
    };
        //Upload file with the indicator data to the server
    $scope.uploadFile =function(){
        $scope.uploader.queue[$scope.uploader.queue.length-1].upload();
    };
        
    var errorCallback = function (e) {
        console.log(e);
        var errorCode = "-1";
        if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
            errorCode = e.resultCode || e.data.resultCode;
        };
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
//        notificationFactory.errorInfo(e.statusText,e.data.description);       
    };
    
        //delete indicator data for period
    $scope.deleteData = function(){
        var contObject = $scope.contObject || $cookies.contObject;
        var contZpoint = $scope.contZPoint || $cookies.contZpoint;      
        var timeDetailType = "24h";
        var fromDate = $rootScope.startDateToDel;
        var toDate = $rootScope.endDateToDel;
        var url = "../api/subscr/contObjects/" + contObject + "/contZPoints/" + contZpoint + "/service/" + timeDetailType + "/csv" + "?beginDate=" + fromDate + "&endDate=" + toDate;
        $http.delete(url)
            .success(function(data){       
                notificationFactory.success();
                $scope.linkToFileWithDeleteData = "../api/subscr/service/out/csv/" + data.filename;
                $scope.fileWithDeleteData = data.filename;
                $scope.showLinkToFileFlag = true;
//console.log("getData on success deleteData");            
                $scope.getData(1);
            })
            .error(errorCallback);
    };
        
    $scope.setOrderBy = function(field){
        var asc = $scope.ctrlSettings.orderBy.field === field ? !$scope.ctrlSettings.orderBy.asc : true;
        var ord = (asc==true) ? "asc" : "desc";
        $cookies.indicatorsortorder = ord;
        $scope.ctrlSettings.orderBy = { field: field, asc: asc, order: $cookies.indicatorsortorder };
//console.log("getData on setOrderBy");        
        $scope.getData(1);
    };
    
    //check indicators for data (проверка: есть данные для отображения или нет)
    $scope.isHaveData = function(){
        if (angular.isUndefined($scope.data) || ($scope.data.length == 0)){
            return false;
        };
        return true;
    };
        
    $scope.isTDTabs = function(tdt){
console.log(tdt == "1h_abs");        
        return (tdt == "1h_abs");
    };
        
        //control visibles
    var setVisibles = function(ctxId){
        var ctxFlag = false;
        var tmp = mainSvc.getContextIds();
        tmp.forEach(function(element){
            if(element.permissionTagId.localeCompare(ctxId) == 0){
                ctxFlag = true;
            };
            var elDOM = document.getElementById(element.permissionTagId);//.style.display = "block";
//console.log(elDOM);            
            if (angular.isUndefined(elDOM) || (elDOM == null)){
                return;
            };              
            $('#'+element.permissionTagId).removeClass('nmc-hide');
        });
//        if (ctxFlag == false){
//            window.location.assign('#/');
//        };
    };
    setVisibles($scope.ctrlSettings.ctxId);
    //listen change of service list
    $rootScope.$on('servicePermissions:loaded', function(){
        setVisibles($scope.ctrlSettings.ctxId);
    });
        
    window.setTimeout(function(){
//console.log("Timeout. setVisibles");            
        setVisibles($scope.ctrlSettings.ctxId);
    }, 500);    
                
}]);