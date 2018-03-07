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