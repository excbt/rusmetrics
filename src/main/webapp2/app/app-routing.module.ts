import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute, leftMainMenuRoute, headerbarRoute } from './layouts';
import { DEBUG_INFO_ENABLED } from './app.constants';

const LAYOUT_ROUTES = [
    headerbarRoute,
    leftMainMenuRoute
    ,
    ...errorRoute
];

@NgModule({
    imports: [

         RouterModule.forRoot(LAYOUT_ROUTES, { useHash: true , enableTracing: DEBUG_INFO_ENABLED })

       // RouterModule.forChild(LAYOUT_ROUTES)
        // , { useHash: true , enableTracing: DEBUG_INFO_ENABLED })
    ],
    exports: [
        RouterModule
    ]
})
export class JhipsterAppRoutingModule {}
