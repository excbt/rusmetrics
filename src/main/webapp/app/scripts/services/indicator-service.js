angular.module('portalNMC')
.service('indicatorSvc',function(){
    var contObjectId = null;
    var contZpointId = null;
    var contObjectName = null;
    var contZpointName = null;
    var timeDetailType = "24h";
    var fromDate = moment().subtract(6, 'days').startOf('day');
    var toDate = moment().endOf('day');

    var getContObjectId = function(){
        return contObjectId;
    };
    var getZpointId = function(){
        return contZpointId;
    };

    var setContObjectId = function(contObjId){
        contObjectId = contObjId;
    };
    var setZpointId = function(zpId){
        contZpointId = zpId;
    };
    
    var getContObjectName = function(){
        return contObjectName;
    };
    var getZpointName = function(){
        return contZpointName;
    };

    var setContObjectName = function(contObjName){
        contObjectName = contObjName;
    };
    
    var setZpointName = function(zpName){
        contZpointName = zpName;
    };
    
    var getTimeDetailType = function(){
        return timeDetailType;
    };
    
    var setTimeDetailType = function(tDType){
        timeDetailType = tDType;
    };
    
    var getFromDate = function(){
        return fromDate;
    };
    
    var setFromDate = function(newDate){
        fromDate = newDate;
    };
    
    var getToDate = function(){
        return toDate;
    };
    
    var setToDate = function(newDate){
        toDate = newDate;
    };
    
    return {
        getContObjectId,
        getContObjectName,
        getFromDate,
        getTimeDetailType,
        getToDate,
        getZpointId,
        getZpointName,
        setContObjectId,
        setContObjectName,
        setFromDate,
        setTimeDetailType,
        setToDate,
        setZpointId,
        setZpointName        
    };
});