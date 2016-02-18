'use strict';

angular.module('portalNMC')
.controller('ObjectsCtrl', function($scope, $rootScope, $cookies, $http, mainSvc){
console.log("Objects ctrl.");
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.pageAccess = false;
    $scope.ctrlSettings.pageCtxId = "object_list_view_page";
    $rootScope.ctxId = "object_list_view_page";
    
    $scope.ctrlSettings.loadingPermissions = mainSvc.getLoadingServicePermissionFlag();
    $scope.ctrlSettings.pageAccess = mainSvc.checkContext($scope.ctrlSettings.pageCtxId);
    
    
    
});