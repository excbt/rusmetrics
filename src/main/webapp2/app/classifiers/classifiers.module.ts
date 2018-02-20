import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { JhipsterSharedModule } from '../shared';

import { classifiersState} from './';
import { ClassifiersDashboardComponent} from './';

import { PortalEntityModule } from '.././entities/entity.module';
import { CardModule } from 'primeng/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
    imports: [
        JhipsterSharedModule,
        RouterModule.forChild(classifiersState),
        CardModule,
        MatButtonModule,
        MatIconModule,
        PortalEntityModule
        // RouterModule.forChild([ HOME_ROUTE ]) CLASSIFIERS_HOME_ROUTE
    ],
    declarations: [
        ClassifiersDashboardComponent
    ],
    entryComponents: [],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PortalClassifiersModule {}
