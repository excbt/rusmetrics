export class ContZPointAccess {
    constructor(
        public subscriberId: number,        // Код абонента
        public contZPointId: number,
        public contServiceTypeKeyname: string,
        public contZPointTsNumber: number,
        public contZPointCustomServiceName: string,
        public accessType: string,
        public accessTtl: Date,
        public grantTZ: Date,
        public accessEnabled?: boolean
    ) { }
}
