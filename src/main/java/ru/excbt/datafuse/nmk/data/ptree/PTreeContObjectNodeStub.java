package ru.excbt.datafuse.nmk.data.ptree;

public class PTreeContObjectNodeStub extends PTreeNodeObject<StubId> {

    public PTreeContObjectNodeStub(StubId stubId) {
        super(PTreeNodeType.CONT_OBJECT, stubId, DataType.STUB);
    }

    public PTreeContZPointNodeStub addContZPointStub(StubId stubId) {
        PTreeContZPointNodeStub contZPointNode = new PTreeContZPointNodeStub(stubId);
        this.childNodes.add(contZPointNode);
        return contZPointNode;
    }

}
