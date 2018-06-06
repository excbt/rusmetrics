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

        const optionalData: OptionalNodeData = {dataType: 'TREE_NODE', subscrNode: objectTree};

        const result: TreeNode = {
            label: objectTree.objectName,
            data: optionalData,
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
        if (node.data && node.data.dataType && node.data.dataType === 'CONT_OBJECT') {
            console.log('Drag From Tree: ' + JSON.stringify(node.data) + ' parent nodeId: ' + node.parent.data.subscrNode.id);
            this.draggableNode = node;
        }
    }

    dropToTree(event, destNode: TreeNode) {
        if (destNode.data.subscrNode.type = 'TREE_NODE') {
            console.log('nodeId: ' + JSON.stringify(destNode.data.subscrNode.id));
            const rId = this.currentTree.id;
            const nId = destNode.data.subscrNode.id;
            const objId = this.draggableContObject.contObjectId;
            this.subscrObjectTreeService.putContObjectDataAdd(
                {rootNodeId: rId, nodeId: nId},
                {contObjectIds: [objId]})
                .subscribe(() => {
                    this.addDraggableToNode(destNode);
                    this.removeContObjectFromAvailable(objId);
                });
        } else {
            this.draggableNode = null;
        }
    }

    dropFromTree(event) {
        if (this.draggableNode && this.draggableNode.parent) {
            const rId = this.currentTree.id;
            const nId = this.draggableNode.parent.data.subscrNode.id;
            const objId = this.draggableNode.data.contObject.contObjectId;
            this.subscrObjectTreeService.putContObjectDataAdd(
                {rootNodeId: rId, nodeId: nId},
                {contObjectIds: [objId]})
                .subscribe(() => {
                    this.removeDraggabeFromNode(this.draggableNode);
                    this.addContObjectToAvailable(this.draggableNode.data.contObject);
                });
        }
    }

    removeContObjectFromAvailable(id: number) {
        this.availableContObjects = this.availableContObjects.filter((d) => d.contObjectId !== id);
    }

    addContObjectToAvailable(contObject: ContObjectShortVM) {
        if (contObject) {
            const co = Object.assign({}, contObject);
            this.availableContObjects.push(co);
            this.availableContObjects.sort( (a, b) => {
                return a.uiCaption.toLocaleLowerCase().localeCompare(b.uiCaption.toLocaleLowerCase());
            });
        }
    }

    addDraggableToNode(destNode: TreeNode) {
        const treeContObject = Object.assign({}, this.draggableContObject);
        const newNode = this.convertContObjectVMToTreeNode(treeContObject);
        destNode.children.push(newNode);
    }

    removeDraggabeFromNode(srcNode: TreeNode) {
        srcNode.parent.children = srcNode.parent.children
            .filter((node) => node.data.dataType === 'CONT_OBJECT' && node.data.contObject.contObjectId !== srcNode.data.contObject.contObjectId);
    }

    loadContObjectData(destNode: TreeNode) {
        if ((destNode.data.dataType === 'TREE_NODE') && !destNode.data.subscrNode.isLinkDeny) {
            this.subscrObjectTreeService.getContObjectsLinked({rootNodeId: this.currentTree.id, nodeId: destNode.data.subscrNode.id})
            .subscribe((data) => {
                const treeNodes = data.map((co) => this.convertContObjectVMToTreeNode(co));
                treeNodes.forEach((n) => destNode.children.push(n));
            });
        }
    }

    convertContObjectVMToTreeNode(contObjectVM: ContObjectShortVM): TreeNode {

        const optionalData: OptionalNodeData = {dataType: 'CONT_OBJECT', contObject: contObjectVM};

        const newNode: TreeNode = {
            label: contObjectVM.uiCaption,
            data: optionalData,
            icon: 'exc-icon-building-default-16',
            expanded: false
        };
        return newNode;
    }

}

export interface OptionalNodeData {
    dataType: string;
    subscrNode?: SubscrObjectTree;
    contObject?: ContObjectShortVM;
}
