import { Route } from '@angular/router';
import { PSubscribersComponent } from './p-subscribers.component';
import { UserRouteAccessService } from '../../shared';
import { PSubscriberEditComponent } from './p-subscriber-edit.component';
import { PSubscribersPartnerComponent } from './p-subscriber-partner.component';
import { PSubscribersCustomerComponent } from './p-subscriber-customer.component';

export const pSubscrPartnerRoute: Route = {
    path: 'partners',
    component: PSubscribersPartnerComponent,
    data: {
        pageTitle: 'organizations.subscribers.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const pSubscrCustomerRoute: Route = {
    path: 'customers',
    component: PSubscribersCustomerComponent,
    data: {
        pageTitle: 'organizations.customer.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const pSubscrPartnerEditRoute: Route = {
    path: 'partners/:id/edit',
    component: PSubscriberEditComponent,
    data: {
        pageTitle: 'organizations.partner.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const pSubscrCustomerEditRoute: Route = {
    path: 'customers/:id/edit',
    component: PSubscriberEditComponent,
    data: {
        pageTitle: 'organizations.customer.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};
