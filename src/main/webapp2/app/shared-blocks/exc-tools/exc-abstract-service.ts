import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';
import { ExcPage, ExcPageParams, ExcApiParams } from './';
import { ExcEditFormEntityProvider } from '..';
import { ServiceParams } from './exc-api-service';

export const defaultPageSuffix = 'page/';

export class ExcAbstractService<T> {

    readonly resourceUrl = SERVER_API_URL + this.params.apiUrl;

    constructor(
        readonly params: ServiceParams,
        readonly http: HttpClient) { }

    findPage(pageParams: ExcPageParams, pageSuffix: string = defaultPageSuffix): Observable<ExcPage<T>> {
        return this.http.get<ExcPage<T>>(this.resourceUrl + pageSuffix, {
            params : this.defaultPageParams(pageParams)
        } );
    }

    findPageUrl(subUrl: string, pageParams: ExcPageParams, pageSuffix: string = defaultPageSuffix): Observable<ExcPage<T>> {
        return this.http.get<ExcPage<T>>(this.resourceUrl + subUrl + pageSuffix, {
            params : this.defaultPageParams(pageParams)
        } );
    }

    findAll(): Observable<T[]> {
        return this.http.get<T[]>(this.resourceUrl);
    }

    findOne(id: any): Observable<T> {
        return this.http.get<T>(this.resourceUrl + id);
    }

    update(data: T): Observable<T> {
        return this.http.put<T>(this.resourceUrl, data);
    }

    delete(id: any): Observable<any> {
        return this.http.delete(this.resourceUrl + id);
    }

    defaultPageParams(pp: ExcPageParams, apiParams?: ExcApiParams): HttpParams {
        let params = new HttpParams()
            .set('sort', pp.pageSorting ? pp.pageSorting.orderString() : '')
            .set('page', pp.pageSize && pp.pageSize.page ? pp.pageSize.page.toString() : '')
            .set('size', pp.pageSize && pp.pageSize.size ? pp.pageSize.size.toString() : '')
            .set('searchString', pp.searchString ? pp.searchString.toString() : '');

        if (apiParams) {
            Object.keys(apiParams).forEach((k) => {
                params = params.set(k, apiParams[k]);
            });
        }

        return params;
    }

    entityProvider(): ExcEditFormEntityProvider<T> {
        return {
            load: (id) => this.findOne(id),
            update: (data) => this.update(data),
            delete: (id) => this.delete(id)
        };
    }

}
