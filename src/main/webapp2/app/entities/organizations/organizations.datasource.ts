import {Organization} from './organization.model';
import {OrganizationsService} from './organizations.service';
import {catchError, finalize} from 'rxjs/operators';
import {of} from 'rxjs/observable/of';
import { ExcPageSize, ExcPageSorting, ExcPage } from '../../shared-blocks';
import {AnyModelDataSource} from '../../shared-blocks';

// ***************************************************************************
//
// ***************************************************************************
export class OrganizationsDataSource extends AnyModelDataSource<Organization> {

    private subscriberMode: boolean;

    constructor(private organizationsService: OrganizationsService, subscriberMode?: boolean) {
        super();
        this.subscriberMode = (subscriberMode === true);
    }

     findSearchPage(pageSorting: ExcPageSorting, pageSize: ExcPageSize, searchString?: string) {
     this.startLoading();
     this.organizationsService.findSearchPage(pageSorting, pageSize, searchString, this.subscriberMode)
         .pipe(
             catchError(() => of([])),
             finalize(() => this.finishLoading())
         )
         .subscribe( (page: ExcPage<Organization>) => {
             this.nextPage(page);
         });
     }
}
