/*global angular, console*/
/***
    created by Artamonov A.A. , Dec. 2017
*/
(function () {
    'use strict';
    
    angular.module('objectTreeModule')
        .component('contObjectControlComponent', {
            bindings: {
                node: '<'
            },
            templateUrl: "components/object-tree-module/cont-object-control-component/cont-object-control-component.html",
            controller: contObjectControlComponentController
        });
    
    contObjectControlComponentController.$inject = ['$scope', '$element', '$attrs', 'contObjectControlComponentService', '$stateParams'];
    
    function contObjectControlComponentController($scope, $element, $attrs, contObjectControlComponentService, $stateParams) {
        /*jshint validthis: true*/
        var ctrl = this;
        
        var IMG_PATH_MONITOR_TEMPLATE = "components/object-tree-module/cont-object-control-component/object-state-",
            IMG_EXT = ".png";
        
        var contObjectCtrlSvc = contObjectControlComponentService;
        
        function errorCallback(e) {
            console.error(e);
        }
        
        
        function successLoadObjectsCallback(resp) {
            console.log(resp);
            var tmpBuf = angular.copy(resp.data);
            tmpBuf.forEach(function (elm) {
                elm.imgpath = IMG_PATH_MONITOR_TEMPLATE + elm.colorKey.toLowerCase() + IMG_EXT;
            });
            ctrl.objects = tmpBuf;
        }
        
        //get objects with statuses
        ctrl.loadObjects = function (nodeId) {
            contObjectCtrlSvc.loadNodeObjects(nodeId).then(successLoadObjectsCallback, errorCallback);
        };
        
        ctrl.$onInit = function () {
            console.log($stateParams);
            var node = $stateParams.node;
            if (angular.isDefined(node) && node !== null) {
                ctrl.loadObjects(node.id || node._id);
            }
        };
    }
    
}());