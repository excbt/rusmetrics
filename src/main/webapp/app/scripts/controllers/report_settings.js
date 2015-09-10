'use strict';
var app = angular.module('portalNMC');

app.controller('ReportSettingsCtrl',['$scope', '$rootScope', '$resource', 'crudGridDataFactory', 'notificationFactory', 'mainSvc', function($scope, $rootScope, $resource,crudGridDataFactory, notificationFactory, mainSvc){
    
    //ctrl settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.dateFormat = "DD.MM.YYYY"; //date format
    
    $scope.isPositionSystemChanged = false;
    $scope.active_tab_active_templates = true;
    $scope.currentObject = {};
    $scope.createByTemplate_flag = false;
    $scope.archiveTemplate = {};
    $scope.activeStartDateFormat = new Date();
    $scope.activeStartDateFormatted = moment().format($scope.ctrlSettings.dateFormat);
    $scope.currentReportType = {};  
    $scope.objects = [];   
    $scope.columns = [
        {"name":"reportTypeName","header":"Тип отчета", "class":"col-md-11"}
    ];   
    $scope.crudTableName = "../api/reportTemplate"; 
    
    $scope.extraProps={"idColumnName":"id", "defaultOrderBy" : "name", "deleteConfirmationProp":"name"};    
    
    $scope.isSystemuser = function(){
        $scope.userInfo = $rootScope.userInfo;
        return $scope.userInfo._system;
    };
    
    $scope.reportTypes = [];
    $scope.getReportTypes = function(){
        var table = "../api/reportSettings/reportType";
        crudGridDataFactory(table).query(function(data){
            $scope.reportTypes = data;
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
                
                newObjects.push(newObject);
            };           
            $scope.objects = newObjects;
console.log($scope.reportTypes);            
            $scope.getActive();
        });
    };
    $scope.getReportTypes();    

    $scope.oldColumns = [
        {"name":"name", "header":"Название шаблона", "class":"col-md-5"}
        ,{"name":"activeStartDate", "header":"Действует с", "class":"col-md-2"}
    ];
   
    var successCallback = function (e) {      
        notificationFactory.success();
        $('#editTemplateModal').modal('hide');
        $('#moveToArchiveModal').modal('hide');
        $('#createTemplateModal').modal('hide');
        if (!$scope.createByTemplate_flag){
            $scope.getActive();
        };
        $scope.setDefault();
    };
    
    var successDeleteCallback = function (e) {     
        notificationFactory.success();
        
        $('#deleteObjectModal').modal('hide');
        $scope.getArchive();
        $scope.setDefault();
        
    };

    var errorCallback = function (e) {      
        notificationFactory.errorInfo(e.statusText,e.data.description);       
    };
    
    $scope.selectToDelete = function(parent, object){
        $scope.setCurrentReportType(parent);
        $scope.selectedItem(object);
    };
    
    $scope.deleteObject = function (object) {
        var table = $scope.crudTableName +"/archive"+ $scope.currentReportType.suffix;
        crudGridDataFactory(table).delete({ id: object[$scope.extraProps.idColumnName] }, successDeleteCallback, errorCallback);
    };
       
    $scope.getTemplates = function(table, type){
        crudGridDataFactory(table).query(function (data) {
            type.templates = data;
            type.templatesCount = data.length;
        });
    };  
    
 //get templates   
    $scope.getActive = function(){
        if (($scope.objects == []) || (typeof $scope.objects[0].suffix == 'undefined')){return;};
        for (var i=0; i<$scope.objects.length; i++){
            $scope.getTemplates($scope.crudTableName+$scope.objects[i].suffix, $scope.objects[i]);
        };
    };
    
    $scope.getArchive = function(){
        if (($scope.objects == []) || (typeof $scope.objects[0].suffix == 'undefined')){return;};
        for (var i=0; i<$scope.objects.length; i++){
            $scope.getTemplates($scope.crudTableName+"/archive"+$scope.objects[i].suffix, $scope.objects[i]);
        };
    };
        
    $scope.toogleShowGroupDetails = function(curObject){//switch option: current goup details
                    curObject.showGroupDetails = !curObject.showGroupDetails;
    };
    
   
    $scope.selectedItem = function(item){       
        var curObject = angular.copy(item);
		$scope.currentObject = curObject;       
    };
    
    $scope.updateTemplate = function(object){
        var table = "";
        if ($scope.createByTemplate_flag){
            object.activeStartDate = $scope.activeStartDateFormat==null?null:$scope.activeStartDateFormat.getTime();
            var activeStartDate = new Date(object.activeStartDate);
console.log(curObject);        
        $scope.activeStartDateFormatted = (object.activeStartDate == null) ? "" : moment([activeStartDate.getUTCFullYear(),activeStartDate.getUTCMonth(), activeStartDate.getUTCDate()]).format($scope.ctrlSettings.dateFormat);
            table = $scope.crudTableName+"/createByTemplate/"+$scope.archiveTemplate.id;    
            crudGridDataFactory(table).save({}, object, successCallback, errorCallback);
            return;
        };
        
        table =$scope.crudTableName+$scope.currentReportType.suffix;
        crudGridDataFactory(table).update({reportTemplateId: object.id}, object, successCallback, errorCallback);
    };
    
    $scope.saveTemplate = function(){
        var result = {};
        result.reportTemplate = $scope.currentObject;
//        result.reportTemplate.activeStartDate = $scope.activeStartDateFormat==null?null:$scope.activeStartDateFormat.getTime();
        var astDate = (new Date($scope.activeStartDateFormatted));                    
        var UTCastdt = Date.UTC(astDate.getFullYear(), astDate.getMonth(), astDate.getDate()); 
        result.reportTemplate.activeStartDate = (!mainSvc.checkStrForDate($scope.activeStartDateFormatted))?null:UTCastdt.getTime();
//        result.reportTemplate.activeStartDate = $scope.activeStartDateFormat==null?null:$scope.activeStartDateFormat.getTime();
        
        result.reportTemplate._active = true;
        result.reportColumnSettings = {};
        result.reportColumnSettings.allTsList = $scope.systems;
        if ($scope.isPositionSystemChanged){
            result.reportColumnSettings.ts1List = $scope.system2.defineColumns;
            result.reportColumnSettings.ts2List = $scope.system1.defineColumns;
        }else{
            result.reportColumnSettings.ts1List = $scope.system1.defineColumns;
            result.reportColumnSettings.ts2List = $scope.system2.defineColumns;
        };             
        var table = "../api/reportWizard"+$scope.currentReportType.suffix;
        crudGridDataFactory(table).save(result, successCallback, errorCallback);        
    };
                         
    $scope.toArchive = function(url, id) {
        return $resource(url, {
            }, {
                update: {method: 'PUT', params:{reportTemplateId:id}}
            });
    };
    
    $scope.moveToArchive = function(object){
        var table = $scope.crudTableName+"/archive/move"  
        $scope.toArchive(table, object.id).update({}, object, successCallback, errorCallback);
    };
    
    $scope.createByTemplate =  function(parentObject,object){
        $scope.setCurrentReportType(parentObject);
        $scope.createByTemplate_flag = true;
        $scope.archiveTemplate = {};
        $scope.archiveTemplate.id = object.id;
        $scope.archiveTemplate.name = object.name;
        $scope.currentObject = {};
        $scope.currentObject.name = angular.copy(object.name);
        $scope.currentObject.description = angular.copy(object.description);
    };
       
    //for template designer
    $scope.systems = [];
    $scope.system1 ={} ;
    $scope.system2 ={} ;
    $scope.systems = [$scope.system1, $scope.system2];
    
    $scope.getWizard = function(){
        var table = "../api/reportWizard/columnSettings"+$scope.currentReportType.suffix;///commerce";
        crudGridDataFactory(table).get(function(data){         
            $scope.obtainedSystems = data;
            $scope.systems = data.allTsList;
            $scope.system1 = data.allTsList[0];
            $scope.system2 = data.allTsList[1];
            $scope.system1.defaultColumns = data.ts1List;
            $scope.system2.defaultColumns = data.ts2List;
            $scope.system1.defineColumns = [];
            $scope.system2.defineColumns = [];

        });
    };
    
    $scope.addColumns= function(defaultColumns, defineColumns){
        var result = defineColumns;
        var colSelected = 0;      
        for (var i =0; i<defaultColumns.length; i++)
        {
            if (defaultColumns[i].selected){  
                defaultColumns[i].class="";
                defaultColumns[i].selected = false;
                if (typeof defineColumns != 'undefined'){
                    var flagElementAlreadyAdded = false;
                    for (var j=0; j<=defineColumns.length; j++){
                        if (typeof defineColumns[j] != 'undefined' && (defineColumns[j].columnNumber == defaultColumns[i].columnNumber)){
                            flagElementAlreadyAdded = true;
                        };
                    }
                
                    if (flagElementAlreadyAdded){continue;};
                }
                     
                var elem = angular.copy(defaultColumns[i]);
                elem.selected = false;
                elem.class = "";
                result.push(elem);
                colSelected=colSelected+1;                
            };
        }
             
        return result;
    };
    
    $scope.removeColumns= function(defineColumns){
        var result= [];
        for (var i =0; i<defineColumns.length; i++)
        {
            if (!defineColumns[i].selected){
                defineColumns[i].selected = false;
                result.push(defineColumns[i]);
            };

        }
        return result;
    };

    $scope.moveColumnsUp= function(defineColumns){
        var tmp={};
        for (var i =1; i<defineColumns.length; i++)
        {
            if (defineColumns[i].selected){
                tmp = defineColumns[i-1];
                defineColumns[i-1] = defineColumns[i];
                defineColumns[i] = tmp;
            };

        }
        return defineColumns;
    };

    $scope.moveColumnsDown= function(defineColumns){
         var tmp={};

        for (var i =defineColumns.length-2; i>=0; i--)
        {
            if (defineColumns[i].selected){
                tmp = defineColumns[i+1];
                defineColumns[i+1] = defineColumns[i];
                defineColumns[i] = tmp;
            };

        }
        return defineColumns;
    };
    
    $scope.changeSystemPosition = function(){
        $scope.isPositionSystemChanged = !$scope.isPositionSystemChanged;
       var tmp = $scope.systems[0]; 
        $scope.systems[0] = $scope.systems[1]; 
        $scope.systems[1] = tmp; 
    }; 
    
    $scope.selectColumn = function(item){
        item.selected = !item.selected;
        item.class= item.selected?"active":"";
    };
    
    $scope.toggleEditColumnName = function(definecolumn){   
        definecolumn.edit = !definecolumn.edit;    
    };
    $scope.cancelEditColumnName = function(definecolumn){
        definecolumn.columnHeader = definecolumn.oldColumnName;
        $scope.toggleEditColumnName(definecolumn);   
    };
    $scope.editColumnName = function(definecolumn){
        definecolumn.oldColumnName = definecolumn.columnHeader;
        $scope.toggleEditColumnName(definecolumn);  
    };
    
    $scope.setDefault = function(){
        $scope.currentObject = {};
        $scope.createByTemplate_flag = false;
        $scope.archiveTemplate = {};
        $scope.activeStartDateFormat = new Date();
        $scope.system1.defineColumns = [];
        $scope.system2.defineColumns = [];
    };
    
    $scope.setCurrentReportType = function(object){
        $scope.currentReportType.reportType = object.reportType;
        $scope.currentReportType.reportTypeName=object.reportTypeName;
        $scope.currentReportType.suffix=object.suffix;
    };
    $scope.editTemplate = function(object, oldObject){
//console.log(object);        
        $scope.selectedItem(oldObject);
        $scope.setCurrentReportType(object);
    };
    
    $scope.addTemplate = function(object){
        $scope.setCurrentReportType(object);
        $scope.getWizard();
        $scope.setDefault();
        $('#createTemplateModal').modal();
    };
    
    $scope.preview = function(template){
        var url = template.reportType.previewUrl;
//console.log(template);        
        window.open(url);
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
//        console.log(e.keyCode);
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

    


    
}]);