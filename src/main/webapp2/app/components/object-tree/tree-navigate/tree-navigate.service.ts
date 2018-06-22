import 'rxjs/add/operator/toPromise';
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

import { TreeNode } from 'primeng/api';

import { Observable } from 'rxjs/Observable';

import { PTreeNode } from '../models/p-tree-node.model';
import { PTreeNodeWrapper } from '../models/p-tree-node-wrapper.model';
// import { SubscrObjectTree } from '../models/subscr-object-tree.model';

import { SubscrPrefService, SubscrPrefValue } from '../subscr-pref';
import { SubscrTreeService, SubscrContObjectTreeType1 } from '../subscr-tree';
import { PTreeNodeMonitorService, PTreeNodeMonitor } from '../p-tree-node-monitor';

import { SERVER_API_URL } from '../../../app.constants';

@Injectable()
export class TreeNavigateService {

//    private SUBSCR_TREES_URL = '../api/subscr/subscrObjectTree/contObjectTreeType1';
//    private DEFAULT_TREE_SETTING_URL = '../api/subscr/subscrPrefValue?subscrPrefKeyname=SUBSCR_OBJECT_TREE_CONT_OBJECTS';

    private SUBSCR_OBJECT_TREE_CONT_OBJECTS = 'SUBSCR_OBJECT_TREE_CONT_OBJECTS';
    private P_TREE_NODE_URL = SERVER_API_URL + 'api/p-tree-node/';
    private RADIX = 10;

    private currentTree: TreeNode[];
    private subscrObjectTreeList: SubscrContObjectTreeType1[];
    private defaultTreeSetting: SubscrPrefValue;
    private ptreeNodeMonitor: PTreeNodeMonitor[];
//    private selectedPTreeNodeId: number;

    constructor(private http: HttpClient,
                 private subscrTreeService: SubscrTreeService,
                 private subscrPrefService: SubscrPrefService,
                 private pTreeNodeMonitorService: PTreeNodeMonitorService
                ) {}

//    getSelectedPTreeNodeId(): number {
//        return this.selectedPTreeNodeId;
//    }
//
//    setSelectedPTreeNodeId(nodeId: number) {
//        this.selectedPTreeNodeId = nodeId;
//    }

    getCurrentTree(): TreeNode[] {
        return this.currentTree;
    }

    setCurrentTree(curTree: TreeNode[]) {
        this.currentTree = curTree;
    }

    getSubscrObjectTreeList(): SubscrContObjectTreeType1[] {
        return this.subscrObjectTreeList;
    }

    setSubscrObjectTreeList(subscrTreeList: SubscrContObjectTreeType1[]) {
        this.subscrObjectTreeList = subscrTreeList;
    }

    getDefaultTreeSetting(): SubscrPrefValue {
        return this.defaultTreeSetting;
    }

    setDefaultTreeSetting(defTreeSet: SubscrPrefValue) {
        this.defaultTreeSetting = defTreeSet;
    }

    loadSubscrTrees(): Observable<SubscrContObjectTreeType1[]> {
        return this.subscrTreeService.loadAll();
//        return this.http.get<SubscrObjectTree[]>(this.SUBSCR_TREES_URL);
    }

    loadSubscrDefaultTreeSetting(): Observable<SubscrPrefValue> {
        return this.subscrPrefService.loadValue(this.SUBSCR_OBJECT_TREE_CONT_OBJECTS);
//        return this.http.get(this.DEFAULT_TREE_SETTING_URL);
    }

    loadPTree(treeId: number, childLvl = 0): Observable<TreeNode[]> {
//        this.setSelectedPTreeNodeId(treeId);
        return this.http.get<PTreeNode>(this.P_TREE_NODE_URL + treeId, {params : new HttpParams()
            .set('childLevel', childLvl.toString()) })
            .map((ptree: PTreeNode) => this.convertPTreeToPrimeNGTreeNode(ptree));
    }

    initTreeNavigate() {
        return Observable
            .forkJoin(this.loadSubscrTrees(), this.loadSubscrDefaultTreeSetting())
            .switchMap((res) => this.performTreesAndLoadDefaultPTree(res));
    }

    loadPTreeMonitor(ptreeId) {
        return this.pTreeNodeMonitorService
            .loadPTreeNodeMonitor(ptreeId)
            .map((resp) => this.ptreeNodeMonitor = resp);
    }

