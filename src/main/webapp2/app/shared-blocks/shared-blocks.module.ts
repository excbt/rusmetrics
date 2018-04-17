import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
// import { RouterModule } from '@angular/router';
import { JhipsterSharedModule } from '../shared';

import { ExcEditButtonsComponent } from './exc-edit-buttons/exc-edit-buttons.component';
import { ExcFormTemplateComponent } from './exc-form-template/exc-form-template.component';
import { ExcTopHeaderComponent } from './exc-top-header/exc-top-header.component';
import { ExcFormDetailFieldComponent } from './exc-form-detail-field/exc-form-detail-field.component';
import { ExcListFormMenuComponent } from './exc-form-menu/exc-list-form-menu.component';
import { ExcEditFormMenuComponent } from './exc-form-menu/exc-edit-form-menu.component';
import { ExcSearchFieldComponent } from './exc-search-field/exc-search-field.component';
// import { ExcEditFormComponent } from './exc-edit-form/exc-edit-form.component';

// import { ExcCustomValidators, ExcFormControlChecker } from './exc-validators/exc.validators';

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
  // ErrorStateMatcher,
  // ShowOnDirtyErrorStateMatcher
} from '@angular/material';

import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/button';

@NgModule({
  imports: [
      BrowserAnimationsModule,
      JhipsterSharedModule,
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
      // PRIME NG
      CardModule,
      InputTextModule,
      MenubarModule,
      ButtonModule
  ],
  declarations: [
    ExcEditButtonsComponent,
    ExcFormTemplateComponent,
    ExcFormDetailFieldComponent,
    ExcTopHeaderComponent,
    ExcListFormMenuComponent,
    ExcEditFormMenuComponent,
    ExcSearchFieldComponent
  ],
  entryComponents: [
  ],
  providers: [
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  exports: [
    ExcEditButtonsComponent,
    ExcFormTemplateComponent,
    ExcFormDetailFieldComponent,
    ExcTopHeaderComponent,
    ExcListFormMenuComponent,
    ExcEditFormMenuComponent,
    ExcSearchFieldComponent,
    // NG module
    ButtonModule
  ]
})
export class PortalSharedBlocksModule {}
