xdescribe("PortalNMC: energoPassportSvc tests:", function () {
	var APP_NAME = "portalNMC",
		CONT_OBJECT_PASSPORT_URL = "../api/subscr/energy-passports/cont-objects",
		PASSPORT_URL = "../api/subscr/energy-passports",
		TEST_CONTOBJECT_PASSPORT = {
			id: 1,
			passportName: "Test unit passport",
			description: "Test unit passport description",
			passportDate: [2017, 6, 6],
			templateKeyname: "ENERGY_PASSPORT_X"
		},
		TEST_OBJECT_ID = 1111;
	var energoPasService, httpBackend;

	beforeEach(module(APP_NAME));

	beforeEach(inject(function ($httpBackend, _energoPassportSvc_) {
		httpBackend = $httpBackend;
		energoPasService = _energoPassportSvc_;
	}));

	beforeEach(function () {
		httpBackend.when("GET", /api/).respond(null);
	});

	afterEach(function () {
		httpBackend.verifyNoOutstandingExpectation();
		httpBackend.verifyNoOutstandingRequest();
	});

	it("energoPassportSvc is defined:", function () {
		expect(energoPasService).toBeDefined();
		httpBackend.flush();
	});

	it("energoPassportSvc base function:", function () {
		expect(energoPasService).toBeDefined();
		httpBackend.flush();
		expect(energoPasService.getDocumentTypes).toBeDefined();
		expect(Object.keys(energoPasService.getDocumentTypes()).length).toEqual(4);

		expect(energoPasService.getEnergyDeclarationForms).toBeDefined();
		expect(Object.keys(energoPasService.getEnergyDeclarationForms()).length).toEqual(2);
	});

	it("createContObjectPassport:", function () {
		httpBackend.flush();
		expect(energoPasService.createContObjectPassport).toBeDefined();
		httpBackend.expect("POST", CONT_OBJECT_PASSPORT_URL + "/" + TEST_OBJECT_ID).respond(TEST_CONTOBJECT_PASSPORT);		
		expect(energoPasService.createContObjectPassport(TEST_CONTOBJECT_PASSPORT, TEST_OBJECT_ID)).not.toBe(null);
		httpBackend.flush();
	});

	it("createPassport:", function () {
		httpBackend.flush();
		expect(energoPasService.createPassport).toBeDefined();
		httpBackend.expect("POST", PASSPORT_URL).respond(TEST_CONTOBJECT_PASSPORT);		
		expect(energoPasService.createPassport(TEST_CONTOBJECT_PASSPORT, TEST_OBJECT_ID)).not.toBe(null);
		httpBackend.flush();
	});

	it("updatePassport:", function () {
		httpBackend.flush();
		expect(energoPasService.updatePassport).toBeDefined();
		httpBackend.expect("PUT", PASSPORT_URL + "/" + TEST_CONTOBJECT_PASSPORT.id).respond(TEST_CONTOBJECT_PASSPORT);		
		expect(energoPasService.updatePassport(TEST_CONTOBJECT_PASSPORT)).not.toBe(null);
		httpBackend.flush();
	});

	it("updatePassport, reject:", inject(function ($rootScope) {
		httpBackend.flush();
		var UPDATE_ERROR_MESSAGE = "Update: document id is undefined or null!";
		expect(energoPasService.updatePassport).toBeDefined();
		//httpBackend.when("PUT", PASSPORT_URL + "/" + TEST_CONTOBJECT_PASSPORT.id).respond(TEST_CONTOBJECT_PASSPORT);		
		var rejectValue;
		//console.log(energoPasService.updatePassport(null));
		var test_null_passport = null;
		energoPasService.updatePassport(test_null_passport)
			.then(function (resp) {}, function (err) {rejectValue = err});

		expect(rejectValue).toBeUndefined();		
		$rootScope.$apply();
		expect(rejectValue).toEqual(UPDATE_ERROR_MESSAGE);

		var test_passport_without_id = angular.copy(TEST_CONTOBJECT_PASSPORT);
		delete test_passport_without_id.id;
		var rejectValue2;
		energoPasService.updatePassport(test_passport_without_id)
			.then(function (resp) {}, function (err) {rejectValue2 = err});

		expect(rejectValue2).toBeUndefined();		
		$rootScope.$apply();
		expect(rejectValue2).toEqual(UPDATE_ERROR_MESSAGE);		
	}));

	it("updateContObjectPassport:", function () {
		httpBackend.flush();
		expect(energoPasService.updateContObjectPassport).toBeDefined();
		var reqUrl = CONT_OBJECT_PASSPORT_URL + "/" + TEST_OBJECT_ID;
		// console.log(reqUrl);
		httpBackend.expect("PUT", reqUrl).respond(TEST_CONTOBJECT_PASSPORT);
		var upRequest = energoPasService.updateContObjectPassport(TEST_CONTOBJECT_PASSPORT, TEST_OBJECT_ID);
		// console.log(upRequest);
		expect(upRequest).not.toBe(null);
		httpBackend.flush();
	});

	it("updateContObjectPassport, reject:", inject(function ($rootScope) {
		httpBackend.flush();
		var UPDATE_ERROR_MESSAGE = "Update: document id is undefined or null!";
		expect(energoPasService.updateContObjectPassport).toBeDefined();
		//httpBackend.when("PUT", PASSPORT_URL + "/" + TEST_CONTOBJECT_PASSPORT.id).respond(TEST_CONTOBJECT_PASSPORT);		
		var rejectValue;
		//console.log(energoPasService.updatePassport(null));
		var test_null_passport = null;
		energoPasService.updateContObjectPassport(test_null_passport, TEST_OBJECT_ID)
			.then(function (resp) {}, function (err) {rejectValue = err});

		expect(rejectValue).toBeUndefined();		
		$rootScope.$apply();
		expect(rejectValue).toEqual(UPDATE_ERROR_MESSAGE);

		var test_passport_without_id = angular.copy(TEST_CONTOBJECT_PASSPORT);
		delete test_passport_without_id.id;
		var rejectValue2;
		energoPasService.updateContObjectPassport(test_passport_without_id, TEST_OBJECT_ID)
			.then(function (resp) {}, function (err) {rejectValue2 = err});

		expect(rejectValue2).toBeUndefined();		
		$rootScope.$apply();
		expect(rejectValue2).toEqual(UPDATE_ERROR_MESSAGE);		
	}));

	it("loadPassports test", function () {
		httpBackend.flush();
		var passportsArray = [],
			responsePassports;
		passportsArray.push(TEST_CONTOBJECT_PASSPORT);

		httpBackend.expect("GET", PASSPORT_URL).respond(passportsArray);
		energoPasService.loadPassports()
			.then(function(resp) {responsePassports = resp.data});
		expect(responsePassports).toBeUndefined();
		httpBackend.flush();
		//console.log(responsePassports);
		expect(responsePassports).toEqual(passportsArray);

		var passportId = TEST_CONTOBJECT_PASSPORT.id,
			responsePassport;
		httpBackend.expect("GET", PASSPORT_URL + "/" + passportId).respond(TEST_CONTOBJECT_PASSPORT);
		energoPasService.loadPassports(passportId)
			.then(function(resp) {responsePassport = resp.data});
		httpBackend.flush();	
		expect(responsePassport).toEqual(TEST_CONTOBJECT_PASSPORT);	
	});

	it("deletePassport test", inject(function ($rootScope) {
		httpBackend.flush();
		expect(energoPasService.deletePassport).toBeDefined();

		var deletePassportId = TEST_CONTOBJECT_PASSPORT.id;
		httpBackend.expect("DELETE", PASSPORT_URL + "/" + deletePassportId).respond({});
		energoPasService.deletePassport(deletePassportId);
		httpBackend.flush();

		var DELETE_ERROR_MESSAGE = "Delete: document id is undefined or null!",
			deleteRejectMessage;
		energoPasService.deletePassport()
			.then(function (resp) {}, function (err) {deleteRejectMessage = err});
		expect(deleteRejectMessage).toBeUndefined();	
		$rootScope.$apply();
		expect(deleteRejectMessage).toEqual(DELETE_ERROR_MESSAGE);	
	}));

	it("loadPassportData test", function () {
		httpBackend.flush();
		expect(energoPasService.loadPassportData).toBeDefined();
	});

	it("saveEntry test", function () {
		httpBackend.flush();
		expect(energoPasService.saveEntry).toBeDefined();
	});

	it("deleteEntry test", function () {
		httpBackend.flush();
		expect(energoPasService.deleteEntry).toBeDefined();
	});

	it("savePassport test", function () {
		httpBackend.flush();
		expect(energoPasService.savePassport).toBeDefined();
	});

	it("loadContObjectPassports test", function () {
		httpBackend.flush();
		expect(energoPasService.loadContObjectPassports).toBeDefined();
	});

	it("findContObjectActivePassport test", function () {
		httpBackend.flush();
		expect(energoPasService.findContObjectActivePassport).toBeDefined();

		//incorrect input data test
		expect(energoPasService.findContObjectActivePassport()).toBe(null);
		expect(energoPasService.findContObjectActivePassport({})).toBe(null);
		expect(energoPasService.findContObjectActivePassport([])).toBe(null);

		//correct input data
		var passportArray = [],
			passport,
			arrCounter;
		for (arrCounter = 0; arrCounter < 10; arrCounter += 1) {
			passport = angular.copy(TEST_CONTOBJECT_PASSPORT);
			passport.passportDate2 = arrCounter;
			passportArray.push(passport);
		}

		var activePassport = energoPasService.findContObjectActivePassport(passportArray);
		expect(activePassport).toEqual(passportArray[passportArray.length - 1]);
	});
});