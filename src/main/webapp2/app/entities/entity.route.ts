import { Routes } from '@angular/router';
import {
    organizationsGRoute,
    organizationDetailRoute,
    organizationEditRoute,
    organizationEditGRoute,
    organizationNewRoute
 } from './organizations/organizations.route';

const ENTITY_ROUTES = [
    organizationsGRoute,
    organizationDetailRoute,
    organizationEditRoute,
    organizationEditGRoute,
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
