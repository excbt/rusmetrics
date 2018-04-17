import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'jhi-organizations-widget',
  templateUrl: './organizations-widget.component.html',
  styles: []
})
export class OrganizationsWidgetComponent implements OnInit {

  @Input() masterFlag: boolean;

  constructor() { }

  ngOnInit() {
  }

}
