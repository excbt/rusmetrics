import { Component, Input, OnDestroy } from '@angular/core';
import { ExcListFormComponent, ExcListDatasourceProvider } from '../../shared-blocks/exc-list-form/exc-list-form.component';
import { PSubscriber } from './p-subscriber.model';
import { Router, ActivatedRoute } from '@angular/router';
import { PSubscriberService } from './p-subscriber.service';
import { PSubscriberDataSource } from './p-subscriber.datasource';
import { Subscription } from 'rxjs';

@Component({
    selector: 'jhi-p-subscribers',
    templateUrl: './p-subscribers.component.html',
    styleUrls: ['../blocks/list-form.scss']
})
export class PSubscribersComponent extends ExcListFormComponent<PSubscriber> implements OnDestroy {

    displayedColumns = ['id', 'subscriberName'];

    private routeUrlSubscription: Subscription;
    customerMode: boolean;

    constructor(
        private pSubscriberService: PSubscriberService,
        router: Router,
        activatedRoute: ActivatedRoute,
    ) {
        super({modificationEventName: 'subscriberModification'}, router, activatedRoute);
        this.routeUrlSubscription = this.activatedRoute.url.subscribe((data) => {
            this.customerMode = (data && (data[0].path ===  'customers'));
        });
    }

    getDataSourceProvider(): ExcListDatasourceProvider<PSubscriber> {
        return {getDataSource: () => new PSubscriberDataSource(this.pSubscriberService, this.customerMode)};
    }

    ngOnDestroy() {
        this.routeUrlSubscription.unsubscribe();
        super.ngOnDestroy();
    }
}
