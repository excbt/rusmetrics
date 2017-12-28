package ru.excbt.datafuse.nmk.data.ptree;

public class PTreeContZPointNodeStub extends PTreeNodeObject<StubId>  {

    public PTreeContZPointNodeStub(StubId stubId) {
            super(PTreeNodeType.CONT_ZPOINT, stubId, DataType.STUB);
    }

    public PTreeDeviceObjectNodeStub addDeviceObjectStub(StubId stubId) {
        PTreeDeviceObjectNodeStub deviceNode = new PTreeDeviceObjectNodeStub(stubId);
        this.childNodes.add(deviceNode);
        return deviceNode;
    }
}
