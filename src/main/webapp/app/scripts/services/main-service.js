angular.module('portalNMC')
.service('mainSvc', function(){
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
    
    //TODO: 
    //get system user info
    
    return {
        getMonitorMapSettings,
        getObjectMapSettings,
        setMonitorMapSettings,
        setObjectMapSettings,
        strDateToUTC
    };
});