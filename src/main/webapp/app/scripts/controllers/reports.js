//reports controller
var app = angular.module('portalNMC');
app.controller('ReportsCtrl',['$scope', '$rootScope', '$http', 'crudGridDataFactory', 'notificationFactory', 'objectSvc', function($scope, $rootScope, $http, crudGridDataFactory, notificationFactory, objectSvc){
                     
    $scope.set_of_objects_flag = false; //флаг: истина - открыта вкладка с объектами
    $scope.showAvailableObjects_flag = false; // флаг, устанавливающий видимость окна с доступными объектами
    $scope.currentSign = 9999;// устанавливаем начальное значение отличное от нулл и других возможных значение; нулл - будем отлавливать

    $scope.extraProps={"idColumnName":"id", "defaultOrderBy" : "name", "deleteConfirmationProp":"name"};    
    $scope.activeStartDateFormat = new Date();
    
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
    $scope.crudTableName = "../api/reportParamset"; 
    
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
console.log(curObject.paramsets);        
    };
    
    $scope.selectedItem = function(parentItem, item){
        $scope.setCurrentReportType(parentItem);       
        var curObject = angular.copy(item);
		$scope.currentObject = curObject;
        $scope.activeStartDateFormat = (curObject.activeStartDate == null) ? null : new Date(curObject.activeStartDate);
        
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
            $scope.paramsetEndDateFormat= (new Date(object.paramsetEndDate));
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
    
        $scope.removeSelectedObject = function(object){
        $scope.availableObjects.push(object);
        $scope.selectedObjects.splice($scope.selectedObjects.indexOf(object), 1);
        objectSvc.sortObjectsByFullName($scope.availableObjects);
    }
    
    $scope.addSelectedObjects = function(){
    //console.log($scope.availableObjects);          
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
    }
    
    //checkers
        //check date interval
    $scope.checkDateInterval = function(left, right){
        if ((left==null)|| (right==null)){return false;};
        return right>=left;
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
            intervalValidate_flag = $scope.checkDateInterval($scope.paramsetStartDateFormat, $scope.paramsetEndDateFormat);
        };
        
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
            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && $scope.paramsetStartDateFormat==null)
            {
                $scope.messageForUser += "- Не задано начало периода"+"\n";
            };
                        //end date
            if ($scope.currentReportType.reportMetaParamCommon.endDateRequired && $scope.paramsetEndDateFormat==null)
            {
                $scope.messageForUser += "- Не задан конец периода"+"\n";
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
//        window.open(url);
        
    };
    
    $scope.previewReport = function(type,paramset){
        var url ="../api/reportService"+type.suffix+"/"+paramset.id+"/preview";
        window.open(url);    
    };
    
    $scope.createReportWithParams = function(type, paramset, previewFlag){
        var tmpParamset = angular.copy(paramset);
        var objectIds = $scope.selectedObjects.map(function(element){
            var result = element.id;
            return result;
        }); 
         //set the list of the special params
        tmpParamset.paramSpecialList = $scope.currentParamSpecialList;
        //
        if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){
            tmpParamset.paramsetStartDate = (new Date($scope.paramsetStartDateFormat)) /*(new Date($rootScope.reportStart))*/ || null;
            tmpParamset.paramsetEndDate = (new Date($scope.paramsetEndDateFormat)) /*(new Date($rootScope.reportEnd))*/ || null;
        }else{
            tmpParamset.paramsetStartDate = null;
            tmpParamset.paramsetEndDate = null;
        }

//console.log(paramset);        
        var fileExt = "";
        if (previewFlag){
            tmpParamset.outputFileType="HTML";
            tmpParamset.outputFileZipped=false;
            fileExt = "html";
        }else{
            fileExt=tmpParamset.outputFileZipped?"zip":tmpParamset.outputFileType.toLowerCase();
        }
        var url ="../api/reportService"+type.suffix+"/"+tmpParamset.id+"/download";  
        var responseType = "arraybuffer";
//        $http.put(url, paramset, { contObjectIds: objectIds }, {responseType: responseType})
        $http({
            url: url, 
            method: "PUT",
            params: { contObjectIds: objectIds },
            data: tmpParamset,
            responseType: responseType
        })
        .then(function(response) {
           
            var fileName = response.headers()['content-disposition']; 
            fileName = fileName.substr(fileName.indexOf('=') + 2, fileName.length-fileName.indexOf('=')-3);
            var file = new Blob([response.data], { type: response.headers()['content-type'] });            
            if (previewFlag){                
                var url = window.URL.createObjectURL(file);
                window.open(url);
            }else{    
                saveAs(file,fileName);
            };
        })
        .catch(function(e){
            notificationFactory.errorInfo(e.statusText,e);
        });
    };
}]);