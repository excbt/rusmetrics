/*jslint node: true*/
/*global angular*/

'use strict';
var app = angular.module('portalNMC');
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
    
        .when('/objects/newlist', {
            templateUrl: 'views/objects-newlist.html',
            controller: 'NewObjectsCtrl',
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        })
    
        .when('/documents/energo-passports', {
            templateUrl: 'views/documents-energo-passports.html',
            controller: 'documentsEnergoPassportsCtrl'
        })
        .when('/documents/energo-passport/:param', {
            reloadOnSearch: false,
            templateUrl: 'views/documents-energo-passport.html',
            controller: 'documentsEnergoPassportCtrl'
        })
/*        .when('/documents/energo-passports1', {
            templateUrl: 'views/documents-energo-passports1.html',
            controller: 'documentsEnergoPassportsCtrl1'
        })
        .when('/documents/energo-passports2', {
            templateUrl: 'views/documents-energo-passports2.html',
            controller: 'documentsEnergoPassportsCtrl2'
        })
*/
//        .when('/documents/object-passport/:param', {
//            templateUrl: 'views/documents-object-passport.html',
//            controller: 'documentsObjectPassportCtrl'
//        })
        .when('/settings/object-passport/:object/:param', {
            reloadOnSearch: false,
            templateUrl: 'views/documents-object-passport.html',
            controller: 'documentsObjectPassportCtrl'
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
            controller: 'MngmtDeviceModelsController'
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