export class ContObjectAccess {
    constructor(
        public subscriberId: number,        // Код абонента
        public contObjectId: number,        // Код объекта
        public contObjectName: string,
        public contObjectFullName: string,
        public contObjectFullAddress: string,
        public contObjectNumber: string,
//          public grantTz: null,        // Время получения доступа

        public accessType: string,        // Тип доступа
//          public accessTtl: null,        // Время, до которого будет предоставлен доступ
//          public accessTtlTz: null,        // Время, до которого будет предоставлен доступ
        public grantTz: Date,
        public revokeTz: Date,        // Время отзыва доступа
        public accessEnabled?: boolean,
        public accessContZPointCnt?: number,
        public allContZPointCnt?: number,
    ) { }

}
