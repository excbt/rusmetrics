import { Route } from '@angular/router';

import { LeftMainMenuComponent } from './left-main-menu.component';

export const leftMainMenuRoute: Route = {
    path: '',
    component: LeftMainMenuComponent,
    outlet: 'mainmenu'
};
