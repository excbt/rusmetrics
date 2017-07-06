/*jslint node: true, nomen: true*/
/*global angular, $, alert*/
'use strict';
var app = angular.module('portalNMC');

app.controller('documentsObjectPassportCtrl', ['mainSvc', '$scope', '$routeParams', 'energoPassportSvc', '$q', 'notificationFactory', function (mainSvc, $scope, $routeParams, energoPassportSvc, $q, notificationFactory) {
    
    var passportRequestCanceller = null;
    
    $scope.isActivePassport = false;
    
    $scope.extraValues = [];
    
    $scope.isReadOnly = function () {
//console.log(mainSvc.isReadonly());        
//console.log($scope.isActivePassport);        
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
//        var tmp = resp.data,
//            activePassport = resp.data[0];
//        tmp.forEach(function (passport) {
//            if (passport.passportDate2 > activePassport.passportDate2) {
//                activePassport = passport;
//            } else if (passport.passportDate2 === activePassport.passportDate2) {
//                if (passport.id > activePassport.id) {
//                    activePassport = passport;
//                }
//            }
//        });
        var activePassport = energoPassportSvc.findContObjectActivePassport(resp.data);
console.log(activePassport);
console.log($routeParams);
        if (activePassport !== null && activePassport.id === Number($routeParams.param)) {
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
    
    function prepareExtraPassportValues(inputParams) {
        if (mainSvc.checkUndefinedNull(inputParams.buildingType) || mainSvc.checkUndefinedNull(inputParams.buildingTypeCategory)) {
            return false;
        }
        var values = [];
//        switch (inputParams.buildingType) {
//        case "H":
//            values.push({_complexIdx: "P_2a_i2",
//                     value: true});
//            break;
//        case "NR":
//            values.push({_complexIdx: "P_2a_i1",
//                     value: true});
//            break;
//        default:
//            values.push({_complexIdx: "P_2a_i3",
//                     value: true});
//            break;
//        }
        
        switch (inputParams.buildingTypeCategory) {
        case "NR_HEALTH":
        case "X_1000090":
        case "X_1000091":
        case "X_1000092":
        case "X_1000093":
        case "X_1000094":
        case "X_1000095":
        case "X_1000096":
        case "X_1000097":
        case "X_1000098":
            values.push({_complexIdx: "P_2a_i1",
                 value: true});
            values.push({_complexIdx: "P_2b_i1",
                     value: true});
            break;
        case "NR_LEARN":
        case "X_1000080":
        case "X_1000081":
        case "X_1000082":
        case "X_1000083":
        case "X_1000084":
        case "X_1000085":
        case "X_1000086":
            values.push({_complexIdx: "P_2a_i1",
                     value: true});
            values.push({_complexIdx: "P_2c_i1",
                     value: true});
            break;
                
        case "X_1000000":
            values.push({_complexIdx: "P_2a_i2",
                     value: true});
            values.push({_complexIdx: "P_2b_i2",
                     value: true});
            break;
                
        case "X_1000072":
            values.push({_complexIdx: "P_2a_i2",
                     value: true});
            values.push({_complexIdx: "P_2c_i2",
                     value: true});
            break;

        case "X_1000070":
            values.push({_complexIdx: "P_2a_i2",
                     value: true});
            values.push({_complexIdx: "P_2d_i2",
                     value: true});
            break;
                
        case "H_APT_BUILDINGS":
        case "X_1000001":
        case "X_1000002":
        case "X_1000003":
        case "X_1000004":
        case "X_1000005":
        case "X_1000006":
            values.push({_complexIdx: "P_2a_i2",
                     value: true});
            values.push({_complexIdx: "P_2e_i2",
                     value: true});
            break;
                
        case "GB_GOV_BUILDINGS":
        case "X_1000170":
        case "X_1000171":
        case "X_1000172":
        case "X_1000173":
        case "X_1000180":
        case "X_1000181":
        case "X_1000182":
        case "X_1000183":
            values.push({_complexIdx: "P_2a_i1",
                     value: true});
            values.push({_complexIdx: "P_2e_i1",
                     value: true});
            break;
                
        default:
            values.push({_complexIdx: "P_2a_i3",
                     value: true});
            values.push({_complexIdx: "P_2b_i3",
                     value: inputParams.buildingTypeCategoryCaption});
            break;
        }
        $scope.extraValues = values;
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
//console.log($routeParams.buildingType);
//console.log($routeParams.buildingTypeCategory);
        prepareExtraPassportValues($routeParams);
    }
    
    initCtrl();
}]);