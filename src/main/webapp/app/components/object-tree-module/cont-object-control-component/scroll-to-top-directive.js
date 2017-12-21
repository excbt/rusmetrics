/*global angular, console*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .directive('scrollToTop', directive);

    directive.$inject = ['$timeout'];

    /* @ngInject */
    function  directive($timeout) {
        var directive = {            
            link: link            
        };
        return directive;

        function link(scope, element, attrs, controller) {
            scope.$on(attrs.scrollToTop, function (ev, args) {
               $timeout(function () {
                   var tbl = angular.element(element)[0];
                   var elm = tbl.rows[args.index + 1];  // +1 - if table with header                
                   elm.scrollIntoView();
               }); 
            });
        }
    }
})();