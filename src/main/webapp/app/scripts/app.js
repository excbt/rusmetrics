'use strict';

/**
 * @ngdoc overview
 * @name portalNMK
 * @description
 * # portalNMK
 *
 * Main module of the application.
 */

var app = angular
  .module('portalNMK', [
	'react',
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'ui.tree',
    'daterangepicker'
  ]);

app.config(function ($routeProvider) {
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
    .when('/private/tariffs', {
        templateUrl: 'views/tariffs.html',
        controller: 'TariffsCtrl'
      })
    .when('/test', {
        templateUrl: 'views/test.html',
        controller: 'TestCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
