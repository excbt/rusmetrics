import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';
import { FormBuilder, FormGroup, AbstractControl} from '@angular/forms';
import { Validators} from '@angular/forms';

import { Organization } from './organization.model';
import { OrganizationType } from '../organization-types/organization-type.model';
import { OrganizationsService } from './organizations.service';
import { OrganizationTypeService } from '../organization-types/organization-type.service';
import { catchError, finalize } from 'rxjs/operators';
import {of} from 'rxjs/observable/of';
import { ExcCustomValidators, ExcFormControlChecker } from '../../shared-blocks';
import { ExcEditFormComponent } from '../../shared-blocks/exc-edit-form/exc-edit-form.component';

export class FormControlCheck {
    constructor(
        public c: string,
        public e: string
    ) {}
}

@Component({
    selector: 'jhi-organization-edit',
    templateUrl: './organization-edit.component.html',
    styleUrls: ['../blocks/form-edit.scss', './organization-edit.component.scss']
  })
  export class OrganizationEditComponent implements OnInit, OnDestroy {

    organizationForm: FormGroup;
    organization: Organization;

    organizationTypes: OrganizationType[];

    loading: boolean;

    // private subscription: Subscription;
    private eventSubscriber: Subscription;
    private newFlag: boolean;

    constructor(private fb: FormBuilder,
                private eventManager: JhiEventManager,
                private service: OrganizationsService,
                private organizationTypeService: OrganizationTypeService,
                private router: Router,
                private activatedRoute: ActivatedRoute) {
    }

    ngOnInit() {
        console.log('Edit load');
        // this.subscription =
        this.activatedRoute.params.subscribe((params) => {
            if (params['id'] && params['id'] !== 'new') {
                this.loadOrganization(params['id']);
            } else {
                this.newFlag = true;
            }
        });
        this.initForm();
        this.registerChangeInOrganization();
        this.loadOrganizationTypes();
    }

    registerChangeInOrganization() {
        this.eventSubscriber = this.eventManager.subscribe(
            'organizationModification',
            (response) => this.loadOrganization(this.organization.id)
        );
    }

    loadOrganization(id) {
        console.log('Load Organization');
        this.service.find(id).subscribe((data) => {
            this.organization = data;
            console.log('loaded organization id:' + this.organization.id);
            this.createForm(data);
        });
    }

    loadOrganizationTypes() {
        this.organizationTypeService.findAll().subscribe((data) => {
            this.organizationTypes = data;
        });
    }

    createForm(data: Organization) {
        this.organizationForm = this.fb.group({

            id: [data.id],
            organizationName: [data.organizationName],
            organizationFullName: [data.organizationFullName],
            organizationFullAddress: [data.organizationFullAddress],
            flagRso: [data.flagRso],
            flagManagement: [data.flagManagement],
            flagRma: [data.flagRma],
            keyname: [data.keyname],
            flagCm: [data.flagCm],
            organizationDescription: [data.organizationDescription],
            isCommon: [data.isCommon],
            flagServ: [data.flagServ],
            inn: [data.inn, [ExcCustomValidators.valueLength([10, 12]), ExcCustomValidators.onlyNumbersPattern()]],
            kpp: [data.kpp, [ExcCustomValidators.valueLength(9), ExcCustomValidators.onlyNumbersPattern()]],
            okpo: [data.okpo, [ExcCustomValidators.valueLength([8, 10]), ExcCustomValidators.onlyNumbersPattern()]],
            ogrn: [data.ogrn, [ExcCustomValidators.valueLength(13), ExcCustomValidators.onlyNumbersPattern()]],
            legalAddress: [data.legalAddress],
            factAddress: [data.factAddress],
            postAddress: [data.postAddress],
            reqAccount: [data.reqAccount, [ExcCustomValidators.valueLength(20), ExcCustomValidators.onlyNumbersPattern()]],
            reqBankName: [data.reqBankName],
            reqCorrAccount: [data.reqCorrAccount, [ExcCustomValidators.valueLength(20), ExcCustomValidators.onlyNumbersPattern()]],
            reqBik: [data.reqBik, [ExcCustomValidators.valueLength(9), ExcCustomValidators.onlyNumbersPattern()]],
            contactPhone: [data.contactPhone, [Validators.maxLength(12)]],
            contactPhone2: [data.contactPhone2, [Validators.maxLength(12)]],
            contactPhone3: [data.contactPhone3, [Validators.maxLength(12)]],
            contactEmail: [data.contactEmail, [Validators.maxLength(60)]],
            siteUrl: [data.siteUrl],
            directorFio: [data.directorFio],
            chiefAccountantFio: [data.chiefAccountantFio],
            organizationTypeId: [data.organizationTypeId],
        });
    }

    initForm() {
        this.organizationForm = this.fb.group({
            id: null,
            organizationName: null,
            organizationFullName: null,
            organizationFullAddress: null,
            flagRso: false,
            flagManagement: false,
            flagRma: false,
            keyname: null,
            flagCm: false,
            organizationDescription: null,
            isCommon: false,
            rmaSubscriberId: null,
            flagServ: false,
            inn: [null, [Validators.minLength(8), Validators.maxLength(12)]],
            kpp: [null, [Validators.maxLength(9)]],
            okpo: [null, [Validators.maxLength(10)]],
            ogrn: [null, [Validators.maxLength(13)]],
            legalAddress: null,
            factAddress: null,
            postAddress: null,
            reqAccount: [null, [Validators.maxLength(20)]],
            reqBankName: null,
            reqCorrAccount: [null, [Validators.maxLength(20)]],
            reqBik: [null, [Validators.maxLength(9)]],
            contactPhone: [null, [Validators.maxLength(12)]],
            contactPhone2: [null, [Validators.maxLength(12)]],
            contactPhone3: [null, [Validators.maxLength(12)]],
            contactEmail: [null, [Validators.maxLength(60)]],
            siteUrl: null,
            directorFio: null,
            chiefAccountantFio: null,
            organizationTypeId: null
        });
    }

    revertForm() {
      this.createForm(this.organization);
    }

    saveForm() {
        this.updateOrganization(this.organizationForm);
    }

    ngOnDestroy() {
        // this.subscription.unsubscribe();
        if (this.eventSubscriber) {
            this.eventManager.destroy(this.eventSubscriber);
        }
    }

    previousState() {
        window.history.back();
    }

    updateOrganization(formGroup: FormGroup) {
        this.organization = this.presaveOrganization();
        this.loading = true;
        this.service.update(this.organization).pipe(
            catchError(() => of([])),
            finalize(() => this.loading = false)
        )
        .subscribe( (data: Organization) => {
            this.loading = false;
            if (data['id']) {
                if (this.newFlag) {
                    this.router.navigate(['/organizations']);
                } else {
                    this.createForm(this.organization = data);
                }
            }
        });
    }

    checkEmpty(val: any) {
        return (val === '') ? null : val;
    }

    presaveOrganization(): Organization {
        const formModel = this.organizationForm.value;

        const saveOrganization: Organization = {
            id: this.checkEmpty(formModel.id as number),
            organizationName: this.checkEmpty(formModel.organizationName as string),
            organizationFullName: this.checkEmpty(formModel.organizationFullName as string),
            organizationFullAddress: this.checkEmpty(formModel.organizationFullAddress as string),
            flagRso: this.checkEmpty(formModel.flagRso as boolean),
            flagManagement: this.checkEmpty(formModel.flagManagement as boolean),
            flagRma: this.checkEmpty(formModel.flagRma as boolean),
            keyname: this.checkEmpty(formModel.keyname as string),
            flagCm: this.checkEmpty(formModel.flagCm as boolean),
            organizationDescription: this.checkEmpty(formModel.organizationDescription as string),
            isCommon: this.checkEmpty(formModel.isCommon as boolean),
            flagServ: this.checkEmpty(formModel.flagServ as boolean),
            inn: this.checkEmpty(formModel.inn as string),
            kpp: this.checkEmpty(formModel.kpp as string),
            okpo: this.checkEmpty(formModel.okpo as string),
            ogrn: this.checkEmpty(formModel.ogrn as string),
            legalAddress: this.checkEmpty(formModel.legalAddress as string),
            factAddress: this.checkEmpty(formModel.factAddress as string),
            postAddress: this.checkEmpty(formModel.postAddress as string),
            reqAccount: this.checkEmpty(formModel.reqAccount as string),
            reqBankName: this.checkEmpty(formModel.reqBankName as string),
            reqCorrAccount: this.checkEmpty(formModel.reqCorrAccount as string),
            reqBik: this.checkEmpty(formModel.reqBik as string),
            contactPhone: this.checkEmpty(formModel.contactPhone as string),
            contactPhone2: this.checkEmpty(formModel.contactPhone2 as string),
            contactPhone3: this.checkEmpty(formModel.contactPhone3 as string),
            contactEmail: this.checkEmpty(formModel.contactEmail as string),
            siteUrl: this.checkEmpty(formModel.siteUrl as string),
            directorFio: this.checkEmpty(formModel.directorFio as string),
            chiefAccountantFio: this.checkEmpty(formModel.chiefAccountantFio as string),
            organizationTypeId: this.checkEmpty(formModel.organizationTypeId as number),        };
        return saveOrganization;
    }

    checkFormControl(controlName: string, errorName: string, errorNameMask?: string[] | null): boolean {
        const control: AbstractControl = this.organizationForm.controls[controlName];
        return ExcFormControlChecker.checkControlError(control, errorName, errorNameMask);
    }

}

