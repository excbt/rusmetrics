/**
 * 
 */
'use strict';
angular.module('portalNMC')
.service('securityCheck', ['$http',
                       function ($http) {
	 
    return {
        isAuthenficated: function () {
        	
            var request = {
                    url: "../api/securityCheck/isAuthenticated", 
                    method: "GET",
                    timeout: 5000
                }; 

            
            var promise = 
        	  $http(request)
        	  .then(function(response) {
        	      return response.data.success;
        	  }, function errorCallback(response) {
        		    // called asynchronously if an error occurs
        		    // or server returns response with an error status.
        		  return false
        		  })
        	  .catch(function(e) {
        		  return false;
        	  });
            
            return promise;
        	
        }
    };
}]);