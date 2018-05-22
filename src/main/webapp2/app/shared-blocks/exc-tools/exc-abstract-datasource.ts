import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

export abstract class ExcAbstractDataSource<T> implements DataSource<T> {

    modelSubject = new BehaviorSubject<T[]>([]);

    private loadingSubject = new BehaviorSubject<boolean>(false);

    private totalElements = new BehaviorSubject<number>(0);
    private totalPages = new BehaviorSubject<number>(0);

    public loading$ = this.loadingSubject.asObservable();
    public totalElements$ = this.totalElements.asObservable();

    connect(collectionViewer: CollectionViewer): Observable<T[]> {
        return this.modelSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.modelSubject.complete();
        this.loadingSubject.complete();
    }

    startLoading() {
      this.loadingSubject.next(true);
    }

    finishLoading() {
      this.loadingSubject.next(false);
    }

    abstract findData();

    wrapPageService(anyService: Observable<T[]>) {
        this.startLoading();
        anyService
            .pipe(
                catchError(() => of([])),
                finalize(() => this.finishLoading())
            )
            .subscribe( (data) => {
                this.finishLoading();
                this.nextData(data);
            });
    }

    makeEmpty() {
        this.nextData([]);
    }

    nextData(data: T[]) {
        this.modelSubject.next(data);
        this.totalElements.next(data.length);
    }

}
