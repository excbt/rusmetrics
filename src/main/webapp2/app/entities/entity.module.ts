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
import {
    FormListTemplateComponent,
    } from './blocks/form-blocks';

import { FormSearchComponent } from './blocks/form-search.component';
import { FormListMenuComponent } from './blocks/form-list-menu.component';

import { PortalSharedBlocksModule } from '../shared-blocks/shared-blocks.module';

import {
    MatGridListModule,
    MatInputModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    MatSortModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCheckboxModule,
    ErrorStateMatcher,
    ShowOnDirtyErrorStateMatcher } from '@angular/material';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatPaginatorIntl } from '@angular/material';
import { ruPaginatorIntl } from './shared/ru-paginator-intl';
import { CustomMatPaginatorIntl } from './shared/custom-mat-paginator-int';

// PRIME NG
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { MenubarModule } from 'primeng/menubar';
import { MenuItem } from 'primeng/api';

// Etc
import { entityState } from './';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        BrowserAnimationsModule,
        JhipsterSharedModule,
        PortalSharedBlocksModule,
        RouterModule.forChild(entityState),
        FormsModule,
        ReactiveFormsModule,
        // Angular Material
        MatGridListModule,
        MatInputModule,
        MatPaginatorModule,
        MatProgressSpinnerModule,
        MatProgressBarModule,
        MatSortModule,
        MatTableModule,
        MatButtonModule,
        MatIconModule,
        MatCheckboxModule,
        MatSelectModule,
        MatTooltipModule,

        // PRIME NG
        CardModule,
        InputTextModule,
        MenubarModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [
        OrganizationsComponent,
        OrganizationDetailComponent,
        OrganizationEditComponent,
        OrganizationsWidgetComponent,
        FormListTemplateComponent,
        FormSearchComponent,
        FormListMenuComponent
    ],
    entryComponents: [
    ],
    providers: [
        OrganizationsService,
        OrganizationTypeService,
        { provide: ErrorStateMatcher, useClass: ShowOnDirtyErrorStateMatcher },
        { provide: MatPaginatorIntl, useClass: CustomMatPaginatorIntl }
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    exports: [
        OrganizationsWidgetComponent
    ]
})
export class PortalEntityModule {}
