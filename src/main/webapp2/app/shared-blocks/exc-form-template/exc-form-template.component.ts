import {
  Component,
  HostBinding} from '@angular/core';
import { excSlideInDownAnimation } from '../exc-animations';

@Component({
  selector: 'jhi-exc-form-template',
  template: `
    <div>
      <ng-content></ng-content>
    </div>`,
  animations: [ excSlideInDownAnimation ],
  styles: [`
    :host {
      display: flex;
      padding: 0%;
      width: 100%;
    }`]
})
export class ExcFormTemplateComponent {
  @HostBinding('@routeAnimation') routeAnimation = true;
  @HostBinding('style.display')   display = 'block';
  @HostBinding('style.position')  position = 'absolute';
}
