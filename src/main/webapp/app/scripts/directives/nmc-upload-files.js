/*jslint node: true*/
/*global angular, $*/
'use strict';
angular.module("portalNMC")
    .directive("uploadFilesModal", function () {
        return {
            replace: true,
            restrict: "AEC",
            templateUrl: "scripts/directives/templates/nmc-upload-files-template.html",
            controller: ['$scope', 'FileUploader', 'mainSvc', 'notificationFactory', '$timeout', function ($scope, FileUploader, mainSvc, notificationFactory, $timeout) {
                
                function errorCallback(e) {
                    var errorObj = mainSvc.errorCallbackHandler(e);
                    notificationFactory.errorInfo(errorObj.caption, errorObj.description);
                }
                
                //file upload settings
                $scope.initFileUploader =  function () {
//console.log("initFileUploader");                    
                    var url = "";
                    $scope.uploader = new FileUploader({url: url});
                    $scope.uploader.onErrorItem = function (fileItem, response, status, headers) {
                        console.info('onErrorItem', status, response);
                        errorCallback(response);
                        $scope.successOnUpload = false;
                        $scope.errorOnUpload = true;
                        $scope.uploadFlag = false;
                        //notificationFactory.errorInfo(response.resultCode, response.description);
                    };

                    $scope.uploader.onSuccessItem = function (item, response, status, headers) {
            //console.log(item);
                    };
                    $scope.uploader.onCompleteAll = function () {
//                        $scope.getData(1);
                        $scope.successOnUpload = true;
                        $scope.errorOnUpload = false;
            //            $scope.$apply();
                    };

                    //register listeners for window
                    $("#upLoadFilesModal").on('shown.bs.modal', function () {
//console.log("upLoadFileModal shown");
//                        $timeout(function () {
//                            $scope.initFileUploader();
//                        }, 10);

                    });
                    $timeout(function () {
                       

                        $("#upLoadFilesModal").on('hidden.bs.modal', function () {
//console.log("upLoadFileModal hidden");        
//                            $scope.uploader = null;
                            $scope.uploadFlag = false;
                            $scope.successOnUpload = false;
                            $scope.errorOnUpload = false;
                            $scope.$apply();
                        });
                    }, 10);
                    
                };
                
                function initDirective() {
//console.log("init directive");                    
                    $scope.initFileUploader();
                }
                
                initDirective();
            }]
        };
    });