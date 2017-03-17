/*jslint node: true, es5: true*/
/*global angular, moment*/
'use strict';
var app = angular.module('portalNMC');
app.service('indicatorSvc', ['mainSvc', function (mainSvc) {
    var contObjectId = null,
        contZpointId = null,
        contObjectName = null,
        contZpointName = null,
        timeDetailType = "24h",
        detailType = "",
        timeType = "24h",
        deviceModel = null,
        deviceSN = null,
        fromDate = moment().subtract(6, 'days').startOf('day').format('YYYY-MM-DD'),
        toDate = moment().endOf('day').format('YYYY-MM-DD');
        
// ************************************************************************
//                 Definde HW columns
// ************************************************************************    
    var intotalColumns = [
            {
                header : "ГКал на входе",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                fieldName: "h_in",
                "imgpath" : "",
                "imgclass": "",
                "title": ""
            },
            {
                header : "ГКал на выходе",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                fieldName: "h_out",
                "imgpath" : "",
                "imgclass": "",
                "title": ""
            },
            {
                header : "Потребление тепловой энергии, ГКал",
//                header : "",
                class : "col-xs-1 col-md-1 nmc-distinguish",
                fieldName: "h_delta",
                "imgpath" : "",
                "imgclass": "",
                "title": ""
            },
            {
                header : "Масса подачи, т",
//                header : "",
                class : "col-xs-1 col-md-1",
                fieldName: "m_in",
                "imgpath" : "",
                "imgclass": "",
                "title": ""
            },
            {
                header : "Масса обратки, т",
//                header : "",
                class : "col-xs-1 col-md-1",
                fieldName: "m_out",
                "imgpath" : "",
                "imgclass": "",
                "title": ""
            },
            {
                header : "Разница масс воды, т",
//                header : "",
                class : "col-xs-1 col-md-1 nmc-distinguish",
//                class : "col-md-1 nmc-th-invisible",
                fieldName: "m_delta",
                "imgpath" : "",
                "imgclass": "",
                "title": ""
            },
            {
                header : "Объем подачи, м3",
//                header : "",
                class : "col-xs-1 col-md-1",
                fieldName: "v_in",
                "imgpath" : "",
                "imgclass": "",
                "title": ""
            },
            {
                header : "Объем обратки, м3",
//                header : "",
                class : "col-xs-1 col-md-1",
                fieldName: "v_out",
                "imgpath" : "",
                "imgclass": "",
                "title": ""
            },
            {
                header : "Разница объемов, м3",
//                header : "",
                
                class : "col-xs-1 col-md-1 nmc-distinguish",
//                class : "col-md-1 nmc-th-invisible",
                fieldName: "v_delta",
                "imgpath" : "",
                "imgclass": "",
                "title": ""
            },
            {
                header : "Давление на подаче, Мпа",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                fieldName: "p_in",
                "imgpath" : "",
                "imgclass": "",
                "title": ""
            },
            {
                header : "Давление на обратке, Мпа",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                fieldName: "p_out",
                "imgpath" : "",
                "imgclass": "",
                "title": ""
            },
            {
                header : "Разность давлений, Мпа",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                fieldName: "p_delta",
                "imgpath" : "",
                "imgclass": "",
                "title": ""
            },
            {
                header : "Темп. подачи",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                fieldName: "t_in",
                "imgpath" : "",
                "imgclass": "",
                "title": "",
                dataType: "temperature"
            },
            {
                header : "Темп. обратки",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                fieldName: "t_out",
                "imgpath" : "",
                "imgclass": "",
                "title": "",
                dataType: "temperature"
            },
            {
                header : "Темп. ХВС",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                fieldName: "t_cold",
                "imgpath" : "",
                "imgclass": "",
                "title": "",
                dataType: "temperature"
            },
            {
                header : "Темп. окр. среды",
//                header : "",
                class : "col-xs-1 col-md-1",
//                class : "col-md-1 nmc-th-invisible",
                fieldName: "t_outdoor",
                "imgpath" : "",
                "imgclass": "",
                "title": "",
                dataType: "temperature"
            }];

    var indicatorColumns = [
        {
            header : "Дата",
            headerClass : "col-xs-2 col-md-2",
            dataClass : "col-xs-2 col-md-2",
            fieldName: "dataDate",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: false,
            isvisible: 'isvisible'
        },
        {
            header : "Время наработки, час",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "workTime",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: false,
            isvisible: 'isvisible'
        },
        {
            header : "ГКал на входе",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "h_in",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "ГКал на выходе",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "h_out",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Потребление тепловой энергии, ГКал",
            headerClass : "col-xs-1 col-md-1 nmc-distinguish",
            dataClass : "col-xs-1 col-md-1 nmc-distinguish",
            fieldName: "h_delta",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Масса подачи, т",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "m_in",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Масса обратки, т",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "m_out",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Разность масс, т",
            headerClass : "col-xs-1 col-md-1 nmc-distinguish",
            dataClass : "col-xs-1 col-md-1 nmc-distinguish",
            fieldName: "m_delta",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Объем подачи, м3",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "v_in",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Объем обратки, м3",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "v_out",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Разность объемов, м3",
            headerClass : "col-xs-1 col-md-1 nmc-distinguish",
            dataClass : "col-xs-1 col-md-1 nmc-distinguish",
            fieldName: "v_delta",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Давление на подаче, Мпа",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "p_in",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Давление на обратке, Мпа",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "p_out",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Разность давлений, Мпа",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "p_delta",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Темп. подачи",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "t_in",
            dataType: "temperature",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Темп. обратки",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "t_out",
            dataType: "temperature",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Темп. ХВС",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "t_cold",
            dataType: "temperature",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        },
        {
            header : "Темп. окр. среды",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "t_outdoor",
            dataType: "temperature",
            "1h": "1h",
            "24h" : "24h",
            "1h_abs" : "1h_abs",
            "24h_abs" : "24h_abs",
            istunable: "istunable"
        }
    ];
// ************************************************************************
//                end Definde HW columns
// ************************************************************************    
    
// ************************************************************************
//                 Work with El columns
// ************************************************************************ 
    
    // ******************** create columns ****************************
    var electricityColumns = [];
    
    function initElectricityColumns() {
        var elecType = [{"name": "p_A", "caption": "A"}, /* active*/
                    {"name": "q_R", "caption": "R"}]; /* reactive*/
        var elecKind = [{"name": "p", "caption": "+"}, /*positive*/
                        {"name": "n", "caption": "-"}];/*negative*/
        var tariffPlans = [1, 2, 3, 4, 5];//use 5 tariff plans
        var columns = [
            {
                header : "Дата",
                headerClass : "col-xs-2 col-md-2 nmc-text-align-center",
                dataClass : "col-xs-2 col-md-2",
                fieldName: "dataDateString",
                type: "string",
                date: true,
                istunable: false,
                isvisible: 'isvisible'
            }
        ];
        //columns for active and reactive parts
        var type, tariff, kind,
            column;
        for (type = 0; type < elecType.length; type += 1) {
            //columns for tariff plans
            for (tariff = 0; tariff < tariffPlans.length; tariff += 1) {
                for (kind = 0; kind < elecKind.length; kind += 1) {
                    column = {};
                    column.header = elecType[type].caption + elecKind[kind].caption + " (T" + tariffPlans[tariff] + ")";
                    column.headerClass = "nmc-view-digital-data nmc-text-align-center";
                    column.dataClass = "nmc-view-digital-data";
                    column.fieldName = elecType[type].name + elecKind[kind].name + tariffPlans[tariff];
                    column.elKind = elecKind[kind].name;
                    column.elType = elecType[type].name;
                    column.istunable = "istunable";
                    columns.push(column);
                }
            }
            //columns for sum
            for (kind = 0; kind < elecKind.length; kind += 1) {
                column = {};
                column.header = "\u03A3" + elecType[type].caption + elecKind[kind].caption;
                column.headerClass = "nmc-view-digital-data nmc-text-align-center";
                column.dataClass = "nmc-el-totals-indicator-highlight nmc-view-digital-data";
                column.fieldName = elecType[type].name + elecKind[kind].name;
                column.isSummary = true;
                column.elKind = elecKind[kind].name;
                column.elType = elecType[type].name;
                column.istunable = "istunable";
                columns.push(column);
            }
        }
        electricityColumns = columns;
        return columns;
    }
//console.log(columns);    
    // ******************************* end Create columns **************************
// ************************************************************************
//               end  work with El columns
// ************************************************************************
    
    // **********************************************************************
    //              Impulse columns
    // ********************************************************************
    var impulseIndicatorsColumns = [
            {
                header : "Дата",
                headerClass : "col-xs-2 nmc-text-align-center",
                dataClass : "col-xs-2",
                fieldName: "dataDate",
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs",
                "24h_abs" : "24h_abs",
                istunable: false,
                isvisible: 'isvisible'
            },
            {
                header : "Показания",
                headerClass : "col-xs-1 nmc-text-align-center",
                dataClass : "col-xs-1",
                fieldName: "dataValue", /*dataValue*/
                "1h": "1h",
                "24h" : "24h",
                "1h_abs" : "1h_abs",
                "24h_abs" : "24h_abs",
                istunable: "istunable",
                isvisible: 'isvisible'
            }
        ];

    var getContObjectId = function () {
        return contObjectId;
    };
    var getZpointId = function () {
        return contZpointId;
    };

    var setContObjectId = function (contObjId) {
        contObjectId = contObjId;
    };
    var setZpointId = function (zpId) {
        contZpointId = zpId;
    };
    
    var getContObjectName = function () {
        return contObjectName;
    };
    var getZpointName = function () {
        return contZpointName;
    };
    var getDeviceModel = function () {
        return deviceModel;
    };
    var getDeviceSN = function () {
        return deviceSN;
    };
    
    var setDeviceModel = function (dmodel) {
        deviceModel = dmodel;
    };
    var setDeviceSN = function (dSN) {
        deviceSN = dSN;
    };

    var setContObjectName = function (contObjName) {
        contObjectName = contObjName;
    };
    
    var setZpointName = function (zpName) {
        contZpointName = zpName;
    };
    
    var getTimeDetailType = function () {
        return timeDetailType;
    };
    
    var setTimeDetailType = function (tDType) {
        timeDetailType = tDType;
    };
    
    var getTimeType = function () {
        return timeType;
    };
    
    var setTimeType = function (tType) {
        timeType = tType;
    };
    
    var getDetailType = function () {
        return detailType;
    };
    
    var setDetailType = function (dType) {
        detailType = dType;
    };
    
    var getFromDate = function () {
        return fromDate;
    };
    
    var setFromDate = function (newDate) {
        fromDate = newDate;
    };
    
    var getToDate = function () {
        return toDate;
    };
    
    var setToDate = function (newDate) {
        toDate = newDate;
    };
    
    var getIndicatorColumns = function () {
        return indicatorColumns;
    };
    
    var getIntotalColumns = function () {
        return intotalColumns;
    };
    
    var getElectricityColumns = function () {
        return electricityColumns;
    };
    
    var getWaterColumns = function () {
        return indicatorColumns;
    };
    
    function getImpulseColumns() {
        return impulseIndicatorsColumns;
    }
    
    function setColorHighlightsForManualAndCrc(dataArray) {
        if (mainSvc.checkUndefinedNull(dataArray) || !angular.isArray(dataArray)) {
            console.log("Can't set color highlights");
            return false;
        }
        dataArray.forEach(function (el) {
            switch (el.dataMstatus) {
            case 3:
                el.dataClass = "nmc-tr-indicator-manual";
                break;
            }
            
            if (el.crc32Valid === false) {
                el.dataClass = "nmc-tr-indicator-crc-error";
            }
        });
    }
    
    function initSvc() {
        initElectricityColumns();
    }
    initSvc();
    
    return {
        getContObjectId: getContObjectId,
        getContObjectName: getContObjectName,
        getDetailType: getDetailType,
        getDeviceModel: getDeviceModel,
        getDeviceSN: getDeviceSN,
        getElectricityColumns: getElectricityColumns,
        getFromDate: getFromDate,
        getIntotalColumns: getIntotalColumns,
        getImpulseColumns: getImpulseColumns,
        getWaterColumns: getWaterColumns,
        getTimeDetailType: getTimeDetailType,
        getTimeType: getTimeType,
        getToDate: getToDate,
        getZpointId: getZpointId,
        getZpointName: getZpointName,
        setColorHighlightsForManualAndCrc: setColorHighlightsForManualAndCrc,
        setContObjectId: setContObjectId,
        setContObjectName: setContObjectName,
        setDetailType: setDetailType,
        setDeviceModel: setDeviceModel,
        setDeviceSN: setDeviceSN,
        setFromDate: setFromDate,
        setTimeDetailType: setTimeDetailType,
        setTimeType: setTimeType,
        setToDate: setToDate,
        setZpointId: setZpointId,
        setZpointName: setZpointName
    };
}]);