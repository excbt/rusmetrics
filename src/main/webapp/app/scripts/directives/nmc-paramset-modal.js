/*jslint node: true, white: true*/
/*global angular, $*/
'use strict';
angular.module('portalNMC')
.directive('nmcParamsetModal', function () {
    return {
        replace: true,
        templateUrl: "scripts/directives/templates/nmc-paramset-modal.html",
        controller: function ($scope, mainSvc) {
            
            function setPropForSpecDate() {
                $scope.currentParamSpecialList.forEach( function (cps) {
                    $('#inputSpecialDate' + cps.id).datepicker({
                    //$('.nmc-spec-date').datepicker({
                      dateFormat: "dd.mm.yy",
                      firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
                      dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
                      monthNames: $scope.dateOptsParamsetRu.locale.monthNames,
                        beforeShow: function () {
                            setTimeout(function () {
                                $('.ui-datepicker-calendar').css("display", "table");
                            }, 1);
                        },
                        onChangeMonthYear: function () {
                            setTimeout(function () {
                                $('.ui-datepicker-calendar').css("display", "table");
                            }, 1);
                        }
                    });
                });
            }
            
            var setPropForSettlementMonth = function () {
                $('#inputReportSettlementMonth').datepicker({
                  dateFormat: "MM, yy",
                  firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
                  dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
                  monthNames: $scope.dateOptsParamsetRu.locale.monthNames,
                    monthNamesShort: ['Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн', 'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек'],
                    changeMonth: true,
                    changeYear: true,
                    showButtonPanel: true,
                    closeText: "Ок",
                    currentText: "",
                    onClose: function(dateText, inst) {
                        $(this).datepicker('setDate', new Date(inst.selectedYear, inst.selectedMonth, 1));
                        $scope.currentObject.settlementMonth = inst.selectedMonth + 1;
                        $scope.currentObject.settlementYear = inst.selectedYear;
                        setTimeout(function () {
                            $('.ui-datepicker-calendar').addClass("nmc-hide");
                        }, 1);
                    },
                    beforeShow: function () {
                        setTimeout(function () {
                            $('.ui-datepicker-calendar').addClass("nmc-hide");
                            $('.ui-datepicker-current').addClass("nmc-hide");
                        }, 1);
                    },
                    onChangeMonthYear: function () {
                        setTimeout(function () {
                            $('.ui-datepicker-current').addClass("nmc-hide");
                            $('.ui-datepicker-calendar').addClass("nmc-hide");
                        }, 1);
                    }
                });

                $('#inputReportSettlementMonth').datepicker('setDate', new Date($scope.currentObject.settlementYear, $scope.currentObject.settlementMonth - 1, 1));
            }; 
            var setPropForStartDate = function () {
                $('#inputSingleDateStart').datepicker({
                      dateFormat: "dd.mm.yy",
                      firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
                      dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
                      monthNames: $scope.dateOptsParamsetRu.locale.monthNames,
                        beforeShow: function () {
                            setTimeout(function () {
                                $('.ui-datepicker-calendar').css("display", "table");
                            }, 1);
                        },
                      onChangeMonthYear: function () {
                            setTimeout(function () {
                                $('.ui-datepicker-calendar').css("display", "table");
                            }, 1);
                        }
                  });
            };
            var setPropForEndDate = function () {
                $('#inputSingleDateEnd').datepicker({
                  dateFormat: "dd.mm.yy",
                  firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
                  dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
                  monthNames: $scope.dateOptsParamsetRu.locale.monthNames,
                    beforeShow: function () {
                        setTimeout(function () {
                            $('.ui-datepicker-calendar').css("display", "table");
                        }, 1);
                    },
                  onChangeMonthYear: function () {
                        setTimeout(function () {
                            $('.ui-datepicker-calendar').css("display", "table");
                        }, 1);
                    }
                });
            };
            var setPropForSingleDate = function () {
              $('#inputStartDate').datepicker({
                  dateFormat: "dd.mm.yy",
                  firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
                  dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
                  monthNames: $scope.dateOptsParamsetRu.locale.monthNames,
                    beforeShow: function () {
                        setTimeout(function () {
                            $('.ui-datepicker-calendar').css("display", "table");
                        }, 1);
                    },
                  onChangeMonthYear: function () {
                        setTimeout(function () {
                            $('.ui-datepicker-calendar').css("display", "table");
                        }, 1);
                    }
              });
            };
            
            $('#editParamsetModal').on('shown.bs.modal', function () {                
                if (Number($scope.currentObject.settlementDay) > 3 && Number($scope.currentObject.settlementDay) <= 9){
                    $scope.currentObject.settlementDay = "0" + $scope.currentObject.settlementDay;
                    $scope.$apply();
                }
                $("#inputFileTemplate").inputmask('Regex', { regex: "[a-zA-Z0-9]+"} );
                $("#inputReportSettlementDay").inputmask("d", {placeholder: ""});
                setPropForSettlementMonth();
                setPropForStartDate();
                setPropForEndDate();
                setPropForSingleDate();
                setPropForSpecDate();
            });

            $('#editParamsetModal').on('hidden.bs.modal', function () {
                $scope.createReportWithParamsRequestCancel();
                $scope.createReportWithParamsInProgress = false;
            	
            });
            
            $scope.$watch('currentObject.reportPeriodKey', function (newKey) {
                //отслеживаем изменение периода у варианта отчета
                if (mainSvc.checkUndefinedNull($scope.reportPeriods)) {
                    console.log($scope.reportPeriods);
                    return "reportPeriods is undefined or null.";
                }
                var i;
                for (i = 0; i < $scope.reportPeriods.length; i += 1) {
                    if (newKey == $scope.reportPeriods[i].keyname) {
                        $scope.currentSign = $scope.reportPeriods[i].sign;
                        $scope.currentReportPeriod = $scope.reportPeriods[i];
                        if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')) {
                            $scope.paramsetStartDateFormat = ($scope.currentObject.paramsetStartDate == null) ? null : (new Date($scope.currentObject.paramsetStartDate));
                            $scope.paramsetEndDateFormat = ($scope.currentObject.paramsetEndDate == null) ? null : (new Date($scope.currentObject.paramsetEndDate));
                        }
                    }
                }
                if (!mainSvc.checkUndefinedNull($scope.currentReportPeriod) && $scope.currentReportPeriod.isSettlementMonth == true) {
                    setPropForSettlementMonth();
                }
            }, false);
            
            //**********************************************************
            //Creation report window
            //**********************************************************
                        
            $('#creationReportWindow').on('hidden.bs.modal', function(){
                $scope.createReportWithParamsRequestCancel();
//                $scope.createReportCancel();
                $scope.createReportWithParamsInProgress = false;
            	
            });
        }
    };
});