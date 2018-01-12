/*global angular*/
(function () {
    'use strict';

    angular
        .module('contObjectModule')
        .service('contObjectService', Service);

    Service.$inject = ['$http', '$q'];

    /* @ngInject */
    function Service($http, $q) {
        
        var CONT_OBJECT_SHORT_INFO_URL = '../api/subscr/cont-objects/short-info';
        var requestCanceler = null,
            httpOptions = null,
            contObjectsShortInfoArray = null;
        
        var svc = this;
        svc.getContObjectsShortInfoArray = getContObjectsShortInfoArray;
        svc.getRequestCanceler = getRequestCanceler;
        svc.loadContObjectsShortInfo = loadContObjectsShortInfo;
        svc.setContObjectsShortInfoArray = setContObjectsShortInfoArray;
        
        initSvc();

        ////////////////

        function getContObjectsShortInfoArray() {
            return contObjectsShortInfoArray;
        }
        
        function getRequestCanceler() {
            return requestCanceler;
        }
        
        function loadContObjectsShortInfo() {
            var url = CONT_OBJECT_SHORT_INFO_URL;
            return $http.get(url, httpOptions);
        }
        
        function setContObjectsShortInfoArray(arr) {
            contObjectsShortInfoArray = arr;
        }
        
        //service initialization
        var initSvc = function () {
            
            requestCanceler = $q.defer();
            httpOptions = {
                timeout: requestCanceler.promise
            };            
        };
    }
})();