import { Component, OnInit } from '@angular/core';
import { SubscrObjectTreeService } from '../subscr-object-tree.service';
import { JhiEventManager } from 'ng-jhipster';
import { ConfirmationService, TreeNode } from 'primeng/api';
import { SubscrObjectTree } from '../subscr-object-tree.model';
import { BehaviorSubject, Observable } from 'rxjs';
import { ContObjectShortVM } from '../../cont-objects/cont-object-shortVm.model';

@Component({
    selector: 'jhi-cont-object-tree-edit',
    templateUrl: './cont-object-tree-edit.component.html',
    styleUrls: ['./cont-object-tree-edit.component.scss'],
    providers: [ConfirmationService]
})
export class ContObjectTreeEditComponent implements OnInit {

    treeList: SubscrObjectTree[];
    selectedTreeShort: SubscrObjectTree;

    currentTree: SubscrObjectTree;

    objectTree: TreeNode[];
    selectedNode: TreeNode;

    availableContObjects: ContObjectShortVM[];

    private draggableContObject: ContObjectShortVM;

    private draggableNode: TreeNode;

    private selectedTreeSubject = new BehaviorSubject<number>(null);

    private selectedTreeId$ = this.selectedTreeSubject.asObservable();

    constructor(
        private subscrObjectTreeService: SubscrObjectTreeService,
        private eventManager: JhiEventManager,
        private confirmationService: ConfirmationService
    ) { }

    ngOnInit() {
        this.subscrObjectTreeService.findAll().subscribe((data) => {
            this.treeList = data;
            if (data && data.length > 0) {
                this.selectedTreeSubject.next(data[0].id);
            }
        });
        this.selectedTreeId$.filter((id) => id !== null && id !== undefined).flatMap((id) => this.subscrObjectTreeService.getContObjectType1(id))
            .subscribe((data) => {
                this.currentTree = data;
                this.objectTree = [this.convertTreeDataToTreeNode(data)];
            });

        this.selectedTreeId$.filter((id) => id !== null && id !== undefined)
            .flatMap((id) => this.subscrObjectTreeService.getContObjectsAvailable({ rootNodeId: id}))
            .subscribe((data) => {
                this.availableContObjects = data;
            });

    }

    treeChange(event) {
        if (event && event.value && event.value.id) {
            this.selectedTreeSubject.next(event.value.id);
        }
    }

    convertTreeDataToTreeNode(objectTree: SubscrObjectTree): TreeNode {
        const childrenList = objectTree.childObjectList
            ? objectTree.childObjectList.map((i) =>
                  this.convertTreeDataToTreeNode(i)
              )
            : [];

        const treeNode = new NodeData('TREE_NODE', objectTree);

        const result: TreeNode = {
            label: objectTree.objectName,
            data: treeNode,
            children: childrenList,
            expanded: true
        };

        console.log('convert load id:' + objectTree.id + ' conditions ' + !objectTree.isLinkDeny + ' - ' + (objectTree.id !== this.currentTree.id));
        if (!objectTree.isLinkDeny && objectTree.id !== this.currentTree.id) {
            this.loadContObjectData(result);
        }

        return result;
    }

    dragToTreeStart(event, co: ContObjectShortVM) {
        console.log('contObjectId: ' + co.contObjectId);
        this.draggableContObject = co;
    }

    dragFromTreeStart(event, node: TreeNode) {
        if (node.data && node.data.type && node.data.type === 'CONT_OBJECT') {
            console.log('Drag From Tree: ' + JSON.stringify(node.data) + ' parent nodeId: ' + node.parent.data.content.id);
            this.draggableNode = node;
        }
    }

    dropToTree(event, destNode: TreeNode) {
        if (destNode.data.content.type = 'TREE_NODE') {
            console.log('nodeId: ' + JSON.stringify(destNode.data.content.id));
            const rId = this.currentTree.id;
            const nId = destNode.data.content.id;
            const objId = this.draggableContObject.contObjectId;
            this.subscrObjectTreeService.putContObjectData(
                {rootNodeId: rId, nodeId: nId},
                {contObjectIds: [objId]})
                .subscribe(() => {
                    this.addDraggableToNode(destNode);
                    this.removeContObject(objId);
                });
        }
    }

    removeContObject(id: number) {
        this.availableContObjects = this.availableContObjects.filter((d) => d.contObjectId !== id);
    }

    addDraggableToNode(destNode: TreeNode) {
        const treeContObject = Object.assign({}, this.draggableContObject);
        const newNode = this.convertContObjectVMToTreeNode(treeContObject);
        destNode.children.push(newNode);
    }

    loadContObjectData(destNode: TreeNode) {
        console.log('Load Data Action');
        if ((destNode.data.nodeType === 'TREE_NODE') && !destNode.data.content.isLinkDeny) {
            this.subscrObjectTreeService.getContObjectsLinked({rootNodeId: this.currentTree.id, nodeId: destNode.data.content.id})
            .subscribe((data) => {
                console.log('loaded ' + data.length + ' items');
                const treeNodes = data.map((co) => this.convertContObjectVMToTreeNode(co));
                treeNodes.forEach((n) => destNode.children.push(n));
            });
        }
    }

    convertContObjectVMToTreeNode(contObjectVM: ContObjectShortVM): TreeNode {

        const treeNode = new NodeData('CONT_OBJECT', contObjectVM);

        const newNode: TreeNode = {
            label: contObjectVM.uiCaption,
            data: treeNode,
            icon: 'exc-icon-building-default-16',
            expanded: false
        };
        return newNode;
    }

}

export class NodeData {
    constructor(
        readonly nodeType: string,
        readonly content: SubscrObjectTree | ContObjectShortVM
    ) {}
}

export interface OptionalNodeData {
    nodeType: string;
    objectTree?: SubscrObjectTree;
    contObject?: ContObjectShortVM;
}
