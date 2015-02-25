'use strict';

/**
 * @ngdoc function
 * @name portalNMK.controller:NotificationCtrl
 * @description
 * # MainCtrl
 * Controller of the portalNMK
 */
angular.module('portalNMK')
  .controller('NoticeCtrl', function ($scope) {
    $scope.notificationsMes = [
    {
      "notifiId": "1",
      "notifiCat": "Критическая",
      "notifiType": "Малая разница температур в контуре циркуляции",
        "notifiText": "Разница температур ( dT=2,8C меньше заданной 3,0C).",
        "notifiDate": "29.07.2014 09:00:00"
    }
    ];
  });
