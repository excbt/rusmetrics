export class Organization {
    constructor(
        public id: number,        // ID. Первичный ключ
        public organizationName: string,        // Наименование организации
        public organizationFullName: string,        // Полное наименование организации
        public organizationFullAddress: string,        // Полный адрес организации
        public flagRso: boolean,        // Признак "РСО"
        public flagManagement: boolean,        // deprecated Признак "УК"
        public flagRma: boolean,        // Признак "РМА"
        public keyname: string,        // Ключевое наименование. Первичный ключ
        public flagCm: boolean,        // Признак "УК"
        public organizationDescription: string,        // Описание организации
        public isCommon: boolean,        // Признак "глобальный"
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
        public organizationTypeId: number
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
