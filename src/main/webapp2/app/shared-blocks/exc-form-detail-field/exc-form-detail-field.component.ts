import { Component, Input} from '@angular/core';

@Component({
  selector: 'jhi-exc-form-detail-field',
  templateUrl: `./exc-form-detail-field.component.html`,
  styleUrls: ['./exc-form-detail-field.component.scss'],
  // encapsulation: ViewEncapsulation.Native
})
export class ExcFormDetailFieldComponent {
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
