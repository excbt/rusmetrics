//'use strict';

/**
 * @ngdoc function
 * @name portalNMC.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the portalNMC
 */
var app = angular.module('portalNMC');
  app.controller('MainCtrl', ['$scope','$rootScope', '$cookies', '$window', '$location', 'monitorSvc', function ($scope, $rootScope, $cookies, $window, $location, monitorSvc) {
console.log("MainCtrl");      
    var monitorSvcInit = monitorSvc.getAllMonitorObjects();

    $scope.showPrivateOfficeMenu = false;
    $rootScope.showIndicatorsParam = false;
      
    $scope.setMenuVisibles = function(privateOfficeMenuVisible, indicatorsParamVisible){
        $scope.showPrivateOfficeMenu = privateOfficeMenuVisible;
        $rootScope.showIndicatorsParam = indicatorsParamVisible;
    };  
      
      
       $rootScope.timeDetailType =  "1h";//$scope.timeDetailType;

       // $rootScope.endDate = ""; //"2014-03-20";//new Date();                 
       //   $rootScope.beginDate ="";// "2014-03-19";//endDate;  

      
    //end for indicators  
      
 
//  flags for selected menu item                  
      $scope.menuMassive = {};
//      if (!$cookies.hasOwnProperty('menuMassive')){
//console.log("no MenuMassive");          
//          $cookies.menuMassive = {};
//      }
      function initMenu(){
          $scope.menuMassive.object_menu_item = ($cookies.object_menu_item==="true" ? true:false);
          $scope.menuMassive.report_menu_item= ($cookies.report_menu_item==="true" ? true:false);
          $scope.menuMassive.notice_menu_item = ($cookies.notice_menu_item==="true" ? true:false);
          $scope.menuMassive.setting_menu_item = ($cookies.setting_menu_item==="true" ? true:false);
          $scope.menuMassive.admin_menu_item = ($cookies.admin_menu_item==="true" ? true:false);
          $scope.menuMassive.object_menu_list_item = ($cookies.object_menu_list_item==="true" ? true:false);
          $scope.menuMassive.contact_menu_item= ($cookies.contact_menu_item==="true" ? true:false);
          $scope.menuMassive.directory_menu_item= ($cookies.directory_menu_item==="true" ? true:false);
          $scope.menuMassive.object_control_menu_item= ($cookies.object_control_menu_item==="true" ? true:false);
          $scope.menuMassive.metadata_control_menu_item= ($cookies.metadata_control_menu_item==="true" ? true:false);
          $scope.menuMassive.setting_group_menu_item= ($cookies.setting_group_menu_item==="true" ? true:false);
          $scope.menuMassive.setting_tariff_menu_item= ($cookies.setting_tariff_menu_item==="true" ? true:false);
          $scope.menuMassive.setting_report_menu_item= ($cookies.setting_report_menu_item==="true" ? true:false);
          var menuFlag = false;
          for (var k in $scope.menuMassive){
              if (!$scope.menuMassive[k]){
                  continue;
              };
              menuFlag = true;
          };
          if (!menuFlag){
              $scope.setDefaultMenuState();
          };
//console.log("Menu flag = "+menuFlag);          
//console.log($scope.menuMassive);          
//console.log($location.path());          
          if ($location.path()!=""){
              return;
          };
          if ($scope.menuMassive.object_menu_item){
              window.location.assign("#/");
          };
          if ($scope.menuMassive.report_menu_item){
              window.location.assign("#/reports/");
          };
          if ($scope.menuMassive.notice_menu_item){
             window.location.assign("#/notices/monitor/");
          };
          if ($scope.menuMassive.setting_menu_item){
              window.location.assign("#/settings/tariffs/");
          };
          if ($scope.menuMassive.admin_menu_item){
              window.location.assign("/");
          };
//          if ($scope.menuMassive.object_menu_item){
//              window.location.assign("/");
//          };
//          if ($scope.menuMassive.object_menu_item){
//              window.location.assign("/");
//          };
          
      };
      
      
//for (var k in $scope.menuMassive){
//console.log(k);                    
//console.log($cookies[k]); 
//console.log($scope.menuMassive[k]);     
//};
//      set selected menu item
    $scope.clickMenu = function(menu){
          for (var k in $scope.menuMassive){
        //console.log(k);                    
        //console.log($cookies[k]); 
              $cookies[k] = false;
              $scope.menuMassive[k] = false;
          };
         $cookies[menu] = true;         
         $scope.menuMassive[menu]=true;  
        //          var el = document.getElementById(el_id);
        //          el.className = el.className + "excbt_a_list_group_item_selected";
    };

    $scope.setDefaultMenuState = function(){
console.log("setDefaultMenuState");        
      for (var k in $scope.menuMassive){
        //console.log(k);                    
        //console.log($cookies[k]); 
              $cookies[k] = false;
              $scope.menuMassive[k] = false;
          };
         $cookies.object_menu_item = true;         
         $scope.menuMassive.object_menu_item=true;  
        //          var el = document.getElementById(el_id);
        //          el.className = el.className + "excbt_a_list_group_item_selected";
    };
      
//    $rootScope.$on('$destroy',function(){
//        $scope.setDefaultMenuState();
//        alert('Destroy app');
//    });
//      $window.on('onbeforeunload',function(){
//          alert("close");
//      });
//      for(var k in $window){
//          console.log("window["+k+"]= "+$window[k]);
//      };
//        $window.onunload = function(){
//            alert("jdskjhnk");
//        };
      
      $(window).bind("beforeunload",function(){
          console.log("beforeunload");
          $scope.setDefaultMenuState();
      });
      
      $(window).bind("reflow",function(){
          console.log("reflow");
          $scope.setDefaultMenuState();
      });
      
      initMenu();
      
          // Проверка пользователя - системный/ не системный
//    $scope.isSystemuser = function(){
//        $scope.userInfo = $rootScope.userInfo;
//        return $scope.userInfo._system;
//    };
    
  }]);


