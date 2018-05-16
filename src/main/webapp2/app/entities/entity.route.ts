import { Routes } from '@angular/router';
import {
    organizationsRoute,
    subscrOrganizationsRoute,
    organizationDetailRoute,
    organizationEditRoute,
    subscrOrganizationEditRoute,
    organizationNewRoute
 } from './organizations/organizations.route';
import { pSubscrPartnerRoute, pSubscrCustomerRoute, pSubscrPartnerEditRoute, pSubscrCustomerEditRoute } from './p-subscribers/p-subscribers.route';
import { subscrUsersRoute, subscrUserEditRoute } from './subscr-users/subscr-users.route';
import { subscrAccessDashboardRoute } from './subscr-access/subscr-access.route';

const ENTITY_ROUTES = [
    organizationsRoute,
    subscrOrganizationsRoute,
    organizationDetailRoute,
    organizationEditRoute,
    subscrOrganizationEditRoute,
    organizationNewRoute,
    pSubscrPartnerRoute,
    pSubscrPartnerEditRoute,
    pSubscrCustomerRoute,
    pSubscrCustomerEditRoute,
    subscrUsersRoute,
    subscrUserEditRoute,
    subscrAccessDashboardRoute
];

export const entityState: Routes = [{
    path: '',
    data: {
        authorities: []
    },
    canActivate: [],
    children: ENTITY_ROUTES
}];
