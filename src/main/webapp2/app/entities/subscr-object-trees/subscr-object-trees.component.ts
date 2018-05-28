import { Component, OnInit } from '@angular/core';
import { ExcListFormComponent, ExcListDatasourceProvider } from '../../shared-blocks/exc-list-form/exc-list-form.component';
import { Router, ActivatedRoute } from '@angular/router';
import { SubscrObjectTree } from './subscr-object-tree.model';
import { SubscrObjectTreeService } from './subscr-object-tree.service';
import { SubscrObjectTreeDataSource } from './subscr-object-tree.datasource';
import { TreeNode } from 'primeng/api';
import { ExcPageSize, ExcPageSorting, defaultPageSize, defaultPageSizeOptions } from '../../shared-blocks';

@Component({
    selector: 'jhi-subscr-object-trees',
    templateUrl: './subscr-object-trees.component.html',
    styleUrls: ['../blocks/list-form.scss', './subscr-object-trees.component.scss']
})
export class SubscrObjectTreesComponent implements OnInit {

    displayedColumns = [];

    dataSource: SubscrObjectTreeDataSource;

    totalElements: number;
    pageSize = defaultPageSize;
    pageSizeOptions = defaultPageSizeOptions;

    constructor(
        private deviceModelService: SubscrObjectTreeService,
        router: Router,
        activatedRoute: ActivatedRoute,
    ) {}

    ngOnInit() {
        this.dataSource = new SubscrObjectTreeDataSource(this.deviceModelService);
        this.dataSource.totalElements$.subscribe(
            (count) => {
              this.totalElements = count;
            }
          );
        this.initSearch();
        this.displayedColumns = ['id', 'objectName'];
    }

    initSearch() {
        this.dataSource.findPage ({ pageSorting: new ExcPageSorting(), pageSize: new ExcPageSize() });
    }

}
