'use strict';
angular.module('portalNMC')
    .service('objectSvc', ['crudGridDataFactory', '$http', '$cookies',
             function(crudGridDataFactory, $http, $cookies)
//             function()
             {
        var svcObjects = [{fullName:"yaa"
        }];
        var loading = true;
        var crudTableName = '../api/subscr/contObjects';
        var urlRefRange = '../api/subscr/contObjects/';
                //Функция для получения эталонного интервала для конкретной точки учета конкретного объекта
        function getRefRangeByObjectAndZpoint(object, zpoint){
            var url = urlRefRange + object.id + '/zpoints/' + zpoint.id + '/referencePeriod';                  
            $http.get(url)
            .success(function(data){
                if(data[0] != null){
                    var beginDate = new Date(data[0].periodBeginDate);
                    var endDate =  new Date(data[0].periodEndDate);                                   
                    zpoint.zpointRefRange = "c "+beginDate.toLocaleDateString()+" по "+endDate.toLocaleDateString();
                    zpoint.zpointRefRangeAuto = data[0]._auto?"auto":"manual";
                }
                else {
                    zpoint.zpointRefRange = "Не задан";
                    zpoint.zpointRefRangeAuto = "notSet";
                }
            })
            .error(function(e){
                console.log(e);
            });
        }
                 
        var zPointsByObject = [];
        var getZpointsDataByObject = function(obj, mode){ 
            obj.zpoints = [];
            var table = crudTableName+"/"+obj.id+"/contZPoints"+mode;//Ex";
            return $http.get(table);
//            crudGridDataFactory(table).query(function (data) {
//                var tmp = [];
//                if (mode == "Ex"){
//                    tmp = data.map(function(el){
//                        var result = {};
//                        result = el.object;
//                        result.lastDataDate = el.lastDataDate;
//                        return result;
//                    });
//                }else{
//                    tmp = data;
//                };
//                zPointsByObject = tmp;
//                var zpoints = [];
//                for(var i=0;i<zPointsByObject.length;i++){
//                    var zpoint = {};
//                    zpoint.id = zPointsByObject[i].id;
//                    zpoint.zpointType = zPointsByObject[i].contServiceType.keyname;
//                    zpoint.zpointName = zPointsByObject[i].customServiceName;
//                    if ((typeof zPointsByObject[i].rso != 'undefined') && (zPointsByObject[i].rso!=null)){
//                        zpoint.zpointRSO = zPointsByObject[i].rso.organizationFullName || zPointsByObject[i].rso.organizationName;
//                    }else{
//                        zpoint.zpointRSO = "Не задано"
//                    };
//                    zpoint.checkoutTime = zPointsByObject[i].checkoutTime;
//                    zpoint.checkoutDay = zPointsByObject[i].checkoutDay;
//                    if(typeof zPointsByObject[i].doublePipe == 'undefined'){
//                        zpoint.piped = false;
//
//                    }else {
//                        zpoint.piped = true;
//                        zpoint.doublePipe = zPointsByObject[i].doublePipe;
//                        zpoint.singlePipe = !zpoint.doublePipe;
//                    };
//                    if ((typeof zPointsByObject[i].deviceObjects != 'undefined') && (zPointsByObject[i].deviceObjects.length>0)){                                
//                        if (zPointsByObject[i].deviceObjects[0].hasOwnProperty('deviceModel')){
//                            zpoint.zpointModel = zPointsByObject[i].deviceObjects[0].deviceModel.modelName;
//                        }else{
//                            zpoint.zpointModel = "Не задано";
//                        };
//                        zpoint.zpointNumber = zPointsByObject[i].deviceObjects[0].number;
//                    };
//                    zpoint.zpointLastDataDate  = zPointsByObject[i].lastDataDate;   
                    // Получаем эталонный интервал для точки учета
//                    getRefRangeByObjectAndZpoint(obj, zpoint);
//                    zpoints[i] = zpoint;                  
//                }
//                obj.zpoints = zpoints;

//            });
        };         
        
        var getData = function (cb) {
           return $http.get(crudTableName).success(function (data) {
                var tmp = data;    
                var curObjId = $cookies.contObject;                   
//                for (var i=0; i<tmp.length; i++){                                                    
//                    getZpointsDataByObject(tmp[i], "Ex");                            
//                    if (tmp[i].id == curObjId){tmp[i].showGroupDetails=true};
//                }
                svcObjects = tmp;
                if (cb) cb();
            });
        };

       var promise = getData(
            function () {
                loading = false;
        });
                 
//        var getObjects = function(){
//            return objects;
//        };
//console.log(promise);    
        return {
//            getObjects,
            loading,
            promise,
            getZpointsDataByObject
        }
    
}]);