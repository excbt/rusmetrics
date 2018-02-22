import { Route } from '@angular/router';

import { OrganizationsComponent } from './organizations.component';
import { OrganizationDetailComponent } from './organization-detail.component';
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
        pageTitle: 'organization.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};
