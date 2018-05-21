import { ContObjectAccess } from './cont-object-access.model';
import { Injectable } from '@angular/core';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { ExcPageParams, ExcPage } from '../../shared-blocks';
import { ContZPointAccess } from './cont-zpoint-access.model';

@Injectable()
export class ContObjectAccessService extends ExcAbstractService<ContObjectAccess> {
    constructor(http: HttpClient) {
        super({apiUrl: 'api/subscr-access/'}, http);
     }

     findContZPointAccess(contObjectId: number): Observable<ContZPointAccess[]> {
        return this.http.get<ContZPointAccess[]>(this.resourceUrl + 'cont-zpoints/',
        {params: new HttpParams().set('contObjectId', contObjectId.toString())}
        );
    }

    findContObjectsPage(pageParams: ExcPageParams): Observable<ExcPage<ContObjectAccess>> {
        return this.http.get<ExcPage<ContObjectAccess>>(this.resourceUrl + 'cont-objects/page', {
            params : this.defaultPageParams(pageParams)
        } );
    }

}
