import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { Organization } from './organization.model';

@Injectable()
export class OrganizationsService {

    private resourceUrl = SERVER_API_URL + 'api/organizations/';
    constructor(private http: HttpClient) { }

    findAll(): Observable<Organization[]> {
        return this.http.get<Organization[]>(this.resourceUrl);
    }

}
