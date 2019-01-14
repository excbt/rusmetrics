/*global angular, console*/
(function () {
//************************************************************************************
// Helper directives
// **********************************************************************************
    'use strict';
    angular.module('portalNMC')
        .directive('focusMe', focusMe);
    
    focusMe.$inject = ['$timeout', '$parse'];
    
    function focusMe($timeout, $parse) {
        return {
        //scope: true,   // optionally create a child scope
            link: function (scope, element, attrs) {
                var model = $parse(attrs.focusMe);
                scope.$watch(model, function (value) {
    //console.log('value=',value);
    //console.log(element);          
                    if (value === true) {
                        $timeout(function () {
    //console.log(element[0]);
    //console.log(element.focus());              
                            element.focus();
                        }, 100);
                    }
                });
          // to address @blesh's comment, set attribute value to 'false'
          // on blur event:
                element.bind('blur', function () {
                    console.log('blur');
                    scope.$apply(model.assign(scope, false));
                });
            }
        };
    }
}());