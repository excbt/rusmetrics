import { Route } from '@angular/router';
import { PSubscribersComponent } from './p-subscribers.component';
import { UserRouteAccessService } from '../../shared';

export const pSubscribersRoute: Route = {
    path: 'subscribers',
    component: PSubscribersComponent,
    data: {
        pageTitle: 'organizations.title',
        authorities: ['ROLE_SUBSCR_ADMIN', 'ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService]
};
