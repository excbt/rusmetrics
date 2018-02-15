import { Route } from '@angular/router';

import { HeaderbarComponent } from './headerbar.component';

export const headerbarRoute: Route = {
    path: '',
    component: HeaderbarComponent,
    outlet: 'headerbar'
};
