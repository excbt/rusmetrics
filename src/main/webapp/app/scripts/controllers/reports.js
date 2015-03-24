//reports controller
var app = angular.module('portalNMK');
app.controller('ReportsCtrl', function($scope){
                    



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