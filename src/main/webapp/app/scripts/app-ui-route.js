/*jslint node: true*/
/*global angular*/

'use strict';
var app = angular.module('portalNMC');

app.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
    var states = [
        {
            name: 'objects',
            url: '/',
            templateUrl: 'views/objects_edit.html',
            controller: 'ObjectsCtrl',
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        },
        {
            name: 'objectsPTree',
            url: '/objects/newlist',
            templateUrl: 'views/objects-newlist.html',
            controller: 'NewObjectsCtrl',
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            },
            cache: true
        },
        {
            name: 'demoMap',
            url: '/objects/demo-map',
            templateUrl: 'views/objects_map.html',
            controller: 'ObjectsMapCtrl',
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        },
        {
            name: 'energoPassports',
            url: '/documents/energo-passports',
            templateUrl: 'views/documents-energo-passports.html',
            controller: 'documentsEnergoPassportsCtrl'
        },
        {
            name: 'energoPassport',
            url: '/documents/energo-passports/:param',
            reloadOnSearch: false,
            templateUrl: 'views/documents-energo-passport.html',
            controller: 'documentsEnergoPassportCtrl'
        },
        {
            name: 'objectPassport',
            url: '/settings/object-passport/:object/:param',
            reloadOnSearch: false,
            templateUrl: 'views/documents-object-passport.html',
            controller: 'documentsObjectPassportCtrl'
        },
        {
            name: 'notices',
            url: '/notices/list',
            templateUrl: 'views/notice.html',
            controller: 'NoticeCtrl',
            reloadOnSearch: false,
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        },
        {
            name: 'noticesMonitor',
            url: '/notices/monitor',
            templateUrl: 'views/monitor.html',
            controller: 'MonitorCtrl',
            reloadOnSearch: false,
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        },
        {
            name: 'noticesMonitorMap',
            url: '/notices/monitor_map',
            templateUrl: 'views/monitor_map.html',
            controller: 'MonitorMapCtrl',
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        },
        {
            name: 'privateOffice',
            url: '/private',
            templateUrl: 'views/private_office.html',
            controller: 'ObjectsCtrl'
        },
        {
            name: 'objectsEdit',
            url: '/objects_edit',
            templateUrl: 'views/objects_edit.html',
            controller: 'ObjectsCtrl',
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        },
        {
            name: 'privateOfficeObjectsList',
            url: '/private/objects_list',
            templateUrl: 'views/objects_list.html',
            controller: 'ObjectsCtrl'
        },
        {
            name: 'privateOfficeDirectories',
            url: '/private/directories',
            templateUrl: 'views/directories.html',
            controller: 'DirectoryCtrl'
        },
        {
            name: 'reports',
            url: '/reports',
            templateUrl: 'views/reports_with_cst.html',
            controller: 'ReportsCtrl'
        },
        {
            name: 'reportsCommercial',
            url: '/reports/commercial_report',
            templateUrl: 'views/commercial_report.html',
            controller: 'MainCtrl'
        },
        {
            name: 'indicators',
            url: '/objects/indicators',
            templateUrl: 'views/indicators.html',
            controller: 'IndicatorsCtrl',
            reloadOnSearch: false,
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        },
        {
            name: 'indicatorsElectricity',
            url: '/objects/indicator-electricity',
            templateUrl: 'views/indicator-electricity.html',
            controller: 'ElectricityCtrl',
            reloadOnSearch: false,
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        },
        {
            name: 'indicatorsImpulse',
            url: '/objects/impulse-indicators',
            templateUrl: 'views/impulse-indicators.html',
            controller: 'ImpulseIndicatorsCtrl',
            reloadOnSearch: false,
            resolve: {
                permissions: ['mainSvc', function (mainSvc) {
                    return mainSvc.getLoadedServicePermission();
                }]
            }
        },
        {
            name: 'settigsTariffs',
            url: '/settings/tariffs',
            templateUrl: 'views/tariffs.html',
            controller: 'TariffsCtrl'
        },
        {
            name: 'settigsParamsets',
            url: '/settings/paramsets',
            templateUrl: 'views/param_sets.html',
            controller: 'ParamSetsCtrl'
        },
        {
            name: 'settigsDelivery',
            url: '/settings/delivery',
            templateUrl: 'views/delivery.html',
            controller: 'DlvrCtrl'
        },
        {
            name: 'settigsContacts',
            url: '/settings/contacts',
            templateUrl: 'views/contacts.html',
            controller: 'ContactsCtrl'
        },
        {
            name: 'settigsObjectGroups',
            url: '/settings/object_groups',
            templateUrl: 'views/object_groups.html',
            controller: 'ObjectGroupsCtrl'
        },
        {
            name: 'settigsUsers',
            url: '/settings/users',
            templateUrl: 'views/settings-users.html',
            controller: 'SettingsUsersCtrl'
        },
        {
            name: 'settigsManagementServices',
            url: '/settings/management_services',
            templateUrl: 'views/management-services.html',
            controller: 'ManagementServicesCtrl'
        },
        {
            name: 'settigsNotices',
            url: '/settings/notices',
            templateUrl: 'views/settings-notices.html',
            controller: 'SettingsNoticesCtrl'
        },
        {
            name: 'settigsTenants',
            url: '/settings/tenants',
            templateUrl: 'views/settings-tenants.html',
            controller: 'SettingsTenantsCtrl'
        },
        {
            name: 'settigsObjectTrees',
            url: '/settings/object_trees',
            templateUrl: 'views/settings-object-trees.html',
            controller: 'SettingsObjectTreesCtrl'
        },
        {
            name: 'settigsProgram',
            url: '/settings/program',
            templateUrl: 'views/settings-program.html',
            controller: 'SettingsProgramCtrl'
        },
        {
            name: 'settigsMeterPeriodSetting',
            url: '/settings/meter-period-setting',
            templateUrl: 'views/settings-meter-period-setting.html',
            controller: 'SettingsMeterPeriodSettingCtrl'
        },
        {
            name: 'settigsObjectView',
            url: '/settings/object-view',
            templateUrl: 'views/settings-object-view.html',
            controller: 'SettingsObjectViewCtrl'
        },
        {
            name: 'settigsAboutProgram',
            url: '/settings/about-program',
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
        },
        {
            name: 'managementObjects',
            url: '/management/objects',
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
        },
        {
            name: 'managementClients',
            url: '/management/clients',
            templateUrl: 'views/management-rma-clients.html',
            controller: 'MngmtClientsCtrl'
        },
        {
            name: 'managementDatasources',
            url: '/management/datasources',
            templateUrl: 'views/management-rma-data-sources.html',
            controller: 'MngmtDatasourcesCtrl'
        },
        {
            name: 'managementDevices',
            url: '/management/devices',
            templateUrl: 'views/management-rma-devices.html',
            controller: 'MngmtDevicesCtrl'
        },
        {
            name: 'managementUsers',
            url: '/management/users',
            templateUrl: 'views/management-rma-users.html',
            controller: 'MngmtUsersCtrl'
        },
        {
            name: 'managementPrice',
            url: '/management/price',
            templateUrl: 'views/management-rma-price.html',
            controller: 'MngmtPriceCtrl'
        },
        {
            name: 'managementOrganizations',
            url: '/management/organizations',
            templateUrl: 'views/management-rma-organizations.html',
            controller: 'MngmtOrganizationsCtrl'
        },
        {
            name: 'managementTempSch',
            url: '/management/temp-sch',
            templateUrl: 'views/management-rma-temp-sch.html',
            controller: 'TempSchCtrl'
        },
        {
            name: 'managementModems',
            url: '/management/modems',
            templateUrl: 'views/management-rma-modems.html',
            controller: 'MngmtModemsCtrl'
        },
        {
            name: 'managementDeviceModels',
            url: '/management/device-models',
            templateUrl: 'views/management-rma-device-models.html',
            controller: 'MngmtDeviceModelsController'
        },
        {
            name: 'logSessionLog',
            url: '/log/session-log',
            templateUrl: 'views/log-view-resizable-paged.html',
            controller: 'LogViewCtrl'
        },
        {
            name: 'logSmsLog',
            url: '/log/sms-log',
            templateUrl: 'views/log-sms.html',
            controller: 'LogSmsCtrl'
        },
        {
            name: 'test',
            url: '/test',
            templateUrl: 'views/test.html',
            controller: 'testCtrl'
        }
        
    ];
    
    states.forEach(function (state) {
        $stateProvider.state(state);
    });
                   
    $urlRouterProvider.otherwise('/');
    
    
}]);