/*global angular, console*/
/*jslint eqeq: true*/
(function () {
    'use strict';
    /********************************************************************************************************
    * Define filters (описываем фильтры)
    **********************************************************************************************************
    */
    angular.module('portalNMC')
        .filter('columnFilter', columnFilter)
        .filter('propsFilter', propsFilter)
        .filter('isIntegrators', isIntegrators)
        .filter('notEmptyCategoriesByContServiceType', notEmptyCategoriesByContServiceType)
        .filter('notEmptyContServiceTypesByCategory', notEmptyContServiceTypesByCategory);
    
    
    function columnFilter() {
        return function (items, props) {
            var props = [props];
            var out = [];
            if (angular.isArray(items)) {
                items.forEach(function (item) {
                    var itemMatches = false,
                        keys = Object.keys(props),
                        i;
                    for (i = 0; i < keys.length; i += 1) {
                        var prop = Number(keys[i]);
                        var text = props[prop].toLowerCase();
                        if (angular.isDefined(item[text]) && (item[text].toString().toLowerCase().indexOf(text) !== -1)) {
                            itemMatches = true;
                            break;
                        }
                    }

                    if (itemMatches) {
                        out.push(item);
                    }
                });
            } else {
          // Let the output be the input untouched
                out = items;
            }
            return out;
        };
    }

    function propsFilter() {
        return function (items, props) {
            var out = [];
            if (angular.isArray(items)) {
                items.forEach(function (item) {
                    var itemMatches = false,
                        keys = Object.keys(props),
                        i;
                    for (i = 0; i < keys.length; i += 1) {
                        var prop = keys[i];
                        var text = props[prop].toLowerCase();
        //console.log(prop);                        
        //console.log(item);            
                        if (item[prop].toString().toLowerCase().indexOf(text) !== -1) {
                            itemMatches = true;
                            break;
                        }
                    }

                    if (itemMatches) {
                        out.push(item);
                    }
                });
            } else {
          // Let the output be the input untouched
                out = items;
            }

            return out;
        };
    }

    // Filter for zpoint metadata editor
    function isIntegrators() {
        return function (items, propVal) {
            var out = [];
            if (angular.isArray(items)) {
                items.forEach(function (item) {
                    if (angular.isUndefined(item.isIntegrator) || item.isIntegrator == null || item.isIntegrator == propVal) {
                        out.push(item);
                    }
                });
            } else {
          // Let the output be the input untouched
                out = items;
            }

            return out;
        };
    }

    notEmptyCategoriesByContServiceType.$inject = ['$filter'];
    function notEmptyCategoriesByContServiceType($filter) {
        return function (items, props) {
    //console.log(items);        
            var out = [];
            if (angular.isArray(items)) {
                items.forEach(function (item) {
                    var filteredReportTypes = $filter('serviceTypesFilter')(item.reportTypes, props);
                    if (filteredReportTypes.length == 0) {
                        return;
                    }
                    var checkReportTypes = false;
                    filteredReportTypes.some(function (rt) {
                        if (rt.paramsetsCount && rt.paramsetsCount == rt.checkedParamsetsCount) {
                            checkReportTypes = true;
                            return true;
                        }
                    });
                    if (checkReportTypes == true) {
                        out.push(item);
                    }
                });
            } else {
                out = items;
            }
            return out;
        };
    }

    notEmptyContServiceTypesByCategory.$inject = ['$filter'];
    function notEmptyContServiceTypesByCategory($filter) {
        return function (items, props) {
    //console.log(items);        
            var out = [];
            if (angular.isArray(items)) {
                items.forEach(function (item) {
    //console.log(props);                
    //console.log(props.reportTypes);   
    //console.log(item);           
                    var filteredReportTypes = [];
                    if (angular.isDefined(props)) {
                        filteredReportTypes = $filter('serviceTypesFilter')(props.reportTypes, item);
                    }
                    if (filteredReportTypes.length === 0) {
                        return;
                    }
                    var checkReportTypes = false;
                    filteredReportTypes.some(function (rt) {
                        if (rt.paramsetsCount && rt.paramsetsCount === rt.checkedParamsetsCount) {
                            checkReportTypes = true;
                            return true;
                        }
                    });
                    if (checkReportTypes === true) {
                        out.push(item);
                    }
                });
            } else {
                out = items;
            }
            return out;
        };
    }
}());