import { Chart } from 'chart.js/dist/Chart.js';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap } from 'rxjs/operators';
// import { ChartModule } from 'primeng/chart';

import {
    TreeNodeColorStatusService,
    TreeNodeColorStatus
} from './';

export class Doughnut {
    data: any[];
    labels: string[];
    colors: string[];
    options: any;
}

//    this.doughnutTemplate = {};
//    this.doughnutTemplate.data = [];
//    this.doughnutTemplate.labels = labels;
//    this.doughnutTemplate.colors = doughuntColors;
//    this.doughnutTemplate.options = doughuntOpts;

@Component({
    selector: 'jhi-tree-node-color-status',
    templateUrl: './tree-node-color-status.component.html',
    styleUrls: [

    ]
})
export class TreeNodeColorStatusComponent implements OnInit {

    nodeGraphData: any;
    nodeGraphOptions: any;
    doughnuts: any;

    private chartBgColors: string[];
    private chartLabels: string[];
    public resources: string[];
    public statusKeynames: string[];

    public doughuntOpts: any = {
        responsive: false,
        cutoutPercentage: 80,
        legend: {
            display: true,
            position: 'right',
            onClick: this.legendOnClick,
            labels: {
                fontSize: 14,
                generateLabels: this.generateLabels
            },
        }/*,
        legendCallback: legendCallback
        */
        /*title: {
            display: true,
            postion: "top",
            text: "<img src = \"components\object-tree-module\cont-object-monitor-component\heat.png\"/> (123)"
        }*/
    };

    // settings for resource doughnut grid
    dougRows = [{val: 1}, {val: 2}];
    dougCols = [
        {
            class: 'ui-g-6',
            val: 0
        },
        {
            class: 'ui-g-6',
            val: 1
        }
    ];

    doughuntColors = ['#ef473a', '#FDB45C', '#6e7b90'];
//    this.doughuntColors = doughuntColors;
    doughnutTemplate: any;
//    this.doughnutTemplate = {};
//    this.doughnutTemplate.data = [];
//    this.doughnutTemplate.labels = labels;
//    this.doughnutTemplate.colors = doughuntColors;
//    this.doughnutTemplate.options = doughuntOpts;

    constructor(
        private treeNodeColorStatusService: TreeNodeColorStatusService,
         private route: ActivatedRoute
    ) {}

    ngOnInit() {
        this.chartBgColors = this.treeNodeColorStatusService.getChartBgColors();
        this.chartLabels = this.treeNodeColorStatusService.getChartLabels();
        this.resources = this.treeNodeColorStatusService.getResources();
        this.statusKeynames = this.treeNodeColorStatusService.getStatusKeynames();
//        for (let res in this.resources) {
//            this.doughnuts[this.resources[res]] = new Doughnut();
//        }

console.log(this.route);
//        this.route.data.subscribe((data) => console.log('Route data: ', data));
        const tmp = this.route.paramMap.pipe(
            switchMap((params: ParamMap) => {
//                for (let res in this.resources) {
//                    this.initResourceChart(params.get('treeNodeId'), this.resources[res]);
//                }
                return this.treeNodeColorStatusService.loadCommonData(params.get('treeNodeId'));
            })
        );

        tmp.subscribe((data) => this.performTreeNodeColorStatus(data));

//        this.treeNodeColorStatusService
//            .loadCommonData()
//            .subscribe();
    }

//    performTreeNodeMonitorData(param) {
//        this.performTreeNodeColorStatus(param);
//        this.performResourceData(param);
//    }
//
//    performResourceData() {
//
//    }

    legendOnClick(ev, label, arg3) {
        console.log('Label click!');
        console.log(ev);
        console.log(label);
        console.log(label.index);
        console.log(arg3);
    }

    performTreeNodeColorStatus(data: TreeNodeColorStatus[]) {
console.log('Color data: ', data);
        const redElm = data.filter((elm) => elm.levelColor === 'RED')[0];
        const yellowElm = data.filter((elm) => elm.levelColor === 'YELLOW')[0];
        const greenElm = data.filter((elm) => elm.levelColor === 'GREEN')[0];
        const red = redElm ? redElm.contObjectCount : 0;
        const yellow = yellowElm ? yellowElm.contObjectCount : 0;
        const green = greenElm ? greenElm.contObjectCount : 0;
console.log(red, yellow, green);
        this.nodeGraphData = {
            datasets: [
                {
                    data: [ red, yellow, green ],
                    backgroundColor: this.chartBgColors,
                    label: 'Общая статистика по узлу.'
                }
            ],
            labels: this.chartLabels
        };

        this.nodeGraphOptions = {
            responsive: true,
            scale: {
                gridLines: {
                    color: 'rgba(0, 255, 0, 1)'
                }
            },
            legend: {
                display: false
            }
        };

console.log(this.nodeGraphData);
console.log(this.nodeGraphOptions);
    }

    generateLabels(chart) {
//                            console.log("generateLabels", chart);
        const data = chart.data;
//                            console.log(data);
        if (data.labels.length && data.datasets.length) {
            const result = data.labels.map(function(label, i) {
                const meta = chart.getDatasetMeta(0);
                const ds = data.datasets[0];
                const arc = meta.data[i];
                const custom = arc && arc.custom || {};
                const getValueAtIndexOrDefault = Chart.helpers.getValueAtIndexOrDefault;
                const arcOpts = chart.options.elements.arc;
                const fill = custom.backgroundColor ? custom.backgroundColor : getValueAtIndexOrDefault(ds.backgroundColor, i, arcOpts.backgroundColor);
                const stroke = custom.borderColor ? custom.borderColor : getValueAtIndexOrDefault(ds.borderColor, i, arcOpts.borderColor);
                const bw = custom.borderWidth ? custom.borderWidth : getValueAtIndexOrDefault(ds.borderWidth, i, arcOpts.borderWidth);

                return {
                    text: data.datasets[0].data[i]/*label*/,
                    fillStyle: fill,
                    strokeStyle: stroke,
                    lineWidth: bw,
                    hidden: isNaN(ds.data[i]) || meta.data[i].hidden,

                    // Extra data used for toggling the correct item
                    index: i
                };
            });
//                                console.log(result);
            return result;
        }
        return [];
    }

    initResourceChart(nodeId, resource) {
        this.treeNodeColorStatusService.loadResourceData(nodeId, resource)
            .subscribe((resp) => {
// console.log(resp);
            if (!resp || resp === null) {
                console.warn('Resource data: ' + resource + ' is empty', resp);
                return false;
            }
            this.doughnuts[resource] = Object.assign({}, this.doughnutTemplate); // angular.copy(this.doughnutTemplate);
            this.doughnuts[resource].options = Object.assign({}, this.doughnutTemplate.options);
            this.doughnuts[resource].options.name = resource;
            this.doughnuts[resource].data = [0, 0, 0];
            this.doughnuts[resource].allCount = 0;
            resp.forEach( (colorObj) => this.performResourceColorData(colorObj, resource));
        });
    }

    performResourceColorData(colorObj, resource) {
        this.doughnuts[resource].allCount += colorObj.contObjectCount;
        switch (colorObj.levelColor) {
            case 'RED': case 'red':
                this.doughnuts[resource].data[0] = colorObj.contObjectCount;
                break;
            case 'YELLOW': case 'yellow':
                this.doughnuts[resource].data[1] = colorObj.contObjectCount;
                break;
            case 'GREEN': case 'green':
                this.doughnuts[resource].data[2] = colorObj.contObjectCount;
                break;
        }
    }

}
