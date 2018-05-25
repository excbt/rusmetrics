import { Route } from '@angular/router';
import { UserRouteAccessService } from '../../shared';
import { DeviceModelsComponent } from './device-models.component';

export const deviceModelsRoute: Route = {
    path: 'device-models',
    component: DeviceModelsComponent,
    data: {
        pageTitle: 'deviceModel.listTitle'
    },
    canActivate: [UserRouteAccessService]
};
