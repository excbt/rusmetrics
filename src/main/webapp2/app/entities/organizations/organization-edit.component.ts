import { Component, OnInit, OnDestroy, HostBinding } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { slideInDownAnimation } from '../animations';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';
import { FormBuilder, FormGroup, AbstractControl, ValidatorFn } from '@angular/forms';
import { FormControl, FormGroupDirective, NgForm, Validators, ValidationErrors } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';

import { Organization } from './organization.model';
import { OrganizationType } from '../organization-types/organization-type.model';
import { OrganizationsService } from './organizations.service';
import { OrganizationTypeService } from '../organization-types/organization-type.service';
import { catchError, finalize } from 'rxjs/operators';
import {of} from 'rxjs/observable/of';

/** Error when invalid control is dirty, touched, or submitted. */
export class MyErrorStateMatcher implements ErrorStateMatcher {
    isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
      const isSubmitted = form && form.submitted;
      return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
    }
}

export function caseLenValidator(v1: number, v2: number): ValidatorFn {
    return (control: AbstractControl): {[key: string]: any} => {
      const goodInnLen = (String(control.value).trim() === '') || (String(control.value).length === v1) || (String(control.value).length === v2);
    //   console.log('invalidLength:' + JSON.stringify(control.value) + ' len:' + String(control.value).length +
    //   ' cond: ' + goodInnLen + 'trim <' + String(control.value).trim() + '>');
      return !goodInnLen ? {'invalidLength': {value: control.value}} : null;
    };
}

export class FormControlCheck {
    constructor(
        public c: string,
        public e: string
    ) {}
}

@Component({
    selector: 'jhi-organization-edit',
    templateUrl: './organization-edit.component.html',
    animations: [ slideInDownAnimation ],
    styleUrls: ['./organization-edit.scss']
  })
  export class OrganizationEditComponent implements OnInit, OnDestroy {

    @HostBinding('@routeAnimation') routeAnimation = true;
    @HostBinding('style.display')   display = 'block';
    @HostBinding('style.position')  position = 'absolute';

    organizationForm: FormGroup;
    organization: Organization;

    organizationTypes: OrganizationType[];

    matcher = new MyErrorStateMatcher();

    loading: boolean;

    private subscription: Subscription;
    private eventSubscriber: Subscription;

    private onlyNumbersValidator: ValidatorFn = Validators.pattern('[0-9 ]*');

    constructor(private fb: FormBuilder,
                private eventManager: JhiEventManager,
                private service: OrganizationsService,
                private organizationTypeService: OrganizationTypeService,
                private route: ActivatedRoute) {
    }

    ngOnInit() {
        console.log('Edit load');
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInOrganization();
        this.loadOrganizationTypes();
    }

    loadOrganizationTypes() {
        this.organizationTypeService.findAll().subscribe((data) => {
            this.organizationTypes = data;
        });
    }

    createForm(data: Organization) {
        this.organizationForm = this.fb.group({

            id: [data.id, [Validators.required]],
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
            inn: [data.inn, [caseLenValidator(10, 12), this.onlyNumbersValidator]],
            kpp: [data.kpp, [Validators.minLength(9), Validators.maxLength(9), this.onlyNumbersValidator]],
            okpo: [data.okpo, [caseLenValidator(8, 10), this.onlyNumbersValidator]],
            ogrn: [data.ogrn, [Validators.minLength(13), Validators.maxLength(13), this.onlyNumbersValidator]],
            legalAddress: [data.legalAddress],
            factAddress: [data.factAddress],
            postAddress: [data.postAddress],
            reqAccount: [data.reqAccount, [caseLenValidator(20, 20), this.onlyNumbersValidator]],
            reqBankName: [data.reqBankName],
            reqCorrAccount: [data.reqCorrAccount, [caseLenValidator(20, 20), this.onlyNumbersValidator]],
            reqBik: [data.reqBik, [caseLenValidator(9, 9), Validators.maxLength(9), this.onlyNumbersValidator]],
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
            id: ['', [Validators.required]],
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

    load(id) {
        this.service.find(id).subscribe((data) => {
            this.organization = data;
            console.log('id:' + this.organization.id);
            this.createForm(data);
        });
    }

    registerChangeInOrganization() {
          this.eventSubscriber = this.eventManager.subscribe(
              'organizationModification',
              (response) => this.load(this.organization.id)
          );
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
        .subscribe( (organizations) => this.loading = false);
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
            organizationTypeId: formModel.organizationTypeId as number
        };
        return saveOrganization;
    }

    checkFormControl(controlName: string, errorName: string, errorNameMask?: string[] | null): boolean {
        const control: AbstractControl = this.organizationForm.controls[controlName];
        let mask = false;
        if (errorNameMask != null) {
            errorNameMask.forEach((i) => {
                mask = mask || control.hasError(i);
            });
        }
        return mask ? false : control.hasError(errorName);
    }

}
