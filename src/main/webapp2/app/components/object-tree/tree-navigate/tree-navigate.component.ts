import { Component, OnInit } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { TreeNavigateService } from './tree-navigate.service';

import { SubscrObjectTree } from '../models/subscr-object-tree.model';

@Component({
    selector: 'jhi-tree-navigate',
    templateUrl: './tree-navigate.component.html',
    styleUrls: [
        './tree-navigate.component.scss'
    ]

})
export class TreeNavigateComponent implements OnInit {
    tree: TreeNode[];
    subscrObjectTreeList: SubscrObjectTree[];
    currentTree: SubscrObjectTree;

    constructor(private treeNavService: TreeNavigateService) {
    }

    ngOnInit() {
//        this.treeNavService.getTree().then((tree) => this.tree = tree);
        this.treeNavService.loadSubscrTrees().subscribe((trees) => this.successLoadTrees(trees));
    }

    successLoadTrees(data) {
// console.log('successLoadTrees', data);
        this.subscrObjectTreeList = data;
        this.treeNavService.loadSubscrDefaultTreeSetting().subscribe((treeSetting) => this.successLoadDefaultTreeSetting(treeSetting));
    }

    successLoadDefaultTreeSetting(treeSetting) {
// console.log('successLoadDefaultTree', treeSetting);
        this.currentTree = this.subscrObjectTreeList.filter((objectTree) => objectTree.id === Number(treeSetting.value))[0];
// console.log('Current tree: ', this.currentTree);
        // load default tree with childLevel = 0 - zero lvl of tree
        this.treeNavService.loadPTree(this.currentTree.id, 0).subscribe((resp) => this.successLoadPTree(resp));
    }

    successLoadPTree(resp) {
console.log(resp);
        this.tree = resp;
    }

    loadNode(event) {
console.log(event);
        if (event.node) {
            const ptreeNodeId = event.node.data._id || event.node.data.id || event.node.data.nodeObject.id;
            this.treeNavService.loadPTree(ptreeNodeId, 0).subscribe((resp) => this.successLoadNode(event, resp));
        }
    }
    
    successLoadNode(event, resp) {
console.log(resp);
        if (resp.length > 0) {
            event.node.children = resp[0].children;
        }
    }

}
