/*global angular*/
(function () {
    'use strict';

    angular
        .module('portalNMC')
        .service('mainHelperService', Service);

    Service.$inject = [];

    /* @ngInject */
    function Service() {
        var leftMainMenu = {};
        this.getLeftMainMenu = getLeftMainMenu;
        this.setLeftMainMenu = setLeftMainMenu;

        ////////////////

        function getLeftMainMenu() {
            return leftMainMenu;
        }
        function setLeftMainMenu(lmenu) {
            leftMainMenu = lmenu;
        }
    }
})();