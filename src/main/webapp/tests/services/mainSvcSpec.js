xdescribe("Portal NMC, main servce tests", function () {
	var USER_INFO = {
			userName: "unitTestUser",
			subscriber: {
				subscriberName: "unitTestSubscriber"
			}
		},
		EMPTY_OBJECT = {},
		TEST_CERT_SUBSCR = "TEST_CERTIFICATE",

		USER_INFO_URL = "../api/systemInfo/fullUserInfo",
		PERMISSIONS_URL = "../api/subscr/manage/service/permissions",

		TEST_PERMISSIONS = [
			{
				keyname: "WEB_ALLOW_OBJECT_LIST_VIEW_PAGE",
				permissionTagId: "object_list_view_page",
				priority: 100
			},
			{
				keyname: "WEB_ALLOW_OBJECT_MAIN_MENU_ITEM",
				permissionTagId: "object_main_menu_item",
				priority: 100
			}
		];

	var mainService, httpBackend;	

	beforeEach(function () {
		//load app 'portalNMC'
		module("portalNMC");
		//get mainSvc
		//get httpBackend for mock server request/response
		inject(function ($httpBackend, _mainSvc_) {
			mainService = _mainSvc_;
			httpBackend = $httpBackend;
		});
	});

	beforeEach(function () {
		httpBackend.when("GET", /api/).respond(null);
	});

	afterEach(function () {
		httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
	});

	it("mainSvc test spec", function () {
		expect(mainService).toBeDefined();
		expect(mainService.getUseTest).toBeDefined();
		expect(mainService.checkUndefinedNull).toBeDefined();
		expect(mainService.getRequestCanceler).toBeDefined();
		expect(mainService.getRequestCanceler()).not.toBe(null);		
		httpBackend.flush();
	});

	//CHECKERS test

	it("mainSvc.checkUndefinedNull test", function () {
		var testObj = {field1: 1};
		expect(mainService.checkUndefinedNull).toBeDefined();
		httpBackend.flush();
		expect(mainService.checkUndefinedNull(undefined)).toBe(true);
		expect(mainService.checkUndefinedNull(null)).toBe(true);

		expect(mainService.checkUndefinedNull(testObj)).toBe(false);
		expect(mainService.checkUndefinedNull(EMPTY_OBJECT)).toBe(false);
		expect(mainService.checkUndefinedNull('undefined')).toBe(false);
	});

	it("mainSvc.checkEmptyObject test", function () {
		var testObj = {field1: 1};
		expect(mainService.checkEmptyObject).toBeDefined();
		httpBackend.flush();

		expect(mainService.checkEmptyObject(testObj)).toBe(false);
		expect(mainService.checkEmptyObject(EMPTY_OBJECT)).toBe(true);
		expect(mainService.checkEmptyObject('undefined')).toBe(false);
	});

	it("mainSvc.checkUndefinedEmptyNullValue test", function () {
		httpBackend.flush();
		expect(mainService.checkUndefinedEmptyNullValue).toBeDefined();

		//false 
		expect(mainService.checkUndefinedEmptyNullValue(123)).toBe(false);
		expect(mainService.checkUndefinedEmptyNullValue("test")).toBe(false);

		//true
		expect(mainService.checkUndefinedEmptyNullValue(null)).toBe(true);
		expect(mainService.checkUndefinedEmptyNullValue(undefined)).toBe(true);
		expect(mainService.checkUndefinedEmptyNullValue("")).toBe(true);
	});

	it("mainSvc.isNumeric test", function () {
		httpBackend.flush();
		expect(mainService.isNumeric).toBeDefined();

		expect(mainService.isNumeric("")).toBe(false);
		expect(mainService.isNumeric("string")).toBe(false);
		expect(mainService.isNumeric("string123")).toBe(false);
		expect(mainService.isNumeric(1.0/0)).toBe(false);

		expect(mainService.isNumeric(undefined)).toBe(false);
		expect(mainService.isNumeric(null)).toBe(false);

		expect(mainService.isNumeric(123)).toBe(true);
		expect(mainService.isNumeric(0)).toBe(true);
		expect(mainService.isNumeric(-123.0)).toBe(true);
	});

	it("mainSvc.checkNumericValue test", function () {
		httpBackend.flush();
		expect(mainService.checkNumericValue).toBeDefined();
		
		expect(mainService.checkNumericValue("string")).toBe(false);
		expect(mainService.checkNumericValue("string123")).toBe(false);
		expect(mainService.checkNumericValue(1.0/0)).toBe(false);

		expect(mainService.checkNumericValue("")).toBe(true);
		expect(mainService.checkNumericValue(123)).toBe(true);
		expect(mainService.checkNumericValue(0)).toBe(true);
		expect(mainService.checkNumericValue(-123.0)).toBe(true);
	});

	it("mainSvc.checkPositiveNumberValue test", function () {
		httpBackend.flush();
		expect(mainService.checkPositiveNumberValue).toBeDefined();

		expect(mainService.checkPositiveNumberValue(undefined)).toBe(false);
		expect(mainService.checkPositiveNumberValue(null)).toBe(false);

		expect(mainService.checkPositiveNumberValue("")).toBe(false);
		expect(mainService.checkPositiveNumberValue("string")).toBe(false);
		expect(mainService.checkPositiveNumberValue("string123")).toBe(false);
		expect(mainService.checkPositiveNumberValue(1.0/0)).toBe(false);
		//number is < 0
		expect(mainService.checkPositiveNumberValue(-123.0)).toBe(false);
		//true result
		expect(mainService.checkPositiveNumberValue(123)).toBe(true);
		expect(mainService.checkPositiveNumberValue(0)).toBe(true);
		expect(mainService.checkPositiveNumberValue(123.4)).toBe(true);		
		
	});

	it("mainSvc.checkHHmm test", function () {
		httpBackend.flush();
		expect(mainService.checkHHmm).toBeDefined();
		
		expect(mainService.checkHHmm("string")).toBe(false);
		expect(mainService.checkHHmm("string123")).toBe(false);
		expect(mainService.checkHHmm(1.0/0)).toBe(false);

		expect(mainService.checkHHmm("")).toBe(false);
		expect(mainService.checkHHmm(123)).toBe(false);
		expect(mainService.checkHHmm(0)).toBe(false);
		expect(mainService.checkHHmm(-123.0)).toBe(false);

		expect(mainService.checkHHmm(undefined)).toBe(false);
		expect(mainService.checkHHmm(null)).toBe(false);

		//?expect(mainService.checkHHmm("11112:111")).toBe(false);

		expect(mainService.checkHHmm("12:45")).toBe(true);
		expect(mainService.checkHHmm("00:00")).toBe(true);
		expect(mainService.checkHHmm("23:59")).toBe(true);
	});

	//USER info tests

	it("mainSvc.isSystemuser is false test, userInfo is null", function () {		
		expect(mainService.isSystemuser).toBeDefined();
		httpBackend.flush();
		expect(mainService.isSystemuser()).toBe(false);		
	});

	it("mainSvc.isSystemuser is false test, userInfo is NOT null", function () {
		httpBackend.expect("GET", USER_INFO_URL).respond(USER_INFO);		
		expect(mainService.isSystemuser).toBeDefined();
		httpBackend.flush();
		expect(mainService.isSystemuser()).toBe(false);		
	});

	it("mainSvc.isSystemuser is true test", function () {
		var systemUser = angular.copy(USER_INFO);
		systemUser._system = true;
		httpBackend.expectGET(USER_INFO_URL).respond(systemUser);
		httpBackend.flush();
		expect(mainService.isSystemuser()).toBe(true);
		
	});

	it("mainSvc.isRma is false test, userInfo is null", function () {
		expect(mainService.isRma).toBeDefined();
		httpBackend.flush();
		expect(mainService.isRma()).toBe(false);		
	});

	it("mainSvc.isRma is false test, userInfo is NOT null", function () {
		httpBackend.expect("GET", USER_INFO_URL).respond(USER_INFO);		
		expect(mainService.isRma).toBeDefined();
		httpBackend.flush();
		expect(mainService.isRma()).toBe(false);		
	});

	it("mainSvc.isRma is true test", function () {
		var rmaUser = angular.copy(USER_INFO);
		rmaUser.isRma = true;
		httpBackend.expectGET(USER_INFO_URL).respond(rmaUser);
		httpBackend.flush();
		expect(mainService.isRma()).toBe(true);
		
	});

	it("mainSvc.isAdmin is false test, userInfo is null", function () {
		expect(mainService.isAdmin).toBeDefined();
		httpBackend.flush();
		expect(mainService.isAdmin()).toBe(false);		
	});

	it("mainSvc.isAdmin is false test, userInfo is NOT null", function () {
		httpBackend.expect("GET", USER_INFO_URL).respond(USER_INFO);		
		expect(mainService.isAdmin).toBeDefined();
		httpBackend.flush();
		expect(mainService.isAdmin()).toBe(false);		
	});

	it("mainSvc.isAdmin is true test", function () {
		var adminUser = angular.copy(USER_INFO);
		adminUser.isAdmin = true;
		httpBackend.expectGET(USER_INFO_URL).respond(adminUser);
		httpBackend.flush();
		expect(mainService.isAdmin()).toBe(true);
		
	});

	it("mainSvc.isCabinet is false test, userInfo is null", function () {		
		expect(mainService.isCabinet).toBeDefined();
		httpBackend.flush();
		expect(mainService.isCabinet()).toBe(false);		
	});

	it("mainSvc.isCabinet is false test, userInfo is NOT null", function () {
		httpBackend.expect("GET", USER_INFO_URL).respond(USER_INFO);		
		expect(mainService.isCabinet).toBeDefined();
		httpBackend.flush();
		expect(mainService.isCabinet()).toBe(false);		
	});

	it("mainSvc.isCabinet is true test", function () {
		var cabinetUser = angular.copy(USER_INFO);
		cabinetUser.isCabinet = true;
		httpBackend.expectGET(USER_INFO_URL).respond(cabinetUser);
		httpBackend.flush();
		expect(mainService.isCabinet()).toBe(true);
		
	});

	it("mainSvc.isTestMode is false test, userInfo is null", function () {		
		expect(mainService.isTestMode).toBeDefined();
		httpBackend.flush();
		expect(mainService.isTestMode()).toBe(false);		
	});

	it("mainSvc.isTestMode is false test, userInfo is NOT null", function () {
		httpBackend.expect("GET", USER_INFO_URL).respond(USER_INFO);		
		expect(mainService.isTestMode).toBeDefined();
		httpBackend.flush();
		expect(mainService.isTestMode()).toBe(false);		
	});

	//TEST mode test

	it("mainSvc.isTestMode is true test", function () {
		var testCertUser = angular.copy(USER_INFO);
		testCertUser.subscrType = TEST_CERT_SUBSCR;
		httpBackend.expectGET(USER_INFO_URL).respond(testCertUser);
		httpBackend.flush();
		expect(mainService.isTestMode()).toBe(true);
		
	});

	it("mainSvc.getUseColorHighlightIndicatorData test", function () {		
		expect(mainService.getUseColorHighlightIndicatorData).toBeDefined();
		httpBackend.flush();
		expect(mainService.getUseColorHighlightIndicatorData()).toBe(true);		
	});

	it("mainSvc.getViewSystemInfo test", function () {		
		expect(mainService.getViewSystemInfo).toBeDefined();
		httpBackend.flush();
		expect(mainService.getViewSystemInfo()).toBe(true);		
	});

	it("mainSvc.isReadonly is false test, userInfo is null", function () {		
		expect(mainService.isReadonly).toBeDefined();
		httpBackend.flush();
		expect(mainService.isReadonly()).toBe(false);		
	});

	it("mainSvc.isReadonly is false test, userInfo is NOT null", function () {
		httpBackend.expect("GET", USER_INFO_URL).respond(USER_INFO);		
		expect(mainService.isReadonly).toBeDefined();
		httpBackend.flush();
		expect(mainService.isReadonly()).toBe(false);		
	});

	it("mainSvc.isReadonly is true test", function () {
		var readOnlyUser = angular.copy(USER_INFO);
		readOnlyUser.isReadonly = true;
		httpBackend.expectGET(USER_INFO_URL).respond(readOnlyUser);
		httpBackend.flush();
		expect(mainService.isReadonly()).toBe(true);
		
	});

	//Permissions tests
	
	it("mainSvc.getContextIds test", function () {		
		httpBackend.flush();
		expect(mainService.getContextIds()).toEqual([]);		
	});
	
	it("mainSvc.getLoadedServicePermission test, isCabinet is false", function () {						
		httpBackend.flush();
		expect(mainService.getLoadedServicePermission()).not.toBe(null);		
	});	

	it("mainSvc.getUserServicesPermissions test, isCabinet is false", function () {				
		httpBackend.expect("GET", PERMISSIONS_URL).respond(TEST_PERMISSIONS);		
		httpBackend.flush();
		expect(mainService.getContextIds().length).toEqual(4);		
	});

	it("mainSvc.getUserServicesPermissions test, isCabinet is true", function () {		
		var cabinetUser = angular.copy(USER_INFO);
		cabinetUser.isCabinet = true;
		httpBackend.expectGET(USER_INFO_URL).respond(cabinetUser);		
		httpBackend.flush();

		httpBackend.expect("GET", PERMISSIONS_URL).respond(TEST_PERMISSIONS);
		mainService.getUserServicesPermissions();		
		httpBackend.flush();

		expect(mainService.getContextIds()).toEqual(TEST_PERMISSIONS);		
	});

	it("mainSvc.checkContext test", function () {		
		httpBackend.expect("GET", PERMISSIONS_URL).respond(TEST_PERMISSIONS);		
		httpBackend.flush();
		
		expect(mainService.checkContext(TEST_PERMISSIONS[0].permissionTagId)).toBe(true);

		var deniedPermissionTag = "object_map_idx";
		expect(mainService.checkContext(deniedPermissionTag)).toBe(false);
	});

});