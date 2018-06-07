import { Injectable, OnDestroy } from '@angular/core';
import { ExcAbstractService, defaultPageSuffix } from '../../shared-blocks/exc-tools/exc-abstract-service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SubscrObjectTree, SubscrObjectTreeVM, TreeNodeVM } from './subscr-object-tree.model';
import { ExcPage, ExcPageParams } from '../../shared-blocks';
import { BehaviorSubject, Observable } from 'rxjs';
import { ContObjectShortVM } from '../cont-objects/cont-object-shortVm.model';

export interface TreeDataVM {
    contObjectIds: number[];
}

@Injectable()
export class SubscrObjectTreeService extends ExcAbstractService<SubscrObjectTree> implements OnDestroy {

    private currentObjectTreeIdSubject = new BehaviorSubject<number>(null);

    public currentObjectTreeId$ = this.currentObjectTreeIdSubject.asObservable();

    private treeTypeSuffix1 = 'contObjectTreeType1/';

    readonly linkFilterAvailable = 'AVAILABLE';
    readonly linkFilterLinked = 'LINKED';

    constructor(http: HttpClient) {
        super({apiUrl: 'api/subscr-object-trees/'}, http);
    }

    findContObjectType1Page(pageParams: ExcPageParams, pageSuffix: string = defaultPageSuffix) {
        return this.http.get<ExcPage<SubscrObjectTree>>(this.resourceUrl + this.treeTypeSuffix1 + pageSuffix, {
            params : this.defaultPageParams(pageParams)
        } );

    }

    ngOnDestroy() {
        this.currentObjectTreeIdSubject.complete();
    }

    public selectNode(id: number) {
        this.currentObjectTreeIdSubject.next(id);
    }

    getContObjectType1(id: number): Observable<SubscrObjectTree> {
        return this.http.get<SubscrObjectTree>(this.resourceUrl + this.treeTypeSuffix1 + id);
    }

    addNewTree(newTreeName: string): Observable<SubscrObjectTree> {
        return this.http.put<SubscrObjectTree>(this.resourceUrl + this.treeTypeSuffix1 + 'new', null, {params: new HttpParams().set('newTreeName', newTreeName)});
    }

    updateTreeNodeNode(treeVM: SubscrObjectTree): Observable<SubscrObjectTreeVM> {
        return this.http.put<SubscrObjectTreeVM>(this.resourceUrl + this.treeTypeSuffix1, treeVM);
    }

    addTreeNodeNode(treeVM: SubscrObjectTreeVM): Observable<SubscrObjectTreeVM> {
        return this.http.put<SubscrObjectTreeVM>(this.resourceUrl + this.treeTypeSuffix1, treeVM);
    }

    deleteTreeNodeNode(treeVM: SubscrObjectTreeVM): Observable<any> {
        return this.http.delete<SubscrObjectTreeVM>(this.resourceUrl + this.treeTypeSuffix1 + treeVM.id);
    }

    getContObjects(treeNodeVM: TreeNodeVM, linkFilter: string): Observable<ContObjectShortVM[]> {
        let reqParams = new HttpParams();
        if (linkFilter) {
            reqParams = reqParams.set('linkFilter', linkFilter);
        }
        if (treeNodeVM.rootNodeId) {
            reqParams = reqParams.set('rootNodeId', treeNodeVM.rootNodeId + '');
        }
        if (treeNodeVM.nodeId) {
            reqParams = reqParams.set('nodeId', treeNodeVM.nodeId + '');
        }

        return this.http.get<ContObjectShortVM[]>(this.resourceUrl + this.treeTypeSuffix1 + 'cont-objects', {params: reqParams});
    }

    getContObjectsAvailable(treeNodeVM: TreeNodeVM): Observable<ContObjectShortVM[]> {
        return this.getContObjects(treeNodeVM, this.linkFilterAvailable);
    }

    getContObjectsLinked(treeNodeVM: TreeNodeVM): Observable<ContObjectShortVM[]> {
        return this.getContObjects(treeNodeVM, this.linkFilterLinked);
    }

    putContObjectDataAdd(nodeVM: TreeNodeVM, data: TreeDataVM): Observable<any> {
        let reqParams = new HttpParams();
        if (nodeVM && nodeVM.rootNodeId) {
            reqParams = reqParams.set('rootNodeId', '' + nodeVM.rootNodeId);
        }
        if (nodeVM && nodeVM.nodeId) {
            reqParams = reqParams.set('nodeId', '' + nodeVM.nodeId);
        }
        return this.http.put<any>(this.resourceUrl + this.treeTypeSuffix1 + 'add-cont-objects', data, {params: reqParams});
    }

    putContObjectDataRemove(nodeVM: TreeNodeVM, data: TreeDataVM): Observable<any> {
        let reqParams = new HttpParams();
        if (nodeVM && nodeVM.rootNodeId) {
            reqParams = reqParams.set('rootNodeId', '' + nodeVM.rootNodeId);
        }
        if (nodeVM && nodeVM.nodeId) {
            reqParams = reqParams.set('nodeId', '' + nodeVM.nodeId);
        }
        return this.http.put<any>(this.resourceUrl + this.treeTypeSuffix1 + 'remove-cont-objects', data, {params: reqParams});
    }

}
