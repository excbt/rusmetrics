export class SubscrContObjectTreeType1 {
    constructor(
        public id: number,
        parentId: number,
        childObjectList: any[],
        rmaSubscriberId: number,
        subscriberId: number,
        isRma: boolean,
        objectTreeType: string,
        objectName: string,
        objectDescription: string,
        objectComemnt: string,
        devComment: string,
        templateId: number,
        templateItemId: number,
        isLinkDeny: boolean,
        version: number
    ) {}
}
