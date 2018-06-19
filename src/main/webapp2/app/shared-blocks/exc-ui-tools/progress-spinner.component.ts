import { Component } from '@angular/core';
import { LoadingStatusService } from '../exc-tools/loading-status-service';

@Component({
  selector: 'jhi-exc-progress-spinner',
  templateUrl: './progress-spinner.component.html',
  styleUrls: ['./progress-spinner.component.scss']
})
export class LoadingSpinnerComponent  {

    readonly color = 'secondary';
    readonly mode = 'indeterminate';

    readonly diameter = 20;

    constructor(readonly loadingStatus: LoadingStatusService) { }

}
