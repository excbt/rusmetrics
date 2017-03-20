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
        'infinite-scroll',
        'angularFileUpload',
        'leaflet-directive',
        'ui.select',
        'ui.mask',
        'angularWidget',
        'chart.js'
    ]);

//routing config
app.config(['$routeProvider', function ($routeProvider) {
//console.log("Run routeProviderConfig");    
    $routeProvider
        .when('/', {
            templateUrl: 'views/objects_edit.html',
            controller: 'ObjectsCtrl',
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        })
        .when('/objects/list', {
            templateUrl: 'views/objects_edit.html',
            controller: 'ObjectsCtrl',
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        })
        .when('/objects/demo-map', {
            templateUrl: 'views/objects_map.html',
            controller: 'ObjectsMapCtrl',
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        })
        .when('/notices/list', {
            templateUrl: 'views/notice.html',
            controller: 'NoticeCtrl',
            reloadOnSearch: false,
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        })
        .when('/notices/monitor', {
            templateUrl: 'views/monitor.html',
            controller: 'MonitorCtrl',
            reloadOnSearch: false,
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        })
        .when('/notices/monitor_map', {
            templateUrl: 'views/monitor_map.html',
            controller: 'MonitorMapCtrl',
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        })
        .when('/private', {
            templateUrl: 'views/private_office.html',
            controller: 'ObjectsCtrl'
        })
        .when('/objects_edit', {
            templateUrl: 'views/objects_edit.html',
            controller: 'ObjectsCtrl',
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        })
        .when('/private/objects_list', {
            templateUrl: 'views/objects_list.html',
            controller: 'ObjectsCtrl'
        })
        .when('/objects123/indicators', {
            templateUrl: 'views/indicators.html',
            controller: 'MainCtrl',
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        })
        .when('/private/directories', {
            templateUrl: 'views/directories.html',
            controller: 'DirectoryCtrl'
        })
        .when('/reports', {
            templateUrl: 'views/reports_with_cst.html',
            controller: 'ReportsCtrl'
        })
        .when('/reports/commercial_report', {
            templateUrl: 'views/commercial_report.html',
            controller: 'MainCtrl'
        })
        .when('/objects/indicators', {
            templateUrl: 'views/indicators.html',
            controller: 'IndicatorsCtrl',
            reloadOnSearch: false,
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        })
        .when('/objects/indicator-electricity', {
            templateUrl: 'views/indicator-electricity.html',
            controller: 'ElectricityCtrl',
            reloadOnSearch: false,
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        })
        .when('/objects/impulse-indicators', {
            templateUrl: 'views/impulse-indicators.html',
            controller: 'ImpulseIndicatorsCtrl',
            reloadOnSearch: false,
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        })
        .when('/settings/tariffs', {
            templateUrl: 'views/tariffs.html',
            controller: 'TariffsCtrl'
        })
//      .when('/settings/reports', {
//        templateUrl: 'views/report_settings.html',
//        controller: 'ReportSettingsCtrl'
//      })
        .when('/settings/paramsets', {
            templateUrl: 'views/param_sets.html',
            controller: 'ParamSetsCtrl'
        })
        .when('/settings/delivery', {
            templateUrl: 'views/delivery.html',
            controller: 'DlvrCtrl'
        })
        .when('/settings/contacts', {
            templateUrl: 'views/contacts.html',
            controller: 'ContactsCtrl'
        })
        .when('/settings/object_groups', {
            templateUrl: 'views/object_groups.html',
            controller: 'ObjectGroupsCtrl'
        })
        .when('/settings/users', {
            templateUrl: 'views/settings-users.html',
            controller: 'SettingsUsersCtrl'
        })
        .when('/settings/management_services', {
            templateUrl: 'views/management-services.html',
            controller: 'ManagementServicesCtrl'
        })
        .when('/settings/notices', {
            templateUrl: 'views/settings-notices.html',
            controller: 'SettingsNoticesCtrl'
        })
        .when('/settings/tenants', {
            templateUrl: 'views/settings-tenants.html',
            controller: 'SettingsTenantsCtrl'
        })
        .when('/settings/object_trees', {
            templateUrl: 'views/settings-object-trees.html',
            controller: 'SettingsObjectTreesCtrl'
        })
        .when('/settings/program', {
            templateUrl: 'views/settings-program.html',
            controller: 'SettingsProgramCtrl'
        })
        .when('/settings/meter-period-setting', {
            templateUrl: 'views/settings-meter-period-setting.html',
            controller: 'SettingsMeterPeriodSettingCtrl'
        })
        .when('/settings/object-view', {
            templateUrl: 'views/settings-object-view.html',
            controller: 'SettingsObjectViewCtrl'
        })
        .when('/settings/about-program', {
            templateUrl: 'views/about-program.html',
            controller: 'AboutProgramCtrl',
            resolve: {
                allow: ['mainSvc', '$q', '$location', function (mainSvc, $q, $location) {
                    var deferred = $q.defer();
                    if (mainSvc.getViewSystemInfo() === true) {
                        deferred.resolve("System info is allow.");
                    } else {
                        $location.path("/");
                        deferred.reject("Access denied!");
                    }
                    return deferred.promise;
                }]
            }
        })
        .when('/management/objects', {
            templateUrl: 'views/management-rma-objects.html',
            controller: 'MngmtObjectsCtrl',
            resolve: {
                rsoOrgs: ['objectSvc', function (objectSvc) {
                    return objectSvc.getRsoOrganizations();
                }],
                servTypes: ['objectSvc', function (objectSvc) {
                    return objectSvc.getServiceTypes();
                }]
            }
        })
        .when('/management/clients', {
            templateUrl: 'views/management-rma-clients.html',
            controller: 'MngmtClientsCtrl'
        })
        .when('/management/datasources', {
            templateUrl: 'views/management-rma-data-sources.html',
            controller: 'MngmtDatasourcesCtrl'
        })
        .when('/management/devices', {
            templateUrl: 'views/management-rma-devices.html',
            controller: 'MngmtDevicesCtrl'
        })
        .when('/management/users', {
            templateUrl: 'views/management-rma-users.html',
            controller: 'MngmtUsersCtrl'
        })
        .when('/management/price', {
            templateUrl: 'views/management-rma-price.html',
            controller: 'MngmtPriceCtrl'
        })
        .when('/management/organizations', {
            templateUrl: 'views/management-rma-organizations.html',
            controller: 'MngmtOrganizationsCtrl'
        })
        .when('/management/temp-sch', {
            templateUrl: 'views/management-rma-temp-sch.html',
            controller: 'TempSchCtrl'
        })
        .when('/management/modems', {
            templateUrl: 'views/management-rma-modems.html',
            controller: 'MngmtModemsCtrl'
        })
        .when('/management/device-models', {
            templateUrl: 'views/management-rma-device-models.html',
            controller: 'MngmtDeviceModelsCtrl'
        })
        .when('/log/session-log', {
            templateUrl: 'views/log-view-resizable-paged.html',
            controller: 'LogViewCtrl'
        })
        .when('/log/sms-log', {
            templateUrl: 'views/log-sms.html',
            controller: 'LogSmsCtrl'
        })
        .when('/test', {
            templateUrl: 'views/test.html',
            controller: 'testCtrl'
        })
        .otherwise({
            redirectTo: '/'
        });
}]);

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
    widgetsProvider.setManifestGenerator(function () {
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

//        console.log("$routeChangeStart");
        var checkPromise = securityCheck.isAuthenficated();
        $q.all([checkPromise]).then(function (result) {
            //console.log("isAuthenficated result: " + JSON.stringify(result) + "   " + result);
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