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
import { ExcFormListMenuComponent } from '../../shared-blocks';
import { ActivatedRoute, Router } from '@angular/router';
import { ExcListFormComponent } from '../../shared-blocks/exc-list-form/exc-list-form.component';
import { JhiEventManager } from 'ng-jhipster';

@Component({
  selector: 'jhi-organizations-g',
  templateUrl: './organizations-g.component.html',
  styleUrls: ['./organizations.scss']
})
export class OrganizationsGComponent extends ExcListFormComponent<Organization> implements OnInit, OnDestroy, AfterViewInit {

  displayedColumns = ['id', 'organizationName', 'inn', 'okpo', 'ogrn', 'edit'];

  constructor( organizationService: OrganizationsService,
              router: Router,
              activatedRoute: ActivatedRoute) {
    super({baseUrl: '/organizations'}, {newDataSource: () => new OrganizationsDataSource(organizationService)}, router, activatedRoute);
  }

}
