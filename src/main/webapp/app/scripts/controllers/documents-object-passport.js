/*jslint node: true, nomen: true*/
/*global angular, $, alert*/
'use strict';
var app = angular.module('portalNMC');

app.controller('documentsObjectPassportCtrl', ['mainSvc', '$scope', '$routeParams', 'energoPassportSvc', '$q', 'notificationFactory', function (mainSvc, $scope, $routeParams, energoPassportSvc, $q, notificationFactory) {
    
    var passportRequestCanceller = null;
    
    $scope.isActivePassport = false;
    
    $scope.isReadOnly = function () {
        return mainSvc.isReadonly() || !$scope.isActivePassport;
    };
    
    $scope.cancelObjectPassportEdit = function () {
        window.close();
    };
    
    var errorCallback = function (e) {
        $scope.treeLoading = false;
        var errorObj = mainSvc.errorCallbackHandler(e);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };
    
    function successLoadPassportsCallback(resp) {
        if (!angular.isArray(resp.data) || resp.data.length <= 0) {
            //console.warn("Response from server is incorrect:", resp);
            return false;
        }
        // find active passport
        var tmp = resp.data,
            activePassport = resp.data[0];
        tmp.forEach(function (passport) {
            if (passport.passportDate2 > activePassport.passportDate2) {
                activePassport = passport;
            } else if (passport.passportDate2 === activePassport.passportDate2) {
                if (passport.id > activePassport.id) {
                    activePassport = passport;
                }
            }
        });
        if (activePassport.id === Number($routeParams.param)) {
            $scope.isActivePassport = true;
        }
    }
    
    function loadContObjectPassports(contObjectId) {
        
        if (passportRequestCanceller !== null) {
            passportRequestCanceller.resolve();
        }

        passportRequestCanceller = $q.defer();
        var httpOptions = {
            timeout: passportRequestCanceller.promise
        };

        energoPassportSvc.loadContObjectPassports(contObjectId, httpOptions)
            .then(successLoadPassportsCallback, errorCallback);

    }
    
    $scope.$on('$destroy', function () {
        if (passportRequestCanceller !== null) {
            passportRequestCanceller.resolve();
        }
    });
    
    function initCtrl() {
//        console.log($routeParams);
        if ($routeParams.object !== "new") {
            loadContObjectPassports(Number($routeParams.object));
        } else {
            $scope.isActivePassport = true;
        }
    }
    
    initCtrl();
}]);