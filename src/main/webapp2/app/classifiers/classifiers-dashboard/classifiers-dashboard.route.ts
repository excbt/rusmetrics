
import { Route } from '@angular/router';

import { ClassifiersDashboardComponent } from './classifiers-dashboard.component';

export const classifiersDashboardRoute: Route = {
    path: 'classifiers',
    component: ClassifiersDashboardComponent,
    data: {
        pageTitle: 'Справочники'
    }
};
