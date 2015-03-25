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
                $scope.objects = angular.fromJson($attrs.datasource); 
                
                $scope.lookups = [];
                $scope.object = {};
                $scope.columns = angular.fromJson($attrs.columns);
                $scope.captions = angular.fromJson($attrs.captions);
                $scope.extraProps = angular.fromJson($attrs.exprops);
                $scope.addMode = false;
                $scope.orderBy = { field: $scope.extraProps["defaultOrderBy"], asc: true };
                $scope.loading = true;
                $scope.filter = '';
                $scope.filterType='';
                $scope.bReports = angular.fromJson($attrs.breports) || false; //Признак того, что объекты выводятся в окне "Отчеты"
                $scope.bGroupByObject = angular.fromJson($attrs.bgroup) || false;
                $scope.bExtraMenu = angular.fromJson($attrs.bextramenu) || false; //признак дополнительного меню
                $scope.bObject = angular.fromJson($attrs.bobject) || false; //Признак, что страница отображает объекты
                $scope.bList = angular.fromJson($attrs.blist); //|| true; //Признак того, что объекты выводятся только для просмотра

console.log("Attrs = "+$attrs);                
console.log("bGroup = "+$scope.bGroupByObject);
console.log("bExtraMenu = "+$scope.bExtraMenu);                
console.log("bList = "+$scope.bList);                
                
                ///$scope.crudTableName = $scope.$eval($attrs.table);     
                //console.log($scope.crudTableName);
