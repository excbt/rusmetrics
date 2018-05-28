import { ExcAbstractPageDataSource, ExcPageParams } from '../../shared-blocks';
import { SubscrObjectTree } from './subscr-object-tree.model';
import { SubscrObjectTreeService } from './subscr-object-tree.service';

export class SubscrObjectTreeDataSource extends ExcAbstractPageDataSource<SubscrObjectTree> {

    constructor( private deviceModelService: SubscrObjectTreeService) {
        super();
    }

    findPage(pageParams: ExcPageParams) {
        this.wrapPageService(this.deviceModelService.findContObjectType1(pageParams));
    }

}
