import { Routes } from '@angular/router';
import {
    organizationsRoute,
    subscrOrganizationsRoute,
    organizationDetailRoute,
    organizationEditRoute,
    subscrOrganizationEditRoute,
    organizationNewRoute
 } from './organizations/organizations.route';

const ENTITY_ROUTES = [
    organizationsRoute,
    subscrOrganizationsRoute,
    organizationDetailRoute,
    organizationEditRoute,
    subscrOrganizationEditRoute,
    organizationNewRoute
];

export const entityState: Routes = [{
    path: '',
    data: {
        authorities: []
    },
    canActivate: [],
    children: ENTITY_ROUTES
}];
