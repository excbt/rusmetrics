//'use strict';

/**
 * @ngdoc function
 * @name portalNMK.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the portalNMK
 */
var app = angular.module('portalNMK');
  app.controller('MainCtrl', ['$scope','$rootScope', 'crudGridDataFactory', function ($scope, $rootScope, crudGridDataFactory) {
    
//    $scope.$on('IdleEnd', function() {
//        window.location.assign('/logout');
//    });
    $scope.showPrivateOfficeMenu = false;
    $rootScope.showIndicatorsParam = false;
      
    $scope.setMenuVisibles = function(privateOfficeMenuVisible, indicatorsParamVisible){
        $scope.showPrivateOfficeMenu = privateOfficeMenuVisible;
        $rootScope.showIndicatorsParam = indicatorsParamVisible;
    };  
      
//      $scope.getIndicators(){
//          window.location.replace("#/objects/indicators/");
//      };
      
    //for indicators
      
       $rootScope.timeDetailType =  "1h";//$scope.timeDetailType;

       // $rootScope.endDate = ""; //"2014-03-20";//new Date();                 
       //   $rootScope.beginDate ="";// "2014-03-19";//endDate;  

      
    //end for indicators  
      
      //report
//                    $rootScope.reportStart= new Date();
//                    $rootScope.reportEnd=new Date(2015, 03, 22);
//                    $scope.welcome = "Вас обслуживает контролер отчетов.";
//                    $scope.setDateRange = function(){
//                        
//                                        $('input[name="daterange"]').daterangepicker();
//                    };

//                    $scope.openReport = function(){
//                        window.open("http://ya.ru");
//                        alert("Дата начала = "+$scope.reportStart+"; Дата завершения"+$scope.reportEnd);
//                    } ;
    
//                    $rootScope.navPlayerDates = {
//                            startDate : moment().startOf('day'),
//                            endDate : moment().endOf('day'),
//                        };
//
//                    $rootScope.queryDateOptsRu = {
//                                locale : {
//                                    applyClass : 'btn-green',
//                                    applyLabel : "Применить",
//                                    fromLabel : "с",
//                                    toLabel : "по",
//                                    cancelLabel : 'Отмена',
//                                    customRangeLabel : 'Период',
//                                    daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
//                                    firstDay : 1,
//                                    monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
//                                            'Май', 'Июнь', 'Июль', 'Август', 'Сентабрь',
//                                            'Октябрь', 'Ноябрь', 'Декабрь' ]
//                                },
//                                ranges : {
//                                    'Текущий день' : [ moment().startOf('day'),
//                                            moment().endOf('day') ],
//                                    'Посл 7 дней' : [
//                                            moment().subtract(6, 'days').startOf('day'),
//                                            moment().endOf('day') ],
//                                    'Посл 30 дней' : [
//                                            moment().subtract(29, 'days').startOf('day'),
//                                            moment().endOf('day') ]
//                                },
//                                startDate : moment().startOf('day'),
//                                endDate : moment().endOf('day'),
//
//                                format : 'DD-MM-YYYY'
//                            };
//
//                        $rootScope.queryDateOptsEn = {
//                                locale : {
//                                    applyClass : 'btn-green',
//                                },
//                                ranges : {
//                                    'Today' : [ moment().startOf('day'),
//                                            moment().endOf('day') ],
//                                    'Last 7 days' : [
//                                            moment().subtract(6, 'days')
//                                                    .startOf('day'),
//                                            moment().endOf('day') ],
//                                    'Last 30 days' : [
//                                            moment().subtract(29, 'days')
//                                                    .startOf('day'),
//                                            moment().endOf('day') ]
//                                },
//                                startDate : moment().startOf('day'),
//                                endDate : moment().endOf('day'),
//
//                                format : 'DD-MM-YYYY'
//                        };


//                        $rootScope.$watch('navPlayerDates', function (newDates) {
//console.log("newDates[]= "+newDates);
//for (var k in newDates){                            
//    console.log("newDates["+k+"]= "+newDates[k]);
//};
//                            $rootScope.reportStart = moment(newDates.startDate).format('YYYY-MM-DD');
//                            $rootScope.reportEnd = moment(newDates.endDate).format('YYYY-MM-DD');
//console.log("This change");                            
                            //  $scope.getReport(newDates);                
//                        }, false);
      
                
//                $scope.notices = [];            
//               
//
//                    $scope.getNoticesByObject = function(tableName, object){
//                        var tempArr = [];
//                         var noticeIter = 0;
//                        var table = tableName+"/"+object.id+"/events";
//                        crudGridDataFactory(table).query(function (data) {
//                                                        $scope.noticesByObject = data;
//                            
//console.log("TableName = "+table);
//
//                                                        for(var j=0;j<$scope.noticesByObject.length;j++){                                                                            
//                                                      
//                                                            
//                                                            var notice = {};
//                                                            notice.noticeType = $scope.noticesByObject[j].contEventType.name;
//                                                            notice.noticeText = $scope.noticesByObject[j].message;
//                                                            notice.noticeDate = new Date($scope.noticesByObject[j].eventTime);
//                                                            notice.noticeObject = object.fullName;
//                                                            notice.noticeZpoint  = $scope.noticesByObject[j].contServiceType;   
//                                                            tempArr[noticeIter] = notice;
//                                                            noticeIter=noticeIter+1;
//                                                        }
//                            
//                                                        $scope.notices = tempArr;
//                        });
//                        
//                       
//                    
//                };  
    
//  
//     function generateRows( ) {
//       // var rows = [];
//         var object = {"id": "18811505", "fullName": "Какой-то объект"};
//        var tableName = "../api/subscr/contObjects";// + object.id+ "/events";
//
//          $scope.getNoticesByObject(tableName, object);
//        //return rows;
//      }
//
//        generateRows();
//
//      $scope.cols = [ 'Тип', 'Сообщение', 'Дата', 'Объект', 'Точка учета'];
//       $scope.table = {
//          cols: [ 'Тип', 'Сообщение', 'Email', 'Twitter', 'Id', 'Modified' ],
//          rows: $scope.notices
//        };
//                    
      
    
  }]);


