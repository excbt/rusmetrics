import { Route } from '@angular/router';

import { OrganizationDetailComponent } from './organization-detail.component';
import { OrganizationEditGComponent } from './organization-edit-g.component';
import { UserRouteAccessService } from '../../shared';
import { OrganizationsComponent } from './organizations.component';

export const organizationsGRoute: Route = {
    path: 'organizations',
    component: OrganizationsComponent,
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
    component: OrganizationEditGComponent,
    data: {
        pageTitle: 'organization.edit.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};
