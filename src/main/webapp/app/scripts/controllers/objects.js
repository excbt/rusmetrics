'use strict';

angular.module('portalNMK')
.controller('ObjectsCtrl', function($scope, $rootScope, $http){
//console.log("loading ObjectsCtrl.");  
    var url = "../api/systemInfo/fullUserInfo";
    $http.get(url)
        .success(function(data, satus, headers, config){
            $rootScope.userInfo = data;
//console.log($rootScope.userInfo);        
        })
        .error();
//    $rootScope.userInfo = 
//     $scope.navPlayerDates = {
//                            startDate : moment().startOf('day'),
//                            endDate : moment().endOf('day'),
//                        };
//
//                    $scope.queryDateOptsRu = {
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
//                        $scope.queryDateOptsEn = {
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


//                        $scope.$watch('navPlayerDates', function (newDates) {
//console.log('New date set: ', newDates);
//                            $scope.reportStart = moment(newDates.startDate).format('YYYY-MM-DD');
//                            $scope.reportEnd = moment(newDates.endDate).format('YYYY-MM-DD');
                            //  $scope.getReport(newDates);                
//                        }, false);      
});