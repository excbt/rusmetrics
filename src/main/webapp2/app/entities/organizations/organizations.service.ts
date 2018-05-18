import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { Organization } from './organization.model';
import { ExcPage, ExcPageParams } from '../../shared-blocks';
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

}
