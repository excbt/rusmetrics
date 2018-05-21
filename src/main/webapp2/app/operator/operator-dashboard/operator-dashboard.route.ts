import { Route } from '@angular/router';
import { OperatorDashboardComponent } from './operator-dashboard.component';

export const operatorDashboardRoute: Route = {
    path: 'operator-dashboard',
    component: OperatorDashboardComponent,
    data: {
        pageTitle: 'classifiers.title'
    }
};
