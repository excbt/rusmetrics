import { ExcAbstractDataSource, ExcPageParams } from '../../shared-blocks';
import { SubscrUser } from './subscr-user.model';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';

export class SubscrUserDataSource extends ExcAbstractDataSource<SubscrUser> {

    constructor( private subscrUserService: ExcAbstractService<SubscrUser>) {
        super();
    }

    findPage(pageParams: ExcPageParams) {
        this.wrapPageService(this.subscrUserService.findPage(pageParams));
    }
}
