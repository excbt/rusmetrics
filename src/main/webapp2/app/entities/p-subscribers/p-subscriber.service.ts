import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PSubscriber } from './p-subscriber.model';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';

@Injectable()
export class PSubscriberService extends ExcAbstractService<PSubscriber> {

    constructor(http: HttpClient) {
        super({apiUrl: 'api/subscribers/'}, http);
     }

}
