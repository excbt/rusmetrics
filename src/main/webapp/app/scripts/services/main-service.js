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
    
    return {
        getMonitorMapSettings,
        getObjectMapSettings,
        setMonitorMapSettings,
        setObjectMapSettings
    };
});