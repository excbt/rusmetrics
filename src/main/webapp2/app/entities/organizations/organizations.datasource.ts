import {CollectionViewer, DataSource, } from '@angular/cdk/collections';
import {Observable} from 'rxjs/Observable';
import {Organization} from './organization.model';
import {OrganizationsService} from './organizations.service';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {catchError, finalize} from 'rxjs/operators';
import {of} from 'rxjs/observable/of';
import { ExcPageSize, ExcPageSorting, ExcPage } from '../shared';

export class OrganizationsDataSource implements DataSource<Organization> {

    private organizationSubject = new BehaviorSubject<Organization[]>([]);

    private loadingSubject = new BehaviorSubject<boolean>(false);

    private totalElements = new BehaviorSubject<number>(0);
    private totalPages = new BehaviorSubject<number>(0);

    public loading$ = this.loadingSubject.asObservable();
    public totalElements$ = this.totalElements.asObservable();
    public totalPages$ = this.totalPages.asObservable();

    constructor(private organizationsService: OrganizationsService) {

    }

    findAll(field = 'id', sortOrder = 'asc' ) {
        this.loadingSubject.next(true);

        this.organizationsService.findAll(field, sortOrder).pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe( (organizations) => this.organizationSubject.next(organizations));
    }

    // findAllPaged(pageSorting: ExcPageSorting, pageSize: ExcPageSize) {
    //     this.loadingSubject.next(true);

    //     this.organizationsService.findAllPaged(pageSorting, pageSize).pipe(
    //             catchError(() => of([])),
    //             finalize(() => this.loadingSubject.next(false))
    //         )
    //         .subscribe( (organizations) => this.organizationSubject.next(organizations));
    // }

    findAllPage(pageSorting: ExcPageSorting, pageSize: ExcPageSize) {
        this.loadingSubject.next(true);

        this.organizationsService.findAllPage(pageSorting, pageSize).pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe( (page: ExcPage<Organization>) => {
                this.organizationSubject.next(page.content);
                this.totalElements.next(page.totalElements);
                this.totalPages.next(page.totalPages);
            });
    }

    findSearchPage(pageSorting: ExcPageSorting, pageSize: ExcPageSize, searchString?: string) {
        this.loadingSubject.next(true);
        this.organizationsService.findSearchPage(pageSorting, pageSize, searchString).pipe(
            catchError(() => of([])),
            finalize(() => this.loadingSubject.next(false))
        )
        .subscribe( (page: ExcPage<Organization>) => {
            this.organizationSubject.next(page.content);
            this.totalElements.next(page.totalElements);
            this.totalPages.next(page.totalPages);
        });
    }

    connect(collectionViewer: CollectionViewer): Observable<Organization[]> {
        return this.organizationSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.organizationSubject.complete();
        this.loadingSubject.complete();
    }

}
