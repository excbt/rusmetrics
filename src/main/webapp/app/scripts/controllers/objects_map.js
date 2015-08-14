angular.module('portalNMC')
.controller('ObjectsMapCtrl', function($rootScope, $scope, $cookies, objectSvc){
    $scope.mapSettings = {};
    $scope.mapSettings.zoomBound = 9; //zoom>zoomBound - view objects on map; zoom<zoomBound - view cities on map
    $scope.mapSettings.dateFrom = moment().subtract(30, 'days').startOf('day').format('YYYY-MM-DD');
    $scope.mapSettings.dateTo = moment().endOf('day').format('YYYY-MM-DD');
    $scope.mapSettings.loadingFlag = objectSvc.getLoadingStatus();
    
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
    //get map settings from user context
    if (angular.isDefined($cookies.objectMapZoom)){
        mapCenter.zoom = Number($cookies.objectMapZoom);
    };
    if (angular.isDefined($cookies.objectMapLat)){
        mapCenter.lat = Number($cookies.objectMapLat);
    };
    if (angular.isDefined($cookies.objectMapLng)){
        mapCenter.lng = Number($cookies.objectMapLng);
    };
    
    
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
                            tmpDiff.heatArea = city.servicesSummary.heatArea;
                            if ((city.servicesSummary.hw!=="Нет данных")&&($scope.newCityData.servicesSummary.hw!=="Нет данных")){
                                tmpDiff.hw = ((city.servicesSummary.hw-$scope.newCityData.servicesSummary.hw)).toFixed(2);
                                tmpDiff.hwPerCent = ((city.servicesSummary.hw-$scope.newCityData.servicesSummary.hw)*100/$scope.newCityData.servicesSummary.hw).toFixed(2);
                                tmpDiff.hwMeasureUnit = 'м3';
                                tmpDiff.hwAveragePerCent = ((tmpDiff.heatArea>0))?((city.servicesSummary.hwAverage-$scope.newCityData.servicesSummary.hwAverage)*100/$scope.newCityData.servicesSummary.hwAverage).toFixed(2):null;
                                tmpDiff.hwAverage = ((city.servicesSummary.hwAverage-$scope.newCityData.servicesSummary.hwAverage)).toFixed(2);
                            };
                            if ((city.servicesSummary.heat!=="Нет данных")&&($scope.newCityData.servicesSummary.heat!=="Нет данных")){
                                tmpDiff.heat = ((city.servicesSummary.heat-$scope.newCityData.servicesSummary.heat)).toFixed(2);
                                tmpDiff.heatPerCent = ((city.servicesSummary.heat-$scope.newCityData.servicesSummary.heat)*100/$scope.newCityData.servicesSummary.heat).toFixed(2);
                                tmpDiff.heatMeasureUnit = 'м3';
                                tmpDiff.heatAveragePerCent = ((tmpDiff.heatArea>0))?((city.servicesSummary.heatAverage-$scope.newCityData.servicesSummary.heatAverage)*100/$scope.newCityData.servicesSummary.heatAverage).toFixed(2):null;
                                tmpDiff.heatAverage = ((city.servicesSummary.heatAverage-$scope.newCityData.servicesSummary.heatAverage)).toFixed(2);
                            };
                            
console.log((tmpDiff.heatAveragePerCent));                              
console.log((tmpDiff.heatAveragePerCent!==Infinity));                            
console.log(tmpDiff);            
                            $scope.diffCityData = tmpDiff;
                            $scope.diffData = tmpDiff;
                            $scope.newData = angular.copy($scope.newCityData.servicesSummary);
                            $scope.markerSettings.isDiffDataFlag = true;
                            $scope.comparingFlag = false;
                        },
                       function(err){
                            console.log(err);
                            $scope.comparingFlag = false;
                        }
        );
    };
    
    var refreshCitiesData = function(){
               
        var citiesPromise = objectSvc.getCitiesConsumingData($scope.mapSettings);
        citiesPromise.then(function(response){
                            $scope.cities = response.data;
                            $scope.objectsOfCities = getObjectsDataForCities($scope.cities, $scope.objectsOfCities);     
                            if ($scope.mapCenter.zoom > $scope.mapSettings.zoomBound){
                                markersForObjects = $scope.setObjectsOnMap($scope.objectsOfCities, markersForObjects);
                                $scope.markersOnMap = [];
                                angular.extend($scope, {markersOnMap: markersForObjects});
                            }else{
                                markersForCities = $scope.setCitiesOnMap($scope.cities, markersForCities);
                                $scope.markersOnMap = [];
                                angular.extend($scope, {markersOnMap: markersForCities});
                            };
                            $scope.mapSettings.loadingFlag = false;
                        },
                       function(err){
                            console.log(err);
                            $scope.mapSettings.loadingFlag = false;
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
//console.log("viewObjectsOnMap."); 
        //clear popup
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
        markersForObjects = new Array();
        $scope.setObjectsOnMap(tmpObjects, markersForObjects); 
        $scope.markersOnMap = markersForObjects;
//console.log(curCity);        
console.log(markersForObjects);        
//        angular.extend($scope, {markersOnMap: markersForObjects});
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
        $scope.markerSettings.cmpDataFlag = true;
        $scope.markerSettings.runFlag = true;
        var tmpSettings = angular.copy(settings);
//        tmpSettings.objectCompareFlag == true;
//        refreshCitiesDataWithParams(tmpCities, tmpObjects, tmpSettings);
        objectSvc.getObjectConsumingData(tmpSettings,object.contObject.id)
            .then(function(response){
                $scope.newObjectData = response.data;
//console.log($scope.newObjectData);            
                for (var i=0;i<$scope.newObjectData.serviceTypeARTs.length;i++){
                    var curData = $scope.newObjectData.serviceTypeARTs[i];
//console.log(curData);                        
                    curData.absConsValue = (curData.absConsValue===null)?null:Number(curData.absConsValue.toFixed(2));
                    curData.tempValue = (curData.tempValue===null)?null:Number(curData.tempValue.toFixed(2));
                    
//console.log(curData);                    
                };
//console.log(object);            
                var tmpDiff = {};
                $scope.newData = prepareObjectData(response.data);
                var curData = prepareObjectData(object);
//console.log(curData);
//console.log($scope.newData);            
            
//                if (!angular.isNumber(servType.absConsValue)||(angular.isUndefined(servType.absConsValue))||(servType.absConsValue===null)){
//                    tmpDiff.hw = "Нет данных";
//                    tmpDiff.hwMeasureUnit ="";
//                    tmpDiff.hwAverage = "Нет данных";
//                    tmpDiff.hwAverageMeasureUnit ="";
                if ((curData.hw!=="Нет данных")&&($scope.newData.hw!=="Нет данных")){
                    tmpDiff.hw = (curData.hw-$scope.newData.hw).toFixed(2);
                    tmpDiff.hwPerCent = ((curData.hw-$scope.newData.hw)*100/$scope.newData.hw).toFixed(2);
                    tmpDiff.hwMeasureUnit = " м3";//servType.measureUnit;//"м<sup>3</sup>";
                    if (object.contObject.heatArea>0){
                        tmpDiff.hwAverage = ((curData.hwAverage-$scope.newData.hwAverage)).toFixed(2);
                        tmpDiff.hwAveragePerCent = ((curData.hwAverage-$scope.newData.hwAverage)*100/($scope.newData.hwAverage)).toFixed(2);
                        tmpDiff.hwAverageMeasureUnit = "m3/m2";//"м<sup>3</sup>/м<sup>2</sup>";
                    }else{
                        tmpDiff.hwAverage = "Не задана площадь";
                        tmpDiff.hwAverageMeasureUnit="";
                    };
                };
//                if (!angular.isNumber(servType.absConsValue)||(angular.isUndefined(servType.absConsValue))||(servType.absConsValue===null)){
//                    tmpDiff.heat = "Нет данных";
//                    tmpDiff.heatMeasureUnit ="";
//                    tmpDiff.heatAverage = "Нет данных";
//                    tmpDiff.heatAverageMeasureUnit ="";
                if ((curData.heat!=="Нет данных")&&($scope.newData.heat!=="Нет данных")){
                    tmpDiff.heat = (curData.heat-$scope.newData.heat).toFixed(2);
                    tmpDiff.heatPerCent = ((curData.heat-$scope.newData.heat)*100/$scope.newData.heat).toFixed(2);
                    tmpDiff.heatMeasureUnit = " ГКал";//servType.measureUnit;//"м<sup>3</sup>";
                    if (object.contObject.heatArea>0){
                        tmpDiff.heatAverage = ((curData.heatAverage-$scope.newData.heatAverage)).toFixed(2);
                        tmpDiff.heatAveragePerCent = ((curData.heatAverage-$scope.newData.heatAverage)*100/($scope.newData.heatAverage)).toFixed(2);
                        tmpDiff.heatAverageMeasureUnit = "ГКал/m2";//"м<sup>3</sup>/м<sup>2</sup>";
                    }else{
                        tmpDiff.heatAverage = "Не задана площадь";
                        tmpDiff.heatAverageMeasureUnit="";
                    };
                };

            
     ///// to deleeteeee
                var tmpDiffArray = [];
                
                object.serviceTypeARTs.forEach(function(serv){
                    for (var i=0; i<$scope.newObjectData.serviceTypeARTs.length;i++){
                        var curData = $scope.newObjectData.serviceTypeARTs[i];
                        if (serv.contServiceType === curData.contServiceType){
console.log(curData);                            
console.log(serv);          
                        
//                            tmpDiff[contServiceType] = {};
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
                $scope.diffData = tmpDiff;
                $scope.comparingFlag = false;
console.log($scope.diffData);            
            });
        
//        var 
    };
    
    var getObjectsConsumingForCity = function(city, settings){
        $scope.markerSettings.cmpDataFlag=true;
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
//serch indicators <0                
//if(sys.absConsValue<0){                
//    console.log(obj);      
//};
                switch (sys.contServiceType){
                    case "heat":heatSum+=sys.absConsValue; break;
                    case "hw" : hwSum+=sys.absConsValue; break;
                };
            });
        });
        var servicesSummary = {};
        servicesSummary.hw = "Нет данных";
        servicesSummary.hwMeasureUnit = '';
        servicesSummary.heat = "Нет данных";
        servicesSummary.heatMeasureUnit = '';
        servicesSummary.heatArea = null;
        servicesSummary.heatAreaMeasureUnit = '';
        servicesSummary.heatAverage = "Не задана площадь";
        servicesSummary.heatAverageMeasureUnit = '';
        servicesSummary.hwAverage = "Не задана площадь";
        servicesSummary.hwAverageMeasureUnit = '';
        
        servicesSummary.hw = hwSum.toFixed(2);
        servicesSummary.hwMeasureUnit = 'м3';
        servicesSummary.heat = heatSum.toFixed(2);
        servicesSummary.heatMeasureUnit = 'ГКал';
        servicesSummary.heatArea = areaSum.toFixed(2);
        servicesSummary.heatAreaMeasureUnit = 'м2';
        if (areaSum != 0){
            servicesSummary.heatAverage = (heatSum/areaSum).toFixed(2);
            servicesSummary.heatAverageMeasureUnit = 'ГКал/м2';
            servicesSummary.hwAverage = (hwSum/areaSum).toFixed(2);
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
        markerMessage+="    <div class='col-md-8'>";
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
        markerMessage+="    <div class='col-md-3'>";
        markerMessage+="        <button title='Сравнить' ng-disabled='markerSettings.runFlag' class='btn btn-xs btn-primary' ng-click='comparingFlag=true;getObjectsConsumingForCity(curCity, cmpPeriod)'>Сравнить</button><span ng-show=\"comparingFlag\" class=\"nmc-loading marginLeft5\">";
        markerMessage+="<i class=\"fa fa-spinner fa-spin\"></i> </span>";
        markerMessage+="    </div>";
        markerMessage+="</div>";
        markerMessage+="<div class='text-center'>";
        markerMessage+="    <i class='btn btn-xs glyphicon glyphicon-chevron-down' ng-class=\"{'glyphicon-chevron-down':!markerSettings.cmpFlag,'glyphicon-chevron-up':markerSettings.cmpFlag}\" ng-click='compareWith()'></i>";
        markerMessage+="</div>";
        markerMessage+="<hr class='nmc-hr-in-modal'>";
        
        markerMessage+="<table class='table table-striped table-condensed nmc-table-popup'>";
        markerMessage+="    <tr>";
        markerMessage+="        <td class='col-md-4'>";
        markerMessage+="            <label>Горячая вода:</label>";
        markerMessage+="        </td>";
        markerMessage+="        <td class='col-md-4'>";
        markerMessage+="        </td>";
        markerMessage+="        <td class='col-md-4'>";
        markerMessage+="            <span ng-if='markerSettings.cmpDataFlag'><label>Горячая вода:</label></span>";
        markerMessage+="        </td>";
        markerMessage+="    </tr>";
        markerMessage+="    <tr >";
        markerMessage+="        <td>";          
        markerMessage+="            <span class='marginLeft15'>Всего:</span>";
        markerMessage+="            "+city.servicesSummary.hw+" "+city.servicesSummary.hwMeasureUnit;
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="            <span ng-if='(diffData.hwPerCent!=null)&&(diffData.hwPerCent!=\"NaN\")&&(diffData.hwPerCent!=\"Infinity\")&&(diffData.hw>0)'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffData.hwPerCent<100),'nmc-alert':(diffData.hwPerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.hwPerCent!=null)&&(diffData.hwPerCent!=\"NaN\")&&(diffData.hwPerCent!=\"Infinity\")&&(diffData.hw<0)'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diffData.hwPerCent<100),'nmc-alert':(diffData.hwPerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.hwPerCent!=null)&&(diffData.hwPerCent!=\"NaN\")&&(diffData.hwPerCent!=\"Infinity\")'>{{diffData.hwPerCent}}% {{diffData.hw}} м<sup>3</sup></span>";
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="            <span ng-if='markerSettings.cmpDataFlag'><span class='marginLeft15'>Всего:</span>";
        markerMessage+="            {{newData.hw}} <span ng-if='(newData.hw!=null)&&(newData.hw!=\"NaN\")&&(newData.hw!=\"Infinity\")&&(newData.hw!=\"Нет данных\")&&(newData.hw!=\"Не задана площадь\")'>м<sup>3</sup></span></span>";
        markerMessage+="        </td>";
        markerMessage+="    </tr>";
        markerMessage+="    <tr >";
        markerMessage+="        <td>";
        markerMessage+="            <span class='marginLeft15'>Удельное:</span>";
        markerMessage+="            "+city.servicesSummary.hwAverage+" "+city.servicesSummary.hwAverageMeasureUnit;
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="            <span ng-if='(diffData.hwAveragePerCent!=null)&&(diffData.hwAveragePerCent!=\"NaN\")&&(diffData.hwAveragePerCent!=\"Infinity\")&&(diffData.hwAverage>0)'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffData.hwAveragePerCent<100),'nmc-alert':(diffData.hwAveragePerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.hwAveragePerCent!=null)&&(diffData.hwAveragePerCent!=\"NaN\")&&(diffData.hwAveragePerCent!=\"Infinity\")&&(diffData.hwAverage<0)'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diffData.hwAveragePerCent<100),'nmc-alert':(diffData.hwAveragePerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.hwAveragePerCent!=null)&&(diffData.hwAveragePerCent!=\"NaN\")&&(diffData.hwAveragePerCent!=\"Infinity\")'>{{diffData.hwAveragePerCent}}% {{diffData.hwAverage}} м<sup>3</sup>/м<sup>2</sup></span>";
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="           <span ng-if='markerSettings.cmpDataFlag'> <span class='marginLeft15'>Удельное:</span>";
        markerMessage+="            {{newData.hwAverage}} <span ng-if='(newData.hwAverage!=null)&&(newData.hwAverage!=\"NaN\")&&(newData.hwAverage!=\"Infinity\")&&(newData.hwAverage!=\"Нет данных\")&&(newData.hwAverage!=\"Не задана площадь\")'>м<sup>3</sup>/м<sup>2</sup></span></span>";
        markerMessage+="        </td>";
        markerMessage+="    </tr>";
        markerMessage+="    <tr >";
        markerMessage+="        <td>";
        markerMessage+="            <label >Отопление:</label>";
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="            <span ng-if='markerSettings.cmpDataFlag'><label>Отопление:</label></span>";
        markerMessage+="        </td>";
        markerMessage+="    </tr>";
        markerMessage+="    <tr >";
        markerMessage+="        <td>";
        markerMessage+="            <span class='marginLeft15'>Всего:</span>";
        markerMessage+="            "+city.servicesSummary.heat+" "+city.servicesSummary.heatMeasureUnit;
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="            <span ng-if='(diffData.heatPerCent!=null)&&(diffData.heatPerCent!=\"NaN\")&&(diffData.heatPerCent!=\"Infinity\")&&(diffData.heat>0)'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffData.heatPerCent<100),'nmc-alert':(diffData.heatPerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.heatPerCent!=null)&&(diffData.heatPerCent!=\"NaN\")&&(diffData.heatPerCent!=\"Infinity\")&&(diffData.heat<0)'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diffData.heatPerCent<100),'nmc-alert':(diffData.heatPerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.heatPerCent!=null)&&(diffData.heatPerCent!=\"NaN\")&&(diffData.heatPerCent!==\"Infinity\")'>{{diffData.heatPerCent}}% {{diffData.heat}} ГКал</span>";
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="           <span ng-if='markerSettings.cmpDataFlag'> <span class='marginLeft15'>Всего:</span>";
        markerMessage+="            {{newData.heat}} {{newData.heatMeasureUnit}}</span>";
        markerMessage+="        </td>";
        markerMessage+="    </tr>";
        markerMessage+="    <tr >";
        markerMessage+="        <td>";
        markerMessage+="            <span class='marginLeft15'>Удельное:</span>";
        markerMessage+="            "+city.servicesSummary.heatAverage+" "+city.servicesSummary.heatAverageMeasureUnit;
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="            <span ng-if='(diffData.heatAveragePerCent!=null)&&(diffData.heatAveragePerCent!=\"NaN\")&&(diffData.heatAveragePerCent!=\"Infinity\")&&(diffData.heatAverage>0)'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffData.heatAveragePerCent<100),'nmc-alert':(diffData.heatAveragePerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.heatAveragePerCent!=null)&&(diffData.heatAveragePerCent!=\"NaN\")&&(diffData.heatAveragePerCent!=\"Infinity\")&&(diffData.heatAverage<0)'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diffData.heatAveragePerCent<100),'nmc-alert':(diffData.heatAveragePerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.heatAveragePerCent!=null)&&(diffData.heatAveragePerCent!=\"NaN\")&&(diffData.heatAveragePerCent!==\"Infinity\")'>{{diffData.heatAveragePerCent}}% {{diffData.heatAverage}} ГКал/м<sup>2</sup></span>";
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="            <span ng-if='markerSettings.cmpDataFlag'><span class='marginLeft15'>Удельное:</span>";
        markerMessage+="            {{newData.heatAverage}} <span ng-if='(newData.heatAverage!=null)&&(newData.heatAverage!=\"NaN\")&&(newData.heatAverage!=\"Infinity\")&&(newData.heatAverage!=\"Нет данных\")&&(newData.heatAverage!=\"Не задана площадь\")'>ГКал/м<sup>2</sup></span></span>";
        markerMessage+="        </td>";
        markerMessage+="    </tr>";
        markerMessage+="</table>";
        
        
//        markerMessage+="    <div class='container-fluid'>";
//        markerMessage+="        <div class='row'>";
                //left column
//        markerMessage+="            <div class='col-md-4 noPadding'>";
//        markerMessage+="    <label>Горячая вода:</label><br>";
//        markerMessage+="        <div class='paddingLeft5'>Всего: "+city.servicesSummary.hw.toFixed(2)+" "+city.servicesSummary.hwMeasureUnit+"<br>";
//        if (city.servicesSummary.heatArea>0){
//            markerMessage+="        Удельное: "+city.servicesSummary.hwAverage.toFixed(2)+" "+city.servicesSummary.hwAverageMeasureUnit+"<br>";
//        };
//        markerMessage+="        </div>";
//        markerMessage+="    <label>Отопление:</label><br>";
//        markerMessage+="        <div class='paddingLeft5'>Всего: "+city.servicesSummary.heat.toFixed(2)+" "+city.servicesSummary.heatMeasureUnit+"<br>";
//        if (city.servicesSummary.heatArea>0){
//            markerMessage+="        Удельное: "+city.servicesSummary.heatAverage.toFixed(2)+" "+city.servicesSummary.heatAverageMeasureUnit+"<br>";
//        };
//        markerMessage+="        </div>";
//
//        markerMessage+="    </div>";
            // center column
//        markerMessage+="            <div class='col-md-4 noPadding' ng-class=\"{'nmc-hide':!markerSettings.isDiffDataFlag,'nmc-show':markerSettings.isDiffDataFlag}\">";
//        markerMessage+="                <br>";
//        markerMessage+="                <div><span ng-if='diffCityData.hw>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffCityData.hwPerCent<100),'nmc-alert':(diffCityData.hwPerCent>=100)}\"></i></span><span ng-if='diffCityData.hw<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-alert':(diffCityData.hwPerCent>=100),'nmc-normal-alt':(diffCityData.hwPerCent<100)}\"></i></span><span ng-if='diffCityData.hwPerCent!=null'>{{diffCityData.hwPerCent}}%  {{diffCityData.hw}} м<sup>3</sup></span></div>";
//        markerMessage+="                <div><span ng-if='diffCityData.hwAverage>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-alert':(diffCityData.hwAveragePerCent>=100),'nmc-normal':(diffCityData.hwAveragePerCent<100)}\"></i></span><span ng-if='diffCityData.hwAverage<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-alert':(diffCityData.hwAveragePerCent>=100),'nmc-normal-alt':(diffCityData.hwAveragePerCent<100)}\"></i></span><span ng-if='diffCityData.hwAveragePerCent!=null'>{{diffCityData.hwAveragePerCent}}% {{diffCityData.hwAverage}}м<sup>3</sup></span></div>";
//        markerMessage+="                   <br>";
//        markerMessage+="                    <div class='marginBottom0_8em'></div>";
//        markerMessage+="                <div><span ng-if='diffCityData.heat>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-alert':(diffCityData.heatPerCent>=100),'nmc-normal':(diffCityData.heatPerCent<100)}\"></i></span><span ng-if='diffCityData.heat<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-alert':'diffCityData.heatPerCent>=100','nmc-normal-alt':(diffCityData.heatPerCent<100)}\"></i></span><span ng-if='diffCityData.heatPerCent!=null'>{{diffCityData.heatPerCent}}% {{diffCityData.heat}} ГКал</span></div>";
//        markerMessage+="                <div><span ng-if='diffCityData.heatAverage>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-alert':(diffCityData.heatAveragePerCent>=100), 'nmc-normal':(diffCityData.heatAveragePerCent<100)}\"></i></span><span ng-if='diffCityData.heatAverage<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-alert':(diffCityData.heatAveragePerCent>=100),'nmc-normal-alt':(diffCityData.heatAveragePerCent<100)}\"></i></span><span ng-if='diffCityData.heatAveragePerCent!=null'>{{diffCityData.heatAveragePerCent}}% {{diffCityData.heatAverage}}ГКал</span></div>";
//        markerMessage+="    </div>";

            //right column
//        markerMessage+="            <div class='col-md-4 noPadding' ng-class=\"{'nmc-hide':!markerSettings.isNewDataFlag,'nmc-show':markerSettings.isNewDataFlag}\">";
//        markerMessage+="    <label>Горячая вода:</label><br>";
//        markerMessage+="        <div class='paddingLeft5'>Всего: {{newCityData.servicesSummary.hw.toFixed(2)}}{{newCityData.servicesSummary.hwMeasureUnit}}<br>";
//            markerMessage+="        <span ng-if='newCityData.servicesSummary.heatArea>0'> Удельное: {{newCityData.servicesSummary.hwAverage.toFixed(2)}} {{newCityData.servicesSummary.hwAverageMeasureUnit}}</span>";
//        markerMessage+="        </div>";
//        markerMessage+="    <label>Отопление:</label><br>";
//        markerMessage+="        <div class='paddingLeft5'>Всего: {{newCityData.servicesSummary.heat.toFixed(2)}} {{newCityData.servicesSummary.heatMeasureUnit}}<br>";
//            markerMessage+="        <span ng-if='newCityData.servicesSummary.heatArea>0'> Удельное: {{newCityData.servicesSummary.heatAverage.toFixed(2)}} {{newCityData.servicesSummary.heatAverageMeasureUnit}}</span>";
//        markerMessage+="        </div>";

//        markerMessage+="    </div>";
//        
//        markerMessage+="        </div>";
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
        
        marker.focus= false;
        marker.city = city;
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
                var tmpCity = angular.copy(marker.city);
                angular.extend($scope, {curCity: tmpCity});
                angular.extend($scope,{changeCmpPeriod: changeCmpPeriod});
                angular.extend($scope,{compareWith: compareWith});
                angular.extend($scope, {viewObjectsOnMap: viewObjectsOnMap});
                angular.extend($scope, {getObjectsConsumingForCity: getObjectsConsumingForCity});
                return $scope; };
            marker.message = ""+markerMessage+"";
            marker.compileMessage = true;
            marker.icon =  angular.copy(monitorMarker); //set current marker      
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
    
    var prepareObjectData = function(currentObject){
        var curData = {};
        
        curData.hw = "Нет данных";
        curData.hwMeasureUnit ="";
        curData.heat = "Нет данных";
        curData.heatMeasureUnit ="";
        curData.hwAverage = "Нет данных";
        curData.hwAverageMeasureUnit ="";
        curData.heatAverage = "Нет данных";
        curData.heatAverageMeasureUnit ="";
        
        
        if ((angular.isUndefined(currentObject.serviceTypeARTs))||(currentObject.serviceTypeARTs===null)||(currentObject.serviceTypeARTs.length===0)){
            curData.hw = "Нет данных";
            curData.hwMeasureUnit ="";
            curData.hwTemp = "Нет данных";
            curData.heat = "Нет данных";
            curData.heatMeasureUnit ="";
            curData.heatTemp = "Нет данных";
            curData.hwAverage = "Нет данных";
            curData.hwAverageMeasureUnit ="";
            curData.heatAverage = "Нет данных";
            curData.heatAverageMeasureUnit ="";
        };
        currentObject.serviceTypeARTs.forEach(function(servType){
            switch (servType.contServiceType){
                case "hw": 
                    if (!angular.isNumber(servType.absConsValue)||(angular.isUndefined(servType.absConsValue))||(servType.absConsValue===null)){
                        curData.hw = "Нет данных";
                        curData.hwMeasureUnit ="";
                        curData.hwAverage = "Нет данных";
                        curData.hwAverageMeasureUnit ="";
                    }else{
                        curData.hw = servType.absConsValue.toFixed(2);
                        curData.hwMeasureUnit = "м<sup>3</sup>";
                        if (currentObject.contObject.heatArea>0){
                            curData.hwAverage = (servType.absConsValue/currentObject.contObject.heatArea).toFixed(2);
                            curData.hwAverageMeasureUnit = "м<sup>3</sup>/м<sup>2</sup>";
                        }else{
                            curData.hwAverage = "Не задана площадь";
                            curData.hwAverageMeasureUnit="";
                        };
                    };
                    break;
                case "heat": 
                    if (!angular.isNumber(servType.absConsValue)||(angular.isUndefined(servType.absConsValue))||(servType.absConsValue===null)){
                        curData.heat = "Нет данных";
                        curData.heatMeasureUnit ="";
                        curData.heatAverage = "Нет данных";
                        curData.heatAverageMeasureUnit ="";
                    }else{;
                        curData.heat = servType.absConsValue.toFixed(2);
                        curData.heatMeasureUnit = "ГКал";
                        if (currentObject.contObject.heatArea>0){
                            curData.heatAverage = (servType.absConsValue/currentObject.contObject.heatArea).toFixed(2);
                            curData.heatAverageMeasureUnit = "ГКал/м<sup>2</sup>";
                        }else{
                            curData.heatAverage = "Не задана площадь";
                            curData.heatAverageMeasureUnit="";
                        };
                    }
                    break;
                default: 
                    curData.wtf = servType.absConsValue;
                    curData.wtfMeasureUnit = "Wtf";
                    break;
            };
        });
        return curData;
    };
    
     //create message for object marker
    var createMessageForObjectMarker = function(currentObject){
        //prepare data
        var curData = prepareObjectData(currentObject);
        
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
        markerMessage+="        <button title='Сравнить' ng-diabled='markerSettings.runFlag' class='btn btn-xs btn-primary' ng-click='getObjectsConsumingForObject(curObject,cmpPeriod)'>Сравнить</button><span ng-show=\"comparingFlag\" class=\"nmc-loading marginLeft5\">";
        markerMessage+="<i class=\"fa fa-spinner fa-spin\"></i> </span>";
        markerMessage+="    </div>";
        markerMessage+="</div>";
        markerMessage+="<div class='text-center'>";
        markerMessage+="    <i class='btn btn-xs glyphicon glyphicon-chevron-down' ng-class=\"{'glyphicon-chevron-down':!markerSettings.cmpFlag,'glyphicon-chevron-up':markerSettings.cmpFlag}\" ng-click='compareWith()'></i>";
        markerMessage+="</div>";
        markerMessage+="<hr class='nmc-hr-in-modal'>";
//        markerMessage+="    <div class='container-fluid'>";
        markerMessage+="<table class='table table-striped table-condensed nmc-table-popup'>";
        markerMessage+="    <tr>";
        markerMessage+="        <td class='col-md-4'>";
        markerMessage+="            <label>Горячая вода:</label>";
        markerMessage+="        </td>";
        markerMessage+="        <td class='col-md-4'>";
        markerMessage+="        </td>";
        markerMessage+="        <td class='col-md-4'>";
        markerMessage+="            <span ng-if='markerSettings.cmpDataFlag'><label>Горячая вода:</label></span>";
        markerMessage+="        </td>";
        markerMessage+="    </tr>";
        markerMessage+="    <tr >";
        markerMessage+="        <td>";          
        markerMessage+="            <span class='marginLeft15'>Всего:</span>";
        markerMessage+="            "+curData.hw+" "+curData.hwMeasureUnit;
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="            <span ng-if='(diffData.hwPerCent!=null)&&(diffData.hwPerCent!=\"NaN\")&&(diffData.hwPerCent!=\"Infinity\")&&(diffData.hw>0)'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffData.hwPerCent<100),'nmc-alert':(diffData.hwPerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.hwPerCent!=null)&&(diffData.hwPerCent!=\"NaN\")&&(diffData.hwPerCent!=\"Infinity\")&&(diffData.hw<0)'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diffData.hwPerCent<100),'nmc-alert':(diffData.hwPerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.hwPerCent!=null)&&(diffData.hwPerCent!=\"NaN\")&&(diffData.hwPerCent!=\"Infinity\")'>{{diffData.hwPerCent}}% {{diffData.hw}} м<sup>3</sup></span>";
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="            <span ng-if='markerSettings.cmpDataFlag'><span class='marginLeft15'>Всего:</span></span>";
        markerMessage+="            {{newData.hw}} <span ng-if='(newData.hw!=null)&&(newData.hw!=\"NaN\")&&(newData.hw!=\"Infinity\")&&(newData.hw!=\"Нет данных\")&&(newData.hw!=\"Не задана площадь\")'>м<sup>3</sup></span>";
        markerMessage+="        </td>";
        markerMessage+="    </tr>";
        markerMessage+="    <tr >";
        markerMessage+="        <td>";
        markerMessage+="            <span class='marginLeft15'>Удельное:</span>";
        markerMessage+="            "+curData.hwAverage+" "+curData.hwAverageMeasureUnit;
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="            <span ng-if='(diffData.hwAveragePerCent!=null)&&(diffData.hwAveragePerCent!=\"NaN\")&&(diffData.hwAveragePerCent!=\"Infinity\")&&(diffData.hwAverage>0)'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffData.hwAveragePerCent<100),'nmc-alert':(diffData.hwAveragePerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.hwAveragePerCent!=null)&&(diffData.hwAveragePerCent!=\"NaN\")&&(diffData.hwAveragePerCent!=\"Infinity\")&&(diffData.hwAverage<0)'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diffData.hwAveragePerCent<100),'nmc-alert':(diffData.hwAveragePerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.hwAveragePerCent!=null)&&(diffData.hwAveragePerCent!=\"NaN\")&&(diffData.hwAveragePerCent!=\"Infinity\")'>{{diffData.hwAveragePerCent}}% {{diffData.hwAverage}} м<sup>3</sup>/м<sup>2</sup></span>";
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="            <span ng-if='markerSettings.cmpDataFlag'><span class='marginLeft15'>Удельное:</span></span>";
        markerMessage+="            {{newData.hwAverage}} <span ng-if='(newData.hwAverage!=null)&&(newData.hwAverage!=\"NaN\")&&(newData.hwAverage!=\"Infinity\")&&(newData.hwAverage!=\"Нет данных\")&&(newData.hwAverage!=\"Не задана площадь\")'>м<sup>3</sup>/м<sup>2</sup></span>";
        markerMessage+="        </td>";
        markerMessage+="    </tr>";
        markerMessage+="    <tr >";
        markerMessage+="        <td>";
        markerMessage+="            <label >Отопление:</label>";
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="           <span ng-if='markerSettings.cmpDataFlag'> <label>Отопление:</label></span>";
        markerMessage+="        </td>";
        markerMessage+="    </tr>";
        markerMessage+="    <tr >";
        markerMessage+="        <td>";
        markerMessage+="            <span class='marginLeft15'>Всего:</span>";
        markerMessage+="            "+curData.heat+" "+curData.heatMeasureUnit;
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="            <span ng-if='(diffData.heatPerCent!=null)&&(diffData.heatPerCent!=\"NaN\")&&(diffData.heatPerCent!=\"Infinity\")&&(diffData.heat>0)'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffData.heatPerCent<100),'nmc-alert':(diffData.heatPerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.heatPerCent!=null)&&(diffData.heatPerCent!=\"NaN\")&&(diffData.heatPerCent!=\"Infinity\")&&(diffData.heat<0)'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diffData.heatPerCent<100),'nmc-alert':(diffData.heatPerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.heatPerCent!=null)&&(diffData.heatAveragePerCent!=\"NaN\")&&(diffData.heatAveragePerCent!=\"Infinity\")'>{{diffData.heatPerCent}}% {{diffData.heat}} ГКал</span>";
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="           <span ng-if='markerSettings.cmpDataFlag'> <span class='marginLeft15'>Всего:</span>";
        markerMessage+="            {{newData.heat}} {{newData.heatMeasureUnit}}</span>";
        markerMessage+="        </td>";
        markerMessage+="    </tr>";
        markerMessage+="    <tr >";
        markerMessage+="        <td>";
        markerMessage+="            <span class='marginLeft15'>Удельное:</span>";
        markerMessage+="            "+curData.heatAverage+" "+curData.heatAverageMeasureUnit;
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="            <span ng-if='(diffData.heatAveragePerCent!=null)&&(diffData.heatAveragePerCent!=\"NaN\")&&(diffData.heatAveragePerCent!=\"Infinity\")&&(diffData.heatAverage>0)'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffData.heatAveragePerCent<100),'nmc-alert':(diffData.heatAveragePerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.heatAveragePerCent!=null)&&(diffData.heatAveragePerCent!=\"NaN\")&&(diffData.heatAveragePerCent!=\"Infinity\")&&(diffData.heatAverage<0)'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diffData.heatAveragePerCent<100),'nmc-alert':(diffData.heatAveragePerCent>=100)}\"></i></span>";
        markerMessage+="            <span ng-if='(diffData.heatAveragePerCent!=null)&&(diffData.heatAveragePerCent!=\"NaN\")&&(diffData.heatAveragePerCent!==\"Infinity\")'>{{diffData.heatAveragePerCent}}% {{diffData.heatAverage}} ГКал/м<sup>2</sup></span>";
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="           <span ng-if='markerSettings.cmpDataFlag'> <span class='marginLeft15'>Удельное:</span>";
        markerMessage+="            {{newData.heatAverage}} <span ng-if='(newData.heatAverage!=null)&&(newData.heatAverage!=\"NaN\")&&(newData.heatAverage!=\"Infinity\")&&(newData.heatAverage!=\"Нет данных\")&&(newData.heatAverage!=\"Не задана площадь\")'>ГКал/м<sup>2</sup></span> </span>";
        markerMessage+="        </td>";
        markerMessage+="    </tr>";
        markerMessage+="</table>";
//        markerMessage+="        <div class='row'>";
                //left column
//        markerMessage+="            <div class='col-md-4 noPadding'>";
        
//        currentObject.serviceTypeARTs.forEach(function(servType){
//            if(!angular.isNumber(servType.absConsValue) || !angular.isNumber(servType.tempValue)){
//                return;
//            };
//            var measureUnit = "";
//            markerMessage+="<div class='nmc-div-data'>";
//            switch (servType.contServiceType){
//                case "hw": 
//                    markerMessage+="    <label>Горячая вода:</label><br>";
//                    measureUnit="м<sup>3</sup>";
//                    break;
//                case "heat": 
//                    markerMessage+="    <label>Отопление:</label><br>";
//                    measureUnit="ГКал";
//                    break;
//                default: markerMessage +=""+servType.contServiceType+"";
//                    break;
//            };
//            markerMessage+="</div>";
//            markerMessage+="        <div class='paddingLeft5'>Всего: "+(angular.isNumber(servType.absConsValue)?servType.absConsValue.toFixed(2):"0")+" "+measureUnit+"<br>";
//            if (currentObject.contObject.heatArea>0){
//                markerMessage+="        Удельное: "+(servType.absConsValue/currentObject.contObject.heatArea).toFixed(2)+" "+measureUnit+"<br>";
//            };
//            markerMessage+="        t : "+(angular.isNumber(servType.tempValue)?servType.tempValue.toFixed(2):"")+" &deg;C";
//            markerMessage+="        </div>";                   
//        });
//        
//        markerMessage+="    </div>";
            // center column
//        markerMessage+="            <div class='col-md-4 noPadding' ng-class=\"{'nmc-hide':!markerSettings.cmpFlag,'nmc-show':markerSettings.cmpFlag}\">";
//                    
//        markerMessage+="    <div ng-repeat='diff in diffObjectData'>";
//        markerMessage+="<br>";
//        markerMessage+="       <div> <span ng-if='diff.absConsValue>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diff.absConsValuePerCent<100),'nmc-alert':(diff.absConsValuePerCent>=100)}\"></i></span><span ng-if='diff.absConsValue<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diff.absConsValuePerCent<100),'nmc-alert':(diff.absConsValuePerCent>=100)}\"></i></span><span ng-if='diff.absConsValuePerCent!=null'>{{diff.absConsValuePerCent}}% {{diff.absConsValue}} <span ng-switch on='diff.measureUnit'><span ng-switch-when='V_M3'>м <sup>3</sup></span> <span ng-switch-when='V_GCAL'>ГКал</span></span></span></div>";
//        markerMessage+="       <div> <span ng-if='diff.absConsValueAverage>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diff.absConsValueAveragePerCent<100),'nmc-alert':(diff.absConsValueAveragePerCent>=100)}\"></i></span><span ng-if='diff.absConsValueAverage<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diff.absConsValueAveragePerCent<100),'nmc-alert':(diff.absConsValueAveragePerCent>=100)}\"></i></span><span ng-if='diff.absConsValueAveragePerCent!=null'>{{diff.absConsValueAveragePerCent}}% {{diff.absConsValueAverage}} <span ng-switch on='diff.measureUnit'><span ng-switch-when='V_M3'>м <sup>3</sup></span> <span ng-switch-when='V_GCAL'>ГКал</span></span></span></div>";
//        markerMessage+="       <div> <span ng-if='diff.tempValue>0'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diff.tempValuePerCent<100),'nmc-alert':(diff.tempValuePerCent>=100)}\"></i></span><span ng-if='diff.tempValue<0'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diff.tempValuePerCent<100),'nmc-alert':(diff.tempValuePerCent>=100)}\"></i></span><span ng-if='diff.tempValuePerCent!=null'>{{diff.tempValuePerCent}}% {{diff.tempValue}} </span></div>";
//        markerMessage+="<br>";
//        markerMessage+="    </div>";
//        markerMessage+="    </div>";
            //right column
//        markerMessage+="            <div class='col-md-4 noPadding' ng-class=\"{'nmc-hide':!markerSettings.cmpFlag,'nmc-show':markerSettings.cmpFlag}\">";
//        markerMessage+="{{newServiceData}}";
//        markerMessage+="<div ng-repeat='newServiceData in newObjectData.serviceTypeARTs'>";
//        
//        markerMessage+="<div ng-switch on='newServiceData.contServiceType'>";
//        markerMessage+="    <div ng-switch-when='hw'>";
//        markerMessage+="        <label>Горячая вода:</label><br>";
//        markerMessage+="     </div>";
//        markerMessage+="    <div ng-switch-when='heat'>";
//        markerMessage+="        <label>Отопление:</label><br>";
//        markerMessage+="    </div>";
//        markerMessage+="    <div class='paddingLeft5'>Всего: {{newServiceData.absConsValue || 0}} <span ng-switch-when='hw'>м<sup>3</sup></span><span ng-switch-when='heat'>ГКал</span><br>";
//        if (currentObject.contObject.heatArea>0){
//            markerMessage+="        Удельное: {{(newServiceData.absConsValue/curObject.contObject.heatArea).toFixed(2) || 0}} <span ng-switch-when='hw'>м<sup>3</sup></span><span ng-switch-when='heat'>ГКал</span>/м<sup>2</sup><br>";
//        };
//        markerMessage+="        t : {{newServiceData.tempValue}} &deg;C";
//        markerMessage+="    </div>";
//        markerMessage+="</div>";
//        markerMessage+="</div>";
//        markerMessage+="    </div>";
        
//        markerMessage+="        </div>";//container-fluid
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
        marker.object = object;
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
                var obj = angular.copy(marker.object);
                angular.extend($scope,{curObject:obj});
                angular.extend($scope,{changeCmpPeriod: changeCmpPeriod});
                angular.extend($scope,{compareWith: compareWith});
                angular.extend($scope, {viewObjectsOnMap: viewObjectsOnMap});
                angular.extend($scope, {getObjectsConsumingForObject: getObjectsConsumingForObject});
                return $scope; };
            marker.message = ""+markerMessage+"";
            marker.compileMessage = true;
            marker.icon =  angular.copy(monitorMarker); //set current marker
        
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
//console.log(objectArray, markerArray.length);        
        if (!angular.isArray(objectArray)||(objectArray.length === 0)||!angular.isArray(markerArray)){
            return [];
        };
        markerArray = deleteObjectMarkers(objectArray, markerArray);
console.log(markerArray.length);        
                //check objects
//console.log(">>> prepare markers <<<");
//console.log($scope.mapSettings.dateFrom);        
        objectArray.forEach(function(elem){
//console.log(elem);            
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
//console.log(marker);    
//console.log(markerArray);
          
            if (isMarkerExists!==true){
                markerArray.push(marker);  
            };      
//console.log(marker == markerArray[0]) ;             
        });
//console.log(markerArray.length);        
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
        $scope.markersOnMap = markersForObjects;
//        angular.extend($scope, {markersOnMap: markersForObjects});
    }else{
//        $scope.setCitiesOnMap($scope.cities, markers);
        markersForCities = $scope.setCitiesOnMap($scope.cities, markersForCities);
        $scope.markersOnMap = markersForCities;
//        angular.extend($scope, {markersOnMap: markersForCities});
    };
//    markersOnMap = markers;
//    angular.extend($scope, {markersOnMap});
    
    $scope.$watch("mapCenter.zoom", function(newZoom, oldZoom){
//console.log($scope.objectsOfCities);
//console.log($scope.cities);
        $cookies.objectMapZoom = newZoom;
        if (newZoom>$scope.mapSettings.zoomBound){
            if (oldZoom<=$scope.mapSettings.zoomBound)
            {                  
                if ($scope.viewCurrentCityObjectsFlag === true){
                    $scope.viewCurrentCityObjectsFlag = false;
                    return;
                };  
                //clear open popup
                var popupPane = document.getElementsByClassName("leaflet-pane leaflet-popup-pane");
                popupPane[0].innerHTML = "";
//                markers = $scope.setObjectsOnMap($scope.objectsOfCities, markers);   
                markersForObjects = $scope.setObjectsOnMap($scope.objectsOfCities, markersForObjects);
                $scope.markersOnMap = markersForObjects;
//                angular.extend($scope, {markersOnMap: markersForObjects});
            };           
        };
        if (newZoom<=$scope.mapSettings.zoomBound){
            if (oldZoom>$scope.mapSettings.zoomBound){
                //clear open popup
                var popupPane = document.getElementsByClassName("leaflet-pane leaflet-popup-pane");
                popupPane[0].innerHTML = "";
//                markers = $scope.setCitiesOnMap($scope.cities, markers);
                markersForCities = $scope.setCitiesOnMap($scope.cities, markersForCities);
                $scope.markersOnMap = markersForCities;
//                angular.extend($scope, {markersOnMap: markersForCities});
            };    
        };
//        if (angular.isArray($scope.markersOnMap)&&($scope.markersOnMap.length ===0)){
//            markersOnMap = markers;
//            angular.extend($scope,{markersOnMap});
//        }else{
//            $scope.markersOnMap = markers;
//        };
        
//        angular.extend($scope, {markersOnMap: markers});
    }, false);
    
    $scope.$watch('mapCenter.lat',function(newLat){
        $cookies.objectMapLat = newLat;
    });
    $scope.$watch('mapCenter.lng',function(newLng){
        $cookies.objectMapLng = newLng;
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
        var openedPopup = document.getElementsByClassName("leaflet-pane leaflet-popup-pane");
console.log(openedPopup);        
        if (angular.isDefined(openedPopup)){
            openedPopup[0].innerHTML= "";
        };
        $scope.mapSettings.loadingFlag = true;

        refreshCitiesData();
    }, false);
   
    //reset extended objects, when open popup
    $scope.$on('leafletDirectiveMarker.click', function(e, args){
        $scope.diffObjectData =[];
        $scope.newObjectData =[];
        $scope.newCityData = {};
        $scope.diffCityData = {};
        $scope.markerSettings = {};
        $scope.diffData ={};
        $scope.newData = {};
        //set the width of the popup window
//        var popup = document.getElementsByClassName("leaflet-popup-content-wrapper");
//        popup[0].className = "leaflet-popup-content-wrapper nmc-leaflet-popup-content-wrapper-compare";
    });
    //
    $rootScope.$on('objectSvc:loaded',function(){
        $scope.mapSettings.loadingFlag = objectSvc.getLoadingStatus();
    });
});