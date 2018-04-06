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
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { debounceTime, distinctUntilChanged, startWith, tap, delay} from 'rxjs/operators';
import { DEBUG_INFO_ENABLED } from '../../app.constants';

const debounceTimeValue = 150;

@Component({
  selector: 'jhi-form-search',
  templateUrl: `./form-search.component.html`,
  styleUrls: ['./form-search.component.scss'],
  // encapsulation: ViewEncapsulation.Native
})
export class FormSearchComponent implements AfterViewInit {

    @ViewChild('input') input: ElementRef;
    @Output() readonly searchAction: EventEmitter<string> = new EventEmitter();

    // public searchAction$ = this.searchAction.asObservable;

    // private searchString = new BehaviorSubject<String>(null);
    // public searchString$ = this.searchString.asObservable();

    ngAfterViewInit() {
        Observable.fromEvent(this.input.nativeElement, 'keyup')
        .pipe(
            debounceTime(debounceTimeValue),
            distinctUntilChanged(),
            tap(() => {
                    const val = this.input.nativeElement.value;
                    this.searchAction.emit(val);
                    // this.searchString.next(val);
            })
        ).subscribe();
    }

}
