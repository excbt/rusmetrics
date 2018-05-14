import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../../app.constants';
import { SubscrContObjectTreeType1 } from './subscr-cont-object-tree-type1.model';
// import { PTreeNodeLinkedObject } from './p-tree-node.model';

@Injectable()
export class SubscrTreeService {

//    private resourceUrl = SERVER_API_URL + 'api/p-tree-node/';
    private resourceUrl = SERVER_API_URL + 'api/subscr/subscrObjectTree/contObjectTreeType1';
    constructor(private http: HttpClient) { }

    loadOne(id: number): Observable<SubscrContObjectTreeType1> {
        return this.http.get<SubscrContObjectTreeType1>(this.resourceUrl + id);
    }

    loadAll(): Observable<SubscrContObjectTreeType1[]> {
        return this.http.get<SubscrContObjectTreeType1[]>(this.resourceUrl);
    }

}

// @Injectable()
// export class PTreeNodeLinkedObjectService {
//
//    private resourceUrl = SERVER_API_URL + 'api/p-tree-node-monitor/all-linked-objects/';
//    constructor(private http: HttpClient) { }
//
//    findLinkedObjects(nodeId: number): Observable<PTreeNodeLinkedObject[]> {
//        return this.http.get<PTreeNodeLinkedObject[]>(this.resourceUrl + nodeId);
//    }
//
// }
