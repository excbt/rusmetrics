import { Routes } from '@angular/router';
import {classifiersDashboardRoute} from './';
import { UserRouteAccessService } from '../shared';

const CLASSIFIERS_ROUTES = [
    classifiersDashboardRoute
];

export const classifiersState: Routes = [{
    path: '',
    data: {
        authorities: ['ROLE_ADMIN', 'ROLE_SUBSCR_ADMIN']
    },
    canActivate: [UserRouteAccessService],
    children: CLASSIFIERS_ROUTES
}];
