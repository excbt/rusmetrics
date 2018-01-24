/*global angular, console*/
(function() {
    'use strict';

    angular.module('objectTreeModule')
        .component('treeNodeInfoViewComponent', {
            bindings: {
                node: '<'
            },
            templateUrl: "components/object-tree-module/tree-node-info-view-component/tree-node-info-view-component.html",
            controller: treeNodeInfoViewComponentController
    });

    treeNodeInfoViewComponentController.$inject = ['$stateParams', 'treeNodeInfoViewComponentService'];

    /* @ngInject */
    function treeNodeInfoViewComponentController($stateParams, treeNodeInfoViewComponentService) {
        /*jshint validthis: true*/
        var vm = this;
        vm.property = 'Controller';
        vm.nodeInfo = {
        };
        vm.svc = treeNodeInfoViewComponentService;
        
        var nodeTypes = {
            "ELEMENT": "Узел",
            "CONT_OBJECT": "Объект учета",
            "CONT_ZPOINT": "Точка учета",
            "DEVICE_OBJECT": "Прибор"
        };
        

//        activate();
        vm.$onInit = activate;

        ////////////////
        
        function errorCallback(e) {
            console.log(e);
            vm.nodeInfo.isLoading = false;
        }
        function prepareNodeInfo(node) {
            var nodeInfo = {};
            var loadedNode = node;
            nodeInfo.type = nodeTypes[loadedNode.nodeType];
            nodeInfo.nameCaption = vm.svc.isDeviceNode(loadedNode) ? "Серийный номер" : "Название";
            nodeInfo.name = loadedNode.nodeName || loadedNode.nodeObject.fullName || loadedNode.nodeObject.customServiceName || loadedNode.nodeObject.number;
            nodeInfo.childCount = angular.isArray(loadedNode.childNodes)? loadedNode.childNodes.length : 0;
            nodeInfo.contObjectCount = angular.isArray(loadedNode.linkedContObjects) ? loadedNode.linkedNodeObjects.length : 0;
            return nodeInfo;
        }
        
        function successLoadPTreeNodeCallback(resp) {
            vm.nodeInfo = prepareNodeInfo(resp.data);
            vm.nodeInfo.isLoading = false;
        }

        function activate() {
            if (angular.isUndefined(vm.node) || vm.node === null) {
                if ($stateParams.hasOwnProperty('node') && $stateParams.node !== null) {
                    vm.node = $stateParams.node;
                    if (vm.svc.isElementNode(vm.node)) {
                        vm.nodeInfo.isLoading = true;
                        vm.svc.loadPTreeNode(vm.node._id || vm.node.nodeObject.id, 1).then(successLoadPTreeNodeCallback, errorCallback);
                    } else {
                        vm.nodeInfo = prepareNodeInfo(vm.node);
                    }
                    
                }
            }
        }
    }
})();