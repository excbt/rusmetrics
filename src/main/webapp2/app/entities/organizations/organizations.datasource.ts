import {Organization} from './organization.model';
import {OrganizationsService} from './organizations.service';
import { ExcPageParams } from '../../shared-blocks';
import {ExcAbstractDataSource} from '../../shared-blocks';

// ***************************************************************************
//
// ***************************************************************************
export class OrganizationsDataSource extends ExcAbstractDataSource<Organization> {

    private subscriberMode: boolean;

    constructor(private organizationsService: OrganizationsService, subscriberMode?: boolean) {
        super();
        this.subscriberMode = (subscriberMode === true);
    }

    //  findSearchPage(pageSorting: ExcPageSorting, pageSize: ExcPageSize, searchString?: string) {
    //  this.startLoading();
    //  this.organizationsService.findSearchPage(pageSorting, pageSize, searchString, this.subscriberMode)
    //      .pipe(
    //          catchError(() => of([])),
    //          finalize(() => this.finishLoading())
    //      )
    //      .subscribe( (page: ExcPage<Organization>) => {
    //          this.nextPage(page);
    //      });
    //  }

     findPage(pageParams: ExcPageParams) {
        this.wrapPageService(this.organizationsService.findPageSubscriber(pageParams, this.subscriberMode));
     }
}
