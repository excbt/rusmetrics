import { Injectable } from '@angular/core';

import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../../app.constants';

import { SubscrPref, SubscrPrefValue } from './';

@Injectable()
export class SubscrPrefService {

    private resourceUrl = SERVER_API_URL + 'api/subscr/subscrPrefValue';
//    ?subscrPrefKeyname=SUBSCR_OBJECT_TREE_CONT_OBJECTS

    constructor(private http: HttpClient) {}

//    loadAll() {
//
//    }
//
//    loadOne(keyname: string) {
//
//    }

    loadValue(subscrPrefKeyname: string): Observable<SubscrPrefValue> {
        return this.http.get<SubscrPrefValue>(this.resourceUrl, { params: new HttpParams().set('subscrPrefKeyname', subscrPrefKeyname) });
    }
}
