'use strict';
angular.module('portalNMC')
.controller('SettingsObjectTreesCtrl', ['$scope', '$rootScope', '$routeParams', '$resource', '$cookies', '$compile', '$parse', 'crudGridDataFactory', 'notificationFactory', '$http', 'objectSvc', 'mainSvc', '$timeout', '$window',
            function ($scope, $rootScope, $routeParams, $resource, $cookies, $compile, $parse, crudGridDataFactory, notificationFactory, $http, objectSvc, mainSvc, $timeout, $window) {
                $rootScope.ctxId = "management_rma_objects_page";
//console.log('Run Object management controller.');  
//var timeDirStart = (new Date()).getTime();
                
                    //messages for user
                $scope.messages = {};                
                $scope.messages.markAllOn = "Выбрать все";
                $scope.messages.markAllOff = "Отменить все";
                $scope.messages.moveToNode = "Привязать к узлу";
                $scope.messages.releaseFromNode = "Отвязать от узла";
                
                    //object ctrl settings
                $scope.crudTableName = objectSvc.getObjectsUrl();
                $scope.objectCtrlSettings = {};
                $scope.objectCtrlSettings.isCtrlEnd =false;                
                $scope.objectCtrlSettings.allSelected = false;      //флаг для объектов: true - все объекты выбраны
                $scope.objectCtrlSettings.anySelected = false;      //выбран хотя бы один объект                
                $scope.objectCtrlSettings.anyClientSelected = false; //выбран хотя бы один абонент
                $scope.objectCtrlSettings.objectsPerScroll = 34;    //the pie of the object array, which add to the page on window scrolling
                $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;//50;//current the count of objects, which view on the page
                
                $scope.objectCtrlSettings.vzletSystemList = []; //list of system for meta data editor
                
                $scope.objectCtrlSettings.extendedInterfaceFlag = true; //flag on/off extended user interface
                                
                $scope.objectCtrlSettings.serverTimeZone = 3; //server time zone at Hours
                
                $scope.objectCtrlSettings.dateFormat = "DD.MM.YYYY";    //date format for user
                
                //service permission settings
//                $scope.objectCtrlSettings.mapAccess = false;
                $scope.objectCtrlSettings.ctxId = "management_objects_2nd_menu_item";
                
                $scope.objectCtrlSettings.rmaUrl = "../api/rma";
                $scope.objectCtrlSettings.clientsUrl = "../api/rma/subscribers";
                $scope.objectCtrlSettings.subscrObjectsSuffix = "/subscrContObjects";
                $scope.objectCtrlSettings.tempSchBaseUrl = "../api/rma/temperatureCharts/byContObject";
                
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
                
                var performObjectsData = function(response){
                    var tempArr = response.data;
//console.log(tempArr);                    
                    $scope.objects = response.data;
                    //sort by name
                    objectSvc.sortObjectsByFullName($scope.objects);
                    if (angular.isUndefined($scope.filter) || ($scope.filter === "")){
                        $scope.objectsWithoutFilter = $scope.objects;
                        $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;
                        tempArr =  $scope.objects.slice(0, $scope.objectCtrlSettings.objectsPerScroll);
                        $scope.objectsOnPage = tempArr;                            
                        $rootScope.$broadcast('objectSvc:loaded');

                    } else {
                        $scope.searchObjects($scope.filter);
                    };
                    $scope.loading = false;  
                };
                
                var getObjectsData = function(objId){
//console.log("getObjectsData");
                    $rootScope.$broadcast('objectSvc:requestReloadData');
                    $scope.loading = true;
                    objectSvc.getRmaPromise().then(performObjectsData);
                };

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
console.log(e);
//console.log($scope.currentObject);                    
                    $scope.currentObject._activeContManagement = e._activeContManagement;
                                        //update zpoints info
                    var mode = "Ex";
                    objectSvc.getZpointsDataByObject(e, mode).then(function(response){
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
                        e.zpoints = zpoints;

                    });
                    var objIndex = null;
                    objIndex = findObjectIndexInArray(e.id, $scope.objects);
                    if (objIndex != null) {$scope.objects[objIndex] = e};
                    objIndex = null;
                    objIndex = findObjectIndexInArray(e.id, $scope.objectsOnPage);
                    if (objIndex != null) {$scope.objectsOnPage[objIndex] = e};
//                    $scope.currentObject = {};
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
                    if ($scope.objectCtrlSettings.isTreeView == false || mainSvc.checkUndefinedNull($scope.data.currentTree)){
                        getObjectsData(e.id);
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
                    //zpoint settings saving flag reset
                    $scope.zpointSettings.isSaving = false;
                    $scope.currentObject.isSaving = false;
                };

                $scope.setOrderBy = function (field) {
                    var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
                    $scope.orderBy = { field: field, asc: asc };
                };
              
                $scope.selectedItem = function (item) {
			        var curObject = angular.copy(item);
			        $scope.currentObject = curObject;                    
			    };
                
                $scope.selectedObject = function(objId, isLightForm){
                    objectSvc.getObject(objId)
                    .then(function(resp){
                        $scope.currentObject = resp.data;
                    }, function(error){
                        console.log(error);
                    });
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
        
                //$scope.checkString  
                $scope.emptyString = function(str){
                    return mainSvc.checkUndefinedEmptyNullValue(str);
                };
                $scope.checkUndefinedNull = function(val){
                    return mainSvc.checkUndefinedNull(val);
                };
                
                // search objects
                $scope.searchObjects = function(searchString){
                    if (($scope.objects.length <= 0)){
                        return;
                    };
                    
                                          //close all opened objects zpoints
                    $scope.objectsOnPage.forEach(function(obj){
                        if (obj.showGroupDetailsFlag == true){
                            var trObj = document.getElementById("obj" + obj.id);
                            if (!mainSvc.checkUndefinedNull(trObj)){                                    
                                var trObjZp = trObj.getElementsByClassName("nmc-tr-zpoint")[0];                                                 
                                trObjZp.innerHTML = "";
                                var btnDetail = document.getElementById("btnDetail" + obj.id);
                                btnDetail.classList.remove("glyphicon-chevron-down");
                                btnDetail.classList.add("glyphicon-chevron-right");
                            };
                        };
                        obj.showGroupDetailsFlag = false;
                    });
                    
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
                    $scope.objectCtrlSettings.allSelected = false;
                    var anySelectedFlag = false;
                    $scope.objectsOnPage.some(function(elem){
                        if (elem.selected == true){
                            anySelectedFlag = true;
                            return true;
                        };
                    });
                    $scope.objectCtrlSettings.anySelected = anySelectedFlag;
                };
       
                $scope.invokeHelp = function(){
                    alert('This is SPRAVKA!!!111');
                };

                var setConfirmCode = function(useImprovedMethodFlag){
                    $scope.confirmCode = null;
                    var tmpCode = mainSvc.getConfirmCode(useImprovedMethodFlag);
                    $scope.confirmLabel = tmpCode.label;
                    $scope.sumNums = tmpCode.result;                    
                };

// **************************************************************************************************            
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
                

                var prepareObjectsIdsArray = function(){
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
                    return tmp;
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
                
                $('#showZpointOptionModal').on('hidden.bs.modal', function(){
                    $scope.zpointSettings = {};
                });
                $('#showZpointExplParameters').on('hidden.bs.modal', function(){
                    $scope.zpointSettings = {};
                });
                
                $('#showObjOptionModal').on('hidden.bs.modal', function(){
                    $scope.currentObject.isSaving = false;
                    $scope.currentSug = null;
                });             
                
// ********************************************************************************************
                //  TREEVIEW
//*********************************************************************************************
                $scope.data.treeTemplates = objectSvc.getRmaTreeTemplates();
                $scope.objectCtrlSettings.treeSettings = {};
                $scope.objectCtrlSettings.treeSettings.nodesPropName = 'childObjectList'; //'objectName'
                $scope.data.currentDeleteMessage = "";

                var ROOT_NODE = {
                    objectName: "",
                    type: "root",
                    childObjectList: []
                };
                
                $scope.objectCtrlSettings.isTreeView = true;
                $scope.data.currentTree = {};
                $scope.data.newTree = {};
                
                var findNodeInTree = function(node, tree){
                    return mainSvc.findNodeInTree(node, tree);
                };
                
                $scope.findNodeInTree = function(node, tree){//wrapper for testing
                    return mainSvc.findNodeInTree(node, tree);
                };
                
                $scope.selectNode = function(item){
                    $scope.objectCtrlSettings.allSelected = false;
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
//                        $rootScope.$broadcast('objectSvc:requestReloadData');
                        $scope.loading = true;
                        if (item.type == 'root'){
                            objectSvc.loadFreeObjectsByTree($scope.data.currentTree.id).then(performObjectsData);
                        }else{
                            objectSvc.loadObjectsByTreeNode($scope.data.currentTree.id, item.id).then(performObjectsData);
                        };
                    };                    
                };
                
                $scope.data.trees = [];
                
                $scope.loadTree = function(tree, objId){
//                    $rootScope.$broadcast('objectSvc:requestReloadData');
                    $scope.loading = true;
                    $scope.objectCtrlSettings.allSelected = false;
                    objectSvc.loadTree(tree.id).then(function(resp){
                            $scope.messages.treeMenuHeader = tree.objectName || tree.id;
//console.log(resp.data);                        
                            var respTree = angular.copy(resp.data);
                            sortTreeNodesByObjectName(respTree);
//console.log(respTree);                        
                            respTree.childObjectList.unshift(angular.copy(ROOT_NODE));
                            $scope.data.currentTree = respTree;
                            respTree.childObjectList[0].isSelected = true;
                            $scope.data.selectedNode = angular.copy(respTree.childObjectList[0]);
                            objectSvc.loadFreeObjectsByTree(tree.id).then(performObjectsData);
                            if (mainSvc.checkUndefinedNull($scope.data.currentTree.templateId)){
                                return "Tree is free";
                            };
                            objectSvc.loadTreeTemplateItems($scope.data.currentTree.templateId).then(function(resp){
                                $scope.data.currentTreeTemplateItems = angular.copy(resp.data);
                                respTree.childObjectList[0].templateId = $scope.data.currentTree.templateId;
                                respTree.childObjectList[0].templateItemId = $scope.data.currentTree.templateItemId;
                            }, errorProtoCallback);
//                            $scope.data.trees.push(respTree);
                        }, errorProtoCallback);
                };
                
                var loadTrees = function(){
                    $scope.data.currentTree = {};
                    objectSvc.loadTrees().then(function(resp){
                        mainSvc.sortItemsBy(resp.data, "objectName");
                        $scope.data.trees = angular.copy(resp.data);
                        if (!angular.isArray($scope.data.trees) || $scope.data.trees.length <=0){ 
                            $scope.messages.treeMenuHeader = "Выберете иерархию";
                            getObjectsData();
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
                    objectSvc.updateTree($scope.data.currentTree).then(function(resp){                      
                        var respTree = resp.data;
                        sortTreeNodesByObjectName(respTree);
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
                                $scope.data.currentLevel.levelType = currentLevel.itemName;
                                $scope.data.currentLevel.objectName = currentLevel.itemNameTemplate;
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
                    objectSvc.createTree(tree).then(function(resp){
                        var createdTree = angular.copy(resp.data);
                        createdTree.childObjectList.unshift(angular.copy(ROOT_NODE));
                        if (!mainSvc.checkUndefinedNull(createdTree.templateId)){
                            createdTree.childObjectList[0].templateId = createdTree.templateId;
                        };
                        if (!mainSvc.checkUndefinedNull(createdTree.templateItemId)){
                            createdTree.childObjectList[0].templateItemId = createdTree.templateItemId;
                        };
                        $scope.data.trees.push(createdTree);
                        mainSvc.sortItemsBy($scope.data.trees, "objectName");
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
                            objectSvc.deleteTree($scope.data.currentTree.id).then(function(resp){
                                loadTrees();
                            }, errorProtoCallback);                            
                        }else{
                            objectSvc.deleteTreeNode($scope.data.currentTree.id, delItem.id).then(function(resp){
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
                        $scope.objectCtrlSettings.anySelected = false;
                    };
                    $scope.data.treeForMove = angular.copy($scope.data.currentTree);
                    $scope.data.treeForMove.childObjectList.shift();
                    $scope.data.treeForMove.movingObjects = tmpMovingObjectArr;
                }
                
                $scope.moveToNode = function(){
                    objectSvc.putObjectsToTreeNode($scope.data.currentTree.id, $scope.data.selectedNodeForMove.id, $scope.data.treeForMove.movingObjects).then(function(resp){
                        $scope.loading = true;
                        if ($scope.data.selectedNode.type == 'root'){
                            objectSvc.loadFreeObjectsByTree($scope.data.currentTree.id).then(performObjectsData);
                        }else{
                            objectSvc.loadObjectsByTreeNode($scope.data.currentTree.id, $scope.data.selectedNode.id).then(performObjectsData);
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
                        $scope.objectCtrlSettings.anySelected = false;
                    };                   
                    objectSvc.releaseObjectsFromTreeNode($scope.data.currentTree.id, $scope.data.selectedNode.id, tmpMovingObjectArr).then(function(resp){
                        $scope.loading = true;
                        if ($scope.data.selectedNode.type == 'root'){
                            objectSvc.loadFreeObjectsByTree($scope.data.currentTree.id).then(performObjectsData);
                        }else{
                            objectSvc.loadObjectsByTreeNode($scope.data.currentTree.id, $scope.data.selectedNode.id).then(performObjectsData);
                        };
                    }, errorProtoCallback);
                };
                
                $scope.toggleTreeView = function(){
                    $scope.objectCtrlSettings.isTreeView = !$scope.objectCtrlSettings.isTreeView;
                    //if tree is off
                    if ($scope.objectCtrlSettings.isTreeView == false){
                        getObjectsData();
                    }else{
                    //if tree is on
                        loadTrees();                    
                    };
                };
                
                $scope.createTreeByTemplate = function(template){
                    objectSvc.loadTreeTemplateItems(template.id).then(function(resp){
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
//                    $scope.data.currentTreeTemplateItems.some(function(item){
//                        if (curItem.templateItemId == item.id){
//                            currentLevel = item.itemLevel;
//                            return true;
//                        }
//                    });
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
                        $scope.data.currentLevel.levelType = currentLevel.itemName;
                        $scope.data.currentLevel.objectName = currentLevel.itemNameTemplate;
                        $scope.data.currentLevel.templateId = currentLevel.templateId;
                        $scope.data.currentLevel.templateItemId = currentLevel.id;
                    };
                };
                
                var sortTreeNodesByObjectName = function(tree){
                    mainSvc.sortTreeNodesBy(tree, "objectName");
//                    if (mainSvc.checkUndefinedNull(tree.childObjectList) || !angular.isArray(tree.childObjectList)){
//                        return;
//                    };
//                    mainSvc.sortItemsBy(tree.childObjectList, "objectName");
//                    tree.childObjectList.forEach(function(node){
//                        sortTreeNodesByObjectName(node);
//                    });                    
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
                
                $('#showObjOptionModal').on('shown.bs.modal', function(){
                    $('#inputContObjectName').focus();
                });
                
                $('#showZpointOptionModal').on('shown.bs.modal', function(){
                    $('#inputZpointName').focus();
                });
                //controller initialization
                var initCtrl = function(){
                    //if tree is off
                    if ($scope.objectCtrlSettings.isTreeView == false){
                        getObjectsData();
                    }else{
                    //if tree is on
                        loadTrees();                    
                    };
                };
                initCtrl();
//            }]
}]);