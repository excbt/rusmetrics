import { Component, OnDestroy, AfterViewInit } from '@angular/core';
import { OrganizationsService } from './organizations.service';
import { OrganizationsDataSource } from './organizations.datasource';
import { Organization, organizationModification } from './organization.model';
import { ActivatedRoute, Router } from '@angular/router';
import { ExcListFormComponent, ExcListDatasourceProvider } from '../../shared-blocks/exc-list-form/exc-list-form.component';
import { Subscription } from 'rxjs';
import { subscrUrlSuffix } from '../../shared-blocks/exc-tools/exc-constants';

@Component({
  selector: 'jhi-organizations',
  templateUrl: './organizations.component.html',
  styleUrls: ['./organizations.component.scss', '../blocks/list-form.scss']
})
export class OrganizationsComponent extends ExcListFormComponent<Organization> implements OnDestroy, AfterViewInit {

  private masterColumns = ['select', 'id', 'organizationName', 'inn', 'okpo', 'ogrn' ];
  private subscrColumns = ['select', 'id', 'organizationName', 'inn', 'okpo', 'ogrn' ];

  displayedColumns = this.subscrColumns;

  private routeUrlSubscription: Subscription;
  subscriberMode: boolean;

  constructor(private organizationService: OrganizationsService,
              router: Router,
              activatedRoute: ActivatedRoute) {
    super({modificationEventName: organizationModification},
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

  ngOnDestroy() {
    this.routeUrlSubscription.unsubscribe();
    super.ngOnDestroy();
  }

  navigateNew() {
    this.router.navigate([this.subscriberMode ? subscrUrlSuffix : '', 'organizations', 'new', 'edit']);
  }

  navigateEdit() {

    const superAdminMode =  false; // this.principal.hasAnyAuthorityDirect(['ROLE_ADMIN']);

    if (!this.selection.isEmpty()) {
      if (this.subscriberMode || superAdminMode) {
        this.router.navigate([this.subscriberMode ? subscrUrlSuffix : '', 'organizations', this.selection.selected[0].id, 'edit']);
      } else {
        this.router.navigate([this.subscriberMode ? subscrUrlSuffix : '', 'organizations', this.selection.selected[0].id]);
      }
    }
  }

}
