/*jslint white:true, node: true */
/*global angular, window, $, document */
(function () {
    'use strict';

    /**
     * @ngdoc function
     * @name portalNMC.controller:MainCtrl
     * @description
     * # MainCtrl
     * Controller of the portalNMC
     * @author Artamonov
     * @date 2015
     */
    //var app = angular.module('portalNMC');
    angular.module('portalNMC')
        .component('main', {
            templateUrl: "components/portal-nmc-module/main/main.html",
            controller: mainCtrl,
            controllerAs: "mainVm"
        });
    
    mainCtrl.$inject = [

        '$rootScope',
        '$cookies',
        '$location',
        'mainSvc',
        'notificationFactory',
        '$http',
        'objectSvc',
        'reportSvc',
        'monitorSvc',
        'logSvc',
        'APP_LABEL',
        'settingModeService',
        '$scope',
        '$mdSidenav',
        '$timeout',
        'mainHelperService'
    ];

    function mainCtrl(
                       $rootScope,
                       $cookies,
                       $location,
                       mainSvc,
                       notificationFactory,
                       $http,
                       objectSvc,
                       reportSvc,
                       monitorSvc,
                       logSvc,
                       APP_LABEL,
                       settingModeService,
                       $scope,
                       $mdSidenav,
                       $timeout,
                       mainHelperService
                      ) {
        /*jshint validthis: true*/
        var vm = this;
//        vm.data = {};
//        vm.mainCtrlSettings = {};
    //console.log("MainCtrl");      
          //main ctrl settings
        vm.mainCtrlSettings = {};
          //show on/off menu title
        vm.mainCtrlSettings.showFullMenuFlag = true;
        vm.mainCtrlSettings.loadingServicePermissionFlag = mainSvc.getLoadingServicePermissionFlag();
        vm.mainCtrlSettings.ctxId = "nmc_main";

        vm.debugModeFlag = settingModeService.getModeState();

        vm.data = {};
        vm.data.menuLabels = {
            object_menu_item: "Объекты",
            object_new_menu_item: "Объекты (NEW!)",
            report_menu_item: "Отчеты",
            setting_menu_item: "Настройки",
            admin_menu_item: "Управление",
            log_menu_item: "Журналы",
            energy_menu_item: "Энергоэффективность",
            test_menu_item: "Тест"
        };

        vm.showPrivateOfficeMenu = false;
        $rootScope.showIndicatorsParam = false;

        vm.setMenuVisibles = function (privateOfficeMenuVisible, indicatorsParamVisible) {
            vm.showPrivateOfficeMenu = privateOfficeMenuVisible;
            $rootScope.showIndicatorsParam = indicatorsParamVisible;
        };

        $rootScope.timeDetailType = "1h";//vm.timeDetailType;

           // $rootScope.endDate = ""; //"2014-03-20";//new Date();                 
           //   $rootScope.beginDate ="";// "2014-03-19";//endDate;  


        //end for indicators 

        vm.debugModeClick = function() {
            vm.debugModeFlag = !vm.debugModeFlag;
            settingModeService.setModeState(vm.debugModeFlag);
        };
        $scope.$on(settingModeService.EVENTS.modeStateChanged, function () {
            vm.debugModeFlag = settingModeService.getModeState();
        });

    //  flags for selected menu item                  
        vm.menuMassive = mainHelperService.getLeftMainMenu();

        function setPageTitle(pageName) {
            var title = APP_LABEL;
            if (!mainSvc.checkUndefinedNull(pageName)) {
                title += " - " + pageName;
            }
            document.title = title;
        }

          //Menu initialization
        function initMenu() {
            //get current location
            var loca = $location.path(),
                menuFlag = false,
                mkeys = null;
    //console.log(loca);          
            //set menu flag with current location
            vm.menuMassive.object_menu_item = (loca.indexOf("/objects/list") !== -1 ? true : false);
            vm.menuMassive.object_new_menu_item = (loca.indexOf("/objects/newlist") !== -1 ? true : false);

            vm.menuMassive.report_menu_item = (loca === "/reports" ? true : false);
            vm.menuMassive.object_menu_item = (loca.indexOf("/notices/") !== -1 ? true : false);
            vm.menuMassive.setting_menu_item = (loca.indexOf("/settings/") !== -1 ? true : false);
            vm.menuMassive.admin_menu_item = (loca.indexOf("/management/") !== -1 ? true : false);
            vm.menuMassive.log_menu_item = (loca.indexOf("/log") !== -1 ? true : false);
            vm.menuMassive.energy_menu_item = (loca.indexOf("/documents/energo-passports") !== -1 ? true : false);
            vm.menuMassive.test_menu_item = (loca.indexOf("/test") !== -1 ? true : false);

            //check menu flags
            mkeys = Object.keys(vm.menuMassive);
            mkeys.some(function(mkey){
              if (vm.menuMassive[mkey] === false) {
                  return false;
              }
              menuFlag = true;
              setPageTitle(vm.data.menuLabels[mkey]);   
              return true;
            });
            //if check menu flag is false
            if (!menuFlag){
              vm.setDefaultMenuState();//set default menu
            }         
            if ($location.path() !== "") {
              return;
            }
            //look menu flags and set location with current flag value
            if (vm.menuMassive.object_menu_item) {
              window.location.assign("#/");
            }
            if (vm.menuMassive.object_new_menu_item) {
                window.location.assign("#/objects/newlist");                
            }
            if (vm.menuMassive.report_menu_item) {
              window.location.assign("#/reports/");
            }
    //        if (vm.menuMassive.notice_menu_item){
    //         window.location.assign("#/notices/monitor/");
    //        }
            if (vm.menuMassive.setting_menu_item) {
              window.location.assign("#/settings/tariffs/");
            }
            if (vm.menuMassive.admin_menu_item) {
              window.location.assign("#/management/objects/");
            }
            if (vm.menuMassive.log_menu_item) {
              window.location.assign("#/log/");
            }
            if (vm.menuMassive.energy_menu_item) {
              window.location.assign("#/documents/energo-passports");
            }
            if (vm.menuMassive.test_menu_item) {
              window.location.assign("#/test/");
            }
        }

    //      set selected menu item
        vm.clickMenu = function(menu) {
            var mkeys = Object.keys(vm.menuMassive);
            mkeys.forEach(function (key) {
                vm.menuMassive[key] = false;
            });
    //          for (var k in vm.menuMassive){ 
    //              vm.menuMassive[k] = false;
    //          };        
             vm.menuMassive[menu] = true;             
             setPageTitle(vm.data.menuLabels[menu]);
        };

          //set default menu state
        vm.setDefaultMenuState = function () {
    //console.log("setDefaultMenuState"); 
            var mkeys = Object.keys(vm.menuMassive);
            mkeys.forEach(function (key) {
                vm.menuMassive[key] = false;
            });
    //      for (var k in vm.menuMassive){
    //              vm.menuMassive[k] = false;
    //          };        
             vm.menuMassive.object_menu_item = true;         
             setPageTitle(vm.data.menuLabels.object_menu_item);
    //console.log(window.location.href);        
    //console.log(window.location);        
        };

        $(window).bind("beforeunload", function () {
    //      console.log("beforeunload");
          vm.setDefaultMenuState();
        });

        function allRequestCancel () {
            $http.pendingRequests.forEach(function (request) {
                if (request.cancel) {
                    request.cancel.resolve();                
                    console.log("Cancelling Request " + request.method + " on URL:" + request.url);                
                }
            });
        }

        console.log("Cookies: ", $cookies.getAll());
        function removeAllCookies() {
    //        $cookies = {};
            var cooks = $cookies.getAll(),
                cookKey;
            for (cookKey in cooks) {
                if (cooks.hasOwnProperty(cookKey)) {
                    $cookies.remove(cookKey);
                }
            }
        }

        vm.logOut = function () {        
            removeAllCookies();
    //        allRequestCancel();
            //cancel all request        
            objectSvc.getRequestCanceler().resolve();
            mainSvc.getRequestCanceler().resolve();
            reportSvc.getRequestCanceler().resolve();
            monitorSvc.getRequestCanceler().resolve();
            logSvc.getRequestCanceler().resolve();
            vm.setDefaultMenuState();
        };

        initMenu();
    //      vm.setDefaultMenuState();
    //    $rootScope.$on('servicePermissions:loaded', function(event, args){
    //        vm.mainCtrlSettings.loadingServicePermissionFlag = mainSvc.getLoadingServicePermissionFlag(); 
    //    });
        //control visibles
        var setVisibles = function (ctxId) {
            var /*ctxFlag = false,*/
                tmp;
            tmp = mainSvc.getContextIds();
            tmp.forEach(function (element) {
    //            if(element.permissionTagId.localeCompare(ctxId) === 0){
    //                ctxFlag = true;
    //            }
                var elDOM = document.getElementById(element.permissionTagId);//.style.display = "block";
//console.log(element.permissionTagId);                
//console.log(elDOM);                
                if (angular.isUndefined(elDOM) || (elDOM === null)) {
                    return;
                }
                $('#' + element.permissionTagId).removeClass('nmc-hide');
            });
    //        if (ctxFlag == false){
    //            window.location.assign('#/');
    //        };
        };
        setVisibles(vm.mainCtrlSettings.ctxId);
        //listen change of service list
        $rootScope.$on('servicePermissions:loaded', function () {
            setVisibles(vm.mainCtrlSettings.ctxId);
        });

    //    window.setTimeout(function(){
    //        setVisibles(vm.ctrlSettings.ctxId);
    //    }, 500);

          //check user
//        vm.isRma = function () {
//            return mainSvc.isRma();
//        };
                           
        vm.isRma = mainSvc.isRma;
                           
        vm.isCabinet = mainSvc.isCabinet;

//        vm.getCtx = function () {
//          return $rootScope.ctxId;
//        };                

        vm.isTestMode = mainSvc.isTestMode;
//                           function () {
//    console.log("mainCtrl isTestMode = " + mainSvc.isTestMode());        
//            return mainSvc.isTestMode();
//        };
                           
        vm.menuToggleList = function () {
            $mdSidenav('left-main-menu').toggle();
        };
//                           
//        vm.openUserMenu = function ($mdMenu, ev) {
//            $mdMenu.open(ev);
//        };
//                           
//        vm.startSearch = function () {
//            vm.searchFlag = true;
//            $timeout(function () {
//                var el = document.getElementById("searchInput");
//                el.focus();
//            }, 0);
//        };
//                           
//        vm.cancelSearch = function () {
//            vm.searchFlag = false;
//            vm.mainFilter = "";
//            $rootScope.$broadcast('mainSearch:filtering', {filter: vm.mainFilter});
//        };
//                           
//        vm.isObjectsPTree = function () {
//            return vm.menuMassive.object_new_menu_item;
//        };
//                           
////        vm.mainFiltering = function (filterString) {
////            $rootScope.$broadcast('mainSearch:filtering', {filter: filterString});
////        };
//        vm.searching = function () {
//            $rootScope.$broadcast('mainSearch:filtering', {filter: vm.mainFilter});
//        };
        
    //    setPageTitle();

    //    function initCtrl () {
    //console.log(reportSvc.getRequestCanceler());        
    //    }

    //    initCtrl();

    }
}());