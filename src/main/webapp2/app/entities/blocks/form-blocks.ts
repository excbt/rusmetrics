import { Component, Input, Output, EventEmitter, HostBinding, ViewChild } from '@angular/core';
import {
    FormGroup,
    Validators,
    AbstractControl,
    ValidatorFn
} from '@angular/forms';
import { DEBUG_INFO_ENABLED } from '../../app.constants';
import { slideInDownAnimation } from '../animations';
import { FormSearchComponent } from './form-search.component';

// ****************************************************************
// Form List Template
// ****************************************************************
@Component({
    selector: 'jhi-form-list-template',
    templateUrl: `./form-list-template.component.html`,
    animations: [ slideInDownAnimation ],
    styleUrls: ['./form-list-template.component.scss']
})
export class FormListTemplateComponent {
    @HostBinding('@routeAnimation') routeAnimation = true;
    @HostBinding('style.display')   display = 'block';
    @HostBinding('style.position')  position = 'absolute';

    @ViewChild(FormSearchComponent) formSearch: FormSearchComponent;
}

// ****************************************************************
// Validators for form
// ****************************************************************
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
