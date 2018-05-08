import { Component } from '@angular/core';
import { Organization } from './organization.model';
import { OrganizationsService } from './organizations.service';
import { ExcPageSize, ExcPageSorting, ExcPage } from '../../shared-blocks';
import { FormControl } from '@angular/forms';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, flatMap, startWith, distinctUntilChanged, tap, debounceTime } from 'rxjs/operators';
import { searchDebounceTimeValue } from '../../shared-blocks/exc-tools/exc-constants';

@Component({
    selector: 'jhi-organization-autocomplete',
    templateUrl: './organization-autocomplete.component.html',
    styleUrls: ['./organization-autocomplete.component.scss']
})
export class OrganizationAutocompleteComponent {

    private sorting = new ExcPageSorting('organizationName', 'asc');
    private pSize = new ExcPageSize(0, 30);

    stateCtrl: FormControl;

    filteredOrganizations: Observable<Organization[]>;

    // private filteredOrganizations = new BehaviorSubject<Organization[]>([]);

    // public filteredOrganizations$ = this.filteredOrganizations.asObservable();

    private moreResults2 = new BehaviorSubject<boolean>(false);

    moreResults: boolean;
    moreResults2$ = this.moreResults2.asObservable();

    constructor(private organizationService: OrganizationsService) {
        this.stateCtrl = new FormControl();
        // this.stateCtrl.valueChanges.pipe(
        //     debounceTime(searchDebounceTimeValue),
        //     startWith(''),
        //     distinctUntilChanged(),
        //     tap((arg) => this.findOrganizations(arg))
        // );

        this.filteredOrganizations = this.stateCtrl.valueChanges.pipe(
            debounceTime(searchDebounceTimeValue),
            startWith(''),
            distinctUntilChanged(),
            flatMap((arg) => this.findFilteredOrganizations(arg))
        );
    }

    // findOrganizations(search: string) {
    //     this.organizationService.findPage({pageSize: this.pSize, pageSorting: this.sorting, searchString: search}).subscribe((data) => {
    //         this.moreResults2.next(data.totalPages > 1);
    //         this.filteredOrganizations.next(data.content);
    //     });
    // }

    findFilteredOrganizations(search: string): Observable<Organization[]> {
        return this.organizationService.findPage({pageSize: this.pSize, pageSorting: this.sorting, searchString: search})
            .map((data) => {
                this.moreResults = data.totalPages > 1;
                return data.content;
            });
    }

}
