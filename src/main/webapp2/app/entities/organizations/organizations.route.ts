import { Route } from '@angular/router';

import { OrganizationsComponent } from './organizations.component';
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
