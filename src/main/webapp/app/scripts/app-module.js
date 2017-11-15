/*jslint node: true, eqeq: true*/
/*global angular*/
(function () {
    'use strict';

    /**
     * @ngdoc overview
     * @name portalNMC
     * @description
     * # portalNMC
     *
     * Main module of the application.
     */

    angular
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

            'angularFileUpload',
            'leaflet-directive',
            'ui.select',
            'ui.mask',
            'angularWidget',
            'chart.js',
            'ui.layout',
            'ui.router'
        ]);
}());