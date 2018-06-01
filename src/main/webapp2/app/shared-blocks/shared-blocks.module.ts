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
  MatAutocompleteModule,
  MatSlideToggleModule,
  MatToolbarModule,
  MatListModule,
  MatSelectModule,
  MatTooltipModule
  // ErrorStateMatcher,
  // ShowOnDirtyErrorStateMatcher
} from '@angular/material';

import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { TreeTableModule } from 'primeng/treetable';
import { ContextMenuModule } from 'primeng/contextmenu';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { TableModule } from 'primeng/table';
import { ConfirmDialogModule } from 'primeng/confirmdialog';

// 3rd Party Modules
import { AngularSplitModule } from 'angular-split';
import { ExcToolbarComponent } from './exc-toolbar/exc-toolbar.component';
import { ExcSearchInputComponent } from './exc-form-menu/exc-search-input.component';

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
      MatAutocompleteModule,
      MatSlideToggleModule,
      MatToolbarModule,
      MatListModule,
      // PRIME NG
      CardModule,
      InputTextModule,
      MenubarModule,
      ButtonModule,
      CheckboxModule,
      AutoCompleteModule,
      TreeTableModule,
      ContextMenuModule,
      ScrollPanelModule,
      TableModule,
      ConfirmDialogModule,
      // 3rd party modules
      AngularSplitModule
  ],
  declarations: [
    ExcEditButtonsComponent,
    ExcFormTemplateComponent,
    ExcFormDetailFieldComponent,
    ExcTopHeaderComponent,
    ExcListFormMenuComponent,
    ExcEditFormMenuComponent,
    ExcSearchFieldComponent,
    ExcToolbarComponent,
    ExcSearchInputComponent
  ],
  entryComponents: [
  ],
  providers: [
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  exports: [
    ExcSearchInputComponent,
    ExcEditButtonsComponent,
    ExcFormTemplateComponent,
    ExcFormDetailFieldComponent,
    ExcTopHeaderComponent,
    ExcListFormMenuComponent,
    ExcEditFormMenuComponent,
    ExcSearchFieldComponent,
    ExcToolbarComponent,
    ExcSearchInputComponent,
    // NG Material module
    MatToolbarModule,
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
    MatSlideToggleModule,
    // Prime NG mudule
    TreeTableModule,
    ContextMenuModule,
    OverlayPanelModule,
    ButtonModule,
    CheckboxModule,
    CardModule,
    InputTextModule,
    MenubarModule,
    ScrollPanelModule,
    TableModule,
    ConfirmDialogModule,
    // 3rd party modules
    AngularSplitModule
  ]
})
export class PortalSharedBlocksModule {}
