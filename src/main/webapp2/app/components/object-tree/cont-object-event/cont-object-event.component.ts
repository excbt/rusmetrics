import { Component, OnInit, Input } from '@angular/core';
import { ContObjectEventService } from './cont-object-event.service';
import { ContObjectEvent } from './cont-object-event.model';
import { ContObjectEventWrapper } from './cont-object-event-wrapper.model';

import { DateWrapper } from '../utils/date-utils';

@Component({
    selector: 'jhi-cont-object-event',
    templateUrl: './cont-object-event.component.html',
    styleUrls: [
        './cont-object-event.component.scss'
    ]
})
export class ContObjectEventComponent implements OnInit {
    @Input() contObjectId: number;
    contObjectEventList: ContObjectEvent[];
    contObjectEventWrapperList: ContObjectEventWrapper[];
    dataSource: ContObjectEventWrapper[];
    displayedColumns = ['contServiceType', 'contEventColorState', 'contEventDate', 'contEventTime', 'contEventMessage'];

    constructor(private contObjectEventService: ContObjectEventService) {}

    ngOnInit() {
        console.log('ContObjectEventComponent: contObjectId: ', this.contObjectId);
        if (this.contObjectId) {
            this.contObjectEventService
                .loadEvents(this.contObjectId.toString())
                .subscribe((res) => { 
                    console.log(res); this.contObjectEventList = res; 
                    this.contObjectEventWrapperList = this.contObjectEventList
                        .map((coe) => {
                            const coew: ContObjectEventWrapper = new ContObjectEventWrapper(coe);
                            const datew: DateWrapper = new DateWrapper(coe.contEventTime);
                            coew.setContEventDisplayedDate(datew.getDate());
                            coew.setContEventDisplayedTime(datew.getTime());
                            return coew;
                        });
                    this.dataSource = this.contObjectEventWrapperList;
                });
        }
    }
}
