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
import { subscrAccessDashboardRoute, subscrAccessManageRoute } from './subscr-access/subscr-access.route';
import { UserRouteAccessService } from '../shared';
import { deviceModelsRoute } from './device-models/device-model.route';
import { subscrObjectTreeManageRoute, contObjectTreeEditRoute } from './subscr-object-trees/subscr-object-tree.route';

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
    subscrAccessDashboardRoute,
    subscrAccessManageRoute,
    deviceModelsRoute,
    subscrObjectTreeManageRoute,
    contObjectTreeEditRoute
];

export const entityState: Routes = [{
    path: '',
    data: {
        authorities: []
    },
    canActivate: [UserRouteAccessService],
    children: ENTITY_ROUTES
}];
