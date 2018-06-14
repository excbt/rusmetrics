import {
  Component,
  HostBinding} from '@angular/core';
import { excSlideInDownAnimation } from '../exc-animations';
import { DEBUG_INFO_ENABLED } from '../../app.constants';

@Component({
  selector: 'jhi-exc-frame',
  templateUrl: './exc-frame.component.html',
  animations: [ excSlideInDownAnimation ],
  styleUrls: ['./exc-frame.component.scss']
})
export class ExcFrameComponent {
  @HostBinding('@routeAnimation') routeAnimation = true;
  @HostBinding('style.display')   display = 'block';
  @HostBinding('style.position')  position = 'absolute';

  showDebugFrame(): boolean {
    return DEBUG_INFO_ENABLED;
  }

}
