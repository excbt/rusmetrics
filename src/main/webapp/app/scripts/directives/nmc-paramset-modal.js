angular.module('portalNMC')
.directive('nmcParamsetModal', function(){
    return {
        replace: true,
        templateUrl: "scripts/directives/templates/nmc-paramset-modal.html",
        controller: function($scope){
            $('#editParamsetModal').on('shown.bs.modal', function(){
                console.log($scope.currentReportType);
            });
        }
    }
});