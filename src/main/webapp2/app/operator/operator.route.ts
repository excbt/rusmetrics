import { Routes } from '@angular/router';
import { operatorDashboardRoute } from './operator-dashboard/operator-dashboard.route';
import { UserRouteAccessService } from '../shared';
import { operatorAccessManagementRoute } from './access-management/access-management.route';

const OPERATOR_ROUTES = [
    operatorDashboardRoute,
    operatorAccessManagementRoute
];

export const operatorState: Routes = [{
    path: '',
    data: {
        authorities: ['ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService],
    children: OPERATOR_ROUTES
}];
