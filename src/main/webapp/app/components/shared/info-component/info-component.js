/*global angular, console*/
(function () {
    'use strict';
    
    var infoComponentOpt = {
        bindings: {
            message: '<',
            value: '<',
            bgColor: '<'
        },
        templateUrl: 'components/shared/info-component/info-component.html',
        controller: infoComponentController
    };
    
    infoComponentController.$inject = ['$scope', '$element', '$attrs'];
    
    function infoComponentController($scope, $element, $attrs) {
        /*jshint validthis: true*/
        var ctrl = this;
        
//        console.log(ctrl.message);
//        console.log(ctrl.value);
//        console.log(ctrl.bgColor);
        
    }
    
    angular.module('portalNMC')
        .component('infoComponent', infoComponentOpt);
}());