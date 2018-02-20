import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhipsterSharedModule } from '../shared';
import { OrganizationsComponent } from './organizations/organizations.component';
import { OrganizationsWidgetComponent } from './organizations/organizations-widget.component';
import { OrganizationsService } from './organizations/organizations.service';

import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CardModule } from 'primeng/card';

import { entityState } from './';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        JhipsterSharedModule,
        RouterModule.forChild(entityState),
        MatTableModule,
        MatButtonModule,
        MatIconModule,
        CardModule
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [
        OrganizationsComponent,
        OrganizationsWidgetComponent
    ],
    entryComponents: [
    ],
    providers: [
        OrganizationsService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    exports: [
        OrganizationsWidgetComponent
    ]
})
export class PortalEntityModule {}
