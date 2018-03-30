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
        
        var COMMON_DATA_URL = "components/object-tree-module/cont-object-monitor-component/testdata/commondata.json",
            RESOURCE_DATA_URL = "components/object-tree-module/cont-object-monitor-component/testdata/{resource}data.json";
        
        var svc = this;
        svc.loadCommonData = loadCommonData;
        svc.loadResourceData = loadResourceData;
        
        ////////////////////////////////////////////////////////////////
        
        function loadCommonData() {
            var url = COMMON_DATA_URL;
            return $http.get(url);
        }
        
        function loadResourceData(resource) {
            var url = RESOURCE_DATA_URL.replace("{resource}", resource);
            return $http.get(url);
        }

    }
})();