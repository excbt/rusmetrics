/*jslint node: true, white: true, nomen: true, eqeq: true*/
/*global angular*/
'use strict';
angular.module('portalNMC')
    .service('meterPeriodsSvc', ['$http', '$cookies', '$interval', '$rootScope', '$q', function($http, $cookies, $interval, $rootScope, $q) {
        
        var METER_PERIOD_SETTING_URl = "../api/subscr/meter-period-settings";
        
        var service = {};
        
        function loadMeterPeriods() {
            var url = METER_PERIOD_SETTING_URl;
            return $http.get(url);
        }
        
        function saveMeterPeriod(rmethod, dataRms) {
            return $http(
                {
                    method: rmethod,
                    url: METER_PERIOD_SETTING_URl, 
                    data: dataRms
                });
        }
        function deleteMeterPeriod(meterPeriodId) {
            $http.delete(METER_PERIOD_SETTING_URl + "/" + meterPeriodId);
        }
        
        service.loadMeterPeriods = loadMeterPeriods;
        service.saveMeterPeriod = saveMeterPeriod;
        service.deleteMeterPeriod = deleteMeterPeriod;
        return service;
    }]);