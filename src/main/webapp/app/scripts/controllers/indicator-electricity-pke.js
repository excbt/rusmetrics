/*jslint node: true, eqeq: true*/
/*global angular, moment*/
'use strict';
var app = angular.module('portalNMC');
app.controller('ElectricityPkeCtrl', ['$scope', '$http', 'indicatorSvc', 'mainSvc', '$location', '$cookies', '$rootScope', '$filter', 'notificationFactory', '$window', '$timeout', 'objectSvc', function ($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope, $filter, notificationFactory, $window, $timeout, objectSvc) {
//console.log("Run ElectricityPkeCtrl.");
    $scope.electroKind = "Pke";
    
    $scope.pkeTypes = [];
    $scope.pkeData = [];
    
    $scope.states = {};
    $scope.states.isSelectedAllPkeTypes = true;
    $scope.states.isSelectedAllPkeTypesInWindow = true;
        
    $scope.defaultFilterCaption = "Все";
    $scope.selectedPkeTypes_list = {
        caption: $scope.defaultFilterCaption
    };
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.loading = true;
    $scope.ctrlSettings.systemDateFormat = "YYYY-MM-DD";
    
    $scope.columns = [
        {
            header : "Дата начала",
            headerClass : "col-xs-3 col-md-3 nmc-text-align-center",
            dataClass : "col-xs-3 col-md-3 nmc-text-align-right",
            fieldName: "warnStartDateStr",
            type: "string",
            date: true
        },
        {
            header : "Дата окончания",
            headerClass : "col-xs-3 col-md-3 nmc-text-align-center",
            dataClass : "col-xs-3 col-md-3 nmc-text-align-right",
            fieldName: "warnEndDateStr",
            type: "string",
            date: true
        },
        {
            header : "Значение предела",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data nmc-text-align-center",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data nmc-text-align-right",
            fieldName: "warnValue"
        },
        {
            header : "Ед. изм.",
            headerClass : "col-xs-1 col-md-1 nmc-view-digital-data nmc-text-align-center",
            dataClass : "col-xs-1 col-md-1 nmc-view-digital-data nmc-text-align-left",
            fieldName: "pkeMeasureUnit"
        },
        {
            header : "Тип",
            headerClass : "col-xs-4 col-md-4 nmc-text-align-center",
            dataClass : "col-xs-4 col-md-4 nmc-text-align-right",
            fieldName: "pkeTypeCaption"
        }
    ];
    // sort settings
    $scope.ctrlSettings.orderBy = {"field": $scope.columns[0].fieldName, "asc": true};
    
    $scope.setOrderBy = function (field) {
        var asc = $scope.ctrlSettings.orderBy.field === field ? !$scope.ctrlSettings.orderBy.asc : true;
        $scope.ctrlSettings.orderBy = { field: field, asc: asc };
    };
        
    var intervalSettings = {};
    $scope.indicatorPkeDates = {
        startDate : moment().subtract(6, 'days').startOf('day'),
        endDate : moment().endOf('day')
    };
//    intervalSettings.minDate = moment().subtract(29, 'days').startOf('day');    
    $scope.dateRangeOptsPkeRu = mainSvc.getDateRangeOptions("indicator-ru", intervalSettings);
    $scope.dateRangeOptsPkeRu.startDate = moment().subtract(6, 'days').startOf('day');
    $scope.dateRangeOptsPkeRu.endDate = moment().endOf('day');
    $scope.dateRangeOptsPkeRu.dateLimit = {"months": 1}; //set date range limit with 1 month
//console.log($scope.dateRangeOptsPkeRu);    
    
    ///api/subscr/66948436/serviceElProfile/30min/159919982
    //{beginDate=[2015-12-01], endDate=[2015-12-31]}
    var apiSubscUrl = "../api/subscr/";
    var timeDetailType = "abs";
    var viewMode = "serviceElPke";//deviceObjects/pke/%d/warn
    var dataUrl = apiSubscUrl + "deviceObjects/pke/byContZPoint/" + $scope.contZPoint + "/warn";
    var pkeTypesUrl = apiSubscUrl + "deviceObjects/pke/types";
    
    
    var errorCallback = function (e) {
        var errorObj = mainSvc.errorCallbackHandler(e);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };
    
    var getPke = function (pkeTypes) {
        $scope.ctrlSettings.loading = true;
        var url = dataUrl;
        var params = {};
        //add time interval
        url += "/?beginDate=" + moment($scope.indicatorPkeDates.startDate).format($scope.ctrlSettings.systemDateFormat) + "&endDate=" + moment($scope.indicatorPkeDates.endDate).format($scope.ctrlSettings.systemDateFormat);
        //add pkeTypes
        if (!mainSvc.checkUndefinedNull(pkeTypes) && angular.isArray(pkeTypes)) {
            var pkeTypeKeyNames = pkeTypes.map(function (elem) { return elem.keyname; });
            params = { pkeTypeKeynames: pkeTypeKeyNames };
        }
//        $http.get(url)
        $http({
            method: "GET",
            url: url,
            params: params
        })
            .then(function (resp) {
                $scope.pkeData = angular.copy(resp.data);
                $scope.pkeData.forEach(function (elem) {
                    $scope.pkeTypes.some(function (type) {
                        if (elem.deviceObjectPkeTypeKeyname == type.keyname) {
                            elem.pkeTypeCaption = type.caption;
                            $scope.measureUnits.all.some(function (mu) {
                                if (mu.keyname === type.pkeMeasureUnit) {
                                    elem.pkeMeasureUnit = mu.caption;
                                    return true;
                                }
                            });
                            return true;
                        }
                    });


                });
                $scope.ctrlSettings.loading = false;
                $timeout(function () {
                    $scope.setScoreStyles();
                }, 10);
            }, errorCallback);
    };
    
    var getPkeTypes = function () {
        $http.get(pkeTypesUrl)
            .then(function (resp) {
                $scope.pkeTypes = angular.copy(resp.data);
                $scope.pkeTypesInWindow = angular.copy(resp.data);
                getPke();
            }, errorCallback);
    };
    
    getPkeTypes();
    
    var closeFilter = function () {
                //close filter list
        var btnGroup = document.getElementsByClassName("btn-group open");
        if (!mainSvc.checkUndefinedNull(btnGroup) && (btnGroup.length != 0)) {
            btnGroup[0].classList.remove("open");
        }
        $scope.states.isSelectElement = false;
    };
    
    $scope.getDataWithSelectedTypes = function () {
        closeFilter();
        if (!angular.isArray($scope.pkeTypesInWindow)) {
            getPke();
        }
        $scope.pkeTypes = angular.copy($scope.pkeTypesInWindow);
        $scope.states.isSelectedAllPkeTypes = angular.copy($scope.states.isSelectedAllPkeTypesInWindow);
        var selectedTypes = [];
        $scope.pkeTypes.forEach(function (elem) {
            if (elem.selected == true) {
                selectedTypes.push(elem);
            }
        });
        if (selectedTypes.length == 0) {
            $scope.selectedPkeTypes_list.caption = $scope.defaultFilterCaption;
        } else {
            $scope.selectedPkeTypes_list.caption = selectedTypes.length;
        }
        getPke(selectedTypes);
    };
    
    $scope.selectPkeTypesClick = function () {
        $scope.states.isSelectElement = false;
        //Create the copy of categories
        $scope.pkeTypesInWindow = angular.copy($scope.pkeTypes);
        $scope.states.isSelectedAllPkeTypesInWindow = angular.copy($scope.states.isSelectedAllPkeTypes);
    };
    
    $scope.selectAllTypes = function () {
        var filteredTypes = $filter('filter')($scope.pkeTypes, $scope.pkeTypesFilter);
        filteredTypes.forEach(function (elem) {
            elem.selected = $scope.isSelectedAllTypes;
        });
        $scope.getDataWithSelectedTypes();
    };
    
    $scope.selectAllElements = function (elements) {
        $scope.states.isSelectElement = true;
        elements.forEach(function (elem) {
            elem.selected = false;
        });
    };
        
    $scope.selectElement = function (flagName) {
        $scope.states.isSelectElement = true;
        $scope.states[flagName] = false;
        return false;
    };
    
    $scope.isFilterApplyDisabled = function (checkElements, checkFlag) {
        if (!angular.isArray(checkElements) || mainSvc.checkUndefinedNull(checkFlag)) {
            return false;
        }
        if (checkFlag == true) {
            return false;
        }
        return !checkElements.some(function (elem) {
            if (elem.selected == true) {
                return true;
            }
        });
    };
    
    $scope.clearPkeTypeFilter = function () {
        $scope.pkeTypesFilter = "";
        $scope.isSelectedAllTypes = false;
        $scope.pkeTypesInWindow.forEach(function (elem) {
            elem.selected = false;
        });
        $scope.states.isSelectedAllPkeTypesInWindow = true;
        $scope.getDataWithSelectedTypes();
    };
    
    $scope.$watch('indicatorPkeDates', function (newDates, oldDates) {
//console.log(newDates);
//console.log(oldDates);        
//        if (newDates.startDate == oldDates)
        $scope.getDataWithSelectedTypes();
    });
    
    $scope.setScoreStyles = function () {
        //ровняем таблицу, если появляются полосы прокрутки
        var tableHeader = document.getElementById("pkeTableHeader");
        var tableDiv = document.getElementById("divPkeTable");
        if (!mainSvc.checkUndefinedNull(tableDiv) && !mainSvc.checkUndefinedNull(tableHeader)) {
            if (tableHeader.offsetWidth == 0) {
                return "Pke. tableHeader.offsetWidth == 0";
            }
            if (tableDiv.offsetWidth > tableDiv.clientWidth) {
                tableDiv.style.width = tableHeader.offsetWidth + 17 + 'px';
            } else {
                tableDiv.style.width = tableHeader.offsetWidth + 'px';
            }
        }
    };
        //listen window resize
    var wind = angular.element($window);
    var windowResize = function () {
        if (angular.isDefined($scope.setScoreStyles)) {
            $scope.setScoreStyles();
        }
        $scope.$apply();
    };
    wind.bind('resize', windowResize);
        
    $scope.$on('$destroy', function () {
        wind.unbind('resize', windowResize);
    });
    
    $scope.onTableLoad = function () {
        $scope.setScoreStyles();
    };
    
    $timeout(function () {
        $scope.setScoreStyles();
    }, 500);
    
    //controller initialization
    $scope.initCtrl = function () {
        $scope.pkeTypesInWindow = angular.copy($scope.pkeTypes);
        $scope.measureUnits = objectSvc.getDeviceMetadataMeasures();
    };
    $scope.initCtrl();
}]);