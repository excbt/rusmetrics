'use strict';
angular.module('portalNMC')
.controller('MngmtDevicesCtrl', ['$rootScope', '$scope','$http', 'objectSvc', 'notificationFactory', 'mainSvc', function($rootScope, $scope, $http, objectSvc, notificationFactory, mainSvc){
console.log('Run devices management controller.');
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
            header: 'Единици измерения',
            headClass : 'col-xs-1 col-md-1',
            name: 'srcMeasureUnit',
            type: 'select',
            disabled: false
        }
    ];
            //get devices
    function sortDevicesByConObjectFullName(array){
            if (angular.isUndefined(array) || (array == null)|| !angular.isArray(array)){
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
                        elem.subscrDataSourceId = elem.activeDataSource.subscrDataSource.id;
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
            function(error){
                console.log(error.data);
                notificationFactory.errorInfo(error.statusText, error.data.description || error.data);
            }
        );
    };
    $scope.getDevices();
    
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
            function(error){
                notificationFactory.errorInfo(error.statusText, error.description);
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
        $('#showDeviceModal').modal();
    };
    $scope.editDevice = function(device){
        $scope.selectedItem(device);
        $('#showDeviceModal').modal();
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
    };
    
    var successCallback = function(response){
        notificationFactory.success();
        $scope.getDevices();
        $('#showDeviceModal').modal('hide');
        $('#deleteObjectModal').modal('hide');
        $scope.data.currentObject = {};
    };
    
    var errorCallback = function(e){       
        console.log(e);
        var errorCode = "-1";
        if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
            errorCode = e.resultCode || e.data.resultCode;
        };
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };
    
    $scope.saveDevice = function(device){ 
        //check device data
        var checkDsourceFlag = true;
        if (device.contObjectId==null){
            notificationFactory.errorInfo("Ошибка","Не задан объект учета");
            checkDsourceFlag = false;
        };
        if (device.subscrDataSourceId==null){
            notificationFactory.errorInfo("Ошибка","Не задан источник данных");
            checkDsourceFlag = false;
        };
        if (checkDsourceFlag === false){
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
                $scope.currentDevice = device;                           
                $('#vzletMetaDataEditorModal').modal();
            },
            errorCallback
        );
    };

    $scope.updateDeviceMetaDataVzlet = function(device){   
        var method = "";
        if(angular.isDefined(device.metaData.id) && (device.metaData.id !== null)){
            method = "PUT";
        }else{
            method = "POST";
        };
        var url = objectSvc.getRmaObjectsUrl() + "/" + device.contObjectId + "/deviceObjects/" + device.id + "/metaVzlet";
        $http({
            url: url,
            method: method,
            data: device.metaData
        })
        .then(
            function(response){
                $scope.currentDevice =  {};
                $('#vzletMetaDataEditorModal').modal('hide');
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
    
    $scope.isSystemuser = function(){
        return mainSvc.isSystemuser();
    };
    
    $(document).ready(function(){
        $('#inputVerificationInterval').inputmask();
        $('#inputVerificationDate').datepicker({
          dateFormat: "dd.mm.yy",
          firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
          dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
          monthNames: $scope.dateOptsParamsetRu.locale.monthNames
        });
    });
}]);