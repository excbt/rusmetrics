
export class PTreeNode {
    constructor(
        public _id: number,
        public nodeType?: string,
        public childNodes?: PTreeNode[],
        public nodeName?: string,
        public lazyNode?: boolean,
        public linkedNodeObjects?: PTreeNodeObject[]
    ) { }
}

export class PTreeNodeObject extends PTreeNode {
    constructor(
        public _id: number,
        public nodeType?: string,
        public childNodes?: PTreeNode[],
        public nodeName?: string,
        public lazyNode?: boolean,
        public linkedNodeObjects?: PTreeNodeObject[],
        public dataType?: string,
        public nodeObject?: any
    ) {
        super(_id, name, childNodes, nodeName, lazyNode, linkedNodeObjects);
    }
}

export class PTreeNodeLinkedObject {
    constructor(
        public colorKey: string,
        public monitorObjectId: number,
        public nodeType: number
    ) {}
}
