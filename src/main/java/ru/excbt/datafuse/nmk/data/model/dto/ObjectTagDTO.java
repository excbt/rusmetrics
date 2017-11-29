package ru.excbt.datafuse.nmk.data.model.dto;

public class ObjectTagDTO {

    private String objectTagKeyname;

    private Long objectId;

    private String tagName;

    public String getObjectTagKeyname() {
        return objectTagKeyname;
    }

    public void setObjectTagKeyname(String objectTagKeyname) {
        this.objectTagKeyname = objectTagKeyname;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
