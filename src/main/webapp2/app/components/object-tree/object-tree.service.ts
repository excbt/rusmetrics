import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { PTreeNode } from './p-tree-node.model';
import { PTreeNodeLinkedObject } from './p-tree-node.model';

@Injectable()
export class PTreeNodeService {

    private resourceUrl = SERVER_API_URL + 'api/p-tree-node/';
    private resourceUrlSuffix = '/stub';
    constructor(private http: HttpClient) { }

    findNode(id: number): Observable<PTreeNode> {
        return this.http.get<PTreeNode>(this.resourceUrl + id + this.resourceUrlSuffix);
    }

}

@Injectable()
export class PTreeNodeLinkedObjectService {

    private resourceUrl = SERVER_API_URL + 'api/p-tree-node-monitor/all-linked-objects/';
    constructor(private http: HttpClient) { }

    findLinkedObjects(nodeId: number): Observable<PTreeNodeLinkedObject[]> {
        return this.http.get<PTreeNodeLinkedObject[]>(this.resourceUrl + nodeId);
    }

}
