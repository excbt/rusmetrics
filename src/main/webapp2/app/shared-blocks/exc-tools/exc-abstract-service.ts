import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';
import { ExcPageSize, ExcPageSorting, ExcPage } from '../../shared-blocks';

export interface ServiceParams {
    apiUrl: string;
}

export const defaultPageSuffix = 'page/';

export interface ExcPageParams {
    pageSorting?: ExcPageSorting;
    pageSize?: ExcPageSize;
    searchString?: string;
}

export class ExcAbstractService<T> {

    readonly resourceUrl = SERVER_API_URL + this.params.apiUrl;

    constructor(
        readonly params: ServiceParams,
        private http: HttpClient) { }

    findSearchPage(pageSorting: ExcPageSorting, pageSize: ExcPageSize, searchString: string): Observable<ExcPage<T>> {
        return this.http.get<ExcPage<T>>(this.resourceUrl + defaultPageSuffix, {
            params : this.defaultPageParams({ pageSorting, pageSize, searchString })
        } );
    }

    findOne(id: any): Observable<T> {
        return this.http.get<T>(this.params.apiUrl + id);
    }

    defaultPageParams(params: ExcPageParams): HttpParams {
        return new HttpParams()
        .set('sort', params.pageSorting.orderString())
        .set('page', String(params.pageSize.page))
        .set('size', String(params.pageSize.size))
        .set('searchString', String(params.searchString));
    }
}
