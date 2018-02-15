import { Route } from '@angular/router';

import { LeftMainMenuComponent } from './layouts';

export const leftMainMenu: Route = {
    path: '',
    component: LeftMainMenuComponent,
    outlet: 'mainmenu'
};
