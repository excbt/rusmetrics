//reports controller
var app = angular.module('portalNMC');
app.controller('ReportsCtrl',['$scope', '$rootScope', '$http', 'crudGridDataFactory', 'notificationFactory', 'objectSvc', 'mainSvc', function($scope, $rootScope, $http, crudGridDataFactory, notificationFactory, objectSvc, mainSvc){
    
        //ctrl settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.dateFormat = "DD.MM.YYYY"; //date format
    
    $scope.set_of_objects_flag = false; //флаг: истина - открыта вкладка с объектами
    $scope.showAvailableObjects_flag = false; // флаг, устанавливающий видимость окна с доступными объектами
    $scope.currentSign = 9999;// устанавливаем начальное значение отличное от нулл и других возможных значение; нулл - будем отлавливать

    $scope.extraProps={"idColumnName":"id", "defaultOrderBy" : "name", "deleteConfirmationProp":"name"};    
    $scope.activeStartDateFormat = new Date();
    $scope.activeStartDateFormatted = moment().format($scope.ctrlSettings.dateFormat);
    
    //file types
    $scope.fileTypes = ["PDF", "HTML", "XLSX"];
    
    $scope.currentObject = {};
    $scope.objects = [];
    $scope.columns = [
        {"name":"reportTypeName","header":"Тип отчета", "class":"col-md-11"}
    ];
    $scope.paramsetColumns = [
        {"name":"name","header":"Наименование", "class":"col-md-1"}
        ,{"name":"reportTemplateName","header":"Шаблон", "class":"col-md-1"}
        ,{"name":"period","header":"Период", "class":"col-md-1"}
        ,{"name":"fileType","header":"Тип файла", "class":"col-md-1"}
    ];
    $scope.groupUrl = "../api/contGroup";
    $scope.crudTableName = "../api/reportParamset"; 
    
        //Headers of modal window
    $scope.headers = {}
    $scope.headers.addObjects = "Доступные объекты";//header of add objects window
    
    $scope.isSystemuser = function(){
        $scope.userInfo = $rootScope.userInfo;
        return $scope.userInfo._system;
    };
    //report types
    $scope.reportTypes = [];
    $scope.getReportTypes = function(){
        var table = "../api/reportSettings/reportType";
        crudGridDataFactory(table).query(function(data){
            $scope.reportTypes = data;
//console.log(data);
            var newObjects = [];
            var newObject = {};
            for (var i = 0; i<data.length; i++){
                if (!data[i]._enabled){
                    continue;
                };
                if ((!$scope.isSystemuser()&&data[i].isDevMode)){
                    continue;
                };
                newObject = {};
                newObject.reportType = data[i].keyname;
                newObject.reportTypeName = data[i].caption;
                newObject.suffix = data[i].suffix;
                newObject.reportMetaParamSpecialList = data[i].reportMetaParamSpecialList;
                newObject.reportMetaParamCommon = data[i].reportMetaParamCommon;
                    //flag: the toggle visible flag for the special params page.
                newObject.reportMetaParamSpecialList_flag = (data[i].reportMetaParamSpecialList.length>0?true:false);
                
                newObjects.push(newObject);
            };        
            $scope.objects = newObjects;        
            $scope.getActive();
        });
    };
    $scope.getReportTypes();
    
    //report periods
    $scope.reportPeriods = [];
    $scope.getReportPeriods = function(){
        var table = "../api/reportSettings/reportPeriod";
        crudGridDataFactory(table).query(function(data){
            $scope.reportPeriods = data;
        });
    };
    $scope.getReportPeriods();
    
    $scope.oldColumns = [
        {"name":"name", "header":"Название варианта", "class":"col-md-5"}
        ,{"name":"activeStartDate", "header":"Действует с", "class":"col-md-2"}
    ];

    var successCallback = function (e) {
        notificationFactory.success();
    };

    var errorCallback = function (e) {
        notificationFactory.errorInfo(e.statusText,e.data.description);       
    };
    
    $scope.getParamsets = function(table, type){
        crudGridDataFactory(table).query(function (data) {
            type.paramsets = data;
            type.paramsetsCount = data.length;            
        });
    };
      

 //get templates   
    $scope.getActive = function(){
        if (($scope.objects == []) || (typeof $scope.objects[0].suffix == 'undefined')){return;};
        for (var i=0; i<$scope.objects.length; i++){
            $scope.getParamsets($scope.crudTableName+$scope.objects[i].suffix, $scope.objects[i]);
        };
    };

    $scope.toogleShowGroupDetails = function(curObject){//switch option: current goup details     
         curObject.showGroupDetails = !curObject.showGroupDetails;
//console.log(curObject.paramsets);        
    };
    
    $scope.selectedItem = function(parentItem, item){
        $scope.setCurrentReportType(parentItem);       
        var curObject = angular.copy(item);
		$scope.currentObject = curObject;
        $scope.activeStartDateFormat = (curObject.activeStartDate == null) ? null : new Date(curObject.activeStartDate);
        var activeStartDate = new Date(curObject.activeStartDate);
console.log(curObject);         
        $scope.activeStartDateFormatted = (curObject.activeStartDate == null) ? "" : moment([activeStartDate.getUTCFullYear(),activeStartDate.getUTCMonth(), activeStartDate.getUTCDate()]).format($scope.ctrlSettings.dateFormat);
        $scope.getTemplates();       
    };
    
    $scope.checkAndRunParamset = function(type,object, previewFlag){
        var flag = $scope.checkRequiredFieldsOnSave();
        if (flag===false){
            $('#messageForUserModal').modal();
        }else{
            $scope.createReportWithParams(type, object, previewFlag);
        };
    };
    
    $scope.currentReportType = {};
    $scope.setCurrentReportType = function(object){
        $scope.currentReportType = object;
//        $scope.currentReportType.reportType = object.reportType;
//        $scope.currentReportType.reportTypeName=object.reportTypeName;
//        $scope.currentReportType.suffix=object.suffix;
    };
    
    var activateMainPropertiesTab = function(){
        $('#main_properties_tab').addClass("active");
        $('#set_of_objects_tab').removeClass("active");
        $('#extra_properties_tab').removeClass("active");
        $('#editParamsetModal').modal();
    };
    
        //get custom directory data
    $scope.getDirectory = function(url, obj){
        $http.get(url)
            .success(function(data){
                obj.specialTypeDirectoryValues = data;  
//console.log(obj);    
//                obj.specialTypeDirectoryValues.forEach(function(element){
//                    console.log(element[obj.specialTypeDirectoryValue]);
//                    console.log(element[obj.specialTypeDirectoryCaption]);
//                });
            })
            .error(function(e){
                console.log(e);
            });
    };
        //Проверка параметра - ссылочный параметр или нет
    function isParamSpecialTypeDirectory(element){
        var result=  (element.paramSpecialType.specialTypeDirectoryUrl!=null)
                && (typeof element.paramSpecialType.specialTypeDirectoryUrl!='undefined')
        && (element.paramSpecialType.specialTypeDirectoryKey!=null)
                && (typeof element.paramSpecialType.specialTypeDirectoryKey!='undefined')
        && (element.paramSpecialType.specialTypeDirectoryCaption!=null)
                && (typeof element.paramSpecialType.specialTypeDirectoryCaption!='undefined')
        && (element.paramSpecialType.specialTypeDirectoryValue!=null)
                && (typeof element.paramSpecialType.specialTypeDirectoryValue!='undefined');       
        return result;
    };
    
    $scope.editParamSet =function(parentObject,object){
//        $scope.setCurrentReportType(parentObject);     
        $scope.selectedItem(parentObject, object);
        $scope.currentParamSpecialList = $scope.currentReportType.reportMetaParamSpecialList.map(function(element){
            var result = {};
            result.paramSpecialCaption = element.paramSpecialCaption;
            result.reportMetaParamSpecialId = element.id;
            result.paramSpecialRequired = element.paramSpecialRequired;
            result.paramSpecialTypeKeyname = element.paramSpecialType.keyname;
            if (isParamSpecialTypeDirectory(element))
            {
                result.specialTypeDirectoryUrl =element.paramSpecialType.specialTypeDirectoryUrl;
                result.specialTypeDirectoryKey =element.paramSpecialType.specialTypeDirectoryKey;
                result.specialTypeDirectoryCaption = element.paramSpecialType.specialTypeDirectoryCaption;
                result.specialTypeDirectoryValue =element.paramSpecialType.specialTypeDirectoryValue;
                $scope.getDirectory(".."+result.specialTypeDirectoryUrl, result);                
            };
            //Ищем значение этого параметра в массиве параметров варианта отчета
            if (object.paramSpecialList.length==0){
                result.textValue = null;
                result.numericValue = null;
                result.oneDateValue = null;
                result.startDateValue = null;
                result.endDateValue = null;
                result.oneDateValueFormatted=null;
                result.startDateValueFormatted=null;
                result.endDateValueFormatted=null;
                result.directoryValue = null;
                return result;
            }
            var elementIndex = -1;
            object.paramSpecialList.some(function(el,index,array){
                if (el.reportMetaParamSpecialId === element.id){
                    elementIndex = index;
                    return true;
                }else{
                    return false;
                }
            });
            if (elementIndex!=-1){
                result.id = object.paramSpecialList[elementIndex].id || null;
                result.textValue = object.paramSpecialList[elementIndex].textValue || null;
                result.numericValue = object.paramSpecialList[elementIndex].numericValue || null;
                result.oneDateValue = object.paramSpecialList[elementIndex].oneDateValue || null;
                result.startDateValue = object.paramSpecialList[elementIndex].startDateValue || null;
                result.endDateValue = object.paramSpecialList[elementIndex].endDateValue || null;
                result.oneDateValueFormatted=(object.paramSpecialList[elementIndex].oneDateValue == null) ? null :new Date(object.paramSpecialList[elementIndex].oneDateValue);
                result.startDateValueFormatted=(object.paramSpecialList[elementIndex].startDateValue == null) ? null :new Date(object.paramSpecialList[elementIndex].startDateValue);
                result.endDateValueFormatted=(object.paramSpecialList[elementIndex].endDateValue == null) ? null :new Date(object.paramSpecialList[elementIndex].endDateValue);
                result.directoryValue = Number(object.paramSpecialList[elementIndex].directoryValue) || null;
                result.version = object.paramSpecialList[elementIndex].version || null;
            }else{
                result.id = null;
                result.textValue = null;
                result.numericValue = null;
                result.oneDateValue = null;
                result.startDateValue = null;
                result.endDateValue = null;
                result.directoryValue = null;
                
                result.oneDateValueFormatted=null;
                result.startDateValueFormatted=null;
                result.endDateValueFormatted=null;
            }
            return result;
            
        });
        $scope.currentObject.showParamsBeforeRunReport = !$scope.currentObject.allRequiredParamsPassed;
//console.log($scope.currentObject.allRequiredParamsPassed);         
        $scope.editParamset_flag = true;
        $scope.createByTemplate_flag = false;
        $scope.getAvailableObjects(object.id);//получаем доступные объекты для заданного парамсета
        $scope.getSelectedObjects();
        
        $scope.currentSign = object.reportPeriod.sign;
        if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){           
            $scope.paramsetStartDateFormat = (new Date(object.paramsetStartDate));
            $scope.psStartDateFormatted =(object.paramsetStartDate!=null)?moment([$scope.paramsetStartDateFormat.getUTCFullYear(), $scope.paramsetStartDateFormat.getUTCMonth(), $scope.paramsetStartDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat):"";
console.log($scope.psStartDateFormatted);            
            $scope.paramsetEndDateFormat= (new Date(object.paramsetEndDate));
            $scope.psEndDateFormatted =(object.paramsetEndDate!=null)?moment([$scope.paramsetEndDateFormat.getUTCFullYear(), $scope.paramsetEndDateFormat.getUTCMonth(), $scope.paramsetEndDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat):"";
console.log($scope.psEndDateFormatted);
        }
    //settings for activate tab "Main options", when edit window opened.
        $scope.set_of_objects_flag=false;
        $scope.showAvailableObjects_flag = false;
//        $('#main_properties_tab').addClass("active");
//        $('#set_of_objects_tab').removeClass("active");
//        $('#createParamsetModal').modal();
        activateMainPropertiesTab();
    };
    
    //Account objects
    $scope.availableObjects = [];
    $scope.selectedObjects = [];
    $scope.availableObjectGroups = [];
    
    $scope.getAvailableObjects = function(paramsetId){      
        var table=$scope.crudTableName+"/"+paramsetId+"/contObject/available";        
        crudGridDataFactory(table).query(function(data){           
            $scope.availableObjects = data; 
            objectSvc.sortObjectsByFullName($scope.availableObjects);
        });        
    };
//    $scope.getAvailableObjects();
    $scope.getSelectedObjects = function(){
        var table=$scope.crudTableName+"/"+$scope.currentObject.id+"/contObject";
        crudGridDataFactory(table).query(function(data){
            $scope.selectedObjects = data;
            objectSvc.sortObjectsByFullName($scope.selectedObjects);
        });
    };
    
    $scope.prepareObjectsList = function(){
        $scope.availableObjectGroups.forEach(function(el){el.selected = false});
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
    
    $scope.addSelectedEntities = function(){
    //console.log($scope.availableObjects);  
        if ($scope.showAvailableObjectGroups_flag){
            var totalGroupObjects = $scope.joinObjectsFromSelectedGroups($scope.availableEntities);   
//console.log(totalGroupObjects);            
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
    
    $scope.removeObject = function(object){
        $scope.addObject_flag = false;
        $scope.currentObjectId = object.id;
        objectPerform(false, object.id);

    };
    
        //templates
    $scope.templatesForCurrentParaset = [];
    $scope.getTemplates = function(){        
       var table = "../api/reportTemplate"+$scope.currentReportType.suffix; 
        crudGridDataFactory(table).query(function(data){
            $scope.templatesForCurrentParaset = data;
            //if create new paraset
            if ($scope.createParamset_flag){
                //set default template
                $scope.currentObject.reportTemplate = $scope.templatesForCurrentParaset[0];
            }
        });
    };
    
    $scope.$watch('currentObject.reportPeriodKey', function (newKey) {
        //отслеживаем изменение периода у варианта отчета
        for (var i = 0; i<$scope.reportPeriods.length;i++){
            if (newKey == $scope.reportPeriods[i].keyname){
                $scope.currentSign = $scope.reportPeriods[i].sign;
                if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){           
                    $scope.paramsetStartDateFormat = ($scope.currentObject.paramsetStartDate == null) ? null : (new Date($scope.currentObject.paramsetStartDate));
                    $scope.paramsetEndDateFormat= ($scope.currentObject.paramsetEndDate == null) ? null : (new Date($scope.currentObject.paramsetEndDate));
                }
            };
        };
              
    }, false);
    
    
    $scope.isSystemuser = function(){
        $scope.userInfo = $rootScope.userInfo;
        return $scope.userInfo._system;
    };
    
    $scope.isDisabled = function(){
//console.log($scope.currentObject.common || !$scope.currentObject._active);        
//        return $scope.currentObject.common || !$scope.currentObject._active;
        return false;
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
                  $('#inputSingleDateStart').daterangepicker(
                      { 
                        locale : $scope.dateOptsParamsetRu.locale,
                        singleDatePicker: $scope.dateOptsParamsetRu.singleDatePicker,
                        format: $scope.dateOptsParamsetRu.format
                      }, 
                      function(start, end, label) {
//                        console.log(start.toISOString(), end.toISOString(), label);
                        }
                  );
        
                  $('#inputSingleDateEnd').daterangepicker(
                      { 
                        locale : $scope.dateOptsParamsetRu.locale,
                        singleDatePicker: $scope.dateOptsParamsetRu.singleDatePicker,
                        format: $scope.dateOptsParamsetRu.format
                      }, 
                      function(start, end, label) {
//                        console.log(start.toISOString(), end.toISOString(), label);
                        }
                  );
        
                  $('#inputStartDate').daterangepicker(
                      { 
                        locale : $scope.dateOptsParamsetRu.locale,
                        singleDatePicker: $scope.dateOptsParamsetRu.singleDatePicker,
                        format: $scope.dateOptsParamsetRu.format
                      }, 
                      function(start, end, label) {
//                        console.log(start.toISOString(), end.toISOString(), label);
                        }
                  );
    });
    
            //key down listener
    window.onkeydown = function(e){ 
        console.log(e.keyCode);
        if (e.keyCode == 27){//ESC pressed
//            $('#inputSingleDateStart').daterangepicker('hide');
            var datePikeckerDiv = document.getElementsByClassName('daterangepicker dropdown-menu single opensright show-calendar');
//console.log(datePikeckerDiv);            
            if (angular.isDefined(datePikeckerDiv)){
                for(var i = 0; i<datePikeckerDiv.length;i++){
                    datePikeckerDiv[i].style.display = 'none';
                };
            };
        };
    };

    
    
    //checkers
        //check date interval
    $scope.checkDateInterval = function(left, right){      
        if ((left==null)|| (right==null)||(left=="")||(right=="")){return false;};
        var startDate = mainSvc.strDateToUTC(left, $scope.ctrlSettings.dateFormat);
        var sd = (startDate!=null)?(new Date(startDate)) : null;         
        var endDate = mainSvc.strDateToUTC(right, $scope.ctrlSettings.dateFormat);
        var ed = (endDate!=null)?(new Date(endDate)) : null;                
//        if ((isNaN(startDate.getTime()))|| (isNaN(endDate.getTime()))){return false;};       
        if ((sd==null)|| (ed==null)){return false;};               
        return ed>=sd;
    };
        //check fields
    $scope.checkRequiredFields = function(){
        var result;
        if (!($scope.currentObject.hasOwnProperty('reportPeriodKey'))||!($scope.currentObject.hasOwnProperty('reportTemplate'))){
//            $scope.currentObject.showParamsBeforeRunReport = true;
            return false;
        };  
        //interval validate flag
            //default value    
        var intervalValidate_flag = true; 
            //if the paramset use interval
        if ($scope.currentSign==null){
                //check interval
            var startDate = new Date($scope.psStartDateFormatted);
            var endDate = new Date($scope.psEndDateFormatted);
            intervalValidate_flag = (!isNaN(startDate.getTime()))||(!isNaN(endDate.getTime()))||$scope.checkDateInterval($scope.psStartDateFormatted, $scope.psEndDateFormatted);
        };
console.log(intervalValidate_flag);        
        result = !((($scope.currentObject.reportPeriodKey==null) ||   
        ($scope.currentObject.reportTemplate.id==null)))
        &&intervalValidate_flag;
//        $scope.currentObject.allRequiredParamsPassed = !result;      
        return result;
        
    };
    
        //check field "outputFileZipped"
    $scope.checkOutputFileZipped = function(){
        if (!$scope.currentReportType.hasOwnProperty('reportMetaParamCommon')){
            return false;
        };
        var result = false;
//console.log($scope.currentReportType);        
        if ($scope.currentReportType.reportMetaParamCommon.manyContObjectsZipOnly && ($scope.selectedObjects.length>1))
        {
            $scope.currentObject.outputFileZipped =  true;
            result= true;
        };
        return result;
    };
        //check fields before save
    $scope.checkRequiredFieldsOnSave = function(){
        if (!$scope.currentReportType.hasOwnProperty('reportMetaParamCommon')){
            return true;
        };
        var result= true;
        $scope.messageForUser = "Не все параметры варианта отчета заданы:\n";
        //Check common params before save
            //file ext
        if (angular.isUndefined($scope.currentObject.outputFileType)||($scope.currentObject.outputFileType===null)||($scope.currentObject.outputFileType==="")){
            $scope.messageForUser += "- Не задан тип файла"+"\n";
        };
        
            //one date - for future
//        if ($scope.currentReportType.reportMetaParamCommon.oneDateRequired && something)
        
            //start date
            //if the paramset use a date interval
        if ($scope.currentSign==null){
//            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && $scope.paramsetStartDateFormat==null)
//            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && $scope.psStartDateFormatted=="")    
//            {
//                $scope.messageForUser += "- Не задано начало периода"+"\n";
//            };
            var startDateMillisec = mainSvc.strDateToUTC($scope.psStartDateFormatted, $scope.ctrlSettings.dateFormat);
            var startDate = new Date(startDateMillisec);
            var endDateMillisec = mainSvc.strDateToUTC($scope.psEndDateFormatted, $scope.ctrlSettings.dateFormat);
            var endDate = new Date(endDateMillisec);
            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && isNaN(startDate.getTime()))    
            {
                $scope.messageForUser += "- Некорректно задано начало периода"+"\n";
            };
                        //end date
//            if ($scope.currentReportType.reportMetaParamCommon.endDateRequired && $scope.paramsetEndDateFormat==null)
//            if ($scope.currentReportType.reportMetaParamCommon.endDateRequired && $scope.psEndDateFormatted=="")    
//            {
//                $scope.messageForUser += "- Не задан конец периода"+"\n";
//            };
            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && isNaN(endDate.getTime()))    
            {
                $scope.messageForUser += "- Некорректно задан конец периода"+"\n";
            };
        }

                    //Count of objects
        if ($scope.currentReportType.reportMetaParamCommon.oneContObjectRequired && ($scope.selectedObjects.length==0))
        {
            $scope.messageForUser += "- Должен быть выбран хотя бы один объект"+"\n";
        };
        if ($scope.currentReportType.reportMetaParamCommon.manyContObjectRequired && ($scope.selectedObjects.length<=0))
        {
            $scope.messageForUser += "- Необходимо выбрать несколько объектов"+"\n";
        };
        
        if ($scope.currentReportType.reportMetaParamCommon.manyContObjectsZipOnly && ($scope.selectedObjects.length>1))
        {
            $scope.currentObject.outputFileZipped =  true;
        };
        
        $scope.currentParamSpecialList.forEach(function(element, index, array){
            if (element.paramSpecialRequired && !(element.textValue 
                                                 || element.numericValue 
                                                 || element.oneDateValue 
                                                 || element.startDateValue
                                                 || element.endDateValue
                                                 || element.directoryValue)
               )
            {
                $scope.messageForUser += "- Не задан параметр \""+element.paramSpecialCaption+"\" \n";
            }
        });
        if($scope.messageForUser!="Не все параметры варианта отчета заданы:\n"){
//            $scope.messageForUser = "Внимание!!! \n"+$scope.messageForUser;
//            $scope.messageForUser += "\n Этот вариант отчета нельзя запустить без уточнения или использовать в рассылке, не задав обязательных параметров. Продолжить?";
//            $scope.messageForUser+=" Этот вариант отчета нельзя запустить без уточнения, а также его нельзя использовать в рассылке. Продолжить?";
//            alert($scope.messageForUser);
//            $('#messageForUserModal').modal();
            result= false;
        };
        result =result && $scope.checkRequiredFields();  
//        $scope.currentObject.showParamsBeforeRunReport =!result;
        if (!result){
//console.log("1");            
            $scope.currentObject.showParamsBeforeRunReport = true;
        };
//        else{
//            $scope.currentObject.showParamsBeforeRunReport = false;
//        };
        return result;
    };
    
    $scope.createReport = function(type,paramset){
        var url ="../api/reportService"+type.suffix+"/"+paramset.id+"/download";
        $http.get(url, {responseType: 'arraybuffer'})
            .then(function(response) {        
                var fileName = response.headers()['content-disposition'];           
                fileName = fileName.substr(fileName.indexOf('=') + 2, fileName.length-fileName.indexOf('=')-3);
                var file = new Blob([response.data], { type: response.headers()['content-type'] });
                saveAs(file,fileName);
            })
            .catch(function(e){
                notificationFactory.errorInfo(e.statusText,e.data.description);
            });    
    };
    
    $scope.previewReport = function(type,paramset){
        var url ="../api/reportService"+type.suffix+"/"+paramset.id+"/preview";
        window.open(url);    
    };
console.log(new Date(null));    
    //Формируем отчет с заданными параметрами
    $scope.createReportWithParams = function(type // тип отчета
                                            , paramset //вариант отчета
                                            , previewFlag //флаг - формировать отчет или сделать предпросмотр
                                            ){
        var tmpParamset = angular.copy(paramset);//делаем копию варианта отчета
        //формируем массив ИД объектов, для которых формируется отчет.          
        var objectIds = $scope.selectedObjects.map(function(element){          
            var result = element.id;
            return result;
        });      
         //set the list of the special params - устанавливаем специальные параметры отчета
        tmpParamset.paramSpecialList = $scope.currentParamSpecialList;
        //Если вариант отчета создается за период, задаем начало и конец периода
        if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){
//            tmpParamset.paramsetStartDate = (new Date($scope.paramsetStartDateFormat)) /*(new Date($rootScope.reportStart))*/ || null;
//            tmpParamset.paramsetEndDate = (new Date($scope.paramsetEndDateFormat)) /*(new Date($rootScope.reportEnd))*/ || null;
            var startDate = mainSvc.strDateToUTC($scope.psStartDateFormatted, $scope.ctrlSettings.dateFormat);
            tmpParamset.paramsetStartDate = (startDate!=null)?(new Date(startDate)) : null;
            var endDate = mainSvc.strDateToUTC($scope.psEndDateFormatted, $scope.ctrlSettings.dateFormat);
            tmpParamset.paramsetEndDate = (endDate!=null)?(new Date(endDate)) : null;
        }else{
            tmpParamset.paramsetStartDate = null;
            tmpParamset.paramsetEndDate = null;
        };

//console.log(paramset);        
        var fileExt = "";
        if (previewFlag){//проверяем флаг предпросмотра,
            //если флаг установлен, то
            tmpParamset.outputFileType="HTML";//ставим формат выходного файла - HTML
            tmpParamset.outputFileZipped=false;//ставим флаг -не архивировать полученный отчет
            fileExt = "html";
        }else{
            fileExt=tmpParamset.outputFileZipped?"zip":tmpParamset.outputFileType.toLowerCase();
        }
        var url ="../api/reportService"+type.suffix+"/"+tmpParamset.id+"/download";  //формируем url адрес запроса
        var responseType = "arraybuffer";//указываем тип ответа от сервера
        //делаем запрос на сервер
//        $http.put(url, paramset, { contObjectIds: objectIds }, {responseType: responseType})
        $http({
            url: url, 
            method: "PUT",
            params: { contObjectIds: objectIds },
            data: tmpParamset,
            responseType: responseType
        })
        .then(function(response) {
           //обрабатываем полученный результат запроса
            var fileName = response.headers()['content-disposition']; //читаем кусок заголовка, в котором пришло название файла
            fileName = fileName.substr(fileName.indexOf('=') + 2, fileName.length-fileName.indexOf('=')-3);//вытаскиваем непосредственно название файла.
            var file = new Blob([response.data], { type: response.headers()['content-type']/* тип файла тоже приходит в заголовке ответа от сервера*/ });//формируем файл из полученного массива байт
            if (previewFlag){              
                //если нажат предпросмотр, то
                var url = window.URL.createObjectURL(file);//формируем url на сформированный файл
                window.open(url);//открываем сформированный файл в новой вкладке браузера
            }else{    
                saveAs(file,fileName);//если нужен отчет, то сохраняем файл на диск клиента
            };
        })
        .catch(function(e){
            //если при запросе произошла ошибка, то выводим ее на экран во всплывающем окне
            notificationFactory.errorInfo(e.statusText,e);
        });
    };
}]);