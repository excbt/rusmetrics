import { Component } from '@angular/core';
import { DeviceModel } from './device-model.model';
import { DeviceModelService } from './device-model.service';
import { Router, ActivatedRoute } from '@angular/router';
import { DeviceModelDataSource } from './device-model.datasource';
import { ExcListDatasourceProvider } from '../../shared-blocks/exc-list-form/exc-list-form.params';
import { ExcListForm2Component } from '../../shared-blocks/exc-list-form/exc-list-form.component.2';

@Component({
    selector: 'jhi-device-models',
    templateUrl: './device-models.component.2.html',
    styleUrls: ['../blocks/list-form.scss']
})
export class DeviceModels2Component extends ExcListForm2Component<DeviceModel> {

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

    highlightRow(data) {
        this.selectedRowIndex = data.id;
        this.selectedRowData = data;
    }

}
