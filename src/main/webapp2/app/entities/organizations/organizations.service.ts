import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { Organization } from './organization.model';

@Injectable()
export class OrganizationsService {

    private resourceUrl = SERVER_API_URL + 'api/organizations/';
    constructor(private http: HttpClient) { }

    findAll(sortField: string, sortOrder: string): Observable<Organization[]> {
        return this.http.get<Organization[]>(this.resourceUrl, {params : new HttpParams().set('sort', sortField.concat(',', sortOrder))});
    }

    find(id: number | string): Observable<Organization> {
        console.log('query');
        return this.http.get<Organization>(this.resourceUrl.concat('' + id));
    }

    update(organization: Organization): Observable<boolean> {
        // dsd
        console.log('save');
        return Observable.of(true);
    }
}
