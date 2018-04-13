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
import { ExcEditFormEntityProvider } from '../../shared-blocks/exc-edit-form/exc-edit-form.component';
import { FormGroupInitializer } from '../../shared-blocks/exc-edit-form/exc-edit-form.component';

@Component({
  selector: 'jhi-organization-edit-g',
  templateUrl: './organization-edit-g.component.html',
  styleUrls: ['../blocks/form-edit.scss', './organization-edit.component.scss']
})
export class OrganizationEditGComponent extends ExcEditFormComponent<Organization> implements OnInit, OnDestroy {

    organizationTypes: OrganizationType[];

    constructor(
        eventManager: JhiEventManager,
        router: Router,
        activatedRoute: ActivatedRoute,
        private fb: FormBuilder,
        private service: OrganizationsService,
        private organizationTypeService: OrganizationTypeService) {
            super(
                {   modificationEventName: 'organizationModification',
                    backUrl: '/organizations',
                    onSaveUrl: '/organizations'
                },
                service.entityProvider(),
                eventManager,
                router,
                activatedRoute);
            this.loadOrganizationTypes();
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
            version: this.entity.version
        };
        return saveOrganization;
    }

}
