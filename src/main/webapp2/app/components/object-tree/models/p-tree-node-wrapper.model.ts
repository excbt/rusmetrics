import { PTreeNode } from './p-tree-node.model';
import { PTreeNodeMonitor } from '../p-tree-node-monitor';

export class PTreeNodeWrapper {

    private ptreeNodeMonitor: PTreeNodeMonitor;

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

    getPTreeNodeId(): number {
        let nodeId: number;
        if (this.ptreeNode._id && this.ptreeNode._id != null) {
            nodeId = this.ptreeNode._id;
        } else if (this.ptreeNode.nodeObject && this.ptreeNode.nodeObject != null) {
            nodeId = this.ptreeNode.nodeObject.id;
        } else {
            console.error('PtreeNode error: ', this.getPTreeNode());
            throw new Error('Wrong PTreeNode');
        }
        return nodeId;
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

    getExpandedTypeIcon() {
        let color = 'green';
        if (this.ptreeNodeMonitor && this.ptreeNodeMonitor.colorKey) {
            color = this.ptreeNodeMonitor.colorKey.toLowerCase();
        }
        if (this.isElementNode()) {
            return 'fa-folder-open' + ' ' + 'nmc-tree-nav-' + color + '-node';
        } else if (this.isContZpointNode()) {
            return 'fa-map-marker' + ' ' + 'nmc-tree-nav-zpoint-' + color + '-node';
        } else if (this.isConObjectNode()) {
            return this.getContObjectBuildingIcon();
        }
    }

    getContObjectBuildingIcon() {
        // get building category
        // TODO: Get building type icon by building category - how do it???   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<=======
        return 'nmc-building-default-16';
    }

    setExpandedIcon(): string {
        return this.getExpandedTypeIcon();
    }

    setCollapsedIcon(): string {
        return this.getExpandedTypeIcon();
    }

    setPTreeMonitor(ptreeMon: PTreeNodeMonitor) {
        this.ptreeNodeMonitor = ptreeMon;
    }

    isLeaf() {
        return this.isContZpointNode();
//        return this.isContZpointNode() ||
//            (this.ptreeNode.childNodes && this.ptreeNode.childNodes.length === 0 &&
//             this.ptreeNode.linkedNodeObjects && this.ptreeNode.linkedNodeObjects.length === 0);
    }
}
