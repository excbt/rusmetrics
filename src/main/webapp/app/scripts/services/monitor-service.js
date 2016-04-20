'use strict';
angular.module('portalNMC')
    .service('monitorSvc', ['$rootScope', '$http', '$interval', '$cookies', '$location', 'objectSvc', function($rootScope, $http, $interval, $cookies, $location, objectSvc){
//console.log("Monitor service. Run Monitor service.");        
                //url to data
        var notificationsUrl = "../api/subscr/contEvent/notifications"; 
        var objectUrl = notificationsUrl + "/contObject";
        var cityWithObjectsUrl = objectUrl + "/cityStatusCollapse";
        
        var objectsMonitorSvc = [];
        var citiesMonitorSvc = [];   

        //monitor settings
        var monitorSvcSettings = {};
        monitorSvcSettings.refreshPeriod = "180";
        monitorSvcSettings.loadingFlag = true;
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
        
                    //get cities with objects function
        var getCitiesAndObjects = function(url, monitorSvcSettings){ 
//console.log("MonitorSvc. Get cities and objects");    
            monitorSvcSettings.loadingFlag = true;
            var targetUrl = url + "/?fromDate=" + monitorSvcSettings.fromDate + "&toDate=" + monitorSvcSettings.toDate + "&noGreenColor=" + monitorSvcSettings.noGreenObjectsFlag;
            $http.get(targetUrl)
                .success(function(data){
//console.log(data);                
                    citiesMonitorSvc = data;                
                    objectsMonitorSvc = getObjectsFromCities(data);
                   
                    //sort objects by name
                    objectSvc.sortObjectsByConObjectFullName(objectsMonitorSvc);
                    monitorSvcSettings.loadingFlag = false;//data has been loaded
                    $rootScope.$broadcast('monitorObjects:updated');
                })
                .error(function(e){
                    console.log(e);
                    monitorSvcSettings.loadingFlag = false;//data has been loaded
                    $rootScope.$broadcast('monitorObjects:updated');
                });
            monitorSvcSettings.noGreenObjectsFlag = false; //reset flag
        };
        
            //get objects function
        var getObjects = function(url, monitorSvcSettings){ 
//console.log("MonitorSvc. Get objects");    
            monitorSvcSettings.loadingFlag = true;
            var targetUrl = url + "/statusCollapse?fromDate=" + monitorSvcSettings.fromDate + "&toDate=" + monitorSvcSettings.toDate + "&noGreenColor=" + monitorSvcSettings.noGreenObjectsFlag;
//console.log(targetUrl);  
            $http.get(targetUrl)
                .success(function(data){
                    objectsMonitorSvc = data;
//    console.log(data);            
                    //sort objects by name
                    objectSvc.sortObjectsByConObjectFullName(objectsMonitorSvc);
                    monitorSvcSettings.loadingFlag = false;//data has been loaded
                    $rootScope.$broadcast('monitorObjects:updated');
                })
                .error(function(e){
                    console.log(e);
                });
            monitorSvcSettings.noGreenObjectsFlag = false; //reset flag
        };
        
        //run getObjects
        getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
            
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
            interval = $interval(function(){
                var time = (new Date()).toLocaleString();
                monitorSvcSettings.loadingFlag = true;
                getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
            }, Number(monitorSvcSettings.refreshPeriod) * 1000);

        }, false);
        
            //Вызвываем с заданным периодом обновление монитора
        interval = $interval(function(){
            var time = (new Date()).toLocaleString();
            monitorSvcSettings.loadingFlag = true;
            getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
        },Number(monitorSvcSettings.refreshPeriod) * 1000);
        
        
        $rootScope.$on('monitor:updateObjectsRequest',function(){
//console.log("MonitorSvc. monitor:updateObjectsRequest");            
            getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
        });
        
        $rootScope.$on('$destroy',function(){
            stopRefreshing();
        });
        
         return {
            
            getAllMonitorObjects,
            getAllMonitorCities,
            getLoadingStatus,
            getMonitorEventsByObject, 
            getMonitorSettings,
            setMonitorSettings,
        };
        
    }]);