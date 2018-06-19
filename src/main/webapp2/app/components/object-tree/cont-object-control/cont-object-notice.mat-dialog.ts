import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material';

@Component({
    selector: 'jhi-cont-object-notice-mat-dialog',
    templateUrl: './cont-object-notice.mat-dialog.html'
})
export class ContObjectNoticeMatDialogComponent {
    constructor(@Inject(MAT_DIALOG_DATA) public data: any) {}
}
