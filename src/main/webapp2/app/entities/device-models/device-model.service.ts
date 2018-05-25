import { Injectable } from '@angular/core';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';
import { DeviceModel } from './device-model.model';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class DeviceModelService extends ExcAbstractService<DeviceModel> {

    displayedColumns = ['id', 'deviceName', 'deviceModelName', 'modelDriverType'];

    constructor(http: HttpClient) {
        super({apiUrl: 'api/device-models/'}, http);
    }

}
