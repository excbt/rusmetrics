'use strict';

angular.module('portalNMK').directive('crudGridPaged', function () {
    return {
        restrict: 'A',
        replace: false,
        scope: true,
        // templateUrl:
		// '/web.app/public/template/directives/crudGridPagedDirective.jsp',
        templateUrl: './scripts/directives/templates/crud-grid-paged-directive-template.html',
        controller: ['$scope', '$element', '$attrs', 'crudGridPagedDataFactory', 'notificationFactory',
            function ($scope, $element, $attrs, CrudGridPagedDataFactory, NotificationFactory) {
                $scope.objects = [];
                $scope.lookups = [];
                $scope.object = {};
                $scope.dataVO = {};
                $scope.columns = angular.fromJson($attrs.columns);
                $scope.captions = angular.fromJson($attrs.captions);
                $scope.addMode = false;
                //$scope.orderBy = { field: 'Name', asc: true };
                $scope.orderBy = {};
                $scope.loading = true;
                $scope.filter = '';
                
                $scope.pageToGet = 0;
                $scope.pageInfo = {
                		currentPage : $scope.pageToGet,
						totalPages : 1,
						totalElements : 0
                };

                var $docScope = angular.element(document).scope();

                $scope.setLookupData = function () {
                    for (var i = 0; i < $scope.columns.length; i++) {
                        var c = $scope.columns[i];
                        if (c.lookup && !$scope.hasLookupData(c.lookup.table)) {
                            CrudGridPagedDataFactory(c.lookup.table).query(function (data) {
                                $scope.setIndividualLookupData(c.lookup.table, data);
                            });
                        }
                    }
                };

                $scope.resetLookupData = function(table) {
                    $scope.setIndividualLookupData(table, {});
                    $scope.setLookupData();
                };

                $scope.getLookupData = function (table) {
                    return typeof table == 'undefined' ? null : $scope.lookups[table.toLowerCase()];
                };

                $scope.setIndividualLookupData = function (table, data) {
                    $scope.lookups[table.toLowerCase()] = data;
                };

                $scope.hasLookupData = function (table) {                    
                    return !$.isEmptyObject($scope.getLookupData(table));
                };

                $scope.getLookupValue = function (lookup, key) {
                    var data = $scope.getLookupData(lookup.table);

                    if (typeof data != 'undefined') {
                        for (var i = 0; i < data.length; i++) {
                            if (data[i][lookup.key] === key)
                                return data[i][lookup.value];
                        }
                    }

                    return '';
                };

                $scope.toggleAddMode = function () {
                    $scope.addMode = !$scope.addMode;
                    $scope.object = {};
                };

                $scope.toggleEditMode = function (object) {
                    object.editMode = !object.editMode;
                };

                var successCallback = function (e, cb) {
                    NotificationFactory.success();
                    $docScope.$broadcast('lookupDataChange', [$attrs.table]);
                    $scope.getData(cb);
                };

                var successPostCallback = function (e) {
                    successCallback(e, function () {
                        $scope.toggleAddMode();
                    });
                };

                $scope.$on('lookupDataChange', function (scope, table) {
                    $scope.resetLookupData(table[0]);
                });

                var errorCallback = function (e) {
                    NotificationFactory.error(e.data.ExceptionMessage);
                };

                $scope.addObject = function () {
                    CrudGridPagedDataFactory($attrs.table).save($scope.object, successPostCallback, errorCallback);
                };

                $scope.deleteObject = function (object) {
                    CrudGridPagedDataFactory($attrs.table).delete({ id: object.objUUID }, successCallback, errorCallback);
                };

                $scope.updateObject = function (object) {
                    CrudGridPagedDataFactory($attrs.table).update({ id: object.objUUID }, object, successCallback, errorCallback);
                };

                $scope.getData = function (cb) {

                    CrudGridPagedDataFactory($attrs.table).query({page : $scope.pageToGet},function (data) {
                    	$scope.dataVO = data;
                    	$scope.objects = data.elements;
                    	$scope.pageInfo = {
                    			currentPage : $scope.pageToGet,
                    			firstPage : $scope.pageToGet === 0,
                    			lastPage : $scope.pageToGet === (data.totalPages - 1),
								totalPages : data.totalPages,
								totalElements : data.totalElements
                    	};
                    	
                        if (cb) cb();
                    });
                };

                $scope.setOrderBy = function (field) {
                    var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
                    $scope.orderBy = { field: field, asc: asc };
                };

                
				$scope.changePage = function(page) {
					$scope.pageToGet = page;
					$scope.getData(function () {
                        $scope.setLookupData();
                        $scope.loading = false;
                    });
				};                  

				
				
				$scope.changePage ($scope.pageToGet);
				
//                $scope.getData(
//                    function () {
//                        $scope.setLookupData();
//                        $scope.loading = false;
//                    });
                
              
            }]
    };
});