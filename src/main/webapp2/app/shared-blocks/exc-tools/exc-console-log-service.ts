import { DEBUG_INFO_ENABLED } from '../../app.constants';

export class ExcConsoleLogService {

    private debugOn = DEBUG_INFO_ENABLED;

    private callConsole(action: () => void) {
        if (this.debugOn) {
            action();
        }
    }

    public group(title: string) {
        this.callConsole(() => console.group(title));
    }

    public groupCollapsed(title: string) {
        this.callConsole(() => console.groupCollapsed(title));
    }

    public groupEnd() {
        this.callConsole(() => console.groupEnd);
    }

    public log(msg: string) {
        this.callConsole(() => console.log(msg));
    }

    public error(msg: string) {
        this.callConsole(() => console.error(msg));
    }

}
