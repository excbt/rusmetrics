'use strict';

angular.module('portalNMK').directive('crudGridObjects', function () {
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
                    notificationFactory.error(e.data.ExceptionMessage);
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
               
            }]
    };
});