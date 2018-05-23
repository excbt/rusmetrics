import { ContObjectAccess } from './cont-object-access.model';
import { Injectable } from '@angular/core';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { ExcPageParams, ExcPage } from '../../shared-blocks';
import { ContZPointAccess } from './cont-zpoint-access.model';
import { PSubscriber } from '../p-subscribers/p-subscriber.model';
import { SubscriberAccessStats } from './subcriber-access-stats.model';

@Injectable()
export class ContObjectAccessService extends ExcAbstractService<ContObjectAccess> {
    constructor(http: HttpClient) {
        super({apiUrl: 'api/subscr-access/'}, http);
     }

     findContZPointAccess(subscriberId: number, contObjectId: number): Observable<ContZPointAccess[]> {
        let myParams = new HttpParams().set('contObjectId', contObjectId.toString());
        if (subscriberId) {
            myParams = myParams.set('subscriberId', subscriberId.toString());
        }
        return this.http.get<ContZPointAccess[]>(this.resourceUrl + 'cont-zpoints/', {params: myParams});
    }

    findContObjectsPage(subscriberId: number, addMode: boolean, pageParams: ExcPageParams): Observable<ExcPage<ContObjectAccess>> {

        let myParams: HttpParams = this.defaultPageParams(pageParams);
        if (subscriberId) {
            myParams = myParams.set('subscriberId', subscriberId.toString());
            myParams = myParams.set('addMode', (addMode === true).toString());
        }

        return this.http.get<ExcPage<ContObjectAccess>>(this.resourceUrl + 'cont-objects/page', {
            params : myParams
        } );
    }

    findSubscriberManageList(): Observable<PSubscriber[]> {
        return this.http.get<PSubscriber[]>(this.resourceUrl + 'subscriber-manage-list');
    }

    findAvailableContObjects(subscriberId: number): Observable<ContObjectAccess[]> {
        return this.http.get<ContObjectAccess[]>(this.resourceUrl + 'available-cont-objects',
        {params: new HttpParams().set('subscriberId', subscriberId.toString())});
    }

    grantContObjectAccess(subscriberId: number, contObjectId: number): Observable<any> {
        return this.http.put(this.resourceUrl + 'cont-objects', null, {
            params: new HttpParams().set('subscriberId', subscriberId.toString())
                    .set('contObjectId', contObjectId.toString())
                    .set('action', 'grant')
        });
    }

    revokeContObjectAccess(subscriberId: number, contObjectId: number): Observable<any> {
        return this.http.put(this.resourceUrl + 'cont-objects', null, {
            params: new HttpParams().set('subscriberId', subscriberId.toString())
                    .set('contObjectId', contObjectId.toString())
                    .set('action', 'revoke')
        });
    }

    grantContZPointAccess(subscriberId: number, contZPointId: number): Observable<any> {
        return this.http.put(this.resourceUrl + 'cont-zpoints', null, {
            params: new HttpParams().set('subscriberId', subscriberId.toString())
                    .set('contZPointId', contZPointId.toString())
                    .set('action', 'grant')
        });
    }

    revokeContZPointAccess(subscriberId: number, contZPointId: number): Observable<any> {
        return this.http.put(this.resourceUrl + 'cont-zpoints', null, {
            params: new HttpParams().set('subscriberId', subscriberId.toString())
                    .set('contZPointId', contZPointId.toString())
                    .set('action', 'revoke')
        });
    }

    getSubscriberAccessStats(subscriberId: number): Observable<SubscriberAccessStats> {
        let myParams = new HttpParams();
        if (subscriberId) {
            myParams = myParams.set('subscriberId', subscriberId.toString());
        }
        return this.http.get<SubscriberAccessStats>(this.resourceUrl + 'subscriber-access-stats', {params: myParams});
    }
}
