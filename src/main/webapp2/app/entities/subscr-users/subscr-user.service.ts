import { Injectable } from '@angular/core';
import { SubscrUser } from './subscr-user.model';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable()
export class SubscrUserService extends ExcAbstractService<SubscrUser> {

    constructor(http: HttpClient) {
        super({apiUrl: 'api/subscr-users/'}, http);
     }

     checkUserNotTaken(username: string) {
        return this.http.get('/api/subscr-users/check', {params: new HttpParams().set('username', username)})
            .delay(1000)
            .map((res) => res['result']);
     }

}
