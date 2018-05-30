import { Component, OnInit, Input } from '@angular/core';
@Component({
    selector: 'jhi-cont-object-control',
    templateUrl: './cont-object-control.component.html'
})
export class ContObjectControlComponent implements OnInit {
    @Input() contObjectList: number[];

    constructor() {}

    ngOnInit() {
        console.log('ContObjectControlComponent: contObjectList: ', this.contObjectList);
    }
}
