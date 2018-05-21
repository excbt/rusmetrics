import { Component, OnInit, OnDestroy } from '@angular/core';
import { ExcEditFormComponent } from '../../shared-blocks';
import { PSubscriber } from './p-subscriber.model';
import { JhiEventManager } from 'ng-jhipster';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PSubscriberCustomerService } from './p-subscriber.service';
import { Subscription } from 'rxjs';
import { PSubscriberFormInitializer } from './p-subscriber.form-initializer';
import { MatSlideToggleChange } from '@angular/material';
import { TimezoneDef } from '../timezoneDef/timezoneDef.model';
import { TimezoneDefService } from '../timezoneDef/timezoneDef.service';

@Component({
    selector: 'jhi-p-subscriber-edit-customer',
    templateUrl: './p-subscriber-edit.component.html',
    styleUrls: ['../blocks/form-edit.scss']
  })
export class PSubscriberEditCustomerComponent extends ExcEditFormComponent<PSubscriber> implements OnInit, OnDestroy {

    private routeUrlSubscription: Subscription;
    private formInitializer: PSubscriberFormInitializer;

    subscriberMode: string;
    menuHeaderKey: string;

    timezoneDefOptions: TimezoneDef[] = [];

    constructor(
        eventManager: JhiEventManager,
        router: Router,
        activatedRoute: ActivatedRoute,
        private fb: FormBuilder,
        service: PSubscriberCustomerService,
        private timezoneDefService: TimezoneDefService) {
            super(
                {   modificationEventName: 'pSubscriberCustomerModification',
                    backUrl: '/customers',
                    onSaveUrl: '/customers',
                    onDeleteUrl: '/customers'
                },
                service.entityProvider(),
                eventManager,
                router,
                activatedRoute);

            this.formInitializer = new PSubscriberFormInitializer(this.fb);
            this.subscriberMode = 'CUSTOMER';

            this.routeUrlSubscription = this.activatedRoute.url.subscribe((data) => {
                this.menuHeaderKey = data[1].path === 'new' ? 'subscribers.customer.newTitle' : 'subscribers.customer.editTitle';
            });

            this.timezoneDefService.findAll().subscribe((data) => this.timezoneDefOptions = data);
    }

    ngOnDestroy() {
        if (this.routeUrlSubscription) {
            this.routeUrlSubscription.unsubscribe();
        }
        super.ngOnDestroy();
    }

    createForm(data: PSubscriber): FormGroup {
        return this.formInitializer.createForm(data);
    }

    initForm(): FormGroup {
        return this.formInitializer.initForm();
    }

    prepareEntity(form: FormGroup): PSubscriber {
        return this.formInitializer.prepareEntity(this.entityForm, this.entity);
    }

    navigateBack() {
        this.router.navigate(['customers']);
    }

    organizationSelect(id: number) {
        this.entityForm.controls['organizationId'].setValue(id);
        this.entityForm.controls['organizationId'].markAsDirty();
    }

    canCreateChildToggle(event: MatSlideToggleChange) {
        this.entityForm.controls['canCreateChild'].setValue(event.checked);
        this.entityForm.controls['canCreateChild'].markAsDirty();
    }

}
