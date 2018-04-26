import { Component, Input, OnDestroy } from '@angular/core';
import { ExcListFormComponent, ExcListDatasourceProvider } from '../../shared-blocks/exc-list-form/exc-list-form.component';
import { PSubscriber } from './p-subscriber.model';
import { Router, ActivatedRoute } from '@angular/router';
import { PSubscriberPartnerService } from './p-subscriber.service';
import { PSubscriberDataSource } from './p-subscriber.datasource';

@Component({
    selector: 'jhi-p-subscribers-partner',
    templateUrl: './p-subscribers.component.html',
    styleUrls: ['../blocks/list-form.scss']
})
export class PSubscribersPartnerComponent extends ExcListFormComponent<PSubscriber> implements OnDestroy {

    displayedColumns = ['select', 'id', 'subscriberName'];

    subscriberMode: string;

    constructor(
        private service: PSubscriberPartnerService,
        router: Router,
        activatedRoute: ActivatedRoute,
    ) {
        super({modificationEventName: 'subscriberModification'}, router, activatedRoute);
        this.subscriberMode = 'RMA';
    }

    getDataSourceProvider(): ExcListDatasourceProvider<PSubscriber> {
        return {getDataSource: () => new PSubscriberDataSource(this.service)};
    }

}
