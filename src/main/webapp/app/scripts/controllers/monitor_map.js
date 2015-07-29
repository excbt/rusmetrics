angular.module('portalNMC')
.controller('MonitorMapCtrl', function($rootScope, $scope, $compile, $cookies, $http, monitorSvc){
    //first steps with angular-leaflet-directive
    
    var noticesUrl = "#/notices/list/";
    var notificationsUrl = "../api/subscr/contEvent/notifications"; 
    var objectUrl = notificationsUrl+"/contObject";//"resource/objects.json";  
    var monitorUrl = notificationsUrl+"/monitorColor";
    
    $scope.markers = new Array();
    $scope.objects = monitorSvc.getAllMonitorObjects();//[];
    
    $scope.setObjectsOnMap = function(objectArray, markerArray){
        objectArray.forEach(function(elem){
            var marker = {};
            marker.icon ={
                type: 'extraMarker',
                icon: 'fa-home',
                markerColor: 'blue',
                prefix: 'fa',
                shape: 'circle'
            };
            marker.lat = 56+Math.random();
            marker.lng = 53+Math.random();
            var markerMessage = "";//"<div id='"+elem.contObject.id+"'>";
            markerMessage = ""+elem.contObject.fullName+"<br>";
            markerMessage+="<hr class='nmc-hr-in-modal'>";
            markerMessage +="<a title='Всего уведомлений' href=''>"+elem.eventsCount+" / "+elem.eventsTypesCount +"</a>(<a title='Непрочитанные уведомления' href=''>"+elem.newEventsCount+"</a>) <br>";
            markerMessage+="<hr class='nmc-hr-in-modal'>";
            markerMessage+="<div eventTypes>";
            
//TODO: add notice types for object marker
//console.log(elem);            
//            if (angular.isDefined(elem.eventTypes)){
//console.log("elem.eventTypes is defined.");                
//                elem.eventTypes.forEach(function(eventType){
//                    var size = 16;
//                    var title = "";
//                    if (eventType['typeCategory']=="green"){
//                        size = 1;
//                    };
//                    switch (event[column.name]){
//                        case "red": title = "Критическая ситуация"; break;
//                        case "orange": title = "Некритическая ситуация"; break;
//
//                    };
//                    markerMessage +="<a href=''>"+"<img title=\""+title+"\" height=\""+size+"\" width=\""+size+"\" src=\""+"images/object-state-"+eventType['typeCategory']+".png"+"\"/>"+"</a>"
//                });
//            };        
            marker.message = markerMessage;
            marker.contObjectId = elem.contObject.id;
            marker.icon.markerColor = elem.statusColor.toLowerCase();
            
//            marker.lat = elem.lat;
//            marker.lng = elem.lng;
            markerArray.push(marker);        
        });
    };
    $scope.setObjectsOnMap($scope.objects, $scope.markers);
    
    $scope.$on('leafletDirectiveMarker.click', function(event, args){
//console.log(event);
//console.log(args);        
        $scope.getEventTypesByObject(args.model);
    });
    
    $scope.$on('monitorObjects:updated',function(){
        $scope.objects = monitorSvc.getAllMonitorObjects();
        $scope.markers = [];
        $scope.setObjectsOnMap($scope.objects, $scope.markers);
//console.log($scope.objects);        
//console.log("Monitor ctrl. Objects are got."); 
//var time = new Date();
//console.log(time);  
        
//        $scope.objects.forEach(function(element){
//            if ((element.statusColor === "RED") ||(element.statusColor === "ORANGE") ){
//                monitorSvc.getMonitorEventsByObject(element);
//            }
//        });
    });
    
    $scope.izhevsk = {
        lat: 56.85,
        lng: 53.216667,
        zoom: 9
    };
    
    $scope.london = {
        lat: 51.505,
        lng: -0.09,
        zoom: 12
    };
    
    $scope.cityMarkers = {
        izhevsk:{
            lat: 56.85,
            lng: 53.216667
        },
        sarapul:{
            lat: 56.466667,
            lng: 53.8
        },
        votkinsk:{
            lat: 57.05,
            lng: 54
        }
    };
    
    $scope.$on("leafletDirectiveMap.click",function(event, args){
        var leafEvent = args.leafletEvent;
        
        $scope.markers.push({
            lat: leafEvent.latlng.lat,
            lng: leafEvent.latlng.lng,
            message: "What's place? <button id='btn1' class='btn btn-default btn-xs' onclick='press()'>press</button>"
        });
    });
    
    var mapCenter = $scope.izhevsk;
    
    angular.extend($scope,{
       mapCenter 
    });
    
//    var btn1 = document.getElementById('btn1');
//console.log(btn1);    
//    var btn1onclick = function(){
//        console.log("Onclicklistener. GotoNotices() run.");        
//        window.location.assign("#/notices/list/");
//    };
//    
    
    function findObjectById(objId){
        var obj = null;
        $scope.objects.some(function(element){
            if (element.contObject.id === objId){
                obj = element;
                return true;
            }
        });        
        return obj;
    };
    //get event types by object
    $scope.getEventTypesByObject = function(model){
        var marker = {};
        $scope.markers.some(function(element){
            if (element.contObjectId === model.contObjectId){
                marker = element;
                return true;
            }
        });
        var obj = findObjectById(model.contObjectId);    
        //if cur object = null => exit function
        if (obj == null){
            return;
        };
      
        var url = objectUrl+"/"+obj.contObject.id+"/eventTypes/statusCollapse"+"?fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd;
//console.log(url);          
        $http.get(url)
            .success(function(data){
            //if data is not array - exit
                if (!data.hasOwnProperty('length')||(data.length == 0)){
                    return;
                };
                //temp array
                var tmpTypes = [];
                //make the new array of the types wich formatted to display
                data.forEach(function(element){
                    var tmpType = {};
                    tmpType.id = element.contEventType.id;
                    tmpType.isBaseEvent = element.contEventType.isBaseEvent;
                    tmpType.typeCategory = element.statusColor.toLowerCase();
                    tmpType.typeEventCount = element.totalCount;
                    tmpType.typeName = element.contEventType.caption;
                    tmpTypes.push(tmpType);
                });
                tmpTypes.sort(function(a, b){
                    if (a.typeEventCount > b.typeEventCount){
                        return -1;
                    };
                    if (a.typeEventCount < b.typeEventCount){
                        return 1;
                    };
                    return 0;
                });
                obj.eventTypes = tmpTypes;
                if (angular.isDefined(obj.eventTypes)){
                    var newMessages = marker.message.split("<div eventTypes>");
                    newMessages[0]+="<div eventTypes>";
                    obj.eventTypes.forEach(function(eventType){
                        var size = 16;
                        var title = "";
                        if (eventType['typeCategory']=="green"){
                            size = 1;
                        };
                        switch (eventType['typeCategory']){
                            case "red": title = "Критическая ситуация"; break;
                            case "orange": title = "Некритическая ситуация"; break;

                        };
                        
                        newMessages[0] +="<br><a href=''>"+"<img title=\""+title+"\" height=\""+size+"\" width=\""+size+"\" src=\""+"images/object-state-"+eventType['typeCategory']+".png"+"\"/>"+" "+eventType['typeName']+" ("+eventType['typeEventCount']+")"+"</a>";
                        newMessages[0]+="</div eventTypes>";
                        
                    });
                    marker.message = newMessages[0];
                };     
            })
            .error(function(e){
                console.log(e);
            });        
    };
    
    
    //first steps without angular directive
//    var map = new L.Map('map');//.setView([51.505, -0.09], 13); 
//
//	// create the tile layer with correct attribution
//	var osmUrl='http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
//	var osmAttrib='Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
//	var osm = new L.TileLayer(osmUrl, {minZoom: 8, maxZoom: 12, attribution: osmAttrib});		
//
//	// start the map in South-East England
//	map.setView(new L.LatLng(56.81, 53.13),9);
//	map.addLayer(osm);
//    
//    var marker = L.marker([56.85, 53.216667]).addTo(map);
//    var objectCount = 15;
//    
//    var cityName = "Ижевск";
//    $cookies.cityName = cityName;
//    
//    var detailButton = "<a class=\"btn btn-xs\" href=\"#/notices/list/\">5 нештатных ситуаций</a>";
//    
//    var markerPopup = "<b>"+cityName+"</b>"+objectCount+" объектов <br>"+detailButton;
//    
//    marker.bindPopup(markerPopup)
////console.log(marker);    
////    $compile(marker)($scope);
//    var circle = L.circle([56.81, 53.13], 1000, {
//        color: 'red',
//        fillColor: '#f03',
//        fillOpacity: 0.5
//    }).addTo(map);
//    
//    var polygon = L.polygon([
//        [56.819, 53.08],
//        [56.603, 53.16],
//        [56.603, 53.047],
//        [56.71, 53.047]
//        
//    ]).addTo(map);
//    
//    
    $scope.gotoNotices = function(){
console.log("gotoNotices() run.");        
        window.location.assign("#/notices/list/");
    };
//    
//    $scope.setMarker = function(cityName, coordinates){
//    };
});


function press(){
    console.log("js listener onclick. GotoNotices() run.");        
        window.location.assign("#/notices/list/");
};