import { Component, Input, OnInit, AfterViewInit, ViewChild, Output, EventEmitter } from '@angular/core';
import { Organization } from './organization.model';
import { OrganizationsService } from './organizations.service';
import { ExcPageSize, ExcPageSorting, ExcPage } from '../../shared-blocks';
import { FormControl, AbstractControl } from '@angular/forms';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, flatMap, startWith, distinctUntilChanged, tap, debounceTime } from 'rxjs/operators';
import { searchDebounceTimeValue } from '../../shared-blocks/exc-tools/exc-constants';
import { MatAutocompleteSelectedEvent, MatAutocompleteTrigger } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';

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

    moreResults2$ = this.moreResults2.asObservable();

    private autoselectOrganizations = new BehaviorSubject<Organization[]>([]);

    autoselectOrganizations$ = this.autoselectOrganizations.asObservable();
    private innTranslation: string;

    constructor(private organizationService: OrganizationsService,
                private translate: TranslateService) {

        // this.getAndInitTranslations();
        this.innTranslation = 'ИНН';
        this.organizationCtrl = new FormControl();
        this.stateCtrlChanges().subscribe((data) => this.autoselectOrganizations.next(data));

        this.organizationCtrl.valueChanges.pipe(
            debounceTime(searchDebounceTimeValue),
            distinctUntilChanged()).subscribe((arg) => {
                if (!arg) {
                    this.onChange.next(null);
                }
            });
    }

    getAndInitTranslations() {
        this.translate.get(['organization.inn']).subscribe( (translation) => {
          this.innTranslation = translation['organization.inn'];
        });
    }

    @Input()
    get organizationId(): number {
        return this._organizationId;
    }

    set organizationId(id: number) {
        this._organizationId = id;
        if (id) {
            this.organizationService.findOne(id).subscribe((data) => {
                this.autoselectOrganizations.next([data]);
                this.organizationCtrl.setValue(data);
            });
            // this.organizationCtrl.setValue(id);
            // this.filteredOrganizations = this.organizationCtrl.valueChanges.pipe(
            //     debounceTime(searchDebounceTimeValue),
            //     startWith(''),
            //     distinctUntilChanged(),
            //     flatMap((arg) => Observable.forkJoin(
            //         this.findFilteredOrganizations(arg),
            //         this.organizationService.findOne(id).map((data) => {
            //             return [data];
            //         })
            //         ).map((results) => results[1].concat(results[0]))
            //     ));
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
            this.moreResults2.next(data.totalPages > 1);
            return data.content;
        });
    }

    findOrganization(id: any): Observable<Organization> {
        return this.organizationService.findOne(id);
    }

    itemSelected(evt: MatAutocompleteSelectedEvent) {
        const org = evt.option.value;
        this.onChange.next(org.id);
    }

    getDisplayFn() {
        return (val) => this.formatOrganization(val);
    }

    formatOrganization(org: Organization): string {
        const inn = this.formatOrganizationInn(org);
        return org ? (org.organizationName + inn) : 'N/A';
    }

    formatOrganizationInn(org: Organization): string {
        return (org && org.inn) ? (' | ' + this.innTranslation + ':' + org.inn) : '';
    }

}
