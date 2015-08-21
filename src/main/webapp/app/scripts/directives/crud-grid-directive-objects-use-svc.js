'use strict';

angular.module('portalNMC')
    .directive('crudGridObjectsUseSvc', function () {
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
        templateUrl: 'scripts/directives/templates/crud-grid-directive-objects-template-use-svc.html',
        link : function (scope, element, attrs) {
        	//scope.crudTableName = scope.$eval($attrs.table);  
        	//console.log(scope.crudTableName);
        },
        controller: ['$scope', '$rootScope', '$element', '$attrs', '$routeParams', '$resource', '$cookies', '$compile', '$parse', 'crudGridDataFactory', 'notificationFactory', '$http', 'objectSvc',
            function ($scope, $rootScope, $element, $attrs, $routeParams, $resource, $cookies, $compile, $parse, crudGridDataFactory, notificationFactory, $http, objectSvc) {
                
console.log("Objects directive.");
//var timeDirStart = (new Date()).getTime();
                
                    //messages for user
                $scope.messages = {};
                $scope.messages.setSelectedInWinterMode = "Перевести выделенные объекты на зимний режим";
                $scope.messages.setSelectedInSummerMode = "Перевести выделенные объекты на летний режим";
                $scope.messages.setAllInWinterMode = "Перевести все объекты на зимний режим";
                $scope.messages.setAllInSummerMode = "Перевести все объекты на летний режим";
                $scope.messages.markAllOn = "Выбрать все";
                $scope.messages.markAllOff = "Отменить все";
                
                    //monitor settings
                $scope.objectCtrlSettings = {};
//                $scope.monitorSettings.refreshPeriod = monitorSvc.monitorSvcSettings.refreshPeriod;//"180";
//                $scope.monitorSettings.createRoundDiagram = false;
//                $scope.monitorSettings.loadingFlag = true;//monitorSvc.monitorSvcSettings.loadingFlag;
            //console.log($scope.monitorSettings.loadingFlag);      
                //flag: false - get all objectcs, true - get only  red, orange and yellow objects.
//                $scope.monitorSettings.noGreenObjectsFlag = false;
                $scope.objectCtrlSettings.isCtrlEnd =false;
                $scope.objectCtrlSettings.allSelected = false;
                $scope.objectCtrlSettings.objectsPerScroll = 50;//the pie of the object array, which add to the page on window scrolling
                $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;//50;//current the count of objects, which view on the page
                $scope.objectCtrlSettings.currentScrollYPos = window.pageYOffset || document.documentElement.scrollTop; 
                $scope.objectCtrlSettings.objectTopOnPage =0;
                $scope.objectCtrlSettings.objectBottomOnPage =50;
                
                //list of system for meta data editor
                $scope.objectCtrlSettings.vzletSystemList = [];
                
                //flag on/off extended user interface
                $scope.objectCtrlSettings.extendedInterfaceFlag = false;
                
                
                $scope.object = {};
                $scope.objects = [];
                $scope.objectsOnPage = [];
                
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
                objectSvc.promise.then(function(response){
                    var tempArr = response.data;
                    tempArr.forEach(function(element){
                        element.imgsrc='images/object-mode-'+element.currentSettingMode+'.png';
//                        $scope.cont_zpoint_setting_mode_check
                        if (element.currentSettingMode===$scope.cont_zpoint_setting_mode_check[0].keyname){
                            element.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[0].caption;
                            
                        }else if(element.currentSettingMode===$scope.cont_zpoint_setting_mode_check[1].keyname){
                            element.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[1].caption;
                        };
                    });
                    $scope.objects = response.data;
                    //sort by name
                    objectSvc.sortObjectsByFullName($scope.objects);
                    
                    $scope.objectsWithoutFilter = $scope.objects;
                    tempArr =  $scope.objects.slice(0, $scope.objectCtrlSettings.objectsPerScroll);
                    $scope.objectsOnPage = tempArr;
//                    makeObjectTable(tempArr, true);
                    $scope.loading = false;  
                    //if we have the contObject id in cookies, then draw the Zpoint table for this object.
                    if (angular.isDefined($cookies.contObject) && $cookies.contObject!=="null"){
                        $scope.toggleShowGroupDetails(Number($cookies.contObject));
                        $cookies.contObject = null;          
                    };
                    $rootScope.$broadcast('objectSvc:loaded');
                });
                
                function makeObjectTable(objectArray, isNewFlag){
                    
                    var objTable = document.getElementById('objectTable').getElementsByTagName('tbody')[0];       
            //        var temptableHTML = "";
                    var tableHTML = "";
                    if (!isNewFlag){
                        tableHTML = objTable.innerHTML;
                    };

                    objectArray.forEach(function(element, index){
                        var globalElementIndex = $scope.objectCtrlSettings.objectBottomOnPage-objectArray.length+index;
                        var trClass= globalElementIndex%2>0?"":"nmc-tr-odd"; //Подкрашиваем разным цветом четные / нечетные строки
                        tableHTML += "<tr class=\""+trClass+"\" id=\"obj"+element.id+"\"><td class=\"nmc-td-for-buttons\"> <i title=\"Показать/Скрыть точки учета\" id=\"btnDetail"+element.id+"\" class=\"btn btn-xs noMargin glyphicon glyphicon-chevron-right nmc-button-in-table\" ng-click=\"toggleShowGroupDetails("+element.id+")\"></i>";
                        tableHTML += "<i title=\"Редактировать свойства объекта\" ng-show=\"!bList\" class=\"btn btn-xs glyphicon glyphicon-edit nmc-button-in-table\" ng-click=\"selectedObject("+element.id+")\" data-target=\"#showObjOptionModal\" data-toggle=\"modal\"></i>";
                        tableHTML+= "</td>";
                        tableHTML += "<td ng-click=\"toggleShowGroupDetails("+element.id+")\">"+element.fullName;
                        if ($scope.isSystemuser()){
                            tableHTML+=" <span>(id = "+element.id+")</span>";
                        };
                        tableHTML+="</td></tr>";
                        tableHTML +="<tr id=\"trObjZp"+element.id+"\">";
                        tableHTML += "</tr>";                       
                    });
//console.log(objTable); 
                    if (angular.isDefined(objTable.innerHTML)){
//console.log("angular.isDefined(objTable.innerHTML) =  true");                        
                        objTable.innerHTML = tableHTML;
                    };
//console.log(objTable);                    
                    $compile(objTable)($scope);
                    
                };
                
//                $scope.objects = objectSvc.getObjects();
                $scope.loading = objectSvc.getLoadingStatus();//loading;
                $scope.columns = angular.fromJson($attrs.columns);
                $scope.captions = angular.fromJson($attrs.captions);
                $scope.extraProps = angular.fromJson($attrs.exprops);
                $scope.addMode = false;
                $scope.orderBy = { field: $scope.extraProps["defaultOrderBy"], asc: true };

                $scope.filter = '';
                $scope.filterType='';
                //Признак того, что объекты выводятся в окне "Отчеты"
                $scope.bGroupByObject = angular.fromJson($attrs.bgroup) || false;
                $scope.bObject = angular.fromJson($attrs.bobject) || false; //Признак, что страница отображает объекты
                $scope.bList = angular.fromJson($attrs.blist); //|| true; //Признак того, что объекты выводятся только для просмотра        
                //zpoint column names
                $scope.oldColumns = angular.fromJson($attrs.zpointcolumns);
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
                    notificationFactory.success();
                    $('#showZpointOptionModal').modal('hide');
                    var curIndex = -1;
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
                            $scope.objects.some(function(elem, ind){
                                if($scope.currentObject.id === elem.id){
                                    objectIndex = ind;
                                };
                            });
                            if (objectIndex>-1){
                                //update zpoint data in arrays
                                $scope.objects[objectIndex].zpoints[curIndex].customServiceName = $scope.zpointSettings.customServiceName;
                                $scope.objectsOnPage[objectIndex].zpoints[curIndex].zpointName = $scope.zpointSettings.customServiceName;
                                $scope.objects[objectIndex].zpoints[curIndex].isManualLoading = $scope.zpointSettings.isManualLoading;
                                $scope.objectsOnPage[objectIndex].zpoints[curIndex].isManualLoading = $scope.zpointSettings.isManualLoading;
                            };
                            //remake zpoint table
                            if((repaintZpointTableFlag)){
                                makeZpointTable($scope.objectsOnPage[objectIndex]);
                            };
//                        };
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

                var successCallback = function (e, cb) {
                    notificationFactory.success();
                    $('#deleteObjectModal').modal('hide');
                    $('#showObjOptionModal').modal('hide');
                    var elIndex = -1;                 
                    if ((angular.isDefined($scope.currentObject))&&($scope.currentObject!=={})){
                        $scope.objects.some(function(element, index){                     
                            if (element.id == $scope.currentObject.id){
                                elIndex = index;
                                return true;
                            };
                        });
                        if(elIndex!=-1){
                            $scope.objects[elIndex]=$scope.currentObject;
                            $scope.objects[elIndex].imgsrc='images/object-mode-'+$scope.currentObject.currentSettingMode+'.png';
                            $scope.objectsOnPage[elIndex]=$scope.currentObject;
                            $scope.objectsOnPage[elIndex].imgsrc='images/object-mode-'+$scope.currentObject.currentSettingMode+'.png';                       
                        };
                        $scope.currentObject={};
                    };
                };

                var successPostCallback = function (e) {
                    successCallback(e, function () {
                        $scope.toggleAddMode();
                    });
                };

                var errorCallback = function (e) {
                    notificationFactory.errorInfo(e.statusText,e.data.description);       
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

                $scope.setOrderBy = function (field) {
                    var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
                    $scope.orderBy = { field: field, asc: asc };
                };
              
                $scope.selectedItem = function (item) {
			        var curObject = angular.copy(item);
			        $scope.currentObject = curObject;
			    };
                
                $scope.selectedObject = function(objId){
                    var curObject = null;
                    $scope.objects.some(function(element){
                        if (element.id === objId){
                            curObject = angular.copy(element);
                            return true;
                        }
                    });
                    $scope.currentObject = curObject;
                };
                
                $scope.selectedZpoint = function(objId, zpointId){
                    $scope.selectedObject(objId);
                    
                    var curZpoint = null;
                    $scope.currentObject.zpoints.some(function(element){
                        if (element.id === zpointId){
                            curZpoint = angular.copy(element);
                            return true;
                        }
                    });
                    $scope.currentZpoint = curZpoint;
//console.log($scope.currentObject);                    
                };
                
                $scope.toggleShowGroupDetails = function(objId){//switch option: current goup details
                    var curObject = findObjectById(objId);//null;
//                    $scope.objects.some(function(element){
//                        if (element.id === objId){
//                            curObject = element;
//                            return true;
//                        }
//                    });                  
                    //if cur object = null => exit function
                    if (curObject == null){
                        return;
                    };
                    //else
                    
                    var zpTable = document.getElementById("zpointTable"+curObject.id);
//console.log(zpTable);                    
                    if ((curObject.showGroupDetails==true) && (zpTable==null)){                        
                        curObject.showGroupDetails =true;
                    }else{                       
                        curObject.showGroupDetails =!curObject.showGroupDetails;
                    };                                           
                    //if curObject.showGroupDetails = true => get zpoints data and make zpoint table
                    if (curObject.showGroupDetails === true){
                      
                        var mode = "Ex";
                        objectSvc.getZpointsDataByObject(curObject, mode).then(function(response){
                            var tmp = [];
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
//console.log(tmp);                            
                            var zpoints = [];
                            for(var i=0;i<zPointsByObject.length;i++){
                                var zpoint = {};
//console.log(zPointsByObject[i]);                                
                                zpoint.id = zPointsByObject[i].id;
                                zpoint.zpointType = zPointsByObject[i].contServiceType.keyname;
                                zpoint.isManualLoading = zPointsByObject[i].isManualLoading;
                                zpoint.customServiceName = zPointsByObject[i].customServiceName;
                                zpoint.zpointName = zPointsByObject[i].customServiceName || zPointsByObject[i].contServiceType.caption;
                                if ((typeof zPointsByObject[i].rso != 'undefined') && (zPointsByObject[i].rso!=null)){
                                    zpoint.zpointRSO = zPointsByObject[i].rso.organizationFullName || zPointsByObject[i].rso.organizationName;
                                }else{
                                    zpoint.zpointRSO = "Не задано"
                                };
                                zpoint.checkoutTime = zPointsByObject[i].checkoutTime;
                                zpoint.checkoutDay = zPointsByObject[i].checkoutDay;
                                if((typeof zPointsByObject[i].doublePipe == 'undefined')){
                                    zpoint.piped = false;

                                }else {
                                    zpoint.piped = true;
                                    zpoint.doublePipe = (zPointsByObject[i].doublePipe===null)?false:zPointsByObject[i].doublePipe;
                                    zpoint.singlePipe = !zpoint.doublePipe;
                                };
//console.log(zpoint);
                                if ((typeof zPointsByObject[i].deviceObjects != 'undefined') && (zPointsByObject[i].deviceObjects.length>0)){                                
                                    if (zPointsByObject[i].deviceObjects[0].hasOwnProperty('deviceModel')){
                                        zpoint.zpointModel = zPointsByObject[i].deviceObjects[0].deviceModel.modelName;
                                    }else{
                                        zpoint.zpointModel = "Не задано";
                                    };
                                    zpoint.zpointNumber = zPointsByObject[i].deviceObjects[0].number;
                                };
                                zpoint.zpointLastDataDate  = zPointsByObject[i].lastDataDate;   
                                // Получаем эталонный интервал для точки учета
                                getRefRangeByObjectAndZpoint(curObject, zpoint);
                                zpoints[i] = zpoint;                  
                            }
                            curObject.zpoints = zpoints;
                            makeZpointTable(curObject);
                            var btnDetail = document.getElementById("btnDetail"+curObject.id);
                            btnDetail.classList.remove("glyphicon-chevron-right");
                            btnDetail.classList.add("glyphicon-chevron-down");
                            
                            curObject.showGroupDetailsFlag = !curObject.showGroupDetailsFlag;
                        });
                    }//else if curObject.showGroupDetails = false => hide child zpoint table
                    else{
                        var trObj = document.getElementById("obj"+curObject.id);
                        var trObjZp = trObj.getElementsByClassName("nmc-tr-zpoint")[0];//.getElementById("trObjZp");                      
                        trObjZp.innerHTML = "";
                        var btnDetail = document.getElementById("btnDetail"+curObject.id);
                        btnDetail.classList.remove("glyphicon-chevron-down");
                        btnDetail.classList.add("glyphicon-chevron-right");
                    };
                    
                };
                //Формируем таблицу с точками учета
                function makeZpointTable(object){ 
//console.log(object);                                        
                    var trObj = document.getElementById("obj"+object.id);
//console.log(trObj);                    
                    if ((angular.isUndefined(trObj))||(trObj===null)){
                        return;
                    };
                    var trObjZp = trObj.getElementsByClassName("nmc-tr-zpoint")[0];//.getElementById("trObjZp");                    
//console.log(trObjZp);                                        
//                    var trObjZp = document.getElementById("trObjZp"+object.id);                 
                    var trHTML = "";

                    trHTML+="<td class=\"nmc-td-for-buttons-in-object-page\" ng-hide=\"!objectCtrlSettings.extendedInterfaceFlag\"></td><td></td><td style=\"padding-top: 2px !important;\"><table id=\"zpointTable"+object.id+"\" class=\"crud-grid table table-lighter table-bordered table-condensed table-hover nmc-child-object-table\">";
                    trHTML+="<thead><tr class=\"nmc-child-table-header\">";
                    trHTML+="<th ng-show=\"bObject || bList\" class=\"nmc-td-for-buttons-3\"></th>";
                    $scope.oldColumns.forEach(function(column){
                        trHTML+="<th ng-class=\""+column.class+"\">";
                        trHTML+=""+(column.header || column.name)+"";
                        trHTML+="</th>";
                    });
                    trHTML+="</tr></thead>";
                    
                    object.zpoints.forEach(function(zpoint){
                        trHTML +="<tr id=\"trZpoint"+zpoint.id+"\" ng-dblclick=\"getIndicators("+object.id+","+zpoint.id+")\">";
                        trHTML +="<td class=\"nmc-td-for-buttons-3\">"+
                                "<i class=\"btn btn-xs glyphicon glyphicon-edit nmc-button-in-table\""+
                                    "ng-click=\"getZpointSettings("+object.id+","+zpoint.id+")\""+
                                    "data-target=\"#showZpointOptionModal\""+
                                    "data-toggle=\"modal\""+
                                    "data-placement=\"bottom\""+
                                    "title=\"Свойства точки учёта\">"+
                                "</i>"+
                                "<i class=\"btn btn-xs nmc-button-in-table\""+
                                    "ng-click=\"getZpointSettings("+object.id+","+zpoint.id+")\""+
                                    "data-target=\"#showZpointExplParameters\""+
                                    "data-toggle=\"modal\""+
                                    "data-placement=\"bottom\""+
                                    "title=\"Эксплуатационные параметры точки учёта\">"+
                                        "<img height=12 width=12 src=\"vendor_components/glyphicons_free/glyphicons/png/glyphicons-140-adjust-alt.png\" />"+
                                "</i>"+
                                "<a href='#/objects/indicators/?objectId="+object.id+"&zpointId="+zpoint.id+"&objectName="+object.fullName+"&zpointName="+zpoint.zpointName+"'><i class=\"btn btn-xs glyphicon glyphicon-list nmc-button-in-table\""+
//                                    "ng-click=\"getIndicators("+object.id+","+zpoint.id+")\""+
                                    "ng-mousedown=\"setIndicatorsParams("+object.id+","+zpoint.id+")\""+
                                    "title=\"Показания точки учёта\">"+
                                "</i></a>"+

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
                                            imgPath = "images/es.png";
                                            break;
                                        default:
                                            imgPath = column['zpointType'];
                                            break;   
                                    };                                   
                                    trHTML += "<td>";
                                    trHTML += "<img height=12 width=12 src=\""+imgPath+"\"> <span class='paddingLeft5'></span>";
                                    trHTML += zpoint[column.name]+"<span ng-show=\"isSystemuser()\">(id = "+zpoint.id+")</span></td>"; 
                                    break;
                                case "zpointLastDataDate" : trHTML +="<td>{{"+zpoint[column.name]+" | date: 'dd.MM.yyyy HH:mm'}}</td>"; break;   
                                case "zpointRefRange": trHTML += "<td id=\"zpointRefRange"+zpoint.id+"\"></td>"; break;
                                default : trHTML += "<td>"+zpoint[column.name]+"</td>"; break;
                            };
                        });
                        trHTML +="</tr>";
                    });    
                    trHTML += "</table></td>";
                    trObjZp.innerHTML = trHTML;
                    
                    $compile(trObjZp)($scope);                
                }; 
                
                //Функция для получения эталонного интервала для конкретной точки учета конкретного объекта
                function getRefRangeByObjectAndZpoint(object, zpoint){
//                    var url = $scope.urlRefRange + object.id + '/zpoints/' + zpoint.id + '/referencePeriod'; 
//console.log(url);                    
                    objectSvc.getRefRangeByObjectAndZpoint(object, zpoint)
                    .success(function(data){
                        if(data[0] != null){
                            var beginDate = new Date(data[0].periodBeginDate);
                            var endDate =  new Date(data[0].periodEndDate);
//console.log(data[0]);                                    
                            zpoint.zpointRefRange = "c "+beginDate.toLocaleDateString()+" по "+endDate.toLocaleDateString();
                            zpoint.zpointRefRangeAuto = data[0]._auto?"auto":"manual";
                        }
                        else {
                            zpoint.zpointRefRange = "Не задан";
                            zpoint.zpointRefRangeAuto = "notSet";
                        }
                        viewRefRangeInTable(zpoint);
                    })
                    .error(function(e){
                        console.log(e);
                    });
                };
                
                // Прорисовываем эталонный интервал в таблице
                function viewRefRangeInTable(zpoint){
                    //Получаем столбец с эталонным интервалом для заданной точки учета
                    var element = document.getElementById("zpointRefRange"+zpoint.id);
                    //Записываем эталонный интервал в таблицу
                    switch (zpoint.zpointRefRangeAuto){
                        case "auto":element.innerHTML = '<div class="progress progress-striped noMargin">'+
                                            '<div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"><strong>'+zpoint.zpointRefRange+
                                            '</strong></div>'+
                                        '</div>';
                                    break;
                        case "manual":element.innerHTML = '<div class="progress progress-striped noMargin">'+
                                            '<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"><strong>'+zpoint.zpointRefRange+
                                            '</strong></div>'+
                                        '</div>';
                                    break;
                        default : element.innerHTML = ''+zpoint.zpointRefRange+'';
                    }
                    $compile(element)($scope);
                    
                };
                
                //for page "Objects"
                
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
                $scope.getIndicators = function(objectId, zpointId){  
                    $scope.setIndicatorsParams(objectId, zpointId);
//                    $scope.selectedZpoint(objectId, zpointId);
//                    $cookies.contZPoint = $scope.currentZpoint.id;
//                    $cookies.contObject=$scope.currentObject.id;
//                    $cookies.contZPointName = $scope.currentZpoint.zpointName;
//                    $cookies.contObjectName=$scope.currentObject.fullName;
//                    $cookies.timeDetailType="24h";
//                    $cookies.isManualLoading = ($scope.currentZpoint.isManualLoading===null?false:$scope.currentZpoint.isManualLoading) || false;
//console.log($scope.currentZpoint);                    
//                    $rootScope.reportStart = moment().subtract(6, 'days').startOf('day').format('YYYY-MM-DD');
//                    $rootScope.reportEnd = moment().endOf('day').format('YYYY-MM-DD');
                                      
                    window.location.assign("#/objects/indicators/?objectId="+objectId+"&zpointId="+zpointId+"&objectName="+$scope.currentObject.fullName+"&zpointName="+$scope.currentZpoint.zpointName);
                };
                
                $scope.setIndicatorsParams = function(objectId, zpointId){
                    $scope.selectedZpoint(objectId, zpointId);
                    $cookies.contZPoint = $scope.currentZpoint.id;
                    $cookies.contObject=$scope.currentObject.id;
                    $cookies.contZPointName = $scope.currentZpoint.zpointName;
                    $cookies.contObjectName=$scope.currentObject.fullName;
                    $cookies.timeDetailType="24h";
                    $cookies.isManualLoading = ($scope.currentZpoint.isManualLoading===null?false:$scope.currentZpoint.isManualLoading) || false;
//console.log($scope.currentZpoint);                    
                    $rootScope.reportStart = moment().subtract(6, 'days').startOf('day').format('YYYY-MM-DD');
                    $rootScope.reportEnd = moment().endOf('day').format('YYYY-MM-DD');
                                      
//                    window.location.assign("#/objects/indicators/");
                };
                
                //Свойства точки учета
                $scope.zpointSettings = {};
                $scope.getZpointSettings = function(objId, zpointId){
                    $scope.selectedZpoint(objId, zpointId);          
//console.log($scope.currentZpoint);                    
                    var object = $scope.currentZpoint;
                    var zps = {};
                    zps.id = object.id;
                    zps.isManualLoading = object.isManualLoading;
                    zps.customServiceName = object.customServiceName;
                    zps.zpointName = object.zpointName;
                    switch (object.zpointType){
                       case "heat" :  zps.zpointType="ТС"; break;
                       case "hw" : zps.zpointType="ГВС"; break;
                       case "cw" : zps.zpointType="ХВ"; break;    
                        default : zps.zpointType=object.zpointType;        
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
                        // Готовим редактор эталонного периода
                        $scope.prepareRefRange();
                    });
                };
                
                // Подготовка редактора эталонного интервала
                $scope.prepareRefRange = function () {
               		getRefRange($scope.currentObject.id, $scope.zpointSettings.id);
               		$scope.editRefRangeOff();
                };
                
                // Активация формы редактирования эталонного интервала
                $scope.editRefRangeOn = function () {
                    $scope.toggleFlag = true; //переключаем видимость кнопок Изменить/сохранить
					document.getElementById('i_ref_range_save').style.display = 'block';
					document.getElementById('i_ref_range_add').style.display = 'none';
					document.getElementById('inp_ref_range_start').disabled = false;
					document.getElementById('inp_ref_range_end').disabled = false;
                }
                
                // Деактивация формы редактирования эталонного интервала
                $scope.editRefRangeOff = function () {
                    $scope.toggleFlag = false; //переключаем видимость кнопок Изменить/сохранить
					document.getElementById('i_ref_range_save').style.display = 'none';
					document.getElementById('i_ref_range_add').style.display = 'block';
					document.getElementById('inp_ref_range_start').disabled = true;
					document.getElementById('inp_ref_range_end').disabled = true;
                }
                
                // Запрос с сервера данных об эталонном интервале
                var getRefRange = function (objectId, zpointId) {
                	var url = $scope.urlRefRange + '/' + objectId + '/zpoints/' + zpointId + '/referencePeriod'; 
                	$http.get(url)
					.success(function(data){
						// Проверяем, задан ли интервал
						if(data[0] != null){
							$scope.refRange = data[0];
							$scope.refRange.cont_zpoint_id = zpointId;
							$scope.beginDate = new Date($scope.refRange.periodBeginDate);
							$scope.endDate =  new Date($scope.refRange.periodEndDate);
//							console.log($scope.beginDate, document.getElementById('inp_ref_range_start').value);
							// Проверяем, был ли интервал расчитан автоматически
							if($scope.refRange._auto == false) {
								document.getElementById('spn_if_manual').style.display = 'block';
								document.getElementById('spn_if_auto').style.display = 'none';
							}
							else {
								document.getElementById('spn_if_manual').style.display = 'none';
								document.getElementById('spn_if_auto').style.display = 'block';								
							}
						}
						else {
							$scope.refRange = {};
							$scope.refRange.cont_zpoint_id = zpointId;
							$scope.beginDate = '';
							$scope.endDate = '';
							document.getElementById('spn_if_manual').style.display = 'none';
							document.getElementById('spn_if_auto').style.display = 'none';
						}
					})
					.error(function(e){
						notificationFactory.errorInfo(e.statusText,e.description);
					});
                };
                
                // Отправка на сервер данных об эталонном интервале
                $scope.addRefRange = function (){
                	var url = $scope.urlRefRange + '/' + $scope.currentObject.id + '/zpoints/' + $scope.zpointSettings.id + '/referencePeriod';
                	$scope.refRange.id = '';
                	$scope.refRange.periodBeginDate = $scope.beginDate.getTime();
                	$scope.refRange.periodEndDate = $scope.endDate.getTime();
                	$http.post(url, $scope.refRange)
					.success(function(data){
						$scope.editRefRangeOff();
						$scope.refRange = data;
                        //прорисовываем эталонный интервал в таблице
                        var refRangeEl = document.getElementById("zpointRefRange"+$scope.currentZpoint.id);
                        $scope.beginDate = new Date($scope.refRange.periodBeginDate);
				        $scope.endDate =  new Date($scope.refRange.periodEndDate);                                 
                        
                        $scope.currentZpoint.zpointRefRange = "c "+$scope.beginDate.toLocaleDateString()+" по "+$scope.endDate.toLocaleDateString();
                        $scope.currentZpoint.zpointRefRangeAuto = $scope.refRange._auto?"auto":"manual";
                        
                        viewRefRangeInTable($scope.currentZpoint);
					})
					.error(function(e){
						notificationFactory.errorInfo(e.statusText,e.description);
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
                    $('#showZpointExplParameters').modal('hide');
                     $scope.zpointSettings={};
                };
                
                //Update the common zpoint setiing - for example, Name
                $scope.updateZpointCommonSettings = function(){
//console.log($scope.zpointSettings);                    
                    var url = $scope.crudTableName+"/"+$scope.currentObject.id+"/zpoints/"+$scope.zpointSettings.id;
                    $http({
                        url: url,
                        method: 'PUT',
                        data: $scope.zpointSettings
                    })
                        .then(successCallbackOnZpointUpdate, errorCallback);
//                    crudGridDataFactory(url).update({}, $scope.zpointSettings, successCallback, errorCallback);
                };
                
                //Update the zpoint settings, which set the mode for Summer or Winter season
                $scope.updateZpointModeSettings = function(){                   
                    var tableSummer = $scope.crudTableName+"/"+$scope.currentObject.id+"/zpoints/"+$scope.zpointSettings.id+"/settingMode";
                    crudGridDataFactory(tableSummer).update({ id: $scope.zpointSettings.summer.id }, $scope.zpointSettings.summer, successZpointSummerCallback, errorCallback);
                };
                
                // search objects
                $scope.searchObjects = function(searchString){
                    if (($scope.objects.length<=0)){
                        return;
                    };
                    
                    if (angular.isUndefined(searchString) || (searchString==='')){                      
                        var tempArr = [];
                        var endIndex = $scope.objectCtrlSettings.objectsOnPage+$scope.objectCtrlSettings.objectsPerScroll;
                        if((endIndex >= $scope.objects.length)){
                            endIndex = $scope.objects.length;
                        }; 
                        tempArr =  $scope.objects.slice($scope.objectCtrlSettings.objectsOnPage,endIndex);
                        Array.prototype.push.apply($scope.objectsOnPage, tempArr);
                    }else{
//                        $scope.objectsOnPage = $scope.objects;
                        var tempArr = [];
                        
                        $scope.objects.forEach(function(elem){
                            if (elem.fullName.indexOf(searchString)!=-1){
                                tempArr.push(elem);
                            };
                        });
                        $scope.objectsOnPage = tempArr;
                    };
                };
                
                $scope.$on('$destroy', function() {
                    window.onkeydown = undefined;
                }); 
                
                
                //keydown listener for ctrl+end
                window.onkeydown = function(e){                                          
                    if ((e.ctrlKey && e.keyCode == 35) && ($scope.objectCtrlSettings.objectsOnPage<$scope.objects.length)){
                        $scope.loading =  true;    
                        var tempArr =  $scope.objects.slice($scope.objectCtrlSettings.objectsOnPage,$scope.objects.length);
                        Array.prototype.push.apply($scope.objectsOnPage, tempArr);
                        $scope.objectCtrlSettings.objectsOnPage+=$scope.objects.length;
                        
                        $scope.objectCtrlSettings.isCtrlEnd = true;
                        
                    };
                };
                
                //function set cursor to the bottom of the object table, when ctrl+end pressed
                $scope.onTableLoad = function(){ 
                    if ($scope.objectCtrlSettings.isCtrlEnd === true){                    
                        var pageHeight = (document.body.scrollHeight>document.body.offsetHeight)?document.body.scrollHeight:document.body.offsetHeight;
                        window.scrollTo(0, Math.round(pageHeight));
                        $scope.objectCtrlSettings.isCtrlEnd = false;
                        $scope.loading =  false;
                    };
                };
                
                //function add more objects for table on user screen
                $scope.addMoreObjects = function(){
console.log("addMoreObjects. Run");
                    if (($scope.objects.length<=0)){
                        return;
                    };
                    
                    //set end of object array - определяем конечный индекс объекта, который будет выведен при текущем скролинге
                    var endIndex = $scope.objectCtrlSettings.objectsOnPage+$scope.objectCtrlSettings.objectsPerScroll;
//console.log($scope.objects.length);                    
                    if((endIndex >= $scope.objects.length)){
                        endIndex = $scope.objects.length;
                    };
                    //вырезаем из массива объектов элементы с текущей позиции, на которой остановились в прошлый раз, по вычесленный конечный индекс
                    var tempArr =  $scope.objects.slice($scope.objectCtrlSettings.objectsOnPage,endIndex);
                        //добавляем к выведимому на экран массиву новый блок элементов
                    Array.prototype.push.apply($scope.objectsOnPage, tempArr);
                    if(endIndex >= ($scope.objects.length)){
                        $scope.objectCtrlSettings.objectsOnPage = $scope.objects.length;
                    }else{
                        $scope.objectCtrlSettings.objectsOnPage+=$scope.objectCtrlSettings.objectsPerScroll;
                    };
                };
                
                
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
                    $scope.objects.forEach(function(el){
                        el.selected = flag;
                    });
                    $scope.objectsOnPage.forEach(function(el){
                        el.selected = flag;
                    });
                };
                
                $scope.setModeForObjects = function(mode){
                    $scope.settedMode = mode;
                    //get the object ids array
                    var contObjectIds = [];
                    if ($scope.objectCtrlSettings.allSelected===true){
                        contObjectIds = $scope.objects.map(function(el){return el.id});
                    }else{
                        $scope.objectsOnPage.forEach(function(el){
                            if(el.selected===true){
                                contObjectIds.push(el.id);
                            };
                        });
                    };
                    
                    //send data to server
                    var url = objectSvc.getObjectsUrl()+"/settingModeType";
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
                $scope.getVzletSystemList = function(){
                    var tmpSystemList = objectSvc.getVzletSystemList();
                    if (tmpSystemList.length===0){
                        objectSvc.getDeviceMetaDataSystemList()
                            .then(
                            function(response){
                                $scope.objectCtrlSettings.vzletSystemList = response.data;                           
                            },
                            function(e){
                                notificationFactory.errorInfo(e.statusText,e.description);
                            }
                        );
                    }else{
                        $scope.objectCtrlSettings.vzletSystemList =tmpSystemList;
                    };
                };
                $scope.getVzletSystemList();
                    //get devices
                $scope.getDevices = function(obj){
                    objectSvc.getDevicesByObject(obj).then(
                        function(response){
                            //select only vzlet devices
                            var tmpArr = [];//response.data;
                            response.data.forEach(function(element){
                                if (element.metaVzletExpected === true){
                                    tmpArr.push(element);
                                };
                            });
                            obj.devices = tmpArr;//response.data; 
                        },
                        function(error){
                            notificationFactory.errorInfo(error.statusText,error.description);
                        }
                    );
                };
                
                    //get device meta data and show it
                $scope.getDeviceMetaData = function(obj, device){
                    objectSvc.getDeviceMetaData(obj, device).then(
                        function(response){                           
                            device.metaData = response.data; 
                            $scope.currentDevice =  device;                           
                            $('#metaDataEditorModal').modal();
                        },
                        function(error){
                            notificationFactory.errorInfo(error.statusText,error.description);
                        }
                    );
                };
                
                $scope.updateDeviceMetaData = function(device){
//console.log(device);    
                    var method = "";
                    if(angular.isDefined(device.metaData.id)&&(device.metaData.id!==null)){
                        method = "PUT";
                    }else{
                        method = "POST";
                    };
                    var url = "../api/subscr/contObjects/"+device.contObject.id+"/deviceObjects/"+device.id+"/metaVzlet";
                    $http({
                        url: url,
                        method: method,
                        data: device.metaData
                    })
//                    $http.put(url, device.metaData)
                        .then(
//                    objectSvc.putDeviceMetaData(device).then(
                        function(response){
                            $scope.currentDevice =  {};
                            $('#metaDataEditorModal').modal('hide');
                        },
                        function(error){
                            console.log(error);                            
                            notificationFactory.errorInfo(error.statusText,error.description);
                        }
                    );
                };
                
                $scope.invokeHelp = function(){
                    alert('This is SPRAVKA!!!111');
                };
                
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
                
                $scope.checkNumericInterval = function(leftBorder, rightBorder){  
                     if (($scope.checkEmptyNullValue(leftBorder))||( $scope.checkEmptyNullValue(rightBorder))){                                     
                         return false;
                     };
                     if (!(($scope.checkNumericValue(leftBorder))&&( $scope.checkNumericValue(rightBorder)))){                         
                         return false;
                     };
                     if(parseInt(rightBorder)>=parseInt(leftBorder)){                        
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
                    if((zpointSettings == null)||(!zpointSettings.hasOwnProperty('summer'))||(!zpointSettings.hasOwnProperty('winter'))){
                        return true;
                    };
                    return $scope.checkPositiveNumberValue(zpointSettings.summer.ov_BalanceM_ctrl) && 
                        $scope.checkPositiveNumberValue(zpointSettings.winter.ov_BalanceM_ctrl)&&
                        $scope.checkHHmm(zpointSettings.summer.ov_Worktime)&&
                        $scope.checkHHmm(zpointSettings.winter.ov_Worktime)&&
                        $scope.checkPositiveNumberValue(zpointSettings.summer.leak_Gush)&&
                        $scope.checkPositiveNumberValue(zpointSettings.winter.leak_Gush)&&
                        $scope.checkPositiveNumberValue(zpointSettings.summer.leak_Night)&&
                        $scope.checkPositiveNumberValue(zpointSettings.winter.leak_Night)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_deltaT_min, zpointSettings.summer.wm_deltaT_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_deltaQ_min, zpointSettings.summer.wm_deltaQ_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_deltaT_min, zpointSettings.winter.wm_deltaT_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_deltaQ_min, zpointSettings.winter.wm_deltaQ_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_P2_min, zpointSettings.summer.wm_P2_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_P1_min, zpointSettings.summer.wm_P1_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_P2_min, zpointSettings.winter.wm_P2_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_P1_min, zpointSettings.winter.wm_P1_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_T2_min, zpointSettings.summer.wm_T2_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_T1_min, zpointSettings.summer.wm_T1_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_T2_min, zpointSettings.winter.wm_T2_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_T1_min, zpointSettings.winter.wm_T1_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_M2_min, zpointSettings.summer.wm_M2_max)&&
                        $scope.checkNumericInterval(zpointSettings.summer.wm_M1_min, zpointSettings.summer.wm_M1_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_M2_min, zpointSettings.winter.wm_M2_max)&&
                        $scope.checkNumericInterval(zpointSettings.winter.wm_M1_min, zpointSettings.winter.wm_M1_max);
                };
                
                $scope.checkObjectPropertiesForm = function(object){
//                    if ((object==null)||(object.cwTemp === undefined)){
                    if ((object==null)||(!object.hasOwnProperty('cwTemp'))||(!object.hasOwnProperty('heatArea'))){
                        return true;
                    };
                    return $scope.checkNumericValue(object.cwTemp) && ($scope.checkNumericValue(object.heatArea));
                };              
            }]
    };
});