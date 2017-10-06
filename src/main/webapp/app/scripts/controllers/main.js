/*jslint white:true, node: true */
/*global angular, window, $, document */
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
var app = angular.module('portalNMC');
app.controller('MainCtrl', ['$scope', '$rootScope', '$cookies', '$location', 'mainSvc', 'notificationFactory', '$http', 'objectSvc', 'reportSvc', 'monitorSvc', 'logSvc', 'APP_LABEL', function ($scope, $rootScope, $cookies, $location, mainSvc, notificationFactory, $http, objectSvc, reportSvc, monitorSvc, logSvc, APP_LABEL) {
//console.log("MainCtrl");      
      //main ctrl settings
    $scope.mainCtrlSettings = {};
      //show on/off menu title
    $scope.mainCtrlSettings.showFullMenuFlag = true;
    $scope.mainCtrlSettings.loadingServicePermissionFlag = mainSvc.getLoadingServicePermissionFlag();
    $scope.mainCtrlSettings.ctxId = "nmc_main";
    
    $scope.debugModeFlag = false;
      
    $scope.data = {};
    $scope.data.menuLabels = {
        object_menu_item: "Объекты",
        object_new_menu_item: "Объекты (NEW!)",
        report_menu_item: "Отчеты",
        setting_menu_item: "Настройки",
        admin_menu_item: "Управление",
        log_menu_item: "Журналы",
        energy_menu_item: "Энерго - эффективность",
        test_menu_item: "Тест"
    };

    $scope.showPrivateOfficeMenu = false;
    $rootScope.showIndicatorsParam = false;
      
    $scope.setMenuVisibles = function (privateOfficeMenuVisible, indicatorsParamVisible) {
        $scope.showPrivateOfficeMenu = privateOfficeMenuVisible;
        $rootScope.showIndicatorsParam = indicatorsParamVisible;
    };
      
    $rootScope.timeDetailType = "1h";//$scope.timeDetailType;

       // $rootScope.endDate = ""; //"2014-03-20";//new Date();                 
       //   $rootScope.beginDate ="";// "2014-03-19";//endDate;  

      
    //end for indicators 
    
    $scope.debugModeClick = function() {
        $scope.debugModeFlag = !$scope.debugModeFlag;       
    };
 
//  flags for selected menu item                  
    $scope.menuMassive = {};
    
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
        $scope.menuMassive.object_menu_item = (loca.indexOf("/objects/list") !== -1 ? true : false);
        $scope.menuMassive.object_new_menu_item = (loca.indexOf("/objects/newlist") !== -1 ? true : false);
        
        $scope.menuMassive.report_menu_item = (loca === "/reports" ? true : false);
        $scope.menuMassive.object_menu_item = (loca.indexOf("/notices/") !== -1 ? true : false);
        $scope.menuMassive.setting_menu_item = (loca.indexOf("/settings/") !== -1 ? true : false);
        $scope.menuMassive.admin_menu_item = (loca.indexOf("/management/") !== -1 ? true : false);
        $scope.menuMassive.log_menu_item = (loca.indexOf("/log") !== -1 ? true : false);
        $scope.menuMassive.energy_menu_item = (loca.indexOf("/documents/energo-passports") !== -1 ? true : false);
        $scope.menuMassive.test_menu_item = (loca.indexOf("/test") !== -1 ? true : false);
        
        //check menu flags
        mkeys = Object.keys($scope.menuMassive);
        mkeys.some(function(mkey){
          if ($scope.menuMassive[mkey] === false) {
              return false;
          }
          menuFlag = true;          
          setPageTitle($scope.data.menuLabels[mkey]);
          return true;
        });
//        for (var k in $scope.menuMassive){
//          if ($scope.menuMassive[k]===false){
//              continue;
//          };
//          menuFlag = true;
//          break;
//        };
        //if check menu flag is false
        if (!menuFlag){
          $scope.setDefaultMenuState();//set default menu
        }         
        if ($location.path() !== "") {
          return;
        }
        //look menu flags and set location with current flag value
        if ($scope.menuMassive.object_menu_item) {
          window.location.assign("#/");
        }
        if ($scope.menuMassive.object_new_menu_item) {
          window.location.assign("#/objects/newlist");
        }
        if ($scope.menuMassive.report_menu_item) {
          window.location.assign("#/reports/");
        }
//        if ($scope.menuMassive.notice_menu_item){
//         window.location.assign("#/notices/monitor/");
//        }
        if ($scope.menuMassive.setting_menu_item) {
          window.location.assign("#/settings/tariffs/");
        }
        if ($scope.menuMassive.admin_menu_item) {
          window.location.assign("#/management/objects/");
        }
        if ($scope.menuMassive.log_menu_item) {
          window.location.assign("#/log/");
        }
        if ($scope.menuMassive.energy_menu_item) {
          window.location.assign("#/documents/energo-passports");
        }
        if ($scope.menuMassive.test_menu_item) {
          window.location.assign("#/test/");
        }
    }
      
//      set selected menu item
    $scope.clickMenu = function(menu) {
        var mkeys = Object.keys($scope.menuMassive);
        mkeys.forEach(function (key) {
            $scope.menuMassive[key] = false;
        });
//          for (var k in $scope.menuMassive){ 
//              $scope.menuMassive[k] = false;
//          };        
         $scope.menuMassive[menu] = true;         
         setPageTitle($scope.data.menuLabels[menu]);
    };

      //set default menu state
    $scope.setDefaultMenuState = function () {
//console.log("setDefaultMenuState"); 
        var mkeys = Object.keys($scope.menuMassive);
        mkeys.forEach(function (key) {
            $scope.menuMassive[key] = false;
        });
//      for (var k in $scope.menuMassive){
//              $scope.menuMassive[k] = false;
//          };        
         $scope.menuMassive.object_menu_item = true;         
         setPageTitle($scope.data.menuLabels.object_menu_item);
//console.log(window.location.href);        
//console.log(window.location);        
    };
      
    $(window).bind("beforeunload", function () {
//      console.log("beforeunload");
      $scope.setDefaultMenuState();
    });
    
    function allRequestCancel () {
        $http.pendingRequests.forEach(function (request) {
            if (request.cancel) {
                request.cancel.resolve();                
                console.log("Cancelling Request " + request.method + " on URL:" + request.url);                
            }
        });
    }
      
    $scope.logOut = function () {
        $cookies = {};
//        allRequestCancel();
        //cancel all request        
        objectSvc.getRequestCanceler().resolve();
        mainSvc.getRequestCanceler().resolve();
        reportSvc.getRequestCanceler().resolve();
        monitorSvc.getRequestCanceler().resolve();
        logSvc.getRequestCanceler().resolve();
//        $cookies.fromDate = undefined;
//        $cookies.toDate = undefined;
        $scope.setDefaultMenuState();
    };

    initMenu();
//      $scope.setDefaultMenuState();
//    $rootScope.$on('servicePermissions:loaded', function(event, args){
//        $scope.mainCtrlSettings.loadingServicePermissionFlag = mainSvc.getLoadingServicePermissionFlag(); 
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
            if (angular.isUndefined(elDOM) || (elDOM === null)) {
                return;
            }
            $('#' + element.permissionTagId).removeClass('nmc-hide');
        });
