import { Component, OnDestroy } from '@angular/core';
import { SubscrObjectTreeService } from './subscr-object-tree.service';
import { TreeNode, ConfirmationService } from 'primeng/api';
import {
    SubscrObjectTree,
    SubscrObjectTreeVM,
    SubscrObjectTreeModificationEvent
} from './subscr-object-tree.model';
import { OverlayPanel } from 'primeng/overlaypanel';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription } from 'rxjs';

@Component({
    selector: 'jhi-subscr-object-tree-edit',
    providers: [ConfirmationService],
    templateUrl: './subscr-object-tree-edit.component.html',
    styleUrls: ['./subscr-object-tree-edit.component.scss']
})
export class SubscrObjectTreeEditComponent implements OnDestroy {
    rootNodeId: number;

    nameOfTree: string;

    objectTree: TreeNode[];
    selectedNode: TreeNode;

    currentObjectTreeNode: SubscrObjectTree;
    newEditVM: SubscrObjectTreeVM;

    data: SubscrObjectTree;

    private treeAddMode: string;

    dialogHeaderKey: string;

    private eventSubscription: Subscription;

    constructor(
        private subscrObjectTreeService: SubscrObjectTreeService,
        private eventManager: JhiEventManager,
        private confirmationService: ConfirmationService
    ) {
        this.subscrObjectTreeService.currentObjectTreeId$.subscribe((id) => {
            if (id !== this.rootNodeId) {
                this.rootNodeId = id;
                this.selectedNode = null;
                if (id) {
                    this.loadData(id);
                } else {
                    this.objectTree = [];
                }
            }
        });

        this.registerChangeInTree();
    }

    ngOnDestroy() {
        if (this.eventSubscription) {
            this.eventManager.destroy(this.eventSubscription);
        }
    }

    registerChangeInTree() {
        this.eventSubscription = this.eventManager.subscribe(
            SubscrObjectTreeModificationEvent,
            (response) => {
                if (response.id === this.rootNodeId) {
                    this.loadData(this.rootNodeId);
                }
            }
        );
    }

    loadData(id: number) {
        if (id) {
            this.subscrObjectTreeService
                .getContObjectType1(id)
                .subscribe((res) => {
                    this.data = res;
                    this.nameOfTree = res.objectName;
                    this.objectTree = [this.convertDataToObjectTree(res)];
                });
        } else {
            this.data = null;
            this.objectTree = [];
        }
    }

    convertDataToObjectTree(objectTree: SubscrObjectTree): TreeNode {
        const childrenList = objectTree.childObjectList
            ? objectTree.childObjectList.map((i) =>
                  this.convertDataToObjectTree(i)
              )
            : [];

        const result: TreeNode = {
            label: objectTree.objectName,
            data: objectTree,
            children: childrenList,
            expanded: true
        };
        if (
            objectTree &&
            this.selectedNode &&
            this.selectedNode.data &&
            objectTree.id === this.selectedNode.data.id
        ) {
            this.selectedNode = result;
        }
        return result;
    }

    addNodeDialog(
        event,
        overlaypanel: OverlayPanel,
        node: SubscrObjectTree,
        mode: string
    ) {
        this.currentObjectTreeNode = Object.assign({}, node);
        this.treeAddMode = mode;
        this.newEditVM = {
            objectName: ''
        };
        this.dialogHeaderKey =
            mode === 'child'
                ? 'subscrObjectTree.captions.childActionTitle'
                : 'subscrObjectTree.captions.siblingsActionTitle';
        overlaypanel.toggle(event);
    }

    editNodeDialog(event, overlaypanel: OverlayPanel, node: SubscrObjectTree) {
        this.dialogHeaderKey = 'subscrObjectTree.captions.renameTitle';
        this.currentObjectTreeNode = Object.assign({}, node);
        overlaypanel.toggle(event);
    }

    saveNodeAdd(event, panel: OverlayPanel) {
        if (this.newEditVM) {
            const vm: SubscrObjectTreeVM = {
                objectName: this.newEditVM.objectName,
                parentId:
                    this.treeAddMode === 'child'
                        ? this.currentObjectTreeNode.id
                        : this.currentObjectTreeNode.parentId
            };
            this.subscrObjectTreeService.addTreeNodeNode(vm).subscribe((data) => {
                panel.hide();
                this.currentObjectTreeNode = null;
                this.eventManager.broadcast({name: SubscrObjectTreeModificationEvent, id: this.rootNodeId});
            });
        }
    }

    saveNodeEdit(event, panel: OverlayPanel) {
        if (this.currentObjectTreeNode) {
            this.subscrObjectTreeService
                .updateTreeNodeNode(this.currentObjectTreeNode)
                .subscribe((data) => {
                    panel.hide();
                    this.currentObjectTreeNode = null;
                    if (data.parentId) {
                        this.loadData(this.rootNodeId);
                    } else {
                        this.eventManager.broadcast({name: SubscrObjectTreeModificationEvent, id: this.rootNodeId});
                    }
                });
        }
    }

    cancelDialog(event, panel: OverlayPanel) {
        panel.hide();
        this.currentObjectTreeNode = null;
    }

    switchIsLinkDeny(node: SubscrObjectTree) {
        this.currentObjectTreeNode = Object.assign({}, node);
        this.currentObjectTreeNode.isLinkDeny = this.currentObjectTreeNode
            .isLinkDeny
            ? !this.currentObjectTreeNode.isLinkDeny
            : true;
        this.subscrObjectTreeService
            .updateTreeNodeNode(this.currentObjectTreeNode)
            .subscribe((data) => {
                this.currentObjectTreeNode = null;
                this.loadData(this.rootNodeId);
            });
    }

    deleteNode(node: SubscrObjectTree) {
        this.currentObjectTreeNode = null;
        if (node && node.id) {
            this.subscrObjectTreeService
                .deleteTreeNodeNode({ id: node.id })
                .subscribe((data) => this.loadData(this.rootNodeId));
        }
    }

    deleteTree(node: SubscrObjectTree) {
        this.currentObjectTreeNode = null;
        if (node && node.id) {
            this.subscrObjectTreeService
                .deleteTreeNodeNode({ id: node.id })
                .subscribe((data) => {
                        this.eventManager.broadcast({name: SubscrObjectTreeModificationEvent, id: null});
                        this.objectTree = [];
                        this.selectedNode = null;
                    });
        }
    }

    confirmNodeDelete() {
        if (this.selectedNode) {
            this.confirmationService.confirm({
                message: null,
                accept: () => {
                    this.deleteNode(this.selectedNode.data);
                },
                reject: () => {
                }
            });
        }
    }

    confirmTreeDelete() {
        if (this.selectedNode) {
            this.confirmationService.confirm({
                message: null,
                accept: () => {
                    this.deleteTree(this.selectedNode.data);
                },
                reject: () => {
                }
            });
        }
    }
}
