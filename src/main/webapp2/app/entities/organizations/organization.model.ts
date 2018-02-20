export class Organization {
    constructor(
        public id: number,
        public organizationName: string,
        public organizationFullName: string,
        public organizationFullAddress: string,
        public version: string,
        public flagRso: string,
        public flagCm: string,
        public flagRma: string,
        public keyname: string,
        public organizationDescription: string,
    ) { }
}
