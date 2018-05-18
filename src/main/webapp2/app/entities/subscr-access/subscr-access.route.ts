import { Route } from '@angular/router';
import { SubscrAccessDashboardComponent } from './subscr-access-dashboard.component';
import { UserRouteAccessService } from '../../shared';
import { SubscrAccessManageComponent } from './subscr-access-manage.component';

export const subscrAccessDashboardRoute: Route = {
    path: 'subscr-access-dashboard',
    component: SubscrAccessDashboardComponent,
    data: {
        pageTitle: 'subscrUsers.main.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const subscrAccessManageRoute: Route = {
    path: 'subscr-access-manage',
    component: SubscrAccessManageComponent,
    data: {
        pageTitle: 'subscrUsers.main.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};
