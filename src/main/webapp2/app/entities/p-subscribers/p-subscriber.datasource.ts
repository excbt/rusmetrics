import { PSubscriber } from './p-subscriber.model';
import { ExcAbstractDataSource, ExcPageSorting, ExcPageSize, ExcPage } from '../../shared-blocks';
import { PSubscriberService } from './p-subscriber.service';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

export class PSubscriberDataSource extends ExcAbstractDataSource<PSubscriber> {

    constructor( private pSubscriberService: PSubscriberService) {
        super();
    }

    findSearchPage(pageSorting: ExcPageSorting, pageSize: ExcPageSize, searchString?: string) {
        this.wrapPageService(this.pSubscriberService.findSearchPage(pageSorting, pageSize, searchString));
    }
}
