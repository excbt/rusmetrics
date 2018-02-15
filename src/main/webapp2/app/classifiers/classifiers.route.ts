import { Routes } from '@angular/router';

import {
    classifiersDashboardRoute
} from './';

const CLASSIFIERS_ROUTES = [
    classifiersDashboardRoute
];

export const classifiesrState: Routes = [{
    path: '',
    data: {
        authorities: []
    },
    canActivate: [],
    children: CLASSIFIERS_ROUTES
}];
