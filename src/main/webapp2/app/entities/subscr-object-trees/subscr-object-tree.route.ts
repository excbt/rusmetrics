import { Route } from '@angular/router';
import { UserRouteAccessService } from '../../shared';
import { SubscrObjectTreeManageComponent } from './subscr-object-tree-manage.component';
import { ContObjectTreeEditComponent } from './cont-object-tree/cont-object-tree-edit.component';

export const subscrObjectTreeManageRoute: Route = {
    path: 'subscr-object-trees-manage',
    component: SubscrObjectTreeManageComponent,
    data: {
        pageTitle: 'subscrObjectTree.captions.title'
    },
    canActivate: [UserRouteAccessService]
};

export const contObjectTreeEditRoute: Route = {
    path: 'cont-object-tree-edit',
    component: ContObjectTreeEditComponent,
    data: {
        pageTitle: 'subscrObjectTree.contObjectEdit.title'
    },
    canActivate: [UserRouteAccessService]
};
