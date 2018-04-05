import {
    Component,
    Input,
    Output,
    EventEmitter,
    HostBinding,
    ViewChild,
    ElementRef,
    AfterViewInit
} from '@angular/core';
import {
    FormGroup,
    Validators,
    AbstractControl,
    ValidatorFn
} from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import { debounceTime, distinctUntilChanged, startWith, tap, delay} from 'rxjs/operators';
import { DEBUG_INFO_ENABLED } from '../../app.constants';

@Component({
  selector: 'jhi-form-search',
  templateUrl: `./form-search.component.html`,
  styleUrls: ['./form-search.component.scss'],
  // encapsulation: ViewEncapsulation.Native
})
export class FormSearchComponent implements AfterViewInit {

    @ViewChild('input') input: ElementRef;
    @Output() searchAction: EventEmitter<String> = new EventEmitter();

    ngAfterViewInit() {
        Observable.fromEvent(this.input.nativeElement, 'keyup')
        .pipe(
            debounceTime(150),
            distinctUntilChanged(),
                tap(() => {
                    this.searchAction.emit(this.input.nativeElement.value);
            })
        ).subscribe();
    }

}
