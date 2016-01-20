//ManagementServicesCtrl
'use strict';
angular.module('portalNMC')
.filter('viewHistory', function(){
    return function(input, revisionFlag){
        if (angular.isArray(input, revisionFlag)){
            var tmpArr = [];
            if(revisionFlag){
                return input;
            };
            input.forEach(function(el){
                if ((angular.isUndefined(el.factEndDate)) && (!el.isArchive)){
                    tmpArr.push(el);
                };
            });
            return tmpArr;
        }
    }
});

angular.module('portalNMC')
.controller('MngmtPriceCtrl', ['$scope', '$http', 'mainSvc', 'notificationFactory', '$log', function($scope, $http, mainSvc, notificationFactory, $log){
    console.log("MngmtPriceCtrl run.");
    //messages & titles
    $scope.messages = {};
    $scope.messages.priceMenuItem1 = "Активировать";
    $scope.messages.priceMenuItem2 = "Назначить абонентам";
    $scope.messages.priceMenuItem3 = "Редактировать цены";
    $scope.messages.priceMenuItem4 = "Копировать";
    $scope.messages.priceMenuItem5 = "Удалить";
    $scope.messages.priceMenuItem6 = "Свойства";
    $scope.messages.priceMenuItem7 = "Посмотреть цены";
    
    //ctrl settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.dateFormat = "DD.MM.YYYY"; //date format
    $scope.ctrlSettings.modeView = false; // the price list mode view: false - r/w, true - r 
    
    $scope.ctrlSettings.subscrUrl = "../api/subscr";
    $scope.ctrlSettings.rmaUrl = "../api/rma";
    $scope.ctrlSettings.priceSuffix = "/priceList";
    $scope.ctrlSettings.clientsUrl = "../api/rma/subscribers";
    $scope.ctrlSettings.rmaClientsUrl = "../api/rma/priceList/rma";
    $scope.ctrlSettings.modesUrl = "../api/rma/priceList/subscribers";
    $scope.ctrlSettings.servicesUrl = $scope.ctrlSettings.subscrUrl+"/manage/service";
    $scope.ctrlSettings.packagesUrl = $scope.ctrlSettings.servicesUrl + "/servicePackList";
    $scope.ctrlSettings.itemsUrl = $scope.ctrlSettings.servicesUrl+ "/serviceItemList";
    $scope.ctrlSettings.priceUrl = $scope.ctrlSettings.servicesUrl+ "/servicePriceList";
//    /api/rma/%d/priceList/%d/items
    $scope.ctrlSettings.accountServicesUrl = $scope.ctrlSettings.servicesUrl+ "/access";
    $scope.ctrlSettings.subscriberContObjectCountUrl = $scope.ctrlSettings.subscrUrl+ "/info/subscriberContObjectCount";
    
    $scope.ctrlSettings.MASTER_RMA_ID = 0;
    
    $scope.ctrlSettings.priceListColumns = [
        {
            "name": "priceListName",
            "caption": "Наименование",
            "class": "col-xs-3 col-md-3"
        },{
            "name": "factBeginDate",
            "caption": "Дата ввода",
            "class": "col-xs-1 col-md-1"
        },{
            "name": "factEndDate",
            "caption": "Дата завершения",
            "class": "col-xs-1 col-md-1"
        }
    ];
    
    $scope.ctrlSettings.subscriberContObjectCount = null;
    
    $scope.ctrlSettings.currency = "";//"y.e."; 
    
    //var initialization
        //available service packages
    $scope.availablePackages = null;
        //service packages which is edition on form
    $scope.serviceListEdition = null;
    
    //The packages, which available for the subscriber
    $scope.availablePackages = [];
    
    //data
    $scope.data= {};
    $scope.data.currentClient = {};
    $scope.data.currentPrice = {};
    //mode - one of the list: rma or one of the rma clients
    $scope.data.currentMode = {};
    $scope.data.priceModes = [];
    $scope.data.clients = [];
    $scope.data.clientsAtWindow = [];
    $scope.data.prices = [];
    
    //---------------------------- Prices ----------------------
        //callbacks
     var successCallback = function (e, cb) {                    
        notificationFactory.success();
        $('#deletePriceModal').modal('hide');
        $('#pricePropModal').modal('hide');
        $('#clonePriceModal').modal('hide');
        $('#editPriceModal').modal('hide');
        $scope.data.currentPrice = {};
        getModePrices($scope.data.currentMode.id);
    };

    var errorCallback = function (e) {
//        notificationFactory.errorInfo(e.statusText,e.data.description || e.data); 
        console.log(e.data);
        var errorCode = "-1";
        if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
            errorCode = e.resultCode || e.data.resultCode;
        };
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };
    
    var getModePrices = function(subscrId){
        var targetUrl = $scope.ctrlSettings.rmaUrl+"/"+subscrId+$scope.ctrlSettings.priceSuffix;
//console.log(targetUrl);        
        $http.get(targetUrl)
        .then(function(response){
            if (angular.isUndefined(response.data)||(response.data == null)|| !angular.isArray(response.data)){
                return false;
            };
            $scope.data.prices = response.data;
//console.log($scope.data.prices);            
        },
              errorCallback/*function(e){
            notificationFactory.errorInfo(e.status, e.data);
            console.log(e);
        }*/);
    };
    
    $scope.cloneInit = function(pl){
        $scope.selectItem(pl);
        $scope.data.clientsAtWindow = angular.copy($scope.data.clients);
    };
    
    $scope.selectedAllPressed = function(){
        $scope.data.selectedAll = !$scope.data.selectedAll;
        $scope.data.clientsAtWindow.forEach(function(el){
            el.selected = $scope.data.selectedAll;
        });
    };
    $scope.activatedAllPressed = function(){
        $scope.data.activatedAll = !$scope.data.activatedAll;
        $scope.data.clientsAtWindow.forEach(function(el){
            if (el.selected){
                el.activated = $scope.data.activatedAll;
            };
        });
    };
    
    $scope.selectItem = function(object){
        $scope.data.currentPrice = angular.copy(object);
    };
    
    $scope.dblClickPriceList = function(pl){
        $scope.selectItem(pl);
        $('#pricePropModal').modal();
    };
    
    $scope.deletePriceInit = function(object){
        $scope.selectItem(object);
        //generation confirm code
        $scope.confirmCode = null;
        var tmpCode = mainSvc.getConfirmCode();
        $scope.confirmLabel = tmpCode.label;
        $scope.sumNums = tmpCode.result;
    };
    
    $scope.deletePrice = function(object){
        var targetUrl = $scope.ctrlSettings.rmaUrl+"/"+$scope.data.currentMode.id+$scope.ctrlSettings.priceSuffix+"/"+object.id;
        $http.delete(targetUrl).then(successCallback, errorCallback)
        
    };
    
    $scope.savePriceProp = function(object){
        var targetUrl = $scope.ctrlSettings.rmaUrl+"/"+$scope.data.currentMode.id+$scope.ctrlSettings.priceSuffix+"/"+object.id;
        $http.put(targetUrl, object).then(successCallback, errorCallback)
        
    };
    
    $scope.copyPriceList = function(srcPrice){
        var targetUrl = $scope.ctrlSettings.rmaUrl+"/"+$scope.data.currentMode.id+$scope.ctrlSettings.priceSuffix+"/?srcPriceListId="+srcPrice.id;
        $http.post(targetUrl, null).then(successCallback, errorCallback);
    };
    
    $scope.clonePriceList = function(){
        //get selected clients ids
        var tmpSelected = [];
        $scope.data.clientsAtWindow.forEach(function(el){
            if(el.selected){
                tmpSelected.push(el.id);
            };
        });
        //get activated clients ids
        var tmpActivated = [];
        $scope.data.clientsAtWindow.forEach(function(el){
            if(el.activated){
                tmpActivated.push(el.id);
            };
        });  
        if (tmpSelected.length == 0){
            notificationFactory.info("Не выбрано ни одного абонента!");
            return 1;
        };
//console.log(tmpSelected);        
        var targetUrl = $scope.ctrlSettings.rmaUrl+"/"+$scope.data.currentMode.id+$scope.ctrlSettings.priceSuffix+"/"+ $scope.data.currentPrice.id+"/subscr";
        $http({
            method: "POST",
            url: targetUrl,
            params: {
                subscriberIds: tmpSelected,
                activeIds: tmpActivated
            }
        })
            .then(successCallback, errorCallback);
        
    };
    
    $scope.activatePriceList = function(pl){
        $scope.selectItem(pl);
        var targetUrl = $scope.ctrlSettings.rmaUrl+"/"+$scope.data.currentMode.id+$scope.ctrlSettings.priceSuffix+"/"+ $scope.data.currentPrice.id+"/activate";
        $http.put(targetUrl).then(successCallback, errorCallback);
    };
    //----------------------------------------------------------
    
        //    get subscribers
    var getClients = function(url){
        var targetUrl = url;//$scope.ctrlSettings.clientsUrl;
        $http.get(targetUrl)
        .then(function(response){
            if (angular.isUndefined(response.data)||(response.data == null)|| !angular.isArray(response.data)){
                return false;
            };
//console.log(response.data);            
            response.data.forEach(function(el){
                el.organizationName = (angular.isDefined(el.organization)&&(el.organization!=null))? el.organization.organizationFullName : el.subscriberName;
            });
            $scope.data.clients = response.data;   
//$log.debug("(MngmtPriceCtrl: 237) Subscriber list:",$scope.data.clients);            
        },
             function(e){
            console.log(e);
        });
    };
    
    
    var getModes = function(){
        var targetUrl = $scope.ctrlSettings.modesUrl;
        $http.get(targetUrl)
        .then(function(response){
            if (angular.isUndefined(response.data)||(response.data == null)|| !angular.isArray(response.data)){
                return false;
            };
            $scope.data.priceModes = angular.copy(response.data);
//console.log($scope.data.priceModes);            
                //set current mode - rma mode
            $scope.data.currentMode = angular.copy($scope.data.priceModes[0]);           
            //get prices for rma
            getModePrices($scope.data.currentMode.id);
            var targetUrl = $scope.ctrlSettings.clientsUrl;
            if ($scope.data.currentMode.id == $scope.ctrlSettings.MASTER_RMA_ID){
                targetUrl = $scope.ctrlSettings.rmaClientsUrl;
            };
            getClients(targetUrl);
        },
             function(e){
            console.log(e);
        });
    };
    getModes();
    
    $scope.changeMode = function(modeId){      
        var mode = null;
        $scope.data.priceModes.some(function(el){
            if (el.id == modeId){
                mode = angular.copy(el);
                return true;
            };
        });
        $scope.data.currentMode = angular.copy(mode); 
//console.log($scope.data.currentMode);        
        getModePrices($scope.data.currentMode.id);
        var targetUrl = $scope.ctrlSettings.clientsUrl;
        if ($scope.data.currentMode.id == $scope.ctrlSettings.MASTER_RMA_ID){
            targetUrl = $scope.ctrlSettings.rmaClientsUrl;
        };               
        getClients(targetUrl);
    };
    
    //get subscriber contObject count
    $scope.getSubscriberContObjectCount = function(url){
        var targetUrl = url;
        $http.get(targetUrl)
        .then(function(response){
            $scope.ctrlSettings.subscriberContObjectCount = response.data;
        },
              function(e){
            console.log(e);
        });
    };
    $scope.getSubscriberContObjectCount($scope.ctrlSettings.subscriberContObjectCountUrl);
    //get price list
    $scope.getPriceList = function(url){
        var targetUrl = url;
        $http.get(targetUrl).then(function(response){
            var tmpPrices = angular.copy(response.data);   
//console.log(response.data);            
            if (angular.isUndefined(tmpPrices)||(tmpPrices == null)|| !angular.isArray(tmpPrices)){
                return false;
            };
//            $scope.availablePackages.forEach(function(pack){
            $scope.serviceListEdition.forEach(function(pack){    
                tmpPrices.forEach(function(price){
                    if ((price.packId === pack.id) && (angular.isUndefined(price.itemId)||(price.itemId === null))){
                        pack.price = price;
                        $scope.ctrlSettings.currency = price.currency;
                        if (angular.isDefined(price.priceValue)&&(angular.isNumber(price.priceValue))){
                            pack.isAvailable = true;
                        };
                    };
                });
                pack.serviceItems.forEach(function(svitem){
                     tmpPrices.forEach(function(price){
                        if ((angular.isUndefined(price.packId)||(price.packId === null)) && (price.itemId === svitem.id)){
                            svitem.price = price;
                            $scope.ctrlSettings.currency = price.currency;
                            if (angular.isDefined(price.priceValue)&&(angular.isNumber(price.priceValue))){
                                svitem.isAvailable = true;
                            };
                        };
                     });
                });
            });
            $scope.ctrlSettings.currency ="";           
            $('#editPriceModal').modal();        
//console.log($scope.serviceListEdition);            
        },
                                 function(e){
            console.log(e);
        });
    };
    
    //get packages
    $scope.getPackages = function(url){
        var targetUrl = url;
        $http.get(targetUrl)
            .then(function(response){
                    var tmp = response.data;
//console.log(tmp)            
                    $scope.availablePackages = tmp;// [tmp, angular.copy(tmp)];
        },
                 function(e){
                    console.log(e);
        });
    };
    
    //set popup with defined params
    var setQtip = function(elem, text){
        $(elem).qtip({
            suppress: false,
            content:{
                text: text,
                button : true
            },
            show:{
                event: 'click'
            },
            style:{
                classes: 'qtip-nmc-indicator-tooltip',
                width: 1000
            },
            hide: {
                event: 'unfocus'
            },
            position:{
                my: 'top left',
                at: 'bottom left'
            }
        });
    };
    
    $scope.getPackages($scope.ctrlSettings.packagesUrl);
    
    //save changes
        //prepare price list to send to server
    var prepareData = function(packageList){
        var result = null;
        var tmp = [];
        $scope.serviceListEdition.forEach(function(pack){
            if (angular.isDefined(pack.price)&&(pack.price!=null)){
                if (angular.isUndefined(pack.price.id)||(pack.price.id == null)){
                    pack.price.packId = pack.id;
                    pack.price.priceValue = Number(pack.price.priceValue);
                };
                tmp.push(pack.price);
            };
            pack.serviceItems.forEach(function(serv){
                if (angular.isDefined(serv.price)&&(serv.price!=null)){
                    if (angular.isUndefined(serv.price.id)||(serv.price.id == null)){
                        serv.price.itemId = serv.id;
                        serv.price.priceValue = Number(serv.price.priceValue);
                    };
                    tmp.push(serv.price);
                };
            });
        });
        result = tmp;
        return result;
    };
    
    $scope.updatePriceList = function(){
        //var targetUrl = $scope.ctrlSettings.accountServicesUrl;
        var targetUrl = $scope.ctrlSettings.rmaUrl+"/"+$scope.data.currentMode.id+$scope.ctrlSettings.priceSuffix+"/"+$scope.data.currentPrice.id+"/items";
        var data = [];
//console.log($scope.serviceListEdition);        
        data = prepareData($scope.serviceListEdition);
        var checkPrice = true;      
        data.some(function(el){
            if (!$scope.checkNumericValue(el.priceValue)){
//console.log(el);                
                notificationFactory.errorInfo("Ошибка!","Неправильно задана цена услуги!");
                checkPrice = false;
                return true;
            };
        });
        if (!checkPrice){
            return false;
        };
//console.log(data);        
//return;        
        $http.put(targetUrl, data).then(successCallback,errorCallback);
    };
    
    //toggle show/hide package consist
    $scope.toggleShowGroupDetails = function(pack){
        pack.showDetailsFlag = !pack.showDetailsFlag;
    };
    
    //Edit button onClick listener
    $scope.editPrice = function(priceList){
        //generate confirm code
        $scope.confirmCode = null;
        var tmpCode = mainSvc.getConfirmCode();
        $scope.confirmLabel = tmpCode.label;
        $scope.sumNums = tmpCode.result;        
        $scope.viewPrice(priceList, false)
//        $scope.ctrlSettings.modeView = false;
//        $scope.selectItem(priceList);
//        $scope.serviceListEdition = angular.copy($scope.availablePackages);
//        var targetUrl = $scope.ctrlSettings.rmaUrl+"/"+$scope.data.currentMode.id+$scope.ctrlSettings.priceSuffix+"/"+$scope.data.currentPrice.id+"/items";
//        $scope.getPriceList(targetUrl);
        //$('#editPriceModal').modal();        
    };
    
    //view price list
    $scope.viewPrice = function(priceList, mode){
        $scope.ctrlSettings.modeView = mode;
        $scope.selectItem(priceList);
        $scope.serviceListEdition = angular.copy($scope.availablePackages);
        var targetUrl = $scope.ctrlSettings.rmaUrl+"/"+$scope.data.currentMode.id+$scope.ctrlSettings.priceSuffix+"/"+$scope.data.currentPrice.id+"/items";
        $scope.getPriceList(targetUrl);
    };
    
    //listener on edit price modal window show
    $('#editPriceModal').on('show.bs.modal',function(e){
        window.setTimeout(function(){
                //add popup for editable packages and services
            $scope.availablePackages.forEach(function(pack){
                var elDOM = "#editpackage"+pack.id;
//console.log($(elDOM));
                setQtip(elDOM, pack.packDescription);
                pack.serviceItems.forEach(function(serv){
                    var servDOM = "#editservice"+serv.id;
                    setQtip(servDOM, serv.itemDescription);
                });

            });
                //apply mask
            $("input[name='inputPrice']").inputmask();
        }, 500);
    });
    
    //check user
    $scope.isSystemuser = function(){
        return mainSvc.isSystemuser();
    };
    
    //check numeric value
    $scope.checkNumericValue = function(numvalue){
        return mainSvc.checkNumericValue(numvalue);
    };
    
    //date picker
    $scope.dateOptsParamsetRu ={
        locale : {
            daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
            firstDay : 1,
            monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                    'Октябрь', 'Ноябрь', 'Декабрь' ]
        },
        singleDatePicker: true,
        format: $scope.ctrlSettings.dateFormat
    };
    $(document).ready(function() {
        $('#inputPlanBeginDate').datepicker({
          dateFormat: "yy-mm-dd",
          firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
          dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
          monthNames: $scope.dateOptsParamsetRu.locale.monthNames
        });
        $('#inputPlanEndDate').datepicker({
          dateFormat: "yy-mm-dd",
          firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
          dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
          monthNames: $scope.dateOptsParamsetRu.locale.monthNames
        });                  
    });
    
    //set mask for price value
    $(document).ready(function(){
        $(':input').inputmask();
    });
    
    //set setting for history toggle
    $(document).ready(function(){
        $('#history-view').bootstrapToggle({
            on: "да",
            off: "нет"
        });
        $('#history-view').change(function(){
            $scope.ctrlSettings.viewHistoryFlag = Boolean($(this).prop('checked'));
            $scope.$apply();
        });
    });
    
    //key listener for pricelist properties window
    $('#pricePropModal').on('shown.bs.modal', function () {
        $('#inputName').focus();
        $('#pricePropModal').keydown(function(e){          
            if(e.keyCode==13){ //enter press
                $scope.savePriceProp($scope.data.currentPrice);
                $scope.$apply();
            };
        });
    });
    
    $('#pricePropModal').on('hide.bs.modal', function(){
        $('#pricePropModal').off('keydown');
    });
    
    //check access to buttons
    //properties - always
    //view price - always
    //activate - systemuser & (rmaMode || subscrMode) & !isActive & !isArchive || isRma & subscrMode & !isActive & !isArchive =   
    // = (systemuser & (rmaMode || subscrMode) || isRma & subscrMode) & !isActive & !isArchive
    
    //clone - rmaMode & !isArchive
    //copy - !isArchive
    //edit - !rma & !isArchive & !isDraft
    //delete - !isDraft & !isArchive
    $scope.checkPropBtn = function(priceList){
        return true;
    };
    
    $scope.checkViewBtn = function(priceList){
        return true;
    };
    
    $scope.checkActivateBtn = function(priceList){
        var result = ($scope.isSystemuser() && ($scope.data.currentMode.id!=0) || !$scope.isSystemuser()&&$scope.isRma() && !$scope.data.currentMode.rma) & !priceList.isActive && !priceList.isArchive;
        return result;
    };
    
    $scope.checkDeleteBtn = function(priceList){
        return priceList.isDraft && !priceList.isArchive;
    };
    
    $scope.checkEditBtn = function(priceList){
        return priceList.isDraft && !priceList.isArchive;
    };
    
    $scope.checkCopyBtn = function(priceList){
        return !priceList.isArchive;
    };
    $scope.checkCloneBtn = function(priceList){
        return priceList.isDraft && !priceList.isArchive && $scope.data.currentMode.rma;
    };
    
}]);