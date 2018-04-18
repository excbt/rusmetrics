import { OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { MatSort } from '@angular/material';
import { MatPaginator } from '@angular/material/paginator';
import { merge } from 'rxjs/observable/merge';
import { ExcPageSize, ExcPageSorting } from '../exc-tools/pagination-tools';
import { defaultPageSize, defaultPageSizeOptions } from '../exc-tools/pagination-tools';
import { ActivatedRoute, Router } from '@angular/router';
import { SelectionModel } from '@angular/cdk/collections';
import { Subscription } from 'rxjs/Rx';
import { AnyModelDataSource } from '../exc-tools/exc-datasource';
import { ExcListFormMenuComponent } from '..';
import {
  // debounceTime,
  distinctUntilChanged,
  // startWith,
  tap
  // , delay
} from 'rxjs/operators';

// export interface ExcListFormEntityProvider<T> {
//   load: (pageSorting: ExcPageSorting, pageSize: ExcPageSize, searchString?: string) => Observable<T>;
// }

export interface ExcListDatasourceProvider<T> {
  getDataSource: () => AnyModelDataSource<T>;
}

export interface ExcListFormParams {
  baseUrl: string;
  modificationEventName?: string;
  onSaveUrl?: string;
  onDeleteUrl?: string;
}

export abstract class ExcListFormComponent<T> implements OnInit, OnDestroy, AfterViewInit {

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(ExcListFormMenuComponent) formMenu: ExcListFormMenuComponent;

  selection: SelectionModel<T>;

  private routeDataSubscription: Subscription;
  // private routeUrlSubscription: Subscription;

  // routeUrlSergments: UrlSegment[];
  dataSource: AnyModelDataSource<T>;

  public searchString: String;

  totalElements: number;
  pageSize = defaultPageSize;
  pageSizeOptions = defaultPageSizeOptions;

  constructor(
    private params: ExcListFormParams,
    readonly router: Router,
    readonly activatedRoute: ActivatedRoute,
  ) {
      const initialSelection = [];
      const allowMultiSelect = false;
      this.selection = new SelectionModel<T>(allowMultiSelect, initialSelection);

      this.routeDataSubscription = this.activatedRoute.data.subscribe((data) => {
        if (data['searchParams']) {
          this.searchString = data['searchParams'].searchParams;
        }
      });
      // this.routeUrlSubscription = this.activatedRoute.url.subscribe((data) => this.routeUrlSergments = data);
  }

  abstract getDataSourceProvider(): ExcListDatasourceProvider<T>;

  ngOnInit() {
    this.dataSource = this.getDataSourceProvider().getDataSource();
    this.initSearch();
    this.dataSource.totalElements$.subscribe(
      (count) => {
        this.totalElements = count;
      }
    );
  }

  ngAfterViewInit() {
    // server side search
    this.formMenu.searchAction.pipe(
          distinctUntilChanged(),
          tap((arg) => {
            this.searchString = arg;
            this.paginator.pageIndex = 0;
            this.loadList(arg);
          })
        ).subscribe();

    // reset the paginator after sorting
    this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);

    // on sort or paginate events, load a new page
    merge(this.sort.sortChange, this.paginator.page).pipe(
          tap(() => {
            this.loadList();
            console.log('from change');
          })
      ).subscribe();

  }

  ngOnDestroy() {
    this.routeDataSubscription.unsubscribe();
    // this.routeUrlSubscription.unsubscribe();
  }

  initSearch() {
    const sorting = new ExcPageSorting();
    const pageSize: ExcPageSize = new ExcPageSize();
    this.dataSource.findSearchPage (sorting, pageSize, '');
  }

  loadList(searchString?: string) {
    console.log('sort.active:' + this.sort.active + ', sort.direction:' + this.sort.direction);
    const sorting = new ExcPageSorting(this.sort.active, this.sort.direction);
    const pageSize: ExcPageSize = new ExcPageSize(this.paginator.pageIndex, this.paginator.pageSize);
    this.dataSource.findSearchPage (sorting, pageSize, searchString ? searchString : '');
  }

  previousState() {
    window.history.back();
  }

  newNavigate() {
    this.router.navigate([this.params.baseUrl + '/new/edit']);
  }

  editNavigate(entityId: any) {
    this.router.navigate([this.params.baseUrl + '/' + entityId + '/edit']);
  }

}
