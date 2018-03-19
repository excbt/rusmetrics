/*global angular, $, console, moment*/
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

    contZpointMonitorComponentController.$inject = ['dateSvc'];

    /* @ngInject */
    function contZpointMonitorComponentController(dateSvc) {
        var MODES = {
            history: {
                name: "history"
            },
            situations: {
                name: "situations"
            }
        };
        /*jshint validthis: true*/
        var vm = this;
        vm.MODES = MODES;
        vm.showSettingsFlag = false;
        vm.mode = vm.MODES.situations;
        vm.$onInit = initCmpnt;
        
        var IMG_PATH_MONITOR_TEMPLATE = "components/object-tree-module/cont-zpoint-monitor-component/zpoint-state-",
            IMG_EXT = ".png",
            OBJECTS_PER_PAGE = 100;
        
        var stateFilterValues = [
            IMG_PATH_MONITOR_TEMPLATE + "red" + IMG_EXT,
            IMG_PATH_MONITOR_TEMPLATE + "yellow" + IMG_EXT,
            IMG_PATH_MONITOR_TEMPLATE + "green" + IMG_EXT,
        ];
        
        var dateFilterValues = [
            "01.02.2018",
            "31.01.2018"
        ];
        
        var timeFilterValues = [
            "23:59",
            "23:00",
            "22:00",
            "21:00",
            "00:01"
        ];
        
        var eventFilterValues = [
            "Наименование желтого события",
            "Наименование красного события",
            "Наименование зеленого события"
        ];
        
        vm.columns = [
            {
                name: "status",
                headerClass: "paddingLeft20I nmc-zp-monitor-status-th",
                caption: "",
                type: "img",
                filterValues: stateFilterValues
            },
            {
                name: "dateString",
                headerClass: "nmc-zp-monitor-date-th",
                caption: "Дата",
                type: "datetext",
                filterValues: dateFilterValues
            },
            {
                name: "timeString",
                headerClass: "noPadding nmc-zp-monitor-time-th",
                caption: "Время",
                type: "timetext",
                filterValues: timeFilterValues
            },
            {
                name: "event",
                headerClass: "nmc-zp-monitor-event-th",
                caption: "Событие",
                type: "text",
                filterValues: eventFilterValues
            }/*,
            {
                name: "rate",
                headerClass: "col-xs-3",
                caption: "Степень отклонения",
                type: "text"
            },*/
        ];
        
        var events = [
            {
                status: "yellow",
                dateTimeString: "01.02.2018 23:59",
                dateTime: new Date("02.01.2018 23:59"),
                dateString: "01.02.2018",
                timeString: "23:59",
                event: "Наименование желтого события",
                rate: "Средняя"
            },
            {
                status: "red",
                dateTimeString: "01.02.2018 23:00",
                dateTime: new Date("02.01.2018 23:00"),
                dateString: "01.02.2018",
                timeString: "23:00",
                event: "Наименование красного события. Очень длинное описание красного события, такое что не влазит на экран целиком.",
                rate: "Высокая"
            },
            {
                status: "green",
                dateTimeString: "01.02.2018 00:01",
                dateTime: new Date("02.01.2018 00:01"),
                dateString: "01.02.2018",
                timeString: "00:01",
                event: "Наименование зеленого события",
                rate: "Нет"
            },
            {
                status: "yellow",
                dateTimeString: "31.01.2018 22:00",
                dateTime: new Date("01.31.2018 22:00"),
                dateString: "31.01.2018",
                timeString: "22:00",
                event: "Наименование желтого события.",
                rate: "Средняя"
            },
            {
                status: "yellow",
                dateTimeString: "31.01.2018 21:00",
                dateTime: new Date("01.31.2018 21:00"),
                dateString: "31.01.2018",
                timeeString: "21:00",
                event: "Наименование желтого события. Очень длинное описание желтого события, такое что не влазит на экран целиком.",
                rate: "Средняя"
            }
        ];
        
        vm.orderBy = {field: 'dateTime', asc: true};
        vm.periodStart = "31.01.2018 21:00";
        vm.periodEnd = "01.02.2018 23:59";
        
        vm.setOrderBy = function (field) {
            vm.orderBy.asc = !vm.orderBy.asc;
//            var asc = vm.orderBy.field === field ? !vm.orderBy.asc : true;
//            vm.orderBy = { field: field, asc: asc };
//            vm.filterObjects();
        };
        
        vm.events = events.concat(events).concat(events);        
        
        vm.toggleSettings = function () {
            vm.showSettingsFlag = !vm.showSettingsFlag;
        };
        vm.daterange = null;
        vm.daterangeOpts = null;
        
        vm.setHistoryMode = setHistoryMode;
        vm.setSituationsMode = setSituationsMode;

//        vm.$onChanges = changeCmpnt;
        
        ////////////////////////////
        function initCmpnt() {
//            console.log("contZpointMonitorComponentController on Init.", vm);
//            console.log("01.02.2018 23:59" > "01.02.2018 00:01");
//            console.log(events);
            
            //init date time range picker
            vm.daterangeOpts = dateSvc.getDaterangeOptions();            
            vm.daterangeOpts.timePicker = true;
            vm.daterangeOpts.timePickerIncrement = 1;
            vm.daterangeOpts.timePicker24Hour = true;
            vm.daterangeOpts.locale.format = 'DD.MM.YYYY HH:mm';
            vm.daterangeOpts.separator = " - ";
            vm.daterangeOpts.eventHandlers = {
                'apply.daterangepicker': function (ev, picker) {
                    console.log(ev);
                    console.log(picker);
                    console.log(vm.daterange);
                }
            };
            
//            vm.daterangeOpts.isCustomDate = function () {
//                return "nmc-date-class";
//            };

            console.log(vm.daterangeOpts);            
            
            //init date
            vm.daterange = {
                startDate: moment().startOf('Day'),
                startDateStr: moment().startOf('Day').format("DD.MM.YYYY HH:mm"),
                endDate: moment().endOf('Day'),
                endDateStr: moment().endOf('Day').format("DD.MM.YYYY HH:mm")
            };
        }
        
//        function changeCmpnt(changes) {
//            console.log(changes);
//        }
        
        function setHistoryMode() {
            vm.mode = vm.MODES.history;            
        }
        
        function setSituationsMode() {
            vm.mode = vm.MODES.situations;
        }
        
    }
})();