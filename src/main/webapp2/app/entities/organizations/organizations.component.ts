import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
// import { MatSort } from '@angular/material';
// import { MatPaginator } from '@angular/material/paginator';
import { OrganizationsService } from './organizations.service';
import { OrganizationsDataSource } from './organizations.datasource';
import { Organization } from './organization.model';
// import { merge } from 'rxjs/observable/merge';
// import { ExcPageSize, ExcPageSorting } from '../../shared-blocks';
// import { defaultPageSize, defaultPageSizeOptions } from '../../shared-blocks';
// import {
//     // debounceTime,
//     distinctUntilChanged,
//     // startWith,
//     tap
//     // , delay
// } from 'rxjs/operators';
// import { SelectionModel } from '@angular/cdk/collections';
// import { ExcListFormMenuComponent } from '../../shared-blocks';
import { ActivatedRoute, Router } from '@angular/router';
import { ExcListFormComponent, ExcListDatasourceProvider } from '../../shared-blocks/exc-list-form/exc-list-form.component';
import { Subscription } from 'rxjs';
import { subscrUrlSuffix } from '../../shared-blocks/exc-tools/exc-constants';

@Component({
  selector: 'jhi-organizations',
  templateUrl: './organizations.component.html',
  styleUrls: ['./organizations.scss']
})
export class OrganizationsComponent extends ExcListFormComponent<Organization> implements OnInit, OnDestroy, AfterViewInit {

  private masterColumns = ['select', 'id', 'organizationName', 'inn', 'okpo', 'ogrn', 'isCommon' ];
  private subscrColumns = ['select', 'id', 'organizationName', 'inn', 'okpo', 'ogrn', 'isCommon' ];

  displayedColumns = this.subscrColumns;

  private routeUrlSubscription: Subscription;
  subscriberMode: boolean;

  constructor(private organizationService: OrganizationsService,
              router: Router,
              activatedRoute: ActivatedRoute) {
    super({baseUrl: '/organizations'},
          router,
          activatedRoute);
    this.routeUrlSubscription = this.activatedRoute.url.subscribe((data) => {
      this.subscriberMode = (data && (data[0].path ===  subscrUrlSuffix));
      this.displayedColumns = this.subscriberMode ? this.subscrColumns : this.masterColumns;
    });
  }

  getDataSourceProvider(): ExcListDatasourceProvider<Organization>  {
    return {getDataSource: () => new OrganizationsDataSource(this.organizationService, this.subscriberMode)};
  }

  ngOnInit() {
    super.ngOnInit();
  }

  ngOnDestroy() {
    this.routeUrlSubscription.unsubscribe();
    super.ngOnDestroy();
  }

  editNavigate(entityId: any) {
    this.router.navigate([this.subscriberMode ? subscrUrlSuffix : '', 'organizations', entityId, 'edit']);
  }

  navigateNew() {
    this.router.navigate([this.subscriberMode ? subscrUrlSuffix : '', 'organizations', 'new', 'edit']);
  }

  navigateEdit() {
    if (!this.selection.isEmpty()) {
      if (this.subscriberMode) {
        this.router.navigate([this.subscriberMode ? subscrUrlSuffix : '', 'organizations', this.selection.selected[0].id, 'edit']);
      } else {
        this.router.navigate([this.subscriberMode ? subscrUrlSuffix : '', 'organizations', this.selection.selected[0].id]);
      }
    }
  }

}
