/*jslint node: true*/
/*global angular, $*/
'use strict';
angular.module("portalNMC")
    .directive("uploadFilesModal", function () {
        return {
            replace: true,
            restrict: "AEC",
            scope: true,
            templateUrl: "scripts/directives/templates/nmc-upload-files-template.html",
            controller: ['$scope', 'FileUploader', 'mainSvc', 'notificationFactory', '$timeout', function ($scope, FileUploader, mainSvc, notificationFactory, $timeout) {
//console.log($scope);                
                $scope.serverUrl = "../api/subscr/service/datahwater/contObjects/upload";
                
                function errorCallback(e) {
                    var errorObj = mainSvc.errorCallbackHandler(e);
                    notificationFactory.errorInfo(errorObj.caption, errorObj.description);
                }
                
                //file upload settings
                $scope.initFileUploader =  function () {
//console.log("initFileUploader");                    
//                    var url = "../api/subscr/service/datahwater/contObjects/upload";
                    $scope.uploader = new FileUploader({url: $scope.serverUrl});
//                    $scope.uploader = new FileUploader();
//console.log($scope.uploader);
//console.log(typeof $scope.uploader);                    
                    $scope.uploader.onErrorItem = function (fileItem, response, status, headers) {
//console.log("onErrorItem");                        
                        console.info('onErrorItem', status, response);
                        errorCallback(response);
                        $scope.successOnUpload = false;
                        $scope.errorOnUpload = true;
                        $scope.uploadFlag = false;
                        //notificationFactory.errorInfo(response.resultCode, response.description);
                    };
                    
//                    $scope.uploader.onBeforeUploadItem(function (item) {
//                        item.url = $scope.serverUrl;
//console.log($scope.serverUrl);
//                    });

                    $scope.uploader.onSuccessItem = function (item, response, status, headers) {
            //console.log(item);
                    };
                    $scope.uploader.onCompleteAll = function () {
//console.log("onCompleteAll");                        
//                        $scope.getData(1);
                        $scope.onUploadAll = true;
//                        $scope.successOnUpload = true;
//                        $scope.errorOnUpload = false;
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
                            $scope.uploader.clearQueue();
                            $scope.uploadFlag = false;
                            $scope.successOnUpload = false;
                            $scope.errorOnUpload = false;
                            $scope.onUploadAll = true;
                            $scope.$apply();
                        });
                    }, 10);
                    
                };
                
                //Upload file with the indicator data to the server
                $scope.uploadFiles = function () {
//console.log("uploadFiles: " +$scope.serverUrl);
//                    $scope.uploader.url = $scope.serverUrl;                                        
                    $scope.uploadFlag = true;
//console.log($scope.uploader);
                    $scope.uploader.uploadAll();
                };
                
//                $scope.$watch('serverUrl', function (newval, oldval) {
//                    console.log("watch ");
//                    console.log(newval);
//                    console.log(oldval);
//                    $scope.uploader.onBeforeUploadItem(function (item) {
//                        item.url = $scope.serverUrl;
//console.log($scope.serverUrl);
//                    });
//                });
                
                function initDirective() {
//console.log("init directive");                    
                    $scope.initFileUploader();
                }
                
                initDirective();
            }]
        };
    });