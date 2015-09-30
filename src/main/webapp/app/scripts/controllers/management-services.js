//ManagementServicesCtrl
'use strict';
angular.module('portalNMC')
.controller('ManagementServicesCtrl', ['$scope', '$http', function($scope, $http){
    console.log("ManagementServicesCtrl run.");
    //ctrl settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.dateFormat = "DD.MM.YYYY"; //date format
    
    $scope.ctrlSettings.servicesUrl = "../api/subscr/manage/service";
    $scope.ctrlSettings.packagesUrl = $scope.ctrlSettings.servicesUrl + "/servicePackList";
    $scope.ctrlSettings.itemsUrl = $scope.ctrlSettings.servicesUrl+ "/serviceItemList";
    $scope.ctrlSettings.priceUrl = $scope.ctrlSettings.servicesUrl+ "/servicePriceList";
    $scope.ctrlSettings.accountServicesUrl = $scope.ctrlSettings.servicesUrl+ "/access";
    
    //var initialization
        //available service packages
    $scope.availablePackages = null;
        //service packages which is edition on form
    $scope.serviceListEdition = null;
    
    //package columns definition
    //not used
    $scope.ctrlSettings.packageColumns = [
        {"name":"name", "header" : "Название", "class":"col-md-4"}
        ,{"name":"description", "header" : "Описание", "class":"col-md-5"}
        ,{"name":"cost", "header" : "Стоимость, руб.", "class":"col-md-1"}
    ];
    //The packages, wich selected by the subscriber
    $scope.packages =[];
    //The packages, which available for the subscriber
    $scope.availablePackages = [];
    
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
console.log($scope.availablePackages);            
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
        },
                                 function(e){
            console.log(e);
        });
    };
    
    $scope.getPackages($scope.ctrlSettings.packagesUrl);

    //control changes: 
    //one way
    //compare 2 package lists
    var comparePackageLists = function(originalList, reversedList){
        var result = null;
        
        originalList.forEach(function(originalPack){
            reversedList.forEach(function(reversedPack){
                
            });
        });
        return result;
    };
    
    //another way
    //control change list, when user click service list
    $scope.serviceClick = function(pack, serv){
        if (serv.selected === true){
            serv.added = true;
        }else{
            //remove service from change list
        };
        
    };
    
    $scope.packageClick = function(pack){
        pack;
    };
    
    
    //save changes
    $scope.checkPackages = function(){
        //Получить лист изменений
            //сравнить два списка : 
        $scope.availablePackages;
        $scope.serviceListEdition;
        //Вывести изменения на экран
        $('#confirmSavingModal').modal();
        //Запросить подтверждение сохранения изменений
        //send changes to server
    };
    
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
    
    //Edit button onClick listener
    $scope.editPackages = function(){
        $scope.serviceListEdition = angular.copy($scope.availablePackages);
        $('#editServiceListModal').modal();
    };
    
}]);