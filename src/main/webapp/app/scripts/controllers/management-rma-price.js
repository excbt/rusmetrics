//ManagementServicesCtrl
'use strict';
angular.module('portalNMC')
.filter('withoutOldPrices', function(){
    return function(input){
        if (angular.isArray(input, revisionFlag)){
            var tmpArr = [];
            input.forEach(function(el){
                if (revisionFlag && (angular.isUndefined(el.toDate))){
                    tmpArr.push(el);
                };
            });
            return tmpArr;
        }
    }
});

angular.module('portalNMC')
.controller('MngmtPriceCtrl', ['$scope', '$http', 'mainSvc', 'notificationFactory', function($scope, $http, mainSvc, notificationFactory){
    console.log("MngmtPriceCtrl run.");
    //messages & titles
    $scope.messages = {};
    $scope.messages.priceMenuItem1 = "Активировать";
    $scope.messages.priceMenuItem2 = "Клонировать";
    $scope.messages.priceMenuItem3 = "Редактировать";
    $scope.messages.priceMenuItem4 = "Копировать";
    $scope.messages.priceMenuItem5 = "Удалить";
    
    //ctrl settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.dateFormat = "DD.MM.YYYY"; //date format
    
    $scope.ctrlSettings.subscrUrl = "../api/subscr";
    $scope.ctrlSettings.clientsUrl = "../api/rma/subscribers";
    $scope.ctrlSettings.servicesUrl = $scope.ctrlSettings.subscrUrl+"/manage/service";
    $scope.ctrlSettings.packagesUrl = $scope.ctrlSettings.servicesUrl + "/servicePackList";
    $scope.ctrlSettings.itemsUrl = $scope.ctrlSettings.servicesUrl+ "/serviceItemList";
    $scope.ctrlSettings.priceUrl = $scope.ctrlSettings.servicesUrl+ "/servicePriceList";
    $scope.ctrlSettings.accountServicesUrl = $scope.ctrlSettings.servicesUrl+ "/access";
    $scope.ctrlSettings.subscriberContObjectCountUrl = $scope.ctrlSettings.subscrUrl+ "/info/subscriberContObjectCount";
    
    // ----------------------- dummy -------------------------
    $scope.ctrlSettings.pricesUrl = "resource/prices.json";
    // -------------------------- end dummy -------------------
    
    $scope.ctrlSettings.subscriberContObjectCount = null;
    
    $scope.ctrlSettings.currency = "y.e."; 
    
    //var initialization
        //available service packages
    $scope.availablePackages = null;
        //service packages which is edition on form
    $scope.serviceListEdition = null;
    
    //package columns definition
    //not used
//    $scope.ctrlSettings.packageColumns = [
//        {"name":"name", "header" : "Название", "class":"col-md-4"}
//        ,{"name":"description", "header" : "Описание", "class":"col-md-5"}
//        ,{"name":"cost", "header" : "Стоимость, руб.", "class":"col-md-1"}
//    ];
    //The packages, wich selected by the subscriber
    $scope.packages =[];
    //The packages, which available for the subscriber
    $scope.availablePackages = [];
    
    //data
    $scope.data= {};
    $scope.data.currentClient = {};
    $scope.data.currentMode = {};
    $scope.data.priceModes = [];
    $scope.data.clients = [];
    $scope.data.prices = [];
    
    //---------------------------- Prices ----------------------
    var getPrices = function(){
        var targetUrl = $scope.ctrlSettings.pricesUrl;
        $http.get(targetUrl)
        .then(function(response){
            $scope.data.prices = response.data;
//console.log($scope.data.prices);            
        },
              function(e){
            notificationFactory.errorInfo(e.status, e.data);
            console.log(e);
        });
    };
    getPrices();
    
    $scope.cloneInit = function(){
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
    //----------------------------------------------------------
    
        //    get subscribers
    var getClients = function(){
        var targetUrl = $scope.ctrlSettings.clientsUrl;
        $http.get(targetUrl)
        .then(function(response){
            response.data.forEach(function(el){
                el.organizationName = el.organization.organizationFullName;
            });
            $scope.data.clients = response.data;
            $scope.data.priceModes = angular.copy(response.data);
            var rmaClient = angular.copy($scope.data.clients[0]);
            rmaClient.id = 8080;
            rmaClient.isRma = true;
            rmaClient.organization.organizationFullName = "Партнерский";
            rmaClient.subscriberName = rmaClient.organization.organizationFullName;
            rmaClient.organizationName = rmaClient.organization.organizationFullName;
            $scope.data.priceModes.unshift(rmaClient);
//console.log($scope.data.clients); 
            $scope.data.currentMode = angular.copy($scope.data.priceModes[0]);
            //$scope.data.currentClient.id = $scope.data.clients[0].id;           
        },
             function(e){
            console.log(e);
        });
    };
    getClients();
    
    $scope.changeMode = function(modeId){
        var mode = null;
        $scope.data.priceModes.some(function(el){
            if (el.id == modeId){
                mode = angular.copy(el);
                return true;
            };
        });
        $scope.data.currentMode = mode;
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
            var tmpPrices = response.data;
            
            $scope.availablePackages.forEach(function(pack){
                tmpPrices.forEach(function(price){
                    if ((price.packId === pack.id) && (price.itemId === null)){
                        pack.price = price;
                    };
                });
                pack.serviceItems.forEach(function(svitem){
                     tmpPrices.forEach(function(price){
                        if ((price.packId === null) && (price.itemId === svitem.id)){
                            svitem.price = price;
                        };
                     });
                });
            });
//console.log($scope.availablePackages);            
        },
                                 function(e){
            console.log(e);
        });
    };
    
    //get packages
    $scope.getPackages = function(url){
        var targetUrl = url;
        $http.get(targetUrl).then(function(response){
            var tmp = response.data;
//console.log(tmp)            
            $scope.availablePackages = tmp;// [tmp, angular.copy(tmp)];
//            for (var i=1; i<3; i++){
//                var tmp1 = angular.copy(tmp);
//                tmp1.
//            };
            $scope.getPriceList($scope.ctrlSettings.priceUrl);
            $scope.getSelectedPackages($scope.ctrlSettings.accountServicesUrl);
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
    
    //get account services list
    $scope.getSelectedPackages = function(url){
        var targetUrl = url;
        $http.get(targetUrl).then(function(response){
            var tmp = response.data;            
            tmp.forEach(function(elem){
                $scope.availablePackages.forEach(function(pack){
                    if ((pack.id === elem.packId)&&(!elem.hasOwnProperty("itemId"))){
                        pack.selected = true;
                    };
                    pack.serviceItems.forEach(function(serv){
                        if ((pack.id === elem.packId)&&(elem.hasOwnProperty("itemId"))&&(serv.id === elem.itemId)){
                            serv.selected = true;
                        };  
                    });
                });
            });
            //add popup for packages and services
            $scope.availablePackages.forEach(function(pack){
                var elDOM = "#package"+pack.id;
//    console.log($(elDOM));
                setQtip(elDOM, pack.packDescription);
                pack.serviceItems.forEach(function(serv){
                    var servDOM = "#service"+serv.id;
                    setQtip(servDOM, serv.itemDescription);
                });

            });
        },
                                 function(e){
            console.log(e);
        });
    };
    
    $scope.getPackages($scope.ctrlSettings.packagesUrl);

    //control changes: 
    //control change list, when user click service list
    var serviceSetFlag = function(serv){
        if (serv.selected === true){
            serv.changedFlag +=1; 
        }else{
            //remove service from change list
            serv.changedFlag -=1;
        };       
    };
    //listener on service check box click
    $scope.serviceClick = function(pack, serv){
        serviceSetFlag(serv);
        //check package services, if user select all -> set pack.selected = true
        var tmpPackFlag = false;
        pack.serviceItems.some(function(serv){
            if (serv.selected!=true){
                tmpPackFlag = true;
                return true;
            };
        });
        if (tmpPackFlag === false){pack.selected = true;};
    };
    
    //get list with user service changes - "selector" = -1 -> get removed services, 1 -> get adding services
    var getAddingRemovedList = function(originalArr, selector){
        var result = [];
        if (angular.isArray(originalArr)){
            originalArr.forEach(function(pack){
                var tmpPack = angular.copy(pack);
                var addPackFlag = false;
                tmpPack.serviceItems = [];
    //            $scope.serviceAddedList.push(tmpPack);
                pack.serviceItems.forEach(function(serv){
                    if (serv.changedFlag == selector){
                        var tmpServ = angular.copy(serv);
                        tmpPack.serviceItems.push(tmpServ);
                        addPackFlag = true;
                    };
                });
                if (addPackFlag == true){result.push(tmpPack);};
            });
        };
        return result;
    };
    
    //save changes
    $scope.checkPackages = function(){
        //Получить лист изменений
        
        $scope.serviceAddedList = [];
        $scope.serviceRemovedList = [];
//console.log($scope.serviceListEdition);
        //get added services
        $scope.serviceAddedList = getAddingRemovedList($scope.serviceListEdition, 1);

//console.log($scope.serviceAddedList);  
        //get removed services
        $scope.serviceRemovedList = getAddingRemovedList($scope.serviceListEdition, -1);
//console.log($scope.serviceRemovedList); 
    
        //generation confirm code
        $scope.confirmCode = null;
//        $scope.firstNum = mainSvc.getConfirmCode().firstNum;//Math.round(Math.random()*100);
//        $scope.secondNum = mainSvc.getConfirmCode().secondNum;//Math.round(Math.random()*100);
        var tmpCode = mainSvc.getConfirmCode();
        $scope.confirmLabel = tmpCode.label;
        $scope.sumNums = tmpCode.result;//$scope.firstNum + $scope.secondNum;
        //Вывести изменения на экран
        $('#confirmSavingModal').modal();
    };
    //prepare data to send to server
    //create struct:
    //  {"packId":<packId>,
    //   "itemId":<itemId>}
    var prepareData = function(packageList){
        var result = null;
        var tmp = [];
        $scope.serviceListEdition.forEach(function(pack){
            if (pack.selected === true){
                tmp.push({"packId":pack.id});
            };
            pack.serviceItems.forEach(function(serv){
                if (serv.selected === true){
                    tmp.push({"packId":pack.id, "itemId":serv.id});
                };
            });
        });
        result = tmp;
        return result;
    };
    
    $scope.savePackages = function(){
        var targetUrl = $scope.ctrlSettings.accountServicesUrl;
        var data = null;
        data = prepareData($scope.serviceListEdition);
//console.log(data);        
//return;        
        $http.put(targetUrl, data)
        .then(function(response){
                location.reload();
        },
             function(e){
                notificationFactory.errorInfo(e.statusText,e.data.description);
                console.log(e);
        });
    };
    
    //toggle show/hide package consist
    $scope.toggleShowGroupDetails = function(pack){
        pack.showDetailsFlag = !pack.showDetailsFlag;
    };
    
    var initChangdFlags = function(plist){
//        plist.changeFlag = 0;
        plist.forEach(function(pack){
            pack.changedFlag = 0;
            pack.serviceItems.forEach(function(serv){
                serv.changedFlag = 0;
            });
        });
        
    };
    
    //Edit button onClick listener
    $scope.editPrice = function(){
        $scope.serviceListEdition = angular.copy($scope.availablePackages);
        initChangdFlags($scope.serviceListEdition);
        $('#editPriceModal').modal();
    };
    
    //listener on edit service modal window show
    $('#editPriceModal').on('show.bs.modal',function(e){
        window.setTimeout(function(){
                //add popup for editable packages and services
            $scope.serviceListEdition.forEach(function(pack){
                var elDOM = "#editpackage"+pack.id;
//console.log($(elDOM));
                setQtip(elDOM, pack.packDescription);
                pack.serviceItems.forEach(function(serv){
                    var servDOM = "#editservice"+serv.id;
                    setQtip(servDOM, serv.itemDescription);
                });

            });
        }, 500);
    });
    
    //listener on package checkbox click
    $scope.packClick = function(pack){
        if (pack.selected === true){
            pack.changedFlag +=1; 
        }else{
            //remove service from change list
            pack.changedFlag -=1;
        };
//        if (pack.selected == true){
            if (angular.isArray(pack.serviceItems)){
                pack.serviceItems.forEach(function(serv){
//console.log(serv);                    
                    if (serv.selected !== pack.selected){
                        serv.selected = pack.selected;
                        serviceSetFlag(serv);
                    };
                    
                });
            };
//        };
    };
    
    //check user
    $scope.isSystemuser = function(){
        return mainSvc.isSystemuser();
    };
    
}]);