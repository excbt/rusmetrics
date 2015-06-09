var app = angular.module('portalNMC');
app
    .controller('DataRangeSettings', function($scope, $interval, $rootScope){
  // Общие настройки элемента управления интервалом дат
    $scope.navPlayerDates = {
        startDate :  moment().startOf('day'),
        endDate :  moment().endOf('day')
    };
    
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
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентабрь',
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
        startDate : moment().startOf('day'),
        endDate : moment().endOf('day'),

        format : 'DD.MM.YYYY'
        ,separator: " по "
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
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентабрь',
                    'Октябрь', 'Ноябрь', 'Декабрь' ]
        },
        startDate : moment().startOf('day'),
        endDate : moment().endOf('day'),

        format : 'DD.MM.YYYY'
        ,separator: " по "
    };

    $scope.queryDateOptsEn = {
        locale : {
            applyClass : 'btn-green',
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
    
    $scope.$watch('navPlayerDates', function (newDates) {
        $rootScope.reportStart = moment(newDates.startDate).format('YYYY-MM-DD');
        $rootScope.reportEnd = moment(newDates.endDate).format('YYYY-MM-DD');                                
    }, false);
    
                        
    // Настройки для страницы с показаниями
    $scope.indicatorDates = {
        startDate : moment().subtract(6, 'days').startOf('day'),
        endDate :  moment().endOf('day')
    };
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
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентабрь',
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

        format : 'DD.MM.YYYY'
        ,separator: " по "
    };

    $scope.$watch('indicatorDates', function (newDates) {
//console.log("New dates");        
//console.log(newDates);        
        $rootScope.reportStart = moment(newDates.startDate).format('YYYY-MM-DD');
        $rootScope.reportEnd = moment(newDates.endDate).format('YYYY-MM-DD');                                
    }, false);
});