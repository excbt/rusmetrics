/*global angular, $, console*/
(function () {
    'use strict';

    angular
        .module('portalNMC')
        .directive('showZpointOptionsModal', directive);

    directive.$inject = [];

    /* @ngInject */
    function  directive() {
        var directive = {
            bindToController: true,
            controller: Controller,
            controllerAs: 'vm',
            templateUrl: 'scripts/directives/templates/show-zpoint-options-modal.html',
            link: link,
            restrict: 'A',
            scope: {
                contObjectId: "@",
                contZpointId: "@"
            }
        };
        return directive;

        function link(scope, element, attrs, controller) {
            
        }
    }

    Controller.$inject = ['$scope', 'objectSvc', '$cookies', 'mainSvc', 'notificationFactory', '$http', '$rootScope', 'dateTimeSvc'];

    /* @ngInject */
    function Controller($scope, objectSvc, $cookies, mainSvc, notificationFactory, $http, $rootScope, dateTimeSvc) {
        var vmc = this;
        var EVENTS = {};
        EVENTS.DEVICE_LOADED = 'deviceLoaded';
        EVENTS.ZPOINT_LOADED = 'zpointLoaded';
            
        
        vmc.loading = true;
        vmc.contObject = null;
        vmc.contZpoint = null;
        vmc.serviceTypes = null;
        vmc.rsoOrganizationsLoading = true;
        
        function errorCallback(e) {
            console.log(e);
            vmc.loading = false;
        }
        
        function successLoadZpointCallback(resp) {
            vmc.loading = false;
            vmc.contZpoint = resp.data;
            vmc.setTemperatureChartToZpoint(vmc.contZpoint, vmc.contObject.tempSchedules);
            $scope.$broadcast(EVENTS.ZPOINT_LOADED);
        }
        
        
        function loadZpoint(contObjectId, contZpointId) {
            objectSvc.loadZpointById(contObjectId, contZpointId)
                .then(successLoadZpointCallback)
                .catch(errorCallback);
        }
        
        vmc.checkUndefinedNull = mainSvc.checkUndefinedNull;
        
        function successLoadContObjectSchedulesCallback() {
            
            //define: create new Zpoint or update exists
            if (vmc.checkUndefinedNull(vmc.contZpointId) || vmc.emptyString(vmc.contZpointId)) {
                //create new zpoint
                var contZpoint = {};
                //set recent service type
                if (!vmc.checkUndefinedNull($cookies.get('recentContServiceTypeKeyname'))) {
                    contZpoint.contServiceTypeKeyname = $cookies.get('recentContServiceTypeKeyname');
                    vmc.changeServiceType(contZpoint);
                }
                //set recent rso
                if (!vmc.checkUndefinedNull($cookies.get('recentRsoId'))) {
                    contZpoint.rsoId = Number($cookies.get('recentRsoId'));
                }
                vmc.contZpoint = contZpoint;
            } else {
                //load exists                
                loadZpoint(vmc.contObjectId, vmc.contZpointId);
            }
        }
        
        //        var loadTemperatureSchedulesByObjectForZpoint = function (objId, zp) {
        var loadTemperatureSchedulesByObject = function (contObject) {
            contObject.tempSchedulesLoading = true;
            objectSvc.loadTemperatureSchedulesByObject(contObject.id)
                .then(function (resp) {
                    contObject.tempSchedules = resp.data;                    
                    contObject.tempSchedulesLoading = false;
                    successLoadContObjectSchedulesCallback();
                }, function (e) {
                    errorCallback(e);
                    contObject.tempSchedulesLoading = false;
                });
        };
        
        vmc.setTemperatureChartToZpoint = function (zp, tempSchedules) {
            if (vmc.checkUndefinedNull(zp) || vmc.checkUndefinedNull(tempSchedules)) {
                return false;
            }
            if (vmc.checkUndefinedNull(zp.temperatureChartId)) {
                return "temperatureChartId is null";
            }
            tempSchedules.some(function (sch) {
                if (sch.id == zp.temperatureChartId) {
                    zp.tChart = sch;
                    return true;
                }
            });
        };
        
        function successLoadContObjectCallback(resp) {            
            vmc.contObject = resp.data;
            vmc.getDevices(vmc.contObject);
            loadTemperatureSchedulesByObject(vmc.contObject);
        }
        
        function loadContObject(contObjectId) {
            objectSvc.getObject(contObjectId)
                .then(successLoadContObjectCallback)
                .catch(errorCallback);
        }
        
        
        
        function loadServiceTypes() {
            objectSvc.getServiceTypes()
                .then(function (response) {
                    vmc.serviceTypes = response.data;        
                });
        }
        
        vmc.emptyString = mainSvc.checkUndefinedEmptyNullValue;
        
        vmc.changeServiceType = function (zpSettings) {
            if (!vmc.checkUndefinedNull(zpSettings.contServiceTypeKeyname)) {
//                $cookies.recentContServiceTypeKeyname = zpSettings.contServiceTypeKeyname;
                $cookies.put('recentContServiceTypeKeyname', zpSettings.contServiceTypeKeyname);
            }
            if (vmc.emptyString(zpSettings.customServiceName)) {
                switch (zpSettings.contServiceTypeKeyname) {
                case "heat":
                    zpSettings.customServiceName = "Система отопления";
                    break;
                default:
                    vmc.serviceTypes.some(function (svType) {
                        if (svType.keyname == zpSettings.contServiceTypeKeyname) {
                            zpSettings.customServiceName = svType.caption;
                            return true;
                        }
                    });

                }
            }
        };
        
        vmc.setDeviceToZpoint = function () {
            var obj = vmc.contObject,
                zp = vmc.contZpoint;
            if (vmc.checkUndefinedNull(zp) || vmc.checkUndefinedNull(obj) || !vmc.checkUndefinedNull(zp.id)) {
                return false;
            }
            if (!vmc.checkUndefinedNull(obj.devices) && obj.devices.length > 0 && vmc.checkUndefinedNull(zp.deviceObjectId)) {
                zp.deviceObjectId = obj.devices[0].id;                    
            }
        };
        
        vmc.getDevices = function (obj) {
            obj.devicesLoading = true;
            objectSvc.getDevicesByObject(obj).then(
                function (response) {
                    
                    var tmpArr = response.data;
                    tmpArr.forEach(function (elem) {

                        var tmpDevCaption = elem.deviceModel.modelName || "";
                        tmpDevCaption += elem.number ? ", №" + elem.number : "";
                        elem.devCaption = tmpDevCaption;
                    });
                    obj.devices = tmpArr;//response.data;                    
                    
                    obj.devicesLoading = false;
                    $scope.$broadcast(EVENTS.DEVICE_LOADED);
                }, function (e) {
                    errorCallback(e); 
                    obj.devicesLoading = false;
                }
            );
        };
        
        function performDeviceArchive(devArr) {
            devArr.forEach(function (elm) {
                elm.startDateStr = dateTimeSvc.dateFormating(elm.startDate);
                elm.endDateStr = dateTimeSvc.dateFormating(elm.endDate);
            });
        }
        
        vmc.viewDeviceArchive = function (zpSettings) {
            if (vmc.checkUndefinedNull(zpSettings) || vmc.checkUndefinedNull(zpSettings.id)) {
                return false;
            }
            if (!vmc.checkUndefinedNull(zpSettings.showDeviceArchive)) {
                zpSettings.showDeviceArchive = !zpSettings.showDeviceArchive;
            } else {
                zpSettings.showDeviceArchive = true;
            }
            zpSettings.deviceArchiveLoading = true;
            objectSvc.loadZpointDeviceArchive(zpSettings.id)
                .then(function (resp) {
                       zpSettings.deviceArchive = resp.data;
                       performDeviceArchive(zpSettings.deviceArchive);
                        zpSettings.deviceArchiveLoading = false;
                    }, 
                      function (e) {
                        zpSettings.deviceArchiveLoading = false;
                        errorCallback(e);
                    });
        };
        
        var getRsoOrganizations = function () {
            vmc.rsoOrganizationsLoading = true;
            objectSvc.getRsoOrganizations()
                .then(function (response) {
                    vmc.rsoOrganizations = response.data;
                    vmc.rsoOrganizationsLoading = false;
                })
                .catch(function (e) {
                    errorCallback(e);
                    vmc.rsoOrganizationsLoading = false;
                });
        };
        
        vmc.changeRso = function (zpSettings) {
            if (!vmc.checkUndefinedNull(zpSettings.rsoId)) {
                $cookies.put('recentRsoId', zpSettings.rsoId);
            }
        };

        var checkZpointCommonSettings = function () {
            //check name, type, rso, device
            var result = true;
            if (vmc.emptyString(vmc.contZpoint.customServiceName)) {
                notificationFactory.errorInfo("Ошибка", "Не задано наименование точки учета!");
                result = false;
            }
            if (vmc.emptyString(vmc.contZpoint.contServiceTypeKeyname)) {
                notificationFactory.errorInfo("Ошибка", "Не задан тип точки учета!");
                result = false;
            }
            if (vmc.checkUndefinedNull(vmc.contZpoint.deviceObjectId)) {
                notificationFactory.errorInfo("Ошибка", "Не задан прибор для точки учета!");
                result = false;
            }
            if (vmc.checkUndefinedNull(vmc.contZpoint.rsoId)) {
                notificationFactory.errorInfo("Ошибка", "Не задано РСО для точки учета!");
                result = false;
            }
            return result;
        };
        
        function successCallbackOnZpointUpdate(resp) {
            $rootScope.$broadcast(objectSvc.BROADCASTS.ZPOINT_SAVED, {"response": resp});
            $('#showZpointOptionModal').modal('hide');
        }
        
        vmc.saveZpoint = function () {
            vmc.contZpoint.isSaving = true;
            if (!checkZpointCommonSettings()) {
                //zpoint settings saving flag reset
                vmc.contZpoint.isSaving = false;
                return false;
            }
                //perform temperature schedule
            if (!vmc.checkUndefinedNull(vmc.contZpoint.tChart)) {
                vmc.contZpoint.temperatureChartId = vmc.contZpoint.tChart.id;
                vmc.contZpoint.tChart = null;
            } else {
                vmc.contZpoint.temperatureChartId = null;
            }
            var url = objectSvc.getRmaObjectsUrl() + "/" + vmc.contObject.id + "/zpoints";
            if (angular.isDefined(vmc.contZpoint.id) && (vmc.contZpoint.id !== null)) {
                url = url + "/" + vmc.contZpoint.id;

                $http({
                    url: url,
                    method: 'PUT',
                    data: vmc.contZpoint
                })
                    .then(successCallbackOnZpointUpdate, errorCallback);
            } else {
                vmc.contZpoint.startDate = Date.now();
                $http({
                    url: url,
                    method: 'POST',
                    data: vmc.contZpoint
                })
                    .then(successCallbackOnZpointUpdate, errorCallback);
            }
        };
        
        vmc.removeTChart = function (zpSettings) {
            zpSettings.tChart = null;
            zpSettings.temperatureChartId = null;
        };
        
        $('#showZpointOptionModal').on('shown.bs.modal', function () {
            $('#inputZpointName').focus();
            vmc.loading = true;
            getRsoOrganizations();
            loadServiceTypes();
            loadContObject(vmc.contObjectId);            
        });
        
        $scope.$on(EVENTS.DEVICE_LOADED, function () {
            vmc.setDeviceToZpoint();
        });
        
        $scope.$on(EVENTS.ZPOINT_LOADED, function () {
            vmc.setDeviceToZpoint();
        });
        
        $('#showZpointOptionModal').on('hidden.bs.modal', function () {
            vmc.contObject = null;
            vmc.contZpoint = null;
            vmc.serviceTypes = null;
        });
        
    }
})();