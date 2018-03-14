/*global Chart*/
(function () {
    'use strict';
    
    
    Chart.pluginService.register({
        beforeDraw: function (chart) {
//console.log(chart);            
            if (chart.config.type != "doughnut") {
                return;
            }
            var width = chart.chart.width,
                height = chart.chart.height,
                ctx = chart.chart.ctx,
                data = chart.config.data.datasets[0].data;
            
            ctx.restore();
            var fontSize = (height / 114).toFixed(2);
//            if (height === 130) {
//                fontSize = (height / 114 + 1).toFixed(2);
//            }             
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
//            if (height === 130) {
                // labels at cont object monitor component
//                textX = Math.round((height + 30 - ctx.measureText(text).width) / 2);
//            }
//            ctx.fillText(text, textX, textY);
//            var img =new Image();
//            img.src = "components/object-tree-module/cont-object-monitor-component/db.png";
//            console.log("img", img);
//            ctx.drawImage(img, textX, textY);
            ctx.save();
            
        }
    });
    
//    Chart.types.Doughnut.extend({
//        name: "Doughnut sl",
//        draw: function () {
//            Chart.types.Doughnut.prototype.draw.apply(this, arguments);
//            ctx.drawImage()
//        }
//    });
}());