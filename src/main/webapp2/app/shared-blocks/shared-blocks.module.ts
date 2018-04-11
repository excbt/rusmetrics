import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { JhipsterSharedModule } from '../shared';
import { ExcEditButtonsComponent } from './exc-edit-buttons/exc-edit-buttons.component';

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
    ExcEditButtonsComponent
  ],
  entryComponents: [
  ],
  providers: [
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  exports: [
    ExcEditButtonsComponent
  ]
})
export class PortalSharedBlocksModule {}
