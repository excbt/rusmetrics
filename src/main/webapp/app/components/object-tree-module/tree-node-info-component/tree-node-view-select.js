/*global angular, console*/
(function () {
    'use strict';
    angular.module('objectTreeModule')
        .component('treeNodeViewSelect', {
            bindings: {
                node: '<'
            },
            templateUrl: "components/object-tree-module/tree-node-info-component/tree-node-view-select.html",
            controller: treeNodeInfoComponentController
        });
    
    treeNodeInfoComponentController.$inject = ['$stateParams', 'treeNodeInfoComponentService', '$state', '$rootScope', '$scope'];
    
    function treeNodeInfoComponentController($stateParams, treeNodeInfoComponentService, $state, $rootScope, $scope) {
        /*jshint validthis: true*/
        var ctrl = this;
        ctrl.node = null;
        ctrl.nodeWidgets = [];
        ctrl.svc = treeNodeInfoComponentService;
        
        function performInputData(node) {
//console.log(node);            
            ctrl.node = node;
            if (angular.isUndefined(ctrl.node) || ctrl.node === null) {
                return false;
            }
            ctrl.nodeWidgets = ctrl.svc.getNodeWidgets(ctrl.node.nodeType);
//console.log(ctrl.nodeWidgets);            
            ctrl.currentWidget = treeNodeInfoComponentService.getCurrentWidget(ctrl.node.nodeType) === null ? ctrl.nodeWidgets[0] : treeNodeInfoComponentService.getCurrentWidget(ctrl.node.nodeType);
            ctrl.changeWidget(ctrl.currentWidget);
        }
        
        ctrl.$onInit = function () {
//console.log('$stateParams: ', $stateParams);    
            performInputData($stateParams.node);
        };
        
        
        ctrl.changeWidget = function (widget) {
            if (ctrl.node !== null) {
                $state.go(widget.stateName, {node: ctrl.node});
            }
            treeNodeInfoComponentService.setCurrentWidget(ctrl.node.nodeType, widget);
            $rootScope.$broadcast(ctrl.svc.EVENTS.setWidget);
        };
        
        ctrl.widgetSelectDisabled = function () {
            return ctrl.node === null;
        };
        
        $scope.$on('treeNav:selectPNode', function (ev, args) {
            performInputData(args.node);
        });
    }
    
})();