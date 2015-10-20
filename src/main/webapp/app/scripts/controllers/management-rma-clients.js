'use strict';
angular.module('portalNMC')
.controller('MngmtClientsCtrl', ['$scope', '$http','objectSvc', 'notificationFactory', 'crudGridDataFactory', function($scope, $http, objectSvc, notificationFactory, crudGridDataFactory){
console.log('Run Client management controller.');
    
    $scope.extraProps = {"idColumnName":"id", "defaultOrderBy" : "fullName", "nameColumnName":"fullName"};//angular.fromJson($attrs.exprops);
    $scope.orderBy = { field: $scope.extraProps["defaultOrderBy"], asc: true};
    
    //controller settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.orgUrl = "../api/rma/contObjects/cmOrganizations";
    $scope.ctrlSettings.clientsUrl = "../api/rma/subscribers";
    
    //client columns
    $scope.ctrlSettings.clientColumns =[
        {
            "name": "subscriberName",
            "caption": "Наименование",
            "class": "col-md-3"
        },
        {
            "name": "info",
            "caption": "Информация",
            "class": "col-md-3"
        },
        {
            "name": "comment",
            "caption": "Комментарий",
            "class": "col-md-2"
        },
        {
            "name": "organizationName",
            "caption": "Организация",
            "class": "col-md-2"
        }
    ];
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
            response.data.forEach(function(el){
                el.organizationName = el.organization.organizationFullName;
            });
            $scope.data.clients = response.data;
            
//console.log($scope.data.clients);            
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
//console.log($scope.data.organizations);
            getClients();
        },
             function(e){
            console.log(e);
        });
    };
    
    $scope.selectClient = function(client){
        $scope.data.currentClient = client;
    };
    $scope.selectedItem = function (item) {
        var curObject = angular.copy(item);
        $scope.data.currentClient = curObject;
    };
    
    $scope.addClient = function(){
        $scope.data.currentClient = {};
        $('#showClientOptionModal').modal();
    };
    
    // get timezones
    var getTimezones = function(){
        objectSvc.getTimezones()
        .then(function(response){
            $scope.data.timezones = response.data;                     
            getOrganizations();
        });
    };
    getTimezones();
    
    //data processing
     var successCallback = function (e, cb) {                    
        notificationFactory.success();
        $('#deleteClientModal').modal('hide');
        $('#showClientOptionModal').modal('hide');
         getClients();
    };
    
    var successPostCallback = function (e) {
        successCallback(e, null);
        location.reload();
    };

    var errorCallback = function (e) {
        notificationFactory.errorInfo(e.statusText,e.data.description); 
        console.log(e);
    };
    
    $scope.sendClientToServer = function(obj){
        obj.organizationId = 726;
        obj.timezoneDef = null;
        var url = $scope.ctrlSettings.clientsUrl;                    
        if (angular.isDefined(obj.id)&&(obj.id!=null)){
            $scope.updateObject(url, obj);
        }else{
            $scope.addObject(url,obj);
        };
    };
    
    $scope.addObject = function (url, obj) {                    
        crudGridDataFactory(url).save(obj, successCallback, errorCallback);
    };

    $scope.deleteObject = function (obj) {
        var url = $scope.ctrlSettings.clientsUrl;                  
        crudGridDataFactory(url).delete({ id: obj[$scope.extraProps.idColumnName] }, successCallback, errorCallback);
    };

    $scope.updateObject = function (url, object) {
        var params = { id: object[$scope.extraProps.idColumnName]};
        crudGridDataFactory(url).update(params, object, successCallback, errorCallback);
    };
    
    $scope.deleteObjectInit = function(object){
        $scope.selectedItem(object);
        //generation confirm code
        $scope.confirmCode = null;
        $scope.firstNum = Math.round(Math.random()*100);
        $scope.secondNum = Math.round(Math.random()*100);
        $scope.sumNums = $scope.firstNum + $scope.secondNum;
    };
    
}]);