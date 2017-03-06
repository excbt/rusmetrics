/*jslint node: true*/
/*global angular, $*/
'use strict';
angular.module('portalNMC')
    .directive('nmcCreationReportModal', function () {
        return {
            replace: true,
            templateUrl: "scripts/directives/templates/nmc-creation-report-modal.html",
            controller: ['$scope', 'mainSvc', function ($scope, mainSvc) {

                //**********************************************************
                //Creation report window
                //**********************************************************

                $('#creationReportModal').on('hidden.bs.modal', function () {
                    $scope.createReportWithParamsRequestCancel();
    //                $scope.createReportCancel();
                    $scope.createReportWithParamsInProgress = false;
                    $scope.createReportProgress = false;
                });
            }]
        };
    });