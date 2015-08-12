angular.module('portalNMC')
.controller('ObjectsMapCtrl', function($rootScope, $scope, objectSvc){
    $scope.mapSettings = {};
    $scope.mapSettings.zoomBound = 9; //zoom>zoomBound - view objects on map; zoom<zoomBound - view cities on map
    $scope.mapSettings.dateFrom = moment().subtract(30, 'days').startOf('day').format('YYYY-MM-DD');
    $scope.mapSettings.dateTo = moment().endOf('day').format('YYYY-MM-DD');
    
    var noticesUrl = "#/notices/list/";
    var notificationsUrl = "../api/subscr/contEvent/notifications"; 
    var objectUrl = notificationsUrl+"/contObject";//"resource/objects.json";  
    var monitorUrl = notificationsUrl+"/monitorColor";
    
    var markers = new Array();
    var markersForObjects = new Array();
    var markersForCities = new Array();
    var markersOnMap = new Array();
    
    $scope.objects = [];//objectSvc.getAllMonitorObjects();//[];
//    objectSvc.sortObjectsByFullName($scope.objects);
    $scope.cities = [];//objectSvc.getAllMonitorCities();
    $scope.objectsOfCities = [];
    
    //cities positions
    $scope.izhevsk = {
        lat: 56.85,
        lng: 53.216667,
        zoom: 8
    };
    
    var mapCenter = $scope.izhevsk; //center of map
    angular.extend($scope,{
       mapCenter 
    });
    
    var monitorMarker = {
        type: 'extraMarker',
        icon: 'fa-home',
        markerColor: 'blue',
        prefix: 'fa',
        shape: 'circle'
    };
    
    var openstreetmap = {
        url: "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        options: {
            attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }
    };
    
    var currentTile = openstreetmap; //set curent tile
    angular.extend($scope, {currentTile});
    
    var getObjectsDataForCities = function(cities, objectArr){
        var tmp = [];
        if (!angular.isArray(cities)||(cities.length === 0)){
            return [];
        };
        cities.forEach(function(city){
            if (!angular.isArray(city.cityObjects) || (city.cityObjects.length===0)){
                return;
            };
            city.cityObjects.forEach(function(cityObject){
                tmp.push(cityObject);
            });
        });
        objectArr = tmp;
//console.log(objectArr);          
        return objectArr;      
    };
    
    //refresh data from server
    var getCityDataWithParams = function(city, settings){
               
        var citiesPromise = objectSvc.getCityConsumingData(city.cityFiasUUID, settings);
        citiesPromise.then(function(response){
//                            var ind = Math.round(Math.random()*3);
//                            cities = [response.data[ind]]; 
//                            $scope.newCityData = response.data;
                            if(!angular.isArray(response.data)||(response.data.length===0)){
                                console.log("Not enough objects for the city.");
                                console.log(response.data);
                                return;
                            };
                            $scope.newCityData = response.data[0];
                            $scope.markerSettings.isNewDataFlag = true;
                            calculateCityServicesSummary($scope.newCityData);
console.log($scope.newCityData);            
console.log(city);
                            //city vs newCity
                            var tmpDiff = {};
                            tmpDiff.hw = ((city.servicesSummary.hw-$scope.newCityData.servicesSummary.hw)).toFixed(2);
                            tmpDiff.hwPerCent = ($scope.newCityData.servicesSummary.hw>0)?((city.servicesSummary.hw-$scope.newCityData.servicesSummary.hw)*100/$scope.newCityData.servicesSummary.hw).toFixed(2):null;
                            tmpDiff.hwMeasureUnit = 'м3';
                            tmpDiff.heat = ((city.servicesSummary.heat-$scope.newCityData.servicesSummary.heat)).toFixed(2);
                            tmpDiff.heatPerCent = ($scope.newCityData.servicesSummary.heat>0)?((city.servicesSummary.heat-$scope.newCityData.servicesSummary.heat)*100/$scope.newCityData.servicesSummary.heat).toFixed(2):null;
                            tmpDiff.hwMeasureUnit = 'м3';
                            tmpDiff.heatArea = city.servicesSummary.heatArea;
                            tmpDiff.heatAveragePerCent = (($scope.newCityData.servicesSummary.heatAverage>0)&&(tmpDiff.heatArea>0))?((city.servicesSummary.heatAverage-$scope.newCityData.servicesSummary.heatAverage)*100/$scope.newCityData.servicesSummary.heatAverage).toFixed(2):null;
                            tmpDiff.heatAverage = ($scope.newCityData.servicesSummary.heatAverage>0)?((city.servicesSummary.heatAverage-$scope.newCityData.servicesSummary.heatAverage)).toFixed(2):null;
                            tmpDiff.hwAveragePerCent = (($scope.newCityData.servicesSummary.hwAverage>0)&&(tmpDiff.heatArea>0))?((city.servicesSummary.hwAverage-$scope.newCityData.servicesSummary.hwAverage)*100/$scope.newCityData.servicesSummary.hwAverage).toFixed(2):null;
                            tmpDiff.hwAverage = (($scope.newCityData.servicesSummary.hwAverage>0))?((city.servicesSummary.hwAverage-$scope.newCityData.servicesSummary.hwAverage)).toFixed(2):null;
console.log(tmpDiff);            
                            $scope.diffCityData = tmpDiff;
                            $scope.markerSettings.isDiffDataFlag = true;
                        },
                       function(err){
                            console.log(err);
                        }
        );
    };
    
    var refreshCitiesData = function(){
               
        var citiesPromise = objectSvc.getCitiesConsumingData($scope.mapSettings);
        citiesPromise.then(function(response){
//                            var ind = Math.round(Math.random()*3);
//                            cities = [response.data[ind]]; 
                            $scope.cities = response.data;
                            $scope.objectsOfCities = getObjectsDataForCities($scope.cities, $scope.objectsOfCities);     
                            if ($scope.mapCenter.zoom > $scope.mapSettings.zoomBound){
//                                markers = $scope.setObjectsOnMap($scope.objectsOfCities, markers);
                                markersForObjects = $scope.setObjectsOnMap($scope.objectsOfCities, markersForObjects);
                                $scope.markersOnMap = [];
                                angular.extend($scope, {markersOnMap: markersForObjects});
                            }else{
                                markersForCities = $scope.setCitiesOnMap($scope.cities, markersForCities);
                                $scope.markersOnMap = [];
                                angular.extend($scope, {markersOnMap: markersForCities});
                            };

//                            angular.extend($scope, {markersOnMap: markers});          
                        },
                       function(err){
                            console.log(err);
                        }
        );
    };
    

    
    //get data from server
    var tmpObj = {};
    tmpObj.cities = $scope.cities;
    tmpObj.objectsOfCities = $scope.objectsOfCities;
    var citySet = {};
    citySet.dateFrom = $scope.mapSettings.dateFrom;
    citySet.dateTo = $scope.mapSettings.dateTo; 
    refreshCitiesData();
    
    
    var viewObjectsOnMap = function(cityFiasUUID){
console.log("viewObjectsOnMap."); 
        
        var popupPane = document.getElementsByClassName("leaflet-pane leaflet-popup-pane");
        popupPane[0].innerHTML = "";
              
        var mc = {};
        var curCity = {};
        $scope.cities.some(function(elem){
            if (elem.cityFiasUUID === cityFiasUUID){
                mc.lat = elem.cityGeoPosY;
                mc.lng = elem.cityGeoPosX;
                curCity = elem;               
                return true;
            };
        });
        mc.zoom = $scope.mapSettings.zoomBound+2;
        
        var tmpObjects = curCity.cityObjects;
        $scope.setObjectsOnMap(tmpObjects, markersForObjects); 
        $scope.markersOnMap = [];
console.log(curCity);        
console.log(markersForObjects);        
        angular.extend($scope, {markersOnMap: markersForObjects});
        $scope.viewCurrentCityObjectsFlag = true;

        angular.extend($scope, {mapCenter: mc});
console.log($scope.mapCenter); 
    };
    
    var compareWith = function(){
        $scope.markerSettings.cmpFlag = !$scope.markerSettings.cmpFlag;
        
//        if($scope.markerSettings.cmpFlag === true){ 
//            var popup = document.getElementsByClassName("leaflet-popup-content-wrapper");
//            popup[0].className += " nmc-leaflet-popup-content-wrapper-compare";
//        }else{
//            var popup = document.getElementsByClassName("leaflet-popup-content-wrapper");
//            popup[0].className = "leaflet-popup-content-wrapper";
//        };
    };
    
    var changeCmpPeriod = function(firstPeriod, daysAgo, secondPeriod){
        $scope.markerSettings.runFlag = false;
        var result = {};
        result.dateFrom = moment(firstPeriod.dateFrom).subtract(daysAgo, 'days').format('YYYY-MM-DD');
        result.dateTo = moment(firstPeriod.dateTo).subtract(daysAgo, 'days').format('YYYY-MM-DD');
        secondPeriod = result;
        $scope.cmpPeriod = result;      
        return result;
    };
    
    //TODO: доделать сравнение по городу
    
        //данные для сравнения по объекту
//    получаем данные за заданный период по объекту, делаем сравнение с текущими данными
    //записываем новые полученные данные за заданный период в массив newObjectData
    //делаем расчет разностей
    // записываем полученные разности в массив diffObjectData
    var getObjectsConsumingForObject = function(object, settings){
        var tmpSettings = angular.copy(settings);
//        tmpSettings.objectCompareFlag == true;
//        refreshCitiesDataWithParams(tmpCities, tmpObjects, tmpSettings);
        objectSvc.getObjectConsumingData(tmpSettings,object.contObject.id)
            .then(function(response){
                $scope.newObjectData = response.data;
                for (var i=0;i<$scope.newObjectData.serviceTypeARTs.length;i++){
                    var curData = $scope.newObjectData.serviceTypeARTs[i];
console.log(curData);                        
                    curData.absConsValue = (curData.absConsValue===null)?null:Number(curData.absConsValue.toFixed(2));
                    curData.tempValue = (curData.tempValue===null)?null:Number(curData.tempValue.toFixed(2));
                    
//console.log(curData);                    
                };
                var tmpDiffArray = [];
                object.serviceTypeARTs.forEach(function(serv){
                    for (var i=0; i<$scope.newObjectData.serviceTypeARTs.length;i++){
                        var curData = $scope.newObjectData.serviceTypeARTs[i];
                        if (serv.contServiceType === curData.contServiceType){
console.log(curData);                            
console.log(serv);                                                        
                            var tmp = {};
                            tmp.contServiceType= serv.contServiceType;
                            tmp.measureUnit = serv.measureUnit;
                            tmp.absConsValue = Number((serv.absConsValue - curData.absConsValue).toFixed(2));
                            tmp.absConsValueAverage = (object.contObject.heatArea>0)?Number((tmp.absConsValue/object.contObject.heatArea).toFixed(2)):null;
                          
                            tmp.absConsValueAveragePerCent = ((object.contObject.heatArea>0)&&((curData.absConsValue!==null))&&(curData.absConsValue!==0))?Number((tmp.absConsValueAverage/curData.absConsValue*object.contObject.heatArea*100).toFixed(2)):null;
//                                (object.contObject.heatArea>0)?Number(((tmp.absConsValue)*100/curData.absConsValue).toFixed(2)):null;
                            tmp.tempValue = Number((serv.tempValue - curData.tempValue).toFixed(2));
                            tmp.absConsValuePerCent =(curData.absConsValue>0)? Number(((serv.absConsValue - curData.absConsValue)/curData.absConsValue*100).toFixed(2)): null;
                            tmp.tempValuePerCent = (curData.tempValue>0)?Number(((serv.tempValue - curData.tempValue)/curData.tempValue*100).toFixed(2)):null;
                            tmpDiffArray.push(tmp);
                        };
                    };
                });
                $scope.diffObjectData = tmpDiffArray;
console.log($scope.diffObjectData);            
            });
        
//        var 
    };
    
    var getObjectsConsumingForCity = function(city, settings){
        $scope.markerSettings.runFlag = true;
        var tmpCities = [];
        var tmpObjects = [];
        var tmpSettings = angular.copy(settings);
        tmpSettings.cityCompareFlag == true;
        getCityDataWithParams(city, tmpSettings);
        
//        var 
    };
    var runCityCmp = function(){
        //get city data with city and period
        
        //calculate hw and heat data
        //calculate diff in % and number
        
        
    };
    
//***************************** perform cities data*****************************
    //************************************************************************
    
    //prepare city agregation data
    var calculateCityServicesSummary= function(city){
        var hwSum = 0;
        var heatSum = 0;
        var areaSum = 0
        city.cityObjects.forEach(function(obj){
            var hw = 0;
            var heat = 0;
            if (angular.isDefined(obj.contObject.heatArea)&&(obj.contObject.heatArea!=null)){
                areaSum +=obj.contObject.heatArea;
            };
            if (angular.isUndefined(obj.serviceTypeARTs) || (obj.serviceTypeARTs ===null) || (obj.serviceTypeARTs.length===0)){
                return;
            };
            obj.serviceTypeARTs.forEach(function(sys){
                switch (sys.contServiceType){
                    case "heat":heatSum+=sys.absConsValue; break;
                    case "hw" : hwSum+=sys.absConsValue; break;
                };
            });
        });
        var servicesSummary = {};
        servicesSummary.hw = hwSum;
        servicesSummary.hwMeasureUnit = 'м3';
        servicesSummary.heat = heatSum;
        servicesSummary.heatMeasureUnit = 'ГКал';
        servicesSummary.heatArea = areaSum;
        servicesSummary.heatAreaMeasureUnit = 'м2';
        if (areaSum != 0){
            servicesSummary.heatAverage = heatSum/areaSum;
            servicesSummary.heatAverageMeasureUnit = 'ГКал/м2';
            servicesSummary.hwAverage = hwSum/areaSum;
            servicesSummary.hwAverageMeasureUnit = 'м3/м2';
        };
        city.servicesSummary =servicesSummary; 
    };
    
        //create message for city marker
    var createMessageForCityMarker = function(city){
        var markerMessage = "<div id='"+city.cityFiasUUID+"' class=''>";
        markerMessage += "<label>"+city.cityName+"</label>, "+city.cityObjects.length+" объектов <button class='glyphicon glyphicon-search marginLeft5' ng-click='viewObjectsOnMap(\""+city.cityFiasUUID+"\")' title='См. объекты на карте'></button><br>";
        markerMessage+="<hr class='nmc-hr-in-modal'>";
        markerMessage+="Потребление в период с "+$scope.mapSettings.dateFrom+" по "+$scope.mapSettings.dateTo+"<br>";
        markerMessage+="<div class='row nmc-hide' ng-class=\"{'nmc-hide':!markerSettings.cmpFlag,'nmc-show':markerSettings.cmpFlag}\">";
        markerMessage+="    <div class='col-md-9'>";
        markerMessage+="сравнить с аналогичным периодом";
        markerMessage+="        <select ng-model='daysAgo' ng-init='daysAgo=\"30\";changeCmpPeriod(markerSettings, daysAgo, cmpPeriod)' ng-change='changeCmpPeriod(markerSettings, daysAgo, cmpPeriod)'>";
        markerMessage+="            <option value='30'>месяц назад</option>";
        markerMessage+="            <option value='92'>квартал назад</option>";
        markerMessage+="            <option value='182'>пол года назад</option>";
        markerMessage+="            <option value='365'>год назад</option>";
        markerMessage+="        </select>";
        markerMessage+="        <div>";
        markerMessage+="            с {{cmpPeriod.dateFrom}} по {{cmpPeriod.dateTo}}";
        markerMessage+="        </div>";
        markerMessage+="    </div>";
        markerMessage+="    <div class='col-md-2'>";
        markerMessage+="        <button title='Сравнить' ng-disabled='markerSettings.runFlag' class='btn btn-xs btn-primary' ng-click='getObjectsConsumingForCity(curCity, cmpPeriod)'>Сравнить</button>";
        markerMessage+="    </div>";
        markerMessage+="</div>";
        markerMessage+="<div class='text-center'>";
        markerMessage+="    <i class='btn btn-xs glyphicon glyphicon-chevron-down' ng-class=\"{'glyphicon-chevron-down':!markerSettings.cmpFlag,'glyphicon-chevron-up':markerSettings.cmpFlag}\" ng-click='compareWith()'></i>";
        markerMessage+="</div>";
        markerMessage+="<hr class='nmc-hr-in-modal'>";
        markerMessage+="    <div class='container-fluid'>";
        markerMessage+="        <div class='row'>";
                //left column
        markerMessage+="            <div class='col-md-4 noPadding'>";
        markerMessage+="    <label>Горячая вода:</label><br>";
        markerMessage+="        <div class='paddingLeft5'>Всего: "+city.servicesSummary.hw.toFixed(2)+" "+city.servicesSummary.hwMeasureUnit+"<br>";
        if (city.servicesSummary.heatArea>0){
            markerMessage+="        Удельное: "+city.servicesSummary.hwAverage.toFixed(2)+" "+city.servicesSummary.hwAverageMeasureUnit+"<br>";
        };
        markerMessage+="        </div>";
        markerMessage+="    <label>Отопление:</label><br>";
        markerMessage+="        <div class='paddingLeft5'>Всего: "+city.servicesSummary.heat.toFixed(2)+" "+city.servicesSummary.heatMeasureUnit+"<br>";
        if (city.servicesSummary.heatArea>0){
            markerMessage+="        Удельное: "+city.servicesSummary.heatAverage.toFixed(2)+" "+city.servicesSummary.heatAverageMeasureUnit+"<br>";
        };
        markerMessage+="        </div>";

        markerMessage+="    </div>";
            // center column
        markerMessage+="            <div class='col-md-4 noPadding' ng-class=\"{'nmc-hide':!markerSettings.isDiffDataFlag,'nmc-show':markerSettings.isDiffDataFlag}\">";
        markerMessage+="                <br>";
        markerMessage+="                <div><span ng-if='diffCityData.hw>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffCityData.hwPerCent<100),'nmc-alert':(diffCityData.hwPerCent>=100)}\"></i></span><span ng-if='diffCityData.hw<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-alert':(diffCityData.hwPerCent>=100),'nmc-normal':(diffCityData.hwPerCent<100)}\"></i></span><span ng-if='diffCityData.hwPerCent!=null'>{{diffCityData.hwPerCent}}%  {{diffCityData.hw}} м<sup>3</sup></span></div>";
        markerMessage+="                <div><span ng-if='diffCityData.hwAverage>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-alert':(diffCityData.hwAveragePerCent>=100),'nmc-normal':(diffCityData.hwAveragePerCent<100)}\"></i></span><span ng-if='diffCityData.hwAverage<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-alert':(diffCityData.hwAveragePerCent>=100),'nmc-normal':(diffCityData.hwAveragePerCent<100)}\"></i></span><span ng-if='diffCityData.hwAveragePerCent!=null'>{{diffCityData.hwAveragePerCent}}% {{diffCityData.hwAverage}}м<sup>3</sup></span></div>";
        markerMessage+="                   <br>";
        markerMessage+="                <div><span ng-if='diffCityData.heat>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-alert':(diffCityData.heatPerCent>=100),'nmc-normal':(diffCityData.heatPerCent<100)}\"></i></span><span ng-if='diffCityData.heat<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-alert':'diffCityData.heatPerCent>=100','nmc-normal':(diffCityData.heatPerCent<100)}\"></i></span><span ng-if='diffCityData.heatPerCent!=null'>{{diffCityData.heatPerCent}}% {{diffCityData.heat}} ГКал</span></div>";
        markerMessage+="                <div><span ng-if='diffCityData.heatAverage>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-alert':(diffCityData.heatAveragePerCent>=100), 'nmc-normal':(diffCityData.heatAveragePerCent<100)}\"></i></span><span ng-if='diffCityData.heatAverage<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-alert':(diffCityData.heatAveragePerCent>=100),'nmc-normal':(diffCityData.heatAveragePerCent<100)}\"></i></span><span ng-if='diffCityData.heatAveragePerCent!=null'>{{diffCityData.heatAveragePerCent}}% {{diffCityData.heatAverage}}ГКал</span></div>";
        markerMessage+="    </div>";

            //right column
        markerMessage+="            <div class='col-md-4 noPadding' ng-class=\"{'nmc-hide':!markerSettings.isNewDataFlag,'nmc-show':markerSettings.isNewDataFlag}\">";
        markerMessage+="    <label>Горячая вода:</label><br>";
        markerMessage+="        <div class='paddingLeft5'>Всего: {{newCityData.servicesSummary.hw.toFixed(2)}}{{newCityData.servicesSummary.hwMeasureUnit}}<br>";
            markerMessage+="        <span ng-if='newCityData.servicesSummary.heatArea>0'> Удельное: {{newCityData.servicesSummary.hwAverage.toFixed(2)}} {{newCityData.servicesSummary.hwAverageMeasureUnit}}</span>";
        markerMessage+="        </div>";
        markerMessage+="    <label>Отопление:</label><br>";
        markerMessage+="        <div class='paddingLeft5'>Всего: {{newCityData.servicesSummary.heat.toFixed(2)}} {{newCityData.servicesSummary.heatMeasureUnit}}<br>";
            markerMessage+="        <span ng-if='newCityData.servicesSummary.heatArea>0'> Удельное: {{newCityData.servicesSummary.heatAverage.toFixed(2)}} {{newCityData.servicesSummary.heatAverageMeasureUnit}}</span>";
        markerMessage+="        </div>";

        markerMessage+="    </div>";
        
        markerMessage+="        </div>";
        markerMessage+="    </div>";
        markerMessage+="</div>";  
        return markerMessage;
    };
    
        //prepare city marker
    $scope.prepareCityMarker = function(city, marker){                  
        if ((city.cityGeoPosX === null) || (city.cityGeoPosY===null)){
            return;
        }; 
        
        //calculate system indicators for all objects of the city
        calculateCityServicesSummary(city);
        
        var markerMessage = createMessageForCityMarker(city);

        
//console.log(markerMessage);        
//        marker = {
//            focus: false,
//            lng: city.cityGeoPosX,
//            lat: city.cityGeoPosY,
//            getMessageScope: function () {              
//                return $scope; },
//            message : ""+markerMessage+"",
//            compileMessage : true,
//            icon : monitorMarker //set current marker
//        };
        
        marker.focus= false;
        marker.lng=city.cityGeoPosX;
            marker.lat= city.cityGeoPosY;
            marker.getMessageScope=function () {
                angular.extend($scope, {
                    markerSettings:{
                        cmpFlag: false,
                        dateFrom: $scope.mapSettings.dateFrom,
                        dateTo: $scope.mapSettings.dateTo
                    }
                });
                angular.extend($scope, {curCity: city});
                angular.extend($scope,{changeCmpPeriod: changeCmpPeriod});
                angular.extend($scope,{compareWith: compareWith});
                angular.extend($scope, {viewObjectsOnMap: viewObjectsOnMap});
                angular.extend($scope, {getObjectsConsumingForCity: getObjectsConsumingForCity});
                return $scope; };
            marker.message = ""+markerMessage+"";
            marker.compileMessage = true;
            marker.icon =  monitorMarker; //set current marker      
        marker.cityFiasUUID = city.cityFiasUUID;                
        return marker;
    };
    
    var deleteCityMarkers = function(objectsArray, markerArray){
        var deletedMarkers = [];
        if ((angular.isDefined(markerArray.length))&&(markerArray.length!=0)){
            markerArray.forEach(function(el, index){
                if (angular.isUndefined(el.cityFiasUUID)){
                    return;
                };
//console.log(el);                
                var isMarkerDelete = true;
                objectsArray.some(function(elem){
                    if((elem.cityFiasUUID===el.cityFiasUUID)){
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
                markerArray.splice(delIndexEl, 1);
            });
        };
        return markerArray;
    };
    
    $scope.setCitiesOnMap = function(cityArray, markerArray){
//console.log(cityArray, markerArray);        
        if (!angular.isArray(cityArray)||(cityArray.length === 0)||!angular.isArray(markerArray)){
            return [];
        };
        markerArray = deleteCityMarkers(cityArray, markerArray);
       
        cityArray.forEach(function(elem){            
            if((elem.cityGeoPosX ===null)||(elem.cityGeoPosY===null)){
console.warn("Warning. City without coordinates.");                
console.warn(elem);                                
                return;
            };
            var marker = {};
            var isMarkerExists = false;
            if ((angular.isDefined(markerArray.length))&&(markerArray.length!=0)){
                markerArray.some(function(el){
                    if(angular.isUndefined(el.cityFiasUUID)){
                        return false;
                    };
                    if((elem.cityFiasUUID===el.cityFiasUUID)){
                        marker = el;                                               
                        isMarkerExists = true;
                        return true;
                    };
                });
            };                        
            marker= $scope.prepareCityMarker(elem, marker);
            marker.focus = false;
            if (isMarkerExists!==true){
                markerArray.push(marker);  
            };                   
        });
       
        var arrLength = markerArray.length;
        while (arrLength>=1){
            arrLength--;                                               
            if (angular.isUndefined(markerArray[arrLength].cityFiasUUID)){                   
                markerArray.splice(arrLength, 1);
            };
        }; 

        return markerArray;
    };
    
    
// *************************** prepare objects markers *************************
    //**********************************************************************
    
    
     //create message for object marker
    var createMessageForObjectMarker = function(currentObject){
        var markerMessage = "<div id='"+currentObject.contObject.id+"' class=''>";
        markerMessage += "<label>"+currentObject.contObject.fullName+"</label><br>";
        markerMessage+="<hr class='nmc-hr-in-modal'>";
        markerMessage+="Потребление в период с "+$scope.mapSettings.dateFrom+" по "+$scope.mapSettings.dateTo+"<br>";
        markerMessage+="<div class='row nmc-hide' ng-class=\"{'nmc-hide':!markerSettings.cmpFlag,'nmc-show':markerSettings.cmpFlag}\">";
        markerMessage+="    <div class='col-md-9'>";
        markerMessage+="сравнить с аналогичным периодом";
        markerMessage+="        <select ng-model='daysAgo' ng-init='daysAgo=\"30\";changeCmpPeriod(markerSettings, daysAgo, cmpPeriod)' ng-change='changeCmpPeriod(markerSettings, daysAgo, cmpPeriod)'>";
        markerMessage+="            <option value='30'>месяц назад</option>";
        markerMessage+="            <option value='92'>квартал назад</option>";
        markerMessage+="            <option value='182'>пол года назад</option>";
        markerMessage+="            <option value='365'>год назад</option>";
        markerMessage+="        </select>";
        markerMessage+="        <div>";
        markerMessage+="            с {{cmpPeriod.dateFrom}} по {{cmpPeriod.dateTo}}";
        markerMessage+="        </div>";
        markerMessage+="    </div>";
        markerMessage+="    <div class='col-md-2'>";
        markerMessage+="        <button title='Сравнить' ng-diabled='markerSettings.runFlag' class='btn btn-xs btn-primary' ng-click='getObjectsConsumingForObject(curObject,cmpPeriod)'>Сравнить</button>";
        markerMessage+="    </div>";
        markerMessage+="</div>";
        markerMessage+="<div class='text-center'>";
        markerMessage+="    <i class='btn btn-xs glyphicon glyphicon-chevron-down' ng-class=\"{'glyphicon-chevron-down':!markerSettings.cmpFlag,'glyphicon-chevron-up':markerSettings.cmpFlag}\" ng-click='compareWith()'></i>";
        markerMessage+="</div>";
        markerMessage+="<hr class='nmc-hr-in-modal'>";
        markerMessage+="    <div class='container-fluid'>";
        markerMessage+="        <div class='row'>";
                //left column
        markerMessage+="            <div class='col-md-4 noPadding'>";
        
        currentObject.serviceTypeARTs.forEach(function(servType){
//            if(!angular.isNumber(servType.absConsValue) || !angular.isNumber(servType.tempValue)){
//                return;
//            };
            var measureUnit = "";
            switch (servType.contServiceType){
                case "hw": 
                    markerMessage+="    <label>Горячая вода:</label><br>";
                    measureUnit="м<sup>3</sup>";
                    break;
                case "heat": 
                    markerMessage+="    <label>Отопление:</label><br>";
                    measureUnit="ГКал";
                    break;
                default: markerMessage +=""+servType.contServiceType+"";
                    break;
            };
        
            markerMessage+="        <div class='paddingLeft5'>Всего: "+(angular.isNumber(servType.absConsValue)?servType.absConsValue.toFixed(2):"0")+" "+measureUnit+"<br>";
            if (currentObject.contObject.heatArea>0){
                markerMessage+="        Удельное: "+(servType.absConsValue/currentObject.contObject.heatArea).toFixed(2)+" "+measureUnit+"<br>";
            };
            markerMessage+="        t : "+(angular.isNumber(servType.tempValue)?servType.tempValue.toFixed(2):"")+" &deg;C";
            markerMessage+="        </div>";                   
        });
        
        markerMessage+="    </div>";
            // center column
        markerMessage+="            <div class='col-md-4 noPadding' ng-class=\"{'nmc-hide':!markerSettings.cmpFlag,'nmc-show':markerSettings.cmpFlag}\">";
                    
        markerMessage+="    <div ng-repeat='diff in diffObjectData'>";
        markerMessage+="<br>";
        markerMessage+="       <div> <span ng-if='diff.absConsValue>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diff.absConsValuePerCent<100),'nmc-alert':(diff.absConsValuePerCent>=100)}\"></i></span><span ng-if='diff.absConsValue<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal':(diff.absConsValuePerCent<100),'nmc-alert':(diff.absConsValuePerCent>=100)}\"></i></span><span ng-if='diff.absConsValuePerCent!=null'>{{diff.absConsValuePerCent}}% {{diff.absConsValue}} <span ng-switch on='diff.measureUnit'><span ng-switch-when='V_M3'>м <sup>3</sup></span> <span ng-switch-when='V_GCAL'>ГКал</span></span></span></div>";
        markerMessage+="       <div> <span ng-if='diff.absConsValueAverage>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diff.absConsValueAveragePerCent<100),'nmc-alert':(diff.absConsValueAveragePerCent>=100)}\"></i></span><span ng-if='diff.absConsValueAverage<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal':(diff.absConsValueAveragePerCent<100),'nmc-alert':(diff.absConsValueAveragePerCent>=100)}\"></i></span><span ng-if='diff.absConsValueAveragePerCent!=null'>{{diff.absConsValueAveragePerCent}}% {{diff.absConsValueAverage}} <span ng-switch on='diff.measureUnit'><span ng-switch-when='V_M3'>м <sup>3</sup></span> <span ng-switch-when='V_GCAL'>ГКал</span></span></span></div>";
        markerMessage+="       <div> <span ng-if='diff.tempValue>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diff.tempValuePerCent<100),'nmc-alert':(diff.tempValuePerCent>=100)}\"></i></span><span ng-if='diff.tempValue<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal':(diff.tempValuePerCent<100),'nmc-alert':(diff.tempValuePerCent>=100)}\"></i></span><span ng-if='diff.tempValuePerCent!=null'>{{diff.tempValuePerCent}}% {{diff.tempValue}} </span></div>";
        markerMessage+="<br>";
        markerMessage+="    </div>";
        markerMessage+="    </div>";
            //right column
        markerMessage+="            <div class='col-md-4 noPadding' ng-class=\"{'nmc-hide':!markerSettings.cmpFlag,'nmc-show':markerSettings.cmpFlag}\">";
        markerMessage+="{{newServiceData}}";
        markerMessage+="<div ng-repeat='newServiceData in newObjectData.serviceTypeARTs'>";
        
        markerMessage+="<div ng-switch on='newServiceData.contServiceType'>";
        markerMessage+="    <div ng-switch-when='hw'>";
        markerMessage+="        <label>Горячая вода:</label><br>";
        markerMessage+="     </div>";
        markerMessage+="    <div ng-switch-when='heat'>";
        markerMessage+="        <label>Отопление:</label><br>";
        markerMessage+="    </div>";
        markerMessage+="    <div class='paddingLeft5'>Всего: {{newServiceData.absConsValue || 0}} <span ng-switch-when='hw'>м<sup>3</sup></span><span ng-switch-when='heat'>ГКал</span><br>";
        if (currentObject.contObject.heatArea>0){
            markerMessage+="        Удельное: {{(newServiceData.absConsValue/curObject.contObject.heatArea).toFixed(2) || 0}} <span ng-switch-when='hw'>м<sup>3</sup></span><span ng-switch-when='heat'>ГКал</span>/м<sup>2</sup><br>";
        };
        markerMessage+="        t : {{newServiceData.tempValue}} &deg;C";
        markerMessage+="    </div>";
        markerMessage+="</div>";
        markerMessage+="</div>";
        markerMessage+="    </div>";
        
        markerMessage+="        </div>";
        markerMessage+="    </div>";
        markerMessage+="</div>";  
        return markerMessage;
    };
    
    
    //create object marker
    $scope.prepareObjectMarker = function(object, marker){                  
        if ((object.contObject.contObjectGeo.geoPosX === null) || (object.contObject.contObjectGeo.geoPosY===null)){
            return;
        }; 
        
        var markerMessage = createMessageForObjectMarker(object);
        
        marker.focus= false;
        marker.lng=object.contObject.contObjectGeo.geoPosX;
            marker.lat= object.contObject.contObjectGeo.geoPosY;
            marker.getMessageScope=function () {
                angular.extend($scope, {
                    markerSettings:{
                        cmpFlag: false,
                        dateFrom: $scope.mapSettings.dateFrom,
                        dateTo: $scope.mapSettings.dateTo
                    }
                });
                angular.extend($scope,{curObject:object});
                angular.extend($scope,{changeCmpPeriod: changeCmpPeriod});
                angular.extend($scope,{compareWith: compareWith});
                angular.extend($scope, {viewObjectsOnMap: viewObjectsOnMap});
                angular.extend($scope, {getObjectsConsumingForObject: getObjectsConsumingForObject});
                return $scope; };
            marker.message = ""+markerMessage+"";
            marker.compileMessage = true;
            marker.icon =  monitorMarker; //set current marker
        
        marker.contObjectId = object.contObject.id;
                
        return marker;
    };
    
    
    var deleteObjectMarkers = function(objectsArray, markerArray){
console.log(markerArray.length);        
        var deletedMarkers = [];
        if ((angular.isDefined(markerArray.length))&&(markerArray.length!=0)){
            markerArray.forEach(function(el, index){
                if (angular.isUndefined(el.contObjectId)){
                    return;
                };
//console.log(el);                
                var isMarkerDelete = true;
                objectsArray.some(function(elem){
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
                markerArray.splice(delIndexEl, 1);
            });
        };
console.log(deletedMarkers);         
console.log(markerArray.length);         
        return markerArray;
    };
    
    $scope.setObjectsOnMap = function(objectArray, markerArray){
console.log(objectArray, markerArray.length);        
        if (!angular.isArray(objectArray)||(objectArray.length === 0)||!angular.isArray(markerArray)){
            return [];
        };
        markerArray = deleteObjectMarkers(objectArray, markerArray);
console.log(markerArray.length);        
                //check objects
//console.log(">>> prepare markers <<<");
//console.log($scope.mapSettings.dateFrom);        
        objectArray.forEach(function(elem){
console.log(elem);            
            if((elem.contObject.contObjectGeo.geoPosX ===null)||(elem.contObject.contObjectGeo.geoPosY===null)){
console.warn("Warning. Object without coordinates.");                
console.warn(elem);                                
                return;
            };
            var marker = {};
            var isMarkerExists = false;
            if ((angular.isDefined(markerArray.length))&&(markerArray.length!=0)){
                markerArray.some(function(el){
                    if(angular.isUndefined(el.contObjectId)){
                        return false;
                    };
                    if((elem.contObject.id===el.contObjectId)){
                        marker = el;                         
//console.log('isMarkerExists');                        
                        isMarkerExists = true;
                        return true;
                    };
                });
            };            
//console.log(marker == markerArray[0]) ;             
            marker= $scope.prepareObjectMarker(elem, marker);
            marker.focus = false;
//            marker.visible = false;
//            marker
console.log(marker);    
//console.log(markerArray);
          
            if (isMarkerExists!==true){
                markerArray.push(marker);  
            };      
//console.log(marker == markerArray[0]) ;             
        });
console.log(markerArray.length);        
        var arrLength = markerArray.length;
        while (arrLength>=1){
            arrLength--;                                               
            if (angular.isUndefined(markerArray[arrLength].contObjectId)){                   
                markerArray.splice(arrLength, 1);
            };
        }; 
//console.log(markerArray);
        return markerArray;
    };
    
    if ($scope.mapCenter.zoom > $scope.mapSettings.zoomBound){
//        $scope.setObjectsOnMap($scope.objectsOfCities, markers);
        markersForObjects = $scope.setObjectsOnMap($scope.objectsOfCities, markersForObjects);
        $scope.markersOnMap = [];
        angular.extend($scope, {markersOnMap: markersForObjects});
    }else{
//        $scope.setCitiesOnMap($scope.cities, markers);
        markersForCities = $scope.setCitiesOnMap($scope.cities, markersForCities);
        $scope.markersOnMap = [];
        angular.extend($scope, {markersOnMap: markersForCities});
    };
//    markersOnMap = markers;
//    angular.extend($scope, {markersOnMap});
    
    $scope.$watch("mapCenter.zoom", function(newZoom, oldZoom){
//console.log($scope.objectsOfCities);
//console.log($scope.cities);
        if (newZoom>$scope.mapSettings.zoomBound){
            if (oldZoom<=$scope.mapSettings.zoomBound)
            {                  
                if ($scope.viewCurrentCityObjectsFlag === true){
                    $scope.viewCurrentCityObjectsFlag = false;
                    return;
                };                
//                markers = $scope.setObjectsOnMap($scope.objectsOfCities, markers);   
                markersForObjects = $scope.setObjectsOnMap($scope.objectsOfCities, markersForObjects);
                $scope.markersOnMap = [];
                angular.extend($scope, {markersOnMap: markersForObjects});
            };           
        };
        if (newZoom<=$scope.mapSettings.zoomBound){
            if (oldZoom>$scope.mapSettings.zoomBound){
//                markers = $scope.setCitiesOnMap($scope.cities, markers);
                markersForCities = $scope.setCitiesOnMap($scope.cities, markersForCities);
                $scope.markersOnMap = [];
                angular.extend($scope, {markersOnMap: markersForCities});
            };    
        };
        
//        angular.extend($scope, {markersOnMap: markers});
    }, false);
    
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
    
    
            //object map date settings
    $scope.objectDates = {
        startDate :  moment().subtract(30, 'days').startOf('day'),
        endDate :  moment().endOf('day')
    };
//console.log($scope.monitorDates);    
    $scope.queryDateOptsObjMapRu = {
        locale : {
            applyClass : 'btn-green',
            applyLabel : "Применить",
            fromLabel : "с",
            toLabel : "по",
            cancelLabel : 'Отмена',
            customRangeLabel : 'Период',
            daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
            firstDay : 1,
            monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентабрь',
                    'Октябрь', 'Ноябрь', 'Декабрь' ]
        },
        ranges : {
            'Текущий день' : [ moment().startOf('day'),
                    moment().endOf('day') ],
            'Посл 7 дней' : [
                    moment().subtract(6, 'days').startOf('day'),
                    moment().endOf('day') ],
            'Посл 30 дней' : [
                    moment().subtract(29, 'days').startOf('day'),
                    moment().endOf('day') ]
        },
        startDate : moment().subtract(30, 'days').startOf('day'),
        endDate : moment().endOf('day'),
//        startDate : moment().subtract(6, 'days').startOf('day'),
//        endDate : moment().endOf('day'),
        maxDate: moment().endOf('day'),

        format : 'DD.MM.YYYY'
        ,separator: " по "
    };

    $scope.$watch('objectDates', function (newDates, oldDates) {                    
console.log('Watch objectDates');    
        if (angular.isUndefined(oldDates)){
            return;
        };
        if(oldDates === newDates){
            return;
        };
        $scope.mapSettings.dateFrom = moment(newDates.startDate).format('YYYY-MM-DD');
        $scope.mapSettings.dateTo = moment(newDates.endDate).format('YYYY-MM-DD');
        var citySet = {};
        citySet.dateFrom = $scope.mapSettings.dateFrom;
        citySet.dateTo = $scope.mapSettings.dateTo; 
        refreshCitiesData();
    }, false);
   
    //reset extended objects, when open popup
    $scope.$on('leafletDirectiveMarker.click', function(e, args){
        $scope.diffObjectData =[];
        $scope.newObjectData =[];
        $scope.newCityData = {};
        $scope.diffCityData = {};
        $scope.markerSettings = {};
        //set the width of the popup window
        var popup = document.getElementsByClassName("leaflet-popup-content-wrapper");
        popup[0].className = "leaflet-popup-content-wrapper nmc-leaflet-popup-content-wrapper-compare";
    });
    //
    
});