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

@Component({
  selector: 'jhi-organizations',
  templateUrl: './organizations.component.html',
  styleUrls: ['./organizations.scss']
})
export class OrganizationsComponent implements OnInit, OnDestroy, AfterViewInit {

  selection: SelectionModel<Organization>;
  dataSource: OrganizationsDataSource;

  displayedColumns = ['id', 'organizationName', 'inn', 'okpo', 'ogrn', 'edit'];

  rowCount = 10;

  private searchString: String;

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(ExcFormListMenuComponent) formMenu: ExcFormListMenuComponent;

  routeData: any;

  constructor(
      private organizationService: OrganizationsService,
      private router: Router,
      private activatedRoute: ActivatedRoute,
  ) {
        const initialSelection = [];
        const allowMultiSelect = false;
        this.selection = new SelectionModel<Organization>(allowMultiSelect, initialSelection);

        this.routeData = this.activatedRoute.data.subscribe((data) => {
          if (data['searchParams']) {
            this.searchString = data['searchParams'].searchParams;
          }
      });
   }

  ngOnInit() {
    this.dataSource = new OrganizationsDataSource(this.organizationService);
    this.initSearch();
    this.dataSource.totalElements$.subscribe(
      (count) => {
        this.rowCount = count;
      }
    );
  }

  ngOnDestroy() {
    this.routeData.unsubscribe();
  }

  ngAfterViewInit() {
    // server side search
    this.formMenu.searchAction.pipe(
          distinctUntilChanged(),
          tap((arg) => {
            this.searchString = arg;
            this.paginator.pageIndex = 0;
            this.loadOrganization(arg);
          })
        ).subscribe();

    // reset the paginator after sorting
    this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);

    // on sort or paginate events, load a new page
    merge(this.sort.sortChange, this.paginator.page).pipe(
          tap(() => {
            this.loadOrganization();
            console.log('from change');
          })
      ).subscribe();

  }

  loadOrganization(searchString?: string) {
    console.log('sort.active:' + this.sort.active + ', sort.direction:' + this.sort.direction);
    const sorting = new ExcPageSorting(this.sort.active, this.sort.direction);
    const pageSize: ExcPageSize = new ExcPageSize(this.paginator.pageIndex, this.paginator.pageSize);
    //  this.dataSource.findAllPaged (sorting, pageSize);
    this.dataSource.findSearchPage (sorting, pageSize, searchString ? searchString : '');
  }

  initSearch() {
    const sorting = new ExcPageSorting();
    const pageSize: ExcPageSize = new ExcPageSize(0, defaultPageOptions[0]);
    this.dataSource.findSearchPage (sorting, pageSize, '');
  }

  newRecord() {
    this.router.navigate(['/organizations/new/edit']);
  }

}
