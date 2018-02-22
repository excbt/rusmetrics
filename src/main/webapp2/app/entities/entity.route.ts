import { Routes } from '@angular/router';
import { organizationsRoute} from './organizations/organizations.route';
import { organizationDetailRoute} from './organizations/organizations.route';
// import { UserRouteAccessService } from '../shared';

const ENTITY_ROUTES = [
    organizationsRoute,
    organizationDetailRoute
];

export const entityState: Routes = [{
    path: '',
    data: {
        authorities: []
    },
    canActivate: [],
    children: ENTITY_ROUTES
}];