//        if (ctxFlag == false){
//            window.location.assign('#/');
//        };
    };
    setVisibles($scope.mainCtrlSettings.ctxId);
    //listen change of service list
    $rootScope.$on('servicePermissions:loaded', function () {
        setVisibles($scope.mainCtrlSettings.ctxId);
    });
    
//    window.setTimeout(function(){
//        setVisibles($scope.ctrlSettings.ctxId);
//    }, 500);
      
      //check user
    $scope.isRma = function () {
        return mainSvc.isRma();
    };
    $scope.isCabinet = function () {
        return mainSvc.isCabinet();
    };
      
    $scope.getCtx = function () {
      return $rootScope.ctxId;
    };
      
    $scope.emptyString = function (str) {
        return mainSvc.checkUndefinedEmptyNullValue(str);
    };
    
    $scope.isTestMode = function () {
//console.log("mainCtrl isTestMode = " + mainSvc.isTestMode());        
        return mainSvc.isTestMode();
    };
      
      
    function errorCallback (e) {
        console.log(e);
        notificationFactory.errorInfo("Не удалось сменить пароль!", "Проверьте правильность текущего пароля.");
    }
      
// *****************************************************************************
//          work with user password
// *****************************************************************************
    $scope.changePasswordInit = function () {
        $scope.data.userInfo = angular.copy($rootScope.userInfo);
    };
      
    $scope.checkPasswordFields = function (userInfo) {
        if (mainSvc.checkUndefinedNull(userInfo)) {
            return false;
        }
        var result = true;
        if ($scope.emptyString(userInfo.oldPassword)) {
            result = false;
        }
        if ($scope.emptyString(userInfo.newPassword)) {
            result = false;
        }
        if (userInfo.newPassword !== userInfo.newPasswordConfirm) {
            result = false;
        }
        return result;
    };

    function checkPassword (userInfo) {
        if (mainSvc.checkUndefinedNull(userInfo)) {
            return false;
        }
        var result = true;
        if ($scope.emptyString(userInfo.oldPassword)) {
            notificationFactory.errorInfo("Ошибка", "Поле \"Текущий пароль\" должно быть заполнено!");
            result = false;
        }
        if ($scope.emptyString(userInfo.newPassword)) {
            notificationFactory.errorInfo("Ошибка", "Пароль не должен быть пустым!");
            result = false;
        }
        if (userInfo.newPassword !== userInfo.newPasswordConfirm) {
            notificationFactory.errorInfo("Ошибка", "Поля \"Пароль\" и \"Подтверждение пароля\" не совпадают!");
            result = false;
        }
        return result;
    }
      
      //change user password
    $scope.changeUserPassword = function (userInfo) {
        if (checkPassword(userInfo) === false) {return "Password is incorrect!";}
        var url = "../api/systemInfo/passwordChange",
        params = {
            oldPassword : userInfo.oldPassword,
            newPassword : userInfo.newPassword
        };
        $http({
            method: "PUT",
            url: url,
            params: params
        }).then(function () {
            notificationFactory.success();
            $('#changePasswordModal').modal('hide');
        }, errorCallback);
    };
//    setPageTitle();
    
//    function initCtrl () {
//console.log(reportSvc.getRequestCanceler());        
//    }
    
//    initCtrl();
      
  }]);