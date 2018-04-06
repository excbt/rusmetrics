import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatSort } from '@angular/material';
import { MatPaginator } from '@angular/material/paginator';
import { OrganizationsService } from './organizations.service';
import { OrganizationsDataSource } from './organizations.datasource';
import { Organization } from './organization.model';
import { merge } from 'rxjs/observable/merge';
import { ExcPageSize, ExcPageSorting } from '../shared/pagination-tools';
import { defaultPageSize, defaultPageOptions } from '../shared/pagination-tools';
import {
    // debounceTime,
    distinctUntilChanged,
    // startWith,
    tap
    // , delay
} from 'rxjs/operators';
import { SelectionModel } from '@angular/cdk/collections';
import { FormSearchComponent } from '../blocks/form-search.component';

@Component({
  selector: 'jhi-organizations',
  templateUrl: './organizations.component.html',
  styleUrls: ['./organizations.scss']
})
export class OrganizationsComponent implements OnInit, AfterViewInit {

  selection: SelectionModel<Organization>;
  dataSource: OrganizationsDataSource;

  displayedColumns = ['id', 'inn', 'organizationName', 'ogrn', 'edit'];
  // dataSource = new MatTableDataSource(ELEMENT_DATA);

  rowCount = 10;

  private searchString: String;

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(FormSearchComponent) formSearch: FormSearchComponent;

  constructor(
      private organizationService: OrganizationsService,
  ) {
        const initialSelection = [];
        const allowMultiSelect = false;
        this.selection = new SelectionModel<Organization>(allowMultiSelect, initialSelection);
   }

  ngOnInit() {
    this.dataSource = new OrganizationsDataSource(this.organizationService);
    this.dataSource.totalElements$.subscribe(
      (count) => {
        this.rowCount = count;
      }
    );
  }

  ngAfterViewInit() {
    // server side search
    this.formSearch.searchAction
    .pipe(
      distinctUntilChanged(),
      tap((arg) => {
        this.searchString = arg;
        this.paginator.pageIndex = 0;
        console.log('from search:', arg);
        this.loadOrganization(arg);
      })
    ).subscribe();

    // reset the paginator after sorting
    this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);

    // on sort or paginate events, load a new page
    merge(this.sort.sortChange, this.paginator.page)
    .pipe(
        tap(() => {
          this.loadOrganization();
          console.log('from change');
        })
    ).subscribe();

    this.loadOrganization();
  }

  loadOrganization(searchString?: string) {
    console.log('sort.active:' + this.sort.active + ', sort.direction:' + this.sort.direction);
    const sorting = new ExcPageSorting(this.sort.active, this.sort.direction);
    const pageSize: ExcPageSize = new ExcPageSize(this.paginator.pageIndex, this.paginator.pageSize);
    //  this.dataSource.findAllPaged (sorting, pageSize);
    this.dataSource.findSearchPage (sorting, pageSize, searchString ? searchString : '');
  }

}
