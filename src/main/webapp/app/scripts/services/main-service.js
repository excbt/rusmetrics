angular.module('portalNMC')
.service('mainSvc', function($cookies){
    //object map
    var objectMapSettings = {};
    objectMapSettings.zoom = null;
    objectMapSettings.lat = null;
    objectMapSettings.lng = null;
    //monitor map
    var monitorMapSettings = {};
    monitorMapSettings.zoom = null;
    monitorMapSettings.lat = null;
    monitorMapSettings.lng = null;
    
    //main menu settings
    
    //setters and getters
    var getObjectMapSettings = function(){
        return objectMapSettings;
    };
    
    var getMonitorMapSettings = function(){
        return monitorMapSettings;
    };
    
    var setObjectMapSettings = function(mapSettings){
        objectMapSettings = mapSettings;
    };
    var setMonitorMapSettings = function(mapSettings){
        monitorMapSettings = mapSettings;
    };
    
    //methods for the work with the dates
        //get UTC time from the string with date
    var strDateToUTC = function(strWithDate, strFormat){       
        var stDate = (new Date(moment(strWithDate, strFormat).format("YYYY-MM-DD"))); //reformat date string to ISO 8601      
        var result = (!isNaN(stDate.getTime()))?Date.UTC(stDate.getFullYear(), stDate.getMonth(), stDate.getDate()):null;
        return result;
    };
    
    var checkStrForDate = function(strWithDate){
        //check date for format: DD/MM/YYYY
        if (/(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\d\d/.test(strWithDate)){
            return true;
        };  
        return false;
    };
    
    //date range settings
    var dateRangeOptsRu = {
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
        startDate : moment($cookies.fromDate) || moment().subtract(6, 'days').startOf('day'),
        endDate : moment($cookies.toDate) || moment().endOf('day'),

        format : 'DD.MM.YYYY'
        ,separator: " по "
    };
    
    var dateRangeMonitorOptsRu = dateRangeOptsRu;
    dateRangeMonitorOptsRu.startDate = moment().subtract(6, 'days').startOf('day');
    dateRangeMonitorOptsRu.endDate = moment().endOf('day');
    dateRangeMonitorOptsRu.maxDate = moment().endOf('day');
    
    var getDateRangeOptions = function(param){
        var result = null;
        switch (param){
            case "ru": result=dateRangeOptsRu;break;
            case "monitor-ru": result=dateRangeMonitorOptsRu;break;
        };
        return result;
    };
    
    //TODO: 
    //get system user info
    
    return {
        checkStrForDate,
        getMonitorMapSettings,
        getObjectMapSettings,
        getDateRangeOptions,
        setMonitorMapSettings,
        setObjectMapSettings,
        strDateToUTC
    };
});