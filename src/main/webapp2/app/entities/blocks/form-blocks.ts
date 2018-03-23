import { Component, Input, Output, EventEmitter, HostBinding } from '@angular/core';
import {
    FormGroup,
    Validators,
    AbstractControl,
    ValidatorFn
} from '@angular/forms';
import { DEBUG_INFO_ENABLED } from '../../app.constants';
import { slideInDownAnimation } from '../animations';

@Component({
    selector: 'jhi-top-header',
    templateUrl: `./top-header.component.html`
})
export class TopHeaderComponent {
    @Input() headerTitle: string;
    @Input() headerId: any = null;

    showId(): boolean {
        return DEBUG_INFO_ENABLED;
    }
}

@Component({
    selector: 'jhi-form-edit-buttons',
    templateUrl: `./form-edit-buttons.component.html`
})
export class FormEditButtonsComponent {
    @Input() formGroup: FormGroup;
    @Output() saveAction: EventEmitter<any> = new EventEmitter();
    @Output() revertAction: EventEmitter<any> = new EventEmitter();

    constructor() {

    }

    previousState() {
        window.history.back();
    }

    save() {
        this.saveAction.emit(null);
    }

    revert() {
        this.revertAction.emit(null);
    }
}

@Component({
    selector: 'jhi-form-detail-field',
    templateUrl: `./form-detail-field.component.html`,
    styleUrls: ['./form-detail-field.component.scss'],
    // encapsulation: ViewEncapsulation.Native
})
export class FormDetailFieldComponent {
    @Input() placeholder: string;
    @Input() fieldValue: any;
    @Input() fieldType = 'text';

    isTextField(): boolean {
        return this.fieldType === 'text';
    }

    isCheckboxField(): boolean {
        return this.fieldType === 'check';
    }
}

@Component({
    selector: 'jhi-form-template',
    templateUrl: `./form-template.component.html`,
    animations: [ slideInDownAnimation ],
    styleUrls: ['./form-template.component.scss']
})
export class FormTemplateComponent {
    @HostBinding('@routeAnimation') routeAnimation = true;
    @HostBinding('style.display')   display = 'block';
    @HostBinding('style.position')  position = 'absolute';
}

export class CustomValidators {

    static numValueValidatorOld(v1: number, v2: number): ValidatorFn {
        return (control: AbstractControl): {[key: string]: any} => {
          const goodInnLen = (String(control.value).trim() === '') || (String(control.value).length === v1) || (String(control.value).length === v2);
        //   console.log('invalidLength:' + JSON.stringify(control.value) + ' len:' + String(control.value).length +
        //   ' cond: ' + goodInnLen + 'trim <' + String(control.value).trim() + '>');
          return !goodInnLen ? {'valueLength': {value: control.value}} : null;
        };
    }

    static valueLength(value: number | number[]): ValidatorFn {
        const checkNumbers: number[] = [];
        if (value instanceof Array) {
            value.forEach((i) => checkNumbers.push(i));
        } else {
            checkNumbers.push(value);
        }
        return (control: AbstractControl): {[key: string]: any} => {
          const goodInnLen = (String(control.value).trim() === '') || checkNumbers.includes(String(control.value).length);
        //   console.log('invalidLength:' + JSON.stringify(control.value) + ' len:' + String(control.value).length +
        //   ' cond: ' + goodInnLen + 'trim <' + String(control.value).trim() + '>');
          return !goodInnLen ? {'valueLength': {value: control.value}} : null;
        };
    }

    static onlyNumbersPattern(): ValidatorFn {
        return Validators.pattern('[0-9 ]*');
    }

}

export class FormControlChecker {

    static checkControlError(control: AbstractControl, errorName: string, errorNameMask?: string[] | null): boolean {
        let mask = false;
        if (errorNameMask != null) {
            errorNameMask.forEach((i) => {
                mask = mask || control.hasError(i);
            });
        }
        return mask ? false : control.hasError(errorName);
    }

}
