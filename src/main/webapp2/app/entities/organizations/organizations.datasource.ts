import {CollectionViewer, DataSource, } from '@angular/cdk/collections';
import {Observable} from 'rxjs/Observable';
import {Organization} from './organization.model';
import {OrganizationSort} from './organization.model';
import {OrganizationsService} from './organizations.service';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {catchError, finalize} from 'rxjs/operators';
import {of} from 'rxjs/observable/of';
import {MatTableDataSource, MatSort} from '@angular/material';

export class OrganizationsDataSource implements DataSource<Organization> {

    private lessonsSubject = new BehaviorSubject<Organization[]>([]);

    private loadingSubject = new BehaviorSubject<boolean>(false);

    public loading$ = this.loadingSubject.asObservable();

    constructor(private organizationsService: OrganizationsService) {

    }

    findAll(field = 'id', sortOrder = 'asc' ) {
        this.loadingSubject.next(true);

        this.organizationsService.findAll(field, sortOrder).pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe( (organizations) => this.lessonsSubject.next(organizations));
    }

    connect(collectionViewer: CollectionViewer): Observable<Organization[]> {
        return this.lessonsSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.lessonsSubject.complete();
        this.loadingSubject.complete();
    }

}
