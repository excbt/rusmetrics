import { Component, Input, Output, EventEmitter, HostBinding } from '@angular/core';
import { ViewEncapsulation } from '@angular/core';
import {
    FormControl,
    FormGroupDirective,
    FormGroup,
    NgForm,
    Validators
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
    styleUrls: ['./form-detail-field.component.scss']
})
export class FormDetailFieldComponent {
    @Input() placeholder: string;
    @Input() fieldValue: string;
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
