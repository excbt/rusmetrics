import { Component, Input, OnInit, AfterViewInit, ViewChild, Output, EventEmitter } from '@angular/core';
import { Organization } from './organization.model';
import { OrganizationsService } from './organizations.service';
import { ExcPageSize, ExcPageSorting, ExcPage } from '../../shared-blocks';
import { FormControl, AbstractControl } from '@angular/forms';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, flatMap, startWith, distinctUntilChanged, tap, debounceTime } from 'rxjs/operators';
import { searchDebounceTimeValue } from '../../shared-blocks/exc-tools/exc-constants';
import { MatAutocompleteSelectedEvent, MatAutocompleteTrigger } from '@angular/material';

@Component({
    selector: 'jhi-organization-autocomplete',
    templateUrl: './organization-autocomplete.component.html',
    styleUrls: ['./organization-autocomplete.component.scss']
})
export class OrganizationAutocompleteComponent {

    @Output() onChange = new EventEmitter<number>();

    private _organizationId: number;

    private sorting = new ExcPageSorting('organizationName', 'asc');
    private pSize = new ExcPageSize(0, 30);

    organizationCtrl: FormControl;

    filteredOrganizations: Observable<Organization[]>;

    private moreResults2 = new BehaviorSubject<boolean>(false);

    moreResults: boolean;
    moreResults2$ = this.moreResults2.asObservable();

    // filteredOrganizations: Observable<Organization[]>;

    private displayedValue: string;

    constructor(private organizationService: OrganizationsService) {
        this.organizationCtrl = new FormControl();
        this.filteredOrganizations = this.stateCtrlChanges();
    }

    @Input()
    get organizationId(): number {
        return this._organizationId;
    }

    set organizationId(id: number) {
        this._organizationId = id;
        if (id) {
            // this.organizationCtrl.setValue(id);
            this.filteredOrganizations = this.organizationCtrl.valueChanges.pipe(
                debounceTime(searchDebounceTimeValue),
                startWith(''),
                distinctUntilChanged(),
                flatMap((arg) => Observable.forkJoin(
                    this.findFilteredOrganizations(arg),
                    this.organizationService.findOne(id).map((data) => {
                        return [data];
                    })
                    ).map((results) => results[1].concat(results[0]))
                ));
            // this.pushSelected(id);
        }
    }

    stateCtrlChanges(): Observable<Organization[]> {
        return this.organizationCtrl.valueChanges.pipe(
            debounceTime(searchDebounceTimeValue),
            startWith(''),
            distinctUntilChanged(),
            flatMap((arg) => this.findFilteredOrganizations(arg)));
    }

    findFilteredOrganizations(search: string): Observable<Organization[]> {
        return this.organizationService.findPage({pageSize: this.pSize, pageSorting: this.sorting, searchString: search})
        .map((data) => {
            this.moreResults = data.totalPages > 1;
            this.moreResults2.next(data.totalPages > 1);
            return data.content;
        });
    }

    findOrganization(id: any): Observable<Organization> {
        return this.organizationService.findOne(id);
    }

    itemSelected(evt: MatAutocompleteSelectedEvent) {
        const org = evt.option.value;
        console.log('evt: ' + org);
        this.onChange.next(org.id);
        // this.pushSelected(id);
    }

    // pushSelected(id) {
    //     this.filteredOrganizations.flatMap((data) => data.filter((i) => i.id === id)).subscribe((data) => this.selectedOrganization.next(data));
    // }

    getDisplayFn() {
        return (val) => this.displayFn(val);
    }

    displayFn(id) {
        // const value = this.selectedOrganization.getValue();
        return id ? id.organizationName : 'N/A';
    }

}
