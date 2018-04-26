import { PSubscriber } from './p-subscriber.model';
import { ExcAbstractDataSource, ExcPageSorting, ExcPageSize, ExcPage, ExcPageParams } from '../../shared-blocks';
import { PSubscriberService } from './p-subscriber.service';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';

export class PSubscriberDataSource extends ExcAbstractDataSource<PSubscriber> {

    constructor( private pSubscriberService: ExcAbstractService<PSubscriber>) {
        super();
    }

    findPage(pageParams: ExcPageParams) {
        this.wrapPageService(this.pSubscriberService.findPage(pageParams));
    }
}
