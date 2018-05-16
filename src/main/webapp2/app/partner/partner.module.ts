import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { JhipsterSharedModule } from '../shared';
import { PortalSharedBlocksModule } from '../shared-blocks';
import { PortalEntityModule } from '../entities/entity.module';
import { RouterModule } from '@angular/router';
import { partnerState } from './partner.route';
import { PartnerDashboardComponent } from './partner-dashboard/partner-dashboard.component';

@NgModule({
    imports: [
        JhipsterSharedModule,
        PortalSharedBlocksModule,
        PortalEntityModule,
        RouterModule.forChild(partnerState)
    ],
    declarations: [
        PartnerDashboardComponent
    ],
    entryComponents: [
    ],
    providers: [
    ],
    exports: [
        PartnerDashboardComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PartnerModule {}
