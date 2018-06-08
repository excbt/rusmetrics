import { Component} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { OrganizationsService } from './organizations.service';
import { Organization, organizationModification } from './organization.model';
import { JhiEventManager  } from 'ng-jhipster';
import { ExcDetailFormComponent } from '../../shared-blocks/exc-detail-form/exc-detail-form.component';

@Component({
  selector: 'jhi-organization-detail',
  templateUrl: './organization-detail.component.html',
  styleUrls: ['../blocks/form-edit.scss', './organization-edit.component.scss']
})
export class OrganizationDetailComponent extends ExcDetailFormComponent {

    organization: Organization;

    constructor(
                private organizationService: OrganizationsService,
                eventManager: JhiEventManager,
                router: Router,
                activatedRoute: ActivatedRoute) {

        super({ modificationEventName: organizationModification },
                eventManager,
                router,
                activatedRoute);
    }

    load(id) {
        this.organizationService.findOne(id).subscribe((data) => {
            this.organization = data;
        });
    }

    navigateBack() {
        this.router.navigate(['organizations']);
    }
}
