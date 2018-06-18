import { BehaviorSubject, Observable } from 'rxjs';
import { Inject, Injectable } from '@angular/core';
import { timer } from 'rxjs/observable/timer';
import { takeUntil } from 'rxjs/operators';

const loadingDelayMillis = 100;

@Injectable()
export class LoadingStatusService {
    private loadingSubject = new BehaviorSubject<boolean>(false);

    private loadingDebug = new BehaviorSubject<boolean>(false);

    private loadingDebug$ = this.loadingDebug.asObservable();

    readonly loading$: Observable<boolean>;

    constructor() {
        this.loading$ = this.loadingSubject.asObservable();
    }

    startRequest() {
        this.loadingDebug.next(true);
        const requestCompleteThreshosd$ = this.loadingDebug$.filter((v) => v === false);
        timer(loadingDelayMillis).pipe(
             takeUntil(requestCompleteThreshosd$)
        ).subscribe(() => this.loadingSubject.next(true));
    }

    stopRequest() {
        this.loadingSubject.next(false);
        this.loadingDebug.next(false);
    }
}
