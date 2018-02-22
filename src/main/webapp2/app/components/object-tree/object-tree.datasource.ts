import { CollectionViewer, DataSource, } from '@angular/cdk/collections';
import { Observable } from 'rxjs/Observable';
import { PTreeNode } from './p-tree-node.model';
import { PTreeNodeLinkedObjectService } from './object-tree.service';
import { PTreeNodeLinkedObject } from './p-tree-node.model';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

export class PTreeNodeLinkedObjectDataSource implements DataSource<PTreeNodeLinkedObject> {

    private lessonsSubject = new BehaviorSubject<PTreeNodeLinkedObject[]>([]);

    private loadingSubject = new BehaviorSubject<boolean>(false);

    public loading$ = this.loadingSubject.asObservable();

    constructor(private service: PTreeNodeLinkedObjectService) {

    }

    findNode(id) {
        this.loadingSubject.next(true);

        this.service.findLinkedObjects(id).pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe( (objects) => this.lessonsSubject.next(objects));
    }

    connect(collectionViewer: CollectionViewer): Observable<PTreeNodeLinkedObject[]> {
        return this.lessonsSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.lessonsSubject.complete();
        this.loadingSubject.complete();
    }

}
