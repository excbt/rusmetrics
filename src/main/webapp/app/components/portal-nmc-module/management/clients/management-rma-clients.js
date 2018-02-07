/*jslint node: true, eqeq: true, es5: true*/
/*global angular, $*/
'use strict';
var app = angular.module('portalNMC');
app.controller('MngmtClientsCtrl', ['$rootScope', '$scope', '$http', 'objectSvc', 'notificationFactory', 'crudGridDataFactory', 'mainSvc', function ($rootScope, $scope, $http, objectSvc, notificationFactory, crudGridDataFactory, mainSvc) {
//console.log('Run Client management controller.');
    $rootScope.ctxId = "management_rma_clients_page";
    $scope.extraProps = {"idColumnName" : "id", "defaultOrderBy" : "subscriberName", "nameColumnName" : "subscriberName"};
    $scope.orderBy = { field: $scope.extraProps["defaultOrderBy"], asc: true};
    
    //controller settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.rmaUrl = "../api/rma";
    $scope.ctrlSettings.clientsUrl = "../api/rma/subscribers";
    $scope.ctrlSettings.orgUrl = $scope.ctrlSettings.clientsUrl + "/organizations";
    
    $scope.ctrlSettings.availableObjectsSuffix = "/availableContObjects";
    $scope.ctrlSettings.subscrObjectsSuffix = "/subscrContObjects";

    $scope.ctrlSettings.groupUrl = "../api/subscr/contGroup";
    
    //Headers of modal window
    $scope.headers = {};
    $scope.headers.addObjects = "Доступные объекты";//header of add objects window
    
    //client columns
    $scope.ctrlSettings.clientColumns = [
        {
            "name": "subscriberName",
            "caption": "Наименование",
            "class": "col-xs-3"
        },
        {
            "name": "info",
            "caption": "Информация",
            "class": "col-xs-3"
        },
        {
            "name": "comment",
            "caption": "Комментарий",
            "class": "col-xs-2"
        },
        {
            "name": "organizationName",
            "caption": "Организация",
            "class": "col-xs-2"
        }
    ];
    //data
    $scope.data = {};
    $scope.data.organizations = [];
    $scope.data.clients = [];
    $scope.data.currentClient = {};
    
//    get subscribers
    var getClients = function () {
        var targetUrl = $scope.ctrlSettings.clientsUrl;
        $http.get(targetUrl)
            .then(function (response) {
    //console.log(response.data);
                if (mainSvc.checkUndefinedNull(response) || mainSvc.checkUndefinedNull(response.data) || !angular.isArray(response.data)) {
                    return false;
                }
                response.data.forEach(function (el) {
                    if (mainSvc.checkUndefinedNull(el.organization)) {
                        return false;
                    }
                    el.organizationName = el.organization.organizationFullName || el.organization.organizationName;
                });
                $scope.data.clients = response.data;

    //console.log($scope.data.clients);            
            },
                function (e) {
                    console.log(e);
                });
    };
    
    //get organizations
    var getOrganizations = function () {
        var targetUrl = $scope.ctrlSettings.orgUrl;
        $http.get(targetUrl)
            .then(function (response) {
                $scope.data.organizations =  response.data;
                mainSvc.sortOrganizationsByName($scope.data.organizations);
    //console.log($scope.data.organizations);
                getClients();
            },
                function (e) {
                    console.log(e);
                });
    };
    
    $scope.selectClient = function (client) {
        $scope.data.currentClient = client;
    };
    $scope.selectedItem = function (item) {
        var curObject = angular.copy(item);
        $scope.data.currentClient = curObject;
//console.log($scope.data.currentClient);        
    };
    
    $scope.editObjectList =  function (client) {
        $scope.selectedItem(client);
        $scope.getAvailableObjects($scope.data.currentClient.id);
        $scope.getSelectedObjects($scope.data.currentClient.id);
        $scope.showAvailableObjects_flag = false;
    };
    
    $scope.addClient = function () {
        $scope.data.currentClient = {};
        $('#showClientOptionModal').modal();
    };
    
    $scope.setOrderBy = function (field) {
        var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
        $scope.orderBy = { field: field, asc: asc };
    };
    
    // get timezones
    var getTimezones = function () {
        objectSvc.getTimezones()
            .then(function (response) {
                $scope.data.timezones = response.data;
    //            mainSvc.sortItemsBy($scope.data.timezones, 'caption');
                getOrganizations();
            });
    };
    getTimezones();
    
    //data processing
    var successCallback = function (e, cb) {
        notificationFactory.success();
        $('#deleteClientModal').modal('hide');
        $('#showClientOptionModal').modal('hide');
        $('#showObjectListModal').modal('hide');
        getClients();
    };
    
    var successCallbackSignObjects = function (e) {
        notificationFactory.success();
        $('#showObjectListModal').modal('hide');
        $rootScope.$broadcast('objectSvc:requestReloadData');
    };
    
    var successPostCallback = function (e) {
        successCallback(e, null);
        location.reload();
    };

    var errorCallback = function (e) {
        var errorObj = mainSvc.errorCallbackHandler(e);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };
    
    var checkData = function (obj) {
        var result = true;
        if (angular.isUndefined(obj) || (obj == null) || angular.isUndefined(obj.subscriberName) || (obj.subscriberName == null) || (obj.subscriberName == "")) {
            notificationFactory.errorInfo("Ошибка", "Не задано наименование абонента!");
            result = false;
        }
        if (angular.isUndefined(obj) || (obj == null) || angular.isUndefined(obj.organizationId) || (obj.organizationId == null)) {
            notificationFactory.errorInfo("Ошибка", "Не задана организация абонента!");
            result = false;
        }
        if (angular.isUndefined(obj) || (obj == null) || angular.isUndefined(obj.timezoneDefKeyname) || (obj.timezoneDefKeyname == null)) {
            notificationFactory.errorInfo("Ошибка", "Не задан часовой пояс абонента!");
            result = false;
        }
        return result;
    };
    
    $scope.sendClientToServer = function (obj) {
//        obj.organizationId = 726;
//        obj.timezoneDef = null;
//console.log(obj);
        
        //check data before sending
        if (checkData(obj) == false) {
            return;
        }
        
        var url = $scope.ctrlSettings.clientsUrl;
        if (angular.isDefined(obj.id) && (obj.id != null)) {
            $scope.updateObject(url, obj);
        } else {
            $scope.saveNewClient(url, obj);
        }
    };
    
    $scope.saveNewClient = function (url, obj) {
        crudGridDataFactory(url).save(obj, successCallback, errorCallback);
    };

    $scope.deleteObject = function (obj) {
        var url = $scope.ctrlSettings.clientsUrl;
        crudGridDataFactory(url).delete({ id: obj[$scope.extraProps.idColumnName] }, successCallback, errorCallback);
    };

    $scope.updateObject = function (url, object) {
//        object.organization = null;
        var params = { id: object[$scope.extraProps.idColumnName]};
        crudGridDataFactory(url).update(params, object, successCallback, errorCallback);
    };
    
    $scope.deleteObjectInit = function (object) {
        $scope.selectedItem(object);
        //generation confirm code
        $scope.confirmCode = null;
        var tmpCode = mainSvc.getConfirmCode();
        $scope.confirmLabel = tmpCode.label;
        $scope.sumNums = tmpCode.result;
    };
    
    //client's objects
//    processing client's objects
//    *******************************************
    $scope.availableObjects = [];
    $scope.availableObjectGroups = [];
    $scope.selectedObjects = [];
    
    $scope.getAvailableObjects = function (clientId) {
        var table = $scope.ctrlSettings.rmaUrl + "/" + clientId + $scope.ctrlSettings.availableObjectsSuffix;
        crudGridDataFactory(table).query(function (data) {
            $scope.availableObjects = data;
            objectSvc.sortObjectsByFullName($scope.availableObjects);
//console.log($scope.availableObjects);            
        });
    };
    $scope.getSelectedObjects = function () {
        var table = $scope.ctrlSettings.rmaUrl + "/" + $scope.data.currentClient.id + $scope.ctrlSettings.subscrObjectsSuffix;
        crudGridDataFactory(table).query(function (data) {
            $scope.selectedObjects = data;
            objectSvc.sortObjectsByFullName($scope.selectedObjects);
//console.log($scope.selectedObjects);            
        });
    };
    
    $scope.getGroupObjects = function (group) {
        var url = $scope.ctrlSettings.groupUrl + "/" + group.id + "/contObject";
        crudGridDataFactory(url).query(function (data) {
            group.objects = data;
        });
    };
    
    $scope.getAvailableObjectGroups = function () {
        crudGridDataFactory($scope.ctrlSettings.groupUrl).query(function (data) {
            var tempGroupArr = data;
            tempGroupArr.forEach(function (group) {
                $scope.getGroupObjects(group);
            });
            $scope.availableObjectGroups = tempGroupArr;
        });
    };
    
    $scope.getAvailableObjectGroups();
    
    $scope.viewAvailableObjects = function (objectGroupFlag) {
        $scope.showAvailableObjects_flag = !$scope.showAvailableObjects_flag;
        $scope.showAvailableObjectGroups_flag = objectGroupFlag;
        if (objectGroupFlag) {
            $scope.headers.addObjects = "Доступные группы объектов";
            //prepare the object goups to view in table
//            var tmpArr = $scope.availableObjectGroups.map(function(element){
//                var result = element;
//                result.fullName = element.contGroupName;//set the field, which view entity name in table
//                return result;
//            });
            $scope.availableEntities = $scope.availableObjectGroups;//tmpArr;
        } else {
            $scope.headers.addObjects = "Доступные объекты";
            $scope.availableEntities = $scope.availableObjects;
        }
    };
    
    var objectPerform = function (addObject_flag, currentObjectId) {
        var el = {};
        var arr1 = [];
        var arr2 = [];
        var resultArr = [],
            i;
        if ($scope.addObject_flag) {
            arr1 = $scope.availableObjects;
            arr2 = $scope.selectedObjects;
            resultArr = arr2;
        } else {
            arr2 = $scope.availableObjects;
            arr1 = $scope.selectedObjects;
            resultArr = arr1;
        }
       
        for (i = 0; i < arr1.length; i += 1) {
            if (arr1[i].id == $scope.currentObjectId) {
                el = angular.copy(arr1[i]);
                el.selected = false;
                arr1.splice(i, 1);
                break;
            }
        }
        arr2.push(el);
        
        var tmp = resultArr.map(function (elem) {
            return elem.id;
        });
        return tmp; //Возвращаем массив Id-шников выбранных объектов
    };
 
    $scope.addObject = function (object) {
        $scope.addObject_flag = true;
        $scope.currentObjectId = object.id;
        objectPerform(true, object.id);
    };
    
    $scope.removeObject = function (object) {
        $scope.addObject_flag = false;
        $scope.currentObjectId = object.id;
        objectPerform(false, object.id);
    };
    
    $scope.removeSelectedObject = function (object) {
        $scope.availableObjects.push(object);
        $scope.selectedObjects.splice($scope.selectedObjects.indexOf(object), 1);
        objectSvc.sortObjectsByFullName($scope.availableObjects);
    };
    
    $scope.joinObjectsFromSelectedGroups = function (groups) {
        var result = [];
        groups.forEach(function (group) {
            if (group.selected) {
                Array.prototype.push.apply(result, group.objects);
//                    totalGroupObjects = group.objects;
            }
        });
        return result;
    };
    
    $scope.deleteDoublesObjects = function (targetArray) {
        var arrLength = targetArray.length;
        while (arrLength >= 2) {
            arrLength -= 1;
            if (targetArray[arrLength].fullName === targetArray[arrLength - 1].fullName) {
                targetArray.splice(arrLength, 1);
            }
        }
    };
    
    $scope.addUniqueObjectsFromGroupsToSelectedObjects = function (arrFrom, arrTo) {
        var j,
            i;
        for (j = 0; j < arrFrom.length; j += 1) {
            var uniqueFlag = true;
            for (i = 0; i < arrTo.length; i += 1) {
                if (arrFrom[j].fullName === arrTo[i].fullName) {
                    uniqueFlag = false;
                    break;
                }
            }
            if (uniqueFlag) {
                arrTo.push(arrFrom[j]);
            }
        }
    };
    
    $scope.removeGroupObjectsFromAvailableObjects = function (objectsFromGroup, availableObjects) {
        var j,
            i;
        for (j = 0; j < objectsFromGroup.length; j += 1) {
            var elementIndex = -1;
            for (i = 0; i < availableObjects.length; i += 1) {
                if (objectsFromGroup[j].fullName === availableObjects[i].fullName) {
                    elementIndex = i;
                    break;
                }
            }
            if (elementIndex >= 0) {
                availableObjects.splice(elementIndex, 1);
            }
        }
    };
    
    $scope.selectAllAvailableEntities = function () {
        var index;
        for (index = 0; index < $scope.availableEntities.length; index += 1) {
            $scope.availableEntities[index].selected = $scope.ctrlSettings.selectedAll;
        }
        $scope.ctrlSettings.selectedAll = false;
    };
    
    $scope.addSelectedEntities = function () {
    //console.log($scope.availableObjects);       
        if ($scope.showAvailableObjectGroups_flag) {
            var totalGroupObjects = $scope.joinObjectsFromSelectedGroups($scope.availableEntities);
//console.log(totalGroupObjects);            
            objectSvc.sortObjectsByFullName(totalGroupObjects);
            //del doubles
            
            $scope.deleteDoublesObjects(totalGroupObjects);
            //add groupObjects to selected objects
                //add only unique objects
            $scope.addUniqueObjectsFromGroupsToSelectedObjects(totalGroupObjects, $scope.selectedObjects);
            //remove groupObjects from availableObjects
            $scope.removeGroupObjectsFromAvailableObjects(totalGroupObjects, $scope.availableObjects);
        }
        var tmpArray = angular.copy($scope.availableObjects),
            i;
        for (i = 0; i < $scope.availableObjects.length; i += 1) {
            var curObject = $scope.availableObjects[i];

            if (curObject.selected) {
    //console.log(curObject);                            
    // console.log("curObject is performanced");               
                var elem = angular.copy(curObject);
                elem.selected = null;
    //console.log(tmpArray.indexOf(curObject));  
                var elementIndex = -1;
                tmpArray.some(function (element, index, array) {
                    if (element.fullName === curObject.fullName) {
                        elementIndex = index;
                        return true;
                    }
                });
                tmpArray.splice(elementIndex, 1);
                $scope.selectedObjects.push(elem);
                curObject.selected = null;
            }
        }
        $scope.availableObjects = tmpArray;
        objectSvc.sortObjectsByFullName($scope.selectedObjects);
        if ($scope.showAvailableObjectGroups_flag) {
            $scope.availableEntities = $scope.availableObjectGroups;
        } else {
            $scope.availableEntities = $scope.availableObjects;
        }
//        $scope.showAvailableObjects_flag=false;
    };
    
    $scope.showAddObjectButton = function () {
        return !$scope.showAvailableObjects_flag;// && $scope.set_of_objects_flag;
    };
    
    $scope.saveObject = function () {
        var tmp = $scope.selectedObjects.map(function (elem) {
            return elem.id;
        });
        if (($scope.data.currentClient.id != null) && (typeof $scope.data.currentClient.id != 'undefined')) {
            var table = $scope.ctrlSettings.rmaUrl + "/" + $scope.data.currentClient.id + $scope.ctrlSettings.subscrObjectsSuffix;
//            crudGridDataFactory(table).update({}, tmp, successCallback, errorCallback);
            $http.put(table, tmp).then(successCallbackSignObjects, errorCallback);
        }
    };
    
    //checkers
    $scope.isSystemuser = function () {
        return mainSvc.isSystemuser();
    };
    
    $scope.isTestMode = function () {
        return mainSvc.isTestMode();
    };
    
//***************************************************
    
}]);