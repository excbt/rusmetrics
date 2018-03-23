export class Organization {
    constructor(
        public id: number,
        public organizationName: string,
        public organizationFullName: string,
        public organizationFullAddress: string,
        public exCode: string,
        public exSystem: string,
        public flagRso: boolean,
        public flagManagement: boolean,
        public flagRma: boolean,
        public keyname: string,
        public isDevMode: boolean,
        public flagCm: boolean,
        public organizationDescription: string,
        public isCommon: boolean,
        public rmaSubscriberId: number,
        public flagServ: boolean,
        public inn: string,
        public kpp: string,
        public okpo: string,
        public ogrn: string,
        public legalAddress: string,
        public factAddress: string,
        public postAddress: string,
        public reqAccount: string,
        public reqBankName: string,
        public reqCorrAccount: string,
        public reqBik: string,
        public contactPhone: string,
        public contactPhone2: string,
        public contactPhone3: string,
        public contactEmail: string,
        public siteUrl: string,
        public directorFio: string,
        public chiefAccountantFio: string,
        public organizationTypeId: number,
        public organizationTypeName: string,
        public version: number
    ) { }
}

export class OrganizationSort {
    constructor(
        public field: string,
        public sortOrder: string
    ) { }

    sortString(): string {
        return this.field.concat(',', this.sortOrder);
    }

}
