import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../../app.constants';

import { PTreeNodeMonitor } from './p-tree-node-monitor.model';

@Injectable()
export class PTreeNodeMonitorService {

    private resourceUrl = SERVER_API_URL + 'api/p-tree-node-monitor/all-linked-objects/';
    constructor(private http: HttpClient) { }

    loadPTreeNodeMonitor(nodeId: number): Observable<PTreeNodeMonitor[]> {
        return this.http.get<PTreeNodeMonitor[]>(this.resourceUrl, {params: new HttpParams()
                                                                    .set('nodeId', nodeId.toString())});
    }

}
