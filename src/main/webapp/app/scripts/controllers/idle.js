angular.module('portalNMK')
    .controller('IdleCtrl', function($scope){
        $scope.$on('IdleEnd', function() {
//console.log('IdleEnd');            
            $('#warnDialogModal').modal('hide');   
        });
        $scope.$on('IdleStart', function() {
//console.log('IdleStart');
//            $modal.open({
//              templateUrl: 'warning-dialog.html',
//              windowClass: 'modal-danger'
//            });
            $('#warnDialogModal').modal();
        });

        $scope.$on('IdleTimeout', function() {
//console.log('IdleTimeout');  
//            $modal.open({
//              templateUrl: 'timedout-dialog.html',
//              windowClass: 'modal-danger'
//            });
//            $('#timedout-dialog.html').modal();
            window.location.assign('../logout');
        });
    
});