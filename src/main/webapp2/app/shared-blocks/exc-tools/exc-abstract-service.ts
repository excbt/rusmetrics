import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';
import { ExcPageSize, ExcPageSorting, ExcPage, ExcPageParams } from './';

export interface ServiceParams {
    apiUrl: string;
}

export const defaultPageSuffix = 'page/';

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

    findPage(params: ExcPageParams, pageSuffix: string = defaultPageSuffix): Observable<ExcPage<T>> {
        return this.http.get<ExcPage<T>>(this.resourceUrl + pageSuffix, {
            params : this.defaultPageParams(params)
        } );
    }

    findOne(id: any): Observable<T> {
        return this.http.get<T>(this.params.apiUrl + id);
    }

    defaultPageParams(pp: ExcPageParams): HttpParams {
        const params = new HttpParams()
            .set('sort', pp.pageSorting ? pp.pageSorting.orderString() : null)
            .set('page', pp.pageSize && pp.pageSize.page ? pp.pageSize.page.toString() : null)
            .set('size', pp.pageSize && pp.pageSize.size ? pp.pageSize.size.toString() : null)
            .set('searchString', pp.searchString ? pp.searchString.toString() : null);

        if (pp.extraParams) {
            Object.keys(pp.extraParams).forEach((k) => params.set(k, pp.extraParams[k]));
        }

        return params;
    }
}
