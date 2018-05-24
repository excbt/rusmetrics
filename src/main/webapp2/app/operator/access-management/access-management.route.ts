import { Route } from '@angular/router';
import { OperatorAccessManagementObjectsComponent } from './access-management-objects.component';

export const operatorAccessManagementRoute: Route = {
    path: 'operator-access-management-objects',
    component: OperatorAccessManagementObjectsComponent,
    data: {
        pageTitle: 'operator.contObjectAccess'
    }
};
