/*global angular, console*/
(function () {
    'use strict';
    angular.module('objectTreeModule')
        .component('treeNodeInfoComponent', {
            bindings: {
                node: '<'
            },
            templateUrl: "components/object-tree-module/tree-node-info-component/tree-node-info-component.html",
            controller: treeNodeInfoComponentController
        });
    
    treeNodeInfoComponentController.$inject = ['$stateParams', 'treeNodeInfoComponentService', '$state'];
    
    function treeNodeInfoComponentController($stateParams, treeNodeInfoComponentService, $state) {
        /*jshint validthis: true*/
        var ctrl = this;
        ctrl.node = null;
        ctrl.nodeWidgets = [];
        ctrl.svc = treeNodeInfoComponentService;
        
        ctrl.$onInit = function () {
//console.log('$stateParams: ', $stateParams);    
            ctrl.node = $stateParams.node;
            if (angular.isUndefined(ctrl.node) || ctrl.node === null) {
                return false;
            }
            ctrl.nodeWidgets = ctrl.svc.getNodeWidgets(ctrl.node.nodeType);
            ctrl.currentWidget = treeNodeInfoComponentService.getCurrentWidget(ctrl.node.nodeType) === null ? ctrl.nodeWidgets[0] : treeNodeInfoComponentService.getCurrentWidget(ctrl.node.nodeType);
            ctrl.changeWidget(ctrl.currentWidget);
        };
        
        ctrl.changeWidget = function (widget) {
            if (ctrl.node !== null) {
                $state.go(widget.stateName, {node: ctrl.node});
            }
            treeNodeInfoComponentService.setCurrentWidget(ctrl.node.nodeType, widget);
        };
        
        ctrl.widgetSelectDisabled = function () {
            return ctrl.node === null;
        };
    }
    
})();