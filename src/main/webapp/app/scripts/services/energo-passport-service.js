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
    
    function prepareDocumentToSaving(doc) {
        var docBody = null;
        if (mainSvc.checkUndefinedNull(doc)) {
            return null;
        }
        if (!mainSvc.checkUndefinedNull(doc.id)) {
            docBody = {id: doc.id};
        }
        if (!mainSvc.checkUndefinedNull(doc.passportName)) {
            if (docBody === null) {
                docBody = {};
            }
            docBody.passportName = doc.passportName;
        }
        if (!mainSvc.checkUndefinedNull(doc.description)) {
            if (docBody === null) {
                docBody = {};
            }
            docBody.description = doc.description;
        }
        if (!mainSvc.checkUndefinedNull(doc.passportDate)) {
            if (docBody === null) {
                docBody = {};
            }
            docBody.passportDate = doc.passportDate;
        }
        if (!mainSvc.checkUndefinedNull(doc.templateKeyname)) {
            if (docBody === null) {
                docBody = {};
            }
            docBody.templateKeyname = doc.templateKeyname;
        }
        if (!mainSvc.checkUndefinedNull(doc.version)) {
            if (docBody === null) {
                docBody = {};
            }
            docBody.version = doc.version;
        }
        
        return docBody;
    }
    
    function createPassport(doc) {
        var url = PASSPORT_URL;
        var docBody = prepareDocumentToSaving(doc);
       
        return $http.post(url, docBody);
    }
    
    function updatePassport(doc) {
        if (mainSvc.checkUndefinedNull(doc) || mainSvc.checkUndefinedNull(doc.id)) {
            var defer = $q.defer();
            defer.reject("Update: document id is undefined or null!");
            return defer;
        }
        var url = PASSPORT_URL + "/" + doc.id;
        var docBody = prepareDocumentToSaving(doc);
        return $http.put(url, docBody);
    }

    function loadPassports(id) {
        var url = PASSPORT_URL;
        if (!mainSvc.checkUndefinedNull(id)) {
            url += "/" + id;
        }
        return $http.get(url);
    }
    
    function deletePassport(id) {
        if (mainSvc.checkUndefinedNull(id)) {
            var defer = $q.defer();
            defer.reject("Delete: document id is undefined or null!");
            return defer;
        }
        var url = PASSPORT_URL + "/" + id;
        
        return $http.delete(url);
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
            defer.reject("Entry data is undefined or null!");
            return defer;
        }
    }
    
    function deleteEntry(passportId, sectionId, entry) {
        var url = PASSPORT_URL;
        if (!mainSvc.checkUndefinedNull(passportId) && !mainSvc.checkUndefinedNull(sectionId) && !mainSvc.checkUndefinedNull(entry)) {
            url += "/" + passportId + "/section/" + sectionId + "/entries/" + entry.id;
            return $http.delete(url);
        } else {
            var defer = $q.defer();
            defer.reject("Entry data is undefined or null!");
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
    service.deletePassport = deletePassport;
    service.loadPassports = loadPassports;
    service.loadPassportData = loadPassportData;
    service.deleteEntry = deleteEntry;
    service.saveEntry = saveEntry;
    service.savePassport = savePassport;
    service.updatePassport = updatePassport;
    
    return service;
}]);