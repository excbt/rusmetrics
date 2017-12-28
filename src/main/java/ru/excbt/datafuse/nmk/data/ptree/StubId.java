package ru.excbt.datafuse.nmk.data.ptree;

public class StubId {
    private final Long id;

    public StubId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public boolean isStub() {
        return true;
    }
}
