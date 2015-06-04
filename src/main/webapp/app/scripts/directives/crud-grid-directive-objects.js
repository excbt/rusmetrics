'use strict';

angular.module('portalNMC').directive('crudGridObjects', function () {
    return {
        restrict: 'A',
        replace: false,
        scope: {
        	crudTableName : '=table',
        	newIdValue : '=',
        	newIdProperty : '=',
            reportStart: '=',
            reportEnd: '='
        },
        templateUrl: 'scripts/directives/templates/crud-grid-directive-objects-template.html',
        link : function (scope, element, attrs) {
        	//scope.crudTableName = scope.$eval($attrs.table);  
        	//console.log(scope.crudTableName);
        },
        controller: ['$scope', '$rootScope', '$element', '$attrs', '$routeParams', '$resource', '$cookies', 'crudGridDataFactory', 'notificationFactory',
            function ($scope, $rootScope, $element, $attrs, $routeParams, $resource, $cookies, crudGridDataFactory, notificationFactory) {
                $scope.object = {};
                $scope.columns = angular.fromJson($attrs.columns);
                $scope.captions = angular.fromJson($attrs.captions);
                $scope.extraProps = angular.fromJson($attrs.exprops);
                $scope.addMode = false;
                $scope.orderBy = { field: $scope.extraProps["defaultOrderBy"], asc: true };
                $scope.loading = true;
                $scope.filter = '';
                $scope.filterType='';
                //Признак того, что объекты выводятся в окне "Отчеты"
                $scope.bGroupByObject = angular.fromJson($attrs.bgroup) || false;
                $scope.bObject = angular.fromJson($attrs.bobject) || false; //Признак, что страница отображает объекты
                $scope.bList = angular.fromJson($attrs.blist); //|| true; //Признак того, что объекты выводятся только для просмотра        
                //zpoint column names
                $scope.oldColumns = angular.fromJson($attrs.zpointcolumns);               
                
                //Режимы функционирования (лето/зима)
                $scope.cont_zpoint_setting_mode_check = [
                    {"keyname":"summer", "caption":"Летний режим"}
                    ,{"keyname":"winter", "caption":"Зимний режим"}
                ];
                

                $scope.toggleAddMode = function () {
                    $scope.addMode = !$scope.addMode;
                    $scope.object = {};
                    if ($scope.addMode) {
                    	if ($scope.newIdProperty && $scope.newIdValue)
                    		$scope.object[$scope.newIdProperty] =  $scope.newIdValue;
                    }
                };

                $scope.toggleEditMode = function (object) {
                    object.editMode = !object.editMode;
                };

                var successCallback = function (e, cb) {
                    notificationFactory.success();
                    $('#deleteObjectModal').modal('hide');
                    $('#showObjOptionModal').modal('hide');
                    $scope.currentObject={};
                    $scope.getData(cb);
                    
                };

                var successPostCallback = function (e) {
                    successCallback(e, function () {
                        $scope.toggleAddMode();
                    });
                };

                var errorCallback = function (e) {
                    notificationFactory.errorInfo(e.statusText,e.data.description);       
                };

                $scope.addObject = function () {
                    crudGridDataFactory($scope.crudTableName).save($scope.object, successPostCallback, errorCallback);
                };

                $scope.deleteObject = function (object) {
                    crudGridDataFactory($scope.crudTableName).delete({ id: object[$scope.extraProps.idColumnName] }, successCallback, errorCallback);
                };
                
//                function delete for directory
                $scope.deleteObject = function (tableName, objId) {
                    crudGridDataFactory(tableName).delete({ id: objId }, successCallback, errorCallback);
                };
                                
                $scope.updateObject = function (object) {
                    crudGridDataFactory($scope.crudTableName).update({ id: object[$scope.extraProps.idColumnName] }, object, successCallback, errorCallback);
                };

                $scope.getData = function (cb) {
                    crudGridDataFactory($scope.crudTableName).query(function (data) {
                        var tmp = data;    
                        var curObjId = $cookies.contObject;
//console.log(curObjId);                        
                        for (var i=0; i<tmp.length; i++){                                                    
                            $scope.getZpointsDataByObject(tmp[i], "Ex");  
//console.log(tmp[i].id);                               
                            if (tmp[i].id == curObjId){tmp[i].showGroupDetails=true};
                        }
                        $scope.objects = tmp;
                        $cookies.contObject = null;
                        if (cb) cb();
                    });
                };

                $scope.setOrderBy = function (field) {
                    var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
                    $scope.orderBy = { field: field, asc: asc };
                };

                $scope.getData(
                    function () {
                        $scope.loading = false;
                    });
              
                $scope.selectedItem = function (item) {
			        var curObject = angular.copy(item);
			        $scope.currentObject = curObject;
			    };
                
                $scope.toggleShowGroupDetails = function(curObject){//switch option: current goup details
                    curObject.showGroupDetails = !curObject.showGroupDetails;
                };
                
                $scope.zPointsByObject = [];
                $scope.getZpointsDataByObject = function(obj, mode){ 
                    obj.zpoints = [];
                    var table = $scope.crudTableName+"/"+obj.id+"/contZPoints"+mode;//Ex";
                    crudGridDataFactory(table).query(function (data) {
                        var tmp = [];
                        if (mode == "Ex"){
                            tmp = data.map(function(el){
                                var result = {};
                                result = el.object;
                                result.lastDataDate = el.lastDataDate;
                                return result;
                            });
                        }else{
                            tmp = data;
                        };
                        $scope.zPointsByObject = tmp;
                        var zpoints = [];
                        for(var i=0;i<$scope.zPointsByObject.length;i++){
                            var zpoint = {};
                            zpoint.id = $scope.zPointsByObject[i].id;
                            zpoint.zpointType = $scope.zPointsByObject[i].contServiceType.keyname;
                            zpoint.zpointName = $scope.zPointsByObject[i].customServiceName;
                            if ((typeof $scope.zPointsByObject[i].rso != 'undefined') && ($scope.zPointsByObject[i].rso!=null)){
                                zpoint.zpointRSO = $scope.zPointsByObject[i].rso.organizationFullName || $scope.zPointsByObject[i].rso.organizationName;
                            }else{
                                zpoint.zpointRSO = "Не задано"
                            };
                            zpoint.checkoutTime = $scope.zPointsByObject[i].checkoutTime;
                            zpoint.checkoutDay = $scope.zPointsByObject[i].checkoutDay;
                            if(typeof $scope.zPointsByObject[i].doublePipe == 'undefined'){
                                zpoint.piped = false;
                                
                            }else {
                                zpoint.piped = true;
                                zpoint.doublePipe = $scope.zPointsByObject[i].doublePipe;
                                zpoint.singlePipe = !zpoint.doublePipe;
                            };
                            if (typeof $scope.zPointsByObject[i].deviceObjects != 'undefined'){
                                zpoint.zpointModel = $scope.zPointsByObject[i].deviceObjects[0].deviceModel.modelName;
                                zpoint.zpointNumber = $scope.zPointsByObject[i].deviceObjects[0].number;
                            };
                            zpoint.zpointLastDataDate  = $scope.zPointsByObject[i].lastDataDate;   
                            zpoints[i] = zpoint;                  
                        }
                        obj.zpoints = zpoints;
                        
                    });
                };
                //for page "Objects"
                
                //Фильтр "Только непросмотренные"
                $scope.onlyNoRead = false;
                $scope.showRow = function(obj){
                    if ( (typeof obj.isRead =='undefined') && (!$scope.onlyNoRead)){
                        return true;
                    };                                     
                    if($scope.onlyNoRead){
                        if($scope.onlyNoRead == !obj.isRead){
                            return true;
                        }else{
                            return false;
                        }
                    };
                    return true;
                };
                               
                $scope.showDetails = function(obj){
                    if($scope.bdirectories){
                        $scope.currentObject = obj;
                        $('#showDirectoryStructModal').modal();
                    }
                };

                // Показания точек учета
                $scope.getIndicators = function(object){
                    $cookies.contZPoint = object.id;
                    $cookies.contObject=$scope.currentObject.id;
                    $cookies.contZPointName = object.zpointName;
                    $cookies.contObjectName=$scope.currentObject.fullName;
                    $cookies.timeDetailType="1h";
                                      
                    window.location.assign("#/objects/indicators/");
                };
                
                //Свойства точки учета
                $scope.zpointSettings = {};
                $scope.getZpointSettings = function(object){
                    var zps = {};
                    zps.id = object.id;
                    zps.zpointName = object.zpointName;
                    switch (object.zpointType){
                       case "heat" :  zps.zpointType="ТС"; break;
                       case "hw" : zps.zpointType="ГВС"; break;
                       case "cw" : zps.zpointType="ХВ"; break;    
                        default : zps.zpointType=object.zpointType;        
                    }
                    zps.zpointModel = object.zpointModel;
                    zps.zpointRSO = object.zpointRSO;
                    zps.checkoutTime = object.checkoutTime;
                    zps.checkoutDay = object.checkoutDay;
                    zps.winter = {};
                    zps.summer = {};
                    //http://localhost:8080/nmk-p/api/subscr/contObjects/18811505/zpoints/18811559/settingMode
                    var table = $scope.crudTableName+"/"+$scope.currentObject.id+"/zpoints/"+object.id+"/settingMode";
                    crudGridDataFactory(table).query(function (data) {
                        for(var i = 0; i<data.length;i++){
                                                    
                            if(data[i].settingMode == "winter"){
                                zps.winter = data[i];
                            }else if(data[i].settingMode == "summer"){
                                zps.summer=data[i];
                            }  
                        };                 
                     $scope.zpointSettings = zps;
                    });
                };
            
                var successZpointSummerCallback = function (e) {
                    notificationFactory.success();
                    var tableWinter = $scope.crudTableName+"/"+$scope.currentObject.id+"/zpoints/"+$scope.zpointSettings.id+"/settingMode";
                    crudGridDataFactory(tableWinter).update({ id: $scope.zpointSettings.winter.id }, $scope.zpointSettings.winter, successZpointWinterCallback, errorCallback);           
                    
                };
                
                 var successZpointWinterCallback = function (e) {
                    notificationFactory.success();    
                    $('#showZpointOptionModal').modal('hide');                    
                     $scope.zpointSettings={};
                                
                };
                
                $scope.updateZpointSettings = function(){                   
                    var tableSummer = $scope.crudTableName+"/"+$scope.currentObject.id+"/zpoints/"+$scope.zpointSettings.id+"/settingMode";
                    crudGridDataFactory(tableSummer).update({ id: $scope.zpointSettings.summer.id }, $scope.zpointSettings.summer, successZpointSummerCallback, errorCallback);
                };
                
                
                //
                $scope.isSystemuser = function(){
                    $scope.userInfo = $rootScope.userInfo;
                    return $scope.userInfo._system;
                };
                
                //checkers            
                $scope.checkEmptyNullValue = function(numvalue){                    
                    var result = false;
                    if ((numvalue === "") || (numvalue==null)){
                        result = true;
                        return result;
                    }
                    return result;
                };
                
                function isNumeric(n) {
                  return !isNaN(parseFloat(n)) && isFinite(n);
                }
                
                $scope.checkNumericValue = function(numvalue){ 
                    var result = true;
                    if ($scope.checkEmptyNullValue(numvalue)){
                        return result;
                    }
                    if (!isNumeric(numvalue)){
                        result = false;
                        return result;
                    };
                    return result;
                };
                
                $scope.checkPositiveNumberValue = function(numvalue){                    
                    var result = true;
                    result = $scope.checkNumericValue(numvalue)
                    if (!result){
                        //if numvalue is not number -> return false
                        return result;
                    }
                    result = parseInt(numvalue)>=0?true:false;
                    return result;
                };
                
                $scope.checkNumericInterval = function(leftBorder, rightBorder){  
                     if (($scope.checkEmptyNullValue(leftBorder))||( $scope.checkEmptyNullValue(rightBorder))){                                     
                         return false;
                     };
                     if (!(($scope.checkNumericValue(leftBorder))&&( $scope.checkNumericValue(rightBorder)))){                         
                         return false;
                     };
                     if(parseInt(rightBorder)>=parseInt(leftBorder)){                        
                         return true;
                     };
                     return false;
                };
                
                $scope.checkHHmm = function(hhmmValue){
//console.log(hhmmValue);                    
                    if (/(0[0-9]|1[0-9]|2[0-3]){1,2}:([0-5][0-9]){1}/.test(hhmmValue)){
                        return true;
                    };
                    return false;
                };
                
                $scope.checkZpointSettingsFrom = function(zpointSettings){              
                    if((zpointSettings == null)||(!zpointSettings.hasOwnProperty('summer'))||(!zpointSettings.hasOwnProperty('winter'))){
                        return true;
                    };
                    return $scope.checkPositiveNumberValue(zpointSettings.summer.ov_BalanceM_ctrl) && 
                        $scope.checkPositiveNumberValue(zpointSettings.winter.ov_BalanceM_ctrl)&&
                        $scope.checkHHmm(zpointSettings.summer.ov_Worktime)&&
                        $scope.checkHHmm(zpointSettings.winter.ov_Worktime)&&
                        $scope.checkPositiveNumberValue(zpointSettings.summer.leak_Gush)&&
                        $scope.checkPositiveNumberValue(zpointSettings.winter.leak_Gush)&&
                        $scope.checkPositiveNumberValue(zpointSettings.summer.leak_Night)&&
                        $scope.checkPositiveNumberValue(zpointSettings.winter.leak_Night)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_deltaT_min, zpointSettings.summer.wm_deltaT_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_deltaQ_min, zpointSettings.summer.wm_deltaQ_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_deltaT_min, zpointSettings.winter.wm_deltaT_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_deltaQ_min, zpointSettings.winter.wm_deltaQ_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_P2_min, zpointSettings.summer.wm_P2_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_P1_min, zpointSettings.summer.wm_P1_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_P2_min, zpointSettings.winter.wm_P2_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_P1_min, zpointSettings.winter.wm_P1_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_T2_min, zpointSettings.summer.wm_T2_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_T1_min, zpointSettings.summer.wm_T1_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_T2_min, zpointSettings.winter.wm_T2_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_T1_min, zpointSettings.winter.wm_T1_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_M2_min, zpointSettings.summer.wm_M2_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_M1_min, zpointSettings.summer.wm_M1_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_M2_min, zpointSettings.winter.wm_M2_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_M1_min, zpointSettings.winter.wm_M1_max);
                        
                };
                
                $scope.checkObjectPropertiesForm = function(object){
//                    if ((object==null)||(object.cwTemp === undefined)){
                    if ((object==null)||(!object.hasOwnProperty('cwTemp'))||(!object.hasOwnProperty('heatArea'))){
                        return true;
                    };
                    return $scope.checkNumericValue(object.cwTemp) && ($scope.checkNumericValue(object.heatArea));
                };
               
            }]
    };
});