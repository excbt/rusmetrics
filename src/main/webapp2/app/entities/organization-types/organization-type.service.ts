import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { OrganizationType } from './organization-type.model';

@Injectable()
export class OrganizationsService {

    private resourceUrl = SERVER_API_URL + 'api/organization-types/';
    constructor(private http: HttpClient) { }

    findAll(sortField: string, sortOrder: string): Observable<OrganizationType[]> {
        return this.http.get<OrganizationType[]>(this.resourceUrl);
    }

}
