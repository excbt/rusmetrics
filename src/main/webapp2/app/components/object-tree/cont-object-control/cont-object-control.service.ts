import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { MonitorState } from './monitor-state.model';
import { MonitorStateWrapper } from './monitor-state-wrapper.model';
import { ContObjectControl } from './cont-object-control.model';

@Injectable()
export class ContObjectControlService {

    private resourceUrl = 'api/widgets/cont-event-monitor/cont-objects/{contObjectId}/monitor-state';
    private contObjectList: number[];
    private isInited = false;

    constructor(private http: HttpClient,
                private eventManager: JhiEventManager) {}

    initSvc() {
        if (this.isInited) {
            return;
        }

        // TODO: Listen changes cont object lists
        this.eventManager.subscribe('nodeObjectsChanged', (eventData) => {
            this.contObjectList = eventData.content;
//            console.log('ContObjectControlComponent: contObjectList: ', this.contObjectList);
            this.eventManager.broadcast({name: 'contObjectListChanged', content: this.contObjectList});
        });

        this.isInited = true;
    }

    loadMonitorState(contObjectId: string): Observable<ContObjectControl> {
        const url = this.resourceUrl.replace(/{contObjectId}/, contObjectId);
        return this.http.get<MonitorState>(url)
            .map((monitorState: MonitorState) => this.convertMonitorStateToContObjectControl(monitorState));
    }

    convertMonitorStateToContObjectControl(monitorState: MonitorState) {
        // CCO:
//        private coSettingMode: string,
//        private coBuildingType: string,
//        private coName: string,
//        private coHeat: string,
//        private coHw: string,
//        private coCw: string,
//        private coEl: string

console.log(monitorState);
        const settingMode = monitorState.contObjectShortInfo.currentSettingMode;
        const buildingType = monitorState.contObjectShortInfo.buildingType;
        const name = monitorState.contObjectShortInfo.contObjectFullName;
        const monitorStateWrapper: MonitorStateWrapper = new MonitorStateWrapper(monitorState);
        const heatState = monitorStateWrapper.getHeatState();
        const hwState = monitorStateWrapper.getHwState();
        const cwState = monitorStateWrapper.getCwState();
        const elState = monitorStateWrapper.getElState();
        const cco: ContObjectControl = new ContObjectControl(settingMode, buildingType, name, heatState, hwState, cwState, elState);
        return cco;
    }
}
