//reports controller
var app = angular.module('portalNMC');
app.filter('resourceCategoryFilter', function(){
    return function(items, props){
        if (props.isAll == true){
            return items;
        };
        var filteredItems = [];      
        items.forEach(function(item){
            if (item.resourceCategory == props.name){
                filteredItems.push(item);
            };
        });     
        return filteredItems;
    }
});
app.controller('ReportsCtrl',['$scope', '$rootScope', '$http', 'crudGridDataFactory', 'notificationFactory', 'objectSvc', 'mainSvc', '$timeout', 'reportSvc', function($scope, $rootScope, $http, crudGridDataFactory, notificationFactory, objectSvc, mainSvc, $timeout, reportSvc){
    
    $rootScope.ctxId = "reports_page";
//console.log(navigator.userAgent);    
        //ctrl settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.dateFormat = "DD.MM.YYYY"; //date format
    $scope.ctrlSettings.selectedAll = false;

        //html- с индикатором загрузки страницы
    $scope.ctrlSettings.htmlLoading = mainSvc.getHtmlLoading();
    //report open modes
    $scope.ctrlSettings.openModes = {
        "create":
        { "name": "create",
         "isContext": false
        }, 
        "edit":{ "name": "edit"
        }, 
        "preview":{ "name": "preview"
        }
    };
    $scope.ctrlSettings.currentReportPreviewEnabledFlag = false;
    
    $scope.set_of_objects_flag = false; //флаг: истина - открыта вкладка с объектами
    $scope.showAvailableObjects_flag = false; // флаг, устанавливающий видимость окна с доступными объектами
    $scope.currentSign = 9999;// устанавливаем начальное значение отличное от нулл и других возможных значение; нулл - будем отлавливать

    $scope.extraProps = {"idColumnName": "id", "defaultOrderBy" : "name", "deleteConfirmationProp": "name"};    
    $scope.activeStartDateFormat = new Date();
    $scope.activeStartDateFormatted = moment().format($scope.ctrlSettings.dateFormat);
    
    //file types
    $scope.fileTypes = ["PDF", "HTML", "XLSX"];
    
    $scope.currentObject = {};
    $scope.reportObjects = [];
//    $scope.columns = [
//        {"name":"reportTypeName","header":"Тип отчета", "class":"col-xs-11 col-md-11"}
//    ];
    $scope.paramsetColumns = [
        {"name":"name", "header":"Наименование", "class":"col-xs-3 col-md-3"}
        ,{"name":"reportTemplateName", "header":"Шаблон", "class":"col-xs-3 col-md-3"}
        ,{"name":"period", "header":"Период", "class":"col-xs-2 col-md-2"}
        ,{"name":"fileType", "header":"Тип файла", "class":"col-xs-1 col-md-1"}
    ];
    $scope.groupUrl = "../api/contGroup";
    $scope.paramsetsUrl = "../api/reportParamset"; 
    
        //Headers of modal window
    $scope.headers = {}
    $scope.headers.addObjects = "Доступные объекты";//header of add objects window
    
    //report context launch setting
    $scope.ctrlSettings.reportCountList = 5;//border report count: if the reports is more than this border that view two-level menu, else - one-level
    
    $scope.isSystemuser = function(){
        $scope.userInfo = $rootScope.userInfo;
        return $scope.userInfo._system;
    };
        //get report categories
    $scope.categories = reportSvc.getReportCategories();
        //prepare resource categories
    $scope.resourceCategories = reportSvc.getResourceCategories();
    $scope.currentResourceCategory = reportSvc.getDefaultResourceCategory();
    
     //get paramsets   
    
    $scope.$on('reportSvc:reportTypesIsLoaded', function(){
        //report types
        $scope.reportObjects = reportSvc.getReportTypes();
        $scope.getActive();
    });
    
    $scope.$on('reportSvc:reportPeriodsIsLoaded', function(){
        //report types
        $scope.reportPeriods = reportSvc.getReportPeriods();        
    });
    
    //report types
//    $scope.reportTypes = [];
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
                if ((!$scope.isSystemuser() && data[i].isDevMode)){
                    continue;
                };
                newObject = {};
                newObject.reportType = data[i].keyname;        
                newObject.reportTypeName = data[i].caption;
                newObject.suffix = data[i].suffix;
                newObject.reportMetaParamSpecialList = data[i].reportMetaParamSpecialList;
                newObject.reportMetaParamCommon = data[i].reportMetaParamCommon;
                newObject.reportCategory = data[i].reportCategory;
                    //flag: the toggle visible flag for the special params page.
                newObject.reportMetaParamSpecialList_flag = (data[i].reportMetaParamSpecialList.length > 0 ? true : false);                
                for (var categoryCounter = 0; categoryCounter < $scope.categories.length; categoryCounter++){                         
                    if (newObject.reportCategory.localeCompare($scope.categories[categoryCounter].name) == 0){    
                        newObject.reportTypeName = newObject.reportTypeName.slice(3, newObject.reportTypeName.length);
                        $scope.categories[categoryCounter].reportTypes.push(newObject);                     
                        continue;
                    };
                };
                
                newObjects.push(newObject);
            };        
            $scope.reportObjects = newObjects;        
            $scope.getActive();
        });
    };
