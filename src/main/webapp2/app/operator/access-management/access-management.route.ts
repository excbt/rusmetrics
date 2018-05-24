import { Route } from '@angular/router';
import { OperatorAccessManagementComponent } from './access-management.component';

export const operatorAccessManagementRoute: Route = {
    path: 'operator-access-management',
    component: OperatorAccessManagementComponent,
    data: {
        pageTitle: 'classifiers.title'
    }
};
