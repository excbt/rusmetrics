import { Component, OnInit, OnDestroy } from '@angular/core';
import { ExcEditFormComponent } from '../../shared-blocks';
import { PSubscriber } from './p-subscriber.model';
import { JhiEventManager } from 'ng-jhipster';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PSubscriberService } from './p-subscriber.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'jhi-p-subscriber-edit',
    templateUrl: './p-subscriber-edit.component.html',
    styleUrls: ['../blocks/form-edit.scss']
  })
export class PSubscriberEditComponent extends ExcEditFormComponent<PSubscriber> implements OnInit, OnDestroy {

    private headerSubscription: Subscription;

    private routeUrlSubscription: Subscription;

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

            this.routeUrlSubscription = this.activatedRoute.url.subscribe((data) => {
                if (data && (data[0].path ===  'customers')) {
                    this.subscriberMode = 'NORMAL';
                    this.menuHeaderKey = data[1].path === 'new' ? 'subscribers.subsriber.newTitle' : 'subscribers.subsriber.editTitle';
                }
                if (data && (data[0].path ===  'partners')) {
                    this.subscriberMode = 'RMA';
                    this.menuHeaderKey = 'subscribers.partner.newTitle';
                    this.menuHeaderKey = data[1].path === 'new' ? 'subscribers.partner.newTitle' : 'subscribers.partner.editTitle';
                }
            });
        }

    createForm(data: PSubscriber): FormGroup {
        const form = this.fb.group({
            id: data.id,
            organizationId: [data.organizationId],
            subscriberName: [data.subscriberName],
            subscriberInfo: [data.subscriberInfo],
            subscriberComment: [data.subscriberComment],
            timezoneDef: [data.timezoneDef],
            subscrType: [data.subscrType],
            contactEmail: [data.contactEmail],
        });
        return form;
    }

    initForm(): FormGroup {
        const form = this.fb.group({
            id: null,
            organizationId: null,
            subscriberName: null,
            subscriberInfo: null,
            subscriberComment: null,
            subscrType: null,
            canCreateChild: false,
            contactEmail: null,
        });
        return form;
    }

    prepareEntity(form: FormGroup): PSubscriber {
        const formModel = this.entityForm.value;

        const savePSubscriber: PSubscriber = {
            id: this.checkEmpty(formModel.id as number),
            organizationId: this.checkEmpty(formModel.organizationId as number),
            subscriberName: this.checkEmpty(formModel.subscriberName as string),
            subscriberInfo: this.checkEmpty(formModel.subscriberInfo as string),
            subscriberComment: this.checkEmpty(formModel.subscriberComment as string),
            timezoneDef: this.checkEmpty(formModel.timezoneDef as string),
            subscrType: this.checkEmpty(formModel.subscrType as string),
            contactEmail: this.checkEmpty(formModel.contactEmail as string),
            version: this.entity.version
        };
        return savePSubscriber;
    }

    navigateBack() {
        this.router.navigate([this.subscriberMode === 'NORMAL' ? 'customers' : 'partners']);
    }

}
