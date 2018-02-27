/*jslint node: true*/
/*global angular*/
'use strict';

var app = angular.module('portalNMC');
app.controller('ObjectsCtrl', ['$scope', '$rootScope', '$http', 'mainSvc', function ($scope, $rootScope, $http, mainSvc) {
//console.log("Objects ctrl.");
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.pageAccess = false;
    $scope.ctrlSettings.pageCtxId = "object_list_view_page";
    $rootScope.ctxId = "object_list_view_page";
    
    $scope.ctrlSettings.loadingPermissions = mainSvc.getLoadingServicePermissionFlag();
    $scope.ctrlSettings.pageAccess = mainSvc.checkContext($scope.ctrlSettings.pageCtxId) || mainSvc.isCabinet();
}]);