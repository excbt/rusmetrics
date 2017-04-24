/*jslint node: true, es5: true*/
/*global angular*/
'use strict';
var app = angular.module('portalNMC');
app.service('energoPassportSvc', ['mainSvc', '$http', '$q', function (mainSvc, $http, $q) {
    var service = {};
    
    var PASSPORT_URL_OLD = "../api/energy-passport-templates",
        PASSPORT_URL_OLD_NEW = PASSPORT_URL_OLD + "/new",
        PASSPORT_URL = "../api/subscr/energy-passports";
    
    function createPassportOld() {
        return $http.get(PASSPORT_URL_OLD_NEW);
    }
    
//    function addQueryParam(url, paramName, paramValue) {
//        if (!mainSvc.checkUndefinedNull(paramValue)) {
//            if (url.indexOf('?') === -1) {
//                url += "?";
//            } else {
//                url += "&";
//            }
//            url += encodeURIComponent(paramName) + "=" + encodeURIComponent(paramValue);
//        }
//        return url;
//    }
    
    function createPassport(passportName, passportDescription) {
        var url = PASSPORT_URL;
        url = mainSvc.addParamToURL(url, "passportName", passportName);
        url = mainSvc.addParamToURL(url, "passportDescription", passportDescription);
       
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
    
    function loadPassportData(id, sectionId, entryId) {
        var url = PASSPORT_URL;
        if (!mainSvc.checkUndefinedNull(id)) {
            url += "/" + id + "/data";
            url = mainSvc.addParamToURL(url, "sectionId", sectionId);
            url = mainSvc.addParamToURL(url, "sectionEntryId", entryId);
            return $http.get(url);
        } else {
            var defer = $q.defer();
            defer.reject("Passport id is undefined or null!");
            return defer;
        }
        
    }
    
    function saveEntry(passportId, sectionId, entry) {
        var url = PASSPORT_URL;
        if (!mainSvc.checkUndefinedNull(passportId) && !mainSvc.checkUndefinedNull(sectionId) && !mainSvc.checkUndefinedNull(entry)) {
            url += "/" + passportId + "/section/" + sectionId + "/entries";
            return $http.put(url, entry);
        } else {
            var defer = $q.defer();
            defer.reject("Passport data is undefined or null!");
            return defer;
        }
    }
    
    function savePassport(passportId, passportSectionData) {
        var url = PASSPORT_URL;
        if (!mainSvc.checkUndefinedNull(passportId) && !mainSvc.checkUndefinedNull(passportSectionData)) {
            url += "/" + passportId + "/data";
            return $http.put(url, passportSectionData);
        } else {
            var defer = $q.defer();
            defer.reject("Passport data is undefined or null!");
            return defer;
        }
    }
    
    service.createPassport = createPassport;
    service.loadPassports = loadPassports;
    service.loadPassportData = loadPassportData;
    service.saveEntry = saveEntry;
    service.savePassport = savePassport;
    
    return service;
}]);