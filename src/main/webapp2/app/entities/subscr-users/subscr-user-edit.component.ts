import { Component, OnInit, OnDestroy } from '@angular/core';
import { ExcEditFormComponent, ExcFormValue } from '../../shared-blocks';
import { SubscrUser } from './subscr-user.model';
import { SubscrUserService } from './subscr-user.service';
import { JhiEventManager } from 'ng-jhipster';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { MatSlideToggleChange } from '@angular/material';

@Component({
    selector: 'jhi-subscr-user-edit',
    templateUrl: './subscr-user-edit.component.html',
    styleUrls: ['../blocks/form-edit.scss']
})
export class SubscrUserEditComponent extends ExcEditFormComponent<SubscrUser> implements OnInit, OnDestroy {
    constructor(
        eventManager: JhiEventManager,
        router: Router,
        activatedRoute: ActivatedRoute,
        private fb: FormBuilder,
        private subscrUserService: SubscrUserService) {
            super(
                {   modificationEventName: 'subscrUserModification',
                backUrl: '/subscr-users',
                onSaveUrl: '/subscr-users',
                onDeleteUrl: '/subscr-users'
            },
            subscrUserService.entityProvider(),
            eventManager,
            router,
            activatedRoute);
        }

    createForm(data: SubscrUser): FormGroup {
        const form = this.fb.group({
            userName: [data.userName, [Validators.required]],
            userNickname: [data.userNickname],
            userComment: [data.userComment],
            userEmail: [data.userEmail],
            contactEmail: [data.contactEmail],
            isBlocked: [data.isBlocked],
            isAdmin: [data.isAdmin],
            isReadonly: [data.isReadonly],
            userDescription: [data.userDescription]
        });
        return form;
    }

    initForm(): FormGroup {
        const form = this.fb.group({
            userName: [null, [Validators.required]],
            userNickname: [null],
            userComment: [null],
            userEmail: [null],
            contactEmail: [null],
            isBlocked: [false],
            isAdmin: [false],
            isReadonly: [false],
            userDescription: ['']
        });
        return form;
    }

    prepareEntity(): SubscrUser {
        const formModel = this.entityForm.value;

        const subscrUser: SubscrUser = {
            id: (this.entity && this.entity.id) ? this.entity.id : null,
            userName: ExcFormValue.clearEmptyString(formModel.userName as string),
            userNickname: ExcFormValue.clearEmptyString(formModel.userNickname as string),
            version: (this.entity && this.entity.version) ? this.entity.version : 0,
            subscriberId: null,
            userUuid: null,
            userComment: ExcFormValue.clearEmptyString(formModel.userComment as string),
            userEmail: ExcFormValue.clearEmptyString(formModel.userEmail as string),
            isBlocked: null,
            contactEmail: this.checkEmpty(formModel.contactEmail as string),
            userDescription: this.checkEmpty(formModel.userDescription as string),
            isAdmin: this.checkEmpty(formModel.isAdmin as boolean),
            isReadonly: this.checkEmpty(formModel.isReadonly as boolean),
            authorities: []
        };
        return subscrUser;
    }

    isBlockedToggle(event: MatSlideToggleChange) {
        this.entityForm.controls['isBlocked'].setValue(event.checked);
        this.entityForm.controls['isBlocked'].markAsDirty();
    }

    isAdminToggle(event: MatSlideToggleChange) {
        this.entityForm.controls['isAdmin'].setValue(event.checked);
        this.entityForm.controls['isAdmin'].markAsDirty();
        this.entityForm.controls['isReadonly'].setValue(false);
        this.entityForm.controls['isReadonly'].markAsDirty();
    }

    isReadonlyToggle(event: MatSlideToggleChange) {
        this.entityForm.controls['isReadonly'].setValue(event.checked);
        this.entityForm.controls['isReadonly'].markAsDirty();
        this.entityForm.controls['isAdmin'].setValue(false);
        this.entityForm.controls['isAdmin'].markAsDirty();
    }

    navigateNew() {
        this.router.navigate(['subscr-users', 'new', 'edit']);
    }
}
