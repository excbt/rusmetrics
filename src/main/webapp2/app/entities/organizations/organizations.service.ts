import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { Organization } from './organization.model';
import { ExcPageSize, ExcPageSorting, ExcPage, ExcPageParams } from '../../shared-blocks';
import { ExcEditFormEntityProvider } from '../../shared-blocks';
import { ExcDetailFormEntityProvider } from '../../shared-blocks/exc-detail-form/exc-detail-form.component';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';

@Injectable()
export class OrganizationsService extends ExcAbstractService<Organization> {

    constructor(http: HttpClient) {
        super({apiUrl: 'api/organizations/'}, http);
     }

     findPageSubscriber(pageParams: ExcPageParams, subscrMode: boolean): Observable<ExcPage<Organization>> {
        return this.http.get<ExcPage<Organization>>(this.resourceUrl + 'page/', {
            params : this.defaultPageParams(pageParams, {subscriberMode: subscrMode.toString()})
        } );
    }

    entityProvider(): ExcEditFormEntityProvider<Organization> {
        return {
            load: (id) => this.findOne(id),
            update: (data) => this.update(data),
            delete: (id) => this.delete(id)
        };
    }
}
