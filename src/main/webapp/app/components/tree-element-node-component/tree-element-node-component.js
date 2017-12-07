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
    
    treeElementNodeController.$inject = ['$scope', '$element', '$attrs', 'treeElementNodeComponentService', '$stateParams'];
    
    function treeElementNodeController($scope, $element, $attrs, treeElementNodeComponentService, $stateParams) {
        /*jshint validthis: true*/
        var ctrl = this;
        ctrl.data = {};
        ctrl.data.chartModes = ['CRITICALS', 'CATEGORIES', 'TYPES'];
        ctrl.data.chartClass = 'col-xs-' + 12 / ctrl.data.chartModes.length + ' noPadding'; // 12 - col count in grid
        
        ctrl.contEventTypesRaw = [];
        ctrl.statListRaw = [];
        
        ctrl.contEventTypes = {};
        ctrl.statList = {};
        
        ctrl.allEventsCount = 0;
        ctrl.objectWithEventsCount = 0;
        ctrl.criticalEventsCount = 0;
        ctrl.objectWithCriticalEventsCount = 0;
        
        ctrl.isCriticalEvent = treeElementNodeComponentService.isCriticalEvent;
        
        function prepareContEventData(inputContEventData, field) {
            console.log(inputContEventData);
            if (!angular.isArray(inputContEventData)) {
                return {};
            }
            var result = {};
            inputContEventData.forEach(function (icet) {
                if (icet.hasOwnProperty(field)) {
                    result[icet[field]] = icet;
                }
            });
            return result;
        }
        
        function allEventsCalc(inputData) {
            var result = 0;
            inputData.forEach(function (stat) {
                result += stat.count;
            });
            return result;
        }
        
        function objectWithEventsCalc(inputData) {
            var result = 0;
            result = inputData.length;            
            return result;
        }
        
        function criticalEventsCalc(inputData) {
            var result = 0;
            inputData.forEach(function (stat) {
                if (ctrl.isCriticalEvent(stat, ctrl.contEventTypes)) {
                    result += stat.count;
                }
            });
            return result;
        }
        
        function objectWithCriticalEventsCalc(inputData) {
            var result = 0;
            inputData.forEach(function (stat) {
                if (ctrl.isCriticalEvent(stat, ctrl.contEventTypes)) {
                    result += 1;
                }
            });
            return result;
        }
        
        function analysisInputData(inputData) {
            if (!angular.isArray(inputData)) {
                return false;
            }
            
            //all events count
            ctrl.allEventsCount = allEventsCalc(inputData);
            
            ctrl.objectWithEventsCount = objectWithEventsCalc(inputData);
            ctrl.criticalEventsCount = criticalEventsCalc(inputData);
            ctrl.objectWithCriticalEventsCount = objectWithCriticalEventsCalc(inputData);
            
        }
        
        function errorCallback(e) {
            console.log(e);
        }
        
        function successLoadStatsCallback(resp) {
//            console.log(resp);
            ctrl.statListRaw = resp.data;
            analysisInputData(ctrl.statListRaw);
        }
        
        function successLoadContEventTypesCallback(resp) {
//            console.log(resp);
            ctrl.contEventTypesRaw = resp.data;
            ctrl.contEventTypes = prepareContEventData(ctrl.contEventTypesRaw, 'id');            
            loadNodeStats();
        }
        
        function loadContEventTypes() {
            treeElementNodeComponentService.loadContEventTypes()
                .then(successLoadContEventTypesCallback, errorCallback);
        }
        
        function loadNodeStats() {
            treeElementNodeComponentService.loadPTreeNodeStats(ctrl.spnode._id)
                .then(successLoadStatsCallback, errorCallback);
        }
        
        function initCtrl() {
            if ((angular.isUndefined(ctrl.spnode) || ctrl.spnode ===  null) && angular.isDefined($stateParams.node) && $stateParams.node !== null) {
                ctrl.spnode = $stateParams.node;
            }
            loadContEventTypes();
        }
        
        ctrl.$onInit = function () {
            initCtrl();
            console.log($stateParams);
        };
    }
    
    angular.module('portalNMC')
        .component('treeElementNodeComponent', treeElementNodeComponentOpt);
    
} ());