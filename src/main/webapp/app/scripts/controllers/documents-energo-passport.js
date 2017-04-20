/*jslint node: true, nomen: true*/
/*global angular, $*/
'use strict';
var app = angular.module('portalNMC');
app.controller('documentsEnergoPassportCtrl', ['$location', 'mainSvc', 'energoPassportSvc', 'notificationFactory', '$scope', '$routeParams', '$timeout', function ($location, mainSvc, energoPassportSvc, notificationFactory, $scope, $routeParams, $timeout) {
    
//    console.log('documentsEnergoPassportCtrl is run');
//    console.log($location.path);
//    console.log($routeParams);
    var INDEX_OF_DEFAULT_SECTION = 0;
    $scope.showContentsFlag = true;
    
    $scope.ctrlSettings = {};
//    $scope.ctrlSettings.loading = true;
    $scope.ctrlSettings.cssMeasureUnit = "rem";
    $scope.ctrlSettings.passportLoading = true;
//    $scope.ctrlSettings.emptyString = " ";
    
//    $timeout(function () {
//        $scope.ctrlSettings.loading = false;
//    }, 1500);
    
    $scope.data = {};
    
    $scope.data.isSectionChanged = false; //flag of section data changed
    
    $scope.data.passport = {};
    
    //hash map for passport values
    $scope.data.passDocValues = {};
    //hash map for current section values
    $scope.data.currentSectionValues = {};
    //passport structure
//    $scope.data.passDocStructure = null;
    //current passport structure which is shown at present moment
    $scope.data.currentPassDocSection = null;
    
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
            preparedRows[preparedRows.length - 1].dynamic = true;
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
            var theads = [],
                tbodies = [],
                dynamicRowTemplate = null;
            passDocPart.innerPdTable.parts.forEach(performTablePart);
            passDocPart.innerPdTable.parts.forEach(function (part) {
                if (part.partType === "HEADER") {
//                    Array.prototype.push.apply(theads, part.headerRows);
                    theads = theads.concat(part.headerRows);
                }
                if (part.partType === "ROW") {
//                    Array.prototype.push.apply(tbodies, part.tbody);
                    tbodies = tbodies.concat(part.tbody);
                    if (part.dynamic === true) {
                        dynamicRowTemplate = part;
                    }
                }
                
            });
            passDocPart.innerPdTable.dynamicRowTemplate = angular.copy(dynamicRowTemplate);
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
    
    function performLoadedSection(loadedSection, sectionValues) {
        loadedSection.sectionData = JSON.parse(loadedSection.sectionDataJson);
        if (mainSvc.checkUndefinedNull(sectionValues[loadedSection.sectionKey])) {
            sectionValues[loadedSection.sectionKey] = {};
        }
        if (mainSvc.checkUndefinedNull(sectionValues[loadedSection.sectionKey][loadedSection.sectionEntryId])) {
            sectionValues[loadedSection.sectionKey][loadedSection.sectionEntryId] = {};
        }
        sectionValues[loadedSection.sectionKey][loadedSection.sectionEntryId].version = loadedSection.sectionData.version;
        loadedSection.sectionData.elements.forEach(function (dataValueElem) {
            sectionValues[loadedSection.sectionKey][loadedSection.sectionEntryId][dataValueElem._complexIdx] = angular.copy(dataValueElem);
        });
        return sectionValues;
    }
    
    function rebuildInterfaceWithLoadedData(loadedSection) {
        var tmpSectionData = JSON.parse(loadedSection.sectionDataJson),
            isDynamic = false,
            rowCounter = 0,
            loadedSectionKey = loadedSection.sectionKey;
        tmpSectionData.elements.some(function (elm) {
            if (!mainSvc.checkUndefinedNull(elm._dynamic) && elm._dynamic === true) {
                isDynamic = true;
                return true;
            }
        });
        if (isDynamic === false) {
            return;
        }
            //data for dynamic part            
        tmpSectionData.elements.forEach(function (elm) {
            if (elm._dynamicIdx > rowCounter) {
                rowCounter = elm._dynamicIdx;
            }
        });
        
        if (rowCounter <= 1) { //one row is build when template was loaded
            return;
        }
        
        //rebuild
        var passportSectionStructure = null;
        $scope.data.passport.sections.some(function (pds) {
            if (pds.sectionKey === loadedSectionKey) {
                passportSectionStructure = pds.preparedSection;
                return true;
            }
        });
        
        if (passportSectionStructure === null) {
            console.warn("Passport section not found: " + loadedSectionKey);
            return;
        }
        
        var pssDynamicTablePart = null;
        passportSectionStructure.parts.some(function (part) {
            if (part.partType === "INNER_TABLE") {
                pssDynamicTablePart = part;
                return true;
            }
        });
        
        if (pssDynamicTablePart === null) {
            console.warn("INNER_TABLE at passport section not found.");
            return;
        }
        var rCount = 0;
        for (rCount = 2; rCount <= rowCounter; rCount += 1) {
            $scope.addRowToTable(pssDynamicTablePart);
        }
        //pssDynamicTablePart.innerPdTable.
        
        //console.log(rowCounter);
    }
    
    function successPutCallback(resp) {
        notificationFactory.success();
//        console.log(resp);
        if (mainSvc.checkUndefinedNull(resp) || mainSvc.checkUndefinedNull(resp.data)) {
            console.log("Empty response from server: " + resp);
            return false;
        }
        //update passport.passportData
        $scope.data.passport.passportData.some(function (pd) {
            if (pd.sectionKey === resp.data.sectionKey && pd.sectionEntryId === resp.data.sectionEntryId) {
                pd = angular.copy(resp.data);
            }
        });
//console.log($scope.data.passport);
        //perform section data
        var sectionValues = angular.copy($scope.data.passDocValues);
        $scope.data.passDocValues = performLoadedSection(resp.data, sectionValues);
    }
    
    function successLoadPassportDataCallback(resp) {
        //TODO: comment
//        console.log(resp);
        //hash map
        var sectionValues = {},
            passDocData = angular.copy(resp.data);
        $scope.data.passport.passportData = angular.copy(resp.data);
        passDocData.forEach(function (dataElm) {
            //rebuild interface if it is necessary
            rebuildInterfaceWithLoadedData(dataElm);
            //fill hash map
            sectionValues = performLoadedSection(dataElm, sectionValues);
//            dataElm.sectionData = JSON.parse(dataElm.sectionDataJson);
            //fill hash map values            
//            sectionValues[dataElm.sectionKey] = {};
//            sectionValues[dataElm.sectionKey].version = dataElm.sectionData.version;
//            dataElm.sectionData.elements.forEach(function (dataValueElem) {
//                sectionValues[dataElm.sectionKey][dataValueElem._complexIdx] = angular.copy(dataValueElem);
//            });
        });
//        $scope.data.passport.passportData = passDocData;
        $scope.data.passDocValues = sectionValues;
        $scope.data.currentSectionValues = $scope.data.passDocValues[$scope.data.currentPassDocSection.preparedSection.sectionKey][$scope.data.currentPassDocSection.preparedSection.sectionEntryId || 0];
        $scope.ctrlSettings.passportLoading = false;
//        console.log(passDocData);
//        console.log(sectionValues);
    }
    
    function successLoadSectionEntryDataCallback(resp) {
        if (mainSvc.checkUndefinedNull(resp) || mainSvc.checkUndefinedNull(resp.data)) {
            console.warn("Empty response from server: ");
            console.warn(resp);
            return false;
        }
        if (!angular.isArray(resp.data) || resp.data.length === 0) {
            console.log("Response from server is not array: ");
            console.warn(resp.data);
            return false;
        }
        //update passport.passportData
        var isPassportDataElementFound = false,
            respData = resp.data[0];
        $scope.data.passport.passportData.some(function (pd) {
            if (pd.sectionKey === respData.sectionKey && pd.sectionEntryId === respData.sectionEntryId) {
                isPassportDataElementFound = true;
                pd = angular.copy(resp.data);
                return true;
            }
        });
        if (isPassportDataElementFound === false) {
            $scope.data.passport.passportData.push(angular.copy(respData));
        }
//console.log($scope.data.passport);
        //perform section data
        var sectionValues = angular.copy($scope.data.passDocValues);
//console.log(respData);        
//console.log(respData.sectionDataJson);        
//console.log(JSON.parse(respData.sectionDataJson));        
        $scope.data.passDocValues = performLoadedSection(respData, sectionValues);
    }
    
    function loadPassportData(passport) {
        energoPassportSvc.loadPassportData(passport.id)
            .then(successLoadPassportDataCallback, errorCallback);
    }
    
    function successCreatePassportCallback(response) {
        //TODO: comment
//        console.log(response);
        if (mainSvc.checkUndefinedNull(response) || mainSvc.checkUndefinedNull(response.data)) {
            console.warn("Loaded passport is empty!");
            return false;
        }
        
        var result = [],
            tmp = angular.copy(response.data);
        
        //$scope.data.passport = angular.copy(response.data);
        
        tmp.sections.forEach(function (secTempl) {
            secTempl.preparedSection = preparePassDoc(JSON.parse(secTempl.sectionJson));
            result.push(preparePassDoc(JSON.parse(secTempl.sectionJson)));
        });
//        result = preparePassDoc(result);
        $scope.data.passport = tmp;
        //TODO: comment
//        console.log($scope.data.passport);
//        console.log(result);
//return;        
//        $scope.data.passDocStructure = result;
//        if ($scope.data.passDocStructure.length >= 1) {
        if ($scope.data.passport.sections.length >= 1) {
            $scope.data.currentPassDocSection = $scope.data.passport.sections[INDEX_OF_DEFAULT_SECTION]; //$scope.data.passDocStructure[INDEX_OF_DEFAULT_SECTION];
//            if (mainSvc.checkUndefinedNull($scope.data.currentPassDocSection.values)) {
//                $scope.data.currentPassDocSection.values = {};
//            }
            $scope.data.currentPassDocSection.isSelected = true;
            $timeout(function () {
                $(':input').inputmask();
//                    $(':input').focus();
            }, 0);
        }
        
        //load passport data
        loadPassportData(response.data);
        
//        $scope.ctrlSettings.passportLoading = false;
    }
    
    function createPassDocInit() {
        energoPassportSvc.createPassport()
            .then(successCreatePassportCallback, errorCallback);
    }
    
    function loadPassDoc(id) {
        energoPassportSvc.loadPassports(id)
            .then(successCreatePassportCallback, errorCallback);
    }
    
    function loadEntryData(passportId, sectionId, entryId) {
        energoPassportSvc.loadPassportData(passportId, sectionId, entryId)
            .then(successLoadSectionEntryDataCallback, errorCallback);
    }
    
    function changeSection(part) {
        if (!mainSvc.checkUndefinedNull($scope.data.currentPassDocSection)) {
            $scope.data.currentPassDocSection.isSelected = false;
        }
        if (!mainSvc.checkUndefinedNull($scope.data.currentPassDocSection.entries) && $scope.data.currentPassDocSection.entries.length > 0) {
            $scope.data.currentPassDocSection.entries.forEach(function (entry) {
                entry.isSelected = false;
            });
        }
        $scope.data.currentPassDocSection = part;
        $scope.data.currentPassDocSection.isSelected = true;
        
        //set section values
//console.log($scope.data.passDocValues);
//console.log($scope.data.passDocValues[$scope.data.currentPassDocSection.preparedSection.sectionKey]);
//console.log($scope.data.passDocValues[$scope.data.currentPassDocSection.preparedSection.sectionKey][$scope.data.currentPassDocSection.preparedSection.sectionEntryId || 0]);
        if (mainSvc.checkUndefinedNull($scope.data.passDocValues[$scope.data.currentPassDocSection.preparedSection.sectionKey][$scope.data.currentPassDocSection.preparedSection.sectionEntryId || 0])) {
            $scope.data.passDocValues[$scope.data.currentPassDocSection.preparedSection.sectionKey][$scope.data.currentPassDocSection.preparedSection.sectionEntryId || 0] = {};
            //load entry data
            loadEntryData($scope.data.passport.id, $scope.data.currentPassDocSection.sectionId || $scope.data.currentPassDocSection.id, $scope.data.currentPassDocSection.preparedSection.sectionEntryId || 0);
        }
        $scope.data.currentSectionValues = $scope.data.passDocValues[$scope.data.currentPassDocSection.preparedSection.sectionKey][$scope.data.currentPassDocSection.preparedSection.sectionEntryId || 0];
        
        $timeout(function () {
            $(':input').inputmask();
        }, 0);
    }
    
    $scope.contentsPartSelect = function (part) {
//        console.log(part);
        //if previous section was changed - need it save or cancel changes
        if ($scope.data.isSectionChanged === true) {
            //open window with message and to buttons - save and cancel
        } else {
            changeSection(part);
        }
        
    };
    
    $scope.contentsPartEntrySelect = function (part, entry) {
        changeSection(part);
        //part.isSelected = false;
        //entry.isSelected = true;
        //load section template for entry interface build
        entry.preparedSection = angular.copy(part.preparedSection);
        entry.preparedSection.sectionEntryId = entry.id;
        changeSection(entry);
        //load entry data
    };
    
    $scope.tdBlur = function (cell, values, complexIdx) {
//console.log(cell);
//        console.log(values);        
//        console.log(complexIdx);
        
    };
    
    $scope.cancelEnergoPassportEdit = function () {
        $location.path("/documents/energo-passports");
    };
    
    function prepareTableRowCell(cell, row, rowInd) {
        if (cell.cellType === "VALUE") {
            cell._dynamicIdx = rowInd;
            cell._complexIdx = cell.partKey + row.dynamicSuffix + rowInd + row.valueIdxSuffix + cell.keyValueIdx;
            if (mainSvc.isNumeric(cell.valuePackIdx)) {
                cell._complexIdx += "[" + cell.valuePackIdx + "]";
            }
            //add value to global array
            if (!mainSvc.checkUndefinedNull($scope.data.passDocValues[$scope.data.currentPassDocSection.sectionKey])) {
                $scope.data.passDocValues[$scope.data.currentPassDocSection.sectionKey][$scope.data.currentPassDocSection.preparedSection.sectionEntryId || 0][cell._complexIdx] = angular.copy(cell);
            }
        }
        if (mainSvc.checkUndefinedNull(cell.elements)) {
            return;
        }
        cell.elements.forEach(function (subElem) {
            prepareTableRowCell(subElem, row, rowInd);
        });
    }
    
    /**
    Add row at dynamic table.
    Need add value to global values hash map
    */
    $scope.addRowToTable = function (part) {
        if (part.innerPdTable.parts.length < 2) {
            console.error("Incorrect part.innerPdTable.parts: " + part);
            return false;
        }
        //find dynamic part
        var dynamicRowTemplate = null;
        part.innerPdTable.parts.some(function (part) {
            if (part.dynamic === true) {
                dynamicRowTemplate = part;
                return true;
            }
        });
        if (dynamicRowTemplate === null) {
            console.error("Not enough dynamic parts!");
            console.error(part);
        }
        var addingRow = angular.copy(dynamicRowTemplate);
        addingRow.elements.forEach(function (rowElem) {
            if (rowElem.__type === 'Counter') {
                if (angular.isUndefined(part.innerPdTable.counterValue)) {
                    //calculate dynamic rows count
                    var counter = 0;
                    part.innerPdTable.parts.forEach(function (innerPart) {
                        if (innerPart.partType === "ROW" && innerPart.dynamic === true) {
                            counter += 1;
                        }
                    });
                    part.innerPdTable.counterValue = counter;
                }
                part.innerPdTable.counterValue += 1;
                rowElem._dynamicIdx = part.innerPdTable.counterValue;
                rowElem._complexIdx = rowElem.partKey + addingRow.dynamicSuffix + part.innerPdTable.counterValue + addingRow.valueIdxSuffix + rowElem.keyValueIdx;
                if (mainSvc.isNumeric(rowElem.valuePackIdx)) {
                    rowElem._complexIdx += "[" + rowElem.valuePackIdx + "]";
                }
                //add value to global array
//console.log($scope.data.currentPassDocSection);
//console.log($scope.data.passDocValues);
//console.log($scope.data.currentSectionValues);                
                if (!mainSvc.checkUndefinedNull($scope.data.currentSectionValues)) {
                    $scope.data.currentSectionValues[rowElem._complexIdx] = angular.copy(rowElem);
                }
                rowElem.value = part.innerPdTable.counterValue;
            } else {
                prepareTableRowCell(rowElem, addingRow, part.innerPdTable.counterValue);
            }
            
        });
        prepareTableRowRecursion(addingRow);
        part.innerPdTable.tbodies = part.innerPdTable.tbodies.concat(addingRow.tbody);
        
        $timeout(function () {
            $(':input').inputmask();
//                    $(':input').focus();
        }, 0);
        //if need - clear row values;
//        part.innerPdTable.parts.push(addingRow);
    };
    
    $scope.deleteRowFromTable = function (part, ind) {
        //ind - row index at tbodies, it is not real row index;
        var dynamicRowTemplate = null;
        part.innerPdTable.parts.some(function (part) {
            if (part.dynamic === true) {
                dynamicRowTemplate = part;
                return true;
            }
        });
        if (dynamicRowTemplate === null) {
            console.error("Not enough dynamic parts!");
            console.error(part);
        }
        var partLength = dynamicRowTemplate.tbody.length,
            deleteRow = angular.copy(part.innerPdTable.tbodies[ind - dynamicRowTemplate.tbody.length + 1]);
        //TODO: delete values from Values array; ??????????????????? Need delete last row value?
        //move array elements for 1 row ahead and delete last row
//        deleteRow.tds.forEach(function (td) {
//            if (mainSvc.checkUndefinedNull(td._complexIdx)) {
//                return;
//            }
//            if (!mainSvc.checkUndefinedNull($scope.data.passDocValues[$scope.data.currentPassDocSection.sectionKey]) && !mainSvc.checkUndefinedNull($scope.data.passDocValues[$scope.data.currentPassDocSection.sectionKey][td._complexIdx])) {
//                delete $scope.data.passDocValues[$scope.data.currentPassDocSection.sectionKey][td._complexIdx];
//            }
//        });
        ///// end ????????????????????????????????
        
        
        //search _dynamicIdx for replace data at values array
        var deletedRows = part.innerPdTable.tbodies.splice(ind - partLength + 1, partLength);
        var dataIndex = 0;
        deletedRows.forEach(function (deleteRow) {
            deleteRow.tds.some(function (td) {
                if (mainSvc.checkUndefinedNull(td._dynamicIdx)) {
                    return false;
                }
                dataIndex = td._dynamicIdx;
            });
        });
        //replace data at values array:
        //all values with index > dataIndex => [dataIndex] = [dataIndex -1]
        if (dataIndex > 0) {
//            var vkey;
//            for (vkey in $scope.data.passDocValues[$scope.data.currentPassDocSection.sectionKey]) {
//                if ($scope.data.passDocValues[$scope.data.currentPassDocSection.sectionKey].hasOwnProperty(vkey) && $scope.data.passDocValues[$scope.data.currentPassDocSection.sectionKey][vkey]._dynamicIdx === dataIndex) {
//                    delete $scope.data.passDocValues[$scope.data.currentPassDocSection.sectionKey][vkey];
//                }
//            }
            var maxDataIdx = part.innerPdTable.counterValue;
            var vkey, dataIndexCounter;
            for (dataIndexCounter = dataIndex; dataIndexCounter < maxDataIdx; dataIndexCounter += 1) {
//console.log($scope.data.currentSectionValues);                
                for (vkey in  $scope.data.currentSectionValues) {
//console.log(vkey);
//console.log($scope.data.currentSectionValues[vkey]);                    
                    if ($scope.data.currentSectionValues.hasOwnProperty(vkey) && !mainSvc.checkUndefinedNull($scope.data.currentSectionValues[vkey]) && $scope.data.currentSectionValues[vkey].hasOwnProperty("_dynamicIdx") && $scope.data.currentSectionValues[vkey]._dynamicIdx === dataIndexCounter) {
                        var currentVal = $scope.data.currentSectionValues[vkey];
                        var newComplexIdx = currentVal.partKey + dynamicRowTemplate.dynamicSuffix + (dataIndexCounter + 1) + dynamicRowTemplate.valueIdxSuffix + currentVal.keyValueIdx;
                        if (mainSvc.isNumeric(currentVal.valuePackIdx)) {
                            newComplexIdx += "[" + currentVal.valuePackIdx + "]";
                        }
                        
//                        console.log("dataIndexCounter: " + dataIndexCounter);
//                        console.log("currentVal:");
//                        console.log(currentVal);
//                        console.log("currentVal._complexIdx: " + currentVal._complexIdx);
//                        console.log("newComplexIdx: " + newComplexIdx);
                        
                        var newVal = angular.copy($scope.data.currentSectionValues[newComplexIdx]);
                        newVal._dynamicIdx = dataIndexCounter;
                        newVal._complexIdx = currentVal._complexIdx;
                        
//                        console.log("newVal:");
//                        console.log(newVal);
                        
                        $scope.data.currentSectionValues[vkey] = newVal;
                    }
                    
                }
            }
            //delete last row from values array
            for (vkey in  $scope.data.currentSectionValues) {
                if ($scope.data.currentSectionValues.hasOwnProperty(vkey)  && !mainSvc.checkUndefinedNull($scope.data.currentSectionValues[vkey]) && $scope.data.currentSectionValues[vkey].hasOwnProperty("_dynamicIdx") && $scope.data.currentSectionValues[vkey]._dynamicIdx === maxDataIdx) {
                    delete $scope.data.currentSectionValues[vkey];
                }
            }
            
//            console.log("New values array: ");
//            console.log($scope.data.passDocValues[$scope.data.currentPassDocSection.sectionKey]);
        }
        
        //TODO: recount table rows, rebuild table
        var counter = 0;
        part.innerPdTable.tbodies.forEach(function (row) {
//            if (row.startPartRow === true) {
            row.tds.some(function (td) {
                if (td.__type === 'Counter') {
                    counter += 1;
                    td.value = counter;
//                        return true;
                }
                td._dynamicIdx = counter;
                td._complexIdx = td.partKey + dynamicRowTemplate.dynamicSuffix + counter + dynamicRowTemplate.valueIdxSuffix + td.keyValueIdx;
//                    rowElem._complexIdx = rowElem.partKey + addingRow.dynamicSuffix + part.innerPdTable.counterValue + addingRow.valueIdxSuffix + rowElem.keyValueIdx;
                if (mainSvc.isNumeric(td.valuePackIdx)) {
                    td._complexIdx += "[" + td.valuePackIdx + "]";
                }
            });
                
//            } else {
//                row.tds.some(function (td) {
//                    td._dynamicIdx = counter;
//                    td._complexIdx = td.partKey + part.innerPdTable.parts[1].dynamicSuffix + counter + part.innerPdTable.parts[1].valueIdxSuffix + td.keyValueIdx;
//                    if (mainSvc.isNumeric(td.valuePackIdx)) {
//                        td._complexIdx += "[" + td.valuePackIdx + "]";
//                    }
//                });
//            }
        });
        
//        console.log("new part.tbodies:");
//        console.log(part.innerPdTable.tbodies);
        
        part.innerPdTable.counterValue = counter;
    };
    
    $scope.savePassportSection = function (passportSection) {
//        console.log(passportSection);
//        console.log($scope.data.passDocValues[passportSection.preparedSection.sectionKey]);
        //prepare value array
        var sectionData = {},
            sectionValues = $scope.data.passDocValues[passportSection.preparedSection.sectionKey][passportSection.preparedSection.sectionEntryId || 0];
        sectionData.version = sectionValues.version;
        delete sectionValues.version;
        sectionData.elements = [];
        var vkey;
        for (vkey in sectionValues) {
            if (sectionValues.hasOwnProperty(vkey)) {
                sectionData.elements.push(sectionValues[vkey]);
            }
        }
//        console.log(sectionData);
        var currentPassportSection = null;
        $scope.data.passport.passportData.some(function (pd) {
            if (pd.sectionKey === passportSection.preparedSection.sectionKey && pd.sectionEntryId === (passportSection.preparedSection.sectionEntryId || 0)) {
                currentPassportSection = angular.copy(pd);
            }
        });
        if (currentPassportSection === null) {
            console.warn("currentPassportSection === null!");
        }
        currentPassportSection.sectionDataJson = JSON.stringify(sectionData);
//console.log(currentPassportSection);
        //call save function: passport id, section key, values
        energoPassportSvc.savePassport($scope.data.passport.id, currentPassportSection)
            .then(successPutCallback, errorCallback);
    };
    
// **** work with section entry ***
    
    $scope.emptyString = function (str) {
        return mainSvc.checkUndefinedEmptyNullValue(str);
    };
    
    $scope.selectEntry = function () {
        //TODO: add code
    };
    
    $scope.addEntryInit = function () {
        $scope.data.currentSectionEntry = {};
        $('#showSectionEntryOptionModal').modal();
    };
    
    $scope.editEntryInit = function (entry) {
        $scope.data.currentSectionEntry = angular.copy(entry);
        $('#showSectionEntryOptionModal').modal();
    }
    
    function successSaveEntryCallback(resp) {
//        console.log(resp);
        if ($scope.data.currentPassDocSection.hasEntries !== true) {
            console.error("Секция:");
            console.error($scope.data.currentPassDocSection);
            console.error("не может иметь дочерних секций!");
            return false;
        }
        $scope.data.currentPassDocSection.entries.push(resp.data);
        $('#showSectionEntryOptionModal').modal('hide');
        
    }
    
    $scope.saveEntry = function (passportId, sectionId, entry) {
        //TODO: check passportId, sectionId, entry
        
        if (mainSvc.checkUndefinedNull(passportId) || mainSvc.checkUndefinedNull(sectionId) || mainSvc.checkUndefinedNull(entry)) {
            notificationFactory.errorInfo("Ошибка", "Некорректно заданы параметры для записи секции.");
            console.error("passportId: " + passportId);
            console.error("sectionId:" + sectionId);
            console.error("SectionEntry:");
            console.error(entry);
            return false;
        }
        //add sectionId to entry
        entry.sectionId = sectionId;
        energoPassportSvc.saveEntry(passportId, sectionId, entry)
            .then(successSaveEntryCallback, errorCallback);
    };
    
    $('#showSectionEntryOptionModal').on('shown.bs.modal', function () {
        $('#inputSectionEntryName').focus();
        $('#inputSectionOrder').inputmask();
    });
// **** end of work with section entry ***    
    
    function initCtrl() {
        var routeParams = $routeParams;
        if (routeParams.param === "new") {
            createPassDocInit();
        } else {
            var passDocId = Number(routeParams.param);
            loadPassDoc(passDocId);
//            console.log("Don't understand request!");
        }
    }
    
    initCtrl();
    
}]);