/*global angular, $, console*/
(function() {
    'use strict';

    angular
        .module('objectTreeModule')
        .component('contZpointMonitorComponent', {        
            bindings: {
                contZpointId: '<',
                contZpointName: '<',
                contZpointType: '<'
            },
            templateUrl: "components/object-tree-module/cont-zpoint-monitor-component/cont-zpoint-monitor-component.html",
            controller: contZpointMonitorComponentController
        });

    contZpointMonitorComponentController.$inject = [];

    /* @ngInject */
    function contZpointMonitorComponentController() {
        /*jshint validthis: true*/
        var vm = this;
        vm.showSettingsFlag = false;
        vm.$onInit = initCmpnt;
        
        vm.columns = [
            {
                name: "status",
                headerClass: "col-xs-1",
                caption: "",
                type: "img"
            },
            {
                name: "dateString",
                headerClass: "col-xs-3",
                caption: "Дата",
                type: "text"
            },
            {
                name: "event",
                headerClass: "col-xs-5",
                caption: "Событие",
                type: "text"
            },
            {
                name: "rate",
                headerClass: "col-xs-3",
                caption: "Степень отклонения",
                type: "text"
            },
        ];
        
        var events = [
            {
                status: "yellow",
                dateString: "01.02.2018 23:59",
                event: "Наименование желтого события",
                rate: "Средняя"
            },
            {
                status: "red",
                dateString: "01.02.2018 23:00",
                event: "Наименование красного события",
                rate: "Высокая"
            },
            {
                status: "green",
                dateString: "01.02.2018 00:01",
                event: "Наименование зеленого события",
                rate: "Нет"
            },
            {
                status: "yellow",
                dateString: "31.01.2018 22:00",
                event: "Наименование желтого события",
                rate: "Средняя"
            },
            {
                status: "yellow",
                dateString: "31.01.2018 21:00",
                event: "Наименование желтого события",
                rate: "Средняя"
            }
        ];
        vm.events = events.concat(events).concat(events);
        
        vm.toggleSettings = function () {
            vm.showSettingsFlag = !vm.showSettingsFlag;
        };

//        vm.$onChanges = changeCmpnt;
        
        ////////////////////////////
        function initCmpnt() {
            console.log("contZpointMonitorComponentController on Init.", vm);
        }
        
//        function changeCmpnt(changes) {
//            console.log(changes);
//        }
        
    }
})();