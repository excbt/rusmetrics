/*global angular*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .service('treeNodeInfoViewComponentService', Service);

    Service.$inject = ['$http', 'treeNavigateComponentService', 'objectTreeService'];

    /* @ngInject */
    function Service($http, treeNavigateComponentService, objectTreeService) {
        this.loadPTreeNode = treeNavigateComponentService.loadPTreeNode;
        this.isDeviceNode = objectTreeService.isDeviceNode;
        this.isElementNode = objectTreeService.isElementNode;
    }
})();