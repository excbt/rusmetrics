/*global angular, console*/
angular.module('scroll', [])
    .directive('whenScrolled', function () {
    return function (scope, elm, attr) {
        var raw = elm[0];
//        console.log(raw);
        elm.bind('scroll', function () {
            if (raw.scrollTop + raw.offsetHeight >= raw.scrollHeight) {
                scope.$apply(attr.whenScrolled);
            }
        });
    };
});