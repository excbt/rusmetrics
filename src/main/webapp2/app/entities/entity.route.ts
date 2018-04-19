import { Routes } from '@angular/router';
import {
    organizationsRoute,
    subscrOrganizationsRoute,
    organizationDetailRoute,
    organizationEditRoute,
    subscrOrganizationEditRoute,
    organizationNewRoute
 } from './organizations/organizations.route';
import { pSubscribersRoute } from './p-subscribers/p-subscribers.route';

const ENTITY_ROUTES = [
    organizationsRoute,
    subscrOrganizationsRoute,
    organizationDetailRoute,
    organizationEditRoute,
    subscrOrganizationEditRoute,
    organizationNewRoute,
    pSubscribersRoute
];

export const entityState: Routes = [{
    path: '',
    data: {
        authorities: []
    },
    canActivate: [],
    children: ENTITY_ROUTES
}];
