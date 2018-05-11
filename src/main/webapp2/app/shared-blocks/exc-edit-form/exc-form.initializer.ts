import { FormGroup } from '@angular/forms';

export abstract class ExcFormInitializer<T> {

    abstract createForm(data: T): FormGroup;
    abstract initForm(): FormGroup;
    abstract prepareEntity(form: FormGroup, loadedEntity: T);

    checkEmpty(val: any) {
        return (val === '') ? null : val;
    }

}
