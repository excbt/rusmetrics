import { Route } from '@angular/router';

import { OrganizationsComponent } from './organizations.component';
import { OrganizationDetailComponent } from './organization-detail.component';
import { OrganizationEditComponent } from './organization-edit.component';
import { UserRouteAccessService } from '../../shared';

export const organizationsRoute: Route = {
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
        pageTitle: 'organization.detail',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
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
