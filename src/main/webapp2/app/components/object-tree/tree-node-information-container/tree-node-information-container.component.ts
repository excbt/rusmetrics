import { Component, OnInit, Input } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';

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
                 private route: ActivatedRoute
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
        const treeNodeId = event.content;
        this.treeNodeId = +event.content;
        console.log('TreeNodeInformationContainerComponent: treeNodeId: ', treeNodeId);
        this.router.navigate([this.currentWidget.url, this.treeNodeId], {relativeTo: this.route});
    }

    changeWidget(widget) {
        console.log(widget);
        this.currentWidget = widget;
        this.router.navigate([widget.url, this.treeNodeId], {relativeTo: this.route});
    }

}
