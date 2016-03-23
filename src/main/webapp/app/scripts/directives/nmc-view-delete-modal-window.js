angular.module('portalNMC')
.directive('nmcViewDeleteModalWindow', function(){
    return {
        restrict: "AE",
        replace: false,
        templateUrl: "scripts/directives/templates/nmc-view-delete-modal-window.html",
        scope: {
            message: "@",
            confirmLabel: "@",
            controlCode: "@",
            deleteItemClick: "&",
            isSystemuser: "&"
        },
        controller: function($scope){
            $scope.confirmCode = null;
            $('#deleteWindowModal').on('shown.bs.modal', function(){                                
            });
            $('#deleteWindowModal').on('hidden.bs.modal', function(){
                $scope.confirmCode = null;                              
            });
        }
    }
});