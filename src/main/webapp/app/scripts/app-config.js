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
     * Configure of the application.
     */



    //app.config(['dateRangePickerConfig', function(dateRangePickerConfig) {
    //    dateRangePickerConfig.separator = ' по ';
    //    dateRangePickerConfig.format= 'DD.MM.YYYY';
    //}]);

    //config for ngIdle
    angular.module('portalNMC')
        .config(configure);
    //start Idle service
    //app.run(['Idle', function(Idle) {
    //  Idle.watch();
    //}]);

    configure.$inject = ['KeepaliveProvider',
                         'IdleProvider',
                         '$logProvider',
                         'uiMask.ConfigProvider',
                         '$locationProvider',
                         'widgetsProvider',
                         '$mdIconProvider'
                     ];

    function configure(KeepaliveProvider,
                        IdleProvider,
                        $logProvider,
                        uiMaskConfigProvider,
                        $locationProvider,
                        widgetsProvider,
                        $mdIconProvider
                    ) {
        //  IdleProvider.idle(3600); //idle time in seconds
        //  IdleProvider.timeout(30); //time out in seconds
        //  KeepaliveProvider.interval(10);//keepAlive - not used

        //configure $log service
        $logProvider.debugEnabled(true);

        uiMaskConfigProvider.maskDefinitions({'a': /[a-z]/, 'A': /[A-Z]/, '*': /[a-zA-Z0-9]/, '9': /\d/, 'M': /[A-F0-9]/});
        
        //$location config
        $locationProvider.hashPrefix('');
        
        //widget configuration
        widgetsProvider.setManifestGenerator(function resourceWidgetManufest() {
            return function (name) {
                return {
                    module: name + 'Widget',
                    html: 'widgets/' + name + '/' + name + '.html',
                    files: [
                        'widgets/' + name + '/' + name + '.js',
                        'widgets/' + name + '/' + name + '.css'
                    ]
                };
            };
        });

        widgetsProvider.setManifestGenerator(function treeNotificationsDiagramWidgetManifest() {
            return function (name) {
                return name === 'treeNotificationsDiagram' && {
                    module: name + 'Widget',
                    html: 'widgets/' + name + '/' + name + '.html',
                    files: [
                        'widgets/' + name + '/' + name + '.js',
                        'widgets/' + name + '/' + name + 'Service.js',
                        'widgets/' + name + '/' + name + '.css'
                    ]
                };
            };
        });
        
        $mdIconProvider.icon("menu", "./assets/svg/menu.svg");
    }

}());