import './vendor.ts';

import { NgModule, Injector } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { Ng2Webstorage } from 'ngx-webstorage';
import { JhiEventManager } from 'ng-jhipster';

// PRIME NG
import { SidebarModule } from 'primeng/sidebar';
import { PanelModule } from 'primeng/panel';
import { ToolbarModule } from 'primeng/toolbar';

// Angular Material
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';

import { AuthExpiredInterceptor } from './blocks/interceptor/auth-expired.interceptor';
import { ErrorHandlerInterceptor } from './blocks/interceptor/errorhandler.interceptor';
import { NotificationInterceptor } from './blocks/interceptor/notification.interceptor';
import { JhipsterSharedModule, UserRouteAccessService } from './shared';
import { JhipsterAppRoutingModule } from './app-routing.module';
import { JhipsterHomeModule } from './home/home.module';
import { PortalClassifiersModule } from './classifiers/classifiers.module';
import { PortalObjectTreeModule } from './components/object-tree';
// import { JhipsterAdminModule } from './admin/admin.module';
// import { JhipsterAccountModule } from './account/account.module';
import { PortalEntityModule } from './entities/entity.module';
import { PortalSharedBlocksModule } from './shared-blocks/shared-blocks.module';

import { PaginationConfig } from './blocks/config/uib-pagination.config';
import { StateStorageService } from './shared/auth/state-storage.service';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import {
    JhiMainComponent,
    LeftMainMenuComponent,
    FooterComponent,
    ProfileService,
    MainMenuService,
    PageRibbonComponent,
    ActiveMenuDirective,
    ErrorComponent,
    HeaderbarComponent
} from './layouts';
import { PartnerModule } from './partner/partner.module';
import { OperatorModule } from './operator/operator.module';
// import { ClassifiersHomeComponent } from './classifiers/classifiers-home.component';
// import { LeftMainMenuComponent } from './layouts/left-main-menu/left-main-menu.component';

@NgModule({
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-'}),
        // UI modules
        MatMenuModule,
        // Prime NG
        SidebarModule,
        ToolbarModule,
        PanelModule,
        MatToolbarModule,
        MatButtonModule,
        MatIconModule,
        MatListModule,
        // Work modules
        JhipsterAppRoutingModule,
        JhipsterSharedModule,
        JhipsterHomeModule,
        // JhipsterAdminModule,
        // JhipsterAccountModule,
        PortalClassifiersModule,
        PortalEntityModule,
        PortalObjectTreeModule,
        PortalSharedBlocksModule,
        PartnerModule,
        OperatorModule
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [
        JhiMainComponent,
        HeaderbarComponent,
        LeftMainMenuComponent,
//        ClassifiersHomeComponent,
        ErrorComponent,
        PageRibbonComponent,
        ActiveMenuDirective,
        FooterComponent
        // MainmenuComponent
    ],
     providers: [
        MainMenuService,

        ProfileService,
        PaginationConfig,
        UserRouteAccessService,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthExpiredInterceptor,
            multi: true,
            deps: [
                StateStorageService,
                Injector
            ]
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorHandlerInterceptor,
            multi: true,
            deps: [
                JhiEventManager
            ]
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: NotificationInterceptor,
            multi: true,
            deps: [
                Injector
            ]
        }
    ],
    bootstrap: [ JhiMainComponent ]
})
export class JhipsterAppModule {}
