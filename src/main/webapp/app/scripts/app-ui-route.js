/*global angular*/
(function () {
    'use strict';
    //var app = angular.module('portalNMC');

    angular.module('portalNMC')
        .config(stateProviderConfig);
    
    stateProviderConfig.$inject = ['$stateProvider', '$urlRouterProvider'];
    
    function stateProviderConfig($stateProvider, $urlRouterProvider) {
        var states = [
            {
                name: 'objects',
                url: '/',
                templateUrl: 'components/portal-nmc-module/objects/objects-list/objects_edit.html',
                controller: 'ObjectsCtrl',
                resolve: {
                    permissions: ['mainSvc', function (mainSvc) {
                        return mainSvc.getLoadedServicePermission();
                    }]
                }
            },
/* common version*/            
/*            {
                name: 'objectsPTree',
                url: '/objects/newlist/',
                templateUrl: 'views/objects-newlist.html',
                controller: 'NewObjectsCtrl',
                resolve: {
                    permissions: ['mainSvc', function (mainSvc) {
                        return mainSvc.getLoadedServicePermission();
                    }]
                }
            },
  */
/* component version*/            
            {
                name: 'objectsPTree',
                url: '/objects/newlist/',
                component: 'treeComponent',
                
                resolve: {
                    permissions: ['mainSvc', function (mainSvc) {
                        return mainSvc.getLoadedServicePermission();
                    }]
                }
            },
/*            {
                name: 'objectsPTree.info',
                url: 'tree-info',
                component: 'treeElementNodeComponent',
                params: {
                    node: null
                }
            },*/
            {
                name: 'objectsPTree.contObjectControl',
                url: 'cont-object-control',
                component: 'nodeControlComponent',
                params: {
                    node: null
                }
            },
            
            {
                name: 'objectsPTree.treeNodeInfo',
                url: 'tree-node-info',
                component: 'treeNodeInfoComponent',
                params: {
                    node: null
                }
            },
            
            {
                name: 'objectsPTree.treeNodeInfo.contObjectControl',
                url: '/cont-object-control',
                component: 'nodeControlComponent',
                params: {
                    node: null
                }
            },
            
            {
                name: 'objectsPTree.treeNodeInfo.nodeNotifications',
                url: '/node-notifications',
                component: 'treeNodeNotificationsComponent',
                params: {
                    node: null
                }
            },            
            {
                name: 'objectsPTree.treeNodeInfo.nodeInfoView',
                url: '/node-info-view',
                component: 'treeNodeInfoViewComponent',
                params: {
                    node: null
                }
            },
            {
                name: 'objectsPTree.treeNodeInfo.nodeIndicatorView',
                url: '/node-indicator-view',
                component: 'ptreeNodeIndicatorView',
                params: {
                    node: null
                }
            },
            {
                name: 'objectsPTree.treeNodeInfo.contObjectMonitor',
                url: '/cont-object-monitor',
                component: 'contObjectMonitorComponent',
                params: {
                    node: null
                }
            },
            {
                name: 'objectsPTree.treeNodeInfo.contZpointConsWidget',
                url: '/cont-zpoint-cons',
                component: 'contZpointConsWidgetComponent',
                params: {
                    contObjectId: null,
                    zpointId: null
                }
            },
/*end 'component version'*/            
            {
                name: 'demoMap',
                url: '/objects/demo-map/',
                templateUrl: 'components/portal-nmc-module/objects/objects-map/objects-map.html',
                controller: 'ObjectsMapCtrl',
                resolve: {
                    permissions: ['mainSvc', function (mainSvc) {
                        return mainSvc.getLoadedServicePermission();
                    }]
                }
            },
            {
                name: 'energoPassports',
                url: '/documents/energo-passports/',
                templateUrl: 'components/portal-nmc-module/energy/documents-energo-passports.html',
                controller: 'documentsEnergoPassportsCtrl'
            },
            {
                name: 'energoPassport',
                url: '/documents/energo-passport/:param',
                reloadOnSearch: false,
                templateUrl: 'components/portal-nmc-module/energy/documents-energo-passport.html',
                controller: 'documentsEnergoPassportCtrl'
            },
            {
                name: 'objectPassport',
                url: '/settings/object-passport/:object/:param',
                reloadOnSearch: false,
                templateUrl: 'components/portal-nmc-module/settings/object-passport/documents-object-passport.html',
                controller: 'documentsObjectPassportCtrl'
            },
            {
                name: 'notices',
                url: '/notices/list/',
                templateUrl: 'components/portal-nmc-module/notices/notice.html',
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
                url: '/notices/monitor/',
                templateUrl: 'components/portal-nmc-module/objects/monitor/monitor.html',
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
                url: '/notices/monitor_map/',
                templateUrl: 'components/portal-nmc-module/objects/monitor-map/monitor-map.html',
                controller: 'MonitorMapCtrl',
                resolve: {
                    permissions: ['mainSvc', function (mainSvc) {
                        return mainSvc.getLoadedServicePermission();
                    }]
                }
            },
            /*{
                name: 'privateOffice',
                url: '/private/',
                templateUrl: 'views/private_office.html',
                controller: 'ObjectsCtrl'
            },
            {
                name: 'objectsEdit',
                url: '/objects_edit/',
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
                url: '/private/objects_list/',
                templateUrl: 'views/objects_list.html',
                controller: 'ObjectsCtrl'
            },
            {
                name: 'privateOfficeDirectories',
                url: '/private/directories/',
                templateUrl: 'views/directories.html',
                controller: 'DirectoryCtrl'
            },*/
            {
                name: 'reports',
                url: '/reports/',
                templateUrl: 'components/portal-nmc-module/reports/reports_with_cst.html',
                controller: 'ReportsCtrl'
            },
            /*{
                name: 'reportsCommercial',
                url: '/reports/commercial_report/',
                templateUrl: 'views/commercial_report.html',
                controller: 'MainCtrl'
            },*/
            {
                name: 'indicators',
                url: '/objects/indicators/',
                templateUrl: 'components/portal-nmc-module/indicators/water/indicators.html',
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
                url: '/objects/indicator-electricity/',
                templateUrl: 'components/portal-nmc-module/indicators/electricity/indicator-electricity.html',
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
                url: '/objects/impulse-indicators/',
                templateUrl: 'components/portal-nmc-module/indicators/impulse/impulse-indicators.html',
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
                url: '/settings/tariffs/',
                templateUrl: 'components/portal-nmc-module/settings/tariffs/tariffs.html',
                controller: 'TariffsCtrl'
            },
            {
                name: 'settigsParamsets',
                url: '/settings/paramsets/',
                templateUrl: 'components/portal-nmc-module/settings/param-sets/param_sets.html',
                controller: 'ParamSetsCtrl'
            },
            {
                name: 'settigsDelivery',
                url: '/settings/delivery/',
                templateUrl: 'components/portal-nmc-module/settings/delivery/delivery.html',
                controller: 'DlvrCtrl'
            },
            {
                name: 'settigsContacts',
                url: '/settings/contacts/',
                templateUrl: 'components/portal-nmc-module/settings/contacts/contacts.html',
                controller: 'ContactsCtrl'
            },
            {
                name: 'settigsObjectGroups',
                url: '/settings/object_groups/',
                templateUrl: 'components/portal-nmc-module/settings/object-groups/object-groups.html',
                controller: 'ObjectGroupsCtrl'
            },
            {
                name: 'settigsUsers',
                url: '/settings/users/',
                templateUrl: 'components/portal-nmc-module/settings/users/settings-users.html',
                controller: 'SettingsUsersCtrl'
            },
            {
                name: 'settigsManagementServices',
                url: '/settings/management_services',
                templateUrl: 'components/portal-nmc-module/settings/services/management-services.html',
                controller: 'ManagementServicesCtrl'
            },
            {
                name: 'settigsNotices',
                url: '/settings/notices',
                templateUrl: 'components/portal-nmc-module/settings/notices/settings-notices.html',
                controller: 'SettingsNoticesCtrl'
            },
            {
                name: 'settigsTenants',
                url: '/settings/tenants/',
                templateUrl: 'components/portal-nmc-module/settings/tenants/settings-tenants.html',
                controller: 'SettingsTenantsCtrl'
            },
            {
                name: 'settigsObjectTrees',
                url: '/settings/object_trees/',
                templateUrl: 'components/portal-nmc-module/settings/object-trees/settings-object-trees.html',
                controller: 'SettingsObjectTreesCtrl'
            },
            {
                name: 'settigsProgram',
                url: '/settings/program',
                templateUrl: 'components/portal-nmc-module/settings/program/settings-program.html',
                controller: 'SettingsProgramCtrl'
            },
            {
                name: 'settigsMeterPeriodSetting',
                url: '/settings/meter-period-setting/',
                templateUrl: 'components/portal-nmc-module/settings/meter-periods/settings-meter-period-setting.html',
                controller: 'SettingsMeterPeriodSettingCtrl'
            },
            {
                name: 'settigsObjectView',
                url: '/settings/object-view/',
                templateUrl: 'components/portal-nmc-module/settings/object-views/settings-object-view.html',
                controller: 'SettingsObjectViewCtrl'
            },
            {
                name: 'settigsAboutProgram',
                url: '/settings/about-program/',
                templateUrl: 'components/portal-nmc-module/settings/about-program/about-program.html',
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
                url: '/management/objects/',
                templateUrl: 'components/portal-nmc-module/management/objects/management-rma-objects.html',
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
                url: '/management/clients/',
                templateUrl: 'components/portal-nmc-module/management/clients/management-rma-clients.html',
                controller: 'MngmtClientsCtrl'
            },
            {
                name: 'managementDatasources',
                url: '/management/datasources/',
                templateUrl: 'components/portal-nmc-module/management/data-sources/management-rma-data-sources.html',
                controller: 'MngmtDatasourcesCtrl'
            },
            {
                name: 'managementDevices',
                url: '/management/devices/',
                templateUrl: 'components/portal-nmc-module/management/devices/management-rma-devices.html',
                controller: 'MngmtDevicesCtrl'
            },
            {
                name: 'managementUsers',
                url: '/management/users/',
                templateUrl: 'components/portal-nmc-module/management/users/management-rma-users.html',
                controller: 'MngmtUsersCtrl'
            },
            {
                name: 'managementPrice',
                url: '/management/price/',
                templateUrl: 'components/portal-nmc-module/management/prices/management-rma-price.html',
                controller: 'MngmtPriceCtrl'
            },
            {
                name: 'managementOrganizations',
                url: '/management/organizations/',
                templateUrl: 'components/portal-nmc-module/management/organizations/management-rma-organizations.html',
                controller: 'MngmtOrganizationsCtrl'
            },
            {
                name: 'managementTempSch',
                url: '/management/temp-sch/',
                templateUrl: 'components/portal-nmc-module/management/temperature-schedules/management-rma-temp-sch.html',
                controller: 'TempSchCtrl'
            },
            {
                name: 'managementModems',
                url: '/management/modems/',
                templateUrl: 'components/portal-nmc-module/management/modems/management-rma-modems.html',
                controller: 'MngmtModemsCtrl'
            },
            {
                name: 'managementDeviceModels',
                url: '/management/device-models/',
                templateUrl: 'components/portal-nmc-module/management/device-models/management-rma-device-models.html',
                controller: 'MngmtDeviceModelsController'
            },
            {
                name: 'logSessionLog',
                url: '/log/session-log/',
                templateUrl: 'components/portal-nmc-module/logs/log-view-resizable-paged.html',
                controller: 'LogViewCtrl'
            },
            {
                name: 'logSmsLog',
                url: '/log/sms-log/',
                templateUrl: 'components/portal-nmc-module/logs/log-sms.html',
                controller: 'LogSmsCtrl'
            }/*,
            {
                name: 'test',
                url: '/test/',
                templateUrl: 'views/test.html',
                controller: 'testCtrl'
            }*/

        ];
    //console.log(states);
        states.forEach(function (state) {
            $stateProvider.state(state);
        });

        $urlRouterProvider.otherwise('/');


    }
}());