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
}
