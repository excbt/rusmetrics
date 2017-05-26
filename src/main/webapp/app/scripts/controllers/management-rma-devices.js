/*jslint node: true, eqeq: true, es5: true*/
/*global angular, moment, $, confirm*/
'use strict';
var app = angular.module('portalNMC');
app.controller('MngmtDevicesCtrl', ['$rootScope', '$scope', '$http', '$timeout', 'objectSvc', 'notificationFactory', 'mainSvc', '$interval', 'logSvc', '$cookies', function ($rootScope, $scope, $http, $timeout, objectSvc, notificationFactory, mainSvc, $interval, logSvc, $cookies) {
//console.log('Run devices management controller.');
    
    //for testing
//    var impulseDevModel = {
//        modelName: "Импульсный прибор",
//        isImpulse: true,
//        id: 123321
//    };
    //end testing
    
    $rootScope.ctxId = "management_rma_devices_page";
    //settings
    var SESSION_TASK_URL = "../api/rma/subscrSessionTask",
        SESSION_DETAIL_TYPE_URL = SESSION_TASK_URL + "/contZPointSessionDetailType/byDeviceObject",
        SESSION_DETAIL_TYPE_FOR_DEVICE_URL = SESSION_TASK_URL + "/sessionDetailTypes/byDeviceObject",
        REFRESH_PERIOD = 10000;//10 sec
    
    var interval = null;//interval for load session data
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.datasourcesUrl = objectSvc.getDatasourcesUrl();
    $scope.ctrlSettings.loading = true;
    $scope.ctrlSettings.userDateFormat = "DD.MM.YYYY";
    $scope.ctrlSettings.sessionsUrl = "../api/rma/logSessions";
    
    $scope.ctrlSettings.sessionColumns = [
        {
            name: "currentStatusColor",
            caption: "",
            type: 'color',
            headerClass: "col-xs-1 col-md-1 nmc-td-for-button noPadding"
        }, {
            name: "startDate",
            caption: "Время начала",
            headerClass: "col-xs-2 col-md-2 noPadding"
        }, {
            name: "endDate",
            caption: "Время завершения",
            headerClass: "col-xs-2 col-md-2 noPadding"
        }, {
            name: "currentStatus",
            caption: "Текущий статус",
            headerClass: "col-xs-1 col-md-1"
        }, {
            name: "statusMessage",
            caption: "Сообщение",
            headerClass: "col-xs-5 col-md-5"
        }
        
    ];
    
    var SESSION_TERMINATE_STATUSES = ["COMPLETE", "COMPLETE WITH ERROR", "FAILURE"];
    
    $scope.ctrlSettings.logColumns = [
        {
            name: "stepDateStr",
            caption: "Дата-время",
            headerClass: "col-xs-2 col-md-2",
            type: "id"
        }, {
            name: "stepType",
            caption: "Тип",
            headerClass: "col-xs-1 col-md-1"
        }, {
            name: "stepMessage",
            caption: "Текст",
            headerClass: "col-xs-7 col-md-7"
        }, {
            name: "isIncremental",
            caption: "Счетчик",
            headerClass: "col-xs-1 col-md-1",
            type: "checkbox"
            
        }, {
            name: "sumRows",
            caption: "Значение",
            headerClass: "col-xs-1 col-md-1"
        }
        
    ];
    
    $scope.ctrlSettings.deviceColumns = [
        {
            name: "deviceContObjectCaption",
            caption: "Объект",
            headerClass: "col-xs-4 col-md-4"
        }, {
            name: "deviceModelCaption",
            caption: "Модель",
            headerClass: "col-xs-1 col-md-1"
        }, {
            name: "number",
            caption: "s/n",
            headerClass: "col-xs-2 col-md-2"
        }, {
            name: "deviceDatasourceCaption",
            caption: "Источник данных",
            headerClass: "col-xs-3 col-md-3"
            
        }, {
            name: "deviceTimeOffsetString",
            caption: "Расхождение времени",
            headerClass: "col-xs-2 col-md-2"
        }
        
    ];
    
    $scope.ctrlSettings.daterangeOpts = mainSvc.getDateRangeOptions("ru");
    $scope.ctrlSettings.daterangeOpts.startDate = moment().startOf('day');
    $scope.ctrlSettings.daterangeOpts.endDate = moment().endOf('day');
//    $scope.ctrlSettings.daterangeOpts.dateLimit = {"months": 1}; //set date range limit with 1 month
    $scope.ctrlSettings.dataLoadDaterange = {
        startDate: moment().startOf('day'),
        endDate: moment().endOf('day')
    };
    //data
    $scope.data = {};
    $scope.data.dataSources = [];
    $scope.data.objects = [];
    $scope.data.devices = [];
    $scope.data.deviceModels = [];
    $scope.data.deviceModelTypes = [];
    $scope.data.impulseCounterTypes = [];
    $scope.data.currentObject = {};
    $scope.data.currentScheduler = {};
    $scope.data.currentSession = {};
    
    $scope.data.metadataSchema = [
        {
            header: 'Поле источник',
            headClass : 'col-xs-2 col-md-2',
            name: 'srcProp',
            type: 'input/text'
        }, {
            header: 'Поле приемник',
            headClass : 'col-xs-2 col-md-2',
            name: 'destProp',
            type: 'input/text'
        }, {
            header: 'Единицы измерения',
            headClass : 'col-xs-1 col-md-1',
            name: 'srcMeasureUnit',
            type: 'select',
            disabled: false
        }
    ];
    
    $scope.data.deviceModalWindowTabs = [
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
    
    $scope.data.deviceInstTypes = [
        {
            keyname: "P",
            caption: "Индивидуальный"
        }, {
            keyname: "S",
            caption: "Общедомовой"
        }
    ];
    
    function setActivePropertiesTab(tabName) {
        $scope.data.deviceModalWindowTabs.forEach(function (tabElem) {
            var tab, tabPanel;
            tab = document.getElementById(tabElem.name) || null;
            tabPanel = document.getElementById(tabElem.tabpanel) || null;
//console.log(tab);            
//console.log(tabPanel);            
//console.log(tabName);
//console.log(tabElem.name);                            
//console.log(tabElem.name.localeCompare(tabName));                                        
//console.log(tabElem.name.localeCompare(tabName) !== 0);                            
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
    
    var setConPropertiesActiveTab = function () {
//        var tab = document.getElementById('main_properties_tab');     
//        tab.classList.remove("active");        
//        tab = document.getElementById('time_properties_tab') || null;
//        tab.classList.remove("active");        
//        tab = document.getElementById("con_properties_tab") || null;
//        tab.classList.add("active"); 
//                
//        tab = document.getElementById('main_properties') || null;
//        tab.classList.remove("active");
//        tab = document.getElementById('time_properties') || null;
//        tab.classList.remove("active");
//        tab = document.getElementById("con_properties") || null;
//        tab.classList.add("in");
//        tab.classList.add("active");
        
        
        setActivePropertiesTab("con_properties_tab");
        //set focus for first error input
        if ($('#divInputNetDevAddr').hasClass('has-error')) {
            $('#inputNetDevAddr').focus();
            return;
        }
        if ($('#divInputNetAddrImpDev').hasClass('has-error')) {
            $('#inputNetAddrImpDev').focus();
            return;
        }
        if ($('#divInputTikCost').hasClass('has-error')) {
            $('#inputTikCost').focus();
            return;
        }
        if ($('#divInputDevMeasure').hasClass('has-error')) {
            $('#inputDevMeasure').focus();
            return;
        }
    };
    
    var setMainPropertiesActiveTab = function () {
        setActivePropertiesTab("main_properties_tab");
//        var tab = document.getElementById('con_properties_tab');     
//        tab.classList.remove("active");        
//        tab = document.getElementById('time_properties_tab') || null;
//        tab.classList.remove("active");        
//        tab = document.getElementById("main_properties_tab") || null;
//        tab.classList.add("active"); 
//                
//        tab = document.getElementById('con_properties') || null;
//        tab.classList.remove("active");
//        tab = document.getElementById('time_properties') || null;
//        tab.classList.remove("active");
//        tab = document.getElementById("main_properties") || null;
//        tab.classList.add("in");
//        tab.classList.add("active");        
    };
            //get devices
    function sortDevicesByConObjectFullName(array) {
        if (angular.isUndefined(array) || (array === null) || !angular.isArray(array)) {
            return false;
        }
        array.sort(function (a, b) {
            if (a.contObjectInfo.fullName.toUpperCase() > b.contObjectInfo.fullName.toUpperCase()) {
                return 1;
            }
            if (a.contObjectInfo.fullName.toUpperCase() < b.contObjectInfo.fullName.toUpperCase()) {
                return -1;
            }
            return 0;
        });
    }
    
    var errorProtoCallback = function (e) {
        var errorObj = mainSvc.errorCallbackHandler(e);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };
    
        
    var successCallback = function (response) {
        notificationFactory.success();
        $scope.getDevices();
        $('#showDeviceModal').modal('hide');
        $('#deleteObjectModal').modal('hide');
        $('#scheduleEditorModal').modal('hide');
        $scope.data.currentObject = {};
        $scope.data.currentScheduler = {};
    };
    
    var errorCallback = function (e) {
        errorProtoCallback(e);
        // reset saving device flag
        $scope.data.currentObject.isSaving = false;
        //reset saving scheduler flag
        $scope.data.currentScheduler.isSaving = false;
        //session data load flag
        $scope.ctrlSettings.sessionsLoading = false;
    };
    
    $scope.getDevices = function () {
        objectSvc.getAllDevices().then(
            function (response) {
                var tmp = response.data;
                tmp.forEach(function (elem) {
                    if (angular.isDefined(elem.contObjectInfo) && (elem.contObjectInfo !== null)) {
                        elem.contObjectId = elem.contObjectInfo.contObjectId;
                    }
                    if (angular.isDefined(elem.activeDataSource) && (elem.activeDataSource !== null)) {
                        elem.subscrDataSourceId = Number(elem.activeDataSource.subscrDataSource.id);
                        elem.curDatasource = elem.activeDataSource.subscrDataSource;
                        elem.subscrDataSourceAddr = elem.activeDataSource.subscrDataSourceAddr;
                        elem.dataSourceTable1h = elem.activeDataSource.dataSourceTable1h;
                        elem.dataSourceTable24h = elem.activeDataSource.dataSourceTable24h;
                    }
                    if (!mainSvc.checkUndefinedNull(elem.verificationDate)) {
                        elem.verificationDateString = mainSvc.dateFormating(elem.verificationDate, $scope.ctrlSettings.userDateFormat);
                    }
                    elem.deviceContObjectCaption = elem.contObjectInfo.fullName;
                    elem.deviceModelCaption = elem.deviceModel.caption || elem.deviceModel.modelName;
                    if ($scope.isDeviceDisabled(elem) === true) {
                        switch (elem.exSystemKeyname) {
                        case "LERS":
                            elem.deviceDatasourceCaption = "ЛЭРС";
                            break;
                        case "VZLET":
                            elem.deviceDatasourceCaption = "ВЗЛЕТ";
                            break;
                        }
                    } else {
                        if (!mainSvc.checkUndefinedNull(elem.activeDataSource)) {
                            elem.deviceDatasourceCaption = elem.activeDataSource.subscrDataSource['dataSourceName' || 'id'];
                        }
                    }
                    elem.deviceTimeOffsetString = mainSvc.prepareTimeOffset(elem.deviceObjectTimeOffset);
                    if (!mainSvc.checkUndefinedNull(elem.deviceTimeOffsetString)) {
                        if (elem.deviceTimeOffsetString.indexOf("+") === 0) {
                            elem.deviceTimeOffsetStringTitle = "Часы прибора спешат";
                        }
                        if (elem.deviceTimeOffsetString.indexOf("-") === 0) {
                            elem.deviceTimeOffsetStringTitle = "Часы прибора отстают";
                        }
                    }
                    
                });
                sortDevicesByConObjectFullName(tmp);
                $scope.data.devices = tmp;
                $scope.ctrlSettings.loading = false;
            },
            errorProtoCallback
        );
    };
    
//    function getDeviceModelTypes() {
//        objectSvc.getDeviceModelTypes().then(function(response){
//            $scope.data.deviceModelTypes = response.data;
//            $scope.getDevices();
//        }, errorProtoCallback);
//    }
    
    function getImpulseCounterTypes() {
        objectSvc.getImpulseCounterTypes().then(function (response) {
            $scope.data.impulseCounterTypes = response.data;
            $scope.getDevices();
        }, errorProtoCallback);
    }
    
//    $scope.getDevices();
//    getDeviceModelTypes();
    getImpulseCounterTypes();
    
    //get device scheduler
    $scope.getDeviceSchedulerSettings = function (objId, device) {
        objectSvc.getDeviceSchedulerSettings(objId, device.id).then(
            function (resp) {
                $scope.data.currentScheduler = resp.data;
                $scope.selectedItem(device);
                $('#scheduleEditorModal').modal();
            },
            errorProtoCallback
        );
    };
    //save scheduler settings
    $scope.saveScheduler = function (objId, device, scheduler) {
        if (mainSvc.checkUndefinedNull(objId)) {
            return "ObjId of scheduler is undefined or null.";
        }
        if (mainSvc.checkUndefinedNull(device) || mainSvc.checkUndefinedNull(device.id)) {
            return "Device of scheduler is empty.";
        }
        if (mainSvc.checkUndefinedNull(scheduler)) {
            return "Scheduler is empty.";
        }
        scheduler.isSaving = true;
        objectSvc.putDeviceSchedulerSettings(objId, device.id, scheduler).then(successCallback, errorCallback);
    };
    
    function findDeviceModelById(modelId) {
        var resultModel = null;
        if (mainSvc.checkUndefinedNull($scope.data.deviceModels) || !angular.isArray($scope.data.deviceModels)) {
            return resultModel;
        }
        $scope.data.deviceModels.some(function (devModel) {
            if (devModel.id === modelId) {
                resultModel = devModel;
                return true;
            }
        });
        return resultModel;
    }
    
    //checkers for model and datasource
    $scope.checkDatasource = function () {
        var result = true;
        if ($scope.data.currentObject.id === null &&  mainSvc.checkUndefinedNull($scope.data.currentObject.subscrDataSourceId) && $scope.data.currentObject.isManual) {
            result = false;
        }
        if (mainSvc.checkUndefinedNull($scope.data.currentObject.deviceModelId)) {
            return result;
        }
        var tmpDevModel = findDeviceModelById($scope.data.currentObject.deviceModelId);
        if (!mainSvc.checkUndefinedNull($scope.data.currentObject) && !mainSvc.checkUndefinedNull($scope.data.currentObject.curDatasource) && tmpDevModel.isImpulse === true && $scope.data.currentObject.curDatasource.dataSourceType.isRaw !== true) {
            result = false;
        }
        return result;
    };
    
    $scope.checkModel = function () {
        var result = true;
        if ($scope.data.currentObject.id === null && mainSvc.checkUndefinedNull($scope.data.currentObject.deviceModelId) && $scope.data.currentObject.isManual) {
            result = false;
        }
        if (mainSvc.checkUndefinedNull($scope.data.currentObject.deviceModelId)) {
            return result;
        }
        var tmpDevModel = findDeviceModelById($scope.data.currentObject.deviceModelId);
        if (!mainSvc.checkUndefinedNull($scope.data.currentObject) && !mainSvc.checkUndefinedNull($scope.data.currentObject.curDatasource) && tmpDevModel.isImpulse === true && $scope.data.currentObject.curDatasource.dataSourceType.isRaw !== true) {
            result = false;
        }
        return result;
    };
    
    function checkImpulseCompatibility(model, datasource) {
        if (!mainSvc.checkUndefinedNull(model) && model.isImpulse === true && datasource.dataSourceType.isRaw !== true) {
            notificationFactory.errorInfo("Ошибка", "Выбранный источник данных не поддерживает работу с импульсными приборами");
            return false;
        }
        return true;
    }
    
    function setImpulseSettings(targetDevice, isImpulse, impulseK, impulseMu, impulseCounterTypeKeyname) {
        targetDevice.isImpulse = isImpulse;
        targetDevice.impulseK = impulseK;
        targetDevice.impulseMu = impulseMu;
        targetDevice.impulseCounterType = impulseCounterTypeKeyname || null;
    }
    
    $scope.deviceModelChange = function () {
        if (!mainSvc.checkUndefinedNull($scope.data.currentObject.deviceModelId)) {
            var tmpDevModel = findDeviceModelById($scope.data.currentObject.deviceModelId);
            if (!mainSvc.checkUndefinedNull($scope.data.currentObject) && !mainSvc.checkUndefinedNull($scope.data.currentObject.curDatasource)) {
                checkImpulseCompatibility(tmpDevModel, $scope.data.currentObject.curDatasource);
            }
            
            $cookies.recentDeviceModelId = $scope.data.currentObject.deviceModelId;
            $scope.data.currentModel = tmpDevModel;
            $scope.data.currentObject.curModel = tmpDevModel;
            
            //change impulseK and impulseMu
            if ($scope.data.currentModel.isImpulse === true) {
                setImpulseSettings($scope.data.currentObject, $scope.data.currentModel.isImpulse, $scope.data.currentModel.defaultImpulseK, $scope.data.currentModel.defaultImpulseMu, $scope.data.impulseCounterTypes[0].keyname || null);
            } else {
                setImpulseSettings($scope.data.currentObject, false, null, null, null);
            }
            
        }
    };
    
    $scope.deviceInstTypeChange = function () {
        $cookies.recentInstType = $scope.data.currentObject.instType;
    };
    
                //get device models
    $scope.getDeviceModels = function () {
        objectSvc.getDeviceModels().then(
            function (response) {
                //ставлю модель "Не определена" на первое место списка
                var ndDeviceModel = null;
                var ndDeviceModelIndex = -1;
                response.data.some(function (dm, dmindex) {
                    if (dm.modelName === "NOT_ASSIGNED") {
                        ndDeviceModel = dm;
                        ndDeviceModelIndex = dmindex;
                        return true;
                    }
                });
                if (ndDeviceModelIndex !== -1) {
                    response.data.splice(ndDeviceModelIndex, 1);
                    response.data.splice(0, 0, ndDeviceModel);
                }
                $scope.data.deviceModels = response.data;
                
                //test
//                $scope.data.deviceModels.push(impulseDevModel);
            },
            errorProtoCallback
        );
    };
    $scope.getDeviceModels();
    
           //get data sources
    var getDatasources = function (url) {
        var targetUrl = url;
        $http.get(targetUrl)
            .then(function (response) {
                var tmp = response.data;
                $scope.data.dataSources = tmp;
                mainSvc.sortItemsBy($scope.data.dataSources, 'dataSourceName');
            },
                function (e) {
                    console.log(e);
                });
    };
    getDatasources($scope.ctrlSettings.datasourcesUrl);
    
                 //get Objects
    $scope.getObjects = function () {
        objectSvc.getRmaPromise().then(function (response) {
            objectSvc.sortObjectsByFullName(response.data);
            $scope.data.objects = response.data;
        });
    };
    $scope.getObjects();
    
    $scope.selectedItem = function (item) {
        $scope.data.currentObject = angular.copy(item);
        $scope.data.currentObject.beginDeviceModelId = $scope.data.currentObject.deviceModelId;
    };
    
    $scope.addDevice = function () {
        $scope.data.currentObject = {};
        $scope.data.currentObject.id = null;
        $scope.data.currentObject.isManual = true;
        $scope.data.currentObject.deviceLoginInfo = {};
        if (!mainSvc.checkUndefinedNull($cookies.recentDeviceModelId)) {
            $scope.data.currentObject.deviceModelId = Number($cookies.recentDeviceModelId);
            $scope.data.currentModel = findDeviceModelById($scope.data.currentObject.deviceModelId);
            $scope.data.currentObject.curModel = findDeviceModelById($scope.data.currentObject.deviceModelId);
            
            if ($scope.data.currentModel.isImpulse === true) {
                setImpulseSettings($scope.data.currentObject, $scope.data.currentModel.isImpulse, $scope.data.currentModel.defaultImpulseK, $scope.data.currentModel.defaultImpulseMu, $scope.data.impulseCounterTypes[0].keyname || null);
            }
        }
        if (!mainSvc.checkUndefinedNull($cookies.recentInstType)) {
            $scope.data.currentObject.instType = $cookies.recentInstType;
        }
        if (!mainSvc.checkUndefinedNull($cookies.recentDataSourceId)) {
            $scope.data.currentObject.subscrDataSourceId = Number($cookies.recentDataSourceId);
            $scope.deviceDatasourceChange();
        }
        getDatasources($scope.ctrlSettings.datasourcesUrl);
        $('#showDeviceModal').modal();
    };
    
    $scope.editDevice = function (device) {
        $scope.selectedItem(device);
        if (!mainSvc.checkUndefinedNull(device.deviceModelId)) {
            $scope.data.currentModel = findDeviceModelById(device.deviceModelId);
            $scope.data.currentObject.curModel = findDeviceModelById(device.deviceModelId);
        }
        
        objectSvc.getDeviceDatasources(device.contObjectId, device.id).then(
            function (resp) {
                $scope.data.dataSources = angular.copy(resp.data);
                $scope.getDeviceMetaDataVzlet($scope.data.currentObject);
                $('#showDeviceModal').modal();
            },
            errorCallback
        );
        
    };
    
    $scope.deviceDatasourceChange = function () {
        var curDataSource = null;
        $scope.data.dataSources.some(function (source) {
            if (source.id === $scope.data.currentObject.subscrDataSourceId) {
                curDataSource = source;
                return true;
            }
        });
//        if (!mainSvc.checkUndefinedNull($scope.data.currentModel) && $scope.data.currentModel.isImpulse === true && curDataSource.dataSourceType.isRaw !== true) {
//            notificationFactory.errorInfo("Ошибка", "Выбранный источник данных не поддерживает работу с импульсными приборами");
////            $scope.data.currentObject.subscrDataSourceId = Number($cookies.recentDataSourceId) || null;
////            return false;
//        }
        checkImpulseCompatibility($scope.data.currentModel, curDataSource);
        
        $scope.data.currentObject.dataSourceTable1h = null;
        $scope.data.currentObject.dataSourceTable24h = null;
        $scope.data.currentObject.subscrDataSourceAddr = null;

        $scope.data.currentObject.curDatasource = curDataSource;
        
        if (!mainSvc.checkUndefinedNull($scope.data.currentObject.subscrDataSourceId)) {
            $cookies.recentDataSourceId = $scope.data.currentObject.subscrDataSourceId;
        }
        
    };
    
    function checkDeviceImpulseProperties(device) {
        var result = true;
        if (mainSvc.checkUndefinedNull(device.impulseK) || device.impulseK == 0) { /* == - only, === - don't catch '0', "Number(device.impulseK) === 0" - do not test*/
            notificationFactory.errorInfo("Ошибка", "Не задано число импульсов в единице измерения");
            result = false;
        }
        if (mainSvc.checkUndefinedNull(device.impulseMu) || device.impulseMu === "") {
            notificationFactory.errorInfo("Ошибка", "Не задана единица измерения");
            result = false;
        }
        if (mainSvc.checkUndefinedNull(device.impulseCounterAddr) || device.impulseCounterAddr === "") {
            notificationFactory.errorInfo("Ошибка", "Не задан адрес счетчика импульсов");
            result = false;
        }
        if (mainSvc.checkUndefinedNull(device.impulseCounterSlotAddr) || device.impulseCounterSlotAddr === "") {
            notificationFactory.errorInfo("Ошибка", "Не задан адрес прибора в счетчике импульсов");
            result = false;
        }
        return result;
    }
    
    function checkDevice(device) {
                //set device saving...
        device.isSaving = true;
        //check device data
        var checkDsourceFlag = true;
        if (mainSvc.checkUndefinedNull(device.contObjectId)) {
            notificationFactory.errorInfo("Ошибка", "Не задан объект учета");
            checkDsourceFlag = false;
        }
        if (device.id === null && mainSvc.checkUndefinedNull(device.subscrDataSourceId) && device.isManual) {
            notificationFactory.errorInfo("Ошибка", "Не задан источник данных");
            checkDsourceFlag = false;
        }
        if (device.id === null && mainSvc.checkUndefinedNull(device.deviceModelId) && device.isManual) {
            notificationFactory.errorInfo("Ошибка", "Не задана модель прибора");
            checkDsourceFlag = false;
        }
//console.log(device);
//        if (!mainSvc.checkUndefinedNull(device.curModel) && device.curModel.isImpulse === true && device.curDatasource.dataSourceType.isRaw !== true) {
        if (checkImpulseCompatibility(device.curModel, device.curDatasource) === false) {
            checkDsourceFlag = false;
        }
        if (!mainSvc.checkUndefinedNull(device.curModel) && device.curModel.isImpulse === true && device.curDatasource.dataSourceType.isRaw === true) {
            var inputCDFlag = checkDsourceFlag;
            if (checkDeviceImpulseProperties(device) === false) {
                checkDsourceFlag = false;
            }
            //Open Connection params tab if common params is checked, but impulse param check is failed
            if (inputCDFlag === true && checkDsourceFlag === false) {
                setConPropertiesActiveTab();
            }
            //Open Main properties tab
            if (inputCDFlag === false) {
                setMainPropertiesActiveTab();
            }
        }
        if (checkDsourceFlag === false) {
            device.isSaving = false;
            return false;
        }
        if (!mainSvc.checkUndefinedNull(device.verificationDateString) || (device.verificationDateString !== "")) {
            device.verificationDate = mainSvc.strDateToUTC(device.verificationDateString, $scope.ctrlSettings.userDateFormat);
        }
//console.log(device);
        var userDecision =  false;
        if (!mainSvc.checkUndefinedNull(device.id) && $scope.isDirectDevice(device) && (device.beginDeviceModelId !== device.deviceModelId)) {
            //прибор не новый, идет прямая загрузка с прибора и модель была изменена         
            //выдать предупреждение о том, что модель была изменена и мета данные будут стерты
            userDecision = confirm("Модель прибора была изменена. Метаданные прибора будут перезаписаны метаданными текущей модели. Продолжить?");
            if (!userDecision) {
                device.isSaving = false;
//                return "Save operation is canceled by user.";
                return false;
            }
        }
        return true;
    }
    
    $scope.saveDevice = function (device) {
        //check device
        if (checkDevice(device) === false) {
            return false;
        }
        //send to server
        objectSvc.sendDeviceToServer(device).then(successCallback, errorCallback);
    };
    
    var setConfirmCode = function () {
        $scope.confirmCode = null;
        var tmpCode = mainSvc.getConfirmCode();
        $scope.confirmLabel = tmpCode.label;
        $scope.sumNums = tmpCode.result;
    };
    
    $scope.deleteObjectInit = function (device) {
        $scope.selectedItem(device);
        setConfirmCode();
    };
    
    $scope.deleteObject = function (device) {
        var targetUrl = objectSvc.getRmaObjectsUrl();
        targetUrl = targetUrl + "/" + device.contObjectInfo.contObjectId + "/deviceObjects/" + device.id;
        $http.delete(targetUrl).then(successCallback, errorCallback);
    };
    
    // device metadata 
    $scope.data.deviceMetadataMeasures = objectSvc.getDeviceMetadataMeasures();
    $scope.$on('objectSvc:deviceMetadataMeasuresLoaded', function () {
        $scope.data.deviceMetadataMeasures = objectSvc.getDeviceMetadataMeasures();
    });
    
    $scope.updateDeviceMetaData = function (device) {
        device.isSaving = true;
        var method = "PUT";
        var url = objectSvc.getRmaObjectsUrl() + "/" + device.contObjectId + "/deviceObjects/" + device.id + "/metadata";
        $http({
            url: url,
            method: method,
            data: device.metadata
        })
            .then(
                function (response) {
                    $scope.currentObject = {};
                    $('#metaDataEditorModal').modal('hide');
                    notificationFactory.success();
                },
                errorCallback
            );
    };
    
        //check device: data source vzlet or not?
    $scope.vzletDevice = function (device) {
        var result = false;
        if (angular.isDefined(device.activeDataSource) && (device.activeDataSource !== null)) {
            if (angular.isDefined(device.activeDataSource.subscrDataSource) && (device.activeDataSource.subscrDataSource !== null)) {
                if (device.activeDataSource.subscrDataSource.dataSourceTypeKey === "VZLET") {
                    result = true;
                }
            }
        }
        return result;
    };
    
    $scope.isDirectDevice = function (device) {
        return objectSvc.isDirectDevice(device);
    };
    
    //get device meta data and show it
    $scope.getDeviceMetaDataVzlet = function (device) {
        if (mainSvc.checkUndefinedNull(device)) {
            var errorMsg = "getDeviceMetaDataVzlet: Device undefined or null.";
            console.log(errorMsg);
            return errorMsg;
        }
        objectSvc.getRmaDeviceMetaDataVzlet(device.contObjectInfo.contObjectId, device).then(
            function (response) {
                device.metaData = response.data;
            },
            errorCallback
        );
    };

     //get the list of the systems for meta data editor
    $scope.getVzletSystemList = function () {
        var tmpSystemList = objectSvc.getVzletSystemList();
        if (tmpSystemList.length === 0) {
            objectSvc.getDeviceMetaDataVzletSystemList()
                .then(function (response) {
                    $scope.data.vzletSystemList = response.data;
                },
                    errorCallback
                    );
        } else {
            $scope.data.vzletSystemList = tmpSystemList;
        }
    };
    $scope.getVzletSystemList();
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
    
    //**************************************************************************************
    //                      Work with sessions
    ////////////////////////////////////////////////////////////////////////////////////////
    function checkTaskSettings() {
        var result = true;
//        if (mainSvc.checkUndefinedNull($scope.data.currentContZpoint) || mainSvc.checkUndefinedNull($scope.data.currentContZpoint.contZPointId)){
//            result = false;
//            notificationFactory.errorInfo("Ошибка", "Не выбрана точка учета.");
//        };
        if (mainSvc.checkUndefinedNull($scope.data.currentObject) || mainSvc.checkUndefinedNull($scope.data.currentObject.id)) {
            result = false;
            notificationFactory.errorInfo("Ошибка", "Некорректно задан прибор.");
        }
        if (mainSvc.checkUndefinedNull($scope.ctrlSettings.dataLoadDaterange.startDate)) {
            result = false;
            notificationFactory.errorInfo("Ошибка", "Некорректно задано начало периода.");
        }
        if (mainSvc.checkUndefinedNull($scope.ctrlSettings.dataLoadDaterange.endDate)) {
            result = false;
            notificationFactory.errorInfo("Ошибка", "Некорректно задан конец периода.");
        }
        if ($scope.ctrlSettings.dataLoadDaterange.endDate < $scope.ctrlSettings.dataLoadDaterange.startDate) {
            result = false;
            notificationFactory.errorInfo("Ошибка", "Некорректно заданы границы периода.");
        }
        var isSelectedDT = false;
        $scope.data.detailTypes.some(function (dt) {
            if (dt.selected === true) {
                isSelectedDT = true;
                return true;
            }
        });
        if (isSelectedDT === false) {
            result = false;
            notificationFactory.warning("Не выбрано ни одного типа загружаемых данных. Загрузка не будет запущена.");
        }
        return result;
    }

    // task structure
//	private Long contZPointId;
//	private Long deviceObjectId;
//	private Date periodBeginDate;
//	private Date periodEndDate;
//	private String[] sessionDetailTypes;
    function checkSessionsForComplete(sessions) {
        $scope.ctrlSettings.dataIsLoaded = false;
        if (mainSvc.checkUndefinedNull(sessions) || sessions.length === 0) {
            return false;
        }
        var compledSessionsCount = 0;
        sessions.forEach(function (session) {
            if (SESSION_TERMINATE_STATUSES.indexOf(session.currentStatus) !== -1) {
                compledSessionsCount += 1;
            }
        });
        if (compledSessionsCount === sessions.length) {
            $scope.ctrlSettings.dataIsLoaded = true;
        }
    }
                        
    function loadDeviceSessionsData() {
        $scope.ctrlSettings.sessionsLoading = true;
        var url = SESSION_TASK_URL + "/" + $scope.data.currentSessionTask.id + "/logSessions";
        $http.get(url).then(function (resp) {
            $scope.ctrlSettings.sessionsLoading = false;
            if (mainSvc.checkUndefinedNull(resp.data) || resp.data.length === 0) {
                return "Session array is empty";
            }
                //save current session before update table, that don't lost it
            var tmpCurSession = null;
            if (!mainSvc.checkUndefinedNull($scope.data.currentSession) && !mainSvc.checkEmptyObject($scope.data.currentSession)) {
                tmpCurSession = angular.copy($scope.data.currentSession);
            }
            $scope.data.sessionsOnView = logSvc.serverDataParser(angular.copy(resp.data));
            checkSessionsForComplete($scope.data.sessionsOnView);
            //if currentSession is not set -> currentSession = first session from array           
            if (mainSvc.checkUndefinedNull(tmpCurSession) || mainSvc.checkEmptyObject(tmpCurSession)) {
                $scope.data.currentSession = $scope.data.sessionsOnView[0];
                tmpCurSession = angular.copy($scope.data.currentSession);
            }
            if (!mainSvc.checkUndefinedNull(tmpCurSession) && !mainSvc.checkEmptyObject(tmpCurSession)) {
                $scope.data.sessionsOnView.some(function (elem) {
                    if (elem.id === tmpCurSession.id) {
                        $scope.loadLogData(elem);
                    }
                });
            }
        }, errorCallback);
    }
    
    function loadSessionTask() {
        var url = SESSION_TASK_URL + "/" + $scope.data.currentSessionTask.id;
        $http.get(url).then(function (resp) {
            $scope.data.currentSessionTask = resp.data;
        }, errorProtoCallback);
    }
    
    function refreshData() {
        loadSessionTask();
        loadDeviceSessionsData();
    }
    
    $scope.startLoadData = function () {
        $scope.data.sessionsOnView = [];
        $scope.data.sessionLog = [];
        var check = checkTaskSettings();
        if (check === false) {
            return;
        }
        var task = {};
//        task.contZPointId = $scope.data.currentContZpoint.contZPointId;
        task.deviceObjectId = $scope.data.currentObject.id;
        task.periodBeginDate = $scope.ctrlSettings.dataLoadDaterange.startDate;
        task.periodEndDate = $scope.ctrlSettings.dataLoadDaterange.endDate;
        task.sessionDetailTypes = [];
        if (angular.isArray($scope.data.detailTypes) && $scope.data.detailTypes.length > 0) {
            $scope.data.detailTypes.forEach(function (dt) {
                if (dt.selected === true) {
                    task.sessionDetailTypes.push(dt.keyname);
                }
            });
        }
        var url = SESSION_TASK_URL;
        $http.post(url, task).then(function (resp) {
            if (mainSvc.checkUndefinedNull(resp) || mainSvc.checkUndefinedNull(resp.data)) {
                return;
            }
            $scope.data.currentSessionTask = resp.data;
            interval = $interval(refreshData, REFRESH_PERIOD);
        }, errorCallback);
    };
    
    function loadContZPointSessionDetailType(device) {
        var url = SESSION_DETAIL_TYPE_URL + "/" + device.id;
        $http.get(url).then(function (resp) {
            if (mainSvc.checkUndefinedNull(resp) || mainSvc.checkUndefinedNull(resp.data)) {
                console.log("Session detail type is empty.");
                return "Session detail type is empty.";
            }
            $scope.data.sessionDetailType = resp.data;
            
        }, errorProtoCallback);
    }
    
    function loadSessionDetailType(device) {
        var url = SESSION_DETAIL_TYPE_FOR_DEVICE_URL + "/" + device.id;
        $http.get(url).then(function (resp) {
            if (mainSvc.checkUndefinedNull(resp) || mainSvc.checkUndefinedNull(resp.data)) {
                console.log("Session detail type is empty.");
                return "Session detail type is empty.";
            }
            $scope.data.detailTypes = angular.copy(resp.data);
            
        }, errorProtoCallback);
    }
    
    $scope.initManualLoading = function (device) {
        $scope.ctrlSettings.dataIsLoaded = false;
        $scope.data.sessionsOnView = [];
        $scope.data.currentSession = {};
        $scope.data.currentSessionTask = {};
        $scope.data.sessionLog = [];
        $scope.data.currentContZpoint = null;
        $scope.data.sessionDetailType = [];
        $scope.data.detailTypes = [];
        $scope.selectedItem(device);
        loadContZPointSessionDetailType(device);
        loadSessionDetailType(device);
    };
    
    $scope.changeSessionDetailType = function () {
        $scope.data.detailTypes = angular.copy($scope.data.currentContZpoint.sessionDetailTypes);
    };
    
        // **********************************************************************
    // session log
    
    $scope.loadLogData = function (session) {
        $scope.data.currentSession.selected = false;
        session.selected = true;
        $scope.ctrlSettings.logLoading = true;
        $scope.data.currentSession = session;
        var url = $scope.ctrlSettings.sessionsUrl + "/" + session.id + "/steps";
        $http.get(url).then(function (resp) {
            $scope.data.sessionLog = resp.data;
            $scope.ctrlSettings.logLoading = false;
        }, errorCallback);
    };
    
    $("#deviceSessionModal").on('hidden.bs.modal', function () {
        if (!mainSvc.checkUndefinedNull(interval)) {
            $interval.cancel(interval);
        }
    });
    
    $scope.isDisabledStartButton = function () {
        //if $scope.data.currentSessionTask have id, that mean task start and we need block Start button
        return !mainSvc.checkUndefinedNull($scope.data.currentSessionTask) && !mainSvc.checkUndefinedNull($scope.data.currentSessionTask.id);
    };
    
    //************************************************
    //
    ////////////////////////////////////////////////////
    
    //keydown listener
    $scope.$on('$destroy', function () {
        window.onkeydown = undefined;
    });
    
    //keydown listener for ctrl+end
    window.onkeydown = function (e) {
//console.log(e); 
        var elem = null;
        if (e.code === "ArrowUp") { /*e.keyCode == 38*/
            elem = document.getElementById("divWithDeviceTable");
            elem.scrollTop = elem.scrollTop - 20;
            return;
        }
        if (e.code === "ArrowDown") { /*e.keyCode == 40*/
            elem = document.getElementById("divWithDeviceTable");
            elem.scrollTop = elem.scrollTop + 20;
            return;
        }
        if (e.code === "PageDown") { /*e.keyCode == 34*/
            elem = document.getElementById("divWithDeviceTable");
            elem.scrollTop = elem.scrollTop + 34 * 10;
            return;
        }
        if (e.code === "PageUp") { /*e.keyCode == 33*/
            elem = document.getElementById("divWithDeviceTable");
            elem.scrollTop = elem.scrollTop - 34 * 10;
            return;
        }
        if (e.ctrlKey && e.code === "Home") { /*e.keyCode == 36*/
            elem = document.getElementById("divWithDeviceTable");
            elem.scrollTop = 0;
            return;
        }
        if (e.ctrlKey && e.code === "End") { /*e.keyCode == 35*/
            elem = document.getElementById("divWithDeviceTable");
            elem.scrollTop = elem.scrollHeight;

        }
    };
    
    
    //checkers
    $scope.isSystemuser = function () {
        return mainSvc.isSystemuser();
    };
    
    $scope.isTestMode = function () {
        return mainSvc.isTestMode();
    };
    
    $scope.isDeviceDisabled = function (device) {
//console.log(device);        
//console.log(device.exSystemKeyname === 'VZLET' || device.exSystemKeyname === 'LERS' || (device.isSaving === true));        
//        return !device.isManual;
        return device.exSystemKeyname === 'VZLET' || device.exSystemKeyname === 'LERS' || (device.isSaving === true);
    };
    
    $scope.isDeviceProtoLoaded = function (device) {
//console.log(device);        
//        return !device.isManual;
        return device.exSystemKeyname === 'VZLET' || device.exSystemKeyname === 'LERS';
    };
    
    $scope.checkHHmm = function (hhmmValue) {
        return mainSvc.checkHHmm(hhmmValue);
    };
    
    $scope.checkPositiveNumberValue = function (numvalue) {
        return mainSvc.checkPositiveNumberValue(numvalue);
    };
    
    $scope.checkScheduler = function (scheduler) {
        return $scope.checkHHmm(scheduler.loadingInterval)
            && $scope.checkHHmm(scheduler.loadingRetryInterval)
            && $scope.checkPositiveNumberValue(scheduler.loadingAttempts);
    };
    
    $scope.isAvailableConPropertiesTab = function () {
        if (mainSvc.checkUndefinedNull($scope.data.currentObject.curDatasource)) {
            return false;
        }
        return $scope.data.currentObject.curDatasource.dataSourceType.isRaw === true || $scope.data.currentObject.curDatasource.dataSourceType.isDbTablePair === true ||
            $scope.data.currentObject.exSystemKeyname === 'VZLET';
    };
    
    $scope.checkAutoLoadingDisabled = function () {
        if (mainSvc.checkUndefinedNull($scope.data.currentObject) || mainSvc.checkUndefinedNull($scope.data.currentObject.activeDataSource) || mainSvc.checkUndefinedNull($scope.data.currentObject.activeDataSource.subscrDataSource)) {
            return false;
        }
        return $scope.data.currentObject.activeDataSource.subscrDataSource.rawConnectionType === 'CLIENT' && $scope.data.currentObject.activeDataSource.subscrDataSource.rawModemDialEnable !== true;
    };
    
    $scope.checkNodataIdleTimeEnabled = function () {
        if (mainSvc.checkUndefinedNull($scope.data.currentObject) || mainSvc.checkUndefinedNull($scope.data.currentObject.activeDataSource) || mainSvc.checkUndefinedNull($scope.data.currentObject.activeDataSource.subscrDataSource)) {
            return false;
        }
        return $scope.data.currentObject.activeDataSource.subscrDataSource.rawConnectionType === 'CLIENT' && $scope.data.currentObject.activeDataSource.subscrDataSource.rawModemDialEnable === true;
    };
    
    $(document).ready(function () {
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
        $('#inputAttemptsNumberShd').inputmask();
    });
    
    $scope.setInputTikCostMask = function () {
        $('#inputTikCost').inputmask();
    };
    
    $("#showDeviceModal").on("shown.bs.modal", function () {
        $("#inputDeviceModel").focus();
    });
    
    $("#showDeviceModal").on("hidden.bs.modal", function () {
        setMainPropertiesActiveTab();
    });
    
//    $scope.$watch("data.currentModel", function(newModel) {
//console.log("New currentModel:");
//console.log(newModel);        
//        
//    });
//    
//    $scope.$watch("data.currentModel", function(newModel) {
//console.log("New currentModel:");
//console.log(newModel);        
//        if (mainSvc.checkUndefinedNull($scope.currentObject) || mainSvc.checkUndefinedNull($scope.currentObject.curDatasource)) {
//            return false;
//        }
//        if (newModel.isImpulse === true && $scope.currentObject.curDatasource.isRaw !== true) {
//            notificationFactory.errorInfo("Ошибка", "Текущий источник данных не поддерживает работу с импульсными приборами");
//        }
//    });
//    
//    $scope.$watch("data.currentObject.curDatasource", function(newDS) {
//        if (mainSvc.checkUndefinedNull($scope.data.currentModel) || mainSvc.checkUndefinedNull(newDS)) {
//            return false;
//        }
//        if ($scope.data.currentModel.isImpulse === true && newDS.isRaw !== true) {
//            notificationFactory.errorInfo("Ошибка", "Текущий источник данных не поддерживает работу с импульсными приборами");
//        }
//    });
    
    /*******************************************/
    /* Test generation*/
    
    var SESSION_ROW_COUNT = 21;
    var LOG_ROW_COUNT = 1;
    
    function generateSessions() {
        var sessions = [],
            i;
        for (i = 0; i < SESSION_ROW_COUNT; i += 1) {
            var ses = {};
            //color status
            var statusColor = Math.random();
//            if (statusColor >= 0 && statusColor <= 0.3){
//                ses.colorStatus = "RED";
//            }else if (statusColor > 0.3 && statusColor <= 0.6){
//                ses.colorStatus = "YELLOW";
//            }else if (statusColor > 0.6 && statusColor <= 1){
//                ses.colorStatus = "GREEN";
//            };
            //data device: source, model, number
            var rndIntNumber = Math.round(statusColor * 100);
            ses.dataSource = "Источник " + rndIntNumber;
            ses.deviceModel = "Модель " + rndIntNumber;
            ses.deviceNumber = "№ 0000" + rndIntNumber;
            //time
            ses.startDate = moment().subtract(Math.round(statusColor * 10), 'days').format("DD-MM-YYYY HH:mm");
            ses.endDate = moment().subtract(Math.round(statusColor * 10 + 1), 'days').format("DD-MM-YYYY HH:mm");
            //other
            ses.author = statusColor > 0.5 ? "Артамонов А.А" : "Расписание";
            ses.currentStatus = statusColor < 0.2 ? "Закрыта" : "Открыта";
            ses.colorStatus = statusColor < 0.2 ? "RED" : "GREEN";
            ses.totalRow = Math.round(statusColor * 10);
            
            if (statusColor <= 0.3) {
                ses.type = "OID";
                ses.childs = [];
                var j;
                for (j = 0; j <= Math.round(statusColor * 10); j += 1) {
                    var child = angular.copy(ses);
                    child.type = "OP";
                    child.dataSource = "Прибор " + rndIntNumber + "-" + j;
                    ses.childs.push(child);
                }
            }
            sessions.push(ses);
//console.log(ses);            
        }
        $scope.data.sessionsOnView = sessions;
    }
    
    function generateSessionLog() {
        var WORDS = ["Земля", "портфель", "идет", "лопатать", "потом", "дравина", "успел", "растопша", "трап", "мышь", "лететь"];
        var logs = [],
            i;
        for (i = 0; i < LOG_ROW_COUNT; i += 1) {
            var log = {};
            var rndNum = Math.random();
            log.stepDateStr = moment().format("DD-MM-YYYY HH:mm");
            if (rndNum <= 0.3) {
                log.stepType = "Аларм";
            } else if (rndNum > 0.3 && rndNum <= 0.6) {
                log.stepType = "Варнинг";
            } else {
                log.stepType = "Инфо";
            }
            var msg = "",
                j;
            for (j = 0; j <= 20; j += 1) {
                msg += WORDS[Math.round(Math.random() * WORDS.length)] + " ";
            }
            log.stepMessage = msg;
            logs.push(log);
        }
        $scope.data.sessionLog = logs;
        
    }
    
    function generate() {
        generateSessions();
        generateSessionLog();
    }
    
//    generate();
    /* end Test generation*/
    /*************************************************************/
}]);