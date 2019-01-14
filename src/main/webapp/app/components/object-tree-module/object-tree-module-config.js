/*global angular*/
(function () {
    'use strict';
    angular.module('objectTreeModule')
        .config(configuration);

    configuration.$inject = ['widgetsProvider'];

    /* @ngInject */
    function configuration (widgetsProvider) {
        
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
    }
})();