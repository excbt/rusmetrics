import { Component, ViewChild, AfterViewInit, ElementRef, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { ExcSearchToolService } from '../exc-tools/exc-search-tool-service';

@Component({
  selector: 'jhi-exc-search-input',
  template: `
    <mat-icon toolbar-right mat-list-icon>search</mat-icon>
    <input toolbar-right class="searchInput" type="text" pInputText #input>`,
  styles: [`
    .searchInput {
      font-size: 80%;
      height: 25px;
      width: 30rem;
  }`]
})
export class ExcSearchInputComponent implements AfterViewInit  {
  @ViewChild('input') input: ElementRef;
  @Output() readonly searchAction: EventEmitter<string> = new EventEmitter();

  private searchToolService = new ExcSearchToolService();

  constructor() {}

 ngAfterViewInit() {
    Observable.fromEvent(this.input.nativeElement, 'keyup')
      .subscribe((data) => this.searchToolService.filterInput(this.input.nativeElement.value));
    this.searchToolService.searchString$.subscribe((data) => {
      this.searchAction.emit(data);
    });
  }
}
