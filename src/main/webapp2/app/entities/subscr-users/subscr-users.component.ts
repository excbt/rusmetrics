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
export class SubscrUsersComponent extends ExcListFormComponent<SubscrUser> implements OnDestroy {

    displayedColumns = ['select', 'id', 'userName'];

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

    navigateEdit() {
        if (!this.selection.isEmpty()) {
            // this.router.navigate([this.router.url, entityId, 'edit']);
            // console.log('route:' + ['subscr-users', this.selection.selected[0].id, 'edit']);
            this.router.navigate(['subscr-users', this.selection.selected[0].id, 'edit']);
            // super.navigateEdit(this.selection.selected[0].id);
        }
      }

}
