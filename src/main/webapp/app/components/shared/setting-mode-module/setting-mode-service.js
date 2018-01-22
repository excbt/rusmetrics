/*global angular*/
(function () {
    'use strict';

    angular
        .module('settingModeModule')
        .service('settingModeService', Service);

    Service.$inject = ['$rootScope'];

    /* @ngInject */
    function Service($rootScope) {
        
        var EVENTS = {
            modeStateChanged: "settingModeService:modeStateChanged"
        };
        
        var modeState = false;
        
        var svc = this;
        svc.EVENTS = EVENTS;
        svc.getModeState = getModeState;
        svc.setModeState = setModeState;

        ////////////////

        function getModeState() {            
            return modeState;
        }
        
        function setModeState(mState) {
            if (angular.isDefined(mState) && mState === true) {
                modeState = mState;
            } else {
                modeState = false;
            }
            $rootScope.$broadcast(EVENTS.modeStateChanged);
        }
    }
})();