/*jslint node: true, eqeq: true*/
/*global angular*/
'use strict';

/**
 * @ngdoc overview
 * @name portalNMC
 * @description
 * # portalNMC
 *
 * Main module of the application.
 */

var app = angular
    .module('portalNMC', [
        'ngAnimate',
        'ngCookies',
        'ngResource',
        'ngRoute',
        'ngSanitize',
        'ngTouch',
        'ui.tree',
        'daterangepicker',
        'angularUtils.directives.dirPagination',
        'ngIdle',
        
        'angularFileUpload',
        'leaflet-directive',
        'ui.select',
        'ui.mask',
        'angularWidget',
        'chart.js',
        'ui.layout',
        'ui.router'
    ]);

app.constant("APP_LABEL", "Русметрикс");



//app.config(['dateRangePickerConfig', function(dateRangePickerConfig) {
//    dateRangePickerConfig.separator = ' по ';
//    dateRangePickerConfig.format= 'DD.MM.YYYY';
//}]);

//config for ngIdle
app.config(['KeepaliveProvider', 'IdleProvider', function (KeepaliveProvider, IdleProvider) {
//  IdleProvider.idle(3600); //idle time in seconds
//  IdleProvider.timeout(30); //time out in seconds
//  KeepaliveProvider.interval(10);//keepAlive - not used
}]);
//start Idle service
//app.run(['Idle', function(Idle) {
//  Idle.watch();
//}]);

//configure $log service
app.config(['$logProvider', function ($logProvider) {
    $logProvider.debugEnabled(true);
}]);

app.config(['uiMask.ConfigProvider', function (uiMaskConfigProvider) {
    uiMaskConfigProvider.maskDefinitions({'a': /[a-z]/, 'A': /[A-Z]/, '*': /[a-zA-Z0-9]/, '9': /\d/, 'M': /[A-F0-9]/});
}]);

//config widget
app.config(['widgetsProvider', function initializemanifestGenerator(widgetsProvider) {
    widgetsProvider.setManifestGenerator(function resourceWidgetManufest() {
        return function (name) {
            return {
                module: name + 'Widget',
                html: 'widgets/' + name + '/' + name + '.html',
                files: [
                    'widgets/' + name + '/' + name + '.js',
                    'widgets/' + name + '/' + name + '.css'
                ]
            };
        };
    });
    
    widgetsProvider.setManifestGenerator(function treeNotificationsDiagramWidgetManifest() {
        return function (name) {
            return name === 'treeNotificationsDiagram' && {
                module: name + 'Widget',
                html: 'widgets/' + name + '/' + name + '.html',
                files: [
                    'widgets/' + name + '/' + name + '.js',
                    'widgets/' + name + '/' + name + 'Service.js',
                    'widgets/' + name + '/' + name + '.css'
                ]
            };
        };
    });
}]);

//app.run(['objectSvc', 'mainSvc', function(objectSvc, mainSvc){
//console.log("Run main, object and monitor services.");  
//    var mainSvcInit = mainSvc.getUserServicesPermissions();    
//    var objectSvcInit = objectSvc.promise;
//}]);

app.run(['objectSvc', 'monitorSvc', 'mainSvc', 'reportSvc', function (objectSvc, monitorSvc, mainSvc, reportSvc) {
//console.log("Run main, object and monitor services.");  
    var mainSvcInit = mainSvc.getUserServicesPermissions();
    var monitorSvcInit = monitorSvc.getAllMonitorObjects();
    var objectSvcInit = objectSvc.promise;
}]);

/********************************************************************************************************
* Define filters (описываем фильтры)
**********************************************************************************************************
*/
app.filter('columnFilter', function () {
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
});

app.filter('propsFilter', function () {
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
});

// Filter for zpoint metadata editor
app.filter('isIntegrators', function () {
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
});

app.filter('notEmptyCategoriesByContServiceType', ['$filter', function ($filter) {
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
}]);

app.filter('notEmptyContServiceTypesByCategory', ['$filter', function ($filter) {
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
}]);


//************************************************************************************
// Helper directives
// **********************************************************************************
app.directive('focusMe', ['$timeout', '$parse', function ($timeout, $parse) {
    return {
    //scope: true,   // optionally create a child scope
        link: function (scope, element, attrs) {
            var model = $parse(attrs.focusMe);
            scope.$watch(model, function (value) {
//console.log('value=',value);
//console.log(element);          
                if (value === true) {
                    $timeout(function () {
//console.log(element[0]);
//console.log(element.focus());              
                        element.focus();
                    }, 100);
                }
            });
      // to address @blesh's comment, set attribute value to 'false'
      // on blur event:
            element.bind('blur', function () {
                console.log('blur');
                scope.$apply(model.assign(scope, false));
            });
        }
    };
}]);


app.run(["$rootScope", "$location", "$q", "securityCheck", function ($rootScope, $location, $q, securityCheck) {

    $rootScope.$on("$routeChangeStart", function (evt, to, from) {
//console.log(evt);
//console.log(to);        
//console.log(from);        
//        console.log("$routeChangeStart");
        var checkPromise = securityCheck.isAuthenficated();
//        $q.all([checkPromise]).then(function (result) {
        securityCheck.isAuthenficated().then(function (result) {
//            console.log("isAuthenficated result: " + JSON.stringify(result) + "   " + result);
            if (result == false) {
                var url = "../login";
                window.location.replace(url);
            }
        }, function (e) {
            console.log(e);
            var url = "../login";
            window.location.replace(url);
        });
    });
}]);