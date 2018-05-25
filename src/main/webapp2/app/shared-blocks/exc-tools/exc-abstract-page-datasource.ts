import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { ExcPage, ExcPageParams } from '../../shared-blocks';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

export abstract class ExcAbstractPageDataSource<T> implements DataSource<T> {

    modelSubject = new BehaviorSubject<T[]>([]);

    private loadingSubject = new BehaviorSubject<boolean>(false);

    private totalElements = new BehaviorSubject<number>(0);
    private totalPages = new BehaviorSubject<number>(0);

    public loading$ = this.loadingSubject.asObservable();
    public totalElements$ = this.totalElements.asObservable();
    public totalPages$ = this.totalPages.asObservable();

    readonly startLoadingDelay = 160;

    connect(collectionViewer: CollectionViewer): Observable<T[]> {
        return this.modelSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.modelSubject.complete();
        this.loadingSubject.complete();
    }

    nextPage(page: ExcPage<T>) {
      this.modelSubject.next(page.content);
      this.totalElements.next(page.totalElements);
      this.totalPages.next(page.totalPages);
    }

    startLoading() {
      Observable.timer(this.startLoadingDelay).takeUntil(this.modelSubject).subscribe(() => this.loadingSubject.next(true));
    }

    finishLoading() {
      this.loadingSubject.next(false);
    }

    abstract findPage(params: ExcPageParams);

    wrapPageService(anyService: Observable<ExcPage<T>>) {
        this.startLoading();
        anyService
            .pipe(
                catchError(() => of([])),
                finalize(() => this.finishLoading())
            )
            .subscribe( (page: ExcPage<T>) => {
                this.finishLoading();
                this.nextPage(page);
            });
    }

    makeEmpty() {
        this.nextPage(new ExcPage([], 0, 0, 0));
    }
}