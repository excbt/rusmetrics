import { Component } from '@angular/core';

@Component({
  selector: 'jhi-operator-access-management',
  template: `
    <jhi-exc-form-template>
      <jhi-cont-object-access operationMode="OPERATOR"></jhi-cont-object-access>
    </jhi-exc-form-template>
  `
})
export class OperatorAccessManagementObjectsComponent {

  constructor() { }

}
