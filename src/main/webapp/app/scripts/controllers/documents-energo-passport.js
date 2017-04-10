/*jslint node: true*/
/*global angular*/
'use strict';
var app = angular.module('portalNMC');
app.controller('documentsEnergoPassportCtrl', ['$location', 'mainSvc', 'energoPassportSvc', 'notificationFactory', '$scope', '$routeParams', '$timeout', function ($location, mainSvc, energoPassportSvc, notificationFactory, $scope, $routeParams, $timeout) {
    
//    console.log('documentsEnergoPassportCtrl is run');
//    console.log($location.path);
//    console.log($routeParams);
    var INDEX_OF_DEFAULT_PART = 2;
    $scope.showContentsFlag = true;
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.loading = true;
    $scope.ctrlSettings.cssMeasureUnit = "em";
    $scope.ctrlSettings.passportLoading = true;
    $scope.ctrlSettings.emptyString = " ";
    
    $timeout(function () {
        $scope.ctrlSettings.loading = false;
    }, 1500);
    
    $scope.data = {};
    $scope.data.passportList = [
        
    ];
    
    function errorCallback(e) {
        var errorObj = mainSvc.errorCallbackHandler(e);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    }
    
//console.log(inputTableDef);
    function performElementsRecursion(elements, level, headerRows) {
        if (angular.isUndefined(elements) || elements === null) {
            return;
        }
        elements.forEach(function (elm) {
            if (headerRows.length <= level) {
                headerRows.push([]);
            }
            headerRows[level].push(angular.copy(elm));
            performElementsRecursion(elm.elements, level + 1, headerRows);
        });
    }
    
    function prepareTableHeaderRecursion(tablePart) {
        var headerRows = [];
        performElementsRecursion(tablePart.elements, 0, headerRows);        
        tablePart.headerRows = headerRows;
        return tablePart;
    }
    
    function performTablePart(tablePart) {
        if (tablePart.partType === "HEADER") {
            tablePart = prepareTableHeaderRecursion(tablePart);
            return true;
        }

    }
    
    function preparePassDoc(passDoc) {
        //Prepare headers for inner tables
        switch (passDoc.viewType) {
        default:
            passDoc.parts.forEach(function (passDocPart) {
                if (passDocPart.partType !== "INNER_TABLE") {
                    return;
                }         
                passDocPart.innerPdTable.parts.forEach(performTablePart);
            });
            break;
//        case "TABLE":
//            passDoc.parts.forEach(performTablePart);
//            break;
        }
        return passDoc;
    }    
    
    $scope.passDocStructure = null;
    //$scope.passDocStructureFromServer = null;
    $scope.currentPassDocPart = null;
    
    function successCreatePassportCallback(response) {
        console.log(response);
        if (mainSvc.checkUndefinedNull(response) || mainSvc.checkUndefinedNull(response.data)) {
            return false;
        }
        var result = [],
            tmp = angular.copy(response.data);
        tmp.sectionTemplates.forEach(function (secTempl) {
            result.push(preparePassDoc(JSON.parse(secTempl.sectionJson)));
        });
//        result = preparePassDoc(result);
console.log(result);        
//return;        
        $scope.passDocStructure = result;
        if ($scope.passDocStructure.length >= 1) {
            $scope.currentPassDocPart = $scope.passDocStructure[INDEX_OF_DEFAULT_PART];
            $scope.currentPassDocPart.isSelected = true;
            $timeout(function () {
                $(':input').inputmask();
//                    $(':input').focus();
            }, 0);
        }        
        $scope.ctrlSettings.passportLoading = false;
    }
    
    function createPassDocInit() {
        energoPassportSvc.createPassport()
            .then(successCreatePassportCallback, errorCallback);
    }
    
    $scope.contentsPartSelect = function(part) {
//console.log(part);
        $scope.currentPassDocPart.isSelected = false;
        $scope.currentPassDocPart = part;
        $scope.currentPassDocPart.isSelected = true;
        $timeout(function () {
            $(':input').inputmask();
//            $(':input').focus();
        }, 0);
        
    };
    
    $scope.tdBlur = function (cell) {
console.log(cell);        
    };
    
    $scope.cancelEnergoPassportEdit = function () {
        $location.path("/documents/energo-passports");
    }
    
    $scope.addRowToTable = function (part) {
        var addingRow = angular.copy(part.innerPdTable.parts[1]);
        addingRow.elements.some(function (rowElem) {
            if (rowElem.__type === 'Counter') {
                rowElem.value = part.innerPdTable.parts.length;
            }
        });
        //if need - clear row values;
        part.innerPdTable.parts.push(addingRow);
    }
    
    $scope.deleteRowFromTable = function (part, ind) {
        part.innerPdTable.parts.splice(ind, 1);
        part.innerPdTable.parts.forEach(function (row, rowInd) {
            if (row.partType === 'HEADER' || angular.isUndefined(row.dynamic) || (row.dynamic !== true)) {
                return false;
            }
            row.elements.some(function (rowElem) {
                if (rowElem.__type === 'Counter') {
                    rowElem.value = rowInd;
                }
            })
        });
    }
    
    function initCtrl() {
        var routeParams = $routeParams;
        if (routeParams.param === "new") {
            createPassDocInit();
        } else {
console.log("Don't understand request!");            
        }
    }
    
    initCtrl();
    
}]);