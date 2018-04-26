import { Component, OnInit, Input } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
@Component({
    selector: 'jhi-tree-node-information-container',
    templateUrl: './tree-node-information-container.component.html'
})
export class TreeNodeInformationContainerComponent implements OnInit {
    
    widgetList: any[];
    @Input() treeNodeId: number;
    
    constructor(private eventManager: JhiEventManager) {
        
    }
    
    ngOnInit() {
console.log('TreeNodeInformationContainerComponent: ngOnInit()');        
console.log('this.treeNodeId', this.treeNodeId);        
        this.widgetList = [
            {
                caption: 'Контроль'
            },
            {
                caption: 'Мониторинг'
            },
        ];
        
        this.eventManager.subscribe('setTreeNode', (treeNodeId) => console.log('TreeNodeInformationContainerComponent: treeNodeId: ', treeNodeId));
    }
    
}