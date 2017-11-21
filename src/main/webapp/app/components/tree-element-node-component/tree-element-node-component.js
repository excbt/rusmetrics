/*global angular, window, console*/
(function () {
    'use strict';
    
    var treeElementNodeComponentOpt = {
        bindings: {
            'spnode': '<'
        },
        templateUrl: 'components/tree-element-node-component/tree-element-node-component.html',
        controller: treeElementNodeController
    };
    
    treeElementNodeController.$inject = ['$scope', '$element', '$attrs'];
    
    function treeElementNodeController($scope, $element, $attrs) {
        /*jshint validthis: true*/
        var ctrl = this;
        ctrl.data = {};
        ctrl.data.chartModes = ['CRITICALS', 'CATEGORIES', 'TYPES'];
        ctrl.data.chartClass = 'col-xs-' + 12 / ctrl.data.chartModes.length + ' noPadding'; // 12 - col count in grid
    }
    
    angular.module('portalNMC')
        .component('treeElementNodeComponent', treeElementNodeComponentOpt);
    
} ());