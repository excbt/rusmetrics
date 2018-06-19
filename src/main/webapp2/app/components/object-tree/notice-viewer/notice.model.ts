export class Notice {
    constructor(public contEvent?: any,
                public contEventCategoryKeyname?: string,
                public contEventDeviationKeyname?: string,
                public contEventId?: number,
                public contEventLevel?: number,
                public contEventLevelColor?: string,
                public contEventTime?: number,
                public contEventTimeDT?: number,
                public contEventType?: any,
                public contEventTypeId?: number,
                public contObjectId?: number,
                public id?: number,
                public _new?: boolean,
                public notificationTime?: number,
                public notificationTimeTZ?: number,
                public subscribeId?: number) {}
}