///////////////////////////////test

//angular.module('portalNMK').
//factory('DTLoadingTemplate', dtLoadingTemplate);
//function dtLoadingTemplate() {
//    return {
//        html: '<h3>Загрузка...</h3>'
//    };
//}


angular.module('portalNMK').controller('WithPromiseCtrl', WithPromiseCtrl);

function WithPromiseCtrl(DTOptionsBuilder, DTColumnBuilder, $resource) {
    var crudTableName= "../api/subscr/contObjects";
    
    var vm = this;
    vm.dtOptions = DTOptionsBuilder
//    .fromSource(crudTableName)
    .fromFnPromise(function() {
        var table =  crudTableName+"/events";
          return $resource(crudTableName).query().$promise;
    })
    .withPaginationType('full_numbers')
    .withLanguageSource('vendor_components/DataTables-1.10.6/plugins/i18n/Russian.json')
//    .withOption('fnRowCallback', rowCallback)
//    .withOption('fnPreDrawCallback', preDrawCallback)
//    .withOption('fnInitComplete', initComplete)
    //.withDataProp('{}.contManagements');
    .withFnServerData(serverData)
    ;
    
    function serverData(sSource, aoData, fnCallback, oSettings) {
console.log("In serverData");        
for(var k in sSource){
    console.log("sSource["+k+"]= "+sSource[k]);
};         
//        oSettings.jqXHR = $.ajax({
//            'dataType': 'json',
//            'type': 'POST',
//            'url': sSource,
//            'data': aoData,
//            'success': fnCallback
//        });
    }
    
    function rowCallback(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
        console.log(aData);
        return nRow;
    }
    
    function preDrawCallback(oSettings) {
for(var k in oSettings){
    console.log("oSettings["+k+"]= "+oSettings[k]);
};             
    }
    
    function initComplete(oSettings, json) {
for(var k in oSettings){
    console.log("oSettings["+k+"]= "+oSettings[k]);
};  
console.log("JSON= "+json);        
    }    

    vm.dtColumns = [
//        DTColumnBuilder.newColumn('id').withTitle('id').notVisible(),
//        DTColumnBuilder.newColumn('contObjectId').withTitle('Тип'),
//        DTColumnBuilder.newColumn('message').withTitle('Уведомление')
//        ,DTColumnBuilder.newColumn('noticeDate').withTitle('Дата') 
        DTColumnBuilder.newColumn('id').withTitle('id').notVisible(),
        DTColumnBuilder.newColumn('fullName').withTitle('Название').renderWith(function(data, type, full){
            return full.fullName + "Я влез и изменил содержимое колонки";
        }),
        DTColumnBuilder.newColumn('fullAddress').withTitle('Адрес')
    ];
}
////////////////////////////////// end test


