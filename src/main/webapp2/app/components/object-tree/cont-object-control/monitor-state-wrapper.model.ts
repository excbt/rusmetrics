import { MonitorState } from './monitor-state.model';
import { ContZPointMonitorState } from '../models/cont-z-point-monitor-state.model';

export class MonitorStateWrapper {
    constructor(public monitorState: MonitorState) {}

    getHeatState(): string {
        const contZPointMonitorState: ContZPointMonitorState[] = this.monitorState.contZPointMonitorState;
        const heatState: ContZPointMonitorState = contZPointMonitorState.filter((zpState) => zpState.contServiceTypeKeyname === 'heat')[0];
        const heatStateColor: string = heatState ? heatState.stateColor : null;
        return heatStateColor;
    }

    getHwState(): string {
        const contZPointMonitorState: ContZPointMonitorState[] = this.monitorState.contZPointMonitorState;
        const hwState = contZPointMonitorState.filter((zpState) => zpState.contServiceTypeKeyname === 'hw')[0];
        const hwStateColor: string = hwState ? hwState.stateColor : null;
        return hwStateColor;
    }

    getCwState(): string {
        const contZPointMonitorState: ContZPointMonitorState[] = this.monitorState.contZPointMonitorState;
        const cwState = contZPointMonitorState.filter((zpState) => zpState.contServiceTypeKeyname === 'cw')[0];
        const cwStateColor = cwState ? cwState.stateColor : null;
        return cwStateColor;
    }

    getElState(): string {
        const contZPointMonitorState: ContZPointMonitorState[] = this.monitorState.contZPointMonitorState;
        const elState = contZPointMonitorState.filter((zpState) => zpState.contServiceTypeKeyname === 'el')[0];
        const elStateColor = elState ? elState.stateColor : null;
        return elStateColor;
    }
}
