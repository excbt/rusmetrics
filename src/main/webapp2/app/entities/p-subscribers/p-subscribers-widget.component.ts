import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'jhi-p-subscribers-widget',
  templateUrl: './p-subscribers-widget.component.html',
  styles: []
})
export class PSubscribersWidgetComponent implements OnInit {

  @Input() subscrMode: string;

  readonly partnerMode = 'partner';
  readonly customerMode = 'customer';

  constructor() { }

  ngOnInit() {
  }

}
