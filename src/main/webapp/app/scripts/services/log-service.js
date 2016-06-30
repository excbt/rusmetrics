'use strict';
var app = angular.module('portalNMC');
app.service('logSvc', ['$rootScope', '$http', '$interval', 'mainSvc', function($rootScope, $http, $interval, mainSvc){
    //запрос данных журнала
    var SESSION_STATUSES = [
        {
            keyname: "LOADING",
            color: "YELLOW"
        },{
            keyname: "PARSING",
            color: "YELLOW"
        },{
            keyname: "DB INSERTING",
            color: "YELLOW"
        },{
            keyname: "COMPLETE",
            color: "GREEN"
        },{
            keyname: "COMPLETE WITH ERROR",
            color: "RED"
        },{
            keyname: "FAILURE",
            color: "RED"
        }
    ];
    var REFRESH_PERIOD = 30000;
    var SYSTEM_DATE_FORMAT = "YYYY-MM-DD";
    var sessionsUrl = "../api/rma/logSessions";
    var sessions = null;
    var params = {};
    var sessionsLogDaterange = {
        startDate: moment().startOf('day'),                        
        endDate: moment().endOf('day')
    };
    params.fromDate = sessionsLogDaterange.startDate.format(SYSTEM_DATE_FORMAT);
    params.toDate = sessionsLogDaterange.endDate.format(SYSTEM_DATE_FORMAT);
    
    function getSessionsLogDaterange(){
        return sessionsLogDaterange;
    }
    
    function setSessionsLogDaterange(interval){
        sessionsLogDaterange = interval;
    }
    
    function getSessions(){
        return sessions;
    }
    
    function successCallback(e){
//console.log(e.data);
        if (mainSvc.checkUndefinedNull(e.data) || !angular.isArray(e.data) || e.data.length == 0){
//console.log("Session loading. Response is empty.");            
            sessions = [];
            $rootScope.$broadcast('logSvc:sessionsLoaded');
            return;
        }
        sessions = serverDataParser(angular.copy(e.data));
        if (mainSvc.checkUndefinedNull(params.contObjectIds) || params.contObjectIds.length <= 0){
            sessions = defineChildSessions();
        }
        $rootScope.$broadcast('logSvc:sessionsLoaded');
    }
    
    function errorCallback(e){
        console.log(e);
    }
    
    function loadSessions(){
        $http({
            method: "GET",
            url: sessionsUrl,
            params: params
        }).then(successCallback, errorCallback);
    }
    
    function serverDataParser(data){ 
//console.time('Data parsing');
        if (mainSvc.checkUndefinedNull(data) || !angular.isArray(data) || data.length == 0)
            return null;
        var result = data.map(function(dataRow){
            var tmpParsedRow = {};
            tmpParsedRow.id = dataRow.id;
            if (!mainSvc.checkUndefinedNull(dataRow.dataSourceInfo))
                tmpParsedRow.dataSource = dataRow.dataSourceInfo.caption || dataRow.dataSourceInfo.dataSourceName;
            if (!mainSvc.checkUndefinedNull(dataRow.deviceObjectInfo)){
                tmpParsedRow.deviceModel = dataRow.deviceObjectInfo.deviceModelName;
                tmpParsedRow.deviceNumber = dataRow.deviceObjectInfo.number;
            }
            tmpParsedRow.startDate = dataRow.sessionDateStr;
            tmpParsedRow.endDate = dataRow.sessionEndDateStr;
            if (!mainSvc.checkUndefinedNull(dataRow.authorInfo))
                tmpParsedRow.author = dataRow.authorInfo.authorName;
            tmpParsedRow.currentStatus = dataRow.sessionStatus;
            tmpParsedRow.statusMessage = dataRow.statusMessage;
            tmpParsedRow.sessionMessage = dataRow.sessionMessage;
            tmpParsedRow.sessionUuid = dataRow.sessionUuid;
            tmpParsedRow.masterSessionUuid = dataRow.masterSessionUuid;
            
            SESSION_STATUSES.some(function(status){              
                if(status.keyname == dataRow.sessionStatus){                   
                    tmpParsedRow.currentStatusColor = status.color;
                    return true;
                }
            })
            return tmpParsedRow;
        });
//console.timeEnd('Data parsing');                
        return result;        
    }
    
    function defineChildSessions(){
//console.time('Find child sessions');
        if (mainSvc.checkUndefinedNull(sessions) || !angular.isArray(sessions) || sessions.length == 0)
            return;
        var tmpData = angular.copy(sessions);
        var newData = [];
        var childSessions = [];
            //find child sessions      
        tmpData.forEach(function(session){
            if (!mainSvc.checkUndefinedNull(session.masterSessionUuid)){
                if (mainSvc.checkUndefinedNull(childSessions[session.masterSessionUuid])){
                    childSessions[session.masterSessionUuid] = [];
                }
                childSessions[session.masterSessionUuid].push(angular.copy(session));
            }else{          
                newData.push(session);
            }
        });
//console.log(childSessions);        
            //add child sessions to their master sessions
        for (var uuid in childSessions){               
            newData.some(function(masterSession){                
                if (uuid == masterSession.sessionUuid){
                    masterSession.childs = childSessions[uuid];
                    return true;
                }
            });
        }        
//console.timeEnd('Find child sessions');
        return newData;
    }
    
//    $rootScope.$broadcast('logSvc:requestSessionsLoading', {params: params});
    $rootScope.$on('logSvc:requestSessionsLoading', function(even, args){
//console.log("logSvc:requestSessionsLoading");        
        params = args.params;
        setSessionsLogDaterange({startDate: params.fromDate, endDate: params.toDate});
        loadSessions();
    });

    function initSvc(){
        $interval(loadSessions, REFRESH_PERIOD);
    }
    
    initSvc();
    
    return {
        getSessions,
        getSessionsLogDaterange,
        serverDataParser,
        setSessionsLogDaterange
    }
}])