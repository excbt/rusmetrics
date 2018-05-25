import { ExcAbstractPageDataSource, ExcPageParams } from '../../shared-blocks';
import { DeviceModel } from './device-model.model';
import { DeviceModelService } from './device-model.service';

export class DeviceModelDataSource extends ExcAbstractPageDataSource<DeviceModel> {

    constructor( private deviceModelService: DeviceModelService) {
        super();
    }

    findPage(pageParams: ExcPageParams) {
        this.wrapPageService(this.deviceModelService.findPage(pageParams));
    }

}
