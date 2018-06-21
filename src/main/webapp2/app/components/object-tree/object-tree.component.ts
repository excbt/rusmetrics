import { Component, OnInit, ViewEncapsulation } from '@angular/core';

@Component({
    selector: 'jhi-object-tree',
    templateUrl: './object-tree.component.html',
    styleUrls: [
        './object-tree.component.scss'
    ],
    encapsulation: ViewEncapsulation.None

})
export class PortalObjectTreeComponent implements OnInit {

    treeNodeId: number;

    constructor() {
        this.treeNodeId = 777;
    }

    ngOnInit() {
    }
}
