angular.module('portalNMC')
.service('indicatorSvc',function(){
    var contObjectId = null;
    var contZpointId = null;
    var contObjectName = null;
    var contZpointName = null;

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
    
    return {
        getContObjectId,
        getContObjectName,
        getZpointId,
        getZpointName,
        setContObjectId,
        setContObjectName,
        setZpointId,
        setZpointName        
    };
});