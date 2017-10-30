describe("PortalNMC: documentsObjectPassportCtrl test:", function () {
	var APP_NAME = "portalNMC",
		CONTROLLER_NAME = "documentsObjectPassportCtrl";
	var ctrl, rootScope, scope, controller, httpBackend;	

	beforeEach(function () {
		module(APP_NAME);		
	});

	describe("", function () {

		beforeEach(inject(function (_$controller_, _$rootScope_) {
			controller = _$controller_;
			rootScope = _$rootScope_;
			scope = rootScope.$new();
			ctrl = controller(CONTROLLER_NAME, {$scope: scope});
		}));

		it(CONTROLLER_NAME + " is exists:", function () {
			expect(ctrl).toBeDefined();
			expect(ctrl).not.toBe(null);
		});

		it("$scope.isActivePassport default", function () {
			expect(scope.isActivePassport).toBe(false);
			expect(scope.isReadOnly).toBeDefined();
			expect(scope.isReadOnly()).toBe(true);
		});

		it("$scope.extraValues default", function () {
			expect(scope.extraValues).toEqual([]);
		});

		it("$scope.cancelObjectPassportEdit()", function () {
			expect(scope.cancelObjectPassportEdit).toBeDefined();
		});
		
	});

	describe("", function () {
		it("New passport", inject(function ($rootScope, $controller) {
			var scope = $rootScope.$new(),
				ctrl = $controller(CONTROLLER_NAME, {$scope: scope, $routeParams: {object: "new"}});

			expect(scope.isActivePassport).toBe(true);
			expect(scope.isReadOnly).toBeDefined();
			expect(scope.isReadOnly()).toBe(false);
		}));

		function testCurrentBuildingTypeCategory(inputBuildingType, expectValue) {			
			it("Building type: " + inputBuildingType.buildingType + " and category: " + inputBuildingType.buildingTypeCategory + " import test:", inject(function ($rootScope, $controller) {
			// httpBackend = $httpBackend;
			// httpBackend.when("GET", /api/).respond(null);
				var inputBuildingTypeCategoryCaption;
				if (inputBuildingType.hasOwnProperty("buildingTypeCategoryCaption")) {
					inputBuildingTypeCategoryCaption = inputBuildingType.buildingTypeCategoryCaption;
				}
				var scope = $rootScope.$new(),
					ctrl = $controller(CONTROLLER_NAME, 
						{
							$scope: scope, 
							$routeParams: {
								buildingType: inputBuildingType.buildingType,
								buildingTypeCategory: inputBuildingType.buildingTypeCategory,
								buildingTypeCategoryCaption: inputBuildingTypeCategoryCaption
							}
						});				
				// scope.$digest();
				expect(scope.extraValues).toEqual(expectValue);	
			}));
		}

		function testBuildingTypeCategoryImporting(inputBuildingTypeArr, expectValue) {			
			var testCounter = 0;
			for (testCounter = 0; testCounter < inputBuildingTypeArr.length; testCounter += 1) {
				testCurrentBuildingTypeCategory(inputBuildingTypeArr[testCounter], expectValue);
			}	
		}

		var buildingTypeCounter = 0,
			NR_HEALTH_BUILDING_TYPES = 9,
			NR_LEARN_BUILDING_TYPES = 7,
			H_APT_BUILDINGS_BUILDING_TYPES = 6,
			GB_GOV_BUILDINGS_BUILDING_TYPES = 4,
			NR_HEALTH_EXTRA_VALUES = [
				{
					_complexIdx: "P_2a_i1",
					value: true
				},
				{
					_complexIdx: "P_2b_i1",
					value: true
				}
			],
			NR_LEARN_EXTRA_VALUES = [
				{
					_complexIdx: "P_2a_i1",
					value: true
				},
				{
					_complexIdx: "P_2c_i1",
					value: true
				}
			],
			H_IND_HOUSES_EXTRA_VALUES = [
				{
					_complexIdx: "P_2a_i2",
					value: true
				},
				{
					_complexIdx: "P_2b_i2",
					value: true
				}
			],
			H_TEMP_STAY_COMMON_EXTRA_VALUES = [
				{
					_complexIdx: "P_2a_i2",
					value: true
				},
				{
					_complexIdx: "P_2c_i2",
					value: true
				}
			],
			H_TEMP_STAY_HOTEL_EXTRA_VALUES = [
				{
					_complexIdx: "P_2a_i2",
					value: true
				},
				{
					_complexIdx: "P_2d_i2",
					value: true
				}
			],
			H_APT_BUILDINGS_EXTRA_VALUES = [
				{
					_complexIdx: "P_2a_i2",
					value: true
				},
				{
					_complexIdx: "P_2e_i2",
					value: true
				}
			],
			GB_GOV_BUILDINGS_EXTRA_VALUES = [
				{
					_complexIdx: "P_2a_i1",
					value: true
				},
				{
					_complexIdx: "P_2e_i1",
					value: true
				}
			],
			DEFAULT_EXTRA_VALUES = [
				{
					_complexIdx: "P_2a_i3",
					value: true
				},
				{
					_complexIdx: "P_2b_i3",
					value: "true"
				}
			];

		//Test NR Health building categories
		var testBuildingTypes = [
			{
				buildingType: "NR",
				buildingTypeCategory: "NR_HEALTH",
				buildingTypeCategoryCaption: "Здравоохранение"
			},
		];

		for (buildingTypeCounter = 0; buildingTypeCounter < NR_HEALTH_BUILDING_TYPES; buildingTypeCounter += 1) {
			testBuildingTypes.push({
				buildingType: "NR",
				buildingTypeCategory: "X_100009" + buildingTypeCounter,
			});

		}		
		testBuildingTypeCategoryImporting(testBuildingTypes, NR_HEALTH_EXTRA_VALUES);

		//Test NR Learn building categories
		testBuildingTypes = [
			{
				buildingType: "NR",
				buildingTypeCategory: "NR_LEARN",
				buildingTypeCategoryCaption: "Образование"
			},
		];
				
		for (buildingTypeCounter = 0; buildingTypeCounter < NR_LEARN_BUILDING_TYPES; buildingTypeCounter += 1) {
			testBuildingTypes.push({
				buildingType: "NR",
				buildingTypeCategory: "X_100008" + buildingTypeCounter,
			});

		}		
		testBuildingTypeCategoryImporting(testBuildingTypes, NR_LEARN_EXTRA_VALUES);

		//test H_IND_HOUSES
		var hIndHouseBuildingType = {
			buildingType: "H",
			buildingTypeCategory: "X_1000000"
		};
		testCurrentBuildingTypeCategory(hIndHouseBuildingType, H_IND_HOUSES_EXTRA_VALUES);

		//test H_TEMP_STAY hOStel
		var hTempStayBuildingType = {
			buildingType: "H",
			buildingTypeCategory: "X_1000072"
		};
		testCurrentBuildingTypeCategory(hTempStayBuildingType, H_TEMP_STAY_COMMON_EXTRA_VALUES);

		//test H_TEMP_STAY hOtel
		var hTempStayBuildingType = {
			buildingType: "H",
			buildingTypeCategory: "X_1000070"
		};
		testCurrentBuildingTypeCategory(hTempStayBuildingType, H_TEMP_STAY_HOTEL_EXTRA_VALUES);

		//Test H_APT_BUILDINGS building categories
		testBuildingTypes = [
			{
				buildingType: "H",
				buildingTypeCategory: "H_APT_BUILDINGS",
				buildingTypeCategoryCaption: "Многоквартирный дом"
			},
		];
				
		for (buildingTypeCounter = 1; buildingTypeCounter <= H_APT_BUILDINGS_BUILDING_TYPES; buildingTypeCounter += 1) {
			testBuildingTypes.push({
				buildingType: "H",
				buildingTypeCategory: "X_100000" + buildingTypeCounter,
			});

		}		
		testBuildingTypeCategoryImporting(testBuildingTypes, H_APT_BUILDINGS_EXTRA_VALUES);

		//Test GB_GOV_BUILDINGS building categories
		testBuildingTypes = [
			{
				buildingType: "GB",
				buildingTypeCategory: "GB_GOV_BUILDINGS",
				buildingTypeCategoryCaption: "Органы власти и управления"
			},
		];
				
		for (buildingTypeCounter = 0; buildingTypeCounter < GB_GOV_BUILDINGS_BUILDING_TYPES; buildingTypeCounter += 1) {
			testBuildingTypes.push({
				buildingType: "GB",
				buildingTypeCategory: "X_100017" + buildingTypeCounter,
			});

		}
		for (buildingTypeCounter = 0; buildingTypeCounter < GB_GOV_BUILDINGS_BUILDING_TYPES; buildingTypeCounter += 1) {
			testBuildingTypes.push({
				buildingType: "GB",
				buildingTypeCategory: "X_100018" + buildingTypeCounter,
			});

		}		
		testBuildingTypeCategoryImporting(testBuildingTypes, GB_GOV_BUILDINGS_EXTRA_VALUES);

		//Test default extra value for building category
		var anotherBuildingType = {
			buildingType: "H",
			buildingTypeCategory: "H_COT_BUILDINGS",
			buildingTypeCategoryCaption: "Коттеджи на несколько семей"
		}
		var defExtraVal = angular.copy(DEFAULT_EXTRA_VALUES);
		defExtraVal[1].value = anotherBuildingType.buildingTypeCategoryCaption;
		testCurrentBuildingTypeCategory(anotherBuildingType, defExtraVal);
	});

	describe("", function () {
		beforeEach(function (){
			module(function ($provide) {
				$provide.factory('mainSvc', [function () {
					function isReadonly() {
						return false;
					}

					function getUserServicesPermissions() {

					}

					function checkUndefinedNull(numvalue) {
				        var result = false;
				        if ((angular.isUndefined(numvalue)) || (numvalue == null)) {
				            result = true;
				        }
				        return result;
				    }

					return {
						isReadonly: isReadonly,
						getUserServicesPermissions: getUserServicesPermissions,
						checkUndefinedNull: checkUndefinedNull
					};
				}])
			});
		});

		it("mainSvc mock", inject(function ($rootScope, $controller, mainSvc) {
			var scope = $rootScope.$new(),
				mockMainSvc = mainSvc;
			spyOn(mockMainSvc, 'isReadonly');//.andCallThrough();
			var ctrl = $controller(CONTROLLER_NAME, {
				$scope: scope,
				mainSvc: mockMainSvc
			});

			scope.isActivePassport = true;
			var isReadOnlyTest = scope.isReadOnly();
			// scope.$digest();			
			expect(mockMainSvc.isReadonly).toHaveBeenCalled();
			expect(isReadOnlyTest).toBe(false);

			scope.isActivePassport = false;
			var isReadOnlyTest = scope.isReadOnly();
			// scope.$digest();			
			expect(mockMainSvc.isReadonly).toHaveBeenCalled();
			expect(isReadOnlyTest).toBe(true);
		}));		
	});
});