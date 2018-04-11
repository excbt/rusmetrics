import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { JhipsterSharedModule } from '../shared';

import { ExcEditButtonsComponent } from './exc-edit-buttons/exc-edit-buttons.component';
import { ExcFormTemplateComponent } from './exc-form-template/exc-form-template.component';
import { ExcTopHeaderComponent } from './exc-top-header/exc-top-header.component';
import { ExcFormDetailFieldComponent } from './exc-form-detail-field/exc-form-detail-field.component';
import { ExcCustomValidators, ExcFormControlChecker } from './exc-validators/exc.validators';

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
  ],
  declarations: [
    ExcEditButtonsComponent,
    ExcFormTemplateComponent,
    ExcFormDetailFieldComponent,
    ExcTopHeaderComponent
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
    ExcTopHeaderComponent
  ]
})
export class PortalSharedBlocksModule {}
