import { ContObjectShortInfo } from '../models/cont-object-short-info.model';
import { ContZPointMonitorState } from '../models/cont-z-point-monitor-state.model';

export class MonitorState {
    constructor(public contObjectShortInfo: ContObjectShortInfo,
                public contZPointMonitorState: ContZPointMonitorState[]) {}
}
