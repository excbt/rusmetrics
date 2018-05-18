import { PSubscriber } from './p-subscriber.model';
import { ExcAbstractDataSource, ExcPageParams } from '../../shared-blocks';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';

export class PSubscriberDataSource extends ExcAbstractDataSource<PSubscriber> {

    constructor( private pSubscriberService: ExcAbstractService<PSubscriber>) {
        super();
    }

    findPage(pageParams: ExcPageParams) {
        this.wrapPageService(this.pSubscriberService.findPage(pageParams));
    }
}
