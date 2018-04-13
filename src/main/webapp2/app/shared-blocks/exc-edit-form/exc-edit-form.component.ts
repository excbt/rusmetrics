import {
  Component,
  Input,
  Output,
  OnInit,
  OnDestroy
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, AbstractControl} from '@angular/forms';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Rx';
import { catchError, finalize } from 'rxjs/operators';
import {of} from 'rxjs/observable/of';

import { JhiEventManager  } from 'ng-jhipster';

import { ExcCustomValidators, ExcFormControlChecker } from '../../shared-blocks';

export interface EntityProvider<T> {
  load: (id: any) => Observable<T>;
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

  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();

  entity: T;

  constructor(
    private eventManager: JhiEventManager,
    private router: Router,
    private activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    // this.subscription =
    console.log('Start Init');
    this.activatedRoute.params.subscribe((params) => {
        if (params['id'] && params['id'] !== 'new') {
          this.entityId = params['id'];
          this.loadData(this.entityId);
        } else {
            this.newFlag = true;
        }
    });
    this.entityForm = this.initForm();
    // this.registerChangeInOrganization();
    // this.loadOrganizationTypes();
  }

  registerChangeInOrganization() {
    this.eventSubscriber = this.eventManager.subscribe(
        'organizationModification',
        (response) => this.loadEntity(this.entityId)
    );
  }

  ngOnDestroy() {
    // this.subscription.unsubscribe();
    if (this.eventSubscriber) {
        this.eventManager.destroy(this.eventSubscriber);
    }
  }

  abstract createForm(data: T): FormGroup;
  abstract initForm(): FormGroup;

  abstract loadEntity(id: number): Observable<T>;
  abstract updateEntity(data: T): Observable<T>;
  abstract prepareEntity(form: FormGroup);

  revertForm() {
    this.entityForm = this.createForm(this.entity);
  }

  saveForm() {
      this.updateData(this.entityForm);
  }

  private loadData(id: any) {
    this.loadEntity(id).subscribe((data) => {
      this.entity = data;
      this.entityForm = this.createForm(this.entity);
    });
  }

  private updateData(formGroup: FormGroup) {
    this.entity = this.prepareEntity(this.entityForm);
    this.loadingSubject.next(true);
    this.entity = this.prepareEntity(this.entityForm);

    this.updateEntity(this.entity).pipe(
        catchError(() => of([])),
        finalize(() => this.loadingSubject.next(false))
    )
    .subscribe( (data: T) => {
        this.loadingSubject.next(false);
        if (data['id']) {
            if (this.newFlag) {
                this.router.navigate(['/organizations']);
            } else {
              this.entityForm = this.createForm(this.entity = data);
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

}
