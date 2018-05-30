import { Routes } from '@angular/router';
import { PortalObjectTreeComponent,
//        TreeNodeInformationContainerComponent,
        TreeNodeInformationComponent,
        TreeNodeColorStatusComponent,
        TreeNodeControlComponent
       } from './';
// import { TreeNodeInformationContainerComponent } from './tree-node-information-container';
// import { TreeNodeInformationComponent } from './tree-node-information';

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

// const TREE_NODE_INFORMATION_CONTAINER: Routes = [
//    {
//        path: 'tree-node-info',
//        component: TreeNodeInformationContainerComponent,
//        outlet: 'treeNodeView'
//    }
// ];
//
// const TREE_NODE_INFORMATION_CONTAINER_WIDGETS: Routes = [
//    {
//        path: 'tree-node-information',
//        component: TreeNodeInformationComponent
//    }
// ];

const OBJECT_TREE_ROUTES = [
    {
        path: 'object-trees',
        component: PortalObjectTreeComponent,
        children: [
            {
                path: 'tree-node-information',
                component: TreeNodeInformationComponent
            },
            {
                path: 'tree-node-color-status/:treeNodeId',
                component: TreeNodeColorStatusComponent
            },
            {
                path: 'tree-node-control/:treeNodeId',
                component: TreeNodeControlComponent
            },
        ]
    }
];

export const objectTreeState: Routes = [
    {
        path: '',
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_SUBSCR_ADMIN'],
            pageTitle: 'objectTree.title'
        },
        canActivate: [UserRouteAccessService],
        children: OBJECT_TREE_ROUTES
    }
];

// export const objectTreeState: Routes = [{
//    path: 'object-trees',
//    component: PortalObjectTreeComponent,
//    data: {
//        authorities: ['ROLE_ADMIN', 'ROLE_SUBSCR_ADMIN'],
//        pageTitle: 'objectTree.title'
//    },
//    canActivate: [UserRouteAccessService],
//    children: [
//        {
//            path: 'tree-node-information',
//            component: TreeNodeInformationComponent,
//            children: [
//                {
//                    path: '',
//                    redirectTo: 'tree-node-information',
//                    pathMatch: 'full'
//                }
//            ]
//        },
//        {
//            path: '',
//            redirectTo: 'object-trees',
//            pathMatch: 'full'
//        }
//    ]
// }];

//
// export const objectTreeState: Routes = [{
//    path: 'object-trees',
//    component: PortalObjectTreeComponent,
//    data: {
//        authorities: ['ROLE_ADMIN', 'ROLE_SUBSCR_ADMIN'],
//        pageTitle: 'objectTree.title'
//    },
//    canActivate: [UserRouteAccessService],
//    children: [
//        {
//            path: '',
//            redirectTo: '/object-trees',
//            pathMatch: 'full'
//        },
//        {
//            path: 'tree-node-information',
//            component: TreeNodeInformationComponent,
//            children: [
//                {
//                    path: '',
//                    redirectTo: 'tree-node-information',
//                    pathMatch: 'full'
//                }
//            ]
//        }
//    ]
// }];
