import { NodeObject } from './node-object.model';

export class PTreeNode {
    constructor(
        public id: number,
         public nodeName: string,
         public nodeType: string,
         nodeObject: NodeObject,
         linkedNodeObjects: PTreeNode[],
         childNodes: PTreeNode[],
         dataType: string,   // STUB or FULL
         lazyNode: boolean
    ) {}
}
