'use strict';
angular.module('portalNMC')
.controller('MngmtClientsCtrl', ['$scope', '$http', function($scope, $http){
console.log('Run Client management controller.'); 
    
    //controller settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.orgUrl = "../api/rma/contObjects/cmOrganizations";
    $scope.ctrlSettings.clientsUrl = "../api/rma/subscribers";
    //data
    $scope.data={};
    $scope.data.organizations = [];
    $scope.data.clients = [];
    $scope.data.currentClient = {};
    
//    get subscribers
    var getClients = function(){
        var targetUrl = $scope.ctrlSettings.clientsUrl;
        $http.get(targetUrl)
        .then(function(response){
            $scope.data.clients = response.data;
console.log($scope.data.clients);            
        },
             function(e){
            console.log(e);
        });
    };
    
    //get organizations
    var getOrganizations = function(){
        var targetUrl = $scope.ctrlSettings.orgUrl
        $http.get(targetUrl)
        .then(function(response){
            $scope.data.organizations =  response.data;
console.log($scope.data.organizations);
            getClients();
        },
             function(e){
            console.log(e);
        });
    };
    getOrganizations();
    
}]);