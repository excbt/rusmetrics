// import { MomentModule } from 'angular-2-moment/moment.module';
import { Injectable } from '@angular/core';
import { DatePipe } from '@angular/common';

@Injectable()
export class DateUtils {

    private USER_DATE_TIME_FORMAT = 'dd.MM.yyyy HH:mm';
    private USER_DATE_FORMAT = 'dd.MM.yyyy';
    private USER_TIME_FORMAT = 'HH:mm';
    private SYSTEM_DATE_TIME_FORMAT = 'yyyy-MM-dd HH:mm';
    private SYSTEM_DATE_FORMAT = 'yyyy-MM-dd';

    public dateOptions = {
        firstDateOfWeek: 1,
        dayNames: ['понедельник', 'вторник', 'среда', 'четверг', 'пятница', 'субботк', 'воскресенье'],
        dayNamesShort: ['пн', 'вт', 'ср', 'чт', 'пт', 'сб', 'вс'],
        dayNamesMin: ['П', 'В', 'С', 'Ч', 'П', 'С', 'В'],
        monthNames: ['январь', 'февраль', 'март', 'апрель', 'май', 'июнь', 'июль', 'август', 'сентябрь', 'октябрь', 'ноябрь', 'декабрь'],
        monthNamesShort: ['янв', 'фев', 'мар', 'апр', 'май', 'июн', 'июл', 'авг', 'сен', 'окт', 'ноя', 'дек'],
        today: 'Сегодня',
        clear: 'Очистить'
    };

    constructor() {}

    getSystemFormat(): string {
        return this.SYSTEM_DATE_FORMAT;
    }

    dateToString(date: Date | number, formatString: string) {
        const pipe = new DatePipe('en-US');
// console.log('dateUtils pipe: ', pipe.transform(date, formatString));
// console.log('dateUtils formatDate: ', formatDate(date, formatString));
//        return '';
        return pipe.transform(date, formatString);
//        return formatDate(date, formatString);

    }

    getUserTimeFormat() {
        return this.USER_TIME_FORMAT;
    }
    getUserDateFormat() {
        return this.USER_DATE_FORMAT;
    }

    getUserDateTimeFormat() {
        return this.USER_DATE_TIME_FORMAT;
    }

    getSystemDateTimeFormat() {
        return this.SYSTEM_DATE_TIME_FORMAT;
    }

}

export class DateWrapper {

    private year: number;
    private month: number;
    private day: number;
    private hour: number;
    private minute: number;
//    private second: number;

    constructor(private dateArr: number[]) {
        // inputArr: [yyyy, MM, DD, HH, mm, ss, ???]
        if (!this.dateArr || this.dateArr.length === 0) {
            console.warn('Date array is empty! ', dateArr);
            return;
        }
        this.year = this.dateArr[0];
        this.month = this.dateArr[1];
        this.day = this.dateArr[2];
        this.hour = this.dateArr[3];
        this.minute = this.dateArr[4];
//        this.second = dateArr[5];
    }

    private performDatePart(datePart: number): string {
        let performedDatePart: string = datePart.toString();
        if (datePart >= 0 && datePart <= 9) {
            performedDatePart = '0' + performedDatePart;
        }
        return performedDatePart;
    }

    getFullDate(): string {
        return this.performDatePart(this.day) + '.'
            + this.performDatePart(this.month) + '.'
            + this.performDatePart(this.year) + ' '
            + this.performDatePart(this.hour) + ':'
            + this.performDatePart(this.minute);
    }

    getDate(): string {
        return this.performDatePart(this.day) + '.' + this.performDatePart(this.month) + '.' + this.performDatePart(this.year);
    }

    getTime(): string {
        return this.performDatePart(this.hour) + ':' + this.performDatePart(this.minute);
    }

}
