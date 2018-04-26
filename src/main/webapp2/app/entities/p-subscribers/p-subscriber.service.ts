import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PSubscriber } from './p-subscriber.model';
import { ExcAbstractService, defaultPageSuffix } from '../../shared-blocks/exc-tools/exc-abstract-service';
import { ExcPageParams, ExcPage, ExcEditFormEntityProvider } from '../../shared-blocks';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class PSubscriberService extends ExcAbstractService<PSubscriber> {

    constructor(http: HttpClient) {
        super({apiUrl: 'api/subscribers/'}, http);
     }

     findPageMode(pageParams: ExcPageParams, subscriberMode: string): Observable<ExcPage<PSubscriber>> {
        return this.http.get<ExcPage<PSubscriber>>(this.resourceUrl +  defaultPageSuffix, {
            params : this.defaultPageParams(pageParams).set('subscriberMode', subscriberMode)
        } );
    }
}

@Injectable()
export class PSubscriberPartnerService extends ExcAbstractService<PSubscriber> {

    constructor(http: HttpClient) {
        super({apiUrl: 'api/subscribers/rma/'}, http);
     }
}

@Injectable()
export class PSubscriberCustomerService extends ExcAbstractService<PSubscriber> {

    constructor(http: HttpClient) {
        super({apiUrl: 'api/subscribers/normal/'}, http);
     }

}
