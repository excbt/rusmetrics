import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { ViewEncapsulation } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { SidebarModule } from 'primeng/sidebar';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';

import { MainMenuService } from './main-menu.service';

@Component({
  selector: 'jhi-left-main-menu',
  templateUrl: './left-main-menu.component.html',
  styleUrls: [
    './left-main-menu.scss'
  ],
  encapsulation: ViewEncapsulation.None
})
export class LeftMainMenuComponent implements OnInit, OnDestroy {

  display: Boolean;
  mainMenuTogleSubscription: Subscription;

  constructor(
    private mainMenuService: MainMenuService,
    private router: Router
  ) {
    this.mainMenuTogleSubscription = this.mainMenuService.toggleMenuSubject$.subscribe(
      (displayState: Boolean) => {
        this.display = displayState;
      });
  }

  ngOnInit() {
  }

  ngOnDestroy() {
    if (this.mainMenuTogleSubscription) {
      this.mainMenuTogleSubscription.unsubscribe();
    }
  }

  toggleMenu() {
    this.mainMenuService.toggleMainMenu();
  }

  hideMenu() {
    this.mainMenuService.hideMainMenu();
  }

  showMenu() {
    this.mainMenuService.showMainMenu();
  }

}
