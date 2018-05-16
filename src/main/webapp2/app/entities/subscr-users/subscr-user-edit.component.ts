import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { ExcEditFormComponent, ExcFormValue, ExcCustomValidators } from '../../shared-blocks';
import { SubscrUser } from './subscr-user.model';
import { SubscrUserService } from './subscr-user.service';
import { JhiEventManager } from 'ng-jhipster';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators, FormControl, AbstractControl, ValidatorFn } from '@angular/forms';
import { MatSlideToggleChange } from '@angular/material';
import { debounceTime } from 'rxjs/operators';
import { distinctUntilChanged } from 'rxjs/operators';
import { searchDebounceTimeValue } from '../../shared-blocks/exc-tools/exc-constants';

@Component({
    selector: 'jhi-subscr-user-edit',
    templateUrl: './subscr-user-edit.component.html',
    styleUrls: ['../blocks/form-edit.scss']
})
export class SubscrUserEditComponent extends ExcEditFormComponent<SubscrUser> implements OnInit, OnDestroy {
    constructor(eventManager: JhiEventManager,
                router: Router,
                activatedRoute: ActivatedRoute,
                private fb: FormBuilder,
                private subscrUserService: SubscrUserService) {
        super(
            {modificationEventName: 'subscrUserModification',
            backUrl: '/subscr-users',
            onSaveUrl: '/subscr-users',
            onDeleteUrl: '/subscr-users'
        },
        subscrUserService.userEntityProvider(),
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
            userDescription: [data.userDescription],
            newPassword: [null]
            // confPassword: [null, [ExcCustomValidators.valuesEquals('newPassword')]]
        });
        this.initPasswordChanges(form);
        return form;
    }

    initForm(): FormGroup {
        const form = this.fb.group({
            userName: ['', [Validators.required, Validators.pattern('[a-z]*[a-z0-9_]*'), Validators.minLength(5)], this.validateUserNotTaken.bind(this)],
            userNickname: [''],
            userComment: [''],
            userEmail: [''],
            contactEmail: [''],
            userDescription: [''],
            isBlocked: [false],
            isAdmin: [false],
            isReadonly: [false],
            newPassword: [null, [Validators.required, Validators.minLength(5)]]
            // confPassword: [null, [ExcCustomValidators.valuesEquals('newPassword')]]
        });
        this.initPasswordChanges(form);
        return form;
    }

    initPasswordChanges(form: FormGroup) {
        const newPass = form.controls['newPassword'];
        if (newPass) {
            newPass.valueChanges.subscribe((arg) => {
                if (!form.controls['confPassword']) {
                    const contControl = new FormControl('', [Validators.required, ExcCustomValidators.valuesEquals('newPassword')]);
                    form.addControl('confPassword', contControl);
                } else {
                    form.controls['confPassword'].reset();
                }
                if (!this.newFlag) {
                    if (!form.controls['oldPassword']) {
                        const contControl = new FormControl('', [Validators.required]);
                        form.addControl('oldPassword', contControl);
                    }
                }
            });
        }
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
            isBlocked: ExcFormValue.checkEmpty(formModel.isBlocked as boolean),
            contactEmail: ExcFormValue.checkEmpty(formModel.contactEmail as string),
            userDescription: ExcFormValue.checkEmpty(formModel.userDescription as string),
            isAdmin: ExcFormValue.checkEmpty(formModel.isAdmin as boolean),
            isReadonly: ExcFormValue.checkEmpty(formModel.isReadonly as boolean),
            newPassword: ExcFormValue.clearEmptyString(formModel.newPassword as string),
            oldPassword: this.entityForm.controls['oldPassword'] ? ExcFormValue.clearEmptyString(formModel.oldPassword as string) : null,
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

    validateUserNotTaken(control: AbstractControl) {
        return this.subscrUserService.checkUserNotTaken(control.value).map((res) => {
            return res ? null : { usernameTaken: true };
        });
    }

}
