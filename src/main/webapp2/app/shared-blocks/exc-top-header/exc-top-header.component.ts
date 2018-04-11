import { Component, Input, Output, EventEmitter} from '@angular/core';
import { DEBUG_INFO_ENABLED } from '../../app.constants';

@Component({
  selector: 'jhi-exc-top-header',
  templateUrl: `./top-header.component.html`
})
export class ExcTopHeaderComponent {
  @Input() headerTitle: string;
  @Input() headerId: any = null;

  showId(): boolean {
      return DEBUG_INFO_ENABLED;
  }
}
