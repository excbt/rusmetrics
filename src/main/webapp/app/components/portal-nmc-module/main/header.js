/*global angular, document, console, $*/
(function() {
    'use strict';

    angular
        .module('portalNMC')
        .component('header', {
            bindings: {
                menu: "<"
            },
            templateUrl: "components/portal-nmc-module/main/header.html",
            controller: Controller
        });

    Controller.$inject = ['$mdSidenav', '$timeout', '$rootScope', '$scope', 'notificationFactory', 'mainSvc', '$http', 'mainHelperService'];

    /* @ngInject */
    function Controller($mdSidenav, $timeout, $rootScope, $scope, notificationFactory, mainSvc, $http, mainHelperService){
        var vm = this;
        vm.leftMainMenu = mainHelperService.getLeftMainMenu();
        
        vm.menuToggleList = function () {
            $mdSidenav('left-main-menu').toggle();
        };
                           
        vm.openUserMenu = function ($mdMenu, ev) {
            $mdMenu.open(ev);
        };
                           
        vm.startSearch = function () {            
            if (vm.searchFlag) {
                vm.cancelSearch();
                return;
            }
            vm.searchFlag = true;
            $timeout(function () {
                var el = document.getElementById("searchInput");
                el.focus();
            }, 0);
        };
                           
        vm.cancelSearch = function () {            
            vm.searchFlag = false;
            vm.mainFilter = "";
            $rootScope.$broadcast('mainSearch:filtering', {filter: vm.mainFilter});
        };
        
//        $scope.$on('leftMainMenu:click', function (ev, args) {
//            console.log(args);
//            vm.leftMainMenu = args.menu; //'object_new_menu_item' === args.menu;
//        });
                           
        vm.isObjectsPTree = function () {
            return vm.leftMainMenu.object_new_menu_item;
        };
                           
//        vm.mainFiltering = function (filterString) {
//            $rootScope.$broadcast('mainSearch:filtering', {filter: filterString});
//        };
        vm.searching = function () {
            if (vm.mainFilter === null || vm.mainFilter === '') {
                return false;
            }
            $rootScope.$broadcast('mainSearch:filtering', {filter: vm.mainFilter});
        };
                function errorCallback (e) {
            console.log(e);
            notificationFactory.errorInfo("Не удалось сменить пароль!", "Проверьте правильность текущего пароля.");
        }
        
        vm.emptyString = mainSvc.checkUndefinedEmptyNullValue;

    // *****************************************************************************
    //          work with user password
    // *****************************************************************************
        vm.changePasswordInit = function () {            
            vm.userInfo = angular.copy($rootScope.userInfo);
        };

        vm.checkPasswordFields = function (userInfo) {
            if (mainSvc.checkUndefinedNull(userInfo)) {
                return false;
            }
            var result = true;
            if (vm.emptyString(userInfo.oldPassword)) {
                result = false;
            }
            if (vm.emptyString(userInfo.newPassword)) {
                result = false;
            }
            if (userInfo.newPassword !== userInfo.newPasswordConfirm) {
                result = false;
            }
            return result;
        };

        function checkPassword (userInfo) {
            if (mainSvc.checkUndefinedNull(userInfo)) {
                return false;
            }
            var result = true;
            if (vm.emptyString(userInfo.oldPassword)) {
                notificationFactory.errorInfo("Ошибка", "Поле \"Текущий пароль\" должно быть заполнено!");
                result = false;
            }
            if (vm.emptyString(userInfo.newPassword)) {
                notificationFactory.errorInfo("Ошибка", "Пароль не должен быть пустым!");
                result = false;
            }
            if (userInfo.newPassword !== userInfo.newPasswordConfirm) {
                notificationFactory.errorInfo("Ошибка", "Поля \"Пароль\" и \"Подтверждение пароля\" не совпадают!");
                result = false;
            }
            return result;
        }

          //change user password
        vm.changeUserPassword = function (userInfo) {
            if (checkPassword(userInfo) === false) {return "Password is incorrect!";}
            var url = "../api/systemInfo/passwordChange",
            params = {
                oldPassword : userInfo.oldPassword,
                newPassword : userInfo.newPassword
            };
            $http({
                method: "PUT",
                url: url,
                params: params
            }).then(function () {
                notificationFactory.success();
                $('#changePasswordModal').modal('hide');
            }, errorCallback);
        };
    }
})();