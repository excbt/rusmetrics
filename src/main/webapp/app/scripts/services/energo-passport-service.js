/*jslint node: true, es5: true*/
/*global angular*/
'use strict';
var app = angular.module('portalNMC');
app.service('energoPassportSvc', ['mainSvc', '$http', function (mainSvc, $http) {
    var service = {};
    
    var PASSPORT_URL = "../api/energy-passport-templates",
        PASSPORT_URL_NEW = PASSPORT_URL + "/new";
    
    function createPassport() {
        return $http.get(PASSPORT_URL_NEW);
    }
    
    service.createPassport = createPassport;
    return service;
}]);