export class ContObjectShortVM {
    constructor(
        public contObjectId: number,
        public contObjectName: string,
        public contObjectFullName: string,
        public currentSettingMode: string,
        public buildingType: string,
        public buildingTypeCategory: string,
        public contObjectFullAddress: string,
        public uiCaption?: string
    ) { }
}
