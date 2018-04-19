export class PSubscriber {
  constructor(
      public id: number,        // ID. Первичный ключ
      public organizationId: number,        // Код организации
      public subscriberName: string,        // Наименование абонента
      public subscriberInfo: string,        // Информация об абоненте
      public subscriberComment: string,        // Комментарий для пользователя
      public timezoneDef: string,        // Часовой пояс абонента
      public subscriberUuid: string,        // UUID абонента
      public parentSubscriberId: number,        // Код родительского абонента
      public isRma: boolean,        // Признак "РМА"
      public rmaSubscriberId: number,        // Код абонента РМА
      public ghostSubscriberId: number,        // Код абонента режима "Тень". Зарезервировано для внутреннего использования
      public isGhostMode: boolean,        // Режим "Тень". Зарезервировано для внутреннего использования
      public skipServiceFilter: boolean,
      public rmaLdapOu: string,        // Информация LDAP. Зарезервировано для внутреннего использования
      public mapCenterLat: number,        // Центр карты. Широта
      public mapCenterLng: number,        // Центр карты. Долгота
      public mapZoom: number,        // Начальный зум для карты
      public mapZoomDetail: number,
      public subscrType: string,        // Тип абонента
      public canCreateChild: boolean,        // Признак "Создание дочерних абонентов"
      public isChild: boolean,        // Признак "Дочерний абонент"
      // public childLdapOu: string,        // Информация LDAP для дочерних абонентов. Зарезервировано для внутреннего использования
      public subscrCabinetNr: string,        // Код кабинета абонента
      public contactEmail: string,        // Контактный email
      // public subscrCabinetSeq: string,        // Генератор кодов для кабинетов. Зарезервировано для внутреннего использования
  ) { }
}
