import { Component } from '@angular/core';
import { ExcListFormComponent, ExcListDatasourceProvider } from '../../shared-blocks/exc-list-form/exc-list-form.component';
import { PSubscriber } from './p-subscriber.model';
import { Router, ActivatedRoute } from '@angular/router';
import { PSubscriberService } from './p-subscriber.service';
import { PSubscriberDataSource } from './p-subscriber.datasource';

@Component({
    selector: 'jhi-p-subscribers',
    templateUrl: './p-subscribers.component.html',
    styleUrls: ['../blocks/list-form.scss']
})
export class PSubscribersComponent extends ExcListFormComponent<PSubscriber> {

    displayedColumns = ['id', 'subscriberName'];

    constructor(
        private pSubscriberService: PSubscriberService,
        router: Router,
        activatedRoute: ActivatedRoute,
    ) {
        super({baseUrl: 'p-subscr'}, router, activatedRoute);
    }

    getDataSourceProvider(): ExcListDatasourceProvider<PSubscriber> {
        return {getDataSource: () => new PSubscriberDataSource(this.pSubscriberService)};
    }
}
