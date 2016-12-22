'use strict';
angular.module('portalNMC')
.service('subscrCabinetsSvc', ['$http', '$cookies', '$interval', '$rootScope',
             function($http, $cookies, $interval, $rootScope){
//console.log("Cabinet Service. Run."); 
                 
        var svcCabinets = [
            {fullName: "Ошибка. Объекты не были загружены."
            }
        ];
//                 /api/subscr/subscrCabinetService/contObjectCabinetInfo
        var loading = true;
        var urlApi = '../api';
        var urlSubscr = urlApi + '/subscr';
        var urlCabinets = urlSubscr + '/subscrCabinet';
        var urlCabinetsInfo = urlCabinets + '/contObjectCabinetInfo';
                 
        var urlRma = urlApi + '/rma';
        var crudTableName = urlSubscr + '/contObjects';
        var urlRmaContCabinets = urlRma + '/contObjects';                 
        var rmaTreeTemplatesUrl = urlSubscr + '/subscrObjectTreeTemplates';
        var rmaTreesUrl = urlRma + '/subscrObjectTree/contObjectTreeType1';
        var subscrTreesUrl = urlSubscr + '/subscrObjectTree/contObjectTreeType1';                 
        
        var rmaTreeTemplates = [];
                 
        var currentCabinet = null; //the current selected Cabinet at interface

        var CabinetSvcSettings = {};
                 
        var getCurrentCabinet = function(){
            return currentCabinet;
        };
        var setCurrentCabinet = function(obj){
            currentCabinet = obj;
        };
                 
        var getCabinetSettings = function(){
            return CabinetSvcSettings;
        };
                 
        var getRmaTreeTemplates = function(){
            return rmaTreeTemplates;
        };
        
        var setCabinetSettings = function(CabinetSettings){
            for (var key in CabinetSettings){
                CabinetSvcSettings[key] = CabinetSettings[key];
            };
        };
        
        var getCabinetsUrl = function(){
            return urlCabinets;
        };
                 
        var getRmaCabinetsUrl = function(){
            return urlRmaContCabinets;
        };
        var getSubscrUrl = function(){
            return urlSubscr;
        };
        var getRmaUrl = function(){
            return urlRma;
        };
        
        var getLoadingStatus = function(){
            return loading;
        };
                 
        //universal function
        var getData = function(url){
            return $http.get(url);
        };      

        //get cabinets
        var getCabinetsData = function () {           
           return $http.get(urlCabinetsInfo);//.then(function(res){console.log(res)});
        };
        var getRmaCabinetsData = function () {
           return $http.get(getRmaCabinetsUrl());
        };

                //get Cabinet
        var getCabinet = function (objId) {
           if (angular.isUndefined(objId) || (objId == null)) {return "Cabinet id is null or undefined"};          
           return $http.get(urlCabinetsInfo + "/" +objId);
        };
                 
        var getRmaCabinet = function (objId) {
           if (angular.isUndefined(objId) || (objId == null)) {return "Cabinet id is null or undefined"}; 
           return $http.get(getRmaCabinetsUrl() + "/" + objId);
        }; 
                 
        var checkUndefinedNull = function(numvalue){
            var result = false;
            if ((angular.isUndefined(numvalue)) || (numvalue == null)){
                result = true;
            }
            return result;
        };
                 
                         //create cabinets
        var createCabinets = function(objIds){
            if (checkUndefinedNull(objIds) || !angular.isArray(objIds)){
                return null;
            };
            var url = urlCabinets + '/create';
            return $http.put(url, objIds);
        };
                                 //update user info
        var updateCabinet = function(cabinet){
            if (checkUndefinedNull(cabinet) || checkUndefinedNull(cabinet.cabinet)){
                return null;
            };
            var url = urlCabinets + '/subscrUser/' + cabinet.cabinet.subscrUser.id;
            return $http.put(url, cabinet.cabinet.subscrUser);
        };
                 
                 //deleteCabinets
        var deleteCabinets = function(cabIds){
            if (checkUndefinedNull(cabIds) || !angular.isArray(cabIds)){
                return null;
            };
            var url = urlCabinets + '/delete';
            return $http.put(url, cabIds);
        };
                 
                 //reset cabinet passwords
        var resetPassword = function(userIds){
            if (checkUndefinedNull(userIds) || !angular.isArray(userIds)){
                return null;
            };
            var url = urlCabinets + '/subscrUser/resetPassword';
            return $http.put(url, userIds);
        };
                                  
                 //send passwords by email
        var sendLDByEmail = function(userIds){
            if (checkUndefinedNull(userIds) || !angular.isArray(userIds)){
                return null;
            };
            var url = urlCabinets + '/subscrUser/sendPassword';
            return $http.put(url, userIds);
        };
                 
            // Sort Cabinet array by some string field
        var sortItemsBy = function(itemArray, sortField){
            if (!angular.isArray(itemArray)){
                return "Input value is no array.";
            };
            if (checkUndefinedNull(sortField)){
                return "Field for sort is undefined or null.";
            };
            itemArray.sort(function(firstItem, secondItem){
                if (checkUndefinedNull(firstItem[sortField]) && checkUndefinedNull(secondItem[sortField])){
                    return 0;
                };
                if (checkUndefinedNull(firstItem[sortField])){
                    return -1;
                };
                if (checkUndefinedNull(secondItem[sortField])){
                    return 1;
                };
                if (firstItem[sortField].toUpperCase() > secondItem[sortField].toUpperCase()){
                    return 1;
                };
                if (firstItem[sortField].toUpperCase() < secondItem[sortField].toUpperCase()){
                    return -1;
                };
                return 0;
            });
        };
                 
            // sort the Cabinet array by the fullname
        function sortCabinetsByFullName(array){
            sortItemsBy(array, "fullName");
        };                 
                    // sort the Cabinet array by the fullname, where some Cabinets do not have field "fullName"
        function sortCabinetsByFullNameEx(array){
            if (angular.isUndefined(array) || (array == null) || !angular.isArray(array)){
                return false;
            };
            var tmpArr = [];
            var tmpBadArr = [];
            array.forEach(function(elem){
                if (!checkUndefinedNull(elem.fullName)){
                    tmpArr.push(elem);
                }else{
                    tmpBadArr.push(elem);
                };
            });
            if (tmpArr.length == 0) {return "No Cabinets with fullName"};
            sortCabinetsByFullName(tmpArr);
            Array.prototype.push.apply(tmpBadArr, tmpArr);
            return tmpBadArr;
        };
                 
        function sortCabinetsByContObjectFullName(array){
            if (angular.isUndefined(array) || (array == null) || !angular.isArray(array)){
                return false;
            };           
            array.sort(function(a, b){
                if ((checkUndefinedNull(a.contObjectInfo) || checkUndefinedNull(a.contObjectInfo.fullName)) && 
                    (checkUndefinedNull(b.contObjectInfo) || checkUndefinedNull(b.contObjectInfo.fullName))){         
                    return 0;
                };
                if (checkUndefinedNull(a.contObjectInfo) || checkUndefinedNull(a.contObjectInfo.fullName)){         
                    return -1;
                };
                if (checkUndefinedNull(b.contObjectInfo) || checkUndefinedNull(b.contObjectInfo.fullName)){         
                    return 1;
                };
                if (a.contObjectInfo.fullName.toUpperCase() > b.contObjectInfo.fullName.toUpperCase()){
                    return 1;
                };
                if (a.contObjectInfo.fullName.toUpperCase() < b.contObjectInfo.fullName.toUpperCase()){
                    return -1;
                };
                return 0;
            }); 
        };
                 
        function findCabinetById(objId, cabinetArr){
            var obj = null;
            if (!angular.isArray(cabinetArr)){
                return obj;
            };
            cabinetArr.some(function(element){
                if (element.id === objId){
                    obj = element;
                    return true;
                }
            });        
            return obj;
        };

       var promise = null;//getCabinetsData();
       var rmaPromise = null;//getRmaCabinetsData();
       var loadData = function(){
         promise = getCabinetsData();
//         rmaPromise = getRmaCabinetsData();
       };
                 
       $rootScope.$on('subscrCabinets:requestReloadCabinetData', function(){
//console.log("Reload Cabinets data.");           
           loadData();
       });
        var getPromise = function(){
            return promise;
        };
        var getRmaPromise = function(){
            return rmaPromise;
        };

//****************************************************************************************         
//Cabinets tree
//****************************************************************************************
        var loadTreeTemplates = function(url){
            $http.get(url).then(function(resp){
                rmaTreeTemplates = angular.copy(resp.data);
            }, function(e){
                console.log(e);
            });
        };
                 
        var loadTreeTemplateItems = function(templateId){
            return $http.get(rmaTreeTemplatesUrl + '/' +templateId + '/items');
        };
                 
        var createTree = function(tree){
            return $http.post(rmaTreesUrl, tree);
        };
        
        var loadTrees = function(){
            return $http.get(rmaTreesUrl);
        };
        
        var loadTree = function(treeId){
            return $http.get(rmaTreesUrl + '/' + treeId);
        };
                 
        var updateTree = function(tree){
            return $http.put(rmaTreesUrl + '/' + tree.id, tree);
        };
                 
        var deleteTree = function(treeId){
            return $http.delete(rmaTreesUrl + '/' + treeId);
        };
                 
        var deleteTreeNode = function(treeId, nodeId){
            return $http.delete(rmaTreesUrl + '/' + treeId + '/node/' + nodeId);
        };
                 
        var loadCabinetsByTreeNode = function(treeId, nodeId){            
            return $http.get(rmaTreesUrl + '/' + treeId + '/node/' + nodeId + '/contCabinets');
        };
                 
        var putCabinetsToTreeNode = function(treeId, nodeId, objIds){            
            return $http.put(rmaTreesUrl + '/' + treeId + '/node/' + nodeId + '/contCabinets/add', objIds);
        };
        
        var releaseCabinetsFromTreeNode = function(treeId, nodeId, objIds){            
            return $http.put(rmaTreesUrl + '/' + treeId + '/node/' + nodeId + '/contCabinets/remove', objIds);
        };
                 
        var loadFreeCabinetsByTree = function(treeId){            
            return $http.get(rmaTreesUrl + '/' + treeId + '/contCabinets/free');
        };
                 
        var loadSubscrTrees = function(){
            return $http.get(subscrTreesUrl);
        };
        
        var loadSubscrTree = function(treeId){
            return $http.get(subscrTreesUrl + '/' + treeId);
        };
        
        var loadSubscrFreeCabinetsByTree = function(treeId){            
            return $http.get(subscrTreesUrl + '/' + treeId + '/contCabinets/free');
        };
                 
        var loadSubscrCabinetsByTreeNode = function(treeId, nodeId){            
            return $http.get(subscrTreesUrl + '/' + treeId + '/node/' + nodeId + '/contCabinets');
        };
        
        //service initialization
        var initSvc = function(){
            loadTreeTemplates(rmaTreeTemplatesUrl);
            loadData();
        };

        initSvc();
                    
        return {
            createTree,
            createCabinets,
            deleteCabinets,
            
            deleteTree,
            deleteTreeNode,
            getCurrentCabinet,
            getCabinet,
            getCabinetSettings,
            getLoadingStatus,
            getCabinetsUrl,
            getPromise,
            getRmaCabinet,
            getRmaCabinetsData,
            getRmaCabinetsUrl,
            getRmaPromise,
            getSubscrUrl,
            getRmaTreeTemplates,
            findCabinetById,
            loadFreeCabinetsByTree,
            loadCabinetsByTreeNode,
            loading,
            loadTree,            
            loadTrees,
            loadTreeTemplateItems,
            loadTreeTemplates,
            loadSubscrFreeCabinetsByTree,
            loadSubscrCabinetsByTreeNode,
            loadSubscrTree,
            loadSubscrTrees,
            promise,
            putCabinetsToTreeNode,
            releaseCabinetsFromTreeNode,
            
            resetPassword,
            
            rmaPromise,
            
            sendLDByEmail,
            
            setCabinetSettings,
            setCurrentCabinet,
            sortCabinetsByFullName,
            sortCabinetsByFullNameEx, 
            
            sortCabinetsByContObjectFullName,
            updateCabinet,
            updateTree
        };
    
}]);