import { Component, OnInit, OnDestroy } from '@angular/core';
import { ExcEditFormComponent } from '../../shared-blocks';
import { PSubscriber } from './p-subscriber.model';
import { JhiEventManager } from 'ng-jhipster';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PSubscriberService } from './p-subscriber.service';
import { Subscription } from 'rxjs';
import { PSubscriberFormInitializer } from './p-subscriber.form-initializer';

@Component({
    selector: 'jhi-p-subscriber-edit',
    templateUrl: './p-subscriber-edit.component.html',
    styleUrls: ['../blocks/form-edit.scss']
  })
export class PSubscriberEditComponent extends ExcEditFormComponent<PSubscriber> implements OnInit, OnDestroy {

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
        service: PSubscriberService) {
            super(
                {   modificationEventName: 'pSubscriberModification',
                    backUrl: '/organizations',
                    onSaveUrl: '/organizations',
                    onDeleteUrl: '/organizations'
                },
                service.entityProvider(),
                eventManager,
                router,
                activatedRoute);

            this.formInitializer = new PSubscriberFormInitializer(this.fb);

            this.routeUrlSubscription = this.activatedRoute.url.subscribe((data) => {
                if (data && (data[0].path ===  'customers')) {
                    this.subscriberMode = 'CUSTOMER';
                    this.menuHeaderKey = data[1].path === 'new' ? 'subscribers.customer.newTitle' : 'subscribers.customer.editTitle';
                }
                if (data && (data[0].path ===  'partners')) {
                    this.subscriberMode = 'RMA';
                    this.menuHeaderKey = 'subscribers.partner.newTitle';
                    this.menuHeaderKey = data[1].path === 'new' ? 'subscribers.partner.newTitle' : 'subscribers.partner.editTitle';
                }
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
        this.router.navigate([this.subscriberMode === 'NORMAL' ? 'customers' : 'partners']);
    }

}
