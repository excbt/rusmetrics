'use strict';
angular.module('portalNMC')
.controller('MngmtUsersCtrl', ['$scope', '$rootScope', '$resource', 'crudGridDataFactory', 'notificationFactory', '$http', 'objectSvc', 'mainSvc', function ($scope, $rootScope, $resource, crudGridDataFactory, notificationFactory, $http, objectSvc, mainSvc) {                
console.log('Run user management controller.');
    
    $scope.extraProps = {"idColumnName":"id", "defaultOrderBy" : "fullName", "nameColumnName":"fullName"};//angular.fromJson($attrs.exprops);
        //controller settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.orgUrl = "../api/rma/contObjects/cmOrganizations";
    $scope.ctrlSettings.clientsUrl = "../api/rma/subscribers";
    $scope.ctrlSettings.rmaUrl = "../api/rma";
    $scope.ctrlSettings.userUrlSuffix = "/subscrUsers";
    
    //client columns
    $scope.ctrlSettings.userColumns =[
        {
            "name": "userName",
            "caption": "Логин",
            "class": "col-md-2"
        },
        {
            "name": "firstName",
            "caption": "Имя",
            "class": "col-md-3"
        },
        {
            "name": "lastName",
            "caption": "Фамилия",
            "class": "col-md-3"
        },
        {
            "name": "userComment",
            "caption": "Комментарий",
            "class": "col-md-4"
        }
    ];
    //data
    $scope.data={};
    $scope.data.organizations = [];
    $scope.data.clients = [];
    $scope.data.users = [];
    $scope.data.currentUser = {};
    $scope.data.currentClient = {};
    
    //get users
    var getUsers = function(clientId){
        var targetUrl = $scope.ctrlSettings.rmaUrl+"/"+clientId+$scope.ctrlSettings.userUrlSuffix;
        $http.get(targetUrl)
        .then(function(response){
            $scope.data.users = response.data;
console.log($scope.data.users);            
        },
             function(e){
            console.log(e);
        });
    };
    
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
            $scope.data.currentClient.id = $scope.data.clients[0].id;
            getUsers($scope.data.currentClient.id);
           
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
    getOrganizations(); 
    
    $scope.selectedItem = function (item) {
        var curObject = angular.copy(item);
        $scope.data.currentUser = curObject;
    };
    
    $scope.addUser =  function(){
        $scope.data.currentUser = {};
        $('#showUserOptionModal').modal();
    };
    
    //data processing
     var successCallback = function (e, cb) {                    
        notificationFactory.success();
        $('#deleteUserModal').modal('hide');
        $('#showUserOptionModal').modal('hide');
         getUsers($scope.data.currentClient.id);
    };

    var errorCallback = function (e) {
        notificationFactory.errorInfo(e.statusText,e.data.description); 
        console.log(e);
    };
    
    $scope.sendUserToServer = function(obj){
//        obj.organizationId = 726;
//        obj.timezoneDef = null;"64166467"
        var url = $scope.ctrlSettings.rmaUrl+"/"+$scope.data.currentClient.id+$scope.ctrlSettings.userUrlSuffix;                    
        if (angular.isDefined(obj.id)&&(obj.id!=null)){
            $scope.updateObject(url, obj);
        }else{
            $scope.addObject(url,obj);
        };
    };
    
    $scope.addObject = function (url, obj) {  
        url+="/?isAdmin="+obj.isAdmin+"&newPassword="+obj.password;
        crudGridDataFactory(url).save(obj, successCallback, errorCallback);
    };

    $scope.deleteObject = function (obj) {
        var url = $scope.ctrlSettings.rmaUrl+"/"+$scope.data.currentClient.id+$scope.ctrlSettings.userUrlSuffix;                 
        crudGridDataFactory(url).delete({ id: obj[$scope.extraProps.idColumnName] }, successCallback, errorCallback);
    };

    $scope.updateObject = function (url, object) {
        var params = { id: object[$scope.extraProps.idColumnName],
                     isAdmin: object.isAdmin};
        if (angular.isDefined(object.password)&&(object.password!=null)&&(object.password !="")){
            params.newPassword = object.password;
        };
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
    
    $scope.changeClient = function(){
        getUsers($scope.data.currentClient.id);
    };
    
    //chekers
    $scope.checkPassword = function(){
        var result = false;
        result = !($scope.data.currentUser.password!=$scope.data.currentUser.passwordConfirm);
        return result;
    };
    
}]);