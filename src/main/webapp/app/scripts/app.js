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
    'ui.tree',
    'daterangepicker'
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
      .when('/private/objects_private', {
        templateUrl: 'views/objects_private.html',
        controller: 'ObjectsCtrl'
      })
      .when('/objects_list', {
        templateUrl: 'views/objects_list.html',
        controller: 'ObjectsCtrl'
      })
      .when('/objects/indicators', {
        templateUrl: 'views/indicators.html',
        controller: 'IndicatorsCtrl'
      })
      .when('/private/directories', {
        templateUrl: 'views/directories.html',
        controller: 'DirectoryCtrl'
      })
    .when('/reports', {
        templateUrl: 'views/reports.html',
        controller: 'MainCtrl'
      })
    .when('/reports/commercial_report', {
        templateUrl: 'views/commercial_report.html',
        controller: 'MainCtrl'
      })
    .when('/objects/indicators', {
        templateUrl: 'views/indicators.html',
        controller: 'IndicatorsCtrl'
      })
    .when('/test', {
        templateUrl: 'views/test.html',
        controller: 'TestCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
