'use strict';
var app = angular.module('portalNMC');
app.controller('TariffsCtrl', ['$scope', '$rootScope', '$resource', 'crudGridDataFactory', 'notificationFactory', 'objectSvc', 'mainSvc', function($scope, $rootScope, $resource, crudGridDataFactory, notificationFactory, objectSvc, mainSvc){
    $rootScope.ctxId = "tariffs_page";
    //set default values
    //ctrl settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.dateFormat = "DD.MM.YYYY"; //date format
    $scope.ctrlSettings.selectedAll = false;
    
    $scope.crudTableName = "../api/subscr/tariff"; 
    $scope.groupUrl = "../api/contGroup";
    $scope.columns = [
        {"name":"tariffTypeName", "header" : "Вид услуги", "class":"col-md-1"}
        ,{"name":"tariffPlanName", "header" : "Наименование", "class":"col-md-2"}
        ,{"name":"rso_organization", "header" : "РСО", "class":"col-md-2"}
        ,{"name":"tariff_option", "header" : "Опция", "class":"col-md-3"}
        ,{"name":"tariffPlanValue", "header" : "Значение", "class":"col-md-1"}
        ,{"name":"tariffPlanDescription", "header" : "Описание", "class":"col-md-3"}
    ];
    $scope.extraProps={"idColumnName":"id", "defaultOrderBy" : "tariffPlanName", "deleteConfirmationProp":"tariffPlanName"};    
    $scope.objects = [];
    $scope.rsos = [];
    $scope.tariffTypes = [];
    $scope.tariffOptions = [];
    $scope.startDateFormat = null;
    $scope.endDateFormat = null;
    $scope.object = {};
    $scope.currentObject = {};
    $scope.addMode = false;
    $scope.orderBy = { field: $scope.extraProps["defaultOrderBy"], asc: true };
    $scope.filter = '';
    
    //Headers of modal window
    $scope.headers = {}
    $scope.headers.addObjects = "Доступные объекты";//header of add objects window

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
    
    
    $scope.selectedItem = function (item) {
		var curObject = angular.copy(item);
		$scope.currentObject = curObject;
        $scope.startDateFormat = ($scope.currentObject.startDate == null) ? null : new Date($scope.currentObject.startDate);
        $scope.endDateFormat = ($scope.currentObject.endDate == null) ? null : new Date($scope.currentObject.endDate); 
        
        $scope.paramsetStartDateFormat = (new Date($scope.currentObject.startDate));            
            $scope.psStartDateFormatted =(($scope.currentObject.startDate!=null))?moment([$scope.paramsetStartDateFormat.getUTCFullYear(), $scope.paramsetStartDateFormat.getUTCMonth(), $scope.paramsetStartDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat):"";
//console.log($scope.psStartDateFormatted);   
            
//            $scope.paramsetStartDateFormatted ={
//                startDate: moment([$scope.paramsetStartDateFormat.getUTCFullYear(), $scope.paramsetStartDateFormat.getUTCMonth(), $scope.paramsetStartDateFormat.getUTCDate()]),
//                endDate : moment([$scope.paramsetStartDateFormat.getUTCFullYear(), $scope.paramsetStartDateFormat.getUTCMonth(), $scope.paramsetStartDateFormat.getUTCDate()])
//            };
//console.log($scope.paramsetStartDateFormatted.startDate);             
            
            $scope.paramsetEndDateFormat= (new Date($scope.currentObject.endDate));
            $scope.psEndDateFormatted =($scope.currentObject.endDate!=null)?moment([$scope.paramsetEndDateFormat.getUTCFullYear(), $scope.paramsetEndDateFormat.getUTCMonth(), $scope.paramsetEndDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat):"";
//console.log($scope.psEndDateFormatted);

     };
    
    $scope.setOrderBy = function (field) {
        var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
        $scope.orderBy = { field: field, asc: asc };
    };
    
    var successCallback = function (e, cb) {
        notificationFactory.success();
        $('#deleteObjectModal').modal('hide');
//        $scope.currentObject={};
        $scope.getTariffs(cb);

    };

    var successPostCallback = function (e) {
        successCallback(e, function () {
           $('#editTariffModal').modal('hide');
//            $scope.currentObject={};
        });
    };
    
    //listener for edit tariff modal window
    $('#editTariffModal').on('hide', function (e) {
      $scope.currentObject={};
    })

    var errorCallback = function (e) {
//        notificationFactory.errorInfo(e.statusText,e.data.description); 
        console.log(e);
        var errorCode = "-1";
        if (mainSvc.checkUndefinedNull(e) || mainSvc.checkUndefinedNull(e.data)){
            errorCode = "ERR_CONNECTION";
        };
        if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
            errorCode = e.resultCode || e.data.resultCode;
        };
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };

    $scope.addObject = function () {
        crudGridDataFactory($scope.crudTableName).save($scope.object, successPostCallback, errorCallback);
    };

    $scope.deleteObject = function (object) {
        crudGridDataFactory($scope.crudTableName).delete({ id: object[$scope.extraProps.idColumnName] }, successCallback, errorCallback);
    };

    $scope.getTariffs = function (cb) {
        var table = $scope.crudTableName+"/default";
        crudGridDataFactory(table).query(function (data) {
            $scope.objects = data;
            if (cb) cb();
        });
    };
    
    $scope.getRSOs = function () {
        var table = $scope.crudTableName+"/rso";
        crudGridDataFactory(table).query(function (data) {
            $scope.rsos = data; 
//console.log($scope.rsos);            
        });
    };
    
    $scope.getTariffTypes = function () {
        var table = $scope.crudTableName+"/type";
        crudGridDataFactory(table).query(function (data) {
            $scope.tariffTypes = data;     
        });
    };
    
    $scope.getTariffOptions = function () {
        var table = $scope.crudTableName+"/option";
        crudGridDataFactory(table).query(function (data) {
            $scope.tariffOptions = data;     
        });
    };
    
    $scope.getTariffs();
    $scope.getRSOs();
    $scope.getTariffTypes();
    $scope.getTariffOptions();
    
    $scope.saveObject = function(){   
        $scope.currentObject.tariffOptionKey = $scope.currentObject.tariffOption.keyname;
        
        
        var stDate = (new Date(moment($scope.psStartDateFormatted, $scope.ctrlSettings.dateFormat).format("YYYY-MM-DD"))); //reformat date string to ISO 8601             
        var UTCstdt = Date.UTC(stDate.getFullYear(), stDate.getMonth(), stDate.getDate());              
        $scope.currentObject.startDate = (!isNaN(UTCstdt)) ?new Date(UTCstdt) : null;//(new Date($scope.paramsetStartDateFormat)) /*(new Date($rootScope.reportStart))*/ || null;

        //perform end interval
        var endDate = (new Date(moment($scope.psEndDateFormatted, $scope.ctrlSettings.dateFormat).format("YYYY-MM-DD"))); //reformat date string to ISO 8601                        
        var UTCenddt = Date.UTC(endDate.getFullYear(), endDate.getMonth(), endDate.getDate()); 

        $scope.currentObject.endDate = (!isNaN(UTCenddt)) ?new Date(UTCenddt) : null;//(new Date($scope.paramsetEndDateFormat)) /*(new Date($rootScope.reportEnd))*/ || null;
//        $scope.currentObject.startDate = $scope.startDateFormat==null ? null:(new Date($scope.startDateFormat));// || 
//        $scope.currentObject.endDate = $scope.endDateFormat==null ? null: (new Date($scope.endDateFormat));// || $scope.currentObject.endDate;    
        var tmp = $scope.selectedObjects.map(function(elem){
            return elem.id;
        });
        if (($scope.currentObject.id != null) && (typeof $scope.currentObject.id != 'undefined')){
            crudGridDataFactory($scope.crudTableName).update({ rsoOrganizationId: $scope.currentObject.rso.id, tariffTypeId: $scope.currentObject.tariffType.id, contObjectIds: tmp}, $scope.currentObject, successPostCallback, errorCallback);
        }else{        
            saveTariffOnServer($scope.crudTableName, $scope.currentObject.rso.id, $scope.currentObject.tariffType.id).save({contObjectIds: tmp}, $scope.currentObject, successPostCallback, errorCallback);
        };
    };
    
    var activateMainPropertiesTab = function(){
        $('#main_properties_tab').addClass("active");
        $('#set_of_objects_tab').removeClass("active");
        $('#editTariffModal').modal();
    };
    
    $scope.prepareObjectsList = function(){
        $scope.availableObjectGroups.forEach(function(el){el.selected = false});
        $scope.ctrlSettings.selectedAll = false;
    };
    
    $scope.addTariff = function(){       
        $scope.availableObjects = [];
        $scope.selectedObjects = [];
        $scope.currentObject = {};
        $scope.startDateFormat = null;
        $scope.endDateFormat = null;
        $scope.psStartDateFormatted = moment().format($scope.ctrlSettings.dateFormat);//(new Date());        
        $scope.psEndDateFormatted= moment().format($scope.ctrlSettings.dateFormat);//(new Date()); 
        $scope.getAvailableObjects(0);
        
        $scope.set_of_objects_flag=false;
        $scope.showAvailableObjects_flag = false; // флаг, устанавливающий видимость окна с доступными объектами
        
        //settings for activate tab "Main options", when create window opened.        
        activateMainPropertiesTab();
    };
    
    $scope.editTariff = function(object){
        $scope.selectedItem(object);
        $scope.getAvailableObjects(object.id);
        $scope.getSelectedObjects();
        
        //settings for activate tab "Main options", when edit window opened. 
        $scope.set_of_objects_flag=false;
        $scope.showAvailableObjects_flag = false; // флаг, устанавливающий видимость окна с доступными объектами
        activateMainPropertiesTab();
    };
    
    var saveTariffOnServer = function(url, rsoOrganizationId, tariffTypeId){
        return $resource(url, {},{
            save: {method: 'POST', params:{rsoOrganizationId: rsoOrganizationId, tariffTypeId: tariffTypeId}}
        })
        
    };
    
    $scope.checkRequiredFields = function(){      
        if ((typeof $scope.currentObject.tariffOption=='undefined')||($scope.currentObject.tariffOption==null)){
            return false;
        };       
        return !(($scope.psStartDateFormatted==null) ||
        ($scope.psStartDateFormatted=="") ||
        ($scope.currentObject.tariffOption.keyname==null) ||
        ($scope.currentObject.rso.id==null) ||
        ($scope.currentObject.tariffType.id==null))
        &&$scope.checkPositiveNumberValue($scope.currentObject.tariffPlanValue)
        &&$scope.checkDateIntervalWithRightNull($scope.psStartDateFormatted, $scope.psEndDateFormatted);
//        &&$scope.checkDateIntervalWithRightNull($scope.startDateFormat, $scope.endDateFormat);
    };
    
    
    //tariff objects
    $scope.availableObjects = [];
    $scope.availableObjectGroups = [];
    $scope.selectedObjects = [];
    
    $scope.getAvailableObjects = function(tariffId){  
        var table=$scope.crudTableName+"/"+tariffId+"/contObject/available";        
        crudGridDataFactory(table).query(function(data){           
            $scope.availableObjects = data;
            objectSvc.sortObjectsByFullName($scope.availableObjects);
        });        
    };
    $scope.getSelectedObjects = function(){
        var table=$scope.crudTableName+"/"+$scope.currentObject.id+"/contObject";
        crudGridDataFactory(table).query(function(data){
            $scope.selectedObjects = data;
            objectSvc.sortObjectsByFullName($scope.selectedObjects);
        });
    };
    
    $scope.getGroupObjects = function(group){
        var url = $scope.groupUrl+"/"+group.id+"/contObject";
        crudGridDataFactory(url).query(function(data){           
            group.objects = data;     
        });
        
    };    
    
    $scope.getAvailableObjectGroups = function(){         
        crudGridDataFactory($scope.groupUrl).query(function(data){           
            var tempGroupArr = data;
            tempGroupArr.forEach(function(group){
                $scope.getGroupObjects(group);
            });
            $scope.availableObjectGroups = tempGroupArr;          
        });        
    };
    
    $scope.getAvailableObjectGroups();
    
    $scope.viewAvailableObjects = function(objectGroupFlag){
        $scope.showAvailableObjects_flag=!$scope.showAvailableObjects_flag;
        $scope.showAvailableObjectGroups_flag=objectGroupFlag;
        if (objectGroupFlag){
            $scope.headers.addObjects = "Доступные группы объектов";
            //prepare the object goups to view in table
//            var tmpArr = $scope.availableObjectGroups.map(function(element){
//                var result = element;
//                result.fullName = element.contGroupName;//set the field, which view entity name in table
//                return result;
//            });
            $scope.availableEntities = $scope.availableObjectGroups;//tmpArr;
        }else{
            $scope.headers.addObjects = "Доступные объекты";
            $scope.availableEntities = $scope.availableObjects;
        };
    };
    
    var objectPerform = function(addObject_flag, currentObjectId){
        var el = {};
        var arr1 = [];
        var arr2 = [];
        var resultArr = [];
        if ($scope.addObject_flag){           
            arr1 = $scope.availableObjects;
            arr2 = $scope.selectedObjects; 
            resultArr = arr2;
        }else{             
            arr2 = $scope.availableObjects;
            arr1 = $scope.selectedObjects;
            resultArr = arr1;
        };
       
        for (var i=0; i<arr1.length;i++){
            if (arr1[i].id == $scope.currentObjectId) {               
                el = angular.copy(arr1[i]);
                el.selected = false;
                arr1.splice(i,1);
                break;
            };
        }
        arr2.push(el);
        
        var tmp = resultArr.map(function(elem){
            return elem.id;
        });     
        return tmp; //Возвращаем массив Id-шников выбранных объектов
    };
    
    
    $scope.getResource = function(url, id) {
        return $resource(url, {
            }, {
                update: {method: 'PUT', params:{reportParamsetId:id}},
                addObject: {method: 'POST', params:{contObjectId: id}},
                removeObject: {method: 'DELETE'}
            });
    };
 
    $scope.addObject = function(object){
        $scope.addObject_flag = true;
        $scope.currentObjectId = object.id;
        objectPerform(true, object.id);

    };
    
    $scope.removeObject = function(object){
        $scope.addObject_flag = false;
        $scope.currentObjectId = object.id;
        objectPerform(false, object.id);

    };
    
    $scope.removeSelectedObject = function(object){
        $scope.availableObjects.push(object);
        $scope.selectedObjects.splice($scope.selectedObjects.indexOf(object), 1);
        objectSvc.sortObjectsByFullName($scope.availableObjects);
    };
    
    $scope.joinObjectsFromSelectedGroups = function(groups){
        var result = [];
        groups.forEach(function(group){
                if(group.selected){
                    Array.prototype.push.apply(result, group.objects);
//                    totalGroupObjects = group.objects;
                };
        });                 
        return result;
    };
    
    $scope.deleteDoublesObjects = function(targetArray){
        var arrLength = targetArray.length;
        while (arrLength>=2){
            arrLength--;                                               
            if (targetArray[arrLength].fullName===targetArray[arrLength-1].fullName){                   
                targetArray.splice(arrLength, 1);
            };
        }; 
    };
    
    $scope.addUniqueObjectsFromGroupsToSelectedObjects = function(arrFrom, arrTo){
        for (var j=0; j < arrFrom.length; j++){
            var uniqueFlag = true;
            for (var i = 0; i<arrTo.length; i++){
                if(arrFrom[j].fullName===arrTo[i].fullName){
                    uniqueFlag = false;
                    break;
                };
            };
            if (uniqueFlag){
                arrTo.push(arrFrom[j]);
            };
        }; 
        
    };
    
    $scope.removeGroupObjectsFromAvailableObjects = function(objectsFromGroup, availableObjects){
        for (var j=0; j < objectsFromGroup.length; j++){
            var elementIndex = -1;
            for (var i = 0; i<availableObjects.length; i++){
                if(objectsFromGroup[j].fullName===availableObjects[i].fullName){
                    elementIndex = i;
                    break;
                };
            };
            if (elementIndex>=0){
                availableObjects.splice(elementIndex,1);
            };
        }; 
    };
    
    $scope.selectAllAvailableEntities = function(){      
        for (var index = 0; index<$scope.availableEntities.length; index++){         
            $scope.availableEntities[index].selected = $scope.ctrlSettings.selectedAll;
        };
    };
    
    $scope.addSelectedEntities = function(){
    //console.log($scope.availableObjects);       
        if ($scope.showAvailableObjectGroups_flag){
            var totalGroupObjects = $scope.joinObjectsFromSelectedGroups($scope.availableEntities);            
            objectSvc.sortObjectsByFullName(totalGroupObjects);
            //del doubles
            
            $scope.deleteDoublesObjects(totalGroupObjects);
            //add groupObjects to selected objects
                //add only unique objects
            $scope.addUniqueObjectsFromGroupsToSelectedObjects(totalGroupObjects, $scope.selectedObjects);   
            //remove groupObjects from availableObjects
            $scope.removeGroupObjectsFromAvailableObjects(totalGroupObjects, $scope.availableObjects);   
        };
        var tmpArray = angular.copy($scope.availableObjects);
        for(var i =0; i<$scope.availableObjects.length; i++){
            var curObject = $scope.availableObjects[i];

            if (curObject.selected){
    //console.log(curObject);                            
    // console.log("curObject is performanced");               
                var elem = angular.copy(curObject);
                elem.selected = null;
    //console.log(tmpArray.indexOf(curObject));  
                var elementIndex = -1;
                tmpArray.some(function(element,index,array){
                    if (element.fullName === curObject.fullName){
                        elementIndex = index;
                        return true;
                    }else{
                        return false;
                    }
                });
                tmpArray.splice(elementIndex, 1);
                $scope.selectedObjects.push(elem);
                curObject.selected = null;
            };
        }
        $scope.availableObjects = tmpArray;
        objectSvc.sortObjectsByFullName($scope.selectedObjects);
        $scope.showAvailableObjects_flag=false;
    };    
    
    $scope.isSystemuser = function(){
        return mainSvc.isSystemuser();
    };
    $scope.isAdmin = function(){
        return mainSvc.isAdmin();
    };
    
    $scope.showAddObjectButton = function(){
//console.log('$scope.showAvailableObjects_flag = '+$scope.showAvailableObjects_flag);
//console.log('$scope.set_of_objects_flag = '+$scope.set_of_objects_flag);        
        return !$scope.showAvailableObjects_flag && $scope.set_of_objects_flag;
    };
    
    
        //date picker
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
    $(document).ready(function() {
                  $('#inputSingleDateStart').datepicker({
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
                  $('#inputSingleDateEnd').datepicker({
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
                  $('#inputStartDate').datepicker({
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
    
    $scope.checkDateIntervalWithRightNull = function(left, right){     
        if ((left==null)||(left=="")){return false;};
        if (!mainSvc.checkStrForDate(left)){
            return false;
        };
        if ((right == null)||(right=="")){return true;};
        if (!mainSvc.checkStrForDate(right)){
            return false;
        };        
        
        var startDate = mainSvc.strDateToUTC(left, $scope.ctrlSettings.dateFormat);
        var sd = (startDate!=null)?(new Date(startDate)) : null;         
        var endDate = mainSvc.strDateToUTC(right, $scope.ctrlSettings.dateFormat);
        var ed = (endDate!=null)?(new Date(endDate)) : null;                
//        if ((isNaN(startDate.getTime()))|| (isNaN(endDate.getTime()))){return false;};       
        if ((sd==null)|| (ed==null)){return false;};               
        return ed>=sd;
//        return right>=left;
    };
    
        //check user rights
    $scope.isAdmin = function(){
        return mainSvc.isAdmin();
    };

    $scope.isReadonly = function(){
        return mainSvc.isReadonly();
    };

    $scope.isROfield = function(){
        return ($scope.isReadonly());
    };
    
}]);