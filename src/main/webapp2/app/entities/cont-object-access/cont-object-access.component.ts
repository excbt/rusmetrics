import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { ContObjectAccessService } from './cont-object-access.service';
// import { Principal } from '../../shared';
import { Router, ActivatedRoute } from '@angular/router';
import { ContObjectAccess } from './cont-object-access.model';
import { ExcPageSorting, ExcPageSize, defaultPageSize, ExcListFormMenuComponent, defaultPageSizeOptions } from '../../shared-blocks';
import { ContObjectAccessDataSource } from './cont-object-access.datasource';
import { TreeNode, MenuItem } from 'primeng/api';
import { ContZPointAccess } from './cont-zpoint-access.model';
import { merge } from 'rxjs/observable/merge';
import { tap, distinctUntilChanged } from 'rxjs/operators';
import { MatPaginator } from '@angular/material';

@Component({
    selector: 'jhi-cont-object-access',
    templateUrl: './cont-object-access.component.html',
    styles: ['../blocks/list-form.scss']
})
export class ContObjectAccessComponent implements OnInit, AfterViewInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(ExcListFormMenuComponent) formMenu: ExcListFormMenuComponent;

    objectAccess: TreeNode[];

    selectedNode: TreeNode;

    dataSource: ContObjectAccessDataSource;

    displayedColumns = ['contObjectId', 'contObjectName', 'contObjectFullAddress', 'contObjectManage'];
    totalElements: number;
    pageSize = defaultPageSize;

    pageSizeOptions = defaultPageSizeOptions;

    contextMenuItems: MenuItem[];

    private defaultPageSorting = new ExcPageSorting('contObject.id', 'asc');
    private searchString: string;
    public devMode = true;

    constructor(private contObjectAccessService: ContObjectAccessService,
        // private principal: Principal,
        router: Router,
        activatedRoute: ActivatedRoute) { }

    ngOnInit() {
        this.dataSource = new ContObjectAccessDataSource(this.contObjectAccessService);
        this.dataSource.modelSubject.subscribe((data) => this.objectAccess = this.contObjectAccessToNode(data));
        this.dataSource.totalElements$.subscribe((count) => this.totalElements = count);
        this.initSearch();

        this.contextMenuItems = [
            {label: 'View', icon: 'fa-add', command: (event) => console.log('Hi')},
            {label: 'Delete', icon: 'fa-close', command: (event) => console.log('Hi2')}
        ];
    }

    ngAfterViewInit() {
    // Called after ngAfterContentInit when the component's view has been initialized. Applies to components only.
    // Add 'implements AfterViewInit' to the class.
    // on sort or paginate events, load a new page

        if (this.formMenu && this.formMenu.searchAction) {
            this.formMenu.searchAction.pipe(
            distinctUntilChanged(),
            tap((arg) => {
                this.paginator.pageIndex = 0;
                this.loadList(arg);
                this.searchString = arg;
            })
            ).subscribe();
        }

        merge(this.paginator.page).pipe(
            tap(() => {
                this.loadList(this.searchString);
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

    accessOnChange(node) {
        if (node.data.contZPointId) {
            console.log('Change' + node.data.contZPointId);
        }
    }

}
