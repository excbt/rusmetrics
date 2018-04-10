import { Component, OnInit, ViewChild, AfterViewInit, ViewEncapsulation, ElementRef, Input, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { debounceTime, distinctUntilChanged, startWith, tap, delay} from 'rxjs/operators';
import { MenuItem } from 'primeng/api';
import { Account,  Principal } from '../../shared';
import { JhiEventManager } from 'ng-jhipster';

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
  @Output() reportAction: EventEmitter<any> = new EventEmitter();
  @Output() viewAction: EventEmitter<any> = new EventEmitter();
  @Output() createAction: EventEmitter<any> = new EventEmitter();

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
      console.log('Account authorities: ' + account.authorities);
    });

    this.registerAuthenticationSuccess();

    this.items = [
      {
        icon: 'fa-bars',
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
        debounceTime(debounceTimeValue),
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
    const menuLevel1: MenuItem = this.items[0];
    const menuLevel2: MenuItem[] = <MenuItem[]> menuLevel1.items;
    const createItem = menuLevel2.find((x) => x.id === 'create');
    const reportItem = menuLevel2.find((x) => x.id === 'report');
    const viewItem = menuLevel2.find((x) => x.id === 'view');

    if (createItem) {
      createItem.visible = account.authorities.filter((x) => x === 'ROLE_SUBSCR_ADMIN').length > 0;
    }
  }

}
