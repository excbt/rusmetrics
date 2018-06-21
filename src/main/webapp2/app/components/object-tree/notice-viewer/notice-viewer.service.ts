import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SERVER_API_URL } from '../../../app.constants';
import { Observable } from 'rxjs/observable';

import { Notice } from './notice.model';
import { NoticeWrapperPaged } from './notice-wrapper-paged.model';
import { NoticeWrapper } from './notice-wrapper.model';

import { DateUtils } from '../utils/date-utils';

@Injectable()
export class NoticeViewerService {
//    private DEFULT_PAGE = 0;
//    private DEFAULT_PAGE_SIZE = 100;
    private resourceUrl = SERVER_API_URL + 'api/subscr/contEvent/notifications/paged';

    constructor(private http: HttpClient,
                private dateUtils: DateUtils) {}

    loadNotices(startDate: string, endDate: string, contObjectIds: number[]): Observable<NoticeWrapper[]> {
// console.log('startDate ', startDate);
// console.log('endDate ', endDate);
// console.log('contObjectIds ', contObjectIds);
        const url = this.resourceUrl;
        let httpParams: HttpParams = new HttpParams()
            .set('fromDate', startDate)
            .set('toDate', endDate);
        contObjectIds.forEach((id) => httpParams = httpParams.append('contObjectIds', id.toString()));
//        params.set('contObjectIds', contObjectIds);
// console.log('http params: ', httpParams);
        return this.http.get<NoticeWrapperPaged>(url, {params: httpParams}).map((res) => this.prepareNotices(res.objects));
    }

    prepareNotices(notices: Notice[]): NoticeWrapper[] {
        const noticeWrappers: NoticeWrapper[] = notices.map((notice) => this.prepareNotice(notice));

//        noticeWrapper.push();
        return noticeWrappers;
    }

    prepareNotice(notice: Notice): NoticeWrapper {
        const noticeWrapper: NoticeWrapper = new NoticeWrapper(notice);
//        private USER_DATE_FORMAT: string = 'd.M.yyyy';
//    private USER_TIME_FORMAT: string = 'H:m';
        const timeFormat = this.dateUtils.getUserTimeFormat();
        const dateFormat = this.dateUtils.getUserDateFormat();
        noticeWrapper.setDate(this.dateUtils.dateToString(notice.contEvent.eventTime, dateFormat));
        noticeWrapper.setTime(this.dateUtils.dateToString(notice.contEvent.eventTime, timeFormat));

        return noticeWrapper;
    }
}
