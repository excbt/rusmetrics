import { Component, OnInit, ViewChild, AfterViewInit, ViewEncapsulation, ElementRef, Input, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { debounceTime, distinctUntilChanged, startWith, tap, delay} from 'rxjs/operators';
import { MenuItem } from 'primeng/api';

const debounceTimeValue = 150;

@Component({
  selector: 'jhi-form-list-menu',
  templateUrl: './form-list-menu.component.html',
  styleUrls: ['./form-list-menu.component.scss']
})
export class FormListMenuComponent implements OnInit, AfterViewInit  {
  @Input() headerString: string;
  @ViewChild('input') input: ElementRef;
  @Output() readonly searchAction: EventEmitter<string> = new EventEmitter();

  items: MenuItem[];

  ngOnInit() {
    this.items = [
      {
        icon: 'far fa-bars fa-2x',
        styleClass: 'menu1-bars',
        items: [{
          label: 'Отчет'

          },
          {
            label: 'Свойства'
          },
          {
            label: 'Создать'
          }
        ]
      }
    ];
  }

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
