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
import { Observable } from 'rxjs/Observable';
import { PSubscriber } from '../p-subscribers/p-subscriber.model';
import { FormControl, FormGroup } from '@angular/forms';
import { ExcAbstractDataSource } from '../../shared-blocks/exc-tools/exc-abstract-datasource';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

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
    addModelEnable: FormControl;

    private defaultPageSorting = new ExcPageSorting('contObject.id', 'asc');
    private searchString: string;
    public devMode = true;

    avalialableAccess: ContObjectAccess[];

    private currentSubscriberIdSubject = new BehaviorSubject<number>(null);
    currentSubscriberId$ = this.currentSubscriberIdSubject.asObservable();
    currentSubscriberId: number;

    private expandedContObjectIds: number[] = [];

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
        this.addModelEnable = new FormControl(false);
        this.subscriberGroup.addControl('subscriberSelect', this.subscriberSelect);
        this.subscriberGroup.addControl('subscriberSelectEnable', this.subscriberSelectEnable);
        this.subscriberGroup.addControl('addModelEnable', this.addModelEnable);

        this.dataSource = new ContObjectAccessDataSource(this.contObjectAccessService);
        this.dataSource.modelSubject
            .map((data) => this.contObjectAccessToNode(data))
            .flatMap((data) => this.loadExpanedNodes(data))
            .subscribe((nodes) => this.objectAccess = nodes);
        this.dataSource.totalElements$.subscribe((count) => this.totalElements = count);

        this.loadAccessData();

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
                this.expandedContObjectIds = [];
                this.loadAccessData(arg);
                this.searchString = arg;
            })
            ).subscribe();
        }

        this.subscriberSelect.valueChanges.subscribe((arg) => {
            this.paginator.pageIndex = 0;
            // if (arg) {
                this.expandedContObjectIds = [];
                this.loadAccessData(this.searchString);
                this.currentSubscriberId = +arg;
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

        this.addModelEnable.valueChanges.subscribe((arg) => {
            if (this.currentSubscriberId) {
                this.loadAccessData(this.searchString);
            } else if (this.subscriberSelectEnable.value === true) {
                this.dataSource.makeEmpty();
            }
        });

        merge(this.paginator.page).pipe(
            tap(() => {
                this.loadAccessData(this.searchString);
            })
        ).subscribe();

    }

    loadAccessData(search?: string) {
        const subscriberId = +this.subscriberSelect.value;
        const sorting = this.defaultPageSorting;
        const pSize: ExcPageSize = new ExcPageSize(this.paginator.pageIndex, this.paginator.pageSize);
        this.dataSource.findPageBySubscriber (subscriberId, (this.addModelEnable.value === true), {pageSorting: sorting, pageSize: pSize, searchString: search});
    }

   contObjectAccessToNode(inData: ContObjectAccess[]): TreeNode[] {
       return inData.map((d) => {
        const isExpanded = this.expandedContObjectIds.filter((i) => i === d.contObjectId).length > 0;
            const node: TreeNode = {
                label: d.contObjectName ? d.contObjectName : d.contObjectFullAddress,
                data: d,
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

    loadNodeEvent(event) {
        if (event.node) { this.loadChildNode(event.node)
            .subscribe((data) => event.node.children = data);
        }
    }

    loadChildNode(node: TreeNode): Observable<TreeNode[]> {
        if (node && node.data.contObjectId) {
            const contObjectId = node.data.contObjectId;
            return this.contObjectAccessService.findContZPointAccess(this.currentSubscriberId, contObjectId)
                .map((data) => {
                    this.expandedContObjectIds.push(contObjectId);
                    return this.contZPointAccessToNode(data);
                });
        } else {
            return Observable.of([]);
        }
    }

    loadExpanedNodes(nodes: TreeNode[]): Observable<TreeNode[]> {
        const childNodesLoading: Observable<TreeNode[]>[] = [];
        const resultNodes = nodes.map((n) => {
            const isExpanded = n.data.contObjectId && this.expandedContObjectIds.filter((i) => i === n.data.contObjectId).length > 0;
            let resultNode: TreeNode;
            if (isExpanded) {
                resultNode = {
                    label: n.label,
                    data: n.data,
                    leaf: n.leaf,
                    expanded: true
                };
                childNodesLoading.push(this.loadChildNode(n).pipe(
                    tap((child) => resultNode.children = child)
                ));
            } else {
                resultNode = n;
            }
            return resultNode;
        });

        if (childNodesLoading.length === 0) {
            return Observable.of(resultNodes);
        } else {
            return merge(... childNodesLoading).map(() => resultNodes);
        }
    }

    accessOnChange(node: TreeNode) {
        if (node.data.contZPointId) {
            console.log('Change contZPointId' + node.data.contZPointId);
            const parentNode = node.parent;
            let action: Observable<any>;

            if (node.data.accessEnabled === false) {
                action = this.contObjectAccessService.grantContZPointAccess(this.currentSubscriberId, node.data.contZPointId);
            } else {
                action = this.contObjectAccessService.revokeContZPointAccess(this.currentSubscriberId, node.data.contZPointId);
            }
            action.pipe(
                catchError(() => of([])),
                // finalize(() => {
                //     this.contObjectAccessService.findContZPointAccess(this.currentSubscriberId, node.data.contObjectId)
                //         .subscribe((data) => parentNode.children = this.contZPointAccessToNode(data));
                // }
                finalize(() => {
                    this.loadAccessData(this.searchString);
                })
            ).subscribe();
        }

        if (node.data.contObjectId) {
            let action: Observable<any>;
            console.log('Change contObjectId' + node.data.contObjectId);
            if (node.data.accessEnabled === false) {
                action = this.contObjectAccessService.grantContObjectAccess(this.currentSubscriberId, node.data.contObjectId);
            } else {
                action = this.contObjectAccessService.revokeContObjectAccess(this.currentSubscriberId, node.data.contObjectId);
            }
            action.pipe(
                catchError(() => of([])),
                finalize(() => this.loadAccessData(this.searchString))
            ).subscribe();
        }

    }

}
