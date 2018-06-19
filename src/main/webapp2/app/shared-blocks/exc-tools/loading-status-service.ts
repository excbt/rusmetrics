import { BehaviorSubject } from 'rxjs';
import { Injectable } from '@angular/core';
import { timer } from 'rxjs/observable/timer';
import { takeUntil } from 'rxjs/operators';
import { ExcConsoleLogService } from './exc-console-log-service';

const loadingDelayMillis = 10;

@Injectable()
export class LoadingStatusService {
    private loadingTimerSubject = new BehaviorSubject<boolean>(false);

    private loadingSignal = new BehaviorSubject<boolean>(false);

    private loadingSignal$ = this.loadingSignal.asObservable();

    readonly loadingTimer$ = this.loadingTimerSubject.asObservable();
    readonly loading$ = this.loadingSignal.asObservable();

    private consoleService = new ExcConsoleLogService();

    constructor() {
    }

    startRequest() {
        this.loadingSignal.next(true);
        const requestCompleteThreshosd$ = this.loadingSignal$.filter((v) => v === false);
        timer(loadingDelayMillis).pipe(
            takeUntil(requestCompleteThreshosd$)
        ).subscribe(() => {
            this.loadingTimerSubject.next(true);
            this.consoleService.log('Loading timer TRUE');
        });

    }

    stopRequest() {
        this.loadingTimerSubject.next(false);
        this.loadingSignal.next(false);
    }

    stopRequestNoTimer(err?: string) {
        this.loadingSignal.next(false);
        this.loadingTimerSubject.next(false);
        if (err) {
            this.consoleService.error(err);
        }
    }

}
