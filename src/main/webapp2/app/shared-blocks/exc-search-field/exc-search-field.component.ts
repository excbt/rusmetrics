import {
    Component,
    Output,
    EventEmitter,
    ViewChild,
    ElementRef,
    AfterViewInit
} from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { debounceTime, distinctUntilChanged, tap} from 'rxjs/operators';
// import { DEBUG_INFO_ENABLED } from '../../app.constants';

const debounceTimeValue = 150;

@Component({
  selector: 'jhi-exc-search-field',
  templateUrl: `./exc-search-field.component.html`,
  styleUrls: ['./exc-search-field.component.scss'],
})
export class ExcSearchFieldComponent implements AfterViewInit {

    @ViewChild('input') input: ElementRef;
    @Output() readonly searchAction: EventEmitter<string> = new EventEmitter();

    ngAfterViewInit() {
        Observable.fromEvent(this.input.nativeElement, 'keyup')
        .pipe(
            debounceTime(debounceTimeValue),
            distinctUntilChanged(),
            tap(() => {
                    const val = this.input.nativeElement.value;
                    this.searchAction.emit(val);
            })
        ).subscribe();
    }

}
