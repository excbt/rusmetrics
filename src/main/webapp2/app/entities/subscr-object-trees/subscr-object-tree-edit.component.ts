import { Component } from '@angular/core';
import { SubscrObjectTreeService } from './subscr-object-tree.service';
import { TreeNode } from 'primeng/api';
import { SubscrObjectTree, SubscrObjectTreeVM } from './subscr-object-tree.model';
import { OverlayPanel } from 'primeng/overlaypanel';

@Component({
    selector: 'jhi-subscr-object-tree-edit',
    templateUrl: './subscr-object-tree-edit.component.html',
    styleUrls: ['./subscr-object-tree-edit.component.scss']
})
export class SubscrObjectTreeEditComponent {

    rootNodeId: number;

    nameOfTree: string;

    objectTree: TreeNode[];

    currentObjectTreeNode: SubscrObjectTree;
    newEditVM: SubscrObjectTreeVM;

    data: SubscrObjectTree;

    private treeAddMode: string;

    dialogHeaderKey: string;

    constructor(
        private subscrObjectTreeService: SubscrObjectTreeService
    ) {
        this.subscrObjectTreeService.selectedNodeId$.subscribe((id) => {
            this.rootNodeId = id;
            this.loadData(id);
        });
    }

    loadData(id: number) {
        if (id) {
            this.subscrObjectTreeService.getContObjectType1(id).subscribe((res) => {
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

        const childrenList = objectTree.childObjectList ? objectTree.childObjectList.map((i) => this.convertDataToObjectTree(i)) : [];

        const result: TreeNode =  {
            label: objectTree.objectName,
            data: objectTree,
            children: childrenList,
            expanded: true
        };
        return result;
    }

    addNodeDialog(event, overlaypanel: OverlayPanel, node: SubscrObjectTree, mode: string) {
        this.currentObjectTreeNode = Object.assign({}, node);
        this.treeAddMode = mode;
        this.newEditVM = {
            objectName: ''
        };
        this.dialogHeaderKey = (mode === 'child') ? 'subscrObjectTree.childActionTitle' : 'subscrObjectTree.siblingsActionTitle';
        overlaypanel.toggle(event);
    }

    editNodeDialog(event, overlaypanel: OverlayPanel, node: SubscrObjectTree) {
        this.dialogHeaderKey = 'subscrObjectTree.renameTitle';
        this.currentObjectTreeNode = Object.assign({}, node);
        overlaypanel.toggle(event);
    }

    saveNodeAdd(event, panel: OverlayPanel) {
        if (this.newEditVM) {
            const vm: SubscrObjectTreeVM = {
                objectName: this.newEditVM.objectName,
                parentId: this.treeAddMode === 'child' ? this.currentObjectTreeNode.id : this.currentObjectTreeNode.parentId
            };
            this.subscrObjectTreeService.addTreeNodeNode(vm).subscribe((data) => {
                panel.hide();
                this.currentObjectTreeNode = null;
                this.loadData(this.rootNodeId);
            });
        }
    }

    saveNodeEdit(event, panel: OverlayPanel) {
        if (this.currentObjectTreeNode) {
            this.subscrObjectTreeService.updateTreeNodeNode(this.currentObjectTreeNode).subscribe((data) => {
                panel.hide();
                this.currentObjectTreeNode = null;
                this.loadData(this.rootNodeId);
            });
        }
    }

    cancelDialog(event, panel: OverlayPanel) {
            panel.hide();
            this.currentObjectTreeNode = null;
    }

    switchIsLinkDeny(node: SubscrObjectTree) {
        this.currentObjectTreeNode = Object.assign({}, node);
        this.currentObjectTreeNode.isLinkDeny = this.currentObjectTreeNode.isLinkDeny ? !this.currentObjectTreeNode.isLinkDeny : true;
        this.subscrObjectTreeService.updateTreeNodeNode(this.currentObjectTreeNode).subscribe((data) => {
            this.currentObjectTreeNode = null;
            this.loadData(this.rootNodeId);
        });
    }

    deleteNode(node: SubscrObjectTree) {
        this.currentObjectTreeNode = null;
        if (node && node.id) {
            this.subscrObjectTreeService.deleteTreeNodeNode({id: node.id}).subscribe((data) => this.loadData(this.rootNodeId));
        }
    }

}
