'use strict';
angular.module('portalNMC')
.controller('MngmtObjectsCtrl', ['$scope', '$rootScope', '$routeParams', '$resource', '$cookies', '$compile', '$parse', 'crudGridDataFactory', 'notificationFactory', '$http', 'objectSvc', 'mainSvc', '$timeout',
            function ($scope, $rootScope, $routeParams, $resource, $cookies, $compile, $parse, crudGridDataFactory, notificationFactory, $http, objectSvc, mainSvc, $timeout) {
                
console.log('Run Object management controller.');  
//var timeDirStart = (new Date()).getTime();
                
                    //messages for user
                $scope.messages = {};
                
                $scope.messages.signClientsObjects = "Назначить абонентов";
                $scope.messages.deleteObjects = "Удалить выделенные объекты";
                $scope.messages.deleteObject = "Удалить объект";
                $scope.messages.viewProps = "Свойства объекта";
                $scope.messages.addZpoint = "Добавить точку учета";
                $scope.messages.setClient = "Назначить абонентов";
                $scope.messages.viewDevices = "Приборы";
                $scope.messages.markAllOn = "Выбрать все";
                $scope.messages.markAllOff = "Отменить все";
                
                    //object ctrl settings
                $scope.crudTableName = objectSvc.getObjectsUrl();
                $scope.objectCtrlSettings = {};

                $scope.objectCtrlSettings.isCtrlEnd =false;
                //флаг для объектов: true - все объекты выбраны
                $scope.objectCtrlSettings.allSelected = false;
                //выбран хотя бы один объект
                $scope.objectCtrlSettings.anySelected = false;
                $scope.objectCtrlSettings.objectsPerScroll = 34;//the pie of the object array, which add to the page on window scrolling
                $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;//50;//current the count of objects, which view on the page
                
                //list of system for meta data editor
                $scope.objectCtrlSettings.vzletSystemList = [];
                
                //flag on/off extended user interface
                $scope.objectCtrlSettings.extendedInterfaceFlag = true;
                
                //server time zone at Hours
                $scope.objectCtrlSettings.serverTimeZone = 3;
                //date format for user
                $scope.objectCtrlSettings.dateFormat = "DD.MM.YYYY";
                
                //service permission settings
//                $scope.objectCtrlSettings.mapAccess = false;
                $scope.objectCtrlSettings.ctxId = "management_objects_2nd_menu_item";
                
                $scope.objectCtrlSettings.rmaUrl = "../api/rma";
                $scope.objectCtrlSettings.clientsUrl = "../api/rma/subscribers";
                $scope.objectCtrlSettings.subscrObjectsSuffix = "/subscrContObjects";
                
                var setVisibles = function(){
                    var tmp = mainSvc.getContextIds();
                    tmp.forEach(function(element){
                        var elDOM = document.getElementById(element.permissionTagId);//.style.display = "block";
                        if (angular.isUndefined(elDOM)||(elDOM==null)){
                            return;
                        };                        
                        $('#'+element.permissionTagId).removeClass('nmc-hide');
                    });
                };
                setVisibles();
                //listen change of service list
                $rootScope.$on('servicePermissions:loaded', function(){
                    setVisibles();
                });
                
                $scope.data = {};
                
                $scope.object = {};
                $scope.objects = [];
                $scope.objectsOnPage = [];
                $scope.currentSug = null;
                
                function findObjectById(objId){
                    var obj = null;                 
                    $scope.objects.some(function(element){
                        if (element.id === objId){
                            obj = element;
                            return true;
                        }
                    });        
                    return obj;
                };
                
//console.log(objectSvc.promise);
                var getObjectsData = function(objId){
//console.log("getObjectsData");                    
                    objectSvc.getRmaPromise().then(function(response){
                        var tempArr = response.data;
    //console.log(tempArr);                    
                        $scope.objects = response.data;
                        //sort by name
                        objectSvc.sortObjectsByFullName($scope.objects);

                        $scope.objectsWithoutFilter = $scope.objects;
                        tempArr =  $scope.objects.slice(0, $scope.objectCtrlSettings.objectsPerScroll);
                        $scope.objectsOnPage = tempArr;
                        $scope.loading = false;  
                        //if we have the contObject id in cookies, then draw the Zpoint table for this object.
                        if (angular.isDefined($cookies.contObject) && $cookies.contObject!=="null"){
                            $scope.toggleShowGroupDetails(Number($cookies.contObject));
                            $cookies.contObject = null;          
                        };
                        $rootScope.$broadcast('objectSvc:loaded');
                        
                        if (!mainSvc.checkUndefinedNull(objId)){
                            moveToObject(objId);
                        };
                    });
                };
                getObjectsData();
                
                var getRsoOrganizations = function(){
                    objectSvc.getRsoOrganizations()
                    .then(function(response){
                        $scope.data.rsoOrganizations = response.data;
                    });
                };
                getRsoOrganizations();
                
                var getCmOrganizations = function(){
                    objectSvc.getCmOrganizations()
                    .then(function(response){
                        $scope.data.cmOrganizations = response.data;
                    });
                };
                getCmOrganizations();
                
                var getServiceTypes = function(){
                    objectSvc.getServiceTypes()
                    .then(function(response){
                        $scope.data.serviceTypes = response.data;
//console.log(response.data);                        
                    });
                };
                getServiceTypes();
                
                var getTimezones = function(){
                    objectSvc.getTimezones()
                    .then(function(response){
                        $scope.data.timezones = response.data;
//console.log($scope.data.timezones);                        
                    });
                };
                getTimezones();
                
//                $scope.objects = objectSvc.getObjects();
                $scope.loading = objectSvc.getLoadingStatus();//loading;
                $scope.columns = [

                        {"name":"fullName", "header" : "Название", "class":"col-md-5"},
                        {"name":"fullAddress", "header" : "Адрес", "class":"col-md-5"}

                    ];//angular.fromJson($attrs.columns);
                $scope.captions = {"loading":"loading", "totalElements":"totalElements"};//angular.fromJson($attrs.captions);
                $scope.extraProps = {"idColumnName":"id", "defaultOrderBy" : "fullName", "nameColumnName":"fullName"};//angular.fromJson($attrs.exprops);
                $scope.addMode = false;
                $scope.orderBy = { field: $scope.extraProps["defaultOrderBy"], asc: true };

                $scope.filter = '';
                $scope.filterType='';
                //Признак того, что объекты выводятся в окне "Отчеты"
                $scope.bGroupByObject = false;//angular.fromJson($attrs.bgroup) || false;
                $scope.bObject = false;//angular.fromJson($attrs.bobject) || false; //Признак, что страница отображает объекты
                $scope.bList = true;//angular.fromJson($attrs.blist) || true; //Признак того, что объекты выводятся только для просмотра        
                //zpoint column names
                $scope.oldColumns = [
                        {"name":"zpointName", "header" : "Наименование", "class":"col-md-5"},
                        {"name":"zpointModel", "header" : "Модель", "class":"col-md-2"},
                        {"name":"zpointNumber", "header" : "Номер", "class":"col-md-2"},
//                        {"name":"zpointLastDataDate", "header" : "Последние данные", "class":"col-md-2"}
                    ];//angular.fromJson($attrs.zpointcolumns);
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
                
                var successCallbackOnZpointUpdate = function(e){
//console.log(e);              
                    //e.data содержит данные измененной/созданной точки учета
//                    их теперь нужно 
//                    размапить
                    var mappedZpoint  = mapZpointProp(e.data);
//                    добавить/перезаписать список точек у объекта
                    
//                    отрисовать таблицу точек учета
                    
                    notificationFactory.success();
                    $('#showZpointOptionModal').modal('hide');
                    if (mainSvc.checkUndefinedNull($scope.currentObject.zpoints) || !angular.isArray($scope.currentObject.zpoints)){
                        $scope.zpointSettings = {};
                        return;
                    };
                    var curIndex = -1;
//console.log($scope.currentObject); 
                    $scope.currentObject.zpoints.some(function(elem, index){
                        if (elem.id === $scope.zpointSettings.id){
                            curIndex = index;
                            return true;
                        };
                    });
                    
                    //update view name for zpoint
                    if ((curIndex >-1)){
                            var repaintZpointTableFlag = false;
                            if(($scope.currentObject.zpoints[curIndex].zpointName!==$scope.zpointSettings.customServiceName)){
                                repaintZpointTableFlag = true;
                            };
                            var objectIndex = -1;
                            objectIndex = findObjectIndexInArray($scope.currentObject.id, $scope.objects);
                            var objectOnPageIndex = findObjectIndexInArray($scope.currentObject.id, $scope.objectsOnPage);
                            if (objectIndex>-1){
                                //update zpoint data in arrays                             
                                $scope.objects[objectIndex].zpoints[curIndex] = mappedZpoint;
                                $scope.objectsOnPage[objectOnPageIndex].zpoints[curIndex] = mappedZpoint;
                            };
                            //remake zpoint table
                            if(($scope.objectsOnPage[objectOnPageIndex].showGroupDetails === true)){
                                makeZpointTable($scope.objectsOnPage[objectOnPageIndex]);
                            };
                    }else{
                        var objectIndex = -1;
                        objectIndex = findObjectIndexInArray($scope.currentObject.id, $scope.objects);
                        var objectOnPageIndex = findObjectIndexInArray($scope.currentObject.id, $scope.objectsOnPage);
                        if (objectIndex>-1){
                            //update zpoint data in arrays
                            $scope.objects[objectIndex].zpoints.push(mappedZpoint);                           
                            if ($scope.objectsOnPage[objectOnPageIndex].showGroupDetails === true){
                                makeZpointTable($scope.objectsOnPage[objectOnPageIndex]);
                            };                             
                        };
                    };
                    $scope.zpointSettings = {};

                };
                
                var successCallbackOnSetMode = function(e){
                    notificationFactory.success();                    
                    $scope.objectCtrlSettings.allSelected = false;
                    $scope.objects.forEach(function(el){
                        if (el.selected === true){
                            el.currentSettingMode = $scope.settedMode;
                            el.imgsrc='images/object-mode-'+el.currentSettingMode+'.png';
                        };
                        el.selected = false
                    });
                    $scope.objectsOnPage.forEach(function(el){
                        if (el.selected === true){
                            el.currentSettingMode = $scope.settedMode;
                            el.imgsrc='images/object-mode-'+el.currentSettingMode+'.png';
                        };
                        el.selected = false
                    });
                };
                
                var findObjectIndexInArray = function(objId, targetArr){                  
                    var result = -1;
                    targetArr.some(function(elem, index){
                            if (elem.id == objId){                              
                                result = index;
                                return true;
                            };
                        });
                    return result;
                };
                // *** Переместить курсор на указанный объект
                var moveToObject = function(objId){
                    if (mainSvc.checkUndefinedNull(objId)){
                        return "moveToObject: object id is undefined or null.";
                    };
                    var curObj = objectSvc.findObjectById(Number(objId), $scope.objects);
                    if (curObj != null){
                        var curObjIndex = $scope.objects.indexOf(curObj);                        
                        if (curObjIndex > $scope.objectCtrlSettings.objectsOnPage){
                            //вырезаем из массива объектов элементы с текущей позиции, на которой остановились в прошлый раз, по вычесленный конечный индекс
                            var tempArr =  $scope.objects.slice($scope.objectCtrlSettings.objectsOnPage, curObjIndex + 1);
                                //добавляем к выведимому на экран массиву новый блок элементов
                            Array.prototype.push.apply($scope.objectsOnPage, tempArr);
                            $scope.objectCtrlSettings.objectsOnPage = curObjIndex + 1;
                        };
                        if (!mainSvc.checkUndefinedNull(objId)){
                            $timeout(function(){
                                var curObjElem = document.getElementById("obj" + objId);                     
                                if (!mainSvc.checkUndefinedNull(curObjElem)){        
                                    curObjElem.scrollIntoView();
                                }
                            }, 50);
                        };
                    }
                };
                
                var deleteObjectFromArray = function(objId, targetArr){
                    var curInd = findObjectIndexInArray(objId, targetArr);
                    if (curInd!=-1){
                        targetArr.splice(curInd, 1);
                    };
                };

                var successCallback = function (e, cb) {                    
                    notificationFactory.success();
                    $('#deleteObjectModal').modal('hide');
                    $('#showObjOptionModal').modal('hide');
                    $('#setClientModal').modal('hide');
                };
                
                var successCallbackUpdateObject = function(e){ 
                    $rootScope.$broadcast('objectSvc:requestReloadData');
                    $scope.currentObject._activeContManagement = e._activeContManagement;
                    var objIndex = null;
                    objIndex = findObjectIndexInArray(e.id, $scope.objects);
                    if (objIndex != null) {$scope.objects[objIndex] = e};
                    objIndex = null;
                    objIndex = findObjectIndexInArray(e.id, $scope.objectsOnPage);
                    if (objIndex != null) {$scope.objectsOnPage[objIndex] = e};
                    $scope.currentObject = {};
                    successCallback(e, null);
                };
                
                var successDeviceCallback = function(e){
                    $scope.getDevices($scope.currentObject, true);
                    $('#deleteDeviceModal').modal('hide');
                    $('#showDeviceModal').modal('hide');
                };
                
                var successDeleteDeviceCallback = function (e, cb) {          
                    deleteObjectFromArray($scope.currentDevice.id, $scope.currentObject.devices);
                    successCallback(e, null);
                };
                
                var successDeleteCallback = function (e, cb) {           
                    deleteObjectFromArray($scope.currentObject.id, $scope.objects);
                    deleteObjectFromArray($scope.currentObject.id, $scope.objectsOnPage);
                    successCallback(e, null);
                };
                
                var successDeleteObjectsCallback = function (e, cb) {
                    $scope.currentObject.deleteObjectIds.forEach(function(el){
                        deleteObjectFromArray(el, $scope.objects);
                        deleteObjectFromArray(el, $scope.objectsOnPage);    
                    });
                    successCallback(e, null);
                };
                
                var successDeleteZpointCallback = function(e){
                    $('#deleteZpointModal').modal('hide');
                    deleteObjectFromArray($scope.currentZpoint.id, $scope.currentObject.zpoints);
                    makeZpointTable($scope.currentObject);
                    successCallback(e, null);
                };


                var successPostCallback = function (e) {                  
                    successCallback(e, null);
                    $rootScope.$broadcast('objectSvc:requestReloadData');
                    $scope.loading = true;
                    getObjectsData(e.id);
                    $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;
//                    $timeout(function(){
//                        var curObjElem = document.getElementById("obj" + e.id);
//console.log(e.id);                        
//console.log(curObjElem);                        
//                        if (!mainSvc.checkUndefinedNull(curObjElem)){        
//                            curObjElem.scrollIntoView();
//                        };
//                    }, 100);
//                    location.reload();
                };

                var errorCallback = function (e) {
//                    notificationFactory.errorInfo(e.statusText,e.data.description); 
                    console.log(e);
                    var errorCode = "-1";
                    if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
                        errorCode = e.resultCode || e.data.resultCode;
                    };
                    var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
                    notificationFactory.errorInfo(errorObj.caption, errorObj.description);
                };

                $scope.addObject = function (url, obj) {                    
                    if (angular.isDefined(obj.contManagementId) && (obj.contManagementId != null)){
                        url += "/?cmOrganizationId=" + obj.contManagementId;                       
                    };
                    obj._daDataSraw = null;
                    if (!mainSvc.checkUndefinedNull($scope.currentSug)){
                        obj._daDataSraw = JSON.stringify($scope.currentSug);
                    };
                    crudGridDataFactory(url).save(obj, successPostCallback, errorCallback);
                };

                $scope.deleteObject = function (obj) {
                    var url = objectSvc.getRmaObjectsUrl();                 
                    if (angular.isDefined(obj) && (angular.isDefined(obj.id)) && (obj.id != null)){
                        crudGridDataFactory(url).delete({ id: obj[$scope.extraProps.idColumnName] }, successDeleteCallback, errorCallback);
                    };
                };
                
                $scope.deleteObjects = function(obj){
                    var url = objectSvc.getRmaObjectsUrl();
//console.log(url);                    
//console.log(obj);                    
                    if (angular.isDefined(obj) && (angular.isDefined(obj.id)) && (obj.id != null)){
                        crudGridDataFactory(url).delete({ id: obj[$scope.extraProps.idColumnName] }, successDeleteCallback, errorCallback);
                    }else if (angular.isDefined(obj.deleteObjects) && (obj.deleteObjects != null) && angular.isArray(obj.deleteObjects)){
                        crudGridDataFactory(url).delete({ contObjectIds: obj.deleteObjectIds }, successDeleteObjectsCallback, errorCallback);
                    };
                };
                
                $scope.deleteZpoint = function (zpoint) {
                    var url = objectSvc.getRmaObjectsUrl() + "/" + $scope.currentObject.id + "/zpoints";
//console.log(url);                                       
                    crudGridDataFactory(url).delete({ id: zpoint[$scope.extraProps.idColumnName] }, successDeleteZpointCallback, errorCallback);
                };
                                
                $scope.updateObject = function (url, object) {
                    var params = { id: object[$scope.extraProps.idColumnName]};
                    if (angular.isDefined(object.contManagementId) && (object.contManagementId != null)){
                        var cmOrganizationId = object.contManagementId;
                        params = { 
                            /*id: object[$scope.extraProps.idColumnName],*/
                            cmOrganizationId: cmOrganizationId
                        };                        
                    };
                    object._daDataSraw = null;
                    if (!mainSvc.checkUndefinedNull($scope.currentSug)){
                        object._daDataSraw = JSON.stringify($scope.currentSug);
                    };
                    crudGridDataFactory(url).update(params, object, successCallbackUpdateObject, errorCallback);
                };

                $scope.setOrderBy = function (field) {
                    var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
                    $scope.orderBy = { field: field, asc: asc };
                };
                
                
                var checkObjectSettings = function(obj){
                    //check name, timezone, uk, mode
                    var result = true;
                    if ($scope.emptyString(obj.fullName)){
                        notificationFactory.errorInfo("Ошибка", "Не задано наименование объекта!");
                        result = false;
                    };
                    if ($scope.emptyString(obj.timezoneDefKeyname)){
                        notificationFactory.errorInfo("Ошибка", "Не задан часовой пояс объекта!");
                        result = false;
                    };
                    if ($scope.checkUndefinedNull(obj.contManagementId)){
                        notificationFactory.errorInfo("Ошибка", "Не задана управляющая компания!");
                        result = false;
                    };
                    if ($scope.emptyString(obj.currentSettingMode)){
                        notificationFactory.errorInfo("Ошибка", "Не задан режим функционирования!");
                        result = false;
                    };
                    return result;
                };
                
                $scope.sendObjectToServer = function(obj){
                    if(!checkObjectSettings(obj)){
                        return false;
                    };
                    var url = objectSvc.getRmaObjectsUrl();                    
                    if (angular.isDefined(obj.id) && (obj.id != null)){
                        $scope.updateObject(url, obj);
                    }else{
//                        obj.timezoneDefKeyname = "MSK";
                        $scope.addObject(url,obj);
                    };
                };
              
                $scope.selectedItem = function (item) {
			        var curObject = angular.copy(item);
			        $scope.currentObject = curObject;                    
			    };
                
                $scope.selectedObject = function(objId){
                    objectSvc.getRmaObject(objId)
                    .then(function(resp){
                        $scope.currentObject = resp.data;
                    
                    // $scope.currentObject = angular.copy(objectSvc.findObjectById(objId, $scope.objects));
console.log($scope.currentObject);                    
                        if (angular.isDefined($scope.currentObject._activeContManagement) && ($scope.currentObject._activeContManagement != null)){
                                $scope.currentObject.contManagementId = $scope.currentObject._activeContManagement.organization.id;
                        };
                        checkGeo();
                    }, function(error){
                        console.log(error);
                    });
                };
                
                $scope.selectedZpoint = function(objId, zpointId){
                    $scope.selectedItem(objectSvc.findObjectById(objId, $scope.objects));
//console.log($scope.currentObject);                     
                    var curZpoint = null;
                    if (angular.isDefined($scope.currentObject.zpoints) && angular.isArray($scope.currentObject.zpoints)){
                        $scope.currentObject.zpoints.some(function(element){
                            if (element.id === zpointId){
                                curZpoint = angular.copy(element);
                                return true;
                            }
                        });
                    };
                    $scope.currentZpoint = curZpoint;
//console.log($scope.currentZpoint);                                        
                };
                
                var mapZpointProp = function(zpoint){
                    var result = {};
                              
                    result.id = zpoint.id;
                    result.exSystemKeyname = zpoint.exSystemKeyname;
                    result.tsNumber = zpoint.tsNumber;
                    result.exCode = zpoint.exCode;
                    result.version = zpoint.version;
                    result.contObjectId = zpoint.contObjectId;
                    result.startDate = zpoint.startDate;
                    result._activeDeviceObjectId = zpoint._activeDeviceObjectId;
                    result.rsoId = zpoint.rsoId;
                    result.zpointType = zpoint.contServiceType.keyname;
                    result.isManualLoading = zpoint.isManualLoading;
                    result.customServiceName = zpoint.customServiceName;
                    result.contServiceTypeKeyname = zpoint.contServiceTypeKeyname;
                    result.zpointName = zpoint.customServiceName;
                    result.contZPointComment = zpoint.contZPointComment;
                    if ((typeof zpoint.rso != 'undefined') && (zpoint.rso != null)){
                        result.zpointRSO = zpoint.rso.organizationFullName || zpoint.rso.organizationName;
                        result.rsoId = zpoint.rsoId;
                    }else{
                        result.zpointRSO = "Не задано"
                    };
                    result.checkoutTime = zpoint.checkoutTime;
                    result.checkoutDay = zpoint.checkoutDay;
                    if((typeof zpoint.doublePipe == 'undefined')){
                        result.piped = false;


                    }else {
                        result.piped = true;
                        result.doublePipe = (zpoint.doublePipe === null) ? false : zpoint.doublePipe;
                        result.singlePipe = !result.doublePipe;
                    };
//console.log(zpoint);
                    if ((typeof zpoint.deviceObjects != 'undefined') && (zpoint.deviceObjects.length > 0)){                                
                        if (zpoint.deviceObjects[0].hasOwnProperty('deviceModel')){
                            result.zpointModel = zpoint.deviceObjects[0].deviceModel.modelName;
                        }else{
                            result.zpointModel = "Не задано";
                        };
                        result.zpointNumber = zpoint.deviceObjects[0].number;
                    };
                    result._activeDeviceObjectId = zpoint._activeDeviceObjectId;
                    result.zpointLastDataDate  = zpoint.lastDataDate;  
                    
                    return result;
                };
                
                $scope.toggleShowGroupDetails = function(objId){//switch option: current goup details
                    var curObject = objectSvc.findObjectById(objId, $scope.objects);//null;                
                    //if cur object = null => exit function
                    if (curObject == null){
                        return;
                    };
                    //else
                    
                    var zpTable = document.getElementById("zpointTable" + curObject.id);
//console.log(zpTable);                    
                    if ((curObject.showGroupDetails == true) && (zpTable == null)){                        
                        curObject.showGroupDetails = true;
                    }else{                       
                        curObject.showGroupDetails =!curObject.showGroupDetails;
                    };                                           
                    //if curObject.showGroupDetails = true => get zpoints data and make zpoint table
                    if (curObject.showGroupDetails === true){
                      
                        var mode = "Ex";
                        objectSvc.getZpointsDataByObject(curObject, mode).then(function(response){
                            var tmp = [];
                            var copyTmp = angular.copy(response.data);
//console.log(copyTmp);                              
                            if (mode == "Ex"){
                                tmp = response.data.map(function(el){
                                    var result = {};
                                    result = el.object;
                                    result.lastDataDate = el.lastDataDate;
//console.log(el.lastDataDate);                                    
                                    return result;
                                });
                            }else{
                                tmp = data;
                            };
                            var zPointsByObject = tmp;                          
                            var zpoints = [];
                            for(var i = 0; i < zPointsByObject.length; i++){
                                var zpoint = mapZpointProp(zPointsByObject[i]);
                                zpoints[i] = zpoint;                  
                            }
                            curObject.zpoints = zpoints;
                            makeZpointTable(curObject);
                            var btnDetail = document.getElementById("btnDetail" + curObject.id);
                            if (!mainSvc.checkUndefinedNull(btnDetail)){
                                btnDetail.classList.remove("glyphicon-chevron-right");
                                btnDetail.classList.add("glyphicon-chevron-down");
                            };
                            
                            curObject.showGroupDetailsFlag = !curObject.showGroupDetailsFlag;
                        });
                    }//else if curObject.showGroupDetails = false => hide child zpoint table
                    else{
                        var trObj = document.getElementById("obj" + curObject.id);
                        var trObjZp = trObj.getElementsByClassName("nmc-tr-zpoint")[0];//.getElementById("trObjZp");                      
                        trObjZp.innerHTML = "";
                        var btnDetail = document.getElementById("btnDetail" + curObject.id);
                        btnDetail.classList.remove("glyphicon-chevron-down");
                        btnDetail.classList.add("glyphicon-chevron-right");
                    };
                    
                };
                //Формируем таблицу с точками учета
                function makeZpointTable(object){ 
//console.log(object);                                        
                    var trObj = document.getElementById("obj" + object.id);
//console.log(trObj);                    
                    if ((angular.isUndefined(trObj)) || (trObj === null)){
                        return;
                    };
                    var trObjZp = trObj.getElementsByClassName("nmc-tr-zpoint")[0];//.getElementById("trObjZp");                    
//console.log(trObjZp);                                        
//                    var trObjZp = document.getElementById("trObjZp"+object.id);                 
                    var trHTML = "";

                    trHTML += "<td class=\"nmc-td-for-button-object-control\" ng-hide=\"!objectCtrlSettings.extendedInterfaceFlag\"></td><td></td><td style=\"padding-top: 2px !important;\"><table id=\"zpointTable" +object.id + "\" class=\"crud-grid table table-lighter table-bordered table-condensed table-hover nmc-child-object-table\">";
                    trHTML += "<thead><tr class=\"nmc-child-table-header\">";
                    trHTML += "<th ng-show=\"bObject || bList\" class=\"nmc-td-for-buttons\"></th>";
                    $scope.oldColumns.forEach(function(column){
                        trHTML += "<th class=\"" + column.class + "\">";
                        trHTML += "" + (column.header || column.name) + "";
                        trHTML += "</th>";
                    });
                    trHTML += "<th></th>";
                    trHTML += "</tr></thead>";
                    
                    object.zpoints.forEach(function(zpoint){
                        trHTML += "<tr id=\"trZpoint" + zpoint.id + "\" >";
//                        ng-dblclick=\"getIndicators("+object.id+","+zpoint.id+")\"
                        trHTML += "<td class=\"nmc-td-for-buttons\">"+
                                "<i class=\"btn btn-xs glyphicon glyphicon-edit nmc-button-in-table\""+
                                    "ng-click=\"getZpointSettings(" + object.id + "," + zpoint.id + ")\""+
                                    "data-target=\"#showZpointOptionModal\""+
                                    "data-toggle=\"modal\""+
                                    "data-placement=\"bottom\""+
                                    "title=\"Свойства точки учёта\">"+
                                "</i>"+
                                "<i class=\"btn btn-xs nmc-button-in-table\""+
                                    "ng-click=\"getZpointSettingsExpl(" + object.id + "," + zpoint.id + ")\""+
                                    "data-target=\"#showZpointExplParameters\""+
                                    "data-toggle=\"modal\""+
                                    "data-placement=\"bottom\""+
                                    "title=\"Эксплуатационные параметры точки учёта\">"+
                                        "<img height=12 width=12 src=\"vendor_components/glyphicons_free/glyphicons/png/glyphicons-140-adjust-alt.png\" />"+
                                "</i>"+
//                                "<a href='#/objects/indicators/?objectId="+object.id+"&zpointId="+zpoint.id+"&objectName="+object.fullName+"&zpointName="+zpoint.zpointName+"'><i class=\"btn btn-xs glyphicon glyphicon-list nmc-button-in-table\""+
//                                    "ng-click=\"getIndicators("+object.id+","+zpoint.id+")\""+
//                                    "ng-mousedown=\"setIndicatorsParams("+object.id+","+zpoint.id+")\""+
//                                    "title=\"Показания точки учёта\">"+
//                                "</i></a>"+

                            "</td>";
                        $scope.oldColumns.forEach(function(column){
                            switch (column.name){
                                case "zpointName": 
                                    var imgPath = "";                                  
                                    switch(zpoint['zpointType']){
                                        case "cw":
                                            imgPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-22-snowflake.png";
                                            break;
                                        case "hw":
                                            imgPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-93-tint.png";
                                            break;
                                        case "heat":
                                            imgPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-85-heat.png";
                                            break;
                                        case "gas":
                                            imgPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-23-fire.png";
                                            break;
                                        case "env":
                                            imgPath = "images/es.png";
                                            break;
                                        case "el":
                                            imgPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-242-flash.png";               
                                            break;
                                        default:
                                            imgPath = column['zpointType'];
                                            break;   
                                    };                                   
                                    trHTML += "<td>";
                                    trHTML += "<img height=12 width=12 src=\"" + imgPath + "\"> <span class='paddingLeft5'></span>";
                                    trHTML += (zpoint[column.name] || "Не задано")+"<span ng-show=\"isSystemuser()\">(id = " + zpoint.id + ")</span></td>"; 
                                    break;
                                case "zpointLastDataDate" : trHTML += "<td>{{" + zpoint[column.name] + " | date: 'dd.MM.yyyy HH:mm'}}</td>"; break;   
                                case "zpointRefRange": trHTML += "<td id=\"zpointRefRange" + zpoint.id + "\"></td>"; break;
                                default : trHTML += "<td>" + zpoint[column.name] + "</td>"; break;
                            };
                        });
                        trHTML += "<td>";
                        trHTML += "<div class=\"btn-toolbar\">"+
                                "<div class=\"btn-group pull-right\">"+                   
                                    "<i title=\"Удалить точку учета\" class=\"btn btn-xs glyphicon glyphicon-trash nmc-button-in-table\" ng-click=\"deleteZpointInit(" + object.id + "," + zpoint.id + ")\" data-target=\"#deleteZpointModal\" data-toggle=\"modal\"></i>"+
                                "</div>"+
                            "</div>";
                        trHTML += "</td>";
                        trHTML += "</tr>";
                    });    
                    trHTML += "</table></td>";
                    trObjZp.innerHTML = trHTML;
                    
                    $compile(trObjZp)($scope);                
                }; 
                
                $scope.dateFormat = function(millisec){
                    var result = "";
                    var serverTimeZoneDifferent = Math.round($scope.objectCtrlSettings.serverTimeZone*3600.0*1000.0);
                    var tmpDate = (new Date(millisec+serverTimeZoneDifferent));
//console.log(tmpDate);        
//console.log(tmpDate.getUTCFullYear());   
//console.log(tmpDate.getUTCMonth());
//console.log(tmpDate.getUTCDate());
//console.log(tmpDate.getUTCHours());
//console.log(tmpDate.getUTCMinutes());        
                    result = (tmpDate == null) ? "" : moment([tmpDate.getUTCFullYear(), tmpDate.getUTCMonth(), tmpDate.getUTCDate()]).format($scope.objectCtrlSettings.dateFormat);
                    return result;//
                };

                //for page "Objects"
                
                //Фильтр "Только непросмотренные"
                $scope.onlyNoRead = false;
                $scope.showRow = function(obj){
                    if ( (typeof obj.isRead == 'undefined') && (!$scope.onlyNoRead)){
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
        
                //Свойства точки учета
                $scope.zpointSettings = {};
                $scope.addZpoint = function(object){
                    $scope.selectedItem(object);                    
                    $scope.zpointSettings = {};
                    $scope.getDevices(object, false);
                };
                
                $scope.getZpointSettings = function(objId, zpointId){
                    $scope.selectedZpoint(objId, zpointId);          
//console.log($scope.currentZpoint); 
                    var object = angular.copy($scope.currentZpoint);
                    var zps = {};
                    zps.id = object.id;
                    zps.exSystemKeyname = object.exSystemKeyname;
                    zps.tsNumber = object.tsNumber;
                    zps.exCode = object.exCode;
                    zps.version = object.version;
                    zps.contObjectId = object.contObjectId;
                    zps.startDate = object.startDate;
                    zps._activeDeviceObjectId = object._activeDeviceObjectId;
                    zps.rsoId = object.rsoId;
                    zps.isManualLoading = object.isManualLoading;
                    zps.customServiceName = object.customServiceName;
                    zps.contServiceTypeKeyname = object.contServiceTypeKeyname;
                    zps.contZPointComment = object.contZPointComment;
                    zps.zpointName = object.zpointName;
                    switch (object.zpointType){
                       case "heat" :  zps.zpointType = "Теплоснабжение"; break;
                       case "hw" : zps.zpointType = "ГВС"; break;
                       case "cw" : zps.zpointType = "ХВС"; break;    
                        default : zps.zpointType = object.zpointType;        
                    };
                    zps.piped = object.piped;
                    zps.singlePipe = object.singlePipe;
                    zps.doublePipe = object.doublePipe;
//console.log(zps);
                    zps.zpointModel = object.zpointModel;
                    zps.zpointRSO = object.zpointRSO;
                    zps.checkoutTime = object.checkoutTime;
                    zps.checkoutDay = object.checkoutDay;
                    zps.winter = {};
                    zps.summer = {};
                    $scope.zpointSettings = zps;
                    $scope.getDevices($scope.currentObject, false);
                };
                
                $scope.getZpointSettingsExpl = function(objId, zpointId){
                    $scope.getZpointSettings(objId, zpointId);
                    var winterSet = {};
                    var summerSet = {};
                                        //http://localhost:8080/nmk-p/api/subscr/contObjects/18811505/zpoints/18811559/settingMode
                    var table = $scope.crudTableName + "/" + $scope.currentObject.id + "/zpoints/" + $scope.zpointSettings.id + "/settingMode";
                    crudGridDataFactory(table).query(function (data) {
                        for(var i = 0; i < data.length; i++){
                                                    
                            if(data[i].settingMode == "winter"){
                                winterSet = data[i];
                            }else if(data[i].settingMode == "summer"){
                                summerSet = data[i];
                            }
                        };                 
                        $scope.zpointSettings.winter = winterSet;
                        $scope.zpointSettings.summer = summerSet;
                    });
                };
            
                var successZpointSummerCallback = function (e) {
                    notificationFactory.success();
                    var tableWinter = $scope.crudTableName + "/" + $scope.currentObject.id + "/zpoints/" + $scope.zpointSettings.id + "/settingMode";
                    crudGridDataFactory(tableWinter).update({ id: $scope.zpointSettings.winter.id }, $scope.zpointSettings.winter, successZpointWinterCallback, errorCallback);           
                };
                
                 var successZpointWinterCallback = function (e) {
                    notificationFactory.success();    
                    $('#showZpointOptionModal').modal('hide');
                    $('#showZpointExplParameters').modal('hide');
                     $scope.zpointSettings = {};
                };
                
                //Save new Zpoint | Update the common zpoint setiing - for example, Name
                //$scope.checkString  
                $scope.emptyString = function(str){
                    return mainSvc.checkUndefinedEmptyNullValue(str);
                };
                $scope.checkUndefinedNull = function(val){
                    return mainSvc.checkUndefinedNull(val);
                };
                
                var checkZpointCommonSettings = function(){
                    //check name, type, rso, device
                    var result = true;
                    if ($scope.emptyString($scope.zpointSettings.customServiceName)){
                        notificationFactory.errorInfo("Ошибка", "Не задано наименование точки учета!");
                        result = false;
                    };
                    if ($scope.emptyString($scope.zpointSettings.contServiceTypeKeyname)){
                        notificationFactory.errorInfo("Ошибка", "Не задан тип точки учета!");
                        result = false;
                    };
                    if ($scope.checkUndefinedNull($scope.zpointSettings._activeDeviceObjectId)){
                        notificationFactory.errorInfo("Ошибка", "Не задан прибор для точки учета!");
                        result = false;
                    };
                    if ($scope.checkUndefinedNull($scope.zpointSettings.rsoId)){
                        notificationFactory.errorInfo("Ошибка", "Не задано РСО для точки учета!");
                        result = false;
                    };
                    return result;
                };
                
                $scope.updateZpointCommonSettings = function(){
//console.log($scope.zpointSettings);
                    if (!checkZpointCommonSettings()){
                        return false;
                    };
                    //prepare piped info
//                    if ($scope.zpointSettings.singlePipe){
//                        $scope.zpointSettings.doublePipe = false;
//                    };
                    var url = objectSvc.getRmaObjectsUrl() + "/" + $scope.currentObject.id + "/zpoints";
                    if (angular.isDefined($scope.zpointSettings.id) && ($scope.zpointSettings.id != null)){
                        url = url + "/" + $scope.zpointSettings.id;
                    
                        $http({
                            url: url,
                            method: 'PUT',
                            data: $scope.zpointSettings
                        })
                            .then(successCallbackOnZpointUpdate, errorCallback);
                    }else{
                        $scope.zpointSettings.startDate = Date.now();
                        $http({
                            url: url,
                            method: 'POST',
                            data: $scope.zpointSettings
                        })
                            .then(successCallbackOnZpointUpdate, errorCallback);
                    };
                    
//                    crudGridDataFactory(url).update({}, $scope.zpointSettings, successCallback, errorCallback);
                };
                
                //Update the zpoint settings, which set the mode for Summer or Winter season
                $scope.updateZpointModeSettings = function(){                   
                    var tableSummer = $scope.crudTableName + "/" + $scope.currentObject.id + "/zpoints/" + $scope.zpointSettings.id + "/settingMode";
                    crudGridDataFactory(tableSummer).update({ id: $scope.zpointSettings.summer.id }, $scope.zpointSettings.summer, successZpointSummerCallback, errorCallback);
                };
                
                // search objects
                $scope.searchObjects = function(searchString){
                    if (($scope.objects.length <= 0)){
                        return;
                    };
                    
                    if (angular.isUndefined(searchString) || (searchString === '')){                      
                        var tempArr = [];
                        $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;
                        tempArr =  $scope.objects.slice(0, $scope.objectCtrlSettings.objectsPerScroll);
                        $scope.objectsOnPage = tempArr;
                    }else{
//                        $scope.objectsOnPage = $scope.objects;
                        var tempArr = [];                        
                        $scope.objects.forEach(function(elem){                
                            if (angular.isDefined(elem.fullName) && elem.fullName.toUpperCase().indexOf(searchString.toUpperCase()) != -1){
                                tempArr.push(elem);
                            };
                        });
                        $scope.objectsOnPage = tempArr;
                    };
//console.log($scope.objectsOnPage);                    
                };
                
                $scope.$on('$destroy', function() {
                    window.onkeydown = undefined;
                }); 
                
                
                //keydown listener for ctrl+end
                window.onkeydown = function(e){
//console.log(e);                    
//                    if ((e.ctrlKey && e.keyCode == 35) && ($scope.objectCtrlSettings.objectsOnPage<$scope.objects.length)){
//                        $scope.loading =  true;    
//                        var tempArr =  $scope.objects.slice($scope.objectCtrlSettings.objectsOnPage,$scope.objects.length);
//                        Array.prototype.push.apply($scope.objectsOnPage, tempArr);
//                        $scope.objectCtrlSettings.objectsOnPage+=$scope.objects.length;
//                        
//                        $scope.objectCtrlSettings.isCtrlEnd = true;
//                        
//                    };
                    
                    if (e.keyCode == 38){                        
                        var elem = document.getElementById("divWithObjectTable");
                        elem.scrollTop = elem.scrollTop - 20;                        
                        return;
                    };
                    if (e.keyCode == 40){
                        var elem = document.getElementById("divWithObjectTable");
                        elem.scrollTop = elem.scrollTop + 20;                        
                        return;
                    };
                    if (e.keyCode == 34){
//                        $scope.addMoreObjects();
//                        $scope.$apply();
                        var elem = document.getElementById("divWithObjectTable");
                        elem.scrollTop = elem.scrollTop + $scope.objectCtrlSettings.objectsPerScroll*10;                        
                        return;
                    };
                    if (e.keyCode == 33){
                        var elem = document.getElementById("divWithObjectTable");
                        elem.scrollTop = elem.scrollTop - $scope.objectCtrlSettings.objectsPerScroll*10;
                        return;
                    };
                    if (e.ctrlKey && e.keyCode == 36){
                        var elem = document.getElementById("divWithObjectTable");
                        elem.scrollTop = 0;
                        return;
                    };
                    if ((e.ctrlKey && e.keyCode == 35) /*&& ($scope.objectCtrlSettings.objectsOnPage < $scope.objects.length)*/){ 
                        if ($scope.objectCtrlSettings.objectsOnPage < $scope.objects.length){                            
                            $scope.loading = true;    
                            $timeout(function(){$scope.loading = false;}, $scope.objects.length);
                        };
                        var tempArr = $scope.objects.slice($scope.objectCtrlSettings.objectsOnPage, $scope.objects.length);
                        Array.prototype.push.apply($scope.objectsOnPage, tempArr);
                        $scope.objectCtrlSettings.objectsOnPage += $scope.objects.length;                        
//                        $scope.objectCtrlSettings.isCtrlEnd = true;
                        $scope.$apply();
                        var elem = document.getElementById("divWithObjectTable");
                        elem.scrollTop = elem.scrollHeight;                                            
                        
                    };
                };
                
                //function set cursor to the bottom of the object table, when ctrl+end pressed
//                $scope.onTableLoad = function(){                     
//console.log("Run onTableLoad");                    
//                    if ($scope.objectCtrlSettings.isCtrlEnd === true){                    
////                        var pageHeight = (document.body.scrollHeight>document.body.offsetHeight)?document.body.scrollHeight:document.body.offsetHeight;
////                        window.scrollTo(0, Math.round(pageHeight));
//                        $scope.objectCtrlSettings.isCtrlEnd = false;
////                        $scope.loading =  false;
//                    };
//                };
                
                //function add more objects for table on user screen
                $scope.addMoreObjects = function(){
//console.log("addMoreObjects. Run");
                    if (($scope.objects.length <= 0)){
                        return;
                    };
                    
                    //set end of object array - определяем конечный индекс объекта, который будет выведен при текущем скролинге
                    var endIndex = $scope.objectCtrlSettings.objectsOnPage + $scope.objectCtrlSettings.objectsPerScroll;
//console.log($scope.objects.length);                    
                    if((endIndex >= $scope.objects.length)){
                        endIndex = $scope.objects.length;
                    };
                    //вырезаем из массива объектов элементы с текущей позиции, на которой остановились в прошлый раз, по вычесленный конечный индекс
                    var tempArr =  $scope.objects.slice($scope.objectCtrlSettings.objectsOnPage, endIndex);
                        //добавляем к выведимому на экран массиву новый блок элементов
                    Array.prototype.push.apply($scope.objectsOnPage, tempArr);
                    if(endIndex >= ($scope.objects.length)){
                        $scope.objectCtrlSettings.objectsOnPage = $scope.objects.length;
                    }else{
                        $scope.objectCtrlSettings.objectsOnPage += $scope.objectCtrlSettings.objectsPerScroll;
                    };
                };
                
                var tableScrolling = function(){                    
                    if (angular.isUndefined($scope.filter) || ($scope.filter == '')){
                        $scope.addMoreObjects();
                        $scope.$apply();
                    };
                };
                
                $("#divWithObjectTable").scroll(tableScrolling);               
                
                // Проверка пользователя - системный/ не системный
                $scope.isSystemuser = function(){
                    var result = false;
                    $scope.userInfo = $rootScope.userInfo;
                    if (angular.isDefined($scope.userInfo)){
                        result = $scope.userInfo._system;
                    };
                    return result;
                };
                
                //toggle all objects - selected/unselected
                $scope.toggleObjects = function(flag){
                    $scope.objectCtrlSettings.anySelected = flag;
                    $scope.objects.forEach(function(el){
                        el.selected = flag;
                    });
                    $scope.objectsOnPage.forEach(function(el){
                        el.selected = flag;
                    });
                };
                    //toggle one object
                $scope.toggleObject = function(object){
//                    object.selected = !object.selected;
                    var anySelectedFlag = false;
                    $scope.objectsOnPage.some(function(elem){
                        if (elem.selected == true){
                            anySelectedFlag = true;
                            return true;
                        };
                    });
                    if (anySelectedFlag == true){
                        $scope.objectCtrlSettings.anySelected = true;
                    }else{
                        $scope.objectCtrlSettings.anySelected = false;
                    };
                };
                
                $scope.setModeForObjects = function(mode){
                    $scope.settedMode = mode;
                    //get the object ids array
                    var contObjectIds = [];
                    if ($scope.objectCtrlSettings.allSelected === true){
                        contObjectIds = $scope.objects.map(function(el){return el.id});
                    }else{
                        $scope.objectsOnPage.forEach(function(el){
                            if(el.selected === true){
                                contObjectIds.push(el.id);
                            };
                        });
                    };
                    
                    //send data to server
                    var url = objectSvc.getObjectsUrl() + "/settingModeType";
                    $http({
                        url: url, 
                        method: "PUT",
                        params: { contObjectIds:contObjectIds, currentSettingMode: mode },
                        data: null
                    })
                    .then(successCallbackOnSetMode, errorCallback);
                };
                
                //Work with devices
                    //get the list of the systems for meta data editor
//                $scope.getVzletSystemList = function(){
//                    var tmpSystemList = objectSvc.getVzletSystemList();
//                    if (tmpSystemList.length===0){
//                        objectSvc.getDeviceMetaDataSystemList()
//                            .then(
//                            function(response){
//                                $scope.objectCtrlSettings.vzletSystemList = response.data;                           
//                            },
//                            function(e){
//                                notificationFactory.errorInfo(e.statusText,e.description);
//                            }
//                        );
//                    }else{
//                        $scope.objectCtrlSettings.vzletSystemList =tmpSystemList;
//                    };
//                };
//                $scope.getVzletSystemList();
                    //get devices
                $scope.getDevices = function(obj, showFlag){
                    objectSvc.getDevicesByObject(obj).then(
                        function(response){
                            //select only vzlet devices
                            var tmpArr = response.data;
                            tmpArr.forEach(function(elem){
                                if (angular.isDefined(elem.contObjectInfo) && (elem.contObjectInfo != null)){
                                    elem.contObjectId = elem.contObjectInfo.contObjectId;
                                };
                                if (angular.isDefined(elem.activeDataSource) && (elem.activeDataSource != null)){
                                    elem.subscrDataSourceId = elem.activeDataSource.subscrDataSource.id;
                                    elem.curDatasource = elem.activeDataSource.subscrDataSource;
                                    elem.subscrDataSourceAddr = elem.activeDataSource.subscrDataSourceAddr;
                                    elem.dataSourceTable1h = elem.activeDataSource.dataSourceTable1h;
                                    elem.dataSourceTable24h = elem.activeDataSource.dataSourceTable24h;
                                };
                            });
                            obj.devices = tmpArr;//response.data;
                            $scope.selectedItem(obj);
//console.log(obj);                            
                            if (showFlag == true){
                                $('#contObjectDevicesModal').modal();
                            };
//                            if (showFlag == false){
//                                $('#contObjectDevicesModal').modal();
//                            };
                        },
                        errorCallback/*function(error){
                            notificationFactory.errorInfo(error.statusText,error.description);
                        }*/
                    );
                };
                
                $scope.invokeHelp = function(){
                    alert('This is SPRAVKA!!!111');
                };
                
                $scope.deleteObjectInit = function(object){
                    $scope.selectedItem(object);
                    //generation confirm code
                    setConfirmCode();
                };
                
                $scope.deleteZpointInit = function(objId, zpointId){
                    //setConfirmCode();
                    $scope.selectedZpoint(objId, zpointId);
                    $scope.deleteObjectInit($scope.currentObject);
                };
                
                $scope.addObjectInit = function(){
//console.log("addObjectInit");                    
                    $scope.currentObject = {};
                    checkGeo();
                    $('#showObjOptionModal').modal();
                };
                
//                work with object devices
//                *******************************
                //get device models
                $scope.getDeviceModels = function(){
                    objectSvc.getDeviceModels().then(
                        function(response){
                            $scope.data.deviceModels = response.data;
            //console.log($scope.data.deviceModels);                
                        },
                        errorCallback/*function(error){
                            console.log(error);
                            notificationFactory.errorInfo(error.statusText,error.description);
                        }*/
                    );
                };
                $scope.getDeviceModels();

                       //get data sources
                var getDatasources = function(url){
                    var targetUrl = url;
                    $http.get(targetUrl)
                    .then(function(response){
                        var tmp = response.data;      
                        $scope.data.dataSources = tmp;
            //console.log(tmp);            
                    },
                          errorCallback/*function(e){
                        console.log(e);
                        notificationFactory.errorInfo(e.statusText,e.description);
                    }*/);
                };
                getDatasources(objectSvc.getDatasourcesUrl());
                
                $scope.deviceDatasourceChange = function(){
                    $scope.currentDevice.dataSourceTable1h = null;
                    $scope.currentDevice.dataSourceTable24h = null;
                    $scope.currentDevice.subscrDataSourceAddr = null;
                    var curDataSource = null;
                    $scope.data.dataSources.some(function(source){
                        if (source.id === $scope.currentDevice.subscrDataSourceId){
                            curDataSource = source;
                            return true;
                        };
                    });
                    $scope.currentDevice.curDatasource = curDataSource;       
                };
                
                $scope.selectDevice = function(device){
                    $scope.currentDevice = angular.copy(device);
//                    $scope.currentDevice.contObjectId = $scope.currentObject.id;
                };
                
                var setConfirmCode = function(){
                    $scope.confirmCode = null;
                    var tmpCode = mainSvc.getConfirmCode();
                    $scope.confirmLabel = tmpCode.label;
                    $scope.sumNums = tmpCode.result;                    
                };
                
                $scope.addDevice = function(){
                    $scope.currentDevice = {};
                    $scope.currentDevice.contObjectId = $scope.currentObject.id;
                    $('#showDeviceModal').modal();
                };
                
                $scope.deleteDeviceInit = function(device){
                    setConfirmCode();
                    $scope.selectDevice(device);
                    //$('#deleteDeviceModal').modal();
                };
                
                $scope.deleteObjectsInit = function(){
                    //generate confirm code
                    setConfirmCode();
                    
                    $scope.currentObject = {};
                    var tmpArr = [];
                    if ($scope.objectCtrlSettings.allSelected == true){
                        tmpArr = $scope.objects;
                        $scope.currentObject.deleteObjects = angular.copy($scope.objects);
                        //create array with objects ids 
                        $scope.currentObject.deleteObjectIds = $scope.objects.map(function(obj){return obj.id});
                        $scope.currentObject.countDeleteObjects = $scope.objects.length;
                        $('#deleteObjectModal').modal();
                    }else{
                        tmpArr = $scope.objectsOnPage;
                        var dcount = 0;
                        var dmas = [];
                        // object ids arr  for delete
                        var dmasIds = [];
                        tmpArr.forEach(function(el){
                            if (el.selected == true){
                                dmas.push(angular.copy(el));
                                dmasIds.push(el.id);
                                dcount += 1;
                            };
                        });
                        if(dcount>0){
                            $scope.currentObject.deleteObjects = dmas;
                            $scope.currentObject.deleteObjectIds = dmasIds;
                            $scope.currentObject.countDeleteObjects = dcount;
                            $('#deleteObjectModal').modal();
                        };
                    };
                };
                
                $scope.saveDevice = function(device){ 
                    //check device data
                    var checkDsourceFlag = true;
                    if (device.contObjectId == null){
                        notificationFactory.errorInfo("Ошибка","Не задан объект учета");
                        checkDsourceFlag = false;
                    };
                    if (device.subscrDataSourceId == null){
                        notificationFactory.errorInfo("Ошибка","Не задан источник данных");
                        checkDsourceFlag = false;
                    };
                    if (checkDsourceFlag === false){
                        return;
                    };
            //console.log(device);        
                    //send to server
                    objectSvc.sendDeviceToServer(device).then(successDeviceCallback, errorCallback);
                };

                $scope.deleteDevice = function(device){
            //console.log(device);        
                    var targetUrl = objectSvc.getRmaObjectsUrl();
                    targetUrl = targetUrl + "/" + device.contObjectInfo.contObjectId + "/deviceObjects/" + device.id;
                    $http.delete(targetUrl).then(successDeviceCallback, errorCallback);
                };
// **************************************************************************************************
                
//******************************* Work with subscribers ****************************************
// *********************************************************************************************                
                //    get subscribers
                var getClients = function(){
                    var targetUrl = $scope.objectCtrlSettings.clientsUrl;
                    $http.get(targetUrl)
                    .then(function(response){
                        response.data.forEach(function(el){
                            el.organizationName = el.organization.organizationFullName;
                        });
                        $scope.data.clients = response.data;
//console.log($scope.data.clients);            
                    },
                         function(e){
                        console.log(e);
                    });
                };
                
                getClients();
                
                                
               var deleteDoubleElements = function(targetArray){
                    var resultArray = angular.copy(targetArray);
                    resultArray = resultArray.sort();
                    var arrLength = resultArray.length;
                    while (arrLength >= 2){
                        arrLength--;                                               
                        if (resultArray[arrLength] == resultArray[arrLength-1]){                                          
                            resultArray.splice(arrLength, 1);
                        };
                    };                 
                    return resultArray;
                };
                
                var prepareClient = function(table, objIdsArr){
                    var ids = angular.copy(objIdsArr);
                    $http.get(table).then(function(response){
                        var subscrObjs = response.data;
                        var subscrObjIds = subscrObjs.map(function(obj){return obj.id});
                        Array.prototype.push.apply(ids, subscrObjIds);
                        ids = deleteDoubleElements(ids);
//console.log(table);                        
//console.log(ids);                        
                        $http.put(table, ids).then(successPostCallback, errorCallback);
                        },
                        function(e){
                            console.log(e);
                    }
                    );
                };
                
                //инициализируем переменные и интерфейсы для назначения объектов абонентам
                $scope.setClientsInit = function(object){
                    if (!mainSvc.checkUndefinedNull(object)){
                        object.selected = true;
                    };
                    $scope.data.clientsOnPage = angular.copy($scope.data.clients);
                    $('#setClientModal').modal();
                };
                
                //отправляем запрос на назначение на сервер
                $scope.setClients = function(){
                    //собираем идишники выбранных объектов в один массив
                    var tmp = [];
                    if ($scope.objectCtrlSettings.allSelected == true){
                        $scope.objects.forEach(function(elem){
                            if (elem.selected == true){
                                tmp.push(elem.id);
                                elem.selected = false;
                            };
                        });
                    }else{
                        $scope.objectsOnPage.forEach(function(elem){
                            if (elem.selected == true){
                                tmp.push(elem.id);
                                elem.selected = false;
                            };
                        });
                    };
                    //для каждого абонента надо вызвать rest для задания объектов, 
                    //передавая полученный массив идишников
                    $scope.data.clientsOnPage.forEach(function(cl){
                       if ((cl.id != null) && (typeof cl.id != 'undefined') && (cl.selected == true)){
                            var table = $scope.objectCtrlSettings.rmaUrl + "/" + cl.id + $scope.objectCtrlSettings.subscrObjectsSuffix;
                            prepareClient(table, tmp);
//                            $http.get(table).then(function(response){
//                                var subscrObjs = response.data;
//                                var subscrObjIds = subscrObjs.map(function(obj){return obj.id});
//                                $http.put(table, tmp).then(successCallback, errorCallback);
//                            },
//                                                  errorCallBack
//                            );
                            
                        }; 
                    });                    
                };
                
                $scope.selectAllClients = function(){
                    $scope.data.clientsOnPage.forEach(function(el){
                        el.selected = $scope.objectCtrlSettings.selectedAllClients;
                    });
                };
// ******************************* end subscriber region ***********************                
                
                //checkers            
                $scope.checkEmptyNullValue = function(numvalue){                    
                    var result = false;
                    if ((numvalue === "") || (numvalue == null)){
                        result = true;
                        return result;
                    }
                    return result;
                };
                
                function isNumeric(n) {
                  return !isNaN(parseFloat(n)) && isFinite(n);
                };
                
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
                    result = parseInt(numvalue) >= 0 ? true : false;
                    return result;
                };
                
                $scope.checkNumericInterval = function(leftBorder, rightBorder){  
                     if (($scope.checkEmptyNullValue(leftBorder)) || ( $scope.checkEmptyNullValue(rightBorder))){                                     
                         return false;
                     };
                     if (!(($scope.checkNumericValue(leftBorder)) && ( $scope.checkNumericValue(rightBorder)))){                         
                         return false;
                     };
                     if(parseInt(rightBorder) >= parseInt(leftBorder)){                        
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
                    if((zpointSettings == null) || (!zpointSettings.hasOwnProperty('summer')) || (!zpointSettings.hasOwnProperty('winter'))){
                        return true;
                    };
                    return $scope.checkPositiveNumberValue(zpointSettings.summer.ov_BalanceM_ctrl) && 
                        $scope.checkPositiveNumberValue(zpointSettings.winter.ov_BalanceM_ctrl) &&
                        $scope.checkHHmm(zpointSettings.summer.ov_Worktime) &&
                        $scope.checkHHmm(zpointSettings.winter.ov_Worktime) &&
                        $scope.checkPositiveNumberValue(zpointSettings.summer.leak_Gush) &&
                        $scope.checkPositiveNumberValue(zpointSettings.winter.leak_Gush) &&
                        $scope.checkPositiveNumberValue(zpointSettings.summer.leak_Night) &&
                        $scope.checkPositiveNumberValue(zpointSettings.winter.leak_Night) &&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_deltaT_min, zpointSettings.summer.wm_deltaT_max) &&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_deltaQ_min, zpointSettings.summer.wm_deltaQ_max) &&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_deltaT_min, zpointSettings.winter.wm_deltaT_max) &&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_deltaQ_min, zpointSettings.winter.wm_deltaQ_max) &&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_P2_min, zpointSettings.summer.wm_P2_max) &&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_P1_min, zpointSettings.summer.wm_P1_max) &&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_P2_min, zpointSettings.winter.wm_P2_max) &&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_P1_min, zpointSettings.winter.wm_P1_max) &&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_T2_min, zpointSettings.summer.wm_T2_max) &&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_T1_min, zpointSettings.summer.wm_T1_max) &&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_T2_min, zpointSettings.winter.wm_T2_max) &&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_T1_min, zpointSettings.winter.wm_T1_max) &&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_M2_min, zpointSettings.summer.wm_M2_max) &&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_M1_min, zpointSettings.summer.wm_M1_max) &&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_M2_min, zpointSettings.winter.wm_M2_max) &&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_M1_min, zpointSettings.winter.wm_M1_max);
                };
                
                $scope.checkObjectPropertiesForm = function(object){
//                    if ((object==null)||(object.cwTemp === undefined)){
                    if ((object == null) || (!object.hasOwnProperty('cwTemp')) || (!object.hasOwnProperty('heatArea'))){
                        return true;
                    };
                    return $scope.checkNumericValue(object.cwTemp) && ($scope.checkNumericValue(object.heatArea));
                };

                var checkGeo = function(){
                   $scope.currentObject.geoState = "red";
                   $scope.currentObject.geoStateText = "Не отображается на карте";
// console.log($scope.currentObject.isValidGeoPos);
// console.log($scope.currentSug);
// console.log($scope.currentSug.data.geo_lat);
// console.log($scope.currentSug.data.geo_lon);                
                   if ($scope.currentObject.isValidGeoPos || !mainSvc.checkUndefinedNull($scope.currentSug) && $scope.currentSug.data.geo_lat != null && $scope.currentSug.data.geo_lon != null){
                        $scope.currentObject.geoState = "green";
                        $scope.currentObject.geoStateText = "Отображается на карте";
                    }; 
                };
                
                $(document).ready(function(){
                    $('#inputTSNumber').inputmask();
                    $('#inputEXCode').inputmask();
                    $("#inputAddress").suggestions({
                        serviceUrl: "https://dadata.ru/api/v2",
                        token: "f9879c8518e9c9e794ff06a6e81eebff263f97d5",
                        type: "ADDRESS",
                        count: 10,
                        /* Вызывается, когда пользователь выбирает одну из подсказок */
                        onSelect: function(suggestion) {
                            console.log(suggestion);
                            $scope.currentObject.fullAddress = suggestion.value;
                            $scope.currentSug = suggestion;
                            $scope.currentObject.isAddressAuto = true;
                            checkGeo();
                            $scope.$apply();
                        },
                         /* Вызывается, когда получен ответ от сервера */
                        onSearchComplete: function(query, suggestions) {
                            // $scope.currentSug = null;
                            // if (angular.isArray(suggestions) && (suggestions.length > 0)){                               
                            //     $scope.currentSug = suggestions[0];
                            // };
                            // $scope.$apply();
                        }
                    });
                    $("#inputAddress").change(function(){
                        $scope.currentSug = null;
                        $scope.currentObject.isAddressAuto = false;
                        $scope.currentObject.isValidGeoPos = false;
                        checkGeo();
                        $scope.$apply();
                        // if (!mainSvc.checkUndefinedNull($scope.currentSug) && $scope.currentObject.isAddressAuto){                           
                        //     $scope.currentObject.fullAddress = $scope.currentSug.value;
                        //     $scope.$apply();        
                        // };
                    });
                });
//            }]
}]);