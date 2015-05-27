angular.module('portalNMK')
.controller('ObjectGroupsCtrl', ['$scope', 'crudGridDataFactory', 'notificationFactory', function($scope, crudGridDataFactory, notificationFactory){
    //
    $scope.groups = [
        {'name':"Group 1"},
        {'name': "group 2"}
    ];
    $scope.objects = [];
    $scope.objectsBackup = [];
    $scope.currentGroup = {};
    
    $scope.objectUrl = "../api/subscr/contObjects";
    $scope.groupUrl = "";
    
    var successCallback = function (e) {
        notificationFactory.success();
        $('#deleteGroupModal').modal('hide');
        $('#edit_group').modal('hide');
        $scope.currentGroup={};
        $scope.getData($scope.groupUrl, "groups");

    };

    var errorCallback = function (e) {
        notificationFactory.errorInfo(e.statusText,e.data.description);       
    };
    
    function performObjectArray(){
        var result = [];
        for (var i=0; i<$scope.objects.length;i++){
            if ($scope.objects[i].selected){
                result.push($scope.objects[i].id);
            };
        };
        return result;
    };

    $scope.saveGroup = function () {
        crudGridDataFactory($scope.groupUrl).save($scope.object, successCallback, errorCallback);
    };

    $scope.deleteGroup = function (object) {
        $scope.groups.splice($scope.groups.indexOf(object),1);
        crudGridDataFactory($scope.groupUrl).delete({ id: object[$scope.extraProps.idColumnName] }, successCallback, errorCallback);
    };

    $scope.updateGroup = function (object) {
        crudGridDataFactory($scope.groupUrl).update({ id: object[$scope.extraProps.idColumnName] }, object, successCallback, errorCallback);
    };

    $scope.getData = function (url, type) {
        crudGridDataFactory(url).query(function (data) {
            switch (type){
                case "groups": $scope.groups = data; break;
                case "objects": $scope.objects = data; $scope.objectsBackup=data; break;
                default: console.log("Data type is undefined.");
            }
        });
    };
    $scope.getData($scope.objectUrl, "objects");
    
    $scope.toggleSelected = function(object){
        
    };
    
    $scope.selectedGroup = function(group){
        $scope.currentGroup = group;
    };
    
    $scope.addGroup = function(){
        $scope.currentGroup = {};
        $scope.objects = $scope.objectsBackup;
    };
    
    $scope.checkForm = function(){       
        var result = !(($scope.currentGroup.name ==="")||($scope.currentGroup.name ==null));
        return result;
    };
}]);