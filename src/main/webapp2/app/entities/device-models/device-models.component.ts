import { Component } from '@angular/core';
import { ExcListFormComponent } from '../../shared-blocks/exc-list-form/exc-list-form.component';
import { DeviceModel } from './device-model.model';
import { DeviceModelService } from './device-model.service';
import { Router, ActivatedRoute } from '@angular/router';
import { DeviceModelDataSource } from './device-model.datasource';
import { ExcListDatasourceProvider } from '../../shared-blocks/exc-list-form/exc-list-form.params';

@Component({
    selector: 'jhi-device-models',
    templateUrl: './device-models.component.html',
    styleUrls: ['../blocks/list-form.scss']
})
export class DeviceModelsComponent extends ExcListFormComponent<DeviceModel> {

    displayedColumns = ['id', 'modelName', 'caption'];

    constructor(
        private deviceModelService: DeviceModelService,
        router: Router,
        activatedRoute: ActivatedRoute,
    ) {
        super({modificationEventName: 'deviceModelModification'}, router, activatedRoute);
    }

    getDataSourceProvider(): ExcListDatasourceProvider<DeviceModel> {
        return {getDataSource: () => new DeviceModelDataSource(this.deviceModelService)};
    }

}
