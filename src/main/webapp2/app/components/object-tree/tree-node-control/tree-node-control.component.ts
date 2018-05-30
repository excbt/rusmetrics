import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { TreeNodeControlService } from './tree-node-control.service';
import { PTreeNode } from '../models/p-tree-node.model';

@Component({
    selector: 'jhi-tree-node-control',
    templateUrl: './tree-node-control.component.html',
    styleUrls: [
        './tree-node-control.component.scss'
    ]
})
export class TreeNodeControlComponent implements OnInit {

    treeNodeId: number;
    nodeObjects: number[];

    constructor(private route: ActivatedRoute,
                 private treeNodeControlService: TreeNodeControlService) {}

    ngOnInit() {

        const tmp = this.route.paramMap.pipe(
            switchMap((params: ParamMap) => {
                return this.treeNodeControlService.loadPTreeNodeLinkedObjects(params.get('treeNodeId'));
            })
        );

        tmp.subscribe((data) => this.performTreeNodeControlData(data));
    }

    performTreeNodeControlData(treeNode: PTreeNode) {
        console.log(treeNode);
        this.nodeObjects = this.treeNodeControlService.getNodeObjectIds(treeNode);
        console.log('TreeNodeControlComponent: nodeObjects: ', this.nodeObjects);
    }

}
