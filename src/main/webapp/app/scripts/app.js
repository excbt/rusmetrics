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
    'ngTouch'
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
      .otherwise({
        redirectTo: '/'
      });
  });
