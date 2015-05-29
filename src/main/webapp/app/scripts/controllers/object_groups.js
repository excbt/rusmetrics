angular.module('portalNMK')
.controller('ObjectGroupsCtrl', ['$scope', 'crudGridDataFactory', 'notificationFactory', function($scope, crudGridDataFactory, notificationFactory){
    //
    $scope.groups = [
//        {'name':"Group 1"},
//        {'name': "group 2"}
    ];
    $scope.objects = [];
    $scope.availableObjects = [];
    $scope.selectedObjects = [];
    $scope.objectsBackup = [];
    $scope.currentGroup = {};
    
    $scope.objectUrl = "../api/subscr/contObjects";
    $scope.groupUrl = "../api/contGroup";
    
    var successCallback = function (e) {
        notificationFactory.success();
        $('#deleteGroupModal').modal('hide');
        $('#editGroupModal').modal('hide');
        $scope.currentGroup={};
        $scope.getData($scope.groupUrl, "groups");

    };

    var errorCallback = function (e) {
        notificationFactory.errorInfo(e.statusText,e.data.description);       
    };
    
    function performObjectArray(){
        var result = [];
        for (var i=0; i<$scope.selectedObjects.length;i++){
            if ($scope.selectedObjects[i].selected){
                result.push($scope.selectedObjects[i].id);
            };
        };
        return result;
    };

    $scope.saveGroup = function () {
        var tmp = $scope.selectedObjects.map(function(element, index, array){
            return element.id;
        });
        if ($scope.currentGroup.id == null){
//console.log("Save");            
//console.log($scope.groupUrl);
//console.log(tmp);            
            crudGridDataFactory($scope.groupUrl).save({contObjectIds: tmp},$scope.currentGroup, successCallback, errorCallback);
        }else{
//console.log("Update");            
//console.log($scope.groupUrl);             
            var targetUrl = $scope.groupUrl;//+"/"+$scope.currentGroup.id;
            crudGridDataFactory(targetUrl).update({contObjectIds: tmp}, $scope.currentGroup, successCallback, errorCallback);
        };
    };

    $scope.deleteGroup = function (object) {
//        $scope.groups.splice($scope.groups.indexOf(object),1);
        crudGridDataFactory($scope.groupUrl).delete({ id: object.id }, successCallback, errorCallback);
    };

    $scope.getData = function (url, type) {
        crudGridDataFactory(url).query(function (data) {
            switch (type){
                case "groups": $scope.groups = data; break;
                case "availableObjects": $scope.availableObjects = data; break;
                case "selectedObjects": $scope.selectedObjects = data; break;    
                default: console.log("Data type is undefined.");
            }
        });
    };
      
//    $scope.toggleSelected = function(object){
//        
//    };
    
    $scope.selectedGroup = function(group){
        $scope.currentGroup = group;
        //get group objects
        var selectedObjectUrl = $scope.groupUrl+"/"+group.id+"/contObject";
        $scope.getData(selectedObjectUrl, "selectedObjects");
        //get available objects
        var availableObjectUrl = $scope.groupUrl+"/"+group.id+"/contObject/available";
        $scope.getData(availableObjectUrl, "availableObjects");
       
    };
    
    $scope.addGroup = function(){
        $scope.currentGroup = {};
        $scope.selectedObjects = [];
        //get available objects
        var availableObjectUrl = $scope.groupUrl+"/0/contObject/available";
        $scope.getData(availableObjectUrl, "availableObjects");
        
    };
    
    $scope.checkForm = function(){       
        var result = !(($scope.currentGroup.contGroupName ==="")||($scope.currentGroup.contGroupName ==null));
        return result;
    };
    
    $scope.selectObject = function(object){
        $scope.selectedObjects.push(object);
        $scope.availableObjects.splice($scope.availableObjects.indexOf(object), 1);
    };
    
    $scope.removeSelectedObject = function(object){
        $scope.availableObjects.push(object);
        $scope.selectedObjects.splice($scope.selectedObjects.indexOf(object), 1);
    }
    
    $scope.addSelectedObjects = function(){
//console.log($scope.availableObjects);          
        var tmpArray = angular.copy($scope.availableObjects);
        for(var i =0; i<$scope.availableObjects.length; i++){
            var curObject = $scope.availableObjects[i];

            if (curObject.selected){
//console.log(curObject);                            
// console.log("curObject is performanced");               
                var elem = angular.copy(curObject);
                elem.selected = null;
//console.log(tmpArray.indexOf(curObject));  
                var elementIndex = -1;
                tmpArray.some(function(element,index,array){
                    if (element.fullName === curObject.fullName){
                        elementIndex = index;
                        return true;
                    }else{
                        return false;
                    }
                });
                tmpArray.splice(elementIndex, 1);
                $scope.selectedObjects.push(elem);
                curObject.selected = null;
            };
        }
        $scope.availableObjects = tmpArray;
        $scope.showAvailableObjects_flag=false;
    };
    
    $scope.isSystemuser = function(){
        $scope.userInfo = $rootScope.userInfo;
        return $scope.userInfo._system;
    };
    
     //get the list of groups
    $scope.getData($scope.groupUrl, "groups");
}]);