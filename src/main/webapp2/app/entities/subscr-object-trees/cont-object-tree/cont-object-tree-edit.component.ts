import { Component, OnInit } from '@angular/core';
import { SubscrObjectTreeService } from '../subscr-object-tree.service';
import { TreeNode, SortEvent } from 'primeng/api';
import { SubscrObjectTree } from '../subscr-object-tree.model';
import { BehaviorSubject } from 'rxjs';
import { ContObjectShortVM } from '../../cont-objects/cont-object-shortVm.model';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

const startLoadingDelay = 150;

@Component({
    selector: 'jhi-cont-object-tree-edit',
    templateUrl: './cont-object-tree-edit.component.html',
    styleUrls: ['./cont-object-tree-edit.component.scss']
})
export class ContObjectTreeEditComponent implements OnInit {

    treeList: SubscrObjectTree[];

    currentTree: SubscrObjectTree;

    objectTree: TreeNode[];

    availableContObjects: ContObjectShortVM[];

    private leftLoadingSubject = new BehaviorSubject<boolean>(false);

    leftTreeLoading$ = this.leftLoadingSubject.asObservable();

    selectedContObjectIds: number[] = [];
    private draggableContObjects: ContObjectShortVM[] = [];

    private selectedNodes: TreeNode[] = [];
    private draggableNodes: TreeNode[] = [];

    private currentTreeSubject = new BehaviorSubject<number>(null);

    private currentTreeId$ = this.currentTreeSubject.asObservable();

    private treeContObjectNodesData: OptionalNodeData[] = [];

    constructor(
        private subscrObjectTreeService: SubscrObjectTreeService,
    ) { }

    ngOnInit() {
        this.subscrObjectTreeService.findAll().subscribe((data) => {
            this.treeList = data;
            if (data && data.length > 0) {
                this.currentTreeSubject.next(data[0].id);
            }
        });

        this.currentTreeId$.filter((id) => id !== null && id !== undefined)
            .flatMap((id) => {
                this.leftLoadingSubject.next(true);
                this.startLoading();
                return this.subscrObjectTreeService.getContObjectType1(id);
            })
            .pipe(
                catchError(() => of(null)),
                finalize(() => this.finishLoading())
            )
            .subscribe((data) => {
                this.currentTree = data;
                this.treeContObjectNodesData = [];
                this.objectTree = [this.convertTreeDataToTreeNode(data)];
                this.finishLoading();
            });

        this.currentTreeId$.filter((id) => id !== null && id !== undefined)
            .flatMap((id) => this.subscrObjectTreeService.getContObjectsAvailable({ rootNodeId: id}))
            .subscribe((data) => {
                console.log('Load Cont Object Data');
                this.availableContObjects = data;
                this.clearSelection();
            });

    }

    startLoading() {
        this.leftLoadingSubject.next(true);
        // Observable.timer(startLoadingDelay).takeUntil(this.leftLoadingSubject).subscribe(() => this.leftLoadingSubject.next(true));
    }

    finishLoading() {
        this.leftLoadingSubject.next(false);
        // Observable.timer(startLoadingDelay).takeUntil(this.leftLoadingSubject).subscribe(() => this.leftLoadingSubject.next(true));
    }

    clearSelection() {
        this.selectedContObjectIds = [];
        this.selectedNodes = [];
    }

    treeChange(event) {
        if (event && event.value && event.value.id) {
            this.currentTreeSubject.next(event.value.id);
        }
    }

    treeMenuChange(id: number) {
        this.currentTreeSubject.next(id);
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
        this.draggableContObjects = this.availableContObjects.filter((i) => this.selectedContObjectIds.indexOf(i.contObjectId) > -1);
        if (this.draggableContObjects.indexOf(co) === -1) {
            this.draggableContObjects.push(co);
        }
    }

    dragFromTreeStart(event, node: TreeNode) {
        this.draggableNodes = this.selectedNodes.slice(0);
        if (node.data && node.data.dataType && node.data.dataType === 'CONT_OBJECT') {
            console.log('Drag From Tree: ' + JSON.stringify(node.data) + ' parent nodeId: ' + node.parent.data.subscrNode.id);
            if (this.draggableNodes && this.draggableNodes.indexOf(node) === -1) {
                this.draggableNodes.push(node);
            }
        }
    }

    dropContObjectToTree(event, destNode: TreeNode) {
        if (destNode.data.subscrNode.type = 'TREE_NODE') {
            console.log('nodeId: ' + JSON.stringify(destNode.data.subscrNode.id));
            const workContObjects = this.draggableContObjects.splice(0);
            const rId = this.currentTree.id;
            const nId = destNode.data.subscrNode.id;
            const objIds = workContObjects.map((co) => co.contObjectId).filter((value, index, self) => self.indexOf(value) === index);
            this.subscrObjectTreeService.putContObjectDataAdd(
                {rootNodeId: rId, nodeId: nId},
                {contObjectIds: objIds})
                .subscribe(() => {
                    this.addDraggableToNode(destNode, workContObjects);
                    this.removeContObjectsFromAvailable(objIds);
                    this.selectedContObjectIds = [];
                    this.draggableContObjects = [];
                });
        }
        // else {
        //     this.draggableNodes = [];
        // }
    }

