import { Component, OnInit } from '@angular/core';
import { ContObjectAccessService } from './cont-object-access.service';
import { Principal } from '../../shared';
import { Router, ActivatedRoute } from '@angular/router';
import { DataSource } from '@angular/cdk/table';
import { ContObjectAccess } from './cont-object-access.model';
import { ExcAbstractDataSource, ExcPageSorting, ExcPageSize } from '../../shared-blocks';
import { ContObjectAccessDataSource } from './cont-object-access.datasource';
import { TreeNode } from 'primeng/api';
import { ContZPointAccess } from './cont-zpoint-access.model';

@Component({
    selector: 'jhi-cont-object-acceess-manage',
    templateUrl: './cont-object-access-manage.component.html',
    styles: ['../blocks/list-form.scss']
})
export class ContObjectAccessManageComponent implements OnInit {

    objectAccess: TreeNode[];

    dataSource: ContObjectAccessDataSource;

    displayedColumns = ['contObjectId', 'contObjectName', 'contObjectFullAddress', 'contObjectManage'];

    constructor(private contObjectAccessService: ContObjectAccessService,
        private principal: Principal,
        router: Router,
        activatedRoute: ActivatedRoute) { }

    ngOnInit() {
        this.dataSource = new ContObjectAccessDataSource(this.contObjectAccessService);
        this.dataSource.modelSubject.subscribe((data) => this.objectAccess = this.contObjectAccessToNode(data));
        this.initSearch();
    }

    initSearch() {
        this.dataSource.findPage ({ pageSorting: new ExcPageSorting('contObject.id', 'asc'), pageSize: new ExcPageSize() });
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
