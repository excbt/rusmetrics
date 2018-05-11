import { Component, OnInit, OnDestroy } from '@angular/core';
import { ExcEditFormComponent } from '../../shared-blocks';
import { PSubscriber } from './p-subscriber.model';
import { JhiEventManager } from 'ng-jhipster';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PSubscriberService, PSubscriberPartnerService } from './p-subscriber.service';
import { Subscription } from 'rxjs';
import { PSubscriberFormInitializer } from './p-subscriber.form-initializer';
import { MatSlideToggleChange } from '@angular/material';

@Component({
    selector: 'jhi-p-subscriber-edit-partner',
    templateUrl: './p-subscriber-edit.component.html',
    styleUrls: ['../blocks/form-edit.scss']
  })
export class PSubscriberEditPartnerComponent extends ExcEditFormComponent<PSubscriber> implements OnInit, OnDestroy {

    private headerSubscription: Subscription;

    private routeUrlSubscription: Subscription;
    private formInitializer: PSubscriberFormInitializer;

    subscriberMode: string;
    menuHeaderKey: string;

    constructor(
        eventManager: JhiEventManager,
        router: Router,
        activatedRoute: ActivatedRoute,
        private fb: FormBuilder,
        service: PSubscriberPartnerService) {
            super(
                {   modificationEventName: 'pSubscriberPartnerModification',
                    backUrl: '/partners',
                    onSaveUrl: '/partners',
                    onDeleteUrl: '/partners'
                },
                service.entityProvider(),
                eventManager,
                router,
                activatedRoute);

            this.formInitializer = new PSubscriberFormInitializer(this.fb);

            this.routeUrlSubscription = this.activatedRoute.url.subscribe((data) => {
                this.subscriberMode = 'RMA';
                this.menuHeaderKey = data[1].path === 'new' ? 'subscribers.partner.newTitle' : 'subscribers.partner.editTitle';
            });
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
        this.router.navigate(['partners']);
    }

    organizationSelect(id: number) {
        this.entityForm.controls['organizationId'].setValue(id);
        this.entityForm.controls['organizationId'].markAsDirty();
    }

    canCreateChildToggle(event: MatSlideToggleChange) {
        this.entityForm.controls['canCreateChild'].setValue(event.checked);
        this.entityForm.controls['canCreateChild'].markAsDirty();
        console.log(event.checked);
    }

}
