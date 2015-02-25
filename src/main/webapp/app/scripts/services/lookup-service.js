/**
 * 
 */

'use strict';

app.factory('lookupService', [ 'crudDataFactory', 'notificationFactory', function(crudDataFactory, notificationFactory) {

	var $docScope = angular.element(document).scope();

	var $lookups = [];	
	
	var setIndivisualLookupData_ = function (table, data) {
    	$lookups[table.toLowerCase()] = data;
    };
    
    var hasLookupData = function (table) {                    
        return !$.isEmptyObject(getLookupData_(table));
    };

    var getLookupData_ = function (table) {
    	 return typeof table == 'undefined' ? null : $lookups[table.toLowerCase()];
    };
    
    $docScope.$on('lookupServiceDataChange', function (scope, table) {
    	setIndivisualLookupData(table, {});
    	setLookupData_(table);
    });

    
    var errorCallback = function (data) {
    	notificationFactory.error("Error during initializing lookup. Status code:" + data.status);
    };
    
    
    var setLookupData_= function (table) {
    	if (table && !hasLookupData(table)) {
    	    
    		var successCallback = function (data) {
    			setIndivisualLookupData_(table, data);
		    	console.log("Look up service initialized. Table: " + table);    			
    	    };		    		
    		
    		crudDataFactory(table).query(successCallback, errorCallback);
    	};
    };
    
    var getLookupValue_ = function (lookup, key) {
        var data = getLookupData_(lookup.table);
        if (typeof data != 'undefined') {
            for (var i = 0; i < data.length; i++) {
                if (data[i][lookup.key] === key)
                    return data[i][lookup.value];
            }
        }
        return '';
    };
    
	var serviceInstance = {
		    setLookupTable : function (table) {
		    	return setLookupData_(table);
		    },
		    getLookupTableData : function (table) {
		    	return getLookupData_(table);
            },		    
			resetLookupTableData : function(table) {
				setIndivisualLookupData_(table, {});
		    },
		    getLookupValue : function (lookup, key) {
                return getLookupValue_(lookup, key);
            }
	};
	
	// Our first service
	return serviceInstance;
} ]);