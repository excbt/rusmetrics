'use strict';

angular.module('portalNMC').factory('crudGridDataFactory', [ '$http', '$resource',
		function($http, $resource) {
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