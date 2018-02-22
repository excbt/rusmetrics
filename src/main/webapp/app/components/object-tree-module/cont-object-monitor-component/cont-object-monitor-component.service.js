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
        
        var svc = this;
        svc.exampleFunc = exampleFunc;
        
        function exampleFunc() {
            var greeting = "This is example func!";
            console.log(greeting);
            return greeting;
        }

    }
})();