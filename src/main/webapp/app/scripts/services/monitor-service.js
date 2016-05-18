'use strict';
angular.module('portalNMC')
    .service('monitorSvc', ['$rootScope', '$http', '$interval', '$cookies', '$location', 'objectSvc', function($rootScope, $http, $interval, $cookies, $location, objectSvc){
//console.log("Monitor service. Run Monitor service.");
        var SUBSCR_MONITOR_OBJECT_TREE_CONT_OBJECTS = "SUBSCR_MONITOR_OBJECT_TREE_CONT_OBJECTS";
                //url to data
        var notificationsUrl = "../api/subscr/contEvent/notifications"; 
        var objectUrl = notificationsUrl + "/contObject";
        var defaultCityWithObjectsUrl = objectUrl + "/cityStatusCollapse";
        var cityWithObjectsUrl = null; //dinamically param: when tree off - it = defaultCityWithObjectsUrl; when tree on - subscrTreesUrl + treeId + nodeId ...
        var urlSubscr = "../api/subscr";
        var subscrTreesUrl = urlSubscr + '/subscrObjectTree/contObjectTreeType1';                 
        var defaultTreeUrl = urlSubscr + '/subscrPrefValue?subscrPrefKeyname=' + SUBSCR_MONITOR_OBJECT_TREE_CONT_OBJECTS;
        
        var objectsMonitorSvc = [];
        var citiesMonitorSvc = [];   

        //monitor settings
        var monitorSvcSettings = {};
        monitorSvcSettings.refreshPeriod = "300";
        monitorSvcSettings.loadingFlag = true;
console.log(monitorSvcSettings.loadingFlag);        
        monitorSvcSettings.noGreenObjectsFlag = false;
        monitorSvcSettings.fromDate = moment().subtract(6, 'days').startOf('day').format('YYYY-MM-DD');
        monitorSvcSettings.toDate = moment().endOf('day').format('YYYY-MM-DD');
        
        var getMonitorSettings = function(){            
            return monitorSvcSettings;
        };
        
        var setMonitorSettings = function(monitorSettings){
            for (var key in monitorSettings){
                monitorSvcSettings[key] = monitorSettings[key];
            };
        };
        
        var getLoadingStatus = function(){            
            return monitorSvcSettings.loadingFlag;
        };
        
        var getAllMonitorObjects = function(){
            return objectsMonitorSvc;
        };
        
        var getAllMonitorCities = function(){
            return citiesMonitorSvc;
        };
        
        var checkUndefinedNull = function(numvalue){
            var result = false;
            if ((angular.isUndefined(numvalue)) || (numvalue == null)){
                result = true;
            }
            return result;
        };
        
        function getObjectsFromCities(cities){
            if (angular.isUndefined(cities) || (cities == null) || !angular.isArray(cities)){
                return false;
            };
            var resultObjectArray = [];
            cities.forEach(function(elem){
                elem.contEventNotificationStatuses.forEach(function(obj){
                    resultObjectArray.push(obj);
                });
            });
            return resultObjectArray;
        };
        
        var errorLoadingMonitorData = function(e){
            console.log(e);
            citiesMonitorSvc = [];                
            objectsMonitorSvc = [];
            monitorSvcSettings.loadingFlag = false;//data has been loaded
console.log(monitorSvcSettings.loadingFlag);                
            $rootScope.$broadcast('monitorObjects:updated');
        };
        
                    //get cities with objects function
        var getCitiesAndObjects = function(url, monitorSvcSettings){
console.log(url);            
            if (checkUndefinedNull(url)){
                return 400;
            };
//console.log("MonitorSvc. Get cities and objects");    
            monitorSvcSettings.loadingFlag = true;
console.log(monitorSvcSettings.loadingFlag);            
            var targetUrl = url + "/?fromDate=" + monitorSvcSettings.fromDate + "&toDate=" + monitorSvcSettings.toDate + "&noGreenColor=" + monitorSvcSettings.noGreenObjectsFlag;
            if (!checkUndefinedNull(monitorSvcSettings.contGroupId)){
                targetUrl += "&contGroupId=" + monitorSvcSettings.contGroupId;
            };
            $http.get(targetUrl)
                .success(function(data){
//console.log(data);                
                    citiesMonitorSvc = data;                
                    objectsMonitorSvc = getObjectsFromCities(data);
                   
                    //sort objects by name
                    objectSvc.sortObjectsByConObjectFullName(objectsMonitorSvc);
                    monitorSvcSettings.loadingFlag = false;//data has been loaded
console.log(monitorSvcSettings.loadingFlag);                
                    $rootScope.$broadcast('monitorObjects:updated');
                })
                .error(errorLoadingMonitorData);
//            monitorSvcSettings.noGreenObjectsFlag = false; //reset flag
        };
        
            //get objects function
//        var getObjects = function(url, monitorSvcSettings){    
//            monitorSvcSettings.loadingFlag = true;
//console.log(monitorSvcSettings.loadingFlag);            
//            var targetUrl = url + "/statusCollapse?fromDate=" + monitorSvcSettings.fromDate + "&toDate=" + monitorSvcSettings.toDate + "&noGreenColor=" + monitorSvcSettings.noGreenObjectsFlag; 
//            $http.get(targetUrl)
//                .success(function(data){
//                    objectsMonitorSvc = data;
//                    objectSvc.sortObjectsByConObjectFullName(objectsMonitorSvc);
//                    monitorSvcSettings.loadingFlag = false;
//console.log(monitorSvcSettings.loadingFlag);                
//                    $rootScope.$broadcast('monitorObjects:updated');
//                })
//                .error(function(e){
//                    console.log(e);
//                });
//        };        
            
        //get monitor events
       var getMonitorEventsByObject = function(obj){ 
//console.log(obj);           
            var url = objectUrl + "/" + obj.contObject.id + "/monitorEvents";// + "?fromDate=" + $rootScope.monitorStart + "&toDate=" + $rootScope.monitorEnd;
            $http.get(url)
                .success(function(data){
    //console.log("success");
//console.log(data);                
                //if data is not array - exit
                    if (!data.hasOwnProperty('length') || (data.length == 0)){
                        if (obj.statusColor === "YELLOW"){
                            obj.monitorEvents = "На объекте нет нештатных ситуаций";
                            $rootScope.$broadcast('monitorObjects:getObjectEvents',{"obj":obj});
                        };
                        return;
                    };
                    //temp array
                    var tmpMessage = "";
    //                var tmpMessageEx = "";
                    //make the new array of the types wich formatted to display
                    data.forEach(function(element){
//console.log(element);                        
                        var tmpEvent = "";
                        var contEventTime = new Date(element.contEventTime);
                        var pstyle = "";
                        if(element.contEventLevelColorKey === "RED"){
                            pstyle = "color: red;";
                        };
                        tmpEvent = "<p style='" + pstyle + "'>" + contEventTime.toLocaleString() + ", " + element.contEventType.name + "</p>";
                        tmpMessage += tmpEvent;
                    });
//console.log(tmpMessage);     
                    if (obj.statusColor === "YELLOW"){
                        obj.monitorEvents = "На объекте нет нештатных ситуаций";
                    }else if ((obj.statusColor === "RED") || (obj.statusColor === "ORANGE")){
                        obj.monitorEvents = tmpMessage;
                        obj.monitorEventsForMap = data;
                    };
//console.log(obj);                
                    $rootScope.$broadcast('monitorObjects:getObjectEvents', {"obj": obj});
                })
                .error(function(e){
                    console.log(e);
                });        
        };
        
        //The control of the period monitor refresh(Управление перодическим обновлением монитора)
//**************************************************************************  
        var interval;
        
        function startRefreshing(){
            var time = (new Date()).toLocaleString();
console.log("Обновление данных для монитора. " + time);            
            monitorSvcSettings.loadingFlag = true;
console.log(monitorSvcSettings.loadingFlag);            
            getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
        };

        function stopRefreshing(){
            if (angular.isDefined(interval)){
                $interval.cancel(interval);
                interval = undefined;
            };
        };

        //watch for the change of the refresh period
        $rootScope.$on('monitor:periodChanged', function () {
//console.log("MonitorSvc monitorSvcSettings.refreshPeriod watch");
            stopRefreshing();
            //set new interval
            interval = $interval(startRefreshing, Number(monitorSvcSettings.refreshPeriod) * 1000);

        }, false);
        
            //Вызвываем с заданным периодом обновление монитора
        interval = $interval(startRefreshing, Number(monitorSvcSettings.refreshPeriod) * 1000);
        
        var getMonitorData = function(){            
            loadDefaultMonitorTreeSetting().then(function(resp){                
                monitorSvcSettings.isTreeView = resp.data.isActive;                
                if (monitorSvcSettings.isTreeView == true && monitorSvcSettings.isFullObjectView != true){                    
                    monitorSvcSettings.defaultTreeId = Number(resp.data.value);                    
                    if (checkUndefinedNull(monitorSvcSettings.curTreeId)){
                        monitorSvcSettings.curTreeId = monitorSvcSettings.defaultTreeId;
                    };                    
                    if (checkUndefinedNull(monitorSvcSettings.curTreeNodeId)){
                        citiesMonitorSvc = [];                
                        objectsMonitorSvc = [];
                        monitorSvcSettings.loadingFlag = false;//data has been loaded
console.log(monitorSvcSettings.loadingFlag);                        
                        $rootScope.$broadcast('monitorObjects:updated');
                    }else{                        
                        cityWithObjectsUrl = subscrTreesUrl + '/' + monitorSvcSettings.curTreeId + '/node/' + monitorSvcSettings.curTreeNodeId + '/contObjects/cityStatusCollapse';
                        getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
                    };
                }else{                                            
                    cityWithObjectsUrl = angular.copy(defaultCityWithObjectsUrl);
                    getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
                };
            }, errorLoadingMonitorData);            
        };
        
        $rootScope.$on('monitor:updateObjectsRequest',function(){
//console.log("MonitorSvc. monitor:updateObjectsRequest");
            getMonitorData();
//            if (monitorSvcSettings.isTreeView == true && checkUndefinedNull(monitorSvcSettings.curTreeId)){                
//                if (checkUndefinedNull(monitorSvcSettings.curTreeNodeId)){
//                    citiesMonitorSvc = [];                
//                    objectsMonitorSvc = [];
//                    monitorSvcSettings.loadingFlag = false;//data has been loaded
//                    $rootScope.$broadcast('monitorObjects:updated');
//                }else{
//                    cityWithObjectsUrl = subscrTreesUrl + '/' + monitorSvcSettings.curTreeId + '/node/' + monitorSvcSettings.curTreeNodeId + '/contObjects/cityStatusCollapse';
//                    getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
//                };
//            }else{
//console.log("here");
//                cityWithObjectsUrl = angular.copy(defaultCityWithObjectsUrl);
//                getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
//            };
        });
        
        $rootScope.$on('$destroy',function(){
            stopRefreshing();
        });
        
//******************************************************************
//  Work with trees
//******************************************************************
        var loadSubscrTrees = function(){
            return $http.get(subscrTreesUrl);
        };
        
        var loadSubscrTree = function(treeId){
            return $http.get(subscrTreesUrl + '/' + treeId);
        };
        
        var loadSubscrFreeObjectsByTree = function(treeId){            
            return $http.get(subscrTreesUrl + '/' + treeId + '/contObjects/free');
        };
                 
        var loadSubscrObjectsByTreeNode = function(treeId, nodeId){            
            return $http.get(subscrTreesUrl + '/' + treeId + '/node/' + nodeId + '/contObjects/cityStatusCollapse');
        };
                 
        var loadDefaultMonitorTreeSetting = function(){
            return $http.get(defaultTreeUrl);
        };
//******************************************************************
//******************************************************************
        
        var initSvc = function(){
            //run getObjects
            getMonitorData();
//            getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
        };
        initSvc();
        
         return {
            checkUndefinedNull,
            getAllMonitorObjects,
            getAllMonitorCities,
            getLoadingStatus,
            getMonitorEventsByObject, 
            getMonitorSettings,
             
//            loadSubscrTree,
//            loadSubscrTrees,
//            loadSubscrObjectsByTreeNode,
             
            loadDefaultMonitorTreeSetting, 
             
            setMonitorSettings,
        };
        
    }]);