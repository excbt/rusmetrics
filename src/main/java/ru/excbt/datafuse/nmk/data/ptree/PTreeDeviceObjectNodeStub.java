package ru.excbt.datafuse.nmk.data.ptree;

public class PTreeDeviceObjectNodeStub extends PTreeNodeObject<StubId> {
    public PTreeDeviceObjectNodeStub(StubId stubId) {
        super(PTreeNodeType.DEVICE_OBJECT, stubId, DataType.STUB);
    }
}
