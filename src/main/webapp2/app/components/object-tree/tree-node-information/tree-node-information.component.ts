import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
// import { switchMap } from 'rxjs/operators';
import { JhiEventManager } from 'ng-jhipster';

@Component({
    selector: 'jhi-tree-node-information',
    templateUrl: './tree-node-information.component.html'
})
export class TreeNodeInformationComponent implements OnInit {

    public treeNodeId: number;

    constructor(private route: ActivatedRoute,
                 private eventManager: JhiEventManager
                ) {
        console.log('TreeNodeInformationComponent: Constructor()');
    }

    ngOnInit() {
        console.log('TreeNodeInformationComponent ngOnInit()');
        console.error('TEST ERRROR');
//        this.route.paramMap.pipe(switchMap((params: ParamMap) => console.log(params.get('ptreeNodeId'))));
        this.route.paramMap.subscribe((params: ParamMap) => console.log(params.get('ptreeNodeId')));
        this.eventManager
            .subscribe('setTreeNode',
                       (treeNodeId) => { console.log(treeNodeId); this.treeNodeId = +(treeNodeId.content._id || treeNodeId.content.id || treeNodeId.content.nodeObject.id);
                                        console.log('TreeNodeInformationComponent: treeNodeId: ', treeNodeId);
                                       });
    }
}
