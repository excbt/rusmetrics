import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { MatSort } from '@angular/material';
import { MatPaginator } from '@angular/material/paginator';
import { merge } from 'rxjs/observable/merge';
import { ExcPageSize, ExcPageSorting } from '../exc-tools/pagination-tools';
import { defaultPageSize, defaultPageOptions } from '../exc-tools/pagination-tools';
import { ActivatedRoute, Router } from '@angular/router';
import { SelectionModel } from '@angular/cdk/collections';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Rx';
import { AnyModelDataSource } from '../exc-tools/exc-datasource';
import { ExcFormListMenuComponent } from '..';
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
  newDataSource: () => AnyModelDataSource<T>;
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
  @ViewChild(ExcFormListMenuComponent) formMenu: ExcFormListMenuComponent;

  selection: SelectionModel<T>;

  routeData: Subscription;
  dataSource: AnyModelDataSource<T>;

  public searchString: String;

  rowCount = 10;

  constructor(
    private params: ExcListFormParams,
    private datasourceProvider: ExcListDatasourceProvider<T>,
    // private entityProvider: ExcListFormEntityProvider<T>,
    private router: Router,
    private activatedRoute: ActivatedRoute,
  ) {
      const initialSelection = [];
      const allowMultiSelect = false;
      this.selection = new SelectionModel<T>(allowMultiSelect, initialSelection);

      this.routeData = this.activatedRoute.data.subscribe((data) => {
        if (data['searchParams']) {
          this.searchString = data['searchParams'].searchParams;
        }
    });
  }

  ngOnInit() {
    this.dataSource = this.datasourceProvider.newDataSource();
    this.initSearch();
    this.dataSource.totalElements$.subscribe(
      (count) => {
        this.rowCount = count;
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
    if (this.routeData) {
      this.routeData.unsubscribe();
    }
  }

  initSearch() {
    const sorting = new ExcPageSorting();
    const pageSize: ExcPageSize = new ExcPageSize(0, defaultPageOptions[0]);
    this.dataSource.findSearchPage (sorting, pageSize, '');
  }

  newNavigate() {
    this.router.navigate([this.params.baseUrl + '/new/edit']);
  }

  editNavigate(entityId: any) {
    this.router.navigate([this.params.baseUrl + '/' + entityId + '/edit']);
  }

  loadList(searchString?: string) {
    console.log('sort.active:' + this.sort.active + ', sort.direction:' + this.sort.direction);
    const sorting = new ExcPageSorting(this.sort.active, this.sort.direction);
    const pageSize: ExcPageSize = new ExcPageSize(this.paginator.pageIndex, this.paginator.pageSize);
    this.dataSource.findSearchPage (sorting, pageSize, searchString ? searchString : '');
  }

}
