/*jslint white: true, node: true*/
/*global angular, moment*/
'use strict';
angular.module('portalNMC')
.controller('ObjectsMapCtrl', function($rootScope, $scope, $cookies, objectSvc, mainSvc, $timeout){
    $rootScope.ctxId = "object_map_page";
    $scope.mapSettings = {};
    $scope.mapSettings.userFormat = "DD.MM.YYYY";
    $scope.mapSettings.systemFormat = "YYYY-MM-DD";
    $scope.mapSettings.zoomBound = 9; //zoom>zoomBound - view objects on map; zoom<zoomBound - view cities on map
    $scope.mapSettings.dateFrom = moment().subtract(30, 'days').startOf('day').format($scope.mapSettings.systemFormat);
    $scope.mapSettings.dateTo = moment().endOf('day').format($scope.mapSettings.systemFormat);
    $scope.mapSettings.loadingFlag = objectSvc.getLoadingStatus();
    $scope.mapSettings.drawMarkersFlag = !$scope.mapSettings.loadingFlag;   
    
    $scope.mapSettings.abortCompareFlag = false;
    
    $scope.mapSettings.ctxId = "object_map";
    
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
    
    if (angular.isDefined(objectSvc.getObjectSettings().mapZoomDetail)){
        $scope.mapSettings.zoomDetail = Number(objectSvc.getObjectSettings().mapZoomDetail);
    }
    
    if (angular.isDefined(objectSvc.getObjectSettings().mapZoom)){
        mapCenter.zoom = Number(objectSvc.getObjectSettings().mapZoom);
    }
    if (angular.isDefined(objectSvc.getObjectSettings().mapCenterLat)){
        mapCenter.lat = Number(objectSvc.getObjectSettings().mapCenterLat);
    }
    if (angular.isDefined(objectSvc.getObjectSettings().mapCenterLng)){
        mapCenter.lng = Number(objectSvc.getObjectSettings().mapCenterLng);
    }  
    angular.extend($scope, {
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
//console.log("Begin prepare data.");            
                            //if close popup by escape - abort perform
                            if($scope.mapSettings.abortCompareFlag === true){  
                                $scope.mapSettings.abortCompareFlag = false;
                                $scope.comparingFlag = false;
                                return;
                            };
            
                            if(!angular.isArray(response.data)||(response.data.length === 0)){
                                console.log("Not enough objects for the city.");
                                console.log(response.data);
                                return;
                            };
                            $scope.newCityData = response.data[0];
                            $scope.markerSettings.isNewDataFlag = true;
                            calculateCityServicesSummary($scope.newCityData);
//console.log($scope.newCityData);            
//console.log(city);
                            //city vs newCity
                            var tmpDiff = {};
                            tmpDiff.heatArea = city.servicesSummary.heatArea;
                            if ((city.servicesSummary.hw !== "Нет данных") && ($scope.newCityData.servicesSummary.hw !== "Нет данных")){
                                tmpDiff.hw = ((city.servicesSummary.hw - $scope.newCityData.servicesSummary.hw)).toFixed(2);
                                tmpDiff.hwPerCent = Math.abs((city.servicesSummary.hw - $scope.newCityData.servicesSummary.hw) * 100 / $scope.newCityData.servicesSummary.hw).toFixed(2);
                                tmpDiff.hwMeasureUnit = 'м3';
                                tmpDiff.hwAveragePerCent = ((tmpDiff.heatArea > 0)) ? Math.abs((city.servicesSummary.hwAverage - $scope.newCityData.servicesSummary.hwAverage) * 100 / $scope.newCityData.servicesSummary.hwAverage).toFixed(2) : null;
                                tmpDiff.hwAverage = ((city.servicesSummary.hwAverage - $scope.newCityData.servicesSummary.hwAverage)).toFixed(2);
                            };
                            if ((city.servicesSummary.heat !== "Нет данных") && ($scope.newCityData.servicesSummary.heat !== "Нет данных")){
                                tmpDiff.heat = ((city.servicesSummary.heat - $scope.newCityData.servicesSummary.heat)).toFixed(2);
                                tmpDiff.heatPerCent = Math.abs((city.servicesSummary.heat - $scope.newCityData.servicesSummary.heat) * 100 / $scope.newCityData.servicesSummary.heat).toFixed(2);
                                tmpDiff.heatMeasureUnit = 'м3';
                                tmpDiff.heatAveragePerCent = ((tmpDiff.heatArea > 0)) ? Math.abs((city.servicesSummary.heatAverage - $scope.newCityData.servicesSummary.heatAverage) * 100 / $scope.newCityData.servicesSummary.heatAverage).toFixed(2) : null;
                                tmpDiff.heatAverage = ((city.servicesSummary.heatAverage - $scope.newCityData.servicesSummary.heatAverage)).toFixed(2);
                            };         
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
//console.log($scope.cities);            
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
                            $scope.mapSettings.drawMarkersFlag = !$scope.mapSettings.loadingFlag;
                        },
                       function(err){
                            console.log(err);
                            $scope.mapSettings.loadingFlag = false;
                            $scope.mapSettings.drawMarkersFlag = !$scope.mapSettings.loadingFlag;
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
        mc.zoom = $scope.mapSettings.zoomDetail || $scope.mapSettings.zoomBound+2;
        
        var tmpObjects = curCity.cityObjects;
        markersForObjects = new Array();
        $scope.setObjectsOnMap(tmpObjects, markersForObjects); 
        $scope.markersOnMap = markersForObjects;
        $scope.viewCurrentCityObjectsFlag = true;

        angular.extend($scope, {mapCenter: mc});
    };
    
    var compareWith = function(){
        $scope.markerSettings.cmpFlag = !$scope.markerSettings.cmpFlag;
    };
    
    var changeCmpPeriod = function(firstPeriod, daysAgo, secondPeriod){
//console.log(firstPeriod);
//console.log(daysAgo);
//console.log(secondPeriod);        
        $scope.markerSettings.runFlag = false;
        var result = {};
        result.dateFrom = moment(firstPeriod.dateFrom).subtract(daysAgo, 'days').format($scope.mapSettings.systemFormat);
        result.dateTo = moment(firstPeriod.dateTo).subtract(daysAgo, 'days').format($scope.mapSettings.systemFormat);
        result.dateFromUserformat = moment(result.dateFrom).format($scope.mapSettings.userFormat);
        result.dateToUserformat = moment(result.dateTo).format($scope.mapSettings.userFormat);
        secondPeriod = result;
        $scope.cmpPeriod = result;      
//console.log(result);        
//console.log("===========================================================================");                
        return result;
    };

        //данные для сравнения по объекту
//    получаем данные за заданный период по объекту, делаем сравнение с текущими данными
    //записываем новые полученные данные за заданный период в массив newObjectData
    //делаем расчет разностей
    // записываем полученные разности в массив diffObjectData
    var getObjectsConsumingForObject = function(object, settings){ 
        $scope.mapSettings.abortCompareFlag = false;
        $scope.markerSettings.cmpDataFlag = true;
        $scope.markerSettings.runFlag = true;
        var tmpSettings = angular.copy(settings);

        objectSvc.getObjectConsumingData(tmpSettings, object.contObject.id)
            .then(function(response){
                // if user press ESC - abort perform.
                if($scope.mapSettings.abortCompareFlag === true){ 
                    $scope.mapSettings.abortCompareFlag = false;
                    $scope.comparingFlag = false;
                    return;
                };
                $scope.newObjectData = response.data;
                for (var i = 0; i < $scope.newObjectData.serviceTypeARTs.length; i++){
                    var curData = $scope.newObjectData.serviceTypeARTs[i];
                    curData.absConsValue = (curData.absConsValue === null) ? null : Number(curData.absConsValue.toFixed(2));
                    curData.tempValue = (curData.tempValue === null) ? null : Number(curData.tempValue.toFixed(2));
                };
                var tmpDiff = {};
                $scope.newData = prepareObjectData(response.data);
                var curData = prepareObjectData(object);
                if ((curData.hw !== "Нет данных") && ($scope.newData.hw !== "Нет данных")){
                    tmpDiff.hw = (curData.hw - $scope.newData.hw).toFixed(2);
                    tmpDiff.hwPerCent = Math.abs((curData.hw - $scope.newData.hw) * 100 / $scope.newData.hw).toFixed(2);
                    tmpDiff.hwMeasureUnit = " м3";//servType.measureUnit;//"м<sup>3</sup>";
                    if (object.contObject.heatArea > 0){
                        tmpDiff.hwAverage = ((curData.hwAverage - $scope.newData.hwAverage)).toFixed(2);
                        tmpDiff.hwAveragePerCent = Math.abs((curData.hwAverage - $scope.newData.hwAverage) * 100 / ($scope.newData.hwAverage)).toFixed(2);
                        tmpDiff.hwAverageMeasureUnit = "m3/m2";//"м<sup>3</sup>/м<sup>2</sup>";
                    }else{
                        tmpDiff.hwAverage = "Не задана площадь";
                        tmpDiff.hwAverageMeasureUnit = "";
                    };
                };
                if ((curData.heat !== "Нет данных") && ($scope.newData.heat !== "Нет данных")){
                    tmpDiff.heat = (curData.heat - $scope.newData.heat).toFixed(2);
                    tmpDiff.heatPerCent = Math.abs((curData.heat - $scope.newData.heat) * 100 / $scope.newData.heat).toFixed(2);
                    tmpDiff.heatMeasureUnit = " ГКал";//servType.measureUnit;//"м<sup>3</sup>";
                    if (object.contObject.heatArea > 0){
                        tmpDiff.heatAverage = ((curData.heatAverage - $scope.newData.heatAverage)).toFixed(2);
                        tmpDiff.heatAveragePerCent = Math.abs((curData.heatAverage - $scope.newData.heatAverage) * 100 / ($scope.newData.heatAverage)).toFixed(2);
                        tmpDiff.heatAverageMeasureUnit = "ГКал/m2";//"м<sup>3</sup>/м<sup>2</sup>";
                    }else{
                        tmpDiff.heatAverage = "Не задана площадь";
                        tmpDiff.heatAverageMeasureUnit = "";
                    };
                };

            
     ///// to deleeteeee
                var tmpDiffArray = [];
                
                object.serviceTypeARTs.forEach(function(serv){
                    for (var i = 0; i < $scope.newObjectData.serviceTypeARTs.length; i++){
                        var curData = $scope.newObjectData.serviceTypeARTs[i];
                        if (serv.contServiceType === curData.contServiceType){
//console.log(curData);                            
//console.log(serv);          
                        
//                            tmpDiff[contServiceType] = {};
                            var tmp = {};
                            
                            tmp.contServiceType= serv.contServiceType;
                            tmp.measureUnit = serv.measureUnit;
                            tmp.absConsValue = Number((serv.absConsValue - curData.absConsValue).toFixed(2));
                            tmp.absConsValueAverage = (object.contObject.heatArea > 0) ? Number((tmp.absConsValue / object.contObject.heatArea).toFixed(2)) : null;
                          
                            tmp.absConsValueAveragePerCent = ((object.contObject.heatArea > 0) && ((curData.absConsValue !== null)) && (curData.absConsValue !== 0)) ? Number((tmp.absConsValueAverage / curData.absConsValue * object.contObject.heatArea * 100).toFixed(2)) : null;
//                                (object.contObject.heatArea>0)?Number(((tmp.absConsValue)*100/curData.absConsValue).toFixed(2)):null;
                            tmp.tempValue = Number((serv.tempValue - curData.tempValue).toFixed(2));
                            tmp.absConsValuePerCent = (curData.absConsValue > 0) ? Number(((serv.absConsValue - curData.absConsValue) / curData.absConsValue * 100).toFixed(2)) : null;
                            tmp.tempValuePerCent = (curData.tempValue > 0) ? Number(((serv.tempValue - curData.tempValue) / curData.tempValue * 100).toFixed(2)) : null;
                            tmpDiffArray.push(tmp);
                        };
                    };
                });
                $scope.diffObjectData = tmpDiffArray;
                $scope.diffData = tmpDiff;
                $scope.comparingFlag = false;
//console.log($scope.diffData);            
            });
        
//        var 
    };
    
    var getObjectsConsumingForCity = function(city, settings){
//console.log("start comparing");   
//console.log(city);        
        $scope.mapSettings.abortCompareFlag = false;
        $scope.markerSettings.cmpDataFlag = true;
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
    var calculateCityServicesSummary = function(city){
        var hwSum = 0;
        var heatSum = 0;
        var areaSum = 0
        city.cityObjects.forEach(function(obj){
            var hw = 0;
            var heat = 0;
            if (angular.isDefined(obj.contObject.heatArea) && (obj.contObject.heatArea != null)){
                areaSum += obj.contObject.heatArea;
            };
            if (angular.isUndefined(obj.serviceTypeARTs) || (obj.serviceTypeARTs === null) || (obj.serviceTypeARTs.length === 0)){
                return;
            };
            obj.serviceTypeARTs.forEach(function(sys){
//serch indicators <0                
//if(sys.absConsValue<0){                
//    console.log(obj);      
//};
                switch (sys.contServiceType){
                    case "heat" : heatSum += sys.absConsValue; break;
                    case "hw" : hwSum += sys.absConsValue; break;
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
            servicesSummary.heatAverage = (heatSum / areaSum).toFixed(2);
            servicesSummary.heatAverageMeasureUnit = 'ГКал/м2';
            servicesSummary.hwAverage = (hwSum / areaSum).toFixed(2);
            servicesSummary.hwAverageMeasureUnit = 'м3/м2';
        };
        city.servicesSummary =servicesSummary; 
    };
    
        //create message for city marker
    var createMessageForCityMarker = function(city){
        var markerMessage = "<div id='" + city.cityFiasUUID + "' class=''>";
        markerMessage += "<label>" + city.cityName + "</label>, " + city.cityObjects.length + " объектов <button class='glyphicon glyphicon-search marginLeft5' ng-click='viewObjectsOnMap(\"" + city.cityFiasUUID + "\")' title='См. объекты на карте'></button><br>";
        markerMessage += "<hr class='nmc-hr-in-modal'>";
        markerMessage += "Потребление в период с " + moment($scope.mapSettings.dateFrom).format($scope.mapSettings.userFormat) + " по " + moment($scope.mapSettings.dateTo).format($scope.mapSettings.userFormat) + "<br>";
        markerMessage += "<div class='row nmc-hide' ng-class=\"{'nmc-hide':!markerSettings.cmpFlag,'nmc-show':markerSettings.cmpFlag}\">";
        markerMessage += "    <div class='col-md-8'>";
        markerMessage += "сравнить с аналогичным периодом";
        markerMessage += "        <select ng-model='daysAgo' ng-init='daysAgo=\"30\";changeCmpPeriod(markerSettings, daysAgo, cmpPeriod)' ng-change='changeCmpPeriod(markerSettings, daysAgo, cmpPeriod)'>";
        markerMessage += "            <option value='30'>месяц назад</option>";
        markerMessage += "            <option value='92'>квартал назад</option>";
        markerMessage += "            <option value='182'>пол года назад</option>";
        markerMessage += "            <option value='365'>год назад</option>";
        markerMessage += "        </select>";
        markerMessage += "        <div>";
        markerMessage += "            с {{cmpPeriod.dateFromUserformat}} по {{cmpPeriod.dateToUserformat}}";
        markerMessage += "        </div>";
        markerMessage += "    </div>";
        markerMessage += "    <div class='col-md-3'>";
        markerMessage += "        <button title='Сравнить' ng-disabled='markerSettings.runFlag' class='btn btn-xs btn-primary' ng-click='comparingFlag=true;getObjectsConsumingForCity(curCity, cmpPeriod)'>Сравнить</button><span ng-show=\"comparingFlag\" class=\"nmc-loading marginLeft5\">";
        markerMessage += "<i class=\"fa fa-spinner fa-spin\"></i> </span>";
        markerMessage += "    </div>";
        markerMessage += "</div>";
        markerMessage += "<div class='text-center'>";
        markerMessage += "    <i class='btn btn-xs glyphicon glyphicon-chevron-down' ng-class=\"{'glyphicon-chevron-down':!markerSettings.cmpFlag,'glyphicon-chevron-up':markerSettings.cmpFlag}\" ng-click='compareWith()'></i>";
        markerMessage += "</div>";
        markerMessage += "<hr class='nmc-hr-in-modal'>";
        
        markerMessage += "<table class='table table-striped table-condensed nmc-table-popup'>";
        markerMessage += "    <tr>";
        markerMessage += "        <td class='col-md-4'>";
        markerMessage += "            <label>Горячая вода:</label>";
        markerMessage += "        </td>";
        markerMessage += "        <td class='col-md-4'>";
        markerMessage += "        </td>";
        markerMessage += "        <td class='col-md-4'>";
        markerMessage += "            <span ng-if='markerSettings.cmpDataFlag'><label>Горячая вода:</label></span>";
        markerMessage += "        </td>";
        markerMessage += "    </tr>";
        markerMessage += "    <tr >";
        markerMessage += "        <td>";          
        markerMessage += "            <span class='marginLeft15'>Всего:</span>";
        markerMessage += "            "+city.servicesSummary.hw+" "+city.servicesSummary.hwMeasureUnit;
        markerMessage += "        </td>";
        markerMessage += "        <td>";
        markerMessage += "            <span ng-if='(diffData.hwPerCent!=null)&&(diffData.hwPerCent!=\"NaN\")&&(diffData.hwPerCent!=\"Infinity\")&&(diffData.hw>0)'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffData.hwPerCent<100),'nmc-alert':(diffData.hwPerCent>=100)}\"></i></span>";
        markerMessage += "            <span ng-if='(diffData.hwPerCent!=null)&&(diffData.hwPerCent!=\"NaN\")&&(diffData.hwPerCent!=\"Infinity\")&&(diffData.hw<0)'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diffData.hwPerCent<100),'nmc-alert':(diffData.hwPerCent>=100)}\"></i></span>";
        markerMessage += "            <span ng-if='(diffData.hwPerCent!=null)&&(diffData.hwPerCent!=\"NaN\")&&(diffData.hwPerCent!=\"Infinity\")'>{{diffData.hwPerCent}}% <span class='marginRight5'></span> {{diffData.hw}} м<sup>3</sup></span>";
        markerMessage += "        </td>";
        markerMessage += "        <td>";
        markerMessage += "            <span ng-if='markerSettings.cmpDataFlag'><span class='marginLeft15'>Всего:</span>";
        markerMessage += "            {{newData.hw}} <span ng-if='(newData.hw!=null)&&(newData.hw!=\"NaN\")&&(newData.hw!=\"Infinity\")&&(newData.hw!=\"Нет данных\")&&(newData.hw!=\"Не задана площадь\")'>м<sup>3</sup></span></span>";
        markerMessage += "        </td>";
        markerMessage += "    </tr>";
        markerMessage += "    <tr >";
        markerMessage += "        <td>";
        markerMessage += "            <span class='marginLeft15'>Удельное:</span>";
        markerMessage += "            "+city.servicesSummary.hwAverage+" "+city.servicesSummary.hwAverageMeasureUnit;
        markerMessage += "        </td>";
        markerMessage += "        <td>";
        markerMessage += "            <span ng-if='(diffData.hwAveragePerCent!=null)&&(diffData.hwAveragePerCent!=\"NaN\")&&(diffData.hwAveragePerCent!=\"Infinity\")&&(diffData.hwAverage>0)'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffData.hwAveragePerCent<100),'nmc-alert':(diffData.hwAveragePerCent>=100)}\"></i></span>";
        markerMessage += "            <span ng-if='(diffData.hwAveragePerCent!=null)&&(diffData.hwAveragePerCent!=\"NaN\")&&(diffData.hwAveragePerCent!=\"Infinity\")&&(diffData.hwAverage<0)'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diffData.hwAveragePerCent<100),'nmc-alert':(diffData.hwAveragePerCent>=100)}\"></i></span>";
        markerMessage += "            <span ng-if='(diffData.hwAveragePerCent!=null)&&(diffData.hwAveragePerCent!=\"NaN\")&&(diffData.hwAveragePerCent!=\"Infinity\")'>{{diffData.hwAveragePerCent}}% <span class='marginRight5'></span> {{diffData.hwAverage}} м<sup>3</sup>/м<sup>2</sup></span>";
        markerMessage += "        </td>";
        markerMessage += "        <td>";
        markerMessage += "           <span ng-if='markerSettings.cmpDataFlag'> <span class='marginLeft15'>Удельное:</span>";
        markerMessage += "            {{newData.hwAverage}} <span ng-if='(newData.hwAverage!=null)&&(newData.hwAverage!=\"NaN\")&&(newData.hwAverage!=\"Infinity\")&&(newData.hwAverage!=\"Нет данных\")&&(newData.hwAverage!=\"Не задана площадь\")'>м<sup>3</sup>/м<sup>2</sup></span></span>";
        markerMessage += "        </td>";
        markerMessage += "    </tr>";
        markerMessage += "    <tr >";
        markerMessage += "        <td>";
        markerMessage += "            <label >Отопление:</label>";
        markerMessage += "        </td>";
        markerMessage += "        <td>";
        markerMessage += "        </td>";
        markerMessage += "        <td>";
        markerMessage += "            <span ng-if='markerSettings.cmpDataFlag'><label>Отопление:</label></span>";
        markerMessage += "        </td>";
        markerMessage += "    </tr>";
        markerMessage += "    <tr >";
        markerMessage += "        <td>";
        markerMessage += "            <span class='marginLeft15'>Всего:</span>";
        markerMessage += "            "+city.servicesSummary.heat+" "+city.servicesSummary.heatMeasureUnit;
        markerMessage += "        </td>";
        markerMessage += "        <td>";
        markerMessage += "            <span ng-if='(diffData.heatPerCent!=null)&&(diffData.heatPerCent!=\"NaN\")&&(diffData.heatPerCent!=\"Infinity\")&&(diffData.heat>0)'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffData.heatPerCent<100),'nmc-alert':(diffData.heatPerCent>=100)}\"></i></span>";
        markerMessage += "            <span ng-if='(diffData.heatPerCent!=null)&&(diffData.heatPerCent!=\"NaN\")&&(diffData.heatPerCent!=\"Infinity\")&&(diffData.heat<0)'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diffData.heatPerCent<100),'nmc-alert':(diffData.heatPerCent>=100)}\"></i></span>";
        markerMessage += "            <span ng-if='(diffData.heatPerCent!=null)&&(diffData.heatPerCent!=\"NaN\")&&(diffData.heatPerCent!==\"Infinity\")'>{{diffData.heatPerCent}}% <span class='marginRight5'></span> {{diffData.heat}} ГКал</span>";
        markerMessage += "        </td>";
        markerMessage += "        <td>";
        markerMessage += "           <span ng-if='markerSettings.cmpDataFlag'> <span class='marginLeft15'>Всего:</span>";
        markerMessage += "            {{newData.heat}} {{newData.heatMeasureUnit}}</span>";
        markerMessage += "        </td>";
        markerMessage += "    </tr>";
        markerMessage += "    <tr >";
        markerMessage += "        <td>";
        markerMessage += "            <span class='marginLeft15'>Удельное:</span>";
        markerMessage += "            "+city.servicesSummary.heatAverage+" "+city.servicesSummary.heatAverageMeasureUnit;
        markerMessage += "        </td>";
        markerMessage += "        <td>";
        markerMessage += "            <span ng-if='(diffData.heatAveragePerCent!=null)&&(diffData.heatAveragePerCent!=\"NaN\")&&(diffData.heatAveragePerCent!=\"Infinity\")&&(diffData.heatAverage>0)'><i class='glyphicon glyphicon-triangle-top' ng-class=\"{'nmc-normal':(diffData.heatAveragePerCent<100),'nmc-alert':(diffData.heatAveragePerCent>=100)}\"></i></span>";
        markerMessage += "            <span ng-if='(diffData.heatAveragePerCent!=null)&&(diffData.heatAveragePerCent!=\"NaN\")&&(diffData.heatAveragePerCent!=\"Infinity\")&&(diffData.heatAverage<0)'><i class='glyphicon glyphicon-triangle-bottom' ng-class=\"{'nmc-normal-alt':(diffData.heatAveragePerCent<100),'nmc-alert':(diffData.heatAveragePerCent>=100)}\"></i></span>";
        markerMessage += "            <span ng-if='(diffData.heatAveragePerCent!=null)&&(diffData.heatAveragePerCent!=\"NaN\")&&(diffData.heatAveragePerCent!==\"Infinity\")'>{{diffData.heatAveragePerCent}}% <span class='marginRight5'></span> {{diffData.heatAverage}} ГКал/м<sup>2</sup></span>";
        markerMessage += "        </td>";
        markerMessage += "        <td>";
        markerMessage += "            <span ng-if='markerSettings.cmpDataFlag'><span class='marginLeft15'>Удельное:</span>";
        markerMessage += "            {{newData.heatAverage}} <span ng-if='(newData.heatAverage!=null)&&(newData.heatAverage!=\"NaN\")&&(newData.heatAverage!=\"Infinity\")&&(newData.heatAverage!=\"Нет данных\")&&(newData.heatAverage!=\"Не задана площадь\")'>ГКал/м<sup>2</sup></span></span>";
        markerMessage += "        </td>";
        markerMessage += "    </tr>";
        markerMessage += "</table>";
        markerMessage += "    </div>";
        markerMessage += "</div>";  
        return markerMessage;
    };
    
    var cmgms = function(newMarker){
console.log(newMarker);        
        return function(newMarker){
            console.log("getMessageScope");                
console.log(newMarker);        
            angular.extend($scope, {
                markerSettings:{
                    cmpFlag: false,
                    dateFrom: $scope.mapSettings.dateFrom,
                    dateTo: $scope.mapSettings.dateTo
                }
            });
            var tmpCity = angular.copy(newMarker.city);
    //                $scope.curCity = tmpCity;
            angular.extend($scope, {curCity: tmpCity});
            angular.extend($scope,{changeCmpPeriod: changeCmpPeriod});
            angular.extend($scope,{compareWith: compareWith});
            angular.extend($scope, {viewObjectsOnMap: viewObjectsOnMap});
            angular.extend($scope, {getObjectsConsumingForCity: getObjectsConsumingForCity});
            return $scope;
        };
    };

    var cityMarkerGetMessageScope = function (newMarker) {
console.log("getMessageScope");                
console.log(newMarker);        
        angular.extend($scope, {
            markerSettings:{
                cmpFlag: false,
                dateFrom: $scope.mapSettings.dateFrom,
                dateTo: $scope.mapSettings.dateTo
            }
        });
        var tmpCity = angular.copy(newMarker.city);
//                $scope.curCity = tmpCity;
        angular.extend($scope, {curCity: tmpCity});
        angular.extend($scope,{changeCmpPeriod: changeCmpPeriod});
        angular.extend($scope,{compareWith: compareWith});
        angular.extend($scope, {viewObjectsOnMap: viewObjectsOnMap});
        angular.extend($scope, {getObjectsConsumingForCity: getObjectsConsumingForCity});
        return $scope; 
    };
    
        //prepare city marker
    $scope.prepareCityMarker = function(city, marker){         
        if ((city.cityGeoPosX === null) || (city.cityGeoPosY===null)){
            return;
        };          
        //calculate system indicators for all objects of the city
        calculateCityServicesSummary(city);       
        var markerMessage = createMessageForCityMarker(city);
        //var newMarker = {};
        marker.focus= false;
        marker.city = city;
        marker.lng=city.cityGeoPosX;
        marker.lat= city.cityGeoPosY;
        marker.getMessageScope = null;
        marker.getMessageScope=function(){
//console.log("getMessageScope");                
//console.log(marker);        
            angular.extend($scope, {
                markerSettings:{
                    cmpFlag: false,
                    dateFrom: $scope.mapSettings.dateFrom,
                    dateTo: $scope.mapSettings.dateTo
                }
            });
            var tmpCity = angular.copy(marker.city);
    //                $scope.curCity = tmpCity;
            angular.extend($scope, {curCity: tmpCity});
            angular.extend($scope,{changeCmpPeriod: changeCmpPeriod});
            angular.extend($scope,{compareWith: compareWith});
            angular.extend($scope, {viewObjectsOnMap: viewObjectsOnMap});
            angular.extend($scope, {getObjectsConsumingForCity: getObjectsConsumingForCity});
            return $scope;
        };
        marker.message = ""+markerMessage+"";
        marker.compileMessage = true;
        marker.icon =  angular.copy(monitorMarker); //set current marker      
        marker.cityFiasUUID = city.cityFiasUUID;  
//console.log(marker);        
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
    
    $scope.setCitiesOnMap = function(cityArray, oldmarkerArray){
//console.log(cityArray, markerArray);        
        if (!angular.isArray(cityArray)||(cityArray.length === 0)||!angular.isArray(oldmarkerArray)){
            return [];
        };
        oldmarkerArray = deleteCityMarkers(cityArray, oldmarkerArray);
        var markerArray = [];
        cityArray.forEach(function(elem){            
            if(angular.isUndefined(elem.cityGeoPosX)||angular.isUndefined(elem.cityGeoPosY)||(elem.cityGeoPosX ===null)||(elem.cityGeoPosY===null)){
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
//console.log("marker.Exists");                        
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
        $scope.mapSettings.drawMarkersFlag = false;
//console.log(markerArray);        
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
        
        var markerMessage = "<div id='" + currentObject.contObject.id + "' class=''>";
        markerMessage += "<label>" + currentObject.contObject.fullName + "</label><br>";
        markerMessage+="<hr class='nmc-hr-in-modal'>";
        markerMessage+="Потребление в период с " + moment($scope.mapSettings.dateFrom).format($scope.mapSettings.userFormat) + " по " + moment($scope.mapSettings.dateTo).format($scope.mapSettings.userFormat) + "<br>";
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
        markerMessage+="            с {{cmpPeriod.dateFromUserformat}} по {{cmpPeriod.dateToUserformat}}";
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
        markerMessage+="            <span ng-if='(diffData.hwPerCent!=null)&&(diffData.hwPerCent!=\"NaN\")&&(diffData.hwPerCent!=\"Infinity\")'>{{diffData.hwPerCent}}% <span class='marginRight5'></span> {{diffData.hw}} м<sup>3</sup></span>";
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
        markerMessage+="            <span ng-if='(diffData.hwAveragePerCent!=null)&&(diffData.hwAveragePerCent!=\"NaN\")&&(diffData.hwAveragePerCent!=\"Infinity\")'>{{diffData.hwAveragePerCent}}% <span class='marginRight5'></span> {{diffData.hwAverage}} м<sup>3</sup>/м<sup>2</sup></span>";
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
        markerMessage+="            <span ng-if='(diffData.heatPerCent!=null)&&(diffData.heatPerCent!=\"NaN\")&&(diffData.heatPerCent!=\"Infinity\")'>{{diffData.heatPerCent}}% <span class='marginRight5'></span> {{diffData.heat}} ГКал</span>";
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
        markerMessage+="            <span ng-if='(diffData.heatAveragePerCent!=null)&&(diffData.heatAveragePerCent!=\"NaN\")&&(diffData.heatAveragePerCent!==\"Infinity\")'>{{diffData.heatAveragePerCent}}% <span class='marginRight5'></span> {{diffData.heatAverage}} ГКал/м<sup>2</sup></span>";
        markerMessage+="        </td>";
        markerMessage+="        <td>";
        markerMessage+="           <span ng-if='markerSettings.cmpDataFlag'> <span class='marginLeft15'>Удельное:</span>";
        markerMessage+="            {{newData.heatAverage}} <span ng-if='(newData.heatAverage!=null)&&(newData.heatAverage!=\"NaN\")&&(newData.heatAverage!=\"Infinity\")&&(newData.heatAverage!=\"Нет данных\")&&(newData.heatAverage!=\"Не задана площадь\")'>ГКал/м<sup>2</sup></span> </span>";
        markerMessage+="        </td>";
        markerMessage+="    </tr>";
        markerMessage+="</table>";
        markerMessage+="    </div>";
        markerMessage+="</div>";  
        return markerMessage;
    };
    
    //create object marker
    $scope.prepareObjectMarker = function(object, marker){ 
//console.log("prepareObjectMarker");        
        if ((object.contObjectGeo.geoPosX === null) || (object.contObjectGeo.geoPosY===null)){
            return;
        }; 
        
        var markerMessage = createMessageForObjectMarker(object);      
        marker.focus= false;
        marker.object = object;
        marker.lng = object.contObjectGeo.geoPosX;
        marker.lat = object.contObjectGeo.geoPosY;
        marker.getMessageScope = null;
        marker.getMessageScope=function () {
//console.log("getMessageScope object");
//console.log(marker);            
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
            
            var tmpCity = angular.copy(marker.city);
    //                $scope.curCity = tmpCity;
            angular.extend($scope, {curCity: tmpCity});
            angular.extend($scope, {getObjectsConsumingForCity: getObjectsConsumingForCity});
            
                return $scope; };
            marker.message = ""+markerMessage+"";
            marker.compileMessage = true;
            marker.icon =  angular.copy(monitorMarker); //set current marker
        
        marker.contObjectId = object.contObject.id;
        return marker;
    };
    
    
    var deleteObjectMarkers = function(objectsArray, markerArray){
//console.log(markerArray.length);        
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
//console.log(deletedMarkers);         
//console.log(markerArray.length);         
        return markerArray;
    };
    
    $scope.setObjectsOnMap = function(objectArray, markerArray){
//console.log(objectArray, markerArray.length);        
        if (!angular.isArray(objectArray)||(objectArray.length === 0)||!angular.isArray(markerArray)){
            return [];
        };
        markerArray = deleteObjectMarkers(objectArray, markerArray);
//console.log(markerArray.length);        
                //check objects
//console.log(">>> prepare markers <<<");
//console.log($scope.mapSettings.dateFrom);        
        objectArray.forEach(function(elem){
//console.log(elem);            
            if (angular.isUndefined(elem.contObjectGeo.geoPosX) || angular.isUndefined(elem.contObjectGeo.geoPosY) ||(elem.contObjectGeo.geoPosX ===null)||(elem.contObjectGeo.geoPosY===null)){
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
                        isMarkerExists = true;
                        return true;
                    };
                });
            };                       
            marker= $scope.prepareObjectMarker(elem, marker);
            marker.focus = false;          
            if (isMarkerExists!==true){
                markerArray.push(marker);  
            };                  
        });       
        var arrLength = markerArray.length;
        while (arrLength>=1){
            arrLength--;                                               
            if (angular.isUndefined(markerArray[arrLength].contObjectId)){                   
                markerArray.splice(arrLength, 1);
            };
        }; 
//console.log(markerArray);
        $scope.mapSettings.drawMarkersFlag = false;
        return markerArray;
    };
    
    if ($scope.mapCenter.zoom > $scope.mapSettings.zoomBound){
        markersForObjects = $scope.setObjectsOnMap($scope.objectsOfCities, markersForObjects);
        $scope.markersOnMap = markersForObjects;
    }else{
        markersForCities = $scope.setCitiesOnMap($scope.cities, markersForCities);
        $scope.markersOnMap = markersForCities;
    };
    
    $scope.$watch("mapCenter.zoom", function(newZoom, oldZoom){
//console.log($scope.objectsOfCities);
//console.log($scope.cities);
        objectSvc.setObjectSettings({mapZoom:newZoom});
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
                markersForObjects = $scope.setObjectsOnMap($scope.objectsOfCities, markersForObjects);
                $scope.markersOnMap = [];
                $timeout(function(){
                    $scope.markersOnMap = markersForObjects;
                }, 10);
//console.log($scope.markersOnMap);                
            };           
        };
        if (newZoom<=$scope.mapSettings.zoomBound){
            if (oldZoom>$scope.mapSettings.zoomBound){
                //clear open popup
                var popupPane = document.getElementsByClassName("leaflet-pane leaflet-popup-pane");
                popupPane[0].innerHTML = "";
                markersForCities = $scope.setCitiesOnMap($scope.cities, markersForCities);
                $scope.markersOnMap = [];
                $timeout(function(){
                    $scope.markersOnMap = markersForCities;
                }, 10);
//console.log($scope.markersOnMap);                
            };    
        };
    }, false);
    
    $scope.$watch('mapCenter.lat',function(newLat){
        objectSvc.setObjectSettings({mapCenterLat:newLat});
    });
    $scope.$watch('mapCenter.lng',function(newLng){
        objectSvc.setObjectSettings({mapCenterLng:newLng});        
    });
    
    //test
//    $scope.$watch('markerSettings',function(newval, oldval){
//console.log(newval);        
//console.log(oldval);        
//    });
    //end test 
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
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
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
        maxDate: moment().endOf('day'),

        format : 'DD.MM.YYYY'
        ,separator: " по "
    };

    $scope.$watch('objectDates', function (newDates, oldDates) {                    
//console.log('Watch objectDates');    
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
//console.log("Wark watch");        
        if (angular.isDefined(openedPopup)){
            openedPopup[0].innerHTML= "";
        };
        $scope.mapSettings.loadingFlag = true;
        $scope.mapSettings.drawMarkersFlag = !$scope.mapSettings.loadingFlag;
//console.log("Run refresh data");
        refreshCitiesData();
    }, false);
   
    //reset extended objects, when open popup
    $scope.$on('leafletDirectiveMarker.click', function(e, args){
        $scope.diffObjectData =[];
        $scope.newObjectData =[];
        $scope.newCityData = {};
        $scope.diffCityData = {};
//        $scope.markerSettings = {};
        $scope.diffData ={};
        $scope.newData = {};
    });
    //
    $rootScope.$on('objectSvc:loaded',function(){
        $scope.mapSettings.loadingFlag = objectSvc.getLoadingStatus();
    });
    
    //key down listener
    window.onkeydown = function(e){ 
        if ((e.keyCode == 27)){
            var openedPopup = document.getElementsByClassName("leaflet-pane leaflet-popup-pane");
            if (angular.isDefined(openedPopup)){
                openedPopup[0].innerHTML= "";
            };
            $scope.mapSettings.abortCompareFlag = true;
        };
    };
    
        //off listener, when go away from page
    $scope.$on('$destroy', function() {       
        window.onkeydown = undefined;
    }); 
    
    //control visibles
    var setVisibles = function(ctxId){
        var ctxFlag = false;
        var tmp = mainSvc.getContextIds();
        tmp.forEach(function(element){
            if(element.permissionTagId.localeCompare(ctxId)==0){
                ctxFlag = true;
            };
            var elDOM = document.getElementById(element.permissionTagId);//.style.display = "block";
            if (angular.isUndefined(elDOM)||(elDOM==null)){
                return;
            };              
            $('#'+element.permissionTagId).removeClass('nmc-hide');
        });
        if (ctxFlag == false){
            window.location.assign('#/');
        };
    };
    setVisibles($scope.mapSettings.ctxId);
    //listen change of service list
    $rootScope.$on('servicePermissions:loaded', function(){
        setVisibles($scope.mapSettings.ctxId);
    });
});