import { Component, OnInit, ViewChild, AfterViewInit, ViewEncapsulation, ElementRef, Input, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { debounceTime, distinctUntilChanged, startWith, tap, delay} from 'rxjs/operators';
import { MenuItem } from 'primeng/api';
import { Account,  Principal } from '../../shared';
import { JhiEventManager } from 'ng-jhipster';
import {searchDebounceTimeValue} from '../exc-tools/exc-constants';
import { ExcAbstractMenuComponent } from './exc-abstract-menu.component';

@Component({
  selector: 'jhi-exc-edit-form-menu',
  templateUrl: './exc-edit-form-menu.component.html',
  styleUrls: ['../shared-blocks.scss']
})
export class ExcEditFormMenuComponent extends ExcAbstractMenuComponent implements OnInit {
  @Input() headerString: string;
  @Input() createEnabled: boolean;
  @Output() saveAction: EventEmitter<any> = new EventEmitter();
  @Output() createAction: EventEmitter<any> = new EventEmitter();
  @Output() editAction: EventEmitter<any> = new EventEmitter();
  @Output() reportAction: EventEmitter<any> = new EventEmitter();

  constructor(
    principal: Principal,
    eventManager: JhiEventManager
  ) {
    super(principal, eventManager);
  }

  ngOnInit() {
    super.ngOnInit();
    this.menuItems = [
      {
        icon: 'fa-caret-down fa-2x',
        styleClass: 'menu-bars-size',
        items: [
          {
            icon: 'fa-plus',
            label: 'Создать',
            id: 'create',
            command: (event) => {
              this.createAction.next();
            }
          },
          {
            icon: 'far fa-download',
            label: 'Отчет',
            id: 'report',
            command: (event) => {
              this.reportAction.next();
            },
          }
        ]
      }
    ];
  }

  updateMenuAvailability(account: Account) {
    this.updateMenuItemAvailability('create', this.createEnabled === true , 'ROLE_SUBSCR_ADMIN');
  }

}
