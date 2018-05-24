import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { JhipsterSharedModule } from '../shared';
import { PortalSharedBlocksModule } from '../shared-blocks';
import { PortalEntityModule } from '../entities/entity.module';
import { RouterModule } from '@angular/router';
import { OperatorDashboardComponent } from './operator-dashboard/operator-dashboard.component';
import { operatorState } from './operator.route';
import { OperatorAccessManagementObjectsComponent } from './access-management/access-management-objects.component';
import { OperatorAccessManagementWidgetComponent } from './access-management/access-management-widget.component';

@NgModule({
    imports: [
        JhipsterSharedModule,
        PortalSharedBlocksModule,
        PortalEntityModule,
        RouterModule.forChild(operatorState)
    ],
    declarations: [
        OperatorDashboardComponent,
        OperatorAccessManagementObjectsComponent,
        OperatorAccessManagementWidgetComponent
    ],
    entryComponents: [
    ],
    providers: [
    ],
    exports: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OperatorModule {}
