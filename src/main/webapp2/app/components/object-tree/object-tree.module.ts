import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { PortalObjectTreeComponent } from './object-tree.component';
import { PTreeNodeService } from './object-tree.service';
import { PTreeNodeLinkedObjectService } from './object-tree.service';

@NgModule({
    imports: [
    ],
    declarations: [
        PortalObjectTreeComponent
    ],
    entryComponents: [
    ],
    providers: [
        PTreeNodeService,
        PTreeNodeLinkedObjectService
    ],
    exports: [
        PortalObjectTreeComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PortalObjectTreeModule {}
