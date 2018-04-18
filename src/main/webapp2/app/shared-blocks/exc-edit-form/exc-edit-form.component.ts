import {
  OnInit,
  OnDestroy
} from '@angular/core';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { FormGroup, AbstractControl} from '@angular/forms';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Rx';
import { catchError, finalize } from 'rxjs/operators';
import {of} from 'rxjs/observable/of';

import { JhiEventManager  } from 'ng-jhipster';

import { ExcFormControlChecker } from '../../shared-blocks';

export interface ExcEditFormEntityProvider<T> {
  load: (id: any) => Observable<T>;
  update: (data: T) => Observable<T>;
  delete: (id: any) => Observable<T>;
}

export interface FormGroupInitializer<T> {
  initForm: () => FormGroup;
  createForm: (data: T) => FormGroup;
}

export interface ExcEditFormParams {
  modificationEventName?: string;
  backUrl?: string;
  onSaveUrl?: string;
  onDeleteUrl?: string;
}

// @Component({
//   selector: 'jhi-exc-edit-form',
//   templateUrl: `./exc-edit-form.component.html`
// })
export abstract class ExcEditFormComponent<T> implements OnInit, OnDestroy {

  entityForm: FormGroup;
  private eventSubscriber: Subscription;
  private newFlag: boolean;
  private entityId: number;
  private entityIdSubject = new BehaviorSubject<any>(null);
  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable();
  public enitityId$ = this.entityIdSubject.asObservable();

  private navigationSubscription:  Subscription;
  private paramsSubscription:  Subscription;

  entity: T;

  constructor(
    private params: ExcEditFormParams,
    private entityProvider: ExcEditFormEntityProvider<T>,
    private eventManager: JhiEventManager,
    readonly router: Router,
    readonly activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    this.paramsSubscription = this.activatedRoute.params.subscribe((params) => {
        if (params['id'] && params['id'] !== 'new') {
          this.entityId = params['id'];
          this.entityIdSubject.next(params['id']);
          this.loadData(params['id']);
        } else {
            this.newFlag = true;
        }
    });

    this.navigationSubscription = this.router.events.subscribe((e: any) => {
      // If it is a NavigationEnd event re-initalise the component
      if (e instanceof NavigationEnd) {
        this.reloadForm();
      }
    });
    this.entityForm = this.initForm();
    this.registerChangeInOrganization();
  }

  reloadForm() {
    if (this.newFlag) {
      this.entityForm = this.initForm();
      this.entityIdSubject.next(null);
    } else {
      this.loadData(this.entityId);
    }
  }

  registerChangeInOrganization() {
    if (this.params.modificationEventName) {
        this.eventSubscriber = this.eventManager.subscribe(
          this.params.modificationEventName,
          (response) => this.loadData(this.entityId)
      );
    }
  }

  ngOnDestroy() {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
    this.navigationSubscription.unsubscribe();
    this.paramsSubscription.unsubscribe();
  }

  abstract createForm(data: T): FormGroup;
  abstract initForm(): FormGroup;
  abstract prepareEntity(form: FormGroup);

  revertForm() {
    this.entityForm = this.createForm(this.entity);
  }

  saveForm() {
      this.updateData(this.entityForm);
  }

  private loadData(id: any) {
    this.entityProvider.load(id).subscribe((data) => {
      this.entity = data;
      this.entityForm = this.createForm(this.entity);
      this.entityIdSubject.next(id);
    });
  }

  private updateData(formGroup: FormGroup) {
    this.entity = this.prepareEntity(this.entityForm);
    this.loadingSubject.next(true);
    this.entity = this.prepareEntity(this.entityForm);

    this.entityProvider.update(this.entity).pipe(
        catchError(() => of([])),
        finalize(() => this.loadingSubject.next(false))
    )
    .subscribe( (data: T) => {
        this.loadingSubject.next(false);
        if (data['id']) {
            if (this.newFlag && this.params.onSaveUrl) {
              this.navigateOnSave();
            } else {
              if (this.params.onSaveUrl) {
                this.navigateOnSave(data['id']);
              } else {
                this.entityForm = this.createForm(this.entity = data);
              }
            }
        }
    });
  }

  checkEmpty(val: any) {
    return (val === '') ? null : val;
  }

  checkFormControl(controlName: string, errorName: string, errorNameMask?: string[] | null): boolean {
    if (this.entityForm) {
      const control: AbstractControl = this.entityForm.controls[controlName];
      return ExcFormControlChecker.checkControlError(control, errorName, errorNameMask);
    } else {
      return false;
    }
  }

  deleteEntity(id: any) {
    this.entityProvider.delete(id).pipe(
      catchError(() => of([])),
      finalize(() => this.loadingSubject.next(false))
    )
    .subscribe( (data: any) => {
        this.loadingSubject.next(false);
        this.navigateOnDelete();
    });
  }

  deleteAction() {
    this.deleteEntity(this.entityId);
  }

  navigateNew() {
    this.router.navigate([[this.params.onSaveUrl]]);
  }

  navigateOnSave(entityId?: any) {
    if (this.params.onSaveUrl) {
      this.router.navigate([this.params.onSaveUrl, entityId ? {id: entityId} : null]);
    }
  }

  navigateOnDelete() {
    if (this.params.onDeleteUrl) {
      this.router.navigate([this.params.onDeleteUrl]);
    }
  }

}
