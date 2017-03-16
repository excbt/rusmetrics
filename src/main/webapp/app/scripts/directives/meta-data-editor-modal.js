/*jslint node: true*/
/*global angular, $*/
'use strict';
angular.module('portalNMC')
    .directive('metaDataEditorModal', function () {
        return {
            restrict: "AE",
            replace: true,
            scope: {
                currentZpoint: "=",
                readOnly: "@",
                showOkButton: "@",
                btnClick: "&"
            },
            templateUrl: 'scripts/directives/templates/meta-data-editor-modal.html',
            controller: ['$scope', 'mainSvc', function ($scope, mainSvc) {

                $scope.zpointMetadataIntegratorsFlag = false;

                $scope.data = {};
                $scope.data.metadataSchema = [
                    {
                        header: 'Поле источник',
                        headClass : 'col-xs-3 col-md-3',
                        name: 'srcProp',
                        type: 'select_src_field'
                    }, {
                        header: 'Делитель',
                        headClass : 'col-xs-1 col-md-1',
                        name: 'srcPropDivision',
                        type: 'input/text',
                        disabled: false
                    }, {
                        header: 'Единицы измерения источника',
                        headClass : 'col-xs-1 col-md-1',
                        name: 'srcMeasureUnit',
                        type: 'select_measure_units',
                        disabled: false
                    }, {
                        header: 'Единицы измерения приемника',
                        headClass : 'col-xs-1 col-md-1',
                        name: 'destMeasureUnit',
                        type: 'select_measure_units',
                        disabled: false
                    }, {
                        header: 'Поле приемник',
                        headClass : 'col-xs-1 col-md-1',
                        name: 'destProp',
                        type: 'input/text',
                        disabled: true
                    }, {
                        header: 'Функция',
                        headClass : 'col-xs-1 col-md-1',
                        name: 'propFunc',
                        type: 'input/text',
                        disabled: true
                    }
                ];

                $scope.isDisabled = function () {
                    return $scope.readOnly;
                };

                                //set tooltips for meta device fields
                function setMetaToolTips() {
                    $scope.currentZpoint.metaData.metaData.forEach(function (metaField) {
                        var tmpSrc = null,
                            helpText = "";

                        $scope.currentZpoint.metaData.srcProp.some(function (src) {
                            if (metaField.srcProp === src.columnName) {
                                tmpSrc = src;
                                return true;
                            }
                        });
                        if (mainSvc.checkUndefinedNull(tmpSrc) || mainSvc.checkUndefinedNull(tmpSrc.deviceMapping) || mainSvc.checkUndefinedNull(tmpSrc.deviceMappingInfo)) {

                            return false;
                        }
                        metaField.haveToolTip = true;
                        helpText += "<b>Ячейка памяти прибора:</b> " + tmpSrc.deviceMapping + "<br><br>";
                        helpText += "<b>Назначение:</b> <br> " + tmpSrc.deviceMappingInfo;
                        var targetElem = "#srcHelpBtn" + metaField.metaOrder + "srcProp";
                        mainSvc.setToolTip("Описание поля источника", helpText, targetElem, targetElem, 10, 500);
                    });
                }

                $scope.changeMetaToolTips = function () {
                    setMetaToolTips();
                };

                $("#metaDataEditorModal").on('shown.bs.modal', function () {
                    setMetaToolTips();
                });
            }]
        };
    });