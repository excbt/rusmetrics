import { Routes } from '@angular/router';
import { partnerDashboardRoute } from './partner-dashboard/partner-dashboard.route';

const PARTNER_ROUTES = [
    partnerDashboardRoute
];

export const partnerState: Routes = [{
    path: '',
    data: {
        authorities: ['ROLE_RMA']
    },
    canActivate: [],
    children: PARTNER_ROUTES
}];
