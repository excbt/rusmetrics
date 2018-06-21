export class ContObjectEvent {
    constructor(public contEventId?: number,
                public contEventLevel?: number,
                public contEventLevelColorKeyname?: string,
                public contEventTime?: number[],
                public contEventTimeDT?: number,
                public contEventType?: any,
                public contEventTypeId?: number,
                public contObjectId?: number,
                public contServiceType?: string,
                public contZPointId?: number,
                public id?: number,
                public lastContEventId?: number,
                public lastContEventTime?: number[],
                public scalar?: any,
                public worseContEventId?: any,
                public worseContEventTime?: any,
                public contEvent?: any) {}
}
