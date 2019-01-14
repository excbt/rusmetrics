/*global angular, moment, console*/
(function () {
    'use strict';

    angular
        .module('portalNMC')
        .service('dateTimeSvc', Service);

    Service.$inject = [];

    /* @ngInject */
    function Service() {
        var DEFAULT_USER_FORMAT = 'DD.MM.YYYY HH:mm:ss';
        this.dateFormating = dateFormating;

        ////////////////
        function dateFormatingFromArray(inputData, formatString) {
            var inpData = angular.copy(inputData);
            inpData[1] -= 1;
            if (inpData.length >= 7) {
                inpData.splice(-1, 1);
            }
            var mDate = moment(inpData);            
            return mDate.format(formatString);
        }
        
        function dateFormating(inputData, formatString) {
            
            if (angular.isUndefined(inputData) || inputData === null) {
                return null;
            }
            if (angular.isUndefined(formatString) || formatString === null) {
                formatString = DEFAULT_USER_FORMAT;
            }
            var result = null;            
            
            if (angular.isArray(inputData)) {
                result = dateFormatingFromArray(inputData, formatString);
            }            
            
            return result;
        }
    }
})();