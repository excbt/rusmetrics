import { Routes } from '@angular/router';
import { organizationsRoute} from './organizations/organizations.route';
import { organizationDetailRoute} from './organizations/organizations.route';
import { organizationEditRoute} from './organizations/organizations.route';
import { organizationNewRoute} from './organizations/organizations.route';
// import { UserRouteAccessService } from '../shared';

const ENTITY_ROUTES = [
    organizationsRoute,
    organizationDetailRoute,
    organizationEditRoute,
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
