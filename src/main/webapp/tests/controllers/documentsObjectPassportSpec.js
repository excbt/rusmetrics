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

		it("Building type and category import test:", inject(function ($rootScope, $controller) {
			// httpBackend = $httpBackend;
			// httpBackend.when("GET", /api/).respond(null);

			var scope = $rootScope.$new(),
				ctrl = $controller(CONTROLLER_NAME, 
					{
						$scope: scope, 
						$routeParams: {
							buildingType: "NR",
							buildingTypeCategory: "NR_HEALTH",
							buildingTypeCategoryCaption: "Здравоохранение"
						}
					});
			var expectExtraValues = [
				{
					_complexIdx: "P_2a_i1",
					value: true
				},
				{
					_complexIdx: "P_2b_i1",
					value: true
				},
			];
			// scope.$digest();
			expect(scope.extraValues).toEqual(expectExtraValues);	
		}));
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