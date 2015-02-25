'use strict';

angular.module('portalNMK').factory('crudGridPagedDataFactory', 
		function($resource) {
			return function(type) {
				return $resource(type + '/api/:id', {id: '@id' }, {
					
					    update: {method: 'PUT'},
					    query: {method: 'GET', isArray: false, params : {page : 0}},
					    get: {method: 'GET'},
					    delete: {method: 'DELETE'}
				});
			};
		} );