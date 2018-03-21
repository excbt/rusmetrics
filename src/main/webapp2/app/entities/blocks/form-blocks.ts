import { Component, Input, Output, EventEmitter } from '@angular/core';
import {
    FormControl,
    FormGroupDirective,
    FormGroup,
    NgForm,
    Validators
} from '@angular/forms';

@Component({
    selector: 'jhi-top-header',
    templateUrl: `./top-header.component.html`
})
export class TopHeaderComponent {
    @Input() headerTitle: string;
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
