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

    displayedColumns = ['select', 'id', 'subscriberName'];

    private routeUrlSubscription: Subscription;
    subscriberMode: string;

    constructor(
        private pSubscriberService: PSubscriberService,
        router: Router,
        activatedRoute: ActivatedRoute,
    ) {
        super({modificationEventName: 'subscriberModification'}, router, activatedRoute);
        this.routeUrlSubscription = this.activatedRoute.url.subscribe((data) => {
            if (data && (data[0].path ===  'customers')) {
                this.subscriberMode = 'NORMAL';
            }
            if (data && (data[0].path ===  'partners')) {
                this.subscriberMode = 'RMA';
            }
        });
    }

    getDataSourceProvider(): ExcListDatasourceProvider<PSubscriber> {
        return {getDataSource: () => new PSubscriberDataSource(this.pSubscriberService)};
    }

    ngOnDestroy() {
        this.routeUrlSubscription.unsubscribe();
        super.ngOnDestroy();
    }

    navigateEdit() {
        if (!this.selection.isEmpty()) {

          this.router.navigate([this.subscriberMode === 'NORMAL' ? 'customers' : 'partners', this.selection.selected[0].id, 'edit']);
        }
      }

}
