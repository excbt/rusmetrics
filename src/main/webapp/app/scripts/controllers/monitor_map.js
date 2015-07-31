angular.module('portalNMC')
.controller('MonitorMapCtrl', function($rootScope, $scope, $compile, $cookies, $http, monitorSvc){
    //first steps with angular-leaflet-directive
    
    ////////////////////// tttttessssssssssssstttttttttttttt
    angular.extend($scope, {mymsg: "Мое сообщение. Ангулар отработал"});
    angular.extend($scope, {btnClick:function(){console.log("Нажата кнопка в попапе.");}});
    var markers1 = [];
    var marker1 = {
        lat: 56.85,
        lng: 53.216667,
        getMessageScope: function(){return $scope;},
        message: "<p>{{mymsg}}<button ng-click='btnClick()'>Нажми меня odin</button></p>",
        comileMessage: true
    };
    for (var i =0; i< 10; i++){
        var marker2 = {};
        marker2 = {
            lat: 56.85,
            lng: 53.216667+i,
            getMessageScope: function(){return $scope;},
            message: "<p>{{mymsg}}<button ng-click='btnClick()'>Нажми меня odin</button></p>",
            comileMessage: true
        };
        markers1.push(marker2);
    };
    
    angular.extend($scope,{
        markers1
    });
    /////////////////////end test
    
    var noticesUrl = "#/notices/list/";
    var notificationsUrl = "../api/subscr/contEvent/notifications"; 
    var objectUrl = notificationsUrl+"/contObject";//"resource/objects.json";  
    var monitorUrl = notificationsUrl+"/monitorColor";
    
    var markers = new Array();
    $scope.objects = monitorSvc.getAllMonitorObjects();//[];
    
    //cities positions
    $scope.izhevsk = {
        lat: 56.85,
        lng: 53.216667,
        zoom: 12
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
    
    var gothicCrossMarker ={
        iconUrl: 'images/gothicCross-512.png',
        iconSize: [30, 30]
    };
    
    var monitorMarker = {
        type: 'extraMarker',
        icon: 'fa-home',
        markerColor: 'blue',
        prefix: 'fa',
        shape: 'circle'
    };
    
    var mapbox_wheat= {
        name: 'Mapbox Wheat Paste',
        url: 'http://api.tiles.mapbox.com/v4/{mapid}/{z}/{x}/{y}.png?access_token={apikey}',
        type: 'xyz',
        options: {
            apikey: 'pk.eyJ1IjoiYnVmYW51dm9scyIsImEiOiJLSURpX0pnIn0.2_9NrLz1U9bpwMQBhVk97Q',
            mapid: 'bufanuvols.lia35jfp'
        }
    };
    
    var openstreetmap = {
        url: "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        options: {
            attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }
    };
    
    $scope.currentTile = openstreetmap; //set curent tile
    
    $scope.ngClickExample = function(){
console.log("Angular onclick listener");        
    }; 
    
    //get event types by object  - modify version for popup
    $scope.getEventTypesByObjectModFn = function(objId){
        var obj = findObjectById(objId);    
        //if cur object = null => exit function
        if (obj == null){
            return;
        };
      
        var url = objectUrl+"/"+obj.contObject.id+"/eventTypes/statusCollapse"+"?fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd;
//console.log(url);          
        return $http.get(url);
    };
    
    $scope.prepareMarker = function(obj, marker){             
        var markerMessage = "<div id='"+obj.contObject.id+"'>";
        markerMessage += ""+obj.contObject.fullName+"<br>";
        markerMessage+="<hr class='nmc-hr-in-modal'>";
        markerMessage +="<a title='Всего уведомлений' href='"+noticesUrl+"' ng-mousedown='setNoticeFilterByObject("+obj.contObject.id+")'>"+obj.eventsCount+" / "+obj.eventsTypesCount +"</a>(<a title='Непрочитанные уведомления' href='"+noticesUrl+"' ng-mousedown='setNoticeFilterByObjectAndRevision("+obj.contObject.id+")'>"+obj.newEventsCount+"</a>)<br>";
        markerMessage+="<hr class='nmc-hr-in-modal'>";
        marker = {
            lng: obj.contObject.contObjectGeo.geoPosX,
            lat: obj.contObject.contObjectGeo.geoPosY,
            getMessageScope: function () { 
                var newScope = $scope.$new(true);
//                angular.extend(newScope, {ngClickExample: $scope.ngClickExample});
                angular.extend(newScope, {setNoticeFilterByObject: $scope.setNoticeFilterByObject});
                angular.extend(newScope, {setNoticeFilterByObjectAndRevision: $scope.setNoticeFilterByObjectAndRevision});
                angular.extend(newScope, {setNoticeFilterByObjectAndType: $scope.setNoticeFilterByObjectAndType});
                
                var promise = $scope.getEventTypesByObjectModFn(obj.contObject.id);
                promise.success(function(data){
//        console.log(data);            
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
                            var eventMessages = [];
                            obj.eventTypes.forEach(function(eventType){
                                var eventMessage = {};
                                eventMessage.id = eventType.id;
                                var size = 16;
                                var title = "";
                                if (eventType['typeCategory']=="green"){
                                    size = 1;
                                };
                                switch (eventType['typeCategory']){
                                    case "red": title = "Критическая ситуация"; break;
                                    case "orange": title = "Некритическая ситуация"; break;

                                };
                                eventMessage.size = size;
                                eventMessage.title = title;
                                eventMessage.imgpath = "images/object-state-"+eventType['typeCategory']+".png";
                                eventMessage.name = eventType['typeName'];
                                eventMessage.count = eventType['typeEventCount'];
                                eventMessages.push(eventMessage);


                            });
                            angular.extend(newScope, {eventMessages: eventMessages});
                        };     
                    })
                    .error(function(e){
                        console.log(e);
                    });               
                return newScope; },
            message : ""+markerMessage+"<div ng-repeat='em in eventMessages'> <a href='"+noticesUrl+"' ng-mousedown='setNoticeFilterByObjectAndType("+obj.contObject.id+",em.id)'><img ng-attr-title='{{em.title}}'ng-src='{{em.imgpath}}'/>{{em.name}} ({{em.count}}) </a></div>"+"</div>",
            compileMessage : true,
            icon : monitorMarker //set current marker
        };
        marker.contObjectId = obj.contObject.id;
        marker.icon.markerColor = obj.statusColor.toLowerCase();
        return marker;
    };
    
    $scope.setObjectsOnMap = function(objectArray, markerArray){
        //check markers
        var deletedMarkers = [];
        if ((angular.isDefined(markerArray.length))&&(markerArray.length!=0)){
            markerArray.forEach(function(el, index){
                var isMarkerDelete = true;
                objectArray.some(function(elem){
                    if((elem.contObject.id===el.contObjectId)){
                        isMarkerDelete = false;
                        return true;
                    };
                });
                if (isMarkerDelete===true){
                    deletedMarkers.push(index);
                };
            });
            //delete markers
            deletedMarkers.forEach(function(delIndexEl){
                markerArray.splice(index, 1);
            });
        };
        
        //check objects
        objectArray.forEach(function(elem){
//console.log(elem);            
            if((elem.contObject.contObjectGeo ===null)||(elem.contObject.contObjectGeo.geoPosX===null)){
console.warn("Warning. Object without coordinates.");                
console.warn(elem);                                
                return;
            };
            var marker = {};
            var isMarkerExists = false;
            if ((angular.isDefined(markerArray.length))&&(markerArray.length!=0)){
                markerArray.some(function(el){
                    if((elem.contObject.id===el.contObjectId)){
                        marker = el;
                        isMarkerExists = true;
                        return true;
                    };
                });
            };
            marker= $scope.prepareMarker(elem, marker);
//console.log(marker);    
            if (isMarkerExists!==true){
                markerArray.push(marker);  
            };      
        });
        
    };
    $scope.setObjectsOnMap($scope.objects, markers);
    angular.extend($scope, {markers});
    
    
    //marker onClick listener
    //in new version NOT NEED
//    $scope.$on('leafletDirectiveMarker.click', function(event, args){  
//console.log(event);        
//console.log(args);                
//console.log('leafletDirectiveMarker.click');        
//        $scope.getEventTypesByObject(args.model);
//    });
    
    $scope.$on('monitorObjects:updated',function(){
        $scope.objects = monitorSvc.getAllMonitorObjects();
        //markers = [];
        $scope.setObjectsOnMap($scope.objects, markers);
    });

    //on click listener
//    $scope.$on("leafletDirectiveMap.click",function(event, args){
//        var leafEvent = args.leafletEvent;
//        
//        $scope.markers.push({
//            lat: leafEvent.latlng.lat,
//            lng: leafEvent.latlng.lng,
//            message: "What's place? <button id='btn1' class='btn btn-default btn-xs' onclick='press()'>press</button>"
//        });
//    });
    
    var mapCenter = $scope.izhevsk; //center of map
    
    angular.extend($scope,{
       mapCenter 
    });
    
    
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
    
    
    //in new version NOT NEED
    //get event types by object
    $scope.getEventTypesByObject = function(model){
        var markerTmp = {};
        markers.some(function(element){
            if (element.contObjectId === model.contObjectId){
                markerTmp = element;
//                markerTmp.focus = false;
                return true;
            }
        });
console.log(markerTmp);        
        var obj = findObjectById(model.contObjectId);    
        //if cur object = null => exit function
        if (obj == null){
            return;
        };
      
        var url = objectUrl+"/"+obj.contObject.id+"/eventTypes/statusCollapse"+"?fromDate="+$rootScope.monitorStart+"&toDate="+$rootScope.monitorEnd;
console.log(url);          
        $http.get(url)
            .success(function(data){
console.log(data);            
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
                    var newMessages = markerTmp.message.split("<div id='eventTypes'>");
                    newMessages[0]+="<div id='eventTypes'>";
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
                        
                        
                    });
                    newMessages[0]+="</div>";
console.log("1");                    
                    markerTmp.message = newMessages[0];
//                    markerTmp.focus = true;
console.log(markerTmp);                    
console.log("2");                                        
                };     
            })
            .error(function(e){
                console.log(e);
            });        
    };
        
    //Set filters for notice window
    $scope.setNoticeFilterByObject = function(objId){
//console.log("setNoticeFilterByObject");        
        $rootScope.monitor = {};
        $cookies.monitorFlag = true;
        $cookies.objectMonitorId = objId;
        $cookies.isNew = null;
        $cookies.typeIds = null;
        $cookies.fromDate = $rootScope.monitorStart;
        $cookies.toDate = $rootScope.monitorEnd;
        $rootScope.reportStart = $rootScope.monitorStart;
        $rootScope.reportEnd = $rootScope.monitorEnd;
//console.log($cookies);        
    };
    
    $scope.setNoticeFilterByObjectAndRevision = function(objId){
//console.log("setNoticeFilterByObjectAndRevision");                
        $scope.setNoticeFilterByObject(objId);        
        $cookies.isNew = true;        

    };
    
    $scope.setNoticeFilterByObjectAndType = function(objId, typeId){
//console.log("setNoticeFilterByObjectAndType");                        
        $scope.setNoticeFilterByObject(objId);
        $cookies.typeIds = [typeId];
    };
    
    
    //first steps without angular directive
//    var map = new L.Map('map');//.setView([51.505, -0.09], 13); 
//
//	// create the tile layer with correct attribution
//	var osmUrl='http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
//	var osmAttrib='Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
//	var osm = new L.TileLayer(osmUrl, {minZoom: 8, maxZoom: 12, attribution: osmAttrib});		
//
//	// start the map in Izhevsk
//	map.setView(new L.LatLng(56.81, 53.13),9);
//	map.addLayer(osm);
//    $scope.objects.forEach(function(elem){
//        if((elem.contObject.contObjectGeo ===null)||(elem.contObject.contObjectGeo.geoPosX===null)){
//console.warn("Warning. Object without coordinates.");                
//console.warn(elem);                                
//            return;
//        };
//        var marker = L.marker([elem.contObject.contObjectGeo.geoPosY, elem.contObject.contObjectGeo.geoPosX]).addTo(map);
//    });
    
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