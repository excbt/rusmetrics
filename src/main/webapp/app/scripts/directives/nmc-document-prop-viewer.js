/*jslint node: true*/
/*global angular, $*/
'use strict';
angular.module('portalNMC').directive('nmcDocumentPropViewer', function () {
    return {
        restrict: "AE",
        replace: false,
        scope: {
            currentDocument: '=',
            btnClick: '&',
            isReadOnly: '&',
            saveBtnDisabled: '&'
        },
        templateUrl: 'scripts/directives/templates/nmc-document-prop-viewer.html',
        controller: ['$scope', 'energoPassportSvc', 'mainSvc', function ($scope, energoPassportSvc, mainSvc) {
            
            var DATE_FORMAT_FOR_DATEPICKER = "yy-mm-dd"; //jquery datepicker format
            
            $scope.data = {};
            $scope.data.documentTypes = energoPassportSvc.getDocumentTypes();
            $scope.data.energyDeclarationForms = energoPassportSvc.getEnergyDeclarationForms();
            
//            $scope.isReadOnly = function () {
//                return mainSvc.isReadonly();
//            };
            
            $scope.emptyString = function (str) {
                return mainSvc.checkUndefinedEmptyNullValue(str);
            };
            
            $('#showDocumentPropertiesModal').on("shown.bs.modal", function () {
                $("#inputEnergyDocName").focus();
                var viewDateformat = mainSvc.getDetepickerSettingsFullView();
                viewDateformat.dateFormat = DATE_FORMAT_FOR_DATEPICKER;
                $('#inputEnergyDocDate').datepicker(viewDateformat);
            });
        }]
    };
});