import {ExcPageParams, ExcAbstractPageDataSource } from '../../shared-blocks';
import { ContObjectAccess } from './cont-object-access.model';
import { ContObjectAccessService } from './cont-object-access.service';

export class ContObjectAccessDataSource extends ExcAbstractPageDataSource<ContObjectAccess> {
    constructor( private contObjectAccessService: ContObjectAccessService) {
        super();
    }

    findPage(pageParams: ExcPageParams) {
        this.wrapPageService(this.contObjectAccessService.findContObjectsPage(null, pageParams));
    }

    findSubscriberPage(subscriberId: number, pageParams: ExcPageParams) {
        this.wrapPageService(this.contObjectAccessService.findContObjectsPage(subscriberId, pageParams));
    }

}
