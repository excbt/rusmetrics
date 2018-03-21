import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { OrganizationType } from './organization-type.model';

@Injectable()
export class OrganizationTypeService {

    private resourceUrl = SERVER_API_URL + 'api/organization-types/';
    constructor(private http: HttpClient) { }

    findAll(): Observable<OrganizationType[]> {
        return this.http.get<OrganizationType[]>(this.resourceUrl);
    }

}