//                $scope.crudTableName = typeof $scope.extraProps["routeParamName"] == 'undefined' ? 
//                						$attrs.table : 
//                							$attrs.table + $routeParams[$scope.extraProps["routeParamName"]];
                
                

                var $docScope = angular.element(document).scope();

                $scope.setLookupData = function () {
           
                    for (var i = 0; i < $scope.columns.length; i++) {
                        var c = $scope.columns[i];
                        if (c.lookup && !$scope.hasLookupData(c.lookup.table)) {
                        	var lookup_table = c.lookup.table;
            				$scope.initLookupData(lookup_table);
                        }
                    }    
                };
                
                $scope.resetLookupData = function(table) {
                    $scope.setIndividualLookupData(table, {});
                    $scope.setLookupData();
                };

                $scope.getLookupData = function (table) {
                    return typeof table == 'undefined' ? null : $scope.lookups[table.toLowerCase()];
                };

                $scope.setIndividualLookupData = function (table, data) {

                    $scope.lookups[table.toLowerCase()] = data;
                };

                $scope.hasLookupData = function (table) {                    
                    return !$.isEmptyObject($scope.getLookupData(table));
                };

                $scope.getLookupValue = function (lookup, key) {
                    var data = $scope.getLookupData(lookup.table);

                    if (typeof data != 'undefined') {
                        for (var i = 0; i < data.length; i++) {
                            if (data[i][lookup.key] === key)
                                return data[i][lookup.value];
                        }
                    }

                    return '';
                };

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
                    $scope.currentObject={};
                    $docScope.$broadcast('lookupDataChange', [$attrs.table]);
                    $scope.getData(cb);
                    
                };

                var successPostCallback = function (e) {
                    successCallback(e, function () {
                        $scope.toggleAddMode();
                    });
                };

                $scope.$on('lookupDataChange', function (scope, table) {
                    $scope.resetLookupData(table[0]);
                });

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
                        $scope.objects = data;
                    
                        if (cb) cb();
                    });
                };

                $scope.setOrderBy = function (field) {
                    var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
                    $scope.orderBy = { field: field, asc: asc };
                };

                $scope.getData(
                    function () {
                        $scope.setLookupData();
                        $scope.loading = false;
                    });
                
                $scope.initLookupData = function(table){
                	crudGridDataFactory(table).query(function (data) {
                		$scope.setIndividualLookupData(table, data);
                	});
                };
                
                $scope.selectedItem = function (item) {
			        var curObject = angular.copy(item);
			        $scope.currentObject = curObject;
			    };
                
                
                //Работа с уведомлениями
                    $scope.bGroupByObject = false;
                    $scope.oldObjects = $scope.objects;
                    $scope.oldColumns = $scope.columns;
                    $scope.oldCaptions = $scope.captions;
                    $scope.oldExtraProps = $scope.extraProps;
                
                $scope.cancelGroup = function(){
                    $scope.objects = $scope.oldObjects;
                    $scope.columns = $scope.oldColumns;
                    $scope.captions = $scope.oldCaptions;
                    $scope.extraProps = $scope.oldExtraProps;
                    $scope.bGroupByObject = false;
                };
                
                //
                $scope.groupByObject = function(){
                    //1.сделать копию объектов
                    //2.Найти уникальные объекты
                    //3.Посчитать у каждого объекта количество уведомлений всех и каждого из видов в отдельности
                    //4.Перестроить структуру и вывести объекты
  //                 alert("groupByObject");

                     $scope.bGroupByObject = true;
                    //1.
                    $scope.oldObjects = $scope.objects;
                    $scope.oldColumns = $scope.columns;
                    $scope.oldCaptions = $scope.captions;
                    $scope.oldExtraProps = $scope.extraProps;

                    //2.
                    $scope.objectGroups = [];
                    
                    $scope.groupsCount =0;
                  
                    for (var i=0;i<$scope.objects.length;i++){
                   
                        $scope.objectGroup = {"name":"", "commonCount":0, "critCount":0, "warnCount":0, "infoCount":0};

                        if ( $scope.objectGroups.length == 0){
         
   
                            $scope.objectGroup.name = $scope.objects[i].noticeObject;
                            $scope.objectGroup.commonCount = 1;
                            switch($scope.objects[i].noticeCat){
                                    case "Критическая": $scope.objectGroup.critCount=1; break;
                                    case "Предупреждение": $scope.objectGroup.warnCount=1; break;
                                    case "Информация": $scope.objectGroup.infoCount=1; break;
                                    
                            }
                            $scope.objectGroups[$scope.groupsCount] = $scope.objectGroup;
                            $scope.groupsCount=$scope.groupsCount+1;
                        }else{
     
                            $scope.bNewGroup = false;
                            for(var j=0; j<$scope.objectGroups.length; j++){

                                if ($scope.objectGroups[j].name==$scope.objects[i].noticeObject){
                                    $scope.objectGroups[j].commonCount=$scope.objectGroups[j].commonCount+1; //3.
                                    switch($scope.objects[i].noticeCat){
                                            case "Критическая": $scope.objectGroups[j].critCount=$scope.objectGroups[j].critCount+1; break;
                                            case "Предупреждение": $scope.objectGroups[j].warnCount=$scope.objectGroups[j].warnCount+1; break;
                                            case "Информация": $scope.objectGroups[j].infoCount=$scope.objectGroups[j].infoCount+1; break;
                                    
                                    }
                                    $scope.bNewGroup = false;
                                    break;
                                 
                                }else{
                                    $scope.bNewGroup = true;
                                }
                            }
                            
                            if($scope.bNewGroup){
                                $scope.objectGroup.name = $scope.objects[i].noticeObject;
                                    $scope.objectGroup.commonCount = 1;
                                     switch($scope.objects[i].noticeCat){
                                            case "Критическая": $scope.objectGroup.critCount=1; break;
                                            case "Предупреждение": $scope.objectGroup.warnCount=1; break;
                                            case "Информация": $scope.objectGroup.infoCount=1; break;

                                    }
                                    $scope.objectGroups[$scope.groupsCount] = $scope.objectGroup;
                                    $scope.groupsCount=$scope.groupsCount+1;
                            }

                            
                        }
                    }
                console.log($scope.groupsCount);
                    
                    
                    //4.
                    $scope.columns = [ 
                                      {"name":"name", "header" : "Объект", "class":"col-md-3"}
                                    ,{"name":"commonCount", "header" : "Всего уведомлений", "class":"col-md-2"}
                                    ,{"name":"critCount", "header" : "Критические", "class":"col-md-1"}
                                    ,{"name":"warnCount", "header" : "Предупреждений", "class":"col-md-1"}
                                    ,{"name":"infoCount", "header" : "Информативных", "class":"col-md-1"}
                    ];
                    $scope.objects = $scope.objectGroups;
                    
                }; //End groupByObject
                
                
                $scope.toogleShowGroupDetails = function(curObject){//switch option: current goup details
                   for(var i=0;i<$scope.objects.length;i++){
                       if ($scope.objects[i]!=curObject && $scope.objects[i].showGroupDetails==true){
                           $scope.objects[i].showGroupDetails=false;
                       }
                   }
                    curObject.showGroupDetails = !curObject.showGroupDetails;
                    
                  //  $scope.selectedItem(curObject);
                };
                
                $scope.zPointsByObject = [];
                $scope.getZpointsDataByObject = function(table){
                    
console.log(table);                 
                    crudGridDataFactory(table).query(function (data) {
                        $scope.zPointsByObject = data;
console.log("Data:");                
console.log($scope.zPointsByObject); 
                        
                        
                        for(var i=0;i<$scope.zPointsByObject.length;i++){
                            var zpoint = {};
                            zpoint.zpointType = $scope.zPointsByObject[i].contServiceType.keyname;
                            zpoint.zpointName = $scope.zPointsByObject[i].customServiceName;
                            zpoint.zpointModel = $scope.zPointsByObject[i].deviceObjects[0].deviceModel.modelName;
                            zpoint.zpointNumber = $scope.zPointsByObject[i].deviceObjects[0].number;
                            zpoint.zpointLastDataDate  = "27.02.2015";   
                            
                            $scope.oldObjects[i] = zpoint;
                        
console.log("Device: "+$scope.oldObjects[i].zpointType+"; "+$scope.oldObjects[i].zpointName+"; "+$scope.oldObjects[i].zpointModel+"; "+$scope.oldObjects[i].zpointNumber+";");                        
                        }
                        
                        
                    });
                };
                //for page "Objects"
                $scope.zpoints = angular.fromJson($attrs.zpointdata);
               
                $scope.showObjectDetails = function(obj){
                    $scope.oldObjects = [];
                    var zps = angular.fromJson($attrs.zpointdata);
                    var mas = [];
//                    var masCount = 0;
//                    for (var k=0;k<$scope.zpoints.length;k++){
//                        if($scope.zpoints[k].zpointParent == obj.id){
//                            mas[masCount]=$scope.zpoints[k];
//                            masCount = masCount+1;
//                        }
//                    }
                 //   $scope.oldObjects = mas;
                    $scope.oldColumns = angular.fromJson($attrs.zpointcolumns);
   console.log($scope.crudTableName+"/"+obj.id+"/zpoints");                 
                    $scope.getZpointsDataByObject($scope.crudTableName+"/"+obj.id+"/zpoints");
                    
                    
                  
                };
                
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
                
                
                //Directories