    performTreesAndLoadDefaultPTree(res) {
// console.log(res);
//        let trees = res[0];
        this.setSubscrObjectTreeList(res[0]);
        this.setDefaultTreeSetting(res[1]);
        let defaultTreeId: number;
        if (this.getDefaultTreeSetting() && this.getDefaultTreeSetting().value) {
            defaultTreeId = parseInt(this.getDefaultTreeSetting().value, this.RADIX);
        } else {
            defaultTreeId = this.getSubscrObjectTreeList()[0].id;
        }
//        let treeSetting = res[1];
//        this.pTreeNodeMonitorService
//            .loadPTreeNodeMonitor(defaultTreeId)
//            .subscribe((resp) => this.ptreeNodeMonitor = resp);
        return this.loadPTreeMonitorAndPTree(defaultTreeId); // this.loadPTreeMonitor(defaultTreeId).switchMap((resp) => this.loadPTree(defaultTreeId));

//        return Observable
//            .forkJoin(this.loadPTreeMonitor(defaultTreeId),
//                     this.loadPTree(defaultTreeId))
//            .switchMap((resp) => this.performMonitorAndPTree(resp));

//        return this.loadPTree(this.getDefaultTreeSetting().value);
    }

    loadPTreeMonitorAndPTree(treeId: number) {
        return this.loadPTreeMonitor(treeId).switchMap((resp) => this.loadPTree(treeId));
    }

//    performMonitorAndPTree(resp) {
//        console.log(resp);
//        const monitor: PTreeNodeMonitor[] = resp[0];
//        const tree: TreeNode[] = resp[1];
//        this.updateTreeView(tree[0], monitor);
//        return resp;
//    }

//    updateTreeView(tree: TreeNode, monitor: PTreeNodeMonitor[]) {
//        const ptreeWrapper: PTreeNodeWrapper = new PTreeNodeWrapper(tree.data);
//        const ptreeMonitor: PTreeNodeMonitor = monitor.find( (elm: PTreeNodeMonitor) => elm.monitorObjectId === ptreeWrapper.getPTreeNodeId());
//        ptreeWrapper.setPTreeMonitor(ptreeMonitor);

//        expandedIcon: 'fa-folder-open nmc-tree-nav-green-element',
//            collapsedIcon: 'fa-folder nmc-tree-nav-green-element',

//        tree.expandedIcon = ptreeWrapper.setExpandedIcon();
//        tree.collapsedIcon = ptreeWrapper.setCollapsedIcon();
//        tree.children.map((child) => this.updateTreeView(child, monitor));
// console.log(tree);
//    }

//    findPTreeNodeMonitor(monitor: PTreeNodeMonitor[], id: number): PTreeNodeMonitor {
//        monitor.find( elm: PTreeNodeMonitor => elm.monitorObjectId === id);
//    }

    convertPTreeToPrimeNGTreeNode(ptree: PTreeNode) {
        const expanded = true;
        const convertedTree = this.convertPTreeNodeToPrimeNGTreeNode(ptree, expanded);
        this.setCurrentTree([convertedTree]);
// console.log(convertedTree);
        return this.getCurrentTree();
    }

    convertPTreeNodeToPrimeNGTreeNode(inpPtreeNode: PTreeNode, isExpanded = false): TreeNode {
        let ptreeNodeWrapper: PTreeNodeWrapper; // = new PTreeNodeWrapper(inpPtreeNode);
        if (this.ptreeNodeMonitor && this.ptreeNodeMonitor !== null) {
            const nodeId = inpPtreeNode._id || inpPtreeNode.nodeObject.id;
            const ptreeMonitor: PTreeNodeMonitor = this.ptreeNodeMonitor.find( (elm: PTreeNodeMonitor) => elm.monitorObjectId === nodeId);
//            ptreeNodeWrapper.setPTreeMonitor(ptreeMonitor);
            ptreeNodeWrapper = new PTreeNodeWrapper(inpPtreeNode, ptreeMonitor);
        } else {
            ptreeNodeWrapper = new PTreeNodeWrapper(inpPtreeNode, null);
        }

//        let treeNode1: TreeNode = new TreeNode();

        const treeNode: TreeNode = <TreeNode> {
            data: ptreeNodeWrapper.getPTreeNode(),
            label: ptreeNodeWrapper.createTreeNodeLabel(),
            expandedIcon: ptreeNodeWrapper.setExpandedIcon(), /* 'fa-folder-open nmc-tree-nav-green-element', */
            collapsedIcon: ptreeNodeWrapper.setCollapsedIcon(), /* 'fa-folder nmc-tree-nav-green-element', */
            /* collapsedIcon: 'glyphicon glyphicon-folder', */
            expanded: isExpanded,
            children: [],
            leaf: ptreeNodeWrapper.isLeaf()
        };

        if (ptreeNodeWrapper.getPTreeNode().childNodes && !ptreeNodeWrapper.isContZpointNode()) {
            ptreeNodeWrapper.getPTreeNode().childNodes.forEach((childNode) => {
                treeNode.children.push(this.convertPTreeNodeToPrimeNGTreeNode(childNode));
            });
        }
        if (ptreeNodeWrapper.getPTreeNode().linkedNodeObjects && !ptreeNodeWrapper.isContZpointNode()) {
            ptreeNodeWrapper.getPTreeNode().linkedNodeObjects.forEach((nodeObject) => {
                treeNode.children.push(this.convertPTreeNodeToPrimeNGTreeNode(nodeObject));
            });
        }

        return treeNode;
    }
}
