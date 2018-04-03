/*global angular, console*/
/***
    created by Artamonov A.A. , Dec. 2017
*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .service('contObjectMonitorComponentService', Service);

    Service.$inject = ['$http', 'objectTreeService', '$rootScope', 'contObjectService'];

    /* @ngInject */
    function Service($http, objectTreeService, $rootScope, contObjectService) {
        
//        var COMMON_DATA_URL = "components/object-tree-module/cont-object-monitor-component/testdata/commondata.json",
//            RESOURCE_DATA_URL = "components/object-tree-module/cont-object-monitor-component/testdata/{resource}data.json";
        
        var COMMON_DATA_URL = "../api/p-tree-node-monitor/node-color-status/{nodeId}",
            RESOURCE_DATA_URL = "components/object-tree-module/cont-object-monitor-component/testdata/{resource}data.json";
        
        var svc = this;
        svc.loadCommonData = loadCommonData;
        svc.loadResourceData = loadResourceData;
        
        ////////////////////////////////////////////////////////////////
        
        function loadCommonData(nodeId) {
            var url = COMMON_DATA_URL.replace("{nodeId}", nodeId);
            return $http.get(url);
        }
        
        function loadResourceData(nodeId, resource) {
//            var url = RESOURCE_DATA_URL.replace("{resource}", resource);
            var url = COMMON_DATA_URL.replace("{nodeId}", nodeId);
            url += "?contServiceType=" + resource;
            return $http.get(url);
        }

    }
})();