//'use strict';

/**
 * @ngdoc function
 * @name portalNMK.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the portalNMK
 */
angular.module('portalNMK')
  .controller('MainCtrl', ['$scope','$rootScope', function ($scope, $rootScope) {
    
    $scope.showPrivateOfficeMenu = false;
    $rootScope.showIndicatorsParam = false;
      
    //for indicators
//    $scope.contObjectId = $rootScope.contObjectId;
//    $scope.contZPointId = $rootScope.contZPointId; 
      
      $scope.timeDetailType = "1h";
                   
      $scope.endDate = "2014-03-20";//new Date();                 
      $scope.beginDate = "2014-03-19";//endDate;     
      //$scope.beginDate.setHours(0,0,0,0);
      
      $scope.zpointTable = "../api/subscr/"+$rootScope.contObject.id+"/service/"+$scope.timeDetailType+"/"+$rootScope.contZPoint.id+"?beginDate="+$scope.beginDate+"&endDate="+$scope.endDate;
      
      
    //end for indicators  
      
      
    $scope.reportStart= new Date();
                    $scope.reportEnd=new Date(2015, 03, 22);
                    $scope.welcome = "Вас обслуживает контролер отчетов.";
//                    $scope.setDateRange = function(){
//                        
//                                        $('input[name="daterange"]').daterangepicker();
//                    };

//                    $scope.openReport = function(){
//                        window.open("http://ya.ru");
//                        alert("Дата начала = "+$scope.reportStart+"; Дата завершения"+$scope.reportEnd);
//                    } ;
    
                    $scope.navPlayerDates = {
                            startDate : moment().startOf('day'),
                            endDate : moment().endOf('day'),
                        };

                    $scope.queryDateOptsRu = {
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
                                startDate : moment().startOf('day'),
                                endDate : moment().endOf('day'),

                                format : 'DD-MM-YYYY'
                            };

                        $scope.queryDateOptsEn = {
                                locale : {
                                    applyClass : 'btn-green',
                                },
                                ranges : {
                                    'Today' : [ moment().startOf('day'),
                                            moment().endOf('day') ],
                                    'Last 7 days' : [
                                            moment().subtract(6, 'days')
                                                    .startOf('day'),
                                            moment().endOf('day') ],
                                    'Last 30 days' : [
                                            moment().subtract(29, 'days')
                                                    .startOf('day'),
                                            moment().endOf('day') ]
                                },
                                startDate : moment().startOf('day'),
                                endDate : moment().endOf('day'),

                                format : 'DD-MM-YYYY'
                        };


                        $scope.$watch('navPlayerDates', function (newDates) {
console.log('New date set: ', newDates);
                            $scope.reportStart = moment(newDates.startDate).format('YYYY-MM-DD');
                            $scope.reportEnd = moment(newDates.endDate).format('YYYY-MM-DD');
                            //  $scope.getReport(newDates);                
                        }, false);      
    
  }]);
