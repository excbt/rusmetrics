import { Component, OnDestroy } from '@angular/core';
import { ExcListFormComponent, ExcListDatasourceProvider } from '../../shared-blocks/exc-list-form/exc-list-form.component';
import { SubscrUser } from './subscr-user.model';
import { SubscrUserService } from './subscr-user.service';
import { Router, ActivatedRoute } from '@angular/router';
import { SubscrUserDataSource } from './subscr-user.datasource';

@Component({
    selector: 'jhi-subscr-users',
    templateUrl: './subscr-users.component.html',
    styleUrls: ['../blocks/list-form.scss']
})
export class SubscrUserComponent extends ExcListFormComponent<SubscrUser> implements OnDestroy {

    constructor(
        private subscrUserService: SubscrUserService,
        router: Router,
        activatedRoute: ActivatedRoute,
    ) {
        super({modificationEventName: 'subscrUserModification'}, router, activatedRoute);
    }

    getDataSourceProvider(): ExcListDatasourceProvider<SubscrUser> {
        return {getDataSource: () => new SubscrUserDataSource(this.subscrUserService)};
    }

}
