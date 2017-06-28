/*jslint node: true, eqeq: true, nomen: true, es5: true*/
/*global angular, $, moment, alert*/
'use strict';

angular.module('portalNMC')
    .controller('SettingsObjectViewCtrl', ['$scope', '$rootScope', '$routeParams', '$resource', '$cookies', '$compile', '$parse', '$timeout', 'crudGridDataFactory', 'notificationFactory', '$http', 'objectSvc', 'mainSvc', 'reportSvc', 'indicatorSvc', '$q', 'energoPassportSvc',
            function ($scope, $rootScope, $routeParams, $resource, $cookies, $compile, $parse, $timeout, crudGridDataFactory, notificationFactory, $http, objectSvc, mainSvc, reportSvc, indicatorSvc, $q, energoPassportSvc) {
                
            var RADIX = 10,
                VCOOKIE_URL = "../api/subscr/vcookie",
                USER_VCOOKIE_URL = "../api/subscr/vcookie/user",
                OBJECT_INDICATOR_PREFERENCES_VC_MODE = "OBJECT_INDICATOR_PREFERENCES",
                WIDGETS_URL = "../api/subscr/vcookie/widgets/list",
                OBJECT_PASSPORT_CREATION_WINDOW_NAME = 'objectPassport',
                DATE_FORMAT_FOR_DATEPICKER = "yy-mm-dd";

            var measureUnits = null,
                objectPassportCreationWindow = null;

            $scope.crudTableName = objectSvc.getObjectsUrl();

                //messages for user
            $scope.messages = {};
            $scope.messages.setSelectedInWinterMode = "Перевести выделенные объекты на зимний режим";
            $scope.messages.setSelectedInSummerMode = "Перевести выделенные объекты на летний режим";
            $scope.messages.setAllInWinterMode = "Перевести все объекты на зимний режим";
            $scope.messages.setAllInSummerMode = "Перевести все объекты на летний режим";
            $scope.messages.markAllOn = "Выбрать все";
            $scope.messages.markAllOff = "Отменить все";
            $scope.messages.setIndicatorInterfaceForObjects = "Выбрать представление для точек учета";

            $scope.messages.noObjects = "Объектов нет.";

            $scope.messages.groupMenuHeader = "Полный список объектов";

            $scope.messages.modeHeader = "Выберете представление";

            $scope.messages.setIndicatorInterface = "Настройка представлений для просмотра показаний";

                //object settings
            $scope.objectCtrlSettings = {};
            $scope.objectCtrlSettings.isCtrlEnd = false;
            $scope.objectCtrlSettings.allSelected = false;
            $scope.objectCtrlSettings.objectsPerScroll = objectSvc.OBJECT_PER_SCROLL;//the pie of the object array, which add to the page on window scrolling
            $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;//50;//current the count of objects, which view on the page
//                $scope.objectCtrlSettings.currentScrollYPos = window.pageYOffset || document.documentElement.scrollTop; 
//                $scope.objectCtrlSettings.objectTopOnPage =0;
//                $scope.objectCtrlSettings.objectBottomOnPage =34;

            //list of system for meta data editor
            $scope.objectCtrlSettings.vzletSystemList = [];

            //flag on/off extended user interface
            $scope.objectCtrlSettings.extendedInterfaceFlag = true;

            //server time zone at Hours
            $scope.objectCtrlSettings.serverTimeZone = 3;
            //date format for user
            $scope.objectCtrlSettings.dateFormat = "DD.MM.YYYY";

            //service permission settings
            $scope.objectCtrlSettings.mapAccess = false;
            $scope.objectCtrlSettings.mapCtxId = "object_map_2nd_menu_item";

                    //html- с индикатором загрузки страницы
            $scope.objectCtrlSettings.htmlLoading = mainSvc.getHtmlLoading();

            var setVisibles = function () {
                var tmp = mainSvc.getContextIds();
                tmp.forEach(function (element) {
                    var elDOM = document.getElementById(element.permissionTagId);//.style.display = "block";
                    if (angular.isUndefined(elDOM) || (elDOM === null)) {
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


            $scope.object = {};
            $scope.objects = [];
            $scope.objectsOnPage = [];
            $scope.currentSug = null;
            $scope.data = {};
            $scope.data.currentGroupId = null; //current group id: use for group object filter
            $scope.data.currentDevice = {};
            $scope.data.dataSourcesForCurDevice = [];
            $scope.data.deviceModelsForCurDevice = [];
            $scope.data.currentDatasource = {};
            $scope.data.datasourceTypesForDatasource = {};
            $scope.data.currentContObjectPassports = [];
                
            var checkGeo = function () {
                $scope.currentObject.geoState = "red";
                $scope.currentObject.geoStateText = "Не отображается на карте";
// console.log($scope.currentObject.isValidGeoPos);
// console.log($scope.currentSug);
//console.log($scope.currentObject);
// console.log($scope.currentSug.data.geo_lat);
// console.log($scope.currentSug.data.geo_lon);                
                if ($scope.currentObject.isValidGeoPos || (!mainSvc.checkUndefinedNull($scope.currentSug) && ($scope.currentSug.data.geo_lat != null && $scope.currentSug.data.geo_lon != null))) {
                    $scope.currentObject.geoState = "green";
                    $scope.currentObject.geoStateText = "Отображается на карте";
                }
            };

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

            var getCmOrganizations = function () {
                objectSvc.getCmOrganizations()
                    .then(function (response) {
                        $scope.data.cmOrganizations = response.data;
                        mainSvc.sortOrganizationsByName($scope.data.cmOrganizations);
                    });
            };
            getCmOrganizations();

//console.log(objectSvc.promise);

            var successGetObjectsCallback = function (response) {
                $scope.messages.noObjects = "Объектов нет.";
                var tempArr = response.data;
//console.log(tempArr);                    
                tempArr.forEach(function (element) {
                    element.imgsrc = 'images/object-mode-' + element.currentSettingMode + '.png';
//                        $scope.cont_zpoint_setting_mode_check
                    if (element.currentSettingMode === $scope.cont_zpoint_setting_mode_check[0].keyname) {
                        element.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[0].caption;
                        element.nextSettingMode = $scope.cont_zpoint_setting_mode_check[1].keyname;
                        element.nextSettingModeTitle = $scope.cont_zpoint_setting_mode_check[1].caption;

                    } else if (element.currentSettingMode === $scope.cont_zpoint_setting_mode_check[1].keyname) {
                        element.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[1].caption;
                        element.nextSettingMode = $scope.cont_zpoint_setting_mode_check[0].keyname;
                        element.nextSettingModeTitle = $scope.cont_zpoint_setting_mode_check[0].caption;
                    }
                    if (angular.isDefined(element._activeContManagement) && (element._activeContManagement !== null)) {
                        element.contManagementId = element._activeContManagement.organizationId;
                    }
                });
                $scope.objects = response.data;
                //sort by name
                objectSvc.sortObjectsByFullName($scope.objects);

//                    $scope.objectsWithoutFilter = $scope.objects;
                tempArr =  $scope.objects.slice(0, $scope.objectCtrlSettings.objectsPerScroll);
                $scope.objectsOnPage = tempArr;
//                    makeObjectTable(tempArr, true);
                $scope.loading = false;
                //if we have the contObject id in cookies, then draw the Zpoint table for this object.
                if (angular.isDefined($cookies.contObject) && $cookies.contObject !== "null") {
                    var curObj = objectSvc.findObjectById(Number($cookies.contObject), $scope.objects);
                    if (curObj !== null) {
                        var curObjIndex = $scope.objects.indexOf(curObj);
                        if (curObjIndex > $scope.objectCtrlSettings.objectsOnPage) {
                            //вырезаем из массива объектов элементы с текущей позиции, на которой остановились в прошлый раз, по вычесленный конечный индекс
                            var additionalObjects =  $scope.objects.slice($scope.objectCtrlSettings.objectsOnPage, curObjIndex + 1);
                                //добавляем к выведимому на экран массиву новый блок элементов
                            Array.prototype.push.apply($scope.objectsOnPage, additionalObjects);
                            $scope.objectCtrlSettings.objectsOnPage = curObjIndex + 1;
                            //$scope.objectCtrlSettings.currentObjectSearchFlag = true;                                
                            $scope.objectCtrlSettings.tmpCurContObj = $cookies.contObject;
                            $timeout(function () {
                                var curObjElem = document.getElementById("obj" + $scope.objectCtrlSettings.tmpCurContObj);
                                if (!mainSvc.checkUndefinedNull(curObjElem)) {
                                    curObjElem.scrollIntoView();
                                }
                                $scope.objectCtrlSettings.tmpCurContObj = null;
                            }, 50);
                        }
                        $scope.toggleShowGroupDetails(Number($cookies.contObject));
                    }
                    $cookies.contObject = null;
                }
                $rootScope.$broadcast('objectSvc:loaded');
            };

            var getObjectsData = function () {
                objectSvc.getPromise().then(successGetObjectsCallback);
            };

            $scope.refreshObjectsData = function () {
                $rootScope.$broadcast('objectSvc:requestReloadData', {"contGroupId": $scope.data.currentGroupId});
                $scope.loading = true;
                getObjectsData();
            };

            $scope.objectsDataFilteredByGroup = function (group) {
                $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;
                if (mainSvc.checkUndefinedNull(group)) {
                    $scope.messages.groupMenuHeader = "Полный список объектов";
                    $scope.data.currentGroupId = null;
                } else {
                    $scope.messages.groupMenuHeader = group.contGroupName;
                    $scope.data.currentGroupId = group.id;
                }

                $scope.refreshObjectsData();
            };

            $scope.viewFullObjectList = function () {
                $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;
                $scope.objectCtrlSettings.isFullObjectView = true;
                $scope.messages.treeMenuHeader = 'Полный список объектов';
                getObjectsData();
            };

//            function makeObjectTable(objectArray, isNewFlag) {
//
//                var objTable = document.getElementById('objectTable').getElementsByTagName('tbody')[0];
//        //        var temptableHTML = "";
//                var tableHTML = "";
//                if (!isNewFlag) {
//                    tableHTML = objTable.innerHTML;
//                }
//
//                objectArray.forEach(function (element, index) {
//                    var globalElementIndex = $scope.objectCtrlSettings.objectBottomOnPage - objectArray.length + index;
//                    var trClass = globalElementIndex % 2 > 0 ? "" : "nmc-tr-odd"; //Подкрашиваем разным цветом четные / нечетные строки
//                    tableHTML += "<tr class=\"" + trClass + "\" id=\"obj" + element.id + "\"><td class=\"nmc-td-for-buttons\"> <i title=\"Показать/Скрыть точки учета\" id=\"btnDetail" + element.id + "\" class=\"btn btn-xs noMargin glyphicon glyphicon-chevron-right nmc-button-in-table\" ng-click=\"toggleShowGroupDetails(" + element.id + ")\"></i>";
//                    tableHTML += "<i title=\"Редактировать свойства объекта\" ng-show=\"!bList\" class=\"btn btn-xs glyphicon glyphicon-edit nmc-button-in-table\" ng-click=\"selectedObject(" + element.id + ")\" data-target=\"#showObjOptionModal\" data-toggle=\"modal\"></i>";
//                    tableHTML += "</td>";
//                    tableHTML += "<td ng-click=\"toggleShowGroupDetails(" + element.id + ")\">" + element.fullName;
//                    if ($scope.isSystemuser()) {
//                        tableHTML += " <span>(id = " + element.id + ")</span>";
//                    }
//                    tableHTML += "</td></tr>";
//                    tableHTML += "<tr id=\"trObjZp" + element.id + "\">";
//                    tableHTML += "</tr>";
//                });
////console.log(objTable); 
//                if (angular.isDefined(objTable.innerHTML)) {
////console.log("angular.isDefined(objTable.innerHTML) =  true");                        
//                    objTable.innerHTML = tableHTML;
//                }
////console.log(objTable);                    
//                $compile(objTable)($scope);
//
//            }

//                $scope.objects = objectSvc.getObjects();
            $scope.loading = objectSvc.getLoadingStatus();//loading;
            $scope.treeLoading = true;
            $scope.columns = [
                {"name": "fullName", "header" : "Название", "class": "col-xs-10 col-md-10"}
            ];//angular.fromJson($attrs.columns);
            $scope.captions = {"loading": "loading...", "totalElements": "totalElements"};//angular.fromJson($attrs.captions);
            $scope.extraProps = {"idColumnName": "id", "defaultOrderBy" : "fullName", "nameColumnName": "fullName"};//angular.fromJson($attrs.exprops);
            $scope.addMode = false;
            $scope.orderBy = { field: $scope.extraProps.defaultOrderBy, asc: true };

            $scope.filter = '';
            $scope.filterType = '';
            //Признак того, что объекты выводятся в окне "Отчеты"
//                $scope.bGroupByObject = angular.fromJson($attrs.bgroup) || false;
//                $scope.bObject = angular.fromJson($attrs.bobject) || false; //Признак, что страница отображает объекты
//                $scope.bList = angular.fromJson($attrs.blist); //|| true; //Признак того, что объекты выводятся только для просмотра        
            //zpoint column names
            $scope.oldColumns = [
                {"name": "zpointName", "header" : "Наименование точки учета", "class": "col-xs-3 col-md-3"},
                {"name": "zpointModel", "header" : "Модель прибора учета", "class": "col-xs-2 col-md-2"},
                {"name": "zpointNumber", "header" : "Серийный номер прибора учета", "class": "col-xs-2 col-md-2"},
                {"name": "zpointRefRange", "header" : "Эталонный интервал", "class": "col-xs-2 col-md-2 nmc-width-12-per-cent"},
                {"name": "zpointLastDataDate", "header" : "Последние данные", "class": "col-xs-2 col-md-2"},
                {"name": "zpointTimeOffsetString", "header" : "Расхождение времени", "class": "col-xs-1 col-md-1 nmc-width-10-per-cent"}
            ];//angular.fromJson($attrs.zpointcolumns);
//                {"name":"zpointRefRange", "header" : "Эталонный интервал", "class":"col-xs-2 col-md-2"},
            // Эталонный интервал
            $scope.refRange = {};
            $scope.urlRefRange = '../api/subscr/contObjects/';
            // Промежуточные переменные для начала и конца интервала
//                $scope.beginDate;
//                $scope.endDate;

            //Режимы функционирования (лето/зима)
            $scope.cont_zpoint_setting_mode_check = [
                {"keyname" : "summer", "caption" : "Летний режим"},
                {"keyname" : "winter", "caption" : "Зимний режим"}
            ];


            $scope.toggleAddMode = function () {
                $scope.addMode = !$scope.addMode;
                $scope.object = {};
                if ($scope.addMode) {
                    if ($scope.newIdProperty && $scope.newIdValue) {
                        $scope.object[$scope.newIdProperty] =  $scope.newIdValue;
                    }
                }
            };

            $scope.toggleEditMode = function (object) {
                object.editMode = !object.editMode;
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

                trHTML += "<td class=\"nmc-td-for-buttons-in-object-page\" ng-hide=\"!objectCtrlSettings.extendedInterfaceFlag\"></td><td></td><td style=\"padding-top: 2px !important;\"><table id=\"zpointTable" + object.id + "\" class=\"crud-grid table table-lighter table-bordered table-condensed table-hover nmc-child-object-table\">";
                trHTML += "<thead><tr class=\"nmc-child-table-header\">";
                trHTML += "<th class=\"nmc-td-for-button\"></th>";
                $scope.oldColumns.forEach(function (column) {
                    trHTML += "<th class=\"" + column.class + "\">";
                    trHTML += (column.header || column.name);
                    trHTML += "</th>";
                });
                trHTML += "</tr></thead>";

                object.zpoints.forEach(function (zpoint) {
                    trHTML += "<tr id=\"trZpoint" + zpoint.id + "\" ng-dblclick=\"getIndicators(" + object.id + "," + zpoint.id + ")\">";
                    trHTML += "<td class=\"nmc-td-for-button\">" +

                        "<div class=\"btn-group\">" +
                        "<i title=\"Действия над точкой учета\" type=\"button\" class=\"btn btn-xs glyphicon glyphicon-menu-hamburger nmc-button-in-table dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\" style=\"font-size: .9em;\"></i>" +
                        "<ul class=\"dropdown-menu\">" +
                                "<li><a ng-click=\"getZpointSettings(" + object.id + "," + zpoint.id + ")\"" +
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
                                "<li><a ng-click=\"openDeviceProperties(" + object.id + "," + zpoint.id + ")\"" +
                                    "title=\"Свойства прибора учета\">" +
                                    "Свойства прибора учета" +
                                "</a></li>" +
                                "<li><a ng-click=\"openZpointMetadata(" + object.id + "," + zpoint.id + ")\"" +
                                    "title=\"Метаданные прибора\">" +
                                    "Метаданные прибора" +
                                "</a></li>" +
                                "<li ng-if='isDirectDevice(" + object.id + "," + zpoint.id + ")'><a ng-click=\"openSchedule(" + object.id + "," + zpoint.id + ")\"" +
                                    "title=\"Расписание опроса\">" +
                                    "Расписание опроса" +
                                "</a></li>";
                    if (!mainSvc.checkUndefinedNull(zpoint.deviceObject.activeDataSource)) {
                        trHTML += "<li><a ng-click=\"openDatasource(" + object.id + "," + zpoint.id + ")\"" +
                                "title=\"Источник данных\">" +
                                "Источник данных" +
                            "</a></li>";
                    }
//                                "<i class=\"btn btn-xs glyphicon glyphicon-edit nmc-button-in-table\"" +
//                                    "ng-click=\"getZpointSettings(" + object.id + "," + zpoint.id + ")\"" +
//                                    "data-target=\"#showZpointOptionModal\"" +
//                                    "data-toggle=\"modal\"" +
//                                    "data-placement=\"bottom\"" +
//                                    "title=\"Свойства точки учёта\">" +
//                                "</i>"+
//                                "<i class=\"btn btn-xs nmc-button-in-table\"" +
//                                    "ng-click=\"getZpointSettingsExpl(" + object.id + "," + zpoint.id + ")\"" +
//                                    "data-target=\"#showZpointExplParameters\"" +
//                                    "data-toggle=\"modal\""+
//                                    "data-placement=\"bottom\""+
//                                    "title=\"Эксплуатационные параметры точки учёта\">"+
//                                        "<img height=12 width=12 src=\"vendor_components/glyphicons_free/glyphicons/png/glyphicons-140-adjust-alt.png\" />"+
//                                "</i>";
                    trHTML += "<li>";
                    if (zpoint.isImpulse === true || zpoint.isSpreader === true) {
                        trHTML += "<a href='#/objects/impulse-indicators/";
                    } else if (zpoint.zpointType === 'el') {
                        trHTML += "<a href='#/objects/indicator-electricity/";
                    } else {
                        trHTML += "<a href='#/objects/indicators/";
                    }
//                        url += "&deviceModel=" + $scope.currentZpoint.zpointModel;
//                    url += "&deviceSN=" + $scope.currentZpoint.zpointNumber;
                    trHTML += "?objectId=" + encodeURIComponent(object.id) + "&zpointId=" + encodeURIComponent(zpoint.id) + "&objectName=" + encodeURIComponent(object.fullName) + "&zpointName=" + encodeURIComponent(zpoint.zpointName) + "&deviceModel=" + encodeURIComponent(zpoint.zpointModel) + "&deviceSN=" + encodeURIComponent(zpoint.zpointNumber);
                    if (!mainSvc.checkUndefinedNull(zpoint.measureUnitCaption)) {
                        trHTML += "&mu=" + encodeURIComponent(zpoint.measureUnitCaption);
                    }
                    trHTML += "' target=\"_blank\" ng-mousedown=\"setIndicatorsParams(" + object.id + "," + zpoint.id + ")\">" +
//                            "<i class=\"btn btn-xs glyphicon glyphicon-list nmc-button-in-table\"" +
//                                    "ng-click=\"getIndicators("+object.id+","+zpoint.id+")\""+
//                                    "ng-mousedown=\"setIndicatorsParams(" + object.id + "," + zpoint.id + ")\"" + 
//                                    "title=\"Показания точки учёта\">" + 
//                                "</i>"+
                        "Показания точки учета" +
                        "</a>";
                    trHTML += "</li>";
                    trHTML += "</ul>";
                    trHTML += "</div>";
                    trHTML += "</td>";
                    $scope.oldColumns.forEach(function (column) {
                        switch (column.name) {
                        case "zpointName":
                            var imgPath = "";
                            switch (zpoint.zpointType) {
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
                                //imgPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-206-electricity.png";
                            //imgPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-543-lamp.png";
                                //imgPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-65-lightbulb.png";         
                                imgPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-242-flash.png";
                                break;
                            default:
                                imgPath = column.zpointType;
                                break;
                            }
                            trHTML += "<td>";
                            trHTML += "<img class='marginLeft5' height=12 width=12 src=\"" + imgPath + "\"> <span class='paddingLeft5'></span>";
                            trHTML += (zpoint[column.name] || "Не задано") + "<span ng-show=\"isSystemuser()\">(id = " + zpoint.id + ")</span></td>";
                            break;
                        case "zpointLastDataDate":
                            trHTML += "<td>{{" + zpoint[column.name] + " | date: 'dd.MM.yyyy HH:mm'}} <i ng-if='" + zpoint[column.name] + "' title=\"Показать детали по последним данным\"" +
                                "ng-attr-id=\"{{'zldd'+" + zpoint.id + "}}\"" +
                                "class=\"btn btn-xs glyphicon glyphicon-eject gly-rotate-180\">" +
                                "</i></td>";
                            break;
                        case "zpointRefRange":
                            trHTML += "<td id=\"zpointRefRange" + zpoint.id + "\"></td>";
                            break;
                        case "zpointTimeOffsetString":
                            var title = "";
//console.log(zpoint[column.name]);      
                            if (!mainSvc.checkUndefinedNull(zpoint[column.name])) {
                                if (zpoint[column.name].indexOf('+') === 0) {
                                    title = "Часы прибора спешат";
                                }
                                if (zpoint[column.name].indexOf('-') === 0) {
                                    title = "Часы прибора отстают";
                                }
                            }
                            trHTML += "<td title='" + title + "'>";
                            if (!mainSvc.checkUndefinedNull(zpoint[column.name])) {
                                trHTML += zpoint[column.name];
                            }
                            trHTML += "</td>";
                            break;
                        default:
                            trHTML += "<td>";
                            if (!mainSvc.checkUndefinedNull(zpoint[column.name])) {
                                trHTML += zpoint[column.name];
                            }
                            trHTML += "</td>";
                            break;
                        }
                    });
                    trHTML += "</tr>";
                });
                trHTML += "</table></td>";
                trObjZp.innerHTML = trHTML;

                $compile(trObjZp)($scope);
            }

            var successCallbackOnZpointUpdate = function (e) {
                notificationFactory.success();
                $('#showZpointOptionModal').modal('hide');
                var curIndex = -1;
                $scope.currentObject.zpoints.some(function (elem, index) {
                    if (elem.id === $scope.zpointSettings.id) {
                        curIndex = index;
                        return true;
                    }
                });

                //update view name for zpoint
                if (curIndex > -1) {
                    var repaintZpointTableFlag = false;
                    if (($scope.currentObject.zpoints[curIndex].zpointName !== $scope.zpointSettings.customServiceName)) {
                        repaintZpointTableFlag = true;
                    }
                    var objectIndex = -1;
                    $scope.objects.some(function (elem, ind) {
                        if ($scope.currentObject.id === elem.id) {
                            objectIndex = ind;
                        }
                    });
                    if (objectIndex > -1) {
                        //update zpoint data in arrays
                        $scope.objects[objectIndex].zpoints[curIndex].customServiceName = $scope.zpointSettings.customServiceName;
                        $scope.objectsOnPage[objectIndex].zpoints[curIndex].zpointName = $scope.zpointSettings.customServiceName;
                        $scope.objects[objectIndex].zpoints[curIndex].isManualLoading = $scope.zpointSettings.isManualLoading;
                        $scope.objectsOnPage[objectIndex].zpoints[curIndex].isManualLoading = $scope.zpointSettings.isManualLoading;
                    }
                    //remake zpoint table
                    if (repaintZpointTableFlag) {
                        makeZpointTable($scope.objectsOnPage[objectIndex]);
                    }
//                        };
                }
                $scope.zpointSettings = {};

            };

            var successCallbackOnSetMode = function (e) {
                notificationFactory.success();
                $scope.objectCtrlSettings.allSelected = false;
                $scope.objects.forEach(function (el) {
                    if (el.selected === true) {
                        el.currentSettingMode = $scope.settedMode;
                        el.imgsrc = 'images/object-mode-' + el.currentSettingMode + '.png';
                        if (el.currentSettingMode === $scope.cont_zpoint_setting_mode_check[0].keyname) {
                            el.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[0].caption;
                            el.nextSettingMode = $scope.cont_zpoint_setting_mode_check[1].keyname;
                            el.nextSettingModeTitle = $scope.cont_zpoint_setting_mode_check[1].caption;

                        } else if (el.currentSettingMode === $scope.cont_zpoint_setting_mode_check[1].keyname) {
                            el.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[1].caption;
                            el.nextSettingMode = $scope.cont_zpoint_setting_mode_check[0].keyname;
                            el.nextSettingModeTitle = $scope.cont_zpoint_setting_mode_check[0].caption;
                        }
                    }
                    el.selected = false;
                });
                $scope.objectsOnPage.forEach(function (el) {
                    if (el.selected === true) {
                        el.currentSettingMode = $scope.settedMode;
                        el.imgsrc = 'images/object-mode-' + el.currentSettingMode + '.png';
                        if (el.currentSettingMode === $scope.cont_zpoint_setting_mode_check[0].keyname) {
                            el.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[0].caption;
                            el.nextSettingMode = $scope.cont_zpoint_setting_mode_check[1].keyname;
                            el.nextSettingModeTitle = $scope.cont_zpoint_setting_mode_check[1].caption;

                        } else if (el.currentSettingMode === $scope.cont_zpoint_setting_mode_check[1].keyname) {
                            el.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[1].caption;
                            el.nextSettingMode = $scope.cont_zpoint_setting_mode_check[0].keyname;
                            el.nextSettingModeTitle = $scope.cont_zpoint_setting_mode_check[0].caption;
                        }
                    }
                    el.selected = false;
                });
            };

            function successCallbackOnSetModeOne(resp) {
                var objectId = resp.data[0];
                notificationFactory.success();
                $scope.objects.forEach(function (el) {
                    if (el.id === objectId) {
                        el.currentSettingMode = $scope.settedMode;
                        el.imgsrc = 'images/object-mode-' + el.currentSettingMode + '.png';
                        if (el.currentSettingMode === $scope.cont_zpoint_setting_mode_check[0].keyname) {
                            el.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[0].caption;
                            el.nextSettingMode = $scope.cont_zpoint_setting_mode_check[1].keyname;
                            el.nextSettingModeTitle = $scope.cont_zpoint_setting_mode_check[1].caption;

                        } else if (el.currentSettingMode === $scope.cont_zpoint_setting_mode_check[1].keyname) {
                            el.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[1].caption;
                            el.nextSettingMode = $scope.cont_zpoint_setting_mode_check[0].keyname;
                            el.nextSettingModeTitle = $scope.cont_zpoint_setting_mode_check[0].caption;
                        }
                    }
                });
                $scope.objectsOnPage.forEach(function (el) {
                    if (el.id === objectId) {
                        el.currentSettingMode = $scope.settedMode;
                        el.imgsrc = 'images/object-mode-' + el.currentSettingMode + '.png';
                        if (el.currentSettingMode === $scope.cont_zpoint_setting_mode_check[0].keyname) {
                            el.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[0].caption;
                            el.nextSettingMode = $scope.cont_zpoint_setting_mode_check[1].keyname;
                            el.nextSettingModeTitle = $scope.cont_zpoint_setting_mode_check[1].caption;

                        } else if (el.currentSettingMode === $scope.cont_zpoint_setting_mode_check[1].keyname) {
                            el.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[1].caption;
                            el.nextSettingMode = $scope.cont_zpoint_setting_mode_check[0].keyname;
                            el.nextSettingModeTitle = $scope.cont_zpoint_setting_mode_check[0].caption;
                        }
                    }
                });
            }

            var successCallback = function (e, cb) {
                notificationFactory.success();
                $('#deleteObjectModal').modal('hide');
                $('#showObjOptionModal').modal('hide');
                var elIndex = -1;
                if ((angular.isDefined($scope.currentObject)) && ($scope.currentObject !== {})) {
                    $scope.objects.some(function (element, index) {
                        if (element.id === $scope.currentObject.id) {
                            elIndex = index;
                            return true;
                        }
                    });
                    if (elIndex !== -1) {
                        if ($scope.currentObject.currentSettingMode === $scope.cont_zpoint_setting_mode_check[0].keyname) {
                            $scope.currentObject.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[0].caption;
                            $scope.currentObject.nextSettingMode = $scope.cont_zpoint_setting_mode_check[1].keyname;
                            $scope.currentObject.nextSettingModeTitle = $scope.cont_zpoint_setting_mode_check[1].caption;

                        } else if ($scope.currentObject.currentSettingMode === $scope.cont_zpoint_setting_mode_check[1].keyname) {
                            $scope.currentObject.currentSettingModeTitle = $scope.cont_zpoint_setting_mode_check[1].caption;
                            $scope.currentObject.nextSettingMode = $scope.cont_zpoint_setting_mode_check[0].keyname;
                            $scope.currentObject.nextSettingModeTitle = $scope.cont_zpoint_setting_mode_check[0].caption;
                        }
                        $scope.objects[elIndex] = $scope.currentObject;
                        $scope.objects[elIndex].imgsrc = 'images/object-mode-' + $scope.currentObject.currentSettingMode + '.png';
                        $scope.objectsOnPage[elIndex] = $scope.currentObject;
                        $scope.objectsOnPage[elIndex].imgsrc = 'images/object-mode-' + $scope.currentObject.currentSettingMode + '.png';
                    }
                    $scope.currentObject = {};
                }
            };

            var successCallbackUpdateObject = function (e) {
                $rootScope.$broadcast('objectSvc:requestReloadData');
                $scope.currentObject._activeContManagement = e._activeContManagement;
                $scope.currentObject.isAddressAuto = e.isAddressAuto;
                $scope.currentObject.isValidFiasUUID = e.isValidFiasUUID;
                $scope.currentObject.isValidGeoPos = e.isValidGeoPos;
                successCallback(e, null);
            };

            var successPostCallback = function (e) {
                successCallback(e, function () {
                    $scope.toggleAddMode();
                });
            };

            var errorCallback = function (e) {
                $scope.treeLoading = false;
                $scope.objectCtrlSettings.isPassportsLoading = false;
                $scope.objectCtrlSettings.isDocumentSaving = false;
                var errorObj = mainSvc.errorCallbackHandler(e);
                notificationFactory.errorInfo(errorObj.caption, errorObj.description);
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
                var params = { id: object[$scope.extraProps.idColumnName]};
                if (angular.isDefined(object.contManagementId) && (object.contManagementId !== null)) {
                    var cmOrganizationId = object.contManagementId;
                    params = {
                        id: object[$scope.extraProps.idColumnName],
                        cmOrganizationId: cmOrganizationId
                    };
                }
                object._daDataSraw = null;
                if (!mainSvc.checkUndefinedNull($scope.currentSug)) {
                    object._daDataSraw = JSON.stringify($scope.currentSug);
                }
                crudGridDataFactory($scope.crudTableName).update(params, object, successCallbackUpdateObject, errorCallback);
            };

            $scope.setOrderBy = function (field) {
                var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
                $scope.orderBy = { field: field, asc: asc };
            };

            $scope.selectedObjectBy = function (item) {
                var curObject = angular.copy(item);
                $scope.currentObject = curObject;
                objectSvc.setCurrentObject($scope.currentObject);

                //get default indicator pref for this object
                getDefaultObjectIndicatorMode(curObject.id);
                
                //load object passports
                loadContObjectPassports(curObject.id);
            };

            $scope.selectedObject = function (objId) {
                $scope.currentObject = angular.copy(objectSvc.findObjectById(objId, $scope.objects));
                if (!mainSvc.checkUndefinedNull($scope.currentObject.buildingType)) {
//                            $scope.changeBuildingType($scope.currentObject.buildingType);
//                    performBuildingCategoryList($scope.currentObject.buildingType);
//                    performBuildingCategoryListForUiSelect($scope.currentObject.buildingType);
                    $scope.data.preparedBuildingCategoryListForUiSelect = objectSvc.performBuildingCategoryListForUiSelect($scope.currentObject.buildingType, $scope.data.buildingCategories);
                    setBuildingCategory();
                }
 //console.log($scope.currentObject);                    
            };

            function testCmOrganizationAtList() {
                //find cmOrganization
                var cmOrgId = $scope.currentObject.contManagementId;
                var cmOrgFindFlag = false;
                $scope.data.cmOrganizations.some(function (org) {
                    if (org.id === cmOrgId) {
                        cmOrgFindFlag = true;
                        return true;
                    }
                });
                //if cm not found
                if (cmOrgFindFlag === false) {
                    objectSvc.getCmOrganizationsWithId(cmOrgId).then(function (resp) {
                        $scope.data.cmOrganizations = resp.data;
                        mainSvc.sortOrganizationsByName($scope.data.cmOrganizations);
                    }, function (e) {console.log(e); });
                }
//console.log(cmOrgId);                    
//console.log(cmOrgFindFlag);                    
//console.log($scope.data.cmOrganizations);                     
            }

            $scope.selectedObjectEx = function (objId) {
                $scope.selectedObject(objId);
                if (!angular.isArray($scope.data.cmOrganizations)) {
                    return;
                }
//                    return;
                testCmOrganizationAtList();
                checkGeo();
            };

            $scope.selectedZpoint = function (objId, zpointId) {
                $scope.selectedObject(objId);

                var curZpoint = null;
                $scope.currentObject.zpoints.some(function (element) {
                    if (element.id === zpointId) {
                        curZpoint = angular.copy(element);
                        return true;
                    }
                });
                $scope.currentZpoint = curZpoint;
//console.log($scope.currentZpoint);                    
            };

            function prepareTimeDetailLastDate(tdld) {
                if (mainSvc.checkUndefinedNull(tdld) || mainSvc.checkEmptyObject(tdld)) {
                    return false;
                }
                var tdldKeys = Object.keys(tdld);
                tdldKeys.forEach(function (key) {
                    var elDom = "#zldd" + key;
                    var text = "";//JSON.stringify(tdld[key]);
                    text += "<table class='table table-condensed noMargin'>";
                    text += "<tboby>";
                    tdld[key].forEach(function (el) {
                        text += "<tr><td>";
                        switch (el.timeDetailType) {
                        case "1h":
                            text += "Часовые показания";
                            break;
                        case "24h":
                            text += "Суточные показания";
                            break;
                        case "1mon":
                            text += "Ежемесячные показания";
                            break;
                        case "1h_abs":
                            text += "Часовые интеграторы";
                            break;
                        case "24h_abs":
                            text += "Суточные интеграторы";
                            break;
                        case "1mon_abs":
                            text += "Ежемесячные интеграторы";
                            break;
                        case "abs":
                            text += "Интеграторы";
                            break;
                        default:
                            text += el.timeDetailType;
                            break;
                        }
                        text += "</td>";
                        text += "<td> " + el.dataDateString + "</td></tr>";
                    });
                    text += "</tboby>";
                    text += "</table>";
                    mainSvc.setToolTip("Последние данные", text, elDom, elDom, 1, 300);
                });
//                    var setToolTip = function(title, text, elDom, targetDom)
            }

            function loadZpointLastDataDetails(objId) {
//                    "../api/subscr/contObjects/%d/contZPoints/timeDetailLastDate"
                var url = objectSvc.getObjectsUrl() + "/" + objId + "/contZPoints/timeDetailLastDate";
                $http.get(url).then(function (resp) {
                    prepareTimeDetailLastDate(resp.data);
                }, function (e) { console.log(e); });
            }

            function prepareTimeOffset(rawTimeOffset) {
                return mainSvc.prepareTimeOffset(rawTimeOffset);
            }



            $scope.toggleShowGroupDetails = function (objId) {//switch option: current goup details
                var curObject = objectSvc.findObjectById(objId, $scope.objects);//null;                  
                //if cur object = null => exit function
                if (curObject === null) {
                    return;
                }
                //else

                var zpTable = document.getElementById("zpointTable" + curObject.id);
//console.log(zpTable);                    
                if ((curObject.showGroupDetails === true) && (zpTable === null)) {
                    curObject.showGroupDetails = true;
                } else {
                    curObject.showGroupDetails = !curObject.showGroupDetails;
                }
                //if curObject.showGroupDetails = true => get zpoints data and make zpoint table
                if (curObject.showGroupDetails === true) {

                    var mode = "/vo";
                    objectSvc.getZpointsDataByObject(curObject, mode).then(function (response) {
                        
                        var tmp = [];
                        if (mode === "Ex") {
                            tmp = response.data.map(function (el) {
                                var result = {};
                                result = el.object;
                                result.lastDataDate = el.lastDataDate;
//                                    result.zpointOrder = "" + result.contServiceType.serviceOrder + result.customServiceName;
//console.log(el.lastDataDate);                                    
                                return result;
                            });
                        } else {
                            tmp = response.data;
                        }
                        var zPointsByObject = tmp,
                            zpoints = [],
                            i;
                        for (i = 0; i < zPointsByObject.length; i += 1) {
                            var zpoint = {};
//console.log(zPointsByObject[i]);
                            zpoint.id = zPointsByObject[i].id;
                            zpoint.zpointOrder = zPointsByObject[i].contServiceType.serviceOrder + zPointsByObject[i].customServiceName;
//                                zpoint.zpointOrder = zPointsByObject[i].zpointOrder;
                            zpoint.zpointType = zPointsByObject[i].contServiceType.keyname;
                            zpoint.zpointTypeCaption = zPointsByObject[i].contServiceType.caption;
                            zpoint.isManualLoading = zPointsByObject[i].isManualLoading;
                            zpoint.customServiceName = zPointsByObject[i].customServiceName;
//                                zpoint.zpointName = zPointsByObject[i].contServiceType.caption || zPointsByObject[i].customServiceName;
                            zpoint.zpointName = zPointsByObject[i].customServiceName;
                            if ((typeof zPointsByObject[i].rso != 'undefined') && (zPointsByObject[i].rso != null)) {
                                zpoint.zpointRSO = zPointsByObject[i].rso.organizationFullName || zPointsByObject[i].rso.organizationName;
                            } else {
                                zpoint.zpointRSO = "Не задано";
                            }
                            zpoint.checkoutTime = zPointsByObject[i].checkoutTime;
                            zpoint.checkoutDay = zPointsByObject[i].checkoutDay;
                            if ((typeof zPointsByObject[i].doublePipe == 'undefined')) {
                                zpoint.piped = false;

                            } else {
                                zpoint.piped = true;
                                zpoint.doublePipe = (zPointsByObject[i].doublePipe === null) ? false : zPointsByObject[i].doublePipe;
                                zpoint.singlePipe = !zpoint.doublePipe;
                            }
//console.log(zpoint);
                            if ((typeof zPointsByObject[i].deviceObjects != 'undefined') && (zPointsByObject[i].deviceObjects.length > 0)) {                       zpoint.deviceObject = zPointsByObject[i].deviceObjects[0];
                                if (zPointsByObject[i].deviceObjects[0].hasOwnProperty('deviceModel')) {
                                    zpoint.zpointModel = zPointsByObject[i].deviceObjects[0].deviceModel.modelName;
                                    zpoint.devCaption = zPointsByObject[i].deviceObjects[0].deviceModel.modelName || "";
                                    zpoint.isImpulse = zPointsByObject[i].deviceObjects[0].isImpulse;
                                    zpoint.isSpreader = zPointsByObject[i].deviceObjects[0].deviceModel.deviceType === objectSvc.HEAT_DISTRIBUTOR;
                                    if (zpoint.isSpreader === true) {
                                        zpoint.measureUnitCaption = "Гкал";
                                    }
                                    if (zpoint.isImpulse === true) {
                                        if (!mainSvc.checkUndefinedNull(measureUnits)) {
                                            measureUnits.all.some(function (mu) {
                                                if (mu.keyname === zPointsByObject[i].deviceObjects[0].impulseMu) {
                                                    zpoint.measureUnitCaption = mu.caption;
                                                }
                                            });

                                        }
                                    }
                                } else {
                                    zpoint.zpointModel = "Не задано";
                                }
                                zpoint.zpointNumber = zPointsByObject[i].deviceObjects[0].number;
                                zpoint.devCaption += zPointsByObject[i].deviceObjects[0].number ? ", №" + zPointsByObject[i].deviceObjects[0].number : "";
                                }
                            zpoint.zpointLastDataDate  = zPointsByObject[i].lastDataDate;
                            if (!mainSvc.checkUndefinedNull(zPointsByObject[i].deviceObjectTimeOffset)) {
                                zpoint.zpointTimeOffsetString = prepareTimeOffset(zPointsByObject[i].deviceObjectTimeOffset);
                            }
//                                if (!mainSvc.checkUndefinedNull(zpoint.zpointLastDataDate)){
//                                    loadZpointDetail
//                                }
                            // Получаем эталонный интервал для точки учета
                            getRefRangeByObjectAndZpoint(curObject, zpoint);
                            zpoints[i] = zpoint;
                        }
                        mainSvc.sortItemsBy(zpoints, "zpointOrder");
                        curObject.zpoints = zpoints;
                        makeZpointTable(curObject);
                        var btnDetail = document.getElementById("btnDetail" + curObject.id);
                        if (angular.isDefined(btnDetail) && (btnDetail != null)) {
                            btnDetail.classList.remove("glyphicon-chevron-right");
                            btnDetail.classList.add("glyphicon-chevron-down");
                        }

                        curObject.showGroupDetailsFlag = !curObject.showGroupDetailsFlag;

                        loadZpointLastDataDetails(objId);
                    });

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
                result = (tmpDate === null) ? "" : moment([tmpDate.getUTCFullYear(), tmpDate.getUTCMonth(), tmpDate.getUTCDate()]).format($scope.objectCtrlSettings.dateFormat);
                return result;//
            };

            //Функция для получения эталонного интервала для конкретной точки учета конкретного объекта
            function getRefRangeByObjectAndZpoint(object, zpoint) {
//                    var url = $scope.urlRefRange + object.id + '/zpoints/' + zpoint.id + '/referencePeriod'; 
//console.log(url);                    
                objectSvc.getRefRangeByObjectAndZpoint(object, zpoint)
                    .success(function (data) {
                        if (data[0] != null) {
    //                            var beginDate = new Date(data[0].periodBeginDate);
    //                            var endDate =  new Date(data[0].periodEndDate);
                            var beginDate = $scope.dateFormat(data[0].periodBeginDate);
                            var endDate =  $scope.dateFormat(data[0].periodEndDate);
    //console.log(data[0]);                                    
    //                            zpoint.zpointRefRange = "c "+beginDate.toLocaleDateString()+" по "+endDate.toLocaleDateString();
                            zpoint.zpointRefRange = "c " + beginDate + " по " + endDate;
                            zpoint.zpointRefRangeAuto = data[0].isAuto ? "auto" : "manual";
                        } else {
                            zpoint.zpointRefRange = "Не задан";
                            zpoint.zpointRefRangeAuto = "notSet";
                        }
                        viewRefRangeInTable(zpoint);
                    })
                    .error(function (e) {
                        console.log(e);
                    });
            }

            // ***************************************************************************************
            //                          **  Работа с отчетами 
            // ***************************************************************************************
//                //Получить категории
//                $scope.data.reportCategories = reportSvc.getCategories();
//                //получить доступные варианты отчетов
//                $scope.getReports = function(){
//                    reportSvc.loadReportsContextLaunch().then(successGetReports, errorCallback);
//                };
//                //Если вариантов отчетов больше $scope.objectCtrlSettings.reportCountList, то распределить варианты отчетов по категориям
//                var successGetReports = function(resp){
//                    $scope.data.reports = angular.copy(resp.data);
//                    if ($scope.data.reports.length > $scope.objectCtrlSettings.reportCountList){
//                        $scope.data.reportEntities = angular.copy($scope.data.reportCategories);
//                        $scope.data.reportEntities.forEach(function(elem){elem.reports = []});
//                        $scope.data.reports.forEach(function(rep){
//                            for (var categoryCounter = 0; categoryCounter < $scope.data.reportEntities.length; categoryCounter++){               
//                                if (rep.reportTemplate.reportType.reportCategory.localeCompare($scope.data.reportEntities[categoryCounter].name) == 0){                                    
//                                    $scope.data.reportEntities[categoryCounter].reports.push(rep);
//                                    break;
//                                };
//                            };
//                        });
//                    }else{
//                        $scope.data.reportEntities = $scope.data.reports;
//                    };                    
//                };
//                $scope.getReports();
//                
//                //check fields before save
//                $scope.checkRequiredFieldsOnSave = function(){
//                    $scope.currentObject.selectedObjects = $scope.selectedObjects;
//                    $scope.currentObject.currentParamSpecialList = $scope.currentParamSpecialList;
//                    var checkRes = reportSvc.checkPSRequiredFieldsOnSave($scope.currentReportType, $scope.currentObject, $scope.currentSign, "run");
//                    $scope.messageForUser = checkRes.message;
//                    return checkRes.flag;
//                };
//                
//                $scope.checkAndRunParamset = function(type, object, previewFlag){
//                    var flag = $scope.checkRequiredFieldsOnSave();
//                    if (flag === false){
//                        $('#messageForUserModal').modal();
//                    }else{
//                        var previewWin = null;
//                        //индикация загрузки страницы предпросмотра
//                        var htmlText = $scope.objectCtrlSettings.htmlLoading;
//                        var previewFile = new Blob([htmlText], {type : 'text/html'});//new Blob(["temp"], "temp");//null;
//                        if(previewFlag){
//                            //window.URL= window.URL || window.webkitURL;
//                            var url = window.URL.createObjectURL(previewFile);//формируем url на сформированный файл
//                            previewWin = window.open(url, 'PreviewWin');//открываем сформированный файл в новой вкладке браузера
//                        };
//                        $scope.createReportWithParams(type, object, previewFlag, previewWin);
//                    };
//                };
//                
//                //Формируем отчет с заданными параметрами
//                $scope.createReportWithParams = function(type // тип отчета
//                                                        , paramset //вариант отчета
//                                                        , previewFlag //флаг - формировать отчет или сделать предпросмотр
//                                                        , previewWin //ссылка на превью окно
//                                                        ){
//                    var tmpParamset = angular.copy(paramset);//делаем копию варианта отчета
//                    //формируем массив ИД объектов, для которых формируется отчет.          
//                    var objectIds = $scope.selectedObjects.map(function(element){          
//                        var result = element.id;
//                        return result;
//                    });      
//                     //set the list of the special params - устанавливаем специальные параметры отчета
//                    tmpParamset.paramSpecialList = $scope.currentParamSpecialList;
//                    //Если вариант отчета создается за период, задаем начало и конец периода
//                    if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){
//                        var startDate = mainSvc.strDateToUTC($scope.psStartDateFormatted, $scope.ctrlSettings.dateFormat);
//                        tmpParamset.paramsetStartDate = (startDate!=null)?(new Date(startDate)) : null;
//                        var endDate = mainSvc.strDateToUTC($scope.psEndDateFormatted, $scope.ctrlSettings.dateFormat);
//                        tmpParamset.paramsetEndDate = (endDate!=null)?(new Date(endDate)) : null;
//                    }else{
//                        tmpParamset.paramsetStartDate = null;
//                        tmpParamset.paramsetEndDate = null;
//                    };
//
//            //console.log(paramset);        
//                    var fileExt = "";
//                    if (previewFlag){//проверяем флаг предпросмотра,
//                        //если флаг установлен, то
//                        tmpParamset.outputFileType = "HTML";//ставим формат выходного файла - HTML
//                        tmpParamset.outputFileZipped = false;//ставим флаг -не архивировать полученный отчет
//                        fileExt = "html";
//                    }else{
//                        fileExt = tmpParamset.outputFileZipped ? "zip" : tmpParamset.outputFileType.toLowerCase();
//                    }
//                    var url = "../api/reportService" + type.suffix + "/" + tmpParamset.id + "/download";  //формируем url адрес запроса
//                    var responseType = "arraybuffer";//указываем тип ответа от сервера
//                    //делаем запрос на сервер
//                    var clearContObjectIds = false; //the clear selected paramset objects flag           
//                        if (mainSvc.checkUndefinedNull(objectIds) || objectIds.length == 0){                
//                            clearContObjectIds = true;
//                    };
//                    $http({
//                        url: url, 
//                        method: "PUT",
//                        params: { contObjectIds: objectIds, clearContObjectIds: clearContObjectIds},
//                        data: tmpParamset,
//                        responseType: responseType
//                    })
//                    .then(function(response) {
//                       //обрабатываем полученный результат запроса
//                        var fileName = response.headers()['content-disposition']; //читаем кусок заголовка, в котором пришло название файла
//                        fileName = fileName.substr(fileName.indexOf('=') + 2, fileName.length - fileName.indexOf('=') - 3);//вытаскиваем непосредственно название файла.
//                        var file = new Blob([response.data], { type: response.headers()['content-type']/* тип файла тоже приходит в заголовке ответа от сервера*/ });//формируем файл из полученного массива байт        
//                        if (previewFlag){              
//                            //если нажат предпросмотр, то
//                            var url = window.URL.createObjectURL(file);//формируем url на сформированный файл
//                            window.open(url, 'PreviewWin');//открываем сформированный файл в новой вкладке браузера
//                        }else{  
//                            //create file extension
//                            if ((file.type.indexOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") > -1)){
//                                fileName += ".xlsx";
//                            }else 
//                            if (file.type.indexOf('application/zip') > -1){
//                                fileName += ".zip";
//                            } else
//                            if (file.type.indexOf('application/pdf') > -1){
//                                fileName += ".pdf";
//                            } else
//                            if (file.type.indexOf('text/html') > -1){
//                                fileName += ".html";
//                            };                 
//                            saveAs(file, fileName);//если нужен отчет, то сохраняем файл на диск клиента
//                        };
//                    }, errorCallback)
//                };
//                
//                $scope.reportCreate = function(paramset){
//                    if (!mainSvc.checkUndefinedNull(paramset.reports)){//If parametr "paramset" is not paramset, but it is category
//                        return "Entity is category";//exit function
//                    };
//                    //run report
//                    $scope.checkAndRunParamset(paramset.reportTemplate.reportType, paramset, false);                          
//                };
            // *******************************************************************************************
            //          конец работе с отчетами
            // *******************************************************************************************

            // Прорисовываем эталонный интервал в таблице
            function viewRefRangeInTable(zpoint) {
                //Получаем столбец с эталонным интервалом для заданной точки учета
                var element = document.getElementById("zpointRefRange" + zpoint.id);
                if (angular.isUndefined(element) || element == null) {
                    return false;
                }
                //Записываем эталонный интервал в таблицу
//console.log(zpoint);                    
                switch (zpoint.zpointRefRangeAuto) {
                case "auto":
                    element.innerHTML = '<div class="progress progress-striped noMargin">' +
                                    '<div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"><strong>' + zpoint.zpointRefRange +
                                    '</strong></div>' +
                                '</div>';
                    break;
                case "manual":
                    element.innerHTML = '<div class="progress progress-striped noMargin">' +
                                    '<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"><strong>' + zpoint.zpointRefRange +
                                    '</strong></div>' +
                                '</div>';
                    break;
                default:
                    element.innerHTML = zpoint.zpointRefRange;
                }
                $compile(element)($scope);

            }

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

//            $scope.showDetails = function (obj) {
//                if ($scope.bdirectories) {
//                    $scope.currentObject = obj;
//                    $('#showDirectoryStructModal').modal();
//                }
//            };

            // Показания точек учета
            $scope.getIndicators = function (objectId, zpointId) {
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

//                    window.location.assign("#/objects/indicators/?objectId="+objectId+"&zpointId="+zpointId+"&objectName="+$scope.currentObject.fullName+"&zpointName="+$scope.currentZpoint.zpointName);
                var url = "#/objects";

                if ($scope.currentZpoint.isImpulse === true || $scope.currentZpoint.isSpreader === true) {
                    url += "/impulse-indicators";
                } else if ($scope.currentZpoint.zpointType === 'el') {
                    url += "/indicator-electricity";
                } else {
                    url += "/indicators";
                }
                url += "/?objectId=" + encodeURIComponent(objectId) + "&zpointId=" + encodeURIComponent(zpointId) + "&objectName=" + encodeURIComponent($scope.currentObject.fullName) + "&zpointName=" + encodeURIComponent($scope.currentZpoint.zpointName);
                //add info about device
//console.log($scope.currentZpoint);                    

                url += "&deviceModel=" + encodeURIComponent($scope.currentZpoint.zpointModel);
                url += "&deviceSN=" + encodeURIComponent($scope.currentZpoint.zpointNumber);

                if (!mainSvc.checkUndefinedNull($scope.currentZpoint.measureUnitCaption)) {
                    url += "&mu=" + encodeURIComponent($scope.currentZpoint.measureUnitCaption);
                }

                window.open(url, '_blank');
            };

            $scope.setIndicatorsParams = function (objectId, zpointId) {
//console.log("setIndicatorsParams");                    
                $scope.selectedZpoint(objectId, zpointId);
                $cookies.contZPoint = $scope.currentZpoint.id;
                $cookies.contObject = $scope.currentObject.id;
                $cookies.contZPointName = $scope.currentZpoint.zpointName;
                $cookies.contObjectName = $scope.currentObject.fullName;
                $cookies.deviceModel = $scope.currentZpoint.zpointModel;
                $cookies.deviceSN = $scope.currentZpoint.zpointNumber;

                if (angular.isUndefined($cookies.timeDetailType) || ($cookies.timeDetailType == "undefined") || ($cookies.timeDetailType == "null")) {
                    $cookies.timeDetailType = "24h";
                }

                $cookies.isManualLoading = ($scope.currentZpoint.isManualLoading === null ? false : $scope.currentZpoint.isManualLoading) || false;
//console.log($scope.currentZpoint);                    
                $rootScope.reportStart = moment().subtract(6, 'days').startOf('day').format('YYYY-MM-DD');
                $rootScope.reportEnd = moment().endOf('day').format('YYYY-MM-DD');

//                    window.location.assign("#/objects/indicators/");
            };

            //Свойства точки учета
            $scope.zpointSettings = {};
            $scope.getZpointSettings = function (objId, zpointId) {
//console.log("getZpointSettings");                    
                $scope.selectedZpoint(objId, zpointId);
//console.log($scope.currentZpoint);                    
                var object = $scope.currentZpoint;
                var zps = {};
                zps.id = object.id;
                zps.isManualLoading = object.isManualLoading;
                zps.customServiceName = object.customServiceName;
                zps.zpointTypeCaption = object.zpointTypeCaption;
                zps.zpointName = object.zpointName;
                switch (object.zpointType) {
                case "heat":
                    zps.zpointType = "ТС";
                    break;
                case "hw":
                    zps.zpointType = "ГВС";
                    break;
                case "cw":
                    zps.zpointType = "ХВ";
                    break;
                default:
                    zps.zpointType = object.zpointType;
                }
                zps.piped = object.piped;
                zps.singlePipe = object.singlePipe;
                zps.doublePipe = object.doublePipe;
//console.log(zps);
                zps.zpointModel = object.zpointModel;
                zps.devCaption = object.devCaption;
                zps.zpointRSO = object.zpointRSO;
                zps.checkoutTime = object.checkoutTime;
                zps.checkoutDay = object.checkoutDay;
                zps.winter = {};
                zps.summer = {};
                $scope.zpointSettings = zps;
                // Готовим редактор эталонного периода
                $scope.prepareRefRange();

            };

            $scope.getZpointSettingsExpl = function (objId, zpointId) {
                $scope.getZpointSettings(objId, zpointId);
                var winterSet = {};
                var summerSet = {};
                                    //http://localhost:8080/nmk-p/api/subscr/contObjects/18811505/zpoints/18811559/settingMode
                var table = objectSvc.getObjectsUrl() + "/" + $scope.currentObject.id + "/zpoints/" + $scope.zpointSettings.id + "/settingMode";
                crudGridDataFactory(table).query(function (data) {
                    var i;
                    for (i = 0; i < data.length; i += 1) {

                        if (data[i].settingMode == "winter") {
                            winterSet = data[i];
                        } else if (data[i].settingMode == "summer") {
                            summerSet = data[i];
                        }
                    }
                    $scope.zpointSettings.winter = winterSet;
                    $scope.zpointSettings.summer = summerSet;
                    // Готовим редактор эталонного периода
//                        $scope.prepareRefRange();
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
            };

            // Деактивация формы редактирования эталонного интервала
            $scope.editRefRangeOff = function () {
                $scope.toggleFlag = false; //переключаем видимость кнопок Изменить/сохранить
                document.getElementById('i_ref_range_save').style.display = 'none';
                document.getElementById('i_ref_range_add').style.display = 'block';
                document.getElementById('inp_ref_range_start').disabled = true;
                document.getElementById('inp_ref_range_end').disabled = true;
            };

            // Запрос с сервера данных об эталонном интервале
            var getRefRange = function (objectId, zpointId) {
                var url = $scope.urlRefRange + '/' + objectId + '/zpoints/' + zpointId + '/referencePeriod';
                $http.get(url)
                    .success(function (data) {
                        // Проверяем, задан ли интервал
                        if (data[0] != null) {
    //console.log(data);                            
                            $scope.refRange = data[0];
                            $scope.refRange.cont_zpoint_id = zpointId;
    //							$scope.beginDate = new Date($scope.refRange.periodBeginDate);
    //							$scope.endDate =  new Date($scope.refRange.periodEndDate);
                            $scope.beginDate = $scope.dateFormat($scope.refRange.periodBeginDate);
                            $scope.endDate =  $scope.dateFormat($scope.refRange.periodEndDate);
    //							console.log($scope.beginDate, document.getElementById('inp_ref_range_start').value);
                            // Проверяем, был ли интервал расчитан автоматически
                            if ($scope.refRange.isAuto == false) {
                                document.getElementById('spn_if_manual').style.display = 'block';
                                document.getElementById('spn_if_auto').style.display = 'none';
                            } else {
                                document.getElementById('spn_if_manual').style.display = 'none';
                                document.getElementById('spn_if_auto').style.display = 'block';
                            }
                        } else {
                            $scope.refRange = {};
                            $scope.refRange.cont_zpoint_id = zpointId;
                            $scope.beginDate = '';
                            $scope.endDate = '';
                            document.getElementById('spn_if_manual').style.display = 'none';
                            document.getElementById('spn_if_auto').style.display = 'none';
                        }
                    })
                    .error(function (e) {
                        notificationFactory.errorInfo(e.statusText, e.description);
                    });
            };

            // Отправка на сервер данных об эталонном интервале
            $scope.addRefRange = function () {
                var url = $scope.urlRefRange + '/' + $scope.currentObject.id + '/zpoints/' + $scope.zpointSettings.id + '/referencePeriod';
                $scope.refRange.id = '';
//                $scope.refRange.periodBeginDate = $scope.beginDate.getTime();
//                $scope.refRange.periodEndDate = $scope.endDate.getTime();
                //Приводим установленный период к UTC
                var startDate = (new Date(moment($scope.beginDate, $scope.objectCtrlSettings.dateFormat).format("YYYY-MM-DD"))); //reformat date string to ISO 8601                        
                var UTCstdt = Date.UTC(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
                $scope.refRange.periodBeginDate = (!isNaN(UTCstdt)) ? (new Date(UTCstdt)).getTime() : null;

                var endDate = (new Date(moment($scope.endDate, $scope.objectCtrlSettings.dateFormat).format("YYYY-MM-DD"))); //reformat date string to ISO 8601                        
                var UTCenddt = Date.UTC(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
                $scope.refRange.periodEndDate = (!isNaN(UTCenddt)) ? (new Date(UTCenddt)).getTime() : null;
                $http.post(url, $scope.refRange)
                    .success(function (data) {
                        $scope.editRefRangeOff();
                        $scope.refRange = data;
                        //прорисовываем эталонный интервал в таблице
                        var refRangeEl = document.getElementById("zpointRefRange" + $scope.currentZpoint.id);
    //                        $scope.beginDate = new Date($scope.refRange.periodBeginDate);
    //				        $scope.endDate =  new Date($scope.refRange.periodEndDate);                                 
    //                        
    //                        $scope.currentZpoint.zpointRefRange = "c "+$scope.beginDate.toLocaleDateString()+" по "+$scope.endDate.toLocaleDateString();
                        $scope.beginDate = $scope.dateFormat($scope.refRange.periodBeginDate);
                        $scope.endDate = $scope.dateFormat($scope.refRange.periodEndDate);

                        $scope.currentZpoint.zpointRefRange = "c " + $scope.beginDate + " по " + $scope.endDate;
                        $scope.currentZpoint.zpointRefRangeAuto = $scope.refRange.isAuto ? "auto" : "manual";

                        viewRefRangeInTable($scope.currentZpoint);
                    })
                    .error(function (e) {
                        notificationFactory.errorInfo(e.statusText, e.description);
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

            //Update the common zpoint setiing - for example, Name
            $scope.updateZpointCommonSettings = function () {
                var url = $scope.crudTableName + "/" + $scope.currentObject.id + "/zpoints/" + $scope.zpointSettings.id;
                $http({
                    url: url,
                    method: 'PUT',
                    data: $scope.zpointSettings
                })
                    .then(successCallbackOnZpointUpdate, errorCallback);
            };

            //Update the zpoint settings, which set the mode for Summer or Winter season
            $scope.updateZpointModeSettings = function () {
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
                    //
                    tempArr = [];
                    $scope.objectCtrlSettings.objectsOnPage = $scope.objectCtrlSettings.objectsPerScroll;
                    tempArr =  $scope.objects.slice(0, $scope.objectCtrlSettings.objectsPerScroll);
                    $scope.objectsOnPage = tempArr;
                } else {
                    tempArr = [];
                    $scope.objects.forEach(function (elem) {
                        if (elem.fullName.toUpperCase().indexOf(searchString.toUpperCase()) != -1) {
                            tempArr.push(elem);
                        }
                    });
                    $scope.objectsOnPage = tempArr;
                }
            };

            $scope.$on('$destroy', function () {
                window.onkeydown = undefined;
                if (passportRequestCanceller !== null) {
//console.log(passportRequestCanceller);
                    passportRequestCanceller.resolve();
                }
            });


            //keydown listener for ctrl+end
            window.onkeydown = function (e) {
                var elem = null;
                if (e.keyCode == 38) {
                    elem = document.getElementById("divWithSettingsObjectTable");
                    elem.scrollTop = elem.scrollTop - 20;
                    return;
                }
                if (e.keyCode == 40) {
                    elem = document.getElementById("divWithSettingsObjectTable");
                    elem.scrollTop = elem.scrollTop + 20;
                    return;
                }
                if (e.keyCode == 34) {
//                        $scope.addMoreObjects();
//                        $scope.$apply();
                    elem = document.getElementById("divWithSettingsObjectTable");
                    elem.scrollTop = elem.scrollTop + $scope.objectCtrlSettings.objectsPerScroll * 10;
                    return;
                }
                if (e.keyCode == 33) {
                    elem = document.getElementById("divWithSettingsObjectTable");
                    elem.scrollTop = elem.scrollTop - $scope.objectCtrlSettings.objectsPerScroll * 10;
                    return;
                }
                if (e.ctrlKey && e.keyCode == 36) {
                    elem = document.getElementById("divWithSettingsObjectTable");
                    elem.scrollTop = 0;
                    return;
                }
                if (e.ctrlKey && e.keyCode == 35) { /*&& ($scope.objectCtrlSettings.objectsOnPage < $scope.objects.length)*/
//                        $scope.loading = true;    
                    var tempArr = $scope.objects.slice($scope.objectCtrlSettings.objectsOnPage, $scope.objects.length);
                    Array.prototype.push.apply($scope.objectsOnPage, tempArr);
                    $scope.objectCtrlSettings.objectsOnPage += $scope.objects.length;
//                        $scope.objectCtrlSettings.isCtrlEnd = true;
                    $scope.$apply();
                    elem = document.getElementById("divWithSettingsObjectTable");
                    elem.scrollTop = elem.scrollHeight;
                }
            };

            //function set cursor to the bottom of the object table, when ctrl+end pressed
//                $scope.onTableLoad = function(){ 
//console.log("Run onTableLoad");                    
//                    if (($scope.objectCtrlSettings.isCtrlEnd === true)) {                    
//                        var pageHeight = (document.body.scrollHeight > document.body.offsetHeight) ? document.body.scrollHeight : document.body.offsetHeight;                      
//                        window.scrollTo(0, Math.round(pageHeight));
//                        $scope.objectCtrlSettings.isCtrlEnd = false;
//                        $scope.loading =  false;
//                    };
//                };

            //function add more objects for table on user screen
            $scope.addMoreObjects = function () {
                if (($scope.objects.length <= 0)) {
                    return;
                }

                //set end of object array - определяем конечный индекс объекта, который будет выведен при текущем скролинге
                var endIndex = $scope.objectCtrlSettings.objectsOnPage + $scope.objectCtrlSettings.objectsPerScroll;
                if (endIndex >= $scope.objects.length) {
                    endIndex = $scope.objects.length;
                }
                //вырезаем из массива объектов элементы с текущей позиции, на которой остановились в прошлый раз, по вычесленный конечный индекс
                var tempArr = $scope.objects.slice($scope.objectCtrlSettings.objectsOnPage, endIndex);
                    //добавляем к выведимому на экран массиву новый блок элементов
                Array.prototype.push.apply($scope.objectsOnPage, tempArr);
                if (endIndex >= ($scope.objects.length)) {
                    $scope.objectCtrlSettings.objectsOnPage = $scope.objects.length;
                } else {
                    $scope.objectCtrlSettings.objectsOnPage += $scope.objectCtrlSettings.objectsPerScroll;
                }
            };

            $scope.isSystemViewInfo = function () {
                return mainSvc.getViewSystemInfo();
            };

            // Проверка пользователя - системный/ не системный
            $scope.isSystemuser = function () {
                var result = false;
                $scope.userInfo = $rootScope.userInfo;
                if (angular.isDefined($scope.userInfo)) {
                    result = $scope.userInfo._system;
                }
                return result;
            };

            //toggle all objects - selected/unselected
            $scope.toggleObjects = function (flag) {
                $scope.objects.forEach(function (el) {
                    el.selected = flag;
                });
                $scope.objectsOnPage.forEach(function (el) {
                    el.selected = flag;
                });
            };

            $scope.setModeForObject = function (object) {
                var mode = object.nextSettingMode;
                $scope.settedMode = mode;
                //get the object ids array
                var contObjectIds = [object.id];

                //send data to server
                var url = objectSvc.getObjectsUrl() + "/settingModeType";
                $http({
                    url: url,
                    method: "PUT",
                    params: {currentSettingMode: mode },
                    data: contObjectIds
                })
                    .then(successCallbackOnSetModeOne, errorCallback);
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
                    params: {currentSettingMode: mode },
                    data: contObjectIds
                })
                    .then(successCallbackOnSetMode, errorCallback);
            };
// ******************************************************************                
//              Work with devices
// ******************************************************************                
                //get the list of the systems for meta data editor
            $scope.getVzletSystemList = function () {
                var tmpSystemList = objectSvc.getVzletSystemList();
                if (tmpSystemList.length === 0) {
                    objectSvc.getDeviceMetaDataVzletSystemList()
                        .then(
                            function (response) {
                                $scope.objectCtrlSettings.vzletSystemList = response.data;
                            },
                            function (e) {
                                notificationFactory.errorInfo(e.statusText, e.description);
                            }
                        );
                } else {
                    $scope.objectCtrlSettings.vzletSystemList = tmpSystemList;
                }
            };
            $scope.getVzletSystemList();
                //get devices
            $scope.getDevices = function (obj) {
                objectSvc.getDevicesByObject(obj).then(
                    function (response) {
                        //select only vzlet devices
                        var tmpArr = [];//response.data;
                        response.data.forEach(function (element) {
                            if (element.metaVzletExpected === true) {
                                tmpArr.push(element);
                            }
                        });
                        obj.devices = tmpArr;//response.data; 
                    },
                    errorCallback
                );
            };

                //get device meta data and show it
            $scope.getDeviceMetaDataVzlet = function (obj, device) {
                objectSvc.getDeviceMetaDataVzlet(obj, device).then(
                    function (response) {
                        device.metaData = response.data;
                        $scope.currentDevice =  device;
                        $('#metaDataEditorModal').modal();
                    },
                    function (error) {
                        notificationFactory.errorInfo(error.statusText, error.description);
                    }
                );
            };

            $scope.updateDeviceMetaData = function (device) {
//console.log(device);    
                var method = "";
                if (angular.isDefined(device.metaData.id) && (device.metaData.id !== null)) {
                    method = "PUT";
                } else {
                    method = "POST";
                }
                var url = "../api/subscr/contObjects/" + device.contObject.id + "/deviceObjects/" + device.id + "/metaVzlet";
                $http({
                    url: url,
                    method: method,
                    data: device.metaData
                })
//                    $http.put(url, device.metaData)
                    .then(
//                    objectSvc.putDeviceMetaData(device).then(
                        function (response) {
                            $scope.currentDevice =  {};
                            $('#metaDataEditorModal').modal('hide');
                        },
                        function (error) {
                            console.log(error);
                            notificationFactory.errorInfo(error.statusText, error.description);
                        }
                    );
            };

            $scope.selectedDevice = function (device) {
                $scope.data.currentDevice = angular.copy(device);
                var curDevice = $scope.data.currentDevice;
                if (angular.isDefined(curDevice.contObjectInfo) && (curDevice.contObjectInfo != null)) {
                    curDevice.contObjectId = curDevice.contObjectInfo.contObjectId;
                }
                if (angular.isDefined(curDevice.activeDataSource) && (curDevice.activeDataSource != null)) {
                    curDevice.subscrDataSourceId = Number(curDevice.activeDataSource.id);
                    curDevice.curDatasource = curDevice.activeDataSource.subscrDataSource;
                    curDevice.subscrDataSourceAddr = curDevice.activeDataSource.subscrDataSourceAddr;
                    curDevice.dataSourceTable1h = curDevice.activeDataSource.dataSourceTable1h;
                    curDevice.dataSourceTable24h = curDevice.activeDataSource.dataSourceTable24h;
                }
            };

            $scope.openDeviceProperties = function (objId, zpointId) {
//console.log("openDeviceProperties");                    
                $scope.selectedZpoint(objId, zpointId);
//console.log($scope.currentZpoint);                    
                if (mainSvc.checkUndefinedNull($scope.currentZpoint)) {
                    return false;
                }
                
                objectSvc.loadDeviceById(objId, $scope.currentZpoint.deviceObject.id)
                    .then(function (resp) {
                        if (mainSvc.checkUndefinedNull(resp) || mainSvc.checkUndefinedNull(resp.data)) {
                            console.log("loadDeviceById: Некорректный ответ от сервера.");
                            return false;
                        }
                        $scope.selectedDevice(resp.data);
        //console.log($scope.data.currentDevice);
                        if (!mainSvc.checkUndefinedNull($scope.data.currentDevice.activeDataSource)) {
                            var tmpDataSource = angular.copy($scope.data.currentDevice.activeDataSource.subscrDataSource);
                            $scope.data.dataSourcesForCurDevice = [tmpDataSource];
                        }
                        if (!mainSvc.checkUndefinedNull($scope.data.currentDevice.deviceModel)) {
                            $scope.data.deviceModelsForCurDevice = [$scope.data.currentDevice.deviceModel];
                        }
                        if (!mainSvc.checkUndefinedNull($scope.data.currentDevice.heatRadiatorType)) {
                            $scope.data.heaterTypesForCurDevice = [$scope.data.currentDevice.heatRadiatorType];
                        }
                        if (!mainSvc.checkUndefinedNull($scope.data.currentDevice.verificationDate)) {
                            $scope.data.currentDevice.verificationDateString = mainSvc.dateFormating($scope.data.currentDevice.verificationDate, $scope.objectCtrlSettings.dateFormat);
                        }
                        $('#showDeviceModal').modal();
                    }, errorCallback);
                
            };

            $scope.isDirectDevice = function (objId, zpointId) {
//console.log("isDirectDevice: ", objId, zpointId);
                $scope.currentObject = angular.copy(objectSvc.findObjectById(objId, $scope.objects));
//                $scope.selectedObject(objId);
                var curZpoint = null;
                if (mainSvc.checkUndefinedNull($scope.currentObject) || mainSvc.checkUndefinedNull($scope.currentObject.zpoints)) {
                    return false;
                }
                $scope.currentObject.zpoints.some(function (element) {
                    if (element.id === zpointId) {
                        curZpoint = angular.copy(element);
                        return true;
                    }
                });

//console.log($scope.currentZpoint);                    
                if (mainSvc.checkUndefinedNull(curZpoint)) {
                    return false;
                }
                var curDevice = curZpoint.deviceObject;
                return objectSvc.isDirectDevice(curDevice);
            };
                
            function successSaveDeviceCallback(resp) {
                $('#showDeviceModal').modal('hide');
//console.log(resp);
            }
                
            $scope.saveDevice = function (device) {
//console.log(device);
                if (!mainSvc.checkUndefinedNull(device.verificationDateString) || (device.verificationDateString !== "")) {
                    device.verificationDate = mainSvc.strDateToUTC(device.verificationDateString, $scope.objectCtrlSettings.dateFormat);
                }
                //check device
//                if (checkDevice(device) === false) {
//                    return false;
//                }
                //send to server
                objectSvc.subscrSendDeviceToServer(device).then(successSaveDeviceCallback, errorCallback);
            };
// **************************************************************************
//                  end Work with devices
// **************************************************************************                


            $scope.invokeHelp = function () {
                alert('This is SPRAVKA!!!111');
            };

            //date picker
            $scope.dateOptsParamsetRu = {
                locale: {
                    daysOfWeek: [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
                    firstDay: 1,
                    monthNames: [ 'Январь', 'Февраль', 'Март', 'Апрель',
                            'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                            'Октябрь', 'Ноябрь', 'Декабрь' ]
                },
                singleDatePicker: true,
                format: "dd.mm.yy"
            };
            $(document).ready(function () {
                $('#inp_ref_range_start').datepicker({
                    dateFormat: $scope.dateOptsParamsetRu.format,
                    firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
                    dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
                    monthNames: $scope.dateOptsParamsetRu.locale.monthNames
                });
                $('#inp_ref_range_end').datepicker({
                    dateFormat: $scope.dateOptsParamsetRu.format,
                    firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
                    dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
                    monthNames: $scope.dateOptsParamsetRu.locale.monthNames
                });

                $("#divWithSettingsObjectTable").scroll(function () {
                    if (angular.isUndefined($scope.filter) || ($scope.filter == '')) {
                        $scope.addMoreObjects();
                        $scope.$apply();
                    }
                });
                
                function objectAddressChange(ev) {
            //console.log($scope.currentObject);
            //console.log(ev);            
            //return;            
                    $scope.currentSug = null;
                    $scope.currentObject.isAddressAuto = false;
                    $scope.currentObject.isValidGeoPos = false;
                    checkGeo();
                    $scope.$apply();
                }

                $("#inputAddress").suggestions({
                    serviceUrl: "https://dadata.ru/api/v2",
                    token: "f9879c8518e9c9e794ff06a6e81eebff263f97d5",
                    type: "ADDRESS",
                    count: 5,
                    /* Вызывается, когда пользователь выбирает одну из подсказок */
                    onSelect: function (suggestion) {
//                        console.log(suggestion);
                        $scope.currentObject.fullAddress = suggestion.value;
                        $scope.currentSug = suggestion;
                        $scope.currentObject.isAddressAuto = true;
                        checkGeo();
                        $scope.$apply();
                    },
                    /*Если пользователь ничего не выбрал*/
                    onSelectNothing: function (query) {
                        objectAddressChange(query);
                    }
                });
                
//                $("#inputAddress").change(function () {
//                    $scope.currentSug = null;
//                    $scope.currentObject.isAddressAuto = false;
//                    $scope.currentObject.isValidGeoPos = false;
//                    checkGeo();
//                    $scope.$apply();
//                });

                                //drop menu
//                    $('ul.dropdown-menu[data-toggle=dropdown]').on('click', function(event) {
//console.log("ul.dropdown-menu[data-toggle=dropdown] click");                        
//                        // Avoid following the href location when clicking
//                        event.preventDefault(); 
//                        // Avoid having the menu to close when clicking
//                        event.stopPropagation(); 
//                        // If a menu is already open we close it
//                        //$('ul.dropdown-menu [data-toggle=dropdown]').parent().removeClass('open');
//                        // opening the one you clicked on
//                        $(this).parent().addClass('open');
//
//                        var menu = $(this).parent().find("ul");
//                        var menupos = menu.offset();
//
//                        if ((menupos.left + menu.width()) + 30 > $(window).width()) {
//                            var newpos = - menu.width();      
//                        } else {
//                            var newpos = $(this).parent().width();
//                        }
//                        menu.css({ left:newpos });
//console.log(menu);                        
//                    });
            });

            //checkers            
            $scope.checkEmptyNullValue = function (numvalue) {
                
                var result = false;
                if ((numvalue === "") || (numvalue == null)) {
                    result = true;
                    return result;
                }
                return result;
            };

            function isNumeric(n) {
                return !isNaN(parseFloat(n)) && isFinite(n);
            }

            $scope.checkNumericValue = function (numvalue) {
                var result = true;
                if ($scope.checkEmptyNullValue(numvalue)) {
                    return result;
                }
                if (!isNumeric(numvalue)) {
                    result = false;
                    return result;
                }
                return result;
            };

            $scope.checkPositiveNumberValue = function (numvalue) {
                return mainSvc.checkPositiveNumberValue(numvalue);
            };

            $scope.checkNumericInterval = function (leftBorder, rightBorder) {
                if (($scope.checkEmptyNullValue(leftBorder)) || ($scope.checkEmptyNullValue(rightBorder))) {
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
                //check user rights
            $scope.isAdmin = function () {
                return mainSvc.isAdmin();
            };

            $scope.isReadonly = function () {
                return mainSvc.isReadonly();
            };

            $scope.isROfield = function () {
                return ($scope.isReadonly() || !$scope.isAdmin());
            };

            $scope.isCabinet = function () {
                return mainSvc.isCabinet();
            };
                
            $scope.emptyString = function (str) {
                return mainSvc.checkUndefinedEmptyNullValue(str);
            };

// ********************************************************************************************
//  TREEVIEW
//*********************************************************************************************
            $scope.objectCtrlSettings.isTreeView = (true && !mainSvc.isCabinet());
            $scope.objectCtrlSettings.isFullObjectView = false;

            $scope.data.currentTree = {};
            $scope.data.newTree = {};
            $scope.data.defaultTree = null;// default tree               

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
                    objectSvc.loadSubscrFreeObjectsByTree($scope.data.currentTree.id).then(successGetObjectsCallback);
                } else {
                    objectSvc.loadSubscrObjectsByTreeNode($scope.data.currentTree.id, item.id).then(successGetObjectsCallback);
                }
            };

            $scope.data.trees = [];

            $scope.loadTree = function (tree, objId) {
                $scope.loading = true;
                $scope.treeLoading = true;
                objectSvc.loadSubscrTree(tree.id).then(function (resp) {
                    $scope.treeLoading = false;
                    $scope.messages.treeMenuHeader = tree.objectName || tree.id;
                    var respTree = angular.copy(resp.data);
                    mainSvc.sortTreeNodesBy(respTree, "objectName");
                    $scope.data.currentTree = respTree;
                    $scope.objects = [];
                    $scope.objectsOnPage = [];
                    $scope.loading = false;
                    $rootScope.$broadcast('objectSvc:loaded');
                    $scope.messages.noObjects = "";
                }, errorCallback);
            };

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
                //if tree is off
                if ($scope.objectCtrlSettings.isTreeView == false) {
                    getObjectsData();
                } else {
                //if tree is on
                    objectSvc.loadDefaultTreeSetting().then(successLoadTreeSetting, errorCallback);
                }
            };

// ********************************************************************************************
//  END TREEVIEW
//*********************************************************************************************

// ***********************************************************************************************
//                      Indicator columns editor
// ***********************************************************************************************
            var waterColumns = indicatorSvc.getWaterColumns();
            $scope.data.waterColumns = angular.copy(waterColumns);
            var electricityColumns = indicatorSvc.getElectricityColumns();
            $scope.data.electricityColumns = angular.copy(electricityColumns);

            function getSelectedColumns(columnArr) {
                var selectedItems = [];
                columnArr.forEach(function (elem) {
                    if (elem.isVisible == true) {
                        selectedItems.push(elem.fieldName);
                    }
                });
                return selectedItems;
            }

            function setIndicatorColumnPrefForObject(contObj, resourceKind, columnArr) {
                var selectedItems = getSelectedColumns(columnArr);
                if (selectedItems.length > 0) {
//                        $cookies["indicator" + resourceKind + contObj.id] = selectedItems;
                    var now = new Date();
                    var exp = new Date(now.getFullYear() + 10, now.getMonth(), now.getDate());
                    document.cookie = "indicator" + resourceKind + contObj.id + "=" + selectedItems + ";expires=" + exp.toUTCString();
                } else {
                    $cookies["indicator" + resourceKind + contObj.id] = null;
                }
            }

            function readIndicatorColumnPrefForObject(contObj, resourceKind) {
                var columnPrefs = $cookies["indicator" + resourceKind + contObj.id];
                if (mainSvc.checkUndefinedNull(columnPrefs)) {
                    return null;
                }
                return columnPrefs.split(',');
            }

            function intersecArrays(A, B) {
                var m = A.length, n = B.length, c = 0, C = [], i;
                for (i = 0; i < m; i += 1) {
                    var j = 0, k = 0;
                    while (B[j] !== A[i] && j < n) { j += 1; }
                    while (C[k] !== A[i] && k < c) { k += 1; }
                    if (j != n && k == c) { C[c++] = A[i]; }
                }
                return C;
            }

            function multiIntersecArrays(k, A) {  // При вызовах всегда полагать k=0. А - это двумерный (!)
               //  массив, элементы которого A[ i ] - также массивы,
                var n = A.length;               //  пересечение которых нужно найти.
                if (k == n - 2) {
                    return intersecArrays(A[n - 2], A[n - 1]);   // Функцию IntersecArrays см. выше.
                } else {
                    return intersecArrays(A[k], multiIntersecArrays(k + 1, A));
                }
            }

            $scope.selectAllWaterColumns = function () {
                $scope.data.selectedIndicatorMode.waterColumns.forEach(function (wc) {
                    wc.isVisible = $scope.selectAllWaterColumnFlag;
                });
            };

            $scope.selectAllElectricityColumns = function () {
                $scope.data.selectedIndicatorMode.electricityColumns.forEach(function (ec) {
                    ec.isVisible = $scope.selectAllElectricityColumnFlag;
                });
            };

            $scope.initIndicatorColumnsPref = function () {
                $scope.data.waterColumns = angular.copy(waterColumns);
                $scope.data.electricityColumns = angular.copy(electricityColumns);
                //read all selected objects indicator columns pref
                //make "AND" for this sets and create common set
                //open window with common set
                var waterSettings = [];
                var elecSettings = [];
                $scope.selectAllWaterColumnFlag = false;
                $scope.selectAllElectricityColumnFlag = false;
                $scope.objectsOnPage.forEach(function (el) {
                    if (el.selected === true) {
                        var tmpPrefs = readIndicatorColumnPrefForObject(el, "hw");
                        if (!mainSvc.checkUndefinedNull(tmpPrefs)) {
                            waterSettings.push(tmpPrefs);//= MultiIntersecArrays(0, tmpPrefs);
                        }
                        tmpPrefs = readIndicatorColumnPrefForObject(el, "el");
                        if (!mainSvc.checkUndefinedNull(tmpPrefs)) {
                            elecSettings.push(tmpPrefs);//= MultiIntersecArrays(0, tmpPrefs);
                        }
                    }
                });
                if (waterSettings.length == 1) {
                    waterSettings = waterSettings[0];
                } else if (waterSettings.length > 1) {
                    waterSettings = multiIntersecArrays(0, waterSettings);
                }
                if (waterSettings.length != 0) {
                    waterSettings.forEach(function (ws) {
                        $scope.data.waterColumns.some(function (wc) {
                            if (wc.fieldName == ws) {
                                wc.isVisible = true;
                                return true;
                            }
                        });
                    });
                } else {
                    $scope.selectAllWaterColumnFlag = true;
                    $scope.data.waterColumns.forEach(function (wc) {
                        wc.isVisible = true;
                    });
                }
                if (elecSettings.length == 1) {
                    elecSettings = elecSettings[0];
                } else if (elecSettings.length > 1) {
                    elecSettings = multiIntersecArrays(0, elecSettings);
                }
                if (elecSettings.length != 0) {
                    elecSettings.forEach(function (ws) {
                        $scope.data.electricityColumns.some(function (wc) {
                            if (wc.fieldName == ws) {
                                wc.isVisible = true;
                                return true;
                            }
                        });
                    });
                } else {
                    $scope.selectAllElectricityColumnFlag = true;
                    $scope.data.electricityColumns.forEach(function (ec) {
                        ec.isVisible = true;
                    });
                }

                //set common prefs
                $('#columnSettingsModal').modal();
            };

            function checkSelectedColumns() {
                var result = true;
                var hwcols = getSelectedColumns($scope.data.waterColumns);
//                    if (hwcols.length == 0){
//                        notificationFactory.errorInfo("Ошибка", "Выберете хотя бы одну колонку для водных показаний");
//                        result = false;
//                    }
                var elcols = getSelectedColumns($scope.data.electricityColumns);
                if (elcols.length == 0 && hwcols.length == 0) {
                    notificationFactory.errorInfo("Ошибка", "Выберете хотя бы одну колонку.");
                    result = false;
                }
                return result;
            }

            $scope.setIndicatorColumnsPref = function () {
                //check columns
                var check = checkSelectedColumns();
                if (check == false) {
                    return "Columns are not tested.";
                }
                //selected objects
                $scope.objectsOnPage.forEach(function (el) {
                    if (el.selected === true) {
                        setIndicatorColumnPrefForObject(el, "hw", $scope.data.waterColumns);
                        setIndicatorColumnPrefForObject(el, "el", $scope.data.electricityColumns);
                    }
                });

                $('#columnSettingsModal').modal('hide');
            };

            $scope.data.selectedWaterColumns = [];
            $scope.data.selectedElectricityColumns = [];
// ***********************************************************************************************
//                      end Indicator columns editor
// ***********************************************************************************************

// ********************************************************************************************************
//                  Device scheduler
// ********************************************************************************************************
            $scope.openSchedule = function (objId, zpointId) {
//console.log("openSchedule");
                $scope.selectedZpoint(objId, zpointId);
//console.log($scope.currentZpoint);                    
                var curDevice = $scope.currentZpoint.deviceObject;
                $scope.getDeviceSchedulerSettings(objId, curDevice);
            };

                //get device scheduler
            $scope.getDeviceSchedulerSettings = function (objId, device) {
                objectSvc.getDeviceSchedulerSettings(objId, device.id).then(
                    function (resp) {
                        $scope.data.currentScheduler = resp.data;
//console.log($scope.data.currentScheduler);                            
                        $scope.selectedDevice(device);
                        $('#scheduleEditorModal').modal();
                    },
                    errorCallback
                );
            };
// ********************************************************************************************************
//                  end Device scheduler
// ********************************************************************************************************

// ********************************************************************************************************
//                  The settings the view of a indicator
// ********************************************************************************************************

//                var VCOOKIE_URL = "../api/subscr/vcookie";
//                var USER_VCOOKIE_URL = "../api/subscr/vcookie/user";
//                var OBJECT_INDICATOR_PREFERENCES_VC_MODE = "OBJECT_INDICATOR_PREFERENCES";

            var DEFAULT_INDICATOR_MODE = {

                    keyname: "INDICATOR_MODE_" + (new Date()).getTime(),
                    caption: "Новое представление 1",
                    class: "nmc-mode-menu-item-active",
                    isActive: true,
                    waterColumns: angular.copy(waterColumns),
                    indicatorHwKind: "24h",
                    indicatorHwPerPage: 25,
                    electricityColumns: angular.copy(electricityColumns),
                    indicatorElMode: "",
                    indicatorElKind: "24h"
                };
            DEFAULT_INDICATOR_MODE.waterColumns.forEach(function (wCol) {
                wCol.isVisible = true;
            });

            DEFAULT_INDICATOR_MODE.electricityColumns.forEach(function (eCol) {
                eCol.isVisible = true;
            });


            $scope.data.indicatorModes = [];

            function performIndicatorModes(inputData) {
//console.log(inputData); 
//console.log($scope.data.heatWidgetList);                    
                var result = [];
                if (angular.isArray(inputData) && inputData.length > 0) {
                    inputData.forEach(function (elem) {
//console.log(elem);    
                        if (elem.vcValue === null) {
                            return false;
                        }
                        var tmpMode = elem,
                            vcvalue = JSON.parse(elem.vcValue);
                        tmpMode.keyname = tmpMode.vcKey;
                        tmpMode.caption = vcvalue.caption;
                        tmpMode.waterColumns = vcvalue.waterColumns;
                        tmpMode.electricityColumns = vcvalue.electricityColumns;
                        tmpMode.indicatorHwKind = vcvalue.indicatorHwKind;
                        tmpMode.indicatorHwPerPage = vcvalue.indicatorHwPerPage;
                        tmpMode.indicatorElKind = vcvalue.indicatorElKind;
                        tmpMode.indicatorElMode = vcvalue.indicatorElMode;
                        if (mainSvc.checkUndefinedNull(vcvalue.widgets)) {
                            tmpMode.widgets = {};
                        // ??????????????????????????????????????????????????????????????
                            if ($scope.data.heatWidgetList.length > 0) {
                                tmpMode.widgets.heat = $scope.data.heatWidgetList[0].widgetName;
                                $scope.data.heatWidgetList.some(function (heatw) {
                                    if (heatw.isDefault === true) {
                                        tmpMode.widgets.heat = heatw.widgetName;
                                        return true;
                                    }
                                });
                            }
                            if ($scope.data.hwWidgetList.length > 0) {
                                tmpMode.widgets.hw = $scope.data.hwWidgetList[0].widgetName;
                                $scope.data.hwWidgetList.some(function (hww) {
                                    if (hww.isDefault === true) {
                                        tmpMode.widgets.hw = hww.widgetName;
                                        return true;
                                    }
                                });
                            }
                            if ($scope.data.cwWidgetList.length > 0) {
                                tmpMode.widgets.cw = $scope.data.cwWidgetList[0].widgetName;
                                $scope.data.cwWidgetList.some(function (cww) {
                                    if (cww.isDefault === true) {
                                        tmpMode.widgets.cw = cww.widgetName;
                                        return true;
                                    }
                                });
                            }
                            if ($scope.data.elWidgetList.length > 0) {
                                tmpMode.widgets.el = $scope.data.elWidgetList[0].widgetName;
                                $scope.data.elWidgetList.some(function (elw) {
                                    if (elw.isDefault === true) {
                                        tmpMode.widgets.el = elw.widgetName;
                                        return true;
                                    }
                                });
                            }
                        } else {
                            tmpMode.widgets = vcvalue.widgets;
                        }
//console.log(tmpMode);                            
                        result.push(tmpMode);
                    });
                }

                if (!angular.isArray(result) || result.length === 0) {
                    var tmp = angular.copy(DEFAULT_INDICATOR_MODE);
                    $scope.data.selectedIndicatorMode = tmp;
                    result.push(tmp);
                    return result;
                }
//console.log(result);
                return result;
            }

//                function getIndicatorModesTest () {
//                    var url = VCOOKIE_URL;                    
//                    $http.get(url).then(function(resp){                        
//console.log(resp.data);                        
//                    }, errorCallback);
//                }
//getIndicatorModesTest();                

            function getIndicatorModes() {
                var url = VCOOKIE_URL;
                if (!mainSvc.checkUndefinedNull(OBJECT_INDICATOR_PREFERENCES_VC_MODE)) {
                    url += "?vcMode=" + OBJECT_INDICATOR_PREFERENCES_VC_MODE;
                }
                $http.get(url).then(function (resp) {
                    $scope.data.indicatorModes = performIndicatorModes(resp.data);
                    if (angular.isArray($scope.data.indicatorModes) && $scope.data.indicatorModes.length > 0) {
                        mainSvc.sortItemsBy($scope.data.indicatorModes, "caption");
                        $scope.data.selectedIndicatorMode = angular.copy($scope.data.indicatorModes[0]);
                        $scope.messages.modeHeader = $scope.data.selectedIndicatorMode.caption;
                    }
//console.log($scope.data.indicatorModes);                        
                }, errorCallback);
            }

            //getIndicatorModes();

            function getIndicatorModeForObject(obj) {
                var url = USER_VCOOKIE_URL;
                if (!mainSvc.checkUndefinedNull(OBJECT_INDICATOR_PREFERENCES_VC_MODE)) {
                    url += "?vcMode=" + OBJECT_INDICATOR_PREFERENCES_VC_MODE;
                }
                $http.get(url).then(function (resp) {
                    $scope.data.indicatorModes = resp.data;
                }, errorCallback);
            }


//                $scope.data.selectedIndicatorMode = $scope.data.indicatorModes[0];

            $scope.addIndicatorMode = function () {
                var newMode = {

                    keyname: "INDICATOR_MODE_" + (new Date()).getTime(),
                    caption: "Представление " + ($scope.data.indicatorModes.length + 1),
                    waterColumns: angular.copy(waterColumns),
                    indicatorHwKind: "24h",
                    indicatorHwPerPage: 25,
                    electricityColumns: angular.copy(electricityColumns),
                    indicatorElMode: "",
                    indicatorElKind: "24h"
                };
                $scope.data.indicatorModes.push(newMode);
            };

            $scope.deleteIndicatorModeInit = function (mode) {
                $scope.data.deletingMode = mode;
                $scope.messages.deleteMessage = "Вы действительно хотите удалить представление \"" + mode.caption + "\"?";
                $('#messageForUserModal').modal();
            };

            $scope.deleteIndicatorMode = function (mode) {
                if (mainSvc.checkUndefinedNull(mode) || mainSvc.checkUndefinedNull(mode.vcMode) || mainSvc.checkUndefinedNull(mode.vcKey)) {
                    console.log("Indicator mode is incorrect!");
                    return false;
                }
                var url = VCOOKIE_URL + "/list";
                var requestBody = {};
                requestBody.vcMode = mode.vcMode;
                requestBody.vcKey = mode.vcKey;
                requestBody.vcValue = null;
                $http.put(url, [requestBody]).then(function (resp) {
//console.log(resp.data);                                                
                    notificationFactory.success();
                    $('#messageForUserModal').modal('hide');

                    if (mainSvc.checkUndefinedNull(resp.data) || !angular.isArray(resp.data) || resp.data.length === 0) {
                        console.log("Response from server on the mode deleting  is undefined or null!");
                        return false;
                    }

                    var deletedMode = resp.data[0];
                    var delIndex = -1;
                    $scope.data.indicatorModes.some(function (imode, ind) {
                        if (imode.keyname === deletedMode.vcKey) {
                            delIndex = ind;
                            return true;
                        }
                    });
                    if (delIndex !== -1) {
                        $scope.data.indicatorModes.splice(delIndex, 1);
                    }

                    if ($scope.data.selectedIndicatorMode.keyname == deletedMode.vcKey) {
                        if ($scope.data.indicatorModes.length === 0) {
                            var tmp = angular.copy(DEFAULT_INDICATOR_MODE);
                            $scope.data.indicatorModes.push(tmp);
                        }
                        $scope.data.selectedIndicatorMode = $scope.data.indicatorModes[0];
                        $scope.messages.modeHeader = $scope.data.selectedIndicatorMode.caption;
                    }


                }, errorCallback);
            };

//                $scope.deleteIndicatorMode_v1 = function (mode) {
//                    var delInd = -1;
//                    $scope.data.indicatorModes.some(function(imode, index){
//                        if (imode.keyname === mode.keyname){
//                            delInd = index;
//                            imode.isDeleted = true;
//                            return true;
//                        }
//                    });
//                    
//                    if (delInd !== -1){
//                        $scope.data.indicatorModes.splice(delInd, 1);
//                    }
//                }                

            $scope.modeClick = function (mode) {
//                    $scope.data.selectedIndicatorMode.isActive = false;
//                    $scope.data.selectedIndicatorMode.class = "";
//                    mode.class = "nmc-mode-menu-item-active";
//                    mode.isActive = true;
                $scope.selectAllWaterColumnFlag = false;
                $scope.selectAllElectricityColumnFlag = false;
                $scope.data.selectedIndicatorMode = angular.copy(mode);
                $scope.messages.modeHeader = $scope.data.selectedIndicatorMode.caption;
            };

            $scope.initIndicatorPreferences = function () {
                $scope.selectAllWaterColumnFlag = false;
                $scope.selectAllElectricityColumnFlag = false;
                if (angular.isArray($scope.data.indicatorModes) && $scope.data.indicatorModes.length > 0) {
                    mainSvc.sortItemsBy($scope.data.indicatorModes, "caption");
                    $scope.data.selectedIndicatorMode = angular.copy($scope.data.indicatorModes[0]);
//console.log($scope.data.indicatorModes);                        
//console.log($scope.data.selectedIndicatorMode);                        
                    $scope.messages.modeHeader = $scope.data.selectedIndicatorMode.caption;
                }

//                    $scope.data.indicatorHwKind = "24h";
//                    $scope.data.indicatorHwPerPage = 25;
//                    $scope.data.indicatorElMode = "";
//                    $scope.data.indicatorElKind = "24h";
            };

            function modeMapping(mode) {
                var preparedMode = {},
                    vcvalue = {};
                preparedMode.vcMode = OBJECT_INDICATOR_PREFERENCES_VC_MODE;
                preparedMode.vcKey = mode.keyname;
                vcvalue.caption = mode.caption;
                vcvalue.waterColumns = mode.waterColumns;
                vcvalue.electricityColumns = mode.electricityColumns;
                vcvalue.indicatorHwKind = mode.indicatorHwKind;
                vcvalue.indicatorHwPerPage = mode.indicatorHwPerPage;
                vcvalue.indicatorElMode = mode.indicatorElMode;
                vcvalue.indicatorElKind = mode.indicatorElKind;

                vcvalue.widgets = mode.widgets;

                preparedMode.vcValue = JSON.stringify(vcvalue);
                return preparedMode;
            }

            $scope.saveIndicatorPreferencesAsInit = function (mode) {
                $scope.data.selectedIndicatorModeSaveAs = angular.copy(mode);
                $scope.data.selectedIndicatorModeSaveAs.keyname = "INDICATOR_MODE_" + (new Date()).getTime();
                $('#editIndicatorModeModal').modal();
            };

            $scope.saveIndicatorPreferencesAs = function (mode) {
                if (mainSvc.checkUndefinedNull(mode)) {
                    console.log("Saving mode is undefined or null!");
                    return false;
                }
                $scope.data.selectedIndicatorModeSaveAs = angular.copy(mode);
                $scope.data.selectedIndicatorModeSaveAs.keyname = "INDICATOR_MODE_" + (new Date()).getTime();
                if (mainSvc.checkUndefinedNull($scope.data.selectedIndicatorModeSaveAs)) {
                    console.log("$scope.data.selectedIndicatorModeSaveAs is undefined or null!");
                    return false;
                }

                var checkFlag = true;
                $scope.data.indicatorModes.some(function (imode) {
                    if ((imode.caption === mode.caption) && !mainSvc.checkUndefinedNull(imode.vcKey)) {
                        notificationFactory.errorInfo("Ошибка", "Наименование представления должно быть уникальным. Измените наименование и повторите попытку.");
                        checkFlag = false;
                        return true;
                    }
                });

                if (checkFlag === false) {
                    return false;
                }

                $scope.saveIndicatorPreferences($scope.data.selectedIndicatorModeSaveAs);
            };

            $scope.saveIndicatorPreferences = function (mode) {
//console.log($scope.data.waterColumns);                    
//console.log($scope.data.electricityColumns);                     
//console.log($scope.data.indicatorHwKind);
//console.log($scope.data.indicatorHwPerPage);                                         
//console.log($scope.data.indicatorElMode);
//console.log($scope.data.indicatorElKind);
                var preparedModes = [];
                if (mainSvc.checkUndefinedNull(mode)) {
                    console.log("Saving mode is undefined or null!");
                    return false;
//                        preparedModes = $scope.data.indicatorModes.map(modeMapping);
                } else {
                    var checkFlag = true;
                    if (!(mode.caption > '')) {
                        notificationFactory.errorInfo("Ошибка", "Не задано наименование для представления!");
                        checkFlag = false;
                    }

                    if (checkFlag === false) {
                        return false;
                    }

                    preparedModes.push(modeMapping(mode));
                }

                var url = VCOOKIE_URL + "/list";
                $http.put(url, preparedModes).then(function (resp) {
                    if (mainSvc.checkUndefinedNull(resp.data) || !angular.isArray(resp.data) || resp.data.length === 0) {
                        console.log("Response data = " + resp.data);
                        return false;
                    }
//    console.log(resp.data);
                    if (resp.data.length === 1) {
                        var receivedMode = performIndicatorModes(resp.data);
                        var changeIndex = -1;
                        $scope.data.indicatorModes.some(function (imode, ind) {
                            if (imode.keyname === receivedMode[0].keyname) {
                                changeIndex = ind;
                                return true;
                            }
                        });
                        if (changeIndex !== -1) {
                            $scope.data.indicatorModes[changeIndex] = receivedMode[0];
                        } else {
                            $scope.data.indicatorModes.push(receivedMode[0]);
                        }
                        //delete search and delete DEFAULT_INDICATOR_MODE
                        if ($scope.data.indicatorModes.length > 1) {
                            var delIndex = -1;
                            $scope.data.indicatorModes.some(function (imode, ind) {
                                if (mainSvc.checkUndefinedNull(imode.vcKey)) {
                                    delIndex = ind;
                                    return true;
                                }
                            });
                            if (delIndex !== -1) {
                                $scope.data.indicatorModes.splice(delIndex, 1);
                            }
                        }
                        $scope.messages.modeHeader = receivedMode[0].caption;
                    } else {
                        $scope.data.indicatorModes = performIndicatorModes(resp.data);
                    }
                    $('#columnSettingsModal').modal('hide');
                    $('#editIndicatorModeModal').modal('hide');
                }, errorCallback);

//                    //check columns
//                    var check = checkSelectedColumns();
//                    if (check == false)
//                        return "Columns are not tested.";
//                    //selected objects
//                    $scope.objectsOnPage.forEach(function(el){
//                        if(el.selected === true){
//                            setIndicatorColumnPrefForObject(el, "hw", $scope.data.waterColumns);
//                            setIndicatorColumnPrefForObject(el, "el", $scope.data.electricityColumns);
//                        };
//                    });
//                    
//                    $('#columnSettingsModal').modal('hide');
            };

            function getDefaultObjectIndicatorMode(objId) {
                //reset indicator modes flags to false
                $scope.data.indicatorModes.forEach(function (imode) { imode.isCurrentMode = false; });

                if (mainSvc.checkUndefinedNull(USER_VCOOKIE_URL) || mainSvc.checkUndefinedNull(OBJECT_INDICATOR_PREFERENCES_VC_MODE) || mainSvc.checkUndefinedNull(objId)) {
                    console.log("Request required params is null!");
                    return false;
                }
                var url = USER_VCOOKIE_URL + "?vcMode=" + OBJECT_INDICATOR_PREFERENCES_VC_MODE + "&vcKey=" + "OIP_" + objId;
                $http.get(url).then(function (resp) {
                    if (mainSvc.checkUndefinedNull(resp) || mainSvc.checkUndefinedNull(resp.data) || resp.data.length === 0) {
                        return false;
                    }
//console.log(resp.data);     
                    var objectIndicatorModeKeyname = null;
                    try {
                        objectIndicatorModeKeyname = JSON.parse(resp.data[0].vcValue);
                    } catch (err) {
                        console.error("objectIndicatorModeKeyname parse error: ", err);
                    }
                    $scope.data.indicatorModes.some(function (imode) {
                        if (imode.keyname === objectIndicatorModeKeyname) {
                            imode.isCurrentMode = true;
                            return true;
                        }
                    });
                }, errorCallback);
            }

            $scope.setDefaultObjectIndicatorModeForObjects = function (mode) {
                //reset indicator modes flags to false
//                    $scope.data.indicatorModes.forEach(function(imode){imode.isCurrentMode = false});

                if (mainSvc.checkUndefinedNull(USER_VCOOKIE_URL) || mainSvc.checkUndefinedNull(OBJECT_INDICATOR_PREFERENCES_VC_MODE) || mainSvc.checkUndefinedNull(mode)) {
                    console.log("Request required params is null!");
                    return false;
                }

                                    //get the object ids array
                var requestBody = [];
                if ($scope.objectCtrlSettings.allSelected === true) {
                    requestBody = $scope.objects.map(function (el) {
                        var elem = {};
                        elem.vcMode = OBJECT_INDICATOR_PREFERENCES_VC_MODE;
                        elem.vcKey = "OIP_" + el.id;
                        elem.vcValue = JSON.stringify(mode.keyname);
                        return elem;
                    });
                } else {
                    $scope.objectsOnPage.forEach(function (el) {
                        if (el.selected === true) {
                            requestBody.push({
                                vcMode: OBJECT_INDICATOR_PREFERENCES_VC_MODE,
                                vcKey:  "OIP_" + el.id,
                                vcValue: JSON.stringify(mode.keyname)
                            });
                        }
                    });
                }
                if (requestBody.length === 0) {
                    return false;
                }

                var url = USER_VCOOKIE_URL + "/list";
                $http.put(url, requestBody).then(function (resp) {
                    notificationFactory.success();
                }, errorCallback);
            };

            $scope.setDefaultObjectIndicatorMode = function (objId, mode) {
                //reset indicator modes flags to false
//                    $scope.data.indicatorModes.forEach(function(imode){imode.isCurrentMode = false});

                if (mainSvc.checkUndefinedNull(USER_VCOOKIE_URL) || mainSvc.checkUndefinedNull(OBJECT_INDICATOR_PREFERENCES_VC_MODE) || mainSvc.checkUndefinedNull(objId)) {
                    console.log("Request required params is null!");
                    return false;
                }
                var url = USER_VCOOKIE_URL + "/list";
                var requestBody = {};
                requestBody.vcMode = OBJECT_INDICATOR_PREFERENCES_VC_MODE;
                requestBody.vcKey = "OIP_" + objId;
                requestBody.vcValue = JSON.stringify(mode.keyname);
                $http.put(url, [requestBody]).then(function (resp) {
                    notificationFactory.success();
                }, errorCallback);
            };

            $scope.editDefaultIndicatorMode = function () {
                $scope.data.selectedIndicatorMode.isEditMode = true;
            };

            $scope.applyChangeCaption = function () {
                $scope.messages.modeHeader = $scope.data.selectedIndicatorMode.caption;
                $scope.data.selectedIndicatorMode.isEditMode = false;
            };

            $scope.cancelChangeCaption = function () {
                $scope.data.selectedIndicatorMode.caption = $scope.messages.modeHeader;
                $scope.data.selectedIndicatorMode.isEditMode = false;
            };

            $scope.widgetPreview = function (widgetType) {
                $scope.data.widgetPreview = {};
                $scope.data.widgetPreview.type = widgetType;
                $scope.data.widgetPreview.options = {
                    zpointName: "Наименование точки учета",
                    contZpointId: 1,
                    contObjectId: 1,
                    previewMode: true
                };
//console.log("Widget preview1");
//                    var zpointWidget = {};
//                    zpointWidget.type = "zpointEl";
//                    zpointWidget.zpointName = "zpointEl";
//                    zpointWidget.zpointid = "1";
//                    zpointWidget.objectid = "1";
//                    //var setToolTip = function(title, text, elDom, targetDom, delay, width){
//                    //var tooltipHtml = "Hello<br><hr>";
//                    var tooltipHtml = "<div class='col-xs-12 noPadding'>";                        
//                    tooltipHtml += "<div ng-controller='widgetContainer'>" +
//                            "<span ng-show='title' ng-bind='title'></span>" +                              
//                            "<div ng-show='isLoading'>Загрузка...</div>" +
//                            "<div ng-show='isError'>Ошибка... <button ng-click='reload()'>Перезагрузка</button></div>" + 
//                            "<ng-widget src=\"'" + zpointWidget.type + 
//                            "'\" options=\"{'zpointName' : '" + zpointWidget.zpointName +                                                         
//                            "', 'contZpointId': '" + zpointWidget.zpointid +                              
//                            "', 'contObjectId': '" + zpointWidget.objectid +                                                                               "', 'previewMode': '" + true +            
//                            "' }\" ng-show=\"!isLoading && !isError\"></ng-widget>" +
//                            "</div>";
//                        
//                    tooltipHtml += "</div>";                    
//                    
//                    mainSvc.setToolTip("Предпросмотр виджета", $compile(tooltipHtml)($scope), '#elWidgetPreviewBtn', '#elWidgetPreviewBtn', 1, 1000, 'top left', 'bottom right', 'qtip-nmc-tooltip-widget');                    
            };

// ********************************************************************************************************
//                  end Settings view of indicator
// ********************************************************************************************************

// *****************************************************************************************
//                Zpoint metadata
// ******************************************************************************************                                    
            $scope.openZpointMetadata = function (coId, zpId) {
//console.log("openZpointMetadata");                    
                $scope.selectedZpoint(coId, zpId);
                //get srcProp
                objectSvc.getZpointMetaSrcProp(coId, zpId).then(
                    function (resp) {

                        $scope.currentZpoint.metaData = {};
                        $scope.currentZpoint.metaData.srcProp = resp.data;
                        $scope.currentZpoint.metaData.srcProp.push({columnName: ""});
//console.log(angular.copy($scope.currentZpoint));                            
                        objectSvc.getZpointMetaDestProp(coId, zpId).then(
                            function (resp) {
//console.log(angular.copy($scope.currentZpoint));
//console.log(resp.data);                                    
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
//console.log($scope.currentObject);                    
                objectSvc.getZpointMetadata($scope.currentObject.id, $scope.currentZpoint.id)
                    .then(function (resp) {
                        mainSvc.sortItemsBy(resp.data, "destProp");
                        $scope.currentZpoint.metaData.metaData = resp.data;
                        $('#metaDataEditorModal').modal();
//console.log($scope.currentZpoint);                                            
                    }, errorCallback);
            });

// *****************************************************************************************
//                end Zpoint metadata
// ****************************************************************************************** 

// *****************************************************************************************
//                Zpoint data source
// ******************************************************************************************                  
            $scope.openDatasource = function (coId, zpId) {
//console.log("openZpointMetadata");                    
                $scope.selectedZpoint(coId, zpId);

//                    current-datasource="data.currentDatasource"
//     datasource-types="data.datasourceTypesForDatasource"
                if (mainSvc.checkUndefinedNull($scope.currentZpoint.deviceObject.activeDataSource)) {
                    console.log("Datasource is incorrect!");
                    return false;
                }
                $scope.data.currentDatasource = angular.copy($scope.currentZpoint.deviceObject.activeDataSource.subscrDataSource);
                if (mainSvc.checkUndefinedNull($scope.data.currentDatasource.rawModemIdentity) &&
                        !mainSvc.checkUndefinedNull($scope.data.currentDatasource.rawModemModelId)) {
                    var modemModel = findModemIdentity($scope.data.currentDatasource.rawModemModelId);
                    $scope.data.currentDatasource.rawModemIdentity = modemModel.rawModemModelIdentity;
                    $scope.data.currentDatasource.rawModemDialupAvailable = modemModel.isDialup;
                }
                var tmpDatasourceTypes = [angular.copy($scope.data.currentDatasource.dataSourceType)];
                $scope.data.datasourceTypesForDatasource = tmpDatasourceTypes;
                $('#showDatasourceModal').modal();

            };
// *****************************************************************************************
//             end   Zpoint data source
// ******************************************************************************************                  
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
                    preparedCategory = null,
                    buildingCategories = angular.copy($scope.data.buildingCategories);
                buildingCategories.forEach(function (bcat) {
                    if (bcat.buildingType === buildingType) {
                        categoryListByBuildingType.push(angular.copy(bcat));
                    }
                });
                categoryListByBuildingType.forEach(function (pcat) {
                    buildingCategories.forEach(function (bcat) {
                        if (bcat.parentCategory === pcat.keyname) {
                            preparedCategory = angular.copy(bcat);
                            preparedCategory.parentCategoryCaption = pcat.caption;
                            filtredCategoryList.push(preparedCategory);
                        }
                    });
                });
//                $scope.data.buildingCategories = buildingCategories;
                $scope.data.preparedBuildingCategoryList = filtredCategoryList;
//                    console.log($scope.data.preparedBuildingCategoryList);
            }
                
            function performBuildingCategoryListForUiSelect(buildingType) {
                //find b cat when buildingType === input buildingType
                //find b cat when parentCat === keyname from up ^
                var categoryListByBuildingType = [],
                    filtredCategoryList = [],
                    preparedCategory = null,
                    parentCategories = [];
                $scope.data.buildingCategories.forEach(function (bcat) {
                    if (bcat.buildingType === buildingType && bcat.parentCategory === null) {
                        var parentCat = angular.copy(bcat);
                        parentCat.depth = 1;
                        categoryListByBuildingType.push(parentCat);
                        $scope.data.buildingCategories.forEach(function (cat) {
                            if (cat.parentCategory === bcat.keyname) {
                                var childCat = angular.copy(cat);
                                childCat.depth = 2;
                                categoryListByBuildingType.push(childCat);
                            }
                        });
                    }
                });
                $scope.data.preparedBuildingCategoryListForUiSelect = categoryListByBuildingType;
//                    console.log($scope.data.preparedBuildingCategoryListForUiSelect);
            }

            $scope.changeBuildingType = function (buildingType) {
//                    console.log("changeBuildingType");
                $scope.currentObject.buildingTypeCategory = null;
                $cookies.recentBuildingTypeCategory = $scope.currentObject.buildingTypeCategory;
//                $('#inputBuildingCategory').removeClass('nmc-select-form-high');
//                $('#inputBuildingCategory').addClass('nmc-select-form');
                
                $('#inputBuildingCategoryUI').removeClass('nmc-ui-select-form-high');
                $('#inputBuildingCategoryUI').addClass('nmc-ui-select-form');
                
                if (mainSvc.checkUndefinedNull(buildingType)) {
                    return false;
                }
                $cookies.recentBuildingType = buildingType;
//                performBuildingCategoryList(buildingType);
//                performBuildingCategoryListForUiSelect(buildingType);
                $scope.data.preparedBuildingCategoryListForUiSelect = objectSvc.performBuildingCategoryListForUiSelect(buildingType, $scope.data.buildingCategories);
            };

            function setBuildingCategory() {
                var bCat = null;
//                $scope.data.preparedBuildingCategoryList.some(function (bcat) {
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
//                    $('#inputBuildingCategory').removeClass('nmc-select-form');
//                    $('#inputBuildingCategory').addClass('nmc-select-form-high');
                    
                    $('#inputBuildingCategoryUI').removeClass('nmc-ui-select-form');
                    $('#inputBuildingCategoryUI').addClass('nmc-ui-select-form-high');
                    
                } else {
//                    $('#inputBuildingCategory').removeClass('nmc-select-form-high');
//                    $('#inputBuildingCategory').addClass('nmc-select-form');
                    
                    $('#inputBuildingCategoryUI').removeClass('nmc-ui-select-form-high');
                    $('#inputBuildingCategoryUI').addClass('nmc-ui-select-form');
                }
                if (mainSvc.checkUndefinedNull($scope.currentObject.buildingTypeCategory)) {
                    return false;
                }
            }

            $scope.changeBuildingCategory = function () {
                setBuildingCategory();
                $cookies.recentBuildingTypeCategory = $scope.currentObject.buildingTypeCategory;
            };
// ********************************************************************************************
            //  end Building types
//*********************************************************************************************                

// ********************************************************************************************
            //  Widgets
//*********************************************************************************************                 
            $scope.data.heatWidgetList = [];
            $scope.data.hwWidgetList = [];
            $scope.data.cwWidgetList = [];
            $scope.data.elWidgetList = [];

            function getWidgets() {
//                            _testGetJson("/api/subscr/vcookie/widgets/list");
                var url = WIDGETS_URL;
                $http.get(url).then(function (resp) {
                    //console.log(resp.data);
                    $scope.data.heatWidgetList = [];
                    $scope.data.hwWidgetList = [];
                    $scope.data.cwWidgetList = [];
                    $scope.data.elWidgetList = [];
                    if (!angular.isArray(resp.data)) {
                        return false;
                    }
                    resp.data.forEach(function (elm) {
                        switch (elm.contServiceType) {
                        case "heat":
                            $scope.data.heatWidgetList.push(elm);
                            break;
                        case "hw":
                            $scope.data.hwWidgetList.push(elm);
                            break;
                        case "cw":
                            $scope.data.cwWidgetList.push(elm);
                            break;
                        case "el":
                            $scope.data.elWidgetList.push(elm);
                            break;
                        }

                    });

                    DEFAULT_INDICATOR_MODE.widgets = {};
                    if ($scope.data.heatWidgetList.length > 0) {
                        DEFAULT_INDICATOR_MODE.widgets.heat = $scope.data.heatWidgetList[0].widgetName;
                        $scope.data.heatWidgetList.some(function (heatw) {
                            if (heatw.isDefault === true) {
                                DEFAULT_INDICATOR_MODE.widgets.heat = heatw.widgetName;
                                return true;
                            }
                        });
                    }
                    if ($scope.data.hwWidgetList.length > 0) {
                        DEFAULT_INDICATOR_MODE.widgets.hw = $scope.data.hwWidgetList[0].widgetName;
                        $scope.data.hwWidgetList.some(function (hww) {
                            if (hww.isDefault === true) {
                                DEFAULT_INDICATOR_MODE.widgets.hw = hww.widgetName;
                                return true;
                            }
                        });
                    }
                    if ($scope.data.cwWidgetList.length > 0) {
                        DEFAULT_INDICATOR_MODE.widgets.cw = $scope.data.cwWidgetList[0].widgetName;
                        $scope.data.cwWidgetList.some(function (cww) {
                            if (cww.isDefault === true) {
                                DEFAULT_INDICATOR_MODE.widgets.cw = cww.widgetName;
                                return true;
                            }
                        });
                    }
                    if ($scope.data.elWidgetList.length > 0) {
                        DEFAULT_INDICATOR_MODE.widgets.el = $scope.data.elWidgetList[0].widgetName;
                        $scope.data.elWidgetList.some(function (elw) {
                            if (elw.isDefault === true) {
                                DEFAULT_INDICATOR_MODE.widgets.el = elw.widgetName;
                                return true;
                            }
                        });
                    }
                    getIndicatorModes();
//console.log(DEFAULT_INDICATOR_MODE);                        
                }, errorCallback);


            }
// ********************************************************************************************
            //  end Widgets
//*********************************************************************************************
                
// ********************************************************************************************
    //    Work with cont object passports
//*********************************************************************************************
            var passportRequestCanceller = null;
                
            $scope.objectCtrlSettings.dateFormat = "YYYY-MM-DD";//"DD.MM.YYYY"; //moment format
            $scope.objectCtrlSettings.dateFormatForDatepicker = "yy-mm-dd"; //jquery datepicker format
                
            $scope.data.currentDocument = {};
            $scope.data.documentTypes = energoPassportSvc.getDocumentTypes();
            
            $scope.isReadOnlyPassport = function (passport) {
                return mainSvc.isReadonly() || (!mainSvc.checkUndefinedNull(passport.id) && !passport.isActive);
            };
            
            $scope.openContObjectPassport = function (doc, object) {
                var objectParam = "new";
                if (!mainSvc.checkUndefinedNull(object) && !mainSvc.checkUndefinedNull(object.id)) {
                    objectParam = object.id;
                }
                var winName = "_blank";
                if (objectPassportCreationWindow !== null) {
                    winName = OBJECT_PASSPORT_CREATION_WINDOW_NAME;
                    objectPassportCreationWindow = null;
                }
                var url = "#/settings/object-passport/" + objectParam + "/" + doc.id;
                if (objectParam === "new") {
                    url = mainSvc.addParamToURL(url, "buildingType", $scope.currentObject.buildingType);
                    url = mainSvc.addParamToURL(url, "buildingTypeCategory", $scope.currentObject.buildingTypeCategory);
                    if (!mainSvc.checkUndefinedNull($scope.currentObject.buildingTypeCategory)) {
                        var catCaption = "";
                        $scope.data.buildingCategories.some(function (bcat) {
                            if (bcat.keyname === $scope.currentObject.buildingTypeCategory) {
                                catCaption = bcat.caption;
                            }
                        });
                        url = mainSvc.addParamToURL(url, "buildingTypeCategoryCaption", catCaption);
                    }
                }
                window.open(url, winName);
            };
                
//            $scope.openContObjectPassport_old = function (doc) {
//                window.open("#/documents/object-passport/" + doc.id + "?active=" + doc.isActive, '_blank');
//            };
                
            function successLoadPassportsCallback(resp) {
                $scope.objectCtrlSettings.isPassportsLoading = false;
                if (!angular.isArray(resp.data) || resp.data.length <= 0) {
                    //console.warn("Response from server is incorrect:", resp);
                    return false;
                }
                // find active passport
//                var tmp = resp.data,
//                    activePassport = resp.data[0];
//                tmp.forEach(function (passport) {
//                    if (passport.passportDate2 > activePassport.passportDate2) {
//                        activePassport = passport;
//                    } else if (passport.passportDate2 === activePassport.passportDate2) {
//                        if (passport.id > activePassport.id) {
//                            activePassport = passport;
//                        }
//                    }
//                });
                var activePassport = energoPassportSvc.findContObjectActivePassport(resp.data);
                activePassport.isActive = true;
                mainSvc.sortItemsBy(resp.data, "isActive");
                resp.data.reverse();
                $scope.data.currentContObjectPassports = resp.data;
            }
                
            function successSavePassportCallback(resp) {
                if (mainSvc.checkUndefinedNull(resp) || mainSvc.checkUndefinedNull(resp.data)) {
                    console.error("Incorrect response from server:");
                    console.error(resp);
                    return false;
                }
            }

            function successCreatePassportCallback(resp) {
                if (successSavePassportCallback(resp) === false) {
                    return false;
                }
                $('#showDocumentPropertiesModal').modal('hide');
                $scope.openContObjectPassport(resp.data);
            }
                
            function successCreatePassportCallbackFromTab(resp) {
                $scope.objectCtrlSettings.isDocumentSaving = false;
                $scope.objectCtrlSettings.isPassportCreating = false;
                if (successSavePassportCallback(resp) === false) {
                    return false;
                }
                
                var tmpPassportArr = angular.copy($scope.data.currentContObjectPassports);
                tmpPassportArr.forEach(function (pass) {
                    pass.isActive = false;
                });
                var newPass = angular.copy(resp.data);
                newPass.isActive = true;
                tmpPassportArr.unshift(newPass);
                
                $scope.data.currentContObjectPassports = tmpPassportArr;
                
                $scope.openContObjectPassport(resp.data);
            }
                            
            function successUpdatePassportCallbackFromTab(resp) {
                $scope.objectCtrlSettings.isDocumentSaving = false;
                if (successSavePassportCallback(resp) === false) {
                    return false;
                }
                
//                var tmpPassportArr = angular.copy($scope.data.currentContObjectPassports);
//                tmpPassportArr.forEach(function (pass) {
//                    pass.isActive = false;
//                }); 
//                var newPass = angular.copy(resp.data);
//                newPass.isActive = true;
//                tmpPassportArr.unshift(newPass);
//                
//                $scope.data.currentContObjectPassports = tmpPassportArr;
                
                notificationFactory.success();
                //find and update doc in doc array
                var updatingItem = mainSvc.findItemBy($scope.data.currentContObjectPassports, "id", resp.data.id);
                var docIndexAtArr = $scope.data.currentContObjectPassports.indexOf(updatingItem);
                if (docIndexAtArr > -1) {
                    var newData = angular.copy(resp.data);
                    $scope.data.currentContObjectPassports[docIndexAtArr].passportName = newData.passportName;
                    $scope.data.currentContObjectPassports[docIndexAtArr].description = newData.description;
                    $scope.data.currentContObjectPassports[docIndexAtArr].docDateFormatted = moment(newData.passportDate2).format($scope.objectCtrlSettings.dateFormat);
                }
            }

            function successUpdatePassportCallback(resp) {
                if (successSavePassportCallback(resp) === false) {
                    return false;
                }
                notificationFactory.success();
                //find and update doc in doc array
                var updatingItem = mainSvc.findItemBy($scope.data.currentContObjectPassports, "id", resp.data.id);
                var docIndexAtArr = $scope.data.currentContObjectPassports.indexOf(updatingItem);
                if (docIndexAtArr > -1) {
                    $scope.data.currentContObjectPassports[docIndexAtArr] = angular.copy(resp.data);
                }
            }
                
            function loadContObjectPassports(contObject) {
                $scope.data.currentContObjectPassports = [];
                if (passportRequestCanceller !== null) {
                    passportRequestCanceller.resolve();
                }
                
                $scope.objectCtrlSettings.isPassportsLoading = true;
                
                passportRequestCanceller = $q.defer();
                var httpOptions = {
                    timeout: passportRequestCanceller.promise
                };
                
                energoPassportSvc.loadContObjectPassports(contObject, httpOptions)
                    .then(successLoadPassportsCallback, errorCallback);
                
            }
                
            $scope.createContObjectPassportInit = function (object) {
                $scope.data.currentDocument = {};
                $scope.data.currentDocument.parentObject = object;
                $scope.data.currentDocument.type = $scope.data.documentTypes.OBJECT_PASSPORT.keyname;
//                $scope.data.currentDocument.templateKeyname = $scope.data.energyDeclarationForms[0].templateKeyname;
                $('#showDocumentPropertiesModal').modal();
                //$('#editEnergoPassportModal').modal();
            };

            $scope.editContObjectPassportInit = function (passport, object) {
                $scope.data.currentDocument = angular.copy(passport);
                $scope.data.currentDocument.parentObject = object;
                $scope.data.currentDocument.type = $scope.data.documentTypes.OBJECT_PASSPORT.keyname;
                $scope.data.currentDocument.docDateFormatted = moment($scope.data.currentDocument.passportDate2).format($scope.objectCtrlSettings.dateFormat);
                $('#showDocumentPropertiesModal').modal();
            };
                
            $scope.createContObjectPassportFromTabInit = function (object) {
                $scope.data.currentDocument = {};
                $scope.data.currentDocument.parentObject = object;
                $scope.data.currentDocument.type = $scope.data.documentTypes.OBJECT_PASSPORT.keyname;
                
                $scope.data.currentContObjectPassports.forEach(function (pass) {
                    pass.isPassportEditing = false;
                });
                $scope.objectCtrlSettings.isPassportCreating = true;
                
                var viewDateformat = mainSvc.getDetepickerSettingsFullView();
                viewDateformat.dateFormat = DATE_FORMAT_FOR_DATEPICKER;
                $('#inputEnergyDocDate').datepicker(viewDateformat);

            };
                            
            $scope.editContObjectPassportFromTabInit = function (passport, object) {
                
                $scope.data.currentContObjectPassports.forEach(function (pass) {
                    if (passport.id !== pass.id) {
                        pass.isPassportEditing = false;
                    }
                });
                
                passport.isPassportEditing = !passport.isPassportEditing;
                $scope.objectCtrlSettings.isPassportCreating = false;
                
                $scope.data.currentDocument = angular.copy(passport);
                $scope.data.currentDocument.parentObject = object;
                $scope.data.currentDocument.type = $scope.data.documentTypes.OBJECT_PASSPORT.keyname;
                $scope.data.currentDocument.docDateFormatted = moment($scope.data.currentDocument.passportDate2).format($scope.objectCtrlSettings.dateFormat);
            };
                
            $scope.cancelCreateContObjectPassportFromTab = function (object) {
                $scope.data.currentDocument = {};
                $scope.objectCtrlSettings.isPassportCreating = false;
            };
                
            $scope.cancelEditContObjectPassportFromTab = function (passport) {
                $scope.data.currentDocument = {};
                passport.isPassportEditing = false;
            };
                
            function checkDoc(doc) {
                var checkFlag = true;
                if (mainSvc.checkUndefinedNull(doc.passportName) || $scope.emptyString(doc.passportName)) {
                    notificationFactory.errorInfo("Ошибка", "Не задано название для документа");
                    checkFlag = false;
                }
                if (mainSvc.checkUndefinedNull(doc.type)) {
                    notificationFactory.errorInfo("Ошибка", "Не задан тип для документа");
                    checkFlag = false;
                }
                if (mainSvc.checkUndefinedNull(doc.docDateFormatted) || $scope.emptyString(doc.docDateFormatted)) {
                    notificationFactory.errorInfo("Ошибка", "Не задана дата документа");
                    checkFlag = false;
                }
                return checkFlag;
            }

            $scope.saveDocument = function (doc) {
        //console.log(doc);
                if (checkDoc(doc) === false) {
                    return false;
                }
                //prepare doc date 
                var tmpDate = moment(doc.docDateFormatted, $scope.objectCtrlSettings.dateFormat);
        //console.log(tmpDate);        
        //console.log([tmpDate.year(), tmpDate.month() + 1, tmpDate.date()]);        
        //console.log(doc.passportDate);
                doc.passportDate = [tmpDate.year(), tmpDate.month() + 1, tmpDate.date()];
//console.log(doc);
//console.log(doc.parentObject);   
//return;                
                if (mainSvc.checkUndefinedNull(doc.id)) {
                    
                    objectPassportCreationWindow = window.open("", OBJECT_PASSPORT_CREATION_WINDOW_NAME);
//                    return;
                    energoPassportSvc.createContObjectPassport(doc, doc.parentObject.id)
                        .then(successCreatePassportCallback, errorCallback);
            //        $location.path("/documents/energo-passport/new");
                } else {
                    energoPassportSvc.updateContObjectPassport(doc, doc.parentObject.id)
                        .then(successUpdatePassportCallback, errorCallback);
                }
            };
                
            $scope.saveDocumentFromTab = function (doc) {
//        console.log(doc);
                $scope.objectCtrlSettings.isDocumentSaving = true;
                if (checkDoc(doc) === false) {
                    return false;
                }
                //prepare doc date 
                var tmpDate = moment(doc.docDateFormatted, $scope.objectCtrlSettings.dateFormat);
                doc.passportDate = [tmpDate.year(), tmpDate.month() + 1, tmpDate.date()];
   
                if (mainSvc.checkUndefinedNull(doc.id)) {
                    objectPassportCreationWindow = window.open("", OBJECT_PASSPORT_CREATION_WINDOW_NAME);
                    energoPassportSvc.createContObjectPassport(doc, doc.parentObject.id)
                        .then(successCreatePassportCallbackFromTab, errorCallback);
                } else {
                    energoPassportSvc.updateContObjectPassport(doc, doc.parentObject.id)
                        .then(successUpdatePassportCallbackFromTab, errorCallback);
                }
            };
// ********************************************************************************************
    //    End work with cont object passports
//*********************************************************************************************                

            $('#showObjOptionModal').on('shown.bs.modal', function () {
                $('#inputContObjectName').focus();
                $('#inputNumOfStories').inputmask('integer', {min: 1, max: 200});
            });

            $scope.objectCtrlSettings.objectModalWindowTabs = [
                {
                    name: "main_object_properties_tab",
                    tabpanel: "main_object_properties"
                },
                {
                    name: "extra_object_properties_tab",
                    tabpanel: "extra_object_properties"
                },
                {
                    name: "object_passports_tab",
                    tabpanel: "object_passports"
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

            $('#showObjOptionModal').on('hidden.bs.modal', function () {
                $scope.currentObject.isSaving = false;
                $scope.objectCtrlSettings.isPassportCreating = false;
                $scope.currentSug = null;
                setActiveObjectPropertiesTab("main_object_properties_tab");
            });

            $scope.$on('objectSvc:deviceMetadataMeasuresLoaded', function () {
                measureUnits = objectSvc.getDeviceMetadataMeasures();
            });

            var initCtrl = function () {
                getWidgets();
                                    //if tree is off
                if ($scope.objectCtrlSettings.isTreeView == false) {
                    getObjectsData();
                } else {
                //if tree is on                         
                    objectSvc.loadDefaultTreeSetting().then(successLoadTreeSetting, errorCallback);
                }
                measureUnits = objectSvc.getDeviceMetadataMeasures();
            };

            initCtrl();
        }]);