import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';
import { FormBuilder, FormGroup, AbstractControl} from '@angular/forms';
import { Validators} from '@angular/forms';

import { Organization } from './organization.model';
import { OrganizationType } from '../organization-types/organization-type.model';
import { OrganizationsService } from './organizations.service';
import { OrganizationTypeService } from '../organization-types/organization-type.service';
import { catchError, finalize } from 'rxjs/operators';
import {of} from 'rxjs/observable/of';
import { CustomValidators, FormControlChecker } from '../blocks/form-blocks';

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

    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(private fb: FormBuilder,
                private eventManager: JhiEventManager,
                private service: OrganizationsService,
                private organizationTypeService: OrganizationTypeService,
                private route: ActivatedRoute) {
    }

    ngOnInit() {
        console.log('Edit load');
        this.subscription = this.route.params.subscribe((params) => {
            this.loadOrganization(params['id']);
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
            rmaSubscriberId: [data.rmaSubscriberId],
            flagServ: [data.flagServ],
            inn: [data.inn, [CustomValidators.valueLength([10, 12]), CustomValidators.onlyNumbersPattern()]],
            kpp: [data.kpp, [CustomValidators.valueLength(9), CustomValidators.onlyNumbersPattern()]],
            okpo: [data.okpo, [CustomValidators.valueLength([8, 10]), CustomValidators.onlyNumbersPattern()]],
            ogrn: [data.ogrn, [CustomValidators.valueLength(13), CustomValidators.onlyNumbersPattern()]],
            legalAddress: [data.legalAddress],
            factAddress: [data.factAddress],
            postAddress: [data.postAddress],
            reqAccount: [data.reqAccount, [CustomValidators.valueLength(20), CustomValidators.onlyNumbersPattern()]],
            reqBankName: [data.reqBankName],
            reqCorrAccount: [data.reqCorrAccount, [CustomValidators.valueLength(20), CustomValidators.onlyNumbersPattern()]],
            reqBik: [data.reqBik, [CustomValidators.valueLength(9), CustomValidators.onlyNumbersPattern()]],
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
            id: [''],
            organizationName: '',
            organizationFullName: '',
            organizationFullAddress: '',
            flagRso: false,
            flagManagement: false,
            flagRma: false,
            keyname: '',
            flagCm: false,
            organizationDescription: '',
            isCommon: false,
            rmaSubscriberId: '',
            flagServ: false,
            inn: ['', [Validators.minLength(8), Validators.maxLength(12)]],
            kpp: ['', [Validators.maxLength(9)]],
            okpo: ['', [Validators.maxLength(10)]],
            ogrn: ['', [Validators.maxLength(13)]],
            legalAddress: '',
            factAddress: '',
            postAddress: '',
            reqAccount: ['', [Validators.maxLength(20)]],
            reqBankName: '',
            reqCorrAccount: ['', [Validators.maxLength(20)]],
            reqBik: ['', [Validators.maxLength(9)]],
            contactPhone: ['', [Validators.maxLength(12)]],
            contactPhone2: ['', [Validators.maxLength(12)]],
            contactPhone3: ['', [Validators.maxLength(12)]],
            contactEmail: ['', [Validators.maxLength(60)]],
            siteUrl: '',
            directorFio: '',
            chiefAccountantFio: '',
            organizationTypeId: ''
        });
    }

    revertForm() {
      this.createForm(this.organization);
    }

    saveForm() {
        this.updateOrganization(this.organizationForm);
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
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
            this.createForm(this.organization = data);
        });
    }

    presaveOrganization(): Organization {
        const formModel = this.organizationForm.value;

        const saveOrganization: Organization = {
            id: formModel.id as number,
            organizationName: formModel.organizationName as string,
            organizationFullName: formModel.organizationFullName as string,
            organizationFullAddress: formModel.organizationFullAddress as string,
            exCode: this.organization.exCode,
            exSystem: this.organization.exSystem,
            flagRso: formModel.flagRso as boolean,
            flagManagement: formModel.flagManagement as boolean,
            flagRma: formModel.flagRma as boolean,
            keyname: formModel.keyname as string,
            isDevMode: this.organization.isDevMode,
            flagCm: formModel.flagCm as boolean,
            organizationDescription: formModel.organizationDescription as string,
            isCommon: formModel.isCommon as boolean,
            rmaSubscriberId: formModel.rmaSubscriberId as number,
            flagServ: formModel.flagServ as boolean,
            inn: formModel.inn as string,
            kpp: formModel.kpp as string,
            okpo: formModel.okpo as string,
            ogrn: formModel.ogrn as string,
            legalAddress: formModel.legalAddress as string,
            factAddress: formModel.factAddress as string,
            postAddress: formModel.postAddress as string,
            reqAccount: formModel.reqAccount as string,
            reqBankName: formModel.reqBankName as string,
            reqCorrAccount: formModel.reqCorrAccount as string,
            reqBik: formModel.reqBik as string,
            contactPhone: formModel.contactPhone as string,
            contactPhone2: formModel.contactPhone2 as string,
            contactPhone3: formModel.contactPhone3 as string,
            contactEmail: formModel.contactEmail as string,
            siteUrl: formModel.siteUrl as string,
            directorFio: formModel.directorFio as string,
            chiefAccountantFio: formModel.chiefAccountantFio as string,
            organizationTypeId: formModel.organizationTypeId as number,
            version: this.organization.version
        };
        return saveOrganization;
    }

    checkFormControl(controlName: string, errorName: string, errorNameMask?: string[] | null): boolean {
        const control: AbstractControl = this.organizationForm.controls[controlName];
        return FormControlChecker.checkControlError(control, errorName, errorNameMask);
    }

}
