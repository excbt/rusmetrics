import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-subscr-access-manage',
  template: `
  <jhi-exc-form-template>
    <jhi-cont-object-access></jhi-cont-object-access>
  </jhi-exc-form-template>
  `,
  styles: []
})
export class SubscrAccessManageComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
