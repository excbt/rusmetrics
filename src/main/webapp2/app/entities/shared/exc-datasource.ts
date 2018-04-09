import {CollectionViewer, DataSource, } from '@angular/cdk/collections';
import {Observable} from 'rxjs/Observable';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {catchError, finalize} from 'rxjs/operators';
import {of} from 'rxjs/observable/of';
import { ExcPageSize, ExcPageSorting, ExcPage } from '../shared';

export class AnyModelDataSource<T> implements DataSource<T> {

    modelSubject = new BehaviorSubject<T[]>([]);

    private loadingSubject = new BehaviorSubject<boolean>(false);

    private totalElements = new BehaviorSubject<number>(0);
    private totalPages = new BehaviorSubject<number>(0);

    public loading$ = this.loadingSubject.asObservable();
    public totalElements$ = this.totalElements.asObservable();
    public totalPages$ = this.totalPages.asObservable();

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
      this.loadingSubject.next(true);
    }

    finishLoading() {
      this.loadingSubject.next(false);
    }

}
