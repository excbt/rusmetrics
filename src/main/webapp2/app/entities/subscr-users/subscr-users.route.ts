import { Route } from '@angular/router';
import { SubscrUsersComponent } from './subscr-users.component';
import { UserRouteAccessService } from '../../shared';

export const subscrUsersRoute: Route = {
    path: 'subscr-users',
    component: SubscrUsersComponent,
    data: {
        pageTitle: 'subscrUsers.main.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};
