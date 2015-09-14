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
  ]);

//routing config
app.config(function ($routeProvider) {
console.log("Run routeProviderConfig");    
    $routeProvider
      .when('/', {
        templateUrl: 'views/objects_edit.html',
        controller: 'ObjectsCtrl'
      })
      .when('/objects/list', {
        templateUrl: 'views/objects_edit.html',
        controller: 'ObjectsCtrl'
      })
      .when('/objects/map', {
        templateUrl: 'views/objects_map.html',
        controller: 'ObjectsMapCtrl'
      })
      .when('/notices/list', {
        templateUrl: 'views/notice.html',
        controller: 'NoticeCtrl'
      })
      .when('/notices/monitor', {
        templateUrl: 'views/monitor.html',
        controller: 'MonitorCtrl'
      })
      .when('/notices/monitor_map', {
        templateUrl: 'views/monitor_map.html',
        controller: 'MonitorMapCtrl'
      })
      .when('/private', {
        templateUrl: 'views/private_office.html',
        controller: 'ObjectsCtrl'
      })
      .when('/objects_edit', {
        templateUrl: 'views/objects_edit.html',
        controller: 'ObjectsCtrl'
      })
      .when('/private/objects_list', {
        templateUrl: 'views/objects_list.html',
        controller: 'ObjectsCtrl'
      })
      .when('/objects/indicators', {
        templateUrl: 'views/indicators.html',
        controller: 'MainCtrl'
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
        controller: 'IndicatorsCtrl'
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

app.run(['objectSvc', 'monitorSvc', function(objectSvc, monitorSvc){
console.log("Run object and monitor services.");  
    var monitorSvcInit = monitorSvc.getAllMonitorObjects();
    var objectSvcInit = objectSvc.promise;
}]);