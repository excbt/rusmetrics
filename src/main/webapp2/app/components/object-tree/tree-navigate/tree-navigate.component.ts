import { Component, OnInit, ViewEncapsulation, EventEmitter, Output } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { JhiEventManager } from 'ng-jhipster';
import { TreeNavigateService } from './tree-navigate.service';

// import { SubscrObjectTree } from '../models/subscr-object-tree.model';

import { SubscrContObjectTreeType1 } from '../subscr-tree';
// import { SubscrPrefValue } from '../subscr-pref';
// '../../../../content/fonts/bootstrap/fonts/glyphicons-halflings-regular.ttf'

@Component({
    selector: 'jhi-tree-navigate',
    templateUrl: './tree-navigate.component.html',
    styleUrls: [
        './tree-navigate.component.scss'
    ],
    encapsulation: ViewEncapsulation.None

})
export class TreeNavigateComponent implements OnInit {

    @Output() onSetCurrentNodeId = new EventEmitter<number>();

    tree: TreeNode[];
    treeNodeLoading: boolean;
    subscrObjectTreeList: SubscrContObjectTreeType1[];
//    currentTree: subscrContObjectTreeType1;

    constructor(private treeNavService: TreeNavigateService,
                 private eventManager: JhiEventManager
                ) {}

    ngOnInit() {
//        this.treeNavService.getTree().then((tree) => this.tree = tree);
//        this.treeNavService.loadSubscrTrees().subscribe((trees) => this.successLoadTrees(trees));
        this.treeNodeLoading = true;
        this.treeNavService.initTreeNavigate().subscribe((res) => this.successInit(res));
    }

    successInit(res) {
        this.tree = this.treeNavService.getCurrentTree();
        this.treeNodeLoading = false;
console.log('Tree', this.tree);
        this.eventManager.broadcast({name: 'setTreeNode', content: this.tree[0].data._id});
//        this.onSetCurrentNodeId.emit(this.tree[0].data.getPTreeNodeId());
        this.subscrObjectTreeList = this.treeNavService.getSubscrObjectTreeList();
//        this.tree = this.treeNavService.getTree();
    }

//    successLoadTrees(data) {
// console.log('successLoadTrees', data);
//        this.subscrObjectTreeList = data;
//        this.treeNavService.loadSubscrDefaultTreeSetting().subscribe((treeSetting) => this.successLoadDefaultTreeSetting(treeSetting));
//    }

//    successLoadDefaultTreeSetting(treeSetting: SubscrPrefValue) {
// console.log('successLoadDefaultTree', treeSetting);
// console.log('instanceof treeSettings: ', treeSetting instanceof SubscrPrefValue);
// let spv: SubscrPrefValue = new SubscrPrefValue(treeSetting);
// console.log('spv', spv);
// console.log('instanceof spv: ', spv instanceof SubscrPrefValue);
//        this.currentTree = this.subscrObjectTreeList.filter((objectTree) => objectTree.id === Number(treeSetting.getValue()))[0];
// console.log('Current tree: ', this.currentTree);
        // load default tree with childLevel = 0 - zero lvl of tree
//        this.treeNavService.loadPTree(this.currentTree.id, 0).subscribe((resp) => this.successLoadPTree(resp));
//    }

//    successLoadPTree(resp) {
// console.log(resp);
//        this.tree = resp;
//    }

    loadNode(event) {
        this.treeNodeLoading = true;
console.log(event);
        if (event.node) {
            const ptreeNodeId = event.node.data._id || event.node.data.id || event.node.data.nodeObject.id;
            this.eventManager.broadcast({name: 'setTreeNode', content: ptreeNodeId});

            if (event.node.data.nodeType !== 'ELEMENT') {
                this.treeNodeLoading = false;
                return;
            }

            this.treeNavService.loadPTree(ptreeNodeId, 0).subscribe((resp) => this.successLoadNode(event, resp));
        }
    }

//    selectNode(event) {
// console.log('Select node: ', event);
//        const ptreeNodeId = event.node.data._id || event.node.data.id || event.node.data.nodeObject.id;
//        this.eventManager.broadcast({name: 'setTreeNode', content: ptreeNodeId});
//    }
//
//    clickNode(event) {
// console.log('Click node: ', event);
//        const ptreeNodeId = event.node.data._id || event.node.data.id || event.node.data.nodeObject.id;
//        this.eventManager.broadcast({name: 'setTreeNode', content: ptreeNodeId});
//    }

    successLoadNode(event, resp) {
console.log(resp);
        if (resp.length > 0) {
            event.node.children = resp[0].children;
        }
        this.treeNodeLoading = false;
    }

    changeTree(subscrObjectTree: SubscrContObjectTreeType1) {
        this.treeNodeLoading = true;
        this.treeNavService.loadPTreeMonitorAndPTree(subscrObjectTree.id)
            .subscribe(() => {this.tree = this.treeNavService.getCurrentTree();
                              this.eventManager.broadcast({name: 'setTreeNode', content: this.tree[0].data._id});
                              this.treeNodeLoading = false;
                             });
    }

}
