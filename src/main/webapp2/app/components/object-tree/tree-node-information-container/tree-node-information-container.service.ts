import { Injectable } from '@angular/core';

@Injectable()
export class TreeNodeInformationContainerService {
    
    private COMMON_WIDGET_LIST: Widget[] = [];
    private ELEMENT_WIDGET_LIST: Widget[] = [
        {
            caption: 'Мониторинг',
            url: 'tree-node-color-status'
        },
        {
            caption: 'Контроль',
            url: 'tree-node-control'
        }
    ];
    
    private CONT_OBJECT_WIDGET_LIST: Widget[] = [
        {
            caption: 'Контроль',
            url: 'cont-object-control'
        }
    ];
    
    private widgets = {
        ELEMENT: this.ELEMENT_WIDGET_LIST,
        CONT_OBJECT: this.CONT_OBJECT_WIDGET_LIST
    }
    
    constructor() {}
    
    getWidgetList(nodeType: string) {
        return this.widgets[nodeType];
    }
    
    getCurrentWidget(nodeType: string) {
        return this.widgets[nodeType][0];
    }
    
}

export class Widget {
    constructor(public caption: string,
                public url: string) {}
}
