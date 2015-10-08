//ManagementServicesCtrl
'use strict';
angular.module('portalNMC')
.controller('ManagementServicesCtrl', ['$scope', '$http', 'mainSvc', function($scope, $http, mainSvc){
    console.log("ManagementServicesCtrl run.");
    //ctrl settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.dateFormat = "DD.MM.YYYY"; //date format
    
    $scope.ctrlSettings.subscrUrl = "../api/subscr";
    $scope.ctrlSettings.servicesUrl = $scope.ctrlSettings.subscrUrl+"/manage/service";
    $scope.ctrlSettings.packagesUrl = $scope.ctrlSettings.servicesUrl + "/servicePackList";
    $scope.ctrlSettings.itemsUrl = $scope.ctrlSettings.servicesUrl+ "/serviceItemList";
    $scope.ctrlSettings.priceUrl = $scope.ctrlSettings.servicesUrl+ "/servicePriceList";
    $scope.ctrlSettings.accountServicesUrl = $scope.ctrlSettings.servicesUrl+ "/access";
    $scope.ctrlSettings.subscriberContObjectCountUrl = $scope.ctrlSettings.subscrUrl+ "/info/subscriberContObjectCount";
    
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
        $scope.firstNum = Math.round(Math.random()*100);
        $scope.secondNum = Math.round(Math.random()*100);
        $scope.sumNums = $scope.firstNum + $scope.secondNum;
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
    $scope.editPackages = function(){
        $scope.serviceListEdition = angular.copy($scope.availablePackages);
        initChangdFlags($scope.serviceListEdition);
        $('#editServiceListModal').modal();
    };
    
    //listener on edit service modal window show
    $('#editServiceListModal').on('show.bs.modal',function(e){
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