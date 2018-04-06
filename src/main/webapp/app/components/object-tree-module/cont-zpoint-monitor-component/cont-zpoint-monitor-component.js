/*global angular, $, console, moment*/
(function() {
    'use strict';

    angular
        .module('objectTreeModule')
        .component('contZpointMonitorComponent', {        
            bindings: {
                contObjectId: '<',
                contZpointId: '<',
                contZpointName: '<',
                contZpointType: '<',
                requestMode: '<'
            },
            templateUrl: "components/object-tree-module/cont-zpoint-monitor-component/cont-zpoint-monitor-component.html",
            controller: contZpointMonitorComponentController
        });

    contZpointMonitorComponentController.$inject = ['dateSvc', 'contZpointMonitorComponentService'];

    /* @ngInject */
    function contZpointMonitorComponentController(dateSvc, contZpointMonitorComponentService) {
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
        vm.svc = contZpointMonitorComponentService;
        vm.MODES = MODES;
        vm.showSettingsFlag = false;
        vm.mode = vm.MODES.situations;
        vm.$onInit = initCmpnt;
        
        var IMG_PATH_MONITOR_TEMPLATE = "components/object-tree-module/cont-zpoint-monitor-component/zpoint-state-",
            IMG_PATH_CONT_SERVICE_TYPE_TEMPLATE = "components/object-tree-module/cont-zpoint-monitor-component/",
            IMG_EXT = ".png",
            OBJECTS_PER_PAGE = 100,
            USER_DATE_TIME_FORMAT = dateSvc.getUserDateTimeFormat(),
            SYSTEM_DATE_FORMAT = dateSvc.getSystemDateFormat();
        
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
                name: "contServiceTypeIcon",
                headerClass: "paddingLeft20I nmc-zp-monitor-status-th",
                caption: "",
                type: "img",
                filterValues: null
            },
            {
                name: "statusIcon",
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
        
        vm.events = [];//events.concat(events).concat(events);        
        
        vm.toggleSettings = function () {
            vm.showSettingsFlag = !vm.showSettingsFlag;
        };
        vm.daterange = null;
        vm.daterangeOpts = null;
        
        vm.setHistoryMode = setHistoryMode;
        vm.setSituationsMode = setSituationsMode;

//        vm.$onChanges = changeCmpnt;
        
        ////////////////////////////
        
        function performEvents(eventsRaw) {
            var events = [];
            eventsRaw.forEach(function (elm) {                

                if (angular.isDefined(vm.contZpointType) && vm.contZpointType !== null && elm.hasOwnProperty("contServiceType") && elm.contServiceType !== vm.contZpointType) {
                    return false;
                }
                var event = {};
                event.contServiceType = elm.contServiceType;
                event.contServiceTypeIcon = IMG_PATH_CONT_SERVICE_TYPE_TEMPLATE + elm.contServiceType + IMG_EXT;
                if (elm.hasOwnProperty("contEventLevelColorKeyname")) { 
                    event.status = elm.contEventLevelColorKeyname.toLowerCase();
                    event.statusIcon = IMG_PATH_MONITOR_TEMPLATE + elm.contEventLevelColorKeyname.toLowerCase() + IMG_EXT;
                }
                event.dateTimeString = dateSvc.dateFormating(elm.contEventTime, USER_DATE_TIME_FORMAT);
                event.dateString = dateSvc.dateFormating(elm.contEventTime, "DD.MM.YYYY");
                event.timeString = dateSvc.dateFormating(elm.contEventTime, "HH:mm");
                event.event = elm.contEventType.name;
                if (elm.hasOwnProperty("contEvent") && elm.contEvent !== null && elm.contEvent.hasOwnProperty("message")) {
                    event.event += ": " + elm.contEvent.message;
                }
                events.push(event);
            });
            vm.events = events;
console.log(vm.events);
        }
        
        function performNotifications(eventsRaw) {
            var events = [];
            eventsRaw.forEach(function (elm) {
                var event = {};
                if (elm.hasOwnProperty("contEventLevelColor")) {
                    event.status = elm.contEventLevelColor.toLowerCase();
                    event.statusIcon = IMG_PATH_MONITOR_TEMPLATE + elm.contEventLevelColor.toLowerCase() + IMG_EXT;
                }
                event.dateTimeString = moment(elm.contEventTime).format(USER_DATE_TIME_FORMAT); // dateSvc.dateFormating(elm.contEventTime, USER_DATE_TIME_FORMAT);
                event.dateString = moment(elm.contEventTime).format("DD.MM.YYYY"); // dateSvc.dateFormating(elm.contEventTime, "DD.MM.YYYY");
                event.timeString = moment(elm.contEventTime).format("HH:mm"); // dateSvc.dateFormating(elm.contEventTime, "HH:mm");
                event.event = elm.contEventType.name;
                if (elm.hasOwnProperty("contEvent") && elm.contEvent !== null && elm.contEvent.hasOwnProperty("message")) {
                    event.event += ": " + elm.contEvent.message;
                }
                events.push(event);
            });
            vm.events = events;
console.log(vm.events);
        }
        
        function successLoadEventsCallback(resp) {
console.log(resp);
            if (!angular.isArray(resp.data)) {
                console.warn("Event data is incorrect: ", resp);
                return false;
            }
//                    status: "yellow",
//                dateTimeString: "01.02.2018 23:59",
//                dateTime: new Date("02.01.2018 23:59"),
//                dateString: "01.02.2018",
//                timeString: "23:59",
//                event: "Наименование желтого события",
//                rate: "Средняя"
            performEvents(resp.data);
        }
        
        function successLoadNotificationsCallback(resp) {
console.log(resp);
            if (angular.isUndefined(resp.data) || resp.data === null || !angular.isArray(resp.data.objects)) {
                console.warn("Notification data is incorrect: ", resp);
                return false;
            }            
            performNotifications(resp.data.objects);
        }
        
        function loadEvents() {
            console.log(vm.contObjectId);
            if (angular.isDefined(vm.contObjectId) && vm.contObjectId !== null) {
                vm.svc.loadEvents(vm.contObjectId)
                    .then(successLoadEventsCallback, 
                          function (err) {
                            console.error(err);
                });
            }
        }
        
        function loadNotifications() {
            var objectArray = null;
//console.log("vm.daterange.startDate", vm.daterange.startDate);  
//console.log("vm.daterange.endDate", vm.daterange.endDate); 
//console.log("USER_DATE_TIME_FORMAT", USER_DATE_TIME_FORMAT);
//console.log("SYSTEM_DATE_FORMAT", SYSTEM_DATE_FORMAT);
            var startDate = vm.daterange.startDate.format(SYSTEM_DATE_FORMAT),
                endDate = vm.daterange.endDate.format(SYSTEM_DATE_FORMAT);
            // (startDate, endDate, objectArray, eventTypeArray, categoriesArray, deviationsArray, isNew)
            vm.svc.loadNotifications(startDate, endDate, [vm.contObjectId], null, null, null, null)
                .then(successLoadNotificationsCallback, 
                      function (err) {
                        console.error(err);
            });
        }
        
        function initCmpnt() {
console.log(moment());            
console.log(moment().format(USER_DATE_TIME_FORMAT));
            loadEvents();
//            console.log("contZpointMonitorComponentController on Init.", vm);
//            console.log("01.02.2018 23:59" > "01.02.2018 00:01");
//            console.log(events);
            
            //init date time range picker
            vm.daterangeOpts = dateSvc.getDaterangeOptions();            
            vm.daterangeOpts.timePicker = true;
            vm.daterangeOpts.timePickerIncrement = 1;
            vm.daterangeOpts.timePicker24Hour = true;
            vm.daterangeOpts.locale.format = USER_DATE_TIME_FORMAT;
            vm.daterangeOpts.separator = " - ";
            vm.daterangeOpts.eventHandlers = {
                'apply.daterangepicker': function (ev, picker) {
                    console.log(ev);
                    console.log(picker);
                    console.log(vm.daterange);
                    loadNotifications();
                }
            };
            
//            vm.daterangeOpts.isCustomDate = function () {
//                return "nmc-date-class";
//            };

            console.log(vm.daterangeOpts);            
            
            //init date
            vm.daterange = {
                startDate: moment().startOf('Day'),
                startDateStr: moment().startOf('Day').format(USER_DATE_TIME_FORMAT),
                endDate: moment().endOf('Day'),
                endDateStr: moment().endOf('Day').format(USER_DATE_TIME_FORMAT)
            };
        }
        
//        function changeCmpnt(changes) {
//            console.log(changes);
//        }
        
        function setHistoryMode() {
            vm.mode = vm.MODES.history;
            // load notifications
            vm.events = [];
            // (startDate, endDate, objectArray, eventTypeArray, categoriesArray, deviationsArray, isNew)
            loadNotifications();
        }
        
        function setSituationsMode() {
            vm.mode = vm.MODES.situations;
            vm.events = [];
            loadEvents();
        }
        
//        $scope.$watch('contObjectId', function (newVal) {
//            console.log(newVal);
//        });
        
    }
})();