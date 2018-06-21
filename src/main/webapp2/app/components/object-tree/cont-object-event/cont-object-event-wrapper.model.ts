import { ContObjectEvent } from './cont-object-event.model';

export class ContObjectEventWrapper {
    public contEventId: string;
    public contEventDisplayedDate: string;
    public contEventDisplayedTime: string;
    public contEventDisplayedMessage: string;

    constructor(private contObjectEvent: ContObjectEvent) {
        this.contEventId = contObjectEvent.contEventId.toString();
    }

    setContEventDisplayedDate(dateString: string) {
        this.contEventDisplayedDate = dateString;
    }

    getContEventDisplayedDate(): string {
        return this.contEventDisplayedDate;
    }

    setContEventDisplayedTime(dateString: string) {
        this.contEventDisplayedTime = dateString;
    }

    getContEventDisplayedTime(): string {
        return this.contEventDisplayedTime;
    }

    getContEventDisplayedMessage(): string {
        let message = this.contObjectEvent.contEventType.name;

        if (this.contObjectEvent.hasOwnProperty('contEvent') && this.contObjectEvent.contEvent !== null && this.contObjectEvent.contEvent.hasOwnProperty('message')) {
            message += ': ' + this.contObjectEvent.contEvent.message;
        }
        return message;
    }

    getContServiceType(): string {
        return this.contObjectEvent.contServiceType.toLowerCase();
    }

    getColorState(): string {
        return this.contObjectEvent.contEventLevelColorKeyname.toLowerCase();
    }
}
