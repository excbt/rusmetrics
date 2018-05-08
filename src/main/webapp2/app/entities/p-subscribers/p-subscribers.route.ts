import { Route } from '@angular/router';
import { PSubscribersComponent } from './p-subscribers.component';
import { UserRouteAccessService } from '../../shared';
import { PSubscriberEditComponent } from './p-subscriber-edit.component';
import { PSubscribersPartnerComponent } from './p-subscribers-partner.component';
import { PSubscribersCustomerComponent } from './p-subscribers-cutomer.component';

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
    component: PSubscriberEditComponent,
    data: {
        pageTitle: 'subscribers.partner.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const pSubscrCustomerEditRoute: Route = {
    path: 'customers/:id/edit',
    component: PSubscriberEditComponent,
    data: {
        pageTitle: 'subscribers.customer.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};
