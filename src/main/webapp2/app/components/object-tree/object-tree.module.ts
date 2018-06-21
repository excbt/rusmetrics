import 'chart.js/dist/Chart.js';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { JhipsterSharedModule } from '../../shared';
import { PortalSharedBlocksModule } from '../../shared-blocks';
// import { PortalObjectTreeComponent } from './object-tree.component';
// import { PTreeNodeService } from './object-tree.service';
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
        TreeNodeColorStatusComponent,
        TreeNodeControlService,
        TreeNodeControlComponent,
        PTreeNodeService,
        ContObjectControlService,
        ContObjectControlComponent,
        ContObjectEventService,
        ContObjectEventComponent,
        ContObjectNoticeDialogComponent,
        ContObjectNoticeMatDialogComponent,
        DateUtils,
        NoticeViewerService,
        NoticeViewerComponent
       } from './';

import { ResizableModule } from 'angular-resizable-element';

import { AngularSplitModule } from 'angular-split';
// PRimeNG
import { TreeModule } from 'primeng/tree';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { CalendarModule } from 'primeng/calendar';

// Angular Material
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTableModule, MatProgressSpinnerModule } from '@angular/material';
import { MatDialogModule } from '@angular/material/dialog';
// import { CdkTableModule } from '@angular/cdk/table';

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
        ChartModule,
        TableModule,
        MatTableModule,
        MatProgressSpinnerModule,
        MatDialogModule,
        DialogModule,
        CalendarModule
    ],
    declarations: [
        PortalObjectTreeComponent,
        TreeNavigateComponent,
        TreeNodeInformationContainerComponent,
        TreeNodeInformationComponent,
        TreeNodeColorStatusComponent,
        TreeNodeControlComponent,
        ContObjectControlComponent,
        ContObjectEventComponent,
        ContObjectNoticeDialogComponent,
        ContObjectNoticeMatDialogComponent,
        NoticeViewerComponent
    ],
    entryComponents: [
        ContObjectNoticeDialogComponent,
        ContObjectEventComponent
    ],
    providers: [
        /* PTreeNodeService, */
        PTreeNodeLinkedObjectService,
        TreeNavigateService,
        SubscrTreeService,
        SubscrPrefService,
        PTreeNodeMonitorService,
        TreeNodeColorStatusService,
        TreeNodeControlService,
        PTreeNodeService,
        ContObjectControlService,
        ContObjectEventService,
        DateUtils,
        NoticeViewerService
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
