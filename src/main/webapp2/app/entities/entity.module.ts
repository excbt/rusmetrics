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
import { OrganizationTypeService } from './organization-types/organization-type.service';
import { TopHeaderComponent, FormEditButtonsComponent } from './blocks/form-blocks';

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
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';

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
        MatCheckboxModule,
        MatSelectModule,
        MatTooltipModule
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [
        OrganizationsComponent,
        OrganizationDetailComponent,
        OrganizationEditComponent,
        OrganizationsWidgetComponent,
        TopHeaderComponent,
        FormEditButtonsComponent
    ],
    entryComponents: [
    ],
    providers: [
        OrganizationsService,
        OrganizationTypeService,
        {provide: ErrorStateMatcher, useClass: ShowOnDirtyErrorStateMatcher}
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    exports: [
        OrganizationsWidgetComponent
    ]
})
export class PortalEntityModule {}
