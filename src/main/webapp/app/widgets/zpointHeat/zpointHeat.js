/*jslint node: true*/
/*global angular*/
'use strict';

angular.module('zpointHeatWidget', ['angularWidget', 'chart.js'])
    .config(['ChartJsProvider', function (ChartJsProvider) {
        // Configure all charts
//        ChartJsProvider.setOptions({
//            chartColors: ['#FF5252', '#FF8A80'],
//            responsive: true
//        });
        // Configure all line charts
//        ChartJsProvider.setOptions('line', {
//            showLines: false
//        });
    }])
    .controller('zpointHeatWidgetCtrl', function ($scope, widgetConfig) {
        $scope.widgetOptions = widgetConfig.getOptions();
        var zpstatus = $scope.widgetOptions.zpointStatus;
        $scope.data = {};
        $scope.data.imgPath = "vendor_components/glyphicons_free/glyphicons/png/glyphicons-85-heat.png";
        $scope.data.zpointStatus = "images/zpoint-state-" + zpstatus + ".png";
        $scope.data.zpointStatusTitle = $scope.widgetOptions.zpointStatusTitle;
    
        $scope.lineChart = {};
        $scope.lineChart.labels = ["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"];
        $scope.lineChart.series = ['Нормативная температура теплоносителя', 'Фактическая температура теплоносителя'];
        $scope.lineChart.data = [
            [65, 59, 80, 81, 56, 55, 40],
            [28, 48, 40, 19, 86, 27, 90]
        ];
        $scope.onClick = function (points, evt) {
            console.log(points, evt);
        };
        $scope.lineChart.datasetOverride = [];//[{ yAxisID: 'y-axis-1' }, { yAxisID: 'y-axis-2' }];
        $scope.lineChart.options = {
            legend: {
                display: true
            }
        };
        var tmpOpt = {
            scales: {
                yAxes: [
                    {
                        id: 'y-axis-1',
                        type: 'linear',
                        display: true,
                        position: 'left'
                    },
                    {
                        id: 'y-axis-2',
                        type: 'linear',
                        display: true,
                        position: 'right'
                    }
                ]
            },
            legend: {
                display: true
            }
        };
    });