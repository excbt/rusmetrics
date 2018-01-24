/*global angular*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .service('treeNodeInfoComponentService', Service);

    Service.$inject = [];

    /* @ngInject */
    function Service() {
        var currentWidget = null;
        
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
        var svc = this;
        svc.getCurrentWidget = getCurrentWidget;
        svc.getNodeWidgets = getNodeWidgets;
        svc.setCurrentWidget = setCurrentWidget;

        ////////////////

        function getCurrentWidget() {
            return currentWidget;
        }
        
        function getNodeWidgets() {
            return nodeWidgets;
        }
        
        function setCurrentWidget(curWidget) {
            currentWidget = curWidget;
        }
    }
})();