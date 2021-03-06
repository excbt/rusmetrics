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
        controller: ['$scope', '$rootScope', '$element', '$attrs', '$routeParams', '$resource', '$cookies', 'crudGridDataFactory', 'notificationFactory', '$http',
            function ($scope, $rootScope, $element, $attrs, $routeParams, $resource, $cookies, crudGridDataFactory, notificationFactory, $http) {
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
                // Эталонный интервал
                $scope.refRange = {};
                $scope.urlRefRange = '../api/subscr/contObjects/';
                // Промежуточные переменные для начала и конца интервала
                $scope.beginDate;
                $scope.endDate;
                
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
                //Функция для получения эталонного интервала для конкретной точки учета конкретного объекта
                function getRefRangeByObjectAndZpoint(object, zpoint){
                    var url = $scope.urlRefRange + object.id + '/zpoints/' + zpoint.id + '/referencePeriod'; 
//console.log(url);                    
                    $http.get(url)
                    .success(function(data){
                        if(data[0] != null){
                            var beginDate = new Date(data[0].periodBeginDate);
                            var endDate =  new Date(data[0].periodEndDate);
//console.log(data[0]);                                    
                            zpoint.zpointRefRange = "c "+beginDate.toLocaleDateString()+" по "+endDate.toLocaleDateString();
                            zpoint.zpointRefRangeAuto = data[0]._auto?"auto":"manual";
                        }
                        else {
                            zpoint.zpointRefRange = "Не задан";
                            zpoint.zpointRefRangeAuto = "notSet";
                        }
                    })
                    .error(function(e){
                        console.log(e);
                    });
                }
                
                $scope.zPointsByObject = [];
                $scope.getZpointsDataByObject = function(obj, mode){ 
                    obj.zpoints = [];
//                    var zpoint = {};
//                    for(var i=0; i<2;i++){
//                            zpoint = {};
//                            zpoint.id = 123;
//                            zpoint.zpointType = "Type";
//                            zpoint.zpointName = "Zpoint name";
//                            zpoint.zpointRSO = "RSO"
//                            zpoint.checkoutTime = "checkoutTime";
//                            zpoint.checkoutDay = "checkoutTime";
//                            zpoint.piped = true;
//                            zpoint.doublePipe = true;
//                            zpoint.zpointModel = "Model";
//                            zpoint.zpointNumber ="D503";
//                            zpoint.zpointLastDataDate  = new Date(); 
//                            obj.zpoints.push(zpoint);
//                    };
//                    
//                    return;
                    
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
                            if ((typeof $scope.zPointsByObject[i].deviceObject != 'undefined') ){                                
                                if (typeof $scope.zPointsByObject[i].deviceObject.deviceModel != 'undefined' ){
                                    zpoint.zpointModel = $scope.zPointsByObject[i].deviceObject.deviceModel.modelName;
                                }else{
                                    zpoint.zpointModel = "Не задано";
                                };
                                zpoint.zpointNumber = $scope.zPointsByObject[i].deviceObject.number;
                            };
                            zpoint.zpointLastDataDate  = $scope.zPointsByObject[i].lastDataDate;   
                            // Получаем эталонный интервал для точки учета
                            getRefRangeByObjectAndZpoint(obj, zpoint);
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
                    $cookies.timeDetailType="24h";
                    $rootScope.reportStart = moment().subtract(7, 'days').startOf('day').format('YYYY-MM-DD');
                    $rootScope.reportEnd = moment().endOf('day').format('YYYY-MM-DD');
                                      
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
                        // Готовим редактор эталонного периода
                        $scope.prepareRefRange();
                    });
                };
                
                // Подготовка редактора эталонного интервала
                $scope.prepareRefRange = function () {
               		getRefRange($scope.currentObject.id, $scope.zpointSettings.id);
               		$scope.editRefRangeOff();
                };
                
                // Активация формы редактирования эталонного интервала
                $scope.editRefRangeOn = function () {
                    $scope.toggleFlag = true; //переключаем видимость кнопок Изменить/сохранить
					document.getElementById('i_ref_range_save').style.display = 'block';
					document.getElementById('i_ref_range_add').style.display = 'none';
					document.getElementById('inp_ref_range_start').disabled = false;
					document.getElementById('inp_ref_range_end').disabled = false;
                }
                
                // Деактивация формы редактирования эталонного интервала
                $scope.editRefRangeOff = function () {
                    $scope.toggleFlag = false; //переключаем видимость кнопок Изменить/сохранить
					document.getElementById('i_ref_range_save').style.display = 'none';
					document.getElementById('i_ref_range_add').style.display = 'block';
					document.getElementById('inp_ref_range_start').disabled = true;
					document.getElementById('inp_ref_range_end').disabled = true;
                }
                
                // Запрос с сервера данных об эталонном интервале
                var getRefRange = function (objectId, zpointId) {
                	var url = $scope.urlRefRange + '/' + objectId + '/zpoints/' + zpointId + '/referencePeriod'; 
                	$http.get(url)
					.success(function(data){
						// Проверяем, задан ли интервал
						if(data[0] != null){
							$scope.refRange = data[0];
							$scope.refRange.cont_zpoint_id = zpointId;
							$scope.beginDate = new Date($scope.refRange.periodBeginDate);
							$scope.endDate =  new Date($scope.refRange.periodEndDate);
//							console.log($scope.beginDate, document.getElementById('inp_ref_range_start').value);
							// Проверяем, был ли интервал расчитан автоматически
							if($scope.refRange._auto == false) {
								document.getElementById('spn_if_manual').style.display = 'block';
								document.getElementById('spn_if_auto').style.display = 'none';
							}
							else {
								document.getElementById('spn_if_manual').style.display = 'none';
								document.getElementById('spn_if_auto').style.display = 'block';								
							}
						}
						else {
							$scope.refRange = {};
							$scope.refRange.cont_zpoint_id = zpointId;
							$scope.beginDate = '';
							$scope.endDate = '';
							document.getElementById('spn_if_manual').style.display = 'none';
							document.getElementById('spn_if_auto').style.display = 'none';
						}
					})
					.error(function(e){
						notificationFactory.errorInfo(e.statusText,e.description);
					});
                };
                
                // Отправка на сервер данных об эталонном интервале
                $scope.addRefRange = function (){
                	var url = $scope.urlRefRange + '/' + $scope.currentObject.id + '/zpoints/' + $scope.zpointSettings.id + '/referencePeriod';
                	$scope.refRange.id = '';
                	$scope.refRange.periodBeginDate = $scope.beginDate.getTime();
                	$scope.refRange.periodEndDate = $scope.endDate.getTime();
                	$http.post(url, $scope.refRange)
					.success(function(data){
						$scope.editRefRangeOff();
						$scope.refRange = data;
					})
					.error(function(e){
						notificationFactory.errorInfo(e.statusText,e.description);
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
                    $('#showZpointExplParameters').modal('hide');
                     $scope.zpointSettings={};
                };
                
                $scope.updateZpointSettings = function(){                   
                    var tableSummer = $scope.crudTableName+"/"+$scope.currentObject.id+"/zpoints/"+$scope.zpointSettings.id+"/settingMode";
                    crudGridDataFactory(tableSummer).update({ id: $scope.zpointSettings.summer.id }, $scope.zpointSettings.summer, successZpointSummerCallback, errorCallback);
                };
                
                
                // Проверка пользователя - системный/ не системный
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
                    return mainSvc.checkHHmm(hhmmValue);
//console.log(hhmmValue);                    
//                    if (/(0[0-9]|1[0-9]|2[0-3]){1,2}:([0-5][0-9]){1}/.test(hhmmValue)){
//                        return true;
//                    };
//                    return false;
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