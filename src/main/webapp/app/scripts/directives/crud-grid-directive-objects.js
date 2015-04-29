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
        controller: ['$scope', '$rootScope', '$element', '$attrs', '$routeParams', '$resource', 'crudGridDataFactory', 'notificationFactory',
            function ($scope, $rootScope, $element, $attrs, $routeParams, $resource, crudGridDataFactory, notificationFactory) {
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
                        for (var i=0; i<tmp.length; i++){                                                    
                            $scope.getZpointsDataByObject(tmp[i], "Ex");  
                            
                        }
                        $scope.objects = tmp;
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
                
                
                //Работа с уведомлениями
//                    $scope.bGroupByObject = false;
//                    $scope.oldObjects = $scope.objects;
//                    $scope.oldColumns = $scope.columns;
//                    $scope.oldCaptions = $scope.captions;
//                    $scope.oldExtraProps = $scope.extraProps;
//                
//                $scope.cancelGroup = function(){
//                    $scope.objects = $scope.oldObjects;
//                    $scope.columns = $scope.oldColumns;
//                    $scope.captions = $scope.oldCaptions;
//                    $scope.extraProps = $scope.oldExtraProps;
//                    $scope.bGroupByObject = false;
//                };
                
                //
//                $scope.groupByObject = function(){
                    //1.сделать копию объектов
                    //2.Найти уникальные объекты
                    //3.Посчитать у каждого объекта количество уведомлений всех и каждого из видов в отдельности
                    //4.Перестроить структуру и вывести объекты
  //                 alert("groupByObject");

//                     $scope.bGroupByObject = true;
                    //1.
//                    $scope.oldObjects = $scope.objects;
//                    $scope.oldColumns = $scope.columns;
//                    $scope.oldCaptions = $scope.captions;
//                    $scope.oldExtraProps = $scope.extraProps;

                    //2.
//                    $scope.objectGroups = [];
//                    $scope.groupsCount =0;   
//                    for (var i=0;i<$scope.objects.length;i++){      
//                        $scope.objectGroup = {"name":"", "commonCount":0, "critCount":0, "warnCount":0, "infoCount":0};
//                        if ( $scope.objectGroups.length == 0){
//                            $scope.objectGroup.name = $scope.objects[i].noticeObject;
//                            $scope.objectGroup.commonCount = 1;
//                            switch($scope.objects[i].noticeCat){
//                                    case "Критическая": $scope.objectGroup.critCount=1; break;
//                                    case "Предупреждение": $scope.objectGroup.warnCount=1; break;
//                                    case "Информация": $scope.objectGroup.infoCount=1; break;
//                                    
//                            }
//                            $scope.objectGroups[$scope.groupsCount] = $scope.objectGroup;
//                            $scope.groupsCount=$scope.groupsCount+1;
//                        }else{
//     
//                            $scope.bNewGroup = false;
//                            for(var j=0; j<$scope.objectGroups.length; j++){
//
//                                if ($scope.objectGroups[j].name==$scope.objects[i].noticeObject){
//                                    $scope.objectGroups[j].commonCount=$scope.objectGroups[j].commonCount+1; //3.
//                                    switch($scope.objects[i].noticeCat){
//                                            case "Критическая": $scope.objectGroups[j].critCount=$scope.objectGroups[j].critCount+1; break;
//                                            case "Предупреждение": $scope.objectGroups[j].warnCount=$scope.objectGroups[j].warnCount+1; break;
//                                            case "Информация": $scope.objectGroups[j].infoCount=$scope.objectGroups[j].infoCount+1; break;
//                                    
//                                    }
//                                    $scope.bNewGroup = false;
//                                    break;
//                                 
//                                }else{
//                                    $scope.bNewGroup = true;
//                                }
//                            }
//                            
//                            if($scope.bNewGroup){
//                                $scope.objectGroup.name = $scope.objects[i].noticeObject;
//                                    $scope.objectGroup.commonCount = 1;
//                                     switch($scope.objects[i].noticeCat){
//                                            case "Критическая": $scope.objectGroup.critCount=1; break;
//                                            case "Предупреждение": $scope.objectGroup.warnCount=1; break;
//                                            case "Информация": $scope.objectGroup.infoCount=1; break;
//
//                                    }
//                                    $scope.objectGroups[$scope.groupsCount] = $scope.objectGroup;
//                                    $scope.groupsCount=$scope.groupsCount+1;
//                            }
//
//                            
//                        }
//                    }  
                    //4.
//                    $scope.columns = [ 
//                                      {"name":"name", "header" : "Объект", "class":"col-md-3"}
//                                    ,{"name":"commonCount", "header" : "Всего уведомлений", "class":"col-md-2"}
//                                    ,{"name":"critCount", "header" : "Критические", "class":"col-md-1"}
//                                    ,{"name":"warnCount", "header" : "Предупреждений", "class":"col-md-1"}
//                                    ,{"name":"infoCount", "header" : "Информативных", "class":"col-md-1"}
//                    ];
//                    $scope.objects = $scope.objectGroups;
                    
//                }; 
                     //End groupByObject

                $scope.toogleShowGroupDetails = function(curObject){//switch option: current goup details
//                   for(var i=0;i<$scope.objects.length;i++){
//                       if ($scope.objects[i]!=curObject && $scope.objects[i].showGroupDetails==true){
//                           $scope.objects[i].showGroupDetails=false;
//                       }
//                   }
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
                            zpoint.zpointRSO = $scope.zPointsByObject[i].rso.organizationFullName;
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
//                $scope.showObjectDetails = function(obj){
//                    $scope.oldObjects = [];
//                    var zps = angular.fromJson($attrs.zpointdata);
//                    var mas = [];
//                    $scope.oldColumns = angular.fromJson($attrs.zpointcolumns);               
//                    $scope.getZpointsDataByObject(obj, "Ex");  
//                };
                
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
                    $rootScope.showIndicatorsParam = true;
                    $rootScope.contObject =  $scope.currentObject;
                    $rootScope.contZPoint = object;
                    $rootScope.timeDetailType = "1h";                   
                    window.location.replace("#/objects/indicators/");
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