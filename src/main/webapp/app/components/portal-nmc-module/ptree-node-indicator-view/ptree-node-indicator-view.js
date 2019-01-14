/*global angular, console*/
(function () {
    'use strict';
    angular.module('portalNMC')
        .component('ptreeNodeIndicatorView', {
            bindings: {
                node: '<'
            },
            templateUrl: "components/portal-nmc-module/ptree-node-indicator-view/ptree-node-indicator-view.html",
            controller: ptreeNodeIndicatorViewController
        });
    
    ptreeNodeIndicatorViewController.$inject = ['$stateParams'];
    
    function ptreeNodeIndicatorViewController($stateParams) {
        /*jshint validthis: true*/
        var ctrl = this;
        ctrl.node = null;        
        
        ctrl.$onInit = function () {
//console.log('$stateParams: ', $stateParams);    
            ctrl.node = $stateParams.node;
            if (angular.isUndefined(ctrl.node) || ctrl.node === null) {
                return false;
            }
            
        };
    }
    
})();