    dropFromTree(event) {
        if (this.draggableNodes && this.draggableNodes.length > 0) {
            const workNodes = this.draggableNodes.splice(0);
            const rId = this.currentTree.id;
            const nIds = workNodes.map((n) => n.parent.data.subscrNode.id).filter((value, index, self) => self.indexOf(value) === index);
            const objIds = workNodes.map((n) => n.data.contObject.contObjectId);
            if (nIds.length === 1) {
                this.subscrObjectTreeService.putContObjectDataRemove(
                    {rootNodeId: rId, nodeId: nIds[0]},
                    {contObjectIds: objIds})
                    .subscribe(() => {
                            workNodes.forEach((n) => this.removeDraggabeFromNode(n));
                            workNodes.forEach((n) => this.addContObjectToAvailable(n.data.contObject));
                    });
            } else {
                console.log('No single node');
            }
        }
    }

    removeContObjectFromAvailable(id: number) {
        this.availableContObjects = this.availableContObjects.filter((d) => d.contObjectId !== id);
    }

    removeContObjectsFromAvailable(ids: number[]) {
        this.availableContObjects = this.availableContObjects.filter((d) => ids.indexOf(d.contObjectId) === -1);
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

    addDraggableToNode(destNode: TreeNode, workContObjects: ContObjectShortVM[]) {
        workContObjects.forEach((co) => {
            const newNode = this.convertContObjectVMToTreeNode(co);
            destNode.children.push(newNode);
        });
        // const newChildren = destNode.children.splice(0);
        destNode.children.sort((a, b) => {
            return a.data.contObject.uiCaption.toLocaleLowerCase().localeCompare(a.data.contObject.uiCaption.toLocaleLowerCase());
        });
        // destNode.children = newChildren;
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
                treeNodes.forEach((n) => this.treeContObjectNodesData.push(n.data));
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

    clickTreeContObjectRow(event, node: TreeNode) {
        // const nodeContObjectIds = node.parent.children.filter((n) => n.data.dataType === 'CONT_OBJECT')
        //     .map((n) => n.data.contObject.contObjectId);

        // nodeContObjectIds.forEach((id) => console.log('id: ' + id));

        // this.optContObjectNodes.filter((n) => nodeContObjectIds.indexOf(n.contObject.contObjectId) === -1)
        //     .forEach((n) => n.selected = false);
        node.data.selected = !node.data.selected;
        if (node.data.selected) {
            this.selectedNodes.push(node);
        } else {
            // this.selectedNodes.po
            const index = this.selectedNodes.indexOf(node, 0);
            if (index > -1) {
                this.selectedNodes.splice(index, 1);
            }
        }
        this.selectedContObjectIds = [];
        // let nodes = '';
        // this.selectedNodes.forEach((n) => nodes = nodes + ', ' + n.data.contObject.contObjectId);
        // console.log('Nodes ids:' + nodes);
    }

    clickTableRow(event, vm: ContObjectShortVM) {
        const id = vm.contObjectId;
        const i = this.selectedContObjectIds.indexOf(id);
        if (i === -1) {
            this.selectedContObjectIds.push(id);
        } else {
            this.selectedContObjectIds.splice(i, 1);
        }
        this.selectedNodes.forEach((n) => n.data.selected = false);
        this.selectedNodes = [];
    }

    customTableSort(event: SortEvent) {
        // event.data = Data to sort
        // event.mode = 'single' or 'multiple' sort mode
        // event.field = Sort field in single sort
        // event.order = Sort order in single sort
        // event.multiSortMeta = SortMeta array in multiple sort

        event.data.sort((data1, data2) => {
            const value1 = data1[event.field];
            const value2 = data2[event.field];
            let result = null;

            if (value1 === null && value2 !== null) {
                result = -1;
            } else if (value1 != null && value2 == null) {
                result = 1;
            } else if (value1 == null && value2 == null) {
                result = 0;
            } else if (typeof value1 === 'string' && typeof value2 === 'string') {
                result = value1.localeCompare(value2);
            } else {
                result = (value1 < value2) ? -1 : (value1 > value2) ? 1 : 0;
            }

            return (event.order * result);
        });
    }

}

export interface OptionalNodeData {
    dataType: string;
    subscrNode?: SubscrObjectTree;
    contObject?: ContObjectShortVM;
    selected?: boolean;
}
