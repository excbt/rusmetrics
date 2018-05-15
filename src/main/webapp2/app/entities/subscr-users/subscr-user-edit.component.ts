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
            userName: [data.userName, [Validators.required, Validators.min(1)]],
            userNickname: [data.userNickname],
            userComment: [data.userComment],
            userEmail: [data.userEmail],
            contactEmail: [data.contactEmail],
            isBlocked: [data.isBlocked],
            isAdmin: [data.isAdmin],
            isReadonly: [data.isReadonly],
            userDescription: [data.userDescription],
            newPassword: [null],
            confPassword: [null, [ExcCustomValidators.valuesEquals('newPassword')]]
        });
        this.initPasswordChanges(form);
        return form;
    }

    initForm(): FormGroup {
        const form = this.fb.group({
            userName: [null, [Validators.required, Validators.pattern('[a-z]*[a-z0-9_]*'), Validators.minLength(5)], this.validateUserNotTaken.bind(this)],
            userNickname: [null],
            userComment: [null],
            userEmail: [null],
            contactEmail: [null],
            isBlocked: [false],
            isAdmin: [false],
            isReadonly: [false],
            userDescription: [''],
            newPassword: [null],
            confPassword: [null, [ExcCustomValidators.valuesEquals('newPassword')]]
        });
        this.initPasswordChanges(form);
        return form;
    }

    initPasswordChanges(form: FormGroup) {
        const newPass = form.controls['newPassword'];
        const confPass = form.controls['confPassword'];
        newPass.valueChanges.subscribe((arg) => {
                console.log('Dirty');
                confPass.markAsTouched({ onlySelf: true });
        });
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
            isBlocked: this.checkEmpty(formModel.isBlocked as boolean),
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

    validateUserNotTaken(control: AbstractControl) {
        return this.subscrUserService.checkUserNotTaken(control.value).map((res) => {
            return res ? null : { usernameTaken: true };
        });
    }

    passwordConfirmation(frm: FormGroup) {
        const newPass = frm.controls['newPassword'];
        const confPass = frm.controls['confPassword'];
        console.log('Check Fire');
        const isOk =  (newPass && confPass) ? (newPass.value === confPass.value) || (newPass.untouched && confPass.untouched) : false;
        return {'passwordConfirmation': {value: isOk}};
    }

    passwordConfirmation1(controlName: string): ValidatorFn {
        //   console.log('invalidLength:' + JSON.stringify(control.value) + ' len:' + String(control.value).length +
        //   ' cond: ' + goodInnLen + 'trim <' + String(control.value).trim() + '>');
        return (control: AbstractControl): {[key: string]: any} => {
            const newPass = control.root.get(controlName);
            console.log('Check Fire');
            const isOk =  (newPass) ? (newPass.value === control.value)  : false;
            return !isOk ? {'passwordConfirmation': false} : null;
        };
        // return !isOk ? {'passwordConfirmation': {value: isOk}} : null;
    }

    validateAllFormFields(formGroup: FormGroup) {         // {1}
        Object.keys(formGroup.controls).forEach((field) => {  // {2}
            const control = formGroup.get(field);             // {3}
            if (control instanceof FormControl) {             // {4}
            control.markAsTouched({ onlySelf: true });
        } else if (control instanceof FormGroup) {        // {5}
            this.validateAllFormFields(control);            // {6}
        }
    });
}
}
