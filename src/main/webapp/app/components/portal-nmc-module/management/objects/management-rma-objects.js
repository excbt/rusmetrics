/*jslint node: true, white: false, nomen: true, eqeq: true, es5: true*/
/*global angular, $, alert, moment*/
'use strict';
var app = angular.module('portalNMC');
app.controller('MngmtObjectsCtrl', ['$scope', '$rootScope', '$routeParams', '$resource', '$cookies', '$compile', '$parse', 'crudGridDataFactory', 'notificationFactory', '$http', 'objectSvc', 'mainSvc', '$timeout', '$window', function ($scope, $rootScope, $routeParams, $resource, $cookies, $compile, $parse, crudGridDataFactory, notificationFactory, $http, objectSvc, mainSvc, $timeout, $window) {
    $rootScope.ctxId = "management_rma_objects_page";
//console.log('Run Object management controller.');  
//var timeDirStart = (new Date()).getTime();
                
    var RADIX = 10;//for parseInt
                
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
    $scope.messages.moveToNode = "Привязать к узлу";
    $scope.messages.releaseFromNode = "Отвязать от узла";
    $scope.messages.groupMenuHeader = "Полный список объектов";

    $scope.messages.loadIndicators = "Загрузить показания";

    $scope.messages.noObjects = "Объектов нет.";
                
                    //object ctrl settings
    $scope.crudTableName = objectSvc.getObjectsUrl();
    $scope.objectCtrlSettings = {};
    $scope.objectCtrlSettings.isCtrlEnd = false;
    $scope.objectCtrlSettings.allSelected = false;      //флаг для объектов: true - все объекты выбраны
    $scope.objectCtrlSettings.anySelected = false;      //выбран хотя бы один объект                
    $scope.objectCtrlSettings.anyClientSelected = false; //выбран хотя бы один абонент
    $scope.objectCtrlSettings.objectsPerScroll = objectSvc.OBJECT_PER_SCROLL;    //the pie of the object array, which add to the page on window scrolling
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
//    $scope.objectCtrlSettings.tempSchBaseUrl = "../api/rma/temperatureCharts/byContObject";
                
    var setVisibles = function () {
        var tmp = mainSvc.getContextIds();
        tmp.forEach(function (element) {
            var elDOM = document.getElementById(element.permissionTagId);//.style.display = "block";
            if (angular.isUndefined(elDOM) || (elDOM == null)) {
                return;
            }
            $('#' + element.permissionTagId).removeClass('nmc-hide');
        });
    };
    setVisibles();
    //listen change of service list
    $rootScope.$on('servicePermissions:loaded', function () {
        setVisibles();
    });

    $scope.data = {};
    $scope.data.currentGroupId = null; //current group id: use for group object filter

    $scope.object = {};
    $scope.objects = [];
    $scope.objectsOnPage = [];
    $scope.currentSug = null;

    $scope.groupUrl = "../api/subscr/contGroup";

    function findObjectById(objId) {
        var obj = null;
        $scope.objects.some(function (element) {
            if (element.id === objId) {
                obj = element;
                return true;
            }
        });
        return obj;
    }
                
//console.log(objectSvc.promise);
                
    // *** Переместить курсор на указанный объект
    var moveToObject = function (objId) {
        if (mainSvc.checkUndefinedNull(objId)) {
            return "moveToObject: object id is undefined or null.";
        }
        var curObj = objectSvc.findObjectById(Number(objId), $scope.objects);
//console.log(curObj);                    
        if (curObj != null) {
            var curObjIndex = $scope.objects.indexOf(curObj);
//console.log(curObjIndex);                        
            if (curObjIndex > $scope.objectCtrlSettings.objectsOnPage) {
                //вырезаем из массива объектов элементы с текущей позиции, на которой остановились в прошлый раз, по вычесленный конечный индекс
                var tempArr =  $scope.objects.slice($scope.objectCtrlSettings.objectsOnPage, curObjIndex + 1);
                    //добавляем к выведимому на экран массиву новый блок элементов
                Array.prototype.push.apply($scope.objectsOnPage, tempArr);
                $scope.objectCtrlSettings.objectsOnPage = curObjIndex + 1;
            }
            if (!mainSvc.checkUndefinedNull(objId)) {
                $timeout(function () {
                    var curObjElem = document.getElementById("obj" + objId);
//console.log(curObjElem);                                
                    if (!mainSvc.checkUndefinedNull(curObjElem)) {
                        curObjElem.scrollIntoView();
                    }
                }, 50);
            }
        }
    };
                
    var performObjectsData = function (response) {
        if (mainSvc.checkUndefinedNull(response) || mainSvc.checkUndefinedNull(response.data) || !angular.isArray(response.data) || response.data.length === 0) {
            $scope.messages.noObjects = "Объектов нет.";
            $scope.objects = [];
            $scope.objectsOnPage = [];
            $scope.loading = false;
            $rootScope.$broadcast('objectSvc:loaded');
            return false;
        }
        var tempArr = response.data;
//console.log(tempArr);                    
        $scope.objects = response.data;
        //calculate all objects zpoints
        var zpointCount = 0;
        $scope.objects.forEach(function (obj) {
            if (mainSvc.checkUndefinedNull(obj.contObjectStats)) {
                return;
            }
            zpointCount += obj.contObjectStats.contZpointCount;
        });
        console.log("Количество точек учета у загруженных с сервера объектов: " + zpointCount);
        //sort by name
        objectSvc.sortObjectsByFullName($scope.objects);
        if (angular.isUndefined($scope.filter) || ($scope.filter === "")) {
            $scope.objectsWithoutFilter = $scope.objects;
            $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;
            tempArr =  $scope.objects.slice(0, $scope.objectCtrlSettings.objectsPerScroll);
            $scope.objectsOnPage = tempArr;
            //if we have the contObject id in cookies, then draw the Zpoint table for this object.
            if (angular.isDefined($cookies.get('contObject')) && $cookies.get('contObject') !== "null") {
                $scope.toggleShowGroupDetails(Number($cookies.get('contObject')));
                $cookies.put('contObject', 'null');
            }
            $rootScope.$broadcast('objectSvc:loaded');
//console.log($scope.data.moveToObjectId);                        
            if (!mainSvc.checkUndefinedNull($scope.data.moveToObjectId)) {
                moveToObject($scope.data.moveToObjectId);
                $scope.data.moveToObjectId = null;
            }
        } else {
            $scope.searchObjects($scope.filter);
        }
        $scope.loading = false;
    };
                
    var getObjectsData = function (objId) {
//console.log("getObjectsData");
        $rootScope.$broadcast('objectSvc:requestReloadData', {"contGroupId": $scope.data.currentGroupId});
        $scope.loading = true;
        if (!mainSvc.checkUndefinedNull(objId)) {
            $scope.data.moveToObjectId = objId;
        }
        objectSvc.getRmaPromise().then(performObjectsData);
    };

//      USING GROUP DATA                                     
    $scope.objectsDataFilteredByGroup = function (group) {
        $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;
        if (mainSvc.checkUndefinedNull(group)) {
            $scope.messages.groupMenuHeader = "Полный список объектов";
            $scope.data.currentGroupId = null;
        } else {
            $scope.messages.groupMenuHeader = group.contGroupName;
            $scope.data.currentGroupId = group.id;
            $scope.data.currentGroup = group;
        }
        getObjectsData();
    };

    var getRsoOrganizations = function () {
        objectSvc.getRsoOrganizations()
            .then(function (response) {
                $scope.data.rsoOrganizations = response.data;
            });
    };
                
    var getCmOrganizations = function () {
        objectSvc.getCmOrganizations()
            .then(function (response) {
                //sort cm by organizationName
                mainSvc.sortOrganizationsByName(response.data);
                $scope.data.cmOrganizations = response.data;
            });
    };


    var getTimezones = function () {
        objectSvc.getTimezones()
            .then(function (response) {
                $scope.data.timezones = response.data;
    //                        mainSvc.sortItemsBy($scope.data.timezones, 'caption');
    //console.log($scope.data.timezones);                        
            });
    };

//                $scope.objects = objectSvc.getObjects();
    $scope.loading = objectSvc.getLoadingStatus();//loading;
    $scope.columns = [

        {"name": "fullName", "header" : "Название", "class": "col-md-5"},
        {"name": "fullAddress", "header" : "Адрес", "class": "col-md-5"}

    ];//angular.fromJson($attrs.columns);
    $scope.captions = {"loading": "loading", "totalElements": "totalElements"};//angular.fromJson($attrs.captions);
    $scope.extraProps = {"idColumnName": "id", "defaultOrderBy" : "fullName", "nameColumnName": "fullName"};//angular.fromJson($attrs.exprops);
    $scope.addMode = false;
    $scope.orderBy = { field: $scope.extraProps["defaultOrderBy"], asc: true };

    $scope.filter = '';
    $scope.filterType = '';
    //Признак того, что объекты выводятся в окне "Отчеты"
    $scope.bGroupByObject = false;//angular.fromJson($attrs.bgroup) || false;
    $scope.bObject = false;//angular.fromJson($attrs.bobject) || false; //Признак, что страница отображает объекты
    $scope.bList = true;//angular.fromJson($attrs.blist) || true; //Признак того, что объекты выводятся только для просмотра
    //zpoint column names
    $scope.oldColumns = [
        {"name": "zpointName", "header" : "Наименование", "class": "col-xs-5"},
        {"name": "zpointModel", "header" : "Модель", "class": "col-xs-2"},
        {"name": "zpointNumber", "header" : "Номер", "class": "col-xs-2"},
        {"name": "tsNumber", "header" : "Теплосистема", "class": "col-xs-2"}
//                        {"name":"zpointLastDataDate", "header" : "Последние данные", "class":"col-md-2"}
    ];//angular.fromJson($attrs.zpointcolumns);
    // Эталонный интервал
    $scope.refRange = {};
    $scope.urlRefRange = '../api/subscr/contObjects/';

    //Режимы функционирования (лето/зима)
    $scope.cont_zpoint_setting_mode_check = [
        {"keyname": "summer", "caption": "Летний режим"},
        {"keyname": "winter", "caption": "Зимний режим"}
    ];
                
    $scope.data.metadataSchema = [
        {
            header: 'Поле источник',
            headClass : 'col-xs-3 col-md-3',
            name: 'srcProp',
            type: 'select_src_field'
        }, {
            header: 'Делитель',
            headClass : 'col-xs-1 col-md-1',
            name: 'srcPropDivision',
            type: 'input/text',
            disabled: false
        }, {
            header: 'Единицы измерения источника',
            headClass : 'col-xs-1 col-md-1',
            name: 'srcMeasureUnit',
            type: 'select_measure_units',
            disabled: false
        }, {
            header: 'Единицы измерения приемника',
            headClass : 'col-xs-1 col-md-1',
            name: 'destMeasureUnit',
            type: 'select_measure_units',
            disabled: false
        }, {
            header: 'Поле приемник',
            headClass : 'col-xs-1 col-md-1',
            name: 'destProp',
            type: 'input/text',
            disabled: true
        }, {
            header: 'Функция',
            headClass : 'col-xs-1 col-md-1',
            name: 'propFunc',
            type: 'input/text',
            disabled: true
        }
    ];
                

    $scope.toggleAddMode = function () {
        $scope.addMode = !$scope.addMode;
        $scope.object = {};
        if ($scope.addMode) {
            if ($scope.newIdProperty && $scope.newIdValue) {
                $scope.object[$scope.newIdProperty] = $scope.newIdValue;
            }
        }
    };

    $scope.toggleEditMode = function (object) {
        object.editMode = !object.editMode;
    };
                
    var mapZpointProp = function (zpoint) {
        var result = {};

        result.id = zpoint.id;
        result.zpointOrder = zpoint.contServiceType.serviceOrder + zpoint.customServiceName;
        result.exSystemKeyname = zpoint.exSystemKeyname;
        result.tsNumber = zpoint.tsNumber;
        result.exCode = zpoint.exCode;
        result.version = zpoint.version;
        result.contObjectId = zpoint.contObjectId;
        result.startDate = zpoint.startDate;
        result.deviceObjectId = zpoint.deviceObjectId;
        result.rsoId = zpoint.rsoId;
        result.zpointType = zpoint.contServiceType.keyname;
        result.isManualLoading = zpoint.isManualLoading;
        result.customServiceName = zpoint.customServiceName;
        result.contServiceTypeKeyname = zpoint.contServiceTypeKeyname;
        result.zpointName = zpoint.customServiceName;
        result.contZPointComment = zpoint.contZPointComment;
        if ((typeof zpoint.rso != 'undefined') && (zpoint.rso != null)) {
            result.zpointRSO = zpoint.rso.organizationFullName || zpoint.rso.organizationName;
            result.rsoId = zpoint.rsoId;
        } else {
            result.zpointRSO = "Не задано";
        }
        result.checkoutTime = zpoint.checkoutTime;
        result.checkoutDay = zpoint.checkoutDay;
        if (typeof zpoint.doublePipe == 'undefined') {
            result.piped = false;
        } else {
            result.piped = true;
            result.doublePipe = (zpoint.doublePipe === null) ? false : zpoint.doublePipe;
            result.singlePipe = !result.doublePipe;
        }
        console.log(zpoint);
        if (!mainSvc.checkUndefinedNull(zpoint.deviceObject)) {
            if (!mainSvc.checkUndefinedNull(zpoint.deviceObject.deviceModel)) {
                result.zpointModel = zpoint.deviceObject.deviceModel.modelName;
            } else {
                result.zpointModel = "Не задано";
            }
            // deviceObjects property changed to deviceObject. By AK
            result.zpointNumber = mainSvc.checkUndefinedNull(zpoint.deviceObject.number) ? "" : zpoint.deviceObject.number;
        
            result.deviceObjectId = zpoint.deviceObject.id;
        }
        
        result.zpointLastDataDate  = zpoint.lastDataDate;
        result.isDroolsDisable = zpoint.isDroolsDisable;
        result.temperatureChartId = zpoint.temperatureChartId;
//                    result.tempSchedules = zpoint.tempSchedules;
        //perform flexData
        if (!mainSvc.checkUndefinedNull(zpoint.flexData)) {
            result.flexData = zpoint.flexData;           
        }
        return result;
    };
                                
    var findObjectIndexInArray = function (objId, targetArr) {
        var result = -1;
        targetArr.some(function (elem, index) {
            if (elem.id == objId) {
                result = index;
                return true;
            }
        });
        return result;
    };
    
                    //Формируем таблицу с точками учета
    function makeZpointTable(object) {
//console.log(object);
        var trObj = document.getElementById("obj" + object.id);
//console.log(trObj);
        if ((angular.isUndefined(trObj)) || (trObj === null)) {
            return;
        }
        var trObjZp = trObj.getElementsByClassName("nmc-tr-zpoint")[0];//.getElementById("trObjZp");
//console.log(trObjZp);
//                    var trObjZp = document.getElementById("trObjZp"+object.id);
        var trHTML = "";

        trHTML += "<td class=\"nmc-td-for-button-object-control\" ng-hide=\"!objectCtrlSettings.extendedInterfaceFlag\"></td><td></td><td style=\"padding-top: 2px !important;\"><table id=\"zpointTable" + object.id + "\" class=\"crud-grid table table-lighter table-bordered table-condensed table-hover nmc-child-object-table\">";
        trHTML += "<thead><tr class=\"nmc-child-table-header\">";
        trHTML += "<th ng-show=\"bObject || bList\" class=\"nmc-td-for-button\"></th>";
        $scope.oldColumns.forEach(function (column) {
            trHTML += "<th class=\"" + column.class + "\">";
            trHTML += (column.header || column.name);
            trHTML += "</th>";
        });
        trHTML += "<th></th>";
        trHTML += "</tr></thead>";

        object.zpoints.forEach(function (zpoint) {
            trHTML += "<tr id=\"trZpoint" + zpoint.id + "\" >";
            trHTML += "<td class=\"nmc-td-for-button\">" +

                "<div class=\"btn-group\">" +
                "<i title=\"Действия над точкой учета\" type=\"button\" class=\"btn btn-xs glyphicon glyphicon-menu-hamburger nmc-button-in-table dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\" style=\"font-size: .9em;\"></i>" +
                "<ul class=\"dropdown-menu\">" +
                        "<li><a ng-click=\"getZpointSettingsEx(" + object.id + "," + zpoint.id + ")\"" +
                                "data-target=\"#showZpointOptionModal\"" +
                                "data-toggle=\"modal\"" +
                                "data-placement=\"bottom\"" +
                                "title=\"Свойства точки учёта\">" +
                                "Свойства" +
                        "</a></li>" +
                        "<li><a ng-click=\"getZpointSettingsExpl(" + object.id + "," + zpoint.id + ")\"" +
                            "data-target=\"#showZpointExplParameters\"" +
                            "data-toggle=\"modal\"" +
                            "data-placement=\"bottom\"" +
                            "title=\"Эксплуатационные параметры точки учёта\">" +
                            "Эксплуатационные параметры" +
                        "</a></li>" +
                        "<li><a ng-click=\"openZpointMetadata(" + object.id + "," + zpoint.id + ")\"" +
//                                        "data-target=\"#metaDataEditorModal\""+
//                                        "data-toggle=\"modal\"" +
//                                        "data-placement=\"bottom\"" +
                            "title=\"Метаданные прибора\">" +
                            "Метаданные прибора" +
                        "</a></li>" +
                "</ul>" +
                "</div>" +
                "</td>";
            $scope.oldColumns.forEach(function (column) {
                switch (column.name) {
                case "zpointName":
                    var imgPath = "";
                    switch (zpoint['zpointType']) {
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
                    }
                    trHTML += "<td>";
                    trHTML += "<img class='marginLeft5' height=12 width=12 src=\"" + imgPath + "\"> <span class='paddingLeft5'></span>";
                    trHTML += (zpoint[column.name] || "Не задано") + "<span ng-show=\"isSystemuser()\">(id = " + zpoint.id + ")</span></td>";
                    break;
                case "zpointLastDataDate":
                    trHTML += "<td>{{" + zpoint[column.name] + " | date: 'dd.MM.yyyy HH:mm'}}</td>";
                    break;
                case "zpointRefRange":
                    trHTML += "<td id=\"zpointRefRange" + zpoint.id + "\"></td>";
                    break;
                default:
                    trHTML += "<td>" + (mainSvc.checkUndefinedNull(zpoint[column.name]) ? "" : zpoint[column.name]) + "</td>";
                    break;
                }
            });
            trHTML += "<td>";
            trHTML += "<div class=\"btn-toolbar\">" +
                    "<div class=\"btn-group pull-right\">" +
                        "<i title=\"Удалить точку учета\" class=\"btn btn-xs glyphicon glyphicon-trash nmc-button-in-table\" ng-click=\"deleteZpointInit(" + object.id + "," + zpoint.id + ")\" data-target=\"#deleteZpointModal\" data-toggle=\"modal\"></i>" +
                    "</div>" +
                "</div>";
            trHTML += "</td>";
            trHTML += "</tr>";
        });
        trHTML += "</table></td>";
        trObjZp.innerHTML = trHTML;

        $compile(trObjZp)($scope);
    }
                
    var successCallbackOnZpointUpdate = function (e) {
//console.log(e);              
        //e.data содержит данные измененной/созданной точки учета
//                    их теперь нужно 
//                    размапить
        var mappedZpoint  = mapZpointProp(e.data);
//                    добавить/перезаписать список точек у объекта

//                    отрисовать таблицу точек учета

        notificationFactory.success();
        $('#showZpointOptionModal').modal('hide');
        //get current object index at objects and objectsOnPage arrays              
        var objectIndex = findObjectIndexInArray($scope.currentObject.id, $scope.objects);
        var objectOnPageIndex = findObjectIndexInArray($scope.currentObject.id, $scope.objectsOnPage);
        //update object info about zpoint count
        if (objectIndex > -1 && e.config.method === "POST") {
            $scope.objects[objectIndex].contObjectStats.contZpointCount += 1;
        }
        if (mainSvc.checkUndefinedNull($scope.currentObject.zpoints) || !angular.isArray($scope.currentObject.zpoints)) {
            //$scope.zpointSettings = {};
            return;
        }
        var curIndex = -1;
        $scope.currentObject.zpoints.some(function (elem, index) {
            if (elem.id === $scope.zpointSettings.id) {
                curIndex = index;
                return true;
            }
        });

        //update view name for zpoint
        if ((curIndex > -1)) {
            var repaintZpointTableFlag = false;
            if ($scope.currentObject.zpoints[curIndex].zpointName !== $scope.zpointSettings.customServiceName) {
                repaintZpointTableFlag = true;
            }
            objectIndex = -1;
            objectIndex = findObjectIndexInArray($scope.currentObject.id, $scope.objects);
            objectOnPageIndex = findObjectIndexInArray($scope.currentObject.id, $scope.objectsOnPage);
            if (objectIndex > -1) {
                //update zpoint data in arrays                             
                $scope.objects[objectIndex].zpoints[curIndex] = mappedZpoint;
                $scope.objectsOnPage[objectOnPageIndex].zpoints[curIndex] = mappedZpoint;
            }
            //remake zpoint table
            if ($scope.objectsOnPage[objectOnPageIndex].showGroupDetails === true) {
                mainSvc.sortItemsBy($scope.objectsOnPage[objectIndex].zpoints, 'zpointOrder');
                makeZpointTable($scope.objectsOnPage[objectOnPageIndex]);
            }
        } else {
//                        var objectIndex = -1;
//                        objectIndex = findObjectIndexInArray($scope.currentObject.id, $scope.objects);
//                        var objectOnPageIndex = findObjectIndexInArray($scope.currentObject.id, $scope.objectsOnPage);
            if (objectIndex > -1) {
                //update zpoint data in arrays
                $scope.objects[objectIndex].zpoints.push(mappedZpoint);
                if ($scope.objectsOnPage[objectOnPageIndex].showGroupDetails === true) {
                    mainSvc.sortItemsBy($scope.objectsOnPage[objectIndex].zpoints, 'zpointOrder');
                    makeZpointTable($scope.objectsOnPage[objectOnPageIndex]);
                }
            }
        }
        //$scope.zpointSettings = {};
    };
    
    $rootScope.$on(objectSvc.BROADCASTS.ZPOINT_SAVED, function (ev, args) {
        console.log(args);
        successCallbackOnZpointUpdate(args.response);
    })
                
    var successCallbackOnSetMode = function (e) {
        notificationFactory.success();
        $scope.objectCtrlSettings.allSelected = false;
        $scope.objects.forEach(function (el) {
            if (el.selected === true) {
                el.currentSettingMode = $scope.settedMode;
                el.imgsrc = 'images/object-mode-' + el.currentSettingMode + '.png';
            }
            el.selected = false;
        });
        $scope.objectsOnPage.forEach(function (el) {
            if (el.selected === true) {
                el.currentSettingMode = $scope.settedMode;
                el.imgsrc = 'images/object-mode-' + el.currentSettingMode + '.png';
            }
            el.selected = false;
        });
    };
                
    var deleteObjectFromArray = function (objId, targetArr) {
        var curInd = findObjectIndexInArray(objId, targetArr);
        if (curInd != -1) {
            targetArr.splice(curInd, 1);
        }
    };

    var successCallback = function (e, cb) {
        notificationFactory.success();
        $('#deleteObjectModal').modal('hide');
        $('#showObjOptionModal').modal('hide');
        $('#setClientModal').modal('hide');
    };
                
    var successCallbackUpdateObject = function (e) {
        $rootScope.$broadcast('objectSvc:requestReloadData');
//console.log(e);
//console.log($scope.currentObject);                    
        $scope.currentObject._activeContManagement = e._activeContManagement;
                            //update zpoints info
        var mode = "Ex";
        objectSvc.getZpointsDataByObject(e, mode).then(function (response) {
            var tmp = [],
                copyTmp = angular.copy(response.data);
//console.log(copyTmp);                              
            if (mode == "Ex") {
                tmp = response.data.map(function (el) {
                    var result = {};
                    result = el.object;
                    result.lastDataDate = el.lastDataDate;
//console.log(el.lastDataDate);                                    
                    return result;
                });
            } else {
                tmp = copyTmp;
            }
            var zPointsByObject = tmp,
                zpoints = [],
                i;
            for (i = 0; i < zPointsByObject.length; i += 1) {
                var zpoint = mapZpointProp(zPointsByObject[i]);
                zpoints[i] = zpoint;
            }
            mainSvc.sortItemsBy(zpoints, 'zpointOrder');
            e.zpoints = zpoints;

        });
        var objIndex = null;
        objIndex = findObjectIndexInArray(e.id, $scope.objects);
        if (objIndex != null) {$scope.objects[objIndex] = e; }
        objIndex = null;
        objIndex = findObjectIndexInArray(e.id, $scope.objectsOnPage);
        if (objIndex != null) {$scope.objectsOnPage[objIndex] = e; }
//                    $scope.currentObject = {};
        successCallback(e, null);
    };
                
    var successDeviceCallback = function (e) {
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
        $scope.currentObject.deleteObjectIds.forEach(function (el) {
            deleteObjectFromArray(el, $scope.objects);
            deleteObjectFromArray(el, $scope.objectsOnPage);
        });
        successCallback(e, null);
    };
                
    var successDeleteZpointCallback = function (e) {
        $('#deleteZpointModal').modal('hide');
        deleteObjectFromArray($scope.currentZpoint.id, $scope.currentObject.zpoints);
        makeZpointTable($scope.currentObject);
        //update object info about zpoint count
        var objectIndex = findObjectIndexInArray($scope.currentObject.id, $scope.objects);
        if (objectIndex > -1) {
            $scope.objects[objectIndex].contObjectStats.contZpointCount -= 1;
            deleteObjectFromArray($scope.currentZpoint.id, $scope.objects[objectIndex].zpoints);
        }

        successCallback(e, null);
    };

    function successCreateObjectAtGroupCallback() {
        getObjectsData();
    }
                
    var errorProtoCallback = function (e) {
        console.log(e);
        var errorCode = "-1";
        if (mainSvc.checkUndefinedNull(e) || mainSvc.checkUndefinedNull(e.data)) {
            errorCode = "ERR_CONNECTION";
        }
        if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || (!mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode)))) {
            errorCode = e.resultCode || e.data.resultCode;
        }
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };

    var errorCallback = function (e) {
        errorProtoCallback(e);
        //zpoint settings saving flag reset
        $scope.zpointSettings.isSaving = false;
        if (!mainSvc.checkUndefinedNull($scope.currentObject)) {
            $scope.currentObject.isSaving = false;
        }
    };

    var successPostCallback = function (e) {
        successCallback(e, null);
        if ($scope.objectCtrlSettings.isTreeView == false || mainSvc.checkUndefinedNull($scope.data.currentTree)) {
            getObjectsData(e.id);
            //if current group is defined
            if (!mainSvc.checkUndefinedNull($scope.data.currentGroupId) && !mainSvc.checkUndefinedNull($scope.data.currentGroup)) {
                var targetUrl = $scope.groupUrl;//+"/"+$scope.currentGroup.id;
                var objectIds = $scope.objects.map(function (el) {
                    return el.id;
                });
                objectIds.push(e.id);

                crudGridDataFactory(targetUrl).update({contObjectIds: objectIds}, $scope.data.currentGroup, successCreateObjectAtGroupCallback, errorCallback);
            }
        } else {
        //if tree is on
            if (!mainSvc.checkUndefinedNull($scope.data.selectedNode) && (mainSvc.checkUndefinedNull($scope.data.selectedNode.type) || $scope.data.selectedNode.type != 'root')) {
                $scope.data.selectedNodeForMove = angular.copy($scope.data.selectedNode);
                $scope.data.treeForMove = angular.copy($scope.data.currentTree);
                $scope.data.treeForMove.childObjectList.shift();
                $scope.data.treeForMove.movingObjects = [e.id];
                if (!mainSvc.checkUndefinedNull(e.id)) {
                    $scope.data.moveToObjectId = e.id;
                }
                $scope.moveToNode();
            } else {
                $scope.loadTree($scope.data.currentTree, e.id);
            }
        }

    };

    $scope.addObject = function (url, obj) {
        if (angular.isDefined(obj.contManagementId) && (obj.contManagementId != null)) {
            url += "/?cmOrganizationId=" + obj.contManagementId;
        }
        obj._daDataSraw = null;
        if (!mainSvc.checkUndefinedNull($scope.currentSug)) {
            obj._daDataSraw = JSON.stringify($scope.currentSug);
        }
        crudGridDataFactory(url).save(obj, successPostCallback, errorCallback);
    };

    $scope.deleteObject = function (obj) {
        var url = objectSvc.getRmaObjectsUrl();
        if (angular.isDefined(obj) && (angular.isDefined(obj.id)) && (obj.id != null)) {
            crudGridDataFactory(url).delete({ id: obj[$scope.extraProps.idColumnName] }, successDeleteCallback, errorCallback);
        }
    };

    $scope.deleteObjects = function (obj) {
        var url = objectSvc.getRmaObjectsUrl();
        if (angular.isDefined(obj) && (angular.isDefined(obj.id)) && (obj.id != null)) {
            crudGridDataFactory(url).delete({ id: obj[$scope.extraProps.idColumnName] }, successDeleteCallback, errorCallback);
        } else if (angular.isDefined(obj.deleteObjects) && (obj.deleteObjects != null) && angular.isArray(obj.deleteObjects)) {
            crudGridDataFactory(url).delete({ contObjectIds: obj.deleteObjectIds }, successDeleteObjectsCallback, errorCallback);
        }
    };
                
    $scope.deleteZpoint = function (zpoint) {
        var url = objectSvc.getRmaObjectsUrl() + "/" + $scope.currentObject.id + "/zpoints";
//console.log(url);                                       
        crudGridDataFactory(url).delete({ id: zpoint[$scope.extraProps.idColumnName] }, successDeleteZpointCallback, errorCallback);
    };

    $scope.updateObject = function (url, object) {
        var params = { id: object[$scope.extraProps.idColumnName]};
        if (angular.isDefined(object.contManagementId) && (object.contManagementId != null)) {
            var cmOrganizationId = object.contManagementId;
            params = {
                /*id: object[$scope.extraProps.idColumnName],*/
                cmOrganizationId: cmOrganizationId
            };
        }
        object._daDataSraw = null;
        if (!mainSvc.checkUndefinedNull($scope.currentSug)) {
            object._daDataSraw = JSON.stringify($scope.currentSug);
        }
        crudGridDataFactory(url).update(params, object, successCallbackUpdateObject, errorCallback);
    };

    $scope.setOrderBy = function (field) {
        var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
        $scope.orderBy = { field: field, asc: asc };
    };
                
    var checkObjectSettings = function (obj) {
        //check name, timezone, uk, mode
        var result = true;
        if ($scope.emptyString(obj.fullName)) {
            notificationFactory.errorInfo("Ошибка", "Не задано наименование объекта!");
            result = false;
        }
        if ($scope.emptyString(obj.timezoneDefKeyname) && (obj.isLightForm != true)) {
            notificationFactory.errorInfo("Ошибка", "Не задан часовой пояс объекта!");
            result = false;
        }
        if ($scope.checkUndefinedNull(obj.contManagementId) && (obj.isLightForm != true)) {
            notificationFactory.errorInfo("Ошибка", "Не задана управляющая компания!");
            result = false;
        }
        if ($scope.emptyString(obj.currentSettingMode) && (obj.isLightForm != true)) {
            notificationFactory.errorInfo("Ошибка", "Не задан режим функционирования!");
            result = false;
        }
        return result;
    };
                
    $scope.sendObjectToServer = function (obj) {
        obj.isSaving = true;
        if (!checkObjectSettings(obj)) {
            obj.isSaving = false;
            return false;
        }
        var url = objectSvc.getRmaObjectsUrl();
        if (angular.isDefined(obj.id) && (obj.id != null)) {
            $scope.updateObject(url, obj);
        } else {
//                        obj.timezoneDefKeyname = "MSK";
            $scope.addObject(url, obj);
        }
    };

    $scope.selectedItem = function (item) {
        $scope.currentObject = angular.copy(item);
    };
                
    function testCmOrganizationAtList() {
        //find cmOrganization
        var cmOrgId = $scope.currentObject.contManagementId;
        var cmOrgFindFlag = false;
        $scope.data.cmOrganizations.some(function (org) {
            if (org.id == cmOrgId) {
                cmOrgFindFlag = true;
                return true;
            }
        });
        //if cm not found
        if (cmOrgFindFlag == false) {
            objectSvc.getCmOrganizationsWithId(cmOrgId).then(function (resp) {
                $scope.data.cmOrganizations = resp.data;
                mainSvc.sortOrganizationsByName($scope.data.cmOrganizations);
            }, function (e) {console.log(e); });
        }
//console.log(cmOrgId);                    
//console.log(cmOrgFindFlag);                    
//console.log($scope.data.cmOrganizations);                     
    }
                
    var checkGeo = function () {
        $scope.currentObject.geoState = "red";
        $scope.currentObject.geoStateText = "Не отображается на карте";
// console.log($scope.currentObject.isValidGeoPos);
// console.log($scope.currentSug);
// console.log($scope.currentSug.data.geo_lat);
// console.log($scope.currentSug.data.geo_lon);                
        if ($scope.currentObject.isValidGeoPos || !mainSvc.checkUndefinedNull($scope.currentSug) && $scope.currentSug.data.geo_lat != null && $scope.currentSug.data.geo_lon != null) {
            $scope.currentObject.geoState = "green";
            $scope.currentObject.geoStateText = "Отображается на карте";
        }
    };
                
    $scope.selectedObject = function (objId, isLightForm) {
        objectSvc.getRmaObject(objId)
            .then(function (resp) {
                $scope.currentObject = resp.data;
//    console.log($scope.currentObject);                        
                if (angular.isDefined($scope.currentObject._activeContManagement) && ($scope.currentObject._activeContManagement != null)) {
                    $scope.currentObject.contManagementId = $scope.currentObject._activeContManagement.organization.id;
                }
                testCmOrganizationAtList();
                if (!mainSvc.checkUndefinedNull(isLightForm)) {
                    $scope.currentObject.isLightForm = isLightForm;
                }
                if (!mainSvc.checkUndefinedNull($scope.currentObject.buildingType)) {
    //                            $scope.changeBuildingType($scope.currentObject.buildingType);
//                    performBuildingCategoryList($scope.currentObject.buildingType);
                    $scope.data.preparedBuildingCategoryListForUiSelect = objectSvc.performBuildingCategoryListForUiSelect($scope.currentObject.buildingType, $scope.data.buildingCategories);
                    setBuildingCategory();
                }
                checkGeo();
            }, function (error) {
                console.log(error);
            });
    };
                
    var getTemperatureSchedulesByObjectForZpoint = function (objId, zp) {
        $http.get($scope.objectCtrlSettings.tempSchBaseUrl + "/" + objId).then(function (resp) {
            zp.tempSchedules = resp.data;
            if (mainSvc.checkUndefinedNull(zp.temperatureChartId)) {
                return "temperatureChartId is null";
            }
            zp.tempSchedules.some(function (sch) {
                if (sch.id == zp.temperatureChartId) {
                    zp.tChart = sch;
                    return true;
                }
            });
        }, errorCallback);
    };
                
    $scope.selectedZpoint = function (objId, zpointId) {
        $scope.selectedItem(objectSvc.findObjectById(objId, $scope.objects));
//console.log($scope.currentObject);                     
        var curZpoint = null;
        if (angular.isDefined($scope.currentObject.zpoints) && angular.isArray($scope.currentObject.zpoints)) {
            $scope.currentObject.zpoints.some(function (element) {
                if (element.id === zpointId) {
                    curZpoint = angular.copy(element);
                }
            });
        }
        $scope.currentZpoint = curZpoint;
//console.log($scope.currentZpoint);                                        
    };
                
    $scope.toggleShowGroupDetails = function (objId) {//switch option: current goup details
        var curObject = objectSvc.findObjectById(objId, $scope.objects);//null;    
        //if cur object = null => exit function
        if (curObject == null) {
            return;
        }
        //else

        var zpTable = document.getElementById("zpointTable" + curObject.id);
//console.log(zpTable);                    
        if ((curObject.showGroupDetails == true) && (zpTable == null)) {
            curObject.showGroupDetails = true;
        } else {
            curObject.showGroupDetails = !curObject.showGroupDetails;
        }
        //if curObject.showGroupDetails = true => get zpoints data and make zpoint table
        if (curObject.showGroupDetails === true) {

            var mode = "/vo";
            objectSvc.getZpointsDataByObject(curObject, mode).then(function (response) {
                var tmp = [],
                    copyTmp = angular.copy(response.data);
//console.log(copyTmp);                              
                if (mode == "Ex") {
                    tmp = response.data.map(function (el) {
                        var result = {};
                        result = el.object;
                        result.lastDataDate = el.lastDataDate;
//console.log(el.lastDataDate);                                    
                        return result;
                    });
                } else {
                    tmp = copyTmp;
                }
                var zPointsByObject = tmp;
                var zpoints = [],
                    i;
                for (i = 0; i < zPointsByObject.length; i += 1) {
                    var zpoint = mapZpointProp(zPointsByObject[i]);
                    zpoints[i] = zpoint;
                }
                mainSvc.sortItemsBy(zpoints, 'zpointOrder');
                curObject.zpoints = zpoints;
                makeZpointTable(curObject);
                var btnDetail = document.getElementById("btnDetail" + curObject.id);
                if (!mainSvc.checkUndefinedNull(btnDetail)) {
                    btnDetail.classList.remove("glyphicon-chevron-right");
                    btnDetail.classList.add("glyphicon-chevron-down");
                }

                curObject.showGroupDetailsFlag = !curObject.showGroupDetailsFlag;
            },
                                                                   errorCallback
                                                                  );
        } else {//else if curObject.showGroupDetails = false => hide child zpoint table
            var trObj = document.getElementById("obj" + curObject.id);
            var trObjZp = trObj.getElementsByClassName("nmc-tr-zpoint")[0];//.getElementById("trObjZp");
            trObjZp.innerHTML = "";
            var btnDetail = document.getElementById("btnDetail" + curObject.id);
            btnDetail.classList.remove("glyphicon-chevron-down");
            btnDetail.classList.add("glyphicon-chevron-right");
        }
    };

                
    $scope.dateFormat = function (millisec) {
        var result = "";
        var serverTimeZoneDifferent = Math.round($scope.objectCtrlSettings.serverTimeZone * 3600.0 * 1000.0);
        var tmpDate = (new Date(millisec + serverTimeZoneDifferent));
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
    $scope.showRow = function (obj) {
        if ((typeof obj.isRead == 'undefined') && (!$scope.onlyNoRead)) {
            return true;
        }
        if ($scope.onlyNoRead) {
            if ($scope.onlyNoRead == !obj.isRead) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    };
                               
//                $scope.showDetails = function(obj) {
//                    if ($scope.bdirectories) {
//                        $scope.currentObject = obj;
//                        $('#showDirectoryStructModal').modal();
//                    }
//                };
        
                //Свойства точки учета
    $scope.zpointSettings = {};
    $scope.addZpoint = function (object) {
        $scope.selectedItem(object);
        $scope.zpointSettings = {};

//        if (!mainSvc.checkUndefinedNull($cookies.recentContServiceTypeKeyname)) {
//            $scope.zpointSettings.contServiceTypeKeyname = $cookies.recentContServiceTypeKeyname;
//            $scope.changeServiceType($scope.zpointSettings);
//        }
//        if (!mainSvc.checkUndefinedNull($cookies.recentRsoId)) {
//            $scope.zpointSettings.rsoId = Number($cookies.recentRsoId);
//        }
//        $scope.getDevices(object, false);
//        getTemperatureSchedulesByObjectForZpoint($scope.currentObject.id, $scope.zpointSettings);

        if (!mainSvc.checkUndefinedNull($cookies.get('recentContServiceTypeKeyname'))) {
            $scope.zpointSettings.contServiceTypeKeyname = $cookies.get('recentContServiceTypeKeyname');
            $scope.changeServiceType($scope.zpointSettings);
        }
        if (!mainSvc.checkUndefinedNull($cookies.get('recentRsoId'))) {
            $scope.zpointSettings.rsoId = Number($cookies.get('recentRsoId'));
        }
        $scope.getDevices(object, false);
        getTemperatureSchedulesByObjectForZpoint($scope.currentObject.id, $scope.zpointSettings);
    };
                
    $scope.getZpointSettings = function (objId, zpointId) {
        $scope.selectedZpoint(objId, zpointId);
        if (mainSvc.checkUndefinedNull($scope.currentZpoint)) {
            return "currentZpoint is undefined or null.";
        }
        
        var object = angular.copy($scope.currentZpoint);
        var zps = {};
        zps.id = object.id;
        zps.exSystemKeyname = object.exSystemKeyname;
        zps.tsNumber = object.tsNumber;
        zps.exCode = object.exCode;
        zps.version = object.version;
        zps.contObjectId = object.contObjectId;        
        zps.startDate = object.startDate;
        zps.deviceObjectId = object.deviceObjectId;       
        zps.rsoId = object.rsoId;
        zps.isManualLoading = object.isManualLoading;
        zps.customServiceName = object.customServiceName;
        zps.contServiceTypeKeyname = object.contServiceTypeKeyname;
        zps.contZPointComment = object.contZPointComment;
        zps.zpointName = object.zpointName;
        switch (object.zpointType) {
        case "heat":
            zps.zpointType = "Теплоснабжение";
            break;
        case "hw":
            zps.zpointType = "ГВС";
            break;
        case "cw":
            zps.zpointType = "ХВС";
            break;
        default:
            zps.zpointType = object.zpointType;
        }
        zps.piped = object.piped;
        zps.singlePipe = object.singlePipe;
        zps.doublePipe = object.doublePipe;
//console.log(zps);
        zps.zpointModel = object.zpointModel;
        zps.zpointRSO = object.zpointRSO;
        zps.checkoutTime = object.checkoutTime;
        zps.checkoutDay = object.checkoutDay;
        zps.isDroolsDisable = object.isDroolsDisable;
        zps.temperatureChartId = object.temperatureChartId;
        if (!mainSvc.checkUndefinedNull(object.flexData)) {
            zps.flexData = object.flexData;
        }
        zps.winter = {};
        zps.summer = {};
        $scope.zpointSettings = angular.copy(zps);
        $scope.getDevices($scope.currentObject, false);
//        getTemperatureSchedulesByObjectForZpoint($scope.currentObject.id, $scope.zpointSettings);         
    };
                
    function testRsoOrganizationAtList() {
        //find cmOrganization
        var rsoOrgId = $scope.zpointSettings.contManagementId;
        var rsoOrgFindFlag = false;
        $scope.data.rsoOrganizations.some(function (org) {
            if (org.id == rsoOrgId) {
                rsoOrgFindFlag = true;
                return true;
            }
        });
        //if cm not found
        if (rsoOrgFindFlag == false) {
            objectSvc.getRsoOrganizationsWithId(rsoOrgId).then(function (resp) {
                $scope.data.rsoOrganizations = resp.data;
                mainSvc.sortOrganizationsByName($scope.data.rsoOrganizations);
            }, function (e) {console.log(e); });
        }
//console.log(rsoOrgId);                    
//console.log(rsoOrgFindFlag);                    
//console.log($scope.data.rsoOrganizations);                     
    }
                
                
    $scope.getZpointSettingsEx = function (objId, zpointId) {
        $scope.getZpointSettings(objId, zpointId);
        testRsoOrganizationAtList();
    };

    $scope.getZpointSettingsExpl = function (objId, zpointId) {
        $scope.getZpointSettings(objId, zpointId);
        var winterSet = {},
            summerSet = {},
            i;
                            //http://localhost:8080/nmk-p/api/subscr/contObjects/18811505/zpoints/18811559/settingMode
        var table = $scope.crudTableName + "/" + $scope.currentObject.id + "/zpoints/" + $scope.zpointSettings.id + "/settingMode";
        crudGridDataFactory(table).query(function (data) {
            for (i = 0; i < data.length; i += 1) {
                if (data[i].settingMode == "winter") {
                    winterSet = data[i];
                } else if (data[i].settingMode == "summer") {
                    summerSet = data[i];
                }
            }
            $scope.zpointSettings.winter = winterSet;
            $scope.zpointSettings.summer = summerSet;
        });
    };

    var successZpointWinterCallback = function (e) {
        notificationFactory.success();
        $('#showZpointOptionModal').modal('hide');
        $('#showZpointExplParameters').modal('hide');
       //  $scope.zpointSettings = {};
    };
            
    var successZpointSummerCallback = function (e) {
        notificationFactory.success();
        var tableWinter = $scope.crudTableName + "/" + $scope.currentObject.id + "/zpoints/" + $scope.zpointSettings.id + "/settingMode";
        crudGridDataFactory(tableWinter).update({ id: $scope.zpointSettings.winter.id }, $scope.zpointSettings.winter, successZpointWinterCallback, errorCallback);
    };

    //Save new Zpoint | Update the common zpoint setiing - for example, Name
    //$scope.checkString  
    $scope.emptyString = function (str) {
        return mainSvc.checkUndefinedEmptyNullValue(str);
    };
    $scope.checkUndefinedNull = function (val) {
        return mainSvc.checkUndefinedNull(val);
    };
                
    var checkZpointCommonSettings = function () {
        //check name, type, rso, device
        var result = true;
        if ($scope.emptyString($scope.zpointSettings.customServiceName)) {
            notificationFactory.errorInfo("Ошибка", "Не задано наименование точки учета!");
            result = false;
        }
        if ($scope.emptyString($scope.zpointSettings.contServiceTypeKeyname)) {
            notificationFactory.errorInfo("Ошибка", "Не задан тип точки учета!");
            result = false;
        }
        if ($scope.checkUndefinedNull($scope.zpointSettings.deviceObjectId)) {
            notificationFactory.errorInfo("Ошибка", "Не задан прибор для точки учета!");
            result = false;
        }
        if ($scope.checkUndefinedNull($scope.zpointSettings.rsoId)) {
            notificationFactory.errorInfo("Ошибка", "Не задано РСО для точки учета!");
            result = false;
        }
        return result;
    };
                
//    $scope.updateZpointCommonSettings = function () {
//        $scope.zpointSettings.isSaving = true;
////console.log($scope.zpointSettings);
//        if (!checkZpointCommonSettings()) {
//            //zpoint settings saving flag reset
//            $scope.zpointSettings.isSaving = false;
//            return false;
//        }
//        //prepare piped info
////                    if ($scope.zpointSettings.singlePipe){
////                        $scope.zpointSettings.doublePipe = false;
////                    };
//            //perform temperature schedule
//        if (!mainSvc.checkUndefinedNull($scope.zpointSettings.tChart)) {
//            $scope.zpointSettings.temperatureChartId = $scope.zpointSettings.tChart.id;
//            $scope.zpointSettings.tChart = null;
//        } else {
//            $scope.zpointSettings.temperatureChartId = null;
//        }
//        var url = objectSvc.getRmaObjectsUrl() + "/" + $scope.currentObject.id + "/zpoints";
//        if (angular.isDefined($scope.zpointSettings.id) && ($scope.zpointSettings.id != null)) {
//            url = url + "/" + $scope.zpointSettings.id;
//
//            $http({
//                url: url,
//                method: 'PUT',
//                data: $scope.zpointSettings
//            })
//                .then(successCallbackOnZpointUpdate, errorCallback);
//        } else {
//            $scope.zpointSettings.startDate = Date.now();
//            $http({
//                url: url,
//                method: 'POST',
//                data: $scope.zpointSettings
//            })
//                .then(successCallbackOnZpointUpdate, errorCallback);
//        }
//
////                    crudGridDataFactory(url).update({}, $scope.zpointSettings, successCallback, errorCallback);
//    };
                
                //Update the zpoint settings, which set the mode for Summer or Winter season
    $scope.updateZpointModeSettings = function () {
        $scope.zpointSettings.isSaving = true;
        var tableSummer = $scope.crudTableName + "/" + $scope.currentObject.id + "/zpoints/" + $scope.zpointSettings.id + "/settingMode";
        crudGridDataFactory(tableSummer).update({ id: $scope.zpointSettings.summer.id }, $scope.zpointSettings.summer, successZpointSummerCallback, errorCallback);
    };
                
                // search objects
    $scope.searchObjects = function (searchString) {
        if (($scope.objects.length <= 0)) {
            return;
        }

                              //close all opened objects zpoints
        $scope.objectsOnPage.forEach(function (obj) {
            if (obj.showGroupDetailsFlag == true) {
                var trObj = document.getElementById("obj" + obj.id);
                if (!mainSvc.checkUndefinedNull(trObj)) {
                    var trObjZp = trObj.getElementsByClassName("nmc-tr-zpoint")[0];
                    trObjZp.innerHTML = "";
                    var btnDetail = document.getElementById("btnDetail" + obj.id);
                    btnDetail.classList.remove("glyphicon-chevron-down");
                    btnDetail.classList.add("glyphicon-chevron-right");
                }
            }
            obj.showGroupDetailsFlag = false;
        });

        var tempArr = [];
        if (angular.isUndefined(searchString) || (searchString === '')) {
            $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;
            tempArr =  $scope.objects.slice(0, $scope.objectCtrlSettings.objectsPerScroll);
            $scope.objectsOnPage = tempArr;
        } else {
//                        $scope.objectsOnPage = $scope.objects;
            $scope.objects.forEach(function (elem) {
                if (angular.isDefined(elem.fullName) && elem.fullName.toUpperCase().indexOf(searchString.toUpperCase()) != -1) {
                    tempArr.push(elem);
                }
            });
            $scope.objectsOnPage = tempArr;
        }
//console.log($scope.objectsOnPage);                    
    };
                
//    $scope.changeServiceType = function (zpSettings) {
//        if (!mainSvc.checkUndefinedNull(zpSettings.contServiceTypeKeyname)) {
//            $cookies.recentContServiceTypeKeyname = zpSettings.contServiceTypeKeyname;
//        }
//        if ($scope.emptyString(zpSettings.customServiceName)) {
//            switch (zpSettings.contServiceTypeKeyname) {
//            case "heat":
//                zpSettings.customServiceName = "Система отопления";
//                break;
//            default:
//                $scope.data.serviceTypes.some(function (svType) {
//                    if (svType.keyname == zpSettings.contServiceTypeKeyname) {
//                        zpSettings.customServiceName = svType.caption;
//                        return true;
//                    }
//                });
//
//            }
//        }
//    };

    $scope.changeServiceType = function (zpSettings) {
        if (!mainSvc.checkUndefinedNull(zpSettings.contServiceTypeKeyname)) {
            $cookies.put('recentContServiceTypeKeyname', zpSettings.contServiceTypeKeyname);
        }
        if ($scope.emptyString(zpSettings.customServiceName)) {
            switch (zpSettings.contServiceTypeKeyname) {
            case "heat":
                zpSettings.customServiceName = "Система отопления";
                break;
            default:
                $scope.data.serviceTypes.some(function (svType) {
                    if (svType.keyname == zpSettings.contServiceTypeKeyname) {
                        zpSettings.customServiceName = svType.caption;
                        return true;
                    }
                });

            }
        }
    };
                
    $scope.changeRso = function (zpSettings) {
        if (!mainSvc.checkUndefinedNull(zpSettings.rsoId)) {
            $cookies.put('recentRsoId', zpSettings.rsoId);
        }
    };
    
    $scope.removeTChart = function (zpSettings) {
        zpSettings.tChart = null;
        zpSettings.temperatureChartId = null;
    };

    $scope.$on('$destroy', function () {
        window.onkeydown = undefined;
    });
                
                
    //keydown listener for ctrl+end
    window.onkeydown = function (e) {
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
        var elem = document.getElementById("divWithObjectTable");
        if (e.keyCode == 38) {
            elem.scrollTop = elem.scrollTop - 20;
            return;
        }
        if (e.keyCode == 40) {
            elem.scrollTop = elem.scrollTop + 20;
            return;
        }
        if (e.keyCode == 34) {
            elem.scrollTop = elem.scrollTop + $scope.objectCtrlSettings.objectsPerScroll * 10;
            return;
        }
        if (e.keyCode == 33) {
            elem.scrollTop = elem.scrollTop - $scope.objectCtrlSettings.objectsPerScroll * 10;
            return;
        }
        if (e.ctrlKey && e.keyCode == 36) {
            elem.scrollTop = 0;
            return;
        }
        if (e.ctrlKey && e.keyCode == 35) {/*&& ($scope.objectCtrlSettings.objectsOnPage < $scope.objects.length)*/
            if ($scope.objectCtrlSettings.objectsOnPage < $scope.objects.length) {
                $scope.loading = true;
                $timeout(function () { $scope.loading = false; }, $scope.objects.length);
            }
            var tempArr = $scope.objects.slice($scope.objectCtrlSettings.objectsOnPage, $scope.objects.length);
            Array.prototype.push.apply($scope.objectsOnPage, tempArr);
            $scope.objectCtrlSettings.objectsOnPage += $scope.objects.length;
//                        $scope.objectCtrlSettings.isCtrlEnd = true;
            $scope.$apply();
            elem.scrollTop = elem.scrollHeight;

        }
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
    $scope.addMoreObjects = function () {
//console.log("addMoreObjects. Run");
        if (($scope.objects.length <= 0)) {
            return;
        }

        //set end of object array - определяем конечный индекс объекта, который будет выведен при текущем скролинге
        var endIndex = $scope.objectCtrlSettings.objectsOnPage + $scope.objectCtrlSettings.objectsPerScroll;
//console.log($scope.objects.length);                    
        if (endIndex >= $scope.objects.length) {
            endIndex = $scope.objects.length;
        }
        //вырезаем из массива объектов элементы с текущей позиции, на которой остановились в прошлый раз, по вычесленный конечный индекс
        var tempArr =  $scope.objects.slice($scope.objectCtrlSettings.objectsOnPage, endIndex);
            //добавляем к выведимому на экран массиву новый блок элементов
        Array.prototype.push.apply($scope.objectsOnPage, tempArr);
        if (endIndex >= $scope.objects.length) {
            $scope.objectCtrlSettings.objectsOnPage = $scope.objects.length;
        } else {
            $scope.objectCtrlSettings.objectsOnPage += $scope.objectCtrlSettings.objectsPerScroll;
        }
    };
                
    var tableScrolling = function () {
        if (angular.isUndefined($scope.filter) || ($scope.filter == '')) {
            $scope.addMoreObjects();
            $scope.$apply();
        }
    };

    $("#divWithObjectTable").scroll(tableScrolling);

    // Проверка пользователя - системный/ не системный
    $scope.isSystemuser = function () {
        var result = false;
        $scope.userInfo = $rootScope.userInfo;
        if (angular.isDefined($scope.userInfo)) {
            result = $scope.userInfo._system;
        }
        return result;
    };

    $scope.isTestMode = function () {
        return mainSvc.isTestMode();
    };
                
    //toggle all objects - selected/unselected
    $scope.toggleObjects = function (flag) {
        $scope.objectCtrlSettings.anySelected = flag;
        $scope.objects.forEach(function (el) {
            el.selected = flag;
        });
        $scope.objectsOnPage.forEach(function (el) {
            el.selected = flag;
        });
    };
        //toggle one object
    $scope.toggleObject = function (object) {
//                    object.selected = !object.selected;
        var anySelectedFlag = false;
        $scope.objectsOnPage.some(function (elem) {
            if (elem.selected == true) {
                anySelectedFlag = true;
                return true;
            }
        });
        $scope.objectCtrlSettings.anySelected = anySelectedFlag;
    };
                
    $scope.setModeForObjects = function (mode) {
        $scope.settedMode = mode;
        //get the object ids array
        var contObjectIds = [];
        if ($scope.objectCtrlSettings.allSelected === true) {
            contObjectIds = $scope.objects.map(function (el) { return el.id; });
        } else {
            $scope.objectsOnPage.forEach(function (el) {
                if (el.selected === true) {
                    contObjectIds.push(el.id);
                }
            });
        }

        //send data to server
        var url = objectSvc.getObjectsUrl() + "/settingModeType";
        $http({
            url: url,
            method: "PUT",
            params: { contObjectIds: contObjectIds, currentSettingMode: mode },
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
    $scope.getDevices = function (obj, showFlag) {
        obj.devicesLoading = true;
        objectSvc.getDevicesByObject(obj).then(
            function (response) {
                //select only vzlet devices
                var tmpArr = response.data;
                tmpArr.forEach(function (elem) {
                    // if (angular.isDefined(elem.contObjectInfo) && (elem.contObjectInfo != null)) {
                    //     elem.contObjectId = elem.contObjectInfo.contObjectId;
                    // }
                    if (angular.isDefined(elem.activeDataSource) && (elem.activeDataSource != null)) {
                        elem.subscrDataSourceId = elem.activeDataSource.subscrDataSource.id;
                        elem.curDatasource = elem.activeDataSource.subscrDataSource;
                        elem.subscrDataSourceAddr = elem.activeDataSource.subscrDataSourceAddr;
                        elem.dataSourceTable1h = elem.activeDataSource.dataSourceTable1h;
                        elem.dataSourceTable24h = elem.activeDataSource.dataSourceTable24h;
                    }

                    var tmpDevCaption = elem.deviceModel.modelName || "";
                    tmpDevCaption += elem.number ? ", №" + elem.number : "";
                    elem.devCaption = tmpDevCaption;
//                                if (!mainSvc.checkUndefinedNull(elem.number)) {
//                                    device.deviceModel.modelName + ', №' + device.number
//                                    elem.deviceCaption = tmpDevCaption + ", №" + elem.number;
//                                }
                });
                obj.devices = tmpArr;//response.data;                    
                if (!mainSvc.checkUndefinedNull(obj.devices) && obj.devices.length > 0 && mainSvc.checkUndefinedNull($scope.zpointSettings.deviceObjectId)) {
                    $scope.zpointSettings.deviceObjectId = obj.devices[0].id;                    
                }
//console.log(obj);                            
                if (showFlag == true) {
                    $('#contObjectDevicesModal').modal();
                }
                obj.devicesLoading = false;
                $scope.selectedItem(obj);
//                            if (showFlag == false){
//                                $('#contObjectDevicesModal').modal();
//                            };
            }, function (e) {
                errorCallback(e); 
                obj.devicesLoading = false;
            }/*function(error){
                notificationFactory.errorInfo(error.statusText,error.description);
            }*/
        );
    };
                
    $scope.invokeHelp = function () {
        alert('This is SPRAVKA!!!111');
    };


    var setConfirmCode = function (useImprovedMethodFlag) {
        $scope.confirmCode = null;
        var tmpCode = mainSvc.getConfirmCode(useImprovedMethodFlag);
        $scope.confirmLabel = tmpCode.label;
        $scope.sumNums = tmpCode.result;
    };

    $scope.deleteObjectInit = function (object) {
        $scope.selectedItem(object);
        //generation confirm code
        setConfirmCode();
    };

    $scope.deleteZpointInit = function (objId, zpointId) {
        //setConfirmCode();
        $scope.selectedZpoint(objId, zpointId);
        $scope.deleteObjectInit($scope.currentObject);
    };
                
    $scope.addObjectInit = function (isLightForm) {
//console.log("addObjectInit");                    
        $scope.currentObject = {};
        $scope.currentObject.isLightForm = isLightForm;
        if (!mainSvc.checkUndefinedNull($cookies.get('recentTimezone'))) {
            $scope.currentObject.timezoneDefKeyname = $cookies.get('recentTimezone');
        }
        if (!mainSvc.checkUndefinedNull($cookies.get('recentContManagementId'))) {
            $scope.currentObject.contManagementId = Number($cookies.get('recentContManagementId'));
        }
        if (!mainSvc.checkUndefinedNull($cookies.get('recentSettingMode'))) {
            $scope.currentObject.currentSettingMode = $cookies.get('recentSettingMode');
        }
        if (!mainSvc.checkUndefinedNull($cookies.get('recentBuildingType'))) {
            $scope.currentObject.buildingType = $cookies.get('recentBuildingType');
            $scope.changeBuildingType($scope.currentObject.buildingType);
        }
        if (!mainSvc.checkUndefinedNull($cookies.get('recentBuildingTypeCategory'))) {
            $scope.currentObject.buildingTypeCategory = $cookies.get('recentBuildingTypeCategory');
        }
        checkGeo();
        $('#showObjOptionModal').modal();
        $('#showObjOptionModal').css("z-index", "1041");
//console.log($scope.currentObject);                    
    };
                
    $scope.changeTimeZone = function () {
        if (!mainSvc.checkUndefinedNull($scope.currentObject.timezoneDefKeyname)) {
            $cookies.put('recentTimezone', $scope.currentObject.timezoneDefKeyname);
        }
    };
    $scope.changeContManagement = function () {
        if (!mainSvc.checkUndefinedNull($scope.currentObject.contManagementId)) {
            $cookies.put('recentContManagementId', $scope.currentObject.contManagementId);
        }
    };
    $scope.changeSettingMode = function () {
        if (!mainSvc.checkUndefinedNull($scope.currentObject.currentSettingMode)) {
            $cookies.put('recentSettingMode', $scope.currentObject.currentSettingMode);
        }
    };
                
//                work with object devices
//                *******************************
    //get device models
    $scope.getDeviceModels = function () {
        objectSvc.getDeviceModels().then(
            function (response) {
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
    var getDatasources = function (url) {
        var targetUrl = url;
        $http.get(targetUrl)
            .then(function (response) {
                var tmp = response.data;
                $scope.data.dataSources = tmp;
                mainSvc.sortItemsBy($scope.data.dataSources, 'dataSourceName');
    //console.log(tmp);            
            },
                  errorCallback/*function(e){
                console.log(e);
                notificationFactory.errorInfo(e.statusText,e.description);
            }*/
                 );
    };
    getDatasources(objectSvc.getDatasourcesUrl());
                
    $scope.deviceDatasourceChange = function () {
        $scope.currentDevice.dataSourceTable1h = null;
        $scope.currentDevice.dataSourceTable24h = null;
        $scope.currentDevice.subscrDataSourceAddr = null;
        var curDataSource = null;
        $scope.data.dataSources.some(function (source) {
            if (source.id === $scope.currentDevice.subscrDataSourceId) {
                curDataSource = source;
                return true;
            }
        });
        $scope.currentDevice.curDatasource = curDataSource;
    };
                
    $scope.selectDevice = function (device) {
        $scope.currentDevice = angular.copy(device);
    //                    $scope.currentDevice.contObjectId = $scope.currentObject.id;
    };

    $scope.addDevice = function () {
        $scope.currentDevice = {};
        $scope.currentDevice.contObjectId = $scope.currentObject.id;
        $('#showDeviceModal').modal();
    };

    $scope.deleteDeviceInit = function (device) {
        setConfirmCode();
        $scope.selectDevice(device);
        //$('#deleteDeviceModal').modal();
    };
                
    $scope.deleteObjectsInit = function () {
        //generate confirm code
        setConfirmCode();

        $scope.currentObject = {};
        var tmpArr = [];
        if ($scope.objectCtrlSettings.allSelected == true) {
            tmpArr = $scope.objects;
            $scope.currentObject.deleteObjects = angular.copy($scope.objects);
            //create array with objects ids 
            $scope.currentObject.deleteObjectIds = $scope.objects.map(function (obj) { return obj.id; });
            $scope.currentObject.countDeleteObjects = $scope.objects.length;
            $('#deleteObjectModal').modal();
        } else {
            tmpArr = $scope.objectsOnPage;
            var dcount = 0;
            var dmas = [];
            // object ids arr  for delete
            var dmasIds = [];
            tmpArr.forEach(function (el) {
                if (el.selected == true) {
                    dmas.push(angular.copy(el));
                    dmasIds.push(el.id);
                    dcount += 1;
                }
            });
            if (dcount > 0) {
                $scope.currentObject.deleteObjects = dmas;
                $scope.currentObject.deleteObjectIds = dmasIds;
                $scope.currentObject.countDeleteObjects = dcount;
                $('#deleteObjectModal').modal();
            }
        }
    };
                
    $scope.saveDevice = function (device) {
        //check device data
        var checkDsourceFlag = true;
        if (device.contObjectId == null) {
            notificationFactory.errorInfo("Ошибка", "Не задан объект учета");
            checkDsourceFlag = false;
        }
        if (device.subscrDataSourceId == null) {
            notificationFactory.errorInfo("Ошибка", "Не задан источник данных");
            checkDsourceFlag = false;
        }
        if (checkDsourceFlag === false) {
            return;
        }
//console.log(device);        
        //send to server
        objectSvc.sendDeviceToServer(device).then(successDeviceCallback, errorCallback);
    };

    $scope.deleteDevice = function (device) {
//console.log(device);        
        var targetUrl = objectSvc.getRmaObjectsUrl();
        targetUrl = targetUrl + "/" + device.contObjectId + "/deviceObjects/" + device.id;
        $http.delete(targetUrl).then(successDeviceCallback, errorCallback);
    };
// **************************************************************************************************
                
//******************************* Work with subscribers ****************************************
// *********************************************************************************************                
                //    get subscribers
    var getClients = function () {
        var targetUrl = $scope.objectCtrlSettings.clientsUrl;
        $http.get(targetUrl)
            .then(function (response) {
                if (mainSvc.checkUndefinedNull(response) || mainSvc.checkUndefinedNull(response.data) || !angular.isArray(response.data)) {
                    return false;
                }
                response.data.forEach(function (el) {
                    if (mainSvc.checkUndefinedNull(el.organization)) {
                        return false;
                    }
                    el.organizationName = el.organization.organizationFullName || el.organization.organizationName;

                });
                $scope.data.clients = response.data;
    //console.log($scope.data.clients);            
            },
                 function (e) {
                    console.log(e);
                });
    };
                                             
    var deleteDoubleElements = function (targetArray) {
        var resultArray = angular.copy(targetArray);
        resultArray = resultArray.sort();
        var arrLength = resultArray.length;
        while (arrLength >= 2) {
            arrLength -= 1;
            if (resultArray[arrLength] == resultArray[arrLength - 1]) {
                resultArray.splice(arrLength, 1);
            }
        }
        return resultArray;
    };
                
    var prepareClient = function (table, objIdsArr) {
        var ids = angular.copy(objIdsArr);
        $http.get(table).then(function (response) {
            var subscrObjs = response.data;
            var subscrObjIds = subscrObjs.map(function (obj) {return obj.id; });
//console.log(objIdsArr);                        
//console.log(subscrObjIds);                        
            Array.prototype.push.apply(ids, subscrObjIds);
            ids = deleteDoubleElements(ids);
//console.log(table);                        
//console.log(ids);                        
            $http.put(table, ids).then(successPostCallback, errorCallback);
        },
            function (e) {
                console.log(e);
            });
    };
                
                //инициализируем переменные и интерфейсы для назначения объектов абонентам
    $scope.setClientsInit = function (object) {
        $scope.currentObject = {};
        $scope.data.clientsOnPage = angular.copy($scope.data.clients);
        if (!mainSvc.checkUndefinedNull(object)) {
//                        object.selected = true;
            $scope.selectedItem(object);
            $scope.objectCtrlSettings.isSetClientForOneObject = true;//включаем флаг того, что назначаем абонентов только одному текущему объекту

            //get current object client list
            objectSvc.getRmaObjectSubscribers(object.id).then(function (resp) {
                if (!angular.isArray(resp.data)) {
                    $('#setClientModal').modal();
                    return "Object subscribers array is empty.";
                }
                //if object subscribers is no empty,
                //set seleted flag for clients at clients form.
                var selectedClientIds = resp.data;
                selectedClientIds.forEach(function (selectedClientId) {
                    $scope.data.clientsOnPage.some(function (clientOnPage) {
                        if (selectedClientId == clientOnPage.id) {
                            clientOnPage.selected = true;
                            clientOnPage.selectionDisabled = true;
                            return true;
                        }
                    });
                });
                //view clients form
                $('#setClientModal').modal();
            }, function (e) {
                console.log(e);
            });
        } else {
            $('#setClientModal').modal();
        }
    };
                
    var prepareObjectsIdsArray = function () {
        var tmp = [];
        if ($scope.objectCtrlSettings.allSelected == true) {
            $scope.objects.forEach(function (elem) {
                if (elem.selected == true) {
                    tmp.push(elem.id);
                    elem.selected = false;
                }
            });
        } else {
            $scope.objectsOnPage.forEach(function (elem) {
                if (elem.selected == true) {
                    tmp.push(elem.id);
                    elem.selected = false;
                }
            });
        }
        return tmp;
    };
                
    //отправляем запрос на назначение на сервер
    $scope.setClients = function () {
        var tmp = [];//массив id объектов, которым назначаются абоненты
        if ($scope.objectCtrlSettings.isSetClientForOneObject == true) {
            tmp.push($scope.currentObject.id);//передать в массив id текущего объекта
            $scope.objectCtrlSettings.isSetClientForOneObject = false;//сбросить флаг
        } else {
        //собираем идишники выбранных объектов в один массив
            tmp = prepareObjectsIdsArray();
            $scope.objectCtrlSettings.anySelected = false;
        }
        //для каждого абонента надо вызвать rest для задания объектов, 
        //передавая полученный массив идишников
        $scope.data.clientsOnPage.forEach(function (cl) {
            if ((cl.id != null) && (typeof cl.id != 'undefined') && (cl.selected == true) && (cl.selectionDisabled != true)) {
                var url = $scope.objectCtrlSettings.rmaUrl + "/" + cl.id + $scope.objectCtrlSettings.subscrObjectsSuffix;
                prepareClient(url, tmp);
            }
        });
    };
                
    $scope.selectAllClients = function () {
        $scope.data.clientsOnPage.forEach(function (el) {
            if (el.selectionDisabled == true) {return; }
            el.selected = $scope.objectCtrlSettings.selectedAllClients;
        });
        $scope.objectCtrlSettings.anyClientSelected = $scope.objectCtrlSettings.selectedAllClients;
    };

    $scope.selectClient = function () {
        var anySelectedFlag = false;
        $scope.data.clientsOnPage.some(function (elem) {
            if (elem.selected == true && elem.selectionDisabled != true) {
                anySelectedFlag = true;
                return true;
            }
        });
        $scope.objectCtrlSettings.anyClientSelected = anySelectedFlag;
    };
// ******************************* end subscriber region ***********************
                
    $scope.objectCtrlSettings.objectModalWindowTabs = [
        {
            name: "main_object_properties_tab",
            tabpanel: "main_object_properties"
        }, {
            name: "extra_object_properties_tab",
            tabpanel: "extra_object_properties"
        }
    ];
                
    function setActiveObjectPropertiesTab(tabName) {
        $scope.objectCtrlSettings.objectModalWindowTabs.forEach(function (tabElem) {
            var tab, tabPanel;
            tab = document.getElementById(tabElem.name) || null;
            tabPanel = document.getElementById(tabElem.tabpanel) || null;
            if (tabElem.name.localeCompare(tabName) !== 0) {

                tab.classList.remove("active");
                tabPanel.classList.remove("active");
            } else {
                tab.classList.add("active");
                tabPanel.classList.add("in");
                tabPanel.classList.add("active");
            }
        });
    }
                
    //checkers            
    $scope.checkEmptyNullValue = function (numvalue) {
        var result = false;
        if ((numvalue === "") || (numvalue == null)) {
            result = true;
            return result;
        }
        return result;
    };

//    function isNumeric(n) {
//        return !isNaN(parseFloat(n)) && isFinite(n);
//    }

    $scope.checkNumericValue = function (numvalue) {
        return mainSvc.checkNumericValue(numvalue);
//        var result = true;
//        if ($scope.checkEmptyNullValue(numvalue)) {
//            return result;
//        }
//        if (!isNumeric(numvalue)) {
//            result = false;
//            return result;
//        }
//        return result;
    };
                
    $scope.checkPositiveNumberValue = function (numvalue) {
        return mainSvc.checkPositiveNumberValue(numvalue);
//        var result = true;
//        result = $scope.checkNumericValue(numvalue);
//        if (!result) {
//            //if numvalue is not number -> return false
//            return result;
//        }
//        result = parseInt(numvalue, RADIX) >= 0 ? true : false;
//        return result;
    };

//    $scope.isPositiveNumberValue = function (val) {
//        return mainSvc.isPositiveNumberValue(val);
//    };
                
    $scope.checkNumericInterval = function (leftBorder, rightBorder) {
        if ($scope.checkEmptyNullValue(leftBorder) || $scope.checkEmptyNullValue(rightBorder)) {
            return false;
        }
        if (!(($scope.checkNumericValue(leftBorder)) && ($scope.checkNumericValue(rightBorder)))) {
            return false;
        }
        if (parseInt(rightBorder, RADIX) >= parseInt(leftBorder, RADIX)) {
            return true;
        }
        return false;
    };

    $scope.checkHHmm = function (hhmmValue) {
        return mainSvc.checkHHmm(hhmmValue);
//console.log(hhmmValue);                    
//        if (/(0[0-9]|1[0-9]|2[0-3]){1,2}:([0-5][0-9]){1}/.test(hhmmValue)) {
//            return true;
//        }
//        return false;
    };
                
    $scope.checkZpointSettingsFrom = function (zpointSettings) {
        if ((zpointSettings == null) || (!zpointSettings.hasOwnProperty('summer')) || (!zpointSettings.hasOwnProperty('winter'))) {
            return true;
        }
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
                
    $scope.checkObjectPropertiesForm = function (object) {
//                    if ((object==null)||(object.cwTemp === undefined)){
        if ((object == null) || (!object.hasOwnProperty('cwTemp')) || (!object.hasOwnProperty('heatArea'))) {
            return true;
        }
        return $scope.checkNumericValue(object.cwTemp) && ($scope.checkNumericValue(object.heatArea));
    };
    
    function objectAddressChange(ev) {
//console.log($scope.currentObject);
//console.log(ev);            
//return;            
//        if (!mainSvc.checkUndefinedNull($scope.currentObject) && $scope.currentObject.isSaving === true) {
//            return;
//        }
        $scope.currentSug = null;
        $scope.currentObject.isAddressAuto = false;
        $scope.currentObject.isValidGeoPos = false;
        checkGeo();
        $scope.$apply();
    }
                
    $(document).ready(function () {
        $('#inputTSNumber').inputmask();
        $('#inputEXCode').inputmask();
        $("#inputObjAddress").suggestions({
            serviceUrl: "https://dadata.ru/api/v2",
            token: "f9879c8518e9c9e794ff06a6e81eebff263f97d5",
            type: "ADDRESS",
            count: 10,
            /* Вызывается, когда пользователь выбирает одну из подсказок */
            onSelect: function (suggestion) {
//                            console.log(suggestion);
                $scope.currentObject.fullAddress = suggestion.value;
                $scope.currentSug = suggestion;
                $scope.currentObject.isAddressAuto = true;
                checkGeo();
                $scope.$apply();
            },
             /* Вызывается, когда получен ответ от сервера */
            onSearchComplete: function (query, suggestions) {
                // $scope.currentSug = null;
                // if (angular.isArray(suggestions) && (suggestions.length > 0)){                               
                //     $scope.currentSug = suggestions[0];
                // };
                // $scope.$apply();
            },
            /*Если пользователь ничего не выбрал*/
            onSelectNothing: function (query) {
                objectAddressChange(query);
            }
        });
//        $("#inputObjAddress").change(objectAddressChange);
    });
                
    $('#showZpointOptionModal').on('hidden.bs.modal', function () {
        $scope.zpointSettings = {};
    });
    $('#showZpointExplParameters').on('hidden.bs.modal', function () {
        $scope.zpointSettings = {};
    });

    $('#showObjOptionModal').on('hidden.bs.modal', function () {
        if (!mainSvc.checkUndefinedNull($scope.currentObject)) {
            $scope.currentObject.isSaving = false;
        }
        $scope.currentSug = null;
        setActiveObjectPropertiesTab("main_object_properties_tab");
    });
                
// *****************************************************************************************
//                Zpoint metadata
// ******************************************************************************************
    $scope.zpointMetadataIntegratorsFlag = false;

    $scope.openZpointMetadata = function (coId, zpId) {
        $scope.selectedZpoint(coId, zpId);
        //get srcProp
        objectSvc.getZpointMetaSrcProp(coId, zpId).then(
            function (resp) {
                resp.data.forEach(function (src) {
                    src.title = "Хинт для " + src.columnName;
                });
                $scope.currentZpoint.metaData = {};
                $scope.currentZpoint.metaData.srcProp = resp.data;
                $scope.currentZpoint.metaData.srcProp.push({columnName: "", title: "Пустая строка"});
                objectSvc.getZpointMetaDestProp(coId, zpId).then(
                    function (resp) {
                        $scope.currentZpoint.metaData.destProp = resp.data;
                        objectSvc.getZpointMetaMeasureUnits(coId, zpId);
                    },
                    errorCallback
                );
            },
            errorCallback
        );
    };
                
    $scope.$on('objectSvc:zpointMetadataMeasuresLoaded', function () {
        $scope.currentZpoint.metaData.measures = objectSvc.getZpointMetadataMeasures();
        objectSvc.getZpointMetadata($scope.currentZpoint.contObjectId, $scope.currentZpoint.id).then(
            function (resp) {
                mainSvc.sortItemsBy(resp.data, "destProp");
                $scope.currentZpoint.metaData.metaData = resp.data;
                $('#metaDataEditorModal').modal();
//console.log($scope.currentZpoint);                                            
            },
            errorCallback
        );
    });

    $scope.updateZpointMetaData = function (zpoint) {
//console.log(zpoint);                    
        objectSvc.saveZpointMetadata(zpoint.contObjectId, zpoint.id, zpoint.metaData.metaData).then(
            function (resp) {
                $('#metaDataEditorModal').modal('hide');
                notificationFactory.success();
            },
            errorCallback
        );
    };

    $scope.log = function (logObj) {
        console.log(logObj);
    };
                
//    $scope.showMetaSrcHelp = function (srcName, fieldId, colName, field) {
//console.log(field);                    
//        if (mainSvc.checkUndefinedNull(srcName) || mainSvc.checkUndefinedNull($scope.currentZpoint.metaData.srcProp)){
//            return "Input params is incorrect!";
//        }
//        var tmpSrc = null,
//            helpText = "";
//console.log(srcName);
//console.log($scope.currentZpoint.metaData.srcProp);                    
//        $scope.currentZpoint.metaData.srcProp.some(function (src) {
//            if (srcName === src.columnName) {
//                tmpSrc = src;
//                return true;
//            }
//        });
//console.log(tmpSrc);                    
//        if (mainSvc.checkUndefinedNull(tmpSrc)) {
//            return false;
//        }
//        helpText = "Поле прибора: " + tmpSrc.deviceMapping + "<br>";
//        helpText = "Описание: <br> " + tmpSrc.deviceMappingInfo;
//        var targetElem = "#srcHelpBtn"+fieldId+colName;
//        mainSvc.setToolTip("Описание поля источника", helpText, targetElem, targetElem, 10, 500); 
//    };
// *****************************************************************************************
//                end Zpoint metadata
// ******************************************************************************************                
                
// ********************************************************************************************
                //  TREEVIEW
//*********************************************************************************************
    $scope.viewFullObjectList = function () {
        $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;
        $scope.data.currentTree = null;
        $scope.data.selectedNode = null;
        $scope.objectCtrlSettings.isFullObjectView = true;
        $scope.messages.treeMenuHeader = 'Полный список объектов';
        getObjectsData();
    };

    $scope.objectCtrlSettings.isTreeView = true;
    $scope.objectCtrlSettings.isFullObjectView = false;

    $scope.data.trees = [];
    $scope.data.currentTree = {};
    $scope.data.newTree = {};
    $scope.data.defaultTree = null;// default tree
    $scope.data.selectedNode = null;

    var findNodeInTree = function (node, tree) {
        return mainSvc.findNodeInTree(node, tree);
    };
                
    $scope.selectNode = function (item) {
        var treeForSearch = $scope.data.currentTree;
        var selectedNode = $scope.data.selectedNode;
        if (!mainSvc.checkUndefinedNull(selectedNode)) {
            if (selectedNode.id == item.id || selectedNode.type == item.type == 'root') {
                return;
            }
            var preNode = findNodeInTree(selectedNode, treeForSearch);
            if (!mainSvc.checkUndefinedNull(preNode)) {
                preNode.isSelected = false;
            }
        }

        item.isSelected = true;
        $scope.data.selectedNode = angular.copy(item);
        $scope.loading = true;
        if (item.type == 'root') {
            objectSvc.loadSubscrFreeObjectsByTree($scope.data.currentTree.id).then(performObjectsData);
        } else {
            objectSvc.loadSubscrObjectsByTreeNode($scope.data.currentTree.id, item.id).then(performObjectsData);
        }
    };
                
    $scope.loadTree = function (tree, objId) {/*for moving*/
        $scope.loading = true;
        if (!mainSvc.checkUndefinedNull(objId)) {
            $scope.data.moveToObjectId =  objId;
        }
        objectSvc.loadSubscrTree(tree.id).then(function (resp) {
            $scope.messages.treeMenuHeader = tree.objectName || tree.id;
            var respTree = angular.copy(resp.data);
            mainSvc.sortTreeNodesBy(respTree, "objectName");
            $scope.data.currentTree = respTree;
            $scope.data.selectedNode = null;
            $scope.objects = [];
            $scope.objectsOnPage = [];
            $scope.loading = false;
            $rootScope.$broadcast('objectSvc:loaded');
            $scope.messages.noObjects = "";
            if (!mainSvc.checkUndefinedNull($scope.data.moveToObjectId)) {
                moveToObject($scope.data.moveToObjectId);
                $scope.data.moveToObjectId = null;
            }
        }, errorCallback);
    };
                
//                var loadTrees = function(){
//                    objectSvc.loadSubscrTrees().then(function(resp){
//                        mainSvc.sortItemsBy(resp.data, "objectName");
//                        $scope.data.trees = angular.copy(resp.data);
//                        if (!angular.isArray($scope.data.trees) || $scope.data.trees.length <= 0 || mainSvc.checkUndefinedNull($scope.data.defaultTree) ){ 
//                            $scope.viewFullObjectList();
//                            return "View full object list!";
//                        }; 
//                        $scope.loadTree($scope.data.defaultTree);                        
//                    }, errorCallback);
//                };
                
    var loadTrees = function (treeSetting) {
        $scope.treeLoading = true;
        objectSvc.loadSubscrTrees().then(function (resp) {
            $scope.treeLoading = false;
            mainSvc.sortItemsBy(resp.data, "objectName");
            $scope.data.trees = angular.copy(resp.data);
            if (!mainSvc.checkUndefinedNull(treeSetting) && (treeSetting.isActive == true)) {
                $scope.data.defaultTree = mainSvc.findItemBy($scope.data.trees, "id", Number(treeSetting.value));
            }
            if (!angular.isArray($scope.data.trees) || $scope.data.trees.length <= 0 || mainSvc.checkUndefinedNull($scope.data.defaultTree)) {
                $scope.viewFullObjectList();
                return "View full object list!";
            }
            $scope.loadTree($scope.data.defaultTree);

        }, errorCallback);
    };

    var successLoadTreeSetting = function (resp) {
        $scope.objectCtrlSettings.isTreeView = resp.data.isActive;
        loadTrees(resp.data);
    };
                
    $scope.toggleTreeView = function () {
        $scope.objectCtrlSettings.isTreeView = !$scope.objectCtrlSettings.isTreeView;
        $scope.data.currentGroupId = null;//reset current group
        //if tree is off
        if ($scope.objectCtrlSettings.isTreeView == false) {
            getObjectsData();
        } else {
        //if tree is on
//                        loadTrees();
            objectSvc.loadDefaultTreeSetting().then(successLoadTreeSetting, errorCallback);
        }
    };

//                $scope.toggleTreeView = function(){
//                    $scope.objectCtrlSettings.isTreeView = !$scope.objectCtrlSettings.isTreeView;
//                    //if tree is off
//                    if ($scope.objectCtrlSettings.isTreeView == false){
//                        getObjectsData();
//                    }else{
//                    //if tree is on
//                        objectSvc.loadDefaultTreeSetting().then(successLoadTreeSetting, errorCallback);                     
//                    };
//                };

    $scope.moveToNode = function () {
        objectSvc.putObjectsToTreeNode($scope.data.currentTree.id, $scope.data.selectedNodeForMove.id, $scope.data.treeForMove.movingObjects).then(function (resp) {
            $scope.loading = true;
            if ($scope.data.selectedNode.type == 'root') {
                objectSvc.loadFreeObjectsByTree($scope.data.currentTree.id).then(performObjectsData);
            } else {
                objectSvc.loadObjectsByTreeNode($scope.data.currentTree.id, $scope.data.selectedNode.id).then(performObjectsData);
            }
        }, errorCallback);
    };
// ********************************************************************************************
                //  end TREEVIEW
//*********************************************************************************************
// ********************************************************************************************
                //  Building types
//*********************************************************************************************
    $scope.data.buildingTypes = [];
    $scope.data.buildingCategories = [];
    $scope.data.preparedBuildingCategoryList = [];
    $scope.data.buildingCategories = objectSvc.getBuildingCategories();
    $scope.data.buildingTypes = objectSvc.getBuildingTypes();
    $scope.$on(objectSvc.BROADCASTS.BUILDING_TYPES_LOADED, function () {
        $scope.data.buildingTypes = objectSvc.getBuildingTypes();
    });
    $scope.$on(objectSvc.BROADCASTS.BUILDING_CATEGORIES_LOADED, function () {
        $scope.data.buildingCategories = objectSvc.getBuildingCategories();
    });

    function performBuildingCategoryList(buildingType) {
        //find b cat when buildingType === input buildingType
        //find b cat when parentCat === keyname from up ^
        var categoryListByBuildingType = [],
            filtredCategoryList = [],
            preparedCategory = null;
        $scope.data.buildingCategories.forEach(function (bcat) {
            if (bcat.buildingType === buildingType) {
                categoryListByBuildingType.push(angular.copy(bcat));
            }
        });
        categoryListByBuildingType.forEach(function (pcat) {
            $scope.data.buildingCategories.forEach(function (bcat) {
                if (bcat.parentCategory === pcat.keyname) {
                    preparedCategory = angular.copy(bcat);
                    preparedCategory.parentCategoryCaption = pcat.caption;
                    filtredCategoryList.push(preparedCategory);
                }
            });
        });
        $scope.data.preparedBuildingCategoryList = filtredCategoryList;
//                    console.log($scope.data.preparedBuildingCategoryList);
    }
                
    $scope.changeBuildingType = function (buildingType) {
//                    console.log("changeBuildingType");
        $scope.currentObject.buildingTypeCategory = null;
        $cookies.put('recentBuildingTypeCategory', $scope.currentObject.buildingTypeCategory);
//        $('#inputBuildingCategory').removeClass('nmc-select-form-high');
//        $('#inputBuildingCategory').addClass('nmc-select-form');
        
        $('#inputBuildingCategoryUI').removeClass('nmc-ui-select-form-high');
        $('#inputBuildingCategoryUI').addClass('nmc-ui-select-form');
        
        if (mainSvc.checkUndefinedNull(buildingType)) {
            return false;
        }
        $cookies.put('recentBuildingType', buildingType);
//        performBuildingCategoryList(buildingType);
        $scope.data.preparedBuildingCategoryListForUiSelect = objectSvc.performBuildingCategoryListForUiSelect(buildingType, $scope.data.buildingCategories);
    };

    function setBuildingCategory() {
        var bCat = null;
//        $scope.data.preparedBuildingCategoryList.some(function (bcat) {
        $scope.data.preparedBuildingCategoryListForUiSelect.some(function (bcat) {
            if (bcat.keyname === $scope.currentObject.buildingTypeCategory) {
                bCat = bcat;
                return true;
            }
        });
//                    if (bCat === null) {
//                        return false;
//                    }
        //50 symbols
//                    console.log(bCat);
        if (bCat !== null && bCat.caption.length >= 50) {
//            $('#inputBuildingCategory').removeClass('nmc-select-form');
//            $('#inputBuildingCategory').addClass('nmc-select-form-high');
            $('#inputBuildingCategoryUI').removeClass('nmc-ui-select-form');
            $('#inputBuildingCategoryUI').addClass('nmc-ui-select-form-high');
        } else {
//            $('#inputBuildingCategory').removeClass('nmc-select-form-high');
//            $('#inputBuildingCategory').addClass('nmc-select-form');
            $('#inputBuildingCategoryUI').removeClass('nmc-ui-select-form-high');
            $('#inputBuildingCategoryUI').addClass('nmc-ui-select-form');
        }
        if (mainSvc.checkUndefinedNull($scope.currentObject.buildingTypeCategory)) {
            return false;
        }
    }
                
    $scope.changeBuildingCategory = function () {
        setBuildingCategory();
        $cookies.put('recentBuildingTypeCategory', $scope.currentObject.buildingTypeCategory);
    };
// ********************************************************************************************
                //  end Building types
//*********************************************************************************************                
                
                
    //set focus on first input element when window will be opened                
    $('#showTreeOptionModal').on('shown.bs.modal', function () {
        $('#inputTreeName').focus();
    });

    $('#treeLevelModal').on('shown.bs.modal', function () {
        $('#inputLevelName').focus();
    });

    $('#showObjOptionModal').on('shown.bs.modal', function () {
        $('#inputContObjectName').focus();
        $('#inputNumOfStories').inputmask('integer', {min: 1, max: 200});
    });

    //set tooltips for meta device fields
    function setMetaToolTips() {
        $scope.currentZpoint.metaData.metaData.forEach(function (metaField) {
            var tmpSrc = null,
                helpText = "";

            $scope.currentZpoint.metaData.srcProp.some(function (src) {
                if (metaField.srcProp === src.columnName) {
                    tmpSrc = src;
                    return true;
                }
            });
            if (mainSvc.checkUndefinedNull(tmpSrc) || mainSvc.checkUndefinedNull(tmpSrc.deviceMapping) || mainSvc.checkUndefinedNull(tmpSrc.deviceMappingInfo)) {

                return false;
            }
            metaField.haveToolTip = true;
            helpText += "<b>Ячейка памяти прибора:</b> " + tmpSrc.deviceMapping + "<br><br>";
            helpText += "<b>Назначение:</b> <br> " + tmpSrc.deviceMappingInfo;
            var targetElem = "#srcHelpBtn" + metaField.metaOrder + "srcProp";
            mainSvc.setToolTip("Описание поля источника", helpText, targetElem, targetElem, 10, 500);
        });
    }
                
    $scope.changeMetaToolTips = function () {
        setMetaToolTips();
    };

    $("#metaDataEditorModal").on('shown.bs.modal', function () {
        setMetaToolTips();
    });
    
//    $scope.viewDeviceArchive = function (zpSettings) {
//        if (!mainSvc.checkUndefinedNull(zpSettings.showDeviceArchive)) {
//            zpSettings.showDeviceArchive = !zpSettings.showDeviceArchive;
//        } else {
//            zpSettings.showDeviceArchive = true;
//        }
//        zpSettings.deviceArchiveLoading = true;
//        objectSvc.loadZpointDeviceArchive(zpSettings.id)
//            .then(function (resp) {
//                   zpSettings.deviceArchive = resp.data;
//                    //test
//                    zpSettings.deviceArchive = zpSettings.deviceArchive.concat(resp.data);
//            zpSettings.deviceArchive = zpSettings.deviceArchive.concat(resp.data);
//            zpSettings.deviceArchive = zpSettings.deviceArchive.concat(resp.data);
//            zpSettings.deviceArchive = zpSettings.deviceArchive.concat(resp.data);
//                //end test
//                    zpSettings.deviceArchiveLoading = false;
//                    $('#deviceArchiveModal').modal();
//                }, 
//                  function (e) {
//                    zpSettings.deviceArchiveLoading = false;
//                    errorCallback(e);
//                });
//    }

    //controller initialization
    var initCtrl = function () {
        getRsoOrganizations();
        getCmOrganizations();
//        getServiceTypes();
        getTimezones();
        getClients();
        //if tree is off
        if ($scope.objectCtrlSettings.isTreeView == false) {
            getObjectsData();
        } else {
        //if tree is on
//                        loadTrees(); 
            objectSvc.loadDefaultTreeSetting().then(successLoadTreeSetting, errorCallback);
        }
    };
    initCtrl();
//            }]
}]);