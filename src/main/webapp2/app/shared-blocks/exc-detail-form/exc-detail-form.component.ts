import { Component, OnInit, OnDestroy} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';

export interface ExcDetailFormEntityProvider<T> {
  load: (id: any) => Observable<T>;
}

export interface ExcDetailFormParams {
    modificationEventName?: string;
    backUrl?: string;
    onSaveUrl?: string;
    onDeleteUrl?: string;
  }

export abstract class ExcDetailFormComponent implements OnInit, OnDestroy {

    private entityId: number;
    private paramsSubscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
                private params: ExcDetailFormParams,
                private eventManager: JhiEventManager,
                private router: Router,
                private activatedRoute: ActivatedRoute) { }

    ngOnInit() {
        this.paramsSubscription = this.activatedRoute.params.subscribe((params) => {
            if (params['id']) {
                this.entityId = params['id'];
                this.load(params['id']);
            }
        });
        this.registerChangeInOrganization();
    }

    ngOnDestroy() {
        this.paramsSubscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    abstract load(id: any);

    registerChangeInOrganization() {
        this.eventSubscriber = this.eventManager.subscribe(
            'organizationModification',
            (response) => this.load(this.entityId)
        );
    }

    previousState() {
        window.history.back();
    }

    navigateBack() {
        this.router.navigate(['organizations']);
    }

}
