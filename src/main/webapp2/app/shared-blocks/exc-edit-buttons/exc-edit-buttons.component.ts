import { Component, Input, Output, EventEmitter} from '@angular/core';
import {
    FormGroup,
    Validators,
} from '@angular/forms';

@Component({
  selector: 'jhi-exc-edit-buttons',
  templateUrl: `./exc-edit-buttons.component.html`
})
export class ExcEditButtonsComponent {
  @Input() formGroup: FormGroup;
  @Input() deleteDisabled: boolean;
  @Output() saveAction: EventEmitter<any> = new EventEmitter();
  @Output() revertAction: EventEmitter<any> = new EventEmitter();
  @Output() deleteAction: EventEmitter<any> = new EventEmitter();

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

  delete() {
      this.deleteAction.emit(null);
  }
}
