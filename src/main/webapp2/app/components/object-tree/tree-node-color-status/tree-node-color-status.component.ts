import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap } from 'rxjs/operators';
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

    constructor(
        private treeNodeColorStatusService: TreeNodeColorStatusService,
         private route: ActivatedRoute
    ) {}

    ngOnInit() {
console.log(this.route);
//        this.route.data.subscribe((data) => console.log('Route data: ', data));
        const tmp = this.route.paramMap.pipe(
            switchMap((params: ParamMap) =>
                      this.treeNodeColorStatusService
                        .loadCommonData(params.get('treeNodeId'))
                     )
        );

        tmp.subscribe((data) => console.log('Color data: ', data));

//        this.treeNodeColorStatusService
//            .loadCommonData()
//            .subscribe();
    }

    performTreeNodeColorStatus(data: TreeNodeColorStatus[]) {

    }

}
