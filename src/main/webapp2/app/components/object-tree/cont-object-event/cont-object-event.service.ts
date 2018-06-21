import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { ContObjectEvent } from './cont-object-event.model';

@Injectable()
export class ContObjectEventService {
    private resourceUrl = '../api/subscr/contEvent/notifications/contObject/{objectId}/monitorEventsV2';

    constructor(private http: HttpClient) {}

    loadEvents(objectId: string): Observable<ContObjectEvent[]> {
        const url = this.resourceUrl.replace(/{objectId}/, objectId);
        return this.http.get<ContObjectEvent[]>(url);
    }
}
