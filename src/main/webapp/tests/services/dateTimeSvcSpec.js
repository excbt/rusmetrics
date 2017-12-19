describe("Date time service tests:", function () {

	var svc, dateTimeSvc, dts;
	beforeEach(module('portalNMC'));
	beforeEach(inject(function(dateTimeSvc, $injector, _dateTimeSvc_) {		
		dateTimeSvc = dateTimeSvc;
		svc = $injector.get('dateTimeSvc');
		dts = _dateTimeSvc_;		
	}));

	it('Service test:', function () {		
		expect(svc).toBeDefined();
		expect(dts).toBeDefined();
		expect(dateTimeSvc).not.toBeDefined();		
	});

	it('Date formating test:', function () {
		var result = dts.dateFormating();
		expect(result).toBe(null);
		var inputData1 = [2017, 6];
		result = dts.dateFormating(inputData1);
		expect(result).toEqual("01.06.2017 00:00:00");
		var inputData = [2017, 12, 13, 12, 26, 37, 568000000];		
		result = dts.dateFormating(inputData);
		expect(result).toEqual("13.12.2017 12:26:37");
		var formatString = "YYYY-MM-DD HH:mm:ss";
		result = dts.dateFormating(inputData, formatString);
		expect(result).toEqual("2017-12-13 12:26:37");
	});

})