import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()
export class MainMenuService {

  private displayState: Boolean = false;

  private toggleMenuSubject: BehaviorSubject<Boolean> =  new BehaviorSubject(false);

  toggleMenuSubject$ = this.toggleMenuSubject.asObservable();

  constructor() { }

  toggleMainMenu() {
    console.log('toggle');
    this.displayState = !this.displayState;
    this.toggleMenuSubject.next(this.displayState);
  }

  getDisplay() {
  }

  showMainMenu() {
    this.displayState = true;
    this.toggleMenuSubject.next(true);
  }

  hideMainMenu() {
    this.displayState = false;
    this.toggleMenuSubject.next(false);
  }

}
