import { Component, Input, OnChanges, OnInit, SimpleChange, Output, EventEmitter } from '@angular/core';
import { ContObjectControl } from './cont-object-control.model';

import { DateUtils } from '../utils/date-utils';

@Component({
    selector: 'jhi-cont-object-notice-dialog',
    templateUrl: './cont-object-notice.dialog.html',
    styleUrls: [
        './cont-object-notice.dialog.scss'
    ]
})
export class ContObjectNoticeDialogComponent implements OnChanges, OnInit {
    @Input() contObject: ContObjectControl;
    @Input() showFlag: boolean;
    @Output() setShowFlag = new EventEmitter<boolean>();

    historyDateRange: Date[] = [new Date(), new Date()];

    eventModeFlag = true;
    historyModeFlag = false;

//    showHistoryFlag = false;
    dateLocale: any;

    constructor(private dateUtils: DateUtils) {
        this.dateLocale = this.dateUtils.dateOptions;
    }

    ngOnChanges(changes: {[propKey: string]: SimpleChange}) {
        for (const propName in changes) {
            if (!changes.hasOwnProperty(propName)) {
                continue;
            }
            console.log('propName: ', propName);
            console.log('changedProp: ', changes[propName].currentValue);
        }
    }

    ngOnInit() {
        console.log('showFlag: ', this.showFlag);
    }

    setHistoryMode() {
        this.eventModeFlag = false;
        this.historyModeFlag = true;

//        this.showHistoryFlag = true;
    }

    setEventMode() {
        this.historyModeFlag = false;
        this.eventModeFlag = true;

//        this.showHistoryFlag = false;
    }

    setDaterange() {
        console.log('historyDateRange: ', this.historyDateRange);

//        const format = this.dateUtils.getSystemFormat();
//        const startDate: string = this.dateUtils.dateToString(this.historyDateRange[0], format);
//        const endDate: string = this.dateUtils.dateToString(this.historyDateRange[1], format);
//        console.log('startDate: ', startDate);
//        console.log('endDate: ', endDate);
    }

    closeWindow() {
        this.showFlag = false;
        this.setShowFlag.emit(false);
    }

}
