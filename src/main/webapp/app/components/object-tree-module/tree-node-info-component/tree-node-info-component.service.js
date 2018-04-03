/*global angular*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .service('treeNodeInfoComponentService', Service);

    Service.$inject = ['objectTreeService'];

    /* @ngInject */
    function Service(objectTreeService) {
        
        var EVENTS = {
            "setWidget": "treeNodeInfoComponentService:setWidget"
        };
        
        var nodeTypes = ["ELEMENT", "CONT_OBJECT", "CONT_ZPOINT", "DEVICE_OBJECT"];
        var currentWidget = {
            "ELEMENT": null,
            "CONT_OBJECT": null,
            "CONT_ZPOINT": null,
            "DEVICE_OBJECT": null,
            "DEFAULT": null
        };
        
        var defaultWidgets = [
            {
                keyname: "INFO_VIEW",
                caption: "Информация",
                stateName: "objectsPTree.treeNodeInfo.nodeInfoView"
            }
        ];
        
        var elementNodeWidgets = [
            {
                keyname: "CONTOBJECT_CONTROL",
                caption: "Контроль",
                stateName: "objectsPTree.treeNodeInfo.contObjectControl"
            },
            /*{
                keyname: "NOTIFICATION",
                caption: "Уведомления",
                stateName: "objectsPTree.treeNodeInfo.nodeNotifications"
            },*/
            {
                keyname: "MONITORING",
                caption: "Мониторинг",
                stateName: "objectsPTree.treeNodeInfo.contObjectMonitor"
            }
        ];
        
        var objectNodeWidgets = [
            {
                keyname: "CONTOBJECT_CONTROL",
                caption: "Контроль",
                stateName: "objectsPTree.treeNodeInfo.contObjectControl"
            }/*,
            {
                keyname: "MONITORING",
                caption: "Мониторинг",
                stateName: "objectsPTree.treeNodeInfo.contObjectMonitor"
            }*/
        ]; 
        
        var zpointNodeWidgets = [
            {
                keyname: "INDICATOR_VIEW",
                caption: "Показания",
                stateName: "objectsPTree.treeNodeInfo.nodeIndicatorView"
            }
        ];
        var nodeWidgets = {
            "ELEMENT": elementNodeWidgets.concat(defaultWidgets),
            "CONT_OBJECT": objectNodeWidgets.concat(defaultWidgets),
            "CONT_ZPOINT": zpointNodeWidgets.concat(defaultWidgets),
            "DEVICE_OBJECT": defaultWidgets,
            "DEFAULT": defaultWidgets
        };
        
        var svc = this;
        svc.EVENTS = EVENTS;
        svc.getCurrentWidget = getCurrentWidget;
        svc.getNodeWidgets = getNodeWidgets;
        svc.setCurrentWidget = setCurrentWidget;
//        svc. = objectTreeService.
        svc.isContObjectNode = objectTreeService.isContObjectNode;
        svc.isContZpointNode = objectTreeService.isContZpointNode;
        svc.isDeviceNode = objectTreeService.isDeviceNode;
        svc.isElementNode = objectTreeService.isElementNode;

        ////////////////

        function getCurrentWidget(nodeType) {
            return currentWidget[nodeType];
        }
        
        function getNodeWidgets(nodeType) {
            var result = nodeWidgets[nodeType];
            return result;
        }
        
        function setCurrentWidget(nodeType, curWidget) {
            currentWidget[nodeType] = curWidget;
        }
    }
})();