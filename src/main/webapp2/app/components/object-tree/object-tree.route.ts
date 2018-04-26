import { Routes } from '@angular/router';
import { PortalObjectTreeComponent, TreeNodeInformationContainerComponent } from './';
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

const TREE_NODE_INFORMATION_CONTAINER: Routes = [
    {
        path: 'tree-node-info',
        component: TreeNodeInformationContainerComponent,
        outlet: 'treeNodeView'
    }
]

export const objectTreeState: Routes = [{
    path: 'object-trees',
    component: PortalObjectTreeComponent,
    data: {
        authorities: ['ROLE_ADMIN', 'ROLE_SUBSCR_ADMIN'],
        pageTitle: 'objectTree.title'
    },
    canActivate: [UserRouteAccessService],
   /* children: [...TREE_NODE_INFORMATION_CONTAINER] */
}];
