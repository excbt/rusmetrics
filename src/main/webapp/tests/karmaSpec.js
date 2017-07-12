xdescribe('Karma test:', function () {

	beforeEach(module('portalNMC'));

	var $controller,
		$rootScope;

	beforeEach(inject(function (_$controller_, _$rootScope_) {
		$controller = _$controller_;
		$rootScope = _$rootScope_;
	}));

	it('Karma hello world:', function () {
		console.log("Hello, World!");
		expect(true).toBe(true);
	});

	it('Try AngularJS App "portalNMC":', function () {
		console.log("Connect angular and module 'portalNMC' to test system.");
		var $scope = $rootScope.$new();
		var controller = $controller('MngmtDevicesCtrl', {$scope: $scope});
		expect($scope.ctrlSettings.loading).toBe(true);
	});
})