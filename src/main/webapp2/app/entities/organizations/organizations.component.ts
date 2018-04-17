import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { MatSort } from '@angular/material';
import { MatPaginator } from '@angular/material/paginator';
import { OrganizationsService } from './organizations.service';
import { OrganizationsDataSource } from './organizations.datasource';
import { Organization } from './organization.model';
import { merge } from 'rxjs/observable/merge';
import { ExcPageSize, ExcPageSorting } from '../../shared-blocks';
import { defaultPageSize, defaultPageSizeOptions } from '../../shared-blocks';
import {
    // debounceTime,
    distinctUntilChanged,
    // startWith,
    tap
    // , delay
} from 'rxjs/operators';
import { SelectionModel } from '@angular/cdk/collections';
import { ExcListFormMenuComponent } from '../../shared-blocks';
import { ActivatedRoute, Router, UrlSegment } from '@angular/router';
import { ExcListFormComponent, ExcListDatasourceProvider } from '../../shared-blocks/exc-list-form/exc-list-form.component';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription, BehaviorSubject } from 'rxjs';
import { subscrUrlSuffix } from '../../shared-blocks/exc-tools/exc-constants';

@Component({
  selector: 'jhi-organizations',
  templateUrl: './organizations.component.html',
  styleUrls: ['./organizations.scss']
})
export class OrganizationsComponent extends ExcListFormComponent<Organization> implements OnInit, OnDestroy, AfterViewInit {

  displayedColumns = ['id', 'organizationName', 'inn', 'okpo', 'ogrn', 'isCommon', 'edit'];

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
    });
  }

  getDatasourceProvider(): ExcListDatasourceProvider<Organization>  {
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

  newNavigate() {
    this.router.navigate([this.subscriberMode ? subscrUrlSuffix : '', 'organizations', 'new', 'edit']);
  }

}
