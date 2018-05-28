import 'chart.js/dist/Chart.js';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { JhipsterSharedModule } from '../../shared';
import { PortalSharedBlocksModule } from '../../shared-blocks';
// import { PortalObjectTreeComponent } from './object-tree.component';
import { PTreeNodeService } from './object-tree.service';
import { PTreeNodeLinkedObjectService } from './object-tree.service';
import { RouterModule, Router } from '@angular/router';
import { ChartModule } from 'primeng/chart';

// import { UserRouteAccessService } from '../../shared';

import { objectTreeState,
        PortalObjectTreeComponent,
        TreeNavigateComponent,
        TreeNavigateService,
        SubscrTreeService,
        SubscrPrefService,
        PTreeNodeMonitorService,
        TreeNodeInformationContainerComponent,
        TreeNodeInformationComponent,
        TreeNodeColorStatusService,
        TreeNodeColorStatusComponent
       } from './';

import { ResizableModule } from 'angular-resizable-element';

import { AngularSplitModule } from 'angular-split';

import { TreeModule } from 'primeng/tree';
// Angular Material
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';

@NgModule({
    imports: [
        JhipsterSharedModule,
        PortalSharedBlocksModule,
        RouterModule.forChild(objectTreeState),
        ResizableModule,
        AngularSplitModule,
        TreeModule,
        MatToolbarModule,
        MatButtonModule,
        MatIconModule,
        MatMenuModule,
        ChartModule
    ],
    declarations: [
        PortalObjectTreeComponent,
        TreeNavigateComponent,
        TreeNodeInformationContainerComponent,
        TreeNodeInformationComponent,
        TreeNodeColorStatusComponent
    ],
    entryComponents: [
    ],
    providers: [
        PTreeNodeService,
        PTreeNodeLinkedObjectService,
        TreeNavigateService,
        SubscrTreeService,
        SubscrPrefService,
        PTreeNodeMonitorService,
        TreeNodeColorStatusService
    ],
    exports: [
        PortalObjectTreeComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PortalObjectTreeModule {

    constructor(router: Router) {
        console.log('Router: ', router);
    }
}
