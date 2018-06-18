import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

export class ExcLoadingTool {
    static wrapAnyServiceArr<T>(loadingSubject: BehaviorSubject<boolean>, anyService: Observable<T[]>): Observable<T[]> {
        loadingSubject.next(true);
        return anyService
            .pipe(
                catchError(() => of([])),
                finalize(() => loadingSubject.next(false))
            );
    }

    static wrapAnyService<T>(loadingSubject: BehaviorSubject<boolean>, anyService: Observable<T>): Observable<T> {
        loadingSubject.next(true);
        return anyService
            .pipe(
                catchError(() => of(null)),
                finalize(() => loadingSubject.next(false))
            );
    }
}
