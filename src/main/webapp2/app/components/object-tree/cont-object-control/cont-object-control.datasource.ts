import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { Observable } from 'rxjs/Observable';
import { ContObjectControl } from './cont-object-control.model';
import { ContObjectControlService } from './cont-object-control.service';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

export class ContObjectControlDataSource implements DataSource<ContObjectControl> {

    private contObjectControlList: ContObjectControl[] = [];
    private contObjectControlsSubject = new BehaviorSubject<ContObjectControl[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);

    public loading$ = this.loadingSubject.asObservable();

    constructor(private contObjectControlService: ContObjectControlService) {}

    loadContObjectControlList(contObjectIds: number[]) {
        this.contObjectControlList = [];
        this.loadingSubject.next(true);
        contObjectIds.map((contObjectId: number) => {
            this.contObjectControlService
                .loadMonitorState(contObjectId.toString())
                .pipe(catchError(() => of([])),
                      finalize(() => this.loadingSubject.next(false))
                     )
                .subscribe((coc: ContObjectControl) => {
                    this.contObjectControlList.push(coc);
                    this.contObjectControlsSubject.next(this.contObjectControlList);
                });
        });
    }

    connect(collectionViewer: CollectionViewer): Observable<ContObjectControl[]> {
        return this.contObjectControlsSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.contObjectControlsSubject.complete();
        this.loadingSubject.complete();
    }
}
