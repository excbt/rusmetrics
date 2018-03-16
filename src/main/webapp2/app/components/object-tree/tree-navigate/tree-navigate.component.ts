import { Component, OnInit } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { TreeNavigateService } from './tree-navigate.service';

@Component({
    selector: 'jhi-tree-navigate',
    templateUrl: './tree-navigate.component.html',
    styleUrls: [
        './tree-navigate.component.scss'
    ]

})
export class TreeNavigateComponent implements OnInit {
    tree: TreeNode[];

    constructor(private treeNavService: TreeNavigateService) {
    }

    ngOnInit() {
        this.treeNavService.getTree().then((tree) => this.tree = tree);
    }

}
