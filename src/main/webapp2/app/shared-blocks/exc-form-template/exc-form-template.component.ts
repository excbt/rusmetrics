import {
  Component,
  Input,
  Output,
  HostBinding} from '@angular/core';
import { excSlideInDownAnimation } from '../exc-animations';

@Component({
  selector: 'jhi-exc-form-template',
  templateUrl: `./exc-form-template.component.html`,
  animations: [ excSlideInDownAnimation ],
  styleUrls: ['./exc-form-template.component.scss']
})
export class ExcFormTemplateComponent {
  @HostBinding('@routeAnimation') routeAnimation = true;
  @HostBinding('style.display')   display = 'block';
  @HostBinding('style.position')  position = 'absolute';
}
