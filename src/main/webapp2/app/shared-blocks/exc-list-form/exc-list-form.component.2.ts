import { OnInit, OnDestroy, ViewChild, AfterViewInit, EventEmitter } from '@angular/core';
import { MatSort } from '@angular/material';
import { MatPaginator } from '@angular/material/paginator';
import { merge } from 'rxjs/observable/merge';
import { ExcPageSize, ExcPageSorting } from '../exc-tools/exc-pagination';
import { defaultPageSize, defaultPageSizeOptions } from '../exc-tools/exc-pagination';
import { ActivatedRoute, Router } from '@angular/router';
import { SelectionModel } from '@angular/cdk/collections';
import { Subscription } from 'rxjs/Rx';
import { Observable } from 'rxjs/Observable';
import { ExcAbstractPageDataSource } from '../exc-tools/exc-abstract-page-datasource';
import { ExcListFormMenuComponent } from '..';
import {
  distinctUntilChanged,
  tap
} from 'rxjs/operators';
import { ExcListDatasourceProvider, ExcListFormParams } from './exc-list-form.params';

export abstract class ExcListForm2Component<T> implements OnInit, OnDestroy, AfterViewInit {

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  selectedRowData: T;
  selectedRowIndex: number = -1;

  private routeDataSubscription: Subscription;

  dataSource: ExcAbstractPageDataSource<T>;

  public searchString: String;

  totalElements: number;
  pageSize = defaultPageSize;
  pageSizeOptions = defaultPageSizeOptions;

  private rowSelectionSubscription: Subscription;

  constructor(
    readonly params: ExcListFormParams,
    readonly router: Router,
    readonly activatedRoute: ActivatedRoute,
  ) {
      const initialSelection = [];
      const allowMultiSelect = false;

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

    // reset the paginator after sorting
    this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);

    // on sort or paginate events, load a new page
    merge(this.sort.sortChange, this.paginator.page).pipe(
          tap(() => {
            this.loadList();
            this.selectedRowIndex = null;
            this.selectedRowData = null;
          })
      ).subscribe();

  }

  registerSearchEvent(event: Observable<string>) {
      if (event) {
        if (this.rowSelectionSubscription) {
          this.rowSelectionSubscription.unsubscribe();
        }
        this.rowSelectionSubscription = event.pipe(
          distinctUntilChanged(),
          tap((arg) => {
            this.searchString = arg;
            this.paginator.pageIndex = 0;
            this.loadList(arg);
          })
        ).subscribe();
      }
  }

  ngOnDestroy() {
    this.routeDataSubscription.unsubscribe();
    if (this.rowSelectionSubscription) {
      this.rowSelectionSubscription.unsubscribe();
    }
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
