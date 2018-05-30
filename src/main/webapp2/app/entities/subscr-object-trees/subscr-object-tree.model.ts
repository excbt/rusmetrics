export class SubscrObjectTree {
    constructor(
        public id: number,        // ID. Первичный ключ
        public parentId: number,        // Код дочернего узла
        public childObjectList: SubscrObjectTree[],
        public rmaSubscriberId: number,        // Код абонента РМА
        public subscriberId: number,        // Код абонента
        public isRma: boolean,        // Признак "РМА"
        public objectTreeType: string,        // Тип иерархии
        public objectName: string,        // Наименование иерархии
        public objectDescription: string,        // Описание
        public objectComemnt: string,        // Комментарий
        public devComment: string,        // Внутренний комментарий. Зарезервировано для внутреннего использования
        public templateId: number,        // Код шаблона иерархии
        public templateItemId: number,        // Код элемента шаблона
        public isLinkDeny: boolean,        // Признак "не привязывать объекты учета"
        public isSingleObject: boolean,        // Признак "один объект",
        public treeMode: string
    ) { }
}

export interface SubscrObjectTreeVM {
    id?: number;
    parentId?: number;
    objectName: string;
    isLinkDeny?: boolean;
    isSingleObject?: boolean;
}
