import { Route } from '@angular/router';
import { PartnerDashboardComponent } from './partner-dashboard.component';

export const partnerDashboardRoute: Route = {
    path: 'partner-dashboard',
    component: PartnerDashboardComponent,
    data: {
        pageTitle: 'classifiers.title'
    }
};
