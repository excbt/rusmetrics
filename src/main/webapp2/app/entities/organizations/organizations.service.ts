import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { Organization } from './organization.model';
import { ExcPageSize, ExcPageSorting, ExcPage } from '../shared';

@Injectable()
export class OrganizationsService {

    private resourceUrl = SERVER_API_URL + 'api/organizations/';
    constructor(private http: HttpClient) { }

    findAll(sortField: string, sortOrder: string): Observable<Organization[]> {
        return this.http.get<Organization[]>(this.resourceUrl, {params : new HttpParams().set('sort', sortField.concat(',', sortOrder))});
    }

    findAllPaged(pageSorting: ExcPageSorting, pageSize: ExcPageSize): Observable<Organization[]> {
        return this.http.get<Organization[]>(this.resourceUrl, {params : new HttpParams()
            .set('sort', pageSorting ? pageSorting.orderString() : '')
            .set('page', pageSize ? String(pageSize.page) : '')
            .set('size',  pageSize ? String(pageSize.size) : '') } );
    }

    findAllResponce(pageSorting: ExcPageSorting, pageSize: ExcPageSize): Observable<Organization[]> {
        return this.http.get<Organization[]>(this.resourceUrl, {params : new HttpParams()
            .set('sort', pageSorting.orderString())
            .set('page', String(pageSize.page))
            .set('size', String(pageSize.size)) } );
    }

    findAllPage(pageSorting: ExcPageSorting, pageSize: ExcPageSize): Observable<ExcPage<Organization>> {
        return this.http.get<ExcPage<Organization>>(this.resourceUrl + 'page/', {params : new HttpParams()
            .set('sort', pageSorting.orderString())
            .set('page', String(pageSize.page))
            .set('size', String(pageSize.size)) } );
    }

    find(id: number | string): Observable<Organization> {
        console.log('query');
        return this.http.get<Organization>(this.resourceUrl.concat('' + id));
    }

    update(organization: Organization): Observable<Organization> {
        // dsd
        return this.http.put<Organization>(this.resourceUrl, organization);
        // console.log('save');
        // return Observable.of(true);
    }
}
