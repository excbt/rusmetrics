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
    'daterangepicker'
      ,'angularUtils.directives.dirPagination'
      ,'ngIdle'
      ,'infinite-scroll'
      ,'angularFileUpload'
      ,'leaflet-directive'
      ,'ui.select'
  ]);

//routing config
app.config(function ($routeProvider) {
console.log("Run routeProviderConfig");    
    $routeProvider
      .when('/', {
        templateUrl: 'views/objects_edit.html',
        controller: 'ObjectsCtrl',
        resolve:{
            permissions: ['mainSvc', function(mainSvc){
                return mainSvc.getLoadedServicePermission();
            }]
        }
      })
      .when('/objects/list', {
        templateUrl: 'views/objects_edit.html',
        controller: 'ObjectsCtrl',
        resolve:{
            permissions: ['mainSvc', function(mainSvc){
                return mainSvc.getLoadedServicePermission();
            }]
        }
      })
      .when('/objects/map', {
        templateUrl: 'views/objects_map.html',
        controller: 'ObjectsMapCtrl',
        resolve:{
            permissions: ['mainSvc', function(mainSvc){
                return mainSvc.getLoadedServicePermission();
            }]
        }
      })
      .when('/notices/list', {
        templateUrl: 'views/notice.html',
        controller: 'NoticeCtrl',
        resolve:{
            permissions: ['mainSvc', function(mainSvc){
                return mainSvc.getLoadedServicePermission();
            }]
        }
      })
      .when('/notices/monitor', {
        templateUrl: 'views/monitor.html',
        controller: 'MonitorCtrl',
        reloadOnSearch: false,
        resolve:{
            permissions: ['mainSvc', function(mainSvc){
                return mainSvc.getLoadedServicePermission();
            }]
        }
      })
      .when('/notices/monitor_map', {
        templateUrl: 'views/monitor_map.html',
        controller: 'MonitorMapCtrl',
        resolve:{
            permissions: ['mainSvc', function(mainSvc){
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
        resolve:{
            permissions: ['mainSvc', function(mainSvc){
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
        resolve:{
            permissions: ['mainSvc', function(mainSvc){
                return mainSvc.getLoadedServicePermission();
            }]
        }
      })
      .when('/private/directories', {
        templateUrl: 'views/directories.html',
        controller: 'DirectoryCtrl'
      })
      .when('/reports', {
        templateUrl: 'views/reports.html',
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
        resolve:{
            permissions: ['mainSvc', function(mainSvc){
                return mainSvc.getLoadedServicePermission();
            }]
        }
      })
      .when('/objects/indicator-electricity', {
        templateUrl: 'views/indicator-electricity.html',
        controller: 'ElectricityCtrl',
        reloadOnSearch: false,
        resolve:{
            permissions: ['mainSvc', function(mainSvc){
                return mainSvc.getLoadedServicePermission();
            }]
        }
      })
      .when('/settings/tariffs', {
        templateUrl: 'views/tariffs.html',
        controller: 'TariffsCtrl'
      })
      .when('/settings/reports', {
        templateUrl: 'views/report_settings.html',
        controller: 'ReportSettingsCtrl'
      })
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
      .when('/management/objects', {
        templateUrl: 'views/management-rma-objects.html',
        controller: 'MngmtObjectsCtrl',
        resolve:{
            rsoOrgs:['objectSvc', function(objectSvc){
                return objectSvc.getRsoOrganizations()
            }],
            servTypes:['objectSvc', function(objectSvc){
                return objectSvc.getServiceTypes()
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
      .otherwise({
        redirectTo: '/'
      });
  });

//app.config(['dateRangePickerConfig', function(dateRangePickerConfig) {
//    dateRangePickerConfig.separator = ' по ';
//    dateRangePickerConfig.format= 'DD.MM.YYYY';
//}]);

//config for ngIdle
app.config(['KeepaliveProvider', 'IdleProvider', function(KeepaliveProvider, IdleProvider) {
//  IdleProvider.idle(3600); //idle time in seconds
//  IdleProvider.timeout(30); //time out in seconds
//  KeepaliveProvider.interval(10);//keepAlive - not used
}]);
//start Idle service
//app.run(['Idle', function(Idle) {
//  Idle.watch();
//}]);

//configure $log service
app.config(function($logProvider){
    $logProvider.debugEnabled(true);
});

app.run(['objectSvc', 'monitorSvc', 'mainSvc', function(objectSvc, monitorSvc, mainSvc){
console.log("Run main, object and monitor services.");  
    var mainSvcInit = mainSvc.getUserServicesPermissions();
    var monitorSvcInit = monitorSvc.getAllMonitorObjects();
    var objectSvcInit = objectSvc.promise;
}]);

/********************************************************************************************************
* Define filters (описываем фильтры)
**********************************************************************************************************
*/
app.filter('columnFilter', function() {
  return function(items, props) {
      var props = [props];
    var out = [];
    if (angular.isArray(items)) {
      items.forEach(function(item) {
        var itemMatches = false;
        var keys = Object.keys(props);         
        for (var i = 0; i < keys.length; i++) {
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
  }
});

app.filter('propsFilter', function() {
  return function(items, props) {
    var out = [];     
    if (angular.isArray(items)) {
      items.forEach(function(item) {
        var itemMatches = false;
        var keys = Object.keys(props);                  
        for (var i = 0; i < keys.length; i++) {
          var prop = keys[i];
          var text = props[prop].toLowerCase();            
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
  }
});