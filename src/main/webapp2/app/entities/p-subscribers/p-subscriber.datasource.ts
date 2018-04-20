import { PSubscriber } from './p-subscriber.model';
import { ExcAbstractDataSource, ExcPageSorting, ExcPageSize, ExcPage, ExcPageParams } from '../../shared-blocks';
import { PSubscriberService } from './p-subscriber.service';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

export class PSubscriberDataSource extends ExcAbstractDataSource<PSubscriber> {

    private customerMode: boolean;

    constructor( private pSubscriberService: PSubscriberService, customerMode?: boolean) {
        super();
        this.customerMode = customerMode ? customerMode === true : false;
    }

    findPage(pageParams: ExcPageParams) {
        this.wrapPageService(this.pSubscriberService.findPage(pageParams));
    }
}
