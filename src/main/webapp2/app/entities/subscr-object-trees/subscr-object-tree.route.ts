import { Route } from '@angular/router';
import { UserRouteAccessService } from '../../shared';
import { SubscrObjectTreeManageComponent } from './subscr-object-tree-manage.component';

export const subscrObjectTreeManageRoute: Route = {
    path: 'subscr-object-trees-manage',
    component: SubscrObjectTreeManageComponent,
    data: {
        pageTitle: 'subscrObjectTree.captions.title'
    },
    canActivate: [UserRouteAccessService]
};
