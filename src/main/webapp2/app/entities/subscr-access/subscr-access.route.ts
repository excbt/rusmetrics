import { Route } from '@angular/router';
import { SubscrAccessDashboardComponent } from './subscr-access-dashboard.component';
import { UserRouteAccessService } from '../../shared';

export const subscrAccessDashboardRoute: Route = {
    path: 'subscr-access',
    component: SubscrAccessDashboardComponent,
    data: {
        pageTitle: 'subscrUsers.main.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};
