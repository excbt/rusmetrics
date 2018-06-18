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
  MatTooltipModule,
  MatMenuModule
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
import { DropdownModule } from 'primeng/dropdown';
import { DragDropModule } from 'primeng/dragdrop';

// 3rd Party Modules
import { AngularSplitModule } from 'angular-split';
import { ExcToolbarComponent } from './exc-toolbar/exc-toolbar.component';
import { ExcSearchInputComponent } from './exc-form-menu/exc-search-input.component';
import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';
import { PERFECT_SCROLLBAR_CONFIG } from 'ngx-perfect-scrollbar';
import { PerfectScrollbarConfigInterface } from 'ngx-perfect-scrollbar';
import { ExcFrameComponent } from './exc-form-template/exc-frame.component';
import { TreeModule } from 'primeng/tree';
import { BuildingTypeDecoderService } from './exc-ui-tools/building-type-decoder.service';
import { BuildingTypeIconComponent } from './exc-ui-tools/building-type-icon.component';

const DEFAULT_PERFECT_SCROLLBAR_CONFIG: PerfectScrollbarConfigInterface = {
  suppressScrollX: true
};

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
      MatMenuModule,
      // PRIME NG
      CardModule,
      InputTextModule,
      MenubarModule,
      ButtonModule,
      CheckboxModule,
      AutoCompleteModule,
      TreeTableModule,
      TreeModule,
      ContextMenuModule,
      ScrollPanelModule,
      TableModule,
      ConfirmDialogModule,
      // 3rd party modules
      AngularSplitModule,
      PerfectScrollbarModule
  ],
  declarations: [
    ExcFrameComponent,
    ExcEditButtonsComponent,
    ExcFormTemplateComponent,
    ExcFormDetailFieldComponent,
    ExcTopHeaderComponent,
    ExcListFormMenuComponent,
    ExcEditFormMenuComponent,
    ExcSearchFieldComponent,
    ExcToolbarComponent,
    ExcSearchInputComponent,
    BuildingTypeIconComponent
  ],
  entryComponents: [
  ],
  providers: [
    {
      provide: PERFECT_SCROLLBAR_CONFIG,
      useValue: DEFAULT_PERFECT_SCROLLBAR_CONFIG
    },
    BuildingTypeDecoderService
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  exports: [
    ExcFrameComponent,
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
    BuildingTypeIconComponent,
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
    MatMenuModule,
    // Prime NG mudule
    TreeModule,
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
    DropdownModule,
    DragDropModule,
    // 3rd party modules
    AngularSplitModule,
    PerfectScrollbarModule
  ]
})
export class PortalSharedBlocksModule {}
