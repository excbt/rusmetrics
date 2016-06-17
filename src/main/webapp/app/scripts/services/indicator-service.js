angular.module('portalNMC')
.service('indicatorSvc', function(){
    var contObjectId = null;
    var contZpointId = null;
    var contObjectName = null;
    var contZpointName = null;
    var timeDetailType = "24h";
    var fromDate = moment().subtract(6, 'days').startOf('day').format('YYYY-MM-DD');
    var toDate = moment().endOf('day').format('YYYY-MM-DD');
    
    var intotalColumns = [
            {
                header : "Потребление тепла, ГКал",
//                header : "",
                class : "col-xs-1 col-md-1",
                name: "h_delta",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            }, 
            {
                header : "Масса подачи, т",
//                header : "",
                class : "col-xs-1 col-md-1",
                name: "m_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            }, 
            {
                header : "Масса обратки, т",
//                header : "",
                class : "col-xs-1 col-md-1",
                name: "m_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            }, 
            {
                header : "Разница масс воды, т",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "m_delta",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Темп. подачи",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "t_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
                ,dataType: "temperature"
            }, 
            {
                header : "Темп. обратки",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "t_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
                ,dataType: "temperature"
            } , 
            {
                header : "Темп. ХВС",
//                header : "",
                class : "col-xs-1 col-md-1",    
//                class : "col-md-1 nmc-th-invisible",
                name: "t_cold",
                "imgpath" : "",
                "imgclass": "",
                "title":""
                ,dataType: "temperature"
            } ,
            {
                header : "Темп. окр. среды",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "t_outdoor",
                "imgpath" : "",
                "imgclass": "",
                "title":""
                ,dataType: "temperature"
            },
            {
                header : "Объем подачи, м3",
//                header : "",
                class : "col-xs-1 col-md-1",
                name: "v_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Объем обратки, м3",
//                header : "",
                class : "col-xs-1 col-md-1",
                name: "v_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Разница объемов, м3",
//                header : "",
                
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "v_delta",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "ГКал на входе",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "h_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "ГКал на выходе",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "h_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Давление на подаче, Мпа",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "p_in",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Давление на обратке, Мпа",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "p_out",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            },
            {
                header : "Разность давлений, Мпа",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                name: "p_delta",
                "imgpath" : "",
                "imgclass": "",
                "title":""
            }];

    var indicatorColumns = [{
                header : "Дата",
                headerClass : "col-xs-2 col-md-2",
                dataClass : "col-xs-2 col-md-2",
                fieldName: "dataDate",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            }, 
            {
                header : "Время наработки, час",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "workTime",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            }, 
            {
                header : "Потребление тепла, ГКал",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "h_delta",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            }, 
            {
                header : "Масса подачи, т",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "m_in",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            }, 
            {
                header : "Масса обратки, т",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "m_out",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            },
            {
                header : "Разность масс, т",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "m_delta",
                "1h": "1h",
                "24h" : "24h"
            },
            {
                header : "Темп. подачи",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "t_in"
                ,dataType: "temperature",
                "1h": "1h",
                "24h" : "24h"               
            }, 
            {
                header : "Темп. обратки",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "t_out"
                ,dataType: "temperature",
                "1h": "1h",
                "24h" : "24h"                
            } , 
            {
                header : "Темп. ХВС",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "t_cold"
                ,dataType: "temperature",
                "1h": "1h",
                "24h" : "24h"               
            } ,
            {
                header : "Темп. окр. среды",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "t_outdoor"
                ,dataType: "temperature",
                "1h": "1h",
                "24h" : "24h"                
            },

            {
                header : "Объем подачи, м3",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "v_in",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            },
            {
                header : "Объем обратки, м3",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "v_out",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            },
            {
                header : "Разность объемов, м3",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "v_delta",
                "1h": "1h",
                "24h" : "24h"
            },
            {
                header : "ГКал на входе",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "h_in",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            },
            {
                header : "ГКал на выходе",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "h_out",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs"
            },
            {
                header : "Давление на подаче, Мпа",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "p_in",
                "1h": "1h",
                "24h" : "24h"
            },
            {
                header : "Давление на обратке, Мпа",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "p_out",
                "1h": "1h",
                "24h" : "24h"
            },
            {
                header : "Разность давлений, Мпа",
                headerClass : "col-xs-1 col-md-1",
                dataClass : "col-xs-1 col-md-1",
                fieldName: "p_delta",
                "1h": "1h",
                "24h" : "24h"
            }
    ];    

          
//    $scope.integratorColumns = [
//        {
//            header : "Дата",
//            headerClass : "col-xs-2 col-md-2",
//            dataClass : "col-xs-2 col-md-2",
//            fieldName: "dataDate"
//        }, 
//        {
//            header : "Время наработки, час",
//            headerClass : "col-xs-1 col-md-1",
//            dataClass : "col-xs-1 col-md-1",
//            fieldName: "workTime"
//        },
//        {
//            header : "Потребление тепла, ГКал",
//            headerClass : "col-xs-1 col-md-1",
//            dataClass : "col-xs-1 col-md-1",
//            fieldName: "h_delta"
//        }, 
//        {
//            header : "Масса подачи, т",
//            headerClass : "col-xs-1 col-md-1",
//            dataClass : "col-xs-1 col-md-1",
//            fieldName: "m_in"
//        }, 
//        {
//            header : "Масса обратки, т",
//            headerClass : "col-xs-1 col-md-1",
//            dataClass : "col-xs-1 col-md-1",
//            fieldName: "m_out"
//        },
//        {
//            header : "Объем подачи, м3",
//            headerClass : "col-xs-1 col-md-1",
//            dataClass : "col-xs-1 col-md-1",
//            fieldName: "v_in"
//        },
//        {
//            header : "Объем обратки, м3",
//            headerClass : "col-xs-1 col-md-1",
//            dataClass : "col-xs-1 col-md-1",
//            fieldName: "v_out"
//        },
//        {
//            header : "ГКал на входе",
//            headerClass : "col-xs-1 col-md-1",
//            dataClass : "col-xs-1 col-md-1",
//            fieldName: "h_in"
//        },
//        {
//            header : "ГКал на выходе",
//            headerClass : "col-xs-1 col-md-1",
//            dataClass : "col-xs-1 col-md-1",
//            fieldName: "h_out"
//        }
//    ];

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
    
    var getIndicatorColumns = function(){
        return indicatorColumns;
    };
    
    var getIntotalColumns = function(){
        return intotalColumns;
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