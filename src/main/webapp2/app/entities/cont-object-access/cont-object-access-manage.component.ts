import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { ContObjectAccessService } from './cont-object-access.service';
import { Principal } from '../../shared';
import { Router, ActivatedRoute } from '@angular/router';
import { DataSource } from '@angular/cdk/table';
import { ContObjectAccess } from './cont-object-access.model';
import { ExcAbstractDataSource, ExcPageSorting, ExcPageSize, defaultPageSize } from '../../shared-blocks';
import { ContObjectAccessDataSource } from './cont-object-access.datasource';
import { TreeNode } from 'primeng/api';
import { ContZPointAccess } from './cont-zpoint-access.model';
import { merge } from 'rxjs/observable/merge';
import { tap } from 'rxjs/operators';
import { MatPaginator } from '@angular/material';

@Component({
    selector: 'jhi-cont-object-acceess-manage',
    templateUrl: './cont-object-access-manage.component.html',
    styles: ['../blocks/list-form.scss']
})
export class ContObjectAccessManageComponent implements OnInit, AfterViewInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;

    objectAccess: TreeNode[];

    dataSource: ContObjectAccessDataSource;

    displayedColumns = ['contObjectId', 'contObjectName', 'contObjectFullAddress', 'contObjectManage'];
    totalElements: number;
    pageSize = defaultPageSize;

    private defaultPageSorting = new ExcPageSorting('contObject.id', 'asc');

    constructor(private contObjectAccessService: ContObjectAccessService,
        private principal: Principal,
        router: Router,
        activatedRoute: ActivatedRoute) { }

    ngOnInit() {
        this.dataSource = new ContObjectAccessDataSource(this.contObjectAccessService);
        this.dataSource.modelSubject.subscribe((data) => this.objectAccess = this.contObjectAccessToNode(data));
        this.dataSource.totalElements$.subscribe((count) => this.totalElements = count);
        this.initSearch();
    }

    ngAfterViewInit() {
    // Called after ngAfterContentInit when the component's view has been initialized. Applies to components only.
    // Add 'implements AfterViewInit' to the class.
    // on sort or paginate events, load a new page

    merge(this.paginator.page).pipe(
        tap(() => {
          this.loadList();
          console.log('from change');
        })
    ).subscribe();

    }

    initSearch() {
        this.dataSource.findPage ({ pageSorting: this.defaultPageSorting, pageSize: new ExcPageSize() });
    }

    loadList(search?: string) {
        // const sorting = new ExcPageSorting(this.sort.active, this.sort.direction);
        const sorting = this.defaultPageSorting;
        const pSize: ExcPageSize = new ExcPageSize(this.paginator.pageIndex, this.paginator.pageSize);
        this.dataSource.findPage ({pageSorting: sorting, pageSize: pSize, searchString: search});
    }
   contObjectAccessToNode(inData: ContObjectAccess[]): TreeNode[] {
       return inData.map((i) => {
            const node: TreeNode = {
                label: i.contObjectName ? i.contObjectName : i.contObjectFullAddress,
                data: i,
                leaf: false
            };
            return node;
        });
   }

   contZPointAccessToNode(inData: ContZPointAccess[]): TreeNode[] {
    return inData.map((i) => {
        const node: TreeNode = {
            label: i.contZPointCustomServiceName,
            data: i,
            leaf: true
        };
        return node;
    });
   }

   loadNode(event) {
    console.log('NodeClick');
    if (event.node) {
        // in a real application, make a call to a remote url to load children of the current node and add the new nodes as children
        // this.nodeService.getLazyFilesystem().then(nodes => event.node.children = nodes);
        console.log('Node' + event.node.data.contObjectId);
        this.contObjectAccessService.findContZPointAccess(event.node.data.contObjectId)
            .subscribe((data) => event.node.children = this.contZPointAccessToNode(data));
    }
}

}
