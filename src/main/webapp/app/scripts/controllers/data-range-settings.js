/*jslint node: true, eqeq: true*/
/*global angular, moment*/
'use strict';
var app = angular.module('portalNMC');
app.controller('DataRangeSettings', ['$scope', '$interval', '$rootScope', '$location', '$cookies', function ($scope, $interval, $rootScope, $location, $cookies) {
  // Общие настройки элемента управления интервалом дат
    var locParams = $location.search();
    if (angular.isDefined(locParams.fromDate) && ($location.path() === "/notices/list")) {
//        $rootScope.monitor.toDate
        $scope.navPlayerDates = {
            startDate :  locParams.fromDate,
            endDate : locParams.toDate
        };
    } else {
        $scope.navPlayerDates = {
            startDate :   $rootScope.reportStart || moment().subtract(6, 'days').startOf('day'),
            endDate :   $rootScope.reportEnd || moment().endOf('day')
        };
    }
    
    $scope.queryDateOptsRu = {
        locale : {
            applyClass : 'btn-green',
            applyLabel : "Применить",
            fromLabel : "с",
            toLabel : "по",
            cancelLabel : 'Отмена',
            customRangeLabel : 'Период',
            daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
            firstDay : 1,
            monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                    'Октябрь', 'Ноябрь', 'Декабрь' ]
        },
        ranges : {
            'Текущий день' : [ moment().startOf('day'),
                    moment().endOf('day') ],
            'Посл 7 дней' : [
                moment().subtract(6, 'days').startOf('day'),
                moment().endOf('day') ],
            'Посл 30 дней' : [
                moment().subtract(29, 'days').startOf('day'),
                moment().endOf('day') ]
        },
        startDate : moment($scope.navPlayerDates.startDate).startOf('day'),
        endDate : moment($scope.navPlayerDates.endDate).endOf('day'),

        format : 'DD.MM.YYYY',
        separator: " по "
    };
    $scope.queryDateOptsRuNoRanges = {
        locale : {
            applyClass : 'btn-green',
            applyLabel : "Применить",
            fromLabel : "с",
            toLabel : "по",
            cancelLabel : 'Отмена',
            customRangeLabel : 'Период',
            daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
            firstDay : 1,
            monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                    'Октябрь', 'Ноябрь', 'Декабрь' ]
        },
        startDate : moment().startOf('day'),
        endDate : moment().endOf('day'),

        format : 'DD.MM.YYYY',
        separator: " по "
    };

    $scope.queryDateOptsEn = {
        locale : {
            applyClass : 'btn-green'
        },
        ranges : {
            'Today' : [ moment().startOf('day'),
                    moment().endOf('day') ],
            'Last 7 days' : [
                moment().subtract(6, 'days')
                            .startOf('day'),
                moment().endOf('day') ],
            'Last 30 days' : [
                moment().subtract(29, 'days')
                            .startOf('day'),
                moment().endOf('day') ]
        },
        startDate : moment().startOf('day'),
        endDate : moment().endOf('day'),

        format : 'DD.MM.YYYY'
    };
    
    $scope.$watch('navPlayerDates', function (newDates, oldDates) {
//console.log(newDates);
//console.log(oldDates); 
        if (newDates === oldDates) {
            return;
        }
        if ($location.path() === "/objects/indicators") {
            return;
        }
        $rootScope.reportStart = moment(newDates.startDate).format('YYYY-MM-DD');
        $rootScope.reportEnd = moment(newDates.endDate).format('YYYY-MM-DD');
//!Attention. Эта штуковина может чудить - она меняет url строку.
        if (($location.path() != "/notices/monitor") && ($location.path() != "/objects/indicators")) {
            $location.search("fromDate", $rootScope.reportStart);
            $location.search("toDate", $rootScope.reportEnd);
        }
        
        $rootScope.$broadcast('navPlayerDatesChanged');
//console.log($location);        
//console.log("data-range-settings");         
//console.log($rootScope.reportStart); 
//console.log($rootScope.reportEnd);         
    }, false);
    
                        
    // Настройки для страницы с показаниями
//    if (angular.isDefined($location.search().fromDate)&&($location.search().fromDate!=null)){
//        $scope.indicatorDates = {
//            startDate : $location.search().fromDate,
//            endDate :  $location.search().toDate
//        };
//    }else{
//        $scope.indicatorDates = {
//            startDate : moment().subtract(6, 'days').startOf('day'),
//            endDate :  moment().endOf('day')
//        };
//    };
//console.log($scope.indicatorDates.startDate);
//console.log($scope.indicatorDates.endDate); 

    $scope.queryDateOptsIndicatorRu = {
        locale : {
            applyClass : 'btn-green',
            applyLabel : "Применить",
            fromLabel : "с",
            toLabel : "по",
            cancelLabel : 'Отмена',
            customRangeLabel : 'Период',
            daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
            firstDay : 1,
            monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                    'Октябрь', 'Ноябрь', 'Декабрь' ]
        },
        ranges : {
            'Текущий день' : [ moment().startOf('day'),
                    moment().endOf('day') ],
            'Посл 7 дней' : [
                moment().subtract(6, 'days').startOf('day'),
                moment().endOf('day') ],
            'Посл 30 дней' : [
                moment().subtract(29, 'days').startOf('day'),
                moment().endOf('day') ]
        },
//        startDate : moment().subtract(7, 'days').startOf('day'),
//        endDate : moment().endOf('day'),
//        startDate: '2013-01-01',
//        endDate: '2013-12-31',

        format : 'DD.MM.YYYY',
        separator: " по "
    };

    $scope.$watch('indicatorDates', function (newDates) {
//console.log("Date-range-settings indicatorDates");        
        if ($location.path() !== "/objects/indicators") {
            return;
        }
//console.log("Date-range-settings indicatorDates1");                
//        $rootScope.reportStart = moment(newDates.startDate).format('YYYY-MM-DD');
//        $rootScope.reportEnd = moment(newDates.endDate).format('YYYY-MM-DD');
//console.log($rootScope.reportStart);
//console.log($rootScope.reportEnd);
    }, false);
    
        //Страница с показаниями - удаление показаний - not used
    $scope.deleteIndicatorDates = {
        startDate : moment().subtract(6, 'days').startOf('day'),
        endDate :  moment().endOf('day')
    };
    
    $scope.$watch('deleteIndicatorDates', function (newDates) {
//console.log("Date-range-settings indicatorDates");        
        if ($location.path() !== "/objects/indicators") {
            return;
        }
//console.log("Date-range-settings indicatorDates1");                
        $rootScope.startDateToDel = moment(newDates.startDate).format('YYYY-MM-DD');
        $rootScope.endDateToDel = moment(newDates.endDate).format('YYYY-MM-DD');
    }, false);
    
    
    //monitor settings
    if (angular.isDefined($cookies.get('fromDate'))) {
//        $rootScope.monitor.toDate
//console.log("Data-range-settings. Set monitor dates."); 
//console.log($cookies.fromDate);        
//console.log($rootScope.monitorStart); 
        $scope.monitorDates = {
            startDate :  $cookies.get('fromDate'),
            endDate : $cookies.get('toDate')
        };
    } else {
//        startDate :  moment().subtract(6, 'days').startOf('day'),
//console.log("Data-range-settings. Set monitor default dates.");        
        $scope.monitorDates = {
            startDate :  moment().subtract(6, 'days').startOf('day'),
            endDate :  moment().endOf('day')
        };
    }
