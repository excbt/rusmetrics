import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap } from 'rxjs/operators';
// import { ChartModule } from 'primeng/chart';

import {
    TreeNodeColorStatusService,
    TreeNodeColorStatus
} from './';

@Component({
    selector: 'jhi-tree-node-color-status',
    templateUrl: './tree-node-color-status.component.html',
    styleUrls: [

    ]
})
export class TreeNodeColorStatusComponent implements OnInit {

    nodeData: any;

    constructor(
        private treeNodeColorStatusService: TreeNodeColorStatusService,
         private route: ActivatedRoute
    ) 
    {
//        this.nodeData = {
//            datasets: [
//                {
//                    data: [
//                        0,
//                        0,
//                        0
//                    ],
//                    backgroundColor: [
//                        'red',
//                        'yellow',
//                        'green'
//                    ],
//                    label: 'Общая статистика по узлу.'
//                }
//            ],
//            labels: [
//                'Критически',
//                'Некритические',
//                'Штатные'
//            ]
//        };
    }

    ngOnInit() {
console.log(this.route);
//        this.route.data.subscribe((data) => console.log('Route data: ', data));
        const tmp = this.route.paramMap.pipe(
            switchMap((params: ParamMap) =>
                      this.treeNodeColorStatusService
                        .loadCommonData(params.get('treeNodeId'))
                     )
        );

        tmp.subscribe((data) => this.performTreeNodeColorStatus(data));

//        this.treeNodeColorStatusService
//            .loadCommonData()
//            .subscribe();
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
        this.nodeData = {
            datasets: [
                {
                    data: [
                        red,
                        yellow,
                        green
                    ],
                    backgroundColor: [
                        'red',
                        'yellow',
                        'green'
                    ],
                    label: 'Общая статистика по узлу.'
                }
            ],
            labels: [
                'Критически',
                'Некритические',
                'Штатные'
            ]
        };

console.log(this.nodeData);
    }

}
