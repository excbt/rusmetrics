'use strict';
angular.module('portalNMC')
    .service('objectSvc', ['crudGridDataFactory', '$http', '$cookies', '$interval',
             function(crudGridDataFactory, $http, $cookies, $interval)
//             function()
             {
        var svcObjects = [{fullName:"yaa"
        }];
        var loading = true;
        var crudTableName = '../api/subscr/contObjects';
        var urlRefRange = '../api/subscr/contObjects/';
                //Функция для получения эталонного интервала для конкретной точки учета конкретного объекта
        var getRefRangeByObjectAndZpoint = function(object, zpoint){
            var url = urlRefRange + object.id + '/zpoints/' + zpoint.id + '/referencePeriod';                  
            return $http.get(url);
//            .success(function(data){
//                if(data[0] != null){
//                    var beginDate = new Date(data[0].periodBeginDate);
//                    var endDate =  new Date(data[0].periodEndDate);                                   
//                    zpoint.zpointRefRange = "c "+beginDate.toLocaleDateString()+" по "+endDate.toLocaleDateString();
//                    zpoint.zpointRefRangeAuto = data[0]._auto?"auto":"manual";
//                }
//                else {
//                    zpoint.zpointRefRange = "Не задан";
//                    zpoint.zpointRefRangeAuto = "notSet";
//                }
//            })
//            .error(function(e){
//                console.log(e);
//            });
        }
                 
        var zPointsByObject = [];
        var getZpointsDataByObject = function(obj, mode){ 
            obj.zpoints = [];
            var table = crudTableName+"/"+obj.id+"/contZPoints"+mode;//Ex";
            return $http.get(table);
        };         
        
        var getData = function () {
           return $http.get(crudTableName);
//               .success(function (data) {
//                var tmp = data;    
//                var curObjId = $cookies.contObject;                   
//                svcObjects = tmp;
//                if (cb) cb();
//            });
        };

       var promise = getData();
       $interval(function(){
           var time = (new Date()).toLocaleString();
//           document.getElementById('timeOutput').innerHTML="Время: "+time;
console.log(time);           
       },10000);
                 
        
                    
        return {
//            getObjects,
            loading,
            promise,
            getZpointsDataByObject,
            getRefRangeByObjectAndZpoint
        }
    
}]);