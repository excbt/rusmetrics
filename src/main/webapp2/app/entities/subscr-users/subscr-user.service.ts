import { Injectable } from '@angular/core';
import { SubscrUser } from './subscr-user.model';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { ExcEditFormEntityProvider } from '../../shared-blocks';

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

     update(data: SubscrUser): Observable<SubscrUser> {
         if (data.id) {
            return this.http.put<SubscrUser>(this.resourceUrl, data, {params: this.passwordParams(data)});
         } else {
            return this.http.post<SubscrUser>(this.resourceUrl, data, {params: this.passwordParams(data)});
         }
    }

    passwordParams(data: SubscrUser): HttpParams {
        let params = new HttpParams()
        .set('newPassword', data.newPassword ? data.newPassword : '');
        if (data.oldPassword) {
            params = params.set('oldPassword', data.oldPassword);
        }
        return params;
    }

    userEntityProvider(): ExcEditFormEntityProvider<SubscrUser> {
        return {
            load: (id) => this.findOne(id),
            update: (data) => this.update(data),
            delete: (id) => this.delete(id)
        };
    }

}
