import { Component, OnInit, OnDestroy, HostBinding } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { OrganizationsService } from './organizations.service';
import { slideInDownAnimation } from '../animations';
import { Observable } from 'rxjs/Observable';
import { Organization } from './organization.model';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';

@Component({
  selector: 'jhi-organization-detail',
  templateUrl: './organization-detail.component.html',
//   animations: [ slideInDownAnimation ],
  styles: []
})
export class OrganizationDetailComponent implements OnInit, OnDestroy {

    // @HostBinding('@routeAnimation') routeAnimation = true;
    // @HostBinding('style.display')   display = 'block';
    // @HostBinding('style.position')  position = 'absolute';

    organization: Organization;

    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
                private eventManager: JhiEventManager,
                private service: OrganizationsService,
                private route: ActivatedRoute,
                private router: Router) { }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInOrganization();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    load(id) {
        this.service.find(id).subscribe((data) => {
            this.organization = data;
            console.log('id:' + this.organization.id);
        });
    }

    registerChangeInOrganization() {
        this.eventSubscriber = this.eventManager.subscribe(
            'organizationModification',
            (response) => this.load(this.organization.id)
        );
    }

    previousState() {
        window.history.back();
    }

}
