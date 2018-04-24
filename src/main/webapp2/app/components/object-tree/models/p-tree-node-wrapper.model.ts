import { PTreeNode } from './p-tree-node.model';

export class PTreeNodeWrapper {
    constructor(private ptreeNode: PTreeNode) {}

    getPTreeNode(): PTreeNode {
        return this.ptreeNode;
    }

    isElementNode() {
        return this.ptreeNode.nodeType === 'ELEMENT';
    }

    isConObjectNode() {
// console.log('ptreeNode', ptreeNode);
// console.log('ptreeNode.nodeType === CONT_OBJECT', ptreeNode.nodeType === 'CONT_OBJECT');
        return this.ptreeNode.nodeType === 'CONT_OBJECT';
    }

    isContZpointNode() {
        return this.ptreeNode.nodeType === 'CONT_ZPOINT';
    }

    isDeviceNode() {
        return this.ptreeNode.nodeType === 'DEVICE_OBJECT';
    }

//    <span ng-if = "item.nodeObject.number">S/N: {{item.nodeObject.number}};</span>
//               <span ng-if = "item.nodeObject.deviceObjectName">Название: {{item.nodeObject.deviceObjectName}};</span>
//               <span ng-if = "item.nodeObject.deviceModelId">Модель: {{item.nodeObject.deviceModelId}};</span>
//               <span ng-show="$ctrl.isSystemuser()">(id = {{item.nodeObject.id}})</span>
    public createTreeNodeLabel(): string {
        let label = ''; // ptreeNode.nodeName || ptreeNode.nodeObject.fullName || ptreeNode.nodeObject.customServiceName || ptreeNode.nodeObject.number;
        if (this.isElementNode()) {
            label = this.ptreeNode.nodeName;
        } else if (this.isConObjectNode()) {
            label = this.ptreeNode.nodeObject.fullName;
        } else if (this.isContZpointNode()) {
            label = this.ptreeNode.nodeObject.customServiceName;
        } else if (this.isDeviceNode()) {
            label = 'S/N: ' + this.ptreeNode.nodeObject.number;
            if (this.ptreeNode.nodeObject.deviceObjectName) {
                label += ', Название: ' + this.ptreeNode.nodeObject.deviceObjectName;
            }
            if (this.ptreeNode.nodeObject.deviceModelId) {
                label += ', Модель: ' + this.ptreeNode.nodeObject.deviceModelId;
            }
        }
// console.log('label: ', label);
        return label;
    }
}
