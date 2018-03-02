/*global angular, console*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .filter('contZpointType', filter);

    function filter() {
        
        return filterFilter;

        ////////////////

        function filterFilter(items, val) {
//            console.log(items);
//            console.log(val);
            if (!angular.isArray(items)) {                
                return items;
            }
            if (angular.isUndefined(val) || val === null) {                
                return items;
            }
            var out = [];
            items.forEach(function (item) {
                if (item.contServiceTypeKeyname === val) {
                    out.push(item);
                }
            });
            return out;
        }
    }
})();