//                $scope.paramsTableName = "table";
//                $scope.addParamMode = false;
//                $scope.bdirectories = angular.fromJson($attrs.bdirectories) || false; //flag for page "Directories". If this is set, that visible page is page "Directories"
//                
//                $scope.getCurDirParams = function(){
//                    
//                    $scope.oldObjects = angular.fromJson($scope.currentObject.directParams);
//                    $scope.oldColumns = [ 
//                                      {"name":"paramName", "header" : "Наименование", "class":"col-md-3"}
//                                    ,{"name":"paramType"
//                                      ,"lookup":
//                                        {
//                                            "table": "rest/types",
//                                            "key": "typeId",
//                                            "value":"typeKeyname",
//                                            "orderBy": {"field": "typeKeyname", "asc": "true"}
//                                        }                                      
//                                      ,"header" : "Тип", "class":"col-md-2"}
//                                    ,{"name":"paramDescription", "header" : "Описание", "class":"col-md-4"}
//                                    
//                    ];
//                };
                
//                $scope.toggleAddParamMode = function(){
//                    $scope.addParamMode = !$scope.addParamMode;
//                };
//                
//                $scope.setCurObjToDel = function(obj, idColumnName, deleteConfirmationProp){
//                    $scope.extraProp = {};
//                    $scope.currentObjectToDel = obj;
//                    $scope.extraProp.idColumnName = idColumnName;
//                    $scope.extraProp.deleteConfirmationProp = deleteConfirmationProp;
//                };
//                
//                $scope.treedata =
//                    [
//                        { "label" : "Школы", "id" : "role1", "children" : [
//                            { "label" : "Школа1", "id" : "role11", "children" : [] },
//                            { "label" : "Школа2", "id" : "role12", "children" : [
//                                { "label" : "Корпус2-1", "id" : "role121", "children" : [
//                                    { "label" : "Этаж2-1-1", "id" : "role1211", "children" : [] },
//                                    { "label" : "Этаж2-1-2", "id" : "role1212", "children" : [] }
//                                ]}
//                            ]}
//                        ]},
//                        { "label" : "Заводы", "id" : "role2", "children" : [] },
//                        { "label" : "Администрация", "id" : "role3", "children" : [] }
//                    ];
//                
                $scope.showDetails = function(obj){
                    if($scope.bdirectories){
                        $scope.currentObject = obj;
                        $('#showDirectoryStructModal').modal();
                    }
                };
//                
//                $scope.test = function(){
//                    $('#editDirValueModal').modal();
//                };
//                
//                $scope.selectedObjectsCount=0;
//                $scope.toggleSelectObject = function(object){
//                     
//                   object.selected = !object.selected;  
//                   
//                    if(object.selected){
//                        $scope.selectedObjectsCount++;
//
//                    }else{
//                        if (object.selected == false){
//                            $scope.selectedObjectsCount--;
//                            
//                        }
//                    }                        
//                };
                
                 //run report
            
                 $scope.getReport = function(object, repType) {                           
                            $scope.loading = true;             
                            var repPath = "../api/report/commercial/"+object.id+"/"+repType+"/?beginDate="+$scope.reportStart+"&endDate="+$scope.reportEnd;
                             window.open(repPath);

                };
                
                $scope.getReports = function(){                         
                    for (var i=0;i<$scope.objects.length;i++){
                            var obj = $scope.objects[i];                       
                        if (obj.selected==true){                            
                            $scope.getReport(obj,"pdf");
                        }
                    }
                };
                
                
                
                // Показания точек учета
    
                $scope.getIndicators = function(object){
console.log("$rootScope.showIndicatorsParam = "+$rootScope.showIndicatorsParam);                    
                    $rootScope.showIndicatorsParam = true;
                    
                    //Send:
                    //  object_id
                    //  zpoint_id
                    //  1h
                    //  beginDate = lastDate.nachalo
                    //  endDate = lastDate.konec
                    
                    $rootScope.contObject =  $scope.currentObject;
                    $rootScope.contZPoint = object;
                                        
                    window.location.replace("#/objects/indicators/");

                };
               
            }]
    };
});