import { Component, OnInit, ElementRef, ViewChild, OnDestroy } from '@angular/core';
import { ExcListFormComponent, ExcListDatasourceProvider } from '../../shared-blocks/exc-list-form/exc-list-form.component';
import { Router, ActivatedRoute } from '@angular/router';
import { SubscrObjectTree, SubscrObjectTreeModificationEvent } from './subscr-object-tree.model';
import { SubscrObjectTreeService } from './subscr-object-tree.service';
import { SubscrObjectTreeDataSource } from './subscr-object-tree.datasource';
import { TreeNode } from 'primeng/api';
import { ExcPageSize, ExcPageSorting, defaultPageSize, defaultPageSizeOptions } from '../../shared-blocks';
import { OverlayPanel } from 'primeng/overlaypanel';
import { FormControl } from '@angular/forms';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription } from 'rxjs';

@Component({
    selector: 'jhi-subscr-object-trees',
    templateUrl: './subscr-object-trees.component.html',
    styleUrls: ['../blocks/list-form.scss', './subscr-object-trees.component.scss']
})
export class SubscrObjectTreesComponent implements OnInit, OnDestroy {

    @ViewChild('op')
    panel: OverlayPanel;

    newItemName: string;

    displayedColumns = [];

    subscrObjectTreeData: SubscrObjectTree[];

    selectedRow: SubscrObjectTree;

    dataSource: SubscrObjectTreeDataSource;

    totalElements: number;
    pageSize = defaultPageSize;
    pageSizeOptions = defaultPageSizeOptions;

    private eventSubscription: Subscription;

    constructor(
        private subscrObjectTreeService: SubscrObjectTreeService,
        private eventManager: JhiEventManager,
        private router: Router,
        private activatedRoute: ActivatedRoute
    ) {
        this.dataSource = new SubscrObjectTreeDataSource(this.subscrObjectTreeService);
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
                this.subscrObjectTreeService.selectNode(this.selectedRow.id);
            } else if (data && (data.length > 0) && !this.selectedRow) {
                this.subscrObjectTreeService.selectNode(data[0].id);
                this.selectedRow = data[0];
            } else {
                this.selectedRow = rowCandidate;
            }
        });
        this.registerChangeInTree();
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
    }

    ngOnDestroy() {
        if (this.eventSubscription) {
            this.eventManager.destroy(this.eventSubscription);
        }
    }

    initSearch() {
        console.log('Init Search');
        this.dataSource.findPage ({ pageSorting: new ExcPageSorting(), pageSize: new ExcPageSize() });
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

}
