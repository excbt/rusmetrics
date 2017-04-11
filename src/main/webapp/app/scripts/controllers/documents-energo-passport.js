/*jslint node: true, nomen: true*/
/*global angular, $*/
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
    
    function performHeaderElementsRecursion(elements, level, rows) {
        if (angular.isUndefined(elements) || elements === null) {
            return;
        }
        elements.forEach(function (elm) {
            if (rows.length <= level) {
                rows.push([]);
            }
            
            rows[level].push(angular.copy(elm));
            
            performHeaderElementsRecursion(elm.elements, level + 1, rows);
        });
    }
    
    function performVerticalElement(subElements, rows) {
        subElements.forEach(function (se, ind) {
            if (rows.length <= ind) {
                var row = {};
                row.tds = [];
//                rows.push([]);
                rows.push(row);
            }
            rows[ind].tds.push(angular.copy(se));
        });
    }
    
    function performElementsRecursion(elements, level, rows) {
        if (angular.isUndefined(elements) || elements === null) {
            return;
        }
        elements.forEach(function (elm) {
            if (rows.length <= level) {
                var row = {};
                row.tds = [];
//                rows.push([]);
                rows.push(row);
            }
            if (elm.vertical === true) {
                performVerticalElement(elm.elements, rows);                
            } else {
//console.log(level);
//console.log(rows);
                var tmp = angular.copy(elm);
//if (level === 0) {                
//    console.log(tmp);                
//}
                rows[level].tds.push(tmp);
                performElementsRecursion(elm.elements, level + 1, rows);
            }
        });
    }
    
    function prepareTableHeaderRecursion(tablePart) {
        var headerRows = [];
        performHeaderElementsRecursion(tablePart.elements, 0, headerRows);
        tablePart.headerRows = headerRows;
        return tablePart;
    }
    function prepareTableRowRecursion(tablePart) {
        var preparedRows = [];
        performElementsRecursion(tablePart.elements, 0, preparedRows);
        if (tablePart.dynamic === true && preparedRows.length > 0) {
            preparedRows[0].dynamic = true;
            preparedRows[0].startPartRow = true;
            preparedRows[preparedRows.length - 1].canDelete = true;
            preparedRows[preparedRows.length - 1].endPartRow = true;
        }
        tablePart.tbody = preparedRows;
        return tablePart;
    }
    
    function performTablePart(tablePart) {
        if (tablePart.partType === "HEADER") {
            tablePart = prepareTableHeaderRecursion(tablePart);
            return true;
        }
        if (tablePart.partType === "ROW") {
            tablePart = prepareTableRowRecursion(tablePart);
            return true;
        }

    }
    
    function preparePassDoc(passDoc) {
        //Prepare headers for inner tables
//        switch (passDoc.viewType) {
//        default:
        passDoc.parts.forEach(function (passDocPart) {
            if (passDocPart.partType !== "INNER_TABLE") {
                return;
            }
            var theads = [];
            var tbodies = [];
            passDocPart.innerPdTable.parts.forEach(performTablePart);
            passDocPart.innerPdTable.parts.forEach(function (part) {
                if (part.partType === "HEADER") {
//                    Array.prototype.push.apply(theads, part.headerRows);
                    theads = theads.concat(part.headerRows);
                }
                if (part.partType === "ROW") {
//                    Array.prototype.push.apply(tbodies, part.tbody);
                    tbodies = tbodies.concat(part.tbody);
                }
            });
            passDocPart.innerPdTable.theads = theads;
            passDocPart.innerPdTable.tbodies = tbodies;
        });
//            break;
//        case "TABLE":
//            passDoc.parts.forEach(performTablePart);
//            break;
//        }
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
        tmp.sections.forEach(function (secTempl) {
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
    
    function loadPassDoc(id) {
        energoPassportSvc.loadPassports(id)
            .then(successCreatePassportCallback, errorCallback);
    }
    
    $scope.contentsPartSelect = function (part) {
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
//console.log(cell);
    };
    
    $scope.cancelEnergoPassportEdit = function () {
        $location.path("/documents/energo-passports");
    };
    
    $scope.addRowToTable = function (part) {
        var addingRow = angular.copy(part.innerPdTable.parts[1]);
        addingRow.elements.some(function (rowElem) {
            if (rowElem.__type === 'Counter') {
                if (angular.isUndefined(part.innerPdTable.counterValue)) {
                    part.innerPdTable.counterValue = 1;
                }
                part.innerPdTable.counterValue += 1;
                rowElem.value = part.innerPdTable.counterValue;
            }
        });
        prepareTableRowRecursion(addingRow);
        part.innerPdTable.tbodies = part.innerPdTable.tbodies.concat(addingRow.tbody);
        //if need - clear row values;
//        part.innerPdTable.parts.push(addingRow);
    };
    
    $scope.deleteRowFromTable = function (part, ind) {
        var partLength = part.innerPdTable.parts[1].tbody.length;
        part.innerPdTable.tbodies.splice(ind - partLength + 1, partLength);
        var counter = 0;
        part.innerPdTable.tbodies.forEach(function (row) {
            if (row.startPartRow === true) {
                row.tds.some(function (td) {
                    if (td.__type === 'Counter') {
                        counter += 1;
                        td.value = counter;                        
                        return true;
                    }
                });
                
            }            
        });
        part.innerPdTable.counterValue = counter;
    };
    
    function initCtrl() {
        var routeParams = $routeParams;
        if (routeParams.param === "new") {
            createPassDocInit();
        } else {
            var passDocId = Number(routeParams.param);
            loadPassDoc(passDocId);
            console.log("Don't understand request!");
        }
    }
    
    initCtrl();
    
}]);