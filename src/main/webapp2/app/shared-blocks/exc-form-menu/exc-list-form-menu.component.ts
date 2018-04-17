import { Component, OnInit, ViewChild, AfterViewInit, ViewEncapsulation, ElementRef, Input, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { debounceTime, distinctUntilChanged, startWith, tap, delay} from 'rxjs/operators';
import { MenuItem } from 'primeng/api';
import { Account,  Principal } from '../../shared';
import { JhiEventManager } from 'ng-jhipster';
import {searchDebounceTimeValue} from '../exc-tools/exc-constants';

@Component({
  selector: 'jhi-exc-list-form-menu',
  templateUrl: './exc-list-form-menu.component.html',
  styleUrls: ['../shared-blocks.scss']
})
export class ExcListFormMenuComponent implements OnInit, AfterViewInit  {
  @Input() headerKey: string;
  @ViewChild('input') input: ElementRef;
  @Output() readonly searchAction: EventEmitter<string> = new EventEmitter();
  @Output() reportAction: EventEmitter<any> = new EventEmitter();
  @Output() viewAction: EventEmitter<any> = new EventEmitter();
  @Output() createAction: EventEmitter<any> = new EventEmitter();

  @Input() reportEnabled: boolean;
  @Input() viewEnabled: boolean;
  @Input() createEnabled: boolean;

  account: Account;
  items: MenuItem[];

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

    this.items = [
      {
        icon: 'fa-caret-down',
        styleClass: 'menu-bars-size',
        items: [{
            icon: 'far fa-download',
            label: 'Отчет',
            id: 'report',
            command: (event) => {
              this.reportAction.next();
            }
          },
          {
            icon: 'fa-eye',
            label: 'Свойства',
            id: 'view',
            command: (event) => {
              this.viewAction.next();
            }
          },
          {
            icon: 'fa-plus',
            label: 'Создать',
            id: 'create',
            command: (event) => {
              this.createAction.next();
            }
          }
        ]
      }
    ];
  }

 ngAfterViewInit() {
    Observable.fromEvent(this.input.nativeElement, 'keyup')
    .pipe(
        debounceTime(searchDebounceTimeValue),
        distinctUntilChanged(),
        tap(() => {
                const val = this.input.nativeElement.value;
                this.searchAction.emit(val);
        })
    ).subscribe();
  }

  registerAuthenticationSuccess() {
    this.eventManager.subscribe('authenticationSuccess', (message) => {
        this.principal.identity().then((account) => {
            this.account = account;
            this.updateMenuAvailability(account);
        });
    });
  }

  updateMenuAvailability(account: Account) {
    this.updateMenuItemAvailability('create', this.createEnabled, 'ROLE_SUBSCR_ADMIN');
    this.updateMenuItemAvailability('view', this.viewEnabled);
    this.updateMenuItemAvailability('report', this.reportEnabled);
  }

  updateMenuItemAvailability(menuId: string,  menuEnabled: boolean, roleName?: string) {
    const menuLevel1: MenuItem = this.items[0];
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
