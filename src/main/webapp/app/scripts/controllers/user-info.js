angular.module('portalNMC')
    .controller('UserinfoCtrl', function($rootScope, $http){
        var url = "../api/systemInfo/fullUserInfo";
        $http.get(url)
                .success(function(data, satus, headers, config){
                    $rootScope.userInfo = data;
//        console.log($rootScope.userInfo);        
                })
                .error();
    
});