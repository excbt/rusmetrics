import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { JhipsterSharedModule } from '../shared';
import { OrganizationsComponent } from './organizations/organizations.component';
import { OrganizationDetailComponent } from './organizations/organization-detail.component';
import { OrganizationsWidgetComponent } from './organizations/organizations-widget.component';
import { OrganizationsService } from './organizations/organizations.service';

import {
    MatInputModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSortModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule } from '@angular/material';

    import { CardModule } from 'primeng/card';

import { entityState } from './';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        BrowserAnimationsModule,
        JhipsterSharedModule,
        RouterModule.forChild(entityState),
        MatInputModule,
        MatPaginatorModule,
        MatProgressSpinnerModule,
        MatSortModule,
        MatTableModule,
        MatButtonModule,
        MatIconModule,
        CardModule
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [
        OrganizationsComponent,
        OrganizationDetailComponent,
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
