'use strict';

/**
 * @ngdoc overview
 * @name portalNMK
 * @description
 * # portalNMK
 *
 * Main module of the application.
 */
angular
  .module('portalNMK', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'ui.tree'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl'
      })
      .when('/about', {
        templateUrl: 'views/about.html',
        controller: 'AboutCtrl'
      })
      .when('/notice', {
        templateUrl: 'views/notice.html',
        controller: 'NoticeCtrl'
      })
      .when('/operationlog', {
        templateUrl: 'views/operationlog.html',
        controller: 'OperationlogCtrl'
      })
      .when('/private', {
        templateUrl: 'views/private_office.html',
        controller: 'ObjectsCtrl'
      })
      .when('/private/objects_list', {
        templateUrl: 'views/objects_list.html',
        controller: 'ObjectsCtrl'
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
        controller: 'ReportsCtrl'
      })
    .when('/test', {
        templateUrl: 'views/test.html',
        controller: 'TestCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
