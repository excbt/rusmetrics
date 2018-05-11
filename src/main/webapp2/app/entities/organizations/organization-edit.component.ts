import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager } from 'ng-jhipster';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Validators} from '@angular/forms';
import { Organization, organizationModification } from './organization.model';
import { OrganizationType } from '../organization-types/organization-type.model';
import { OrganizationsService } from './organizations.service';
import { OrganizationTypeService } from '../organization-types/organization-type.service';
import { ExcCustomValidators } from '../../shared-blocks';
import { ExcEditFormComponent } from '../../shared-blocks/exc-edit-form/exc-edit-form.component';
import { subscrUrlSuffix } from '../../shared-blocks/exc-tools/exc-constants';
import { MatSlideToggleChange } from '@angular/material';

@Component({
  selector: 'jhi-organization-edit',
  templateUrl: './organization-edit.component.html',
  styleUrls: ['../blocks/form-edit.scss', './organization-edit.component.scss']
})
export class OrganizationEditComponent extends ExcEditFormComponent<Organization> implements OnInit, OnDestroy {

    organizationTypes: OrganizationType[];

    private headerSubscription: Subscription;

    private routeUrlSubscription: Subscription;
    subscriberMode: boolean;
    menuHeaderKey: string;

    constructor(
        eventManager: JhiEventManager,
        router: Router,
        activatedRoute: ActivatedRoute,
        private fb: FormBuilder,
        service: OrganizationsService,
        private organizationTypeService: OrganizationTypeService) {
            super(
                {   modificationEventName: organizationModification,
                    backUrl: '/organizations',
                    onSaveUrl: '/organizations',
                    onDeleteUrl: '/organizations'
                },
                service.entityProvider(),
                eventManager,
                router,
                activatedRoute);

            this.routeUrlSubscription = this.activatedRoute.url.subscribe((data) => {
                this.subscriberMode = (data && (data[0].path ===  subscrUrlSuffix));
            });
            this.loadOrganizationTypes();

        }

    ngOnInit() {
        super.ngOnInit();
        this.headerSubscription = this.enitityId$.subscribe((id) => {
            this.menuHeaderKey = id ? 'organization.edit.title' : 'organization.new.title';
        });
    }

    ngOnDestroy() {
        this.headerSubscription.unsubscribe();
        this.routeUrlSubscription.unsubscribe();
        super.ngOnDestroy();
    }

    loadOrganizationTypes() {
        this.organizationTypeService.findAll().subscribe((data) => {
            this.organizationTypes = data;
        });
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
            inn: [null, [ExcCustomValidators.valueLength([10, 12]), ExcCustomValidators.onlyNumbersPattern()]],
            kpp: [null, [ExcCustomValidators.valueLength(9), ExcCustomValidators.onlyNumbersPattern()]],
            okpo: [null, [ExcCustomValidators.valueLength([8, 10]), ExcCustomValidators.onlyNumbersPattern()]],
            ogrn: [null, [ExcCustomValidators.valueLength(13), ExcCustomValidators.onlyNumbersPattern()]],
            legalAddress: null,
            factAddress: null,
            postAddress: null,
            reqAccount: [null, [ExcCustomValidators.valueLength(20), ExcCustomValidators.onlyNumbersPattern()]],
            reqBankName: null,
            reqCorrAccount: [null, [Validators.maxLength(20)]],
            reqBik: [null, [ExcCustomValidators.valueLength(9), ExcCustomValidators.onlyNumbersPattern()]],
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
            organizationTypeId: this.checkEmpty(formModel.organizationTypeId as number),
            version: this.entity ? this.entity.version : 1,
            organizationTypeName: null
        };
        return saveOrganization;
    }

    navigateNew() {
        this.router.navigate([this.subscriberMode ? subscrUrlSuffix : '', 'organizations', 'new', 'edit']);
    }

    navigateOnSave(entityId?: any) {
        this.router.navigate([this.subscriberMode ? subscrUrlSuffix : '', 'organizations']);
    }

    navigateOnDelete() {
        this.router.navigate([this.subscriberMode ? subscrUrlSuffix : '', 'organizations']);
    }

    navigateBack() {
        this.router.navigate([this.subscriberMode ? subscrUrlSuffix : '', 'organizations']);
    }

    flagRsoToggle(event: MatSlideToggleChange) {
        this.entityForm.controls['flagRso'].setValue(event.checked);
        this.entityForm.controls['flagRso'].markAsDirty();
    }

    flagCmToggle(event: MatSlideToggleChange) {
        this.entityForm.controls['flagCm'].setValue(event.checked);
        this.entityForm.controls['flagCm'].markAsDirty();
    }

    flagRmaToggle(event: MatSlideToggleChange) {
        this.entityForm.controls['flagRma'].setValue(event.checked);
        this.entityForm.controls['flagRma'].markAsDirty();
    }

}
