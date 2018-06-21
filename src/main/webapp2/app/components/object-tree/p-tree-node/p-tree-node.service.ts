import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../../app.constants';

import { PTreeNode } from '../models/p-tree-node.model';

@Injectable()
export class PTreeNodeService {
    private resourceUrl = SERVER_API_URL + 'api/p-tree-node/';
    private resourceUrlSuffix = '/stub';

//    private ptrees: any;
//    private ptreesStub: any;

    constructor(private http: HttpClient) {}

    loadPTreeNode(treeNodeId: string, childLevel = 0): Observable<PTreeNode> {
        return this.http.get<PTreeNode>(this.resourceUrl + treeNodeId, {params: new HttpParams()
                                                                        .set('childLevel', childLevel.toString())});
    }

    loadPTreeNodeStub(treeNodeId: string, childLevel = 0): Observable<PTreeNode> {
        return this.http.get<PTreeNode>(this.resourceUrl + treeNodeId + this.resourceUrlSuffix, {params: new HttpParams()
                                                                                                 .set('childLevel', childLevel.toString())});
    }
}
