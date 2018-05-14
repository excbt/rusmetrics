/*global angular*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .service('contZpointConsWidgetComponentService', Service);

    Service.$inject = ['$http'];

    /* @ngInject */
    function Service($http) {
        var ZPOINT_INFO_URL = "../api/subscr/contObjects/{contObjectId}/zpoints/{zpointId}";
        
        this.loadZpoint = loadZpoint;
        this.checkUndefinedNull = checkUndefinedNull;

        ////////////////

        function loadZpoint(contObjectId, zpointId) {
            var url = ZPOINT_INFO_URL.replace("{contObjectId}", contObjectId).replace("{zpointId}", zpointId);
            return $http.get(url);
        }
        
        function checkUndefinedNull(obj) {
            return angular.isUndefined(obj) || obj === null;
        }
    }
})();