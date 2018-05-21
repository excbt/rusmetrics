import { Route } from '@angular/router';
import { UserRouteAccessService } from '../../shared';
import { PSubscribersPartnerComponent } from './p-subscribers-partner.component';
import { PSubscribersCustomerComponent } from './p-subscribers-cutomer.component';
import { PSubscriberEditCustomerComponent } from './p-subscriber-edit-customer.component';
import { PSubscriberEditPartnerComponent } from './p-subscriber-edit-partner.component';

export const pSubscrPartnerRoute: Route = {
    path: 'partners',
    component: PSubscribersPartnerComponent,
    data: {
        pageTitle: 'subscribers.partner.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const pSubscrCustomerRoute: Route = {
    path: 'customers',
    component: PSubscribersCustomerComponent,
    data: {
        pageTitle: 'subscribers.customer.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const pSubscrPartnerEditRoute: Route = {
    path: 'partners/:id/edit',
    component: PSubscriberEditPartnerComponent,
    data: {
        pageTitle: 'subscribers.partner.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const pSubscrCustomerEditRoute: Route = {
    path: 'customers/:id/edit',
    component: PSubscriberEditCustomerComponent,
    data: {
        pageTitle: 'subscribers.customer.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};
