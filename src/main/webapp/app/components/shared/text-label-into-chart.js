/*global Chart*/
(function () {
    'use strict';
    
    
    Chart.pluginService.register({
        beforeDraw: function (chart) {
            if (chart.config.type != "doughnut") {
                return;
            }
            var width = chart.chart.width,
                height = chart.chart.height,
                ctx = chart.chart.ctx,
                data = chart.config.data.datasets[0].data;
            
            ctx.restore();
            var fontSize = (height / 114).toFixed(2);
            ctx.font = fontSize + "em sans-serif";
            ctx.textBaseline = "middle";
            ctx.fillStyle = "#337ab7";
            var sumValues = 0;
            data.forEach(function (elm) {
                sumValues += elm;
            });
            var text = sumValues,
                textX = Math.round((width - ctx.measureText(text).width) / 2),
                textY = height / 2;
            
            ctx.fillText(text, textX, textY);
            ctx.save();
            
        }
    });
}());