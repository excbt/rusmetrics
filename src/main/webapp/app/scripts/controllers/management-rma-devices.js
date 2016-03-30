'use strict';
angular.module('portalNMC')
.controller('MngmtDevicesCtrl', ['$rootScope', '$scope','$http', '$timeout', 'objectSvc', 'notificationFactory', 'mainSvc', function($rootScope, $scope, $http, $timeout, objectSvc, notificationFactory, mainSvc){
//console.log('Run devices management controller.');
    $rootScope.ctxId = "management_rma_devices_page";
    //settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.datasourcesUrl = objectSvc.getDatasourcesUrl();
    $scope.ctrlSettings.loading = true;
    $scope.ctrlSettings.userDateFormat = "DD.MM.YYYY";
    //data
    $scope.data = {};
    $scope.data.dataSources = [];
    $scope.data.objects = [];
    $scope.data.devices = [];
    $scope.data.deviceModels = [];
    $scope.data.currentObject = {};
    $scope.data.currentScheduler = {};

    $scope.data.metadataSchema = [
        {
            header: 'Поле источник',
            headClass : 'col-xs-2 col-md-2',
            name: 'srcProp',
            type: 'input/text'
        },{
            header: 'Поле приемник',
            headClass : 'col-xs-2 col-md-2',
            name: 'destProp',
            type: 'input/text'
        },{
            header: 'Единицы измерения',
            headClass : 'col-xs-1 col-md-1',
            name: 'srcMeasureUnit',
            type: 'select',
            disabled: false
        }
    ];
            //get devices
    function sortDevicesByConObjectFullName(array){
            if (angular.isUndefined(array) || (array == null) || !angular.isArray(array)){
                return false;
            };           
            array.sort(function(a, b){
                if (a.contObjectInfo.fullName.toUpperCase() > b.contObjectInfo.fullName.toUpperCase()){
                    return 1;
                };
                if (a.contObjectInfo.fullName.toUpperCase() < b.contObjectInfo.fullName.toUpperCase()){
                    return -1;
                };
                return 0;
            }); 
        };
    
    $scope.getDevices = function(){
        objectSvc.getAllDevices().then(
            function(response){
                var tmp = response.data;
                tmp.forEach(function(elem){
                    if (angular.isDefined(elem.contObjectInfo) && (elem.contObjectInfo != null)){
                        elem.contObjectId = elem.contObjectInfo.contObjectId;
                    };
                    if (angular.isDefined(elem.activeDataSource) && (elem.activeDataSource != null)){
                        elem.subscrDataSourceId = Number(elem.activeDataSource.subscrDataSource.id);
                        elem.curDatasource = elem.activeDataSource.subscrDataSource;
                        elem.subscrDataSourceAddr = elem.activeDataSource.subscrDataSourceAddr;
                        elem.dataSourceTable1h = elem.activeDataSource.dataSourceTable1h;
                        elem.dataSourceTable24h = elem.activeDataSource.dataSourceTable24h;
                    };
                    if (!mainSvc.checkUndefinedNull(elem.verificationDate)){
                        elem.verificationDateString = mainSvc.dateFormating(elem.verificationDate, $scope.ctrlSettings.userDateFormat);
                    };
                });
                sortDevicesByConObjectFullName(tmp);
                $scope.data.devices = tmp;
                $scope.ctrlSettings.loading = false;                
            },
            function(e){
                console.log(e);
                var errorCode = "-1";
                if (mainSvc.checkUndefinedNull(e) || mainSvc.checkUndefinedNull(e.data)){
                    errorCode = "ERR_CONNECTION";
                };
                if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
                    errorCode = e.resultCode || e.data.resultCode;
                };
                var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
                notificationFactory.errorInfo(errorObj.caption, errorObj.description);
//                console.log(error.data);
//                notificationFactory.errorInfo(error.statusText, error.data.description || error.data);
            }
        );
    };
    $scope.getDevices();
    
    //get device scheduler
    $scope.getDeviceSchedulerSettings = function(objId, device){
        objectSvc.getDeviceSchedulerSettings(objId, device.id).then(
            function(resp){
                $scope.data.currentScheduler = resp.data;
                $scope.selectedItem(device);
                $('#sheduleEditorModal').modal();
            },
            function(e){
                console.log(e);
                var errorCode = "-1";
                if (mainSvc.checkUndefinedNull(e) || mainSvc.checkUndefinedNull(e.data)){
                    errorCode = "ERR_CONNECTION";
                };
                if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
                    errorCode = e.resultCode || e.data.resultCode;
                };
                var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
                notificationFactory.errorInfo(errorObj.caption, errorObj.description);
//                console.log(error.data);
//                notificationFactory.errorInfo(error.statusText, error.data.description || error.data);
            }
        );
    };
    //save scheduler settings
    $scope.saveScheduler = function(objId, device, scheduler){
        if (mainSvc.checkUndefinedNull(objId)){
            return "ObjId of scheduler is undefined or null.";
        };
        if (mainSvc.checkUndefinedNull(device) || mainSvc.checkUndefinedNull(device.id)){
            return "Device of scheduler is empty.";
        };
        if (mainSvc.checkUndefinedNull(scheduler) || mainSvc.checkUndefinedNull(scheduler.id)){
            return "Scheduler is empty.";
        };
        scheduler.isSaving = true;
        objectSvc.putDeviceSchedulerSettings(objId, device.id, scheduler).then(successCallback, errorCallback);
    };
    
                //get device models
    $scope.getDeviceModels = function(){
        objectSvc.getDeviceModels().then(
            function(response){
                //ставлю модель "Не определена" на первое место списка
                var ndDeviceModel = null;
                var ndDeviceModelIndex = -1;
                response.data.some(function(dm, dmindex){
                    if (dm.modelName == "NOT_ASSIGNED"){
                        ndDeviceModel = dm;
                        ndDeviceModelIndex = dmindex;
                        return true;
                    };
                });
                if (ndDeviceModelIndex != -1){
                    response.data.splice(ndDeviceModelIndex, 1);
                    response.data.splice(0, 0, ndDeviceModel);
                };
                $scope.data.deviceModels = response.data;               
            },
            function(e){
                console.log(e);
                var errorCode = "-1";
                if (mainSvc.checkUndefinedNull(e) || mainSvc.checkUndefinedNull(e.data)){
                    errorCode = "ERR_CONNECTION";
                };
                if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
                    errorCode = e.resultCode || e.data.resultCode;
                };
                var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
                notificationFactory.errorInfo(errorObj.caption, errorObj.description);
//                notificationFactory.errorInfo(error.statusText, error.description);
            }
        );
    };
    $scope.getDeviceModels();
    
           //get data sources
    var getDatasources = function(url){
        var targetUrl = url;
        $http.get(targetUrl)
        .then(function(response){
            var tmp = response.data;      
            $scope.data.dataSources = tmp;           
        },
              function(e){
            console.log(e);
        });
    };
    getDatasources($scope.ctrlSettings.datasourcesUrl);
    
                 //get Objects
    $scope.getObjects = function(){
        objectSvc.getRmaPromise().then(function(response){
            objectSvc.sortObjectsByFullName(response.data);
            $scope.data.objects = response.data;
        });
    };
    $scope.getObjects();
    
    $scope.selectedItem = function(item){
        $scope.data.currentObject = angular.copy(item);
        $scope.data.currentObject.beginDeviceModelId = $scope.data.currentObject.deviceModelId;
    };
    
    $scope.addDevice = function(){
        $scope.data.currentObject = {};
        $scope.data.currentObject.id = null;
        $scope.data.currentObject.isManual = true;
        getDatasources($scope.ctrlSettings.datasourcesUrl);
        $('#showDeviceModal').modal();
    };
    
    $scope.editDevice = function(device){
        $scope.selectedItem(device);        
        objectSvc.getDeviceDatasources(device.contObjectId, device.id).then(
            function(resp){
                $scope.data.dataSources = angular.copy(resp.data);
                $scope.getDeviceMetaDataVzlet($scope.data.currentObject);
                $('#showDeviceModal').modal();                
            },
            errorCallback
        );
        
    };
    
    $scope.deviceDatasourceChange = function(){
        $scope.data.currentObject.dataSourceTable1h = null;
        $scope.data.currentObject.dataSourceTable24h = null;
        $scope.data.currentObject.subscrDataSourceAddr = null;
        var curDataSource = null;
        $scope.data.dataSources.some(function(source){
            if (source.id === $scope.data.currentObject.subscrDataSourceId){
                curDataSource = source;
                return true;
            };
        });
        $scope.data.currentObject.curDatasource = curDataSource;
        $timeout(function(){
            $('#inputNetAddr').inputmask();
        }, 10);
    };
    
    var successCallback = function(response){
        notificationFactory.success();
        $scope.getDevices();
        $('#showDeviceModal').modal('hide');
        $('#deleteObjectModal').modal('hide');
        $('#sheduleEditorModal').modal('hide');
        $scope.data.currentObject = {};
        $scope.data.currentScheduler = {};
    };
    
    var errorCallback = function(e){       
        console.log(e);
        var errorCode = "-1";
        if (mainSvc.checkUndefinedNull(e) || mainSvc.checkUndefinedNull(e.data)){
            errorCode = "ERR_CONNECTION";
        };
        if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
            errorCode = e.resultCode || e.data.resultCode;
        };
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
        // reset saving device flag
        $scope.data.currentObject.isSaving = false;
        //reset saving csheduler flag
        $scope.data.currentScheduler.isSaving = false;
    };
    
    $scope.saveDevice = function(device){
        //set device saving...
        device.isSaving = true;
        //check device data
        var checkDsourceFlag = true;
        if (device.contObjectId == null){
            notificationFactory.errorInfo("Ошибка","Не задан объект учета");
            checkDsourceFlag = false;
        };
        if (device.id == null && device.subscrDataSourceId == null && device.isManual){
            notificationFactory.errorInfo("Ошибка","Не задан источник данных");
            checkDsourceFlag = false;
        };
        if (device.id == null && device.deviceModelId == null && device.isManual){
            notificationFactory.errorInfo("Ошибка", "Не задана модель прибора");
            checkDsourceFlag = false;
        };
        if (checkDsourceFlag === false){
            device.isSaving = false;
            return;
        };
        if (!mainSvc.checkUndefinedNull(device.verificationDateString) || (device.verificationDateString != "")){
            device.verificationDate = mainSvc.strDateToUTC(device.verificationDateString, $scope.ctrlSettings.userDateFormat);
        }
//console.log(device);
        var userDecision =  false;
        if (!mainSvc.checkUndefinedNull(device.id) && $scope.isDirectDevice(device) && (device.beginDeviceModelId != device.deviceModelId))
            //прибор не новый, идет прямая загрузка с прибора и модель была изменена
        {
            //выдать предупреждение о том, что модель была изменена и мета данные будут стерты
            userDecision = confirm("Модель прибора была изменена. Метаданные прибора будут перезаписаны метаданными текущей модели. Продолжить?");
            if (!userDecision){
                device.isSaving = false;
                return "Save operation is canceled by user.";
            };
        }
        //send to server
        objectSvc.sendDeviceToServer(device).then(successCallback, errorCallback);
    };
    
    var setConfirmCode = function(){
        $scope.confirmCode = null;
        var tmpCode = mainSvc.getConfirmCode();
        $scope.confirmLabel = tmpCode.label;
        $scope.sumNums = tmpCode.result;                    
    };
    
    $scope.deleteObjectInit = function(device){
        $scope.selectedItem(device);
        setConfirmCode();
    };
    
    $scope.deleteObject = function(device){       
        var targetUrl = objectSvc.getRmaObjectsUrl();
        targetUrl = targetUrl + "/" + device.contObjectInfo.contObjectId + "/deviceObjects/" + device.id;
        $http.delete(targetUrl).then(successCallback, errorCallback);
    };
    
    // device metadata 
    $scope.data.deviceMetadataMeasures = objectSvc.getDeviceMetadataMeasures();   
    $scope.$on('objectSvc:deviceMetadataMeasuresLoaded', function(){
        $scope.data.deviceMetadataMeasures = objectSvc.getDeviceMetadataMeasures();       
    });
    
    $scope.getDeviceMetadata = function(objId, device){        
        objectSvc.getRmaDeviceMetadata(objId, device.id)
        .then(function(resp){
            device.metadata = resp.data;
            $scope.selectedItem(device);           
            $('#metaDataEditorModal').modal();
        }, errorCallback);
    };
    
    $scope.updateDeviceMetaData = function(device){
        device.isSaving = true;
        var method = "PUT";
        var url = objectSvc.getRmaObjectsUrl() + "/" + device.contObjectId + "/deviceObjects/" + device.id + "/metadata";
        $http({
            url: url,
            method: method,
            data: device.metadata
        })
        .then(
            function(response){
                $scope.currentObject =  {};
                $('#metaDataEditorModal').modal('hide');
                notificationFactory.success();
            },
            errorCallback
        );
    };
    
        //check device: data source vzlet or not?
    $scope.vzletDevice = function(device){
        var result = false;
        if(angular.isDefined(device.activeDataSource) && (device.activeDataSource != null)){
            if(angular.isDefined(device.activeDataSource.subscrDataSource) && (device.activeDataSource.subscrDataSource != null)){
                if (device.activeDataSource.subscrDataSource.dataSourceTypeKey == "VZLET"){
                    result = true;
                };
            };
        };
        return result;
    };
    
    $scope.isDirectDevice = function(device){
        var result = false;
        if(angular.isDefined(device.activeDataSource) && (device.activeDataSource != null)){
            if(angular.isDefined(device.activeDataSource.subscrDataSource) && (device.activeDataSource.subscrDataSource != null)){
                if (device.activeDataSource.subscrDataSource.dataSourceTypeKey == "DEVICE"){
                    result = true;
                };
            };
        };
        return result;
    };
    
    //get device meta data and show it
    $scope.getDeviceMetaDataVzlet = function(device){
        if (mainSvc.checkUndefinedNull(device)){
            var errorMsg = "getDeviceMetaDataVzlet: Device undefined or null.";
            console.log(errorMsg);
            return errorMsg;
        };
        objectSvc.getRmaDeviceMetaDataVzlet(device.contObjectInfo.contObjectId, device).then(
            function(response){                           
                device.metaData = response.data; 
            },
            errorCallback
        );
    };

     //get the list of the systems for meta data editor
    $scope.getVzletSystemList = function(){
        var tmpSystemList = objectSvc.getVzletSystemList();
        if (tmpSystemList.length === 0){
            objectSvc.getDeviceMetaDataVzletSystemList()
                .then(
                function(response){
                    $scope.data.vzletSystemList = response.data;                           
                },
                errorCallback
            );
        }else{
            $scope.data.vzletSystemList = tmpSystemList;
        };       
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
    
    //keydown listener
    $scope.$on('$destroy', function() {
        window.onkeydown = undefined;
    }); 
    
    //keydown listener for ctrl+end
    window.onkeydown = function(e){
//console.log(e);        
        if (e.code == "ArrowUp" /*e.keyCode == 38*/){                        
            var elem = document.getElementById("divWithDeviceTable");
            elem.scrollTop = elem.scrollTop - 20;                        
            return;
        };
        if (e.code == "ArrowDown" /*e.keyCode == 40*/){
            var elem = document.getElementById("divWithDeviceTable");
            elem.scrollTop = elem.scrollTop + 20;                        
            return;
        };
        if (e.code == "PageDown" /*e.keyCode == 34*/){
            var elem = document.getElementById("divWithDeviceTable");
            elem.scrollTop = elem.scrollTop + 34*10;                        
            return;
        };
        if (e.code == "PageUp" /*e.keyCode == 33*/){
            var elem = document.getElementById("divWithDeviceTable");
            elem.scrollTop = elem.scrollTop - 34*10;
            return;
        };
        if (e.ctrlKey && e.code == "Home" /*e.keyCode == 36*/){
            var elem = document.getElementById("divWithDeviceTable");
            elem.scrollTop = 0;
            return;
        };
        if ((e.ctrlKey && e.code == "End" /*e.keyCode == 35*/)){ 
            var elem = document.getElementById("divWithDeviceTable");
            elem.scrollTop = elem.scrollHeight;                                            

        };
    };
    
    
    //checkers
    $scope.isSystemuser = function(){
        return mainSvc.isSystemuser();
    };
    
    $scope.isDeviceDisabled = function(device){
//console.log(device);        
//        return !device.isManual;
        return device.exSystemKeyname == 'VZLET' || device.exSystemKeyname == 'LERS' || (device.isSaving == true);
    };
    
    $scope.isDeviceProtoLoaded = function(device){
//console.log(device);        
//        return !device.isManual;
        return device.exSystemKeyname == 'VZLET' || device.exSystemKeyname == 'LERS';
    };
    
    $scope.checkHHmm = function(hhmmValue){
        return mainSvc.checkHHmm(hhmmValue);
    };
    
    $scope.checkPositiveNumberValue = function(numvalue){
        return mainSvc.checkPositiveNumberValue(numvalue);
    };
    
    $scope.checkScheduler = function(scheduler){
        return $scope.checkHHmm(scheduler.loadingInterval)
            && $scope.checkHHmm(scheduler.loadingRetryInterval)
            && $scope.checkPositiveNumberValue(scheduler.loadingAttempts)
    };
    
    $(document).ready(function(){
        $('#inputVerificationInterval').inputmask();
        $('#inputVerificationDate').datepicker({
          dateFormat: "dd.mm.yy",
          firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
          dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
          monthNames: $scope.dateOptsParamsetRu.locale.monthNames,
            beforeShow: function(){
                setTimeout(function(){
                    $('.ui-datepicker-calendar').css("display", "table");
                }, 1);
            },
          onChangeMonthYear: function(){
                setTimeout(function(){
                    $('.ui-datepicker-calendar').css("display", "table");
                }, 1);
            }
        });
        $('#inputAttemptsNumberShd').inputmask();        
        $('#inputNetAddr').inputmask();
    });
}]);