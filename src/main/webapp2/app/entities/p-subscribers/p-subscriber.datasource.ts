import { PSubscriber } from './p-subscriber.model';
import { AnyModelDataSource, ExcPageSorting, ExcPageSize, ExcPage } from '../../shared-blocks';
import { PSubscriberService } from './p-subscriber.service';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

export class PSubscriberDataSource extends AnyModelDataSource<PSubscriber> {

    constructor( private pSubscriberService: PSubscriberService) {
        super();
    }

    findSearchPage(pageSorting: ExcPageSorting, pageSize: ExcPageSize, searchString?: string) {
        this.wrapPageService(this.pSubscriberService.findSearchPage(pageSorting, pageSize, searchString));
    }
}
