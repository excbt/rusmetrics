import { Injectable } from '@angular/core';
import { SubscrUser } from './subscr-user.model';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class SubscrUserService extends ExcAbstractService<SubscrUser> {

    constructor(http: HttpClient) {
        super({apiUrl: 'api/subscr-users/'}, http);
     }

}
