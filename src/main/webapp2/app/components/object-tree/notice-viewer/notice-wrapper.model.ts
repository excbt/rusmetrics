import { Notice } from './notice.model';

export class NoticeWrapper {

    private displayedDate: string;
    private displayedTime: string;

    constructor(public notice: Notice) {}

    public getContServiceType(): string {
        return this.notice.contEvent.contServiceType;
    }

    public getMessage(): string {
        return this.notice.contEventType.name + ': ' + this.notice.contEvent.message;
    }

    public getColorState(): string {
        return this.notice.contEventLevelColor.toLowerCase();
    }

    public getDate(): string {
        return this.displayedDate;
    }

    public getTime(): string {
        return this.displayedTime;
    }

    public setDate(date: string) {
        this.displayedDate = date;
    }

    public setTime(time: string) {
        this.displayedTime = time;
    }
}
