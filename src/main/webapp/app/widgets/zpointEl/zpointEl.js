/*jslint node: true, eqeq: true, nomen: true*/
/*global angular, moment*/
'use strict';

angular.module('zpointElWidget', ['angularWidget', 'chart.js'])
    .config(['ChartJsProvider', function (ChartJsProvider) {
        // Configure all charts
        ChartJsProvider.setOptions({
            chartColors: ['#337ab7', '#ef473a', '#FDB45C', '#803690'],
            responsive: true
        });
        // Configure all line charts
//        ChartJsProvider.setOptions('line', {
//            showLines: false
//        });
    }])
    .controller('zpointElWidgetCtrl', ['$scope', '$http', '$rootScope', 'widgetConfig', function ($scope, $http, $rootScope, widgetConfig) {
        //test data
//console.log("zpointElWidgetCtrl");
        var timeDetailTypes = {
            year: {
                timeDetailType: "1mon",
                count: 12,
                dateFormatter: function (param) {
//                    var param = param;
                    return "01-" + (param >= 10 ? param : "0" + param) + "-" + moment().format("YYYY");
                }
            },
            month: {
                timeDetailType: "24h",
                count: 30,
                dateFormatter: function (param) {
                    return (param >= 10 ? param : "0" + param) + "-" + moment().format("MM-YYYY");
                }
            },
            day: {
                timeDetailType: "1h",
                count: 24,
                dateFormatter: function (param) {
                    return moment().subtract(param, "hours").format("DD-MM-YYYY HH:ss");
                }
            },
            week: {
                timeDetailType: "24h",
                count: 7,
                dateFormatter: function (param) {
                    return moment().subtract(7 - param, "days").format("DD-MM-YYYY HH:ss");
                }
            }
        };
        function generateTestData(timeDetailType) {
            var result = [],
                i,
                node;
            for (i = 1; i <= timeDetailType.count; i += 1) {
                node = {};
                node.timeDetailType = timeDetailType.timeDetailType;
                node.p_Ap1 = Math.random();
                node.p_Ap2 = Math.random();
                node.p_Ap = node.p_Ap1 + node.p_Ap2;
                node.dataDateString = timeDetailType.dateFormatter(i);
                result.push(node);
            }
            //console.log(result);
            return result;
        }
    
        /*var test_year_data_json = [
            {
                "id": 183377246,
                "dataDate": 1477947600000,
                "timeDetailType": "1mon",
                "p_Ap1": 52.8150696,
                "p_An1": null,
                "q_Rp1": null,
                "q_Rn1": null,
                "p_Ap2": 18.0862579,
                "p_An2": null,
                "q_Rp2": null,
                "q_Rn2": null,
                "p_Ap3": 0.0,
                "p_An3": null,
                "q_Rp3": null,
                "q_Rn3": null,
                "p_Ap4": 0.0,
                "p_An4": null,
                "q_Rp4": null,
                "q_Rn4": null,
                "p_Ap5": 0.0,
                "p_An5": null,
                "q_Rp5": null,
                "q_Rn5": null,
                "p_Ap": 70.9013275,
                "p_An": null,
                "q_Rp": null,
                "q_Rn": null,
                "crc32Valid": null,
                "dataMstatus": null,
                "dataChanged": null,
                "dataDateString": "01-01-2016"
            },
            {
                "id": 186776317,
                "dataDate": 1480539600000,
                "timeDetailType": "1mon",
                "p_Ap1": 309.4743533,
                "p_An1": null,
                "q_Rp1": null,
                "q_Rn1": null,
                "p_Ap2": 122.5503759,
                "p_An2": null,
                "q_Rp2": null,
                "q_Rn2": null,
                "p_Ap3": 0.0,
                "p_An3": null,
                "q_Rp3": null,
                "q_Rn3": null,
                "p_Ap4": 0.0,
                "p_An4": null,
                "q_Rp4": null,
                "q_Rn4": null,
                "p_Ap5": 0.0,
                "p_An5": null,
                "q_Rp5": null,
                "q_Rn5": null,
                "p_Ap": 432.0247292,
                "p_An": null,
                "q_Rp": null,
                "q_Rn": null,
                "crc32Valid": null,
                "dataMstatus": null,
                "dataChanged": null,
                "dataDateString": "01-02-2016"
            },
            {
                "id": 183377246,
                "dataDate": 1477947600000,
                "timeDetailType": "1mon",
                "p_Ap1": 52.8150696,
                "p_An1": null,
                "q_Rp1": null,
                "q_Rn1": null,
                "p_Ap2": 18.0862579,
                "p_An2": null,
                "q_Rp2": null,
                "q_Rn2": null,
                "p_Ap3": 0.0,
                "p_An3": null,
                "q_Rp3": null,
                "q_Rn3": null,
                "p_Ap4": 0.0,
                "p_An4": null,
                "q_Rp4": null,
                "q_Rn4": null,
                "p_Ap5": 0.0,
                "p_An5": null,
                "q_Rp5": null,
                "q_Rn5": null,
                "p_Ap": 70.9013275,
                "p_An": null,
                "q_Rp": null,
                "q_Rn": null,
                "crc32Valid": null,
                "dataMstatus": null,
                "dataChanged": null,
                "dataDateString": "01-03-2016"
            },
            {
                "id": 186776317,
                "dataDate": 1480539600000,
                "timeDetailType": "1mon",
                "p_Ap1": 309.4743533,
                "p_An1": null,
                "q_Rp1": null,
                "q_Rn1": null,
                "p_Ap2": 122.5503759,
                "p_An2": null,
                "q_Rp2": null,
                "q_Rn2": null,
                "p_Ap3": 0.0,
                "p_An3": null,
                "q_Rp3": null,
                "q_Rn3": null,
                "p_Ap4": 0.0,
                "p_An4": null,
                "q_Rp4": null,
                "q_Rn4": null,
                "p_Ap5": 0.0,
                "p_An5": null,
                "q_Rp5": null,
                "q_Rn5": null,
                "p_Ap": 432.0247292,
                "p_An": null,
                "q_Rp": null,
                "q_Rn": null,
                "crc32Valid": null,
                "dataMstatus": null,
                "dataChanged": null,
                "dataDateString": "01-04-2016"
            },
            {
                "id": 183377246,
                "dataDate": 1477947600000,
                "timeDetailType": "1mon",
                "p_Ap1": 52.8150696,
                "p_An1": null,
                "q_Rp1": null,
                "q_Rn1": null,
                "p_Ap2": 18.0862579,
                "p_An2": null,
                "q_Rp2": null,
                "q_Rn2": null,
                "p_Ap3": 0.0,
                "p_An3": null,
                "q_Rp3": null,
                "q_Rn3": null,
                "p_Ap4": 0.0,
                "p_An4": null,
                "q_Rp4": null,
                "q_Rn4": null,
                "p_Ap5": 0.0,
                "p_An5": null,
                "q_Rp5": null,
                "q_Rn5": null,
                "p_Ap": 70.9013275,
                "p_An": null,
                "q_Rp": null,
                "q_Rn": null,
                "crc32Valid": null,
                "dataMstatus": null,
                "dataChanged": null,
                "dataDateString": "01-05-2016"
            },
            {
                "id": 186776317,
                "dataDate": 1480539600000,
                "timeDetailType": "1mon",
                "p_Ap1": 309.4743533,
                "p_An1": null,
                "q_Rp1": null,
                "q_Rn1": null,
                "p_Ap2": 122.5503759,
                "p_An2": null,
                "q_Rp2": null,
                "q_Rn2": null,
                "p_Ap3": 0.0,
                "p_An3": null,
                "q_Rp3": null,
                "q_Rn3": null,
                "p_Ap4": 0.0,
                "p_An4": null,
                "q_Rp4": null,
                "q_Rn4": null,
                "p_Ap5": 0.0,
                "p_An5": null,
                "q_Rp5": null,
                "q_Rn5": null,
                "p_Ap": 432.0247292,
                "p_An": null,
                "q_Rp": null,
                "q_Rn": null,
                "crc32Valid": null,
                "dataMstatus": null,
                "dataChanged": null,
                "dataDateString": "01-06-2016"
            },
            {
                "id": 183377246,
                "dataDate": 1477947600000,
                "timeDetailType": "1mon",
                "p_Ap1": 52.8150696,
                "p_An1": null,
                "q_Rp1": null,
                "q_Rn1": null,
                "p_Ap2": 18.0862579,
                "p_An2": null,
                "q_Rp2": null,
                "q_Rn2": null,
                "p_Ap3": 0.0,
                "p_An3": null,
                "q_Rp3": null,
                "q_Rn3": null,
                "p_Ap4": 0.0,
                "p_An4": null,
                "q_Rp4": null,
                "q_Rn4": null,
                "p_Ap5": 0.0,
                "p_An5": null,
                "q_Rp5": null,
                "q_Rn5": null,
                "p_Ap": 70.9013275,
                "p_An": null,
                "q_Rp": null,
                "q_Rn": null,
                "crc32Valid": null,
                "dataMstatus": null,
                "dataChanged": null,
                "dataDateString": "01-07-2016"
            },
            {
                "id": 186776317,
                "dataDate": 1480539600000,
                "timeDetailType": "1mon",
                "p_Ap1": 309.4743533,
                "p_An1": null,
                "q_Rp1": null,
                "q_Rn1": null,
                "p_Ap2": 122.5503759,
                "p_An2": null,
                "q_Rp2": null,
                "q_Rn2": null,
                "p_Ap3": 0.0,
                "p_An3": null,
                "q_Rp3": null,
                "q_Rn3": null,
                "p_Ap4": 0.0,
                "p_An4": null,
                "q_Rp4": null,
                "q_Rn4": null,
                "p_Ap5": 0.0,
                "p_An5": null,
                "q_Rp5": null,
                "q_Rn5": null,
                "p_Ap": 432.0247292,
                "p_An": null,
                "q_Rp": null,
                "q_Rn": null,
                "crc32Valid": null,
                "dataMstatus": null,
                "dataChanged": null,
                "dataDateString": "01-08-2016"
            },
            {
                "id": 183377246,
                "dataDate": 1477947600000,
                "timeDetailType": "1mon",
                "p_Ap1": 52.8150696,
                "p_An1": null,
                "q_Rp1": null,
                "q_Rn1": null,
                "p_Ap2": 18.0862579,
                "p_An2": null,
                "q_Rp2": null,
                "q_Rn2": null,
                "p_Ap3": 0.0,
                "p_An3": null,
                "q_Rp3": null,
                "q_Rn3": null,
                "p_Ap4": 0.0,
                "p_An4": null,
                "q_Rp4": null,
                "q_Rn4": null,
                "p_Ap5": 0.0,
                "p_An5": null,
                "q_Rp5": null,
                "q_Rn5": null,
                "p_Ap": 70.9013275,
                "p_An": null,
                "q_Rp": null,
                "q_Rn": null,
                "crc32Valid": null,
                "dataMstatus": null,
                "dataChanged": null,
                "dataDateString": "01-09-2016"
            },
            {
                "id": 186776317,
                "dataDate": 1480539600000,
                "timeDetailType": "1mon",
                "p_Ap1": 309.4743533,
                "p_An1": null,
                "q_Rp1": null,
                "q_Rn1": null,
                "p_Ap2": 122.5503759,
                "p_An2": null,
                "q_Rp2": null,
                "q_Rn2": null,
                "p_Ap3": 0.0,
                "p_An3": null,
                "q_Rp3": null,
                "q_Rn3": null,
                "p_Ap4": 0.0,
                "p_An4": null,
                "q_Rp4": null,
                "q_Rn4": null,
                "p_Ap5": 0.0,
                "p_An5": null,
                "q_Rp5": null,
                "q_Rn5": null,
                "p_Ap": 432.0247292,
                "p_An": null,
                "q_Rp": null,
                "q_Rn": null,
                "crc32Valid": null,
                "dataMstatus": null,
                "dataChanged": null,
                "dataDateString": "01-10-2016"
            },
            {
                "id": 183377246,
                "dataDate": 1477947600000,
                "timeDetailType": "1mon",
                "p_Ap1": 52.8150696,
                "p_An1": null,
                "q_Rp1": null,
                "q_Rn1": null,
                "p_Ap2": 18.0862579,
                "p_An2": null,
                "q_Rp2": null,
                "q_Rn2": null,
                "p_Ap3": 0.0,
                "p_An3": null,
                "q_Rp3": null,
                "q_Rn3": null,
                "p_Ap4": 0.0,
                "p_An4": null,
                "q_Rp4": null,
                "q_Rn4": null,
                "p_Ap5": 0.0,
                "p_An5": null,
                "q_Rp5": null,
                "q_Rn5": null,
                "p_Ap": 70.9013275,
                "p_An": null,
                "q_Rp": null,
                "q_Rn": null,
                "crc32Valid": null,
                "dataMstatus": null,
                "dataChanged": null,
                "dataDateString": "01-11-2016"
            },
            {
                "id": 186776317,
                "dataDate": 1480539600000,
                "timeDetailType": "1mon",
                "p_Ap1": 309.4743533,
                "p_An1": null,
                "q_Rp1": null,
                "q_Rn1": null,
                "p_Ap2": 122.5503759,
                "p_An2": null,
                "q_Rp2": null,
                "q_Rn2": null,
                "p_Ap3": 0.0,
                "p_An3": null,
                "q_Rp3": null,
                "q_Rn3": null,
                "p_Ap4": 0.0,
                "p_An4": null,
                "q_Rp4": null,
                "q_Rn4": null,
                "p_Ap5": 0.0,
                "p_An5": null,
                "q_Rp5": null,
                "q_Rn5": null,
                "p_Ap": 432.0247292,
                "p_An": null,
                "q_Rp": null,
                "q_Rn": null,
                "crc32Valid": null,
                "dataMstatus": null,
                "dataChanged": null,
                "dataDateString": "01-12-2016"
            }
        ];
*/
//        var test_day_data_json = [{"id": 186968998, "dataDate": 1484154000000, "timeDetailType": "1h", "p_Ap1": 1.8053726476881777, "p_An1": 0.0, "q_Rp1": 1.1347377926212197, "q_Rn1": 0.0, "p_Ap2": 3.4239907390676736, "p_An2": 0.0, "q_Rp2": 2.432641733672252, "q_Rn2": 0.0, "p_Ap3": 0.0, "p_An3": 0.0, "q_Rp3": 0.0, "q_Rn3": 0.0, "p_Ap4": 0.0, "p_An4": 0.0, "q_Rp4": 0.0, "q_Rn4": 0.0, "p_Ap5": null, "p_An5": null, "q_Rp5": null, "q_Rn5": null, "p_Ap": 5.2293633867558516, "p_An": 0.0, "q_Rp": 3.5673795262934718, "q_Rn": 0.0, "crc32Valid": null, "dataMstatus": null, "dataChanged": null, "dataDateString": "12-01-2017 18:00"},{"id":187012391,"dataDate":1484157600000,"timeDetailType":"1h","p_Ap1":1.868365222924018,"p_An1":0.0,"q_Rp1":1.0778847601905597,"q_Rn1":0.0,"p_Ap2":3.5842235264270137,"p_An2":0.0,"q_Rp2":2.5887793440565376,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.452588749351031,"p_An":0.0,"q_Rp":3.666664104247097,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"12-01-2017 19:00"},{"id":186968998,"dataDate":1484154000000,"timeDetailType":"1h","p_Ap1":1.8053726476881777,"p_An1":0.0,"q_Rp1":1.1347377926212197,"q_Rn1":0.0,"p_Ap2":3.4239907390676736,"p_An2":0.0,"q_Rp2":2.432641733672252,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.2293633867558516,"p_An":0.0,"q_Rp":3.5673795262934718,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"12-01-2017 20:00"},{"id":187012391,"dataDate":1484157600000,"timeDetailType":"1h","p_Ap1":1.868365222924018,"p_An1":0.0,"q_Rp1":1.0778847601905597,"q_Rn1":0.0,"p_Ap2":3.5842235264270137,"p_An2":0.0,"q_Rp2":2.5887793440565376,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.452588749351031,"p_An":0.0,"q_Rp":3.666664104247097,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"12-01-2017 21:00"},{"id":187076603,"dataDate":1484161200000,"timeDetailType":"1h","p_Ap1":1.8321302360679141,"p_An1":0.0,"q_Rp1":1.0300392807417262,"q_Rn1":0.0,"p_Ap2":3.2305038855012898,"p_An2":0.0,"q_Rp2":3.413253408487848,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.062634121569204,"p_An":0.0,"q_Rp":4.443292689229574,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"12-01-2017 22:00"},{"id":187077874,"dataDate":1484164800000,"timeDetailType":"1h","p_Ap1":1.766980798203347,"p_An1":0.0,"q_Rp1":1.1846837897298537,"q_Rn1":0.0,"p_Ap2":3.580536426564522,"p_An2":0.0,"q_Rp2":2.695549238971778,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.347517224767869,"p_An":0.0,"q_Rp":3.880233028701632,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"12-01-2017 23:00"},{"id":187230224,"dataDate":1484168400000,"timeDetailType":"1h","p_Ap1":1.8062244809481678,"p_An1":0.0,"q_Rp1":1.2386626210912823,"q_Rn1":0.0,"p_Ap2":3.139933812975851,"p_An2":0.0,"q_Rp2":1.797680582864163,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":4.946158293924019,"p_An":0.0,"q_Rp":3.0363432039554454,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 00:00"},{"id":187234415,"dataDate":1484172000000,"timeDetailType":"1h","p_Ap1":1.755357159801231,"p_An1":0.0,"q_Rp1":1.0674094078416119,"q_Rn1":0.0,"p_Ap2":3.524942433808926,"p_An2":0.0,"q_Rp2":1.9924161215894027,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.280299593610157,"p_An":0.0,"q_Rp":3.0598255294310146,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 01:00"},{"id":187322260,"dataDate":1484175600000,"timeDetailType":"1h","p_Ap1":1.8713710374763095,"p_An1":0.0,"q_Rp1":1.1933675365344747,"q_Rn1":0.0,"p_Ap2":3.6810167080387717,"p_An2":0.0,"q_Rp2":2.6289159286469967,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.552387745515081,"p_An":0.0,"q_Rp":3.8222834651814717,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 02:00"},{"id":187322916,"dataDate":1484179200000,"timeDetailType":"1h","p_Ap1":1.7569929193001357,"p_An1":0.0,"q_Rp1":1.0525475085628087,"q_Rn1":0.0,"p_Ap2":3.4367935883180305,"p_An2":0.0,"q_Rp2":2.882872650729439,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.193786507618166,"p_An":0.0,"q_Rp":3.935420159292248,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 03:00"},{"id":187322948,"dataDate":1484182800000,"timeDetailType":"1h","p_Ap1":1.7543796454243412,"p_An1":0.0,"q_Rp1":1.0015790194976733,"q_Rn1":0.0,"p_Ap2":3.9535108562609067,"p_An2":0.0,"q_Rp2":2.638001904831848,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.707890501685248,"p_An":0.0,"q_Rp":3.6395809243295214,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 04:00"},{"id":187385261,"dataDate":1484186400000,"timeDetailType":"1h","p_Ap1":1.8693326668767754,"p_An1":0.0,"q_Rp1":1.229529342854296,"q_Rn1":0.0,"p_Ap2":3.5701717655475624,"p_An2":0.0,"q_Rp2":2.9289761214407357,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.439504432424338,"p_An":0.0,"q_Rp":4.158505464295032,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 05:00"},{"id":187387755,"dataDate":1484190000000,"timeDetailType":"1h","p_Ap1":1.8504337457874964,"p_An1":0.0,"q_Rp1":1.0956169114523886,"q_Rn1":0.0,"p_Ap2":3.5773106082574877,"p_An2":0.0,"q_Rp2":2.689790632360789,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.427744354044984,"p_An":0.0,"q_Rp":3.785407543813178,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 06:00"},{"id":187408820,"dataDate":1484193600000,"timeDetailType":"1h","p_Ap1":1.862383946313428,"p_An1":0.0,"q_Rp1":1.0508082034798678,"q_Rn1":0.0,"p_Ap2":3.3788609847300375,"p_An2":0.0,"q_Rp2":2.4489960616226414,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.2412449310434654,"p_An":0.0,"q_Rp":3.499804265102509,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 07:00"},{"id":187414318,"dataDate":1484197200000,"timeDetailType":"1h","p_Ap1":1.8691837294963725,"p_An1":0.0,"q_Rp1":1.0582991362141547,"q_Rn1":0.0,"p_Ap2":3.47077654537466,"p_An2":0.0,"q_Rp2":2.400483129124905,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.339960274871032,"p_An":0.0,"q_Rp":3.4587822653390594,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 08:00"},{"id":187415988,"dataDate":1484200800000,"timeDetailType":"1h","p_Ap1":1.698397723599968,"p_An1":0.0,"q_Rp1":1.217692009721844,"q_Rn1":0.0,"p_Ap2":3.9403059730861516,"p_An2":0.0,"q_Rp2":2.782586690563388,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.63870369668612,"p_An":0.0,"q_Rp":4.000278700285232,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 09:00"},{"id":187421046,"dataDate":1484204400000,"timeDetailType":"1h","p_Ap1":1.770688617708485,"p_An1":0.0,"q_Rp1":1.247170651508721,"q_Rn1":0.0,"p_Ap2":3.800600399940734,"p_An2":0.0,"q_Rp2":2.8786007343484457,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.571289017649219,"p_An":0.0,"q_Rp":4.125771385857167,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 10:00"},{"id":187422714,"dataDate":1484208000000,"timeDetailType":"1h","p_Ap1":1.7033451324705413,"p_An1":0.0,"q_Rp1":1.0186606877044013,"q_Rn1":0.0,"p_Ap2":3.665451417840052,"p_An2":0.0,"q_Rp2":3.372100469973339,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.368796550310593,"p_An":0.0,"q_Rp":4.390761157677741,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 11:00"},{"id":187427026,"dataDate":1484211600000,"timeDetailType":"1h","p_Ap1":1.7964298623544852,"p_An1":0.0,"q_Rp1":1.1195356501809601,"q_Rn1":0.0,"p_Ap2":3.5594242599083348,"p_An2":0.0,"q_Rp2":2.7476521275738794,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.35585412226282,"p_An":0.0,"q_Rp":3.8671877777548396,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 12:00"},{"id":187428365,"dataDate":1484215200000,"timeDetailType":"1h","p_Ap1":1.7224730800664825,"p_An1":0.0,"q_Rp1":1.2108503739720407,"q_Rn1":0.0,"p_Ap2":3.74411134009961,"p_An2":0.0,"q_Rp2":2.635037847977514,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.466584420166092,"p_An":0.0,"q_Rp":3.8458882219495547,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 13:00"},{"id":187431624,"dataDate":1484218800000,"timeDetailType":"1h","p_Ap1":1.7317976844902643,"p_An1":0.0,"q_Rp1":1.241209648441231,"q_Rn1":0.0,"p_Ap2":3.4198956519602963,"p_An2":0.0,"q_Rp2":2.4548707783827575,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.151693336450561,"p_An":0.0,"q_Rp":3.6960804268239884,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 14:00"},{"id":187434838,"dataDate":1484222400000,"timeDetailType":"1h","p_Ap1":1.8566151545400864,"p_An1":0.0,"q_Rp1":1.019283313866799,"q_Rn1":0.0,"p_Ap2":3.1186497181056128,"p_An2":0.0,"q_Rp2":2.4819389327272416,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":4.975264872645699,"p_An":0.0,"q_Rp":3.5012222465940406,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 15:00"},{"id":187435921,"dataDate":1484226000000,"timeDetailType":"1h","p_Ap1":1.741733090975663,"p_An1":0.0,"q_Rp1":1.0658572555146577,"q_Rn1":0.0,"p_Ap2":3.798266479856783,"p_An2":0.0,"q_Rp2":3.474849673310053,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.539999570832446,"p_An":0.0,"q_Rp":4.540706928824711,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"1-01-2017 16:00"},{"id":187442331,"dataDate":1484229600000,"timeDetailType":"1h","p_Ap1":1.872318066579738,"p_An1":0.0,"q_Rp1":1.1765541499763414,"q_Rn1":0.0,"p_Ap2":3.5679160377214982,"p_An2":0.0,"q_Rp2":3.1673106612656228,"q_Rn2":0.0,"p_Ap3":0.0,"p_An3":0.0,"q_Rp3":0.0,"q_Rn3":0.0,"p_Ap4":0.0,"p_An4":0.0,"q_Rp4":0.0,"q_Rn4":0.0,"p_Ap5":null,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":5.440234104301236,"p_An":0.0,"q_Rp":4.343864811241964,"q_Rn":0.0,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017 17:00"}];
    
//        var test_week_data_json = [{"id":186779139,"dataDate":1483909200000,"timeDetailType":"24h","p_Ap1":10.0455011,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":3.4065411,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":13.4520422,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"09-01-2017"},{"id":186779140,"dataDate":1483995600000,"timeDetailType":"24h","p_Ap1":7.696308,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":4.5387183,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":12.2350263,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"10-01-2017"},{"id":187230814,"dataDate":1484082000000,"timeDetailType":"24h","p_Ap1":7.2649937,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":3.1852348,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":10.4502285,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"11-01-2017"},{"id":187460060,"dataDate":1484168400000,"timeDetailType":"24h","p_Ap1":11.4596275,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":3.9387836,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":15.3984111,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"12-01-2017"},{"id":187763837,"dataDate":1484254800000,"timeDetailType":"24h","p_Ap1":10.4511364,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":4.0780014,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":14.5291378,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017"},{"id":187763838,"dataDate":1484341200000,"timeDetailType":"24h","p_Ap1":10.424177,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":4.960155,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":15.384332,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"14-01-2017"},{"id":187763839,"dataDate":1484427600000,"timeDetailType":"24h","p_Ap1":9.671702,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":4.8189126,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":14.4906146,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"15-01-2017"}];
    
//        var test_month_data_json = [{"id":186779131,"dataDate":1483218000000,"timeDetailType":"24h","p_Ap1":0.0,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":0.0,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":0.0,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"01-01-2017"},{"id":186779132,"dataDate":1483304400000,"timeDetailType":"24h","p_Ap1":0.0757358,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":0.0,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":0.0757358,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"02-01-2017"},{"id":186779133,"dataDate":1483390800000,"timeDetailType":"24h","p_Ap1":0.0,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":0.0,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":0.0,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"03-01-2017"},{"id":186779134,"dataDate":1483477200000,"timeDetailType":"24h","p_Ap1":0.0,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":0.0,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":0.0,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"04-01-2017"},{"id":186779135,"dataDate":1483563600000,"timeDetailType":"24h","p_Ap1":0.0,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":0.0,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":0.0,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"05-01-2017"},{"id":186779136,"dataDate":1483650000000,"timeDetailType":"24h","p_Ap1":7.1100513,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":0.4535556,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":7.5636069,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"06-01-2017"},{"id":186779137,"dataDate":1483736400000,"timeDetailType":"24h","p_Ap1":7.4071974,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":3.0832275,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":10.4904249,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"07-01-2017"},{"id":186779138,"dataDate":1483822800000,"timeDetailType":"24h","p_Ap1":9.4120615,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":2.7586039,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":12.1706654,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"08-01-2017"},{"id":186779139,"dataDate":1483909200000,"timeDetailType":"24h","p_Ap1":10.0455011,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":3.4065411,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":13.4520422,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"09-01-2017"},{"id":186779140,"dataDate":1483995600000,"timeDetailType":"24h","p_Ap1":7.696308,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":4.5387183,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":12.2350263,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"10-01-2017"},{"id":187230814,"dataDate":1484082000000,"timeDetailType":"24h","p_Ap1":7.2649937,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":3.1852348,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":10.4502285,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"11-01-2017"},{"id":187460060,"dataDate":1484168400000,"timeDetailType":"24h","p_Ap1":11.4596275,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":3.9387836,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":15.3984111,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"12-01-2017"},{"id":187763837,"dataDate":1484254800000,"timeDetailType":"24h","p_Ap1":10.4511364,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":4.0780014,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":14.5291378,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"13-01-2017"},{"id":187763838,"dataDate":1484341200000,"timeDetailType":"24h","p_Ap1":10.424177,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":4.960155,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":15.384332,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"14-01-2017"},{"id":187763839,"dataDate":1484427600000,"timeDetailType":"24h","p_Ap1":9.671702,"p_An1":null,"q_Rp1":null,"q_Rn1":null,"p_Ap2":4.8189126,"p_An2":null,"q_Rp2":null,"q_Rn2":null,"p_Ap3":0.0,"p_An3":null,"q_Rp3":null,"q_Rn3":null,"p_Ap4":0.0,"p_An4":null,"q_Rp4":null,"q_Rn4":null,"p_Ap5":0.0,"p_An5":null,"q_Rp5":null,"q_Rn5":null,"p_Ap":14.4906146,"p_An":null,"q_Rp":null,"q_Rn":null,"crc32Valid":null,"dataMstatus":null,"dataChanged":null,"dataDateString":"15-01-2017"}];
        //end test data
        
        
        moment.locale('ru', {
            months : "январь_февраль_март_апрель_май_июнь_июль_август_сентябрь_октябрь_ноябрь_декабрь".split("_"),
            monthsShort : "янв._фев._март_апр._май_июнь_июль_авг._сен._окт._ноя._дек.".split("_")
        });
        var DATA_URL = "../api/subscr/widgets/el",/*//chart/HwTemp";*/
            ZPOINT_STATUS_TEMPLATE = "widgets/zpointEl/zpoint-state-",
            SERVER_DATE_FORMAT = "DD-MM-YYYY HH:mm",
            PRECISION = 3;
        
        $scope.widgetOptions = widgetConfig.getOptions();
//console.log($scope.widgetOptions);
//        var zpstatus = $scope.widgetOptions.zpointStatus;
        $scope.data = {};
        $scope.data.zpointName = $scope.widgetOptions.zpointName;// + " Ну о-о-о-чень длинное название для точки учета";
        
//        $scope.data.MODES = [
//            {
//                keyname: "TODAY",
//                caption: "Сегодня",
//                modeClass: "",
//                timeDetailType: "1h"
//            }, {
//                keyname: "24",
//                caption: "Сутки",
//                modeClass: "",
//                timeDetailType: "1h"
//            }, {
//                keyname: "YESTERDAY",
//                caption: "Вчера",
//                modeClass: "",
//                timeDetailType: "1h"
//            }, {
//                keyname: "WEEK",
//                caption: "Неделя",
//                modeClass: "active",
//                timeDetailType: "24h"
//            }, {
//                keyname: "MONTH",
//                caption: "Текущий месяц",
//                modeClass: "",
//                timeDetailType: "24h"
//            }
//        ];
        $scope.data.MODES = [
            {
                keyname: "YEAR",
                caption: "Год",
                modeClass: "",
                timeDetailType: "1mon",
                dateFormat: "MMM",
                tooltipDateFormat: "MMM, YYYY"
            }, {
                keyname: "MONTH",
                caption: "Месяц",
                modeClass: "",
                timeDetailType: "24h",
                dateFormat: "DD",
                tooltipDateFormat: "DD.MM.YYYY"
            }, {
                keyname: "WEEK",
                caption: "Неделя",
                modeClass: "active",
                timeDetailType: "24h",
                dateFormat: "DD, MMM",
                tooltipDateFormat: "DD.MM.YYYY"
            }, {
                keyname: "DAY",
                caption: "Сутки",
                modeClass: "",
                timeDetailType: "1h",
                dateFormat: "HH:mm",
                tooltipDateFormat: "DD.MM.YYYY HH:mm"
            }
        ];
        $scope.data.startModeIndex = 2;//default mode index; 2 - TODAY
        $scope.data.currentMode = $scope.data.MODES[$scope.data.startModeIndex];
    
        $scope.data.imgPath = "widgets/zpointEl/flash.png";
        $scope.data.zpointStatus = ZPOINT_STATUS_TEMPLATE + "green.png";//"widgets/zpointEl/zpoint-state-" + zpstatus + ".png";
        $scope.data.zpointStatusTitle = $scope.widgetOptions.zpointStatusTitle;
        $scope.data.contZpointId = $scope.widgetOptions.contZpointId;
    
        $scope.data.consumptionSums = [];
    
        $scope.presentDataFlag = false;
        $scope.barChart = {};
        $scope.barChart.labels = [];//["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"];
        $scope.barChart.series = ['Потребление электрической энергии, кВт*ч'];
        $scope.barChart.data = [[]];
        $scope.barChart.dataTitle = [];
//        var tmpData = [
//            [65, 59, 80, 81, 56, 55, 40],
//            [28, 48, 40, 19, 86, 27, 90]
//        ];
        $scope.onClick = function (points, evt) {
            console.log(points, evt);
        };
        $scope.barChart.datasetOverride = [];//[{ yAxisID: 'y-axis-1' }, { yAxisID: 'y-axis-2' }];
        $scope.barChart.options = {
            legend: {
                display: true
            },
            tooltips: {
                callbacks: {
                    beforeTitle: function (arr, data) {
                        var result = "";
                        if (angular.isArray(arr) && arr.length > 0 && $scope.barChart.dataTitle.length > 0 && $scope.barChart.dataTitle[arr[0].index].dataDateString !== null) {
                            result = moment($scope.barChart.dataTitle[arr[0].index].dataDateString, SERVER_DATE_FORMAT).format($scope.data.currentMode.tooltipDateFormat);
                        }
                        return result;
                    },
                    title: function () {
                        return "";
                    }
                }
                
            }
        };
    
        function getDataSuccessCallback(rsp) {
            var tmpData = rsp.data;
//            !angular.isArray(tmpData) || tmpData.length === 0 ||
            if ((angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true)) {
                tmpData = generateTestData(timeDetailTypes[$scope.data.currentMode.keyname.toLowerCase()]);
                /*switch ($scope.data.currentMode.keyname) {
                        case "YEAR": 
                            //tmpData = test_year_data_json;
                            tmpData = generateTestData(timeDetailTypes.year);
                            break;
                        case "DAY": 
                            //tmpData = test_day_data_json;
                            tmpData = generateTestData(timeDetailTypes.day);
                            break;
                        case "WEEK": 
                            //tmpData = test_week_data_json;
                            tmpData = generateTestData(timeDetailTypes.week);
                            break;
                        case "MONTH": 
                            //tmpData = test_month_data_json;
                            tmpData = generateTestData(timeDetailTypes.month);
                            break;
                }
                */
            }
//            console.log(tmpData);            
            if (!angular.isArray(tmpData) || tmpData.length === 0) {
                $scope.presentDataFlag = false;
//                console.log("zpointElWidget: response data is empty!");
                return false;
            }
            $scope.data.consumptionSums = [];
            $scope.presentDataFlag = true;
            $scope.barChart.labels = [];
            $scope.barChart.data = [[]];
            $scope.barChart.dataTitle = [];
            //count el tariffs
            var elTariffCounter = 0,
                elTariffNumber = 0;
            $scope.data.elTariffNumber = elTariffNumber;
            tmpData.some(function (elm) {
                elTariffCounter = 0;
                if (angular.isNumber(elm.p_Ap1) && elm.p_Ap1 > 0) {
                    elTariffCounter += 1;
                }
                if (angular.isNumber(elm.p_Ap2) && elm.p_Ap2 > 0) {
                    elTariffCounter += 1;
                }
                if (angular.isNumber(elm.p_Ap3) && elm.p_Ap3 > 0) {
                    elTariffCounter += 1;
                }
                if (angular.isNumber(elm.p_Ap4) && elm.p_Ap4 > 0) {
                    elTariffCounter += 1;
                }
                if (angular.isNumber(elm.p_Ap5) && elm.p_Ap5 > 0) {
                    elTariffCounter += 1;
                }
                if (elTariffCounter > elTariffNumber) {
                    elTariffNumber = elTariffCounter;
                }
            });
//console.log("elTariffNumber = " + elTariffNumber); 
            if (elTariffNumber === 0) {
                return false;
            }
            var dataTitleElem = {};
            $scope.data.elTariffNumber = elTariffNumber;
//            $scope.data.elTariffNumber = 1;
            if (elTariffNumber > 2 || elTariffNumber === 1) {
                $scope.barChart.series = ['Общее, кВт*ч'];
                $scope.data.consumptionSums[0] = 0;
                tmpData.forEach(function (elm) {
                    $scope.barChart.labels.push(moment(elm.dataDateString, SERVER_DATE_FORMAT).format($scope.data.currentMode.dateFormat));
                    $scope.barChart.data[0].push(elm.p_Ap.toFixed(PRECISION));
                    $scope.data.consumptionSums[0] += Number(elm.p_Ap.toFixed(PRECISION));
                    dataTitleElem = {
                        dataDateString : elm.dataDateString
                    };
                    $scope.barChart.dataTitle.push(dataTitleElem);
                });
                $scope.data.consumptionSums[0] = $scope.data.consumptionSums[0].toFixed(PRECISION);
                $scope.barChart.series[0] += " (Всего: " + $scope.data.consumptionSums[0] + ")";
            } else {
                $scope.barChart.series = ['Тариф 1, кВт*ч', 'Тариф 2, кВт*ч'];
                $scope.barChart.data = [[], []];
                $scope.data.consumptionSums[0] = 0;
                $scope.data.consumptionSums[1] = 0;
                tmpData.forEach(function (elm) {
                    $scope.barChart.labels.push(moment(elm.dataDateString, SERVER_DATE_FORMAT).format($scope.data.currentMode.dateFormat));
                    $scope.barChart.data[0].push(elm.p_Ap1.toFixed(PRECISION));
                    $scope.data.consumptionSums[0] += Number(elm.p_Ap1.toFixed(PRECISION));
                    $scope.barChart.data[1].push(elm.p_Ap2.toFixed(PRECISION));
                    $scope.data.consumptionSums[1] += Number(elm.p_Ap2.toFixed(PRECISION));
                    dataTitleElem = {
                        dataDateString : elm.dataDateString
                    };
                    $scope.barChart.dataTitle.push(dataTitleElem);
                });
                $scope.data.consumptionSums[0] = $scope.data.consumptionSums[0].toFixed(PRECISION);
                $scope.data.consumptionSums[1] = $scope.data.consumptionSums[1].toFixed(PRECISION);
                $scope.barChart.series[0] += " (Всего: " + $scope.data.consumptionSums[0] + ")";
                $scope.barChart.series[1] += " (Всего: " + $scope.data.consumptionSums[1] + ")";
            }
        }
    
        function errorCallback(e) {
            console.log(e);
        }
    
        function getStatusSuccessCallback(resp) {
            if (angular.isUndefined(resp) || resp === null) {
//                console.log("zpointElWidget: status response is empty.");
                return false;
            }
            if (angular.isUndefined(resp.data) || resp.data === null) {
//                console.log("zpointElWidget: status response data is empty.");
                return false;
            }
            if (angular.isDefined(resp.data.color) && resp.data.color !== null && angular.isString(resp.data.color)) {
                $scope.data.zpointStatus = ZPOINT_STATUS_TEMPLATE + resp.data.color.toLowerCase() + ".png";
            }/* else {
                console.log("zpointElWidget: zpoint status color is empty or not string.");
            }*/
        }
    
        $scope.modeClick = function (mode) {
            $scope.data.currentMode = mode;
            //set class
            $scope.data.MODES.forEach(function (mod) {
                mod.modeClass = "";
            });
            mode.modeClass = "active";
            //get data
            if (angular.isUndefined($scope.data.contZpointId) || $scope.data.contZpointId === null || mode === null || mode.keyname === null) {
                console.log("zpointElWidget: contZpoint or mode is null!");
                console.log("data:");
                console.log($scope.data);
                console.log("mode:");
                console.log(mode);
                return false;
            }
            var url = DATA_URL + "/" + encodeURIComponent($scope.data.contZpointId) + "/chart/data/" + encodeURIComponent(mode.keyname);
            $http.get(url).then(getDataSuccessCallback, errorCallback);
        };
    
        function getZpointState() {
            if (angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true) {
                return true;
            }
            var url = DATA_URL + "/" + encodeURIComponent($scope.data.contZpointId) + "/status";
            $http.get(url).then(getStatusSuccessCallback, errorCallback);
        }
                // Показания точек учета
        $scope.getIndicators = function () {
            if (angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true) {
                return true;
            }
//            widgetConfig.requestToGetIndicators({contObjectId: $scope.widgetOptions.contObjectId, contZpointId: $scope.widgetOptions.contZpointId});
            widgetConfig.exportProperties({contObjectId: $scope.widgetOptions.contObjectId, contZpointId: $scope.widgetOptions.contZpointId, action: "openIndicators"});
//            $scope.$broadcast('requestToGetIndicators', {contObjectId: $scope.widgetOptions.contObjectId, contZpointId: $scope.widgetOptions.contZpointId});
            return true;
        };
    
        $scope.openNotices = function () {
            if (angular.isDefined($scope.widgetOptions.previewMode) && $scope.widgetOptions.previewMode === true) {
                return true;
            }
            widgetConfig.exportProperties({contObjectId: $scope.widgetOptions.contObjectId, action: "openNotices"});
        };
        
        function initWidget() {
            $scope.modeClick($scope.data.currentMode);
            getZpointState();
        }
        
        initWidget();
        
    }]);