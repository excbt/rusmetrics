import { Routes, Route } from '@angular/router';
import { PortalObjectTreeComponent } from './';
import { UserRouteAccessService } from '../../shared';

// const OBJECT_TREES_ROUTES: Routes = [
//    {
//        path: 'object-trees',
//        component: PortalObjectTreeComponent
//    }
// ];

// export const objectTreeState: Routes = [
//    {
//        path: '',
//        data: {
//            authorities: ['ROLE_ADMIN', 'ROLE_SUBSCR_ADMIN']
//        },
//        canActivate: [UserRouteAccessService],
//        children: OBJECT_TREES_ROUTES
//    }
// ];

export const objectTreeState: Routes = [{
    path: 'object-trees',
    component: PortalObjectTreeComponent,
    data: {
        authorities: ['ROLE_ADMIN', 'ROLE_SUBSCR_ADMIN']
    },
    canActivate: [UserRouteAccessService]
}];