//console.log($scope.monitorDates);    
    $scope.queryDateOptsMonitorRu = {
        locale : {
            applyClass : 'btn-green',
            applyLabel : "Применить",
            fromLabel : "с",
            toLabel : "по",
            cancelLabel : 'Отмена',
            customRangeLabel : 'Период',
            daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
            firstDay : 1,
            monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                    'Октябрь', 'Ноябрь', 'Декабрь' ]
        },
        ranges : {
            'Текущий день' : [ moment().startOf('day'),
                    moment().endOf('day') ],
            'Посл 7 дней' : [
                moment().subtract(6, 'days').startOf('day'),
                moment().endOf('day') ],
            'Посл 30 дней' : [
                moment().subtract(29, 'days').startOf('day'),
                moment().endOf('day') ]
        },
        startDate : moment($cookies.get('fromDate')) || moment().subtract(6, 'days').startOf('day'),
        endDate : moment($cookies.get('toDate')) || moment().endOf('day'),
//        startDate : moment().subtract(6, 'days').startOf('day'),
//        endDate : moment().endOf('day'),
        maxDate: moment().endOf('day'),

        format : 'DD.MM.YYYY',
        separator: " по "
    };
    
    $scope.$watch('monitor123Dates', function (newDates) {
//console.log("Date-range-settings monitorDates");        
        if ($location.path() !== "/notices/monitor") {
            return;
        }
        $rootScope.monitorStart = moment(newDates.startDate).format('YYYY-MM-DD');
        $rootScope.monitorEnd = moment(newDates.endDate).format('YYYY-MM-DD');
//console.log($rootScope.monitorStart);  
//console.log($rootScope.monitorEnd);         
//console.log("Date-range-settings monitorDates1");         
    }, false);
}]);