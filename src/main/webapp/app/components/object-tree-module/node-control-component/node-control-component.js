/*global angular, console*/
(function() {
    'use strict';

    angular
        .module('objectTreeModule')
        .component('nodeControlComponent', {
            bindings: {
                node: '<'
            },
            templateUrl: "components/object-tree-module/node-control-component/node-control-component.html",
            controller: nodeControlComponentController
        });
        //.controller('Controller', Controller);

    nodeControlComponentController.$inject = ['$stateParams', 'contObjectControlComponentService', '$scope'];
//
    /* @ngInject */
    function nodeControlComponentController($stateParams, contObjectControlComponentService, $scope){
        
        var contObjectCtrlSvc = contObjectControlComponentService;
        
        /*jshint validthis: true*/
        var vm = this;        
        vm.nodeObjects = [];
        vm.objectsLoading = true;
        
        vm.$onInit = function () {
            getNodeContObjects();
        };
        
        vm.loadObjects = function (nodeId) {
            contObjectCtrlSvc.loadNodeObjects(nodeId);
        };
        
        function getNodeContObjects() {
            var node = $stateParams.node;
            if (angular.isDefined(node) && node !== null) {
                var nodeId = node.id || node._id || node.nodeObject.id;
                var nodeObjects = contObjectCtrlSvc.getNodeData(nodeId);
                if (nodeObjects === null) {
                    vm.objectsLoading = true;
                    vm.loadObjects(nodeId);
                } else {
                    nodeObjects.forEach(function (elm) {
                        elm.loading = true;
                    });
                    vm.nodeObjects = nodeObjects;
                    vm.objectsLoading = false;
                }
            }
//console.log(vm.nodeObjects);            
        }
        
        $scope.$on(contObjectCtrlSvc.EVENTS.OBJECTS_LOADED, function () {
            getNodeContObjects();
        });
    }
})();