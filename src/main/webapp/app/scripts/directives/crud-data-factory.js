'use strict';

angular.module('portalNMK').factory('crudDataFactory', ['$resource',
		function($resource) {
			return function(type) {
				return $resource(type + '/:id', {id: '@id' 
				}, {
				    update: {method: 'PUT'},
				    query: {method: 'GET', isArray: true},
				    get: {method: 'GET'},
				    delete: {method: 'DELETE'}
				});
			};
		} ]);