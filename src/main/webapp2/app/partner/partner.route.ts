import { Routes } from '@angular/router';
import { partnerDashboardRoute } from './partner-dashboard/partner-dashboard.route';
import { UserRouteAccessService } from '../shared';

const PARTNER_ROUTES = [
    partnerDashboardRoute
];

export const partnerState: Routes = [{
    path: '',
    data: {
        authorities: ['ROLE_RMA']
    },
    canActivate: [UserRouteAccessService],
    children: PARTNER_ROUTES
}];
