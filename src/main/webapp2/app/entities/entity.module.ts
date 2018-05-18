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
    ShowOnDirtyErrorStateMatcher,
    MatAutocompleteModule,
    MatToolbarModule} from '@angular/material';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatPaginatorIntl } from '@angular/material';
import { CustomMatPaginatorIntl } from './shared/custom-mat-paginator-int';

// PRIME NG
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { MenubarModule } from 'primeng/menubar';

// Etc
import { entityState } from './';
import { PSubscribersComponent } from './p-subscribers/p-subscribers.component';
import { PSubscribersWidgetComponent } from './p-subscribers/p-subscribers-widget.component';
import { PSubscriberCustomerService, PSubscriberService } from './p-subscribers/p-subscriber.service';
import { PSubscriberPartnerService } from './p-subscribers/p-subscriber.service';
import { PSubscribersPartnerComponent } from './p-subscribers/p-subscribers-partner.component';
import { PSubscribersCustomerComponent } from './p-subscribers/p-subscribers-cutomer.component';
import { PSubscriberEditCustomerComponent } from './p-subscribers/p-subscriber-edit-customer.component';
import { PSubscriberEditPartnerComponent } from './p-subscribers/p-subscriber-edit-partner.component';
import { OrganizationAutocompleteComponent } from './organizations/organization-autocomplete.component';
import { TimezoneDefService } from './timezoneDef/timezoneDef.service';
import { SubscrUserService } from './subscr-users/subscr-user.service';
import { SubscrUsersWidgetComponent } from './subscr-users/subscr-users-widget.component';
import { SubscrUsersComponent } from './subscr-users/subscr-users.component';
import { SubscrUserEditComponent } from './subscr-users/subscr-user-edit.component';
import { StTemperatureChartWidgetComponent } from './st-plans/st-temperature-chart-widget.component';
import { SubscrAccessDashboardComponent } from './subscr-access/subscr-access-dashboard.component';
import { ContObjectAccessService } from './cont-object-access/cont-object-access.service';
import { ContObjectAccessComponent } from './cont-object-access/cont-object-access.component';
import { SubscrAccessManageComponent } from './subscr-access/subscr-access-manage.component';
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
        MatAutocompleteModule,

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
        OrganizationAutocompleteComponent,
        PSubscribersComponent,
        PSubscribersPartnerComponent,
        PSubscribersCustomerComponent,
        PSubscriberEditCustomerComponent,
        PSubscriberEditPartnerComponent,
        OrganizationsWidgetComponent,
        PSubscribersWidgetComponent,
        SubscrUsersComponent,
        SubscrUserEditComponent,
        SubscrUsersWidgetComponent,
        StTemperatureChartWidgetComponent,
        SubscrAccessDashboardComponent,
        SubscrAccessManageComponent,
        ContObjectAccessComponent
    ],
    entryComponents: [
    ],
    providers: [
        TimezoneDefService,
        OrganizationsService,
        OrganizationTypeService,
        PSubscriberService,
        PSubscriberCustomerService,
        PSubscriberPartnerService,
        SubscrUserService,
        ContObjectAccessService,
        { provide: ErrorStateMatcher, useClass: ShowOnDirtyErrorStateMatcher },
        { provide: MatPaginatorIntl, useClass: CustomMatPaginatorIntl }
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    exports: [
        OrganizationsWidgetComponent,
        PSubscribersWidgetComponent,
        SubscrUsersWidgetComponent,
        StTemperatureChartWidgetComponent
    ]
})
export class PortalEntityModule {}
