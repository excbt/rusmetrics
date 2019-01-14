xdescribe('Test MngmtDevicesCtrl:', function() {

	var DEVICE_ID = 1,
		HEATER_ID = 123,
		HEAT_DISTRIBUTOR = 'HEAT_DISTRIBUTOR';


	beforeEach(module('portalNMC'));

	var $controller,
		$rootScope;

	beforeEach(inject(function (_$controller_, _$rootScope_) {
		$controller = _$controller_;
		$rootScope = _$rootScope_;
	}));

	describe('Speader and heater devices:', function () {

		it('Test deviceIsSpreader():', function () {
			var $scope = $rootScope.$new();
			var ctrl = $controller('MngmtDevicesCtrl', {$scope: $scope});
			$scope.data.currentModel = {};
			$scope.data.currentModel.deviceType = HEAT_DISTRIBUTOR;
			expect($scope.deviceIsSpreader()).toBe(true);
		});

		it('Test checkHeaterDevice(), device is new:', function () {
			var $scope = $rootScope.$new();
			var ctrl = $controller('MngmtDevicesCtrl', {$scope: $scope});	
			$scope.data.currentObject = {};
			$scope.data.currentObject.id = null;			
			$scope.data.currentObject.isManual = true;
			expect($scope.checkHeaterDevice).toBeDefined();
			expect($scope.checkHeaterDevice()).toBe(false);
		});

		it('Test checkHeaterDevice(), device is no new, heaterDevice is set:', function () {
			var $scope = $rootScope.$new();
			var ctrl = $controller('MngmtDevicesCtrl', {$scope: $scope});	
			$scope.data.currentObject = {};
			$scope.data.currentObject.id = DEVICE_ID;
			$scope.data.currentObject.heaterDeviceId = HEATER_ID; //set heater device Id
			expect($scope.checkHeaterDevice).toBeDefined();
			expect($scope.checkHeaterDevice()).toBe(true);
		});
	});

});