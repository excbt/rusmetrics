import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';
import { ExcPageSize, ExcPageSorting, ExcPage } from '../../shared-blocks';
import { PSubscriber } from './p-subscriber.model';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';

@Injectable()
export class PSubscriberService extends ExcAbstractService<PSubscriber> {

    constructor(http: HttpClient) {
        super({apiUrl: 'api/subscribers/'}, http);
     }

}
