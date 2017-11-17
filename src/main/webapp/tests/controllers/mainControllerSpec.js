/*global describe, beforeEach, module, it, expect, inject, console, xit, xdescribe*/
describe("Main controller testing:", function () {
	var APP_NAME = "portalNMC",
		CONTROLLER_NAME = "MainCtrl",
        TEST_USER_INFO = {
            firstName: "Karma test user"
        };
	var ctrl, rootScope, scope, controller, httpBackend;

	beforeEach(function () {
		module(APP_NAME);

		inject(function(_$controller_, _$rootScope_) {
			controller = _$controller_;
			rootScope = _$rootScope_;
            rootScope.userInfo = Object.assign({}, TEST_USER_INFO);
			ctrl = controller(CONTROLLER_NAME, {$scope: {}});
		});

	});

	it("controller is defined:", function (){
		expect(ctrl).toBeDefined();
        expect(ctrl.mainCtrlSettings).toBeDefined();
        expect(ctrl.data).toBeDefined();
        expect(rootScope).toBeDefined();
        
        expect(ctrl.mainCtrlSettings.showFullMenuFlag).toEqual(true);
        expect(ctrl.mainCtrlSettings.ctxId).toEqual("nmc_main");
        
        expect(ctrl.debugModeFlag).toEqual(false);
	});
    
    it("menu labels:", function () {
        expect(ctrl.data.menuLabels).toBeDefined();
        var testMenuLabels = {
            object_menu_item: "Объекты",
            object_new_menu_item: "Объекты (NEW!)",
            report_menu_item: "Отчеты",
            setting_menu_item: "Настройки",
            admin_menu_item: "Управление",
            log_menu_item: "Журналы",
            energy_menu_item: "Энергоэффективность",
            test_menu_item: "Тест"
        };
        
        expect(Object.keys(ctrl.data.menuLabels).length).toEqual(Object.keys(testMenuLabels).length);
        
        for (var key in testMenuLabels) {
            expect(ctrl.data.menuLabels[key]).toEqual(testMenuLabels[key]);
        }
    });
    
    it("setMenuVisibles test:", function() {
        expect(ctrl.setMenuVisibles).toBeDefined();
    });
    
    it("debugModeClick test:", function() {
        expect(ctrl.debugModeClick).toBeDefined();
        ctrl.debugModeClick();
        expect(ctrl.debugModeFlag).toEqual(true);
        ctrl.debugModeClick();
        expect(ctrl.debugModeFlag).toEqual(false);
    });
    
//    it("setPageTitle test:", function () {
//        console.log("setPageTitle test: Wazzzuuuupppp: How is it test?");
//    });
    
    it("menu massive:", function () {
        var testMenuItems = {
            object_menu_item: true,
            object_new_menu_item: false,
            report_menu_item: false,
            setting_menu_item: false,
            admin_menu_item: false,
            log_menu_item: false,
            energy_menu_item: false,
            test_menu_item: false
        };
        expect(ctrl.menuMassive).toBeDefined();        
//        console.log(ctrl.menuMassive);
//        console.log(Object.keys(ctrl.menuMassive));
        expect(Object.keys(ctrl.menuMassive).length).toEqual(Object.keys(testMenuItems).length);
        
        for (var key in testMenuItems) {
            expect(ctrl.menuMassive[key]).toEqual(testMenuItems[key]);
        }
    });
    
    it("clickMenu & setDefaultMenuState test:", function () {
        var clickedMenuItem = "log_menu_item",
            defaultMenuItem = "object_menu_item";        
        ctrl.clickMenu(clickedMenuItem);
        expect(ctrl.menuMassive[clickedMenuItem]).toEqual(true);
        expect(ctrl.menuMassive[defaultMenuItem]).toEqual(false);
        ctrl.setDefaultMenuState();
        expect(ctrl.menuMassive[clickedMenuItem]).toEqual(false);
        expect(ctrl.menuMassive[defaultMenuItem]).toEqual(true);
    });
    
    it("emptyString test:", function () {
        expect(ctrl.emptyString).toBeDefined();
        var emptyStr = "",
            nullStr = null,
            notEmptyStr = "test text";
        expect(ctrl.emptyString(emptyStr)).toEqual(true);
        expect(ctrl.emptyString(nullStr)).toEqual(true);
        expect(ctrl.emptyString(notEmptyStr)).toEqual(false);
    });
    
    it("changePasswordInit test:", function () {
        expect(rootScope.userInfo).toBeDefined();
        ctrl.changePasswordInit();
        expect(ctrl.data.userInfo).toEqual(TEST_USER_INFO);
    });
    
    it("isRma test", function () {
//        console.log("isRma test:", rootScope.userInfo);
        expect(ctrl.isRma()).toEqual(false);
        rootScope.userInfo.isRma = true;
        expect(ctrl.isRma()).toEqual(true);
        rootScope.userInfo.isRma = false;
        expect(ctrl.isRma()).toEqual(false);
    });
    
    xit("isRma test fail - need check interface:", function () {        
        // test fail - need check on interface this case: userInfo.isRma is no boolean value
        rootScope.userInfo.isRma = "test";
        expect(ctrl.isRma()).toEqual(false);
    });
    
    it("isCabinet test:", function () {
//        console.log("isRma test:", rootScope.userInfo);
        expect(ctrl.isCabinet()).toEqual(false);
        rootScope.userInfo.isCabinet = true;
        expect(ctrl.isCabinet()).toEqual(true);
        rootScope.userInfo.isCabinet = false;
        expect(ctrl.isCabinet()).toEqual(false);
    });
    
//    it("", function () {
//        console.log("", rootScope.userInfo);
//    });
});