import { Component, OnInit, Input, OnChanges, SimpleChange } from '@angular/core';
// import { Notice } from './notice.model';
import { NoticeViewerService } from './notice-viewer.service';
// import { Observable } from 'rxjs/observable';
import { DateUtils } from '../utils';

@Component({
    selector: 'jhi-notice-viewer',
    templateUrl: './notice-viewer.component.html',
    styleUrls: [
        './notice-viewer.component.scss'
    ]
})
export class NoticeViewerComponent implements OnInit, OnChanges {
    @Input() dateRange: Date[];
    @Input() contObjectIds: number[];
    @Input() contServiceTypes: string[];

    displayedColumns = ['noticeServiceType', 'noticeColorState', 'noticeDate', 'noticeTime', 'noticeMessage'];

    dataSource: any;

    constructor(private noticeService: NoticeViewerService,
                private dateUtils: DateUtils) {}
// loadNotices(startDate: string, endDate: string, contObjectIds: number[]): Observable<Notice[]>
    ngOnInit() {
//        const format: string = this.dateUtils.getSystemFormat();
//        const startDate: string = this.dateUtils.dateToString(this.dateRange[0], format);
//        const endDate: string = this.dateUtils.dateToString(this.dateRange[1], format);
//
//        this.noticeService
//            .loadNotices(startDate, endDate, this.contObjectIds)
//            .subscribe((res) => console.log(res));
    }

    ngOnChanges(changes: {[propKey: string]: SimpleChange}) {
        for (const propName in changes) {
            if (!changes.hasOwnProperty(propName)) {
                continue;
            }
            if (propName !== 'dateRange') {
                return;
            }
            const changedProp = changes[propName];
//            console.log('propName: ', propName);
//            console.log('changedProp: ', changedProp);

            const dateRange: Date[] = changedProp.currentValue;
            if (!dateRange) {
                return;
            }
            if (dateRange.hasOwnProperty('length') && dateRange.length === 0) {
                return;
            }

            if ((!dateRange[0] || dateRange[0] === null) || (!dateRange[1] || dateRange[1] === null)) {
                return;
            }

            const format: string = this.dateUtils.getSystemFormat();
            const startDate: string = dateRange[0] && dateRange[0] !== null ? this.dateUtils.dateToString(dateRange[0], format) : null;
            const endDate: string = dateRange[1] && dateRange[1] !== null ? this.dateUtils.dateToString(dateRange[1], format) : null;
// console.log(startDate);
// console.log(endDate);
            this.noticeService
                .loadNotices(startDate, endDate, this.contObjectIds, this.contServiceTypes)
                .subscribe((res) => {/*console.log(res);*/this.dataSource = res; });
        }
    }
}
