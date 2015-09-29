//ManagementServicesCtrl
'use strict';
angular.module('portalNMC')
.controller('ManagementServicesCtrl', ['$scope', '$http', function($scope, $http){
    console.log("ManagementServicesCtrl run.");
    //ctrl settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.dateFormat = "DD.MM.YYYY"; //date format
    
    $scope.ctrlSettings.servicesUrl = "../api/subscr/manage/service";
    $scope.ctrlSettings.packagesUrl = $scope.ctrlSettings.servicesUrl + "/servicePackList";
    $scope.ctrlSettings.itemsUrl = $scope.ctrlSettings.servicesUrl+ "/serviceItemList";
    
    //package columns definition
    //not used
    $scope.ctrlSettings.packageColumns = [
        {"name":"name", "header" : "Название", "class":"col-md-4"}
        ,{"name":"description", "header" : "Описание", "class":"col-md-5"}
        ,{"name":"cost", "header" : "Стоимость, руб.", "class":"col-md-1"}
    ];
    //The packages, wich selected by the subscriber
    $scope.packages =[];
    //The packages, which available for the subscriber
    $scope.availablePackages = [];
    
    //get packages
    $scope.getPackages = function(url){
        var targetUrl = url;
        $http.get(targetUrl).then(function(response){
            var tmp = response.data;
            $scope.availablePackages =  tmp;
        },
                                 function(e){
            console.log(e);
        });
    };
    
    $scope.getPackages($scope.ctrlSettings.packagesUrl);
    
    $scope.editPackages = function(){
        
    };
    //save changes
    $scope.savePackages = function(){
        
    };
    
    //toggle show/hide package consist
    $scope.toggleShowGroupDetails = function(pack){
        pack.showDetailsFlag = !pack.showDetailsFlag;
    };
    
}]);