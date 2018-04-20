import 'rxjs/add/operator/toPromise';
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

import { TreeNode } from 'primeng/api';

import { Observable } from 'rxjs/Observable';

import { PTreeNode } from '../models/p-tree-node.model';
// import { SubscrObjectTree } from '../models/subscr-object-tree.model';

import { SubscrPrefService, SubscrPrefValue } from '../subscr-pref';
import { SubscrTreeService, SubscrContObjectTreeType1 } from '../subscr-tree';

@Injectable()
export class TreeNavigateService {

//    private SUBSCR_TREES_URL = '../api/subscr/subscrObjectTree/contObjectTreeType1';
//    private DEFAULT_TREE_SETTING_URL = '../api/subscr/subscrPrefValue?subscrPrefKeyname=SUBSCR_OBJECT_TREE_CONT_OBJECTS';

    private SUBSCR_OBJECT_TREE_CONT_OBJECTS = 'SUBSCR_OBJECT_TREE_CONT_OBJECTS';
    private P_TREE_NODE_URL = '../api/p-tree-node/';

    private currentTree: TreeNode[];
    private subscrObjectTreeList: SubscrContObjectTreeType1[];
    private defaultTreeSetting: SubscrPrefValue;

    constructor(private http: HttpClient,
                 private subscrTreeService: SubscrTreeService,
                 private subscrPrefService: SubscrPrefService
                ) {}

//    getTree() {
//        return this.http.get<any>('http://192.168.84.239:8089/testTree.json')
//            .toPromise()
//            .then((res) => <TreeNode[]> res.data);
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

    loadPTree(treeId, childLvl = 0): Observable<any> {
        return this.http.get<PTreeNode>(this.P_TREE_NODE_URL + treeId, {params : new HttpParams()
            .set('childLevel', childLvl.toString()) })
            .map((ptree: any) => this.convertPTreeToPrimeNGTreeNode(ptree));
    }

    initTreeNavigate() {
        return Observable
            .forkJoin(this.loadSubscrTrees(), this.loadSubscrDefaultTreeSetting())
            .switchMap((res) => this.performResAndLoadPTree(res));
    }

    performResAndLoadPTree(res) {
console.log(res);
//        let trees = res[0];
        this.setSubscrObjectTreeList(res[0]);
        this.setDefaultTreeSetting(res[1]);
//        let treeSetting = res[1];
        return this.loadPTree(this.getDefaultTreeSetting().value);
    }

    convertPTreeToPrimeNGTreeNode(ptree) {
        const expanded = true;
        const convertedTree = this.convertPTreeNodeToPrimeNGTreeNode(ptree, expanded);
        this.setCurrentTree([convertedTree]);
// console.log(convertedTree);
        return this.getCurrentTree();
    }

    convertPTreeNodeToPrimeNGTreeNode(ptreeNode, isExpanded = false) {
// console.log(ptreeNode);
        const treeNode: TreeNode = <TreeNode> {
            data: ptreeNode,
            label: this.createTreeNodeLabel(ptreeNode),
            expandedIcon: 'fa-folder-open',
            collapsedIcon: 'fa-folder',
            expanded: isExpanded,
            children: [],
            leaf: this.isDeviceNode(ptreeNode)
        };
//        treeNode.id = ptreeNode._id || ptreeNode.id || ptreeNode.nodeObject.id;
//        treeNode.data = ptreeNode;
//        treeNode.label = ptreeNode.nodeName;
//        treeNode.children = ptreeNode.childNodes ? ptreeNode.childNodes : [];
        if (ptreeNode.childNodes) {
            ptreeNode.childNodes.map((childNode) => {
                treeNode.children.push(this.convertPTreeNodeToPrimeNGTreeNode(childNode));
            });
        }
        if (ptreeNode.linkedNodeObjects) {
//            treeNode.children = treeNode.children.concat(ptreeNode.linkedNodeObjects);
            ptreeNode.linkedNodeObjects.map((nodeObject) => {
                treeNode.children.push(this.convertPTreeNodeToPrimeNGTreeNode(nodeObject));
            });
        }

//        let arr = new Array();
//        arr.push(treeNode);
// console.log('treeNode: ', treeNode);
        return treeNode;
    }

    isElementNode(ptreeNode) {
        return ptreeNode.nodeType === 'ELEMENT';
    }

    isConObjectNode(ptreeNode) {
// console.log('ptreeNode', ptreeNode);
// console.log('ptreeNode.nodeType === CONT_OBJECT', ptreeNode.nodeType === 'CONT_OBJECT');
        return ptreeNode.nodeType === 'CONT_OBJECT';
    }

    isContZpointNode(ptreeNode) {
        return ptreeNode.nodeType === 'CONT_ZPOINT';
    }

    isDeviceNode(ptreeNode) {
        return ptreeNode.nodeType === 'DEVICE_OBJECT';
    }

    createTreeNodeLabel(ptreeNode): string {
        let label = ''; // ptreeNode.nodeName || ptreeNode.nodeObject.fullName || ptreeNode.nodeObject.customServiceName || ptreeNode.nodeObject.number;
        if (this.isElementNode(ptreeNode)) {
            label = ptreeNode.nodeName;
        } else if (this.isConObjectNode(ptreeNode)) {
            label = ptreeNode.nodeObject.fullName;
        } else if (this.isContZpointNode(ptreeNode)) {
            label = ptreeNode.nodeObject.customServiceName;
        } else if (this.isDeviceNode(ptreeNode)) {
            label = ptreeNode.nodeObject.number;
        }
// console.log('label: ', label);
        return label;
    }
}
