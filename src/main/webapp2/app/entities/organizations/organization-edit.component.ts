import { Component, OnInit, OnDestroy, HostBinding } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OrganizationsService } from './organizations.service';
import { slideInDownAnimation } from '../animations';
import { Organization } from './organization.model';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';
import { FormBuilder, FormGroup, AbstractControl, ValidatorFn } from '@angular/forms';
import { FormControl, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';

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
      console.log('invalidCaseLen:' + JSON.stringify(control.value) + ' len:' + String(control.value).length +
      ' cond: ' + goodInnLen + 'trim <' + String(control.value).trim() + '>');
      return !goodInnLen ? {'invalidCaseLen': {value: control.value}} : null;
    };
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

    matcher = new MyErrorStateMatcher();

    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(private fb: FormBuilder,
                private eventManager: JhiEventManager,
                private service: OrganizationsService,
                private route: ActivatedRoute) {
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
            inn: [data.inn, [caseLenValidator(10, 12), Validators.pattern('[0-9 ]*')]],
            kpp: [data.kpp, [Validators.minLength(9), Validators.maxLength(9), Validators.pattern('[0-9 ]*')]],
            okpo: [data.okpo, [caseLenValidator(8, 10), Validators.pattern('[0-9 ]*')]],
            ogrn: [data.ogrn, [Validators.minLength(13), Validators.maxLength(13), Validators.pattern('[0-9 ]*')]],
            legalAddress: [data.legalAddress],
            factAddress: [data.factAddress],
            postAddress: [data.postAddress],
            reqAccount: [data.reqAccount, [Validators.minLength(20), Validators.maxLength(20)]],
            reqBankName: [data.reqBankName],
            reqCorrAccount: [data.reqCorrAccount, [Validators.maxLength(20)]],
            reqBik: [data.reqBik, [Validators.maxLength(9)]],
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

    ngOnInit() {
        console.log('Edit load');
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInOrganization();
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

}
