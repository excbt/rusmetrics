/*global angular, console*/
(function () {
    'use strict';

    angular
        .module('contObjectModule')
        .service('contObjectService', Service);

    Service.$inject = ['$http', '$q', '$rootScope'];

    /* @ngInject */
    function Service($http, $q, $rootScope) {
        
        var CONT_OBJECT_SHORT_INFO_URL = '../api/subscr/cont-objects/short-info';
        var EVENTS = {
            CONT_OBJECTS_SHORT_INFO_LOADED: "contObjectService:contObjectsShortInfoLoaded"
        };
        var requestCanceler = null,
            httpOptions = null,
            contObjectsShortInfoArray = null,
            contObjectsShortInfoArrayLoading = false;
        
        var svc = this;
        svc.getContObjectsShortInfoArray = getContObjectsShortInfoArray;
        svc.getRequestCanceler = getRequestCanceler;
        svc.loadContObjectsShortInfo = loadContObjectsShortInfo;
        svc.setContObjectsShortInfoArray = setContObjectsShortInfoArray;        

        ////////////////

        function errorCallback(e) {
            console.log(e);
            contObjectsShortInfoArrayLoading = false;
        }
        
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
        
        function loadContObjectsShortInfoWrap() {
            contObjectsShortInfoArrayLoading = true;
            loadContObjectsShortInfo().then(successLoadContObjectsShortInfo, errorCallback);
        }
        
        function setContObjectsShortInfoArray(arr) {
            contObjectsShortInfoArray = arr;
        }
        
        function successLoadContObjectsShortInfo(resp) {
            contObjectsShortInfoArrayLoading = false;
            if (resp === null || resp.data === null) {
                setContObjectsShortInfoArray(null);
                return false;
            }
            setContObjectsShortInfoArray(resp.data);
            $rootScope.$broadcast(EVENTS.CONT_OBJECTS_SHORT_INFO_LOADED);
        }
        
        //service initialization
        var initSvc = function () {
            
            requestCanceler = $q.defer();
            httpOptions = {
                timeout: requestCanceler.promise
            };            
        };
        initSvc();
    }
})();