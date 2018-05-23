export class SubscriberAccessStats {
    constructor(
      public id: number,        // ID. Первичный ключ
      public organizationId: number,        // Код организации
      public subscriberName: string,        // Наименование абонента
      public subscriberInfo: string,        // Информация об абоненте
      public subscriberComment: string,        // Комментарий для пользователя
      public timezoneDefKeyname: string,        // Часовой пояс абонента
      public subscrType: string,        // Тип абонента
      public contactEmail: string,        // Контактный email
      public rmaLdapOu: string,        // Информация LDAP. Зарезервировано для внутреннего использования
      public canCreateChild: boolean,        // Признак "Создание дочерних абонентов"
      public version: number,
      public organizationInn: string,
      public organizationName: string,
      public totalContObjects?: number,
      public totalContZPoints?: number
  ) { }
  }
