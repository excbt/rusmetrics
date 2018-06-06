import { Component, Input, OnInit } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
import { ContObjectControlService } from './cont-object-control.service';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { from } from 'rxjs/observable/from';
import { merge } from 'rxjs/observable/merge';
import { switchMap, mergeMap } from 'rxjs/operators';

import { ContObjectControl } from './cont-object-control.model';

import { ContObjectControlDataSource } from './cont-object-control.datasource';

@Component({
    selector: 'jhi-cont-object-control',
    templateUrl: './cont-object-control.component.html'
})
export class ContObjectControlComponent implements OnInit {
    @Input() contObjectList: number[];
    contObjectControlList: ContObjectControl[];
    cols: ContObjectControlColumn[];
    displayedColumns = ['coName'];

    dataSource: ContObjectControlDataSource;

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

        this.dataSource = new ContObjectControlDataSource(this.contObjectControlService);

        this.eventManager.subscribe('contObjectListChanged', (eventData) => {
            this.contObjectList = eventData.content;
            console.log('ContObjectControlComponent: contObjectList: ', this.contObjectList);
            this.performContObjectList();
        });

    }

    performContObjectList() {

        this.dataSource.loadContObjectControlList(this.contObjectList);
//        const resArr: ContObjectControl[] = [];
//        this.dataSource = of(resArr);
//        this.dataSource = [];
//        merge(...loadingList).flatMap((res) => {console.log(res); /*this.dataSource.push(res)*/});// .subscribe((subscr) => console.log(subscr));
//        .subscribe((res: ContObjectControl) => {
//            console.log('Merge res: ', res);
//            resArr.push(res);
////            this.dataSource.push(res);
//            console.log('this.dataSource: ', this.dataSource);
//
//        });
    }

    performContObjectListItem(contObjectId: number) {
        this.contObjectControlService
            .loadMonitorState(contObjectId.toString())
            .subscribe((res) => this.contObjectControlList.push(res));
    }
}

export interface ContObjectControlColumn {
    name: string;
    caption: string;
}
