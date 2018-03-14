import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { JhipsterSharedModule } from '../../shared';
// import { PortalObjectTreeComponent } from './object-tree.component';
import { PTreeNodeService } from './object-tree.service';
import { PTreeNodeLinkedObjectService } from './object-tree.service';
import { RouterModule } from '@angular/router';

import { UserRouteAccessService } from '../../shared';

import { objectTreeState,
        PortalObjectTreeComponent
       } from './';

@NgModule({
    imports: [
        JhipsterSharedModule,
        RouterModule.forChild([objectTreeState])
//        RouterModule.forChild([
//            {
//                path: 'object-trees',
//                component: PortalObjectTreeComponent,
//                data: {
//                    authorities: ['ROLE_ADMIN', 'ROLE_SUBSCR_ADMIN']
//                },
//                canActivate: [UserRouteAccessService]
//            }
//        ])
    ],
    declarations: [
        PortalObjectTreeComponent
    ],
    entryComponents: [
    ],
    providers: [
        PTreeNodeService,
        PTreeNodeLinkedObjectService
    ],
    exports: [
        PortalObjectTreeComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PortalObjectTreeModule {}
