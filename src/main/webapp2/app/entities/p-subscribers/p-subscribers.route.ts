import { Route } from '@angular/router';
import { PSubscribersComponent } from './p-subscribers.component';
import { UserRouteAccessService } from '../../shared';

export const pSubscrPartnerRoute: Route = {
    path: 'partners',
    component: PSubscribersComponent,
    data: {
        pageTitle: 'organizations.subscribers.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const pSubscrCustomerRoute: Route = {
    path: 'customers',
    component: PSubscribersComponent,
    data: {
        pageTitle: 'organizations.customer.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};
