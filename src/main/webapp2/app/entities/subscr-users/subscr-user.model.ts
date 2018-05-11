export class SubscrUser {
    constructor(
        public id: number,        // ID. Первичный ключ
        public userName: string,        // Имя пользователя
        public userNickname: string,        // Никнейм. Зарезервировано для внутреннего использования
        public version: number,
        public subscriberId: number,        // Код абонента
        public userUuid: string,        // UUID пользователя
        public userComment: string,        // Комментарий для пользователя
        public userEmail: string,        // email
        public isBlocked: boolean,        // Признак "блокирован"
        public contactEmail: string,        // Контактный email
        public userDescription: string,        // Описание пользователя
        public isAdmin: boolean,        // Признак "администратор"
        public isReadonly: boolean,        // Признак "только чтение"
        public authorities: string[]
        ) { }
}
