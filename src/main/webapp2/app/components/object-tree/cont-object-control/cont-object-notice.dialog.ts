import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material';

@Component({
    selector: 'jhi-cont-object-notice-dialog',
    templateUrl: './cont-object-notice.dialog.html'
})
export class ContObjectNoticeDialogComponent {
    constructor(@Inject(MAT_DIALOG_DATA) public data: any) {}
}
