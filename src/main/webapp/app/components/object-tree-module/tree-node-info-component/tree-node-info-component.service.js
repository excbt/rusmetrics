/*global angular*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .service('treeNodeInfoComponentService', Service);

    Service.$inject = [];

    /* @ngInject */
    function Service() {
        var defaultWidgets = [
            {
                keyname: "INFO_VIEW",
                caption: "Информация",
                stateName: "objectsPTree.treeNodeInfo.infoView"
            }
        ];
        
        var nodeWidgets = [            
            {
                keyname: "CONTOBJECT_CONTROL",
                caption: "Контроль",
                stateName: "objectsPTree.treeNodeInfo.contObjectControl"
            },
            {
                keyname: "NOTIFICATION",
                caption: "Уведомления",
                stateName: "objectsPTree.treeNodeInfo.nodeNotifications"
            }
        ];
        
        var zpointNodeWidgets = [
            {
                keyname: "INDICATOR_VIEW",
                caption: "Показания",
                stateName: "objectsPTree.treeNodeInfo.indicatorView"
            }
        ];
        
        this.getNodeWidgets = getNodeWidgets;

        ////////////////

        function getNodeWidgets() {
            return nodeWidgets;
        }
    }
})();