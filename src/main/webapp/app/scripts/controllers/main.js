//'use strict';

/**
 * @ngdoc function
 * @name portalNMC.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the portalNMC
 */
var app = angular.module('portalNMC');
  app.controller('MainCtrl', ['$scope','$rootScope', 'crudGridDataFactory', function ($scope, $rootScope, crudGridDataFactory) {

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
      $scope.menuMassive.object_menu_item = true;
      $scope.menuMassive.notice_menu_item = false;
      $scope.menuMassive.setting_menu_item = false;
      $scope.menuMassive.admin_menu_item = false;
      $scope.menuMassive.object_menu_list_item = false;
      $scope.menuMassive.contact_menu_item= false;
      $scope.menuMassive.directory_menu_item= false;
      $scope.menuMassive.object_control_menu_item= false;
      $scope.menuMassive.metadata_control_menu_item= false;
      $scope.menuMassive.setting_group_menu_item= false;
      $scope.menuMassive.setting_tariff_menu_item= false;
      $scope.menuMassive.setting_report_menu_item= false;
//      set selected menu item
      $scope.clickMenu = function(menu){
          for (var k in $scope.menuMassive){
              $scope.menuMassive[k] = false;
          };
         $scope.menuMassive[menu]=true;  
//          var el = document.getElementById(el_id);
//          el.className = el.className + "excbt_a_list_group_item_selected";
      };
    
  }]);