@Component({
selector: 'jhi-organization-edit22',
templateUrl: './organization-edit.component.html',
styleUrls: ['../blocks/form-edit.scss', './organization-edit.component.scss']
})
export class OrganizationEdit22Component extends ExcEditFormComponent<Organization> implements OnInit, OnDestroy {

    constructor(
        eventManager: JhiEventManager,
        router: Router,
        activatedRoute: ActivatedRoute,
        private fb: FormBuilder,
        private service: OrganizationsService,
        private organizationTypeService: OrganizationTypeService) {
            super(eventManager, router, activatedRoute);
        }

    createForm(data: Organization): FormGroup {
        const form = this.fb.group({
            id: [data.id],
            organizationName: [data.organizationName],
            organizationFullName: [data.organizationFullName],
            organizationFullAddress: [data.organizationFullAddress],
            flagRso: [data.flagRso],
            flagManagement: [data.flagManagement],
            flagRma: [data.flagRma],
            keyname: [data.keyname],
            flagCm: [data.flagCm],
            organizationDescription: [data.organizationDescription],
            isCommon: [data.isCommon],
            flagServ: [data.flagServ],
            inn: [data.inn, [ExcCustomValidators.valueLength([10, 12]), ExcCustomValidators.onlyNumbersPattern()]],
            kpp: [data.kpp, [ExcCustomValidators.valueLength(9), ExcCustomValidators.onlyNumbersPattern()]],
            okpo: [data.okpo, [ExcCustomValidators.valueLength([8, 10]), ExcCustomValidators.onlyNumbersPattern()]],
            ogrn: [data.ogrn, [ExcCustomValidators.valueLength(13), ExcCustomValidators.onlyNumbersPattern()]],
            legalAddress: [data.legalAddress],
            factAddress: [data.factAddress],
            postAddress: [data.postAddress],
            reqAccount: [data.reqAccount, [ExcCustomValidators.valueLength(20), ExcCustomValidators.onlyNumbersPattern()]],
            reqBankName: [data.reqBankName],
            reqCorrAccount: [data.reqCorrAccount, [ExcCustomValidators.valueLength(20), ExcCustomValidators.onlyNumbersPattern()]],
            reqBik: [data.reqBik, [ExcCustomValidators.valueLength(9), ExcCustomValidators.onlyNumbersPattern()]],
            contactPhone: [data.contactPhone, [Validators.maxLength(12)]],
            contactPhone2: [data.contactPhone2, [Validators.maxLength(12)]],
            contactPhone3: [data.contactPhone3, [Validators.maxLength(12)]],
            contactEmail: [data.contactEmail, [Validators.maxLength(60)]],
            siteUrl: [data.siteUrl],
            directorFio: [data.directorFio],
            chiefAccountantFio: [data.chiefAccountantFio],
            organizationTypeId: [data.organizationTypeId],
        });

        return form;
    }

