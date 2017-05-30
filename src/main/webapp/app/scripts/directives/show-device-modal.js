/*jslint node: true, eqeq: true */
/*global angular, $*/
'use strict';
var app = angular.module('portalNMC');
app.directive('nmcShowDeviceModal', function () {
    return {
        restrict: "AE",
        replace: false,
        scope: {
            currentDevice: "=",
            deviceSources: "=",
            deviceModels: "=",
            contObjects: "=",
            heaterTypes: "=",
            
            readOnly: "@",
            btnClick: "&",
            btnOkCaption: "@",
            showOkButton: "@"
        },
        templateUrl: 'scripts/directives/templates/show-device-modal.html',
        controller: ['$scope', 'mainSvc', '$cookies', 'objectSvc', '$timeout', function ($scope, mainSvc, $cookies, objectSvc, $timeout) {
            
            $scope.dirData = {};
            $scope.dirData.deviceInstTypes = objectSvc.getDeviceInstTypes();
            
            var deviceModalWindowTabs = [
                {
                    name: "main_properties_tab",
                    tabpanel: "main_properties"
                },
                {
                    name: "con_properties_tab",
                    tabpanel: "con_properties"
                },
                {
                    name: "time_properties_tab",
                    tabpanel: "time_properties"
                }
            ];

            function setActivePropertiesTab(tabName) {
                deviceModalWindowTabs.forEach(function (tabElem) {
                    var tab, tabPanel;
                    tab = document.getElementById(tabElem.name) || null;
                    tabPanel = document.getElementById(tabElem.tabpanel) || null;
                    if (tabElem.name.localeCompare(tabName) !== 0) {
                        tab.classList.remove("active");
                        tabPanel.classList.remove("active");
                    } else {
                        tab.classList.add("active");
                        tabPanel.classList.add("in");
                        tabPanel.classList.add("active");
                    }
                });


            }
            
            function setInputmask() {
                $timeout(function () {
                    $('#inputHeaterPower').inputmask();
                });
            }
            
            $scope.isDeviceDisabled = function (device) {
//console.log(device);                
//console.log($scope.readOnly === 'true' || device.exSystemKeyname === 'VZLET' || device.exSystemKeyname === 'LERS' || (device.isSaving === true));
                return $scope.readOnly === 'true' || device.exSystemKeyname === 'VZLET' || device.exSystemKeyname === 'LERS' || (device.isSaving === true);
            };
            
            $scope.isAdminCanEdit = function (device) {
                return mainSvc.isAdmin() && !(device.exSystemKeyname === 'VZLET' || device.exSystemKeyname === 'LERS' || (device.isSaving === true));
            };
            
            $scope.isDeviceDataSourceHide = function (device) {
                return device.exSystemKeyname === 'VZLET' || device.exSystemKeyname === 'LERS' || (device.isSaving === true);
            };
            
            $scope.isDeviceProtoLoaded = function (device) {
                return device.exSystemKeyname === 'VZLET' || device.exSystemKeyname === 'LERS';
            };
            
            $scope.changeDeviceModel = function () {
//console.log("changeDeviceModel");                
                if (!mainSvc.checkUndefinedNull($scope.currentDevice.deviceModelId)) {
                    $cookies.recentDeviceModelId = $scope.currentDevice.deviceModelId;
                }
            };
            
            $scope.deviceDatasourceChange = function () {
                $scope.currentDevice.dataSourceTable1h = null;
                $scope.currentDevice.dataSourceTable24h = null;
                $scope.currentDevice.subscrDataSourceAddr = null;
                var curDataSource = null;
                $scope.deviceSources.some(function (source) {
                    if (source.id === $scope.currentDevice.subscrDataSourceId) {
                        curDataSource = source;
                        return true;
                    }
                });
                $scope.currentDevice.curDatasource = curDataSource;

                if (!mainSvc.checkUndefinedNull($scope.currentDevice.subscrDataSourceId)) {
                    $cookies.recentDataSourceId = $scope.currentDevice.subscrDataSourceId;
                }
            };
            
            $scope.isAvailableConPropertiesTab = function () {
//console.log($scope.currentDevice);                
                if (mainSvc.checkUndefinedNull($scope.currentDevice) || mainSvc.checkUndefinedNull($scope.currentDevice.curDatasource)) {
                    return false;
                }
                return $scope.currentDevice.curDatasource.dataSourceType.isRaw === true || $scope.currentDevice.curDatasource.dataSourceType.isDbTablePair === true ||
                    $scope.currentDevice.exSystemKeyname === 'VZLET';
            };
            
            $scope.deviceIsSpreader = function () {
                return (!mainSvc.checkUndefinedNull($scope.dirData.currentModel) && !mainSvc.checkUndefinedNull($scope.dirData.currentModel.deviceType) && $scope.dirData.currentModel.deviceType === objectSvc.HEAT_DISTRIBUTOR);
            };
            
            $scope.recentHeaterGrouping = function (item) {
                var rhTypes = objectSvc.getRecentHeaterTypes();
                return rhTypes.indexOf(item.id) > -1 ? "Недавно использованные" : undefined;
            };
            
            //date picker
            $scope.dateOptsParamsetRu = {
                locale : {
                    daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
                    firstDay : 1,
                    monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                            'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                            'Октябрь', 'Ноябрь', 'Декабрь' ]
                },
                singleDatePicker: true
            };
            
            $('#showDeviceModal').on('shown.bs.modal', function () {
                
                $scope.dirData.currentModel = mainSvc.findItemBy($scope.deviceModels, 'id', $scope.currentDevice.deviceModelId);
                $scope.currentDevice.subscrDataSourceId = Number($scope.currentDevice.activeDataSource.subscrDataSource.id);
                $scope.$apply();
                
                setInputmask();
                $('#inputVerificationInterval').inputmask();
                $('#inputVerificationDate').datepicker({
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
//                console.log($scope.currentDevice);
//                console.log($scope.deviceSources);
//                
//                console.log($scope.deviceModels);
//                console.log($scope.readOnly);
//                console.log($scope.isDeviceDisabled($scope.currentDevice));
            });
            
            $('#showDeviceModal').on('hidden.bs.modal', function () {
                setActivePropertiesTab("main_properties_tab");
            });
            
        }]
    };
});