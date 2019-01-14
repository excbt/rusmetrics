/*global angular, console*/
/*jslint eqeq: true*/
(function () {
    'use strict';
    angular.module('portalNMC')
        .run(runner);
    runner.$inject = ['Idle',
                      /**/
                      'objectSvc',
                      'monitorSvc',
                      'mainSvc',
                      'reportSvc',
                      /*security check*/
                      '$rootScope',
                      '$location',
                      '$q',
                      'securityCheck'
                     ];
    
    function runner(Idle,
                     objectSvc,
                     monitorSvc,
                     mainSvc,
                     reportSvc,
                     /*security chech*/
                     $rootScope,
                     $location,
                     $q,
                     securityCheck
                    ) {
        //start Idle service
        //  Idle.watch();        
        
        var mainSvcInit = mainSvc.getUserServicesPermissions();
        var monitorSvcInit = monitorSvc.getAllMonitorObjects();
        var objectSvcInit = objectSvc.promise;
        
        //security checker
        $rootScope.$on('$routeChangeStart', function (evt, to, from) {
            var checkPromise = securityCheck.isAuthenficated();
    //        $q.all([checkPromise]).then(function (result) {
            securityCheck.isAuthenficated().then(function (result) {
                if (result == false) {
                    var url = "../login";
                    window.location.replace(url);
                }
            }, function (e) {
                console.log(e);
                var url = "../login";
                window.location.replace(url);
            });
        });
        
    }
    
}());