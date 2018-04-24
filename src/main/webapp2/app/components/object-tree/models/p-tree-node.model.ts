// import { NodeObject } from './node-object.model';

export class PTreeNode {

    constructor(
        public _id: number,
         public nodeName: string,
         public nodeType: string,
         public nodeObject: any,
         public linkedNodeObjects: PTreeNode[],
         public childNodes: PTreeNode[],
         public dataType: string,   // STUB or FULL
         public lazyNode: boolean
    ) {}

//    constructor(ptreeNodeObject: any) {
//        this.id = ptreeNodeObject.id;
//        this.nodeName = ptreeNodeObject.nodeName;
//        this.nodeType = ptreeNodeObject.nodeObject;
//        this.nodeObject = ptreeNodeObject.nodeObject;
//        this.linkedNodeObjects = ptreeNodeObject.linkedNodeObjects;
//        this.childNodes = ptreeNodeObject.childNodes;
//        this.dataType = ptreeNodeObject.dataType;
//        this.lazyNode = ptreeNodeObject.lazyNode;
//    }

//    isElementNode() {
//        return this.nodeType === 'ELEMENT';
//    }

//    isConObjectNode() {
// console.log('ptreeNode', ptreeNode);
// console.log('ptreeNode.nodeType === CONT_OBJECT', ptreeNode.nodeType === 'CONT_OBJECT');
//        return this.nodeType === 'CONT_OBJECT';
//    }

//    isContZpointNode() {
//        return this.nodeType === 'CONT_ZPOINT';
//    }
//
//    isDeviceNode() {
//        return this.nodeType === 'DEVICE_OBJECT';
//    }

//    <span ng-if = "item.nodeObject.number">S/N: {{item.nodeObject.number}};</span>
//               <span ng-if = "item.nodeObject.deviceObjectName">Название: {{item.nodeObject.deviceObjectName}};</span>
//               <span ng-if = "item.nodeObject.deviceModelId">Модель: {{item.nodeObject.deviceModelId}};</span>
//               <span ng-show="$ctrl.isSystemuser()">(id = {{item.nodeObject.id}})</span>
//    public createTreeNodeLabel(): string {
//        let label = ''; // ptreeNode.nodeName || ptreeNode.nodeObject.fullName || ptreeNode.nodeObject.customServiceName || ptreeNode.nodeObject.number;
//        if (this.isElementNode()) {
//            label = this.nodeName;
//        } else if (this.isConObjectNode()) {
//            label = this.nodeObject.fullName;
//        } else if (this.isContZpointNode()) {
//            label = this.nodeObject.customServiceName;
//        } else if (this.isDeviceNode()) {
//            label = 'S/N: ' + this.nodeObject.number;
//            if (this.nodeObject.deviceObjectName) {
//                label += ', Название: ' + this.nodeObject.deviceObjectName;
//            }
//            if (this.nodeObject.deviceModelId) {
//                label += ', Модель: ' + this.nodeObject.deviceModelId;
//            }
//        }
//        return label;
//    }
}
