import { Component, OnInit, Input } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';

import { PTreeNodeService } from '../';
import { TreeNodeInformationContainerService } from './tree-node-information-container.service';

import { PTreeNode } from '../models/p-tree-node.model';
import { PTreeNodeWrapper } from '../models/p-tree-node-wrapper.model';

@Component({
    selector: 'jhi-tree-node-information-container',
    templateUrl: './tree-node-information-container.component.html',
    styleUrls: [
        './tree-node-information-container.component.scss'
    ]
})
export class TreeNodeInformationContainerComponent implements OnInit {

    widgetList: any[];
    currentWidget: any;
    @Input() treeNodeId: number;

    constructor(private eventManager: JhiEventManager,
                 private router: Router,
                 private route: ActivatedRoute,
                 private ptreeNodeService: PTreeNodeService,
                 private treeNodeInformationContainerService: TreeNodeInformationContainerService
                ) {

    }

    ngOnInit() {
// console.log('TreeNodeInformationContainerComponent: ngOnInit()');
// console.log('this.treeNodeId', this.treeNodeId);
        this.widgetList = [
            /*{
                caption: 'Информация',
                url: 'tree-node-information'
            },*/
            {
                caption: 'Мониторинг',
                url: 'tree-node-color-status'
            },
            {
                caption: 'Контроль',
                url: 'tree-node-control'
            }
        ];
        this.currentWidget = this.widgetList[0];

        this.eventManager.subscribe('setTreeNode', (eventData) => this.changeNode(eventData));
    }

    changeNode(event) {
        console.log('EVENT: ', event);
        const treeNodeId = event.content._id || event.content.id || event.content.nodeObject.id;
//        const treeNodeId = event.content;
        this.treeNodeId = +treeNodeId;
//        console.log('TreeNodeInformationContainerComponent: treeNodeId: ', treeNodeId);
        // get node type
//        this.ptreeNodeService.loadPTreeNode(treeNodeId, 0).subscribe((resp) => this.successLoadNode(resp));
        // get widget for node
        this.successLoadNode(event.content);
        // navigate to dest. source
//        this.router.navigate([this.currentWidget.url, this.treeNodeId], {relativeTo: this.route});
    }

    changeWidget(widget) {
        console.log(widget);
        this.currentWidget = widget;
        this.router.navigate([widget.url, this.treeNodeId], {relativeTo: this.route});
    }
    
    successLoadNode(node: PTreeNode) {
        // get node type
        const nodeWrapper: PTreeNodeWrapper = new PTreeNodeWrapper(node, null);
        const nodeType: string = nodeWrapper.getNodeType();
        
        // get widget list for this type
        this.widgetList = this.treeNodeInformationContainerService.getWidgetList(nodeType);
        
        // get current widget for this
        this.currentWidget = this.treeNodeInformationContainerService.getCurrentWidget(nodeType);
        
        //navigate
        this.router.navigate([this.currentWidget.url, this.treeNodeId], {relativeTo: this.route});
    }

}