    initForm() {
        const form = this.fb.group({
            id: null,
            organizationName: null,
            organizationFullName: null,
            organizationFullAddress: null,
            flagRso: false,
            flagManagement: false,
            flagRma: false,
            keyname: null,
            flagCm: false,
            organizationDescription: null,
            isCommon: false,
            rmaSubscriberId: null,
            flagServ: false,
            inn: [null, [Validators.minLength(8), Validators.maxLength(12)]],
            kpp: [null, [Validators.maxLength(9)]],
            okpo: [null, [Validators.maxLength(10)]],
            ogrn: [null, [Validators.maxLength(13)]],
            legalAddress: null,
            factAddress: null,
            postAddress: null,
            reqAccount: [null, [Validators.maxLength(20)]],
            reqBankName: null,
            reqCorrAccount: [null, [Validators.maxLength(20)]],
            reqBik: [null, [Validators.maxLength(9)]],
            contactPhone: [null, [Validators.maxLength(12)]],
            contactPhone2: [null, [Validators.maxLength(12)]],
            contactPhone3: [null, [Validators.maxLength(12)]],
            contactEmail: [null, [Validators.maxLength(60)]],
            siteUrl: null,
            directorFio: null,
            chiefAccountantFio: null,
            organizationTypeId: null
        });
        return form;
    }

