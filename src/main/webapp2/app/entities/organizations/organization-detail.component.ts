import { Component, OnInit, OnDestroy} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { OrganizationsService } from './organizations.service';
import { slideInDownAnimation } from '../animations';
import { Organization } from './organization.model';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';

@Component({
  selector: 'jhi-organization-detail',
  templateUrl: './organization-detail.component.html',
  animations: [ slideInDownAnimation ],
  styleUrls: ['../blocks/form-edit.scss', './organization-edit.component.scss']
})
export class OrganizationDetailComponent implements OnInit, OnDestroy {

    organization: Organization;

    private paramsSubscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
                private eventManager: JhiEventManager,
                private service: OrganizationsService,
                private router: Router,
                private activatedRoute: ActivatedRoute) { }

    ngOnInit() {
        this.paramsSubscription = this.activatedRoute.params.subscribe((params) => {
            if (params['id']) {
                this.load(params['id']);
            }
        });
        this.registerChangeInOrganization();
    }

    ngOnDestroy() {
        this.paramsSubscription.unsubscribe();
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

    navigateBack() {
        this.router.navigate(['organizations']);
    }

}
