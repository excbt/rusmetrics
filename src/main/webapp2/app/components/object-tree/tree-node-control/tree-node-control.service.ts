import { Injectable } from '@angular/core';
// import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { PTreeNode } from '../models/p-tree-node.model';

// import { SERVER_API_URL } from '../../../app.constants';

// import { PTreeNodeColorLinkedObject } from '../models/p-tree-node-color-linked-object';

import { PTreeNodeService } from '../p-tree-node';

@Injectable()
export class TreeNodeControlService {
//    private resourceUrl: string = SERVER_API_URL + 'api/p-tree-node-monitor/all-linked-objects';

    constructor(/*private http: HttpClient,*/
                private ptreeNodeService: PTreeNodeService) {}

//    loadAllLinkedObjects(treeNodeId: string): Observable<PTreeNodeColorLinkedObject[]> {
//        return this.http.get<PTreeNodeColorLinkedObject[]>(this.resourceUrl, {params: new HttpParams().set('nodeId', treeNodeId)});
//    }

    loadPTreeNodeLinkedObjects(treeNodeId: string): Observable<PTreeNode> {
        return this.ptreeNodeService.loadPTreeNodeStub(treeNodeId);
    }

    getNodeObjectIds(treeNode: PTreeNode) {
        const nodeObjectIds = [];
        treeNode.linkedNodeObjects.map((nodeObject: PTreeNode) => nodeObjectIds.push(nodeObject.nodeObject.id));
        return nodeObjectIds;
    }

}
