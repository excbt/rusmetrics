import { Route } from '@angular/router';

import { OrganizationDetailComponent } from './organization-detail.component';
import { OrganizationEditComponent } from './organization-edit.component';
import { OrganizationEditGComponent } from './organization-edit-g.component';
import { UserRouteAccessService } from '../../shared';
import { OrganizationsGComponent } from './organizations-g.component';

export const organizationsGRoute: Route = {
    path: 'organizations-g',
    component: OrganizationsGComponent,
    data: {
        pageTitle: 'organizations.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const organizationDetailRoute: Route = {
    path: 'organizations/:id',
    component: OrganizationDetailComponent,
    data: {
        pageTitle: 'organization.detail.title'
    },
    canActivate: [UserRouteAccessService]
};

export const organizationEditRoute: Route = {
    path: 'organizations/:id/edit',
    component: OrganizationEditComponent,
    data: {
        pageTitle: 'organization.edit.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const organizationEditGRoute: Route = {
    path: 'organizations/:id/edit-g',
    component: OrganizationEditGComponent,
    data: {
        pageTitle: 'organization.edit.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const organizationNewRoute: Route = {
    path: 'organizations/new/edit',
    component: OrganizationEditComponent,
    data: {
        pageTitle: 'organization.edit.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};
