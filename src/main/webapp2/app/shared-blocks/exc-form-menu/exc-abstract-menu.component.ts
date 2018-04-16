import { Component, OnInit, ViewChild, AfterViewInit, ViewEncapsulation, ElementRef, Input, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { debounceTime, distinctUntilChanged, startWith, tap, delay} from 'rxjs/operators';
import { MenuItem } from 'primeng/api';
import { Account,  Principal } from '../../shared';
import { JhiEventManager } from 'ng-jhipster';
import {searchDebounceTimeValue} from '../exc-tools/exc-constants';

export abstract class ExcAbstractMenuComponent implements OnInit  {
  account: Account;
  menuItems: MenuItem[];

  constructor(
    private principal: Principal,
    private eventManager: JhiEventManager
  ) {}

  ngOnInit() {

    this.principal.identity().then((account) => {
      this.account = account;
      this.updateMenuAvailability(account);
    });

    this.registerAuthenticationSuccess();
  }

  abstract updateMenuAvailability(account: Account);

  registerAuthenticationSuccess() {
    this.eventManager.subscribe('authenticationSuccess', (message) => {
        this.principal.identity().then((account) => {
            this.account = account;
            this.updateMenuAvailability(account);
        });
    });
  }

  updateMenuItemAvailability(menuId: string,  menuEnabled: boolean, roleName?: string) {
    if (!this.menuItems) {
      return;
    }
    const menuLevel1: MenuItem = this.menuItems[0];
    const menuLevel2: MenuItem[] = <MenuItem[]> menuLevel1.items;
    const menuItem = menuLevel2.find((x) => x.id === menuId);
    if (menuItem) {
      if (roleName) {
        menuItem.visible = (this.account.authorities.filter((x) => x === roleName).length > 0) && menuEnabled;
      } else {
        menuItem.visible = menuEnabled;
      }
    }
  }
}
