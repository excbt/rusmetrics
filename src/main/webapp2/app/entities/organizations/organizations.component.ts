import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { MatSort } from '@angular/material';
import { MatPaginator } from '@angular/material/paginator';
import { OrganizationsService } from './organizations.service';
import { OrganizationsDataSource } from './organizations.datasource';
import { Organization } from './organization.model';
import { merge } from 'rxjs/observable/merge';
import { ExcPageSize, ExcPageSorting } from '../../shared-blocks';
import { defaultPageSize, defaultPageOptions } from '../../shared-blocks';
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

@Component({
  selector: 'jhi-organizations',
  templateUrl: './organizations.component.html',
  styleUrls: ['./organizations.scss']
})
export class OrganizationsComponent extends ExcListFormComponent<Organization> implements OnInit, OnDestroy, AfterViewInit {

  displayedColumns = ['id', 'organizationName', 'inn', 'okpo', 'ogrn', 'isCommon', 'edit'];

  private routeUrlSubscription: Subscription;
  isSubscriberMode: boolean;
  private subscriberMode: BehaviorSubject<boolean> = new BehaviorSubject(true);
  public subscriberMode$ = this.subscriberMode.asObservable();

  constructor(private organizationService: OrganizationsService,
              router: Router,
              activatedRoute: ActivatedRoute) {
    super({baseUrl: '/organizations'},
          router,
          activatedRoute);
    this.routeUrlSubscription = this.activatedRoute.url.subscribe((data) => {
      this.subscriberMode.next(this.isSubscriberMode = (data && (data[0].path === 'subscr')));
    });
  }

  getDatasourceProvider(): ExcListDatasourceProvider<Organization>  {
    return {getDataSource: () => new OrganizationsDataSource(this.organizationService, this.isSubscriberMode)};
  }

  ngOnInit() {
    super.ngOnInit();
  }

  ngOnDestroy() {
    this.routeUrlSubscription.unsubscribe();
    super.ngOnDestroy();
  }
}
