import {
  Validators,
  AbstractControl,
  ValidatorFn
} from '@angular/forms';

// ****************************************************************
// Validators for form
// ****************************************************************
export class ExcCustomValidators {

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

export class ExcFormControlChecker {

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
