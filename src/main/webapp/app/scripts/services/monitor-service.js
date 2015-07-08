'use strict';
angular.module('portalNMC')
    .service('monitorSvc', ['$rootScope', '$http', '$interval', function($rootScope, $http, $interval){
                //url to data
        var notificationsUrl = "../api/subscr/contEvent/notifications"; 
        var objectUrl = notificationsUrl+"/contObject";
        
        var objectsMonitorSvc = [];
        //default date interval settings
        $rootScope.monitorStart = moment().subtract(6, 'days').startOf('day').format('YYYY-MM-DD');
        $rootScope.monitorEnd =  moment().endOf('day').format('YYYY-MM-DD');    

        //monitor settings
        var monitorSvcSettings = {};
        monitorSvcSettings.refreshPeriod = "180";
//        monitorSvcSettings.createRoundDiagram = false;
        monitorSvcSettings.loadingFlag = true;
        monitorSvcSettings.noGreenObjectsFlag = false;
        monitorSvcSettings.fromDate = $rootScope.monitorStart;
        monitorSvcSettings.toDate = $rootScope.monitorEnd;
        
        var getAllMonitorObjects = function(){
//            if (objectsMonitorSvc.length===0){
//                $rootScope.$broadcast('monitor:updateObjectsRequest');
//            };
            return objectsMonitorSvc;
        };
        
            //get objects function
        var getObjects = function(url, monitorSvcSettings){ 
console.log("MonitorSvc. Get objects");    
            monitorSvcSettings.loadingFlag = true;
            var targetUrl = url+"/statusCollapse?fromDate="+monitorSvcSettings.fromDate+"&toDate="+monitorSvcSettings.toDate+"&noGreenColor="+monitorSvcSettings.noGreenObjectsFlag;
    console.log(targetUrl);  

            $http.get(targetUrl)
                .success(function(data){
                    objectsMonitorSvc = data;
    //console.log(data);            
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
                    objectsMonitorSvc.forEach(function(element){
                        if ((element.statusColor === "RED") ||(element.statusColor === "ORANGE") ){
                            getMonitorEventsByObject(element);
                        }
                    });

//                    makeObjectTable();
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
        getObjects(objectUrl, monitorSvcSettings);
        
            //get monitor events
       var getMonitorEventsByObject = function(obj){       
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
//    console.log("new period = "+newPeriod);        
            //cancel previous interval
            stopRefreshing();
            //set new interval
            interval = $interval(function(){
                var time = (new Date()).toLocaleString();
    //console.log("new interval");            
    console.log(time);
    //console.log(Number($scope.monitorSvcSettings.refreshPeriod)); 
                monitorSvcSettings.loadingFlag = true;
                getObjects(objectUrl, monitorSvcSettings);
            },Number(monitorSvcSettings.refreshPeriod)*1000);

        }, false);
        
            //Вызвываем с заданным периодом обновление монитора
        interval = $interval(function(){
            var time = (new Date()).toLocaleString();
    console.log(time);
    //console.log(Number($scope.monitorSvcSettings.refreshPeriod));
            monitorSvcSettings.loadingFlag = true;
            getObjects(objectUrl, monitorSvcSettings);
        },Number(monitorSvcSettings.refreshPeriod)*1000);
        
        
        $rootScope.$on('monitor:updateObjectsRequest',function(){
console.log("MonitorSvc. monitor:updateObjectsRequest");            
            getObjects(objectUrl, monitorSvcSettings);
        });
        
         return {
            monitorSvcSettings,
            getAllMonitorObjects
        };
        
    }]);