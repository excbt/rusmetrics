import { Component, Input, OnInit} from '@angular/core';
import { DEBUG_INFO_ENABLED } from '../../app.constants';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'jhi-exc-top-header',
  templateUrl: `./exc-top-header.component.html`,
  styleUrls: ['../shared-blocks.scss']
})
export class ExcTopHeaderComponent implements OnInit {
  @Input() headerString: string;
  @Input() headerId: any = null;

  items: MenuItem[];

  showId(): boolean {
      return DEBUG_INFO_ENABLED;
  }

  ngOnInit() {

    this.items = [
      {
        icon: 'fa-caret-down fa-2x',
        styleClass: 'menu-bars-size',
      }
    ];
  }

}