    prepareEntity(): Organization {
        const formModel = this.entityForm.value;

        const saveOrganization: Organization = {
            id: this.checkEmpty(formModel.id as number),
            organizationName: this.checkEmpty(formModel.organizationName as string),
            organizationFullName: this.checkEmpty(formModel.organizationFullName as string),
            organizationFullAddress: this.checkEmpty(formModel.organizationFullAddress as string),
            flagRso: this.checkEmpty(formModel.flagRso as boolean),
            flagManagement: this.checkEmpty(formModel.flagManagement as boolean),
            flagRma: this.checkEmpty(formModel.flagRma as boolean),
            keyname: this.checkEmpty(formModel.keyname as string),
            flagCm: this.checkEmpty(formModel.flagCm as boolean),
            organizationDescription: this.checkEmpty(formModel.organizationDescription as string),
            isCommon: this.checkEmpty(formModel.isCommon as boolean),
            flagServ: this.checkEmpty(formModel.flagServ as boolean),
            inn: this.checkEmpty(formModel.inn as string),
            kpp: this.checkEmpty(formModel.kpp as string),
            okpo: this.checkEmpty(formModel.okpo as string),
            ogrn: this.checkEmpty(formModel.ogrn as string),
            legalAddress: this.checkEmpty(formModel.legalAddress as string),
            factAddress: this.checkEmpty(formModel.factAddress as string),
            postAddress: this.checkEmpty(formModel.postAddress as string),
            reqAccount: this.checkEmpty(formModel.reqAccount as string),
            reqBankName: this.checkEmpty(formModel.reqBankName as string),
            reqCorrAccount: this.checkEmpty(formModel.reqCorrAccount as string),
            reqBik: this.checkEmpty(formModel.reqBik as string),
            contactPhone: this.checkEmpty(formModel.contactPhone as string),
            contactPhone2: this.checkEmpty(formModel.contactPhone2 as string),
            contactPhone3: this.checkEmpty(formModel.contactPhone3 as string),
            contactEmail: this.checkEmpty(formModel.contactEmail as string),
            siteUrl: this.checkEmpty(formModel.siteUrl as string),
            directorFio: this.checkEmpty(formModel.directorFio as string),
            chiefAccountantFio: this.checkEmpty(formModel.chiefAccountantFio as string),
            organizationTypeId: this.checkEmpty(formModel.organizationTypeId as number),        };
        return saveOrganization;
    }

    loadEntity(id: number): Observable<Organization> {
        return this.service.find(id);
    }

    updateEntity(data: Organization): Observable<Organization> {
        return this.service.update(data);
    }
}
