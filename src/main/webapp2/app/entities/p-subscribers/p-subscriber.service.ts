import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PSubscriber } from './p-subscriber.model';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class PSubscriberService extends ExcAbstractService<PSubscriber> {

    constructor(http: HttpClient) {
        super({apiUrl: 'api/subscribers/'}, http);
    }

     findManageList(): Observable<PSubscriber[]> {
        return this.http.get<PSubscriber[]>(this.resourceUrl +  'manage-list');
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
