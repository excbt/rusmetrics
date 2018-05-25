import { BehaviorSubject } from 'rxjs';
import { Observable } from 'rxjs/Observable';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

const searchDebounceTimeValue = 150;

export class ExcSearchToolService {
  private inputSearchString = new BehaviorSubject<string>(null);

  readonly searchString$: Observable<string>;

  constructor() {
    this.searchString$ = this.inputSearchString.pipe(
        debounceTime(searchDebounceTimeValue),
        distinctUntilChanged(),
        // tap((arg) => console.log('fire'))
    );

  }

  filterInput(s: string) {
    this.inputSearchString.next(s);
  }

}
