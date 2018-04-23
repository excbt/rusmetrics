import { PSubscriber } from './p-subscriber.model';
import { ExcAbstractDataSource, ExcPageSorting, ExcPageSize, ExcPage, ExcPageParams } from '../../shared-blocks';
import { PSubscriberService } from './p-subscriber.service';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

export class PSubscriberDataSource extends ExcAbstractDataSource<PSubscriber> {

    private subscriberMode: string;

    constructor( private pSubscriberService: PSubscriberService, subscriberMode?: string) {
        super();
        this.subscriberMode = subscriberMode ? subscriberMode : 'NORMAL';
    }

    findPage(pageParams: ExcPageParams) {
        this.wrapPageService(this.pSubscriberService.findPageMode(pageParams, this.subscriberMode));
    }
}
