import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { JhipsterSharedModule } from '../shared';

import { classifiesrState} from './';
import { ClassifiersDashboardComponent} from './';

@NgModule({
    imports: [
        JhipsterSharedModule,
        RouterModule.forChild(classifiesrState)
        // RouterModule.forChild([ HOME_ROUTE ]) CLASSIFIERS_HOME_ROUTE
    ],
    declarations: [
        ClassifiersDashboardComponent
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NmkClassifiersModule {}
