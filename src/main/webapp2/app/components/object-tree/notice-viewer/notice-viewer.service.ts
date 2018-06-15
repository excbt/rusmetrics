import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SERVER_API_URL } from '../../../app.constants';
import { Observable } from 'rxjs/observable';

import { Notice } from './notice.model';

@Injectable()
export class NoticeViewerService {
    private DEFULT_PAGE = 0;
    private DEFAULT_PAGE_SIZE = 100;
    private resourceUrl = SERVER_API_URL + 'api/subscr/contEvent/notifications/paged';
    
    constructor(private http: HttpClient) {}
    
    loadNotices(startDate: string, endDate: string, contObjectIds: number[]): Observable<Notice[]> {
        const url = this.resourceUrl;
        let params: HttpParams = new HttpParams();
        params.set('fromDate', startDate);
        params.set('toDate', endDate);
        contObjectIds.forEach((id) => params.append('contObjectIds', id.toString()));
//        params.set('contObjectIds', contObjectIds);
        return this.http.get<Notice[]>(url, {params: params});        
    }
}