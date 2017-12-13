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

    Controller.$inject = ['objectSvc', '$cookies', 'mainSvc'];

    /* @ngInject */
    function Controller(objectSvc, $cookies, mainSvc) {
        var vmc = this;
        
        vmc.loading = false;
        vmc.contObject = null;
        vmc.contZpoint = null;
        vmc.serviceTypes = null;
        vmc.rsoOrganizationsLoading = false;
        
        function errorCallback(e) {
            console.log(e);
            vmc.loading = false;
        }
        
        function successLoadZpointCallback(resp) {
            console.log(resp);
            vmc.loading = false;
            vmc.contZpoint = resp.data;
        }
        
        
        function loadZpoint(contObjectId, contZpointId) {
            objectSvc.loadZpointById(contObjectId, contZpointId)
                .then(successLoadZpointCallback)
                .catch(errorCallback);
        }
        
        function successLoadContObjectCallback(resp) {
            console.log(resp);
            vmc.contObject = resp.data;
            vmc.getDevices(vmc.contObject);
            //define: create new Zpoint or update exists
            if (mainSvc.checkUndefinedNull(vmc.contZpointId) || vmc.emptyString(vmc.contZpointId)) {
                //create new zpoint
                var contZpoint = {};
                //set recent service type
                if (!mainSvc.checkUndefinedNull($cookies.get('recentContServiceTypeKeyname'))) {
                    contZpoint.contServiceTypeKeyname = $cookies.get('recentContServiceTypeKeyname');
                    vmc.changeServiceType(contZpoint);
                }
                //set recent rso
                if (!mainSvc.checkUndefinedNull($cookies.get('recentRsoId'))) {
                    contZpoint.rsoId = Number($cookies.get('recentRsoId'));
                }
                vmc.contZpoint = contZpoint;
            } else {
                //load exists                
                loadZpoint(vmc.contObjectId, vmc.contZpointId);
            }
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
            if (!mainSvc.checkUndefinedNull(zpSettings.contServiceTypeKeyname)) {
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
        
        vmc.getDevices = function (obj) {
            obj.devicesLoading = true;
            objectSvc.getDevicesByObject(obj).then(
                function (response) {
                    
                    var tmpArr = response.data;
                    tmpArr.forEach(function (elem) {
                        // if (angular.isDefined(elem.contObjectInfo) && (elem.contObjectInfo != null)) {
                        //     elem.contObjectId = elem.contObjectInfo.contObjectId;
                        // }
//                        if (angular.isDefined(elem.activeDataSource) && (elem.activeDataSource != null)) {
//                            elem.subscrDataSourceId = elem.activeDataSource.subscrDataSource.id;
//                            elem.curDatasource = elem.activeDataSource.subscrDataSource;
//                            elem.subscrDataSourceAddr = elem.activeDataSource.subscrDataSourceAddr;
//                            elem.dataSourceTable1h = elem.activeDataSource.dataSourceTable1h;
//                            elem.dataSourceTable24h = elem.activeDataSource.dataSourceTable24h;
//                        }

                        var tmpDevCaption = elem.deviceModel.modelName || "";
                        tmpDevCaption += elem.number ? ", №" + elem.number : "";
                        elem.devCaption = tmpDevCaption;
                    });
                    obj.devices = tmpArr;//response.data;                    
                    if (!mainSvc.checkUndefinedNull(obj.devices) && obj.devices.length > 0 && mainSvc.checkUndefinedNull(vmc.contZpoint.deviceObjectId)) {
                        vmc.contZpoint.deviceObjectId = obj.devices[0].id;                    
                    }
                    obj.devicesLoading = false;
                }, function (e) {
                    errorCallback(e); 
                    obj.devicesLoading = false;
                }
            );
        };
        
        vmc.viewDeviceArchive = function (zpSettings) {
            if (!mainSvc.checkUndefinedNull(zpSettings.showDeviceArchive)) {
                zpSettings.showDeviceArchive = !zpSettings.showDeviceArchive;
            } else {
                zpSettings.showDeviceArchive = true;
            }
            zpSettings.deviceArchiveLoading = true;
            objectSvc.loadZpointDeviceArchive(zpSettings.id)
                .then(function (resp) {
                       zpSettings.deviceArchive = resp.data;
                        //test
                        zpSettings.deviceArchive = zpSettings.deviceArchive.concat(resp.data);
                zpSettings.deviceArchive = zpSettings.deviceArchive.concat(resp.data);
                zpSettings.deviceArchive = zpSettings.deviceArchive.concat(resp.data);
                zpSettings.deviceArchive = zpSettings.deviceArchive.concat(resp.data);
                    //end test
                        zpSettings.deviceArchiveLoading = false;
                        $('#deviceArchiveModal').modal();
                    }, 
                      function (e) {
                        zpSettings.deviceArchiveLoading = false;
                        errorCallback(e);
                    });
        }
        
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
            if (!mainSvc.checkUndefinedNull(zpSettings.rsoId)) {
                $cookies.put('recentRsoId', zpSettings.rsoId);
            }
        };
        
        $('#showZpointOptionModal').on('shown.bs.modal', function () {
//            console.log(vmc.contObjectId);
//            console.log(vmc.contZpointId);

            vmc.loading = true;
            getRsoOrganizations();
            loadServiceTypes();
            loadContObject(vmc.contObjectId);            
        });
        
    }
})();