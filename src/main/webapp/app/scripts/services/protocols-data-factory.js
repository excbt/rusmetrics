'use strict';

app.factory('protocolsDataFactory', [ 'crudGridDataFactory',
		function(crudGridDataFactory) {
			return crudGridDataFactory('rest/protocols');
		} ]);