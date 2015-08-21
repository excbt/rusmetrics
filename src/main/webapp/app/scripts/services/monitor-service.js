'use strict';
angular.module('portalNMC')
    .service('monitorSvc', ['$rootScope', '$http', '$interval', '$cookies', function($rootScope, $http, $interval, $cookies){
console.log("Monitor service. Run Monitor service.");        
                //url to data
        var notificationsUrl = "../api/subscr/contEvent/notifications"; 
        var objectUrl = notificationsUrl+"/contObject";
        var cityWithObjectsUrl = objectUrl+"/cityStatusCollapse";
        
        var objectsMonitorSvc = [];
        var citiesMonitorSvc = [];
        //default date interval settings
        $rootScope.monitorStart = $cookies.fromDate || moment().subtract(6, 'days').startOf('day').format('YYYY-MM-DD');
        $rootScope.monitorEnd = $cookies.toDate || moment().endOf('day').format('YYYY-MM-DD');    

        //monitor settings
        var monitorSvcSettings = {};
        monitorSvcSettings.refreshPeriod = "180";
//        monitorSvcSettings.createRoundDiagram = false;
        monitorSvcSettings.loadingFlag = true;
        monitorSvcSettings.noGreenObjectsFlag = false;
        monitorSvcSettings.fromDate = $rootScope.monitorStart;
        monitorSvcSettings.toDate = $rootScope.monitorEnd;
        
        var getMonitorSettings = function(){
            return monitorSvcSettings;
        };
        
        var setMonitorSettings = function(monitorSettings){
//            monitorSvcSettings = monitorSettings;
            for (var key in monitorSettings){
                monitorSvcSettings[key]=monitorSettings[key];
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
console.log("MonitorSvc. Get cities and objects");    
            monitorSvcSettings.loadingFlag = true;
            var targetUrl = url+"/?fromDate="+monitorSvcSettings.fromDate+"&toDate="+monitorSvcSettings.toDate+"&noGreenColor="+monitorSvcSettings.noGreenObjectsFlag;
//console.log(targetUrl);  

            $http.get(targetUrl)
                .success(function(data){
//console.log(data);                
                    citiesMonitorSvc = data;                
                    objectsMonitorSvc = getObjectsFromCities(data);
                
//console.log(objectsMonitorSvc);            
                    //sort objects by name
                    objectsMonitorSvc.sort(function(a, b){
                        if (a.contObject.fullName>b.contObject.fullName){
                            return 1;
                        };
                        if (a.contObject.fullName<b.contObject.fullName){
                            return -1;
                        };
                        return 0;
                    });  
                    //get the list of the events, which set the object color
//                    objectsMonitorSvc.forEach(function(element){
//                        if ((element.statusColor === "RED") ||(element.statusColor === "ORANGE") ){
//                            getMonitorEventsByObject(element);
//                        }
//                    });

                monitorSvcSettings.loadingFlag = false;//data has been loaded
                $rootScope.$broadcast('monitorObjects:updated');
//                if (angular.isDefined($rootScope.monitor) && $rootScope.monitor.objectId!==null){
//                    $scope.getEventTypesByObject($rootScope.monitor.objectId, false);
//                    $rootScope.monitor.objectId = null;
//                };
                })
                .error(function(e){
                    console.log(e);
                });
            monitorSvcSettings.noGreenObjectsFlag = false; //reset flag
        };
        
            //get objects function
        var getObjects = function(url, monitorSvcSettings){ 
console.log("MonitorSvc. Get objects");    
            monitorSvcSettings.loadingFlag = true;
            var targetUrl = url+"/statusCollapse?fromDate="+monitorSvcSettings.fromDate+"&toDate="+monitorSvcSettings.toDate+"&noGreenColor="+monitorSvcSettings.noGreenObjectsFlag;
//console.log(targetUrl);  

            $http.get(targetUrl)
                .success(function(data){
                    objectsMonitorSvc = data;
//    console.log(data);            
                    //sort objects by name
                    objectsMonitorSvc.sort(function(a, b){
                        if (a.contObject.fullName>b.contObject.fullName){
                            return 1;
                        };
                        if (a.contObject.fullName<b.contObject.fullName){
                            return -1;
                        };
                        return 0;
                    });  
                    //get the list of the events, which set the object color
//                    objectsMonitorSvc.forEach(function(element){
//                        if ((element.statusColor === "RED") ||(element.statusColor === "ORANGE") ){
//                            getMonitorEventsByObject(element);
//                        }
//                    });

                monitorSvcSettings.loadingFlag = false;//data has been loaded
                $rootScope.$broadcast('monitorObjects:updated');
//                if (angular.isDefined($rootScope.monitor) && $rootScope.monitor.objectId!==null){
//                    $scope.getEventTypesByObject($rootScope.monitor.objectId, false);
//                    $rootScope.monitor.objectId = null;
//                };
                })
                .error(function(e){
                    console.log(e);
                });
            monitorSvcSettings.noGreenObjectsFlag = false; //reset flag
        };
        
        //run getObjects
//        getObjects(objectUrl, monitorSvcSettings);
        getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
            
        //get monitor events
       var getMonitorEventsByObject = function(obj){ 
//console.log("MonitorSvc. getMonitorEventsByObject");           
    //        var obj = findObjectById(objId);    
            //if cur object = null => exit function
    //        if (obj == null){
    //            return;
    //        };
            var url = objectUrl+"/"+obj.contObject.id+"/monitorEvents";//+"?fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd;
            $http.get(url)
                .success(function(data){
    //console.log("success");
                //if data is not array - exit
                    if (!data.hasOwnProperty('length')||(data.length == 0)){
                        return;
                    };
                    //temp array
                    var tmpMessage = "";
    //                var tmpMessageEx = "";
                    //make the new array of the types wich formatted to display
                    data.forEach(function(element){
                        var tmpEvent = "";
                        var contEventTime = new Date(element.contEventTime);
                        tmpEvent = contEventTime.toLocaleString()+", "+element.contEventType.name+"<br/><br/>";
                        tmpMessage+=tmpEvent;
                    });
    //                tmpTypes.sort(function(a, b){
    //                    if (a.typeEventCount > b.typeEventCount){
    //                        return -1;
    //                    };
    //                    if (a.typeEventCount < b.typeEventCount){
    //                        return 1;
    //                    };
    //                    return 0;
    //                });
                    obj.monitorEvents = tmpMessage;
                    obj.monitorEventsForMap = data;
//console.log(obj);                
                    $rootScope.$broadcast('monitorObjects:getObjectEvents',{"obj":obj});
                    //Display message
    //                var imgObj = document.getElementById("imgObj"+obj.contObject.id);
    //                imgObj.title = obj.monitorEvents;

//                    var imgObj = "#imgObj"+obj.contObject.id;          
//                    $(imgObj).qtip({
//                        content:{
//                            text: obj.monitorEvents
//                        },
//                        style:{
//                            classes: 'qtip-bootstrap qtip-nmc-monitor-tooltip'
//                        }
//                    });         
    //                makeEventTypesByObjectTable(obj);
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
console.log("MonitorSvc monitorSvcSettings.refreshPeriod watch");
            stopRefreshing();
            //set new interval
            interval = $interval(function(){
                var time = (new Date()).toLocaleString();
console.log(time);
                monitorSvcSettings.loadingFlag = true;
                getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
            },Number(monitorSvcSettings.refreshPeriod)*1000);

        }, false);
        
            //Вызвываем с заданным периодом обновление монитора
        interval = $interval(function(){
            var time = (new Date()).toLocaleString();
    console.log(time);
    //console.log(Number($scope.monitorSvcSettings.refreshPeriod));
            monitorSvcSettings.loadingFlag = true;
//            getObjects(objectUrl, monitorSvcSettings);
            getCitiesAndObjects(cityWithObjectsUrl, monitorSvcSettings);
        },Number(monitorSvcSettings.refreshPeriod)*1000);
        
        
        $rootScope.$on('monitor:updateObjectsRequest',function(){
console.log("MonitorSvc. monitor:updateObjectsRequest");            
//            getObjects(objectUrl, monitorSvcSettings);
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