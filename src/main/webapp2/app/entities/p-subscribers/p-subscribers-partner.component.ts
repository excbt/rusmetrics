import { Component, OnDestroy } from '@angular/core';
import { ExcListFormComponent } from '../../shared-blocks/exc-list-form/exc-list-form.component';
import { PSubscriber } from './p-subscriber.model';
import { Router, ActivatedRoute } from '@angular/router';
import { PSubscriberPartnerService } from './p-subscriber.service';
import { PSubscriberDataSource } from './p-subscriber.datasource';
import { ExcListDatasourceProvider } from '../../shared-blocks/exc-list-form/exc-list-form.params';

@Component({
    selector: 'jhi-p-subscribers-partner',
    templateUrl: './p-subscribers.component.html',
    styleUrls: ['../blocks/list-form.scss']
})
export class PSubscribersPartnerComponent extends ExcListFormComponent<PSubscriber> implements OnDestroy {

    displayedColumns = ['id',  'subscriberName', 'organizationInn', 'organizationName'];

    subscriberMode = 'RMA';

    constructor(
        private service: PSubscriberPartnerService,
        router: Router,
        activatedRoute: ActivatedRoute,
    ) {
        super({modificationEventName: 'subscriberModification'}, router, activatedRoute);
    }

    getDataSourceProvider(): ExcListDatasourceProvider<PSubscriber> {
        return {getDataSource: () => new PSubscriberDataSource(this.service)};
    }

    navigateEdit() {
        if (this.selectedRowIndex) {
          this.router.navigate(['partners', this.selectedRowIndex, 'edit']);
        }
    }

    navigateBack() {
        this.router.navigate(['operator-dashboard']);
    }

}
