import { Routes } from '@angular/router';
import { operatorDashboardRoute } from './operator-dashboard/operator-dashboard.route';
import { UserRouteAccessService } from '../shared';

const OPERATOR_ROUTES = [
    operatorDashboardRoute
];

export const operatorState: Routes = [{
    path: '',
    data: {
        authorities: ['ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService],
    children: OPERATOR_ROUTES
}];
