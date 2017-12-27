/*global angular*/
(function () {
    'use strict';

    angular
        .module('objectTreeModule')
        .service('treeNodeInfoComponentService', Service);

    Service.$inject = [];

    /* @ngInject */
    function Service() {
        
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
        
        this.getNodeWidgets = getNodeWidgets;

        ////////////////

        function getNodeWidgets() {
            return nodeWidgets;
        }
    }
})();