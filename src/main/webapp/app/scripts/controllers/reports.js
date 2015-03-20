//reports controller
var app = angular.module('portalNMK');
app.controller('ReportsCtrl', function($scope){
                    $scope.reportStart= new Date();
                    $scope.reportEnd=new Date(2015, 03, 22);
                    $scope.welcome = "Вас обслуживает контролер отчетов.";
                    $scope.setDateRange = function(){
                        
                                        $('input[name="daterange"]').daterangepicker();
                    };

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



//                        $scope.getReport = function(queryDates) {
//
//                            var sessDateFrom = moment(queryDates.startDate).format('YYYY-MM-DD');
//                            var sessDateTill = moment(queryDates.endDate).format('YYYY-MM-DD');
//
//                            $scope.loading = true;
//                            
//                            $scope.invokeReport().query({beginDate: sessDateFrom, endDate:sessDateTill}
//                                                        ,function(data){
//                                                            
//                                                          }
//                                                       );

//                            terminalDataFactory("sessDateTerminalIdsPeriod").query({
//                                sessDateFrom : sessDateFrom,
//                                sessDateTill : sessDateTill
//                            }, function(data) {
//
//                                $scope.setTerminals(data);
//
//                                console.log("data:" + data);
//                                $scope.loading = false;                    
//
//                            }, errorCallback);
//                        };
    
//                        $scope.invokeReport = function(type) {
//                            return $resource(type, {beginDate: '@beginDate', endDate: '@endDate' 
//                            }, {
//
//                                query: {method: 'GET', isArray: false}
//
//                            });
//			             };   

                    
                });