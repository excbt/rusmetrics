import { Routes } from '@angular/router';

import {
    classifiersDashboardRoute
} from './';

const CLASSIFIERS_ROUTES = [
    classifiersDashboardRoute
];

export const classifiersState: Routes = [{
    path: '',
    data: {
        authorities: []
    },
    canActivate: [],
    children: CLASSIFIERS_ROUTES
}];
