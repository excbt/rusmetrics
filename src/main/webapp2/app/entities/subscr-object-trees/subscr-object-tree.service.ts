import { Injectable } from '@angular/core';
import { ExcAbstractService, ServiceParams, defaultPageSuffix } from '../../shared-blocks/exc-tools/exc-abstract-service';
import { HttpClient } from '@angular/common/http';
import { SubscrObjectTree } from './subscr-object-tree.model';
import { ExcPage, ExcPageParams } from '../../shared-blocks';

@Injectable()
export class SubscrObjectTreeService extends ExcAbstractService<SubscrObjectTree> {

    constructor(http: HttpClient) {
        super({apiUrl: 'api/subscr-object-trees/'}, http);
    }

    findContObjectType1(pageParams: ExcPageParams, pageSuffix: string = defaultPageSuffix) {
        return this.http.get<ExcPage<SubscrObjectTree>>(this.resourceUrl + 'contObjectTreeType1/' + pageSuffix, {
            params : this.defaultPageParams(pageParams)
        } );

    }

}
