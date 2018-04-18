import { Route } from '@angular/router';

import { OrganizationDetail2Component } from './organization-detail2.component';
import { OrganizationEditComponent } from './organization-edit.component';
import { UserRouteAccessService } from '../../shared';
import { OrganizationsComponent } from './organizations.component';

export const organizationsRoute: Route = {
    path: 'organizations',
    component: OrganizationsComponent,
    data: {
        pageTitle: 'organizations.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const subscrOrganizationsRoute: Route = {
    path: 'subscr/organizations',
    component: OrganizationsComponent,
    data: {
        pageTitle: 'organizations.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};

export const organizationDetailRoute: Route = {
    path: 'organizations/:id',
    component: OrganizationDetail2Component,
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

export const subscrOrganizationEditRoute: Route = {
    path: 'subscr/organizations/:id/edit',
    component: OrganizationEditComponent,
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
