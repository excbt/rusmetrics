/*jslint node: true, white: true*/
/*global angular*/
'use strict';
angular.module("portalNMC")
    .controller('AboutProgramCtrl', ['mainSvc', '$scope', function (mainSvc, $scope) {
        $scope.isTestMode = function (){
            return mainSvc.isTestMode()
        }
    }]);