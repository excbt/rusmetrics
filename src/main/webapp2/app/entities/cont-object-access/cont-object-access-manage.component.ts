import { Component, OnInit } from '@angular/core';
import { ContObjectAccessService } from './cont-object-access.service';
import { Principal } from '../../shared';
import { Router, ActivatedRoute } from '@angular/router';
import { DataSource } from '@angular/cdk/table';
import { ContObjectAccess } from './cont-object-access.model';
import { ExcAbstractDataSource, ExcPageSorting, ExcPageSize } from '../../shared-blocks';
import { ContObjectAccessDataSource } from './cont-object-access.datasource';

@Component({
    selector: 'jhi-cont-object-acceess-manage',
    templateUrl: './cont-object-access-manage.component.html',
    styles: ['./cont-object-access-manage.component.scss']
})
export class ContObjectAccessManageComponent implements OnInit {

    dataSource: ContObjectAccessDataSource;

    constructor(private contObjectAccessService: ContObjectAccessService,
        private principal: Principal,
        router: Router,
        activatedRoute: ActivatedRoute) { }

    ngOnInit() {
        this.dataSource = new ContObjectAccessDataSource(this.contObjectAccessService);
        this.initSearch();
    }

    initSearch() {
        this.dataSource.findPage ({ pageSorting: new ExcPageSorting('contObject.id', 'asc'), pageSize: new ExcPageSize() });
      }

}
