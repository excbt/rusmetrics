/*jslint node: true, eqeq: true*/
/*global angular, toastr*/
'use strict';

angular.module('portalNMC').factory('notificationFactory', function () {
	 
    return {
        success: function () {
            toastr.success("Успешно выполнено.");
        },
        successInfo: function (text) {
            toastr.success(text);
        },
        info: function (text) {
            toastr.success(text);
        },
        error: function (text) {
            toastr.error(text, "Ошибка!");
        },
        errorInfo: function (header, text) {
            toastr.error(text, header);
        },
        warning : function (text) {
            toastr.warning(text);
        }
    };
});