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
import { BehaviorSubject } from 'rxjs';
import { PSubscriber } from '../p-subscribers/p-subscriber.model';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
    selector: 'jhi-cont-object-access',
    templateUrl: './cont-object-access.component.html',
    styles: ['../blocks/list-form.scss', './cont-object-access.component.scss']
})
export class ContObjectAccessComponent implements OnInit, AfterViewInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(ExcListFormMenuComponent) formMenu: ExcListFormMenuComponent;

    objectAccess: TreeNode[];

    selectedNode: TreeNode;

    dataSource: ContObjectAccessDataSource;

    totalElements: number;
    pageSize = defaultPageSize;

    pageSizeOptions = defaultPageSizeOptions;

    subscriberList: PSubscriber[] = [];

    subscriberGroup: FormGroup;
    subscriberSelect: FormControl;
    subscriberSelectEnable: FormControl;

    private defaultPageSorting = new ExcPageSorting('contObject.id', 'asc');
    private searchString: string;
    public devMode = true;

    constructor(private contObjectAccessService: ContObjectAccessService,
        // private principal: Principal,
        router: Router,
        activatedRoute: ActivatedRoute) {
            //  this.subscriberService.findManageList().subscribe((data) => this.subscriberList = data);
            this.contObjectAccessService.findSubscriberManageList().subscribe((data) => this.subscriberList = data);
         }

    ngOnInit() {
        this.subscriberGroup = new FormGroup({
        });
        this.subscriberSelect = new FormControl();
        this.subscriberSelectEnable = new FormControl(false);
        this.subscriberGroup.addControl('subscriberSelect', this.subscriberSelect);
        this.subscriberGroup.addControl('subscriberSelectEnable', this.subscriberSelectEnable);

        this.dataSource = new ContObjectAccessDataSource(this.contObjectAccessService);
        this.dataSource.modelSubject.subscribe((data) => this.objectAccess = this.contObjectAccessToNode(data));
        this.dataSource.totalElements$.subscribe((count) => this.totalElements = count);

        this.loadList();

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

        this.subscriberSelect.valueChanges.subscribe((arg) => {
            this.paginator.pageIndex = 0;
            // if (arg) {
                this.loadList(this.searchString);
            // } else {
            //     this.dataSource.makeEmpty();
            // }
        });

        this.subscriberSelectEnable.valueChanges.subscribe((arg) => {
            if (arg === false) {
                this.subscriberSelect.setValue(null);
            }
            if (arg === true) {
                this.dataSource.makeEmpty();
            }
        });

        merge(this.paginator.page).pipe(
            tap(() => {
                this.loadList(this.searchString);
            })
        ).subscribe();

    }

    initSearch(id?: number) {
        this.dataSource.findSubscriberPage (id, { pageSorting: this.defaultPageSorting, pageSize: new ExcPageSize() });
    }

    loadList(search?: string) {
        const subscriberId = +this.subscriberSelect.value;
        const sorting = this.defaultPageSorting;
        const pSize: ExcPageSize = new ExcPageSize(this.paginator.pageIndex, this.paginator.pageSize);
        this.dataSource.findSubscriberPage (subscriberId, {pageSorting: sorting, pageSize: pSize, searchString: search});
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
    if (event.node) {
        // in a real application, make a call to a remote url to load children of the current node and add the new nodes as children
        // this.nodeService.getLazyFilesystem().then(nodes => event.node.children = nodes);
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
