import { Component, OnInit } from '@angular/core';

@Component({
    selector: 'jhi-tree-node-information',
    templateUrl: './tree-node-information.component.html'
})
export class TreeNodeInformationComponent implements OnInit {
    constructor() {
        console.log('TreeNodeInformationComponent: Constructor()');
    }

    ngOnInit() {
        console.log('TreeNodeInformationComponent ngOnInit()');
        console.error('TEST ERRROR');
    }
}
