import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatTableDataSource, MatSort } from '@angular/material';
import { OrganizationsService } from './organizations.service';
import { OrganizationsDataSource } from './organizations.datasource';
import { Organization, OrganizationSort } from './organization.model';
import { merge} from 'rxjs/observable/merge';
import { debounceTime, distinctUntilChanged, startWith, tap, delay} from 'rxjs/operators';
import { SelectionModel } from '@angular/cdk/collections';

@Component({
  selector: 'jhi-organizations',
  templateUrl: './organizations.component.html',
  styleUrls: ['./organizations.scss']
})
export class OrganizationsComponent implements OnInit, AfterViewInit {

  selection: SelectionModel<Organization>;
  dataSource: OrganizationsDataSource;

  displayedColumns = ['edit', 'id', 'organizationName'];
  // dataSource = new MatTableDataSource(ELEMENT_DATA);

  @ViewChild(MatSort) sort: MatSort;

  constructor(
      private organizationService: OrganizationsService
  ) {
        const initialSelection = [];
        const allowMultiSelect = false;
        this.selection = new SelectionModel<Organization>(allowMultiSelect, initialSelection);
   }

  ngOnInit() {
    // this.organizationService.findAll().subscribe((organizations) => this.organizations = organizations);
    this.dataSource = new OrganizationsDataSource(this.organizationService);
    this.dataSource.findAll();
  }

  ngAfterViewInit() {
    merge(this.sort.sortChange)
            .pipe(
                tap(() => this.loadOrganization())
            )
            .subscribe();
  }

  loadOrganization() {
    this.dataSource.findAll(this.sort.active, this.sort.direction);
  }
}
