import {CollectionViewer, DataSource, } from '@angular/cdk/collections';
import {Observable} from 'rxjs/Observable';
import {Organization} from './organization.model';
import {OrganizationsService} from './organizations.service';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {catchError, finalize} from 'rxjs/operators';
import {of} from 'rxjs/observable/of';
import { ExcPageSize, ExcPageSorting, ExcPage } from '../../shared-blocks';
import {AnyModelDataSource} from '../../shared-blocks';

// ***************************************************************************
//
// ***************************************************************************
export class OrganizationsDataSource extends AnyModelDataSource<Organization> {

    constructor(private organizationsService: OrganizationsService) {
        super();
   }

   findSearchPage(pageSorting: ExcPageSorting, pageSize: ExcPageSize, searchString?: string) {
    this.startLoading();
    this.organizationsService.findSearchPage(pageSorting, pageSize, searchString)
        .pipe(
            catchError(() => of([])),
            finalize(() => this.finishLoading())
        )
        .subscribe( (page: ExcPage<Organization>) => {
            this.nextPage(page);
        });
    }

}
