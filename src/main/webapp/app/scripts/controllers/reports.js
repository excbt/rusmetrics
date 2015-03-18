//reports controller
var app = angular.module('portalNMK');
app.controller('ReportsCtrl', function($scope){
                    $scope.welcome = "Вас обслуживает контролер отчетов.";
                    $scope.setDateRange = function(){
                        
                                        $('input[name="daterange"]').daterangepicker();
                    };
                    
                });