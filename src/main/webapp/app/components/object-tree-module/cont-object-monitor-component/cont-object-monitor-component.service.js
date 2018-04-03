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
            NODE_STATUS_DETAILS_URL = "../api/p-tree-node-monitor/node-color-status/{nodeId}/status-details/{levelColorKeyname}";
        
        var svc = this;
        svc.loadCommonData = loadCommonData;
        svc.loadResourceData = loadResourceData;
        svc.loadNodeColorStatusDetails = loadNodeColorStatusDetails;
        
        ////////////////////////////////////////////////////////////////
        
        function loadCommonData(nodeId) {
            var url = COMMON_DATA_URL.replace("{nodeId}", nodeId);
            return $http.get(url);
        }
        
        function loadResourceData(nodeId, resourceName) {
//            var url = RESOURCE_DATA_URL.replace("{resource}", resource);
            var url = COMMON_DATA_URL.replace("{nodeId}", nodeId);
            url += "?contServiceType=" + resourceName;
            return $http.get(url);
        }
        
        function loadNodeColorStatusDetails(nodeId, levelColor, resourceName) {
            var url = NODE_STATUS_DETAILS_URL.replace("{nodeId}", nodeId).replace("{levelColorKeyname}", levelColor);
            if (angular.isDefined(resourceName) && resourceName !== null) {
                url += "?contServiceType=" + resourceName;
            }
            return $http.get(url);
        }

    }
})();