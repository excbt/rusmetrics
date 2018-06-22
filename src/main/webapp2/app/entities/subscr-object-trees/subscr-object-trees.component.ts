import { Component, OnInit, ElementRef, ViewChild, OnDestroy, AfterViewInit } from '@angular/core';
import { SubscrObjectTree, SubscrObjectTreeModificationEvent } from './subscr-object-tree.model';
import { SubscrObjectTreeService } from './subscr-object-tree.service';
import { SubscrObjectTreeDataSource } from './subscr-object-tree.datasource';
import { OverlayPanel } from 'primeng/overlaypanel';
import { JhiEventManager } from 'ng-jhipster';
import { Observable, Subscription } from 'rxjs';
import { merge } from 'rxjs/observable/merge';
import { tap } from 'rxjs/operators';
import { MatPaginator } from '@angular/material';
import { ExcSearchToolService } from '../../shared-blocks/exc-tools/exc-search-tool-service';
import { defaultPageSizeOptions, defaultPageSize, ExcPageSize, ExcPageSorting } from '../../shared-blocks';

@Component({
    selector: 'jhi-subscr-object-trees',
    templateUrl: './subscr-object-trees.component.html',
    styleUrls: ['../blocks/list-form.scss', './subscr-object-trees.component.scss']
})
export class SubscrObjectTreesComponent implements OnInit, OnDestroy, AfterViewInit {

    @ViewChild('op')
    panel: OverlayPanel;

    @ViewChild(MatPaginator) paginator: MatPaginator;

    @ViewChild('searchString')
    searchStringInput: ElementRef;

    newItemName: string;

    displayedColumns = [];

    subscrObjectTreeData: SubscrObjectTree[];

    selectedRow: SubscrObjectTree;

    dataSource: SubscrObjectTreeDataSource;

    totalElements: number;
    pageSize = defaultPageSize;
    pageSizeOptions = defaultPageSizeOptions;

    private eventSubscription: Subscription;
    private searchToolService = new ExcSearchToolService();
    private searchString: string;

    constructor(
        private subscrObjectTreeService: SubscrObjectTreeService,
        private eventManager: JhiEventManager
    ) {
        this.dataSource = new SubscrObjectTreeDataSource(this.subscrObjectTreeService);
        this.modelSubjectSubsribe();
        this.registerChangeInTree();
    }

    modelSubjectSubsribe() {
        this.dataSource.modelSubject.asObservable().subscribe((data) => {
            this.subscrObjectTreeData = data;

            let rowCandidate;
            if (this.selectedRow && this.selectedRow.id) {
                const currentId = this.selectedRow.id;
                const f = data.filter((d) => d.id === currentId);
                if (f.length > 0) {
                    rowCandidate = f[0];
                    this.selectedRow = rowCandidate;
                }
            }

            if (data && (data.length > 0) && this.selectedRow) {
                this.sendNodeId(this.selectedRow.id);
            } else if (data && (data.length > 0) && !this.selectedRow) {
                this.sendNodeId(data[0].id);
                this.selectedRow = data[0];
            } else {
                this.selectedRow = rowCandidate;
            }

            if (data.length === 0) {
                this.sendNodeId(null);
            }
        });
    }

    sendNodeId(id: number) {
        this.subscrObjectTreeService.selectNode(id);
    }

    registerChangeInTree() {
        this.eventSubscription = this.eventManager.subscribe(
            SubscrObjectTreeModificationEvent,
            (response) => this.initSearch());
    }

    ngOnInit() {
        this.dataSource.totalElements$.subscribe(
            (count) => {
              this.totalElements = count;
            }
          );
        this.initSearch();
        this.displayedColumns = ['id', 'objectName'];

        // on sort or paginate events, load a new page
        merge(this.paginator.page).pipe(
            tap(() => {
                this.initSearch();
            })
        ).subscribe();

        this.searchToolService.searchString$.subscribe((arg) => {
            this.paginator.pageIndex = 0;
            this.initSearch(arg);
            this.searchString = arg;
        });

    }

    ngAfterViewInit() {
        Observable.fromEvent(this.searchStringInput.nativeElement, 'keyup')
        .subscribe((data) => this.searchToolService.filterInput(this.searchStringInput.nativeElement.value));
    }

    ngOnDestroy() {
        if (this.eventSubscription) {
            this.eventManager.destroy(this.eventSubscription);
        }
    }

    initSearch(search?: string) {
        // this.dataSource.findPage ({ pageSorting: new ExcPageSorting(), pageSize: new ExcPageSize() });
        const pSize: ExcPageSize = new ExcPageSize(this.paginator.pageIndex, this.paginator.pageSize);
        this.dataSource.findPage ({ pageSorting: new ExcPageSorting(), pageSize: pSize, searchString: search  });
    }

    refreshData() {
        this.initSearch(this.searchString);
    }

    selectRow(event) {
        if (event.data.id) {
            this.subscrObjectTreeService.selectNode(event.data.id);
        }
    }

    saveNew() {
        console.log('save ' + this.newItemName);
        const treeName = this.newItemName;
        this.subscrObjectTreeService.addNewTree(treeName).subscribe((data) => {
            this.initSearch();
            this.panel.hide();
            this.newItemName = '';
        });
    }

    cancelNew() {
        this.panel.hide();
    }

    deleteTree() {
        if (this.selectedRow && this.selectedRow.id) {
            this.subscrObjectTreeService
                .deleteTreeNodeNode({ id: this.selectedRow.id })
                .subscribe(() => this.refreshData());
        }
    }

    activeFlag() {
        if (this.selectedRow) {
            this.subscrObjectTreeService.putSubscrObjectTreeActive(this.selectedRow.id, !(this.selectedRow.isActive === true))
                .subscribe((d) => {
                    if (d) {
                        this.selectedRow.isActive = d.isActive;
                        this.subscrObjectTreeService.selectNode(this.selectedRow.id);
                    }
                });
        }
    }
}
