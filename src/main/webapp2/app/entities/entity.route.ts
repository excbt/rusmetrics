import { Routes } from '@angular/router';
import { organizationsRoute} from './organizations/organizations.route';

import { UserRouteAccessService } from '../shared';

const ENTITY_ROUTES = [
    organizationsRoute
];

export const entityState: Routes = [{
    path: '',
    data: {
        authorities: []
    },
    canActivate: [],
    children: ENTITY_ROUTES
}];
