angular.module('portalNMC')
.directive("nmcViewMessageForUser", function(){
    return {
        restrict: "AE",
        replace: true, 
        templateUrl: "scripts/directives/templates/nmc-view-message-for-user.html",
        scope: {
            messageForUser: "@",
            btnClick: "&",
            btnCaption: "@"
        }
    }
});