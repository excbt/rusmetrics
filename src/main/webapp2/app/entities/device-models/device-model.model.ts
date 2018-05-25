export class DeviceModel {
    constructor(
        public id: number,        // ID. Первичный ключ
        public modelName: string,        // Наименование модели
        public keyname: string,        // Ключевое наименование. Первичный ключ
        public caption: string,        // Описание для UI. Зарезервировано для внутреннего использования
        public modelComment: string,        // Комментарий для пользователя
        public modelDescription: string,        // Описание модели
        public exLabel: string,        // Описание из внешней системы. Зарезервировано для внутреннего использования
        public modelDriverType: string,        // Тип драйвера модели
        public metaVersion: number,        // Версия трансформации
        public isImpulse: boolean,        // Признак импульсный
        public defaultImpulseK: number,        // Коэффициент по умолчанию
        public defaultImpulseMu: string,        // Еденица измерения по умолчанию
        public deviceType: string,        // Тип прибора учета
    ) { }
}
