'use strict';
angular.module('portalNMC')
.controller('SettingsTenantsCtrl', ['$scope', '$rootScope', '$routeParams', '$resource', '$cookies', '$compile', '$parse', 'crudGridDataFactory', 'notificationFactory', '$http', 'subscrCabinetsSvc', 'mainSvc', '$timeout', '$window',
            function ($scope, $rootScope, $routeParams, $resource, $cookies, $compile, $parse, crudGridDataFactory, notificationFactory, $http, subscrCabinetsSvc, mainSvc, $timeout, $window) {
                $rootScope.ctxId = "settings_tenants_page";
//console.log('Run Object management controller.');  
//var timeDirStart = (new Date()).getTime();
                
                    //messages for user
                $scope.messages = {};
                
                $scope.messages.createTenants = "Создать пользователей";
                $scope.messages.recreateTenantPasswords = "Пересоздать пароли";
                $scope.messages.sendLoginInfoByEmail = "Отправить данные на эл. почту";
                $scope.messages.deleteLogins = "Удалить пользователей";
                $scope.messages.deleteLogin = "Удалить пользователя";                
                $scope.messages.viewProps = "Данные пользователя";                
                $scope.messages.createTenant = "Создать пользователя";
                $scope.messages.changeTenantPassword = "Сменить пароль";
 
                $scope.messages.markAllOn = "Выбрать все";
                $scope.messages.markAllOff = "Отменить все";
                $scope.messages.moveToNode = "Привязать к узлу";
                $scope.messages.releaseFromNode = "Отвязать от узла";
                
                    //object ctrl settings
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
                $scope.objectCtrlSettings.ctxId = "settings_tenants_2nd_menu_item";
                
                $scope.objectCtrlSettings.rmaUrl = "../api/rma";
                
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
                
//console.log(subscrCabinetsSvc.promise);                
                
                var performCabinetsData = function(response){
                    var tempArr = response.data;
                    var controlObjectsCopy = angular.copy($scope.objects);
                    if (angular.isArray(controlObjectsCopy) && controlObjectsCopy.length > 0){
                        tempArr.forEach(function(elem, index){
                            controlObjectsCopy.some(function(oldElem){
                                if (oldElem.contObjectInfo.contObjectId == elem.contObjectInfo.contObjectId){
                                    if (!mainSvc.checkUndefinedNull(oldElem.selected)){
                                        elem.selected = oldElem.selected;
                                    }
                                    return true;
                                };
                            });                            
                        });
                    };
//console.log(tempArr);                    
                    $scope.objects = response.data;
                    //sort by name
                    subscrCabinetsSvc.sortCabinetsByContObjectFullName($scope.objects);

                    if (angular.isUndefined($scope.filter) || ($scope.filter === "")){
                        $scope.objectsWithoutFilter = $scope.objects;
                        $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;
                        tempArr =  $scope.objects.slice(0, $scope.objectCtrlSettings.objectsPerScroll);
                        $scope.objectsOnPage = tempArr;                                                    
                        $rootScope.$broadcast('subscrCabinets:loaded');
//                        if (!mainSvc.checkUndefinedNull(objId)){
//                            moveToObject(objId);
//                        };
                    } else {
                        $scope.searchObjects($scope.filter);
                    };
                    $scope.loading = false;
                    $('#deleteCabinetModal').modal('hide');                                        
                };
                
                var successCabinetsCallback = function(response){
                    performCabinetsData(response);
                    notificationFactory.success();
                };
                
                var successUpdateCabinetCallback = function(response){
                    var result = angular.copy(response.data);
                    var searchIndex = -1;
                    $scope.objects.some(function(elem, index){                       
                        if (mainSvc.checkUndefinedNull(elem.cabinet) || mainSvc.checkUndefinedNull(elem.cabinet.subscrUser)){
                            return false;
                        };                        
                        if (elem.cabinet.subscrUser.id == result.id){
                            searchIndex = index;
//                            elem.cabinet.subscrUser = result;
                            return true;
                        };
                    });                   
                    if (searchIndex > -1){
                        $scope.objects[searchIndex].cabinet.subscrUser = result;
                        
                    };
                    
                    searchIndex = -1;
                    $scope.objectsOnPage.some(function(elem, index){
                        if (mainSvc.checkUndefinedNull(elem.cabinet) || mainSvc.checkUndefinedNull(elem.cabinet.subscrUser)){
                            return false;
                        };
                        if (elem.cabinet.subscrUser.id == result.id){
                            searchIndex = index;
//                            elem.cabinet.subscrUser = result;
                            return true;
                        };
                    });                    
                    if (searchIndex > -1){
                        $scope.objectsOnPage[searchIndex].cabinet.subscrUser = result;
                    };                   
                    $('#createPasswordModal').modal('hide');
                    $('#showTenantOptionModal').modal('hide');
                    notificationFactory.success();
                };
                
                var getCabinetsData = function(objId){
//console.log("getCabinetsData");
                    $rootScope.$broadcast('subscrCabinets:requestReloadCabinetsData');
                    $scope.loading = true;
                    subscrCabinetsSvc.getPromise().then(performCabinetsData);
                };
                
                $scope.selectCabinet = function(cabinet){
                    $scope.data.selectedCabinet = angular.copy(cabinet);
                };

//                $scope.objects = subscrCabinetsSvc.getObjects();
                $scope.loading = subscrCabinetsSvc.getLoadingStatus();//loading;
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
                
                var prepareObjectsIdsArray = function(){
//console.log($scope.objectsOnPage);                    
//console.log($scope.objects);                    
                    var tmp = [];
                    $scope.objects.forEach(function(elem){
                        if (elem.selected == true){
                            tmp.push(elem.contObjectInfo.contObjectId);                                
                        };
                    });
//                    if ($scope.objectCtrlSettings.allSelected == true){
//                        
//                    }else{
//                        $scope.objectsOnPage.forEach(function(elem){
//                            if (elem.selected == true){
//                                tmp.push(elem.contObjectInfo.contObjectId);                                
//                            };
//                        });
//                    };
                    return tmp;
                };
                
                var prepareCabinetsIdsArray = function(){
                    var tmp = [];
                    var srcArray = [];
                    srcArray = $scope.objects;
//                    if ($scope.objectCtrlSettings.allSelected == true){
//                        srcArray = $scope.objects;
//                    }else{
//                        srcArray = $scope.objectsOnPage;
//                    };                    
                    srcArray.forEach(function(elem){
                        if (elem.selected == true){
                            if (!mainSvc.checkUndefinedNull(elem.cabinet)){
                                tmp.push(elem.cabinet.id);                                
                            };
                        };
                    });                    
                    return tmp;
                };
                
                var prepareUserIdsArray = function(){
                    var tmp = [];
                    var srcArray = [];
                    srcArray = $scope.objects;
//                    if ($scope.objectCtrlSettings.allSelected == true){
//                        srcArray = $scope.objects;
//                    }else{
//                        srcArray = $scope.objectsOnPage;
//                    };                    
                    srcArray.forEach(function(elem){
                        if (elem.selected == true){
                            if (!mainSvc.checkUndefinedNull(elem.cabinet) && !mainSvc.checkUndefinedNull(elem.cabinet.subscrUser)){
                                tmp.push(elem.cabinet.subscrUser.id);                                
                            };
                        };
                    });                    
                    return tmp;
                };
                
                $scope.createCabinets = function(obj) {
                    var objectIds = [];
                    if (mainSvc.checkUndefinedNull(obj)){
                        objectIds = prepareObjectsIdsArray();
                    }else{
                        objectIds.push(obj.contObjectInfo.contObjectId);
                    }
                    if (objectIds.length == 0){
                        return "Cabinet array is empty."
                    };
                    subscrCabinetsSvc.createCabinets(objectIds).then(successCabinetsCallback, errorCallback);
                };
                
                $scope.deleteCabinetsInit = function(obj){
                    //generation confirm code
                    setConfirmCode();
                    $scope.data.selectedCabinet = {};
                    var cabinetIds = [];
                    if (mainSvc.checkUndefinedNull(obj)){
                        cabinetIds = prepareCabinetsIdsArray();                        
                    }else{
                        $scope.selectCabinet(obj);
                        if (!mainSvc.checkUndefinedNull(obj.cabinet)){
                            cabinetIds.push(obj.cabinet.id);                                
                        };                        
                    }
                    $scope.data.selectedCabinet.cabinetsIdsForDelete = cabinetIds;
                    
                };
                
                $scope.deleteCabinets = function(obj) {
                    if (obj.cabinetsIdsForDelete.length == 0){
                        return "Cabinet array is empty."
                    };
                    subscrCabinetsSvc.deleteCabinets(obj.cabinetsIdsForDelete).then(successCabinetsCallback, errorCallback);
                };
                
                $scope.resetPasswords = function(obj){
                    //generation confirm code
//                    setConfirmCode();
                    $scope.data.selectedCabinet = {};
                    var userIds = [];
                    if (mainSvc.checkUndefinedNull(obj)){
                        userIds = prepareUserIdsArray();                        
                    }else{
                        $scope.selectCabinet(obj);
                        if (!mainSvc.checkUndefinedNull(obj.cabinet)){
                            userIds.push(obj.cabinet.subscrUser.id);                                
                        };                        
                    }
//                    $scope.data.selectedCabinet.cabinetsIdsForDelete = cabinetIds;
                    subscrCabinetsSvc.resetPassword(userIds).then(successCabinetsCallback, errorCallback);
                };
                
                $scope.checkPasswordFields = function(cabinet){
                    if (mainSvc.checkUndefinedNull(cabinet) || mainSvc.checkUndefinedNull(cabinet.cabinet) || mainSvc.checkUndefinedNull(cabinet.cabinet.subscrUser)){
                        return false;
                    };
                    var result = true;
                    if ($scope.emptyString(cabinet.cabinet.subscrUser.passwordPocket)){                       
                        result = false;
                    };
                    if (cabinet.cabinet.subscrUser.passwordPocket != cabinet.cabinet.subscrUser.passwordPocketConfirm){                        
                        result = false;
                    };
                    return result;
                };
                
                $scope.checkPassword = function(cabinet){
                    if (mainSvc.checkUndefinedNull(cabinet) || mainSvc.checkUndefinedNull(cabinet.cabinet) || mainSvc.checkUndefinedNull(cabinet.cabinet.subscrUser)){
                        return false;
                    };
                    var result = true;
                    if ($scope.emptyString(cabinet.cabinet.subscrUser.passwordPocket)){
                        notificationFactory.errorInfo("Ошибка", "Пароль не должен быть пустым!");
                        result = false;
                    };
                    if (cabinet.cabinet.subscrUser.passwordPocket != cabinet.cabinet.subscrUser.passwordPocketConfirm){
                        notificationFactory.errorInfo("Ошибка", "Поля \"Пароль\" и \"Подтверждение пароля\" не совпадают!");
                        result = false;
                    };
                    return result;
                };
                
                $scope.updateCabinet = function(cabinet, passwordFlag){
                    if (mainSvc.checkUndefinedNull(passwordFlag) || passwordFlag != true){
                        cabinet.cabinet.subscrUser.passwordPocket = null;
                    };
                    subscrCabinetsSvc.updateCabinet(cabinet).then(successUpdateCabinetCallback, errorCallback);
                };
                
                $scope.createPassword = function(cabinet){
                    if (mainSvc.checkUndefinedNull(cabinet)){
                        return "Cabinet is null or undefined.";
                    };
                    if ($scope.checkPassword(cabinet) == false){return "User password is incorrect!"};
                    $scope.updateCabinet(cabinet, true);
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
                
                var checkEmail = function(cabinet, messageFlag){                   
                    var result = true;
                    if (mainSvc.checkUndefinedNull(cabinet.cabinet.subscrUser.contactEmail)){
                        
                        if (messageFlag == true) {
                            notificationFactory.errorInfo("Ошибка", "Не задана эл. почта пользователя");
                        };
                        result = false;
                    };
                    return result;
                };
                
                var checkEmailsAndViewWarning = function(){
                    var warningMessage = "У пользователей:\n";
                    $scope.objects.forEach(function(elem){
                        if (elem.selected == true && !checkEmail(elem, false)){
                            warningMessage += "- " + elem.cabinet.subscrCabinetNr + "\n";
                        };
                    });
                    if (warningMessage != "У пользователей:\n"){
                        warningMessage = "У некоторых пользователей не задана эл. почта. Отправить им письма невозможно. Задайте им эл. почту и повторите отправку.";
                        notificationFactory.warning(warningMessage);
                    };
                };
                
                var checkPasswordForSend = function(cabinet, messageFlag){
                    var result = true;
                    if (mainSvc.checkUndefinedNull(cabinet.cabinet.subscrUser.passwordPocket)){
                        if (messageFlag == true) {
                            notificationFactory.errorInfo("Ошибка", "У пользователя пароль был изменен. Отправка на эл. почту невозможна. Если пароль был утерян, то сгенерируйте ему новый и повторите отправку.");
                        };
                        result = false;
                    };
                    return result;
                };
                
                var checkPasswordsBeforeSendAndViewWarning = function(){
                    var warningMessage = "У пользователей:\n";
                    $scope.objects.forEach(function(elem){
                        if (elem.selected == true && !checkPasswordForSend(elem, false)){
                            warningMessage += "- " + elem.cabinet.subscrCabinetNr + "\n";
                        };
                    });
                    if (warningMessage != "У пользователей:\n"){
                        warningMessage = "У некоторых пользователей пароли были изменены. Отправка на эл. почту невозможна. Если пароли были утеряны, то сгенерируйте им новые и повторите отправку.";
                        notificationFactory.warning(warningMessage);
                    };
                };
                
                $scope.sendLDByEmail = function(obj){
                    $scope.data.selectedCabinet = {};
                    var userIds = [];
                    if (mainSvc.checkUndefinedNull(obj)){                        
                        checkPasswordsBeforeSendAndViewWarning();
                        checkEmailsAndViewWarning();
                        userIds = prepareUserIdsArray();                        
                    }else{
                        if (!checkPasswordForSend(obj, true) || !checkEmail(obj, true)){
                            return "User data is incorrect!";
                        };
                        $scope.selectCabinet(obj);
                        if (!mainSvc.checkUndefinedNull(obj.cabinet)){
                            userIds.push(obj.cabinet.subscrUser.id);                                
                        };                        
                    }
//                    $scope.data.selectedCabinet.cabinetsIdsForDelete = cabinetIds;
                    subscrCabinetsSvc.sendLDByEmail(userIds).then(function(){
                        notificationFactory.success();
                    }, errorCallback);
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
                    var curObj = subscrCabinetsSvc.findObjectById(Number(objId), $scope.objects);
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
                    if (curInd != -1){
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
                    $rootScope.$broadcast('subscrCabinets:requestReloadCabinetsData');
console.log(e);
//console.log($scope.currentObject);                    
                    $scope.currentObject._activeContManagement = e._activeContManagement;

                    var objIndex = null;
                    objIndex = findObjectIndexInArray(e.id, $scope.objects);
                    if (objIndex != null) {$scope.objects[objIndex] = e};
                    objIndex = null;
                    objIndex = findObjectIndexInArray(e.id, $scope.objectsOnPage);
                    if (objIndex != null) {$scope.objectsOnPage[objIndex] = e};
//                    $scope.currentObject = {};
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
                
                var successPostCallback = function (e) {                  
                    successCallback(e, null);
                    if ($scope.objectCtrlSettings.isTreeView == false || mainSvc.checkUndefinedNull($scope.data.currentTree)){
                        getCabinetsData(e.id);
                    }else{
                    //if tree is on
                        if (mainSvc.checkUndefinedNull($scope.data.selectedNode.type) || $scope.data.selectedNode.type != 'root'){
                            $scope.data.selectedNodeForMove = angular.copy($scope.data.selectedNode);
                            $scope.data.treeForMove = angular.copy($scope.data.currentTree);
                            $scope.data.treeForMove.childObjectList.shift();
                            $scope.data.treeForMove.movingObjects = [e.id];
                            $scope.moveToNode();
                        }else{
                            $scope.loadTree($scope.data.currentTree, e.id);                    
                        }
                    };
                    
                };
                
                var errorProtoCallback = function(e){
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

                var errorCallback = function (e) { 
                    errorProtoCallback(e);                    
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
                    var url = subscrCabinetsSvc.getRmaObjectsUrl();                 
                    if (angular.isDefined(obj) && (angular.isDefined(obj.id)) && (obj.id != null)){
                        crudGridDataFactory(url).delete({ id: obj[$scope.extraProps.idColumnName] }, successDeleteCallback, errorCallback);
                    };
                };
                
                $scope.deleteObjects = function(obj){
                    var url = subscrCabinetsSvc.getRmaObjectsUrl();                   
                    if (angular.isDefined(obj) && (angular.isDefined(obj.id)) && (obj.id != null)){
                        crudGridDataFactory(url).delete({ id: obj[$scope.extraProps.idColumnName] }, successDeleteCallback, errorCallback);
                    }else if (angular.isDefined(obj.deleteObjects) && (obj.deleteObjects != null) && angular.isArray(obj.deleteObjects)){
                        crudGridDataFactory(url).delete({ contObjectIds: obj.deleteObjectIds }, successDeleteObjectsCallback, errorCallback);
                    };
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
                    if ($scope.emptyString(obj.timezoneDefKeyname) && (obj.isLightForm != true)){
                        notificationFactory.errorInfo("Ошибка", "Не задан часовой пояс объекта!");
                        result = false;
                    };
                    if ($scope.checkUndefinedNull(obj.contManagementId) && (obj.isLightForm != true)){
                        notificationFactory.errorInfo("Ошибка", "Не задана управляющая компания!");
                        result = false;
                    };
                    if ($scope.emptyString(obj.currentSettingMode) && (obj.isLightForm != true)){
                        notificationFactory.errorInfo("Ошибка", "Не задан режим функционирования!");
                        result = false;
                    };
                    return result;
                };
                
                $scope.sendObjectToServer = function(obj){
                    obj.isSaving = true;
                    if(!checkObjectSettings(obj)){
                        obj.isSaving = false;
                        return false;
                    };
                    var url = subscrCabinetsSvc.getRmaObjectsUrl();                    
                    if (angular.isDefined(obj.id) && (obj.id != null)){
                        $scope.updateObject(url, obj);
                    }else{
//                        obj.timezoneDefKeyname = "MSK";
                        $scope.addObject(url, obj);
                    };
                };
              
                $scope.selectedItem = function (item) {
			        var curObject = angular.copy(item);
			        $scope.currentObject = curObject;                    
			    };
                
                $scope.selectedObject = function(objId, isLightForm){
                    subscrCabinetsSvc.getRmaObject(objId)
                    .then(function(resp){
                        $scope.currentObject = resp.data;
//console.log($scope.currentObject);                        
                        if (angular.isDefined($scope.currentObject._activeContManagement) && 
                            ($scope.currentObject._activeContManagement != null)){
                                $scope.currentObject.contManagementId = $scope.currentObject._activeContManagement.organization.id;
                        };
                        if (!mainSvc.checkUndefinedNull(isLightForm)){
                            $scope.currentObject.isLightForm = isLightForm;
                        };
                        checkGeo();
                    }, function(error){
                        console.log(error);
                    });
                };
                
                $scope.toggleShowGroupDetails = function(objId){//switch option: current goup details
                    var curObject = subscrCabinetsSvc.findObjectById(objId, $scope.objects);//null;                
                    //if cur object = null => exit function
                    if (curObject == null){
                        return;
                    };
                    //else            
                    if ((curObject.showGroupDetails == true)){                        
                        curObject.showGroupDetails = true;
                    }else{                       
                        curObject.showGroupDetails = !curObject.showGroupDetails;
                    };                                           
                    
                };
                
                $scope.dateFormat = function(millisec){
                    var result = "";
                    var serverTimeZoneDifferent = Math.round($scope.objectCtrlSettings.serverTimeZone*3600.0*1000.0);
                    var tmpDate = (new Date(millisec+serverTimeZoneDifferent));       
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
        
                //$scope.checkString  
                $scope.emptyString = function(str){
                    return mainSvc.checkUndefinedEmptyNullValue(str);
                };
                $scope.checkUndefinedNull = function(val){
                    return mainSvc.checkUndefinedNull(val);
                };
                
                // search objects
                $scope.searchObjects = function(searchString){
//console.log(searchString);                    
                    if (($scope.objects.length <= 0)){
                        return;
                    };              
                    
                    if (angular.isUndefined(searchString) || (searchString === '')){                      
                        var tempArr = [];
                        $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;
                        tempArr = $scope.objects.slice(0, $scope.objectCtrlSettings.objectsPerScroll);
                        $scope.objectsOnPage = tempArr;
                    }else{
//                        $scope.objectsOnPage = $scope.objects;
                        var tempArr = [];                        
                        $scope.objects.forEach(function(elem){                
                            if (angular.isDefined(elem.contObjectInfo.fullName) && elem.contObjectInfo.fullName.toUpperCase().indexOf(searchString.toUpperCase()) != -1){
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
                
                $scope.isSystemViewInfo = function () {
                    return mainSvc.getViewSystemInfo();
                }
                
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
                
                $scope.invokeHelp = function(){
                    alert('This is SPRAVKA!!!111');
                };
                
                $scope.deleteObjectInit = function(object){
                    $scope.selectedItem(object);
                    //generation confirm code
                    setConfirmCode();
                };
                
                $scope.addObjectInit = function(isLightForm){
//console.log("addObjectInit");                    
                    $scope.currentObject = {};
                    $scope.currentObject.isLightForm = isLightForm;
                    checkGeo();
                    $('#showObjOptionModal').modal();
                    $('#showObjOptionModal').css("z-index", "1041");
//console.log($scope.currentObject);                    
                };
                
                var setConfirmCode = function(useImprovedMethodFlag){
                    $scope.confirmCode = null;
                    var tmpCode = mainSvc.getConfirmCode(useImprovedMethodFlag);
                    $scope.confirmLabel = tmpCode.label;
                    $scope.sumNums = tmpCode.result;                    
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
                   if ($scope.currentObject.isValidGeoPos || !mainSvc.checkUndefinedNull($scope.currentSug) && $scope.currentSug.data.geo_lat != null && $scope.currentSug.data.geo_lon != null){
                        $scope.currentObject.geoState = "green";
                        $scope.currentObject.geoStateText = "Отображается на карте";
                    }; 
                };
                
                $(document).ready(function(){
                    $('#inputTSNumber').inputmask();
                    $('#inputEXCode').inputmask();
                    $("#inputObjAddress").suggestions({
                        serviceUrl: "https://dadata.ru/api/v2",
                        token: "f9879c8518e9c9e794ff06a6e81eebff263f97d5",
                        type: "ADDRESS",
                        count: 10,
                        /* Вызывается, когда пользователь выбирает одну из подсказок */
                        onSelect: function(suggestion) {
//                            console.log(suggestion);
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
                    $("#inputObjAddress").change(function(){
                        $scope.currentSug = null;
                        $scope.currentObject.isAddressAuto = false;
                        $scope.currentObject.isValidGeoPos = false;
                        checkGeo();
                        $scope.$apply();
                    });
                });
                
                $('#showObjOptionModal').on('hidden.bs.modal', function(){
                    $scope.currentObject.isSaving = false;
                    $scope.currentSug = null;
                });         
                
// ********************************************************************************************
                //  TREEVIEW
//*********************************************************************************************
                $scope.data.treeTemplates = subscrCabinetsSvc.getRmaTreeTemplates();
                $scope.objectCtrlSettings.treeSettings = {};
                $scope.objectCtrlSettings.treeSettings.nodesPropName = 'childObjectList'; //'objectName'
                $scope.data.currentDeleteMessage = "";

                var ROOT_NODE = {
                    objectName: "",
                    type: "root",
                    childObjectList: []
                };
                
                $scope.objectCtrlSettings.isTreeView = false;
                $scope.data.currentTree = {};
                $scope.data.newTree = {};
                
                var findNodeInTree = function(node, tree){
                    return mainSvc.findNodeInTree(node, tree);
                };
                
                $scope.findNodeInTree = function(node, tree){//wrapper for testing
                    return mainSvc.findNodeInTree(node, tree);
                };
                
                $scope.selectNode = function(item){                    
                    var treeForSearch = $scope.data.currentTree;
                    var selectedNode = $scope.data.selectedNode;
                    if ($scope.objectCtrlSettings.isObjectMoving == true){
                        treeForSearch = $scope.data.treeForMove;
                        if (mainSvc.checkUndefinedNull($scope.data.selectedNodeForMove)){
                            item.isSelected = true;                    
                            $scope.data.selectedNodeForMove = angular.copy(item);
                            return ;
                        };
                        selectedNode = $scope.data.selectedNodeForMove;
                    };
                    if (selectedNode.id == item.id || selectedNode.type == item.type == 'root'){                       
                        return ;
                    };             
                    if (!mainSvc.checkUndefinedNull(selectedNode)){                        
                        var preNode = findNodeInTree(selectedNode, treeForSearch);
                        if (!mainSvc.checkUndefinedNull(preNode)){
                            preNode.isSelected = false;
                        }
                    };
                    
                    item.isSelected = true;                    
                    if ($scope.objectCtrlSettings.isObjectMoving == true){
                        $scope.data.selectedNodeForMove = angular.copy(item);
                    }else{
                        $scope.data.selectedNode = angular.copy(item); 
//                        $rootScope.$broadcast('subscrCabinetsSvc:requestReloadData');
                        $scope.loading = true;
                        if (item.type == 'root'){
                            subscrCabinetsSvc.loadFreeObjectsByTree($scope.data.currentTree.id).then(performCabinetsData);
                        }else{
                            subscrCabinetsSvc.loadObjectsByTreeNode($scope.data.currentTree.id, item.id).then(performCabinetsData);
                        };
                    };                    
                };
                
                $scope.data.trees = [];
                
                $scope.loadTree = function(tree, objId){
//                    $rootScope.$broadcast('subscrCabinetsSvc:requestReloadData');
                    $scope.loading = true;
                    subscrCabinetsSvc.loadTree(tree.id).then(function(resp){
                            $scope.messages.treeMenuHeader = tree.objectName || tree.id; 
                            var respTree = angular.copy(resp.data);
                            respTree.childObjectList.unshift(angular.copy(ROOT_NODE));
                            $scope.data.currentTree = respTree;
                            respTree.childObjectList[0].isSelected = true;
                            $scope.data.selectedNode = angular.copy(respTree.childObjectList[0]);
                            subscrCabinetsSvc.loadFreeObjectsByTree(tree.id).then(performCabinetsData);
                            if (mainSvc.checkUndefinedNull($scope.data.currentTree.templateId)){
                                return "Tree is free";
                            };
                            subscrCabinetsSvc.loadTreeTemplateItems($scope.data.currentTree.templateId).then(function(resp){
                                $scope.data.currentTreeTemplateItems = angular.copy(resp.data);
                                respTree.childObjectList[0].templateId = $scope.data.currentTree.templateId;
                                respTree.childObjectList[0].templateItemId = $scope.data.currentTree.templateItemId;
                            }, errorProtoCallback);
//                            $scope.data.trees.push(respTree);
                        }, errorProtoCallback);
                };
                
                var loadTrees = function(){
                    subscrCabinetsSvc.loadTrees().then(function(resp){
                        mainSvc.sortItemsBy(resp.data, "objectName");
                        $scope.data.trees = angular.copy(resp.data);
                        if (!angular.isArray($scope.data.trees) || $scope.data.trees.length <=0){ 
                            $scope.messages.treeMenuHeader = "Выберете дерево";
                            getCabinetsData();
                            return "Object tree array is empty.";
                        }                        
                        $scope.loadTree($scope.data.trees[0]);                        
                    }, errorProtoCallback);
                };                
                
                var performNewNode = function(newNode, parent){
                    var trueParentRoot = null;
                    if (parent.type == 'root'){
                        trueParentRoot = $scope.data.currentTree;
                    }else{
                        trueParentRoot = parent;
                    };
                    if (mainSvc.checkUndefinedNull(trueParentRoot) || !angular.isArray(trueParentRoot.childObjectList)){
                        return "Parent is incorrect node!"
                    };
                    trueParentRoot.childObjectList.push(newNode);                 
                };
                
                $scope.saveNode = function(newNode, parent, anotherNodeFlag){
                    var isCheckTree = checkTree(newNode);      
                    if (isCheckTree == false){
                        return "Node is no correct!";
                    };
                    performNewNode(newNode, parent);
                    // remove temporary root node before update tree
                    if (angular.isArray($scope.data.currentTree.childObjectList) && 
                        ($scope.data.currentTree.childObjectList.length > 0) && 
                        ($scope.data.currentTree.childObjectList[0].type == "root"))
                    {
                        $scope.data.currentTree.childObjectList.shift();
                    };
                    subscrCabinetsSvc.updateTree($scope.data.currentTree).then(function(resp){                      
                        var respTree = resp.data;
                        respTree.childObjectList.unshift(angular.copy(ROOT_NODE));
                        if (!mainSvc.checkUndefinedNull(respTree.templateId)){
                            respTree.childObjectList[0].templateId = respTree.templateId;
                        };
                        if (!mainSvc.checkUndefinedNull(respTree.templateItemId)){
                            respTree.childObjectList[0].templateItemId = respTree.templateItemId;
                        };
                        var findIndex = -1;                        
                        $scope.data.trees.some(function(tree, index){
                            if (tree.id == respTree.id){
                                findIndex = index;
                                return true;
                            };
                        });
                        if (findIndex != -1){
                            $scope.data.trees[findIndex] = respTree;
                            $scope.data.currentTree = respTree;
                        };
                        if (mainSvc.checkUndefinedNull(anotherNodeFlag) || anotherNodeFlag != true){
                            $('#treeLevelModal').modal('hide');
                        }else{
                            $scope.data.parentLevel = findNodeInTree(parent, $scope.data.currentTree);
                            $scope.data.currentLevel = {childObjectList: []};
                            if (!mainSvc.checkUndefinedNull($scope.data.parentLevel.templateItemId)){
                                var parentLevel = findTemplateItemById($scope.data.parentLevel.templateItemId);
                                var currentLevel = findTemplateItemByItemLevel(parentLevel.itemLevel + 1);
                                if (currentLevel == null){
                                    return "Template item is no find.";
                                };
                                $scope.data.currentLevel.objectName = currentLevel.itemName;
                                $scope.data.currentLevel.templateId = currentLevel.templateId;
                                $scope.data.currentLevel.templateItemId = currentLevel.id;
                            };
                            $('#inputLevelName').focus();
                        };
                        //update tree in array
                    }, errorProtoCallback);
                    
                };
                
                var checkTree = function(tree){                   
                    var result = true;
                    if ($scope.emptyString(tree.objectName)){
                        notificationFactory.errorInfo("Ошибка", "Не задано наименование!");
                        result = false;
                    };
                    return result;
                };
                
                $scope.saveTree = function(tree){
                    var isCheckTree = checkTree(tree);      
                    if (isCheckTree == false){
                        return "Tree is no correct!";
                    };
                        // remove temporary root node
                    if (angular.isArray(tree.childObjectList) && (tree.childObjectList.length > 0) && (tree.childObjectList[0].type == "root")){
                        tree.childObjectList.shift();
                    };
                    subscrCabinetsSvc.createTree(tree).then(function(resp){
                        var createdTree = angular.copy(resp.data);
                        createdTree.childObjectList.unshift(angular.copy(ROOT_NODE));
                        if (!mainSvc.checkUndefinedNull(createdTree.templateId)){
                            createdTree.childObjectList[0].templateId = createdTree.templateId;
                        };
                        if (!mainSvc.checkUndefinedNull(createdTree.templateItemId)){
                            createdTree.childObjectList[0].templateItemId = createdTree.templateItemId;
                        };
                        $scope.data.trees.push(createdTree);
                        $scope.loadTree(createdTree);
                        $('#showTreeOptionModal').modal('hide');
                    }, errorProtoCallback);                    
                };
                
                $scope.removeNodeInit = function(node){
                    $scope.data.currentDeleteItem = node;
                    if (node.type == 'root'){
                        $scope.data.currentDeleteMessage = $scope.data.currentTree.objectName || $scope.data.currentTree.id;                     
                    }else{
                        $scope.data.currentDeleteMessage = node.objectName || node.id;
                    };
                    setConfirmCode(true);
                    $scope.deleteHandler = function(delItem){
                        if (mainSvc.checkUndefinedNull(delItem)){
                            return "Deleting item is undefined or null."
                        };
                        if (!mainSvc.checkUndefinedNull(delItem.type) && delItem.type == 'root'){
                            subscrCabinetsSvc.deleteTree($scope.data.currentTree.id).then(function(resp){
                                loadTrees();
                            }, errorProtoCallback);                            
                        }else{
                            subscrCabinetsSvc.deleteTreeNode($scope.data.currentTree.id, delItem.id).then(function(resp){
                                $scope.loadTree($scope.data.currentTree);
                            }, errorCallback);
                        };
                        $("#deleteWindowModal").modal('hide');
                    };
                    $("#deleteWindowModal").modal();
                };
                
                $scope.moveToNodeInit = function(object){
                    $scope.data.selectedNodeForMove = null;
                    var tmpMovingObjectArr = [];
                    if (!mainSvc.checkUndefinedNull(object)){
                        $scope.selectedItem(object);
                        tmpMovingObjectArr.push(object.id);
                    }else{
                        tmpMovingObjectArr = prepareObjectsIdsArray();                        
//                        $scope.objectCtrlSettings.anySelected = false;
                    };
                    $scope.data.treeForMove = angular.copy($scope.data.currentTree);
                    $scope.data.treeForMove.childObjectList.shift();
                    $scope.data.treeForMove.movingObjects = tmpMovingObjectArr;
                }
                
                $scope.moveToNode = function(){
                    subscrCabinetsSvc.putObjectsToTreeNode($scope.data.currentTree.id, $scope.data.selectedNodeForMove.id, $scope.data.treeForMove.movingObjects).then(function(resp){
                        $scope.loading = true;
                        if ($scope.data.selectedNode.type == 'root'){
                            subscrCabinetsSvc.loadFreeObjectsByTree($scope.data.currentTree.id).then(performCabinetsData);
                        }else{
                            subscrCabinetsSvc.loadObjectsByTreeNode($scope.data.currentTree.id, $scope.data.selectedNode.id).then(performCabinetsData);
                        };
                        $('#viewTreeModal').modal('hide');
                    }, errorCallback);
                };
                
                $scope.releaseFromNode = function(object){
                    var tmpMovingObjectArr = [];
                    if (!mainSvc.checkUndefinedNull(object)){
                        $scope.selectedItem(object);
                        tmpMovingObjectArr.push(object.id);
                    }else{
                        tmpMovingObjectArr = prepareObjectsIdsArray();                        
//                        $scope.objectCtrlSettings.anySelected = false;
                    };                   
                    subscrCabinetsSvc.releaseObjectsFromTreeNode($scope.data.currentTree.id, $scope.data.selectedNode.id, tmpMovingObjectArr).then(function(resp){
                        $scope.loading = true;
                        if ($scope.data.selectedNode.type == 'root'){
                            subscrCabinetsSvc.loadFreeObjectsByTree($scope.data.currentTree.id).then(performCabinetsData);
                        }else{
                            subscrCabinetsSvc.loadObjectsByTreeNode($scope.data.currentTree.id, $scope.data.selectedNode.id).then(performCabinetsData);
                        };
                    }, errorProtoCallback);
                };
                
                $scope.toggleTreeView = function(){
                    $scope.objectCtrlSettings.isTreeView = !$scope.objectCtrlSettings.isTreeView;
                    //if tree is off
                    if ($scope.objectCtrlSettings.isTreeView == false){
                        getCabinetsData();
                    }else{
                    //if tree is on
                        loadTrees();                    
                    };
                };
                
                $scope.createTreeByTemplate = function(template){
                    subscrCabinetsSvc.loadTreeTemplateItems(template.id).then(function(resp){
                        $scope.data.currentTreeTemplateItems = angular.copy(resp.data);
                        var treeItemLevel = null;
                        $scope.data.currentTreeTemplateItems.some(function(item){
                            if(item.itemLevel == 0){
                                treeItemLevel = item;
                                return true;
                            };
                        });
                        if (treeItemLevel == null){
                            return "Incorrect template. itemLevel == 0 is absent.";
                        };
                        $scope.data.newTree = {};
                        $scope.data.newTree.templateId = template.id;
                        $scope.data.newTree.templateItemId = treeItemLevel.id;
                        $scope.data.newTree.objectName = treeItemLevel.itemName;
                        $("#showTreeOptionModal").modal();
                        
                    }, errorProtoCallback);
                };
                
                var findTemplateItemById = function(templId){
                    var result = null;
                    $scope.data.currentTreeTemplateItems.some(function(item){
                        if (templId == item.id){
                            result = item;
                            return true;
                        }
                    });
                    return result;
                };
                
                var findTemplateItemByItemLevel = function(level){
                    var result = null;
                    $scope.data.currentTreeTemplateItems.some(function(item){
                        if (level == item.itemLevel){
                            result = item;
                            return true;
                        }
                    });
                    return result;
                };
                
                $scope.addNodeDisabled = function(curItem){                
                    if (mainSvc.checkUndefinedNull(curItem.templateItemId)){                    
                        return false;
                    };
                    if (!angular.isArray($scope.data.currentTreeTemplateItems)){               
                        return true;
                    };
                    var currentLevel = findTemplateItemById(curItem.templateItemId);
                    if (currentLevel == null){                                         
                        return false;
                    };
                    if (currentLevel.itemLevel < ($scope.data.currentTreeTemplateItems.length - 1)){                                             
                        return false;
                    };              
                    return true;
                };
                
                $scope.createNodeInit = function(item){
//console.log(item);                    
                    $scope.data.parentLevel = item;                    
                    $scope.data.currentLevel = {childObjectList: []};
                    if (!mainSvc.checkUndefinedNull(item.templateItemId)){
                        var parentLevel = findTemplateItemById(item.templateItemId);
                        var currentLevel = findTemplateItemByItemLevel(parentLevel.itemLevel + 1);
                        if (currentLevel == null){
                            return "Template item is no find.";
                        };
                        $scope.data.currentLevel.objectName = currentLevel.itemName;
                        $scope.data.currentLevel.templateId = currentLevel.templateId;
                        $scope.data.currentLevel.templateItemId = currentLevel.id;
                    };
                };
                
                $('#viewTreeModal').on('shown.bs.modal', function(){
                    $scope.objectCtrlSettings.isObjectMoving = true;
                    $scope.$apply();
                });
                
                $('#viewTreeModal').on('hidden.bs.modal', function(){
                    $scope.objectCtrlSettings.isObjectMoving = false;
                    $scope.$apply();
                });
// ********************************************************************************************
                //  end TREEVIEW
//*********************************************************************************************
                //set focus on first input element when window will be opened                
                $('#showTreeOptionModal').on('shown.bs.modal', function(){                   
                    $('#inputTreeName').focus();
                });
                
                $('#treeLevelModal').on('shown.bs.modal', function(){
                    $('#inputLevelName').focus();
                });
                
                $('#showTenantOptionModal').on('shown.bs.modal', function(){
                    $('#inputTenantName').focus();
                });
                
                $('#createPasswordModal').on('shown.bs.modal', function(){
                    $('#inputTenantPassword').focus();
                });
                
                //controller initialization
                var initCtrl = function(){
                    //if tree is off
                    if ($scope.objectCtrlSettings.isTreeView == false){
                        getCabinetsData();
                    }else{
                    //if tree is on
                        loadTrees();                    
                    };
                };
                initCtrl();
//            }]
}]);