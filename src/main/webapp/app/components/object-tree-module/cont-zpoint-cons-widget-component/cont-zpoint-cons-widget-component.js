/*global angular, console*/
(function() {
    'use strict';

    angular
        .module('objectTreeModule')
        .component('contZpointConsWidgetComponent', {
            bindings: {
                contObjectId: '<',
                zpointId: '<',
                node: '<'
            },
            templateUrl: "components/object-tree-module/cont-zpoint-cons-widget-component/cont-zpoint-cons-widget-component.html",
            controller: componentController
        });

    componentController.$inject = ['$stateParams', 'contZpointConsWidgetComponentService'];

    /* @ngInject */
    function componentController($stateParams, contZpointConsWidgetComponentService){
        /*jshint validthis: true*/
        var vm = this;
        vm.zpoint = null;
        vm.$onInit = initCmpnt;
        vm.svc = contZpointConsWidgetComponentService;
        vm.checkUndefinedNull = vm.svc.checkUndefinedNull;

        ////////////////

        function initCmpnt() {
console.log(vm.node);            
console.log($stateParams.node);
            if (angular.isDefined($stateParams.node) && $stateParams.node !== null) {
                vm.contObjectId = $stateParams.node.nodeObject.contObjectId;
                vm.zpointId = $stateParams.node.nodeObject.id;
                var zpWidgetOptions = {};
                var zp = $stateParams.node.nodeObject;
                zpWidgetOptions.type = "zpoint" + zp.contServiceTypeKeyname[0].toUpperCase() + zp.contServiceTypeKeyname.substring(1, zp.contServiceTypeKeyname.length) + "_v1";
                zpWidgetOptions.zpointName = zp.customServiceName;
                zpWidgetOptions.contZpointId = zp.id;
                if (!vm.checkUndefinedNull(zp.deviceObject)) { 
                    zpWidgetOptions.contObjectFullName = zp.deviceObject.contObjectFullName;
                    zpWidgetOptions.isImpulse = zp.deviceObject.isImpulse;
                    zpWidgetOptions.zpointNumber = zp.deviceObject.number;
                    if (!vm.checkUndefinedNull(zp.deviceObject.deviceModel)) { 
                        zpWidgetOptions.zpointModel = zp.deviceObject.deviceModel.modelName;
                    } else {
                        zpWidgetOptions.zpointModel = "Не задано";
                    }
                }
                zpWidgetOptions.contObjectId = zp.contObjectId;
                zpWidgetOptions.isManualLoading = zp.isManualLoading;                
                vm.zpoint = {};
                vm.zpoint.widgetOptions = zpWidgetOptions;
            }
            if (angular.isDefined($stateParams.contObjectId) && $stateParams.contObjectId !== null && angular.isDefined($stateParams.zpointId) && $stateParams.zpointId !== null) {
                vm.contObjectId = $stateParams.contObjectId;
                vm.zpointId = $stateParams.zpointId;
            }
            
            if (angular.isDefined(vm.contObjectId) && vm.contObjectId !== null && angular.isDefined(vm.zpointId) && vm.zpointId !== null) {
                vm.svc.loadZpoint(vm.contObjectId, vm.zpointId)
                    .then(function (resp) {
                        console.log(resp);
                }, function (err) {
                    console.error(err);
                });
            }
        }
    }
})();