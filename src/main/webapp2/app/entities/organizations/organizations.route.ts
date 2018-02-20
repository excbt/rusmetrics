import { Route } from '@angular/router';

import { OrganizationsComponent } from './organizations.component';

export const organizationsRoute: Route = {
    path: 'organizations',
    component: OrganizationsComponent,
    data: {
        pageTitle: 'organizations.title'
    }
};
