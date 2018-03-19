/*global angular, moment, console*/
(function () {
    'use strict';

    angular
        .module('dateModule')
        .service('dateSvc', Service);

    Service.$inject = [];

    /* @ngInject */
    function Service() {
        var DEFAULT_USER_FORMAT = 'DD.MM.YYYY HH:mm:ss';
        this.dateFormating = dateFormating;
        this.getDaterangeOptions = getDaterangeOptions;
        
        var dateOptsRu = {
            applyClass : 'btn-primary nmc-drp-ctrl-btns',
            cancelClass: 'nmc-bg-distinguish nmc-drp-ctrl-btns',
            locale : {                
                applyLabel : "Применить",
                fromLabel : "с",
                toLabel : "по",
                cancelLabel : 'Отменить',
                customRangeLabel : 'Произвольный период',
                daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
                firstDay : 1,
                monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                        'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                        'Октябрь', 'Ноябрь', 'Декабрь' ]
            },
            ranges : {
                'Текущие сутки' : [ moment().startOf('day'),
                        moment().endOf('day') ],
                'За сутки' : [ 
                    moment().subtract(24, 'hours'),
                    moment() ],
                'Прошлые сутки' : [ 
                    moment().subtract(1, 'days').startOf('day'),
                    moment().subtract(1, 'days').endOf('day') ],
                'Текущая неделя' : [ 
                    moment().startOf('week').add(1, 'days'),
                    moment() ],
                '7 дней' : [
                    moment().subtract(7, 'days').startOf('day'),
                    moment().endOf('day') ],
                'Текущий месяц' : [
                    moment().startOf('month'),
                    moment().endOf('day') ]
            },
            startDate : moment().startOf('day'),
            endDate : moment().endOf('day'),

            format : 'DD.MM.YYYY',
            separator: " по "
        };

        ////////////////
        function dateFormatingFromArray(inputData, formatString) {
            var inpData = angular.copy(inputData);
            inpData[1] -= 1;
            if (inpData.length >= 7) {
                inpData.splice(-1, 1);
            }
            var mDate = moment(inpData);            
            return mDate.format(formatString);
        }
        
        function dateFormating(inputData, formatString) {
            
            if (angular.isUndefined(inputData) || inputData === null) {
                return null;
            }
            if (angular.isUndefined(formatString) || formatString === null) {
                formatString = DEFAULT_USER_FORMAT;
            }
            var result = null;            
            
            if (angular.isArray(inputData)) {
                result = dateFormatingFromArray(inputData, formatString);
            }            
            
            return result;
        }
        
        function getDaterangeOptions() {
            return angular.copy(dateOptsRu);
        }
    }
})();