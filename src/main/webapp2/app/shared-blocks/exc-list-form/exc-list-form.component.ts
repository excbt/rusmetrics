import { OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { MatSort } from '@angular/material';
import { MatPaginator } from '@angular/material/paginator';
import { merge } from 'rxjs/observable/merge';
import { ExcPageSize, ExcPageSorting } from '../exc-tools/exc-pagination';
import { defaultPageSize, defaultPageSizeOptions } from '../exc-tools/exc-pagination';
import { ActivatedRoute, Router } from '@angular/router';
import { SelectionModel } from '@angular/cdk/collections';
import { Subscription } from 'rxjs/Rx';
import { ExcAbstractPageDataSource } from '../exc-tools/exc-abstract-page-datasource';
import { ExcListFormMenuComponent } from '..';
import {
  // debounceTime,
  distinctUntilChanged,
  // startWith,
  tap
  // , delay
} from 'rxjs/operators';

export interface ExcListDatasourceProvider<T> {
  getDataSource: () => ExcAbstractPageDataSource<T>;
}

export interface ExcListFormParams {
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
  dataSource: ExcAbstractPageDataSource<T>;

  public searchString: String;

  totalElements: number;
  pageSize = defaultPageSize;
  pageSizeOptions = defaultPageSizeOptions;

  constructor(
    readonly params: ExcListFormParams,
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
    if (this.formMenu && this.formMenu.searchAction) {
      this.formMenu.searchAction.pipe(
        distinctUntilChanged(),
        tap((arg) => {
          this.searchString = arg;
          this.paginator.pageIndex = 0;
          this.loadList(arg);
        })
      ).subscribe();
    }

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
    this.dataSource.findPage ({ pageSorting: new ExcPageSorting(), pageSize: new ExcPageSize() });
  }

  loadList(search?: string) {
    console.log('sort.active:' + this.sort.active + ', sort.direction:' + this.sort.direction);
    const sorting = new ExcPageSorting(this.sort.active, this.sort.direction);
    const pSize: ExcPageSize = new ExcPageSize(this.paginator.pageIndex, this.paginator.pageSize);
    this.dataSource.findPage ({pageSorting: sorting, pageSize: pSize, searchString: search});
  }

  previousState() {
    window.history.back();
  }

  navigateNew() {
    this.router.navigate([this.router.url, 'new', 'edit']);
  }

  navigateEdit(entityId: any) {
    this.router.navigate([this.router.url, entityId, 'edit']);
  }

}
