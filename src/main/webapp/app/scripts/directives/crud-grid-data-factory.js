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

angular.module('portalNMC').factory('crudGridDataFactoryWithCanceler', [ '$http', '$resource',
		function($http, $resource) {
			return function(type, requestCanceler) {
//console.log(angular.copy(requestCanceler));                
				return $resource(type + '/:id', {id: '@id' 
				}, {
				    update: {method: 'PUT'},
				    query: {method: 'GET', isArray: true, timeout: requestCanceler.promise},
				    get: {method: 'GET', timeout: requestCanceler.promise},
				    delete: {method: 'DELETE'}
				});
			};
		} ]);