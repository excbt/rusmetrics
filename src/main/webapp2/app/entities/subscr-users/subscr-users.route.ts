import { Route } from '@angular/router';
import { SubscrUsersComponent } from './subscr-users.component';
import { UserRouteAccessService } from '../../shared';
import { SubscrUserEditComponent } from './subscr-user-edit.component';

export const subscrUsersRoute: Route = {
    path: 'subscr-users',
    component: SubscrUsersComponent,
    data: {
        pageTitle: 'subscrUsers.main.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const subscrUserEditRoute: Route = {
    path: 'subscr-users/:id/edit',
    component: SubscrUserEditComponent,
    data: {
        pageTitle: 'subscrUsers.edit.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};
