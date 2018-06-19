import { Component, Input, OnInit, OnChanges, SimpleChange } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
import { ContObjectControlService } from './cont-object-control.service';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { from } from 'rxjs/observable/from';
import { merge } from 'rxjs/observable/merge';
import { switchMap, mergeMap } from 'rxjs/operators';

import { ContObjectControl } from './cont-object-control.model';

import { ContObjectControlDataSource } from './cont-object-control.datasource';

import { ContObjectNoticeDialogComponent } from './cont-object-notice.dialog';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

// import { DateUtils } from '../utils/date-utils';

@Component({
    selector: 'jhi-cont-object-control',
    templateUrl: './cont-object-control.component.html',
    styleUrls: [
        './cont-object-control.component.scss'
    ]
})
export class ContObjectControlComponent implements OnInit, OnChanges {
    @Input() contObjectList: number[];
    contObjectControlList: ContObjectControl[];
    cols: ContObjectControlColumn[];
    displayedColumns = ['coName'];

//    contObjectEventViewFlag = false;
    selectedObject: ContObjectControl = new ContObjectControl(0, '', '', '', '');
    dataSource: ContObjectControlDataSource;
    showEventFlag = false;

    eventModeFlag = true;
    historyModeFlag = false;

    showHistoryFlag = false;

    historyDateRange: any;
//    dateLocale: any;

    constructor(private eventManager: JhiEventManager,
                private contObjectControlService: ContObjectControlService,
                private dialog: MatDialog) {}

    ngOnInit() {
// console.log('DateUtils: ', DateUtils);
// console.log('new DateUtils: ', new DateUtils());
// console.log('dateUtils: ', this.dateUtils);
//        this.dateLocale = this.dateUtils.dateOptions;

        this.contObjectControlService.initSvc();

        this.cols = [
            {
                name: 'coSettingMode',
                caption: 'Режим',
                type: 'text',
                displayed: true
            },
            {
                name: 'coBuildingType',
                caption: 'Тип здания',
                type: 'text',
                displayed: true
            },
            {
                name: 'coName',
                caption: 'Название',
                type: 'text',
                displayed: true
            },
            {
                name: 'coHeat',
                caption: 'Тепло',
                type: 'img',
                displayed: true
            },
            {
                name: 'coHw',
                caption: 'ГВС',
                type: 'img',
                displayed: true
            },
            {
                name: 'coCw',
                caption: 'ХВС',
                type: 'img',
                displayed: true
            },
            {
                name: 'coEl',
                caption: 'Э/эн',
                type: 'img',
                displayed: true
            }
        ];

        this.displayedColumns = this.cols.filter((col) => col.displayed).map((col) => col.name);

        this.dataSource = new ContObjectControlDataSource(this.contObjectControlService);

        this.eventManager.subscribe('contObjectListChanged', (eventData) => {
            this.contObjectList = eventData.content;
//            console.log('ContObjectControlComponent: contObjectList: ', this.contObjectList);
            this.performContObjectList();
        });

//        console.log('contObjectList: ', this.contObjectList);
        if (this.contObjectList && this.contObjectList.length > 0) {
            this.performContObjectList();
        }

    }

    ngOnChanges(changes: {[propKey: string]: SimpleChange}) {
        for (const propName in changes) {
            if (!changes.hasOwnProperty(propName)) {
                continue;
            }
//            console.log('COC: propName: ', propName);
//            console.log('COC: changedValue: ', changes[propName].currentValue);
        }
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

    clickObject(object) {
//        this.contObjectEventViewFlag = false;
//        console.log('Click object: ', object);
        if (object && object.contObjectId) {

//            const dialogRef = this.dialog.open(ContObjectNoticeDialogComponent, {
//                height: '500px',
//                width: '700px',
//                data: {contObjectId: object.contObjectId, contObjectName: object.coName}
//            });
//
//            setTimeout(() => {this.contObjectEventViewFlag = true; this.selectedObject = object.contObjectId; console.log('Set time out: ', this.selectedObject); }, 500);

            this.showEventFlag = true;
            this.selectedObject = object;
        }
    }

    getEventFlag(flag: boolean) {
        this.showEventFlag = flag;
    }

//    setHistoryMode() {
//        this.eventModeFlag = false;
//        this.historyModeFlag = true;
//
//        this.showHistoryFlag = true;
//    }
//
//    setEventMode() {
//        this.historyModeFlag = false;
//        this.eventModeFlag = true;
//
//        this.showHistoryFlag = false;
//    }
//
//    setDaterange() {
//        console.log('historyDateRange: ', this.historyDateRange);

//        const format = this.dateUtils.getSystemFormat();
//        const startDate: string = this.dateUtils.dateToString(this.historyDateRange[0], format);
//        const endDate: string = this.dateUtils.dateToString(this.historyDateRange[1], format);
//        console.log('startDate: ', startDate);
//        console.log('endDate: ', endDate);
//    }
}

export interface ContObjectControlColumn {
    name: string;
    caption: string;
    type: string;
    displayed: boolean;
}