//    $scope.getReportTypes();

    
    //report periods
    if (reportSvc.getReportPeriodsIsLoaded()){
        $scope.reportPeriods = angular.copy(reportSvc.getReportPeriods());    
    };
//    $scope.reportPeriods = [];
//    $scope.getReportPeriods = function(){
//        var table = "../api/reportSettings/reportPeriod";
//        crudGridDataFactory(table).query(function(data){
//            $scope.reportPeriods = data;
//        });
//    };
//    $scope.getReportPeriods();
    
    $scope.oldColumns = [
        {"name":"name", "header":"Название варианта", "class":"col-xs-5 col-md-5"}
        ,{"name":"activeStartDate", "header":"Действует с", "class":"col-xs-2 col-md-2"}
    ];

    var successCallback = function (e) {
        notificationFactory.success();
    };

    var errorCallback = function (e) {
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
//        notificationFactory.errorInfo(e.statusText,e.data.description);       
    };
    
    $scope.getParamsets = function(table, type){
        crudGridDataFactory(table).query(function (data) {
            type.paramsetsCount = data.length;
            type.checkedParamsetsCount = 0;
            var tmp = angular.copy(data);
            tmp.forEach(function(el){
                var currentSign = el.reportPeriod.sign;
                if ((currentSign == null) || (typeof currentSign == 'undefined')){           
                    var paramsetStartDateFormat = (new Date(el.paramsetStartDate));
                    el.psStartDateFormatted = (el.paramsetStartDate != null) ? moment([paramsetStartDateFormat.getUTCFullYear(), paramsetStartDateFormat.getUTCMonth(), paramsetStartDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat) : "";
                    var paramsetEndDateFormat = (new Date(el.paramsetEndDate));
                    el.psEndDateFormatted = (el.paramsetEndDate != null) ? moment([paramsetEndDateFormat.getUTCFullYear(), paramsetEndDateFormat.getUTCMonth(), paramsetEndDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat) : "";
                }
            //settings for activate tab "Main options", when edit window opened.
                $scope.set_of_objects_flag = false;
                $scope.showAvailableObjects_flag = false;
                $scope.getSelectedObjectsByParamset(type, el);
            });
            type.paramsets = tmp;            
        });
    };
      

 //get templates   
    $scope.getActive = function(){
        if (($scope.reportObjects == []) || (typeof $scope.reportObjects[0].suffix == 'undefined')){return;};
        for (var i=0; i<$scope.reportObjects.length; i++){
            $scope.getParamsets($scope.paramsetsUrl + $scope.reportObjects[i].suffix, $scope.reportObjects[i]);
        };
    };
    
    if (reportSvc.getReportTypesIsLoaded()){
            //report types
        $scope.reportObjects = reportSvc.getReportTypes();
        $scope.getActive();
    };

    $scope.toggleReportShowGroupDetails = function(curObject){//switch option: current goup details     
         curObject.showGroupDetails = !curObject.showGroupDetails;
//console.log(curObject.paramsets);        
    };
    
    $scope.selectedReport = function(parentItem, item){
        $scope.setCurrentReportType(parentItem);       
        var curObject = angular.copy(item);
		$scope.currentObject = curObject;
        $scope.activeStartDateFormat = (curObject.activeStartDate == null) ? null : new Date(curObject.activeStartDate);
        var activeStartDate = new Date(curObject.activeStartDate);
//console.log(curObject);         
        $scope.activeStartDateFormatted = (curObject.activeStartDate == null) ? "" : moment([activeStartDate.getUTCFullYear(),activeStartDate.getUTCMonth(), activeStartDate.getUTCDate()]).format($scope.ctrlSettings.dateFormat);
        $scope.getTemplates();       
    };
    
    $scope.checkAndRunParamset = function(type, object, previewFlag){
        var flag = $scope.checkRequiredFieldsOnSave();
        if (flag === false){
            $('#messageForUserModal').modal();
        }else{
            var previewWin = null;
            //индикация загрузки страницы предпросмотра
            var htmlText = $scope.ctrlSettings.htmlLoading;
            var previewFile = new Blob([htmlText], {type : 'text/html'});//new Blob(["temp"], "temp");//null;
            if(previewFlag){
                //window.URL= window.URL || window.webkitURL;
                var url = window.URL.createObjectURL(previewFile);//формируем url на сформированный файл

                previewWin = window.open(url, 'PreviewWin');//открываем сформированный файл в новой вкладке браузера
            };
            $scope.createReportWithParams(type, object, $scope.selectedObjects, previewFlag, previewWin);
        };
    };
    
    $scope.currentReportType = {};
    $scope.setCurrentReportType = function(object){
        $scope.currentReportType = object;
//console.log($scope.currentReportType);        
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
        var result = (element.paramSpecialType.specialTypeDirectoryUrl != null)
                && (typeof element.paramSpecialType.specialTypeDirectoryUrl != 'undefined')
        && (element.paramSpecialType.specialTypeDirectoryKey != null)
                && (typeof element.paramSpecialType.specialTypeDirectoryKey != 'undefined')
        && (element.paramSpecialType.specialTypeDirectoryCaption != null)
                && (typeof element.paramSpecialType.specialTypeDirectoryCaption != 'undefined')
        && (element.paramSpecialType.specialTypeDirectoryValue != null)
                && (typeof element.paramSpecialType.specialTypeDirectoryValue != 'undefined');       
        return result;
    };
    
    var prepareParamSpecialList = function(reportType, reportParamset){
//console.log(reportType);        
        var resultParamSpecialList = reportType.reportMetaParamSpecialList.map(function(element){
            var result = {};
            result.paramSpecialCaption = element.paramSpecialCaption;
            result.reportMetaParamSpecialId = element.id;
            result.paramSpecialRequired = element.paramSpecialRequired;
            result.paramSpecialTypeKeyname = element.paramSpecialType.keyname;
            if (isParamSpecialTypeDirectory(element))
            {
                result.specialTypeDirectoryUrl = element.paramSpecialType.specialTypeDirectoryUrl;
                result.specialTypeDirectoryKey = element.paramSpecialType.specialTypeDirectoryKey;
                result.specialTypeDirectoryCaption = element.paramSpecialType.specialTypeDirectoryCaption;
                result.specialTypeDirectoryValue = element.paramSpecialType.specialTypeDirectoryValue;
                $scope.getDirectory(".."+result.specialTypeDirectoryUrl, result);                
            };
            //Ищем значение этого параметра в массиве параметров варианта отчета
            if (reportParamset.paramSpecialList.length == 0){
                result.textValue = null;
                result.boolValue = null;
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
            reportParamset.paramSpecialList.some(function(el,index,array){
                if (el.reportMetaParamSpecialId === element.id){
                    elementIndex = index;
                    return true;
                }else{
                    return false;
                }
            });
            if (elementIndex!=-1){
                result.id = reportParamset.paramSpecialList[elementIndex].id || null;
                result.textValue = reportParamset.paramSpecialList[elementIndex].textValue || null;
                result.boolValue = reportParamset.paramSpecialList[elementIndex].boolValue || null;
                result.numericValue = reportParamset.paramSpecialList[elementIndex].numericValue || null;
                result.oneDateValue = reportParamset.paramSpecialList[elementIndex].oneDateValue || null;
                result.startDateValue = reportParamset.paramSpecialList[elementIndex].startDateValue || null;
                result.endDateValue = reportParamset.paramSpecialList[elementIndex].endDateValue || null;
                result.oneDateValueFormatted=(reportParamset.paramSpecialList[elementIndex].oneDateValue == null) ? null :new Date(reportParamset.paramSpecialList[elementIndex].oneDateValue);
                result.startDateValueFormatted=(reportParamset.paramSpecialList[elementIndex].startDateValue == null) ? null :new Date(reportParamset.paramSpecialList[elementIndex].startDateValue);
                result.endDateValueFormatted=(reportParamset.paramSpecialList[elementIndex].endDateValue == null) ? null :new Date(reportParamset.paramSpecialList[elementIndex].endDateValue);
                result.directoryValue = (reportParamset.paramSpecialList[elementIndex].directoryValue) || null;
                result.version = reportParamset.paramSpecialList[elementIndex].version || null;
            }else{
                result.id = null;
                result.textValue = null;
                result.boolValue = null;
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
        return resultParamSpecialList;
    };
    
    
    
    $scope.editParamSet = function(parentObject, object, mode, isContext){
//console.log(parentObject);        
//console.log(object);
//console.log(mode);        
        $scope.showMessageForUserModalExFlag = false;
//        $scope.setCurrentReportType(parentObject);     
        $scope.selectedReport(parentObject, object);
//console.log($scope.currentReportType);        
        $scope.currentParamSpecialList = prepareParamSpecialList($scope.currentReportType, object);

        $scope.currentObject.showParamsBeforeRunReport = !$scope.currentObject.allRequiredParamsPassed;
//console.log($scope.currentObject.allRequiredParamsPassed);         
        $scope.editParamset_flag = true;
        $scope.createByTemplate_flag = false;
        $scope.getAvailableObjects(object.id);//получаем доступные объекты для заданного парамсета        
        
        $scope.currentSign = object.reportPeriod.sign;
        if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){           
            $scope.paramsetStartDateFormat = (new Date(object.paramsetStartDate));
            $scope.psStartDateFormatted = (object.paramsetStartDate!=null) ? moment([$scope.paramsetStartDateFormat.getUTCFullYear(), $scope.paramsetStartDateFormat.getUTCMonth(), $scope.paramsetStartDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat) : "";
//console.log($scope.psStartDateFormatted);            
            $scope.paramsetEndDateFormat= (new Date(object.paramsetEndDate));
            $scope.psEndDateFormatted = (object.paramsetEndDate!=null)? moment([$scope.paramsetEndDateFormat.getUTCFullYear(), $scope.paramsetEndDateFormat.getUTCMonth(), $scope.paramsetEndDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat) : "";
//console.log($scope.psEndDateFormatted);
        }
    //settings for activate tab "Main options", when edit window opened.
        $scope.set_of_objects_flag=false;
        $scope.showAvailableObjects_flag = false;
//        $('#main_properties_tab').addClass("active");
//        $('#set_of_objects_tab').removeClass("active");
//        $('#createParamsetModal').modal();
        $scope.getSelectedObjects(mode, isContext);
    };
    
    //Account objects
    $scope.availableObjects = [];
    $scope.selectedObjects = [];
    $scope.availableObjectGroups = [];
    
    $scope.getAvailableObjects = function(paramsetId){      
        var table=$scope.paramsetsUrl + "/" + paramsetId + "/contObject/available";        
        crudGridDataFactory(table).query(function(data){
//console.log(data);            
            $scope.availableObjects = angular.copy(data);             
//console.log($scope.availableObjects);                        
        });        
    };
//    $scope.getAvailableObjects();
    $scope.getSelectedObjects = function(mode, isContext){
        var table = $scope.paramsetsUrl + "/" + $scope.currentObject.id + "/contObject";
        crudGridDataFactory(table).query(function(data){
            $scope.selectedObjects = data;
            if (!mainSvc.checkUndefinedNull(isContext) && (isContext == true)){
                $scope.selectedObjects = [objectSvc.getCurrentObject()];
            };      
            objectSvc.sortObjectsByFullName($scope.selectedObjects); 
//console.log(mode);            
            switch (mode){
                case $scope.ctrlSettings.openModes.edit :  activateMainPropertiesTab(); break;
                case $scope.ctrlSettings.openModes.create :                                                           
                    var flag = $scope.checkRequiredFieldsOnSave();
                    if (flag === false){
                        $scope.showMessageForUserModalExFlag = true;
                        $('#messageForUserModal').modal();
                    }else{                        
                        $scope.createReport($scope.currentReportType, $scope.currentObject, isContext);
                    };
                    break;    
                 case $scope.ctrlSettings.openModes.preview :  
                    var flag = $scope.checkRequiredFieldsOnSave();
                    break; 
            };
        });
    };
    
    $scope.getSelectedObjectsByParamset = function(type, paramset, isContext){
        var table = $scope.paramsetsUrl + "/" + paramset.id + "/contObject";
        crudGridDataFactory(table).query(function(data){
            (!mainSvc.checkUndefinedNull(isContext) && (isContext == true)) ? paramset.selectedObjects = [objectSvc.getCurrentObject()] : paramset.selectedObjects = data;
            objectSvc.sortObjectsByFullName(paramset.selectedObjects);
            paramset.currentParamSpecialList = prepareParamSpecialList(type, paramset);
            var tmpCheck = reportSvc.checkPSRequiredFieldsOnSave(type, paramset, $scope.currentSign, "run"); //$scope.checkPSRequiredFieldsOnSave(type, paramset);
            paramset.checkFlag = tmpCheck.flag;
            paramset.messageForUser = tmpCheck.message;
            type.checkedParamsetsCount += 1;
        });
    };
    
    $scope.prepareObjectsList = function(){
        $scope.availableObjectGroups.forEach(function(el){el.selected = false});
        $scope.ctrlSettings.selectedAll = false;
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
            $scope.availableObjects = objectSvc.sortObjectsByFullNameEx($scope.availableObjects);
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
    
    $scope.selectAllAvailableEntities = function(){      
        for (var index = 0; index<$scope.availableEntities.length; index++){         
            $scope.availableEntities[index].selected = $scope.ctrlSettings.selectedAll;
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
        if (!angular.isArray($scope.reportPeriods)){
            return "reportPeriods is no array.";
        };
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
        //check date interval
    $scope.checkDateInterval = function(left, right){     
        if (!mainSvc.checkStrForDate(left)){
            return false;
        };
        if (!mainSvc.checkStrForDate(right)){
            return false;
        };
        if ((left==null)|| (right==null)||(left=="")||(right=="")){console.log("1");return false;};
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
            var startDateMillisec = mainSvc.strDateToUTC($scope.psStartDateFormatted, $scope.ctrlSettings.dateFormat);
            var startDate = new Date(startDateMillisec);
            var endDateMillisec = mainSvc.strDateToUTC($scope.psEndDateFormatted, $scope.ctrlSettings.dateFormat);
            var endDate = new Date(endDateMillisec);            
            intervalValidate_flag = (!isNaN(startDate.getTime()))&&(!isNaN(endDate.getTime()))&&$scope.checkDateInterval($scope.psStartDateFormatted, $scope.psEndDateFormatted);
        };
//console.log(intervalValidate_flag);        
        result = !((($scope.currentObject.reportPeriodKey==null) ||   
        ($scope.currentObject.reportTemplate.id==null)))
        &&intervalValidate_flag;
//        $scope.currentObject.allRequiredParamsPassed = !result;      
        return result;
        
    };
    
    //for the paramset
    $scope.checkPSRequiredFields = function(paramset){
        var result;
        if (!(paramset.hasOwnProperty('reportPeriodKey'))||!(paramset.hasOwnProperty('reportTemplate'))){
            return false;
        };  
        //interval validate flag
            //default value    
        var intervalValidate_flag = true; 
            //if the paramset use interval
        if ($scope.currentSign==null){
                //check interval
            var startDateMillisec = mainSvc.strDateToUTC(paramset.psStartDateFormatted, $scope.ctrlSettings.dateFormat);
            var startDate = new Date(startDateMillisec);
            var endDateMillisec = mainSvc.strDateToUTC(paramset.psEndDateFormatted, $scope.ctrlSettings.dateFormat);
            var endDate = new Date(endDateMillisec);            
            intervalValidate_flag = (!isNaN(startDate.getTime()))&&(!isNaN(endDate.getTime()))&&$scope.checkDateInterval(paramset.psStartDateFormatted, paramset.psEndDateFormatted);
        };
//console.log(intervalValidate_flag);        
        result = !(((paramset.reportPeriodKey==null) ||   
        (paramset.reportTemplate.id==null)))
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
        $scope.currentObject.psStartDateFormatted = $scope.psStartDateFormatted;
        $scope.currentObject.psEndDateFormatted = $scope.psEndDateFormatted;
        $scope.currentObject.selectedObjects = $scope.selectedObjects;
        $scope.currentObject.currentParamSpecialList = $scope.currentParamSpecialList;
        var checkRes = reportSvc.checkPSRequiredFieldsOnSave($scope.currentReportType, $scope.currentObject, $scope.currentSign, "run");
        $scope.messageForUser = checkRes.message;
        return checkRes.flag;
        
////console.log($scope.currentReportType.reportTypeName);        
//        if (!$scope.currentReportType.hasOwnProperty('reportMetaParamCommon')){
//            return true;
//        };
//        var result= true;
//        $scope.messageForUser = "Не все параметры варианта отчета заданы:\n";
//        //Check common params before save
//            //file ext
//        if (angular.isUndefined($scope.currentObject.outputFileType)||($scope.currentObject.outputFileType===null)||($scope.currentObject.outputFileType==="")){
//            $scope.messageForUser += "Основные свойства: "+"\n";
//            $scope.messageForUser += "\u2022"+" Не задан тип файла"+"\n";
//            result= false;
//        };
//        
//            //one date - for future
////        if ($scope.currentReportType.reportMetaParamCommon.oneDateRequired && something)
//        
//            //start date
//            //if the paramset use a date interval
//        if ($scope.currentSign==null){
////            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && $scope.paramsetStartDateFormat==null)
////            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && $scope.psStartDateFormatted=="")    
////            {
////                $scope.messageForUser += "- Не задано начало периода"+"\n";
////            };
//            var startDateMillisec = mainSvc.strDateToUTC($scope.psStartDateFormatted, $scope.ctrlSettings.dateFormat);
//            var startDate = new Date(startDateMillisec);
//            var endDateMillisec = mainSvc.strDateToUTC($scope.psEndDateFormatted, $scope.ctrlSettings.dateFormat);
//            var endDate = new Date(endDateMillisec); 
//            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && (isNaN(startDate.getTime())||(!mainSvc.checkStrForDate($scope.psStartDateFormatted))))    
//            {
//                if (result){$scope.messageForUser += "Основные свойства: "+"\n";};
//                $scope.messageForUser += "\u2022"+" Некорректно задано начало периода"+"\n";
//                result= false;
//            };
//
//            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && (isNaN(endDate.getTime())||(!mainSvc.checkStrForDate($scope.psEndDateFormatted))))    
//            {
//                if (result){$scope.messageForUser += "Основные свойства: "+"\n";};
//                $scope.messageForUser += "\u2022"+" Некорректно задан конец периода"+"\n";
//                result= false;
//            };
//            
//            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && !isNaN(endDate.getTime())&& !isNaN(startDate.getTime())&&(startDateMillisec>endDateMillisec))    
//            {
//                if (result){$scope.messageForUser += "Основные свойства: "+"\n";};
//                $scope.messageForUser += "\u2022"+" Некорректно заданы границы периода"+"\n";
//                result= false;
//            };
//        }
//
//                    //Count of objects
//        if ($scope.currentReportType.reportMetaParamCommon.oneContObjectRequired && ($scope.selectedObjects.length==0) && $scope.currentReportType.reportMetaParamCommon.manyContObjectRequired)
//        {
//            $scope.messageForUser += "\u2022"+" Должен быть выбран хотя бы один объект"+"\n";
//            result= false;
//        };
//        if ($scope.currentReportType.reportMetaParamCommon.oneContObjectRequired && ($scope.selectedObjects.length==0) && !$scope.currentReportType.reportMetaParamCommon.manyContObjectRequired)
//        {
//            $scope.messageForUser += "\u2022"+" Необходимо выбрать один объект"+"\n";
//            result= false;
//        };
//        if ($scope.currentReportType.reportMetaParamCommon.manyContObjectRequired && ($scope.selectedObjects.length<=0))
//        {
//            $scope.messageForUser += "\u2022"+" Необходимо выбрать несколько объектов"+"\n";
//            result= false;
//        };
//        
//        if (!$scope.currentReportType.reportMetaParamCommon.manyContObjectRequired && ($scope.selectedObjects.length>1) &&  $scope.currentReportType.reportMetaParamCommon.oneContObjectRequired)
//        {
//            $scope.messageForUser += "\u2022"+" Нельзя выбрать более одного объекта"+"\n";
//            result= false;
//        };
//        
//        if ($scope.currentReportType.reportMetaParamCommon.manyContObjectsZipOnly && ($scope.selectedObjects.length>1))
//        {
//            $scope.currentObject.outputFileZipped =  true;
//        };
//        //check special properties
//        var specListFlag = true;
//        $scope.currentParamSpecialList.forEach(function(element, index, array){
//            if (element.paramSpecialRequired && !(element.textValue 
//                                                 || element.numericValue 
//                                                 || element.oneDateValue 
//                                                 || element.startDateValue
//                                                 || element.endDateValue
//                                                 || element.directoryValue)
//               )
//            {
//                if (specListFlag){$scope.messageForUser += "Дополнительные свойства: "+"\n";};
//                $scope.messageForUser += "\u2022"+" Не задан параметр \""+element.paramSpecialCaption+"\" \n";
//                result= false;
//                specListFlag = false;
//            }
//        });
//        if($scope.messageForUser!="Не все параметры варианта отчета заданы:\n"){
////            $scope.messageForUser = "Внимание!!! \n"+$scope.messageForUser;
////            $scope.messageForUser += "\n Этот вариант отчета нельзя запустить без уточнения или использовать в рассылке, не задав обязательных параметров. Продолжить?";
////            $scope.messageForUser+=" Этот вариант отчета нельзя запустить без уточнения, а также его нельзя использовать в рассылке. Продолжить?";
////            alert($scope.messageForUser);
////            $('#messageForUserModal').modal();
//            result= false;
//        };
//        result =result && $scope.checkRequiredFields();  
////        $scope.currentObject.showParamsBeforeRunReport =!result;
//        if (!result){
////console.log("1");            
//            $scope.currentObject.showParamsBeforeRunReport = true;
//        };
////        else{
////            $scope.currentObject.showParamsBeforeRunReport = false;
////        };
//        return result;
    };
    
        //universal check fields before save
//    $scope.checkPSRequiredFieldsOnSave = function(reportType, reportParamset){        
//        if (!reportType.hasOwnProperty('reportMetaParamCommon')){
//            return true;
//        };
//        var result= true;
//        var messageForUser = "Не все параметры варианта отчета заданы:\n";
//        //Check common params before save
//            //file ext
//        if (angular.isUndefined(reportParamset.outputFileType)||(reportParamset.outputFileType===null)||(reportParamset.outputFileType==="")){
//            messageForUser += "Основные свойства: "+"\n";
//            messageForUser += "\u2022"+" Не задан тип файла"+"\n";
//            result= false;
//        };
//            //start date
//            //if the paramset use a date interval
//        if ($scope.currentSign==null){
//            var startDateMillisec = mainSvc.strDateToUTC(reportParamset.psStartDateFormatted, $scope.ctrlSettings.dateFormat);
//            var startDate = new Date(startDateMillisec);
//            var endDateMillisec = mainSvc.strDateToUTC(reportParamset.psEndDateFormatted, $scope.ctrlSettings.dateFormat);
//            var endDate = new Date(endDateMillisec); 
//            if (reportType.reportMetaParamCommon.startDateRequired && (isNaN(startDate.getTime())||(!mainSvc.checkStrForDate(reportParamset.psStartDateFormatted))))    
//            {
//                if (result){messageForUser += "Основные свойства: "+"\n";};
//                messageForUser += "\u2022"+" Некорректно задано начало периода"+"\n";
//                result= false;
//            };
//
//            if (reportType.reportMetaParamCommon.startDateRequired && (isNaN(endDate.getTime())||(!mainSvc.checkStrForDate(reportParamset.psEndDateFormatted))))    
//            {
//                if (result){messageForUser += "Основные свойства: "+"\n";};
//                messageForUser += "\u2022"+" Некорректно задан конец периода"+"\n";
//                result= false;
//            };
//            
//            if (reportType.reportMetaParamCommon.startDateRequired && !isNaN(endDate.getTime())&& !isNaN(startDate.getTime())&&(startDateMillisec>endDateMillisec))    
//            {
//                if (result){messageForUser += "Основные свойства: "+"\n";};
//                messageForUser += "\u2022"+" Некорректно заданы границы периода"+"\n";
//                result= false;
//            };
//        }
//
//                    //Count of objects
//        if (reportType.reportMetaParamCommon.oneContObjectRequired && (reportParamset.selectedObjects.length==0) && reportType.reportMetaParamCommon.manyContObjectRequired)
//        {
//            messageForUser += "\u2022"+" Должен быть выбран хотя бы один объект"+"\n";
//            result= false;
//        };
//        if (reportType.reportMetaParamCommon.oneContObjectRequired && (reportParamset.selectedObjects.length==0) && !reportType.reportMetaParamCommon.manyContObjectRequired)
//        {
//            messageForUser += "\u2022"+" Необходимо выбрать один объект"+"\n";
//            result= false;
//        };
//        if (reportType.reportMetaParamCommon.manyContObjectRequired && (reportParamset.selectedObjects.length<=0))
//        {
//            messageForUser += "\u2022"+" Необходимо выбрать несколько объектов"+"\n";
//            result= false;
//        };
//        
//        if (!reportType.reportMetaParamCommon.manyContObjectRequired && (reportParamset.selectedObjects.length>1) &&  reportType.reportMetaParamCommon.oneContObjectRequired)
//        {
//            messageForUser += "\u2022"+" Нельзя выбрать более одного объекта"+"\n";
//            result= false;
//        };
//        
//        if (reportType.reportMetaParamCommon.manyContObjectsZipOnly && (reportParamset.selectedObjects.length>1))
//        {
//            reportParamset.outputFileZipped =  true;
//        };
//        //check special properties
//        var specListFlag = true;
//        reportParamset.currentParamSpecialList.forEach(function(element, index, array){
//            if (element.paramSpecialRequired && !(element.textValue 
//                                                 || element.numericValue 
//                                                 || element.oneDateValue 
//                                                 || element.startDateValue
//                                                 || element.endDateValue
//                                                 || element.directoryValue)
//               )
//            {
//                if (specListFlag){messageForUser += "Дополнительные свойства: "+"\n";};
//                messageForUser += "\u2022"+" Не задан параметр \""+element.paramSpecialCaption+"\" \n";
//                result= false;
//                specListFlag = false;
//            }
//        });
//        if(messageForUser!="Не все параметры варианта отчета заданы:\n"){
//            result= false;
//        };
//        result =result && $scope.checkPSRequiredFields(reportParamset);  
//        if (!result){          
//            reportParamset.showParamsBeforeRunReport = true;
//        };
////        reportParamset.messageForUser = messageForUser;
//        return {"flag":result,
//                "message": messageForUser};
//    };
    
    $scope.createReport = function(type, paramset, isContext){
        //run report
        var url = "../api/reportService" + type.suffix + "/" + paramset.id;
        if (!mainSvc.checkUndefinedNull(isContext) && (isContext == true) && !mainSvc.checkUndefinedNull(objectSvc.getCurrentObject())){
            url += "/context" + "/" + objectSvc.getCurrentObject().id; 
        }else{
            url += "/download";
        };
        $http.get(url, {responseType: 'arraybuffer'})
            .then(function(response) {        
                var fileName = response.headers()['content-disposition'];           
                fileName = fileName.substr(fileName.indexOf('=') + 2, fileName.length - fileName.indexOf('=') - 3);
                var file = new Blob([response.data], { type: response.headers()['content-type'] });          
                if ((file.type.indexOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") > -1)){
                    fileName += ".xlsx";
                }else 
                if (file.type.indexOf('application/zip') > -1){
                    fileName += ".zip";
                } else
                if (file.type.indexOf('application/pdf') > -1){
                    fileName += ".pdf";
                } else
                if (file.type.indexOf('text/html') > -1){
                    fileName += ".html";
                };              
                saveAs(file, fileName);
                $scope.ctrlSettings.openModes.create.isContext = false;//reset context flag
            }, errorCallback)
            .catch(errorCallback);    
    };
    
    $scope.previewReport = function(type, paramset, isContext){
        $scope.selectedReport(type, paramset);
        if (paramset.checkFlag){
            var url = "../api/reportService" + type.suffix + "/" + paramset.id;
            (!mainSvc.checkUndefinedNull(isContext) && isContext == true) ? url += "/contextPreview/" + objectSvc.getCurrentObject().id : url += "/preview";
            var prevWin = window.open("", "_blank");
            prevWin.document.write($scope.ctrlSettings.htmlLoading);      
            prevWin.document.location.assign(url);            
        }else{
            $scope.showMessageForUserModalExFlag = true;
            $scope.messageForUser = paramset.messageForUser;
            $('#messageForUserModal').modal();
        };
    };  
    //Формируем отчет с заданными параметрами
    $scope.createReportWithParams = function(type // тип отчета
                                            , paramset //вариант отчета
                                            , selectedObjects // массив объектов, для которых строится отчет
                                            , previewFlag //флаг - формировать отчет или сделать предпросмотр
                                            , previewWin //ссылка на превью окно
                                            ){
        var tmpParamset = angular.copy(paramset);//делаем копию варианта отчета
        //формируем массив ИД объектов, для которых формируется отчет.          
        var objectIds = selectedObjects.map(function(element){          
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
            tmpParamset.outputFileType = "HTML";//ставим формат выходного файла - HTML
            tmpParamset.outputFileZipped = false;//ставим флаг -не архивировать полученный отчет
            fileExt = "html";
        }else{
            fileExt = tmpParamset.outputFileZipped ? "zip" : tmpParamset.outputFileType.toLowerCase();
        }
        var url = "../api/reportService" + type.suffix + "/" + tmpParamset.id + "/download";  //формируем url адрес запроса
        var responseType = "arraybuffer";//указываем тип ответа от сервера
        //делаем запрос на сервер
//        $http.put(url, paramset, { contObjectIds: objectIds }, {responseType: responseType})
        var clearContObjectIds = false; //the clear selected paramset objects flag           
            if (mainSvc.checkUndefinedNull(objectIds) || objectIds.length == 0){                
                clearContObjectIds = true;
        };
        $http({
            url: url, 
            method: "PUT",
            params: { contObjectIds: objectIds, clearContObjectIds: clearContObjectIds},
            data: tmpParamset,
            responseType: responseType
        })
        .then(function(response) {
           //обрабатываем полученный результат запроса
            var fileName = response.headers()['content-disposition']; //читаем кусок заголовка, в котором пришло название файла
            fileName = fileName.substr(fileName.indexOf('=') + 2, fileName.length - fileName.indexOf('=') - 3);//вытаскиваем непосредственно название файла.
            var file = new Blob([response.data], { type: response.headers()['content-type']/* тип файла тоже приходит в заголовке ответа от сервера*/ });//формируем файл из полученного массива байт        
            if (previewFlag){              
                //если нажат предпросмотр, то
                var url = window.URL.createObjectURL(file);//формируем url на сформированный файл
                window.open(url, 'PreviewWin');//открываем сформированный файл в новой вкладке браузера
                //previewWin = window.URL.createObjectURL(file);
            }else{  
                //create file extension
                if ((file.type.indexOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") > -1)){
                    fileName += ".xlsx";
                }else 
                if (file.type.indexOf('application/zip') > -1){
                    fileName += ".zip";
                } else
                if (file.type.indexOf('application/pdf') > -1){
                    fileName += ".pdf";
                } else
                if (file.type.indexOf('text/html') > -1){
                    fileName += ".html";
                };                   
                saveAs(file, fileName);//если нужен отчет, то сохраняем файл на диск клиента
            };
            $scope.ctrlSettings.openModes.create.isContext = false;//reset context flag
        }, errorCallback)
        .catch(errorCallback);
    };

        //work with tabs
    $scope.setActiveTab = function(tabId){
        var tab = document.getElementById('rep_a_teplo_sys');     
        tab.classList.remove("active");
        var tab = document.getElementById('rep_a_electro_sys');     
        tab.classList.remove("active");
        var tab = document.getElementById('rep_a_gas_sys');     
        tab.classList.remove("active");
        var tab = document.getElementById(tabId);     
        tab.classList.add("active");
        
    };
    
    $scope.selectedCategory = function(cat){
        $scope.curCategory = angular.copy(cat);
    };
    
    // *************************************************************************************
    // Отчеты для контекстного меню объект
    //**************************************************************************************
    
    $scope.data = {};
    //получить доступные варианты отчетов
    $scope.getContextReports = function(){
        reportSvc.loadReportsContextLaunch().then(successGetContextReports, errorCallback);
    };
    //Если вариантов отчетов больше $scope.ctrlSettings.reportCountList, то распределить варианты отчетов по категориям
    var successGetContextReports = function(resp){
        var reports = angular.copy(resp.data);
        if (reports.length > $scope.ctrlSettings.reportCountList){
            $scope.data.reportEntities = angular.copy($scope.categories);
            $scope.data.reportEntities.forEach(function(elem){elem.reports = []});
            reports.forEach(function(rep){
                for (var categoryCounter = 0; categoryCounter < $scope.data.reportEntities.length; categoryCounter++){
                    if (rep.reportTemplate.reportType.reportCategory.localeCompare($scope.data.reportEntities[categoryCounter].name) == 0){
                        $scope.data.reportEntities[categoryCounter].reports.push(rep);
                        break;
                    };
                };
            });
        }else{
            $scope.data.reportEntities = reports;
        };
            // perform reports as common reports
        var tmp = reports;
        tmp.forEach(function(el){
            var currentSign = el.reportPeriod.sign;
            if ((currentSign == null) || (typeof currentSign == 'undefined')){           
                var paramsetStartDateFormat = (new Date(el.paramsetStartDate));
                el.psStartDateFormatted = (el.paramsetStartDate != null) ? moment([paramsetStartDateFormat.getUTCFullYear(), paramsetStartDateFormat.getUTCMonth(), paramsetStartDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat) : "";
                var paramsetEndDateFormat = (new Date(el.paramsetEndDate));
                el.psEndDateFormatted = (el.paramsetEndDate != null) ? moment([paramsetEndDateFormat.getUTCFullYear(), paramsetEndDateFormat.getUTCMonth(), paramsetEndDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat) : "";
            }
        //settings for activate tab "Main options", when edit window opened.
            $scope.set_of_objects_flag = false;
            $scope.showAvailableObjects_flag = false;
            $scope.getSelectedObjectsByParamset(el.reportTemplate.reportType, el, true);
        });
//console.log($scope.data.reportEntities);        
    };
    $scope.getContextReports();
    $scope.createContextReport = function(paramset){
        if (!mainSvc.checkUndefinedNull(paramset.reports)){//If parametr "paramset" is not paramset, but it is category
            return "Entity is category";//exit function
        };
        //run report
        $scope.ctrlSettings.openModes.create.isContext = true;//set context flag
        paramset.reportTemplate.reportType.reportMetaParamSpecialList_flag = (paramset.reportTemplate.reportType.reportMetaParamSpecialList.length > 0 ? true : false);                
        $scope.editParamSet(paramset.reportTemplate.reportType, paramset, $scope.ctrlSettings.openModes.create, true)
    };    
    
    $scope.previewContextReport = function(paramset){
//console.log(paramset);        
        if (!mainSvc.checkUndefinedNull(paramset.reports)){//If parametr "paramset" is not paramset, but it is category
            return "Entity is category";//exit function
        };
        if (paramset.reportTemplate.reportType.keyname == 'COMMERCE_REPORT' || paramset.reportTemplate.reportType.keyname == 'COMMERCE_REPORT_M_V'){
            notificationFactory.errorInfo("Внимание!", "Предварительный просмотр для коммерческих отчетов невозможен.");
            return "Preview for the commercial reports is not available.";
        };
        //run preview report        
        paramset.reportTemplate.reportType.reportMetaParamSpecialList_flag = (paramset.reportTemplate.reportType.reportMetaParamSpecialList.length > 0 ? true : false);
        $scope.selectedObjects = [objectSvc.getCurrentObject()];
        $scope.previewReport(paramset.reportTemplate.reportType, paramset, true)
    };
    // ************************************************************* контекстные отчеты ****************
}]);