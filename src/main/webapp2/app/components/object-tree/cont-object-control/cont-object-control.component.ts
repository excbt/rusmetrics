import { Component, Input, OnInit } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
import { ContObjectControlService } from './cont-object-control.service';

import { ContObjectControl } from './cont-object-control.model';

@Component({
    selector: 'jhi-cont-object-control',
    templateUrl: './cont-object-control.component.html'
})
export class ContObjectControlComponent implements OnInit {
    @Input() contObjectList: number[];
    contObjectControlList: ContObjectControl[];
    cols: any[];

    constructor(private eventManager: JhiEventManager,
                private contObjectControlService: ContObjectControlService) {}

    ngOnInit() {
        
        this.contObjectControlService.initSvc();
        
        this.cols = [
            {
                name: 'coSettingMode',
                caption: 'Режим'
            },
            {
                name: 'coBuildingType',
                caption: 'Тип здания'
            },
            {
                name: 'coName',
                caption: 'Название'
            },
            {
                name: 'coHeat',
                caption: 'Heat'
            },
            {
                name: 'coHw',
                caption: 'Hw'
            },
            {
                name: 'coCw',
                caption: 'Cw'
            },
            {
                name: 'coEl',
                caption: 'El'
            }
        ];
        
        this.eventManager.subscribe('contObjectListChanged', (eventData) => {
            this.contObjectList = eventData.content;
            console.log('ContObjectControlComponent: contObjectList: ', this.contObjectList);
            this.performContObjectList();
        });
    }
    
    performContObjectList() {
        this.contObjectControlList = [];
        
        this.contObjectList
            .map((co) => this.performContObjectListItem(co));
    }
    
    performContObjectListItem(contObjectId: number) {
        this.contObjectControlService
            .loadMonitorState(contObjectId.toString())
            .subscribe((res)=> this.contObjectControlList.push(res));
    }
}
