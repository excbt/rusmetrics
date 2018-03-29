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
        
        var DATA_URL = "";
        
        var svc = this;
        svc.exampleFunc = exampleFunc;
        svc.loadCommonData = loadCommonData;
        svc.loadResourceData = loadResourceData;
        
        ////////////////////////////////////////////////////////////////
        
        function exampleFunc() {
            var greeting = "This is example func!";
            console.log(greeting);
            return greeting;
        }
        
        function loadCommonData() {
            
        }
        
        function loadResourceData() {
            
        }

    }
})();