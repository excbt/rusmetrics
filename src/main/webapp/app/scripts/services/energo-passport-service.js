/*jslint node: true, es5: true*/
/*global angular*/
'use strict';
var app = angular.module('portalNMC');
app.service('energoPassportSvc', ['mainSvc', '$http', function (mainSvc, $http) {
    var service = {};
    
    var PASSPORT_URL_OLD = "../api/energy-passport-templates",
        PASSPORT_URL_OLD_NEW = PASSPORT_URL_OLD + "/new",
        PASSPORT_URL = "../api/subscr/energy-passports";
    
    function createPassportOld() {
        return $http.get(PASSPORT_URL_OLD_NEW);
    }
    
    function addQueryParam(url, paramName, paramValue) {
        if (!mainSvc.checkUndefinedNull(paramValue)) {
            if (url.indexOf('?') === -1) {
                url += "?";
            } else {
                url += "&";
            }
            url += encodeURIComponent(paramName) + "=" + encodeURIComponent(paramValue);
        }
//        console.log(url);
        return url;
    }
    
    function createPassport(passportName, passportDescription) {
        var url = PASSPORT_URL;
        url = addQueryParam(url, "passportName", passportName);
        url = addQueryParam(url, "passportDescription", passportDescription);
       
        return $http.post(url);
    }
/* test    
    createPassportNew();
    createPassportNew("Pass name", "pass desc");
    createPassportNew("Раз раз", "Описание паспорта");
    createPassportNew("Pass1");
    createPassportNew(null, "Description 2");
*/
    function loadPassports(id) {
        var url = PASSPORT_URL;
        if (!mainSvc.checkUndefinedNull(id)) {
            url += "/" + id;
        }
        return $http.get(url);
    }
    
    service.createPassport = createPassport;
    service.loadPassports = loadPassports;
    
    return service;
}]);