import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { JhipsterSharedModule } from '../shared';
import { FormsModule, ReactiveFormsModule} from '@angular/forms';
import { OrganizationsComponent } from './organizations/organizations.component';
import { OrganizationDetailComponent } from './organizations/organization-detail.component';
import { OrganizationEditComponent } from './organizations/organization-edit.component';
import { OrganizationsWidgetComponent } from './organizations/organizations-widget.component';
import { OrganizationsService } from './organizations/organizations.service';

import {
    MatGridListModule,
    MatInputModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSortModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCheckboxModule,
    ErrorStateMatcher,
    ShowOnDirtyErrorStateMatcher } from '@angular/material';

    import { CardModule } from 'primeng/card';

import { entityState } from './';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        BrowserAnimationsModule,
        JhipsterSharedModule,
        RouterModule.forChild(entityState),
        FormsModule,
        ReactiveFormsModule,
        MatGridListModule,
        MatInputModule,
        MatPaginatorModule,
        MatProgressSpinnerModule,
        MatSortModule,
        MatTableModule,
        MatButtonModule,
        MatIconModule,
        CardModule,
        MatCheckboxModule
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [
        OrganizationsComponent,
        OrganizationDetailComponent,
        OrganizationEditComponent,
        OrganizationsWidgetComponent
    ],
    entryComponents: [
    ],
    providers: [
        OrganizationsService,
        {provide: ErrorStateMatcher, useClass: ShowOnDirtyErrorStateMatcher}
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    exports: [
        OrganizationsWidgetComponent
    ]
})
export class PortalEntityModule {}
