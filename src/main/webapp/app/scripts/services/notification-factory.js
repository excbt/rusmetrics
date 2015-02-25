'use strict';

angular.module('portalNMK').factory('notificationFactory', function () {
	 
    return {
        success: function () {
            toastr.success("Success");
        },
        successInfo: function (text) {
        	toastr.success(text);
        },
        info: function (text) {
        	toastr.success(text);
        },
        error: function (text) {
            toastr.error(text, "Error!");
        },
        warning : function (text) {
        	  toastr.warning(text);
        },
    };
});